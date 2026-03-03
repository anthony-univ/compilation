package fr.ufrst.m1info.comp4.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Ide extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(Ide.class.getResource("ide-view.fxml"));

        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setMaximized(true);
        scene.getStylesheets().add(Ide.class.getResource("stylesheets/dark_theme.css").toExternalForm());

        stage.setTitle("IDE MiniJaja");
        stage.getIcons().add(new Image(Ide.class.getResource("images/logo.png").toExternalForm()));
        stage.setScene(scene);

        Controller controller = loader.getController();
        controller.setStage(stage);

        stage.show();
    }
}
