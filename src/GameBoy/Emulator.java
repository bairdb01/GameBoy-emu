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
//        String filename = "tetris.gb";
        String filename = "C:\\Users\\Ben\\Dropbox\\GameBoy\\test-roms\\cpu_instrs\\individual\\03-op sp,hl.gb";

        // Boot Sequence
        while (regs.getPC() != 0x100) {
            step();
        }

        // Load ROM
        mmu.load(filename);

        // Execute ROM
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
        cpu.handleInterrupts();
    }
}
