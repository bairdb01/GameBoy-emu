package GameBoy;

/**
 * Author: Benjamin Baird
 * Created on: 2018-08-30
 * Last Updated on: 2018-08-30
 * Filename: Memory
 * Description: Holds memory and methods required by the opcodes.
 */
public class Memory {
    int[] memory = new int[0xFFFFFF];   // Check actual size. "8bit memory"


    public int getMemVal(int adr) {
        return memory[adr];
    }

    public int setMemVal(int adr, int val) {
        memory[adr] = val;
        return memory[adr];
    }

    public void push(int SP, int val) {
        memory[SP] = val;
        SP -= 1;
    }

    public void pop(int SP, int dest) {
        dest = memory[SP];
        SP += 1;
    }

}
