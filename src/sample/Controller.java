package sample;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Controller {

    @FXML
    private ImageView imageView;

    @FXML
    private GridPane gridPane;

    public void Initialize()
    {
        //Image imageIn = new Image("asdf.jpg");
        //imageView.setImage(imageIn);

    }

    public ImageView getImageView() {
        return imageView;
    }

    public GridPane getGridPane() {
        return gridPane;
    }
}
