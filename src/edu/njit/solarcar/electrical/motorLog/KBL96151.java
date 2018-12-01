package edu.njit.solarcar.electrical.motorLog;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import edu.njit.solarcar.electrical.motorLog.can.CANBus;
import edu.njit.solarcar.electrical.motorLog.can.CANFrame;

/**
 * Represents the <a
 * href=http://kellycontroller.com/kbl9615124-96v150abldc-controllerwith-regen-p-1110.html>Kelly
 * KBL96151</a>
 * motor controller, assuming a connection via a CAN bus
 * 
 * @author Duemmer
 *
 */
public class KBL96151
{
	// Command codes
	public static final byte CMD_CCP_INVALID = (byte) 0xE3;
	public static final byte CMD_CCP_FLASH_READ = (byte) 0xF2;
	public static final byte CMD_CCP_A2D_BATCH_READ1 = (byte) 0x1B;
	public static final byte CMD_CCP_A2D_BATCH_READ2 = (byte) 0x1A;
	public static final byte CMD_CCP_MONITOR1 = (byte) 0x33;
	public static final byte CMD_CCP_MONITOR2 = (byte) 0x37;
	public static final byte CMD_COM_SW_ACC = (byte) 0x42;
	public static final byte CMD_COM_SW_BRK = (byte) 0x43;
	public static final byte CMD_COM_SW_REV = (byte) 0x44;
	
	// Flash read values
	public static final byte FLASH_INFO_MODULE_NAME = (byte) 64;
	public static final byte FLASH_INFO_MODULE_NAME_LEN = (byte) 8;
	
	public static final byte FLASH_INFO_SOFTWARE_VER = (byte) 83;
	public static final byte FLASH_INFO_SOFTWARE_VER_LEN = (byte) 2;
	
	public static final byte FLASH_TPS_DEAD_ZONE_LOW = (byte) 4;
	public static final byte FLASH_TPS_DEAD_ZONE_LOW_LEN = (byte) 1;
	
	public static final byte FLASH_CAL_TPS_DEAD_ZONE_HIGH = (byte) 5;
	public static final byte FLASH_CAL_TPS_DEAD_ZONE_HIGH_LEN = (byte) 1;
	
	public static final byte FLASH_CAL_BRAKE_DEAD_ZONE_LOW = (byte) 38;
	public static final byte FLASH_CAL_BRAKE_DEAD_ZONE_LOW_LEN = (byte) 1;
	
	public static final byte FLASH_CAL_BRAKE_DEAD_ZONE_HIGH = (byte) 39;
	public static final byte FLASH_CAL_BRAKE_DEAD_ZONE_HIGH_LEN = (byte) 1;
	
	// Application parameters
	public static final int RESPONSE_TIMEOUT = 250;
	public static final double VOLTAGE_SCALAR = 1 / 1.84;
	
	private CANBus canBus;
	private int controllerCanId;
	private int responseCanId;
	
	private CANFrame lastResponse;
	private boolean commandPending;
	private Thread queryThread;
	
	/**
	 * Represents results of an A2D 1 query
	 * 
	 * @author Duemmer
	 *
	 */
	public class AdcBatch1
	{
		public final long timeRecv = System.currentTimeMillis();
		
		public double brake;
		public double throttle;
		public double vs; // voltage on the 5 volt rail
		public double vBat;
		public double vOperating;
	}
	
	/**
	 * Represents results of an A2D 2 query
	 * 
	 * @author Duemmer
	 *
	 */
	public class AdcBatch2
	{
		public final long timeRecv = System.currentTimeMillis();
		
		// Phase Current
		public double ia;
		public double ib;
		public double ic;
		
		// Phase voltage
		public double va;
		public double vb;
		public double vc;
	}
	
	
	
	/**
	 * Represents teh results of a monitor 1 query
	 * @author Duemmer
	 *
	 */
	public class Monitor1
	{
		public final long timeRecv = System.currentTimeMillis();
		
		public double pwm;
		public boolean motorEnabled;
		public double motorTemp;
		public double controllerTemp;
		public double fetmosLowTemp;
		public double fetmosHiTemp;
	}
	
	
	
	/**
	 * Creates a new motor controller.
	 * 
	 * @param controllerCanId the CAN id that the motor controller listens for
	 *                        command frames on
	 * @param responseCanId   the CAN id that the motor controller writes response
	 *                        frames to
	 */
	public KBL96151(int controllerCanId, int responseCanId) {
		this.controllerCanId = controllerCanId;
		this.responseCanId = responseCanId;
	}
	
	
	
	/**
	 * Attempts to connect to the motor controller, by querying it for the model
	 * name. If a response is received in a timely manner with the same ID as
	 * responseCanId, then the connection is established, and communication can
	 * continue. If not, an {@link IOException} will be raised.
	 * 
	 * @param canBus The {@link CanBus} that the motor controller is
	 *               connected to. This should be up and running before
	 *               trying to connect the motor controller
	 * @throws IOException If a CAN communications failure occurs
	 * @return true if the motor controller responded to the name query
	 */
	public boolean connect(CANBus bus) throws IOException {
		this.canBus = bus;
		
		// Add the frame processor. If it gets a response command
		canBus.frameProcessors().add((frame) -> {
			if (frame.id == responseCanId) {
				/*if(frame.data[0] == CMD_CCP_INVALID) {
					System.err.println("Invalid command");
					return;
				}*/
				lastResponse = frame;
				commandPending = false;
				
				// wake up the query thread
				if (queryThread != null)
					queryThread.interrupt();
			}
		});
		
		// Verify the connection
		try {
			byte[] modName = readFlash(
				FLASH_INFO_MODULE_NAME,
				FLASH_INFO_MODULE_NAME_LEN);
			
			// This should be true, but may not be in case of some unforeseen issue
			boolean nameGood = modName[0] != CMD_CCP_INVALID;
			if(!nameGood)
				System.err.println("Invalid ");
			return nameGood;
		}
		catch (TimeoutException e) {
			//return false;
			throw new IOException(e);
		}
	}
	
	
	
	/**
	 * Synchronously sends a query on the CAN bus to the target, and will wait
	 * until it completes, or a timeout of {@link #RESPONSE_TIMEOUT} has elapsed
	 * 
	 * @param command the command code to run
	 * @param data    any extra data to be sent to the device. Can be null.
	 * @return the motor drive's response
	 * @throws TimeoutException If a response from the motor controller is not
	 *                          received in the response period
	 * @throws IOException      if an IO error occurs during sending the CAN frame
	 */
	private CANFrame query(byte command) throws IOException, TimeoutException {
		return query(command, null);
	}
	
	
	
	/**
	 * Synchronously sends a query on the CAN bus to the target, and will wait
	 * until it completes, or a timeout of {@link #RESPONSE_TIMEOUT} has elapsed
	 * 
	 * @param command the command code to run
	 * @param data    any extra data to be sent to the device. Can be null.
	 * @return the motor drive's response
	 * @throws TimeoutException If a response from the motor controller is not
	 *                          received in the response period
	 * @throws IOException      if an IO error occurs during sending the CAN frame
	 */
	private CANFrame query(byte command, byte[] data)
		throws IOException, TimeoutException {
		CANFrame frame = new CANFrame();
		frame.data[0] = command;
		frame.id = controllerCanId;
		
		// Build the frame
		if (data == null)
			frame.len = 1;
		
		else if (data.length > 7)
			throw new IllegalArgumentException("Extra data length must be <= 7");
		
		else {
			for (int i = 0; i < data.length; i++)
				frame.data[i + 1] = data[i];
			frame.len = (byte) (1 + data.length);
		}
		
		commandPending = true;
		queryThread = Thread.currentThread();
		
		// send it out, and wait
		canBus.sendFrame(frame);
		
		try {
			Thread.sleep(RESPONSE_TIMEOUT);
		}
		catch (InterruptedException e) {
			// We (should have) gotten a response. Double check to be sure
			if (commandPending) // signal came from somewhere else
				throw new RuntimeException(e);
			
			// got a response
			return lastResponse;
		}
		
		// Timed out
		throw new TimeoutException("Timed out waiting for device");
	}
	
	
	
	/**
	 * Reads a section of flash memory on the motor controller
	 * 
	 * @param address the starting address in flash
	 * @param length  the number of bytes to read
	 * @return The data read from the device
	 * @throws IOException      If a CAN I/O error occurs
	 * @throws TimeoutException If a response isn't received in time
	 */
	public byte[] readFlash(byte address, byte length)
		throws IOException, TimeoutException {
		byte[] data = new byte[2];
		data[0] = address;
		data[1] = length;
		
		CANFrame response = query(CMD_CCP_FLASH_READ, data);
		return response.data;
	}
	
	
	
	/**
	 * Reads the current electronic RPM of the motor, where electronic RPM is
	 * equal to real RPM multiplied by the pole count.
	 * 
	 * @return The Electronic RPM read from the device
	 * @throws IOException      If a CAN I/O error occurs
	 * @throws TimeoutException If a response isn't received in time
	 */
	public int readElectronicRPM() throws IOException, TimeoutException {
		CANFrame frame = query(CMD_CCP_MONITOR2);
		return frame.data[0] | (frame.data[1] << 8);
	}
	
	
	
	/**
	 * Reads the parameters of CCP_A2D_BATCH_READ1 in the manual
	 * 
	 * @return An {@link AdcBatch1} object containing the data
	 * @throws IOException      If a CAN I/O error occurs
	 * @throws TimeoutException If a response isn't received in time
	 */
	public AdcBatch1 readAdcBatch1() throws IOException, TimeoutException {
		CANFrame frame = query(CMD_CCP_A2D_BATCH_READ1);
		AdcBatch1 val = new AdcBatch1();
		
		val.brake = ((double) Byte.toUnsignedInt(frame.data[0])) * (5.0 / 255.0);
		val.throttle = ((double) Byte.toUnsignedInt(frame.data[1])) * (5.0 / 255.0);
		val.vOperating = ((double) Byte.toUnsignedInt(frame.data[2])) * VOLTAGE_SCALAR;
		val.vs = ((double) Byte.toUnsignedInt(frame.data[3]) - 120) * (5.25 - 4.75) + 4.75;
		val.vBat = ((double) Byte.toUnsignedInt(frame.data[4])) * VOLTAGE_SCALAR;
		
		return val;
	}
	
	
	
	/**
	 * Reads the parameters of CCP_A2D_BATCH_READ2 in the manual
	 * 
	 * @return An {@link AdcBatch2} object containing the data
	 * @throws IOException      If a CAN I/O error occurs
	 * @throws TimeoutException If a response isn't received in time
	 */
	public AdcBatch2 readAdcBatch2() throws IOException, TimeoutException {
		CANFrame frame = query(CMD_CCP_A2D_BATCH_READ2);
		AdcBatch2 val = new AdcBatch2();
		
		val.ia = Byte.toUnsignedInt(frame.data[0]);
		val.ib = Byte.toUnsignedInt(frame.data[1]);
		val.ic = Byte.toUnsignedInt(frame.data[2]);
		
		val.va = ((double) Byte.toUnsignedInt(frame.data[3])) * VOLTAGE_SCALAR;
		val.vb = ((double) Byte.toUnsignedInt(frame.data[4])) * VOLTAGE_SCALAR;
		val.vc = ((double) Byte.toUnsignedInt(frame.data[5])) * VOLTAGE_SCALAR;
		
		return val;
	}
	
	
	public Monitor1 readMonitor1() throws IOException, TimeoutException {
		CANFrame frame = query(CMD_CCP_MONITOR1);
		Monitor1 val = new Monitor1();
		
		val.pwm = ((double)Byte.toUnsignedInt(frame.data[0])) / 100;
		val.motorEnabled = frame.data[1] != 0;
		val.motorTemp = Byte.toUnsignedInt(frame.data[2]);
		val.controllerTemp = Byte.toUnsignedInt(frame.data[3]);
		val.fetmosHiTemp = Byte.toUnsignedInt(frame.data[4]);
		val.fetmosLowTemp = Byte.toUnsignedInt(frame.data[5]);
		
		return val;
	}
	
}
