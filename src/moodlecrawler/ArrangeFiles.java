package moodlecrawler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/*
public class ArrangeFiles {
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
}
*/