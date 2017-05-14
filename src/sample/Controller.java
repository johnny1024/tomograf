package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import java.awt.*;

public class Controller {

    @FXML
    private TextField imageTextField;
    @FXML
    private Label imageErrorLabel;
    @FXML
    private Button loadButton;

    @FXML
    private Slider numberOfDetectorsSlider;
    @FXML
    private Label numberOfDetectorsLabel;
    @FXML
    private Slider detectorsSpreadSlider;
    @FXML
    private Label detectorsSpreadLabel;
    @FXML
    private Slider iterationAngleSlider;
    @FXML
    private Label iterationAngleLabel;
    @FXML
    private Button runButton;


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

    public void init()
    {
        numberOfDetectorsSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                numberOfDetectorsSlider.setValue(newValue.intValue());
                numberOfDetectorsLabel.setText(String.format("%d", newValue.intValue()));
            }
        });

        detectorsSpreadSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                detectorsSpreadSlider.setValue(newValue.intValue());
                detectorsSpreadLabel.setText(String.format("%d", newValue.intValue()));
            }
        });

    }

    public TextField getImageTextField() {
        return imageTextField;
    }
    public Label getImageErrorLabel() {
        return imageErrorLabel;
    }
    public Button getLoadButton() {
        return loadButton;
    }
    public Slider getNumberOfDetectorsSlider() {
        return numberOfDetectorsSlider;
    }
    public Label getNumberOfDetectorsLabel() {
        return numberOfDetectorsLabel;
    }
    public Slider getDetectorsSpreadSlider() {
        return detectorsSpreadSlider;
    }
    public Label getDetectorsSpreadLabel() {
        return detectorsSpreadLabel;
    }
    public Slider getIterationAngleSlider() {
        return iterationAngleSlider;
    }
    public Label getIterationAngleLabel() {
        return iterationAngleLabel;
    }
    public Button getRunButton() {
        return runButton;
    }
    public ImageView getImageViewIn() {
        return imageViewIn;
    }
    public ImageView getImageViewRadar() {
        return imageViewRadar;
    }
    public ImageView getImageViewSin() {
        return imageViewSin;
    }
    public ImageView getImageViewOut() {
        return imageViewOut;
    }
    public ImageView getImageViewSin2() {
        return imageViewSin2;
    }
    public ImageView getImageViewOut2() {
        return imageViewOut2;
    }
}
