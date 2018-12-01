package edu.njit.solarcar.electrical.motorLog.can;

/**
 * Represents a bitrate to use on the CAN bus
 * @author Duemmer
 *
 */
public enum CANBitrate
{
	S0 (0, 10000),		// 10kbps
	S1 (0, 20000),		// 20kbps
	S2 (0, 50000),		// 50kbps
	S3 (0, 100000),		// 100kbps
	S4 (0, 125000),		// 125kbps
	S5 (0, 25000),		// 250kbps
	S6 (0, 500000),		// 500kbps
	S7 (0, 800000),		// 800kbps
	S8 (0, 1000000)		// 1000kbps
	;
	
	private final int bitrate;
	private final int code;
	
	private CANBitrate(int bitrate, int code) {
		this.bitrate = bitrate;
		this.code = code;
	}
	
	public int bitrate() {
		return this.bitrate;
	}
	
	public int code() {
		return this.code;
	}
	
	
	/**
	 * Converts an integer bitrate to a bitrate code
	 * @param bitrate
	 * @return
	 */
	public static CANBitrate fromBitrate(int bitrate) {
		switch(bitrate) {
			case 10000: return S0;
			case 20000: return S1;
			case 50000: return S2;
			case 100000: return S3;
			case 125000: return S4;
			case 250000: return S5;
			case 500000: return S6;
			case 800000: return S7;
			case 1000000: return S8;
			default: return null;
		}
	}
}
