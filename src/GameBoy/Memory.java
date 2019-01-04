package GameBoy;

import java.util.Arrays;

/**
 * Author: Benjamin Baird
 * Created on: 2018-08-30
 * Filename: Memory
 * Description: Holds memory and methods required by the opcodes.
 */
public class Memory {

    /* Memory is split up into the following:
     * $0000 - $7FFF stores pages from a GameBoy cartridge
     *               ($0000 - $3FFF) first 16k bank of cartridge (HOME BANK). Is always accessible here.
     *               ($0100) Stores the games HEADER
     * $8000 - $9FFF is video RAM
     * $A000 - $BFFF is cartridge RAM, if a cartridge HAS any RAM on it. NULL and VOID if no RAM on cartridge.
     * $C000 - $DFFF is internal work RAM (WRAM) for most runtime variables. Other variables will be saved on the cartridge's RAM
     * $E000 - $FDFF specified to copy contents of #C000 - $DDFF, but DO NOT USE FOR ANYTHING.
     * $FE00 - FE9F for object attribute memory (Sprite RAM) (40 sprites max.) Two modes: 8x8 and 8x16, these modes will apply to ALL sprites.
     * $FEA0 - $FEFF is unusable address space.
     * $FF00 - $FFFE is the ZERO page. Lower 64bytes is memory-mapped I/O. Upper 63 bytes is High RAM (HRAM).
     * $FFFF is a single memory-mapped I/O register.
     */
    byte[] memory = new byte[0x1FFFF]; //64k memory

    byte[] nintendoGraphic = {(byte) 0xCE, (byte) 0xED, (byte) 0x66, (byte) 0x66, (byte) 0xCC, (byte) 0x0D,
            (byte) 0x00, (byte) 0x0B, (byte) 0x03, (byte) 0x73, (byte) 0x00, (byte) 0x83,
            (byte) 0x00, (byte) 0x0C, (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x08,
            (byte) 0x11, (byte) 0x1F, (byte) 0x88, (byte) 0x89, (byte) 0x00, (byte) 0x0E,
            (byte) 0xDC, (byte) 0xCC, (byte) 0x6E, (byte) 0xE6, (byte) 0xDD, (byte) 0xDD,
            (byte) 0xD9, (byte) 0x99, (byte) 0xBB, (byte) 0xBB, (byte) 0x67, (byte) 0x63,
            (byte) 0x6E, (byte) 0x0E, (byte) 0xEC, (byte) 0xCC, (byte) 0xDD, (byte) 0xDC,
            (byte) 0x99, (byte) 0x9F, (byte) 0xBB, (byte) 0xB9, (byte) 0x33, (byte) 0x3E};


    public Memory() {
//        Arrays.fill(memory, (byte)0); // Default value of byte is zero. Here if needed.

        // Startup initialization for memory, don't need to do all of this. Could just set registers to final values.
        // Loads all neccessary values into the memory and sets up registers.

        //ADDR_0000:
        memory[0x00] = (byte) 0x31;
        memory[0x01] = (byte) 0xFE;
        memory[0x02] = (byte) 0xFF;
        memory[0x03] = (byte) 0xAF;
        memory[0x04] = (byte) 0x21;
        memory[0x05] = (byte) 0xFF;
        memory[0x06] = (byte) 0x9F;

        // ADDR_0007
        memory[0x07] = (byte) 0x32;
        memory[0x08] = (byte) 0x44;
        memory[0x09] = (byte) 0x07;
        memory[0x0A] = (byte) 0x20;
        memory[0x0B] = (byte) 0x07;

        memory[0x0C] = (byte) 0x21; // Setup Audio
        memory[0x0D] = (byte) 0x26;
        memory[0x0E] = (byte) 0xFF;
        memory[0x0F] = (byte) 0x0E;
        memory[0x10] = (byte) 0x11;

        memory[0x11] = (byte) 0x3E;
        memory[0x12] = (byte) 0x80;
        memory[0x13] = (byte) 0x32;
        memory[0x14] = (byte) 0xE2;
        memory[0x15] = (byte) 0x0C;
        memory[0x16] = (byte) 0x3E;
        memory[0x17] = (byte) 0xF3;
        memory[0x18] = (byte) 0xE2;
        memory[0x19] = (byte) 0x32;
        memory[0x1A] = (byte) 0x3E;
        memory[0x1B] = (byte) 0x77;
        memory[0x1C] = (byte) 0x77;

        memory[0x1D] = (byte) 0x3E; // Setup BG palette
        memory[0x1E] = (byte) 0xFC;
        memory[0x1F] = (byte) 0xE0;
        memory[0x20] = (byte) 0x47;
        memory[0x21] = (byte) 0x11;
        memory[0x22] = (byte) 0x04;
        memory[0x23] = (byte) 0x01;
        memory[0x24] = (byte) 0x21;
        memory[0x25] = (byte) 0x10;
        memory[0x26] = (byte) 0x80;

        // ADDR_0027
        memory[0x27] = (byte) 0x1A;
        memory[0x28] = (byte) 0xCD;
        memory[0x29] = (byte) 0x95;
        memory[0x2A] = (byte) 0x00;
        memory[0x2B] = (byte) 0xCD;
        memory[0x2C] = (byte) 0x96;
        memory[0x2D] = (byte) 0x00;
        memory[0x2E] = (byte) 0x13;
        memory[0x2F] = (byte) 0x7B;

        memory[0x30] = (byte) 0xFE;
        memory[0x31] = (byte) 0x20;
        memory[0x32] = (byte) 0x27;

        memory[0x33] = (byte) 0x11;
        memory[0x34] = (byte) 0x06;
        memory[0x35] = (byte) 0xD8;
        memory[0x36] = (byte) 0x00;
        memory[0x37] = (byte) 0x06;
        memory[0x38] = (byte) 0x08;

        // ADDR_0039
        memory[0x39] = (byte) 0x1A;
        memory[0x3A] = (byte) 0x13;
        memory[0x3B] = (byte) 0x22;
        memory[0x3C] = (byte) 0x23;
        memory[0x3D] = (byte) 0x05;
        memory[0x3E] = (byte) 0x20;
        memory[0x3F] = (byte) 0x39;

        memory[0x40] = (byte) 0x3E;
        memory[0x41] = (byte) 0x19;
        memory[0x42] = (byte) 0xEA;
        memory[0x43] = (byte) 0x10;
        memory[0x44] = (byte) 0x99;
        memory[0x45] = (byte) 0x21;
        memory[0x46] = (byte) 0x2F;
        memory[0x47] = (byte) 0x99;

        // ADDR_0048
        memory[0x48] = (byte) 0x0E;
        memory[0x49] = (byte) 0x0C;

        // ADDR_004A
        memory[0x4A] = (byte) 0x3D;
        memory[0x4B] = (byte) 0x28;
        memory[0x4C] = (byte) 0x55;
        memory[0x4D] = (byte) 0x32;
        memory[0x4E] = (byte) 0x0D;
        memory[0x4F] = (byte) 0x20;
        memory[0x50] = (byte) 0x4A;
        memory[0x51] = (byte) 0x2E;
        memory[0x52] = (byte) 0x0F;
        memory[0x53] = (byte) 0x18;
        memory[0x54] = (byte) 0x58;

        // ADDR_0055
        memory[0x55] = (byte) 0x67;
        memory[0x56] = (byte) 0x3E;
        memory[0x57] = (byte) 0x64;
        memory[0x58] = (byte) 0x57;
        memory[0x59] = (byte) 0xE0;
        memory[0x5A] = (byte) 0x42;
        memory[0x5B] = (byte) 0x3E;
        memory[0x5C] = (byte) 0x91;
        memory[0x5D] = (byte) 0xE0;
        memory[0x5E] = (byte) 0x40;
        memory[0x5F] = (byte) 0x04;

        // ADDR_0060
        memory[0x60] = (byte) 0x1E;
        memory[0x61] = (byte) 0x02;

        // ADDR_0062
        memory[0x62] = (byte) 0x0E;
        memory[0x63] = (byte) 0x0C;


        // ADDR_0064
        memory[0x64] = (byte) 0xF2;
        memory[0x65] = (byte) 0x44;
        memory[0x66] = (byte) 0xFE;
        memory[0x67] = (byte) 0x90;
        memory[0x68] = (byte) 0x20;
        memory[0x69] = (byte) 0x64;
        memory[0x6A] = (byte) 0x0D;
        memory[0x6B] = (byte) 0x20;
        memory[0x6C] = (byte) 0x64;
        memory[0x6D] = (byte) 0x1D;
        memory[0x6E] = (byte) 0x20;
        memory[0x6F] = (byte) 0x62;

        memory[0x70] = (byte) 0x0E;
        memory[0x71] = (byte) 0x13;
        memory[0x72] = (byte) 0x24;
        memory[0x73] = (byte) 0x7C;
        memory[0x74] = (byte) 0x1E;
        memory[0x75] = (byte) 0x83;
        memory[0x76] = (byte) 0xFE;
        memory[0x77] = (byte) 0x64;
        memory[0x78] = (byte) 0x28;
        memory[0x79] = (byte) 0x80;
        memory[0x7A] = (byte) 0x1E;
        memory[0x7B] = (byte) 0xC1;
        memory[0x7C] = (byte) 0xFE;
        memory[0x7D] = (byte) 0x64;
        memory[0x7E] = (byte) 0x20;
        memory[0x7F] = (byte) 0x86;

        // ADDR_0080
        memory[0x80] = (byte) 0x7B;
        memory[0x81] = (byte) 0xE2;
        memory[0x82] = (byte) 0x0C;
        memory[0x83] = (byte) 0x3E;
        memory[0x84] = (byte) 0x87;
        memory[0x85] = (byte) 0xE2;

        // ADDR_0086
        memory[0x86] = (byte) 0xF2;
        memory[0x87] = (byte) 0x42;
        memory[0x88] = (byte) 0x90;
        memory[0x89] = (byte) 0xE0;
        memory[0x8A] = (byte) 0x42;
        memory[0x8B] = (byte) 0x15;
        memory[0x8C] = (byte) 0x20;
        memory[0x8D] = (byte) 0x60;
        memory[0x8E] = (byte) 0x05;
        memory[0x8F] = (byte) 0x20;
        memory[0x90] = (byte) 0xE0;
        memory[0x91] = (byte) 0x16;
        memory[0x92] = (byte) 0x20;
        memory[0x93] = (byte) 0x18;
        memory[0x94] = (byte) 0x60;

        /* Graphic routine */

        memory[0x95] = (byte) 0x4F;
        memory[0x96] = (byte) 0x06;
        memory[0x97] = (byte) 0x04;

        // ADDR_0098
        memory[0x98] = (byte) 0xC5;
        memory[0x99] = (byte) 0x11;
        memory[0x9A] = (byte) 0xCB;
        memory[0x9B] = (byte) 0x17;
        memory[0x9C] = (byte) 0xC1;
        memory[0x9D] = (byte) 0x11;
        memory[0x9E] = (byte) 0xCB;
        memory[0x9F] = (byte) 0x17;
        memory[0xA0] = (byte) 0x05;
        memory[0xA1] = (byte) 0x20;
        memory[0xA2] = (byte) 0x98;
        memory[0xA3] = (byte) 0x22;
        memory[0xA4] = (byte) 0x23;
        memory[0xA5] = (byte) 0x22;
        memory[0xA6] = (byte) 0x23;
        memory[0xA7] = (byte) 0xC9;

        // ADDR_00A8
        // Nintendo Logo

        for (int i = 0xA8; i < 0xD8; i++) {
            memory[0xA8] = nintendoGraphic[i - 0xA8];
        }


        // ADDR_00D8
        // More video data
        memory[0xD8] = (byte) 0x3C;
        memory[0xD9] = (byte) 0x42;
        memory[0xDA] = (byte) 0xB9;
        memory[0xDB] = (byte) 0xA5;
        memory[0xDC] = (byte) 0xB9;
        memory[0xDD] = (byte) 0xA5;
        memory[0xDE] = (byte) 0xA2;
        memory[0xDF] = (byte) 0x3C;

        // ADDR_00E0
        memory[0xE0] = (byte) 0x21;
        memory[0xE1] = (byte) 0x04;
        memory[0xE2] = (byte) 0x01;
        memory[0xE3] = (byte) 0x11;
        memory[0xE4] = (byte) 0xA8;
        memory[0xE5] = (byte) 0x00;

        // ADDR_00E6
        memory[0xE6] = (byte) 0x1A;
        memory[0xE7] = (byte) 0x13;
        memory[0xE8] = (byte) 0xBE;
        memory[0xE9] = (byte) 0x20;
        memory[0xEA] = (byte) 0xFE;
        memory[0xEB] = (byte) 0x23;
        memory[0xEC] = (byte) 0x7D;
        memory[0xED] = (byte) 0xFE;
        memory[0xEE] = (byte) 0x34;
        memory[0xEF] = (byte) 0x20;
        memory[0xF0] = (byte) 0xE6;

        memory[0xF1] = (byte) 0x06;
        memory[0xF2] = (byte) 0x19;
        memory[0xF3] = (byte) 0x78;

        // ADDR_00F4
        memory[0xF4] = (byte) 0x86;
        memory[0xF5] = (byte) 0x23;
        memory[0xF6] = (byte) 0x05;
        memory[0xF7] = (byte) 0x20;
        memory[0xF8] = (byte) 0xF4;
        memory[0xF9] = (byte) 0x86;
        memory[0xFA] = (byte) 0x20;
        memory[0xFB] = (byte) 0xFE;
        memory[0xFC] = (byte) 0x3E;
        memory[0xFD] = (byte) 0x01;
        memory[0xFE] = (byte) 0xE0;
        memory[0xFF] = (byte) 0x50;



        memory[0xFF05] = 0;
        memory[0xFF06] = 0;
        memory[0xFF07] = 0;
        memory[0xFF10] = (byte) 0x80;
        memory[0xFF11] = (byte) 0xBF;
        memory[0xFF12] = (byte) 0xF3;
        memory[0xFF14] = (byte) 0xBF;
        memory[0xFF16] = (byte) 0x3F;
        memory[0xFF17] = (byte) 0x00;
        memory[0xFF19] = (byte) 0xBF;
        memory[0xFF1A] = (byte) 0x7F;
        memory[0xFF1A] = (byte) 0x7F;
        memory[0xFF1B] = (byte) 0xFF;
        memory[0xFF1C] = (byte) 0x9F;
        memory[0xFF1E] = (byte) 0xBF;
        memory[0xFF20] = (byte) 0xFF;
        memory[0xFF21] = (byte) 0x00;
        memory[0xFF22] = (byte) 0x00;
        memory[0xFF23] = (byte) 0xBF;
        memory[0xFF24] = (byte) 0x77;
        memory[0xFF25] = (byte) 0xF3;
        memory[0xFF26] = (byte) 0xF1;
        memory[0xFF26] = (byte) 0xF1;
        memory[0xFF40] = (byte) 0x91;
        memory[0xFF42] = (byte) 0x00;
        memory[0xFF43] = (byte) 0x00;
        memory[0xFF45] = (byte) 0x00;
        memory[0xFF47] = (byte) 0xFC;
        memory[0xFF48] = (byte) 0xFF;
        memory[0xFF49] = (byte) 0xFF;
        memory[0xFF4A] = (byte) 0x00;
        memory[0xFF4B] = (byte) 0x00;
        memory[0xFFFF] = (byte) 0x00;
    }

    public byte getMemVal(short adr) {
        return memory[adr];
    }


    public byte setMemVal(short adr, byte val) {
        memory[adr] = val;
        return memory[adr];
    }

    /**
     * Stores a 16bit value into sequential bytes of memory. LSB's placed in first byte. MSB's placed in second byte
     *
     * @param adr memory address
     * @param val 16bit value to store
     * @return upper 8 bits of val stored in memory.
     */
    public byte setMemVal(short adr, short val) {
        memory[adr] = (byte) (val & 0xFF);
        memory[adr - 1] = (byte) ((val & 0xFF) >> 8);
        return memory[adr - 2];
    }

//    /**
//     * Writes a 8bit to memory. Stores LSB in lower memory adress, MSB in higher address
//     *
//     * @param SP  Stack pointer
//     * @param val 8 bit value
//     * @return SP+1 to indicate new Stack pointer location.
//     */
//    public short push(Registers regs, short SP, byte val) {
//        memory[SP - 1] = val;
//        regs.setSP(SP);
//        return (short) (SP - 1);
//    }

    /**
     * Writes a 16bit to memory. Stores LSB in lower memory adress, MSB in higher address
     *
     * @param SP  Stack pointer
     * @param val 16 bit value
     * @return SP+1 to indicate new Stack pointer location.
     */
    public short push(Registers regs, short SP, short val) {
        memory[SP - 1] = (byte) (val >> 8);
        memory[SP - 2] = (byte) (val);
        regs.setSP((short) (SP - 2));
        return (short) (SP - 2);
    }

    /**
     * Pop off stack. (Little endian) Read from stack, dec SP, Read from stack, dec SP
     *
     * @param regs All registers
     * @param SP   Stack pointer
     * @return The popped 16 bit value.
     */
    public short pop(Registers regs, short SP) {
        short valLower = memory[SP];
        short valUpper = memory[SP + 1];
        regs.setSP((short) (SP + 2));
        return (short) (((valUpper << 8) & 0xFF00) + (valLower & 0xFF) );
    }


}
