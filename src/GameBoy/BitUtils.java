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

    static byte setBit(byte val, int pos) {
        val = (byte) (val | (0x01 << pos));
        return val;
    }

    static byte clearBit(byte val, int pos) {
        val = (byte) (val & ~(1 << pos));
        return val;
    }

    static short mergeBytes(byte upper, byte lower) {
        return (short) ((upper << 8) + (lower & 0xFF));
    }

    static byte reverseByte(byte b) {

        byte shiftedByte = b;

        // Shift
        for (int i = 0; i < 7; i++) {
            // Shift left 1 byte
            byte msb = (byte) ((b >> 7) & 0x1);
            shiftedByte = (byte) (b << 1);

            // Put MSB into LSB
            shiftedByte += msb;
        }

        return shiftedByte;
    }

    static byte getBit(byte b, int pos) {
        return (byte) (b >> pos);
    }
}
