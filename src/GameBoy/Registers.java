package GameBoy;

/**
 * Author: Benjamin Baird
 * Created on: 2018-08-28
 * Last Updated on: 2018-08-28
 * Filename: GameBoy.Registers
 * Description: GameBoy registers (excluding flags) and functions to manage them.
 * TODO Different 8bit and 16bit register functions
 */
public class Registers {
    private Byte a = 0x0, b = 0x0, c = 0x0, d = 0x0, e = 0x0, f = 0x0, h = 0x0, l = 0x0; // GameBoy.Registers A, B, C, D, E, F, H, L (8 bit)
    // AF, BC, DE, HL pairings enable 16bit registers (Note: Bitshift to combine)
    private short SP, PC;          // SP (stack pointer), PC (program counter) (16 bit) registers
    private byte flag = 0x0;    // Flag register reference to make things easier Z=7, N=6, H=5, C=4, Other=0-3

    private byte z_pos = 7;
    private byte n_pos = 6;
    private byte h_pos = 5;
    private byte c_pos = 4;


    public void setRegPair(byte upperReg, byte lowerReg, byte val) {
        // TODO Complete function
    }

    public void getRegPair(byte upperReg, byte lowerReg) {
        // TODO Complete function
    }

    public byte getA() {
        return a;
    }

    public void setA(byte a) {
        this.a = a;
    }

    public byte getB() {
        return b;
    }

    public void setB(byte b) {
        this.b = b;
    }

    public byte getC() {
        return c;
    }

    public void setC(byte c) {
        this.c = c;
    }

    public byte getD() {
        return d;
    }

    public void setD(byte d) {
        this.d = d;
    }

    public byte getE() {
        return e;
    }

    public void setE(byte e) {
        this.e = e;
    }

    public byte getF() {
        return f;
    }

    public void setF(byte f) {
        this.f = f;
    }

    public byte getH() {
        return h;
    }

    public void setH(byte h) {
        this.h = h;
    }

    public byte getL() {
        return l;
    }

    public void setL(byte l) {
        this.l = l;
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

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag_reg) {
        this.flag = flag_reg;
    }

    public void clearBit(byte register, byte pos) {
        register &= ~(1 << pos);
    }

    public void setBit(byte register, byte pos) {
        register |= (1 << pos);
    }

    public void setZ() {
        setBit(flag, z_pos);
    }

    public void clearZ() {
        clearBit(flag, z_pos);
    }

    public void setN() {
        setBit(flag, n_pos);
    }

    public void clearN() {
        clearBit(flag, n_pos);
    }

    public void setH() {
        setBit(flag, h_pos);
    }

    public void clearH() {
        clearBit(flag, h_pos);
    }

    public void setC() {
        setBit(flag, c_pos);
    }

    public void clearC() {
        clearBit(flag, c_pos);
    }


    public short getAF() {
        short AF = (short) (this.getA() << 8);
        AF += this.getC();
        return AF;
    }

    public void setAF(short val) {
        // TODO Complete function
    }

    public short getBC() {
        short BC = (short) (this.getB() << 8);
        BC += this.getC();
        return BC;
    }

    public void setBC(short val) {
        // TODO Complete function
    }

    public short getDE() {
        short DE = (short) (this.getD() << 8);
        DE += this.getE();
        return DE;
    }

    public void setDE(short val) {
        // TODO Complete function
    }

    public short getHL() {
        short HL = (short) (this.getH() << 8);
        HL += this.getL();
        return HL;
    }

    public void setHL(short val) {
        // TODO Complete function
    }

    public void setReg(byte reg, byte val) {
        reg = val;
    }


    public byte getZFlag() {
        return -1;
    }

    public byte getCFlag() {
        return -1;
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
        s += "Flags: " + Integer.toBinaryString(getFlag()) + "\n";
        s += "       ZNHC\n\n";
        return s;


    }
}
