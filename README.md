# 28BYJ-48-Java
A Java API for the 28BYJ-48 stepper motor, and the ULN2003 driver board connected to a Raspberry Pi.

## Prerequisites
You need Java 7 JDK and Maven on your Raspberry Pi.

### Java
On the newer Raspbian Images Java is already installed. You can also get it as follows:
``` shell
sudo apt-get update && sudo apt-get install oracle-java7-jdk
```

### Maven
Find instruction on how to install Maven in your Raspberry Pi [here](http://www.xianic.net/post/installing-maven-on-the-raspberry-pi/).

## Getting, Building

Clone this repo to your Raspberry Pi...
``` shell
git clone https://github.com/joe-akeem/28BYJ-48-Java.git
```

...and build it:
``` shell
cd 28BYJ-48-Java
sudo /opt/apache-maven-3.3.3/bin/mvn install
```

This will install the 28BYJ-48-Java in your maven Repository to be used by other projects such as the [Scratch Robot](https://github.com/joe-akeem/scratch-robot)

## Run Demo

Once you've built the library as described above you can connect a 28BYJ-48 stepper motor on a ULN2003 driver board to your Raspberry Pi and run the example contained in the project which will perform a little demo rotating the stepper motor in various modes and directions.
Also check out the example in the src/main/java/de/joeakeem/m28BYJ48.Example class.

### Connecting the Motor

The table below uses the WiringPi/GPIO numbering system. Connect the motor to the ULN2003 board and hook up the board to the Raspberry Pi oins as follows:  

|ULN2003 Pin|Rasbery Pin|
|-----------|-----------|
|In1        |7          |
|In2        |0          |
|In3        |2          |
|In4        |3          |

The + and - pins can also be connected to the Raspberry Pi or to an independent power source. If you connect it to the Raspberry Pi make sure the + pin is connected to a 5V pin on the Raspberry and the - pin of the ULN2003 board is connected to a Raspberry ground pin.

### Running the Example

Run the motor demo as follows:

``` shell
sudo /opt/apache-maven-3.3.3/bin/mvn exec:java -Dexec.mainClass="de.joeakeem.m28BYJ48.Example"
```