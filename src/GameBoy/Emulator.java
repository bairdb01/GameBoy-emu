package GameBoy;

/**
 * Executes the GameBoy emulator
 * TODO: Priority 1: Load a GB ROM. ROM -> MMU
 * TODO: Audio sounds
 * TODO: Take screen data and draw to screen
 * TODO: Colour Palettes
 * TODO: Change all bytes/shorts to ints, to stop type casting ints.
 */
public class Emulator {
    static GPU gpu = new GPU();
    static CPU cpu = new CPU();
    static MMU mmu = new MMU();    // memory management unit
    static Registers regs = new Registers();
    static Debugger debugger = new Debugger(mmu, regs);

    public static void main(String[] args) {
        String filename = "tetris.gb";
//        String filename = "C:\\Users\\Ben\\Dropbox\\GameBoy\\test-roms\\cpu_instrs\\individual\\03-op sp,hl.gb";

        // Load ROM
        mmu.load(filename);

//        test();

//         Execute ROM
        while (true) {
            while (cpu.clockCycles < cpu.maxCycles) {
                step();
            }
            cpu.clockCycles = 0;
        }
    }

    /**
     * Performs a single instruction, updating the timers, graphics, and handling any interrupts as needed.
     */
    private static void step() {
        int cycles = cpu.runNextOpCode();
        if (debugger.isDisplayable()) debugger.draw();

        cpu.clockCycles += cycles;

        // Timer updates
        mmu.updateTimers(cycles);

        // GPU Operates/Updates
        gpu.updateGraphics(cycles);

        // Handle Interrupts
        Interrupts.handleInterrupts(mmu, regs);
    }


    private static void test() {
//        // Transfers 8 into all OAM bytes
//        for (int i = 0x8000; i < 0xA000; i++)
//            mmu.push(i, (short)i);
//
//        int offset = 8;
//        int startAdr = (offset << 8);
//        for (int i = 0; i < 0xA0; i++) {
//            mmu.mem[startAdr+i] = (byte)offset;
//        }
//
//        mmu.setMemVal(0xFF46, (byte)offset); // DMA transfer @ offset << 8
//
//        mmu.mem[0xFF48] = (byte)0xE4;  // palette
//        mmu.mem[0xFF47] = (byte) 0xE4;
//
//        // Sets Tile CHR CODE 7 to be a 0 sprite
//        int i = 8;  // chr code
//        mmu.setMemVal(0x8000 + 0x10*i, (byte)0x0);
//        mmu.setMemVal(0x8001 + 0x10*i, (byte) 0x0);
//        mmu.setMemVal(0x8002 + 0x10*i, (byte) 0x3C);
//        mmu.setMemVal(0x8003 + 0x10*i, (byte) 0x3C);
//        mmu.setMemVal(0x8004 + 0x10*i, (byte) 0x066);
//        mmu.setMemVal(0x8005 + 0x10*i, (byte) 0x066);
//        mmu.setMemVal(0x8006 + 0x10*i, (byte) 0x066);
//        mmu.setMemVal(0x8007 + 0x10*i, (byte) 0x066);
//        mmu.setMemVal(0x8008 + 0x10*i, (byte) 0x066);
//        mmu.setMemVal(0x8009 + 0x10*i, (byte) 0x066);
//        mmu.setMemVal(0x800A + 0x10*i, (byte) 0x066);
//        mmu.setMemVal(0x800B + 0x10*i, (byte) 0x066);
//        mmu.setMemVal(0x800C + 0x10*i, (byte) 0x03C);
//        mmu.setMemVal(0x800D + 0x10*i, (byte) 0x03C);
//        mmu.setMemVal(0x800E + 0x10*i, (byte) 0x00);
//        mmu.setMemVal(0x800F + 0x10*i, (byte) 0x00);
//
//        // Fill BG data 0
//        for (i = 0x9800; i < 0x9C00; i++) {
//            mmu.mem[i] = (byte)0xAA;
//        }
//        // Fill BG data 1
//        for (i = 0x9C00; i < 0xA000; i++) {
//            mmu.mem[i] = (byte) 0xBB;
//        }
//
//        i = 0xAA;
//        mmu.setMemVal(0x8000 + 0x10 * i, (byte) 0x0);
//        mmu.setMemVal(0x8001 + 0x10 * i, (byte) 0x0);
//        mmu.setMemVal(0x8002 + 0x10 * i, (byte) 0x3C);
//        mmu.setMemVal(0x8003 + 0x10 * i, (byte) 0x3C);
//        mmu.setMemVal(0x8004 + 0x10 * i, (byte) 0x066);
//        mmu.setMemVal(0x8005 + 0x10 * i, (byte) 0x066);
//        mmu.setMemVal(0x8006 + 0x10 * i, (byte) 0x066);
//        mmu.setMemVal(0x8007 + 0x10 * i, (byte) 0x066);
//        mmu.setMemVal(0x8008 + 0x10 * i, (byte) 0x066);
//        mmu.setMemVal(0x8009 + 0x10 * i, (byte) 0x066);
//        mmu.setMemVal(0x800A + 0x10 * i, (byte) 0x066);
//        mmu.setMemVal(0x800B + 0x10 * i, (byte) 0x066);
//        mmu.setMemVal(0x800C + 0x10 * i, (byte) 0x03C);
//        mmu.setMemVal(0x800D + 0x10 * i, (byte) 0x03C);
//        mmu.setMemVal(0x800E + 0x10 * i, (byte) 0x00);
//        mmu.setMemVal(0x800F + 0x10 * i, (byte) 0x00);
//
//
////        mmu.incScanline();
////        mmu.incScanline();
////        mmu.incScanline();
////        mmu.incScanline();
////        mmu.incScanline();
////        mmu.incScanline();
////        mmu.incScanline();
//
//        mmu.mem[0xFF40] = (byte)0x93; // Set lcdc
//
//        debugger.draw();
//        for (i = 0; i < 144;i++ ) {
////            gpu.renderTiles(mmu.mem[0xFF40]);
////            gpu.renderSprites(mmu.mem[0xFF40]);
////            gpu.screen.renderScreen(gpu.mainScreenPixels[i], i);
//            gpu.updateGraphics(456);
//        }
    }
}
