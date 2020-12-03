package fr.duvam;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import fr.duvam.arduino.ArduinoComm;
import fr.duvam.lights.Lights;
import fr.duvam.midi.MidiHandler;
import fr.duvam.video.CommandListener;
import fr.duvam.video.MediaListener;
import fr.duvam.video.MediaManager;
import fr.duvam.video.PlayerManager;

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
		
		PropertiesUtil properties = new PropertiesUtil();
		
		MediaManager mediaManager = new MediaManager();
		listener = new CommandListener(playerManager, mediaManager);
		ArduinoComm arduino = new ArduinoComm(listener, properties);
		MidiHandler midi = new MidiHandler(listener);
		playerManager = new PlayerManager(mediaManager, listener, arduino, midi);
		//lights = new Lights(arduino);
		listener.setPlayerManager(playerManager);

	}

	
	void initLog(){
		//init log4j property logging
		try {
			String logPrefix = OSValidator.getFullOS()+"-"+InetAddress.getLocalHost().getHostName();			
			System.setProperty("hostName", logPrefix);
			LOGGER = Logger.getLogger(Rosita.class);
			LOGGER.info("<<<<<<<<<<<<<<<<<<<<<<<<<<< START >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		} catch (UnknownHostException e) {
			LOGGER.error(e);
		} 
	}
	
	protected void start() {

		playerManager.playDefaultVideo();
		initListeners(listener, lights);
	}

	private void initListeners(CommandListener keyListener, Lights lights) {

		// key listener
		Thread keyListenerThread = new Thread(keyListener);
		keyListenerThread.setDaemon(true);
		keyListenerThread.start();



		// video listener
		MediaListener mediaListener = new MediaListener(playerManager, keyListener, lights);
		Thread mediaListenerThread = new Thread(mediaListener);
		mediaListenerThread.setDaemon(true);
		mediaListenerThread.start();		

		// light listener
		Thread lightsListenerThread = new Thread(lights);
		lightsListenerThread.setDaemon(true);
		lightsListenerThread.start();	
		
	}

}
