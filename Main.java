package org.sysproduto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.sysproduto.utils.PathFXML;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(new FileInputStream(PathFXML.pathBase() + File.separator + "main-view.fxml"));
        Scene scene = new Scene(root, 1080, 680);
        stage.setTitle("Sistema de Gerenciamento de Produtos - POO III");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}