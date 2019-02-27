package GameBoy;

/**
 * Author: Benjamin Baird
 * Created on: 2018-08-28
 * Filename: GameBoy.Registers
 * Description: GameBoy CPU registers and functions to manage them.
 */
public class Registers {
    private Byte[] registers = new Byte[8]; // GameBoy.Registers A, B, C, D, E, F (FLAGS), H, L (8 bit)
    final private int A = 0;
    final private int B = 1;
    final private int C = 2;
    final private int D = 3;
    final private int E = 4;
    final private int F = 5;
    final private int H = 6;
    final private int L = 7;

    // AF, BC, DE, HL pairings enable 16bit registers (Note: Bitshift to combine)
    private short SP = (short) (0xFFFE), PC = 0x100;          // SP (stack pointer), PC (program counter) (16 bit) registers

    private byte z_pos = 7; // Zero
    private byte n_pos = 6; // Subtraction
    private byte h_pos = 5; // Half-carry
    private byte c_pos = 4; // Carry

    Registers() {
        // Register values after a real GameBoy boots through boot ROM
        setAF((short) 0x01B0);
        setBC((short) 0x0013);
        setDE((short) 0x00D8);
        setHL((short) (0x014D));
    }

    /*
     * 8 Bit Getters/setters
     */

    byte getA() {
        return this.registers[A];
    }

    void setA(byte a) {
        this.registers[A] = a;
    }

    byte getB() {
        return this.registers[B];
    }

    void setB(byte b) {
        this.registers[B] = b;
    }

    byte getC() {
        return this.registers[C];
    }

    void setC(byte c) {
        this.registers[C] = c;
    }

    byte getD() {
        return registers[D];
    }

    void setD(byte d) {
        this.registers[D] = d;
    }

    byte getE() {
        return registers[E];
    }

    void setE(byte e) {
        this.registers[E] = e;
    }

    byte getH() {
        return registers[H];
    }

    void setH(byte h) {
        this.registers[H] = h;
    }

    byte getL() {
        return registers[L];
    }

    void setL(byte l) {
        this.registers[L] = l;
    }


    /*
     * 16-bit Getters/Setters
     */

    void setRegPair(int upperReg, int lowerReg, short val) {
        registers[0xFF & lowerReg] = (byte) (val); // Cast lower half to a byte to remove upper bits
        registers[0xFF & upperReg] = (byte) (val >> 8); // Shift upper bits to lower half and fill upper half with 0's.
    }

    short getRegPair(int upperReg, int lowerReg) {
        return BitUtils.mergeBytes(registers[upperReg & 0xFF], registers[lowerReg & 0xFF]);
    }


    short getSP() {
        return SP;
    }

    void setSP(short sp) {
        this.SP = sp;
    }

    short getPC() {
        return PC;
    }

    void setPC(short pc) {
        this.PC = pc;
    }

    short getAF() {
        return getRegPair(0, 5);
    }

    void setAF(short val) {
        setRegPair(A, F, val);
    }

    short getBC() {
        return getRegPair(B, C);
    }

    void setBC(short val) {
        setRegPair(B, C, val);
    }

    short getDE() {
        return getRegPair(D, E);
    }

    void setDE(short val) {
        setRegPair(D, E, val);
    }

    short getHL() {
        return getRegPair(H, L);
    }

    void setHL(short val) {
        setRegPair(H, L, val);
    }


    /*
     * Flag set/clear methods
     */

    void setZFlag() {
        registers[F] = BitUtils.setBit(registers[F], z_pos);
    }

    void setNFlag() {
        registers[F] = BitUtils.setBit(registers[F], n_pos);
    }

    void setHFlag() {
        registers[F] = BitUtils.setBit(registers[F], h_pos);
    }

    void setCFlag() {
        registers[F] = BitUtils.setBit(registers[F], c_pos);
    }


    byte getZFlag() {
        return (byte) ((this.registers[F] >> this.z_pos) & 0x1);
    }

    byte getNFlag() {
        return (byte) ((this.registers[F] >> this.n_pos) & 0x1);
    }

    byte getCFlag() {
        return (byte) ((this.registers[F] >> this.c_pos) & 0x1);
    }

    byte getHFlag() {
        return (byte) ((this.registers[F] >> this.h_pos) & 0x1);
    }

    void clearZFlag() {
        registers[F] = BitUtils.clearBit(registers[F], z_pos);
    }

    void clearNFlag() {
        registers[F] = BitUtils.clearBit(registers[F], n_pos);
    }

    void clearHFlag() {
        registers[F] = BitUtils.clearBit(registers[F], h_pos);
    }

    void clearCFlag() {
        registers[F] = BitUtils.clearBit(registers[F], c_pos);
    }

    void incPC() {
        short oldPC = this.PC;
        this.PC += 1;
        if (oldPC > this.PC) {
            this.PC--;
        }
    }

    public String toString() {
        String s = "";
        s += "Register AF: " + String.format("0x%04X", getAF()) + "\n";
        s += "Register BC: " + String.format("0x%04X", getBC()) + "\n";
        s += "Register DE: " + String.format("0x%04X", getDE()) + "\n";
        s += "Register HL: " + String.format("0x%04X", getHL()) + "\n";

        s += "PC = " + String.format("0x%04X", getPC()) + "\n";
        s += "SP = " + String.format("0x%04X", getSP()) + "\n";
        s += "Z Flag: " + getZFlag() + "\n";
        s += "N Flag: " + getNFlag() + "\n";
        s += "H Flag: " + getHFlag() + "\n";
        s += "C Flag: " + getCFlag() + "\n";
        return s;
    }

}
