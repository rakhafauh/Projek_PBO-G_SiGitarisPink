package com.bocchipet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            URL fxmlUrl = getClass().getResource("/com/bocchipet/fxml/MainGameView.fxml");
            if (fxmlUrl == null) {
                System.err.println("Gak nemu file FXML. Path harus bener.");
                setupEmptyStage(primaryStage);
                return;
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root);

            primaryStage.setTitle("Bocchi Pet Game");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupEmptyStage(Stage primaryStage) {
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
        root.getChildren().add(new javafx.scene.control.Label("MainGameView.fxml belum dibuat."));
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Bocchi Pet Game (Dev Mode)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}