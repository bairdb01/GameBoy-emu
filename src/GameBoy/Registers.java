package GameBoy;

/**
 * Author: Benjamin Baird
 * Created on: 2018-08-28
 * Last Updated on: 2018-08-28
 * Filename: GameBoy.Registers
 * Description: GameBoy registers (excluding flags) and functions to manage them.
 * TODO: Register F should be the flag register
 */
public class Registers {
    private Byte[] registers = new Byte[8]; // GameBoy.Registers A, B, C, D, E, F, H, L (8 bit)


    // AF, BC, DE, HL pairings enable 16bit registers (Note: Bitshift to combine)
    private short SP, PC;          // SP (stack pointer), PC (program counter) (16 bit) registers
    private byte flag = 0x0;    // Flag register reference to make things easier Z=7, N=6, H=5, C=4, Other=0-3

    private byte z_pos = 7; // Zero
    private byte n_pos = 6; // Subtraction
    private byte h_pos = 5; // Half-carry
    private byte c_pos = 4; // Carry

    public Registers() {
        for (int i = 0; i < 8; i++)
            registers[i] = 0x0;
    }

    public void setRegPair(int upperReg, int lowerReg, short val) {
        registers[lowerReg] = (byte) (val);
        registers[upperReg] = (byte) (val >>> 8);

        System.out.println(registers[lowerReg]);
        System.out.println(registers[upperReg]);
        System.out.println("L: " + Integer.toBinaryString(registers[lowerReg]));
        System.out.println("H: " + Integer.toBinaryString(registers[upperReg]));
    }

    public short getRegPair(byte upperReg, byte lowerReg) {
        short regPair = (short) (upperReg << 8);
        regPair += lowerReg;
        return regPair;
    }

    public byte getA() {
        return this.registers[0];
    }

    public void setA(byte a) {
        this.registers[0] = a;
    }

    public byte getB() {
        return this.registers[1];
    }

    public void setB(byte b) {
        this.registers[1] = b;
    }

    public byte getC() {
        return this.registers[2];
    }

    public void setC(byte c) {
        this.registers[2] = c;
    }

    public byte getD() {
        return registers[3];
    }

    public void setD(byte d) {
        this.registers[3] = d;
    }

    public byte getE() {
        return registers[4];
    }

    public void setE(byte e) {
        this.registers[4] = e;
    }

    public byte getF() {
        return registers[5];
    }

    public void setF(byte f) {
        this.registers[5] = f;
    }

    public byte getH() {
        return registers[6];
    }

    public void setH(byte h) {
        this.registers[6] = h;
    }

    public byte getL() {
        return registers[7];
    }

    public void setL(byte l) {
        this.registers[7] = l;
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
        return getRegPair(registers[0], registers[5]);
    }

    public void setAF(short val) {
        setRegPair(0, 5, val);
    }

    public short getBC() {
        return getRegPair(registers[1], registers[2]);
    }

    public void setBC(short val) {
        setRegPair(1, 2, val);
    }

    public short getDE() {
        return getRegPair(registers[3], registers[4]);
    }

    public void setDE(short val) {
        setRegPair(3, 4, val);
    }

    public short getHL() {
        return getRegPair(registers[6], registers[7]);
    }

    public void setHL(short val) {
        setRegPair(6, 7, val);
    }

    public byte getFlag() {
        return flag;
    }

    private void clearBit(byte register, byte pos) {
        register &= ~(1 << pos);
    }

    private void setBit(byte register, byte pos) {
        register |= (1 << pos);
    }

    public void setZFlag() {
        setBit(flag, z_pos);
    }

    public void clearZFlag() {
        clearBit(flag, z_pos);
    }

    public void setNFlag() {
        setBit(flag, n_pos);
    }

    public void clearNFlag() {
        clearBit(flag, n_pos);
    }

    public void setHFlag() {
        setBit(flag, h_pos);
    }

    public void clearHFlag() {
        clearBit(flag, h_pos);
    }

    public void setCFlag() {
        setBit(flag, c_pos);
    }

    public void clearCFlag() {
        clearBit(flag, c_pos);
    }

    public String toString() {
        String s = "";
        s += "Register A: " + getA() + "\n";
        s += "Register B: " + getB() + "\n";
        s += "Register C: " + getC() + "\n";
        s += "Register D: " + getD() + "\n";
        s += "Register E: " + getE() + "\n";
        s += "Register H: " + getH() + "\n";
        s += "Register L: " + getL() + "\n\n";

        s += "SP = " + getSP() + "\n\n";
        s += "Flags: " + Integer.toBinaryString(getFlag()) + "\n"; // probably won't work for short
        s += "       ZNHC\n\n";
        return s;


    }
}
