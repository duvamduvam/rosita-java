package fr.duvam;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import fr.duvam.arduino.ArduinoComm;
import fr.duvam.lights.Lights;
import fr.duvam.listener.CommandListener;
import fr.duvam.listener.MediaListener;
import fr.duvam.media.PlayerManager;
import fr.duvam.midi.MidiHandler;
import fr.duvam.utils.OSValidator;

public class Rosita {

	private static Logger LOGGER;

	private CommandListener listener;
	private PlayerManager playerManager;
	private Lights lights;

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Rosita().start();
			}
		});
	}

	public Rosita() {

		initLog();
		
		LOGGER.info(System.getProperty("java.library.path"));

		PropertiesUtil properties = new PropertiesUtil();

		listener = new CommandListener();
		ArduinoComm arduino = new ArduinoComm(listener, properties);
		MidiHandler midi = new MidiHandler(listener);
		playerManager = new PlayerManager(listener, arduino, midi);
		listener.setPlayerManager(playerManager);
		// lights = new Lights(arduino);
	
	}

	void initLog() {
		// init log4j property logging
		try {
			String logPrefix = OSValidator.getFullOS() + "-" + InetAddress.getLocalHost().getHostName();
			System.setProperty("hostName", logPrefix);
			LOGGER = Logger.getLogger(Rosita.class);
			LOGGER.info("<<<<<<<<<<<<<<<<<<<<<<<<<<< START >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		} catch (UnknownHostException e) {
			LOGGER.error(e);
		}
	}

	protected void start() {

		playerManager.videoPlayer.playDefault();
		initListeners(listener, lights);
	}

	private void initListeners(CommandListener keyListener, Lights lights) {

		// key listener
		Thread keyListenerThread = new Thread(keyListener);
		keyListenerThread.setDaemon(true);
		keyListenerThread.start();

		// video listener
		//MediaListener mediaListener = new MediaListener(playerManager, keyListener, lights);
		//Thread mediaListenerThread = new Thread(mediaListener);
		//mediaListenerThread.setDaemon(true);
		//mediaListenerThread.start();

		// light listener
		//Thread lightsListenerThread = new Thread(lights);
		//lightsListenerThread.setDaemon(true);
		//lightsListenerThread.start();

	}

}
