package fr.duvam.arduino;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.duvam.PropertiesUtil;
import fr.duvam.listener.CommandListener;
import fr.duvam.utils.OSValidator;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class ArduinoComm implements SerialPortEventListener {
	SerialPort serialPort;
	CommandListener commandListener;
	PropertiesUtil properties;

	private static final Logger LOGGER = Logger.getLogger(ArduinoComm.class);

	/*
	 * private static final String PORT_NAMES[] = { "/dev/ttyACM0", "/dev/ttyACM1",
	 * "/dev/ttyACM2", // Raspberry Pi "/dev/ttyAMA0", "/dev/ttyUSB0", // Linux
	 * "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM11", "COM18", "COM19",
	 * "COM24",// Windows };
	 */
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

	public ArduinoComm(CommandListener commandListener, PropertiesUtil properties) {
		this.commandListener = commandListener;
		this.properties = properties;
		initialize();
	}

	public void initialize() {
		// the next line is for Raspberry Pi and
		// gets us into the while loop and was suggested here was suggested
		// https://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186

		String os = OSValidator.getFullOS();

		List<String> ports = new LinkedList<String>();
		if (os.contains("arm")) {
			ports = properties.getList("port.pi");
			LOGGER.info("raspian port " + ports);
		} else if (os.contains("linux")) {
			ports = properties.getList("port.linux");
			LOGGER.info("linux port " + ports);
		} else if (os.contains("win")) {
			ports = properties.getList("port.win");
			LOGGER.info("windows port " + ports);
		}

		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : ports) {

				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					LOGGER.info("open arduino on port : " + portName);

					try {

						if (portId == null) {
							LOGGER.info("Could not find COM port");
							return;
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
						break;
					} catch (Exception e) {
						LOGGER.error("can't reach arduino on port : " + portName);
					}
				}
			}
			LOGGER.error("no arduino found");
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
				serialPort.disableReceiveTimeout();
				serialPort.enableReceiveThreshold(1);
				// TODO
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
		PropertiesUtil properties = new PropertiesUtil();
		ArduinoComm main = new ArduinoComm(null, properties);
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
