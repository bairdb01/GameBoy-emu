package GameBoy;

/**
 * Author: Benjamin Baird
 * Created on: 2018-08-30
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

    /**
     * Stores a 16bit value into sequential bytes of memory. LSB's placed in first byte. MSB's placed in second byte
     *
     * @param adr memory address
     * @param val 16bit value to store
     * @return upper 8 bits of val stored in memory.
     */
    public byte setMemVal(short adr, short val) {
        memory[adr] = (byte) (val & 0xFF);
        memory[adr - 1] = (byte) ((val & 0xFF) >> 8);
        return memory[adr - 2];
    }

//    /**
//     * Writes a 8bit to memory. Stores LSB in lower memory adress, MSB in higher address
//     *
//     * @param SP  Stack pointer
//     * @param val 8 bit value
//     * @return SP+1 to indicate new Stack pointer location.
//     */
//    public short push(Registers regs, short SP, byte val) {
//        memory[SP - 1] = val;
//        regs.setSP(SP);
//        return (short) (SP - 1);
//    }

    /**
     * Writes a 16bit to memory. Stores LSB in lower memory adress, MSB in higher address
     *
     * @param SP  Stack pointer
     * @param val 16 bit value
     * @return SP+1 to indicate new Stack pointer location.
     */
    public short push(Registers regs, short SP, short val) {
        memory[SP - 1] = (byte) (val >> 8);
        memory[SP - 2] = (byte) (val);
        regs.setSP((short) (SP - 2));
        return (short) (SP - 2);
    }

    /**
     * Pop off stack. (Little endian) Read from stack, dec SP, Read from stack, dec SP
     *
     * @param regs All registers
     * @param SP   Stack pointer
     * @return The popped 16 bit value.
     */
    public short pop(Registers regs, short SP) {
        short valLower = memory[SP];
        short valUpper = memory[SP + 1];
        regs.setSP((short) (SP + 2));
        return (short) (((valUpper << 8) & 0xFF00) + (valLower & 0xFF) );
    }

}
