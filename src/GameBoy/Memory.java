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
    byte[] memory = new byte[0xFFFF]; //64k memory

    byte[] scrollingNintendoGrapic = {(byte) 0xCE, (byte) 0xED, (byte) 0x66, (byte) 0x66, (byte) 0xCC, (byte) 0x0D,
            (byte) 0x00, (byte) 0x0B, (byte) 0x03, (byte) 0x73, (byte) 0x00, (byte) 0x83,
            (byte) 0x00, (byte) 0x0C, (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x08,
            (byte) 0x11, (byte) 0x1F, (byte) 0x88, (byte) 0x89, (byte) 0x00, (byte) 0x0E,
            (byte) 0xDC, (byte) 0xCC, (byte) 0x6E, (byte) 0xE6, (byte) 0xDD, (byte) 0xDD,
            (byte) 0xD9, (byte) 0x99, (byte) 0xBB, (byte) 0xBB, (byte) 0x67, (byte) 0x63,
            (byte) 0x6E, (byte) 0x0E, (byte) 0xEC, (byte) 0xCC, (byte) 0xDD, (byte) 0xDC,
            (byte) 0x99, (byte) 0x9F, (byte) 0xBB, (byte) 0xB9, (byte) 0x33, (byte) 0x3E};


    public Memory() {
//        Arrays.fill(memory, (byte)0); // Default value of byte is zero. Here if needed.

        // Startup initialization for memory
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
