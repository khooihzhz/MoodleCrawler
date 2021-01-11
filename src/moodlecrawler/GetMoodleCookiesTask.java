package moodlecrawler;

import javafx.concurrent.Task;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GetMoodleCookiesTask extends Task<Void> {
    private final String userEmail;
    private final String userPassword;

    public GetMoodleCookiesTask(String userEmail, String userPassword)
    {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }
    @Override
    protected Void call() throws Exception {

        UserWebDriver userDriver = UserWebDriver.getInstance();
        WebDriver driver = userDriver.getWebDriver();

        // Find EMAIL and PASSWORD text field
        WebElement eMail = driver.findElement(By.id("userNameInput"));
        WebElement password = driver.findElement(By.id("passwordInput"));

        // clear form
        eMail.clear();
        password.clear();

        // Fill in form
        eMail.sendKeys(userEmail);
        password.sendKeys(userPassword);

        // click submit
        WebElement submitButton = driver.findElement(By.id("submitButton"));
        submitButton.click();

        WebDriverWait wait = new WebDriverWait(driver, 2);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorText")));
        driver.findElement(By.id("errorText")).isDisplayed();
        return null;
    }
}