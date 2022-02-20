package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class StartController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void selectPhoto(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
            root = loader.load();
            MainController controller = loader.getController();
            Image image = new Image(file.toURI().toURL().toExternalForm());
            controller.setImage(image);
            scene = new Scene(root);
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            stage.setOnCloseRequest(eventClose -> {
                eventClose.consume();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Exit");
                alert.setHeaderText("You are about to exit without saving");
                alert.setContentText("Would you like to continue?");

                if(alert.showAndWait().get() == ButtonType.OK)
                    stage.close();
            });
        }
    }
}
