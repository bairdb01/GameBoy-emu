package GameBoy;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Interrupts {
    static PriorityQueue<Interrupt> interrupts = new PriorityQueue<>(new Comparator<Interrupt>() {
        @Override
        public int compare(Interrupt o1, Interrupt o2) {
            if (o1.priority < o2.priority) {
                return -1;
            } else if (o1.priority > o2.priority) {
                return 1;
            }
            return 0;
        }
    });

    static public Interrupt retreiveInterrupt() {
        return interrupts.remove();
    }

    static public void requestInterupt(Interrupt ir) {
        interrupts.add(ir);
    }

}
