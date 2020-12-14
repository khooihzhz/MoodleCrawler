package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        System.setProperty("webdriver.chrome.driver", ".\\chromedriver.exe");

        // ----- SET DOWNLOAD PATH -----
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("download.default_directory",System.getProperty("user.dir") + File.separator + "externalFiles" + File.separator + "downloadFiles");
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);

        WebDriver driver = new ChromeDriver(options);


        // GET TO THE MAIN PAGE
        driver.get("https://elearning.usm.my/sidang2021/");

        // TEST GET TITLE
        String Title = driver.getTitle();
        System.out.println(Title);

        // GET ALL ANCHOR LINKS AND FIND THE LOGIN LINK
        List<WebElement> loginLinks = driver.findElements(By.tagName("a"));
        String loginURL = "";
        for (WebElement link : loginLinks){
            if (link.getAttribute("title").equals(" Login")){    // CHECK HTML PAGE AND CAN KNOW "LOGIN" HAVE THE LINK
                loginURL = link.getAttribute("href");
                //TEST PRINT
                System.out.println(link.getAttribute("href"));
            }
        }

        // CHECK LOGIN URL
        System.out.println(loginURL);

        // STEP 2 :
        // NAVIGATE TO LOGIN URL
        driver.get(loginURL);

        // TEST PRINT ---- TITLE -----
        Title = driver.getTitle();
        System.out.println(Title);

        // STEP 3 : ENTER USERNAME AND PASSWORD

        // Find EMAIL TextBox
        WebElement eMail = driver.findElement(By.id("userNameInput"));

        // Find PassWord Textbox
        WebElement password = driver.findElement(By.id("passwordInput"));

        // STEP 4 : LOGIN
        // Enter User Email and Password Here
        eMail.sendKeys("<YOUREMAIL>");
        password.sendKeys("<YOURPASSWORD>");

        // CLICK LOGIN BUTTON
        WebElement submitButton = driver.findElement(By.id("submitButton"));
        submitButton.click();

        // STEP 5 : GET COURSE LIST
        // WAIT FOR LOGIN
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }
}
