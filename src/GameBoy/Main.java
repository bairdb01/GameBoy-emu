package GameBoy;

/**
 * Executes the GameBoy emulator
 */
public class Main {

    public static void main(String[] args) {
        CPU cpu = new CPU();
        cpu.run();
    }

}
