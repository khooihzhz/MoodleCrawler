package sample;

        import javafx.application.Application;
        import javafx.event.ActionEvent;
        import javafx.event.EventHandler;
        import javafx.fxml.FXMLLoader;
        import javafx.geometry.Insets;
        import javafx.geometry.Pos;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
        import javafx.scene.control.Button;
        import javafx.scene.control.Label;
        import javafx.scene.control.PasswordField;
        import javafx.scene.control.TextField;
        import javafx.scene.layout.GridPane;
        import javafx.stage.Stage;
        import java.io.IOException;

public class Loginpage extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

            Parent root = FXMLLoader.load(getClass().getResource("loginpage.fxml"));
            primaryStage.setTitle("Login Page");
            primaryStage.setScene(new Scene(root, 800, 500));
            primaryStage.show();
        }


    public static void main(String[] args) {
        launch(args);
    }


}