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

    private int detectorNumber;
    private double detectorSpread;
    private double iterationAngleDistance;

    private Image imageIn;
    private Image imageSin;
    private Image imageSin2;
    private Image imageOut;
    private Image imageOut2;

    private void clearImageViews() {
        imageViewSin.setImage(null);
        imageViewSin2.setImage(null);
        imageViewOut.setImage(null);
        imageViewOut2.setImage(null);
        imageViewRadar.setImage(null);
    }

    private void loadImage() {
        runButton.setDisable(true);
        loadButton.setDisable(true);
        clearImageViews();
        imageViewIn.setImage(null);
        //imageErrorLabel.setText(String.format("Loading..."));
        try {
            imageIn = new Image(imageTextField.getText());
            imageViewIn.setImage(imageIn);
            //imageErrorLabel.setText("");
            runButton.setDisable(false);
            loadButton.setDisable(false);
        } catch (Exception e) {
            //.setText("Invalid image!");
            imageViewIn.setImage(null);
            runButton.setDisable(true);
            loadButton.setDisable(false);
        }
    }

    private void runAllOperations() {
        loadButton.setDisable(true);
        runButton.setDisable(true);

        clearImageViews();

        try {
            Sinogram sinogram = new Sinogram(detectorNumber, detectorSpread, iterationAngleDistance, imageIn, imageViewSin, imageViewSin2, imageViewRadar);
            imageSin = sinogram.getSinogram();
            imageSin2 = sinogram.getFilteredSinogram();
            sinogram = null;
            imageViewSin.setImage(imageSin);
            imageViewSin2.setImage(imageSin2);

            Reconstructed output = new Reconstructed(detectorNumber, detectorSpread, iterationAngleDistance, imageSin,
                    (int) imageIn.getHeight(), (int)imageIn.getWidth(), imageViewOut);
            Reconstructed output2 = new Reconstructed(detectorNumber, detectorSpread, iterationAngleDistance, imageSin2,
                    (int) imageIn.getHeight(), (int)imageIn.getWidth(), imageViewOut2);
            imageOut = output.getReconstructed();
            imageOut2 = output2.getReconstructed();
            output = null;
            output2 = null;
            imageViewOut.setImage(imageOut);
            imageViewOut2.setImage(imageOut2);
        } catch (Exception e) { System.out.println(" run all error"); }

        loadButton.setDisable(false);
        runButton.setDisable(false);
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
        detectorNumber = (int)numberOfDetectorsSlider.getValue();
        detectorSpread = (int)detectorsSpreadSlider.getValue();
        iterationAngleDistance = iterationAngleSlider.getValue() / 100;

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
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runAllOperations();
                    }
                });
                t.start();
            }
        });
    }
}
