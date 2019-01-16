package GameBoy;

/**
 * Author: Benjamin Baird
 * Created on: 2018-08-28
 * Filename: GameBoy.CPU
 * Description: An emulation of a GameBoy GameBoy.CPU
 * 8-bit GameBoy.CPU
 * 8kb RAM
 * 8kb VRAM
 * Resolution 160x144
 * Max # of Sprites: 40
 * Max # of sprites/line: 10
 * Min Sprite size: 8x8
 * Clock Speed: 4.19MHz (NOP Instruction: 4 cycles)
 * Machine Cycles: 1.05MHz (NOP Instruction: 1 cycle)
 * Horz Sync: 9198KHz
 * Vert Sync: 59.73Hz
 * Sound: 4 channels
 */

// TODO: Interrupts. Interrupts can't happen during a CB opcode.

public class CPU {

    private Registers regs = new Registers();
    private Flags flags = new Flags();
    private Opcodes opcodes = new Opcodes();
    private MMU mmu = new MMU();    // memory management unit


    public CPU(String filename) {
        mmu.load(filename);
    }
    /**
     * Main loop for the CPU
     * To execute an opcode:
     *      opcodes.execute(0x3E, regs, mmu, args);
     */
    public void run(GPU gpu) {
        boolean exit = false;
        final int maxCycles = 69905;
        while (!exit) {
            update();
            // Testing new mmu
            short adr = (short) 0xFFFF;
            byte val = 0x26;
            mmu.setMemVal(adr, val);
            System.out.println("memory[" + (adr & 0xFFFF) + "]:" + mmu.getMemVal(adr));

            exit = true;
        }

    }

    private void update() {
        final int maxCycles = 69905;
        int clock_cycles = 0;   // Number of cycles performed during this update

        while (clock_cycles < maxCycles) {
            // CPU Operates
            int cycles = runNextOpCode(regs, mmu);
            clock_cycles += cycles;

            // Timer updates
            mmu.updateTimers(cycles);

            // GPU Operates/Updates
//            gpu.step(mmu);

            // Handle Interrupts
            handleInterrupts();

//            System.out.println("Executed");
//            String debug = "CPU Clock: " + clock_cycles + "\n" + regs.toString();
//            System.out.println(debug);


        }
//        gpu.render();
    }

    private void handleInterrupts() {
        // Make sure the system is allowing interrupts
        if (Interrupts.masterInterruptSwitch) {

            for (Interrupt ir : Interrupts.interrupts) {

            }
        }
    }

    private int runNextOpCode(Registers regs, MMU mmu) {
        byte[] args = new byte[2];

        // Load an opcode
        int opcode = 0xFF & mmu.getMemVal(regs.getPC());
        regs.incPC();

        // If CB prefix in opcode, need to load instruction suffix
        if (opcode == 0xCB) {
            opcode = 0xFF & mmu.getMemVal(regs.getPC());
            regs.incPC();
            opcode = 0xCB00 + opcode;   // 0xCBnn
        }


        // Load arguments for opcode
        int numArgs = opcodes.getNumArgs(opcode);
        for (int i = 0; i < numArgs; i++) {
            args[i] = (byte) (0xFF & mmu.getMemVal(regs.getPC()));
            regs.incPC();
        }

        // Execute Instruction
        return opcodes.execute(opcode, regs, mmu, args);
    }

}
