package sample;

import com.google.common.annotations.VisibleForTesting;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;



import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("loginpage.fxml"));
        primaryStage.setTitle("Moodle Crawler");
        primaryStage.getIcons().add(new Image("resources/spiderweb.png"));
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 771, 505));
        primaryStage.show();
    }

    // ------------GLOBAL VARIABLES---------------------
    // Save Cookie for new Drivers
    public static Set<Cookie> moodleCookies;
    public static List<String> courseList = new ArrayList<>();
    public static List<String> courseNameList = new ArrayList<>(); // store course names

    // Setup Crawler Method
    static WebDriver setupCrawler(String SaveDirectory) {
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

    static boolean getMoodleCookies(String userEmail, String userPassword) {
        WebDriver driver = setupCrawler("");
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

    static void modifyMoodleCookies(WebDriver driver) {
        driver.get("https://elearning.usm.my/sidang2021/");
        // REMOVE ALL COOKIES AND ADD NEW ONE
        driver.manage().deleteAllCookies();

        for (Cookie cookie : moodleCookies) {
            driver.manage().addCookie(cookie);
        }

        // refresh
        driver.get("https://elearning.usm.my/sidang2021/");
    }

    static void getCourseList(List<String> courseList, List<String> courseNameList) {
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

    static void findLinks(String resourceType, String courseLink, String courseName) {

        // SETUP CRAWLERS
        WebDriver driver = setupCrawler(courseName);
        modifyMoodleCookies(driver);

        // declaration of variables
        WebDriverWait wait = new WebDriverWait(driver, 2);
        List<WebElement> materialsAttributes = new ArrayList<>();
        List<String> listLinks = new ArrayList<>();
        List<String> listDeepLinks = new ArrayList<>();
        WebElement downloadButton;
        // Find all anchor tags
        // WAIT CONDITION
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(resourceType)));
            materialsAttributes = driver.findElements(By.cssSelector(resourceType));
        } catch (Exception e) {
            // do nothing
        }

        if (!materialsAttributes.isEmpty()) {
            for (WebElement tags : materialsAttributes) {
                try {
                    boolean restricted = false;
                    if (resourceType.equals("li.activity.assign.modtype_assign")) {
                        // if can find this element is restricted
                        // skip it
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.dimmed.dimmed_text")));
                        List<WebElement> restrictedTags = tags.findElements(By.cssSelector("div.dimmed.dimmed_text"));
                        if (!restrictedTags.isEmpty()) {
                            restricted = true;
                        }
                    }
                    if (!restricted) {
                        // else if it is not empty find the link
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.aalink")));
                        List<WebElement> anchorTags = tags.findElements(By.cssSelector("a.aalink"));
                        // if found elements
                        if (!anchorTags.isEmpty()) {
                            for (WebElement anchorTag : anchorTags) {
                                String fileURL = anchorTag.getAttribute("href");
                                if (!listLinks.contains(fileURL)) {
                                    listLinks.add(fileURL);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // do nothing
                }
            }
        }


        // FIRST WAVE OF LINKS
        for (String urls : listLinks) {
            //TRY CREATE NEW WINDOWS
            System.out.println(urls);
            driver.get(urls);
            // DECLARE
            WebElement mainTag;
            List<WebElement> moreDownloadLinks = new ArrayList<>();

            // if redirected
            if (!courseLink.equals(driver.getCurrentUrl())) {
                try {
                    WebDriverWait findWaitTag = new WebDriverWait(driver, 1);
                    if (resourceType.equals("li.activity.assign.modtype_assign")) {

                        findWaitTag.until(ExpectedConditions.visibilityOfElementLocated(By.id("intro")));
                        mainTag = driver.findElement(By.id("intro"));
                        moreDownloadLinks = mainTag.findElements(By.tagName("a"));
                    } else if (resourceType.equals("li.activity.folder.modtype_folder")) {
                        downloadButton = driver.findElement(By.cssSelector("button[type=submit]"));
                        downloadButton.click();
                    } else {
                        findWaitTag.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.resourceworkaround")));
                        mainTag = driver.findElement(By.cssSelector("div.resourceworkaround"));
                        moreDownloadLinks = mainTag.findElements(By.tagName("a"));
                    }

                } catch (Exception e) {
                    // do not break for loop
                }

                // if it is not folder type
                if (!resourceType.equals("li.activity.folder.modtype_folder")) {
                    // use REGEX TO FIND MATERIAL LINKS
                    String fileLinkPattern = ".*pluginfile.*";
                    if (!moreDownloadLinks.isEmpty()) {
                        for (WebElement link : moreDownloadLinks) {
                            try {
                                String fileURL = link.getAttribute("href");
                                // avoid error
                                if (fileURL != null) {
                                    // IS NOT A MP4 FILE
                                    if (!fileURL.endsWith("mp4")) {
                                        if (fileURL.matches(fileLinkPattern)) {
                                            // if this file doesn't exist yet
                                            if (!listDeepLinks.contains(fileURL)) {
                                                listDeepLinks.add(fileURL);
                                            }

                                        }
                                    }

                                }
                            } catch (Exception e) {
                                // do nothing
                            }
                        }
                    }

                }

                //  go back course link this improve performance
                driver.get(courseLink);
            }
        }

        // DOWNLOAD DEEP LINKS
        for (String deepURL : listDeepLinks) {
            if (deepURL != null) {
                driver.get(deepURL);
            }
        }
    }

    static void arrangeFiles(String courseTitle) {
        String[] fileList;
        String downloadPath;
        String folderPath;

        // ELIMINATE SPECIAL CHARACTERS
        courseTitle = courseTitle.replaceAll("[^a-zA-Z0-9&]", " ");

        // STORE DOWNLOAD PATH
        downloadPath = System.getProperty("user.dir") + "\\externalFiles\\downloadFiles";
        File downloadFile = new File(downloadPath);

        // PATH FOR NEW FOLDER
        folderPath = System.getProperty("user.dir")+"\\externalFiles\\"+courseTitle;
        File newFolder = new File(folderPath);
        // CREATE DIRECTORY
        newFolder.mkdir();

        // STORE LIST OF FILE NAMES
        fileList = downloadFile.list();

        // STRING PATTERN FOR ASSIGNMENTS AND TUTORIALS
        String assignPattern = "^.*[Cc]oursework.*$|^.*[Aa]ssignment.*$|^.*[Pp]roject.*$";
        String tutorialPattern = "^.*[Tt]utorial.*$|^.*[Ll]ab.*$|^.*[Tt]ut.*$";
        String path = "";
        // MOVE FILES INTO THE NEW DIRECTORY
        for (String filename:fileList) {
            if (filename.matches(assignPattern)) {
                // STORE ASSIGNMENT FOLDER PATH
                path = folderPath+"\\Assignments";
            }
            else if (filename.matches(tutorialPattern)) {
                // STORE TUTORIAL FOLDER PATH
                path = folderPath+"\\Tutorials";
            }
            else {
                // STORE LECTURES FOLDER PATH
                path = folderPath+"\\Lectures";
            }
            // CREATE DIRECTORY
            File f = new File(path);
            f.mkdir();
            // MOVE FILE TO THE TARGETED DIRECTORY
            try {
                Files.move(Paths.get(downloadPath+"\\"+filename), Paths.get(path+"\\"+filename));
            }
            catch (IOException e){
                // do nothing
            }
        }
    }

    public static void main(String[] args)
    {

        launch(args);
        // === IMPLEMENT SELECTION FUNCTION HERE ====
        //getCourseList(courseList, courseNameList);

        /*
        for (String link : courseList) {
            // DOWNLOAD FILES
            List<String> listResources = new ArrayList<>();
            listResources.add("li.activity.assign.modtype_assign");
            listResources.add("li.activity.resource.modtype_resource");
            listResources.add("li.activity.folder.modtype_folder");

            // DOWNLOAD
            for (String resourceType : listResources){
                driver.get(link);
                System.out.println("SCRAPING " + driver.getTitle());
                findLinks(resourceType, link);

            }

            // ARRANGE FILE
        }

         */

    }

}


/*
    // --- TEST GET COOKIES --- AND PASS IT
    public static void main (String [] args) throws InterruptedException {
        // SETUP DRIVER
        WebDriver driver = SetupCrawler();

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

        // STEP 3 : ENTER USERNAME AND PASSWORD
        Scanner input = new Scanner(System.in);

        String userEmail = "";
        String userPassword = "";
        String emailPattern = "\\w+\\S+@student.usm.my";
        String passPattern = "\\w{8,25}";
        boolean validation = true;

        while (validation) {
            System.out.println("Username: ");
            userEmail = input.nextLine();
            System.out.println("Password: ");
            userPassword = input.nextLine();

            if (userEmail.matches(emailPattern) && userPassword.matches(passPattern)) {
                validation = false;
            } else {
                System.out.println("Invalid username or password!");
            }
        }

        // Find EMAIL TextBox
        WebElement eMail = driver.findElement(By.id("userNameInput"));

        // Find PassWord TextBox
        WebElement password = driver.findElement(By.id("passwordInput"));

        // STEP 4 : LOGIN
        // Enter User Email and Password Here
        // ------ IMPLEMENT SCANNER CLASS TO GET EMAIL AND USERNAME -------

        eMail.sendKeys(userEmail);
        password.sendKeys(userPassword);

        WebElement submitButton = driver.findElement(By.id("submitButton"));
        submitButton.click();

        Set<Cookie> cookieList = driver.manage().getCookies();

        for(Cookie cookie : cookieList){
            System.out.println(cookie.getName() + " HAS VALUE OF " + cookie.getValue());
        }

        System.out.println("EXITED");
        driver.quit();

        // CREATE NEW DRIVER AND SEE IF CAN PUT IN COOKIE
        WebDriver newDriver = SetupCrawler();
        newDriver.get("https://elearning.usm.my/sidang2021/");
        newDriver.manage().deleteAllCookies();
        for(Cookie cookie : cookieList) {
            System.out.println("Adding Cookies");
            newDriver.manage().addCookie(cookie);
        }
        Thread.sleep(2);
        newDriver.get("https://elearning.usm.my/sidang2021/");
    }
*/


    // FUNCTION TO TEST REGEX
    /*
    public static void main(String[] args){
        String testPattern = ".*pluginfile.*";
        String inputString = "https://elearning.usm.my/sidang2021/pluginfile.php/191781/mod_assign/introattachment/0/CAT201_2020-assignment1.pdf?forcedownload=1";

        if (inputString.matches(testPattern)){
            System.out.println("YES");
        }
        else{
            System.out.println("NO");
        }
    }
     */

