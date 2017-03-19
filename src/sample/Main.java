package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Main extends Application {

    // configurable
    private int detectorNumber;
    private double detectorSpread;
    private double iterationAngleDistance;

    // programmed
    private int radius;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Tomography Simulation");

        Controller controller = fxmlLoader.getController();

        Image imageIn = new Image("https://static1.squarespace.com/static/55ccf522e4b0fc9c2b651a5d/55ce42e0e4b065516c646a6d/55ce42fbe4b0ef8aac6a0749/1439580951781/Slayer_Repentless_900.jpg");
        ImageView imageViewIn = controller.getImageViewIn();
        imageViewIn.setImage(imageIn);

        //Image imageSin = createSinogram();
        //ImageView imageViewSin = controller.getImageViewSin();
        //imageViewSin.setImage(imageSin);

        Image imageSin = createSinogram(imageIn);
        imageViewIn.setImage(imageSin);
        Image imageOut = createOutput(imageSin);
        imageViewIn.setImage(imageOut);

        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }

    public Image createSinogram(Image imageIn)
    {
        double transmiterAnglePosition = 0;
        double singleDetectorSpread = detectorSpread / (detectorNumber - 1);
        int iterationsNumber = (int)Math.ceil(360 / iterationAngleDistance); // ceil/floor?

        // new black image
        WritableImage imageSin = new WritableImage(iterationsNumber, detectorNumber);

        for (int i = 0; i < iterationsNumber; i++)
        {
            double detectorStartAngle = transmiterAnglePosition + 180 - (detectorNumber / 2) * singleDetectorSpread;
            double transmiterX = Math.sin(Math.toRadians(transmiterAnglePosition)) * radius; // ??
            double transmiterY = Math.cos(Math.toRadians(transmiterAnglePosition)) * radius; // ??

            int r, g ,b;
            for (int j = 0; j < detectorNumber; j++)
            {
                r = 0; g = 0; b = 0;
                double detectorAngle = detectorStartAngle + j * singleDetectorSpread;
                double detectorX = Math.sin(Math.toRadians(detectorAngle)) * radius; // ??
                double detectorY = Math.cos(Math.toRadians(detectorAngle)) * radius; // ??
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                // MR. B's alg
                // BEGIN

                int x = 0, y = 0, argb = 0;
                argb = imageIn.getPixelReader().getArgb(x, y);

                r += (int)((argb & 0x00FF0000) >> 16);
                g += (int)((argb & 0x0000FF00) >> 8);
                b += (int)((argb & 0x000000FF));

                // END

                argb = (0xFF >> 24) | (r << 16) | (g << 8) | b; // to nie będzie działać // tablica 3D????
                imageSin.getPixelWriter().setArgb(j, i, argb);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            }

            transmiterAnglePosition += iterationAngleDistance;
        }

        // temporary shit
        return new Image("https://static1.squarespace.com/static/55ccf522e4b0fc9c2b651a5d/55ce42e0e4b065516c646a6d/55ce42fbe4b0ef8aac6a0749/1439580951781/Slayer_Repentless_900.jpg");

        //return imageSin;
    }

    public Image createOutput(Image imageSin)
    {
        return new Image("https://static1.squarespace.com/static/55ccf522e4b0fc9c2b651a5d/55ce42e0e4b065516c646a6d/55ce42fbe4b0ef8aac6a0749/1439580951781/Slayer_Repentless_900.jpg");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
