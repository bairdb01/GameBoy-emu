package GameBoy;

import org.jetbrains.annotations.Contract;

/**
 * Author: Benjamin Baird
 * Created on: 2018-08-28
 * Filename: GameBoy.Registers
 * Description: GameBoy registers (excluding flags) and functions to manage them.
 * TODO: Register F should be the flag register
 */
public class Registers {
    private Byte[] registers = new Byte[8]; // GameBoy.Registers A, B, C, D, E, F, H, L (8 bit)
    final private int A = 0;
    final private int B = 1;
    final private int C = 2;
    final private int D = 3;
    final private int E = 4;
    final private int F = 5;
    final private int H = 6;
    final private int L = 7;

    // AF, BC, DE, HL pairings enable 16bit registers (Note: Bitshift to combine)
    private short SP = (short) (0xFFFE), PC = 0;          // SP (stack pointer), PC (program counter) (16 bit) registers
    private byte flag = 0x0;    // Flag register reference to make things easier Z=7, N=6, H=5, C=4, Other=0-3

    private byte z_pos = 7; // Zero
    private byte n_pos = 6; // Subtraction
    private byte h_pos = 5; // Half-carry
    private byte c_pos = 4; // Carry

    public Registers() {
        for (int i = 0; i < 8; i++)
            registers[i] = 0x0;
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

    public byte getF() {
        return registers[F];
    }

    public void setF(byte f) {
        this.registers[F] = f;
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
        registers[lowerReg] = (byte) (val); // Cast lower half to a byte to remove upper bits
        registers[upperReg] = (byte) (val >> 8); // Shift upper bits to lower half and fill upper half with 0's.
    }

    public short getRegPair(int upperReg, int lowerReg) {
        short regPair = (short) (((registers[upperReg] << 8) & 0xFF00) + (registers[lowerReg] & 0xFF));
        return regPair;
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

    public byte getFlag() {
        return flag;
    }

    public byte getCFlag() {
        return (byte) ((this.flag >> this.c_pos) & 0x1);
    }

    public byte getZFlag() {
        return (byte) ((this.flag >> this.z_pos) & 0x1);
    }
    /*
     * Flag set/clear methods
     */

    public byte clearBit(byte register, byte pos) {
        register &= ~(1 << pos);
        return register;
    }

    public byte setBit(byte register, byte pos) {
        register |= (1 << pos);
        return register;
    }

    public void setZFlag() {
        flag = setBit(flag, z_pos);
    }

    public void clearZFlag() {
        flag = clearBit(flag, z_pos);
    }

    public void setNFlag() {
        flag = setBit(flag, n_pos);
    }

    public void clearNFlag() {
        flag = clearBit(flag, n_pos);
    }

    public void setHFlag() {
        flag = setBit(flag, h_pos);
    }

    public void clearHFlag() {
        flag = clearBit(flag, h_pos);
    }

    public void setCFlag() {
        flag = setBit(flag, c_pos);
    }

    public void clearCFlag() {
        flag = clearBit(flag, c_pos);
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
        s += "Flags (ZNHCXXXX): " + getFlag() + "\n";
        return s;


    }
}
