package edu.njit.solarcar.electrical.motorLog;

import java.io.File;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

public class AppController extends Application
{
	private static final ConfigData DEFAULT_DATA = new ConfigData();
	private static Preferences prefs;
	private static ConfigData config = null;
	
	private static MainWindow mainWindowController;
	
	private static ComController comController;
	
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			// load default data
			DEFAULT_DATA.controllerCanId = 0x6B;
			DEFAULT_DATA.controllerResponseId = 0x73;
			DEFAULT_DATA.logDir = new File(System.getProperty("user.home"),
				"KellyControllerLogger");
			DEFAULT_DATA.motorPoles = 3;
			DEFAULT_DATA.samplePeriod = 5;
			DEFAULT_DATA.samplingFreq = 10;
			
			// Obtain a ref to the config
			prefs = Preferences.userRoot().node(getClass().getName());
			
			comController = new ComController(readConfig());
			
			// Load all the FXMl files
			FXMLLoader mainWindowLoader = new FXMLLoader(
				getClass().getResource("MainWindow.fxml"));
			BorderPane mainWindowRoot = (BorderPane) mainWindowLoader.load();
			mainWindowController = mainWindowLoader.getController();
			
			Scene scene = new Scene(mainWindowRoot);
			scene.getStylesheets()
				.add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
	
	public static MainWindow getMainWindowController() {
		return mainWindowController;
	}
	
	
	
	/**
	 * Attempts to read the configuration data from the system, or will return
	 * defaults if none is available
	 * 
	 * @return
	 */
	public static ConfigData readConfig() {
		if(config == null) {
			ConfigData d = new ConfigData();
			
			d.controllerCanId = prefs
				.getInt("controllerCanId", DEFAULT_DATA.controllerCanId);
			
			d.controllerResponseId = prefs
				.getInt("controllerResponseId", DEFAULT_DATA.controllerResponseId);
			
			d.samplingFreq = prefs
				.getDouble("samplingFreq", DEFAULT_DATA.samplingFreq);
			
			d.samplePeriod = prefs
				.getInt("samplePeriod", DEFAULT_DATA.samplePeriod);
			
			d.motorPoles = prefs
				.getInt("motorPoles", DEFAULT_DATA.motorPoles);
			
			// Handle root dir differently because we can't work straight with objects
			d.logDir = new File(
				prefs.get("logDir", DEFAULT_DATA.logDir.getAbsolutePath()));
			
			return d;
		}
		
		return config;
	}
	
	
	/**
	 * Writes configuration
	 * @param d
	 */
	public static void writeConfig(ConfigData d) {
		prefs.putInt("controllerCanId", d.controllerCanId);
		prefs.putInt("controllerResponseId", d.controllerResponseId);
		prefs.putDouble("samplingFreq", d.samplingFreq);
		prefs.putInt("samplePeriod", d.samplePeriod);
		prefs.put("logDir", d.logDir.getAbsolutePath());
		prefs.putInt("motorPoles", d.motorPoles);
		
		config = d;
	}



	public static ComController getComController() {
		return comController;
	}
}
