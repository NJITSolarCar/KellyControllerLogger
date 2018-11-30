package edu.njit.solarcar.electrical.motorLog.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

public class ExceptionAlert extends Alert
{
	private static final double HEIGHT = 300.0;
	private static final double WIDTH = 700.0;
	
	public ExceptionAlert(Throwable t, String message) {
		super(AlertType.ERROR);
		this.setHeaderText(message);
		
		TextArea content = new TextArea();
		// content.setPrefWidth(WIDTH);
		content.setPrefWidth(WIDTH);
		content.setPrefHeight(HEIGHT);
		content.setEditable(false);
		
		// Get the Stack trace as a string
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);

		content.setText(sw.toString());
		this.getDialogPane().setContent(content);
//		});
	}
	
}
