package edu.njit.solarcar.electrical.motorLog;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ConfigController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField controllerCanIdField;

    @FXML
    private TextField samplingRateField;

    @FXML
    private TextField samplePeriodField;

    @FXML
    private TextField logDirectoryField;

    @FXML
    private Button browseButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    void initialize() {
        assert controllerCanIdField != null : "fx:id=\"controllerCanIdField\" was not injected: check your FXML file 'Config.fxml'.";
        assert samplingRateField != null : "fx:id=\"samplingRateField\" was not injected: check your FXML file 'Config.fxml'.";
        assert samplePeriodField != null : "fx:id=\"samplePeriodField\" was not injected: check your FXML file 'Config.fxml'.";
        assert logDirectoryField != null : "fx:id=\"logDirectoryField\" was not injected: check your FXML file 'Config.fxml'.";
        assert browseButton != null : "fx:id=\"browseButton\" was not injected: check your FXML file 'Config.fxml'.";
        assert saveButton != null : "fx:id=\"saveButton\" was not injected: check your FXML file 'Config.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'Config.fxml'.";

    }
}
