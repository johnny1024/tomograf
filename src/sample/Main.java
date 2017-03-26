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
    private int detectorNumber = 3;
    private double detectorSpread = 20;
    private double iterationAngleDistance = 120;

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

        //Image imageIn = new Image("https://static1.squarespace.com/static/55ccf522e4b0fc9c2b651a5d/55ce42e0e4b065516c646a6d/55ce42fbe4b0ef8aac6a0749/1439580951781/Slayer_Repentless_900.jpg");
        Image imageIn = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/SheppLogan_Phantom.svg/220px-SheppLogan_Phantom.svg.png");
        ImageView imageViewIn = controller.getImageViewIn();
        imageViewIn.setImage(imageIn);

        radius = (int)(imageIn.getHeight() / 2) - 3;
        width = (int)imageIn.getWidth();
        height = (int)imageIn.getHeight();

        Image imageSin = createSinogram(imageIn);
        ImageView imageViewSin = controller.getImageViewSin();
        imageViewSin.setImage(imageSin);

//        Image imageSin = createSinogram(imageIn);
//        imageViewIn.setImage(imageSin);
//        Image imageOut = createOutput(imageSin);
//        imageViewIn.setImage(imageOut);

        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }

    private Image test(Image imageIn)
    {
        double transmiterAnglePosition = 0;
        double singleDetectorSpread = detectorSpread / (detectorNumber - 1);
        int iterationsNumber = (int)Math.ceil(360 / iterationAngleDistance); // ceil/floor?

        // new black image
        WritableImage imageSin = new WritableImage(width, height);

        int[][] rgbArray = new int[iterationsNumber][detectorNumber];
        for (int[] row : rgbArray) Arrays.fill(row, 0);

        for (int i = 0; i < iterationsNumber; i++)
        {
            double detectorStartAngle = transmiterAnglePosition + 180 - (detectorNumber / 2) * singleDetectorSpread;
            int transmiterX = (int)((Math.sin(Math.toRadians(transmiterAnglePosition)) + 1) * radius);
            int transmiterY = (int)((-Math.cos(Math.toRadians(transmiterAnglePosition)) + 1) * radius);

            for (int z = transmiterX; z < transmiterX+1; z++)
            {
                for (int x = transmiterY; x < transmiterY+1; x++)
                    imageSin.getPixelWriter().setColor(z, x, Color.rgb(0, 0, 0));
            }

            int color, argb;
            for (int j = 0; j < detectorNumber; j++)
            {
                color = 0;
                double detectorAngle = detectorStartAngle + j * singleDetectorSpread;
                int detectorX = (int)((Math.sin(Math.toRadians(detectorAngle)) + 1) * radius);
                int detectorY = (int)((-Math.cos(Math.toRadians(detectorAngle)) + 1) * radius);

                for (int z = detectorX; z < detectorX+1; z++)
                {
                    for (int x = detectorY; x < detectorY+1; x++)
                        imageSin.getPixelWriter().setColor(z, x, Color.rgb(255, 0, 0));
                }

                // MR. B's alg
                // BEGIN
                int pixelX = transmiterX, pixelY = transmiterY;
                int deltaX = detectorX - transmiterX, deltaY = detectorY - transmiterY;
                if (deltaX == 0)
                {
                    if (deltaY > 0)
                    {
                        for (int k = 0; k < deltaY; k++)
                        {
                            pixelY++;
                            // GET PIXEL
                            argb = imageIn.getPixelReader().getArgb(pixelX, pixelY);
                            color += (argb & 0x00FF0000) >> 16;
                            imageSin.getPixelWriter().setColor(pixelX, pixelY, Color.rgb(255, 0, 255));
                            imageSin.getPixelWriter().setColor(pixelX +1, pixelY, Color.rgb(255, 0, 255));
                            imageSin.getPixelWriter().setColor(pixelX, pixelY+1, Color.rgb(255, 0, 255));
                            imageSin.getPixelWriter().setColor(pixelX+1, pixelY+1, Color.rgb(255, 0, 255));
                        }
                    }
                    else // deltaY < 0
                    {
                        for (int k = 0; k < Math.abs(deltaY); k++)
                        {
                            pixelY--;
                            // GET PIXEL
                            argb = imageIn.getPixelReader().getArgb(pixelX, pixelY);
                            color += (argb & 0x00FF0000) >> 16;
                            imageSin.getPixelWriter().setColor(pixelX, pixelY, Color.rgb(255, 0, 255));
                            imageSin.getPixelWriter().setColor(pixelX +1, pixelY, Color.rgb(255, 0, 255));
                            imageSin.getPixelWriter().setColor(pixelX, pixelY+1, Color.rgb(255, 0, 255));
                            imageSin.getPixelWriter().setColor(pixelX+1, pixelY+1, Color.rgb(255, 0, 255));
                        }
                    }
                }
                else if (Math.abs(deltaX) > Math.abs(deltaY))
                {
                    if (deltaX > 0)
                    {
                        for (int k = 0; k < Math.abs(deltaX); k++)
                        {
                            pixelX++;
                            pixelY = (deltaY / deltaX) * (pixelX - transmiterX) + transmiterY;
                            argb = imageIn.getPixelReader().getArgb(pixelX, pixelY);
                            color += (argb & 0x00FF0000) >> 16;
                            imageSin.getPixelWriter().setColor(pixelX, pixelY, Color.rgb(255, 0, 255));
                            imageSin.getPixelWriter().setColor(pixelX +1, pixelY, Color.rgb(255, 0, 255));
                            imageSin.getPixelWriter().setColor(pixelX, pixelY+1, Color.rgb(255, 0, 255));
                            imageSin.getPixelWriter().setColor(pixelX+1, pixelY+1, Color.rgb(255, 0, 255));
                        }
                    }
                    else // deltaX < 0
                    {
                        for (int k = 0; k < Math.abs(deltaX); k++)
                        {
                            pixelX--;
                            pixelY = (deltaY / deltaX) * (pixelX - transmiterX) + transmiterY;
                            argb = imageIn.getPixelReader().getArgb(pixelX, pixelY);
                            color += (argb & 0x00FF0000) >> 16;
                            imageSin.getPixelWriter().setColor(pixelX, pixelY, Color.rgb(255, 0, 255));
                            imageSin.getPixelWriter().setColor(pixelX +1, pixelY, Color.rgb(255, 0, 255));
                            imageSin.getPixelWriter().setColor(pixelX, pixelY+1, Color.rgb(255, 0, 255));
                            imageSin.getPixelWriter().setColor(pixelX+1, pixelY+1, Color.rgb(255, 0, 255));
                        }
                    }
                }
                else // deltaX < deltaY
                {
                    if (deltaY > 0)
                    {
                        for (int k = 0; k < deltaY; k++)
                        {
                            pixelY++;
                            // ###
                            pixelX = (deltaX / deltaY) * (pixelY - transmiterY) + transmiterX;
                            // ###
                            argb = imageIn.getPixelReader().getArgb(pixelX, pixelY);
                            color += (argb & 0x00FF0000) >> 16;
                            imageSin.getPixelWriter().setColor(pixelX, pixelY, Color.rgb(255, 0, 255));
                            imageSin.getPixelWriter().setColor(pixelX +1, pixelY, Color.rgb(255, 0, 255));
                            imageSin.getPixelWriter().setColor(pixelX, pixelY+1, Color.rgb(255, 0, 255));
                            imageSin.getPixelWriter().setColor(pixelX+1, pixelY+1, Color.rgb(255, 0, 255));
                        }
                    }
                    else // deltaY < 0
                    {
                        for (int k = 0; k < Math.abs(deltaY); k++)
                        {
                            pixelY--;
                            pixelX = (deltaX / deltaY) * (pixelY - transmiterY) + transmiterX;
                            argb = imageIn.getPixelReader().getArgb(pixelX, pixelY);
                            color += (argb & 0x00FF0000) >> 16;
                            imageSin.getPixelWriter().setColor(pixelX, pixelY, Color.rgb(255, 0, 255));
                            imageSin.getPixelWriter().setColor(pixelX +1, pixelY, Color.rgb(255, 0, 255));
                            imageSin.getPixelWriter().setColor(pixelX, pixelY+1, Color.rgb(255, 0, 255));
                            imageSin.getPixelWriter().setColor(pixelX+1, pixelY+1, Color.rgb(255, 0, 255));
                        }
                    }
                }
                // END
                rgbArray[i][j] = color;
            }
            transmiterAnglePosition += iterationAngleDistance;
        }

        // normalization
//        int maxColor = 0;
//        for (int i = 0; i < iterationsNumber; i++)
//        {
//            for (int j = 0; j < detectorNumber; j++)
//            {
//                if (maxColor < rgbArray[i][j]) maxColor = rgbArray[i][j];
//            }
//        }
//        for (int i = 0; i < iterationsNumber; i++)
//        {
//            for (int j = 0; j < detectorNumber; j++)
//            {
//                rgbArray[i][j] /= maxColor;
//                int argb = (0xFF << 24) | (rgbArray[i][j] << 16) | (rgbArray[i][j] << 8) | rgbArray[i][j]; // will this even work?? well it should
//                imageSin.getPixelWriter().setArgb(i, j, argb);
//            }
//        }

        return imageSin;

        // temporary shit
        //return new Image("https://static1.squarespace.com/static/55ccf522e4b0fc9c2b651a5d/55ce42e0e4b065516c646a6d/55ce42fbe4b0ef8aac6a0749/1439580951781/Slayer_Repentless_900.jpg");
    }

    private Image createSinogram(Image imageIn)
    {
        double transmiterAnglePosition = 0;
        double singleDetectorSpread = detectorSpread / (detectorNumber - 1);
        int iterationsNumber = (int)Math.ceil(360 / iterationAngleDistance); // ceil/floor?

        // new black image
        WritableImage imageSin = new WritableImage(iterationsNumber, detectorNumber);

        int[][] rgbArray = new int[iterationsNumber][detectorNumber];
        for (int[] row : rgbArray) Arrays.fill(row, 0);

        for (int i = 0; i < iterationsNumber; i++)
        {
            double detectorStartAngle = transmiterAnglePosition + 180 - (detectorNumber / 2) * singleDetectorSpread;
            int transmiterX = (int)(Math.sin(Math.toRadians(transmiterAnglePosition)) * radius + radius);
            int transmiterY = (int)(Math.cos(Math.toRadians(transmiterAnglePosition)) * radius + radius);

            int color, argb;
            for (int j = 0; j < detectorNumber; j++)
            {
                color = 0;
                double detectorAngle = detectorStartAngle + j * singleDetectorSpread;
                int detectorX = (int)(Math.sin(Math.toRadians(detectorAngle)) * radius + radius);
                int detectorY = (int)(Math.cos(Math.toRadians(detectorAngle)) * radius + radius);

                // MR. B's alg
                // BEGIN
                int pixelX = transmiterX, pixelY = transmiterY;
                int deltaX = detectorX - transmiterX, deltaY = detectorY - transmiterY;
                if (deltaX == 0)
                {
                    if (deltaY > 0)
                    {
                        for (int k = 0; k < deltaY; k++)
                        {
                            pixelY++;
                            // GET PIXEL
                            argb = imageIn.getPixelReader().getArgb(pixelX, pixelY);
                            color += (argb & 0x00FF0000) >> 16;
                        }
                    }
                    else // deltaY < 0
                    {
                        for (int k = 0; k < deltaY; k++)
                        {
                            pixelY--;
                            // GET PIXEL
                            argb = imageIn.getPixelReader().getArgb(pixelX, pixelY);
                            color += (argb & 0x00FF0000) >> 16;
                        }
                    }
                }
                else if (Math.abs(deltaX) > Math.abs(deltaY))
                {
                    if (deltaX > 0)
                    {
                        for (int k = 0; k < Math.abs(deltaX); k++)
                        {
                            pixelX++;
                            pixelY = (deltaY / deltaX) * (pixelX - transmiterX) + transmiterY;
                            argb = imageIn.getPixelReader().getArgb(pixelX, pixelY);
                            color += (argb & 0x00FF0000) >> 16;
                        }
                    }
                    else // deltaX <0
                    {
                        for (int k = 0; k < Math.abs(deltaX); k++)
                        {
                            pixelX--;
                            pixelY = (deltaY / deltaX) * (pixelX - transmiterX) + transmiterY;
                            argb = imageIn.getPixelReader().getArgb(pixelX, pixelY);
                            color += (argb & 0x00FF0000) >> 16;
                        }
                    }
                }
                else // deltaX < deltaY
                {
                    if (deltaY > 0)
                    {
                        for (int k = 0; k < deltaY; k++)
                        {
                            pixelY++;
                            pixelX = (deltaX / deltaY) * (pixelY - transmiterY) + transmiterX;
                            argb = imageIn.getPixelReader().getArgb(pixelX, pixelY);
                            color += (argb & 0x00FF0000) >> 16;
                        }
                    }
                    else // deltaY < 0
                    {
                        for (int k = 0; k < deltaY; k++)
                        {
                            pixelY--;
                            pixelX = (deltaX / deltaY) * (pixelY - transmiterY) + transmiterX;
                            argb = imageIn.getPixelReader().getArgb(pixelX, pixelY);
                            color += (argb & 0x00FF0000) >> 16;
                        }
                    }
                }
                // END
                rgbArray[i][j] = color;
            }
            transmiterAnglePosition += iterationAngleDistance;
        }

        // normalization
        int maxColor = 0;
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
                int argb = (0xFF << 24) | (rgbArray[i][j] << 16) | (rgbArray[i][j] << 8) | rgbArray[i][j]; // will this even work?? well it should
                imageSin.getPixelWriter().setArgb(i, j, argb);
            }
        }

        return imageSin;

        // temporary shit
        //return new Image("https://static1.squarespace.com/static/55ccf522e4b0fc9c2b651a5d/55ce42e0e4b065516c646a6d/55ce42fbe4b0ef8aac6a0749/1439580951781/Slayer_Repentless_900.jpg");
    }

    private Image createOutput(Image imageSin)
    {
        double transmiterAnglePosition = 0;
        double singleDetectorSpread = detectorSpread / (detectorNumber - 1);
        int iterationsNumber = (int)Math.ceil(360 / iterationAngleDistance); // ceil/floor?

        // new black image
        WritableImage imageOut = new WritableImage(width, height);

        int[][] rgbArray = new int[width][height];
        Arrays.fill(rgbArray, 0);

        for (int i = 0; i < iterationsNumber; i++)
        {
            double detectorStartAngle = transmiterAnglePosition + 180 - (detectorNumber / 2) * singleDetectorSpread;
            int transmiterX = (int)(Math.sin(Math.toRadians(transmiterAnglePosition)) * radius + radius);
            int transmiterY = (int)(Math.cos(Math.toRadians(transmiterAnglePosition)) * radius + radius);

            int color;
            for (int j = 0; j < detectorNumber; j++)
            {
                color = imageSin.getPixelReader().getArgb(i, j) & 0x000000FF;
                double detectorAngle = detectorStartAngle + j * singleDetectorSpread;
                int detectorX = (int)(Math.sin(Math.toRadians(detectorAngle)) * radius + radius);
                int detectorY = (int)(Math.cos(Math.toRadians(detectorAngle)) * radius + radius);

                // MR. B's alg
                // BEGIN
                int pixelX = transmiterX, pixelY = transmiterY;
                int deltaX = detectorX - transmiterX, deltaY = detectorY - transmiterY;
                if (deltaX == 0)
                {
                    if (deltaY > 0)
                    {
                        for (int k = 0; k < deltaY; k++)
                        {
                            pixelY++;
                            rgbArray[pixelX][pixelY] += color;
                        }
                    }
                    else // deltaY < 0
                    {
                        for (int k = 0; k < deltaY; k++)
                        {
                            pixelY--;
                            rgbArray[pixelX][pixelY] += color;
                        }
                    }
                }
                else if (Math.abs(deltaX) > Math.abs(deltaY))
                {
                    if (deltaX > 0)
                    {
                        for (int k = 0; k < Math.abs(deltaX); k++)
                        {
                            pixelX++;
                            pixelY = (deltaY / deltaX) * (pixelX - transmiterX) + transmiterY;
                            rgbArray[pixelX][pixelY] += color;
                        }
                    }
                    else // deltaX <0
                    {
                        for (int k = 0; k < Math.abs(deltaX); k++)
                        {
                            pixelX--;
                            pixelY = (deltaY / deltaX) * (pixelX - transmiterX) + transmiterY;
                            rgbArray[pixelX][pixelY] += color;
                        }
                    }
                }
                else // deltaX < deltaY
                {
                    if (deltaY > 0)
                    {
                        for (int k = 0; k < deltaY; k++)
                        {
                            pixelY++;
                            pixelX = (deltaX / deltaY) * (pixelY - transmiterY) + transmiterX;
                            rgbArray[pixelX][pixelY] += color;
                        }
                    }
                    else // deltaY < 0
                    {
                        for (int k = 0; k < deltaY; k++)
                        {
                            pixelY--;
                            pixelX = (deltaX / deltaY) * (pixelY - transmiterY) + transmiterX;
                            rgbArray[pixelX][pixelY] += color;
                        }
                    }
                }
                // END
            }
            transmiterAnglePosition += iterationAngleDistance;
        }

        // normalization
        int maxColor = 0;
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
                int argb = (255 << 24) | (rgbArray[i][j] << 16) | (rgbArray[i][j] << 8) | rgbArray[i][j];
                imageOut.getPixelWriter().setArgb(i, j, argb);
            }
        }

        //return imageOut;

        // temp shit
        return new Image("https://static1.squarespace.com/static/55ccf522e4b0fc9c2b651a5d/55ce42e0e4b065516c646a6d/55ce42fbe4b0ef8aac6a0749/1439580951781/Slayer_Repentless_900.jpg");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
