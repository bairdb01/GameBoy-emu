package GameBoy;

/**
 * Author: Benjamin Baird
 * Created on: 2019-01-24
 * Last Updated on: 2019-01-24
 * Filename: BitUtils
 * Description: Provides useful functions for common operations to bits/bytes/shorts
 */
public class BitUtils {

    public static boolean testBit(int b, int pos) {
        return (((b >> pos) & 0b1) == 1);
    }

    static byte setBit(byte register, byte pos) {
        register |= (1 << pos);
        return register;
    }

    static byte clearBit(byte register, byte pos) {
        register &= ~(1 << pos);
        return register;
    }

    static short mergeBytes(byte upper, byte lower) {
        return (short) (((upper << 8) + (lower & 0xFF)) & 0xFFFF);
    }
}
