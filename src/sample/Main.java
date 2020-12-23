package sample;

import com.google.common.annotations.VisibleForTesting;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }
    // Setup Crawler Method

   public static WebDriver driver = SetupCrawler();

    public static void main(String[] args) {
        // GET TO THE MAIN PAGE
        driver.get("https://elearning.usm.my/sidang2021/");

        // STEP 2 : GET LOGIN WEBSITE
        LoginWebsite();

        // STEP 3 : GET LOGIN INFORMATION AND LOGIN INTO PLACE
        PromptLogin();

        // WAIT FOR TIME
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        // INITIALIZE LIST
        List<String> courseList = new ArrayList<>();

        // STEP 4 : GET COURSE LINKS
        getCourseList(courseList);

        // ----- SCRAPING BEGINS -----
        // LOOP THROUGH LINKS

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

        /*
        // TEST FUNCTION for a course

        String courseURL = courseList.get(1);
        System.out.println(courseURL);
        List<String> listResources = new ArrayList<>();
        //listResources.add("li.activity.assign.modtype_assign");
        //listResources.add("li.activity.resource.modtype_resource");
        listResources.add("li.activity.folder.modtype_folder");

        // DOWNLOAD
        for (String resourceType : listResources){
            driver.get(courseURL);
            System.out.println(resourceType);
            findLinks(resourceType, courseURL);
        }
         */


    }


    static void findLinks(String resourceType, String courseLink) {

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
        }
        catch(Exception e){
            // do nothing
        }

        if (!materialsAttributes.isEmpty())
        {
            for (WebElement tags : materialsAttributes) {
                try {
                    boolean restricted = false;
                    if (resourceType.equals("li.activity.assign.modtype_assign"))
                    {
                        // if can find this element is restricted
                        // skip it
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.dimmed.dimmed_text")));
                        List<WebElement> restrictedTags = tags.findElements(By.cssSelector("div.dimmed.dimmed_text"));
                        if (!restrictedTags.isEmpty())
                        {
                            restricted = true;
                        }
                    }
                    if (!restricted)
                    {
                        // else if it is not empty find the link
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.aalink")));
                        List<WebElement> anchorTags = tags.findElements(By.cssSelector("a.aalink"));
                        // if found elements
                        if (!anchorTags.isEmpty())
                        {
                            for (WebElement anchorTag : anchorTags)
                            {
                                String fileURL = anchorTag.getAttribute("href");
                                if (!listLinks.contains(fileURL))
                                {
                                    listLinks.add(fileURL);
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    // do nothing
                }
            }
        }


        // FIRST WAVE OF LINKS
        for (String urls : listLinks)
        {
            //TRY CREATE NEW WINDOWS
            System.out.println(urls);
            driver.get(urls);
            // DECLARE
            WebElement mainTag;
            List<WebElement> moreDownloadLinks = new ArrayList<>();

            // if redirected
            if (!courseLink.equals(driver.getCurrentUrl()))
            {
                try
                {
                    WebDriverWait findWaitTag = new WebDriverWait(driver, 1);
                    if (resourceType.equals("li.activity.assign.modtype_assign"))
                    {

                        findWaitTag.until(ExpectedConditions.visibilityOfElementLocated(By.id("intro")));
                        mainTag = driver.findElement(By.id("intro"));
                        moreDownloadLinks = mainTag.findElements(By.tagName("a"));
                    }
                    else if (resourceType.equals("li.activity.folder.modtype_folder")) {
                        downloadButton = driver.findElement(By.cssSelector("button[type=submit]"));
                        downloadButton.click();
                    }
                    else
                    {
                        findWaitTag.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.resourceworkaround")));
                        mainTag = driver.findElement(By.cssSelector("div.resourceworkaround"));
                        moreDownloadLinks = mainTag.findElements(By.tagName("a"));
                    }

                }
                catch (Exception e)
                {
                    // do not break for loop
                }

                // if it is not folder type
                if (!resourceType.equals("li.activity.folder.modtype_folder"))
                {
                    // use REGEX TO FIND MATERIAL LINKS
                    String fileLinkPattern = ".*pluginfile.*";
                    if (!moreDownloadLinks.isEmpty())
                    {
                        for (WebElement link : moreDownloadLinks) {
                            try {
                                String fileURL = link.getAttribute("href");
                                // avoid error
                                if (fileURL != null) {
                                    // IS NOT A MP4 FILE
                                    if (!fileURL.endsWith("mp4"))
                                    {
                                        if (fileURL.matches(fileLinkPattern)) {
                                            // if this file doesn't exist yet
                                            if (!listDeepLinks.contains(fileURL)) {
                                                listDeepLinks.add(fileURL);
                                            }

                                        }
                                    }

                                }
                            }
                            catch(Exception e){
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
            if (deepURL != null)
            {
                driver.get(deepURL);
            }
        }
    }

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

    static WebDriver SetupCrawler() {
        System.setProperty("webdriver.chrome.driver", ".\\chromedriver.exe");

        // ----- SET DOWNLOAD PATH -----
        Map<String, Object> prefs = new HashMap<>();

        // CREATE A DOWNLOAD DIRECTORY
        prefs.put("plugins.always_open_pdf_externally", true);
        prefs.put("download.default_directory", System.getProperty("user.dir") + File.separator + "externalFiles" + File.separator + "downloadFiles");
        // DISABLE POP UP
        prefs.put("profile.default_content_settings.popups", 0);
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.setExperimentalOption("prefs", prefs);

        return new ChromeDriver(options);
    }

    static void LoginWebsite() {
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

    static void PromptLogin() {
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

    }

    static void getCourseList(List<String> courseList) {
        // ----- START FIND COURSE LIST ------
        List<WebElement> courseLinks = driver.findElements(By.cssSelector("a.courselist_course.scrollable"));
        /* ----- TEST PRINT -----
        for (WebElement course : courseLinks){
            System.out.println(course.getText());
            System.out.println(course.getAttribute("href"));
        }
        */
        // STEP 6 : LOOP THROUGH EACH LINK
        // REMEMBER COURSE LINKS
        for (WebElement course : courseLinks) {
            String courseURL = course.getAttribute("href");    // GET COURSE LINK
            // System.out.println(courseURL);
            courseList.add(courseURL);
        }
    }

}
