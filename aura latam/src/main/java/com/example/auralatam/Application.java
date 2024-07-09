package com.example.auralatam;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Cargar la interfaz gráfica desde el archivo FXML
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/auralatam/diseño.fxml")));
        primaryStage.setTitle("Auralatam Currency Converter");
        primaryStage.setScene(new Scene(root, 654, 351));
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Lanzar la aplicación JavaFX
        launch(args);
    }
}
