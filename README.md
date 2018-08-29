# GameBoy-emu
A WIP GameBoy emulator.

## Some of my troubles and thoughts
### How to handle opcodes?
At the beginning of this project I had next to no idea of what an opcode was. Upon further research I found out that an opcode is just an instruction from an instruction set. So with that knowledge under my belt I had a bigger problem I asked myself: "How do I store a set of instructions?" Of course I could have used a bunch of switch statements, but that would be very ugly. I originally wanted to use some kind of array of function pointers, however Java doesn't have those. So I went searching and the closest thing I could find was a Functional Interface. The FunctionalInterface checked everybox I wanted. The FunctionalInterface allowed me to pass different functions as a parameter through the use of lambda expressions. This allowed me to create an array of objects which each had different methods.


E.g. FunctionalInterface
```
@FunctionalInterface
interface Operation {
    void cmd(Registers regs, int[] memory, int[] args);
}
...    

private void setOpcode(String label, int opcode, int clocks, Operation op) {
    opcodes[opcode] = new Instructions(label, opcode, clocks, op);
}

public int main(){
    setOpcode("LD B,n", 0x06, 8, (regs, memory, args) -> regs.setB(args[0]));
    setOpcode("INC D", 0x14, 4, (regs, memory, args) -> regs.decD());
}
```

### Understanding the bootrom
It took me longer than I it should've to understand the Bootrom and how it is stored in the GameBoy's memory. Simply put, the opcode is first stored in memory followed by any of the required arguments. E.g. The first instruction (LD SP,$FFFE) takes up 3 bytes of memory ($31 $FF $FE). This is because LD SP,nn is an instruction which can be compressed into a single byte known as the opcode ($31). The following two bytes ($FF and $FE) are then used by the LD instruction and loaded into the SP register.

## Resources:

https://blog.rekawek.eu/2017/02/09/coffee-gb/

https://youtu.be/HyzD8pNlpwI

http://gameboy.mongenel.com/dmg/opcodes.html

[Bootrom](http://gbdev.gg8.se/wiki/articles/Gameboy_Bootstrap_ROM)

[A Look At The Game Boy Bootstrap: Let The Fun Begin!](https://realboyemulator.wordpress.com/2013/01/03/a-look-at-the-game-boy-bootstrap-let-the-fun-begin/)
