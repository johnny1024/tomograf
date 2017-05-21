package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class Controller {

    private int detectorNumber = 300;
    private double detectorSpread = 300;
    private double iterationAngleDistance = 1;

    private Image imageIn;
    private Image placeholder;

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

    private void loadImage() {
        runButton.setDisable(true);
        //imageErrorLabel.setText("Loading...");
        try {
            imageIn = new Image(imageTextField.getText());
            imageViewIn.setImage(imageIn);
            //imageErrorLabel.setText("");
            runButton.setDisable(false);
        } catch (Exception e) {
            //.setText("Invalid image!");
            runButton.setDisable(true);
        }
    }

    private void runAllOperations() {
        Sinogram sinogram = new Sinogram(detectorNumber, detectorSpread, iterationAngleDistance, imageIn, imageViewSin, imageViewSin2, imageViewRadar);

        Reconstructed output = new Reconstructed(detectorNumber, detectorSpread, iterationAngleDistance, sinogram
                .getSinogram(), (int)imageIn.getHeight(), (int)imageIn.getWidth(), imageViewOut);
        Reconstructed output2 = new Reconstructed(detectorNumber, detectorSpread, iterationAngleDistance, sinogram
                .getFilteredSinogram(), (int)imageIn.getHeight(), (int)imageIn.getWidth(), imageViewOut2);
    }

    public void init()
    {
//        placeholder = new Image("file:placeholder.png");
//        imageViewIn.setImage(placeholder);
//        imageViewSin.setImage(placeholder);
//        imageViewSin2.setImage(placeholder);
//        imageViewOut.setImage(placeholder);
//        imageViewOut2.setImage(placeholder);

        imageErrorLabel.setText("");
        runButton.setDisable(true);

        numberOfDetectorsSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if ((double)newValue == 0) newValue = 1;
                numberOfDetectorsSlider.setValue(newValue.intValue());
                numberOfDetectorsLabel.setText(String.format("%d", newValue.intValue()));
                detectorNumber = newValue.intValue();
            }
        });

        detectorsSpreadSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if ((double)newValue == 0) newValue = 1;
                detectorsSpreadSlider.setValue(newValue.intValue());
                detectorsSpreadLabel.setText(String.format("%d", newValue.intValue()));
                detectorSpread = newValue.intValue();
            }
        });

        iterationAngleSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if ((double)newValue == 0) newValue = 1;
                iterationAngleSlider.setValue(newValue.intValue());
                double labelValue = newValue.doubleValue() / 100;
                iterationAngleLabel.setText(String.format("%.2f", labelValue));
                iterationAngleDistance = labelValue;
            }
        });

        loadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadImage();
                    }
                });
                t.start();
            }
        });

        runButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loadButton.setDisable(true);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runAllOperations();
                        loadButton.setDisable(false);
                    }
                });
                t.start();
            }
        });
    }
}
