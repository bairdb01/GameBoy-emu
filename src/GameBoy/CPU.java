package GameBoy;

/**
 * Author: Benjamin Baird
 * Created on: 2018-08-28
 * Last Updated on: 2018-08-28
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
public class CPU {
    Registers regs = new Registers();
    Flags flags = new Flags();
    Opcodes opcodes = new Opcodes();
    private int stack_pointer = regs.getSP();     // Initialized on startup, but should explicity set its value
    private int program_counter = regs.getPC();
    private Memory memory = new Memory();    // Memory stack
    int[] args = new int[3];


    /**
     * Main loop for the CPU
     */
    public void run() {
        // E.g. Execute a command with opcode 0x04
        //opcodes.execute(0x04, regs, memory, args);


        // Testing loads
        args[0] = 1;
        opcodes.execute(0x06, regs, memory, args);
        args[0] = 2;
        opcodes.execute(0x0E, regs, memory, args);
        args[0] = 3;
        opcodes.execute(0x16, regs, memory, args);
        args[0] = 4;
        opcodes.execute(0x1E, regs, memory, args);
        args[0] = 5;
        opcodes.execute(0x26, regs, memory, args);
        args[0] = 6;
        opcodes.execute(0x2E, regs, memory, args);
        args[0] = 7;
        opcodes.execute(0x3E, regs, memory, args);
        String debug = regs.toString();
        System.out.println("Pre-Exe");
        System.out.println(debug);

        opcodes.execute(0x47, regs, memory, args);
        System.out.println(regs.getB());
        opcodes.execute(0x40, regs, memory, args);
        System.out.println(regs.getB());
        opcodes.execute(0x41, regs, memory, args);
        System.out.println(regs.getB());
        opcodes.execute(0x42, regs, memory, args);
        System.out.println(regs.getB());
        opcodes.execute(0x43, regs, memory, args);
        System.out.println(regs.getB());
        opcodes.execute(0x44, regs, memory, args);
        System.out.println(regs.getB());
        opcodes.execute(0x45, regs, memory, args);
        System.out.println(regs.getB());

//        System.out.println("Executed");
//        debug = regs.toString();
//        System.out.println(debug);
    }

}
