package sample;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class Reconstructed
{
    private int detectorNumber;
    private double detectorSpread;
    private double iterationAngleDistance;
    private Image imageSinogram;

    private WritableImage imageReconstructed;

    // calculated locally
    private int radius;
    private int height;
    private int width;
    private double[][] outputArrayRGB;
    private double singleDetectorSpread;
    private int iterationsNumber;

    public Reconstructed(int detectorNumber, double detectorSpread, double iterationAngleDistance, Image imageSinogram, int height, int width) {
        this.detectorNumber = detectorNumber;
        this.detectorSpread = detectorSpread;
        this.iterationAngleDistance = iterationAngleDistance;
        this.imageSinogram = imageSinogram;

        iterationsNumber = (int)Math.ceil(360 / iterationAngleDistance);
        this.height = height;
        this.width = width;
        radius = (height > width) ? ((width / 2) - 1) : ((height / 2) - 1);
        outputArrayRGB = new double[width][height];
        for (double[] row : outputArrayRGB) Arrays.fill(row, 0);
        singleDetectorSpread = detectorSpread / (detectorNumber - 1);
    }

    private void createReconstructed()
    {
        double transmiterAnglePosition = 0;

        for (int i = 0; i < iterationsNumber; i++)
        {
            double detectorStartAngle = transmiterAnglePosition + 180 - (detectorNumber / 2) * singleDetectorSpread;
            int transmiterX = (int)((Math.sin(Math.toRadians(transmiterAnglePosition)) + 1) * radius);
            int transmiterY = (int)((-Math.cos(Math.toRadians(transmiterAnglePosition)) + 1) * radius);

            double color;
            for (int j = 0; j < detectorNumber; j++)
            {
                color = imageSinogram.getPixelReader().getColor(i, j).getBrightness();
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

                outputArrayRGB[pixelX][pixelY] += color; //(color / ((deltaX > deltaY) ? deltaX : deltaY));
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

                        outputArrayRGB[pixelX][pixelY] += color; //(color / ((deltaX > deltaY) ? deltaX : deltaY));
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

                        outputArrayRGB[pixelX][pixelY] += color; //(color / ((deltaX > deltaY) ? deltaX : deltaY));
                    }
                }
            }
            transmiterAnglePosition += iterationAngleDistance;
        }
    }

    private void normalize() {
        double maxColor = 0;
        double minColor = 1000000000;
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                if (maxColor < outputArrayRGB[i][j]) maxColor = outputArrayRGB[i][j];
                if (minColor > outputArrayRGB[i][j]) minColor = outputArrayRGB[i][j];
            }
        }
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                outputArrayRGB[i][j] = (outputArrayRGB[i][j] - minColor) / (maxColor - minColor);
                imageReconstructed.getPixelWriter().setColor(i, j, Color.color(outputArrayRGB[i][j], outputArrayRGB[i][j], outputArrayRGB[i][j]));
            }
        }
    }

    public Image makeReconstruced()
    {
        imageReconstructed = new WritableImage(width, height);

        createReconstructed();
        normalize();

        return imageReconstructed;
    }

    public Image getReconstructed () {
        return imageReconstructed;
    }
}