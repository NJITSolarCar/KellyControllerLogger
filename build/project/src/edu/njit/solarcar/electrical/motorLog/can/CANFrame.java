package edu.njit.solarcar.electrical.motorLog.can;

/**
 * Represents a basic CAN bus frame
 * 
 * @author Duemmer
 *
 */
public class CANFrame
{
//Maximum possible ID. 29 and 11 bits, respectively
	public static final int MAX_ID_STD = 0x7FF;
	public static final int MAX_ID_EXT = 0x1FFFFFFF;
	
	public boolean rtr;																// Remote Request
	public boolean ide;																// Extended ID
	public byte len;																	// Data Length
	public int id;																		// Frame ID
	public byte[] data;																// Frame Data
	
	public long time = 
		System.currentTimeMillis();		// Time that the frame was created

	
	public CANFrame() {
		this(0, new byte[8], false, false);
	}
	
	
	public CANFrame(int id, byte[] data) {
		this(id, data, false, false);
	}
	
	public CANFrame(int id, byte[] data, boolean ide, boolean rtr) {
		if(data.length > 8)
			throw new IllegalArgumentException("Data must have a length between 0 and 8");
		
		int maxId = ide ? MAX_ID_EXT : MAX_ID_STD;
		if(id < 0 || id > maxId)
			throw new IllegalArgumentException("ID must be between 0 and " +maxId);
		
		this.ide = ide;
		this.rtr = rtr;
		this.id = id;
		this.data = data;
		this.len = (byte) data.length;
	}
	
	
}
