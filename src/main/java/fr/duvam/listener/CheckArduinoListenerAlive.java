package fr.duvam.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.duvam.utils.FileChangedWatcher;

public class CheckArduinoListenerAlive implements Runnable {
	
	private static Logger LOGGER = LoggerFactory.getLogger(CheckArduinoListenerAlive.class);
	Thread arduinoThread;
	CommandListener keyListener;
	
	public CheckArduinoListenerAlive(Thread fileListenerThread, CommandListener keyListener) {
		arduinoThread = fileListenerThread;
		this.keyListener = keyListener;
	}
	
	public void run() {
		while (true) {
			try {
				arduinoThread.join();
				LOGGER.trace("arduino thread alive");
				Thread.sleep(1000);
			} catch (Exception e) {
				LOGGER.error("arduino thread dead");
				//arduinoThread = initArduinoListener(keyListener);
			}
		}
	}
	
	private Thread initArduinoListener(CommandListener keyListener) {

		// arduino listener
		FileChangedWatcher fileListener = new FileChangedWatcher(keyListener);
		Thread fileListenerThread = new Thread(fileListener);
		fileListenerThread.setDaemon(true);
		fileListenerThread.start();

		return fileListenerThread;
	}

}
