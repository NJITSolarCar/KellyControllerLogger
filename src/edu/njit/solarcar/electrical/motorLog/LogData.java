package edu.njit.solarcar.electrical.motorLog;

/**
 * Represents a set of data ready to go out to
 * a file
 * 
 * @author Duemmer
 *
 */
public class LogData
{
	public final long time = System.currentTimeMillis();
	public double rpm;
	public double iTotal;
	public double iA;
	public double iB;
	public double iC;
	public double vbat;
	public double vA;
	public double vB;
	public double vC;
	public double vtotal;
	public double throttle;
	public double motorTemp;
	public double controllerTemp;
}
