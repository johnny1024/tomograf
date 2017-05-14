package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class Main extends Application {

    // configurable
    // ADD LOGIC TO PARAMETERS
    private int detectorNumber = 300;
    private double detectorSpread = 300;
    private double iterationAngleDistance = 1;

    private ImageView imageViewIn;
    private ImageView imageViewSin;
    private ImageView imageViewOut;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Tomography Simulation");

        final Controller controller = fxmlLoader.getController();
        controller.init();

        // v1
        Image imageIn = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/SheppLogan_Phantom.svg/220px-SheppLogan_Phantom.svg.png");
        // v2
        //File xxx = new File("https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/SheppLogan_Phantom.svg/220px-SheppLogan_Phantom.svg.png");
        //final Image imageIn = new Image(xxx.toURI().toString());

        imageViewIn = controller.getImageViewIn();
        imageViewIn.setImage(imageIn);

        Sinogram sinogram = new Sinogram(detectorNumber, detectorSpread, iterationAngleDistance, imageIn);
        Image imageSin = sinogram.makeSinogram();
        imageViewSin = controller.getImageViewSin();
        imageViewSin.setImage(imageSin);

        Reconstructed output = new Reconstructed(detectorNumber, detectorSpread, iterationAngleDistance, sinogram.getSinogram(), (int)imageIn.getHeight(), (int)imageIn.getWidth());
        Image imageOut = output.makeReconstruced();
        imageViewOut = controller.getImageViewOut();
        imageViewOut.setImage(imageOut);

        primaryStage.setScene(new Scene(root, 1150, 500));
        primaryStage.show();

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
