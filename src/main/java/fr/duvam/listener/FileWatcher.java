package fr.duvam.listener;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.log4j.Logger;

import fr.duvam.utils.PropertiesUtil;

public class FileWatcher implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(FileWatcher.class);
	protected List<FileListener> listeners = new ArrayList<>();
	CommandListener commandListener;
	protected final File folder;
	File file;

	protected static final List<WatchService> watchServices = new ArrayList<>();

	public FileWatcher(CommandListener keyListener) {
		this.commandListener=keyListener;
		PropertiesUtil properties = new PropertiesUtil();
		this.folder = new File(properties.getArduinoDir());
		String arduinoOutFile = properties.getArduinoOutFile();
		file = new File(arduinoOutFile);
	}

	public void watch() {

		if (folder.exists()) {

			Thread thread = new Thread(this);

			thread.setDaemon(true);

			thread.start();

		}

	}

	@Override
	public void run() {

		try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

			Path path = Paths.get(folder.getAbsolutePath());

			path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

			watchServices.add(watchService);

			boolean poll = true;

			while (poll) {

				poll = pollEvents(watchService);

			}

		} catch (IOException | InterruptedException | ClosedWatchServiceException e) {

			Thread.currentThread().interrupt();

		}

	}

	protected boolean pollEvents(WatchService watchService) throws InterruptedException {

		WatchKey key = watchService.take();

		Path path = (Path) key.watchable();

		for (WatchEvent<?> event : key.pollEvents()) {

			notifyListeners(event.kind(), path.resolve((Path) event.context()).toFile());

		}

		return key.reset();

	}

	
	
	
	protected void notifyListeners(WatchEvent.Kind<?> kind, File file) {

		FileEvent event = new FileEvent(file);
		LOGGER.info(kind);
		if (kind == ENTRY_CREATE) {
			for (FileListener listener : listeners) {
				listener.onCreated(event);
			}
			if (file.isDirectory()) {
				new FileWatcher(commandListener).setListeners(listeners).watch();
			}
		}

		else if (kind == ENTRY_MODIFY) {
			//for (FileListener listener : listeners) {
			//	listener.onModified(event);
			//}
			String key = getLastLine();
			LOGGER.info("new key : "+key);
			commandListener.addKey(key);
		}

		else if (kind == ENTRY_DELETE) {
			for (FileListener listener : listeners) {
				listener.onDeleted(event);
			}
		}

	}

	public FileWatcher addListener(FileListener listener) {

		listeners.add(listener);
		return this;

	}

	public FileWatcher removeListener(FileListener listener) {

		listeners.remove(listener);

		return this;

	}

	public List<FileListener> getListeners() {

		return listeners;

	}

	public FileWatcher setListeners(List<FileListener> listeners) {

		this.listeners = listeners;

		return this;

	}

	public static List<WatchService> getWatchServices() {

		return Collections.unmodifiableList(watchServices);

	}

	private String getLastLine() {

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
