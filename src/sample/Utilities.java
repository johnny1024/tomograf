package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Utilities {

    public static void copyToImage(double[][] tempArray, WritableImage tempImage) {
        for (int i = 0; i < tempArray.length; i++)
        {
            for (int j = 0; j < tempArray[0].length; j++)
            {
                tempImage.getPixelWriter().setColor(i, j, Color.color(tempArray[i][j], tempArray[i][j],
                        tempArray[i][j]));
            }
        }
    }

    public static void normalize(double[][] tempArray) {
        double maxColor = 0;
        double minColor = 1000000000;
        for (int i = 0; i < tempArray.length; i++)
        {
            for (int j = 0; j < tempArray[0].length; j++)
            {
                if (maxColor < tempArray[i][j]) maxColor = tempArray[i][j];
                if (minColor > tempArray[i][j]) minColor = tempArray[i][j];
            }
        }
        for (int i = 0; i < tempArray.length; i++) {
            for (int j = 0; j < tempArray[0].length; j++) {
                tempArray[i][j] = (tempArray[i][j] - minColor) / (maxColor - minColor);
                //rgbArray[i][j] *= 255;
            }
        }
    }

    public static void saveImage(Image image, String name) {
        File output = new File(name + ".png");
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try
        {
            ImageIO.write(bImage, "png", output);
        } catch (IOException e)
        {
            throw new RuntimeException();
        }
    }
}
