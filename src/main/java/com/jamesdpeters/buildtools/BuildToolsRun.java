package com.jamesdpeters.buildtools;

import com.jamesdpeters.buildtools.properties.Flags;
import com.jamesdpeters.buildtools.ui.ConsoleStream;
import com.jamesdpeters.buildtools.ui.ConsoleViewController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class BuildToolsRun {

    private static final String buildTools = "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar";
    private static Process compileProcess;

    public static void compile(List<String> flags, String version, String compileVersion, ProcessListener listener){
        try {
            ConsoleViewController viewController = BuildToolsGUI.showConsole();

            Runnable runnable = () -> {
                // Download latest version of BuildTools
                download();

                // Run BuildTools
                try {
                    String v = version == null ? "latest" : version;

                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.directory(BuildToolsSettings.getBuildToolsFolder());
                    processBuilder.command("java", "-jar", BuildToolsSettings.getBuildToolsJar().getAbsolutePath());

                    if (!flags.contains(Flags.DEV.toString())){
                        processBuilder.command().add("--rev");
                        processBuilder.command().add(v);
                    }

                    for (String flag : flags) {
                        processBuilder.command().add("--"+flag);
                    }

                    processBuilder.command().add("--compile");
                    processBuilder.command().add(compileVersion);

                    compileProcess = processBuilder.start();
                    new ConsoleStream(viewController.console, compileProcess.getInputStream(), compileProcess.getErrorStream());

                    // Wait for BuildTools to finish
                    int exitCode = compileProcess.waitFor();
                    viewController.hasFinished = true;
                    Platform.runLater(listener::onProcessFinished);

                    // Show confirmation alert
                    if(exitCode == 0){
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                                    "BuildTools has successfully compiled into "+BuildToolsSettings.getBuildToolsFolder().getAbsolutePath()+"\n" +
                                            "Do you want to close the console and open the BuildTools folder?", ButtonType.YES, ButtonType.NO);
                            alert.showAndWait();

                            if(alert.getResult() == ButtonType.YES){
                                viewController.getStage().close();
                                try {
                                    Desktop.getDesktop().open(BuildToolsSettings.getBuildToolsFolder());
                                } catch (IOException e) {}
                            } else {
                                alert.close();
                            }
                        });
                    } else {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR,
                                    "BuildTools failed to compile, check the console for more information", ButtonType.OK);
                            alert.showAndWait();
                            alert.close();
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void destroyProcess(){
        if(compileProcess != null) compileProcess.destroy();
    }

    /**
     * Downloads the latest version of BuildTools
     */
    private static void download(){
        try {
            InputStream is = new URL(buildTools).openStream();
            File btFile = BuildToolsSettings.getBuildToolsJar();
            if(!btFile.exists()) {
                btFile.mkdirs();
                btFile.createNewFile();
            }
            Files.copy(is, Paths.get(btFile.toURI()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface ProcessListener {
        void onProcessFinished();
    }
}
