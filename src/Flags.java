/**
 * Author: Benjamin Baird
 * Created on: 2018-08-28
 * Last Updated on: 2018-08-28
 * Filename: Flags
 * Description: GameBoy flag register.
 */
public class Flags {
    private int flag = 0x0;    // Flag register Z=7, N=6, H=5, C=4, Other=0-3
    private int z_pos = 7;
    private int n_pos = 6;
    private int h_pos = 5;
    private int c_pos = 4;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag_reg) {
        this.flag = flag_reg;
    }

    public void setZ() {
        flag |= (1 << z_pos);
    }

    public void clearZ() {
        flag &= ~(1 << z_pos);
    }
}
