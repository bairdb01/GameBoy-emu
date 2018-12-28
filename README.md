# GameBoy-emu
A WIP GameBoy emulator.

## Some of my troubles and thoughts
Since this is my first emulator I have ever programmed, I thought it would be nice to note down some of my thoughts and problems. 

### How to handle opcodes?
At the beginning of this project I had next to no idea of what an opcode was. Upon further research I found out that an opcode is just an instruction from an instruction set. So with that knowledge under my belt I had a bigger problem I asked myself: "How do I store a set of instructions?" Of course I could have used a bunch of switch statements, but that would be very ugly. I originally wanted to use some kind of array of function pointers, however Java doesn't have those. So I went searching and the closest thing I could find was a Functional Interface. The FunctionalInterface checked everybox I wanted. The FunctionalInterface allowed me to pass different functions as a parameter through the use of lambda expressions. This allowed me to create an array of objects which each had different methods.


E.g. FunctionalInterface
```
public class GameBoy.Instructions {
    String label;
    int Opcode;
    int cycles;
    GameBoy.Operation op;

    public GameBoy.Instructions(String label, int Opcode, int clocks, GameBoy.Operation op) {
        this.label = label;
        this.Opcode = Opcode;
        this.cycles = cycles;
        this.op = op;
    }
}


@FunctionalInterface
interface GameBoy.Operation {
    void cmd(GameBoy.Registers regs, int[] memory, int[] args);
}

public Opcode{
    GameBoy.Instructions [] opcodes = new GameBoy.Instructions[0x100];
    private void setOpcode(String label, int opcode, int clocks, GameBoy.Operation op) {
        opcodes[opcode] = new GameBoy.Instructions(label, opcode, clocks, op);
    }

    public int main(){
        setOpcode("LD B,n", 0x06, 8, (regs, memory, args) -> regs.setB(args[0]));
        setOpcode("INC D", 0x14, 4, (regs, memory, args) -> regs.decD());
    }
}
```
### GameBoy.Opcodes
This will probably be one of the most time consuming parts and can be quite tedious. Don't make it any more tedious than you have too! A lot of the functions perform similar actions, so it would be smart to keep things modular.

### Understanding the bootrom
It took me longer than I it should've to understand the Bootrom and how it is stored in the GameBoy's memory. Simply put, the opcode is first stored in memory followed by any of the required arguments. E.g. The first instruction (LD SP,$FFFE) takes up 3 bytes of memory ($31 $FF $FE). This is because LD SP,nn is an instruction which can be compressed into a single byte known as the opcode ($31). The following two bytes ($FF and $FE) are then used by the LD instruction and loaded into the SP register.

### The CB prefixed opcode
The CB prefix notifies the GameBoy.CPU that there will be an additonal byte which must be read (uninterruptable). These two bytes form the opcode for the instruction. Two opcode lists is a solution. One for standard codes and another for CB-prefixed codes. Use second byte as index to the CB-code array.

### How to modify register values if they are primitive data types? (ints, bytes, shorts, etc. are pass by value in Java)
The use of functional interface and lambda to store instructions in a variable allows for more than just one instruction to be performed. I simply return the value the register/memory should be and assign it in the same lambda. Of course this means that I need to return the proper values from each method.

### Emulating a 16 bit register with two 8 bit registers
The idea of the emulation is not tricky. Just use one register for one half of the 16bits and another register for the other half. A silly problem I ran into was forgetting that the `byte` data type in Java is signed. This means that the MSB is only used to determine if a number is positive or negative.
When creating the 16 bit addition method, I ran into a bug which would produce the incorrect sums. As you may have guessed it was because I forgot that Java uses the signed numerical value of the bits. Additionally, when Java performs math with `bytes` or `shorts` it interprets them as an `int`. This means that the two's complement of a negative number is different (more leading 1's). Due to this I had to apply a bitmask to each value to "ignore" or set unwanted bits to 0.

E.g. 0xFF00 and 0xFF are the bitmasks
```
short regPair = (short) ( ((registers[upperReg] << 8) & 0xFF00) + (registers[lowerReg] & 0xFF) );
```

## Resources:

[Coffee-gb](https://blog.rekawek.eu/2017/02/09/coffee-gb/) - Looking at other people's emulators can help get you started

[The Ultimate Game Boy Talk (33c3)](https://youtu.be/HyzD8pNlpwI) - A nice video to get an understanding of how the GameBoy works

[GameBoy GameBoy.CPU Manual](http://marc.rawer.de/Gameboy/Docs/GBCPUman.pdf) - Recommended when writing the GameBoy.CPU. Chapter 3 has almost all you need.

[GameBoy Programming Manual](https://ia801906.us.archive.org/19/items/GameBoyProgManVer1.1/GameBoyProgManVer1.1.pdf) - Highly detailed GameBoy manual. Goes into detail of how the GameBoy works.

[GameBoy GameBoy.Opcodes Summary](http://gameboy.mongenel.com/dmg/opcodes.html) - Provides more detail on how the flags work for different opcodes

[Opcode chart](http://www.pastraiser.com/cpu/gameboy/gameboy_opcodes.html) - Shows the size of bytes each opcode takes up. Prov

[Bootrom](http://gbdev.gg8.se/wiki/articles/Gameboy_Bootstrap_ROM) - The bootrom in assembly

[A Look At The Game Boy Bootstrap: Let The Fun Begin!](https://realboyemulator.wordpress.com/2013/01/03/a-look-at-the-game-boy-bootstrap-let-the-fun-begin/) - An extensive walkthrough of the bootrom

[Bitwise operation](https://en.wikipedia.org/wiki/Bitwise_operation) - Brush up on some bitwise operators.

## Handy Tools
[Two's Complement Convertor](https://www.exploringbinary.com/twos-complement-converter/) Useful for debugging
