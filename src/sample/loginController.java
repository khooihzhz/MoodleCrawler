package sample;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Window;

public class loginController{
    @FXML private TextField stu_email;
    @FXML private PasswordField stu_password;
    @FXML private Button submitButton;

    public void login (ActionEvent event) {

        Window owner = submitButton.getScene().getWindow();

        boolean loginStatus;
        String userEmail = stu_email.getText();
        String userPassword = stu_password.getText();
        loginStatus = Main.getMoodleCookies(userEmail, userPassword);
        if (loginStatus) {
            System.out.println(Main.moodleCookies);
            Main.getCourseList(Main.courseList, Main.courseNameList);
        }
        else {
            showAlert(Alert.AlertType.ERROR, owner, "Error", "Invalid email or password!");
        }
    }

    private static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }

}
