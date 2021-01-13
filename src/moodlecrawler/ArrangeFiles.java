package moodlecrawler;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArrangeFiles {
    static void arrangeFiles(String courseTitle) {
        String[] fileList;      // ARRAY TO STORE FILE LIST
        String downloadPath;    // VARIABLE TO STORE DOWNLOAD PATH
        String folderPath;      // VARIABLE TO STORE NEW FOLDER PATH


        // STORE DOWNLOAD PATH
        downloadPath = System.getProperty("user.dir") + "\\DownloadedFiles\\"+courseTitle;
        File downloadFile = new File(downloadPath);

        // PATH FOR NEW FOLDER
        folderPath = System.getProperty("user.dir")+"\\DownloadedFiles\\"+courseTitle;
        File newFolder = new File(folderPath);
        // CREATE DIRECTORY
        newFolder.mkdir();

        // STORE LIST OF FILE NAMES
        fileList = downloadFile.list();

        // STRING PATTERN FOR ASSIGNMENTS AND TUTORIALS
        String assign = "^.*coursework.*$|^.*assignment.*$|^.*project.*$";
        Pattern assignPattern = Pattern.compile(assign, Pattern.CASE_INSENSITIVE);
        String tutorial = "^.*tutorial.*$|^.*lab.*$|^.*tut.*$";
        Pattern tutorialPattern = Pattern.compile(tutorial, Pattern.CASE_INSENSITIVE);

        String path;
        // MOVE FILES INTO THE NEW DIRECTORY
        for (String filename:fileList) {
            Matcher assignMatch = assignPattern.matcher(filename);
            Matcher tutorialMatch = tutorialPattern.matcher(filename);
            if (assignMatch.matches()) {
                // STORE ASSIGNMENT FOLDER PATH
                path = folderPath+"\\Assignments";
            }
            else if (tutorialMatch.matches()) {
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

}
