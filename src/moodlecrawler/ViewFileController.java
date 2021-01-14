package moodlecrawler;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;

public class ViewFileController {
    // FX COMPONENTS
    @FXML private AnchorPane root;
    @FXML private TreeView<String> treeView;
    @FXML private ImageView rubik;


    public void initialize() {
        // Set Rubik Cube to Rotate
        RotateTransition rt = new RotateTransition(Duration.millis(3000), rubik);
        rt.setAutoReverse(true);
        rt.setByAngle(360);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.play();

        // List file from the directory
        File downloadFile = new File(System.getProperty("user.dir") + "\\DownloadedFiles");
        treeView.setRoot(getNodesForDirectory(downloadFile));
    }

    // LIST FILES FUNCTION
    private TreeItem<String> getNodesForDirectory(File directory) {
        // GET THE TREE ITEM FROM THE FILE
        TreeItem<String> root = new TreeItem<>(directory.getName());
        // LOOP THROUGH THE DIRECTORY
        for (File file : directory.listFiles()) {
            // IF IT IS DIRECTORY, CALL THE FUNCTION RECURSIVELY
            if (file.isDirectory()) {
                root.getChildren().add(getNodesForDirectory(file));
            }
            // ELSE, ADD ITEM TO THE TREE
            else {
                root.getChildren().add(new TreeItem<>(file.getName()));
            }
        }
        return root;
    }

    // SET BUTTON TO NAVIGATE TO HOME
    public void setBtn_home() throws IOException { loadNextScene("courselist.fxml"); }

    // SET BUTTON TO LOGOUT
    public void setBtn_logout() throws IOException { loadNextScene("loginpage.fxml"); }

    // SET BUTTON TO CLOSE THE APPLICATION
    public void setBtn_exit(ActionEvent event) {
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    // LOAD TO NEXT SCENE FUNCTION
    private void loadNextScene(String fxml) throws IOException {
        Parent lastStage = FXMLLoader.load(Main.class.getResource(fxml));
        Scene newScene = new Scene(lastStage);
        Stage currentStage = (Stage) root.getScene().getWindow();
        currentStage.setScene(newScene);
    }
}
