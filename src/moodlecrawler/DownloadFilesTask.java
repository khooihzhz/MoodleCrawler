package moodlecrawler;

import javafx.concurrent.Task;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DownloadFilesTask extends Task<Void> {


    @Override
    protected  Void call() throws Exception {

        UserCookie userCookie = UserCookie.getInstance();
        LinkedHashMap<String, String> courseMap = userCookie.getSelectedCourseMap();

        for (String courseName : courseMap.keySet()) {

            String courseURL = courseMap.get(courseName);
            List<String> listResources = new ArrayList<>();
            listResources.add("li.activity.assign.modtype_assign");
            //listResources.add("li.activity.resource.modtype_resource");
            listResources.add("li.activity.folder.modtype_folder");

            // START DOWNLOAD
            WebDriver driver = SetupCrawler.setupCrawler(courseName);
            SetupCrawler.modifyMoodleCookies(driver);

            for (String resourceType : listResources) {
                //declaration of variables
                WebDriverWait wait = new WebDriverWait(driver, 2);
                List<WebElement> materialsAttributes = new ArrayList<>();
                List<String> listLinks = new ArrayList<>();
                List<String> listDeepLinks = new ArrayList<>();
                WebElement downloadButton;
                driver.get(courseURL);
                // Find all anchor tags
                // WAIT CONDITION
                try {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(resourceType)));
                    materialsAttributes = driver.findElements(By.cssSelector(resourceType));
                } catch (Exception e) {
                    // do nothing
                }

                // if there is certain resources type
                if (!materialsAttributes.isEmpty()) {
                    for (WebElement tags : materialsAttributes) {
                        try {
                            boolean restricted = false;
                            if (resourceType.equals("li.activity.assign.modtype_assign")) {
                                // if can find this element is restricted
                                // skip it
                                try {
                                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.dimmed.dimmed_text")));
                                    List<WebElement> restrictedTags = tags.findElements(By.cssSelector("div.dimmed.dimmed_text"));
                                    if (!restrictedTags.isEmpty()) {
                                        restricted = true;
                                    }
                                }
                                catch (Exception e)
                                {
                                    // do nothing
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
                    System.out.println(urls);
                    driver.get(urls);
                    // DECLARE
                    WebElement mainTag;
                    List<WebElement> moreDownloadLinks = new ArrayList<>();

                    // if redirected
                    if (!courseURL.equals(driver.getCurrentUrl())) {
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
                        driver.get(courseURL);
                    }
                }
                // DOWNLOAD DEEP LINKS
                for (String deepURL : listDeepLinks) {
                    if (deepURL != null) {
                        driver.get(deepURL);
                    }
                }
            }
            driver.quit();
        }
        return null;
    }

}
