# ns-builder
For scaling ns files. 

Requires JDK 8 (tested with 8u121). 

Commands listed are for macOS/Linux, for Windows, please replace `./gradlew` with `gradlew.bat`. Commands are to be run in the root folder of the cloned repository. 

Both builders require a joint node named `joint`. 

Everything in the input file is treated as one block. 

## SimpleBuilder
Multiplies blocks, the `joint` nodes from each block are then connected together in a single LAN. 

Goes from `1` to `n`. 

### To launch SimpleBuilder
Replace arguments with the inputs you want:
```
$ ./gradlew simple --console plain -Parg=<input file>,<number of repetitions>,<output file>
```
Sample command:
```
$ ./gradlew simple --console plain -Parg=test2.ns,2,output.ns
```

## HierarchicalBuilder
Instead of multiplying blocks like in SimpleBuilder, think of HierarchicalBuilder in terms of indices. Where the base is the number of repetitions per level and the index is number of levels. 

Each level has its own `joint` nodes connected to that level's `joint` LAN as well as the next higher level's `joint` node. This repeats until it reaches the root `joint` node. 

Goes from `1` to `m^n`. 

### To launch HierarchicalBuilder
Replace arguments with the inputs you want:
```
$ ./gradlew hierarchical --console plain -Parg=<input file>,<number of levels>,<number of repetitions per level>,<output file>
```
Sample command:
```
$ ./gradlew hierarchical --console plain -Parg=test2.ns,2,2,output.ns
```