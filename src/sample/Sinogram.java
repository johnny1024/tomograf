package sample;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import java.util.Arrays;

public class Sinogram {
    // from constructor
    private int detectorNumber;
    private double detectorSpread;
    private double iterationAngleDistance;
    private Image imageInput;
    private ImageView imageViewSin;
    private ImageView imageViewSin2;
    private ImageView imageViewRadar;

    private WritableImage imageSinogram;
    private WritableImage imageFilteredSinogram;
    private WritableImage imageRadar;

    // calculated locally
    private int radius;
    private double[][] sinogramArrayRGB;
    private double[][] filteredSinogramArrayRGB;
    private double singleDetectorSpread;
    private int iterationsNumber;

    public Sinogram (int detectorNumber, double detectorSpread, double iterationAngleDistance, Image imageInput, ImageView imageViewSin, ImageView imageViewSin2, ImageView imageViewRadar) {
        this.detectorNumber = detectorNumber;
        this.detectorSpread = detectorSpread;
        this.iterationAngleDistance = iterationAngleDistance;
        this.imageInput = imageInput;
        this.imageViewSin = imageViewSin;
        this.imageViewSin2 = imageViewSin2;
        this.imageViewRadar = imageViewRadar;

        iterationsNumber = (int)Math.ceil(360 / iterationAngleDistance);
        radius = (imageInput.getHeight() > imageInput.getWidth()) ? ((int)(imageInput.getWidth() / 2) - 1) : ((int)(imageInput.getHeight() / 2) - 1);
        sinogramArrayRGB = new double[iterationsNumber][detectorNumber];
        filteredSinogramArrayRGB = new double[iterationsNumber][detectorNumber];
        for (double[] row : sinogramArrayRGB) Arrays.fill(row, 0);
        singleDetectorSpread = detectorSpread / (detectorNumber - 1);

        start();
    }

    private void fillKernel(double[] kernel) {
        int kernelCenterIndex = detectorNumber / 2;
        for (int i = 0; i < detectorNumber; i++)
        {
            kernel[i] = ((i - kernelCenterIndex) % 2 == 0) ? 0 : (-4 / Math.pow(Math.PI, 2)  / Math.pow((i - kernelCenterIndex), 2));
        }
        kernel[kernelCenterIndex] = 1;
    }

    private void filter(double[][] tempArray) {
        double[] kernel = new double[detectorNumber];
        fillKernel(kernel);
        int kernelCenterIndex = detectorNumber / 2;
        for (int i = 0; i < iterationsNumber; i++)
        {
            double[] temporaryDetectors = new double[detectorNumber];
            System.arraycopy(tempArray[i], 0, temporaryDetectors, 0, tempArray[i].length);
            Arrays.fill(tempArray[i], 0);
            for (int j = 0; j < detectorNumber; j++)
            {
                int kernelIterator = Math.max(0, kernelCenterIndex - j);
                for (int k = Math.max(0, j - kernelCenterIndex); k < Math.min(detectorNumber, j + kernelCenterIndex); k++)
                {
                    tempArray[i][j] += kernel[kernelIterator] * temporaryDetectors[k];
                    kernelIterator++;
                }
                if (tempArray[i][j] < 0) tempArray[i][j] = 0;
                //rgbArray[i][j] = Math.abs(rgbArray[i][j]);
            }
        }
    }

    private void createSinogram() {
        double transmiterAnglePosition = 0;
        for (int i = 0; i < iterationsNumber; i++)
        {
            cleanRadar();
            imageViewRadar.setImage(imageRadar);
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

                color += imageInput.getPixelReader().getColor(pixelX, pixelY).getBrightness();
                imageRadar.getPixelWriter().setColor(pixelX, pixelY, Color.color(0, 1, 0));

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
                        imageRadar.getPixelWriter().setColor(pixelX, pixelY, Color.color(0, 1, 0));
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
                        imageRadar.getPixelWriter().setColor(pixelX, pixelY, Color.color(0, 1, 0));
                    }
                }
                sinogramArrayRGB[i][j] = color;
                //rgbArray[i][j] = color / ((deltaX > deltaY) ? deltaX : deltaY);
                //rgbArray[i][j] = color / Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
            }
            transmiterAnglePosition += iterationAngleDistance;
        }
    }

    private void cleanRadar() {
        for (int i = 0; i < imageRadar.getWidth(); i++) {
            for (int j = 0; j < imageRadar.getHeight(); j++) {
                imageRadar.getPixelWriter().setColor(i, j, Color.color(0,0,0));
            }
        }
    }

    public void start() {
        imageSinogram = new WritableImage(iterationsNumber, detectorNumber);
        imageFilteredSinogram= new WritableImage(iterationsNumber, detectorNumber);
        imageRadar = new WritableImage((int)imageInput.getHeight(), (int)imageInput.getWidth());

        createSinogram();
        filteredSinogramArrayRGB = sinogramArrayRGB.clone();

        Utilities.normalize(sinogramArrayRGB);
        Utilities.copyToImage(sinogramArrayRGB, imageSinogram);
        imageViewSin.setImage(imageSinogram);

        filter(filteredSinogramArrayRGB);
        Utilities.normalize(filteredSinogramArrayRGB);
        Utilities.copyToImage(filteredSinogramArrayRGB, imageFilteredSinogram);
        imageViewSin2.setImage(imageFilteredSinogram);

        cleanRadar();
        imageViewRadar.setImage(imageRadar);
    }

    public Image getSinogram() {
        return imageSinogram;
    }

    public Image getFilteredSinogram() { return imageFilteredSinogram; }
}
