package moodlecrawler;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SetupCrawler {
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

   /* public static void modifyMoodleCookies(WebDriver driver) {
        driver.get("https://elearning.usm.my/sidang2021/");
        // REMOVE ALL COOKIES AND ADD NEW ONE
        driver.manage().deleteAllCookies();

        for (Cookie cookie : moodleCookies) {
            driver.manage().addCookie(cookie);
        }

        // refresh
        driver.get("https://elearning.usm.my/sidang2021/");
    }

    */
}
