package GameBoy.test;

import GameBoy.Commands;
import GameBoy.MMU;
import GameBoy.Registers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandsTest {
    static MMU mmu;
    static Registers regs;

    @BeforeAll
    static void init() {
        mmu = new MMU();
        regs = new Registers();
    }

    @BeforeEach
    void setUp() {
        mmu = new MMU();
        regs = new Registers();
        regs.setPC((short) 0x100);
    }

//    @Test
//    void ld_src_dest() {
//    }
//
//    @Test
//    void ld_srcPtr_dest() {
//    }
//
//    @Test
//    void ld_src_destPtr() {
//    }
//
//    @Test
//    void ld_srcPtr_destPtr() {
//    }

    @Test
    void ldhl() {
        // Test no change
        Commands.ldhl(regs, (byte) 0);
        assertEquals((short) 0xFFFE, regs.getHL());
        assertEquals(0, regs.getZFlag());
        assertEquals(0, regs.getNFlag());
        assertEquals(0, regs.getHFlag());
        assertEquals(0, regs.getCFlag());

        // Test SP - offset + h flag
        Commands.ldhl(regs, (byte) 0xFF);
        assertEquals((short) 0xFFFD, regs.getHL());
        assertEquals(1, regs.getHFlag());
        assertEquals(0, regs.getCFlag());

        // Test SP + offset
        regs.setSP((short) (0xFF00));
        Commands.ldhl(regs, (byte) 0x7F);
        assertEquals((short) 0xFF7F, regs.getHL());
        assertEquals(0, regs.getHFlag());
        assertEquals(0, regs.getCFlag());
    }

    @Test
    void addToA() {
        Commands.addToA(regs, (byte) 0);
        assertEquals(0, regs.getA());
        assertEquals(1, regs.getZFlag());
        assertEquals(0, regs.getNFlag());
        assertEquals(0, regs.getHFlag());
        assertEquals(0, regs.getCFlag());

        regs.setA((byte) 0xF0);
        Commands.addToA(regs, (byte) 0xF0);
        assertEquals((byte) 0xE0, regs.getA());
        assertEquals(0, regs.getZFlag());
        assertEquals(0, regs.getHFlag());
        assertEquals(1, regs.getCFlag());

        regs.setA((byte) 0x0F);
        Commands.addToA(regs, (byte) 0x0F);
        assertEquals((byte) 0x1E, regs.getA());
        assertEquals(0, regs.getZFlag());
        assertEquals(1, regs.getHFlag());
        assertEquals(0, regs.getCFlag());
    }

    @Test
    void sub() {
        regs.setA((byte) 0);
        Commands.sub(regs, (byte) 0);
        assertEquals(0, regs.getA());
        assertEquals(1, regs.getZFlag());
        assertEquals(1, regs.getNFlag());
        assertEquals(1, regs.getHFlag());
        assertEquals(1, regs.getCFlag());

        regs.setA((byte) 0xF0);
        Commands.sub(regs, (byte) 0x01);
        assertEquals((byte) 0xEF, regs.getA());
        assertEquals(0, regs.getZFlag());
        assertEquals(0, regs.getHFlag());
        assertEquals(1, regs.getCFlag());

        regs.setA((byte) 0xF0);
        Commands.sub(regs, (byte) 0xFF);
        assertEquals((byte) 0xF1, regs.getA());
        assertEquals(0, regs.getZFlag());
        assertEquals(0, regs.getHFlag());
        assertEquals(0, regs.getCFlag());
    }

    @Test
    void AND() {
        regs.setA((byte) 0);
        Commands.AND(regs, (byte) 0);
        assertEquals(0, regs.getA());
        assertEquals(1, regs.getZFlag());
        assertEquals(0, regs.getNFlag());
        assertEquals(1, regs.getHFlag());
        assertEquals(0, regs.getCFlag());

        regs.setA((byte) 0xFF);
        Commands.AND(regs, (byte) 0xF0);
        assertEquals((byte) 0xF0, regs.getA());
        assertEquals(0, regs.getZFlag());

        regs.setA((byte) 0xFF);
        Commands.AND(regs, (byte) 0x0F);
        assertEquals((byte) 0x0F, regs.getA());
        assertEquals(0, regs.getZFlag());
    }

    @Test
    void OR() {
        regs.setA((byte) 0);
        Commands.OR(regs, (byte) 0);
        assertEquals(0, regs.getA());
        assertEquals(1, regs.getZFlag());
        assertEquals(0, regs.getNFlag());
        assertEquals(0, regs.getHFlag());
        assertEquals(0, regs.getCFlag());

        regs.setA((byte) 0xFF);
        Commands.OR(regs, (byte) 0xFF);
        assertEquals((byte) 0xFF, regs.getA());
        assertEquals(0, regs.getZFlag());
    }

    @Test
    void XOR() {
        regs.setA((byte) 0);
        Commands.XOR(regs, (byte) 0);
        assertEquals(0, regs.getA());
        assertEquals(1, regs.getZFlag());
        assertEquals(0, regs.getNFlag());
        assertEquals(0, regs.getHFlag());
        assertEquals(0, regs.getCFlag());

        regs.setA((byte) 0xFF);
        Commands.XOR(regs, (byte) 0xFF);
        assertEquals((byte) 0x0, regs.getA());
        assertEquals(1, regs.getZFlag());

        regs.setA((byte) 0xF0);
        Commands.XOR(regs, (byte) 0x0F);
        assertEquals((byte) 0xFF, regs.getA());
        assertEquals(0, regs.getZFlag());
    }

    @Test
    void cp() {
        regs.setA((byte) 0);
        Commands.cp(regs, (byte) 0);
        assertEquals(0, regs.getA());
        assertEquals(1, regs.getZFlag());
        assertEquals(1, regs.getNFlag());
        assertEquals(1, regs.getHFlag());
        assertEquals(1, regs.getCFlag());

        regs.setA((byte) 0xF0);
        Commands.cp(regs, (byte) 0x01);
        assertEquals((byte) 0xF0, regs.getA());
        assertEquals(0, regs.getZFlag());
        assertEquals(0, regs.getHFlag());
        assertEquals(1, regs.getCFlag());

        regs.setA((byte) 0xF0);
        Commands.cp(regs, (byte) 0xFF);
        assertEquals((byte) 0xF0, regs.getA());
        assertEquals(0, regs.getZFlag());
        assertEquals(0, regs.getHFlag());
        assertEquals(0, regs.getCFlag());
    }

    @Test
    void inc() {
        // inc byte value
        byte ret = Commands.inc(regs, (byte) 0);
        assertEquals((byte) 1, ret);
        assertEquals(0, regs.getZFlag());
        assertEquals(0, regs.getNFlag());
        assertEquals(0, regs.getHFlag());

        ret = Commands.inc(regs, (byte) 0x01);
        assertEquals((byte) 2, ret);
        assertEquals(0, regs.getZFlag());
        assertEquals(0, regs.getHFlag());

        ret = Commands.inc(regs, (byte) 0xFF);
        assertEquals((byte) 0, ret);
        assertEquals(1, regs.getZFlag());
        assertEquals(1, regs.getHFlag());
    }

    @Test
    void dec() {
        byte ret = Commands.dec(regs, (byte) 0);
        assertEquals((byte) 0xFF, ret);
        assertEquals(0, regs.getZFlag());
        assertEquals(1, regs.getNFlag());
        assertEquals(1, regs.getHFlag());

        ret = Commands.dec(regs, (byte) 0x10);
        assertEquals((byte) 0xF, ret);
        assertEquals(0, regs.getZFlag());
        assertEquals(1, regs.getHFlag());


        ret = Commands.dec(regs, (byte) 0x01);
        assertEquals((byte) 0x00, ret);
        assertEquals(1, regs.getZFlag());
        assertEquals(0, regs.getHFlag());
    }

    @Test
    void add() {
        //16bit addShorts
        short ret = Commands.addShorts(regs, (short) 0, (short) 0);
        assertEquals(0, ret);
        assertEquals(0, regs.getNFlag());
        assertEquals(0, regs.getHFlag());
        assertEquals(0, regs.getCFlag());


        ret = Commands.addShorts(regs, (short) 0xFFF, (short) 0x01);
        assertEquals((short) 0x1000, ret);
        assertEquals(1, regs.getHFlag());
        assertEquals(0, regs.getCFlag());

        ret = Commands.addShorts(regs, (short) 0xFFFF, (short) 0x0001);
        assertEquals((short) 0x0, ret);
        assertEquals(1, regs.getHFlag());
        assertEquals(1, regs.getCFlag());
    }

    @Test
    void addToSP() {
        //16bit addShorts
        Commands.addToSP(regs, (short) 0, (byte) 0);
        assertEquals(0, regs.getSP());
        assertEquals(0, regs.getZFlag());
        assertEquals(0, regs.getNFlag());
        assertEquals(0, regs.getHFlag());
        assertEquals(0, regs.getCFlag());


        Commands.addToSP(regs, (short) 0xFFFE, (byte) 0xFF);
        assertEquals((short) 0xFFFD, regs.getSP());
        assertEquals(1, regs.getHFlag());
        assertEquals(1, regs.getCFlag());


        Commands.addToSP(regs, (short) 0xFFFE, (byte) 0x1);
        assertEquals((short) 0xFFFF, regs.getSP());
        assertEquals(0, regs.getHFlag());
        assertEquals(0, regs.getCFlag());

        Commands.addToSP(regs, (short) 0xFFFF, (byte) 0x1);
        assertEquals((short) 0x0, regs.getSP());
        assertEquals(1, regs.getHFlag());
        assertEquals(1, regs.getCFlag());
    }

    @Test
    void inc1() {
        // inc short value
        short ret = Commands.inc((short) 0);
        assertEquals((short) 1, ret);

        ret = Commands.inc((short) 0x01);
        assertEquals((short) 2, ret);

        ret = Commands.inc((short) 0xFFFF);
        assertEquals((short) 0, ret);
    }

    @Test
    void dec1() {
        // dec short value
        short ret = Commands.dec((short) 0);
        assertEquals((short) 0xFFFF, ret);

        ret = Commands.dec((short) 0x01);
        assertEquals((short) 0, ret);

        ret = Commands.dec((short) 0xFFFF);
        assertEquals((short) 0xFFFE, ret);
    }

    @Test
    void swap() {
        byte ret = Commands.swap(regs, (byte) 0);
        assertEquals(0, ret);
        assertEquals(1, regs.getZFlag());
        assertEquals(0, regs.getNFlag());
        assertEquals(0, regs.getHFlag());
        assertEquals(0, regs.getCFlag());


        ret = Commands.swap(regs, (byte) 0xA1);
        assertEquals((byte) 0x1A, ret);
        assertEquals(0, regs.getNFlag());

        ret = Commands.swap(regs, (byte) 0xF0);
        assertEquals((byte) 0x0F, ret);
        assertEquals(0, regs.getNFlag());
    }

    @Test
    void daa() {
    }

    @Test
    void cpl() {
    }

    @Test
    void ccf() {
    }

    @Test
    void rlc() {
    }

    @Test
    void rl() {
    }

    @Test
    void rrc() {
    }

    @Test
    void rr() {
    }

    @Test
    void sla() {
    }

    @Test
    void sra() {
    }

    @Test
    void srl() {
    }

    @Test
    void jpIf() {
    }

    @Test
    void jr() {
    }

    @Test
    void jrif() {
    }

    @Test
    void call() {
    }

    @Test
    void callIf() {
    }

    @Test
    void restart() {
    }

    @Test
    void ret() {
    }

    @Test
    void retIf() {
    }

    @Test
    void testBit() {
    }

    @Test
    void nop() {
    }

    @Test
    void halt() {
    }

    @Test
    void stop() {
    }

    @Test
    void disableInterrupts() {
    }

    @Test
    void enableInterrupts() {
    }
}