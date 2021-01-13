package moodlecrawler;

import org.openqa.selenium.WebDriver;

public final class UserWebDriver {
    private WebDriver webDriver;
    private final static UserWebDriver INSTANCE = new UserWebDriver();

    private UserWebDriver(){}

    public static UserWebDriver getInstance()
    {
        return INSTANCE;
    }

    public void setWebDriver(WebDriver driver)
    {
        this.webDriver = driver;
    }

    public WebDriver getWebDriver()
    {
        return this.webDriver;
    }

}