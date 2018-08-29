/**
 * Author: Benjamin Baird
 * Created on: 2018-08-28
 * Last Updated on: 2018-08-28
 * Filename: OPCODES
 * Description: Various Commands and their related OPCODES
 * Each Opcode corresponds to a specific instruction.
 * TODO:
 *       FUNCTION
 *       FLAG FUNCTIONS
 *       WRITE DEDICATED XOR, OR, AND functions
 */
public class Opcodes {
    final private Instructions opcodes[] = new Instructions[0x100];  //0xFF is highest op code


    public Opcodes() {

        /***
         * 8-BIT LOADS
         */
        //1
        setOpcode("LD B,n", 0x06, 8, (regs, memory, args) -> regs.setB(args[0]));
        setOpcode("LD C,n", 0x0E, 8, (regs, memory, args) -> regs.setC(args[0]));
        setOpcode("LD D,n", 0x16, 8, (regs, memory, args) -> regs.setD(args[0]));
        setOpcode("LD E,n", 0x1E, 8, (regs, memory, args) -> regs.setE(args[0]));
        setOpcode("LD H,n", 0x26, 8, (regs, memory, args) -> regs.setH(args[0]));
        setOpcode("LD L,n", 0x2E, 8, (regs, memory, args) -> regs.setL(args[0]));

        //LD INTO A
        setOpcode("LD A,A", 0x7F, 4, (regs, memory, args) -> regs.setA(regs.getA()));
        setOpcode("LD A,B", 0x78, 4, (regs, memory, args) -> regs.setA(regs.getB()));
        setOpcode("LD A,C", 0x79, 4, (regs, memory, args) -> regs.setA(regs.getC()));
        setOpcode("LD A,D", 0x7A, 4, (regs, memory, args) -> regs.setA(regs.getD()));
        setOpcode("LD A,E", 0x7B, 4, (regs, memory, args) -> regs.setA(regs.getE()));
        setOpcode("LD A,H", 0x7C, 4, (regs, memory, args) -> regs.setA(regs.getH()));
        setOpcode("LD A,L", 0x7D, 4, (regs, memory, args) -> regs.setA(regs.getL()));
        setOpcode("LD A,(BC)", 0x0A, 8, (regs, memory, args) -> regs.setA(regs.getBC()));
        setOpcode("LD A,(DE)", 0x1A, 8, (regs, memory, args) -> regs.setA(regs.getDE()));
        setOpcode("LD A,(HL)", 0x7E, 8, (regs, memory, args) -> regs.setA(regs.getHL()));
        setOpcode("LD A,(nn)", 0xFA, 16, (regs, memory, args) -> regs.setA(regs.getNN(args[0], args[1])));
        setOpcode("LD A,#", 0x3E, 8, (regs, memory, args) -> regs.setA(regs.getN()));
        setOpcode("LD A,(C)", 0xF2, 8, (regs, memory, args) -> regs.setA(getNN(args[0], regs.getC())));
        setOpcode("LDD A,(HL)", 0x3A, 8, (regs, memory, args) -> {
            regs.setA(regs.getHL());
            regs.decHL();
        });
        setOpcode("LDI A,(HL)", 0x2A, 8, (regs, memory, args) -> {
            regs.setA(regs.getHL());
            regs.incHL();
        });
        setOpcode("LDD A,(n)", 0xF0, 12, (regs, memory, args) -> regs.setA(regs.getN(0xFF00 + args[0])));

        //LD INTO B
        setOpcode("LD B,A", 0x47, 4, (regs, memory, args) -> regs.setB(regs.getA()));
        setOpcode("LD B,B", 0x40, 4, (regs, memory, args) -> regs.setB(regs.getB()));
        setOpcode("LD B,C", 0x41, 4, (regs, memory, args) -> regs.setB(regs.getC()));
        setOpcode("LD B,D", 0x42, 4, (regs, memory, args) -> regs.setB(regs.getD()));
        setOpcode("LD B,E", 0x43, 4, (regs, memory, args) -> regs.setB(regs.getE()));
        setOpcode("LD B,H", 0x44, 4, (regs, memory, args) -> regs.setB(regs.getH()));
        setOpcode("LD B,L", 0x45, 4, (regs, memory, args) -> regs.setB(regs.getL()));
        setOpcode("LD B,(HL)", 0x46, 8, (regs, memory, args) -> regs.setB(regs.getHL()));

        //LD INTO C
        setOpcode("LD C,A", 0x4F, 4, (regs, memory, args) -> regs.setC(regs.getA()));
        setOpcode("LD C,B", 0x48, 4, (regs, memory, args) -> regs.setC(regs.getB()));
        setOpcode("LD C,C", 0x49, 4, (regs, memory, args) -> regs.setC(regs.getC()));
        setOpcode("LD C,D", 0x4A, 4, (regs, memory, args) -> regs.setC(regs.getD()));
        setOpcode("LD C,E", 0x4B, 4, (regs, memory, args) -> regs.setC(regs.getE()));
        setOpcode("LD C,H", 0x4C, 4, (regs, memory, args) -> regs.setC(regs.getH()));
        setOpcode("LD C,L", 0x4D, 4, (regs, memory, args) -> regs.setC(regs.getL()));
        setOpcode("LD C,(HL)", 0x4E, 8, (regs, memory, args) -> regs.setC(regs.getHL()));
        setOpcode("LD (C),A", 0xE2, 8, (regs, memory, args) -> regs.setN(args[0], regs.getA()));

        //LD INTO D
        setOpcode("LD L,A", 0x57, 4, (regs, memory, args) -> regs.setD(regs.getA()));
        setOpcode("LD D,B", 0x50, 4, (regs, memory, args) -> regs.setD(regs.getB()));
        setOpcode("LD D,C", 0x51, 4, (regs, memory, args) -> regs.setD(regs.getC()));
        setOpcode("LD D,D", 0x52, 4, (regs, memory, args) -> regs.setD(regs.getD()));
        setOpcode("LD D,E", 0x53, 4, (regs, memory, args) -> regs.setD(regs.getE()));
        setOpcode("LD D,H", 0x54, 4, (regs, memory, args) -> regs.setD(regs.getH()));
        setOpcode("LD D,L", 0x55, 4, (regs, memory, args) -> regs.setD(regs.getL()));
        setOpcode("LD D,(HL)", 0x56, 8, (regs, memory, args) -> regs.setD(regs.getHL()));

        //LD INTO E
        setOpcode("LD E,A", 0x5F, 4, (regs, memory, args) -> regs.setE(regs.getA()));
        setOpcode("LD E,B", 0x58, 4, (regs, memory, args) -> regs.setE(regs.getB()));
        setOpcode("LD E,C", 0x59, 4, (regs, memory, args) -> regs.setE(regs.getC()));
        setOpcode("LD E,D", 0x5A, 4, (regs, memory, args) -> regs.setE(regs.getD()));
        setOpcode("LD E,E", 0x5B, 4, (regs, memory, args) -> regs.setE(regs.getE()));
        setOpcode("LD E,H", 0x5C, 4, (regs, memory, args) -> regs.setE(regs.getH()));
        setOpcode("LD E,L", 0x5D, 4, (regs, memory, args) -> regs.setE(regs.getL()));
        setOpcode("LD E,(HL)", 0x5E, 8, (regs, memory, args) -> regs.setE(regs.getHL()));

        //LD INTO H
        setOpcode("LD H,A", 0x67, 4, (regs, memory, args) -> regs.setH(regs.getA()));
        setOpcode("LD H,B", 0x60, 4, (regs, memory, args) -> regs.setH(regs.getB()));
        setOpcode("LD H,C", 0x61, 4, (regs, memory, args) -> regs.setH(regs.getC()));
        setOpcode("LD H,D", 0x62, 4, (regs, memory, args) -> regs.setH(regs.getD()));
        setOpcode("LD H,E", 0x63, 4, (regs, memory, args) -> regs.setH(regs.getE()));
        setOpcode("LD H,H", 0x64, 4, (regs, memory, args) -> regs.setH(regs.getH()));
        setOpcode("LD H,L", 0x65, 4, (regs, memory, args) -> regs.setH(regs.getL()));
        setOpcode("LD H,(HL)", 0x66, 8, (regs, memory, args) -> regs.setH(regs.getHL()));

        //LD INTO L
        setOpcode("LD L,A", 0x6F, 4, (regs, memory, args) -> regs.setL(regs.getA()));
        setOpcode("LD L,B", 0x68, 4, (regs, memory, args) -> regs.setL(regs.getB()));
        setOpcode("LD L,C", 0x69, 4, (regs, memory, args) -> regs.setL(regs.getC()));
        setOpcode("LD L,D", 0x6A, 4, (regs, memory, args) -> regs.setL(regs.getD()));
        setOpcode("LD L,E", 0x6B, 4, (regs, memory, args) -> regs.setL(regs.getE()));
        setOpcode("LD L,H", 0x6C, 4, (regs, memory, args) -> regs.setL(regs.getH()));
        setOpcode("LD L,L", 0x6D, 4, (regs, memory, args) -> regs.setL(regs.getL()));
        setOpcode("LD L,(HL)", 0x6E, 8, (regs, memory, args) -> regs.setL(regs.getHL()));

        //LD INTO HL
        setOpcode("LD (HL),A", 0x77, 8, (regs, memory, args) -> regs.setHL(regs.getA()));
        setOpcode("LD (HL),B", 0x70, 8, (regs, memory, args) -> regs.setHL(regs.getB()));
        setOpcode("LD (HL),C", 0x71, 8, (regs, memory, args) -> regs.setHL(regs.getC()));
        setOpcode("LD (HL),D", 0x72, 8, (regs, memory, args) -> regs.setHL(regs.getD()));
        setOpcode("LD (HL),E", 0x73, 8, (regs, memory, args) -> regs.setHL(regs.getE()));
        setOpcode("LD (HL),H", 0x74, 8, (regs, memory, args) -> regs.setHL(regs.getH()));
        setOpcode("LD (HL),L", 0x75, 8, (regs, memory, args) -> regs.setHL(regs.getL()));
        setOpcode("LD (HL),n", 0x36, 12, (regs, memory, args) -> regs.setHL(memory.getN(args[0])));
        setOpcode("LDD (HL),A", 0x32, 8, (regs, memory, args) -> {
            regs.setHL(regs.getA());
            regs.decHL();
        });
        setOpcode("LDI (HL),A", 0x22, 8, (regs, memory, args) -> {
            regs.setHL(regs.getA());
            regs.incHL();
        });

        /**
         * 16-bit Loads
         */

        setOpcode("LDHL SP,n", 0xF8, 12, (regs, memory, args) -> {
            regs.setHL(memory.getN(regs.getSP() + args[0]));
            regs.flag.clearZ();
            regs.flag.clearN();
            regs.flagsetH();
            regs.flag.setC();
        }); // See CPU book for flags
        setOpcode("LD HL,nn", 0x21, 12, (regs, memory, args) -> regs.setHL(args[0]));

        // LD INTO (BC)
        setOpcode("LD (BC),nn", 0x01, 12, (regs, memory, args) -> regs.setBC(args[0]));
        setOpcode("LD (BC),A", 0x02, 8, (regs, memory, args) -> regs.setBC(regs.getA()));

        // LD INTO (DE)
        setOpcode("LD DE,nn", 0x11, 12, (regs, memory, args) -> regs.setDE(args[0]));
        setOpcode("LD (DE),A", 0x12, 8, (regs, memory, args) -> regs.setDE(regs.getA()));

        // LD INTO (SP)
        setOpcode("LD SP,nn", 0x31, 12, (regs, memory, args) -> regs.setSP(args[0]));
        setOpcode("LD SP,HL", 0xF9, 8, (regs, memory, args) -> regs.setSP(regs.getHL()));

        setOpcode("LD (NN),A", 0xEA, 8, (regs, memory, args) -> regs.setNN(regs.getA()));
        setOpcode("LD (NN),SP", 0x08, 20, (regs, memory, args) -> regs.setNN(regs.getSP()));


        setOpcode("LDh (n),A", 0xE0, 12, (regs, memory, args) -> regs.setN(0xFF00 + args[0], regs.getA()));


        // PUSH REGISTER PAIR ONTO STACK; DECREMENT SP
        setOpcode("PUSH AF", 0xF5, 16, (regs, memory, args) -> {
            memory.push(regs.getAF(), regs.getSP());
            regs.decSP();
        });
        setOpcode("PUSH BC", 0xC5, 16, (regs, memory, args) -> {
            memory.push(regs.getBC(), regs.getSP());
            regs.decSP();
        });
        setOpcode("PUSH DE", 0xD5, 16, (regs, memory, args) -> {
            memory.push(regs.getDE().regs.getSP());
            regs.decSP();
        });
        setOpcode("PUSH HL", 0xE5, 16, (regs, memory, args) -> {
            memory.push(regs.getHL().regs.getSP());
            regs.decSP();
        });

        // POP REGISTER PAIR OFF OF STACK; INCREMENT SP
        setOpcode("POP AF", 0xF1, 12, (regs, memory, args) -> {
            regs.setAF(memory.pop(regs.getSP()));
            regs.incSP();
        });
        setOpcode("POP BC", 0xC1, 12, (regs, memory, args) -> {
            regs.setBC(memory.pop(regs.getSP()));
            regs.incSP();
        });
        setOpcode("POP DE", 0xD1, 12, (regs, memory, args) -> {
            regs.setDE(memory.pop(regs.getSP()));
            regs.incSP();
        });
        setOpcode("POP HL", 0xE1, 12, (regs, memory, args) -> {
            regs.setHL(memory.pop(regs.getSP()));
            regs.incSP();
        });


        /****
         * 8-Bit ALU
         */

        // ADD TO A; FLAGS AFFECTED;
        setOpcode("ADD A,A", 0x87, 4, (regs, memory, args) -> regs.addA(regs.getA()));
        setOpcode("ADD A,B", 0x80, 4, (regs, memory, args) -> regs.addA(regs.getB()));
        setOpcode("ADD A,C", 0x81, 4, (regs, memory, args) -> regs.addA(regs.getC()));
        setOpcode("ADD A,D", 0x82, 4, (regs, memory, args) -> regs.addA(regs.getD()));
        setOpcode("ADD A,E", 0x83, 4, (regs, memory, args) -> regs.addA(regs.getE()));
        setOpcode("ADD A,H", 0x84, 4, (regs, memory, args) -> regs.addA(regs.getH()));
        setOpcode("ADD A,L", 0x85, 4, (regs, memory, args) -> regs.addA(regs.getL()));
        setOpcode("ADD A,(HL)", 0x86, 8, (regs, memory, args) -> regs.addA(regs.getHL()));
        setOpcode("ADD A,#", 0xC6, 8, (regs, memory, args) -> regs.addA(args[0]));

        // Add register + carry flag to A; FLAGS AFFECTED
        setOpcode("ADD A,A", 0x8F, 4, (regs, memory, args) -> regs.addA(regs.getA() +));
        setOpcode("ADD A,B", 0x88, 4, (regs, memory, args) -> regs.addA(regs.getB()) +);
        setOpcode("ADD A,C", 0x89, 4, (regs, memory, args) -> regs.addA(regs.getC()) +);
        setOpcode("ADD A,D", 0x8A, 4, (regs, memory, args) -> regs.addA(regs.getD()) +);
        setOpcode("ADD A,E", 0x8B, 4, (regs, memory, args) -> regs.addA(regs.getE()) +);
        setOpcode("ADD A,H", 0x8C, 4, (regs, memory, args) -> regs.addA(regs.getH()) +);
        setOpcode("ADD A,L", 0x8D, 4, (regs, memory, args) -> regs.addA(regs.getL()) +);
        setOpcode("ADD A,(HL)", 0x8E, 8, (regs, memory, args) -> regs.addA(regs.getHL()) +);
        setOpcode("ADD A,#", 0xCE, 8, (regs, memory, args) -> regs.addA(args[0]) +);


        // SUBTRACT N FROM A; FLAGS AFFECTED
        setOpcode("SUB A", 0x97, 4, (regs, memory, args) -> regs.subA(regs.getA()));
        setOpcode("SUB B", 0x90, 4, (regs, memory, args) -> regs.subA(regs.getB()));
        setOpcode("SUB C", 0x91, 4, (regs, memory, args) -> regs.subA(regs.getC()));
        setOpcode("SUB D", 0x92, 4, (regs, memory, args) -> regs.subA(regs.getD()));
        setOpcode("SUB E", 0x93, 4, (regs, memory, args) -> regs.subA(regs.getE()));
        setOpcode("SUB H", 0x94, 4, (regs, memory, args) -> regs.subA(regs.getH()));
        setOpcode("SUB L", 0x95, 4, (regs, memory, args) -> regs.subA(regs.getL()));
        setOpcode("SUB (HL)", 0x96, 8, (regs, memory, args) -> regs.subA(regs.getHL()));
        setOpcode("SUB #", 0xD6, 8, (regs, memory, args) -> regs.subA(args[0]));

        // SUBTRACT + CARRY FLAG FROM A
        setOpcode("SBC A", 0x9F, 4, (regs, memory, args) -> regs.sbcA(regs.getA()));
        setOpcode("SBC B", 0x98, 4, (regs, memory, args) -> regs.sbcA(regs.getB()));
        setOpcode("SBC C", 0x99, 4, (regs, memory, args) -> regs.sbcA(regs.getC()));
        setOpcode("SBC D", 0x9A, 4, (regs, memory, args) -> regs.sbcA(regs.getD()));
        setOpcode("SBC E", 0x9B, 4, (regs, memory, args) -> regs.sbcA(regs.getE()));
        setOpcode("SBC H", 0x9C, 4, (regs, memory, args) -> regs.sbcA(regs.getH()));
        setOpcode("SBC L", 0x9D, 4, (regs, memory, args) -> regs.sbcA(regs.getL()));
        setOpcode("SBC (HL)", 0x9E, 8, (regs, memory, args) -> regs.sbcA(regs.getHL()));
        setOpcode("SBC #", -1, -0, (regs, memory, args) -> regs.sbcA(args[0])); // MISSING OP CODE + CLOCKS

        // LOGICAL AND A & N STORED IN A; FLAG AFFECTED
        setOpcode("AND A", 0xA7, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() & regs.getA());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.setH();
            regs.flags.clearC();
        });
        setOpcode("AND B", 0xA0, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() & regs.getB());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.setH();
            regs.flags.clearC();
        });
        setOpcode("AND C", 0xA1, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() & regs.getC());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.setH();
            regs.flags.clearC();
        });
        setOpcode("AND D", 0xA2, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() & regs.getD());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.setH();
            regs.flags.clearC();
        });
        setOpcode("AND E", 0xA3, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() & regs.getE());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.setH();
            regs.flags.clearC();
        });
        setOpcode("AND H", 0xA4, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() & regs.getH());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.setH();
            regs.flags.clearC();
        });
        setOpcode("AND L", 0xA5, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() & regs.getHL());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.setH();
            regs.flags.clearC();
        });
        setOpcode("AND (HL)", 0xA6, 8, (regs, memory, args) -> {
            regs.setA(regs.getA() & regs.getHL());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.setH();
            regs.flags.clearC();
        });
        setOpcode("AND #", 0xE6, 8, (regs, memory, args) -> {
            regs.setA(regs.getA() & args[0]);
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.setH();
            regs.flags.clearC();
        });

        // LOGICAL OR A & N STORED IN A; FLAG AFFECTED
        setOpcode("OR A", 0xB7, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() | regs.getA());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.clearH();
            regs.flags.clearC();
        });
        setOpcode("OR B", 0xB0, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() | regs.getB());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.clearH();
            regs.flags.clearC();
        });
        setOpcode("OR C", 0xB1, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() | regs.getC());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.clearH();
            regs.flags.clearC();
        });
        setOpcode("OR D", 0xB2, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() | regs.getD());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.clearH();
            regs.flags.clearC();
        });
        setOpcode("OR E", 0xB3, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() | regs.getE());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.clearH();
            regs.flags.clearC();
        });
        setOpcode("OR H", 0xB4, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() | regs.getH());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.clearH();
            regs.flags.clearC();
        });
        setOpcode("OR L", 0xB5, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() | regs.getHL());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.clearH();
            regs.flags.clearC();
        });
        setOpcode("OR (HL)", 0xB6, 8, (regs, memory, args) -> {
            regs.setA(regs.getA() | regs.getHL());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.clearH();
            regs.flags.clearC();
        });
        setOpcode("OR #", 0xF6, 8, (regs, memory, args) -> {
            regs.setA(regs.getA() | args[0]);
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.clearH();
            regs.flags.clearC();
        });

        // LOGICAL OR A & N STORED IN A; FLAG AFFECTED
        setOpcode("XOR A", 0xAF, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() ^ regs.getA());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.clearH();
            regs.flags.clearC();
        });
        setOpcode("XOR B", 0xA8, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() ^ regs.getB());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.clearH();
            regs.flags.clearC();
        });
        setOpcode("XOR C", 0xA9, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() ^ regs.getC());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.clearH();
            regs.flags.clearC();
        });
        setOpcode("XOR D", 0xAA, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() ^ regs.getD());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.clearH();
            regs.flags.clearC();
        });
        setOpcode("XOR E", 0xAB, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() ^ regs.getE());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.clearH();
            regs.flags.clearC();
        });
        setOpcode("XOR H", 0xAC, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() ^ regs.getH());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.clearH();
            regs.flags.clearC();
        });
        setOpcode("XOR L", 0xAD, 4, (regs, memory, args) -> {
            regs.setA(regs.getA() ^ regs.getHL());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.clearH();
            regs.flags.clearC();
        });
        setOpcode("XOR (HL)", 0xAE, 8, (regs, memory, args) -> {
            regs.setA(regs.getA() ^ regs.getHL());
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.clearH();
            regs.flags.clearC();
        });
        setOpcode("XOR #", 0xAE, 8, (regs, memory, args) -> {
            regs.setA(regs.getA() ^ args[0]);
            if (regs.getA() == 0) regs.flags.setZ();
            regs.flags.clearN();
            regs.flags.clearH();
            regs.flags.clearC();
        });

        // COMPARE A with N. BASICALLY AN A - N SUBTRACTION, WITH THE RESULTS THROWN AWAY; FLAGS AFFECTED.
        setOpcode("OR A", 0xBF, 4, (regs, memory, args) -> regs.cpA(regs.getA());
        setOpcode("OR B", 0xB8, 4, (regs, memory, args) -> regs.cpA(regs.getB());
        setOpcode("OR C", 0xB9, 4, (regs, memory, args) -> regs.cpA(regs.getC());
        setOpcode("OR D", 0xBA, 4, (regs, memory, args) -> regs.cpA(regs.getD());
        setOpcode("OR E", 0xBB, 4, (regs, memory, args) -> regs.cpA(regs.getE());
        setOpcode("OR H", 0xBC, 4, (regs, memory, args) -> regs.cpA(regs.getH());
        setOpcode("OR L", 0xBD, 4, (regs, memory, args) -> regs.cpA(regs.getL());
        setOpcode("OR (HL)", 0xBE, 8, (regs, memory, args) -> regs.cpA(regs.getHL());
        setOpcode("OR #", 0xFE, 8, (regs, memory, args) -> regs.cpA(regs.getN());

        // INCREMENT REGISTER N; FLAGS AFFECTED
        setOpcode("INC A", 0x3C, 4, (regs, memory, args) -> regs.incA());
        setOpcode("INC B", 0x04, 4, (regs, memory, args) -> regs.incB());
        setOpcode("INC C", 0x0C, 4, (regs, memory, args) -> regs.incC());
        setOpcode("INC D", 0x14, 4, (regs, memory, args) -> regs.incD());
        setOpcode("INC E", 0x1C, 4, (regs, memory, args) -> regs.incE());
        setOpcode("INC H", 0x24, 4, (regs, memory, args) -> regs.incH());
        setOpcode("INC L", 0x2C, 4, (regs, memory, args) -> regs.incL());
        setOpcode("INC (HL)", 0x34, 12, (regs, memory, args) -> regs.incHL());

        // DECREMENT REGISTER N; FLAGS AFFECTED
        setOpcode("INC A", 0x3C, 4, (regs, memory, args) -> regs.decA());
        setOpcode("INC B", 0x04, 4, (regs, memory, args) -> regs.decB());
        setOpcode("INC C", 0x0C, 4, (regs, memory, args) -> regs.decC());
        setOpcode("INC D", 0x14, 4, (regs, memory, args) -> regs.decD());
        setOpcode("INC E", 0x1C, 4, (regs, memory, args) -> regs.decE());
        setOpcode("INC H", 0x24, 4, (regs, memory, args) -> regs.decH());
        setOpcode("INC L", 0x2C, 4, (regs, memory, args) -> regs.decL());
        setOpcode("INC (HL)", 0x34, 12, (regs, memory, args) -> regs.decHL());


        /**
         * 16-Bit Arithmetic
         */


        /**
         * Misc.
         */

        /**
         * Rotates & Shifts
         */

        /**
         * Bit Opcodes
         */

        /**
         * Jumps
         */

        /**
         * Calls
         */

        /**
         * Restarts
         */

        /**
         * Returns
         */


    }

    private void setOpcode(String label, int opcode, int clocks, Operation op) {
        opcodes[opcode] = new Instructions(label, opcode, clocks, op);
    }


}
