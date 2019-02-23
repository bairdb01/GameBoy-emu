package GameBoy;

/**
 * Executes the GameBoy emulator
 * TODO: Priority 1: Load a GB ROM. ROM -> MMU
 * TODO: Audio sounds
 * TODO: Take screen data and draw to screen
 * TODO: Colour Palettes
 * TODO: Change all bytes/shorts to ints, to stop type casting ints.
 */
public class Main {

    public static void main(String[] args) {
        String filename = "Tetris.GB";
        GPU gpu = new GPU();
        CPU cpu = new CPU(filename);
        cpu.run(gpu);
        System.out.println("Exited");
    }

}
