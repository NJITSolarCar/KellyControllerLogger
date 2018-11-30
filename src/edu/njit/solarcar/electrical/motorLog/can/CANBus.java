package edu.njit.solarcar.electrical.motorLog.can;

import java.io.IOException;
import java.util.Set;

/**
 * Defines a hardware layer that a CAN communications
 * interface needs to fulfill
 * 
 * @author Duemmer
 *
 */
public interface CANBus
{
	/**
	 * Attempts to connect the application to the physical CAN bus. This should
	 * be called after any implementation specific configuration is set. If this
	 * successfully connects to the bus, it will immediately begin listening to
	 * and processing bus traffic in the background. In addition, from this point
	 * until the connection is lost, frames can be sent on to the bus.
	 * 
	 * @param proc    the {@link FrameProcessor} that will handle all incoming
	 *                traffic
	 * @param bitrate the speed of the physical bus
	 * @throws IOException If the connection to the bus cannot be established
	 */
	public void connect(CANBitrate bitrate) throws IOException;
	
	
	
	/**
	 * Obtains a reference to the set of {@link FrameProcessor FrameProcessors} that
	 * will be invoked on incoming frames. This allows multiple different devices
	 * to add their own listeners onto the bus, to listen for their own packets
	 * @return
	 */
	public Set<FrameProcessor> frameProcessors();
	
	
	
	/**
	 * Checks the status of the connection to the physical CAN bus
	 * 
	 * @return true if the {@link CANBus} is actively connected to the
	 *         physical bus, false otherwise
	 */
	public boolean isConnected();
	
	
	
	/**
	 * Returns an "address" for identifying the device. The address doesn't
	 * have to be formal, but is here rather to serve as a utility to identify
	 * a CAN device. Note that this address should be a link layer address, like
	 * a COM port for a {@link SerialCAN} device, for instance
	 * @return The address, or an empty string if not connected.
	 */
	public String getAddress();
	
	
	
	/**
	 * Sends a {@link CANFrame} onto the bus
	 * 
	 * @param frame The frame to transmit
	 * @throws IOException If a communications error occurs between the
	 *                     {@link CANBus} and the physical interface
	 */
	public void sendFrame(CANFrame frame) throws IOException;
	
	
	
	/**
	 * Disconnects from the physical layer if connected, does nothing otherwise
	 */
	public void disconnect() throws IOException;
}
