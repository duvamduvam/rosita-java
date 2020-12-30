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

public class FileChangedWatcher2 implements Runnable {
	
	private static final Logger LOGGER = Logger.getLogger(FileChangedWatcher2.class);
	
	File file;
	CommandListener commandListener;
	
    public static void main(String[] args) {


        try {
            WatchService watchService
            = FileSystems.getDefault().newWatchService();

          Path path = Paths.get("/home/david/tmp");
			path.register(
			  watchService, 
			    StandardWatchEventKinds.ENTRY_CREATE, 
			      StandardWatchEventKinds.ENTRY_DELETE, 
			        StandardWatchEventKinds.ENTRY_MODIFY);
	        WatchKey key;
	        while ((key = watchService.take()) != null) {
	            for (WatchEvent<?> event : key.pollEvents()) {
	                System.out.println(
	                  "Event kind:" + event.kind() 
	                    + ". File affected: " + event.context() + ".");
	            }
	            key.reset();
	        }
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    }

	public FileChangedWatcher2(CommandListener commandListener) {
		PropertiesUtil properties = new PropertiesUtil();
		String arduinoOutFile = properties.getArduinoOutFile();
		file = new File(arduinoOutFile);
		this.commandListener = commandListener;
	}

	public void watch(){
		long currentModifiedDate = file.lastModified();

		while (true) {
			LOGGER.trace("check arduino file");
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
