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


    public int getValue(int adr) {
        return memory[adr];
    }

    public int setValue(int adr, int val) {

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
