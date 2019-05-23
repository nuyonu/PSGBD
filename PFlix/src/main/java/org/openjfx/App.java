package org.openjfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    private static final String path = "src/main/resources/org/openjfx/";

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("secondary"));
        File css = new File(path + "stylesheets/primary.css");
        scene.getStylesheets().clear();
        scene.getStylesheets().add("file:///" + css.getAbsolutePath().replace("\\", "/"));
        File iconFile = new File(path + "images/logo.png");
        Image icon = new Image("file:///" + iconFile.getAbsolutePath().replace("\\", "/"));
        stage.setScene(scene);
        stage.setTitle("PFlix");
        stage.getIcons().add(icon);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}