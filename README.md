# 28BYJ-48-Java
A Java API for the 28BYJ-48 stepper motor, and the ULN2003 driver board connected to a Raspberry Pi.

Clone this repo to your Raspberry Pi:
``` shell
git clone https://github.com/joe-akeem/28BYJ-48-Java.git
```

Install maven on your Raspberry Pi:
find instructions [here](http://www.xianic.net/post/installing-maven-on-the-raspberry-pi/).

Execute it:
``` shell
cd 28BYJ-48-Java
sudo /opt/apache-maven-3.3.3/bin/mvn install exec:java -Dexec.mainClass="de.joeakeem.m28BYJ48.Example"
```