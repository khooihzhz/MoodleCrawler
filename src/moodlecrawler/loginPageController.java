package moodlecrawler;

import javafx.animation.RotateTransition;
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
import sample.second;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class loginPageController {
    // FX COMPONENTS
    @FXML
    private TextField stu_email;
    @FXML
    private PasswordField stu_password;
    @FXML
    private Button submitButton;
    @FXML
    private Circle c;

    // variables
    public static Set<Cookie> moodleCookies;
    public static Thread backgroundThread = new Thread(new StartUpThread());


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
            Parent nextStage = FXMLLoader.load(second.class.getResource(fxml));
            Scene newScene = new Scene(nextStage);
            Stage currentStage = (Stage) second.root.getScene().getWindow();
            currentStage.setScene(newScene);
        }
        catch (IOException e) {
            // do nothing
        }
    }

    public void initialize()
    {
        backgroundThread.start();
    }

    // LOGIN
    public void login (ActionEvent event) throws InterruptedException {

        // SET THE CIRCLE VISIBLE AND ROTATE
        c.setVisible(true);
        setRotate(c, false, 360, 3);

        Window owner = submitButton.getScene().getWindow();

        // GET THE INPUT FROM THE USER
        boolean loginStatus;
        String userEmail = stu_email.getText();
        String userPassword = stu_password.getText();
        // CHECK IF LOGIN IS SUCCESSFUL
        loginStatus = getMoodleCookies("", "");
        if (loginStatus) {
            // SWITCH TO NEXT SCENE
            System.out.println(moodleCookies);
            //getCourseList(courseList, courseNameList);
            loadNextScene("progresspage.fxml");
        } else {
            showAlert(Alert.AlertType.ERROR, owner, "Error",
                    "Invalid email or password!\nPlease try again to login.");
            stu_email.clear();
            stu_password.clear();
        }
    }
    public static boolean getMoodleCookies(String userEmail, String userPassword) {
        UserWebDriver userDriver = UserWebDriver.getInstance();
        WebDriver driver = userDriver.getWebDriver();
        // *** USE getText() IN JAVAFX controller to get user input ***
        // Find EMAIL and PASSWORD text field
        WebElement eMail = driver.findElement(By.id("userNameInput"));
        WebElement password = driver.findElement(By.id("passwordInput"));

        // STEP 3 : LOGIN
        // Enter User Email and Password Here
        eMail.sendKeys(userEmail);
        password.sendKeys(userPassword);
        WebElement submitButton = driver.findElement(By.id("submitButton"));
        submitButton.click();

        // CHECK IF ERROR MESSAGE SHOWN OR NOT
        boolean checkLogin = false;

        try{
            // check if this element exists
            WebDriverWait wait = new WebDriverWait(driver, 2);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorText")));
            checkLogin = driver.findElement(By.id("errorText")).isDisplayed();
        }catch (Exception e)
        {
            // do nothing
        }

        // IF errorText SHOWN, RETURN FALSE
        if (checkLogin){
            // fail to login
            driver.quit();
            return false;
        }
        else {
            // STEP 5 : OBTAIN COOKIE AND RETURN TRUE
            moodleCookies = driver.manage().getCookies();
            UserCookie userCookies = UserCookie.getInstance();
            userCookies.setUserCookie(moodleCookies);
            driver.quit();
            return true;
        }
    }

    private void getCourseList(List<String> courseList, List<String> courseNameList) {
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
            // System.out.println(courseURL);
            courseList.add(courseURL);
            // ===== GET COURSE NAME =====
            // System.out.println(course.getText());
            courseNameList.add(course.getText());
        }
        // quit driver everytime we finish a function
        driver.quit();
    }
}