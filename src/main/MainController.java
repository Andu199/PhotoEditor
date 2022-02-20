package main;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Stack;

public class MainController {

    @FXML
    private ImageView imageView;

    @FXML
    private AnchorPane anchorPane;

    private final Stack<Image> undo_images = new Stack<>();
    private Image image = null;
    private final Editor editor = new Editor();
    private boolean firstImage = true;

    public void setImage(Image image) {
        if(firstImage)
            firstImage = false;
        else
            undo_images.push(this.image);
        this.image = image;
        imageView.setImage(image);
    }

    public void undoImage() {
        if(undo_images.empty())
            return;
        this.image = undo_images.pop();
        imageView.setImage(image);
    }

    public void black_white() {
        this.setImage(editor.transform_bw(image));
        imageView.setImage(image);
    }

    public void clustering() {
        this.setImage(editor.clustering(image));
        imageView.setImage(image);
    }

    public void effect1() {
        this.setImage(editor.sepia(image));
        imageView.setImage(image);
    }

    public void effect2() {
        editor.changeFilter(new double[][]{{-1, -1, -1}, {-1, 8, -1}, {-1, -1, -1}});
        this.setImage(editor.applyEffect(image));
        imageView.setImage(image);
    }

    public void effect3() {
        editor.changeFilter(new double[][]{{1, 1, 1}, {1, -7, 1}, {1, 1, 1}});
        this.setImage(editor.applyEffect(image));
        imageView.setImage(image);
    }

    public void effect4() {
        editor.changeFilter(new double[][]{{-2, 0, 0}, {0, 6, 0}, {0, 0, -2}});
        this.setImage(editor.applyEffect(image));
        imageView.setImage(image);
    }

    private void savePhoto() {
        Random random = new Random();
        File file = new File("/photo" + Math.abs(random.nextInt()) + ".png");
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save_and_quit(ActionEvent event) {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save and Exit");
        alert.setHeaderText("You are about to exit!");
        alert.setContentText("All your changes will be saved");
        if (alert.showAndWait().get() == ButtonType.OK) {
            event.consume();
            savePhoto();
            stage.close();
        }
    }
}
