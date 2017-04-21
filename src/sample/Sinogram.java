package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Sinogram {
    // from constructor
    private int detectorNumber;
    private double detectorSpread;
    private double iterationAngleDistance;
    private Image imageInput;

    private WritableImage imageSinogram;

    // calculated locally
    private int radius;
    private double[][] sinogramArrayRGB;
    private double singleDetectorSpread;
    private int iterationsNumber;

    public Sinogram (int detectorNumber, double detectorSpread, double iterationAngleDistance, Image imageInput) {
        this.detectorNumber = detectorNumber;
        this.detectorSpread = detectorSpread;
        this.iterationAngleDistance = iterationAngleDistance;
        this.imageInput = imageInput;

        iterationsNumber = (int)Math.ceil(360 / iterationAngleDistance);
        radius = (imageInput.getHeight() > imageInput.getWidth()) ? ((int)(imageInput.getWidth() / 2) - 1) : ((int)(imageInput.getHeight() / 2) - 1);
        sinogramArrayRGB = new double[iterationsNumber][detectorNumber];
        for (double[] row : sinogramArrayRGB) Arrays.fill(row, 0);
        singleDetectorSpread = detectorSpread / (detectorNumber - 1);
    }

    private void saveImage(Image image, String name)
    {
        File output = new File("D:\\Documents\\!MINE\\Java\\" + name + ".png"); // THIS PATHNAME IS WRONG, WE NEED TO CHANGE IT
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try
        {
            ImageIO.write(bImage, "png", output);
        } catch (IOException e)
        {
            throw new RuntimeException();
        }
    }

    private void fillKernel(double[] kernel)
    {
        int kernelCenterIndex = detectorNumber / 2;
        for (int i = 0; i < detectorNumber; i++)
        {
            kernel[i] = ((i - kernelCenterIndex) % 2 == 0) ? 0 : (-4 / Math.pow(Math.PI, 2)  / Math.pow((i - kernelCenterIndex), 2));
        }
        kernel[kernelCenterIndex] = 1;
    }

    private void normalize() {
        double maxColor = 0;
        double minColor = 1000000000;
        for (int i = 0; i < iterationsNumber; i++)
        {
            for (int j = 0; j < detectorNumber; j++)
            {
                if (maxColor < sinogramArrayRGB[i][j]) maxColor = sinogramArrayRGB[i][j];
                if (minColor > sinogramArrayRGB[i][j]) minColor = sinogramArrayRGB[i][j];
            }
        }
        for (int i = 0; i < iterationsNumber; i++) {
            for (int j = 0; j < detectorNumber; j++) {
                sinogramArrayRGB[i][j] = (sinogramArrayRGB[i][j] - minColor) / (maxColor - minColor);
                //rgbArray[i][j] *= 255;
            }
        }
    }

    private void filter() {
        double[] kernel = new double[detectorNumber];
        fillKernel(kernel);
        int kernelCenterIndex = detectorNumber / 2;
        for (int i = 0; i < iterationsNumber; i++)
        {
            double[] temporaryDetectors = new double[detectorNumber];
            System.arraycopy(sinogramArrayRGB[i], 0, temporaryDetectors, 0, sinogramArrayRGB[i].length);
            Arrays.fill(sinogramArrayRGB[i], 0);
            for (int j = 0; j < detectorNumber; j++)
            {
                int kernelIterator = Math.max(0, kernelCenterIndex - j);
                for (int k = Math.max(0, j - kernelCenterIndex); k < Math.min(detectorNumber, j + kernelCenterIndex); k++)
                {
                    sinogramArrayRGB[i][j] += kernel[kernelIterator] * temporaryDetectors[k];
                    kernelIterator++;
                }
                if (sinogramArrayRGB[i][j] < 0) sinogramArrayRGB[i][j] = 0;
                //rgbArray[i][j] = Math.abs(rgbArray[i][j]);
            }
        }
    }

    private void copyToImage() {
        for (int i = 0; i < iterationsNumber; i++)
        {
            for (int j = 0; j < detectorNumber; j++)
            {
                imageSinogram.getPixelWriter().setColor(i, j, Color.color(sinogramArrayRGB[i][j], sinogramArrayRGB[i][j], sinogramArrayRGB[i][j]));
            }
        }
    }

    private void createSinogram() {
        double transmiterAnglePosition = 0;
        for (int i = 0; i < iterationsNumber; i++)
        {
            double detectorStartAngle = transmiterAnglePosition + 180 - (float)(detectorNumber / 2) * singleDetectorSpread;
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

                color += imageInput.getPixelReader().getColor(pixelX, pixelY).getRed();

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

                        color += imageInput.getPixelReader().getColor(pixelX, pixelY).getBrightness();
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

                        color += imageInput.getPixelReader().getColor(pixelX, pixelY).getBrightness();
                    }
                }
                sinogramArrayRGB[i][j] = color;
                //rgbArray[i][j] = color / ((deltaX > deltaY) ? deltaX : deltaY);
                //rgbArray[i][j] = color / Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
            }
            transmiterAnglePosition += iterationAngleDistance;
        }
    }

    public Image makeSinogram()
    {
        imageSinogram = new WritableImage(iterationsNumber, detectorNumber);

        createSinogram();
        filter();
        normalize();
        copyToImage();

        return imageSinogram;
    }

    public Image getSinogram() {
        return imageSinogram;
    }
}
