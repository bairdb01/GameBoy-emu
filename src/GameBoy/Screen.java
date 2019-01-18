package GameBoy;

import org.w3c.dom.css.RGBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Author: Benjamin Baird
 * Date: 2019-01-16
 * Date Created: 2019-01-03
 * Description: Represents a screen of the GameBoy
 **/
public class Screen extends JPanel {
    int width;
    int height;

    public Screen(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void paint(Graphics g) {
        // Do something
    }

    private Image createImageWithText(String text) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();
        int i = 0;
        for (String s : text.split("\n")) {
            g.drawString(s, 20, 20 + i);
            i += 20;
        }


//        bufferedImage.setRGB(20, 10, 0xFF0000); // Sets a pixel at location to red
        return bufferedImage;
    }

    public void displayText(String text) {
        Graphics g = this.getGraphics();
        Image img = createImageWithText(text);
        g.drawImage(img, 0, 0, this);

    }

    public void createImageWithArray(byte[][] pixels) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = this.getGraphics();
        // Load each pixel into the bufferedImage
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                bufferedImage.setRGB(i, j, pixelColour(pixels[i][j]));
            }
        }
        g.drawImage(bufferedImage, 0, 0, this);
    }


    /**
     * Converts the GameBoy's pixel value (0 - 3) into an RGB value
     *
     * @param pixel Colour of pixel in GameBoy (0-3)
     * @return RGB value of pixel
     */
    public int pixelColour(byte pixel) {
        int rgb = 0;
        switch (pixel) {
            // Off
            case 0:
                rgb = 0xFFFFFF;
                break;
            // 25% On
            case 1:
                rgb = 0xC0C0C0;
                break;
            // 50% On
            case 2:
                rgb = 0x404040;
                break;
            // 100% on
            case 3:
                rgb = 0;
                break;
        }

        return rgb;
    }
}
