package GameBoy;

import java.util.Comparator;
import java.util.HashSet;
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
    static Interrupt[] interrupts = new Interrupt[4];

    /**
     * Add an interrupt to the queue if not already in and
     * Update the interrupt request flag in the MMU.
     *
     * @param mmu The memory management unit with access to the interrupt request register
     * @param ir An interrupt
     */
    static public void requestInterrupt(MMU mmu, Interrupt ir) {
        int priority = ir.getPriority();
        byte interruptRequestFlag = BitUtils.setBit(mmu.getMemVal(0xFF0F), priority); // Sets the interrupt's flag in register
        interrupts[priority] = ir;
        mmu.setMemVal(0xFF0F, interruptRequestFlag);
    }

    /**
     * Handles all interrupts if their respective flags are set.
     */
    static void handleInterrupts(MMU mmu, Registers regs) {
        // Make sure the system is allowing interrupts
        if (Interrupts.masterInterruptSwitch) {
            byte irEnabled = mmu.getMemVal(0xFFFF);

            if (irEnabled != 0) {
                byte irRequest = mmu.getMemVal(0xFF0F);

                // Remove interrupt from queue
                for (int i = 0; i < 4; i++) {
                    // Check if the interruptEnable register has enabled servicing for this interrupt
                    int priority = interrupts[i].getPriority();
                    if (priority != -1) {
                        if (BitUtils.testBit(irRequest, priority) && BitUtils.testBit(irEnabled, priority)) {
                            serviceInterrupt(interrupts[i], mmu, regs);
                            interrupts[i] = new Interrupt();
                        }
                    }
                }
            }
        }
    }

    /**
     * Services an interrupt.
     *
     * @param ir An iterrupt to service
     */
    static private void serviceInterrupt(Interrupt ir, MMU mmu, Registers regs) {
        Interrupts.masterInterruptSwitch = false;   // Need to set to true once interrupts are done
        byte interruptRequest = (byte) (mmu.getMemVal(0xFF0F) | ir.getPriority());  // Clear interrupt request bit
        mmu.setMemVal(0xFF0F, interruptRequest);

        // Push PC to stack
        mmu.push(regs.getSP(), regs.getPC());
        regs.setSP((short) (regs.getSP() - 2));

        // Set program counter to interrupt handler
        regs.setPC(ir.getServiceAdr());
    }

    static public boolean isMasterEnabled() {
        return masterInterruptSwitch;
    }
}
