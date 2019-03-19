package GameBoy;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class Debugger {
    JFrame window = new JFrame();
    JPanel container = new JPanel();
    JPanel memoryViewer = new JPanel();
    JTextArea memText = new JTextArea(25, 12);
    JScrollPane memScrollPane = new JScrollPane(memText);

    JPanel regViewer = new JPanel();
    JTextArea regText = new JTextArea(12, 15);
    JScrollPane regScrollPane = new JScrollPane(regText);

    MMU mmu;
    Registers regs;

    public Debugger(MMU mmu, Registers regs) {
        this.mmu = mmu;
        this.regs = regs;

        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setPreferredSize(new Dimension(700, 600));
        container.setLayout(new BorderLayout());

        memScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        memoryViewer.add(memScrollPane);
        memoryViewer.setBorder(new TitledBorder(new EtchedBorder(), "Memory"));

        regScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        regViewer.setBorder(new TitledBorder(new EtchedBorder(), "Registers"));
        regViewer.add(regScrollPane);

        container.add(memoryViewer, BorderLayout.EAST);
        container.add(regViewer, BorderLayout.WEST);
        window.add(container);
        window.setVisible(true);
    }

    public void draw() {
        regText.setText(regs.stringify());
        memText.setText(mmu.stringify());

        window.pack();
    }

    public boolean isDisplayable() {
        return window.isDisplayable();
    }
}
