package GameBoy;

import java.util.Scanner;

/**
 * Executes the GameBoy emulator
 * TODO: Test Interrupts
 * TODO: Audio
 * TODO: Remaining opcodes (DAA, etc)
 * TODO: Break up Commands.java into modular files (LD, ALU, etc).
 */
public class Emulator {
    static GPU gpu = new GPU();
    static CPU cpu = new CPU();
    static MMU mmu = new MMU();    // memory management unit
    static Registers regs = new Registers();
    static boolean inBios = false;

    static Debugger debugger = new Debugger(mmu, regs);
    static boolean debug = true;


    public static void main(String[] args) {
        String filename = "tetris.gb";
//        String filename = "C:\\Users\\Ben\\Dropbox\\GameBoy\\test-roms\\cpu_instrs\\individual\\03-op sp,hl.gb";
//        String filename = "/Users/ben/Dropbox/GameBoy/test-roms/cpu_instrs/cpu_instrs.gb";
//        String filename = "/Users/ben/Dropbox/GameBoy/test-roms/cpu_instrs/individual/01-special.gb";

        // Load ROM
        mmu.load(filename);
        regs.setPC((short) 0x0);


//        test();

//         Execute ROM
        while (inBios) {
            while ((cpu.clockCycles < cpu.maxCycles) && inBios) {
                step();
                if (regs.getPC() == (short) 0x100) {
                    inBios = false;
                }
            }
            cpu.clockCycles = 0;
        }

        while (true) {
            while ((cpu.clockCycles < cpu.maxCycles)) {
                step();
            }
            cpu.clockCycles = 0;
        }
    }

    /**
     * Performs a single instruction, updating the timers, graphics, and handling any interrupts as needed.
     */
    private static void step() {
        if (regs.getPC() == (short) 0x100) {
            debug = true;
            Scanner s = new Scanner(System.in);
            System.out.println("Found debug line");
            debugger.draw();
            s.nextLine();
        }

        if (debug && debugger.isDisplayable()) {
            debugger.draw();
        }

        int cycles = cpu.runNextOpCode();


        cpu.clockCycles += cycles;

        if (mmu.getMemVal(0xFF02) == (byte) 0x81) {
            System.out.println(mmu.getMemVal(0xFF01) & 0xFF);
        }

        // Timer updates
        mmu.updateTimers(cycles);

        // GPU Operates/Updates
        gpu.updateGraphics(cycles);

        // Handle Interrupts
        Interrupts.handleInterrupts(mmu, regs);
    }


    private static void test() {
        // Test interrupts
        Interrupts.masterInterruptSwitch = true;

        Interrupts.requestInterrupt(mmu, new Interrupt("V-Blank", "test", 0));
        Interrupts.handleInterrupts(mmu, regs);
    }
}

