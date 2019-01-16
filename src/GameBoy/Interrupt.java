package GameBoy;

/**
 * Author Benjamin Baird
 * Created on 2019-01-16
 * Filename GameBoy.Interupt
 * Description: Interrupt's are represented by this class. The priority and name of interrupt is stored. The type of
 * interrupt is stored in the bits of register 0xFF0F.
 * Possible interrupts are as follows:
 * Bit 0: V-Blank Interupt
 * Bit 1: LCD Interupt
 * Bit 2: Timer Interupt
 * Bit 4: Joypad Interupt
 */
public class Interrupt {
    private String name;
    private String origin;
    private int priority = 0;   // Flag's Bit position @ 0xFF0F
    private short serviceAdr;

    public Interrupt(String name, String origin, int priority) {
        this.name = name;
        this.origin = origin;
        this.priority = priority;
        switch(priority) {
            case 0:
                serviceAdr = 0x40;
                break;
            case 1:
                serviceAdr = 0x48;
                break;
            case 2:
                serviceAdr = 0x50;
                break;
            case 3:
                serviceAdr = 0x60;
        }
    }

    public String getName() {
        return name;
    }

    public String getOrigin() {
        return origin;
    }

    public int getPriority() {
        return priority;
    }

    public short getServiceAdr() {
        return serviceAdr;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Name: " + name + "\n");
        s.append("Origin: " + origin + "\n");
        s.append("Priority: " + priority + "\n");
        return s.toString();
    }

    @Override
    public boolean equals(Object obj) {
        Interrupt ir = (Interrupt) obj;

        return ((this.priority == ir.priority) && this.name.equals(ir.name));
    }
}