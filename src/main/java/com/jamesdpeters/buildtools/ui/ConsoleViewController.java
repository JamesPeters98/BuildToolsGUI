package com.jamesdpeters.buildtools.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class ConsoleViewController {

    public boolean hasFinished = false;
    private Stage stage;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    public TextFlow console;

    @FXML
    private ScrollPane scroll_pane;

    @FXML
    void initialize() {
        assert console != null : "fx:id=\"console\" was not injected: check your FXML file 'console.fxml'.";

        scroll_pane.vvalueProperty().bind(console.heightProperty());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }
}
