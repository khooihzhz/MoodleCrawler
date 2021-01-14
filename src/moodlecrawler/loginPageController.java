package moodlecrawler;

import javafx.animation.RotateTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.*;

public class loginPageController {
    // FX COMPONENTS
    @FXML private TextField stu_email;
    @FXML private PasswordField stu_password;
    @FXML private Button submitButton;
    @FXML private Circle c;

    // VARIABLE
    public static Set<Cookie> moodleCookies;
    public static Thread backgroundThread = new Thread(new StartUpThread());
    public static Parent secondStage;

    public void initialize() {
        c.setVisible(false);
        backgroundThread.start();
    }

    // SHOW ALERT METHOD
    private static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }

    // LOADING INDICATOR
    private void setRotate(Circle c, boolean reverse, int angle, int duration) {
        RotateTransition rt = new RotateTransition(Duration.seconds(duration), c);

        rt.setAutoReverse(reverse);
        rt.setByAngle(angle);
        rt.setDelay(Duration.seconds(0));
        rt.setRate(3);
        rt.setCycleCount(10);
        rt.play();
    }

    // LOAD NEXT SCENE
    private void loadNextScene(String fxml) {
        try {
            secondStage = FXMLLoader.load(Main.class.getResource(fxml));
            Scene newScene = new Scene(secondStage);
            Stage currentStage = (Stage) Main.root.getScene().getWindow();
            currentStage.setScene(newScene);
        } catch (IOException e) {
            // do nothing
        }
    }

    // LOGIN
    public void login(ActionEvent event) throws InterruptedException {

        String userEmail = stu_email.getText();
        String userPassword = stu_password.getText();
        backgroundThread.join();
        c.setVisible(true);
        setRotate(c, false, 360, 3);
        Task <Void> GetMoodleCookiesTask = new GetMoodleCookiesTask(userEmail, userPassword);
        new Thread(GetMoodleCookiesTask).start();

        GetMoodleCookiesTask.setOnSucceeded(workerStateEvent -> {
            Window owner = submitButton.getScene().getWindow();
            c.setVisible(false);
            showAlert(Alert.AlertType.ERROR, owner, "Login Failed",
                    "Invalid email or password!\nPlease try again to login.");
            stu_email.clear();
            stu_password.clear();

        });

        GetMoodleCookiesTask.setOnFailed(workerStateEvent -> {
            UserWebDriver userDriver = UserWebDriver.getInstance();
            WebDriver driver = userDriver.getWebDriver();
            moodleCookies = driver.manage().getCookies();
            UserCookie userCookie = UserCookie.getInstance();
            userCookie.setUserCookie(moodleCookies);
            getCourseList();
            // SWITCH TO NEXT SCENE
            loadNextScene("courselist.fxml");
        });
    }

    private void getCourseList() {

        LinkedHashMap <String, String> courseMap = new LinkedHashMap<>();

        UserWebDriver userDriver = UserWebDriver.getInstance();
        WebDriver driver = userDriver.getWebDriver();

        WebDriverWait wait = new WebDriverWait(driver, 2);

        // ----- START FIND COURSE LIST ------
        // wait for page load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.courselist_course.scrollable")));
        List<WebElement> courseLinks = driver.findElements(By.cssSelector("a.courselist_course.scrollable"));

        // STEP 6 : LOOP THROUGH EACH LINK
        // REMEMBER COURSE LINKS
        for (WebElement course : courseLinks) {
            String courseURL = course.getAttribute("href");    // GET COURSE LINK
            // STORE COURSE NAME AND URL
            courseMap.put(course.getText(), courseURL);
        }
        // Quit driver everytime we finish a function
        UserCookie userCookie = UserCookie.getInstance();
        userCookie.setCourseMap(courseMap);
        driver.quit();

    }
}
