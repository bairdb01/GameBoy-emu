package GameBoy;

import static GameBoy.Emulator.mmu;
import static GameBoy.Emulator.regs;

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
    private Opcodes opcodes = new Opcodes();
    int clockCycles = 0;   // Number of cycles performed during each update
    final int maxCycles = 69905;    // 1 frame per second, aim for 60fps





    /**
     * Executes the next opcode
     * @return The number of cycles the opcode took.
     */
    int runNextOpCode() {
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


        System.out.print("---\n| Opcode: " + Integer.toHexString(opcode) + " " + opcodes.getName(opcode) + " ");   // Debug print out
        for (int i : args) {
            System.out.print(Integer.toHexString(i) + " ");
        }
        System.out.println();
        System.out.println(regs.toString());
        System.out.println(mmu.toString());
        System.out.println("---");

        if ((regs.getPC() & 0xFFFF) > 0x000C) {
            System.out.println("");
        }

        // Execute Instruction
        return opcodes.execute(opcode, regs, mmu, args);
    }

}
