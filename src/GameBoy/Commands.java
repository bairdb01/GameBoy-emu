package GameBoy;

/**
 * All commands for the GB GameBoy.CPU
 */

//TODO
//

public class Commands {
    /**
     * Loads an 8 bit value from src into 8 bit register destination
     *
     * @param src  8 bit value
     * @param dest 8 bit register
     * @return dest
     */
    public static int ld_src_dest(int src, int dest) {
        dest = src;
        return dest;
    }

    /**
     * Loads the value pointed at by srcPtr into an 8 bit register
     *
     * @param srcPtr 16 bit location pointing to a value to store in dest
     * @param dest   8 bit register
     * @return dest
     */
    public static int ld_srcPtr_dest(short srcPtr, byte dest, Memory mem) {
        byte value = mem.getMemVal(srcPtr);
        dest = value;
        return dest;
    }

    /**
     * Loads an 8 bit value (src) into the adress pointed at by destPtr
     *
     * @param src     8 bit value
     * @param destPtr 16 bit memory location
     * @return destPtr
     */
    public static int ld_src_destPtr(byte src, short destPtr, Memory mem) {
        mem.setMemVal(destPtr, src);
        return destPtr;
    }

    /**
     * Loads the value pointed at by srcPtr into the memory location destPtr points to.
     *
     * @param srcPtr  16 bit memory location
     * @param destPtr 16 bit memory location
     * @return
     */
    public static int ld_srcPtr_destPtr(short srcPtr, short destPtr, Memory mem) {
        mem.setMemVal(destPtr, mem.getMemVal(srcPtr));
        return destPtr;
    }


    /******************************************
     * 8 bit ALU
     ******************************************/

    /**
     * Add val to the value at a
     *
     * @param a   Register a
     * @param val Value to add to register a
     */
    public static void addToA(int a, int val) {

    }

    /**
     * Add value to register a and set carry flag
     *
     * @param a   register a
     * @param val value to add
     */
    public static void adcToA(int a, int val) {

    }

    /**
     * Subtract val from a
     *
     * @param val value to subtract
     */
    public static byte sub(byte val, Registers regs) {
        byte a = regs.getA();
        byte result = (byte) (a - val);

        // Z Flag
        if (result == 0) {
            regs.setZFlag();
        } else {
            regs.clearZFlag();
        }

        // N Flag
        regs.setNFlag();

        // H Flag set if no borrow, clear if borrow from bit 4
        if (((a & 0xF) - (val & 0xF)) < 0) {
            regs.clearHFlag();
        } else {
            regs.setHFlag();
        }

        // C Flag
        if ((a - val) < 0) {
            regs.setCFlag();
        } else {
            regs.clearCFlag();
        }

        regs.setA(result);
        return result;
    }

    /**
     * Subtract val from a and set carry flag
     *
     * @param a   register a
     * @param val value to subtract
     */
    public static void sbcFromA(int a, int val) {

    }

    /**
     * Logical AND A and S and store in register A
     *
     * @param a register A
     * @param s 8bit value
     */
    public static void AND(int a, int s) {

    }

    /**
     * Logical OR register A and 8 bit value
     *
     * @param a register a
     * @param s 8bit value
     */
    public static void OR(int a, int s) {

    }

    /**
     * Logical XOR register A and s
     *
     * @param a register A
     * @param s 8 bit value
     */
    public static void XOR(int a, int s) {

    }

    /**
     * Compare 8bit value with register A (Same as subtract, but results thrown away)
     *
     * @param s 8bit value
     */
    public static void cp(byte s, Registers regs) {
        byte a = regs.getA();
        sub(s, regs);
        regs.setA(a);
    }

    /**
     * Increment register
     * Z-Flag affected, N-Flag reset, H-Flag set if carry from bit 3, C-Flag not affected
     * @param val 8bit register to increment (r, (HL))
     */
    public static byte inc8Bit(Registers regs, byte val) {
        // Half carry check
        // Truncates register value to first nibble and then adds 1 to see if there is a carry from bit 3 to 4
        if ((((val & 0xf) + (0x01 & 0xF)) & 0x10) == 0x10) {
            regs.setHFlag();
        }

        val += 1;

        // Z and N flags
        if (val == 0) {
            regs.setZFlag();
        } else {
            regs.clearZFlag();
        }
        regs.clearNFlag();

        return val;
    }

    /**
     * Decrement value
     * Z-Flag set if 0, N-Flag set, H-Flag set if no borrow from bit 4, C-Flag not affected
     * @param val 8bit register to increment (r, (HL))
     */
    public static byte dec8Bit(Registers regs, byte val) {
        // Truncates the first 4 bits and subtracts 1. If result is less than 0 (borrowed from bit 4) then it is a half carry.
        if (((val & 0xf) - (0x01)) < 0) {
            regs.clearHFlag();
        } else {
            regs.setHFlag();
        }

        val += 1;

        // Z and N Flags
        if (val == 0) {
            regs.setZFlag();
        } else {
            regs.clearZFlag();
        }
        regs.setNFlag();

        return val;
    }


    /***************************************
     * 16 bit Arithmetic
     ****************************************/

    /**
     * Adds val to the value stored at reg
     *
     * @param reg 16bit register (HL, SP)
     * @param val a value to add stored in a 16bit register (BC, DE,HL,SP)
     * @return the register
     */
    public static int add16(int reg, int val) {

        return reg;
    }

    /**
     * Increments the value stored at a 16bit register
     *
     * @param reg 16bit register (BC, DE, HL, SP)
     * @return reg
     */
    public static int inc16Bit(int reg) {

        return reg;
    }

    /**
     * Decrements the value stored at a 16bit register
     *
     * @param reg 16bit register (BC, DE, HL, SP)
     * @return reg
     */
    public static int dec16Bit(int reg) {

        return reg;
    }


    /**
     * Misc.
     */
    /**
     * Swaps nibbles of register
     *
     * @param reg an 8bit register to swap (8bit register, (HL))
     */
    public static byte swap(byte reg) {
        return (byte) ((reg >>> 4) | (reg << 4));
    }

    public static void daa() {

    }

    public static void cpl() {

    }

    public static void ccf() {

    }

    // Literally nothing
    public static void nop() {
        return;
    }

    public static void halt() {

    }

    public static void stop() {

    }

    public static void disableInterrupts() {

    }

    public static void enableInterrupts() {

    }


    /**
     * Rotates and  Shifts
     */
    public static void rlc(int reg, int flag) {

    }

    public static void rlcRegPair(int upperReg, int lowerReg, int flag) {

    }

    public static void rl(int reg, int flag) {

    }


    public static void rlRegPair(int upperReg, int lowerReg, int flag) {

    }

    public static void rrc(int reg, int flag) {

    }

    public static void rrcRegPair(int upperReg, int lowerReg, int flag) {

    }

    public static void rr(int reg, int flag) {

    }


    public static void rrRegPair(int upperReg, int lowerReg, int flag) {

    }

    public static void sla(int reg, int flag) {

    }

    public static void slaRegPair(int upperReg, int lowerReg, int flag) {

    }

    public static void sra(int reg, int flag) {

    }

    public static void sraRegPair(int upperReg, int lowerReg, int flag) {

    }

    public static void srl(int reg, int flag) {

    }

    public static void srlRegPair(int upperReg, int lowerReg, int flag) {

    }


    /*******************************************************
     *  Jumps
     ******************************************************/
    public static void jp(int adr) {

    }

    public static void jpIf(int flag, int adr) {

    }

    public static void jr(int adr) {

    }

    public static void jrIf(int num) {

    }


    /*******************************************************
     * Calls
     * *****************************************************/

    public static void call(int adr) {

    }

    public static void callIf(int adr) {

    }

    public static void setReg(int reg, int val) {
        reg = val;
    }

    public static void restart(int reg) {

    }
}
