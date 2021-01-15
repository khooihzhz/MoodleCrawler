package moodlecrawler;

import eu.hansolo.enzo.notification.Notification;
import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;


public class ProgressController {
    // FX COMPONENTS
    @FXML private AnchorPane root;
    @FXML private Circle c1;
    @FXML private Circle c2;
    @FXML private Circle c3;
    @FXML private Circle c4;
    @FXML private Text dlText;
    @FXML private Button contButton;


    public void initialize() {
        // HIDE CONTINUE BUTTON
        contButton.setVisible(false);
        // START ROTATING CIRCLE
        setRotate(c1, false, 360, 10);
        setRotate(c2, false, -360, 15);
        setRotate(c3, false, 360, 10);
        setRotate(c4, false, -360, 15);
        Polyline polyline1 = new Polyline();
        polyline1.getPoints().addAll(0.0, 0.0, 400.0, 150.0);
        Polyline polyline2 = new Polyline();
        polyline2.getPoints().addAll(0.0, 0.0, 500.0, 200.0);
        Polyline polyline3 = new Polyline();
        polyline3.getPoints().addAll(0.0, 0.0, 400.0, 200.0);


        // START DOWNLOADING THREAD
        Task<Void> downloadTask = new DownloadFilesTask();
        new Thread(downloadTask).start();

        // WHEN THREAD IS FINISHED, PREPARE TO LOAD NEXT SCENE
        downloadTask.setOnSucceeded(workerStateEvent ->
        {
            dlText.setVisible(false);
            contButton.setVisible(true);
            Notification.Notifier.setPopupLocation(null, Pos.BOTTOM_RIGHT );
            Notification.Notifier.INSTANCE.notifySuccess("Download Completed",
                    "Files are saved to DownloadedFile Folder");
        });
    }

    private void setRotate(Circle c, boolean reverse, int angle, int duration) {
        RotateTransition rt = new RotateTransition(Duration.seconds(duration), c);

        rt.setAutoReverse(reverse);
        rt.setByAngle(angle);
        rt.setDelay(Duration.seconds(0));
        rt.setCycleCount(Animation.INDEFINITE);
        rt.setRate(3);
        rt.play();
    }

    public void setContButton() { loadNextScene("viewfile.fxml"); }

    // LOAD TO NEXT SCENE FUNCTION
    private void loadNextScene(String fxml) {
        try {
            Parent fourthStage = FXMLLoader.load(Main.class.getResource(fxml));
            Scene newScene = new Scene(fourthStage);
            Stage currentStage = (Stage) root.getScene().getWindow();
            currentStage.setScene(newScene);
        } catch (IOException e) {
            // do nothing
        }
    }
}
