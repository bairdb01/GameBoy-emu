package GameBoy;

/**
 * Author: Benjamin Baird
 * Created on: 2018-08-28
 * Last Updated on: 2018-08-28
 * Filename: Opcode
 * Description:
 */
public class Instructions {
    String label;
    int Opcode;
    int cycles;
    Operation op;

    public Instructions(String label, int Opcode, int clocks, Operation op) {
        this.label = label;
        this.Opcode = Opcode;
        this.cycles = cycles;
        this.op = op;
    }

}


/**
 * Interface for Opcode functions
 */
@FunctionalInterface
interface Operation {
    /**
     * Performs a command. To be used via an Opcode
     *
     * @param regs   An object which contains all the registers operable on
     * @param memory Memory(RAM+ROM) to read/write from
     * @param args   additional arguments for any operations
     */
    void cmd(Registers regs, Memory memory, int[] args);

}
