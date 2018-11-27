/**
 * Sample Skeleton for 'MainWindow.fxml' Controller Class
 */

package edu.njit.solarcar.electrical.motorLog;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class MainWindow {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="rpmChart"
    private LineChart<?, ?> rpmChart; // Value injected by FXMLLoader

    @FXML // fx:id="rpmLabel"
    private Label rpmLabel; // Value injected by FXMLLoader

    @FXML // fx:id="totalCurrentLabel"
    private Label totalCurrentLabel; // Value injected by FXMLLoader

    @FXML // fx:id="phaseACurrentLabel"
    private Label phaseACurrentLabel; // Value injected by FXMLLoader

    @FXML // fx:id="phaseBCurrentLabel"
    private Label phaseBCurrentLabel; // Value injected by FXMLLoader

    @FXML // fx:id="phaseCCurrentLabel"
    private Label phaseCCurrentLabel; // Value injected by FXMLLoader

    @FXML // fx:id="currentChart"
    private LineChart<?, ?> currentChart; // Value injected by FXMLLoader

    @FXML // fx:id="totalCurrentCheck"
    private CheckBox totalCurrentCheck; // Value injected by FXMLLoader

    @FXML // fx:id="phaseACurrentCheck"
    private CheckBox phaseACurrentCheck; // Value injected by FXMLLoader

    @FXML // fx:id="phaseBCurrentCheck"
    private CheckBox phaseBCurrentCheck; // Value injected by FXMLLoader

    @FXML // fx:id="phaseCCurrentCheck"
    private CheckBox phaseCCurrentCheck; // Value injected by FXMLLoader

    @FXML // fx:id="comPortLabel"
    private Label comPortLabel; // Value injected by FXMLLoader

    @FXML // fx:id="canIdLabel"
    private Label canIdLabel; // Value injected by FXMLLoader

    @FXML // fx:id="logFileLabel"
    private Label logFileLabel; // Value injected by FXMLLoader

    @FXML // fx:id="logTimeLabel"
    private Label logTimeLabel; // Value injected by FXMLLoader

    @FXML
    void clearCharts(ActionEvent event) {

    }

    @FXML
    void connectToController(ActionEvent event) {

    }

    @FXML
    void disconnectFromController(ActionEvent event) {

    }

    @FXML
    void newLogFile(ActionEvent event) {

    }

    @FXML
    void startLog(ActionEvent event) {

    }

    @FXML
    void stopLog(ActionEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert rpmChart != null : "fx:id=\"rpmChart\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert rpmLabel != null : "fx:id=\"rpmLabel\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert totalCurrentLabel != null : "fx:id=\"totalCurrentLabel\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert phaseACurrentLabel != null : "fx:id=\"phaseACurrentLabel\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert phaseBCurrentLabel != null : "fx:id=\"phaseBCurrentLabel\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert phaseCCurrentLabel != null : "fx:id=\"phaseCCurrentLabel\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert currentChart != null : "fx:id=\"currentChart\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert totalCurrentCheck != null : "fx:id=\"totalCurrentCheck\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert phaseACurrentCheck != null : "fx:id=\"phaseACurrentCheck\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert phaseBCurrentCheck != null : "fx:id=\"phaseBCurrentCheck\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert phaseCCurrentCheck != null : "fx:id=\"phaseCCurrentCheck\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert comPortLabel != null : "fx:id=\"comPortLabel\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert canIdLabel != null : "fx:id=\"canIdLabel\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert logFileLabel != null : "fx:id=\"logFileLabel\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert logTimeLabel != null : "fx:id=\"logTimeLabel\" was not injected: check your FXML file 'MainWindow.fxml'.";

    }
}
