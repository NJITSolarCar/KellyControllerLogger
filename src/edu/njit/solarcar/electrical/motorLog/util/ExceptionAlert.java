package edu.njit.solarcar.electrical.motorLog.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;

public class ExceptionAlert extends Alert
{
	
	public ExceptionAlert(Throwable t, String message) {
		super(AlertType.ERROR);
		this.setHeaderText(message);
		
		// Get the Stack trace as a string
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);

		this.setContentText(sw.toString());
	}
	
}
