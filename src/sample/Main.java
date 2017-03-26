package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Arrays;


public class Main extends Application {

    // configurable
    // ADD LOGIC TO PARAMETERS
    private int detectorNumber = 600;
    private double detectorSpread = 0.1;
    private double iterationAngleDistance = 0.5;

    // programmed
    private int radius;
    private int width;
    private int height;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Tomography Simulation");

        Controller controller = fxmlLoader.getController();

        Image imageIn = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/SheppLogan_Phantom.svg/220px-SheppLogan_Phantom.svg.png");
        ImageView imageViewIn = controller.getImageViewIn();
        imageViewIn.setImage(imageIn);

        radius = (int)(imageIn.getHeight() / 2) - 1;
        width = (int)imageIn.getWidth();
        height = (int)imageIn.getHeight();

        Image imageSin = createSinogram(imageIn);
        ImageView imageViewSin = controller.getImageViewSin();
        imageViewSin.setImage(imageSin);

        Image imageOut = createOutput(imageSin);
        ImageView imageViewOut = controller.getImageViewOut();
        imageViewOut.setImage(imageOut);

        primaryStage.setScene(new Scene(root, 1000, 500));
        primaryStage.show();
    }

    private Image createSinogram(Image imageIn)
    {
        double transmiterAnglePosition = 0;
        double singleDetectorSpread = detectorSpread / (detectorNumber - 1);
        int iterationsNumber = (int)Math.ceil(360 / iterationAngleDistance); // ceil/floor?

        // new black image
        WritableImage imageSin = new WritableImage(iterationsNumber, detectorNumber);

        double[][] rgbArray = new double[iterationsNumber][detectorNumber];
        for (double[] row : rgbArray) Arrays.fill(row, 0);

        for (int i = 0; i < iterationsNumber; i++)
        {
            double detectorStartAngle = transmiterAnglePosition + 180 - (detectorNumber / 2) * singleDetectorSpread;
            int transmiterX = (int)((Math.sin(Math.toRadians(transmiterAnglePosition)) + 1) * radius);
            int transmiterY = (int)((-Math.cos(Math.toRadians(transmiterAnglePosition)) + 1) * radius);

            double color;
            for (int j = 0; j < detectorNumber; j++)
            {
                color = 0;
                double detectorAngle = detectorStartAngle + j * singleDetectorSpread;
                int detectorX = (int)((Math.sin(Math.toRadians(detectorAngle)) + 1) * radius);
                int detectorY = (int)((-Math.cos(Math.toRadians(detectorAngle)) + 1) * radius);

                int stepX, stepY, pixelX, pixelY, deltaX, deltaY, e;
                if(transmiterX <= detectorX) stepX = 1;
                else stepX = -1;
                if(transmiterY <= detectorY) stepY = 1;
                else stepY = -1;
                deltaX = Math.abs(detectorX - transmiterX);
                deltaY = Math.abs(detectorY - transmiterY);
                pixelX = transmiterX;
                pixelY = transmiterY;

                //argb = imageIn.getPixelReader().getArgb(pixelX, pixelY);
                //color += (argb & 0x00FF0000) >> 16;

                color += imageIn.getPixelReader().getColor(pixelX, pixelY).getRed();

                //imageSin.getPixelWriter().setColor(pixelX, pixelY, Color.rgb(255, 0, 255));

                if(deltaX >= deltaY)
                {
                    e = deltaX / 2;
                    for (int k = 0; k < deltaX; k++)
                    {
                        pixelX += stepX;
                        e -= deltaY;
                        if (e < 0)
                        {
                            pixelY += stepY;
                            e += deltaX;
                        }

                        color += imageIn.getPixelReader().getColor(pixelX, pixelY).getBrightness();

                        //argb = imageIn.getPixelReader().getArgb(pixelX, pixelY);
                        //color += (argb & 0x00FF0000) >> 16;

                        //imageSin.getPixelWriter().setColor(pixelX, pixelY, Color.rgb(255, 0, 255));
                    }
                }
                else // deltaX < deltaY
                {
                    e = deltaY / 2;
                    for (int k = 0; k < deltaY; k++)
                    {
                        pixelY += stepY;
                        e -= deltaX;
                        if (e < 0)
                        {
                            pixelX += stepX;
                            e += deltaY;
                        }

                        color += imageIn.getPixelReader().getColor(pixelX, pixelY).getBrightness();

                        //argb = imageIn.getPixelReader().getArgb(pixelX, pixelY);
                        //color += (argb & 0x00FF0000) >> 16;

                        //imageSin.getPixelWriter().setColor(pixelX, pixelY, Color.rgb(255, 0, 255));
                    }
                }
                rgbArray[i][j] = color;
            }
            transmiterAnglePosition += iterationAngleDistance;
        }

        // normalization
        double maxColor = 0;
        for (int i = 0; i < iterationsNumber; i++)
        {
            for (int j = 0; j < detectorNumber; j++)
            {
                if (maxColor < rgbArray[i][j]) maxColor = rgbArray[i][j];
            }
        }
        for (int i = 0; i < iterationsNumber; i++)
        {
            for (int j = 0; j < detectorNumber; j++)
            {
                rgbArray[i][j] /= maxColor;
                //rgbArray[i][j] *= 255;
                imageSin.getPixelWriter().setColor(i, j, Color.color(rgbArray[i][j], rgbArray[i][j], rgbArray[i][j]));
            }
        }

        return imageSin;
    }

    private Image createOutput(Image imageSin)
    {
        double transmiterAnglePosition = 0;
        double singleDetectorSpread = detectorSpread / (detectorNumber - 1);
        int iterationsNumber = (int)Math.ceil(360 / iterationAngleDistance); // ceil/floor?

        // new black image
        WritableImage imageOut = new WritableImage(width, height);

        double[][] rgbArray = new double[width][height];
        for (double[] row : rgbArray) Arrays.fill(row, 0);

        for (int i = 0; i < iterationsNumber; i++)
        {
            double detectorStartAngle = transmiterAnglePosition + 180 - (detectorNumber / 2) * singleDetectorSpread;
            int transmiterX = (int)((Math.sin(Math.toRadians(transmiterAnglePosition)) + 1) * radius);
            int transmiterY = (int)((Math.cos(Math.toRadians(transmiterAnglePosition)) + 1) * radius);

            double color;
            for (int j = 0; j < detectorNumber; j++)
            {
                color = imageSin.getPixelReader().getColor(i, j).getRed();
                double detectorAngle = detectorStartAngle + j * singleDetectorSpread;
                int detectorX = (int)((Math.sin(Math.toRadians(detectorAngle)) + 1) * radius);
                int detectorY = (int)((Math.cos(Math.toRadians(detectorAngle)) + 1) * radius);

                int stepX, stepY, pixelX, pixelY, deltaX, deltaY, e;
                if(transmiterX <= detectorX) stepX = 1;
                else stepX = -1;
                if(transmiterY <= detectorY) stepY = 1;
                else stepY = -1;
                deltaX = Math.abs(detectorX - transmiterX);
                deltaY = Math.abs(detectorY - transmiterY);
                pixelX = transmiterX;
                pixelY = transmiterY;

                rgbArray[pixelX][pixelY] += color;

                if(deltaX >= deltaY)
                {
                    e = deltaX / 2;
                    for (int k = 0; k < deltaX; k++)
                    {
                        pixelX += stepX;
                        e -= deltaY;
                        if (e < 0)
                        {
                            pixelY += stepY;
                            e += deltaX;
                        }

                        rgbArray[pixelX][pixelY] += color;
                    }
                }
                else // deltaX < deltaY
                {
                    e = deltaY / 2;
                    for (int k = 0; k < deltaY; k++)
                    {
                        pixelY += stepY;
                        e -= deltaX;
                        if (e < 0)
                        {
                            pixelX += stepX;
                            e += deltaY;
                        }

                        rgbArray[pixelX][pixelY] += color;
                    }
                }
            }
            transmiterAnglePosition += iterationAngleDistance;
        }

        // normalization
        double maxColor = 0;
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                if (maxColor < rgbArray[i][j]) maxColor = rgbArray[i][j];
            }
        }
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                rgbArray[i][j] /= maxColor;
                imageOut.getPixelWriter().setColor(i, j, Color.color(rgbArray[i][j], rgbArray[i][j], rgbArray[i][j]));
            }
        }

        return imageOut;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
