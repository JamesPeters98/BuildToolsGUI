package com.jamesdpeters.buildtools.ui;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class ConsoleStream {

    private ConsoleTextArea consoleTextArea;
    private InputStream inputStream, errorStream;

    public ConsoleStream(TextFlow textArea, InputStream input, InputStream error){
        this.consoleTextArea = new ConsoleTextArea(textArea);
        this.inputStream = input;
        this.errorStream = error;

        new Thread(new StreamGobbler(inputStream, consoleTextArea::appendText)).start();
        new Thread(new StreamGobbler(errorStream, consoleTextArea::appendError)).start();
    }

    static class ConsoleTextArea {

        TextFlow textArea;

        public ConsoleTextArea(TextFlow textArea){
            this.textArea = textArea;
        }

        public void appendText(String text) {
            Text line = new Text();
            line.setText(text+"\n");
            line.setStyle("-fx-fill: white");
            Platform.runLater(() -> textArea.getChildren().add(line));
        }

        public void appendError(String error){
            Text line = new Text();
            line.setText(error+"\n");
            line.setStyle("-fx-fill: red");
            Platform.runLater(() -> textArea.getChildren().add(line));
        }
    }

    static class StreamGobbler implements Runnable {
        private final InputStream inputStream;
        private final Consumer<String> consumeInputLine;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumeInputLine) {
            this.inputStream = inputStream;
            this.consumeInputLine = consumeInputLine;
        }

        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumeInputLine);
        }
    }

    public ConsoleTextArea getConsoleTextArea() {
        return consoleTextArea;
    }
}
