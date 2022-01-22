package com.jamesdpeters.buildtools.ui;

import com.jamesdpeters.buildtools.BuildToolsRun;
import com.jamesdpeters.buildtools.BuildToolsSettings;
import com.jamesdpeters.buildtools.Utils;
import com.jamesdpeters.buildtools.jdk.JdkChecker;
import com.jamesdpeters.buildtools.properties.Compile;
import com.jamesdpeters.buildtools.properties.Flags;
import com.jamesdpeters.buildtools.properties.Version;
import com.jamesdpeters.buildtools.properties.VersionUtil;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.controlsfx.control.CheckComboBox;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class UIController {

    private Stage stage;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private MenuBar menu;

    @FXML
    private TextField file_path;

    @FXML
    private Button file_chooser;

    @FXML
    private CheckComboBox<String> flags;

    @FXML
    private Button jdk_chooser;

    @FXML
    private TextField jdk_path;

    @FXML
    public ComboBox<String> version;

    @FXML
    private Button compile_button;

    @FXML
    private Label warning_message;

    @FXML
    private ComboBox<String> compile;

    private Map<String, Version> versionMap;

    @FXML
    void onCompileClick(ActionEvent event) {
        File jdkPath = new File(jdk_path.getText());
        if (jdkPath.exists()) {
            var jdkVersion = JdkChecker.getJdkVersion(jdkPath);
            if(Objects.isNull(jdkVersion)) {
                Alert error = new Alert(Alert.AlertType.ERROR, "Invalid JDK path provided, could not verify MAJOR version.");
                error.show();
                return;
            } else {
                var v = versionMap.get(version.getValue());
                if (v != null) {
                    if (!(jdkVersion >= v.javaVersions[0] && jdkVersion <= v.javaVersions[1])) {
                        Alert error = new Alert(Alert.AlertType.ERROR, "JDK version ("+jdkVersion+") is not compatible with "+version.getValue()+". Which requires min: "+ v.javaVersions[0]+" max: "+v.javaVersions[1]);
                        error.show();
                        return;
                    }
                }
            }
        }
        File buildToolsPath = new File(file_path.getText());
        if (buildToolsPath.exists() && buildToolsPath.isDirectory()) {
            BuildToolsSettings.setBuildToolsFolder(buildToolsPath);
            BuildToolsRun.compile(flags.getCheckModel().getCheckedItems(), version.getValue(), compile.getValue(), processListener);
            compile_button.setDisable(true);
            compile_button.setText("Compiling");
        } else {
            Alert error = new Alert(Alert.AlertType.ERROR, "Please enter a valid BuildTools path and ensure the folder exists.");
            error.show();
        }
    }

    @FXML
    void openFileChooser(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(BuildToolsSettings.getBuildToolsFolder());
        File chosenDirectory = directoryChooser.showDialog(stage);
        if(chosenDirectory != null) {
            BuildToolsSettings.setBuildToolsFolder(chosenDirectory);
            file_path.setText(BuildToolsSettings.getBuildToolsFolder().getAbsolutePath());
        }
    }

    @FXML
    void onFilePathTextEnter(ActionEvent event){
    }

    @FXML
    void onBuildToolsHelp(ActionEvent event) {
        try {
            Utils.openWebpage(new URI("https://www.spigotmc.org/wiki/buildtools/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onSetBuildTools(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(BuildToolsSettings.getBuildToolsFolder());
        File file = fileChooser.showOpenDialog(stage);
        if (file != null && file.exists() && file.isFile()) {
            BuildToolsSettings.setBuildToolsJar(file);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a valid file for the BuildTools jar");
            alert.show();
        }
    }

    @FXML
    void onQuit(ActionEvent event){
        BuildToolsRun.destroyProcess();
        stage.close();
        System.exit(0);
    }

    @FXML
    void onResetBuildToolsFolder(){
        File buildToolsPath = new File(file_path.getText());
        if (buildToolsPath.exists() && buildToolsPath.isDirectory()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "This will reset the contents of the currently selected BuildTools folder: " + buildToolsPath.getAbsolutePath(), ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait();
            if(alert.getResult() == ButtonType.OK){
                try {
                    FileUtils.cleanDirectory(buildToolsPath);
                } catch (IOException e) {
                    ExceptionAlert error = new ExceptionAlert(e, "Unable to clean BuildTools folder");
                    error.show();
                }
            }
        }
    }

    @FXML
    void onOpenBuildToolsFolder(){
        try {
            Desktop.getDesktop().open(BuildToolsSettings.getBuildToolsFolder());
        } catch (IOException e) {
            ExceptionAlert alert = new ExceptionAlert(e, "Failed to open BuildTools folder");
            alert.show();
        }
    }

    @FXML
    void initialize() {
        assert menu != null : "fx:id=\"menu\" was not injected: check your FXML file 'filechooser.fxml'.";
        assert file_path != null : "fx:id=\"file_path\" was not injected: check your FXML file 'filechooser.fxml'.";
        assert file_chooser != null : "fx:id=\"file_chooser\" was not injected: check your FXML file 'filechooser.fxml'.";
        assert flags != null : "fx:id=\"flags\" was not injected: check your FXML file 'filechooser.fxml'.";
        assert version != null : "fx:id=\"version\" was not injected: check your FXML file 'filechooser.fxml'.";
        assert compile_button != null : "fx:id=\"compile_button\" was not injected: check your FXML file 'filechooser.fxml'.";

        menu.setUseSystemMenuBar(true);
        file_path.setText(BuildToolsSettings.getBuildToolsFolder().getAbsolutePath());
        flags.getItems().addAll(Flags.toList());
        flags.getCheckModel().getCheckedItems().addListener(listener);
        version.setValue("latest");
        compile.getItems().addAll(Compile.toList());
        compile.setValue(Compile.SPIGOT.toString());
        jdk_path.setText(BuildToolsSettings.getJdkPath().getAbsolutePath());
    }

    //This checks if the --dev flag is enabled and disables the ability to select a version.
    private final ListChangeListener<String> listener = c -> {
        boolean containsDev = flags.getCheckModel().getCheckedItems().contains(Flags.DEV.toString());
        version.setDisable(containsDev);
        warning_message.setVisible(containsDev);
        warning_message.setText("Version selection is disabled with the --dev flag");
    };

    // Listener for when BuildTools has finished
    private final BuildToolsRun.ProcessListener processListener = () -> {
        compile_button.setDisable(false);
        compile_button.setText("Compile");
    };

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void updateVersions() throws IOException {
        this.versionMap = VersionUtil.getVersions();
        filterVersions();
    }

    public void filterVersions() {
        var stringVersions = new ArrayList<>(versionMap.keySet());
        version.getItems().clear();
        version.getItems().addAll(stringVersions);
        version.getItems().sort(VersionUtil.versionComparator);
    }

    public void openJDKChooser(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(BuildToolsSettings.getJdkPath());
        File chosenDirectory = directoryChooser.showDialog(stage);
        if(chosenDirectory != null) {
            Integer version = JdkChecker.getJdkVersion(chosenDirectory);
            if (version != null) {
                BuildToolsSettings.setJdkPath(chosenDirectory);
                jdk_path.setText(BuildToolsSettings.getJdkPath().getAbsolutePath());
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Using JDK Major version: "+version);
                alert.show();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR, "Invalid JDK path provided, could not verify MAJOR version.");
                error.show();
            }
        }
    }
}
