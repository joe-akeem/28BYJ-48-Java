package de.joeakeem.m28BYJ48;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

import static com.pi4j.io.gpio.PinState.LOW;
import static com.pi4j.io.gpio.PinState.HIGH;

/**
 * Represents the 28BYJ-48 stepper motor, and the ULN2003 driver board connected
 * to a Raspberry Pi. It uses Pi4J to set the I/O pins on the Raspberry Pi. This
 * class supports the stepping methods 'wave drive', 'full step' and 'half step'
 * (see <https://www.youtube.com/watch?v=B86nqDRskVU> for details.)
 *
 * @see <http://pi4j.com>
 */
public class StepperMotor28BYJ48 {
	
	private static final Logger LOG = LoggerFactory.getLogger(StepperMotor28BYJ48.class);
	
	private final GpioController gpio = GpioFactory.getInstance();

	/**
	 * The stepping method to be used by the motor.
	 * 
	 * @see <https://www.youtube.com/watch?v=B86nqDRskVU>
	 */
	public enum SteppingMethod {
		WAVE_DRIVE, FULL_STEP, HALF_STEP
	};
	
	private static final PinState WAVE_DRIVE_MOTOR_SEQUENCE[][] = new PinState[][] {
			{ LOW,  LOW,  LOW,  HIGH },
			{ LOW,  LOW,  HIGH, LOW },
			{ LOW,  HIGH, LOW,  LOW },
			{ HIGH, LOW,  LOW,  LOW },
			{ LOW,  LOW,  LOW,  HIGH },
			{ LOW,  LOW,  HIGH, LOW },
			{ LOW,  HIGH, LOW,  LOW },
			{ HIGH, LOW,  LOW,  LOW }
	};
	
	private static final PinState FULL_STEP_MOTOR_SEQUENCE[][] = new PinState[][] {
			{ LOW,  LOW,  LOW,  HIGH },
			{ LOW,  LOW,  HIGH, LOW },
			{ LOW,  HIGH, LOW,  LOW },
			{ HIGH, LOW,  LOW,  LOW },
			{ LOW,  LOW,  LOW,  HIGH },
			{ LOW,  LOW,  HIGH, LOW },
			{ LOW,  HIGH, LOW,  LOW },
			{ HIGH, LOW,  LOW,  LOW }
	};
	
	private static final PinState HALF_STEP_MOTOR_SEQUENCE[][] = new PinState[][] {
			{ LOW,  LOW,  LOW,  HIGH },
			{ LOW,  LOW,  HIGH, HIGH },
			{ LOW,  LOW,  HIGH, LOW },
			{ LOW,  HIGH, HIGH, LOW },
			{ LOW,  HIGH, LOW,  LOW },
			{ HIGH, HIGH, LOW,  LOW },
			{ HIGH, LOW,  LOW,  LOW },
			{ HIGH, LOW,  LOW,  HIGH }
	};
	
	/** The current step duration in milliseconds. **/
	private int stepDuration;
	
	/** Holds the Raspberry Pi pin numbers for pins A though D of the stepper motor. **/
	private GpioPinDigitalOutput[] motorPins;
	
	/** The current stepping method of this motor **/
	private SteppingMethod steppingMethod;
	
	/**
	 * Connects a new stepper motor instance with the pins on a Raspberry Pi
	 * and sets a step duration. Per default the half step stepping method
	 * will be used.
	 * <em>This method uses the Pi4J/wiringPi numbering system of the Raspberry Pi
	 * (see http://pi4j.com/pins/model-b-plus.html).</em>
	 *
	 * @param pinA - the Raspberry Pi pin the pin A of the stepper motor is connected to
	 * @param pinB - the Raspberry Pi pin the pin B of the stepper motor is connected to
	 * @param pinC - the Raspberry Pi pin the pin C of the stepper motor is connected to
	 * @param pinD - the Raspberry Pi pin the pin D of the stepper motor is connected to
	 * @param stepDuration - the pause between two steps in milliseconds.
	 *
	 */
	StepperMotor28BYJ48(Pin pinA, Pin pinB, Pin pinC, Pin pinD, int stepDuration) {
		this(pinA, pinB, pinC, pinD, stepDuration, SteppingMethod.HALF_STEP);
	}

	/**
	 * Connects a new stepper motor instance with the pins on a Raspberry Pi
	 * and sets a step duration and a stepping method.
	 * <em>This method uses the Pi4J/wiringPi numbering system of the Raspberry Pi
	 * (see http://pi4j.com/pins/model-b-plus.html).</em>
	 *
	 * @param pinA - the Raspberry Pi pin the pin A of the stepper motor is connected to
	 * @param pinB - the Raspberry Pi pin the pin B of the stepper motor is connected to
	 * @param pinC - the Raspberry Pi pin the pin C of the stepper motor is connected to
	 * @param pinD - the Raspberry Pi pin the pin D of the stepper motor is connected to
	 * @param stepDuration - the pause between two steps in milliseconds.
	 * @param steppingmethod - the stepping method to be used by the motor
	 *
	 */
	StepperMotor28BYJ48(Pin pinA, Pin pinB, Pin pinC,
			Pin pinD, int stepDuration, SteppingMethod steppingMethod)
	{
		motorPins = new GpioPinDigitalOutput[4];

		motorPins[0] = gpio.provisionDigitalOutputPin(pinA, "Pin A", LOW);
		motorPins[1] = gpio.provisionDigitalOutputPin(pinB, "Pin B", LOW);
		motorPins[2] = gpio.provisionDigitalOutputPin(pinC, "Pin C", LOW);
		motorPins[3] = gpio.provisionDigitalOutputPin(pinD, "Pin D", LOW);
		
		this.stepDuration = stepDuration;
		this.steppingMethod = steppingMethod;
	}
	
	/**
	 * Causes the motor to perform full rotations. If the passed value
	 * is positive the motor will rotate clockwise, if negative it will
	 * rotate counterclockwise.
	 *
	 * @param noOfRotations - the amount of full rotations to perform
	 */
	public void fullRotation(int noOfRotations) {
		halfRotation(2*noOfRotations);
	}

	/**
	 * Causes the motor to perform half rotations. If the passed value
	 * is positive the motor will rotate clockwise, if negative it will
	 * rotate counterclockwise.
	 *
	 * @param noOfRotations - the amount of half rotations to perform
	 */
	public void halfRotation(int noOfHalfRotations) {
		quarterRotation(2*noOfHalfRotations);
	}

	/**
	 * Causes the motor to perform quarter rotations. If the passed value
	 * is positive the motor will rotate clockwise, if negative it will
	 * rotate counterclockwise.
	 *
	 * @param noOfRotations - the amount of quarter rotations to perform
	 */
	public void quarterRotation(int noOfQuarterRotations) {
		switch (steppingMethod) {
			case HALF_STEP:
				step(2 * 512 * noOfQuarterRotations);
				break;
			default:
				step(512 * noOfQuarterRotations);
				break;
		}
	}

	/**
	 * Rotates the motor by a specified angle. Note that the step angle
	 * of the motor is 0.19 degrees in full step and wave drive methods
	 * and 0.09 degrees in half step method.
	 *
	 * @param angle - the angle in degrees
	 */
	public void angleRotation(float angle) {
		int steps;
		switch (steppingMethod) {
			case HALF_STEP:
				steps = (int) (512 * 8 * angle) / 360;
				break;
			default:
				steps = (int) (512 * 4 * angle) / 360;
				break;
		}
		step(steps);
	}

	/**
	 * Moves the stepper motor by one step. Depending on the stepping method
	 * this will move the motor by 0.19 degrees in case of full step and wave drive methods
	 * or 0.09 degrees in half step method. If the passed value is positive the motor
	 * will rotate clockwise, if negative it will rotate counterclockwise.
	 */
	public void step(int noOfSteps) {
		if (noOfSteps > 0) {
			for (int currentStep = noOfSteps; currentStep > 0; currentStep--) {
				int currentSequenceNo = currentStep % 8;
				writeSequence(currentSequenceNo);
			}
		} else {
			for (int currentStep = 0; currentStep < Math.abs(noOfSteps); currentStep++) {
				int currentSequenceNo = currentStep % 8;
				writeSequence(currentSequenceNo);
			}
		}
	}

	/**
	 * Performs a demo of the various methods to move the motor.
	 */
	public void performDemo() {
		LOG.info("Full rotation clockwise in wave drive method... ");
		setSteppingMethod(SteppingMethod.WAVE_DRIVE);
		fullRotation(1);
		LOG.info("Done.");

		LOG.info("Full rotation counterclockwise in full step method... ");
		setSteppingMethod(SteppingMethod.FULL_STEP);
		fullRotation(-1);
		LOG.info("Done.");

		LOG.info("Full rotation clockwise in half step method... ");
		setSteppingMethod(SteppingMethod.HALF_STEP);
		fullRotation(1);
		LOG.info("Done.");

		LOG.info("Half rotation counterclockwise in full step method... ");
		setSteppingMethod(SteppingMethod.FULL_STEP);
		halfRotation(-1);
		LOG.info("Done.");

		LOG.info("Quarter rotation clockwise in full step method... ");
		setSteppingMethod(SteppingMethod.FULL_STEP);
		quarterRotation(1);
		LOG.info("Done.");

		LOG.info("180 degree rotation counterclockwise in full step method... ");
		setSteppingMethod(SteppingMethod.FULL_STEP);
		angleRotation(-180);
		LOG.info("Done.");

		LOG.info("90 degree rotation clockwise in half step method... ");
		setSteppingMethod(SteppingMethod.HALF_STEP);
		angleRotation(270);
		LOG.info("Done.");
	}
	
	/**
	 * Writes the motor sequence to the Raspberry Pi pins.
	 *
	 * @param sequenceNo - references a sequence in one of the motor sequences above.
	 */
	private void writeSequence(int sequenceNo) {
		for (int i = 0; i < 4; i++) {
			switch(steppingMethod) {
				case WAVE_DRIVE:
					motorPins[i].setState(WAVE_DRIVE_MOTOR_SEQUENCE[sequenceNo][i]);
					break;
				case FULL_STEP:
					motorPins[i].setState(FULL_STEP_MOTOR_SEQUENCE[sequenceNo][i]);
					break;
				default:
					motorPins[i].setState(HALF_STEP_MOTOR_SEQUENCE[sequenceNo][i]);
					break;
			}
		}
		try {
			Thread.sleep(stepDuration);
		} catch (InterruptedException e) {
		}
	}
	
	/**
	 * Sets the duration of the pause between each step in milliseconds.
	 *
	 * @param stepDuration - the pause in milliseconds between each step
	 */
	public void setStepDurartion(int stepDuration) {
		if (stepDuration <= 0) {
			throw new IllegalArgumentException("Step duration must be > 0.");
		}
		this.stepDuration = stepDuration;
	}
	
	/**
	 * @return the current duration of the pause between each step in milliseconds.
	 */
	public int getStepDuration() {
		return stepDuration;
	}

	/**
	 * @return the current stepping method of the stepper motor.
	 */
	public SteppingMethod getSteppingMethod() {
		return steppingMethod;
	}

	/**
	 * Sets the stepping method of the stepper motor.
	 *
	 * @param steppingMethod - the stepping method to set.
	 */
	public void setSteppingMethod(SteppingMethod steppingMethod) {
		this.steppingMethod = steppingMethod;
	}
}
