package GameBoy;

import javax.swing.*;

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

    public void step(MMU mmu) {
        byte lcdStatus = mmu.getMemVal(this.stat); // Load the lcdStatus from register

        switch (lcdStatus & 0xF) {
            // Mode: Searching OAM (sprite information)
            case 2:
                mmu.setMemVal(this.stat, (byte) ((lcdStatus & 0xFFF0) + 0x3)); // Set lcd mode to 3
                break;
            // Mode: Transferring data to LCD Driver
            case 3:
                mmu.setMemVal(this.stat, (byte) (lcdStatus & 0xFFF0)); // Set lcd mode to 0
                break;

            // H-Blank
            case 0:
                // Increase the row to draw
                this.drawRow++;

                // Last H-Blank
                if (drawRow == 143) {
                    mmu.setMemVal(this.stat, (byte) ((lcdStatus & 0xFFF0) + 0x1)); // Set lcd mode to 1
                    screen.createImageWithArray(mainScreenPixels);
                } else {
                    mmu.setMemVal(this.stat, (byte) ((lcdStatus & 0xFFF0) + 0x2)); // Set lcd mode to 2
                }

                break;
            // V-Blank
            case 1:
                drawRow++;
                break;
        }
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
