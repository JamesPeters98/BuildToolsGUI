package com.jamesdpeters.buildtools;

import com.jamesdpeters.buildtools.properties.Version;
import com.jamesdpeters.buildtools.ui.ConsoleViewController;
import com.jamesdpeters.buildtools.ui.UIController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class BuildToolsGUI extends Application {

    private static Stage primaryStage;
    private static boolean canClose = false;


    @Override
    public void start(Stage primaryStage) throws Exception{
        BuildToolsGUI.primaryStage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/filechooser.fxml"));
        Parent root = loader.load();
        UIController controller = loader.getController();
        controller.setStage(primaryStage);

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/spigot-og.png")));
        primaryStage.setTitle("Build Tools GUI");
        primaryStage.setScene(new Scene(root, 400, 220));
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnHiding(event -> {
            BuildToolsRun.destroyProcess();
            System.exit(0);
        });

        // Blocking call for versions
        Alert loading = new Alert(Alert.AlertType.INFORMATION, "Loading latest versions");
        loading.initModality(Modality.APPLICATION_MODAL);
        loading.setOnCloseRequest(e ->{
            if(!canClose) e.consume();
        });
        loading.show();
        try {
            List<String> versions = Version.getVersions();
            versions.add(0, "latest");
            controller.version.getItems().addAll(versions);
            canClose = true;
            loading.close();
        } catch (Exception e){}

    }

    public static ConsoleViewController showConsole() throws IOException {
        FXMLLoader loader = new FXMLLoader(BuildToolsGUI.class.getResource("/console.fxml"));
        Parent root = loader.load();
        ConsoleViewController controller = loader.getController();

        Scene consoleScene = new Scene(root, 720, 480);
        Stage consoleStage = new Stage();
        controller.setStage(consoleStage);
        consoleStage.getIcons().add(new Image(BuildToolsGUI.class.getResourceAsStream("/spigot-og.png")));
        consoleStage.setTitle("Console");
        consoleStage.setX(primaryStage.getX()+200);
        consoleStage.setY(primaryStage.getY()+100);
        consoleStage.setScene(consoleScene);
        consoleStage.show();

        consoleStage.setOnCloseRequest(event -> {
            if(controller.hasFinished){
                return;
            }
            Alert alert = new Alert(Alert.AlertType.WARNING, "Are you sure you want to cancel compilation?", ButtonType.YES, ButtonType.CANCEL);
            alert.showAndWait();

            if(alert.getResult() == ButtonType.YES){
                BuildToolsRun.destroyProcess();
            } else {
                event.consume();
            }
        });

        return controller;
    }


    public static void main(String[] args) {
        System.out.println("Opening file");
        launch(args);
    }
}
