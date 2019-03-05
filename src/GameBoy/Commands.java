package GameBoy;

import java.util.Scanner;

/**
 * Created on: 2018-12-23
 * Filename: Commands
 * Description: Operations for various opcodes. These may be broken down into ALU, Bit Operations, etc. in the future.
 *
 * Flag decriptors: - means unaffected, 0/1 clears/sets the flag, and * means affected accordingly
 * TODO: Split this file up into smaller classes
 * TODO: Test subc, adc, PUSH, POP, swap, cpl, ccf, rotate and shift operations
 */

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
    public static int ld_srcPtr_dest(short srcPtr, byte dest, MMU mmu) {
        byte value = mmu.getMemVal(srcPtr);
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
    public static int ld_src_destPtr(byte src, short destPtr, MMU mmu) {
        mmu.setMemVal(destPtr & 0xFFFF, src);
        return destPtr;
    }

    /**
     * Loads the value pointed at by srcPtr into the memory location destPtr points to.
     *
     * @param srcPtr  16 bit memory location
     * @param destPtr 16 bit memory location
     * @return
     */
    public static int ld_srcPtr_destPtr(short srcPtr, short destPtr, MMU mmu) {
        mmu.setMemVal(destPtr & 0xFFFF, mmu.getMemVal(srcPtr));
        return destPtr;
    }

    /**
     * Puts SP + offset into register HL
     *
     * @param regs   Registers containing H and L
     * @param offset One byte to offset SP
     */
    public static void ldhl(Registers regs, byte offset) {
        regs.setHL((short) (regs.getSP() + offset));
        regs.clearZFlag();
        regs.clearNFlag();

        // Set H flag if carry from bit 3
        if (((regs.getSP() & 0xF) + (offset & 0xF)) > 0xF) {
            regs.setHFlag();
        } else {
            regs.clearHFlag();
        }

        if (((regs.getSP() & 0xFFFF) + offset) > 0xFFFF) {
            regs.setCFlag();
        } else {
            regs.clearCFlag();
        }
    }

    /******************************************
     * 8 bit ALU
     ******************************************/

    /**
     * Add val to the value at a
     * Flags: Z=*, N=0, H=*,C=*
     *
     * @param regs All registers, used to access register A
     * @param val  Value to addShorts to register a
     */
    public static byte addToA(Registers regs, byte val) {
        byte aVal = regs.getA();
        byte sum = (byte) (regs.getA() + val);
        regs.clearNFlag();

        // Set if carry from bit 3
        if (((aVal & 0xF) + (val & 0xF)) > 0xF) {
            regs.setHFlag();
        } else {
            regs.clearHFlag();
        }

        // Set if carry from bit 7
        if (((aVal & 0xFF) + (val & 0xFF)) > 0xFF) {
            regs.setCFlag();
        } else {
            regs.clearCFlag();
        }

        if (sum == 0) {
            regs.setZFlag();
        } else {
            regs.clearZFlag();
        }
        regs.setA(sum);
        return sum;
    }


    /**
     * Subtract val from register A
     *
     * @param regs Registers containing register A
     * @param val  value to subtract from A
     */
    public static byte sub(Registers regs, byte val) {
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

        // H Flag set if NO borrow
        if (((a & 0xF) - (val & 0xF)) < 0) {
            regs.clearHFlag();
        } else {
            regs.setHFlag();
        }

        // C Flag set if NO carry
        if ((a & 0xFF) < (val & 0xFF)) {
            regs.clearCFlag();
        } else {
            regs.setCFlag();
        }

        regs.setA(result);
        return result;
    }

    /**
     * Logical AND A and S and store in register A
     *
     * @param regs Registers
     * @param s    8bit value
     */
    public static void AND(Registers regs, byte s) {
        regs.setA((byte) (regs.getA() & s));

        if (regs.getA() == 0) {
            regs.setZFlag();
        } else {
            regs.clearZFlag();
        }
        regs.clearNFlag();
        regs.setHFlag();
        regs.clearCFlag();
    }

    /**
     * Logical OR register A and 8 bit value
     *
     * @param regs All registers
     * @param s    8bit value
     */
    public static void OR(Registers regs, byte s) {
        regs.setA((byte) (regs.getA() | s));
        if (regs.getA() == 0) {
            regs.setZFlag();
        } else {
            regs.clearZFlag();
        }
        regs.clearNFlag();
        regs.clearHFlag();
        regs.clearCFlag();
    }

    /**
     * Logical XOR register A and s
     *
     * @param regs All registers
     * @param s    8 bit value
     */
    public static void XOR(Registers regs, byte s) {
        regs.setA((byte) (regs.getA() ^ s));
        if (regs.getA() == 0) {
            regs.setZFlag();
        } else {
            regs.clearZFlag();
        }
        regs.clearNFlag();
        regs.clearHFlag();
        regs.clearCFlag();
    }

    /**
     * Compare 8bit value with register A (Same as subtract, but results thrown away) Flags affected
     * Z=*, N=1, H=*, C=*
     *
     * @param regs All registers
     * @param s    8bit value
     */
    public static void cp(Registers regs, byte s) {
        byte a = regs.getA();
        sub(regs, s);
        regs.setA(a);
    }

    /**
     * Performs val+1 and modifies flags. Does not change val only returns the sum.
     * Z-Flag affected, N-Flag reset, H-Flag set if carry from bit 3, C-Flag not affected
     *
     * @param regs All registers
     * @param val  8bit register to increment (r, (HL))
     * @return val + 1, if val > byte.MAX_VAL then overflowed value returned.
     */
    public static byte inc(Registers regs, byte val) {
        // Half carry check
        // Truncates register value to first nibble and then adds 1 to see if there is a carry from bit 3 to 4
        if (((val & 0xf) + 0x1) >= 0x10) {
            regs.setHFlag();
        } else {
            regs.clearHFlag();
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
     * Decrement value and returns the decremented value, modifying flags. Does not store result.
     * Z-Flag set if 0, N-Flag set, H-Flag set if no borrow from bit 4, C-Flag not affected
     *
     * @param regs All registers
     * @param val  8bit register to decrement (r, (HL))
     * @return val - 1
     */
    public static byte dec(Registers regs, byte val) {
        // H flag if first 4 bits borrows from upper 4 bits
        if ((val & 0xF) < (0x01)) {
            regs.setHFlag();
        } else {
            regs.clearHFlag();
        }

        val -= 1;

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
     * Adds two short values together and returns the results setting flags
     * Flags: Z=-,N=0, H=*,C=*
     *
     * @param regs Registers containing flags
     * @param a    first number to addShorts
     * @param b    second number to addShorts
     * @return (short)(a + b)
     */
    public static short addShorts(Registers regs, short a, short b) {
        short sum = 0;
        sum = (short) (a + b);

        // Setting flags
        regs.clearNFlag();
        if ((((a & 0xFFF) + (b & 0xFFF)) & 0x1000) == 0x1000) {
            regs.setHFlag();
        } else {
            regs.clearHFlag();
        }


        if ((((a & 0xFFFF) + (b & 0xFFFF)) & 0x10000) == 0x10000) {
            regs.setCFlag();
        } else {
            regs.clearCFlag();
        }


        return sum;
    }

    /**
     * Adds offset to the stack pointer and returns the value. SP is modified.
     * Flags: Z=0, N=0, H=*, C=*
     *
     * @param regs
     * @param sp
     * @param offset
     * @return
     */
    public static short addToSP(Registers regs, short sp, byte offset) {
        short sum = 0;
        sum = (short) (sp + offset);

        // Setting flags
        regs.clearZFlag();
        regs.clearNFlag();

        if ((((sp & 0xFFF) + (offset & 0xFFF)) & 0x1000) == 0x1000) {
            regs.setHFlag();
        } else {
            regs.clearHFlag();
        }

        if ((((sp & 0xFFFF) + (offset & 0xFFFF)) & 0x10000) == 0x10000) {
            regs.setCFlag();
        } else {
            regs.clearCFlag();
        }

        regs.setSP(sum);
        return sum;
    }
    /**
     * Increments val and changes flags. Does not store results, only returns them. Flags not affected.
     *
     * @param val 16bit value
     * @return val + 1
     */
    public static short inc(short val) {
        return (short) (val + 1);
    }

    /**
     * Decrement value and returns the decremented value, modifying flags. Does not store result.
     *
     * @param val 16bit value to decrease
     * @return val - 1
     */
    public static short dec(short val) {
        return (short) (val - 1);
    }

    /**
     * Misc.
     */
    /**
     * Swaps nibbles of register
     * Z=*, N=0, H=0, C=0
     *
     * @param reg an 8bit register to swap (8bit register, (HL))
     */
    public static byte swap(Registers regs, byte reg) {
        byte upperNibble = (byte) ((reg << 4) & 0xF0);
        byte lowerNibble = (byte) ((reg >> 4) & 0x0F);
        byte result = (byte) (upperNibble + lowerNibble);
        if (result == 0) {
            regs.setZFlag();
        } else {
            regs.clearZFlag();
        }

        regs.clearNFlag();
        regs.clearHFlag();
        regs.clearCFlag();
        return result;
    }

    /**
     * Converts a number with a decimal point into binary form
     */
    // TODO DAA Function
    public static void daa() {

    }

    /**
     * Sets register A to the complement of itself
     * Flags: Z=-, N=1, H=1, C=-
     *
     * @param regs All registers
     */
    public static void cpl(Registers regs) {
        regs.setA((byte) (~regs.getA()));
        regs.setNFlag();
        regs.setHFlag();
    }

    /**
     * Flips the Carry flag bit
     *
     * @param regs Flag registers
     */
    public static void ccf(Registers regs) {
        if (regs.getCFlag() == 1) {
            regs.clearCFlag();
        } else {
            regs.setCFlag();
        }
        regs.clearNFlag();
        regs.clearHFlag();
    }


    /*
     * Rotates and  Shifts
     */
    /**
     * Rotates LEFT a byte through the carry flag and returns the new byte. The MSB is shifted into the carry flag and the LSB.
     * Flags: Z=*, N=0, H=0, C=*
     *
     * @param regs Registers containing the flags
     * @param value  the value of a byte
     * @return The shifted value of reg. The C flag is set.
     */
    public static byte rlc(Registers regs, byte value) {
        byte msb = (byte) ((value >> 7) & 0x1);
        byte shiftedByte = (byte) (value << 1);

        // Put MSB into LSB and C Flag
        shiftedByte += msb;
        if (msb == 0) {
            regs.clearCFlag();
        } else {
            regs.setCFlag();
        }

        // ZFlag updates
        if (shiftedByte == 0) {
            regs.setZFlag();
        } else {
            regs.clearZFlag();
        }

        regs.clearHFlag();
        regs.clearNFlag();
        return shiftedByte;
    }

    /**
     * Rotates LEFT and returns a byte. The MSB is shifted into the carry flag. The carry flag is shifted into the LSB.
     * Flags: Z=*, N=0, H=0, C=*
     *
     * @param regs  Registers containing the flags
     * @param value the value of a byte
     * @return The shifted value of reg. The C flag is set.
     */
    public static byte rl(Registers regs, byte value) {
        byte msb = (byte) (value >>> 7);
        byte cFlag = regs.getCFlag();
        byte shiftedByte = (byte) (value << 1);

        // Put old CFlag into LSB
        shiftedByte += cFlag;

        // Put MSB into CFlag
        if (msb == 0) {
            regs.clearCFlag();
        } else {
            regs.setCFlag();
        }

        // Z Flag updates
        if (shiftedByte == 0) {
            regs.setZFlag();
        } else {
            regs.clearZFlag();
        }

        regs.clearHFlag();
        regs.clearNFlag();
        return shiftedByte;

    }

    /**
     * Rotates RIGHT through carry. LSB is sent to MSB and C Flag.
     * Flags: Z=*, N=0, H=0, C=*
     *
     * @param regs  Registers containing the flags
     * @param value Byte value to rotate
     * @return The rotated byte with flags set
     */
    public static byte rrc(Registers regs, byte value) {
        byte lsb = (byte) (value & 0x1);
        byte shiftedByte = (byte) ((value >> 1) & 0x7F);

        // Put LSB into MSB and C Flag
        shiftedByte |= (byte) (lsb << 7);
        if (lsb == 0) {
            regs.clearCFlag();
        } else {
            regs.setCFlag();
        }

        // ZFlag updates
        if (shiftedByte == 0) {
            regs.setZFlag();
        } else {
            regs.clearZFlag();
        }

        regs.clearHFlag();
        regs.clearNFlag();
        return shiftedByte;
    }

    /**
     * Rotates right a byte. Rotates a byte placing LSB into carry flag. C Flag into MSB.
     * Flags: Z=*, N=0, H=0, C=*
     *
     * @param regs  Registers containing the flags
     * @param value A byte value to rotate
     * @return The rotated byte with flags set.
     */
    public static byte rr(Registers regs, byte value) {
        byte lsb = (byte) (value & 0x1);
        byte cFlag = regs.getCFlag();
        byte shiftedByte = (byte) ((value & 0xFF) >>> 1);

        // Put LSB into MSB and C Flag
        shiftedByte += (byte) (cFlag << 7);
        if (lsb == 0) {
            regs.clearCFlag();
        } else {
            regs.setCFlag();
        }

        // ZFlag updates
        if (shiftedByte == 0) {
            regs.setZFlag();
        } else {
            regs.clearZFlag();
        }

        regs.clearHFlag();
        regs.clearNFlag();
        return shiftedByte;
    }

    /**
     * Shift left arithmetically. Places a 0 in LSB. Places MSB into C flag.
     * Flags: Z=*, N=0, H=0, C=*
     *
     * @param regs Registers regs
     * @param value Byte value to shift
     * @return value shifted left. Flags set.
     */
    public static byte sla(Registers regs, byte value) {
        byte msb = (byte) ((value >>> 7));
        byte shiftedByte = (byte) (value << 1);

        // Shift MSB into C Flag
        if (msb == 0) {
            regs.clearCFlag();
        } else {
            regs.setCFlag();
        }

        // Setting remaining flags
        if (shiftedByte == 0) {
            regs.setZFlag();
        } else {
            regs.clearZFlag();
        }
        regs.clearHFlag();
        regs.clearNFlag();

        return shiftedByte;
    }

    /**
     * Shift right arithmetically. Shift bits right. (Signed shift) Duplicate MSB before shift and place into MSB after shift. LSB shifted into C flag.
     * Flags: Z=*, N=0, H=0, C=*
     *
     * @param regs Registers containing the flags
     * @param value Value to shift
     * @return value shifted right. Flags set.
     */
    public static byte sra(Registers regs, byte value) {
        byte lsb = (byte) (value & 0x1);
        byte shiftedByte = (byte) (value >> 1);

        // Shift LSB into C Flag
        if (lsb == 0) {
            regs.clearCFlag();
        } else {
            regs.setCFlag();
        }

        // Setting remaining flags
        if (shiftedByte == 0) {
            regs.setZFlag();
        } else {
            regs.clearZFlag();
        }
        regs.clearHFlag();
        regs.clearNFlag();

        return shiftedByte;
    }

    /**
     * Shift right logically. Shift all bits right. Place 0 into MSB. LSB into C Flag.
     * Flags: Z=*, N=0, H=0, C=*
     *
     * @param regs
     * @param value
     * @return value is shifted. Flags in regs set.
     */
    public static byte srl(Registers regs, byte value) {
        byte lsb = (byte) (value & 0x1);
        byte shiftedByte = (byte) ((value & 0xFF) >>> 1);

        // Shift LSB into C Flag
        if (lsb == 0) {
            regs.clearCFlag();
        } else {
            regs.setCFlag();
        }

        // Setting remaining flags
        if (shiftedByte == 0) {
            regs.setZFlag();
        } else {
            regs.clearZFlag();
        }
        regs.clearHFlag();
        regs.clearNFlag();

        return shiftedByte;
    }


    /*
     ******************************************************
     *  Jumps
     ******************************************************/

    /**
     * Jumps to adr if flag condition is met
     *
     * @param regs          All registers including the Flag register
     * @param adr           16bit Address to jump to
     * @param flagCondition String notation of the flag condition to meet
     */
    public static void jpIf(Registers regs, short adr, String flagCondition) {
        switch (flagCondition) {
            case ("Z"):
                if (regs.getZFlag() == 1) {
                    regs.setPC(adr);
                }
                break;
            case ("NZ"):
                if (regs.getZFlag() == 0) {
                    regs.setPC(adr);
                }
                break;
            case ("C"):
                if (regs.getCFlag() == 1) {
                    regs.setPC(adr);
                }
                break;
            case ("NC"):
                if (regs.getCFlag() == 0) {
                    regs.setPC(adr);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Adds signed offset to program counter
     *
     * @param regs   All registers
     * @param offset 8bit number to inc/dec the PC
     */
    public static void jr(Registers regs, byte offset) {
        regs.setPC((short) (regs.getPC() + offset));
    }

    /**
     * Moves the program counter by an 8bit offset if Flag condition is met
     *
     * @param regs          Registers containing the flags
     * @param offset        8bit number
     * @param flagCondition String to choose a condition to be met
     */
    public static void jrIf(Registers regs, byte offset, String flagCondition) {
        switch (flagCondition) {
            case ("Z"):
                if (regs.getZFlag() == 1) {
                    jr(regs, offset);
                }
                break;
            case ("NZ"):
                if (regs.getZFlag() == 0) {
                    jr(regs, offset);
                }
                break;
            case ("C"):
                if (regs.getCFlag() == 1) {
                    jr(regs, offset);
                }
                break;
            case ("NC"):
                if (regs.getCFlag() == 0) {
                    jr(regs, offset);
                }
                break;
        }
    }

    /*
     *****************************************************
     * Calls
     ******************************************************/

    /**
     * Push PC onto stack and then jump to address
     *
     * @param regs All registers
     * @param mmu  memory management unit
     * @param adr  address to jump to
     */
    public static void call(Registers regs, MMU mmu, int adr) {
        mmu.push(regs.getSP(), regs.getPC());
        regs.setSP((short) (regs.getSP() - 2));
        regs.setPC((short) adr);
    }

    /**
     * If condition is met the PC is pushed onto the stack and PC is then set to adr
     *
     * @param regs      Registers containing the PC
     * @param mmu       Memory management unit containing stack
     * @param adr       Address to jump to
     * @param condition Condition to check "Z"=zero flag set, "NZ"=zero flag NOT set, "C"=carry flag set, "NC'= carry flag NOT set
     */
    public static void callIf(Registers regs, MMU mmu, int adr, String condition) {
        switch (condition) {
            case "Z":
                if (regs.getZFlag() == 1) {
                    call(regs, mmu, adr);
                }
                break;
            case "NZ":
                if (regs.getZFlag() == 0) {
                    call(regs, mmu, adr);
                }
                break;
            case "C":
                if (regs.getCFlag() == 1) {
                    call(regs, mmu, adr);
                }
                break;
            case "NC":
                if (regs.getCFlag() == 0) {
                    call(regs, mmu, adr);
                }
                break;
        }
    }

    /**
     * Pushes PC to stack and sets the PC to $0000 + offset
     *
     * @param regs   Registers containing PC.
     * @param mmu    Memory management unit containing the stack.
     * @param offset PC=0x0000 + offset.
     */
    public static void restart(Registers regs, MMU mmu, byte offset) {
        mmu.push(regs.getSP(), regs.getPC());
        regs.setSP((short) (regs.getSP() - 2));
        regs.setPC((short) (offset & 0xFF));
    }

    /**
     * Pops an address off of the stack and stores in PC.
     * @param regs Registers containing PC and SP.
     * @param mmu Memory management unit containing the stack.
     */
    public static void ret(Registers regs, MMU mmu) {
        regs.setPC(mmu.pop(regs.getSP()));
        regs.setSP((short) (regs.getSP() + 2));
    }

    /**
     * If condition of cc is met, pops an address off of the stack and stores in PC.
     * @param regs Registers containing flags, SP, PC
     * @param mmu memory management unit containing the stack
     * @param cc Condition to meet. "Z", "NZ", "C", "NC". Checks if Z/C flag is set or NOT set.
     */
    public static void retIf(Registers regs, MMU mmu, String cc) {
        switch (cc) {
            case "Z":
                if (regs.getZFlag() == 1) {
                    ret(regs, mmu);
                }
                break;
            case "NZ":
                if (regs.getZFlag() == 0) {
                    ret(regs, mmu);
                }
                break;
            case "C":
                if (regs.getCFlag() == 1) {
                    ret(regs, mmu);
                }
                break;
            case "NC":
                if (regs.getCFlag() == 0) {
                    ret(regs, mmu);
                }
                break;
        }
    }

    /**
     * Tests a bit of a specified register and sets respective flags.
     * @param regs Registers
     * @param value Register id: A=0 ... L=7
     * @param bitPos Bit position to test
     */
    public static void testBit(Registers regs, byte value, byte bitPos) {
        if (BitUtils.testBit(value, bitPos)) {
            regs.clearZFlag();
        } else {
            regs.setZFlag();
        }
        regs.clearNFlag();
        regs.setHFlag();
    }

    /**
     * Does nothing. Used to skip CPU cycles.
     */
    public static void nop() {

    }

    /**
     * Stops CPU until an interrupt occurs
     * TODO HALT function
     */
    public static void halt() {

        // Two nop's for GameBoy hardware bug
        nop();
        nop();
    }

    /**
     * Halts CPU unitl a button is pressed. GB screen goes white with a single dark horizontal line.
     * TODO stop function
     */
    public static void stop() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Stopped... waiting for input.");
        scanner.nextLine();
    }

    /**
     * Disables interrupts by setting the master interrupt.
     * @param mmu Memory management unit containing interrupt register
     */
    public static void disableInterrupts(MMU mmu) {
        Interrupts.masterInterruptSwitch = false;
    }

    /**
     * Enables interrupts by setting the master interrupt.
     * @param mmu Memory management unit containing interrupt register

     */
    public static void enableInterrupts(MMU mmu) {
        Interrupts.masterInterruptSwitch = true;
    }

}
