package edu.njit.solarcar.electrical.motorLog.can;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;

/**
 * Implements a SocketCAN interface over a serial connection. This is mostly
 * intended for use with windows, as it doesn't natively support the official
 * SocketCAN interface. However, it should work on Linux as well, thanks to the
 * cross platform compatibility provided by jssc.
 * 
 * While operational, this will keep a background thread running to manage
 * incoming data, and for other serial port housekeeping.
 * 
 * @author Duemmer
 *
 */
public class SerialCAN implements CANBus
{
	private int rxTimeout = 500;
	private String portName;
	private int baud = 9600;
	private SerialPort port;
	private SerialCanRxListener listener;
	private Set<FrameProcessor> frameProcs;
	
	
	
	public SerialCAN(String portName, int baud) {
		super();
		this.portName = portName;
		this.baud = baud;
	}




	@Override
	public void connect(CANBitrate bitrate)
		throws IOException {
		port = new SerialPort(portName);
		
		// Open the port
		try {
			port.setParams(baud, 8, 1, 0);
			if (!port.openPort())
				throw new IOException("Failed to open serial port!");
			
			// Reset the device, giving it some time to clear itself
			port.writeString("\r\r\r\rC\r");
			try { Thread.sleep(rxTimeout); } catch (InterruptedException e) {}
			port.readBytes();
			
			// Create the listener
			listener = new SerialCanRxListener(frameProcs, port);
			port.addEventListener(listener);
			
			// Perform a connection check implicitly. Will throw an exception
			// if nothing is received, or what is received is invalid
			getVersion();
			
			// Initialize the device
			// Set speed
			port.writeString(bitrate.toString());
			port.writeByte((byte) '\r');
			byte[] r1 = port.readBytes(1, rxTimeout);
			if (r1[0] != '\r') { throw new IOException(
				String.format("Bad response 0x%02X from device", r1[0])); }
			
			// Start the device
			port.writeString("O\r");
			byte[] r2 = port.readBytes(1, rxTimeout);
			if (r2[0] != '\r') { throw new IOException(
				String.format("Bad response 0x%02X from device", r2[0])); }
			
			// Enable the rx listener
			setRxInterruptEnable(true);
		}
		catch (SerialPortException | SerialPortTimeoutException e) {
			throw new IOException(e);
		}
	}


	
	
	/**
	 * Check version. As long as it gives a valid looking version, it's probably fine
	 * @throws IOException If an I/O error occurs, or an invalid response is returned.
	 * @return The version string, exactly as it came in (e.g "V0123\r"
	 */
	public String getVersion() throws IOException {
		try {
			// Wait on any pending receives, and stop the interrupt handler
			setRxInterruptEnable(false);
			
			port.writeString("V\r");
			byte[] ver = port.readBytes(6, rxTimeout);
			if (ver == null)
				throw new IOException("No response from device!");
			
			// Check validity
			String verStr = new String(ver);
			if(ver[0] != 'V' && ver[0] != 'v')
				throw new IOException("Bad version string \"" + new String(ver) +"\"");
			
			return verStr;
		}
		
		catch (SerialPortException | SerialPortTimeoutException e) {
			throw new IOException(e);
		}
		
		finally { setRxInterruptEnable(true); } // Make sure to turn interrupts back on
	}
	
	
	
	
	@Override
	public boolean isConnected() {
		try { 
			getVersion(); 
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	
	
	@Override
	public void sendFrame(CANFrame frame) throws IOException {
		StringBuilder buf = new StringBuilder();
		
		// Select leading character
		char firstChar;
		if (frame.ide && frame.rtr)
			firstChar = 'R';
		else if (!frame.ide && frame.rtr)
			firstChar = 'r';
		else if (frame.ide && !frame.rtr)
			firstChar = 'T';
		else firstChar = 't';
		buf.append(firstChar);
		
		// add ID / data length. Depending on whether or not the frame has extended
		// ID, the id string will be longer
		String fmtStr;
		if (frame.ide)
			fmtStr = "%08X%01d";
		else fmtStr = "%04X%01d";
		buf.append(String.format(fmtStr, frame.id, frame.len));
		
		// add data
		for (byte b : frame.data)
			buf.append(String.format("%02X", b));
		
		// terminate
		buf.append('\r');
		
		// Send. Momentarily disable the receive loop to manually inspect the response
		setRxInterruptEnable(false);
		try {
			if ( !port.writeString(buf.toString()) )
				throw new IOException("Failed to write frame data to port!");
			
			// Check the response
			byte[] resp = port.readBytes(2, rxTimeout);
			if(resp[0] != 'Z' && resp[1] != '\r')
				throw new IOException("Invalid response 0x%02X %02X after packet sent");
		}
		catch (SerialPortException | SerialPortTimeoutException e) {
			throw new IOException(e);
		}
		finally { setRxInterruptEnable(true); } // Make sure to turn interrupts back on
	}
	
	
	
	@Override
	public void disconnect() throws IOException {
		if ( port != null ) {
			try {
				port.writeString("C\r");
				port.closePort();
			}
			catch (SerialPortException e) {
				throw new IOException(e);
			}
		}
	}
	
	
	
	@Override
	public Set<FrameProcessor> frameProcessors() {
		if(frameProcs == null)
			frameProcs = new HashSet<>();
		return frameProcs;
	}
	
	
	/**
	 * Enables or disables the automatic receive interrupt, designed to
	 * fetch packets that come in asynchronously
	 * @param enable whether to enable or disable the interrupt
	 */
	private void setRxInterruptEnable(boolean enable){
		int rxIntMask = enable ? SerialPort.MASK_RXCHAR : 0;
		try {
			if(!enable) // need to wait for pending transactions
				listener.waitRecvDone(rxTimeout);
			port.setEventsMask(rxIntMask);
		}
		
		// Ignore exceptions here as they will probably occur at the same time as
		// other communications exceptions, and we don't want to mask those
		catch (SerialPortException e) {}
	}
	
	
	
	/**
	 * Sets the port to bind to for the Serial connection
	 * 
	 * @param port the name of the port to bind to
	 * @throws IllegalArgumentException if an invalid serial port identifier is
	 *                                  passed to the method
	 */
	public void setPort(String port) throws IllegalArgumentException {
		
		// Check that the list of system ports contains that option
		if (
			!Arrays.asList(SerialPortList.getPortNames()).contains(port)
			)
			throw new IllegalArgumentException("Invalid port name");
		this.portName = port;
	}
	
	
	
	public void setBaud(int baud) {
		this.baud = baud;
	}



	/**
	 * Sets the amount of time that the program will listen for received
	 * data
	 * @param rxTimeout The timeout, in ms. Must be > 0
	 */
	public void setRxTimeout(int rxTimeout) {
		this.rxTimeout = rxTimeout;
	}
	
	
}
