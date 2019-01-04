package GameBoy;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;

public class GPU {
    JFrame window = new JFrame("SwoleBoy");
    Screen screen = new Screen();
    int height = 160;
    int width = 144;

    public GPU() {

        window.getContentPane().add(screen);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(width, height);
        window.setVisible(true);

    }
}
