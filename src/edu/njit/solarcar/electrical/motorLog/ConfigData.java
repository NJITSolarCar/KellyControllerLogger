package edu.njit.solarcar.electrical.motorLog;

import java.io.File;

/**
 * Represents a set of configuration data from the configuration window
 * @author Duemmer
 *
 */
public class ConfigData
{
	public int controllerCanId;
	public int controllerResponseId;
	public double samplingFreq;
	public double currentChartScale;
	public double voltageChartScale;
	public double rpmChartScale;
	public File logDir;
	public int motorPoles;
	public int baud;
	public double plotTime;
}
