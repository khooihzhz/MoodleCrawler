package sample;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.sql.SQLException;
import java.util.Scanner;

public class loginController{
    @FXML private TextField stu_email;
    @FXML private PasswordField stu_password;
    @FXML private Button submitButton;

    @FXML
    public void login (ActionEvent event) {

        Window owner = submitButton.getScene().getWindow();
        String userEmail = stu_email.getText();
        String userPassword = stu_password.getText();



        WebElement eMail = Main.driver.findElement(By.id("userNameInput"));

        // Find PassWord TextBox
        WebElement password = Main.driver.findElement(By.id("passwordInput"));

        // STEP 4 : LOGIN
        // Enter User Email and Password Here
        // ------ IMPLEMENT SCANNER CLASS TO GET EMAIL AND USERNAME -------

        eMail.sendKeys(userEmail);
        password.sendKeys(userPassword);

        WebElement submitButton = Main.driver.findElement(By.id("submitButton"));
        submitButton.click();

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
