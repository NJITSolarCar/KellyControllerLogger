package edu.njit.solarcar.electrical.motorLog.can;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Simple class to read bytes directly off the port as they come in. Updates
 * the input reader state machine as bytes come in.
 * 
 * @author Duemmer
 *
 */
public class SerialCanRxListener implements SerialPortEventListener
{
	private SerialPort port;
	private Queue<CANFrame> frames;
	private Thread frameProcThread;
	private boolean shutdown = false; // if true stop the daemon
	
	// buffer to store raw incoming character data
	private byte[] buf;
	private int bufPtr;
	
	
	
	public SerialCanRxListener(Set<FrameProcessor> procs, SerialPort port) {
		super();
		this.port = port;
		
		buf = new byte[256];
		frames = new LinkedList<>();
		
		// When active, poll through all the received frames and process them
		frameProcThread = new Thread(() -> {
			do {
				while (!frames.isEmpty()) {
					CANFrame f = frames.poll();
					for(FrameProcessor proc : procs)
						proc.processFrame(f);
				}
				
				// Wait until we get some more frames
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException e) {}
			} while (!shutdown);
		});
		
		frameProcThread.setDaemon(true);
		frameProcThread.setName("SerialCAN Frame Processor");
		frameProcThread.start();
	}
	
	
	
	/**
	 * Waits until a transmission is fully received (i.e, none are pending)
	 * 
	 * @param timeout the time to wait if > 0, or infinite if <= 0
	 * @return true if timed out, false otherwise
	 */
	public boolean waitRecvDone(int timeout) {
		long tEnd = System.currentTimeMillis() + timeout;
		
		// if it's bigger than 0, a receive is in progress
		while (bufPtr > 0 && (timeout <= 0 || System.currentTimeMillis() < tEnd))
			;
		
		return tEnd > System.currentTimeMillis();
	}
	
	
	
	/**
	 * Stops the RX listener from processing further
	 * frames. It will process all the frames queued before this, however
	 */
	public void shutdown() {
		shutdown = true;
		frameProcThread.interrupt();
	}
	
	
	
	/**
	 * Process any incoming characters
	 * 
	 * @param event
	 */
	@Override
	public void serialEvent(SerialPortEvent event) {
		byte[] bytes;
		if(event.isRXCHAR() && event.getEventValue() > 0) {
			try {
				bytes = port.readBytes();
//				System.out.print(new String(bytes));
			}
			catch (SerialPortException e) {
				throw new RuntimeException(e);
			}
			
			for (byte b : bytes) {
				buf[bufPtr++] = b;
				
				if (b == '\r') {
//					System.out.println("\\r");
					CANFrame frame = procFrameBuf();
					clearBuf();
					if (frame != null) {
						frames.add(frame);
						frameProcThread.interrupt();
					}
				} else if (b == 0x07) { // TODO: signal an error here
					System.err.println("Got NACK from device");
					clearBuf();
				}
			}
		}
	}
	
	
	
	/**
	 * Assumes a completed message is in the buffer, and from that extracts
	 * a CAN frame
	 * 
	 * @return the parsed frame, or null if the data was invalid
	 */
	private CANFrame procFrameBuf() {
		// end of frame, can parse
		CANFrame frame = new CANFrame();
		
		// Set the flags on the frame based on the transmission character
		int dataIdx;
		switch (buf[0]) {
			case 'R':
				frame.rtr = true;
				frame.ide = true;
				frame.len = (byte) Character.digit(buf[9], 10);
				frame.id = Integer.parseInt(new String(buf, 1, 8), 16);
				dataIdx = 10;
				break;
			case 'r':
				frame.rtr = true;
				frame.ide = false;
				frame.len = (byte) Character.digit(buf[4], 10);
				frame.id = Integer.parseInt(new String(buf, 1, 3), 16);
				dataIdx = 5;
				break;
			case 'T':
				frame.rtr = false;
				frame.ide = true;
				frame.len = (byte) Character.digit(buf[9], 10);
				frame.id = Integer.parseInt(new String(buf, 1, 8), 16);
				dataIdx = 10;
				break;
			case 't':
				frame.rtr = false;
				frame.ide = false;
				frame.len = (byte) Character.digit(buf[4], 10);
				frame.id = Integer.parseInt(new String(buf, 1, 3), 16);
				dataIdx = 5;
				break;
			default: // Not a CAN frame, so ignore it
				return null;
		}
		
		// get the data
		for (int i = 0; i < frame.len; i++) {
			int offset = dataIdx + 2 * i;
			frame.data[i] = (byte) Integer.parseInt(new String(buf, offset, 2), 16);
		}
		
		return frame;
	}
	
	
	
	private void clearBuf() {
		for(int i=0; i<bufPtr; i++)
			buf[i] = 0;
		bufPtr = 0;
	}
	
}
