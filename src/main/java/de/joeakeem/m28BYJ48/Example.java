package de.joeakeem.m28BYJ48;

import de.joeakeem.m28BYJ48.StepperMotor28BYJ48.SteppingMethod;
import static com.pi4j.io.gpio.RaspiPin.*;

/**
 * Hello world!
 *
 */
public class Example 
{
    public static void main( String[] args )
    {
    	StepperMotor28BYJ48 stepperMotor = new
    			StepperMotor28BYJ48(GPIO_07, GPIO_00, GPIO_02, GPIO_03, 3, SteppingMethod.FULL_STEP);
    	
    	stepperMotor.performDemo();
    }
}
