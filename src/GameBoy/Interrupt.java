package GameBoy;

public class Interrupt {
    int priority = 0;
    String name;
    String origin;

    public Interrupt(int priority, String name, String origin) {
        this.priority = priority;
        this.name = name;
        this.origin = origin;
    }

    public Interrupt(String name, String origin) {
        this.name = name;
        this.origin = origin;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Name: " + name + "\n");
        s.append("Origin: " + origin + "\n");
        s.append("Priority: " + priority + "\n");
        return s.toString();
    }
}