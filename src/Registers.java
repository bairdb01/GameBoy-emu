/**
 * Author: Benjamin Baird
 * Created on: 2018-08-28
 * Last Updated on: 2018-08-28
 * Filename: Registers
 * Description: GameBoy registers (excluding flags) and functions to manage them.
 * TODO Switch from int to byte
 * TODO Different 8bit and 16bit register functions
 */
public class Registers {
    private int a = 0x0, b = 0x0, c = 0x0, d = 0x0, e = 0x0, f = 0x0, h = 0x0, l = 0x0; // Registers A, B, C, D, E, F, H, L (8 bit)
    // AF, BC, DE, HL pairings enable 16bit registers (Note: Bitshift to combine)
    private int SP, PC;          // SP (stack pointer), PC (program counter) (16 bit) registers
    private int flag = f;    // Flag register reference to make things easier Z=7, N=6, H=5, C=4, Other=0-3

    private int z_pos = 7;
    private int n_pos = 6;
    private int h_pos = 5;
    private int c_pos = 4;

    public int getA() {
        return a;
    }

    public void setRegPair(int upperReg, int lowerReg, int val) {
        // TODO Complete function
    }

    public void getRegPair(int upperReg, int lowerReg) {
        // TODO Complete function
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    public int getE() {
        return e;
    }

    public void setE(int e) {
        this.e = e;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public int getSP() {
        return SP;
    }

    public void setSP(int sp) {
        this.SP = sp;
    }

    public int getPC() {
        return PC;
    }

    public void setPC(int pc) {
        this.PC = pc;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag_reg) {
        this.flag = flag_reg;
    }

    public void clearBit(int register, int pos) {
        register &= ~(1 << pos);
    }

    public void setBit(int register, int pos) {
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


    public int getAF() {
        int AF = (this.getA() << 8);
        AF += this.getC();
        return AF;
    }

    public void setAF(int val) {
        // TODO Complete function
    }

    public int getBC() {
        int BC = (this.getB() << 8);
        BC += this.getC();
        return BC;
    }

    public void setBC(int val) {
        // TODO Complete function
    }

    public int getDE() {
        int DE = (this.getD() << 8);
        DE += this.getE();
        return DE;
    }

    public void setDE(int val) {
        // TODO Complete function
    }

    public int getHL() {
        int HL = (this.getH() << 8);
        HL += this.getL();
        return HL;
    }

    public void setHL(int val) {
        // TODO Complete function
    }

    // Decrements register by 1
    public void decReg(int reg) {
//        int HL = getHL();
//        HL -= 1;
        // Copy into L
//        for (int i = 0; i < 8; i++) {
//            setBit(l,   ( (HL >> 1 ) & 0xFF)  );
//        }
//        // Copy into H
//        for (int i = 0; i < 8; i++) {
//            setBit(l,   ( (HL >> 1 ) & 0xFF)  );
//        }
    }

    public void decRegPair(int upperReg, int lowerReg) {
        // TODO COMPLETE REG
    }

    public void incRegPair(int upperReg, int lowerReg) {
        // TODO Complete REG
    }

    // Increment register by 1
    public void incReg(int reg) {
        // TODO COMPLETE REG
    }

    public void addToRegPair(int upperReg, int lowerReg, int value) {
        // TODO COMPLETE REG
    }

    public void addToReg(int reg, int val) {
        // TODO Complete function
    }

    public void subFromReg(int reg, int val) {
        // TODO Complete function
    }

    public void subAndCarry(int reg, int val) {
        // TODO Complete function
    }

    public void AND(int reg, int val) {
        reg = reg & val;
        // TODO Flag is affected
    }

    public void OR(int reg, int val) {
        reg = reg | val;
        // TODO FLAG is affected
    }

    public void XOR(int reg, int val) {
        reg = reg ^ val;
        // TODO FLAG is affected
    }

    public void cp(int reg, int val) {
        int prev = Integer.valueOf(reg);    // Probably a much easier way to save the old value of the register. Need to double check "int prev = reg"
        subFromReg(reg, val);
        reg = prev;
    }

    // Swaps upper and lower nibbles. Need to see specs.
    public void swap(int reg) {

    }

    public void daa() {

    }

    public void cpl() {

    }

    public void ccf() {

    }

    // Literally nothing
    public void nop() {

    }

    public void halt() {

    }

    public void stop() {

    }

    public void disableInterrupts() {

    }

    public void enableInterrupts() {

    }


    /**
     * Rotates and  Shifts
     */
    public void rlc(int reg, int flag) {

    }

    public void rlcRegPair(int upperReg, int lowerReg, int flag) {

    }

    public void rl(int reg, int flag) {

    }


    public void rlRegPair(int upperReg, int lowerReg, int flag) {

    }

    public void rrc(int reg, int flag) {

    }

    public void rrcRegPair(int upperReg, int lowerReg, int flag) {

    }

    public void rr(int reg, int flag) {

    }


    public void rrRegPair(int upperReg, int lowerReg, int flag) {

    }

    public void sla(int reg, int flag) {

    }

    public void slaRegPair(int upperReg, int lowerReg, int flag) {

    }

    public void sra(int reg, int flag) {

    }

    public void sraRegPair(int upperReg, int lowerReg, int flag) {

    }

    public void srl(int reg, int flag) {

    }

    public void srlRegPair(int upperReg, int lowerReg, int flag) {

    }

    public void testBit(int reg, int b, int flag) {


    }

    public void jp(int adr) {

    }

    public void jpIf(int flag, int adr) {

    }

    public void jr(int adr) {

    }

    public void jrIf(int num) {

    }

    public void call(int adr) {

    }

    public void callIf(int adr) {

    }

    public void setReg(int reg, int val) {
        reg = val;
    }

    public void restart(int reg) {

    }

    public void popJmp() {

    }

    public void popJmpIf(int bitFlag) {

    }

    public void reti() {
        popJmp();
        enableInterrupts();
    }

    public int getZFlag() {
        return -1;
    }

    public int getCFlag() {
        return -1;
    }
}
