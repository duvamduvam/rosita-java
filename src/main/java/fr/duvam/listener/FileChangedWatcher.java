package fr.duvam.listener;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.log4j.Logger;

import fr.duvam.utils.PropertiesUtil;

public class FileChangedWatcher implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(FileChangedWatcher.class);

	File file;
	Path path;
	CommandListener commandListener;

	public static void main(String[] args) {

	}

	public FileChangedWatcher(CommandListener commandListener) {
		PropertiesUtil properties = new PropertiesUtil();
		String arduinoOutFile = properties.getArduinoOutFile();
		file = new File(arduinoOutFile);
		path = Paths.get(properties.getArduinoDir());
		this.commandListener = commandListener;

	}

	public void watch() {
		try {
			
			LOGGER.info("start watch "+path.toString());
			
			WatchService watchService = FileSystems.getDefault().newWatchService();

			// Only the delete event is fired once

			path.register(watchService,  
				        StandardWatchEventKinds.ENTRY_MODIFY);
			WatchKey key;
			while ((key = watchService.take()) != null) {
				for (WatchEvent<?> event : key.pollEvents()) {

					String lastLine = getLastLine();
					LOGGER.info("marduino msg -> " + lastLine);
					commandListener.addKey(lastLine);

				}
				key.reset();
			}
		} catch (IOException | InterruptedException e) {
			LOGGER.error(e);
		}
	}

	public String getFilePath() {
		return file.getAbsolutePath();
	}

	protected String getLastLine() {

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

	@Override
	public void run() {
		watch();
	}
}
