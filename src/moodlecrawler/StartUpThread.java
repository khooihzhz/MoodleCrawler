package moodlecrawler;

import javafx.concurrent.Task;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class StartUpThread implements Runnable{
    @Override
    public void run() {
        // START DOWNLOAD
        WebDriver driver = SetupCrawler.setupCrawler("");
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

        UserWebDriver userDriver = UserWebDriver.getInstance();
        userDriver.setWebDriver(driver);
    }
}