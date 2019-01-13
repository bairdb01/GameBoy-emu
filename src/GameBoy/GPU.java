package GameBoy;

import javax.swing.*;

public class GPU {
    JFrame window = new JFrame("SwoleBoy");
    JFrame debugWindow = new JFrame("Debug");
    Screen screen = new Screen(160, 144);
    Screen debug = new Screen(300, 400);

    short lcdc = (short) 0xFF40; /* LCD control register
                    Byte 0:
                    CGB Mode: BG display always on
                    DMG Mode:   0: BG display off
                                1: BG display on
                    Byte 1:
                    OBJ On Flag
                                0: Off
                                1: On
                    Byte 2:
                    OBJ Block Composition Selection Flag
                                0: 8 x 8 dots
                                1: 8 x 16 dots
                    Byte 3:
                    BG Code Area Selection Flag
                                0: 9800h-9BFFh
                                1: 9C00h-9FFFh
                    Byte 4:
                    BG Character Data Selection Flag
                                0: 8800h-97FFh
                                1: 8000h-8FFFh
                    Byte 5:
                    Windowing On Flag
                                0: Off
                                1: On
                    Byte 6:
                    Window Code Area Selection Flag
                                0: 9800h-9BFFh
                                1: 9C00h-9FFFh
                    Byte 7:
                    LCD Controller Operation Stop Flag
                                0: LCDC Off (OFF during v-blank)
                                1: LCDC On
                     */
    short stat = (short) 0xFF41; /* LCD Status flag
                    Byte 0, 1: Mode Flag
                            00: Enable CPU Access to all Display RAM (H-Blank period)
                            01: In vertical blanking period (V-Blank period)
                            10: Searching OAM RAM (OAM being used by LCD controller, inaccessible to CPU)
                            11: Transferring data to LCD Driver  (LCD is using 0AM [FE00 - FE90] and [8000-9FFF], CPU cannot access these areas)

                    Byte 2: Match Flag
                            0: LYC = LCDC LY
                            1: LYC = LCDC LY

                    Byte 3, 4, 5, 6: Interrupt Selection According to LCD Status
                            Mode 00 Selection
                            Mode 01 Selection, 0: not selected
                            Mode 10 Selection, 1: selected
                            LYC = LY matching selection
                     */
    short scroll_y = (short) 0xFF42; // Scroll Y (00 - FF)
    short scroll_x = (short) 0xFF43; // Scrp;; X (00 - FF)
    short ly = (short) 0xFF44; // LCDC y-coordinate. 0 - 153 (144 - 153 represent V-Blank period)
    // Writing a value of 0 to bit 7 of the CDC reg when its value is 1 stops the LCD controller and LY becomes 0
    short lyc = (short) 0xFF45; // Register LYC is compared with register ly. If they match, the matchflag of the stat register is set.
    short bgp = (short) 0xFF47; /* BG Palette Data
                                Byte 0,1: Data for dot data 00
                                Byte 2,3: Data for dot data 01
                                Byte 4,5: Data for dot data 10
                                Byte 6,7: Data for dot data 11
                                */
    short obp0 = (short) 0xFF48; // OBG Paletter Data 0 (bit usage same as bgp), when value of OAM palette selection flag is 0
    short obp1 = (short) (short) 0xFF49; // OBJ Palette Data 1 (bit usage same as bgp), when value of OAM palette selection flag is 1
    short wy = (short) 0xFF4A; // Window y-coordinate 0 <= WY <= 143, window is displayed from the top edge
    short wx = (short) 0xFF4B; // Window x-coordinate 7 <= WX <= 166, window is displayed from the left edge

    // OAM Register OBJ0 (OBJ1 - OBJ30 have same composition, registers are probably sequential)
    short obj0_lcdy = (short) 0xFE00; // LCD y-coordinate (00 - FF)
    short obj0_lcdx = (short) 0xFE01; // LCD x-coordinate (0x00-0xFF)
    short obj0_chr = (short) 0xFE02; // CHR code (0x00 - 0xFF)
    short obj0_attr_flag = (short) 0xFE03; /* Attribute flag
                                            Byte 0,1,2: Specifies color palette (CGB only)
                                            Byte 3: Specifies character bank (CGB only)
                                            Byte 4: Specifies palette for DMG and DMG mode (valid only in DMG mode)
                                            Byte 5: Horizontal flip flag
                                                    0: Normal
                                                    1: Flip horizontally
                                            Byte 6: Vertical flip flag
                                                    0: Normal
                                                    1: Flip vertically
                                            Byte 7: Display priority flag
                                                    0: Priority to OBJ
                                                    1: Priority to BG
                                            */
    short dma = (short) 0xFF46; // DMA Transfer and starting address


    public GPU() {

        window.getContentPane().add(screen);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(200, 200);
        window.setVisible(true);

        debugWindow.getContentPane().add(debug);
        debugWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        debugWindow.setSize(320, 400);
        debugWindow.setVisible(true);
    }
}
