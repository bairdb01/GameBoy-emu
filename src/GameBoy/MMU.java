package GameBoy;


import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;


/**
 * Author: Benjamin Baird
 * Created on: 2018-08-30
 * Filename: MMU
 * Description: Holds memory and methods required by the opcodes. Compatible with MBC1 and MBC2.
 * TODO: Handle read only and write only memory addresses
 * TODO: Verify ROM1 - n is being switched. (HandleBanking() testing)
 */
public class MMU {

    /* MMU is split up into the following:
     * $0000 - $7FFF stores pages from a GameBoy cartridge
     *               ($0000 - $3FFF) first 16k bank of cartridge (HOME BANK). Is always accessible here.
     *               ($0100) Stores the games HEADER
     * $8000 - $9FFF is video RAM. ($8000 - 97FF is for Character Data bank 0 + 1, split evenly) ($9800 - $9FFF is for background (BG) data)
     * $A000 - $BFFF is cartridge/external RAM, if a cartridge HAS any RAM on it. NULL and VOID if no RAM on cartridge.
     * $C000 - $DFFF is internal work RAM (WRAM) for most runtime variables. Other variables will be saved on the cartridge's RAM
     * $E000 - $FDFF specified to copy contents of $C000 - $DDFF, but DO NOT USE FOR ANYTHING.
     * $FE00 - $FE9F for object attribute memory (Sprite RAM) (40 sprites max.) Two modes: 8x8 and 8x16, these modes will apply to ALL sprites.
     * $FEA0 - $FEFF is unusable address space.
     * $FF00 - $FFFE is the ZERO page. Lower 64bytes is memory-mapped I/O. Upper 63 bytes is High RAM (HRAM).
     * $FFFF is a single memory-mapped I/O register.
     */

    private byte[] mem = new byte[0x10000];
    private byte[] ramBanks = new byte[0x8000]; // Max of 4 RAM banks, 0x2000 each

    RandomAccessFile cartridge; // Game cartridge file pointer

//    private byte[] rom = new byte[0x8000];
//    private byte[] vram = new byte[0x2000];
//    private byte[] eram = new byte[0x2000];
//    private byte[] wram = new byte[0x2000];
//
//    private byte[] zram = new byte[0x80];
//    private byte[] oam = new byte[0xA0];
//
//    // GPU specific registers
//    private byte[] hram = new byte[0x80];

    // Interrupt Register Toggle @ $FFFF
    private byte interruptEnabled = 0;

    /*
     *  RAM/ROM banking
     */
    private boolean enableERAM = false;
    private boolean usesMBC1 = false;
    private boolean usesMBC2 = false;
    private byte currentRomBank = 1;    // Which ROM bank is currently loaded
    private int currentRAMBank = 0;
    private boolean romBanking = true;

    /*
     *   Timer/Divider
     */
    // Timer located in mmeory address $FF05, increments by amount set at $FF07
    // When timer overflows (>255 byte) it request a timer interrupt and resets to value set at $FF06
    private final int timerAdr = 0xFF05;
    private final int timerModulatorAdr = 0xFF06;
    private final int timerControllerAdr = 0xFF07;
    private final int dividerAdr = 0xFF04;
    private int timerCounter = 1024;// CLOCKSPEED(4194304) / frequency(4096)
    private int dividerCounter = 0;


    public MMU() {
//        try {
//            RandomAccessFile fp = new RandomAccessFile("res/DMG_ROM.bin", "r");
//            // First 16k is always stored in memory $0000 - $3FFF after booting
//            byte b;
//            for (int i = 0; i < 0xFF; i++) {
//                b = fp.readByte();
//                mem[i] = b;
//            }
//        } catch (EOFException eof) {
//            System.err.println("End of file reached before loading home bank.");
//        } catch (IOException ioe) {
//            System.err.println("Error reading file");
//        }

        // Setting up registers post boot up sequence
        setMemVal(0xFF05, (byte) 0);
        setMemVal(0xFF06, (byte) 0);
        setMemVal(0xFF07, (byte) 0);
        setMemVal(0xFF10, (byte) 0x80);
        setMemVal(0xFF11, (byte) 0xBF);
        setMemVal(0xFF12, (byte) 0xF3);
        setMemVal(0xFF14, (byte) 0xBF);
        setMemVal(0xFF16, (byte) 0x3F);
        setMemVal(0xFF17, (byte) 0x00);
        setMemVal(0xFF19, (byte) 0xBF);
        setMemVal(0xFF1A, (byte) 0x7F);
        setMemVal(0xFF1B, (byte) 0xFF);
        setMemVal(0xFF1C, (byte) 0x9F);
        setMemVal(0xFF1E, (byte) 0xBF);
        setMemVal(0xFF20, (byte) 0xFF);
        setMemVal(0xFF21, (byte) 0x00);
        setMemVal(0xFF22, (byte) 0x00);
        setMemVal(0xFF23, (byte) 0xBF);
        setMemVal(0xFF24, (byte) 0x77);
        setMemVal(0xFF25, (byte) 0xF3);
        setMemVal(0xFF26, (byte) 0xF1);
        setMemVal(0xFF26, (byte) 0xF1);
        setMemVal(0xFF40, (byte) 0x91);
        setMemVal(0xFF41, (byte) 0x85);
        setMemVal(0xFF42, (byte) 0x00);
        setMemVal(0xFF43, (byte) 0x00);
        setMemVal(0xFF45, (byte) 0x00);
        setMemVal(0xFF47, (byte) 0xFC);
        setMemVal(0xFF48, (byte) 0xFF);
        setMemVal(0xFF49, (byte) 0xFF);
        setMemVal(0xFF4A, (byte) 0x00);
        setMemVal(0xFF4B, (byte) 0x00);
        setMemVal(0xFFFF, (byte) 0x00);
    }

    /**
     * Gets a byte from memory
     *
     * @param adr Address of byte in memory from 0x000 to 0xFFFF
     * @return A byte from the corresponding address
     */
    public byte getMemVal(int adr) {
        adr = adr & 0xFFFF;
        if ((adr >= 0xA000) && (adr < 0xC000)) {
            return ramBanks[(adr - 0xA000) + (currentRAMBank * 0x2000)];
        } else {
            if (adr == (short) 0xFFFF) {
                return interruptEnabled;
            } else {
                return mem[adr];
            }
        }
        // Split up to handle the varying types memory blocks
//        switch (adr & 0xF000) {
//
//            // BIOS(256b)/ROM0
//            case 0x0000:
//                return rom[adr];
//
//            // ROM0 (Unbanked)(16k)
//            case 0x1000:
//            case 0x2000:
//            case 0x3000:
//                return rom[adr];
//
//            // ROM1 (Unbanked)(16k)
//            case 0x4000:
//            case 0x5000:
//            case 0x6000:
//            case 0x7000:
//                return rom[adr];
//
//            // Graphics (VRAM)(8k)
//            case 0x8000:
//            case 0x9000:
//                return vram[adr & 0x1FFF]; // Size of VRAM is 0x9FFF - 0x8000 = 0x1FFF = 8k
//
//            // External RAM (8k)
//            case 0xA000:
//            case 0xB000:
//                // Only useable if mode is selected
//                if (enableERAM) {
//                    return eram[adr & 0x1FFF]; // Size of ERAM is 0xBFFF - 0xA000 = 0x1FFF
//                }
//
//            // Working RAM (8k)
//            case 0xC000:
//            case 0xD000:
//                return wram[adr & 0x1FFF]; // Size of VRAM is 0xDFFF - 0xC000 = 0x1FFF
//
//            // Working RAM duplicate (first half)
//            case 0xE000:
//                return wram[adr & 0x1FFF];
//
//            // Working RAM duplicate (2nd half), I/O, Zeo-page RAM
//            case 0xF000:
//                switch (adr & 0x0F00) {
//                    // Working RAM duplicate
//                    case 0x000:
//                    case 0x100:
//                    case 0x200:
//                    case 0x300:
//                    case 0x400:
//                    case 0x500:
//                    case 0x600:
//                    case 0x700:
//                    case 0x800:
//                    case 0x900:
//                    case 0xA00:
//                    case 0xB00:
//                    case 0xC00:
//                    case 0xD00:
//                        return wram[adr & 0x1FFF];
//
//                    // Graphics: Object attribute memory (160byte, remaining bytes are 0)
//                    case 0xE00:
//                        if (adr < (short) 0xFEA0) {
//                            return oam[adr & 0xFF];
//                        } else {
//                            return 0;
//                        }
//
//                        // Zero-page
//                    case 0xF00:
//                        if (adr < (short) 0xFF80) {
//                            return zram[adr & 0x7F];
//                        } else {
//                            if (adr == (short) 0xFFFF) {
//                                return interruptEnabled;
//                            }
//                            // TODO: I/O handling
//                            // TODO GPU memory
//                            // I/O, GPU
//                            return hram[adr & 0x80];
//                        }
//                }
//        }
//        return 0;
    }

    /**
     * Stores an 8bit value into memory.
     *
     * @param adr memory address
     * @param val 8bit value to store
     */
    public void setMemVal(int adr, byte val) {
        if (adr < 0x8000) {
            handleBanking(adr, val);
        } else if ((adr >= 0xA000 && adr < 0xC000)) {
            if (enableERAM) {
                ramBanks[adr - 0xA000 + (currentRAMBank * 0x2000)] = val;
            }
        } else {
            if (adr == 0xFF07) {
                // Timer Controller
                if (val != getClockFreq()) {
                    mem[adr] = val;
                    setClockFreq();
                }
            } else if (adr == 0xFF46) {
                // Direct Memory Access (DMA)
                DMATransfer(val);
            } else if (adr == 0xFFFF) {
                interruptEnabled = val;
            } else {
                mem[adr] = val;
            }
        }
        // Handles writing data to memory addresses
//        switch (adr & 0xF000) {
//            // BIOS(256b)/ROM0
//            case 0x0000:
//                rom[adr] = val;
//                break;
//
//            // ROM0 (Unbanked)(16k)
//            case 0x1000:
//            case 0x2000:
//            case 0x3000:
//                rom[adr] = val;
//                break;
//
//                // ROM1 (Unbanked)(16k)
//            case 0x4000:
//            case 0x5000:
//                if (usesMBC2) {
//                    return;
//                }
//            case 0x6000:
//            case 0x7000:
//                rom[adr] = val;
//                break;
//
//                // Graphics (VRAM)(8k)
//            case 0x8000:
//            case 0x9000:
//                vram[adr & 0x1FFF] = val; // Size of VRAM is 0x9FFF - 0x8000 = 0x1FFF = 8k
//                break;
//
//                // External RAM (8k)
//            case 0xA000:
//            case 0xB000:
//                // Only useable if mode is selected
//                if (enableERAM) {
//                    eram[adr & 0x1FFF] = val; // Size of ERAM is 0xBFFF - 0xA000 = 0x1FFF
//                }
//                break;
//                // Working RAM (8k)
//            case 0xC000:
//            case 0xD000:
//                wram[adr & 0x1FFF] = val; // Size of VRAM is 0xDFFF - 0xC000 = 0x1FFF
//                break;
//
//                // Working RAM duplicate (first half)
//            case 0xE000:
//                wram[adr & 0x1FFF] = val;
//                break;
//
//                // Working RAM duplicate (2nd half), I/O, Zeo-page RAM
//            case 0xF000:
//                switch (adr & 0x0F00) {
//                    // Working RAM duplicate
//                    case 0x000:
//                    case 0x100:
//                    case 0x200:
//                    case 0x300:
//                    case 0x400:
//                    case 0x500:
//                    case 0x600:
//                    case 0x700:
//                    case 0x800:
//                    case 0x900:
//                    case 0xA00:
//                    case 0xB00:
//                    case 0xC00:
//                    case 0xD00:
//                        wram[adr & 0x1FFF] = val;
//                        break;
//
//                        // Graphics: Object attribute memory (160byte, remaining bytes are 0)
//                    case 0xE00:
//                        if (adr < (short) 0xFEA0) {
//                            oam[adr & 0xFF] = val;
//                        }
//                        break;
//
//                        // Zero-page
//                    case 0xF00:
//                        if (adr < (short) 0xFF80) {
//                            zram[adr & 0x7F] = val;
//                        } else {
//
//                            if (adr == (short) 0xFF07) {
//                                // Timer Controller
//                                if (val != getClockFreq()) {
//                                    zram[0xFF07 & 0x7F] = val;
//                                    setClockFreq();
//                                }
//                            } else if (adr == (short) (0xFF04)) {
//                                // Attempting to write to the divider register resets it to 0
//                                zram[timerAdr & 0x7F] = 0;
//                            } else if (adr == (short) 0xFF44) {
//                                // Attempting to write to scanline register, resets it
//                                zram[0xFF44 & 0x7F] = 0;
//                            } else if (adr == (short) 0xFF46) {
//                                // Direct Memory Access (DMA)
//                                DMATransfer(val);
//
//                            } else if (adr == (short) 0xFFFF) {
//                                interruptEnabled = val;
//                            }
//                            // TODO: I/O handling
//                            // I/O, GPU
//                            int b = adr & 0x80;
//                            hram[adr & 0x80] = val;
//
//                        }
//                        break;
//                }
//                break;
//        }
    }

    /**
     * Stores a 16bit value into sequential bytes of memory. LSB's placed in first byte. MSB's placed in second byte
     *
     * @param adr memory address
     * @param val 16bit value to store
     */
    public void setMemVal(int adr, short val) {
        byte upperByte = (byte) ((val & 0xFF00) >> 8);
        byte lowerByte = (byte) (val & 0xFF);
        setMemVal(adr & 0xFFFF, upperByte);
        setMemVal((adr - 1) & 0xFFFF, lowerByte);
    }

    /**
     * Writes a 16bit to memory. Stores LSB in lower memory adress, MSB in higher address
     *
     * @param adr  Stack pointer
     * @param val 16 bit value
     */
    public void push(int adr, short val) {
        adr &= 0xFFFF;
        setMemVal(adr-1, val);
    }

    /**
     * Pop off stack. (Little endian) Read from stack, dec SP, Read from stack, dec SP
     *
     * @param adr   Stack pointer address
     * @return The popped 16 bit value.
     */
    public short pop(int adr) {
        byte valLower = getMemVal(adr & 0xFFFF);
        byte valUpper = getMemVal((adr + 1) & 0xFFFF);
        return BitUtils.mergeBytes(valUpper, valLower);
    }

    /**
     * Increment the scanline register
     */
    public void incScanline() {
        setMemVal(0xFF44, (byte) (getMemVal(0xFF44) + 1));
//        zram[0xFF44]++;
    }

    /**
     * Load a GameBoy Application into memory
     *
     * @param filename Relative path of ROM to load into memory
     */
    public void load(String filename) {
        try {
            cartridge = new RandomAccessFile(filename, "r");
            System.out.println("Loading ROM: " + filename);

            // Load homebank
            loadHomeRom(cartridge);

            // Load bank1 to store at 0x4000 - 0x7FFF
            loadBank(cartridge, currentRomBank);

            // State the type of MBC used
            switch (getMemVal(0x147)) {
                case 1:
                case 2:
                case 3:
                    usesMBC1 = true;
                    break;
                case 5:
                case 6:
                    usesMBC2 = true;
                    break;
                default:
                    break;
            }
        } catch (IOException ioErr) {
            ioErr.printStackTrace();
        } finally {
            try {
                if (cartridge != null) {
                    cartridge.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        System.out.println("Loaded ROM: " + filename);
    }

    /**
     * Loads the home rom bank (rom0) into 0x0000 - 0x3FFF
     *
     * @param fp A file pointer to the game cartridge.
     */
    void loadHomeRom(RandomAccessFile fp) {
        // First 16k is always stored in memory $0000 - $3FFF after booting
        byte b;
        try {
            fp.seek(0x100);
            for (int i = 0x100; i < 0x4000; i++) {
                b = fp.readByte();
                mem[i] = b;
            }
        } catch (EOFException eof) {
            System.err.println("End of file reached before loading home bank.");
        } catch (IOException ioe) {
            System.err.println("Error reading file");
        }
    }

    /**
     * Loads a new rom bank into paged memory 0x4000 - 0x7FFF.
     *
     * @param fp   The file pointer to the game cartridge.
     * @param bank The rom bank to load. Bank=0 will not be loaded.
     */
    private void loadBank(RandomAccessFile fp, int bank) {
        if (bank == 0) {
            return;
        }
        byte b;
        try {
            fp.seek(0x4000 * bank);
            for (int i = 0x4000 * bank; i < 0x4000 * (bank + 1); i++) {
                b = fp.readByte();
                mem[i] = b;
            }
        } catch (EOFException eof) {
            System.err.println("End of file reached before loading home bank.");
        } catch (IOException ioe) {
            System.err.println("Error reading file");
        }
    }
    /**
     * Handles requests to change ROM/RAM banks.
     *
     * @param adr Requested address to write to.
     * @param val Value to write to address.
     */
    public void handleBanking(int adr, byte val) {
        // Handles RAM/ROM bank selections
        if (adr >= 0 && adr < 0x2000) {
            if (usesMBC2 || usesMBC1) {
                enableERAMCheck(adr, val);
            }
        } else if (adr >= 0x2000 && adr < 0x4000) {
            // ROM Bank change
            if (usesMBC2 || usesMBC1) {
                // LowRom Bank
                romBankChange(adr, val);
            }
        } else if (adr >= 0x4000 && adr < 0x6000) {
            // No RAM bank in mbc2. Always use ram bank 0
            if (usesMBC1) {
                if (romBanking) {
                    // HiRom Bank
                    romBankChange(adr, val);
                } else {
                    ramBankChange(val);
                }
            }
        } else if (adr >= 0x6000 && adr < 0x8000) {
            romRamModeSwitch(val);
        }
    }

    /**
     * When 0xA is written to to 0xA000 - 0xBFFF, the ERAM can be enabled.
     * If MBC2 is enabled bit 4 must be 0 to enable
     *
     * @param adr Address of memory being written to
     * @param val Value of byte being written to
     */
    private void enableERAMCheck(int adr, byte val) {
        if (usesMBC2) {
            // When using mbc2, bit 4 of address must be 0 to enable
            if (BitUtils.testBit(adr, 4)) return;
        }

        // Data must be 0xA to enable ERAM
        switch (val & 0xF) {
            case 0xA:
                enableERAM = true;
                break;
            case 0x0:
                enableERAM = false;
        }
    }

    /**
     * Handles ROM bank changing for when the game writes data to address 0x2000 - 0x3FFF and 0x4000 - 0x5FFF
     *
     * @param val Byte value of data to be written to an address
     */
    private void romBankChange(int adr, byte val) {
        if (adr >= 0x2000 && adr < 0x4000) {
            // ROM bank changing
            if (usesMBC2) {
                currentRomBank = (byte) (val & 0xF);
                if (currentRomBank == 0) {
                    // Bank 0 never changes, trying to change it results in changing bank 1
                    currentRomBank++;
                }
                loadBank(cartridge, currentRomBank);
            } else if (usesMBC1) {
                // mbc1 means the lower 5bits of current rom bank is set to lower 5 bits of val
                byte lowerFiveBits = (byte) (val & 0x1F);
                currentRomBank = (byte) ((currentRomBank & 0xE0) + lowerFiveBits);
                if (currentRomBank == 0) {
                    currentRomBank++;
                }
                loadBank(cartridge, currentRomBank);
            }
        } else if (adr >= 0x4000 && adr < 0x6000) {
            // ROM/RAM bank change
            if (usesMBC1 || usesMBC2) {
                // Turn off lower 5 bits of data and combine with upper 3 bits of current ROM bank
                currentRomBank &= 0xE0;
                currentRomBank |= (val & 0xE0);
                if (currentRomBank == 0) {
                    currentRomBank++;
                }
                loadBank(cartridge, currentRomBank);
            }
        }
    }

    /**
     * Changes the RAM bank. Only mbc1 can change RAM banks. The game must write to address 0x4000 - 0x5FFF, romBanking must be false.
     *
     * @param val A byte value of data to be written to address 0x4000 - 0x5FFF
     */
    private void ramBankChange(byte val) {
        // currentRAMBank gets set to the lower 2 bits of val
        if (!usesMBC2 && usesMBC1) {
            currentRAMBank = val & 0x3;
        }
    }

    /**
     * Changes between ROM/RAM modes. If LSB of val == 0 then rom banking is enabled.
     *
     * @param val A byte value written to an address in memory
     */
    private void romRamModeSwitch(byte val) {
        romBanking = !BitUtils.testBit(val, 0);
        if (romBanking) {
            currentRAMBank = 0;
        }
    }

    /**
     * Update the timer and divider registers
     * @param cycles
     */
    void updateTimers(int cycles) {
        // Update the divider register
        dividerCounter += cycles;
        if (dividerCounter >= 255) {
            dividerCounter = 0;
            incDividerRegister();
        }

        if (isClockEnabled()) {
            timerCounter -= cycles;

            // Enough CPU cycles passed to update the timer
            if (timerCounter <= 0) {
                // reset timerCounter
                setClockFreq();


                // Updating timer
                if (getMemVal(timerAdr) == (byte) 0xFF) {
                    // Timer overflow handling
                    setMemVal(timerAdr, getMemVal(timerModulatorAdr));
                    Interrupts.requestInterrupt(this, new Interrupt("Timer Overflow", "MMU - updateTimers()", 2));
                } else {
                    setMemVal(timerAdr, (byte) (getMemVal(timerAdr) + 1));
                }
            }
        }
    }

    /**
     * Checks if the clock is enabled
     * @return true if clock is enabled
     */
    private boolean isClockEnabled() {
        return BitUtils.testBit(getMemVal(timerControllerAdr), 2);
    }

    /**
     * Fetches the clock speed from register 0xFF07 and returns the last 3 bits
     * @return a byte representing the number of CPU cycles performed before the timer is incremented
     */
    private byte getClockFreq() {
        return (byte) (mem[timerControllerAdr] & 0x3);
//        return (byte) (zram[timerControllerAdr] & 0x3);
    }

    /**
     * Changes the frequency of timer updates, to be the value stored in the timer modulator register
     */
    private void setClockFreq() {
        switch (getClockFreq() & 0x3) {
            case 0:
                timerCounter = (byte) 1024; // 4096hz
                break;
            case 1:
                timerCounter = (byte) 16; // 262144hz
                break;
            case 2:
                timerCounter = (byte) 64; // 65536hz
                break;
            case 3:
                timerCounter = (byte) 256; // 16382hz
                break;
        }
    }

    /**
     * Increments the divider register, so it doesn't get reset
     */
    private void incDividerRegister() {
        mem[dividerAdr]++;
//        zram[dividerAdr]++;
    }

    /**
     * Transfer 0xA0 worth of memory from RAM/ROM ($0000-$F100) to OAM memory starting at 0xFE00.
     *
     * @param offset The offset of the location to begin transferring data
     *               from. Starting memory address = offset << 8, since all start address are 0x0000, 0x0100, .., 0xFF00
     */
    private void DMATransfer(byte offset) {
        int startAdr = (offset << 8);
        for (int i = 0; i < 0xA0; i++) {
            setMemVal((0xFE00 + i), getMemVal((startAdr + i)));
        }
    }


    /**
     * Prints off important registers for debugging
     *
     * @return String containing values of important registers
     */
    public String toString() {
        return "LCDC:" + String.format("0x%02X", this.mem[0xFF40]) + " " +
                "stat:" + String.format("0x%02X", this.mem[0xFF41]) + " " +
                "ly:" + String.format("0x%02X", this.mem[0xFF44]) + " ";
    }

    /**
     * Stringifies the memory.
     * @return A string representation of the memory.
     */
    String stringify() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 0x10000; i++) {
            if (i < 0x4000) {
                sb.append("(ROM0) ");
            } else if (i < 0x8000) {
                sb.append("(ROM1)");
            } else if (i < 0xA000) {
                sb.append("(VRAM) ");
            } else if (i < 0xC000) {
                sb.append("(RAM) ");
            } else if (i < 0xE000) {
                sb.append("(WRAM) ");
            } else if (i < 0xFE00) {
                sb.append("(DUPE) ");
            } else if (i < 0xFEA0) {
                sb.append("(OAM) ");
            } else if (i < 0xFF00) {
                sb.append("(N/A) ");
            } else if (i < 0xFF80) {
                sb.append("(I/O) ");
            } else if (i < 0xFFFF) {
                sb.append("(HRAM) ");
            }
            sb.append(String.format("0x%04X: ", i));
            sb.append(String.format("0x%02X\n", getMemVal(i)));
        }
        return sb.toString();
    }
}