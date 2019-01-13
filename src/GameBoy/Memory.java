package GameBoy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Author: Benjamin Baird
 * Created on: 2018-08-30
 * Filename: Memory
 * Description: Holds memory and methods required by the opcodes.
 *  Any access to an array is masked with 0xFFFF so the array can be access with signed shorts without being considered a negative number
 *  TODO: Double check boot sequence memory. Need to change all JR cc, n. The n should be offset not absolute address.
 */
public class Memory {

    /* Memory is split up into the following:
     * $0000 - $7FFF stores pages from a GameBoy cartridge
     *               ($0000 - $3FFF) first 16k bank of cartridge (HOME BANK). Is always accessible here.
     *               ($0100) Stores the games HEADER
     * $8000 - $9FFF is video RAM
     * $A000 - $BFFF is cartridge/external RAM, if a cartridge HAS any RAM on it. NULL and VOID if no RAM on cartridge.
     * $C000 - $DFFF is internal work RAM (WRAM) for most runtime variables. Other variables will be saved on the cartridge's RAM
     * $E000 - $FDFF specified to copy contents of #C000 - $DDFF, but DO NOT USE FOR ANYTHING.
     * $FE00 - FE9F for object attribute memory (Sprite RAM) (40 sprites max.) Two modes: 8x8 and 8x16, these modes will apply to ALL sprites.
     * $FEA0 - $FEFF is unusable address space.
     * $FF00 - $FFFE is the ZERO page. Lower 64bytes is memory-mapped I/O. Upper 63 bytes is High RAM (HRAM).
     * $FFFF is a single memory-mapped I/O register.
     */
    byte[] rom = new byte[0x4000];
    byte[] vram = new byte[0x2000];
    byte[] eram = new byte[0x2000];
    byte[] wram = new byte[0x2000];

    byte[] zram = new byte[0x80];
    byte[] oam = new byte[0xA0];

    byte[] nintendoGraphic = {(byte) 0xCE, (byte) 0xED, (byte) 0x66, (byte) 0x66, (byte) 0xCC, (byte) 0x0D,
            (byte) 0x00, (byte) 0x0B, (byte) 0x03, (byte) 0x73, (byte) 0x00, (byte) 0x83,
            (byte) 0x00, (byte) 0x0C, (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x08,
            (byte) 0x11, (byte) 0x1F, (byte) 0x88, (byte) 0x89, (byte) 0x00, (byte) 0x0E,
            (byte) 0xDC, (byte) 0xCC, (byte) 0x6E, (byte) 0xE6, (byte) 0xDD, (byte) 0xDD,
            (byte) 0xD9, (byte) 0x99, (byte) 0xBB, (byte) 0xBB, (byte) 0x67, (byte) 0x63,
            (byte) 0x6E, (byte) 0x0E, (byte) 0xEC, (byte) 0xCC, (byte) 0xDD, (byte) 0xDC,
            (byte) 0x99, (byte) 0x9F, (byte) 0xBB, (byte) 0xB9, (byte) 0x33, (byte) 0x3E};

    /**
     * Gets a byte from memory
     *
     * @param adr Address of byte in memory from 0x000 to 0xFFFF
     * @return A byte from the corresponding address
     */
    public byte getMemVal(short adr) {
        // Split up to handle the varying types memory blocks
        switch (adr & 0xF000) {

            // BIOS(256b)/ROM0
            case 0x0000:
                return rom[adr];

            // ROM0 (Unbanked)(16k)
            case 0x1000:
            case 0x2000:
            case 0x3000:
                return rom[adr];

            // ROM1 (Unbanked)(16k)
            case 0x4000:
            case 0x5000:
            case 0x6000:
            case 0x7000:
                return rom[adr];

            // Graphics (VRAM)(8k)
            case 0x8000:
            case 0x9000:
                return vram[adr & 0x1FFF]; // Size of VRAM is 0x9FFF - 0x8000 = 0x1FFF = 8k

            // External RAM (8k)
            case 0xA000:
            case 0xB000:
                return eram[adr & 0x1FFF]; // Size of ERAM is 0xBFFF - 0xA000 = 0x1FFF

            // Working RAM (8k)
            case 0xC000:
            case 0xD000:
                return wram[adr & 0x1FFF]; // Size of VRAM is 0xDFFF - 0xC000 = 0x1FFF

            // Working RAM duplicate (first half)
            case 0xE000:
                return wram[adr & 0x1FFF];

            // Working RAM duplicate (2nd half), I/O, Zeo-page RAM
            case 0xF000:
                switch (adr & 0x0F00) {
                    // Working RAM duplicate
                    case 0x000:
                    case 0x100:
                    case 0x200:
                    case 0x300:
                    case 0x400:
                    case 0x500:
                    case 0x600:
                    case 0x700:
                    case 0x800:
                    case 0x900:
                    case 0xA00:
                    case 0xB00:
                    case 0xC00:
                    case 0xD00:
                        return wram[adr & 0x1FFF];

                    // Graphics: Object attribute memory (160byte, remaining bytes are 0)
                    case 0xE00:
                        if (adr < 0xFEA0) {
                            return oam[adr & 0xFF];
                        } else {
                            return 0;
                        }

                        // Zero-page
                    case 0xF00:
                        if (adr >= 0xFF80) {
                            return zram[adr & 0x7F];
                        } else {
                            // TODO: I/O handling
                            return 0;
                        }
                }
        }
        return 0;
    }

    /**
     * Stores an 8bit value into memory.
     *
     * @param adr memory address
     * @param val 8bit value to store
     */
    public void setMemVal(short adr, byte val) {
        // Split up to handle the varying types memory blocks
        switch (adr & 0xF000) {

            // BIOS(256b)/ROM0
            case 0x0000:
                rom[adr] = val;
                break;

            // ROM0 (Unbanked)(16k)
            case 0x1000:
            case 0x2000:
            case 0x3000:
                rom[adr] = val;

                // ROM1 (Unbanked)(16k)
            case 0x4000:
            case 0x5000:
            case 0x6000:
            case 0x7000:
                rom[adr] = val;

                // Graphics (VRAM)(8k)
            case 0x8000:
            case 0x9000:
                vram[adr & 0x1FFF] = val; // Size of VRAM is 0x9FFF - 0x8000 = 0x1FFF = 8k

                // External RAM (8k)
            case 0xA000:
            case 0xB000:
                eram[adr & 0x1FFF] = val; // Size of ERAM is 0xBFFF - 0xA000 = 0x1FFF

                // Working RAM (8k)
            case 0xC000:
            case 0xD000:
                wram[adr & 0x1FFF] = val; // Size of VRAM is 0xDFFF - 0xC000 = 0x1FFF

                // Working RAM duplicate (first half)
            case 0xE000:
                wram[adr & 0x1FFF] = val;

                // Working RAM duplicate (2nd half), I/O, Zeo-page RAM
            case 0xF000:
                switch (adr & 0x0F00) {
                    // Working RAM duplicate
                    case 0x000:
                    case 0x100:
                    case 0x200:
                    case 0x300:
                    case 0x400:
                    case 0x500:
                    case 0x600:
                    case 0x700:
                    case 0x800:
                    case 0x900:
                    case 0xA00:
                    case 0xB00:
                    case 0xC00:
                    case 0xD00:
                        wram[adr & 0x1FFF] = val;

                        // Graphics: Object attribute memory (160byte, remaining bytes are 0)
                    case 0xE00:
                        if (adr < 0xFEA0) {
                            oam[adr & 0xFF] = val;
                        }

                        // Zero-page
                    case 0xF00:
                        if (adr >= 0xFF80) {
                            zram[adr & 0x7F] = val;
                        } else {
                            // TODO: I/O handling
                        }
                }
        }
    }

    /**
     * Stores a 16bit value into sequential bytes of memory. LSB's placed in first byte. MSB's placed in second byte
     *
     * @param adr memory address
     * @param val 16bit value to store
     */
    public void setMemVal(short adr, short val) {
        byte upperByte = (byte) ((val >> 8) & 0xFF);
        byte lowerByte = (byte) (val & 0xFF);
        setMemVal(adr, lowerByte);
        setMemVal((short) (adr + 1), upperByte);
    }

    /**
     * Writes a 16bit to memory. Stores LSB in lower memory adress, MSB in higher address
     *
     * @param adr  Stack pointer
     * @param val 16 bit value
     */
    public void push(short adr, short val) {
        setMemVal((short) (adr - 1), val);
    }

    /**
     * Pop off stack. (Little endian) Read from stack, dec SP, Read from stack, dec SP
     *
     * @param adr   Stack pointer address
     * @return The popped 16 bit value.
     */
    public short pop(short adr) {
        short valLower = getMemVal(adr);
        short valUpper = getMemVal((short) (adr + 1));
        return (short) ((valUpper << 8) + valLower );
    }

    /**
     * Load a gameboy ROM into memory
     *
     * @param filename Name/Location of ROM to load into memory
     */
    public void load(String filename) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            System.out.println("Loading ROM: " + filename);

            // Read next byte from file
            int i = 0;
            int loadedByte = fis.read();
            while ((loadedByte = fis.read()) != -1) {
                rom[i++] = (byte) loadedByte;
            }
        } catch (IOException ioErr) {
            ioErr.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        System.out.println("Loaded ROM: " + filename);
    }

}
