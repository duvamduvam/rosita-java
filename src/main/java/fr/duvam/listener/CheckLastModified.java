package fr.duvam.listener;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.log4j.Logger;

import fr.duvam.media.PlayerManager;
import fr.duvam.utils.PropertiesUtil;

public class CheckLastModified implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(CheckLastModified.class);
	
	CommandListener commandListener;
	private PlayerManager playerManager;
	
	public CheckLastModified(PlayerManager playerManager,CommandListener commandListener) {
		this.commandListener = commandListener;
		this.playerManager = playerManager;
	}

	@Override
	public void run() {
		PropertiesUtil properties = new PropertiesUtil();
		String arduinoOutFile = properties.getArduinoOutFile();
		File file = new File(arduinoOutFile);

		long lastModif = file.lastModified();

		while (true) {

			if (file.lastModified() != lastModif) {
				
				String lastLine = getLastLine(file);
				LOGGER.info("new arduino input : "+lastLine);
				
				playerManager.play(lastLine);
				
				//commandListener.addKey(lastLine);
				
				lastModif = file.lastModified() ;
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LOGGER.error(e);
			}

		}
		// TODO Auto-generated method stub

	}
	
	protected String getLastLine(File file) {

		int n_lines = 1;
		ReversedLinesFileReader object;
		String result = "";
		try {
			object = new ReversedLinesFileReader(file);
			for (int i = 0; i < n_lines; i++) {
				String line = object.readLine();
				if (line == null)
					break;
				result += line;
			}
		} catch (IOException e) {
			LOGGER.error(e);
		}

		return result;

	}
	
}
