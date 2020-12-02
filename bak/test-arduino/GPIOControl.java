package fr.duvam.arduino.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.util.CommandArgumentParser;

import fr.duvam.video.CommandListener;

public class GPIOControl {
	
	private static Logger LOGGER = LoggerFactory.getLogger(GPIOControl.class);
	
	public static void main(String[] args) throws InterruptedException {

		// get a handle to the GPIO controller
		final GpioController gpio = GpioFactory.getInstance();

		Pin pin2 = CommandArgumentParser.getPin(RaspiPin.class, // pin provider class to obtain pin instance from
				RaspiPin.GPIO_02, // default pin if no pin argument found
				args); // argument array to search in

		GpioPinPwmOutput pwm = gpio.provisionPwmOutputPin(pin2);

		// you can optionally use these wiringPi methods to further customize the PWM
		// generator
		// see: http://wiringpi.com/reference/raspberry-pi-specifics/
		com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
		com.pi4j.wiringpi.Gpio.pwmSetRange(1000);
		com.pi4j.wiringpi.Gpio.pwmSetClock(500);

		// set the PWM rate to 500
		pwm.setPwm(500);
		LOGGER.info("PWM rate is: " + pwm.getPwm());

		// creating the pin with parameter PinState.HIGH
		// will instantly power up the pin
		final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "PinLED", PinState.HIGH);
		System.out.println("light is: ON");

		// wait 2 seconds
		Thread.sleep(2000);

		// turn off GPIO 1
		pin.low();

		System.out.println("light is: OFF");
		// wait 1 second
		Thread.sleep(1000);
		// turn on GPIO 1 for 1 second and then off
		System.out.println("light is: ON for 1 second");
		pin.pulse(1000, true);

		// release the GPIO controller resources
		gpio.shutdown();
	}
}
