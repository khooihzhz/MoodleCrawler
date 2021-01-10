package moodlecrawler;

import javafx.concurrent.Task;
import org.openqa.selenium.WebDriver;

public class DownloadFilesTask extends Task<Void> {
    // make courseURL unchangeable for each task
    private final String courseURL;

    public DownloadFilesTask(String courseURL) {
        this.courseURL = courseURL;
    }

    @Override
    protected  Void call() throws Exception {
        // START DOWNLOAD
        WebDriver driver = SetupCrawler.setupCrawler("");
        //SetupCrawler.modifyMoodleCookies(driver);
        return null;
    }
}
