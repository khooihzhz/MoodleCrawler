package moodlecrawler;

import javafx.animation.RotateTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;


public class ProgressController {
    // FX COMPONENTS
    @FXML private Circle c1;
    @FXML private Circle c2;
    @FXML private Circle c3;
    @FXML private Circle c4;
    @FXML private Text dlText;
    @FXML private Button contButton;

    // VARIABLE
    public static Parent fourthStage;

    public void initialize() {
        // HIDE CONTINUE BUTTON
        contButton.setVisible(false);
        // START ROTATING CIRCLE
        setRotate(c1, false, 360, 10);
        setRotate(c2, false, -360, 15);
        setRotate(c3, false, 360, 10);
        setRotate(c4, false, -360, 15);


        // START DOWNLOADING THREAD
        Task<Void> downloadTask = new DownloadFilesTask();
        new Thread(downloadTask).start();

        // WHEN THREAD IS FINISHED, PREPARE TO LOAD NEXT SCENE
        downloadTask.setOnSucceeded(workerStateEvent ->
        {
            dlText.setVisible(false);
            contButton.setVisible(true);
        });

        downloadTask.setOnFailed(workerStateEvent -> System.out.println("Something went wrong"));

    }

    private void setRotate(Circle c, boolean reverse, int angle, int duration) {
        RotateTransition rt = new RotateTransition(Duration.seconds(duration), c);

        rt.setAutoReverse(reverse);
        rt.setByAngle(angle);
        rt.setDelay(Duration.seconds(0));
        rt.setRate(3);
        rt.setCycleCount(1000);
        rt.play();
    }

    public void setContButton(ActionEvent e) { loadNextScene("courselist.fxml");}

    // LOAD TO NEXT SCENE FUNCTION
    private void loadNextScene(String fxml) {
        try {
            fourthStage = FXMLLoader.load(Main.class.getResource(fxml));
            Scene newScene = new Scene(fourthStage);
            Stage currentStage = (Stage) CourseListController.thirdStage.getScene().getWindow();
            currentStage.setScene(newScene);
        } catch (IOException e) {
            // do nothing
        }
    }
}
