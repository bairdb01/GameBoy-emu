package GameBoy;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

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
}
