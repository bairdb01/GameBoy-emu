package GameBoy;

/**
 * Executes the GameBoy emulator
 * TODO: Priority 1: Load a GB ROM. ROM -> Memory
 * TODO: Audio sounds
 * TODO: Take screen data and draw to screen
 * TODO: Implement MBC1, MBC2, MBC3, MBC5
 */
public class Main {

    public static void main(String[] args) {

        GPU gpu = new GPU();
        CPU cpu = new CPU();
        cpu.run(gpu);

    }

}
