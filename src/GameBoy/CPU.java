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
    Registers regs = new Registers();
    Flags flags = new Flags();
    Opcodes opcodes = new Opcodes();
    private Memory memory = new Memory();    // Memory stack
    byte[] args = new byte[2];
    int cpu_clock = 0;

    /**
     * Main loop for the CPU
     * To execute an opcode:
     *      opcodes.execute(0x3E, regs, memory, args);
     */
    public void run(GPU gpu) {
        boolean exit = false;
        args[0] = 1;
        while (!exit) {


            // Load an opcode and it's arguments from memory
            args[0] = 0;
            args[1] = 0;
            int opcode = 0xFF & memory.getMemVal(regs.getPC());
            regs.incPC();

            // If CB prefix, need to load instruction
            if (opcode == 0xCB) {
                opcode = 0xFF & memory.getMemVal(regs.getPC());
                regs.incPC();
                opcode = 0xCB00 + opcode;   // 0xCBnn
            }
            int numArgs = opcodes.getNumArgs(opcode);
            for (int i = 0; i < numArgs; i++) {
                args[i] = (byte) (0xFF & memory.getMemVal(regs.getPC()));
                regs.incPC();
            }

            // E.g. Execute a command with opcode 0x3E (LD A,n)
            cpu_clock += runOpCode(opcode, regs, memory, args);
            if (regs.getH() == 0) {
                System.out.println("Finished Clearing VRAM");
            }
            gpu.screen.displayText("This is the main screen");
            gpu.debug.displayText(regs.toString());

            System.out.println("Executed");
            String debug = "CPU Clock: " + cpu_clock + "\n" + regs.toString();
            System.out.println(debug);


//            exit = true;
        }

    }

    private int runOpCode(int opcode, Registers regs, Memory memory, byte[] args) {
        return opcodes.execute(opcode, regs, memory, args);
    }

}
