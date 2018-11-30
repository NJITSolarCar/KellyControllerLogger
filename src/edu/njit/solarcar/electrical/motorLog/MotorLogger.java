package edu.njit.solarcar.electrical.motorLog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents an operational motor log file.
 * <p>
 * Columns:
 * </p>
 * <ul>
 * <li>Relative time (in s)</li>
 * <li>RPM</li>
 * <li>Total Current</li>
 * <li>Phase A Current</li>
 * <li>Phase B Current</li>
 * <li>Phase C Current</li>
 * <li>Battery Voltage</li>
 * <li>Total Voltage</li>
 * <li>Phase A Voltage</li>
 * <li>Phase B Voltage</li>
 * <li>Phase C Voltage</li>
 * <li>Throttle</li>
 * <li>Motor Temperature</li>
 * <li>Controller temperature</li>
 * </ul>
 * 
 * @author Duemmer
 *
 */
public class MotorLogger
{
	private BufferedWriter csvWriter;
	private long startMillis;
	private File targetFile;
	
	
	
	public MotorLogger(File targetFile) throws IOException {
		this.targetFile = targetFile;
		csvWriter = new BufferedWriter(new FileWriter(targetFile, false));
		
		// Write header
		String header = Stream.of(
			"Relative time",
			"RPM",
			"Total Current",
			"Phase A Current",
			"Phase B Current",
			"Phase C Current",
			"Battery Voltage",
			"Total Voltage",
			"Phase A Voltage",
			"Phase B Voltage",
			"Phase C Voltage",
			"Throttle",
			"Motor Temperature",
			"Controller Temperature")
			.collect(Collectors.joining(",", "", "\n\n"));
		csvWriter.write(header);
		
		startMillis = System.currentTimeMillis();
	}
	
	
	/**
	 * Writes a row of data to the log file
	 * @param data
	 * @throws IOException
	 */
	public void writeRow(LogData data) throws IOException {
		double t = ((double) (System.currentTimeMillis() - startMillis)) / 1000;
		String s = Stream.of(
			t, 
			data.rpm, 
			data.iTotal,
			data.iA, 
			data.iB, 
			data.iC, 
			data.vbat,
			data.vtotal,
			data.vA,
			data.vB,
			data.vC,
			data.throttle,
			data.motorTemp,
			data.controllerTemp
			).map((d) -> String.format("%.03f", d))
			.collect(Collectors.joining(",", "", "\n"));
		
		csvWriter.write(s);
	}
	
	
	
	/**
	 * Creates a new log file in the specified directory
	 * 
	 * @param rootDir
	 * @return
	 */
	public static MotorLogger newLogger(File rootDir) throws IOException {
		String fileName = new SimpleDateFormat("YYYY-MM-dd_HH.mm.ss")
			.format(new Date());
		fileName += ".csv";
		
		File targetFile = new File(rootDir, fileName);
		return new MotorLogger(targetFile);
	}



	public void close(){
		try {
			csvWriter.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	public long getMillisRunning() {
		return System.currentTimeMillis() - startMillis;
	}


	public File getTargetFile() {
		return targetFile;
	}
}
