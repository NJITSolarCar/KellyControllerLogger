package edu.njit.solarcar.electrical.motorLog;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import edu.njit.solarcar.electrical.motorLog.util.ExceptionAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import jssc.SerialPortList;

public class CanConnectController
{
	@FXML
	private ResourceBundle resources;
	
	@FXML
	private URL location;
	
	@FXML
	private ChoiceBox<String> comSelect;
	
	@FXML
	private Button connectBtn;
	
	private static CanConnectController impl;
	private Stage stage;
	private String selectedPort;
	
	
	
	public static CanConnectController getController() {
		if (impl == null)
			impl = new CanConnectController();
		return impl;
	}
	
	
	
	public CanConnectController() {
		FXMLLoader ldr = new FXMLLoader();
		ldr.setController(this);
		ldr.setLocation(getClass().getResource("CanConnect.fxml"));
		stage = new Stage();
		try {
			stage.setScene(new Scene(ldr.load()));
		}
		catch (IOException e) {
			Alert a = new ExceptionAlert(e, "Failed to load CanConnect view");
			a.showAndWait();
		}
		stage.setOnCloseRequest((event) -> stage.close());
	}
	
	
	
	@FXML
	void cancelConnect(ActionEvent event) {
		stage.close();
	}
	
	
	
	@FXML
	void connectToDevice(ActionEvent event) {
		selectedPort = comSelect.getSelectionModel().getSelectedItem();
		stage.close();
	}
	
	
	
	/**
	 * Prompts the user to select a communications port to use for
	 * the CAN adapter
	 * 
	 * @return the selected port, or null if none was selected
	 */
	public String promptSelectPort() {
		// Show all the ports
		String[] ports = SerialPortList.getPortNames();
		comSelect.itemsProperty().get().clear();
		comSelect.itemsProperty().get().addAll(ports);
		
		connectBtn.setDisable(ports.length == 0);
		
		selectedPort = null;
		stage.showAndWait();
		
		return selectedPort;
	}
	
}
