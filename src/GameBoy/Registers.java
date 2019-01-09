package GameBoy;

/**
 * Author: Benjamin Baird
 * Created on: 2018-08-28
 * Filename: GameBoy.Registers
 * Description: GameBoy registers (excluding flags) and functions to manage them.
 */
class Registers {
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
    private short SP = (short) (0xFFFE), PC = 0;          // SP (stack pointer), PC (program counter) (16 bit) registers

    private byte z_pos = 7; // Zero
    private byte n_pos = 6; // Subtraction
    private byte h_pos = 5; // Half-carry
    private byte c_pos = 4; // Carry

    Registers() {
        setAF((short) 0);
        setBC((short) 0);
        setDE((short) 0);
        setHL((short) 0);
        setSP((short) 0);
        setPC((short) 0);
//        setAF((short) 0x01B0);
//        setBC((short) 0x0013);
//        setDE((short) 0x00D8);
//        setHL((short) (014D));
//        setSP((short) 0xFFFE);
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

    byte getF() {
        return registers[F];
    }

    void setF(byte f) {
        this.registers[F] = f;
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
        registers[lowerReg] = (byte) (val); // Cast lower half to a byte to remove upper bits
        registers[upperReg] = (byte) (val >> 8); // Shift upper bits to lower half and fill upper half with 0's.
    }

    short getRegPair(int upperReg, int lowerReg) {
        return (short) (((registers[upperReg] << 8) & 0xFF00) + (registers[lowerReg] & 0xFF));
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

    byte getFlag() {
        return getF();
    }

    byte getCFlag() {
        return (byte) ((this.registers[F] >> this.c_pos) & 0x1);
    }

    byte getZFlag() {
        return (byte) ((this.registers[F] >> this.z_pos) & 0x1);
    }
    /*
     * Flag set/clear methods
     */

    byte clearBit(byte register, byte pos) {
        register &= ~(1 << pos);
        return register;
    }

    byte setBit(byte register, byte pos) {
        register |= (1 << pos);
        return register;
    }

    void setZFlag() {
        registers[F] = setBit(registers[F], z_pos);
    }

    void clearZFlag() {
        registers[F] = clearBit(registers[F], z_pos);
    }

    void setNFlag() {
        registers[F] = setBit(registers[F], n_pos);
    }

    void clearNFlag() {
        registers[F] = clearBit(registers[F], n_pos);
    }

    void setHFlag() {
        registers[F] = setBit(registers[F], h_pos);
    }

    void clearHFlag() {
        registers[F] = clearBit(registers[F], h_pos);
    }

    void setCFlag() {
        registers[F] = setBit(registers[F], c_pos);
    }

    void clearCFlag() {
        registers[F] = clearBit(registers[F], c_pos);
    }

    void incPC() {
        this.PC++;
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

        s += "PC = " + String.format("0x%04X", getPC()) + "\n\n";
        s += "SP = " + String.format("0x%04X", getSP()) + "\n\n";
        s += "Flags (ZNHCXXXX): " + getFlag() + "\n";
        return s;
    }

}
