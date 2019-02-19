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

}
