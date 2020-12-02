package fr.duvam.arduino;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import fr.duvam.OSValidator;
import fr.duvam.video.CommandListener;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class ArduinoComm implements SerialPortEventListener {
	SerialPort serialPort;
	/** The port we're normally going to use. */

	CommandListener commandListener;
	
	private static final Logger LOGGER = Logger.getLogger(ArduinoComm.class);

	private static final String PORT_NAMES[] = { "/dev/tty.usbserial-A9007UX1", // Mac OS X
			"/dev/ttyACM0", "/dev/ttyACM1", "/dev/ttyACM2", // Raspberry Pi
			"/dev/ttyAMA0", "/dev/ttyUSB0", // Linux
			"COM6", "COM11", "COM18", "COM19", "COM24",// Windows
	};
	/**
	 * A BufferedReader which will be fed by a InputStreamReader converting the
	 * bytes into characters making the displayed results codepage independent
	 */
	private BufferedReader input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 115200;

	public ArduinoComm(CommandListener commandListener) {
		this.commandListener = commandListener;
		initialize();
		LOGGER.info("Arduino link Started");
	}

	public void initialize() {
		// the next line is for Raspberry Pi and
		// gets us into the while loop and was suggested here was suggested
		// https://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186

		String os = OSValidator.getFullOS();
		// raspian
		if (os.contains("arm")) {
			LOGGER.info("arm set \"/dev/ttyAMA0\"");
			System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyAMA0");
		}
		// debian
		else if (os.contains("linux")) {
			LOGGER.info("uni set \"/dev/ttyACM0\"");
			System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");
		} else if (os.contains("win")) {
			LOGGER.info("win set COM24");
			System.setProperty("gnu.io.rxtx.SerialPorts", "COM24");
		}

		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				LOGGER.info(portName);
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}

		try {

			if (portId == null) {
				LOGGER.info("Could not find COM port");
			}

			serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();

			// add event listeners
			// create access violation error
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			LOGGER.error("can't reach arduino");
		}
	}

	/**
	 * This should be called when you stop using the port. This will prevent port
	 * locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine = input.readLine();
				// serialPort.disableReceiveTimeout();
				// serialPort.enableReceiveThreshold(1);
				commandListener.addKey(inputLine);
				LOGGER.info(" received from arduino : " + inputLine);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}

	public void sendString(String msg) {
		try {
			LOGGER.info(" send " + msg + " to arduino");
			output.write(msg.getBytes());

		} catch (Exception e) {
			LOGGER.error("can't send msg to arduino", e);
		}
	}

	public static void main(String[] args) throws Exception {
		ArduinoComm main = new ArduinoComm(null);
		// SerialTest main = new SerialTest();
		main.sendString("truc");

		/*
		 * while (true) { String inputLine = ""; while ((inputLine =
		 * main.input.readLine()) != null) { System.out.println(inputLine); } }
		 */

		Thread t = new Thread() {
			public void run() {
				// the following line will keep this app alive for 1000 seconds,
				// waiting for events to occur and responding to them (printing incoming
				// messages to console).
				try {
					Thread.sleep(1000000);
				} catch (InterruptedException ie) {
				}
			}
		};
		t.start();
		System.out.println("Started");

	}
}
