package GameBoy;

/**
 * Author: Benjamin Baird
 * Created on: 2018-08-30
 * Last Updated on: 2018-12-25
 * Filename: Memory
 * Description: Holds memory and methods required by the opcodes.
 */
public class Memory {
    byte[] memory = new byte[0xFFFF];   // Check actual size. "8bit memory"


    public byte getMemVal(short adr) {
        return memory[adr];
    }

    public byte setMemVal(short adr, byte val) {
        memory[adr] = val;
        return memory[adr];
    }

    public void push(short SP, byte val) {
        memory[SP] = val;
        SP -= 1;
    }

    public void pop(short SP, short dest) {
        dest = memory[SP];
        SP += 1;
    }

}
