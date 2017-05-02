package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class Tomografos extends Application {

    // configurable
    // ADD LOGIC TO PARAMETERS
    int detectorNumber;// = 300;
    double detectorSpread;// = 300;
    double iterationAngleDistance;// = 1;


    void DrawTomo(Controller controller){
        // v1
        //Image imageIn = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/SheppLogan_Phantom.svg/220px-SheppLogan_Phantom.svg.png");
        // v2
        File xxx = new File("obraz.png");
        final Image imageIn = new Image(xxx.toURI().toString());

        ImageView imageViewIn = controller.getImageViewIn();
        imageViewIn.setImage(imageIn);

        Sinogram sinogram = new Sinogram(detectorNumber, detectorSpread, iterationAngleDistance, imageIn);
        Image imageSin = sinogram.makeSinogram();
        ImageView imageViewSin = controller.getImageViewSin();
        imageViewSin.setImage(imageSin);

        Reconstructed output = new Reconstructed(detectorNumber, detectorSpread, iterationAngleDistance, sinogram.getSinogram(), (int)imageIn.getHeight(), (int)imageIn.getWidth());
        Image imageOut = output.makeReconstruced();
        ImageView imageViewOut = controller.getImageViewOut();
        imageViewOut.setImage(imageOut);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Tomography Simulation");

        final Controller controller = fxmlLoader.getController();
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 1100, 550));
        primaryStage.show();

        //DrawTomo(controller, primaryStage);
//        Thread t = new Thread(new Runnable() {
//            public void run()
//            {
//                Image imageSin = createSinogram(imageIn);
//                imageViewSin = controller.getImageViewSin();
//                imageViewSin.setImage(imageSin);
//
//                Image imageOut = createOutput(imageSin);
//                imageViewOut = controller.getImageViewOut();
//                imageViewOut.setImage(imageOut);
//            }
//        });
//        t.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
