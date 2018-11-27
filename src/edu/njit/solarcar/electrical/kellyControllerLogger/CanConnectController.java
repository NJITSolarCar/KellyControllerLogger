package edu.njit.solarcar.electrical.kellyControllerLogger;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class CanConnectController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ChoiceBox<?> comSelect;

    @FXML
    void cancelConnect(ActionEvent event) {

    }

    @FXML
    void connectToDevice(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert comSelect != null : "fx:id=\"comSelect\" was not injected: check your FXML file 'CanConnect.fxml'.";

    }
}
