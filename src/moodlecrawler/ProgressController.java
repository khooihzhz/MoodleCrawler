package moodlecrawler;

import javafx.animation.RotateTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ProgressController {
    @FXML
    private Circle c1;

    @FXML
    private Circle c2;

    @FXML
    private Circle c3;

    public void initialize() throws InterruptedException {

        setRotate(c1, true, 360, 10);
        setRotate(c2, true, 180, 15);
        setRotate(c3, true, 145, 20);


        Task<Void> downloadTask = new DownloadFilesTask();
        new Thread(downloadTask).start();

        downloadTask.setOnSucceeded(workerStateEvent ->
        {
            System.out.println("download complete");
        });

        downloadTask.setOnFailed(workerStateEvent -> {
            System.out.println("Something went wrong");
        });

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
}
