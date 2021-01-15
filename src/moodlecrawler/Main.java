package moodlecrawler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("loginpage.fxml"));
        primaryStage.setTitle("Moodle Crawler");
        primaryStage.getIcons().add(new Image("resources/spiderweb.png"));
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 775, 510));
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}