package edu.njit.solarcar.electrical.motorLog;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import edu.njit.solarcar.electrical.motorLog.can.CANBitrate;
import edu.njit.solarcar.electrical.motorLog.can.CANBus;
import edu.njit.solarcar.electrical.motorLog.can.SerialCAN;
import javafx.application.Platform;

/**
 * Central class that controls communications and
 * interfacing between the views, logger, and the motor controller
 * 
 * @author Duemmer
 *
 */
public class ComController
{
	private static final int BAUD = 250000;
	
	private KBL96151 motor;
	private CANBus bus;
	private MotorLogger motLog;
	private boolean isLogging = false;
	
	private LogData currData;
	private ConfigData conf;
	
	private ScheduledExecutorService motorPollThread;



	private boolean isPolling = false;
	private long pollingStart;
	
	
	
	public ComController(ConfigData conf) {
		this.conf = conf;
		motorPollThread = Executors.newSingleThreadScheduledExecutor((r) -> {
			Thread t = new Thread(r);
			t.setDaemon(true);
			t.setName("Motor Poll");
			return t;
		});
	}
	
	
	
	/**
	 * Attempts to establish a connection to the motor controller
	 * 
	 * @param canPort
	 * @param baud
	 * @param controllerId
	 * @param receiveId
	 * @return
	 * @throws IOException
	 */
	private boolean connectToMotor(
		String canPort, int controllerId, int receiveId
	) throws IOException {
		bus = new SerialCAN(canPort, BAUD);
		bus.connect(CANBitrate.S8);
		motor = new KBL96151(controllerId, receiveId);
		return motor.connect(bus);
		
	}
	
	
	
	public boolean startPolling(String canPort) throws IOException {
		if (conf.samplingFreq <= 0)
			throw new IllegalArgumentException("Polling frequency must be > 0");
		
		boolean connected = connectToMotor(
			canPort,
			conf.controllerCanId,
			conf.controllerResponseId);
		
		if (!connected)
			return false;
		
		isPolling = true;
		int pollRate = (int) (1000 / conf.samplingFreq);
		pollingStart = System.currentTimeMillis();
		
		motorPollThread.scheduleAtFixedRate(() -> {
			try {
				currData = poll();
				if (isLogging)
					motLog.writeRow(currData);
			}
			catch (IOException e) {
				Platform.runLater(
					() -> AppController.getMainWindowController().pollingError(e)
				);
				stopPolling();
			}
		}, pollRate, pollRate, TimeUnit.MILLISECONDS);
		
		return true;
	}
	
	
	
	private LogData poll() throws IOException {
		try {
			KBL96151.AdcBatch1 b1 = motor.readAdcBatch1();
			KBL96151.AdcBatch2 b2 = motor.readAdcBatch2();
			KBL96151.Monitor1 m1 = motor.readMonitor1();
			
			double rpm = ((double) motor.readElectronicRPM()) / conf.motorPoles;
			double iTotal = phaseToRMS(b2.ia, b2.ib, b2.ic);
			
			LogData d = new LogData();
			d.rpm = rpm;
			d.iTotal = iTotal;
			d.iA = b2.ia;
			d.iB = b2.ib;
			d.iC = b2.ic;
			d.vbat = b1.vBat;
			d.vA = b2.va;
			d.vB = b2.vb;
			d.vC = b2.vc;
			d.vtotal = b1.vOperating;
			d.throttle = b1.throttle;
			d.motorTemp = m1.motorTemp;
			d.controllerTemp = m1.controllerTemp;
			
			return d;
		}
		catch (TimeoutException e) {
			throw new IOException("Timed out waiting for device response", e);
		}
	}
	
	
	
	public void startLogging() throws IOException {
		motLog = MotorLogger.newLogger(conf.logDir);
		isLogging = true;
	}
	
	
	
	public void stopLogging() {
		motLog.close();
		isLogging = false;
	}
	
	
	
	public void stopPolling() {
		try {
			isPolling = false;
			bus.disconnect();
			stopLogging();
			motorPollThread.shutdownNow();
		}
		catch (IOException e) {
			System.err.println("Exception stopping polling");
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * Convert 3 phase wye current into aggregate current. Since we don't know
	 * whether the phase currents listed are rms or raw, so we are stuck doing a
	 * bit of guesswork, unfortunately. Worst comes to worst they can be corrected
	 * in post, as phase currents get logged
	 * 
	 * @param ia
	 * @param ib
	 * @param ic
	 * @return
	 */
	private double phaseToRMS(double ia, double ib, double ic) {
		return Math.sqrt(ia * ia + ib * ib + ic * ic);
	}
	
	
	
	public void setConf(ConfigData conf) {
		this.conf = conf;
	}
	
	
	
	public boolean isPolling() {
		return isPolling;
	}
	
	
	
	public boolean isLogging() {
		return isLogging;
	}



	public LogData getCurrData() {
		return currData;
	}



	public long getPollingStart() {
		return pollingStart;
	}
	
	
	public CANBus getBus() {
		return bus;
	}
	
	
	public File logFile() {
		return motLog.getTargetFile();
	}
	
	
	public long getLogStart() {
		return motLog.getMillisRunning();
	}
	
}
