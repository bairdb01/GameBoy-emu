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

    public Registers() {
        // Register values after a real GameBoy boots through boot ROM
//        setAF((short) 0x01B0);
//        setBC((short) 0x0013);
//        setDE((short) 0x00D8);
//        setHL((short) (0x014D));
        setAF((short) 0);
        setBC((short) 0);
        setDE((short) 0);
        setHL((short) 0);
        PC = 0x0000;
    }

    /*
     * 8 Bit Getters/setters
     */

    public byte getA() {
        return this.registers[A];
    }

    public void setA(byte a) {
        this.registers[A] = a;
    }

    public byte getB() {
        return this.registers[B];
    }

    public void setB(byte b) {
        this.registers[B] = b;
    }

    public byte getC() {
        return this.registers[C];
    }

    public void setC(byte c) {
        this.registers[C] = c;
    }

    public byte getD() {
        return registers[D];
    }

    public void setD(byte d) {
        this.registers[D] = d;
    }

    public byte getE() {
        return registers[E];
    }

    public void setE(byte e) {
        this.registers[E] = e;
    }

    public byte getH() {
        return registers[H];
    }

    public void setH(byte h) {
        this.registers[H] = h;
    }

    public byte getL() {
        return registers[L];
    }

    public void setL(byte l) {
        this.registers[L] = l;
    }


    /*
     * 16-bit Getters/Setters
     */

    public void setRegPair(int upperReg, int lowerReg, short val) {
        registers[0xFF & lowerReg] = (byte) (val); // Cast lower half to a byte to remove upper bits
        registers[0xFF & upperReg] = (byte) (val >> 8); // Shift upper bits to lower half and fill upper half with 0's.
    }

    public short getRegPair(int upperReg, int lowerReg) {
        return BitUtils.mergeBytes(registers[upperReg & 0xFF], registers[lowerReg & 0xFF]);
    }

    public short getSP() {
        return SP;
    }

    public void setSP(short sp) {
        this.SP = sp;
    }

    public short getPC() {
        return PC;
    }

    public void setPC(short pc) {
        this.PC = pc;
    }

    public short getAF() {
        return getRegPair(0, 5);
    }

    public void setAF(short val) {
        setRegPair(A, F, val);
    }

    public short getBC() {
        return getRegPair(B, C);
    }

    public void setBC(short val) {
        setRegPair(B, C, val);
    }

    public short getDE() {
        return getRegPair(D, E);
    }

    public void setDE(short val) {
        setRegPair(D, E, val);
    }

    public short getHL() {
        return getRegPair(H, L);
    }

    public void setHL(short val) {
        setRegPair(H, L, val);
    }

    /*
     * Flag set/clear methods
     */

    public void setZFlag() {
        registers[F] = BitUtils.setBit(registers[F], z_pos);
    }

    public void setNFlag() {
        registers[F] = BitUtils.setBit(registers[F], n_pos);
    }

    public void setHFlag() {
        registers[F] = BitUtils.setBit(registers[F], h_pos);
    }

    public void setCFlag() {
        registers[F] = BitUtils.setBit(registers[F], c_pos);
    }

    public byte getZFlag() {
        return (byte) ((this.registers[F] >> this.z_pos) & 0x1);
    }

    public byte getNFlag() {
        return (byte) ((this.registers[F] >> this.n_pos) & 0x1);
    }

    public byte getCFlag() {
        return (byte) ((this.registers[F] >> this.c_pos) & 0x1);
    }

    public byte getHFlag() {
        return (byte) ((this.registers[F] >> this.h_pos) & 0x1);
    }

    public void clearZFlag() {
        registers[F] = BitUtils.clearBit(registers[F], z_pos);
    }

    public void clearNFlag() {
        registers[F] = BitUtils.clearBit(registers[F], n_pos);
    }

    public void clearHFlag() {
        registers[F] = BitUtils.clearBit(registers[F], h_pos);
    }

    public void clearCFlag() {
        registers[F] = BitUtils.clearBit(registers[F], c_pos);
    }

    public void incPC() {
        short oldPC = this.PC;
        this.PC += 1;
        if (oldPC > this.PC) {
            this.PC--;
        }
    }

    public String toString() {
        String s = "";
        s += "AF: " + String.format("0x%04X", getAF()) + " ";
        s += "BC: " + String.format("0x%04X", getBC()) + " ";
        s += "DE: " + String.format("0x%04X", getDE()) + " ";
        s += "HL: " + String.format("0x%04X", getHL()) + " ";

        s += "PC = " + String.format("0x%04X", getPC()) + " ";
        s += "SP = " + String.format("0x%04X", getSP()) + " ";
        return s;
    }

    public String stringify() {
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
