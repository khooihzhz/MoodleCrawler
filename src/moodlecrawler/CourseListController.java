package moodlecrawler;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedHashMap;

public class CourseListController {

    @FXML private Button dlButton;
    @FXML private VBox vbox;
    @FXML private ListView<CheckBox> listview;

    public static Parent thirdStage;

    public void initialize() {
        UserCookie userCookie = UserCookie.getInstance();
        LinkedHashMap<String, String> courseMap = userCookie.getCourseMap();
        LinkedHashMap<String, String> selectedCourseMap = userCookie.getSelectedCourseMap();

        for (String name : courseMap.keySet()) {
            // DECLARE CHECKBOX OBJECT
            CheckBox checkbox = new CheckBox(name);

            // ADD CONTENT INTO THE TABLE COLUMNS
            //vbox.getChildren().add(checkbox);
            listview.getItems().add(checkbox);

            // CREATE A EVENT HANDLER
            EventHandler<ActionEvent> event = event1 -> {
                if (checkbox.isSelected()) {
                    selectedCourseMap.put(name, courseMap.get(name));

                }
                else {
                    selectedCourseMap.remove(name);
                }
            };

            // SET EVENT TO CHECKBOX
            checkbox.setOnAction(event);
        }
    }

    // DOWNLOAD BUTTON FUNCTION
    public void download(ActionEvent event) {
        loadNextScene("progresspage.fxml");
    }

    // LOAD TO NEXT SCENE FUNCTION
    private void loadNextScene(String fxml) {
        try {
            thirdStage = FXMLLoader.load(Main.class.getResource(fxml));
            Scene newScene = new Scene(thirdStage);
            Stage currentStage = (Stage) loginPageController.secondStage.getScene().getWindow();
            currentStage.setScene(newScene);
        } catch (IOException e) {
            // do nothing
        }
    }
}
