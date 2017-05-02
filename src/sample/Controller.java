package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;


public class Controller {

    @FXML
    private ImageView imageViewIn;
    @FXML
    private ImageView imageViewSin;
    @FXML
    private ImageView imageViewOut;
    @FXML
    private Slider detNum;
    @FXML
    private Slider detSpread;
    @FXML
    private Slider angDist;

    public void Initialize()
    {

    }
    public void handleButtonAction(ActionEvent event) {
        Tomografos tomo = new Tomografos();
        tomo.detectorNumber = (int) detNum.getValue();
        tomo.detectorSpread = (int) detSpread.getValue();
        tomo.iterationAngleDistance = (int) angDist.getValue();
        tomo.DrawTomo(this);
    }

    ImageView getImageViewIn() {
        return imageViewIn;
    }
    ImageView getImageViewSin() {
        return imageViewSin;
    }
    ImageView getImageViewOut() {
        return imageViewOut;
    }

}
