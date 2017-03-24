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
    private int detectorNumber = 5;
    private double detectorSpread = 5;
    private double iterationAngleDistance = 30;

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

        radius = (int)(imageIn.getHeight() / 2);

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

    private Image createSinogram(Image imageIn)
    {
        double transmiterAnglePosition = 0;
        double singleDetectorSpread = detectorSpread / (detectorNumber - 1);
        int iterationsNumber = (int)Math.ceil(360 / iterationAngleDistance); // ceil/floor?

        // new black image
        WritableImage imageSin = new WritableImage(iterationsNumber, detectorNumber);

        int[][] rgbArray = new int[(int)imageIn.getWidth()][(int)imageIn.getHeight()];

        for (int i = 0; i < iterationsNumber; i++)
        {
            //int[][][] rgbArray = new int[(int)imageIn.getWidth()][(int)imageIn.getHeight()][3];
            double detectorStartAngle = transmiterAnglePosition + 180 - (detectorNumber / 2) * singleDetectorSpread;
            int transmiterX = (int)(Math.sin(Math.toRadians(transmiterAnglePosition)) * radius); // ??
            int transmiterY = (int)(Math.cos(Math.toRadians(transmiterAnglePosition)) * radius); // ??

            int color, argb;
            for (int j = 0; j < detectorNumber; j++)
            {
                color = 0;
                double detectorAngle = detectorStartAngle + j * singleDetectorSpread;
                int detectorX = (int)(Math.sin(Math.toRadians(detectorAngle)) * radius); // ??
                int detectorY = (int)(Math.cos(Math.toRadians(detectorAngle)) * radius); // ??

                // MR. B's alg
                // BEGIN
                int pixelX = transmiterX, pixelY = transmiterY;
                int deltaX = detectorX - transmiterX, deltaY = detectorY - transmiterY;
                if (deltaX == 0)
                {
                    for (int k = 0; k < deltaY; k++)
                    {
                        pixelY++;
                        // GET PIXEL
                        argb = imageIn.getPixelReader().getArgb(pixelX, pixelY);
                        color += (argb & 0x00FF0000) >> 16;
                    }
                }
                else if (deltaX > deltaY)
                {
                    for (int k = 0; k < deltaX; k++)
                    {
                        pixelX++;
                        pixelY = (deltaY / deltaX) * (pixelX - transmiterX) + transmiterY;
                        // GET PIXEL
                        argb = imageIn.getPixelReader().getArgb(pixelX, pixelY);
                        color += (argb & 0x00FF0000) >> 16;
                    }
                }
                else // deltaX < deltaY
                {
                    for (int k = 0; k < deltaY; k++)
                    {
                        pixelY++;
                        pixelX = (deltaX / deltaY) * (pixelY - transmiterY) + transmiterX;
                        // GET PIXEL
                        argb = imageIn.getPixelReader().getArgb(pixelX, pixelY);
                        color += (argb & 0x00FF0000) >> 16;
                    }
                }
                // END

                //argb = (0xFF >> 24) | (r << 16) | (g << 8) | b; // to nie będzie działać // tablica 3D????
                rgbArray[i][j] = color;
                //imageSin.getPixelWriter().setArgb(j, i, argb);
            }
            transmiterAnglePosition += iterationAngleDistance;
        }

        // normalization
        int maxColor = 0;
        for (int i = 0; i < (int)imageIn.getWidth(); i++)
        {
            for (int j = 0; j < (int)imageIn.getHeight(); j++)
            {
                if (maxColor < rgbArray[i][j]) maxColor = rgbArray[i][j];
            }
        }
        for (int i = 0; i < (int)imageIn.getWidth(); i++)
        {
            for (int j = 0; j < (int)imageIn.getHeight(); j++)
            {
                rgbArray[i][j] /= maxColor;
                int argb = (0xFF << 24) | (rgbArray[i][j] << 16) | (rgbArray[i][j] << 8) | rgbArray[i][j]; // will this even work??
                imageSin.getPixelWriter().setArgb(i, j, argb);
            }
        }

        //return imageSin;

        // temporary shit
        return new Image("https://static1.squarespace.com/static/55ccf522e4b0fc9c2b651a5d/55ce42e0e4b065516c646a6d/55ce42fbe4b0ef8aac6a0749/1439580951781/Slayer_Repentless_900.jpg");
    }

    private Image createOutput(Image imageSin)
    {
        double transmiterAnglePosition = 0;
        double singleDetectorSpread = detectorSpread / (detectorNumber - 1);
        int iterationsNumber = (int)Math.ceil(360 / iterationAngleDistance); // ceil/floor?

        // new black image
        WritableImage imageOut = new WritableImage(iterationsNumber, detectorNumber);

        int[][] rgbArray = new int[(int)imageSin.getWidth()][(int)imageSin.getHeight()];
        // zerowanie array

        for (int i = 0; i < iterationsNumber; i++)
        {
            //int[][][] rgbArray = new int[(int)imageIn.getWidth()][(int)imageIn.getHeight()][3];
            double detectorStartAngle = transmiterAnglePosition + 180 - (detectorNumber / 2) * singleDetectorSpread;
            int transmiterX = (int)(Math.sin(Math.toRadians(transmiterAnglePosition)) * radius); // ??
            int transmiterY = (int)(Math.cos(Math.toRadians(transmiterAnglePosition)) * radius); // ??

            int color, argb;
            for (int j = 0; j < detectorNumber; j++)
            {
                argb = imageSin.getPixelReader().getArgb(i, j); // ??
                color = (argb & 0x00FF0000) >> 16;; // ??
                double detectorAngle = detectorStartAngle + j * singleDetectorSpread;
                int detectorX = (int)(Math.sin(Math.toRadians(detectorAngle)) * radius); // ??
                int detectorY = (int)(Math.cos(Math.toRadians(detectorAngle)) * radius); // ??

                // MR. B's alg
                // BEGIN
                int pixelX = transmiterX, pixelY = transmiterY;
                int deltaX = detectorX - transmiterX, deltaY = detectorY - transmiterY;
                if (deltaX == 0)
                {
                    for (int k = 0; k < deltaY; k++)
                    {
                        pixelY++;
                        // GET PIXEL
                        rgbArray[pixelX][pixelY] += color;

                    }
                }
                else if (deltaX > deltaY)
                {
                    for (int k = 0; k < deltaX; k++)
                    {
                        pixelX++;
                        pixelY = (deltaY / deltaX) * (pixelX - transmiterX) + transmiterY;
                        // GET PIXEL
                        rgbArray[pixelX][pixelY] += color;
                    }
                }
                else // deltaX < deltaY
                {
                    for (int k = 0; k < deltaY; k++)
                    {
                        pixelY++;
                        pixelX = (deltaX / deltaY) * (pixelY - transmiterY) + transmiterX;
                        // GET PIXEL
                        rgbArray[pixelX][pixelY] += color;
                    }
                }
                // END

                //argb = (0xFF >> 24) | (r << 16) | (g << 8) | b; // to nie będzie działać // tablica 3D????
                //imageSin.getPixelWriter().setArgb(j, i, argb);
            }
            transmiterAnglePosition += iterationAngleDistance;
        }

        // normalization
        int maxColor = 0;
        for (int i = 0; i < (int)imageSin.getWidth(); i++)
        {
            for (int j = 0; j < (int)imageSin.getHeight(); j++)
            {
                if (maxColor < rgbArray[i][j]) maxColor = rgbArray[i][j];
            }
        }
        for (int i = 0; i < (int)imageSin.getWidth(); i++)
        {
            for (int j = 0; j < (int)imageSin.getHeight(); j++)
            {
                rgbArray[i][j] /= maxColor;
                int argb = (0xFF << 24) | (rgbArray[i][j] << 16) | (rgbArray[i][j] << 8) | rgbArray[i][j]; // will this even work??
                imageOut.getPixelWriter().setArgb(i, j, argb);
            }
        }

        //return imageSin;

        return new Image("https://static1.squarespace.com/static/55ccf522e4b0fc9c2b651a5d/55ce42e0e4b065516c646a6d/55ce42fbe4b0ef8aac6a0749/1439580951781/Slayer_Repentless_900.jpg");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
