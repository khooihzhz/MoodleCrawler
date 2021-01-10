package sample;

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
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class loginController{
    // FX COMPONENTS
    @FXML private TextField stu_email;
    @FXML private PasswordField stu_password;
    @FXML private Button submitButton;
    @FXML private Circle c;

    // ------------GLOBAL VARIABLES---------------------
    // Save Cookie for new Drivers
    public static Set<Cookie> moodleCookies;
    public static List<String> courseList = new ArrayList<>();
    public static List<String> courseNameList = new ArrayList<>(); // store course names


    public static WebDriver driver;

    public void initialize() {
        c.setVisible(false);

        driver = setupCrawler("");
        driver.get("https://elearning.usm.my/sidang2021/");
        // GET DOWNLOAD LINK
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("a")));

        List<WebElement> loginLinks = driver.findElements(By.tagName("a"));
        String loginURL = "";
        for (WebElement link : loginLinks) {
            if (link.getAttribute("title").equals(" Login")) {    // CHECK HTML PAGE AND CAN KNOW "LOGIN" HAVE THE LINK
                loginURL = link.getAttribute("href");
            }
        }
        // STEP 2 :
        // NAVIGATE TO LOGIN URL
        driver.get(loginURL);
    }

    // LOGIN
    public void login (ActionEvent event) {
        // SET THE CIRCLE VISIBLE AND ROTATE
        c.setVisible(true);
        setRotate(c, false, 360, 3);

        Window owner = submitButton.getScene().getWindow();

        // GET THE INPUT FROM THE USER
        boolean loginStatus;
        String userEmail = stu_email.getText();
        String userPassword = stu_password.getText();
        // CHECK IF LOGIN IS SUCCESSFUL
        loginStatus = getMoodleCookies(userEmail, userPassword);
        if (loginStatus) {
            // SWITCH TO NEXT SCENE
            loadNextScene("progresspage.fxml");
            System.out.println(moodleCookies);
            getCourseList(courseList, courseNameList);
        } else {
            showAlert(Alert.AlertType.ERROR, owner, "Error",
                    "Invalid email or password!\nPlease try again to login.");
            stu_email.clear();
            stu_password.clear();
        }
        //new Thread(loadTask).start();
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
            Parent nextStage = FXMLLoader.load(Main.class.getResource(fxml));
            Scene newScene = new Scene(nextStage);
            Stage currentStage = (Stage) Main.root.getScene().getWindow();
            currentStage.setScene(newScene);
        }
        catch (IOException e) {
            // do nothing
        }
    }

    // Setup Crawler Method
    public static WebDriver setupCrawler(String SaveDirectory) {
        System.setProperty("webdriver.chrome.driver", ".\\chromedriver.exe");

        // ----- SET DOWNLOAD PATH -----
        Map<String, Object> prefs = new HashMap<>();
        // NAVIGATE TO DOWNLOAD DIRECTORY
        prefs.put("plugins.always_open_pdf_externally", true);
        prefs.put("download.default_directory", System.getProperty("user.dir") + File.separator + SaveDirectory);
        // DISABLE POP UP
        prefs.put("profile.default_content_settings.popups", 0);
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.setExperimentalOption("prefs", prefs);

        return new ChromeDriver(options);
    }

    private static boolean getMoodleCookies(String userEmail, String userPassword) {
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
            driver.quit();
            return true;
        }
    }


    public static void modifyMoodleCookies(WebDriver driver) {
        driver.get("https://elearning.usm.my/sidang2021/");
        // REMOVE ALL COOKIES AND ADD NEW ONE
        driver.manage().deleteAllCookies();

        for (Cookie cookie : moodleCookies) {
            driver.manage().addCookie(cookie);
        }

        // refresh
        driver.get("https://elearning.usm.my/sidang2021/");
    }

    private void getCourseList(List<String> courseList, List<String> courseNameList) {
        WebDriver driver = setupCrawler("");
        modifyMoodleCookies(driver);
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

    Task<Void> loadTask = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
            Window owner = submitButton.getScene().getWindow();

            // GET THE INPUT FROM THE USER
            boolean loginStatus;
            String userEmail = stu_email.getText();
            String userPassword = stu_password.getText();
            // CHECK IF LOGIN IS SUCCESSFUL
            loginStatus = getMoodleCookies(userEmail, userPassword);
            if (loginStatus) {
                // SWITCH TO NEXT SCENE
                loadNextScene("progresspage.fxml");
                System.out.println(moodleCookies);
                getCourseList(courseList, courseNameList);
            } else {
                showAlert(Alert.AlertType.ERROR, owner, "Error",
                        "Invalid email or password!\nPlease try again to login.");
                stu_email.clear();
                stu_password.clear();
            }
            return null;
        }
    };
}
