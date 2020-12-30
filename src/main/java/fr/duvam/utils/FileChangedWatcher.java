package fr.duvam.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.log4j.Logger;

import fr.duvam.listener.CommandListener;

public class FileChangedWatcher implements Runnable {
	
	private static final Logger LOGGER = Logger.getLogger(FileChangedWatcher.class);
	
	File file;
	CommandListener commandListener;
	
	public static void main(String args[]) {
		//FileChangedWatcher watcher = new FileChangedWatcher("/home/david/tmp/out");
		//try {
		//	watcher.watch();
	}

	public FileChangedWatcher(CommandListener commandListener) {
		PropertiesUtil properties = new PropertiesUtil();
		String arduinoOutFile = properties.getArduinoOutFile();
		file = new File(arduinoOutFile);
		this.commandListener = commandListener;
	}

	public void watch(){
		long currentModifiedDate = file.lastModified();

		while (true) {
			long newModifiedDate = file.lastModified();

			if (newModifiedDate != currentModifiedDate) {
				currentModifiedDate = newModifiedDate;
				onModified();
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LOGGER.error(e);
			}
		}
	}

	public String getFilePath() {
		return file.getAbsolutePath();
	}

	protected void onModified() {

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

		LOGGER.info("marduino msg -> "+result);
		commandListener.addKey(result);
	}

	@Override
	public void run() {
		watch();
	}
}
