package fr.duvam.arduino.test;

import org.apache.log4j.Logger;

import fr.duvam.OSValidator;
import jssc.SerialPort;
import jssc.SerialPortException;

//SerialControl class is used by the GUI class to send bytes over serial to the Arduino.
public class SerialControl {
	static SerialPort serialPort;
	final private byte FORWARD = 1;
	final private byte REVERSE = 2;
	final private byte RIGHT = 3;
	final private byte LEFT = 4;
	final private byte STOP = 5;
	
	private static final String linuxPort = "/dev/ttyACM0";
	private static final String winPort = "COM11";
	
	private static final Logger LOGGER = Logger.getLogger(SerialControl.class);
	
	public SerialControl(){
		
		String comPort = null;
		String os = OSValidator.getOS();
		if (os.contains("uni")) {
			comPort = linuxPort;
		}
		if (os.contains("win")) {
			comPort = winPort;
		}

		
		serialPort = new SerialPort(comPort);
		try {
			serialPort.openPort();
			serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		} catch (SerialPortException e) {
			LOGGER.error("can't connect to arduino port", e);
		}

	}
	
	public static void main(String[] args) throws SerialPortException {
		new SerialControl();
		serialPort.writeString("truc");
	}
	
	public void write(String msg) {
		try {
			serialPort.writeString(msg);
		} catch (SerialPortException e) {
			LOGGER.error("can't connect to arduino port", e);
		}
	}
	
	public void close() throws SerialPortException {
		serialPort.closePort();
	}
	
	public void forward() throws SerialPortException {
		serialPort.writeByte(FORWARD);
	}
	
	public void reverse() throws SerialPortException {
		serialPort.writeByte(REVERSE);
	}
	
	public void right() throws SerialPortException {
		serialPort.writeByte(RIGHT);
	}
	
	public void left() throws SerialPortException {
		serialPort.writeByte(LEFT);
	}
	
	public void stop() throws SerialPortException {
		serialPort.writeByte(STOP);
	}

}