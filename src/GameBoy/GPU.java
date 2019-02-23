package GameBoy;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Author: Benjamin Baird
 * Date: 2019-01-16
 * Date Created: 2019-01-03
 * Description: The GPU of the GameBoy handles all processing of graphics.
 * Resolution of 160 x 144, with 144 visible scanlines and 8 invisible scanlines used during the V-Blank period.
 * The current scanline is stored in adr 0xFF44.
 * It takes 456 cpu clock cycles to draw one scanline
 */
public class GPU {
    JFrame window = new JFrame("SwoleBoy");
    JFrame debugWindow = new JFrame("Debug");
    Screen screen = new Screen(144, 160);
    Screen debug = new Screen(300, 400);

    int lcdc = 0xFF40; /* LCD control register
                    Bit 0:
                    CGB Mode: BG display always on
                    DMG Mode:   0: BG display off
                                1: BG display on
                    Bit 1:
                    OBJ On Flag
                                0: Off
                                1: On
                    Bit 2:
                    OBJ Block Composition Selection Flag
                                0: 8 x 8 dots
                                1: 8 x 16 dots
                    Bit 3:
                    BG Code Area Selection Flag
                                0: 9800h-9BFFh
                                1: 9C00h-9FFFh
                    Bit 4:
                    BG Character Data Selection Flag
                                0: 8800h-97FFh
                                1: 8000h-8FFFh
                    Bit 5:
                    Windowing On Flag
                                0: Off
                                1: On
                    Bit 6:
                    Window Code Area Selection Flag
                                0: 9800h-9BFFh
                                1: 9C00h-9FFFh
                    Bit 7:
                    LCD Controller Operation Stop Flag
                                0: LCDC Off (OFF during v-blank)
                                1: LCDC On
                     */
    int stat = 0xFF41; /* LCD Status flag
                    Bit 0, 1: Mode Flag
                            00: Enable CPU Access to all Display RAM (H-Blank period)
                            01: In vertical blanking period (V-Blank period)
                            10: Searching OAM RAM (OAM being used by LCD controller, inaccessible to CPU)
                            11: Transferring data to LCD Driver  (LCD is using 0AM [FE00 - FE90] and [8000-9FFF], CPU cannot access these areas)

                    Bit 2: Match Flag
                            0: LYC = LCDC LY
                            1: LYC = LCDC LY

                    Bit 3, 4, 5, 6: Interrupt Selection According to LCD Status
                            Mode 00 Selection
                            Mode 01 Selection, 0: not selected
                            Mode 10 Selection, 1: selected
                            LYC = LY matching selection
                     */
    int scroll_y = 0xFF42; // Scroll Y (00 - FF) top location of window on background map
    int scroll_x = 0xFF43; // Scroll X (00 - FF) left location of window on background map
    int ly = 0xFF44; // LCDC y-coordinate. 0 - 153 (144 - 153 represent V-Blank period) (Current scanline)

    // Writing a value of 0 to bit 7 of the CDC reg when its value is 1 stops the LCD controller and LY becomes 0
    int lyc = 0xFF45; // Register LYC is compared with register ly. If they match, the matchflag of the stat register is set.
    int bgp = 0xFF47; /* BG Palette Data
                                Bit 0,1: Data for dot data 00
                                Bit 2,3: Data for dot data 01
                                Bit 4,5: Data for dot data 10
                                Bit 6,7: Data for dot data 11
                                */
    int obp0 = 0xFF48; // OBG Palette Data 0 (bit usage same as bgp), when value of OAM palette selection flag is 0
    int obp1 = 0xFF49; // OBJ Palette Data 1 (bit usage same as bgp), when value of OAM palette selection flag is 1

    int wy = 0xFF4A; // Window y-coordinate 0 <= WY <= 143, window is displayed from the top edge
    int wx = 0xFF4B; // Window x-coordinate 7 <= WX <= 166, window is displayed from the left edge

    int dma = 0xFF46; // DMA Transfer and starting address

    int bg_data_0 = 0x9800; // ($9800 - $9BFF) for BG map 0
    int bg_data_1 = 0x9C00; // ($9C00 - $9FFF) for Bg map 1

    byte[][] mainScreenPixels = new byte[144][160];


    public GPU() {
        window.getContentPane().add(screen);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(200, 200);
        window.setVisible(true);

//        debugWindow.getContentPane().add(debug);
//        debugWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
//        debugWindow.setSize(320, 400);
//        debugWindow.setVisible(true);
    }

    public void draw(Registers regs, MMU mmu) {
//        mainScreenPixels = screen.drawRow(mainScreenPixels[i]);
        screen.createImageWithArray(mainScreenPixels);
//        debug.displayText(regs.toString());

    }

    int drawRow = 0;
    int clockCounter = 0; // Keeps track of the number of CPU cycles passed, to keep CPU/GPU in sync

    public boolean isLCDEnabled(MMU mmu) {
        byte enabledFlag = (byte) (mmu.getMemVal(this.lcdc) >> 7);
        return enabledFlag == (byte) 1;
    }

    /**
     * Performs a batch update of the graphics after every 456 CPU cycles.
     *
     * @param mmu    Memory management unit containing graphics registers
     * @param cycles The number of CPU cycles of the last OP code performed
     */
    public void updateGraphics(MMU mmu, int cycles) {
        updateLCDStatus(mmu);

        if (isLCDEnabled(mmu)) {
            clockCounter += cycles;
        } else {
            // No need to do anything if LCD isn't enabled
            return;
        }

        // It takes the GPU 456 CPU cycles to draw one scanline
        if (clockCounter >= 456) {
            clockCounter = 0;

            // Move to next scanline
            mmu.incScanline();
            byte curScanline = mmu.getMemVal(this.ly);

            // V-Blank period, send interrupt
            if (curScanline == (byte) 144) {
                Interrupts.requestInterupt(mmu, new Interrupt("V-Blank", "GPU", 0));

            } else if (curScanline > (byte) 153) {
                // Finished all scanlines, reset
                mmu.setMemVal(this.ly, (byte) 0);

            } else if (curScanline < (byte) 144) {
                // Draw visible scanline
                drawScanline(mmu);
            }
        }

    }

    /**
     * Sets the mode of LCD status register to val, if this changes the mode
     * an interrupt will be attempted.
     *
     * @param mmu  Memory management unit containing the LCD status register
     * @param mode Mode to change the lcd to ( 0 - 3)
     */
    public void setLCDMode(MMU mmu, byte mode, int mask) {
        byte lcdStatus = mmu.getMemVal(this.stat);
        byte lcdMode = (byte) (lcdStatus & mask);

        if (lcdMode != mode) {
            // When LCD status changes to 0, 1, or 2 an LCD interrupt Request can happen

            if (mode == (byte) 0) {
                int irFlag = ((lcdStatus >> 3) & 0x1);
                if (irFlag == 1) {
                    Interrupts.requestInterupt(mmu, new Interrupt("LCD Interrupt", "Switched to mode 0 (H-Blank)", 1));
                }
            } else if (mode == (byte) 1) {
                int irFlag = ((lcdStatus >> 4) & 0x1);
                if (irFlag == 1) {
                    Interrupts.requestInterupt(mmu, new Interrupt("LCD Interrupt", "Switched to mode 1 (V-Blank)", 1));
                }
            } else if (mode == (byte) 2) {
                int irFlag = ((lcdStatus >> 5) & 0x1);
                if (irFlag == 1) {
                    Interrupts.requestInterupt(mmu, new Interrupt("LCD Interrupt", "Switched to Mode 2 (OAM)", 1));
                }
            } else if (mode == (byte) 4) {
                int irFlag = (lcdStatus >> 6) & 0x1;
                if (irFlag == 1) {
                    Interrupts.requestInterupt(mmu, new Interrupt("LCD Interrupt", "Coincidence (0xFF44 == 0xFF45)", 1));
                }
            }
        }
        mmu.setMemVal(this.stat, (byte) ((lcdMode + mode)));
    }

    /**
     * Updates the LCD status register and requests interruptes if applicable.
     *
     * @param mmu Location of GPU registers
     */
    private void updateLCDStatus(MMU mmu) {
        byte lcdStatus = mmu.getMemVal(this.stat);

        // If LCD is disabled, mode must be 1, clock cycle counter and current scanline must be reset
        if (!isLCDEnabled(mmu)) {
            setLCDMode(mmu, (byte) 1, 0xFC);
            clockCounter = 0;
            mmu.setMemVal(this.ly, (byte) 0);
            return;
        }

        // Sets the mode of the LCD status register
        int mode2Length = 80;
        int mode3Length = 172;
        if (clockCounter < mode2Length) {
            // Searching OAM RAM (OAM being used by LCD controller, inaccessible to CPU) (Mode 2)
            setLCDMode(mmu, (byte) 2, 0xFC);
        } else if (clockCounter < (mode2Length + mode3Length)) {
            // Transferring data to LCD driver. (Mode 3)
            setLCDMode(mmu, (byte) 3, 0xFC);
        } else {
            // Enable CPU access to all display RAM (H-Blank period)
            setLCDMode(mmu, (byte) 0, 0xFC);
        }

        // Perform coincidence check
        LCDCoincidenceCheck(mmu);
    }

    /**
     * Checks registers 0xFF44(ly) and 0xFF45(lyc) to see if they are the same.
     * lyc is the scanline the game is interested in and ly is the current scanline.
     * When they are the same, special effects
     * could be performed by the game.
     * Sets corresponding bit in lcd status register.
     * Interrupt request if applicable.
     *
     * @param mmu Location of GPU registers.
     */
    private void LCDCoincidenceCheck(MMU mmu) {
        byte lcdStatus = mmu.getMemVal(this.stat);

        // Bit 2 of stat is set to 1 if 0xFF44 == 0xFF45, else set to 0
        int coincidenceFlag = 0b0000;
        byte curScanline = mmu.getMemVal(this.ly);
        if (curScanline == mmu.getMemVal(this.lyc)) {
            coincidenceFlag = 0b0100;
        }
        mmu.setMemVal(this.stat, (byte) ((curScanline & 0xFB) + coincidenceFlag));

        setLCDMode(mmu, (byte) 4, 0xFB);
    }


    /**
     * Draws one row of pixels to the screen.
     *
     * @param mmu Memory management unit which contains the LCDC, VRAM, and OAM
     */
    public void drawScanline(MMU mmu) {
        byte lcdControl = mmu.getMemVal(this.lcdc);

        // Draw background/window tiles
        if (BitUtils.testBit(lcdControl, 0)) {
            renderTiles(mmu, lcdControl);
        }

        // Draw sprites
        if (BitUtils.testBit(lcdControl, 1)) {
            renderSprites(mmu, lcdControl);
        }
        mmu.setMemVal(ly, (byte) (mmu.getMemVal(ly) + 1));
        screen.createImageWithArray(mainScreenPixels);
    }

    /**
     * Renders the background/window tiles (8x8 pixels).
     * TODO: Take into account window_y and window_x.
     * @param mmu The memory management unit containing the VRAM
     * @param lcdControl The LCD control register's value
     */
    public void renderTiles(MMU mmu, byte lcdControl) {

        // Upper left starting position of the background to be displayed
        byte scrollX = mmu.getMemVal(this.scroll_x);
        byte scrollY = mmu.getMemVal(this.scroll_y);

        // X,Y positions of the window area to start drawing the window from
        byte windowY = mmu.getMemVal(this.wy);
        byte windowX = (byte)(mmu.getMemVal(this.wx) - 7);      // value of wx is offset by 7 to allow scrolling in, 7 <= windowX <=166

        // Check if loading normal Background Tiles or window background tiles
        int bgDataAdr;
        int winDataAdr = bg_data_0;
        boolean usingWindow = false;
        if (BitUtils.testBit(lcdControl, 5)) {
            usingWindow = true;
            // Window tiles needed
            if (BitUtils.testBit(lcdControl, 6)) {
                winDataAdr = bg_data_1;
            } else {
                winDataAdr = bg_data_0;
            }
        }

        // Only Background tiles needed
        if (BitUtils.testBit(lcdControl, 3)) {
            bgDataAdr = bg_data_1;
        } else {
            bgDataAdr = bg_data_0;
        }


        // Check where the Bitmap/tile data is located
        int tileMapAdr;
        boolean signed = false;
        if (BitUtils.testBit(lcdControl, 4)) {
            tileMapAdr = 0x8000;
        } else {
            tileMapAdr = 0x8800;
            signed = true;
        }

        // Draw tiles at the current scanline (LY)
        Tile tile;
        int curScanline = mmu.getMemVal(ly);
        int curRow = (curScanline) / 8; // Account for the block size of 8 (18 blocks)
        for (int curCol = 0; curCol < 20; curCol++) {
            // Find current block/tile
            int blockX = (scrollX / 8 + curCol) % 32;
            int blockY = (32 * (scrollY / 8 + curRow));

            // Check if current pixel is for a window
            int tileDataAdr;
            if (usingWindow && curScanline >= windowY && (windowX) >= curCol * 8) {
                // Within window area
//                blockX = curCol*8 - windowX;
//                blockY = (curScanline - windowY)/8;
                tileDataAdr = winDataAdr;
            } else {
                tileDataAdr = bgDataAdr;
            }

            int blockNum = blockY + blockX;

            // Load CHR_CODE
            int chrCode = (tileDataAdr + blockNum);
            tile = new Tile(mmu.getMemVal(chrCode));

            // Check if CHR_CODE will be signed
            if (signed) {
                chrCode += 128;
            }
            int tileAdr = (tileMapAdr + chrCode * 16);

            // Load Bitmap from tile address
            byte[] bitmap = new byte[16];  // 16 because 2 bytes create 1 row. 16/2 = 8 rows
            for (int i = 0; i < 16; i++) {
                bitmap[i] = mmu.getMemVal((tileAdr + i));
            }
            tile.setBitmap(bitmap);

            // Tile is ready to be drawn in it's 8x8 location
            drawTile(tile, curRow % 8, curCol * 8);
        }
    }

    /**
     * Renders a row of sprites onto the LCD screen.
     * @param mmu Memory management unit containing the OAM and VRAM.
     * @param lcdControl The LCDC register's value.
     */
    public void renderSprites(MMU mmu, byte lcdControl){
        int height = BitUtils.testBit(lcdControl, 2)? 16 : 8;   // LCDC Bit 2 - OBJ (Sprite) Size (0=8x8, 1=8x16)
        for (int spriteNumber = 0; spriteNumber < 40; spriteNumber++) {
            // All 40 sprites are stored at 0xFE00 to 0xFE9F and consist of 4 bytes
            int spriteAdr = 0xFE00 + (spriteNumber * 4);

            // Each sprite consists of 4 bytes
            byte y_coord = mmu.getMemVal((spriteAdr));                               // Byte 1: LCD y_coordinate
            byte x_coord = mmu.getMemVal((spriteAdr + 1));                           // Byte 2: LCD x_coordinate
            byte chr_code = (byte) ((mmu.getMemVal((spriteAdr + 2)) >> 1) << 1);    // Byte 3: CHR_CODE or tile code. Odd CHR_CODES get rounded down. 1->0. 3->2.
            byte attributes = mmu.getMemVal((spriteAdr + 3));                        // Byte 4: Attribute flag - Palette, Horizontal/Vertical Flip Flag, and Priority

            // Load Bitmap from sprite address and CHR_CODE
            byte [] bitmap = new byte[height * 2];
            int tileAdr = (0x8000 + chr_code * 16);
            for (int i = 0; i < (2 * height); i++) { // Account of 8x8 or 8x16 sprites
                bitmap[i] = mmu.getMemVal((tileAdr + i));
            }

            Sprite sprite = new Sprite(height, y_coord, x_coord, chr_code, attributes);
            sprite.setBitmap(bitmap);

            // Draw sprite
            drawTile(sprite, y_coord, x_coord);
        }
    }

    /**
     * Draws a tile to the LCD's current scanline.
     * TODO: Account for priority when drawing.
     * TODO: Account for palette selection.
     * @param t The tile to draw.
     * @param scanlineY The current scanline we are drawing (0-143)
     * @param col The position of the tile within the row.
     */
    public void drawTile(Tile t, int scanlineY, int col) {
        for (int i = 0; i < 8; i++) {
            mainScreenPixels[scanlineY][col + i] = t.getPixel(scanlineY % 8, col + i);
        }
    }
}

