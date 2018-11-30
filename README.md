# PhilStone
A simple tool for the synthesis of concurrent programs using Alloy-Tool and Model Checking

- Compiling the Tool

You can compile the code with the ant tool, just running 'ant' The .class files will be generated and saved in folder bin/

- Running the Tool

1- You must add the jar/ folder to your $CLASSPATH, and set the $PhilStone to the actual folder,

2- Just execute:

   java bin/PS/PhilStone [OPTIONS] [SPEC FILES]

where: [OPTIONS] are the following:

'-scope=k' tries to synthesize the program using a scope of k elements

'-pdf' if a program is synthesized it produces a pdf with the state/transition system for the processes, saved as a .dot in output/  folder

'-genSearch' it tries to use solutions of the problem with less instances 

'-lexSearch' it uses the standard algorithm without counterexamples

The default algorithm uses counterexamples.

- Examples of use:

(cd bin/)

- java PS/PhilStone -scope=6 ../examples/mutex2.spec

it synthesizes a program for mutex with a scope of 6 using counterexamples, if the scope is small  the algorithm couldn't be able to synthesize a program,

- java PS/PhilStone -genSearch=../src/tests/phils/phil.spec -scope=14 ../examples/phils/phil5.spec

It synthesizes code for 5 philosophers using a smaller instance of the problem (2 philosophers).

- Generating JAR file:

 The .jar file can be generated executing 'ant jar', you can find the file in bin/jar, the tool also can be executed using the jar.  

- The examples can be found in examples/ folder, they use a pre/post condition style of specification. After running the tool, an Alloy
specification is generated and saved in the "output" folder. This specification is in First-Order Logic.
 

