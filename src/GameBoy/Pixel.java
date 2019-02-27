package GameBoy;

public class Pixel {
    byte colour;

    public Pixel() {
        colour = 0;
    }

    public Pixel(byte colour) {
        this.colour = colour;
    }

    public void setColour(byte colour) {
        this.colour = colour;
    }
}
