package GameBoy;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Author: Benjamin Baird
 * Created on: 2019-01-16
 * Filename: GameBoy.Interrupts
 * Description: A interrupt handler class. Contains a priority queue to handle interrupts with varying priorities.
 * In order for an interrupt to be processed/executed it must follow these steps:
 *
 *      1. When an event occurs that needs to trigger its interrupt it must make an interrupt request
 *      by setting its corresponding bit in the Interrupt Request Register (0xFF0F).
 *
 *      2. An interrupt can only be serviced if the Interrupt Master Enable switch is set to true
 *
 *      3. If the above two conditions are true and their is no other interupt with a higher priority
 *      awaiting to be serviced then it checks the Interupt Enabled Register(0xFFFF) to see if its
 *      corresponding interrupt bit is set to 1 to allow servicing of this particular interrupt.
 */
public class Interrupts {
    static boolean masterInterruptSwitch = true; // Tool used by GameBoy to enable servicing of an interrupt.
    static PriorityQueue<Interrupt> interrupts = new PriorityQueue<>(new Comparator<Interrupt>() {

        @Override
        public int compare(Interrupt o1, Interrupt o2) {
            if (o1.getPriority() < o2.getPriority()) {
                return -1;
            } else if (o1.getPriority() > o2.getPriority()) {
                return 1;
            }
            return 0;
        }
    });

    static public Interrupt retreiveInterrupt(MMU mmu) {
        return interrupts.remove();
    }

    /**
     * Add an interrupt to the queue if not already in and
     * Update the interrupt request flag in the MMU.
     *
     * @param mmu The memory management unit with access to the interrupt request register
     * @param ir An interrupt
     */
    static public void requestInterrupt(MMU mmu, Interrupt ir) {
        if (!interrupts.contains(ir)) {
            byte interruptRequestFlag = (byte) (mmu.getMemVal(0xFF0F) | ir.getPriority()); // Sets the interrupt's flag in register
            mmu.setMemVal(0xFF0F, interruptRequestFlag);
            interrupts.add(ir);
        }
    }

}
