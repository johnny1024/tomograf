package sample;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Controller {

    @FXML
    private ImageView imageViewIn;
    @FXML
    private ImageView imageViewSin;

    public void Initialize()
    {

    }

    public ImageView getImageViewIn() {
        return imageViewIn;
    }
    public ImageView getImageViewSin() {
        return imageViewSin;
    }
}
