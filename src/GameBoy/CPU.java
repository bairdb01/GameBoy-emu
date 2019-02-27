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
// TODO: Restructure CPU/GPU/etc to an emulator class to perform updates

public class CPU {

    private Registers regs = new Registers();
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
            update(gpu);
//            exit = true;
        }

    }

    private void update(GPU gpu) {
        final int maxCycles = 69905;
        int clock_cycles = 0;   // Number of cycles performed during this update


        int bCount = 0;

        Debugger debugger = new Debugger(mmu, regs);

        while (clock_cycles < maxCycles) {
            // CPU Operates
            int cycles = runNextOpCode(regs, mmu);
            clock_cycles += cycles;

            // Blarg test print out
            if (mmu.getMemVal(0xFF02) == (byte) 0x81) {
                char letter = Character.forDigit(0xFF01, 16);
                System.out.print(mmu.getMemVal(letter));
                bCount++;

                if (bCount >= 160) {
                    bCount = 0;
                    System.out.println();
                }
            }

            // Timer updates
            mmu.updateTimers(cycles);

            // GPU Operates/Updates
//            gpu.updateGraphics(mmu, cycles, regs);



            // Handle Interrupts
            handleInterrupts();

//            System.out.println("Executed");
//            String debug = "CPU Clock: " + clock_cycles + "\n" + regs.toString();
//            System.out.println(debug);
            debugger.draw();
        }
    }

    private void handleInterrupts() {
        // Make sure the system is allowing interrupts
        if (Interrupts.masterInterruptSwitch) {
            byte irEnabled = mmu.getMemVal(0xFFFF);

            // Remove interrupt from queue
            for (Interrupt ir : Interrupts.interrupts)
                // Check if the interruptEnable register has enabled servicing for this interrupt
                if (BitUtils.testBit(irEnabled, ir.getPriority())) {
                    serviceInterrupt(ir);
                    Interrupts.interrupts.remove();
                }

        }
    }

    private void serviceInterrupt(Interrupt ir) {
        Interrupts.masterInterruptSwitch = false;   // Need to set to true once interrupts are done
        byte interruptRequest = (byte) (mmu.getMemVal(0xFF0F) | ir.getPriority());  // Clear interrupt request bit
        mmu.setMemVal(0xFF0F, interruptRequest);

        // Push PC to stack
        mmu.push(regs.getSP(), regs.getPC());
        regs.setSP((short) (regs.getSP() - 2));

        // Set program counter to interrupt handler
        regs.setPC(ir.getServiceAdr());

    }

    private int runNextOpCode(Registers regs, MMU mmu) {
        byte[] args = new byte[2];

        // Load an opcode
        int opcode = 0xFF & mmu.getMemVal(regs.getPC() & 0xFFFF);
        regs.incPC();

        // If CB prefix in opcode, need to load instruction suffix
        if (opcode == 0xCB) {
            opcode = 0xFF & mmu.getMemVal(regs.getPC() & 0xFFFF);
            regs.incPC();
            opcode = 0xCB00 + opcode;   // 0xCBnn
        }

        // Load arguments for opcode
        int numArgs = opcodes.getNumArgs(opcode);
        for (int i = 0; i < numArgs; i++) {
            args[i] = (byte) (0xFF & mmu.getMemVal(regs.getPC() & 0xFFFF));
            regs.incPC();
        }


//        System.out.print("---\n| Opcode: " + Integer.toHexString(opcode) + " " + opcodes.getName(opcode) + " ");   // Debug
//        for (int i : args) {
//            System.out.print(Integer.toHexString(i) + " ");
//        }
//        System.out.println();
//        System.out.println(regs.toString());
//        System.out.println(mmu.toString());
//        System.out.println("---");
//
//        if (opcode == 0xe0) {
//            System.out.print("");
//        }

        // Execute Instruction
        return opcodes.execute(opcode, regs, mmu, args);
    }

}
