/**
 * Author: Benjamin Baird
 * Created on: 2018-08-28
 * Last Updated on: 2018-08-28
 * Filename: Flags
 * Description: GameBoy flag register.
 *  TODO: Move this flag to registers to better represent actual hardware
 */
class Flags {
    private int flag = 0x0;    // Flag register Z=7, N=6, H=5, C=4, Other=0-3
    private int z_pos = 7;
    private int n_pos = 6;
    private int h_pos = 5;
    private int c_pos = 4;

    private int getFlag() {
        return flag;
    }

    private void setFlag(int flag_reg) {
        this.flag = flag_reg;
    }

    private void clearBit(int pos) {
        flag &= ~(1 << pos);
    }

    private void setBit(int pos) {
        flag |= (1 << pos);
    }

    private void setZ() {
        setBit(z_pos);
    }

    private void clearZ() {
        clearBit(z_pos);
    }

    private void setN() {
        setBit(n_pos);
    }

    private void clearN() {
        clearBit(n_pos);
    }

    private void setH() {
        setBit(h_pos);
    }

    private void clearH() {
        clearBit(h_pos);
    }

    private void setC() {
        setBit(c_pos);
    }

    private void clearC() {
        clearBit(c_pos);
    }

}
