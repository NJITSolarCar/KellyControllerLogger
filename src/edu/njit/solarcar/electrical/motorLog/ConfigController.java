package edu.njit.solarcar.electrical.motorLog;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import edu.njit.solarcar.electrical.motorLog.util.ExceptionAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ConfigController
{
	
	@FXML
	private ResourceBundle resources;
	
	@FXML
	private URL location;
	
	@FXML
	private TextField baudField;
	
	@FXML
	private TextField controllerCanIdField;
	
	@FXML
	private TextField controllerResponseIdField;
	
	@FXML
	private TextField samplingFreqField;
	
	@FXML
	private TextField samplePeriodField;
	
	@FXML
	private TextField logDirectoryField;
	
	@FXML
	private TextField plotTimeField;
	
	@FXML
	private TextField motorPolesField;
	
	private static ConfigController impl;
	private Stage stage;
	private ConfigData selection;
	private ConfigData orig;
	
	
	
	public static ConfigController getController() {
		if (impl == null)
			impl = new ConfigController();
		return impl;
	}
	
	
	
	private ConfigController() {
		FXMLLoader ldr = new FXMLLoader();
		ldr.setController(this);
		ldr.setLocation(getClass().getResource("Config.fxml"));
		stage = new Stage();
		try {
			stage.setScene(new Scene(ldr.load()));
		}
		catch (IOException e) {
			Alert a = new ExceptionAlert(e, "Failed to load Config View");
			a.showAndWait();
		}
		stage.setOnCloseRequest((event) -> stage.close());
	}
	
	
	
	private boolean parseFields() {
		ConfigData d = new ConfigData();
		
		// try to parse is
		try {
			d.baud = Integer.parseInt(baudField.getText());
			d.controllerCanId = Integer.decode(controllerCanIdField.getText());
			d.controllerResponseId = Integer
				.decode(controllerResponseIdField.getText());
			d.samplingFreq = Double.parseDouble(samplingFreqField.getText());
			d.samplePeriod = Integer.parseInt(samplePeriodField.getText());
			d.motorPoles = Integer.parseInt(motorPolesField.getText());
			d.plotTime = Double.parseDouble(plotTimeField.getText());
			
			// This will tell if the path is valid. Will throw an exception if the
			// path is bad. NOTE: doesn't work in linux
			Paths.get(logDirectoryField.getText());
			d.logDir = new File(logDirectoryField.getText());
			
			selection = d;
			return true;
		}
		catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setHeaderText("Please enter valid values");
			a.showAndWait();
			
			setFields(orig);
			return false;
		}
	}
	
	
	
	private void setFields(ConfigData d) {
		baudField.setText(String.valueOf(d.baud));
		controllerCanIdField.setText(String.format("0x%X", d.controllerCanId));
		controllerResponseIdField
			.setText(String.format("0x%X", d.controllerResponseId));
		samplingFreqField.setText(String.format("%.1f", d.samplingFreq));
		samplePeriodField.setText(String.valueOf(d.samplePeriod));
		motorPolesField.setText(String.valueOf(d.motorPoles));
		plotTimeField.setText(String.valueOf(d.plotTime));
		logDirectoryField.setText(d.logDir.getAbsolutePath());
	}
	
	
	
	/**
	 * Displays a config dialog, pre-populated with the data in initial
	 * 
	 * @param initial
	 * @return a new {@link ConfigData} if the configurations changed, false
	 *         otherwise
	 */
	public ConfigData promptConfig(ConfigData initial) {
		this.orig = initial;
		this.selection = null;
		
		setFields(initial);
		stage.showAndWait();
		
		return selection;
	}
	
	
	
	@FXML
	void save(ActionEvent event) {
		// if true, the user's data is good, so the dialog can be closed
		if (parseFields())
			stage.close();
	}
	
	
	
	@FXML
	void cancel(ActionEvent event) {
		selection = null;
		stage.close();
	}
	
	
	
	@FXML
	void browse(ActionEvent event) {
		DirectoryChooser dc = new DirectoryChooser();
		File initial = AppController.readConfig().logDir;
		
		// Get the file in the box if it's valid
		try {
			initial = Paths.get(logDirectoryField.getText()).toFile();
		}
		catch (Exception e) {}
		
		dc.setInitialDirectory(initial);
		dc.setTitle("Choose Logging Directory");
		
		File selected = dc.showDialog(stage);
		if (
			selected != null
			)
			logDirectoryField.setText(selected.getAbsolutePath());
	}
}
