package sample;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.awt.*;

public class Controller {

    @FXML
    private TextField image;
    @FXML
    private Label imageAddressErrorLabel;
    @FXML
    private Button loadButton;

    @FXML
    private TextField detectorNumberTextField;
    @FXML
    private Label imageAddressErrorLabel;
    @FXML
    private TextField image;
    @FXML
    private Label imageAddressErrorLabel;
    @FXML
    private TextField image;
    @FXML
    private Label imageAddressErrorLabel;
    @FXML
    private Button loadButton;


    @FXML
    private ImageView imageViewIn;
    @FXML
    private ImageView imageViewRadar;
    @FXML
    private ImageView imageViewSin;
    @FXML
    private ImageView imageViewOut;
    @FXML
    private ImageView imageViewSin2;
    @FXML
    private ImageView imageViewOut2;


    public void Initialize()
    {

    }

    public ImageView getImageViewIn() {
        return imageViewIn;
    }
    public ImageView getImageViewSin() {
        return imageViewSin;
    }
    public ImageView getImageViewOut() {
        return imageViewOut;
    }

}
