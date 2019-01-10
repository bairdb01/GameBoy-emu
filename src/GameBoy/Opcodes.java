package GameBoy;

/**
 * Author: Benjamin Baird
 * Created on: 2018-08-28
 * Filename: OPCODES
 * Description: Various GameBoy.Commands and their related OPCODES
 * Each Opcode corresponds to a specific instruction.
 * TODO: MISSING FUNCTIONS
 * TODO: Clock cycles from GBCPUman may be inaccurate. Double check with other sources.
 * TODO: Some opcodes have varying clock cycles. To fix have an if-else to check which path was taken.
 */
public class Opcodes {

    final private Instructions[] std_opcodes = new Instructions[0x100];  // 0xFF is highest opcode.
    // Using array because we know the exact Opcode,
    // therefore faster random access.

    final private Instructions[] cb_opcodes = new Instructions[0x100]; // GameBoy.Opcodes which have the CB prefix
    // CB prefix just means to use this table, then the CB is thrown out

    int getNumArgs(int opcode) {
        if (opcode < 0x100)
            return std_opcodes[0xFF & opcode].numArgs;
        else {
            opcode &= 0xFF;
            return cb_opcodes[0xFF & opcode].numArgs;
        }
    }

    public Opcodes() {
        /*
         * 8-BIT LOADS
         */
        setOpCode(std_opcodes, "LD B,n", 0x06, 8, 1, (regs, memory, args) -> regs.setB(args[0]));
        setOpCode(std_opcodes, "LD C,n", 0x0E, 8, 1, (regs, memory, args) -> regs.setC(args[0]));
        setOpCode(std_opcodes, "LD D,n", 0x16, 8, 1, (regs, memory, args) -> regs.setD(args[0]));
        setOpCode(std_opcodes, "LD E,n", 0x1E, 8, 1, (regs, memory, args) -> regs.setE(args[0]));
        setOpCode(std_opcodes, "LD H,n", 0x26, 8, 1, (regs, memory, args) -> regs.setH(args[0]));
        setOpCode(std_opcodes, "LD L,n", 0x2E, 8, 1, (regs, memory, args) -> regs.setL(args[0]));

        //LD INTO A
        setOpCode(std_opcodes, "LD A,A", 0x7F, 4, (regs, memory, args) -> regs.setA(regs.getA()));
        setOpCode(std_opcodes, "LD A,B", 0x78, 4, (regs, memory, args) -> regs.setA(regs.getB()));
        setOpCode(std_opcodes, "LD A,C", 0x79, 4, (regs, memory, args) -> regs.setA(regs.getC()));
        setOpCode(std_opcodes, "LD A,D", 0x7A, 4, (regs, memory, args) -> regs.setA(regs.getD()));
        setOpCode(std_opcodes, "LD A,E", 0x7B, 4, (regs, memory, args) -> regs.setA(regs.getE()));
        setOpCode(std_opcodes, "LD A,H", 0x7C, 4, (regs, memory, args) -> regs.setA(regs.getH()));
        setOpCode(std_opcodes, "LD A,L", 0x7D, 4, (regs, memory, args) -> regs.setA(regs.getL()));
        setOpCode(std_opcodes, "LD A,(BC)", 0x0A, 8, (regs, memory, args) -> regs.setA(memory.getMemVal(regs.getBC())));
        setOpCode(std_opcodes, "LD A,(DE)", 0x1A, 8, (regs, memory, args) -> regs.setA(memory.getMemVal(regs.getDE())));
        setOpCode(std_opcodes, "LD A,(HL)", 0x7E, 8, (regs, memory, args) -> regs.setA(memory.getMemVal(regs.getHL())));
        setOpCode(std_opcodes, "LD A,(nn)", 0xFA, 16, 2, (regs, memory, args) -> regs.setA(memory.getMemVal((short) (((args[1] << 8) & 0xFF00) & ((0xFF & args[0]))))));
        setOpCode(std_opcodes, "LD A,n", 0x3E, 8, 1, (regs, memory, args) -> regs.setA(args[0]));
        setOpCode(std_opcodes, "LD A,($FF00 + n)", 0xF2, 8, 1, (regs, memory, args) -> regs.setA(memory.getMemVal((short) (0xFF00 + (args[0] & 0xFF)))));
        setOpCode(std_opcodes, "LDD A,(HL)", 0x3A, 8, (regs, memory, args) -> {
            regs.setA(memory.getMemVal(regs.getHL()));
            regs.setHL(Commands.dec(regs.getHL()));
        });

        setOpCode(std_opcodes, "LD A,(HL+)", 0x2A, 8, (regs, memory, args) -> {
            regs.setA(memory.getMemVal(regs.getHL()));
            regs.setHL(Commands.inc(regs.getHL()));
        });
        setOpCode(std_opcodes, "LDD A,(n)", 0xF0, 12, 1, (regs, memory, args) -> regs.setA(memory.getMemVal((short) (0xFF00 + (args[0] & 0xFF)))));

        //LD INTO B
        setOpCode(std_opcodes, "LD B,A", 0x47, 4, (regs, memory, args) -> regs.setB(regs.getA()));
        setOpCode(std_opcodes, "LD B,B", 0x40, 4, (regs, memory, args) -> regs.setB(regs.getB()));
        setOpCode(std_opcodes, "LD B,C", 0x41, 4, (regs, memory, args) -> regs.setB(regs.getC()));
        setOpCode(std_opcodes, "LD B,D", 0x42, 4, (regs, memory, args) -> regs.setB(regs.getD()));
        setOpCode(std_opcodes, "LD B,E", 0x43, 4, (regs, memory, args) -> regs.setB(regs.getE()));
        setOpCode(std_opcodes, "LD B,H", 0x44, 4, (regs, memory, args) -> regs.setB(regs.getH()));
        setOpCode(std_opcodes, "LD B,L", 0x45, 4, (regs, memory, args) -> regs.setB(regs.getL()));
        setOpCode(std_opcodes, "LD B,(HL)", 0x46, 8, (regs, memory, args) -> regs.setB(memory.getMemVal(regs.getHL())));

        //LD INTO C
        setOpCode(std_opcodes, "LD C,A", 0x4F, 4, (regs, memory, args) -> regs.setC(regs.getA()));
        setOpCode(std_opcodes, "LD C,B", 0x48, 4, (regs, memory, args) -> regs.setC(regs.getB()));
        setOpCode(std_opcodes, "LD C,C", 0x49, 4, (regs, memory, args) -> regs.setC(regs.getC()));
        setOpCode(std_opcodes, "LD C,D", 0x4A, 4, (regs, memory, args) -> regs.setC(regs.getD()));
        setOpCode(std_opcodes, "LD C,E", 0x4B, 4, (regs, memory, args) -> regs.setC(regs.getE()));
        setOpCode(std_opcodes, "LD C,H", 0x4C, 4, (regs, memory, args) -> regs.setC(regs.getH()));
        setOpCode(std_opcodes, "LD C,L", 0x4D, 4, (regs, memory, args) -> regs.setC(regs.getL()));
        setOpCode(std_opcodes, "LD C,(HL)", 0x4E, 8, (regs, memory, args) -> regs.setC(memory.getMemVal(regs.getHL())));
        setOpCode(std_opcodes, "LD ($FF00 + C),A", 0xE2, 8, (regs, memory, args) -> memory.setMemVal((short) (0xFF00 + regs.getC()), regs.getA()));

        //LD INTO D
        setOpCode(std_opcodes, "LD D,A", 0x57, 4, (regs, memory, args) -> regs.setD(regs.getA()));
        setOpCode(std_opcodes, "LD D,B", 0x50, 4, (regs, memory, args) -> regs.setD(regs.getB()));
        setOpCode(std_opcodes, "LD D,C", 0x51, 4, (regs, memory, args) -> regs.setD(regs.getC()));
        setOpCode(std_opcodes, "LD D,D", 0x52, 4, (regs, memory, args) -> regs.setD(regs.getD()));
        setOpCode(std_opcodes, "LD D,E", 0x53, 4, (regs, memory, args) -> regs.setD(regs.getE()));
        setOpCode(std_opcodes, "LD D,H", 0x54, 4, (regs, memory, args) -> regs.setD(regs.getH()));
        setOpCode(std_opcodes, "LD D,L", 0x55, 4, (regs, memory, args) -> regs.setD(regs.getL()));
        setOpCode(std_opcodes, "LD D,(HL)", 0x56, 8, (regs, memory, args) -> regs.setD(memory.getMemVal(regs.getHL())));

        //LD INTO E
        setOpCode(std_opcodes, "LD E,A", 0x5F, 4, (regs, memory, args) -> regs.setE(regs.getA()));
        setOpCode(std_opcodes, "LD E,B", 0x58, 4, (regs, memory, args) -> regs.setE(regs.getB()));
        setOpCode(std_opcodes, "LD E,C", 0x59, 4, (regs, memory, args) -> regs.setE(regs.getC()));
        setOpCode(std_opcodes, "LD E,D", 0x5A, 4, (regs, memory, args) -> regs.setE(regs.getD()));
        setOpCode(std_opcodes, "LD E,E", 0x5B, 4, (regs, memory, args) -> regs.setE(regs.getE()));
        setOpCode(std_opcodes, "LD E,H", 0x5C, 4, (regs, memory, args) -> regs.setE(regs.getH()));
        setOpCode(std_opcodes, "LD E,L", 0x5D, 4, (regs, memory, args) -> regs.setE(regs.getL()));
        setOpCode(std_opcodes, "LD E,(HL)", 0x5E, 8, (regs, memory, args) -> regs.setE(memory.getMemVal(regs.getHL())));

        //LD INTO H
        setOpCode(std_opcodes, "LD H,A", 0x67, 4, (regs, memory, args) -> regs.setH(regs.getA()));
        setOpCode(std_opcodes, "LD H,B", 0x60, 4, (regs, memory, args) -> regs.setH(regs.getB()));
        setOpCode(std_opcodes, "LD H,C", 0x61, 4, (regs, memory, args) -> regs.setH(regs.getC()));
        setOpCode(std_opcodes, "LD H,D", 0x62, 4, (regs, memory, args) -> regs.setH(regs.getD()));
        setOpCode(std_opcodes, "LD H,E", 0x63, 4, (regs, memory, args) -> regs.setH(regs.getE()));
        setOpCode(std_opcodes, "LD H,H", 0x64, 4, (regs, memory, args) -> regs.setH(regs.getH()));
        setOpCode(std_opcodes, "LD H,L", 0x65, 4, (regs, memory, args) -> regs.setH(regs.getL()));
        setOpCode(std_opcodes, "LD H,(HL)", 0x66, 8, (regs, memory, args) -> regs.setH(memory.getMemVal(regs.getHL())));

        //LD INTO L
        setOpCode(std_opcodes, "LD L,A", 0x6F, 4, (regs, memory, args) -> regs.setL(regs.getA()));
        setOpCode(std_opcodes, "LD L,B", 0x68, 4, (regs, memory, args) -> regs.setL(regs.getB()));
        setOpCode(std_opcodes, "LD L,C", 0x69, 4, (regs, memory, args) -> regs.setL(regs.getC()));
        setOpCode(std_opcodes, "LD L,D", 0x6A, 4, (regs, memory, args) -> regs.setL(regs.getD()));
        setOpCode(std_opcodes, "LD L,E", 0x6B, 4, (regs, memory, args) -> regs.setL(regs.getE()));
        setOpCode(std_opcodes, "LD L,H", 0x6C, 4, (regs, memory, args) -> regs.setL(regs.getH()));
        setOpCode(std_opcodes, "LD L,L", 0x6D, 4, (regs, memory, args) -> regs.setL(regs.getL()));
        setOpCode(std_opcodes, "LD L,(HL)", 0x6E, 8, (regs, memory, args) -> regs.setL(memory.getMemVal(regs.getHL())));

        //LD INTO address at HL
        setOpCode(std_opcodes, "LD (HL),A", 0x77, 8, (regs, memory, args) -> memory.setMemVal(regs.getHL(), regs.getA()));
        setOpCode(std_opcodes, "LD (HL),B", 0x70, 8, (regs, memory, args) -> memory.setMemVal(regs.getHL(), regs.getB()));
        setOpCode(std_opcodes, "LD (HL),C", 0x71, 8, (regs, memory, args) -> memory.setMemVal(regs.getHL(), regs.getC()));
        setOpCode(std_opcodes, "LD (HL),D", 0x72, 8, (regs, memory, args) -> memory.setMemVal(regs.getHL(), regs.getD()));
        setOpCode(std_opcodes, "LD (HL),E", 0x73, 8, (regs, memory, args) -> memory.setMemVal(regs.getHL(), regs.getE()));
        setOpCode(std_opcodes, "LD (HL),H", 0x74, 8, (regs, memory, args) -> memory.setMemVal(regs.getHL(), regs.getH()));
        setOpCode(std_opcodes, "LD (HL),L", 0x75, 8, (regs, memory, args) -> memory.setMemVal(regs.getHL(), regs.getL()));
        setOpCode(std_opcodes, "LD (HL),n", 0x36, 12, 1, (regs, memory, args) -> memory.setMemVal(regs.getHL(), args[0]));    // Maybe
        setOpCode(std_opcodes, "LD (HL-),A", 0x32, 8, (regs, memory, args) -> {
            memory.setMemVal(regs.getHL(), regs.getA());
            regs.setHL(Commands.dec(regs.getHL()));
        });
        setOpCode(std_opcodes, "LD (HL+),A", 0x22, 8, (regs, memory, args) -> {
            memory.setMemVal(regs.getHL(), regs.getA());
            regs.setHL(Commands.inc(regs.getHL()));
        });
//


        /*
         * 16-bit Loads
         */
        // See GameBoy.CPU book for flags
        // Put SP + n effective address into HL
        setOpCode(std_opcodes, "LDHL SP,n", 0xF8, 12, 1, (regs, memory, args) -> Commands.ldhl(regs, args[0]));
        setOpCode(std_opcodes, "LD HL,nn", 0x21, 12, 2, (regs, memory, args) -> regs.setHL((short) (((args[1] << 8) & 0xFF00) + ((0xFF & args[0])))));

        // LD INTO BC
        setOpCode(std_opcodes, "LD BC,nn", 0x01, 12, 2, (regs, memory, args) -> regs.setBC((short) (((args[1] << 8) & 0xFF00) + ((0xFF & args[0])))));
        setOpCode(std_opcodes, "LD BC,A", 0x02, 8, (regs, memory, args) -> regs.setBC(regs.getA()));

        // LD INTO DE
        setOpCode(std_opcodes, "LD DE,nn", 0x11, 12, 2, (regs, memory, args) -> regs.setDE((short) (((args[1] << 8) & 0xFF00) + ((0xFF & args[0])))));
        setOpCode(std_opcodes, "LD DE,A", 0x12, 8, (regs, memory, args) -> regs.setDE(regs.getA()));

        // LD INTO SP
        setOpCode(std_opcodes, "LD SP,nn", 0x31, 12, 2, (regs, memory, args) -> regs.setSP((short) (((args[1] << 8) & 0xFF00) + ((0xFF & args[0])))));
        setOpCode(std_opcodes, "LD SP,HL", 0xF9, 8, (regs, memory, args) -> regs.setSP(regs.getHL()));

        setOpCode(std_opcodes, "LD (NN),A", 0xEA, 8, 2, (regs, memory, args) -> memory.setMemVal((short) (((args[1] << 8) & 0xFF00) + ((0xFF & args[0]))), regs.getA()));
        setOpCode(std_opcodes, "LD (NN),SP", 0x08, 20, 2, (regs, memory, args) -> memory.setMemVal((short) (((args[1] << 8) & 0xFF00) + ((0xFF & args[0]))), regs.getSP()));

        // Put A into $FF00 + n
        setOpCode(std_opcodes, "LD ($FF00+n),A", 0xE0, 12, 1, (regs, memory, args) -> memory.setMemVal((short) (0xFF00 + args[0]), regs.getA()));


        // PUSH REGISTER PAIR ONTO STACK; DECREMENT SP
        setOpCode(std_opcodes, "PUSH AF", 0xF5, 16, (regs, memory, args) -> regs.setSP(memory.push(regs, regs.getSP(), regs.getAF())));
        setOpCode(std_opcodes, "PUSH BC", 0xC5, 16, (regs, memory, args) -> regs.setSP(memory.push(regs, regs.getSP(), regs.getBC())));
        setOpCode(std_opcodes, "PUSH DE", 0xD5, 16, (regs, memory, args) -> regs.setSP(memory.push(regs, regs.getSP(), regs.getDE())));
        setOpCode(std_opcodes, "PUSH HL", 0xE5, 16, (regs, memory, args) -> regs.setSP(memory.push(regs, regs.getSP(), regs.getHL())));

        // POP OFF STACK AND STORE IN REGISTER PAIR ; INCREMENT SP
        setOpCode(std_opcodes, "POP AF", 0xF1, 12, (regs, memory, args) -> regs.setAF(memory.pop(regs, regs.getSP())));
        setOpCode(std_opcodes, "POP BC", 0xC1, 12, (regs, memory, args) -> regs.setBC(memory.pop(regs, regs.getSP())));
        setOpCode(std_opcodes, "POP DE", 0xD1, 12, (regs, memory, args) -> regs.setDE(memory.pop(regs, regs.getSP())));
        setOpCode(std_opcodes, "POP HL", 0xE1, 12, (regs, memory, args) -> regs.setHL(memory.pop(regs, regs.getSP())));


        /*
         * 8-Bit ALU
         */
        // ADD TO A; FLAGS AFFECTED;
        setOpCode(std_opcodes, "ADD A,A", 0x87, 4, (regs, memory, args) -> Commands.addToA(regs, regs.getA()));
        setOpCode(std_opcodes, "ADD A,B", 0x80, 4, (regs, memory, args) -> Commands.addToA(regs, regs.getB()));
        setOpCode(std_opcodes, "ADD A,C", 0x81, 4, (regs, memory, args) -> Commands.addToA(regs, regs.getC()));
        setOpCode(std_opcodes, "ADD A,D", 0x82, 4, (regs, memory, args) -> Commands.addToA(regs, regs.getD()));
        setOpCode(std_opcodes, "ADD A,E", 0x83, 4, (regs, memory, args) -> Commands.addToA(regs, regs.getE()));
        setOpCode(std_opcodes, "ADD A,H", 0x84, 4, (regs, memory, args) -> Commands.addToA(regs, regs.getH()));
        setOpCode(std_opcodes, "ADD A,L", 0x85, 4, (regs, memory, args) -> Commands.addToA(regs, regs.getL()));
        setOpCode(std_opcodes, "ADD A,(HL)", 0x86, 8, (regs, memory, args) -> Commands.addToA(regs, memory.getMemVal(regs.getHL())));
        setOpCode(std_opcodes, "ADD A,n", 0xC6, 8, 1, (regs, memory, args) -> Commands.addToA(regs, args[0]));

        // Add register + carry flag to A; FLAGS AFFECTED
        setOpCode(std_opcodes, "ADD A,A", 0x8F, 4, (regs, memory, args) -> Commands.addToA(regs, (byte) (regs.getA() + regs.getCFlag())));
        setOpCode(std_opcodes, "ADD A,B", 0x88, 4, (regs, memory, args) -> Commands.addToA(regs, (byte) (regs.getB() + regs.getCFlag())));
        setOpCode(std_opcodes, "ADD A,C", 0x89, 4, (regs, memory, args) -> Commands.addToA(regs, (byte) (regs.getC() + regs.getCFlag())));
        setOpCode(std_opcodes, "ADD A,D", 0x8A, 4, (regs, memory, args) -> Commands.addToA(regs, (byte) (regs.getD() + regs.getCFlag())));
        setOpCode(std_opcodes, "ADD A,E", 0x8B, 4, (regs, memory, args) -> Commands.addToA(regs, (byte) (regs.getE() + regs.getCFlag())));
        setOpCode(std_opcodes, "ADD A,H", 0x8C, 4, (regs, memory, args) -> Commands.addToA(regs, (byte) (regs.getH() + regs.getCFlag())));
        setOpCode(std_opcodes, "ADD A,L", 0x8D, 4, (regs, memory, args) -> Commands.addToA(regs, (byte) (regs.getL() + regs.getCFlag())));
        setOpCode(std_opcodes, "ADD A,(HL)", 0x8E, 8, (regs, memory, args) -> Commands.addToA(regs, (byte) (memory.getMemVal(regs.getHL()) + regs.getCFlag())));
        setOpCode(std_opcodes, "ADD A,n", 0xCE, 8, 1, (regs, memory, args) -> Commands.addToA(regs, (byte) (args[0] + regs.getCFlag())));

        // SUBTRACT N FROM A; FLAGS AFFECTED
        setOpCode(std_opcodes, "SUB A", 0x97, 4, (regs, memory, args) -> Commands.sub(regs, regs.getA()));
        setOpCode(std_opcodes, "SUB B", 0x90, 4, (regs, memory, args) -> Commands.sub(regs, regs.getB()));
        setOpCode(std_opcodes, "SUB C", 0x91, 4, (regs, memory, args) -> Commands.sub(regs, regs.getC()));
        setOpCode(std_opcodes, "SUB D", 0x92, 4, (regs, memory, args) -> Commands.sub(regs, regs.getD()));
        setOpCode(std_opcodes, "SUB E", 0x93, 4, (regs, memory, args) -> Commands.sub(regs, regs.getE()));
        setOpCode(std_opcodes, "SUB H", 0x94, 4, (regs, memory, args) -> Commands.sub(regs, regs.getH()));
        setOpCode(std_opcodes, "SUB L", 0x95, 4, (regs, memory, args) -> Commands.sub(regs, regs.getL()));
        setOpCode(std_opcodes, "SUB (HL)", 0x96, 8, (regs, memory, args) -> Commands.sub(regs, memory.getMemVal(regs.getHL())));
        setOpCode(std_opcodes, "SUB n", 0xD6, 8, 1, (regs, memory, args) -> Commands.sub(regs, args[0]));

        // SUBTRACT (N - CARRY FLAG) FROM A
        setOpCode(std_opcodes, "SBC A,A", 0x9F, 4, (regs, memory, args) -> Commands.sub(regs, (byte) (regs.getA() - regs.getCFlag())));
        setOpCode(std_opcodes, "SBC A,B", 0x98, 4, (regs, memory, args) -> Commands.sub(regs, (byte) (regs.getB() - regs.getCFlag())));
        setOpCode(std_opcodes, "SBC A,C", 0x99, 4, (regs, memory, args) -> Commands.sub(regs, (byte) (regs.getC() - regs.getCFlag())));
        setOpCode(std_opcodes, "SBC A,D", 0x9A, 4, (regs, memory, args) -> Commands.sub(regs, (byte) (regs.getD() - regs.getCFlag())));
        setOpCode(std_opcodes, "SBC A,E", 0x9B, 4, (regs, memory, args) -> Commands.sub(regs, (byte) (regs.getE() - regs.getCFlag())));
        setOpCode(std_opcodes, "SBC A,H", 0x9C, 4, (regs, memory, args) -> Commands.sub(regs, (byte) (regs.getH() - regs.getCFlag())));
        setOpCode(std_opcodes, "SBC A,L", 0x9D, 4, (regs, memory, args) -> Commands.sub(regs, (byte) (regs.getL() - regs.getCFlag())));
        setOpCode(std_opcodes, "SBC A,(HL)", 0x9E, 8, (regs, memory, args) -> Commands.sub(regs, (byte) (memory.getMemVal(regs.getHL()) - regs.getCFlag())));
        setOpCode(std_opcodes, "SBC A,n", 0xD8, 8, 1, (regs, memory, args) -> Commands.sub(regs, (byte) (args[0] - regs.getCFlag())));

        // LOGICAL AND A & N STORED IN A; FLAGS AFFECTED
        setOpCode(std_opcodes, "AND A", 0xA7, 4, (regs, memory, args) -> Commands.AND(regs, regs.getA()));
        setOpCode(std_opcodes, "AND B", 0xA0, 4, (regs, memory, args) -> Commands.AND(regs, regs.getB()));
        setOpCode(std_opcodes, "AND C", 0xA1, 4, (regs, memory, args) -> Commands.AND(regs, regs.getC()));
        setOpCode(std_opcodes, "AND D", 0xA2, 4, (regs, memory, args) -> Commands.AND(regs, regs.getD()));
        setOpCode(std_opcodes, "AND E", 0xA3, 4, (regs, memory, args) -> Commands.AND(regs, regs.getE()));
        setOpCode(std_opcodes, "AND H", 0xA4, 4, (regs, memory, args) -> Commands.AND(regs, regs.getH()));
        setOpCode(std_opcodes, "AND L", 0xA5, 4, (regs, memory, args) -> Commands.AND(regs, regs.getL()));
        setOpCode(std_opcodes, "AND (HL)", 0xA6, 8, (regs, memory, args) -> Commands.AND(regs, memory.getMemVal(regs.getHL())));
        setOpCode(std_opcodes, "AND n", 0xE6, 8, 1, (regs, memory, args) -> Commands.AND(regs, args[0]));

        // LOGICAL OR A & N STORED IN A; FLAG AFFECTED
        setOpCode(std_opcodes, "OR A", 0xB7, 4, (regs, memory, args) -> Commands.OR(regs, regs.getA()));
        setOpCode(std_opcodes, "OR B", 0xB0, 4, (regs, memory, args) -> Commands.OR(regs, regs.getB()));
        setOpCode(std_opcodes, "OR C", 0xB1, 4, (regs, memory, args) -> Commands.OR(regs, regs.getC()));
        setOpCode(std_opcodes, "OR D", 0xB2, 4, (regs, memory, args) -> Commands.OR(regs, regs.getD()));
        setOpCode(std_opcodes, "OR E", 0xB3, 4, (regs, memory, args) -> Commands.OR(regs, regs.getE()));
        setOpCode(std_opcodes, "OR H", 0xB4, 4, (regs, memory, args) -> Commands.OR(regs, regs.getH()));
        setOpCode(std_opcodes, "OR L", 0xB5, 4, (regs, memory, args) -> Commands.OR(regs, regs.getL()));
        setOpCode(std_opcodes, "OR (HL)", 0xB6, 8, (regs, memory, args) -> Commands.OR(regs, memory.getMemVal(regs.getHL())));
        setOpCode(std_opcodes, "OR n", 0xF6, 8, 1, (regs, memory, args) -> Commands.OR(regs, args[0]));

        // LOGICAL OR A & N STORED IN A; FLAG AFFECTED
        setOpCode(std_opcodes, "XOR A", 0xAF, 4, (regs, memory, args) -> Commands.XOR(regs, regs.getA()));
        setOpCode(std_opcodes, "XOR B", 0xA8, 4, (regs, memory, args) -> Commands.XOR(regs, regs.getB()));
        setOpCode(std_opcodes, "XOR C", 0xA9, 4, (regs, memory, args) -> Commands.XOR(regs, regs.getC()));
        setOpCode(std_opcodes, "XOR D", 0xAA, 4, (regs, memory, args) -> Commands.XOR(regs, regs.getD()));
        setOpCode(std_opcodes, "XOR E", 0xAB, 4, (regs, memory, args) -> Commands.XOR(regs, regs.getE()));
        setOpCode(std_opcodes, "XOR H", 0xAC, 4, (regs, memory, args) -> Commands.XOR(regs, regs.getH()));
        setOpCode(std_opcodes, "XOR L", 0xAD, 4, (regs, memory, args) -> Commands.XOR(regs, regs.getL()));
        setOpCode(std_opcodes, "XOR (HL)", 0xAE, 8, (regs, memory, args) -> Commands.XOR(regs, memory.getMemVal(regs.getHL())));
        setOpCode(std_opcodes, "XOR n", 0xAE, 8, 1, (regs, memory, args) -> Commands.XOR(regs, (args[0])));

        // COMPARE A with N. BASICALLY AN A - N SUBTRACTION, WITH THE RESULTS THROWN AWAY; FLAGS AFFECTED.
        setOpCode(std_opcodes, "CP A", 0xBF, 4, (regs, memory, args) -> Commands.cp(regs, regs.getA()));
        setOpCode(std_opcodes, "CP B", 0xB8, 4, (regs, memory, args) -> Commands.cp(regs, regs.getA()));
        setOpCode(std_opcodes, "CP C", 0xB9, 4, (regs, memory, args) -> Commands.cp(regs, regs.getA()));
        setOpCode(std_opcodes, "CP D", 0xBA, 4, (regs, memory, args) -> Commands.cp(regs, regs.getA()));
        setOpCode(std_opcodes, "CP E", 0xBB, 4, (regs, memory, args) -> Commands.cp(regs, regs.getA()));
        setOpCode(std_opcodes, "CP H", 0xBC, 4, (regs, memory, args) -> Commands.cp(regs, regs.getA()));
        setOpCode(std_opcodes, "CP L", 0xBD, 4, (regs, memory, args) -> Commands.cp(regs, regs.getA()));
        setOpCode(std_opcodes, "CP (HL)", 0xBE, 8, (regs, memory, args) -> Commands.cp(regs, memory.getMemVal(regs.getHL())));
        setOpCode(std_opcodes, "CP n", 0xFE, 8, 1, (regs, memory, args) -> Commands.cp(regs, memory.getMemVal(args[0])));

        // INCREMENT REGISTER N; FLAGS AFFECTED
        setOpCode(std_opcodes, "INC A", 0x3C, 4, (regs, memory, args) -> regs.setA(Commands.inc(regs, regs.getA())));
        setOpCode(std_opcodes, "INC B", 0x04, 4, (regs, memory, args) -> regs.setB(Commands.inc(regs, regs.getB())));
        setOpCode(std_opcodes, "INC C", 0x0C, 4, (regs, memory, args) -> regs.setC(Commands.inc(regs, regs.getC())));
        setOpCode(std_opcodes, "INC D", 0x14, 4, (regs, memory, args) -> regs.setD(Commands.inc(regs, regs.getD())));
        setOpCode(std_opcodes, "INC E", 0x1C, 4, (regs, memory, args) -> regs.setE(Commands.inc(regs, regs.getE())));
        setOpCode(std_opcodes, "INC H", 0x24, 4, (regs, memory, args) -> regs.setH(Commands.inc(regs, regs.getH())));
        setOpCode(std_opcodes, "INC L", 0x2C, 4, (regs, memory, args) -> regs.setL(Commands.inc(regs, regs.getL())));
        setOpCode(std_opcodes, "INC (HL)", 0x34, 12, (regs, memory, args) -> memory.setMemVal(regs.getHL(), Commands.inc(regs, memory.getMemVal(regs.getHL()))));

        // DECREMENT REGISTER N; FLAGS AFFECTED
        setOpCode(std_opcodes, "DEC A", 0x3D, 4, (regs, memory, args) -> regs.setA(Commands.dec(regs, regs.getA())));
        setOpCode(std_opcodes, "DEC B", 0x05, 4, (regs, memory, args) -> regs.setA(Commands.dec(regs, regs.getA())));
        setOpCode(std_opcodes, "DEC C", 0x0D, 4, (regs, memory, args) -> regs.setA(Commands.dec(regs, regs.getA())));
        setOpCode(std_opcodes, "DEC D", 0x15, 4, (regs, memory, args) -> regs.setA(Commands.dec(regs, regs.getA())));
        setOpCode(std_opcodes, "DEC E", 0x1D, 4, (regs, memory, args) -> regs.setA(Commands.dec(regs, regs.getA())));
        setOpCode(std_opcodes, "DEC H", 0x25, 4, (regs, memory, args) -> regs.setA(Commands.dec(regs, regs.getA())));
        setOpCode(std_opcodes, "DEC L", 0x2D, 4, (regs, memory, args) -> regs.setA(Commands.dec(regs, regs.getA())));
        setOpCode(std_opcodes, "DEC (HL)", 0x35, 12, (regs, memory, args) -> memory.setMemVal(regs.getHL(), Commands.dec(regs, memory.getMemVal(regs.getHL()))));


        /*
         * 16-Bit Arithmetic
         */

        // ADD TO HL
        setOpCode(std_opcodes, "ADD HL,BC", 0x09, 8, (regs, memory, args) -> regs.setHL(Commands.add(regs, regs.getHL(), regs.getBC())));
        setOpCode(std_opcodes, "ADD HL,DE", 0x19, 8, (regs, memory, args) -> regs.setHL(Commands.add(regs, regs.getHL(), regs.getBC())));
        setOpCode(std_opcodes, "ADD HL,HL", 0x29, 8, (regs, memory, args) -> regs.setHL(Commands.add(regs, regs.getHL(), regs.getBC())));
        setOpCode(std_opcodes, "ADD HL,SP", 0x39, 8, (regs, memory, args) -> regs.setHL(Commands.add(regs, regs.getHL(), regs.getBC())));

        // ADD TO STACK POINTER
        setOpCode(std_opcodes, "ADD SP,n", 0xE8, 16, (regs, memory, args) -> regs.setSP((short) (regs.getSP() + args[0])));

        // INCREMENT REGISTER PAIR
        setOpCode(std_opcodes, "INC BC", 0x03, 8, (regs, memory, args) -> regs.setBC(Commands.inc(regs.getBC())));
        setOpCode(std_opcodes, "INC DE", 0x13, 8, (regs, memory, args) -> regs.setDE(Commands.inc(regs.getDE())));
        setOpCode(std_opcodes, "INC HL", 0x23, 8, (regs, memory, args) -> regs.setHL(Commands.inc(regs.getHL())));
        setOpCode(std_opcodes, "INC SP", 0x33, 8, (regs, memory, args) -> regs.setSP(Commands.inc(regs.getSP())));

        //  DECREMENT REGISTER PAIR
        setOpCode(std_opcodes, "DEC BC", 0x0B, 8, (regs, memory, args) -> regs.setBC(Commands.dec(regs.getBC())));
        setOpCode(std_opcodes, "DEC DE", 0x1B, 8, (regs, memory, args) -> regs.setDE(Commands.dec(regs.getDE())));
        setOpCode(std_opcodes, "DEC HL", 0x2B, 8, (regs, memory, args) -> regs.setHL(Commands.dec(regs.getHL())));
        setOpCode(std_opcodes, "DEC SP", 0x3B, 8, (regs, memory, args) -> regs.setSP(Commands.dec(regs.getSP())));


        /*
         * Misc.
         */
        // Swap upper and lower nibbles of n
        setOpCode(cb_opcodes, "SWAP A", 0x37, 8, (regs, memory, args) -> regs.setA(Commands.swap(regs.getA())));
        setOpCode(cb_opcodes, "SWAP B", 0x30, 8, (regs, memory, args) -> regs.setB(Commands.swap(regs.getB())));
        setOpCode(cb_opcodes, "SWAP C", 0x31, 8, (regs, memory, args) -> regs.setC(Commands.swap(regs.getC())));
        setOpCode(cb_opcodes, "SWAP D", 0x32, 8, (regs, memory, args) -> regs.setD(Commands.swap(regs.getD())));
        setOpCode(cb_opcodes, "SWAP E", 0x33, 8, (regs, memory, args) -> regs.setE(Commands.swap(regs.getE())));
        setOpCode(cb_opcodes, "SWAP H", 0x34, 8, (regs, memory, args) -> regs.setH(Commands.swap(regs.getH())));
        setOpCode(cb_opcodes, "SWAP L", 0x35, 8, (regs, memory, args) -> regs.setL(Commands.swap(regs.getL())));
        setOpCode(cb_opcodes, "SWAP (HL)", 0x36, 16, (regs, memory, args) -> memory.setMemVal(regs.getHL(), Commands.swap(memory.getMemVal(regs.getHL()))));

        // Decimal adjust register A
//        setOpCode(std_opcodes, "DAA", 27, 4, (regs, memory, args) -> regs.daa());

        // Complement A register
        setOpCode(std_opcodes, "CPL", 0x2F, 4, (regs, memory, args) -> Commands.cpl(regs));

        // Complement carry flag
        setOpCode(std_opcodes, "CCF", 0x3F, 4, (regs, memory, args) -> Commands.ccf(regs));

        // Set carry flag
        setOpCode(std_opcodes, "SCF", 0x37, 4, (regs, memory, args) -> {
            regs.setCFlag();
            regs.clearNFlag();
            regs.clearHFlag();
            regs.setCFlag();
        });

        setOpCode(std_opcodes, "NOP", 0x00, 4, (regs, memory, args) -> Commands.nop());
//        setOpCode(std_opcodes, "HALT", 76, 4, (regs, memory, args) -> regs.halt());
//        setOpCode(std_opcodes, "STOP", 0x1000, 4, (regs, memory, args) -> regs.stop());

        // Disable Interrupt
//        setOpCode(std_opcodes, "DI", 0xF3, 4, (regs, memory, args) -> regs.disableInterrupts());

        // Enable Interrupts
//        setOpCode(std_opcodes, "DI", 0xFB, 4, (regs, memory, args) -> regs.enableInterrupts());


        /*
         * Rotates & Shifts
         */
        // Rotate A left. Old bit 7 to Carry flag. FLAGS AFFECTED
        setOpCode(std_opcodes, "RLCA", 0x07, 4, (regs, memory, args) -> regs.setA(Commands.rlc(regs, regs.getA())));

        // Rotate A left through Carry flag.
        setOpCode(std_opcodes, "RLA", 0x17, 4, (regs, memory, args) -> regs.setA(Commands.rl(regs, regs.getA())));

        // Rotate A right through Old 0 bit to Carry flag.
        setOpCode(std_opcodes, "RRCA", 0x0F, 4, (regs, memory, args) -> regs.setA(Commands.rrc(regs, regs.getA())));

        // Rotate A right through Carry flag.
        setOpCode(std_opcodes, "RRA", 0x1F, 4, (regs, memory, args) -> regs.setA(Commands.rr(regs, regs.getA())));

        // Rotate n left. Old bit 7 to carry flag. Flag affected
        setOpCode(cb_opcodes, "RLC A", 0x07, 8, (regs, memory, args) -> regs.setA(Commands.rlc(regs, regs.getA())));
        setOpCode(cb_opcodes, "RLC B", 0x00, 8, (regs, memory, args) -> regs.setB(Commands.rlc(regs, regs.getB())));
        setOpCode(cb_opcodes, "RLC C", 0x01, 8, (regs, memory, args) -> regs.setC(Commands.rlc(regs, regs.getC())));
        setOpCode(cb_opcodes, "RLC D", 0x02, 8, (regs, memory, args) -> regs.setD(Commands.rlc(regs, regs.getD())));
        setOpCode(cb_opcodes, "RLC E", 0x03, 8, (regs, memory, args) -> regs.setE(Commands.rlc(regs, regs.getE())));
        setOpCode(cb_opcodes, "RLC H", 0x04, 8, (regs, memory, args) -> regs.setH(Commands.rlc(regs, regs.getH())));
        setOpCode(cb_opcodes, "RLC L", 0x05, 8, (regs, memory, args) -> regs.setL(Commands.rlc(regs, regs.getL())));
        setOpCode(cb_opcodes, "RLC (HL)", 0x06, 16, (regs, memory, args) -> memory.setMemVal(regs.getHL(), Commands.rlc(regs, memory.getMemVal(regs.getHL()))));

        // Rotate n left through carry flag. Flag affected
        setOpCode(cb_opcodes, "RL A", 0x17, 8, (regs, memory, args) -> regs.setA(Commands.rl(regs, regs.getA())));
        setOpCode(cb_opcodes, "RL B", 0x10, 8, (regs, memory, args) -> regs.setB(Commands.rl(regs, regs.getB())));
        setOpCode(cb_opcodes, "RL C", 0x11, 8, (regs, memory, args) -> regs.setC(Commands.rl(regs, regs.getC())));
        setOpCode(cb_opcodes, "RL D", 0x12, 8, (regs, memory, args) -> regs.setD(Commands.rl(regs, regs.getD())));
        setOpCode(cb_opcodes, "RL E", 0x13, 8, (regs, memory, args) -> regs.setE(Commands.rl(regs, regs.getE())));
        setOpCode(cb_opcodes, "RL H", 0x14, 8, (regs, memory, args) -> regs.setH(Commands.rl(regs, regs.getH())));
        setOpCode(cb_opcodes, "RL L", 0x15, 8, (regs, memory, args) -> regs.setL(Commands.rl(regs, regs.getL())));
        setOpCode(cb_opcodes, "RL (HL)", 0x16, 16, (regs, memory, args) -> memory.setMemVal(regs.getHL(), Commands.rl(regs, memory.getMemVal(regs.getHL()))));

        // Rotate n right. Old bit 0 to Carry flag. Flag affected
        setOpCode(cb_opcodes, "RRC A", 0x0F, 8, (regs, memory, args) -> regs.setA(Commands.rrc(regs, regs.getA())));
        setOpCode(cb_opcodes, "RRC B", 0x08, 8, (regs, memory, args) -> regs.setB(Commands.rrc(regs, regs.getB())));
        setOpCode(cb_opcodes, "RRC C", 0x09, 8, (regs, memory, args) -> regs.setC(Commands.rrc(regs, regs.getC())));
        setOpCode(cb_opcodes, "RRC D", 0x0A, 8, (regs, memory, args) -> regs.setD(Commands.rrc(regs, regs.getD())));
        setOpCode(cb_opcodes, "RRC E", 0x0B, 8, (regs, memory, args) -> regs.setE(Commands.rrc(regs, regs.getE())));
        setOpCode(cb_opcodes, "RRC H", 0x0C, 8, (regs, memory, args) -> regs.setH(Commands.rrc(regs, regs.getH())));
        setOpCode(cb_opcodes, "RRC L", 0x0D, 8, (regs, memory, args) -> regs.setL(Commands.rrc(regs, regs.getL())));
        setOpCode(cb_opcodes, "RRC (HL)", 0x0E, 16, (regs, memory, args) -> memory.setMemVal(regs.getHL(), Commands.rrc(regs, memory.getMemVal(regs.getHL()))));

        // Rotate n right through  Carry flag. GameBoy.Flags affected
        setOpCode(cb_opcodes, "RR A", 0x1F, 8, (regs, memory, args) -> regs.setA(Commands.rr(regs, regs.getA())));
        setOpCode(cb_opcodes, "RR B", 0x18, 8, (regs, memory, args) -> regs.setB(Commands.rr(regs, regs.getB())));
        setOpCode(cb_opcodes, "RR C", 0x19, 8, (regs, memory, args) -> regs.setC(Commands.rr(regs, regs.getC())));
        setOpCode(cb_opcodes, "RR D", 0x1A, 8, (regs, memory, args) -> regs.setD(Commands.rr(regs, regs.getD())));
        setOpCode(cb_opcodes, "RR E", 0x1B, 8, (regs, memory, args) -> regs.setE(Commands.rr(regs, regs.getE())));
        setOpCode(cb_opcodes, "RR H", 0x1C, 8, (regs, memory, args) -> regs.setH(Commands.rr(regs, regs.getH())));
        setOpCode(cb_opcodes, "RR L", 0x1D, 8, (regs, memory, args) -> regs.setL(Commands.rr(regs, regs.getL())));
        setOpCode(cb_opcodes, "RR (HL)", 0x1E, 16, (regs, memory, args) -> memory.setMemVal(regs.getHL(), Commands.rr(regs, memory.getMemVal(regs.getHL()))));

        // Shift n left into Carry. LSB of n set to 0. FLAGS AFFECTED
        setOpCode(cb_opcodes, "SLA A", 0x27, 8, (regs, memory, args) -> regs.setA(Commands.sla(regs, regs.getA())));
        setOpCode(cb_opcodes, "SLA B", 0x20, 8, (regs, memory, args) -> regs.setB(Commands.sla(regs, regs.getB())));
        setOpCode(cb_opcodes, "SLA C", 0x21, 8, (regs, memory, args) -> regs.setC(Commands.sla(regs, regs.getC())));
        setOpCode(cb_opcodes, "SLA D", 0x22, 8, (regs, memory, args) -> regs.setD(Commands.sla(regs, regs.getD())));
        setOpCode(cb_opcodes, "SLA E", 0x23, 8, (regs, memory, args) -> regs.setE(Commands.sla(regs, regs.getE())));
        setOpCode(cb_opcodes, "SLA H", 0x24, 8, (regs, memory, args) -> regs.setH(Commands.sla(regs, regs.getH())));
        setOpCode(cb_opcodes, "SLA L", 0x25, 8, (regs, memory, args) -> regs.setL(Commands.sla(regs, regs.getL())));
        setOpCode(cb_opcodes, "SLA (HL)", 0x26, 16, (regs, memory, args) -> memory.setMemVal(regs.getHL(), Commands.sla(regs, memory.getMemVal(regs.getHL()))));

        // Shift n right into Carry. MSB doesn't change.
        setOpCode(cb_opcodes, "SRA A", 0x2F, 8, (regs, memory, args) -> regs.setA(Commands.sra(regs, regs.getA())));
        setOpCode(cb_opcodes, "SRA B", 0x28, 8, (regs, memory, args) -> regs.setB(Commands.sra(regs, regs.getB())));
        setOpCode(cb_opcodes, "SRA C", 0x29, 8, (regs, memory, args) -> regs.setC(Commands.sra(regs, regs.getC())));
        setOpCode(cb_opcodes, "SRA D", 0x2A, 8, (regs, memory, args) -> regs.setD(Commands.sra(regs, regs.getD())));
        setOpCode(cb_opcodes, "SRA E", 0x2B, 8, (regs, memory, args) -> regs.setE(Commands.sra(regs, regs.getE())));
        setOpCode(cb_opcodes, "SRA H", 0x2C, 8, (regs, memory, args) -> regs.setH(Commands.sra(regs, regs.getH())));
        setOpCode(cb_opcodes, "SRA L", 0x2D, 8, (regs, memory, args) -> regs.setL(Commands.sra(regs, regs.getL())));
        setOpCode(cb_opcodes, "SRA (HL)", 0x2E, 16, (regs, memory, args) -> memory.setMemVal(regs.getHL(), Commands.sra(regs, memory.getMemVal(regs.getHL()))));

        // Shift n right into Carry. MSB set to 0.
        setOpCode(cb_opcodes, "SRL A", 0x3F, 8, (regs, memory, args) -> regs.setA(Commands.srl(regs, regs.getA())));
        setOpCode(cb_opcodes, "SRL B", 0x38, 8, (regs, memory, args) -> regs.setB(Commands.srl(regs, regs.getB())));
        setOpCode(cb_opcodes, "SRL C", 0x39, 8, (regs, memory, args) -> regs.setC(Commands.srl(regs, regs.getC())));
        setOpCode(cb_opcodes, "SRL D", 0x3A, 8, (regs, memory, args) -> regs.setD(Commands.srl(regs, regs.getD())));
        setOpCode(cb_opcodes, "SRL E", 0x3B, 8, (regs, memory, args) -> regs.setE(Commands.srl(regs, regs.getE())));
        setOpCode(cb_opcodes, "SRL H", 0x3C, 8, (regs, memory, args) -> regs.setH(Commands.srl(regs, regs.getH())));
        setOpCode(cb_opcodes, "SRL L", 0x3D, 8, (regs, memory, args) -> regs.setL(Commands.srl(regs, regs.getL())));
        setOpCode(cb_opcodes, "SRL (HL)", 0x3E, 16, (regs, memory, args) -> memory.setMemVal(regs.getHL(), Commands.srl(regs, memory.getMemVal(regs.getHL()))));


        /*
         * Bit GameBoy.Opcodes
         */
        // Test bit b in register r. GameBoy.Flags affected
        for (byte b = 0; b < 8; b++) {
            final byte bit = b;
            setOpCode(cb_opcodes, "BIT " + b + ",A", 0x47 + (8 * b), 8, (regs, memory, args) -> Commands.testBit(regs, regs.getA(), bit));
            setOpCode(cb_opcodes, "BIT " + b + ",B", 0x40 + (8 * b), 8, (regs, memory, args) -> Commands.testBit(regs, regs.getB(), bit));
            setOpCode(cb_opcodes, "BIT " + b + ",C", 0x41 + (8 * b), 8, (regs, memory, args) -> Commands.testBit(regs, regs.getC(), bit));
            setOpCode(cb_opcodes, "BIT " + b + ",D", 0x42 + (8 * b), 8, (regs, memory, args) -> Commands.testBit(regs, regs.getD(), bit));
            setOpCode(cb_opcodes, "BIT " + b + ",E", 0x43 + (8 * b), 8, (regs, memory, args) -> Commands.testBit(regs, regs.getE(), bit));
            setOpCode(cb_opcodes, "BIT " + b + ",H", 0x44 + (8 * b), 8, (regs, memory, args) -> Commands.testBit(regs, regs.getH(), bit));
            setOpCode(cb_opcodes, "BIT " + b + ",L", 0x45 + (8 * b), 8, (regs, memory, args) -> Commands.testBit(regs, regs.getL(), bit));
            setOpCode(cb_opcodes, "BIT " + b + ",(HL)", 0x46 + (8 * b), 16, (regs, memory, args) -> Commands.testBit(regs, memory.getMemVal(regs.getHL()), bit));
        }

        // Set bit b in register r.
        for (byte b = 0; b < 8; b++) {
            final byte bit = b;
            setOpCode(cb_opcodes, "SET " + b + ",A", 0xC7 + (8 * b), 8, 1, (regs, memory, args) -> regs.setA(regs.setBit(regs.getA(), bit)));
            setOpCode(cb_opcodes, "SET " + b + ",B", 0xC0 + (8 * b), 8, 1, (regs, memory, args) -> regs.setB(regs.setBit(regs.getB(), bit)));
            setOpCode(cb_opcodes, "SET " + b + ",C", 0xC1 + (8 * b), 8, 1, (regs, memory, args) -> regs.setC(regs.setBit(regs.getC(), bit)));
            setOpCode(cb_opcodes, "SET " + b + ",D", 0xC2 + (8 * b), 8, 1, (regs, memory, args) -> regs.setD(regs.setBit(regs.getD(), bit)));
            setOpCode(cb_opcodes, "SET " + b + ",E", 0xC3 + (8 * b), 8, 1, (regs, memory, args) -> regs.setE(regs.setBit(regs.getE(), bit)));
            setOpCode(cb_opcodes, "SET " + b + ",H", 0xC4 + (8 * b), 8, 1, (regs, memory, args) -> regs.setH(regs.setBit(regs.getH(), bit)));
            setOpCode(cb_opcodes, "SET " + b + ",L", 0xC5 + (8 * b), 8, 1, (regs, memory, args) -> regs.setL(regs.setBit(regs.getL(), bit)));
            setOpCode(cb_opcodes, "SET " + b + ",(HL)", 0xC6 + (8 * b), 16, 1, (regs, memory, args) -> memory.setMemVal(regs.getHL(), regs.setBit(memory.getMemVal(regs.getHL()), bit)));
        }

        // RESET BIT B IN REGISTER r
        for (byte b = 0; b < 8; b++) {
            final byte bit = b;
            setOpCode(cb_opcodes, "RES " + b + ",A", 0x87 + (8 * b), 8, 1, (regs, memory, args) -> regs.setA(regs.clearBit(regs.getA(), bit)));
            setOpCode(cb_opcodes, "RES " + b + ",B", 0x80 + (8 * b), 8, 1, (regs, memory, args) -> regs.setB(regs.clearBit(regs.getB(), bit)));
            setOpCode(cb_opcodes, "RES " + b + ",C", 0x81 + (8 * b), 8, 1, (regs, memory, args) -> regs.setC(regs.clearBit(regs.getC(), bit)));
            setOpCode(cb_opcodes, "RES " + b + ",D", 0x82 + (8 * b), 8, 1, (regs, memory, args) -> regs.setD(regs.clearBit(regs.getD(), bit)));
            setOpCode(cb_opcodes, "RES " + b + ",E", 0x83 + (8 * b), 8, 1, (regs, memory, args) -> regs.setE(regs.clearBit(regs.getE(), bit)));
            setOpCode(cb_opcodes, "RES " + b + ",H", 0x84 + (8 * b), 8, 1, (regs, memory, args) -> regs.setH(regs.clearBit(regs.getH(), bit)));
            setOpCode(cb_opcodes, "RES " + b + ",L", 0x85 + (8 * b), 8, 1, (regs, memory, args) -> regs.setL(regs.clearBit(regs.getL(), bit)));
            setOpCode(cb_opcodes, "RES b,(HL)", 0x86 + (8 * b), 16, 1, (regs, memory, args) -> memory.setMemVal(regs.getHL(), regs.clearBit(memory.getMemVal(regs.getHL()), bit)));
        }


        /*
         * Jumps
         */
        //  Jump to address nn
        setOpCode(std_opcodes, "JP NN", 0xC3, 12, (regs, memory, args) -> regs.setPC((short) (((args[1] << 8) & 0xFF00) + (0xFF & args[0]))));


        // Jump to address n if following condition is true
        setOpCode(std_opcodes, "JP NZ,NN", 0xC2, 12, 2, (regs, memory, args) -> Commands.jpIf(regs, (short) (((args[1] << 8) & 0xFF00) + ((0xFF & args[0]))), "NZ"));
        setOpCode(std_opcodes, "JP Z,NN", 0xCA, 12, 2, (regs, memory, args) -> Commands.jpIf(regs, (short) (((args[1] << 8) & 0xFF00) + ((0xFF & args[0]))), "Z"));
        setOpCode(std_opcodes, "JP NC,NN", 0xD2, 12, 2, (regs, memory, args) -> Commands.jpIf(regs, (short) (((args[1] << 8) & 0xFF00) + ((0xFF & args[0]))), "NC"));
        setOpCode(std_opcodes, "JP C,NN", 0xDA, 12, 2, (regs, memory, args) -> Commands.jpIf(regs, (short) (((args[1] << 8) & 0xFF00) + ((0xFF & args[0]))), "C"));


        // JUMP TO ADDRESS HL
        setOpCode(std_opcodes, "JP HL", 0xE9, 4, (regs, memory, args) -> regs.setPC(regs.getHL()));

        // Add n to current address and jump to it
        setOpCode(std_opcodes, "JR n", 0x18, 8, 1, (regs, memory, args) -> regs.setPC((short) (regs.getPC() + (args[0] & 0xFF)))); // Fix function

//        // Conditional jump + add
        setOpCode(std_opcodes, "JR NZ, *", 0x20, 8, 1, (regs, memory, args) -> Commands.jrif(regs, (args[0]), "NZ"));
        setOpCode(std_opcodes, "JR Z,*", 0x28, 8, 1, (regs, memory, args) -> Commands.jrif(regs, (args[0]), "Z"));
        setOpCode(std_opcodes, "JR NC, *", 0x30, 8, 1, (regs, memory, args) -> Commands.jrif(regs, (args[0]), "NC"));
        setOpCode(std_opcodes, "JR C,*", 0x38, 8, 1, (regs, memory, args) -> Commands.jrif(regs, (args[0]), "C"));


        /*
         * Calls
         */
        // Push address of next instruction onto stack and then jump to address nn
        setOpCode(std_opcodes, "CALL nn", 0xCD, 12, (regs, memory, args) -> Commands.call(regs, memory, (short) (((args[1] << 8) & 0xFF00) + ((0xFF & args[0])))));
//
//        // Call adr if
//        setOpCode(std_opcodes, "CALL NZ,nn", 0xC4, 12, (regs, memory, args) -> regs.callIf((short)(((args[1] << 8) & 0xFF00) + ((0xFF & args[0])));
//        setOpCode(std_opcodes, "CALL Z,nn", 0xCC, 12, (regs, memory, args) -> regs.callIf((short)(((args[1] << 8) & 0xFF00) + ((0xFF & args[0])));
//        setOpCode(std_opcodes, "CALL NC,nn", 0xD4, 12, (regs, memory, args) -> regs.callIf((short)(((args[1] << 8) & 0xFF00) + ((0xFF & args[0])));
//        setOpCode(std_opcodes, "CALL C,nn", 0xDC, 12, (regs, memory, args) -> regs.callIf((short)(((args[1] << 8) & 0xFF00) + ((0xFF & args[0])));
//
//
//        /**
//         * Restarts
//         */
//        // Jump to address $0000 + n. 0x00, 0x08, ...
        setOpCode(std_opcodes, "RST 0x00", 0xC7, 32, (regs, memory, args) -> Commands.restart(regs, memory, (short) 0x00));
        setOpCode(std_opcodes, "RST 0x08", 0xCF, 32, (regs, memory, args) -> Commands.restart(regs, memory, (short) 0x08));
        setOpCode(std_opcodes, "RST 0x10", 0xD7, 32, (regs, memory, args) -> Commands.restart(regs, memory, (short) 0x10));
        setOpCode(std_opcodes, "RST 0x18", 0xDF, 32, (regs, memory, args) -> Commands.restart(regs, memory, (short) 0x18));
        setOpCode(std_opcodes, "RST 0x20", 0xE7, 32, (regs, memory, args) -> Commands.restart(regs, memory, (short) 0x20));
        setOpCode(std_opcodes, "RST 0x28", 0xEF, 32, (regs, memory, args) -> Commands.restart(regs, memory, (short) 0x28));
        setOpCode(std_opcodes, "RST 0x30", 0xF7, 32, (regs, memory, args) -> Commands.restart(regs, memory, (short) 0x30));
        setOpCode(std_opcodes, "RST 0x38", 0xFF, 32, (regs, memory, args) -> Commands.restart(regs, memory, (short) 0x38));


        /*
         * Returns
         */
        // Pop two bytes from stack and jump to that address
        setOpCode(std_opcodes, "RET", 0xC9, 8, (regs, memory, args) -> Commands.ret(regs, memory));
//
//        // Return if following condition is true
//        setOpCode(std_opcodes, "RET NZ", 0xC0, 8, (regs, memory, args) -> regs.popJmpIf(regs.getZFlag()));
//        setOpCode(std_opcodes, "RET Z", 0xC8, 8, (regs, memory, args) -> regs.popJmpIf(regs.getZFlag()));
//        setOpCode(std_opcodes, "RET NC", 0xD0, 8, (regs, memory, args) -> regs.popJmpIf(regs.getCFlag()));
//        setOpCode(std_opcodes, "RET C", 0xD8, 8, (regs, memory, args) -> regs.popJmpIf(regs.getCFlag()));
//
//        // Pop two bytes from stack and jump to that address then enable interrupts
//        setOpCode(std_opcodes, "RET", 0xD9, 8, (regs, memory, args) -> regs.reti());

    }

    private void setOpCode(Instructions[] opcodes, String label, int opcode, int clocks, Operation op) {
        opcodes[opcode] = new Instructions(label, opcode, clocks, op);
    }

    private void setOpCode(Instructions[] opcodes, String label, int opcode, int clocks, int numArgs, Operation op) {
        opcodes[opcode] = new Instructions(label, opcode, clocks, numArgs, op);
    }


    int execute(int opcode, Registers regs, Memory memory, byte[] args) {
        if (opcode < 0x100) {
            std_opcodes[0xFF & opcode].op.cmd(regs, memory, args);
            return std_opcodes[0xFF & opcode].cycles;
        } else {
            opcode &= 0xFF; // Remove the CB prefix
            cb_opcodes[0xFF & opcode].op.cmd(regs, memory, args);
            return cb_opcodes[0xFF & opcode].cycles;
        }
    }
}