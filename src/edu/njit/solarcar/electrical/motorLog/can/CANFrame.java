package edu.njit.solarcar.electrical.motorLog.can;

/**
 * Represents a basic CAN bus frame
 * 
 * @author Duemmer
 *
 */
public class CANFrame
{
	public boolean rtr;																// Remote Request
	public boolean ide;																// Extended ID
	public byte len;																	// Data Length
	public int id;																		// Frame ID
	public byte[] data = new byte[8];									// Frame Data
	
	public long time = 
		System.currentTimeMillis();		// Time that the frame was created
}
