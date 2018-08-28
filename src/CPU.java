/**
 * Author: Benjamin Baird
 * Created on: 2018-08-28
 * Last Updated on: 2018-08-28
 * Filename: CPU
 * Description: An emulation of a GameBoy CPU
 * 8-bit CPU
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
    private int stack_pointer = 0xFFFE;     // Initialized on startup, but should explicity set its value


    private int[] memory = new int[0xFFFF];    // Memory stack


    public CPU() {

    }


}
