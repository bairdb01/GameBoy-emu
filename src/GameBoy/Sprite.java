package GameBoy;

/**
 * Author: Benjamin Baird
 * Created on: 2019-02-18
 * Last Updated on: 2019-02-18
 * Filename: Sprite
 * Description: This represents a sprite. A sprite is an extension of a tile and can be 8x8 or 8x16, with additional properties.
 */
public class Sprite extends Tile{
    boolean xFlip;
    boolean yFlip;
    byte yCoord;
    byte xCoord;
    boolean priority;
    short palette;

    public Sprite(int height, int CHR_CODE){
        this.height = height;
        this.CHR_CODE = CHR_CODE;
        this.bitmap = new int[this.height][this.width];
    }


    public Sprite(int height, byte y, byte x, int CHR_CODE, byte attributes){
        this.height = height;
        this.CHR_CODE = CHR_CODE;
        this.bitmap = new int[this.height][this.width];
        this.yCoord = y;
        this.xCoord = x;

        palette = BitUtils.testBit(attributes, 4) ? (short) 0xFF49 : (short) 0xFF48;
        xFlip = BitUtils.testBit(attributes, 5);
        yFlip = BitUtils.testBit(attributes, 6);
        priority = !BitUtils.testBit(attributes, 7);
    }
}
