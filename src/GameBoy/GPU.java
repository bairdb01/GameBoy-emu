package GameBoy;

import javax.swing.*;

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

    short lcdc = (short) 0xFF40; /* LCD control register
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
    short stat = (short) 0xFF41; /* LCD Status flag
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
    short scroll_y = (short) 0xFF42; // Scroll Y (00 - FF) top location of window on background map
    short scroll_x = (short) 0xFF43; // Scroll X (00 - FF) left location of window on background map
    short ly = (short) 0xFF44; // LCDC y-coordinate. 0 - 153 (144 - 153 represent V-Blank period) (Current scanline)

    // Writing a value of 0 to bit 7 of the CDC reg when its value is 1 stops the LCD controller and LY becomes 0
    short lyc = (short) 0xFF45; // Register LYC is compared with register ly. If they match, the matchflag of the stat register is set.
    short bgp = (short) 0xFF47; /* BG Palette Data
                                Bit 0,1: Data for dot data 00
                                Bit 2,3: Data for dot data 01
                                Bit 4,5: Data for dot data 10
                                Bit 6,7: Data for dot data 11
                                */
    short obp0 = (short) 0xFF48; // OBG Palette Data 0 (bit usage same as bgp), when value of OAM palette selection flag is 0
    short obp1 = (short) (short) 0xFF49; // OBJ Palette Data 1 (bit usage same as bgp), when value of OAM palette selection flag is 1
    short wy = (short) 0xFF4A; // Window y-coordinate 0 <= WY <= 143, window is displayed from the top edge
    short wx = (short) 0xFF4B; // Window x-coordinate 7 <= WX <= 166, window is displayed from the left edge

    // OAM Register OBJ0 (OBJ1 - OBJ30 have same composition, registers are probably sequential)
    short obj0_lcdy = (short) 0xFE00; // LCD y-coordinate (00 - FF)
    short obj0_lcdx = (short) 0xFE01; // LCD x-coordinate (0x00-0xFF)
    short obj0_chr = (short) 0xFE02; // CHR code (0x00 - 0xFF)
    short obj0_attr_flag = (short) 0xFE03; /* Attribute flag
                                            Bit 0,1,2: Specifies color palette (CGB only)
                                            Bit 3: Specifies character bank (CGB only)
                                            Bit 4: Specifies palette for DMG and DMG mode (valid only in DMG mode)
                                            Bit 5: Horizontal flip flag
                                                    0: Normal
                                                    1: Flip horizontally
                                            Bit 6: Vertical flip flag
                                                    0: Normal
                                                    1: Flip vertically
                                            Bit 7: Display priority flag
                                                    0: Priority to OBJ
                                                    1: Priority to BG
                                            */
    short dma = (short) 0xFF46; // DMA Transfer and starting address

    short bg_data_0 = (short) 0x9800; // ($9800 - $9BFF) for BG map 0
    short bg_data_1 = (short) 0x9C00; // ($9C00 - $9FFF) for Bg map 1

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
//                drawScanline(mmu);
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

    public void renderRow(MMU mmu) {

        // Load location on background map
        int scroll_x = mmu.getMemVal(this.scroll_x);
        int scroll_y = mmu.getMemVal(this.scroll_y);

        // Load LCDC flags
        byte lcdc = mmu.getMemVal(this.lcdc);
        int objFlag = lcdc & 0b10 >> 1;
        int objBlockSizeFlag = lcdc & 0b100 >> 2;
        int bgFlag = lcdc & 0b1000 >> 3;
        int bgCharacterFlag = lcdc & 0b10000 >> 4;
        int windowingFlag = lcdc & 0b100000 >> 5;
        int windowCodeAreaFlag = lcdc & 0b1000000 >> 6;
        int lcdcStopFlag = lcdc & 0b10000000 >> 7;

        // VRAM offset for the tile map
        short bgMemOffset;
        if (bgFlag == 1)
            bgMemOffset = 0x1C00;
        else {
            bgMemOffset = 0x1800;
        }

        // Which line of tiles to use in the map
        bgMemOffset += ((this.drawRow + scroll_y) & 255) >> 3;

        // Which tile to start with in the map line
        int lineoffs = (this.scroll_x >> 3);

        // Which line of pixels to use in the tiles
        int y = (this.drawRow + scroll_y) & 7;

        // Where in the tileline to start
        int x = scroll_x & 7;

        // Where to render on the canvas
        int canvasoffs = this.drawRow * 160 * 4;

        // Read tile index from the background map
        int tile = mmu.getMemVal((short) (bgMemOffset + lineoffs));

        // If the tile data set in use is #1, the
        // indices are signed; calculate a real tile offset
//        if(GPU._bgtile == 1 && tile < 128) tile += 256;

        // Drawing tiles to the pixels
        for (int i = 0; i < 144; i++) {
            for (int j = 0; j < 156; j += 4) {
//                mmu.setMemVal(this.bgp, colours);
                mainScreenPixels[i][j] = 0;
                mainScreenPixels[i][j + 1] = 1;
                mainScreenPixels[i][j + 2] = 2;
                mainScreenPixels[i][j + 3] = 3;
            }

        }

        screen.createImageWithArray(mainScreenPixels);
    }

}
