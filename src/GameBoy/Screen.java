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
    BufferedImage bufferedImage;

    public Screen(int width, int height) {
        this.width = width;
        this.height = height;
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void paint(Graphics g) {
        // Do something
    }

    private Image createImageWithText(String text) {
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

    public void renderScreen(Pixel[][] pixels, int row) {
        Graphics g = this.getGraphics();
        // Load each pixel into the bufferedImage
        for (int j = 0; j < 160; j++) {
            bufferedImage.setRGB(j, row, pixelColour(pixels[row][j]));
        }
        g.drawImage(bufferedImage, 0, 0, this);
    }


    /**
     * Converts the GameBoy's pixel value (0 - 3) into an RGB value
     *
     * @param pixel Colour of pixel in GameBoy (0-3)
     * @return RGB value of pixel
     */
    public int pixelColour(Pixel pixel) {
        int rgb = 0;
        switch (pixel.colour) {
            case 0:
                // White
                rgb = 0xFFFFFF;
                break;
            case 1:
                // Light Grey
                rgb = 0xC0C0C0;
                break;
            case 2:
                // Dark Grey
                rgb = 0x404040;
                break;
            case 3:
                // Black
                rgb = 0;
                break;
        }

        return rgb;
    }
}
