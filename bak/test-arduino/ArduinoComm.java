package fr.duvam.arduino.test;

import java.io.PrintWriter;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.fazecast.jSerialComm.SerialPort;

import fr.duvam.OSValidator;
import fr.duvam.video.CommandListener;

public class ArduinoComm implements Runnable {
	private SerialPort comPort;
	private static final String linuxPort = "/dev/ttyACM0";
	private static final String winPort = "COM11";

	private static final int baud_rate = 9600;

	private CommandListener listener;

	private static final Logger LOGGER = Logger.getLogger(ArduinoComm.class);

	/*
	 * public ArduinoComm(CommandListener listener) { // make sure to set baud rate
	 * after this.listener = listener;
	 * 
	 * initialize(); }
	 */

	// for testing
	public ArduinoComm() {
		initialize();
		// comPort = SerialPort.getCommPort(portDescription);
		// comPort.setBaudRate(baud_rate);
	}

	private void initialize() {

		String port = null;
		String os = OSValidator.getOS();

		SerialPort[] ports = SerialPort.getCommPorts();
		for (SerialPort p : ports) {
			LOGGER.info(p.getDescriptivePortName());
		}

		if (os.contains("uni")) {
			port = linuxPort;
		}
		if (os.contains("win")) {
			port = winPort;
		}

		LOGGER.info(" os : " + os + ", port : " + port);

		if (port == null) {
			LOGGER.info("Could not find COM port, is arduino plugged in ?");
			return;
		}

		comPort = SerialPort.getCommPort(port);
		if (comPort == null) {
			LOGGER.info("failed to initalise arduino port");
		}
		comPort.setBaudRate(baud_rate);

	}

	public void sendString(String msg) {
		LOGGER.info("arduino msg :" + msg);
		openConnection();
		serialWrite(msg);
		closeConnection();
	}

	public static void main(String[] args) throws InterruptedException {

		ArduinoComm arduino = new ArduinoComm();
		arduino.openConnection();

		// for(int i=0;i<100;i++) {
		// arduino.serialWrite(Integer.toString(i));
		// Thread.sleep(5000);
		// }
		Thread.sleep(5000);
		arduino.serialWrite("a");

		arduino.closeConnection();

		// Arduino obj = new Arduino(portDescription, baud_rate);
		// obj.openConnection();
	}

	public boolean openConnection() {
		if (comPort.openPort()) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
			return true;
		} else {
			LOGGER.error("Error Connecting, Try Another port");
			return false;
		}
	}

	public void closeConnection() {
		comPort.closePort();
	}

	public String serialRead() {
		// will be an infinite loop if incoming data is not bound
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		String out = "";
		Scanner in = new Scanner(comPort.getInputStream());
		try {
			while (in.hasNext()) {
				out = (in.next());
				listener.addKey(out);
				System.out.println(out);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}

	public String serialRead(int limit) {
		// in case of unlimited incoming data, set a limit for number of readings
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		String out = "";
		int count = 0;
		Scanner in = new Scanner(comPort.getInputStream());
		try {
			while (in.hasNext() && count <= limit) {
				out += (in.next() + "\n");
				count++;
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}

	public void serialWrite(String s) {
		// writes the entire string at once.
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);

		try {
			Thread.sleep(5);
		} catch (Exception e) {
		}

		PrintWriter pout = new PrintWriter(comPort.getOutputStream());
		pout.print(s);
		pout.flush();

	}

	public void serialWrite(String s, int noOfChars, int delay) {
		// writes the entire string, 'noOfChars' characters at a time, with a delay of
		// 'delay' between each send.
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		try {
			Thread.sleep(5);
		} catch (Exception e) {
		}
		PrintWriter pout = new PrintWriter(comPort.getOutputStream());
		for (int i = 0; i < s.length() - noOfChars; i += noOfChars) {
			pout.write(s.substring(i, i + noOfChars));
			pout.flush();
			try {
				Thread.sleep(delay);
			} catch (Exception e) {
			}
		}
		// pout.write(s.substring(i));
		// pout.flush();

	}

	public void serialWrite(char c) {
		// writes the character to output stream.
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		try {
			Thread.sleep(5);
		} catch (Exception e) {
		}
		PrintWriter pout = new PrintWriter(comPort.getOutputStream());
		pout.write(c);
		pout.flush();
	}

	public void serialWrite(char c, int delay) {
		// writes the character followed by a delay of 'delay' milliseconds.
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		try {
			Thread.sleep(5);
		} catch (Exception e) {
		}
		PrintWriter pout = new PrintWriter(comPort.getOutputStream());
		pout.write(c);
		pout.flush();
		try {
			Thread.sleep(delay);
		} catch (Exception e) {
		}
	}

	@Override
	public void run() {
		openConnection();
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LOGGER.error("arduino thread interrupted", e);
				closeConnection();
			}
			serialRead();
		}
	}
}
