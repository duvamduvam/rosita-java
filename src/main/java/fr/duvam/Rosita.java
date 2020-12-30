package fr.duvam;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import fr.duvam.arduino.ArduinoComm;
import fr.duvam.lights.Lights;
import fr.duvam.listener.CommandListener;
import fr.duvam.listener.MediaListener;
import fr.duvam.media.PlayerManager;
import fr.duvam.utils.FileChangedWatcher;
import fr.duvam.utils.OSValidator;
import fr.duvam.utils.PropertiesUtil;

public class Rosita {

	private static Logger LOGGER;

	private CommandListener commands;
	private PlayerManager playerManager;
	private Lights lights;
	PropertiesUtil properties;

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Rosita().start();
			}
		});
	}

	public Rosita() {

		properties = new PropertiesUtil();

		initLog();

		commands = new CommandListener();
		// ArduinoComm arduino = new ArduinoComm(commands, properties);
		// midi listener and player !!!! leave the lister before the player therwise the
		// player don't work
		// TODO could be moved inside playerManager
		// new MidiListener(commands, properties);
		// MidiPlayer midiPlayer = new MidiPlayer(properties);

		playerManager = new PlayerManager(commands);
		commands.setPlayerManager(playerManager);
		// lights = new Lights(arduino);

	}

	void initLog() {
		// init log4j property logging
		String logsPath = properties.getLogPath();
		System.setProperty("logsPath", logsPath);
		String os = OSValidator.getFullOS();

		System.setProperty("hostName", os);

		LOGGER = Logger.getLogger(Rosita.class);
		LOGGER.info("current os : " + os);
	}

	protected void start() {

		playerManager.videoPlayer.playDefault();
		
		
		//TODO clean around
		initListeners(commands, lights);

	
		Thread fileListenerThread = initArduinoListener(commands);

		// check arduino listener alive
		new Thread(new Runnable() {
			public void run() {
				Thread innerThread = fileListenerThread;
				try {
					innerThread.join();
				} catch (Exception e) {
					LOGGER.error("arduino thread dead");
					innerThread = initArduinoListener(commands);
				}
			}
		}).start();

	}

	private void initListeners(CommandListener keyListener, Lights lights) {

		// key listener
		Thread keyListenerThread = new Thread(keyListener);
		keyListenerThread.setDaemon(true);
		keyListenerThread.start();

		// video listener
		MediaListener mediaListener = new MediaListener(playerManager);
		Thread mediaListenerThread = new Thread(mediaListener);
		mediaListenerThread.setDaemon(true);
		mediaListenerThread.start();


		// light listener
		// Thread lightsListenerThread = new Thread(lights);
		// lightsListenerThread.setDaemon(true);
		// lightsListenerThread.start();

	}

	private Thread initArduinoListener(CommandListener keyListener) {

		// arduino listener
		FileChangedWatcher fileListener = new FileChangedWatcher(keyListener);
		Thread fileListenerThread = new Thread(fileListener);
		fileListenerThread.setDaemon(true);
		fileListenerThread.start();

		return fileListenerThread;

		// light listener
		// Thread lightsListenerThread = new Thread(lights);
		// lightsListenerThread.setDaemon(true);
		// lightsListenerThread.start();

	}

}
