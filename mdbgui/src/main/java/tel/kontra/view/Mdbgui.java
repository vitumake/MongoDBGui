package tel.kontra.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

public class Mdbgui extends Application {

    private static Dialog<String> alert = new Dialog<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("MDB GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void alert(String message, String title) {
        alert.setTitle(title);
        alert.setContentText(message);
        alert.getDialogPane().getButtonTypes().clear();
        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(Mdbgui.class, args);
    }
}
