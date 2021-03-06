package fr.duvam.listener;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.log4j.Logger;

import fr.duvam.utils.PropertiesUtil;

public class FileWatcher implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(FileWatcher.class);
	CommandListener commandListener;
	WatchService watcher;
	Map<WatchKey, Path> keys;

	public static void main(String[] args) {

	}

	/**
	 * Creates a WatchService and registers the given directory
	 */
	public FileWatcher(CommandListener commandListener) {
		this.commandListener = commandListener;
		try {
			this.watcher = FileSystems.getDefault().newWatchService();
			this.keys = new HashMap<WatchKey, Path>();

			PropertiesUtil properties = new PropertiesUtil();
			String arduinoDir = properties.getArduinoDir();

			walkAndRegisterDirectories(Paths.get(arduinoDir));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Register the given directory with the WatchService; This function will be
	 * called by FileVisitor
	 */
	private void registerDirectory(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void walkAndRegisterDirectories(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				registerDirectory(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * Process all events for keys queued to the watcher
	 */
	void processEvents() {
		for (;;) {

			// wait for key to be signalled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				System.err.println("WatchKey not recognized!!");
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				@SuppressWarnings("rawtypes")
				WatchEvent.Kind kind = event.kind();

				// Context for directory entry event is the file name of entry
				@SuppressWarnings("unchecked")
				Path name = ((WatchEvent<Path>) event).context();
				Path child = dir.resolve(name);

				// print out event
				System.out.format("%s: %s\n", event.kind().name(), child);

				if (child.toString().contains("arduinoOut")) {
					String lastLine = getLastLine(child.toFile());
					LOGGER.info("marduino msg -> " + lastLine);
					commandListener.addKey(lastLine);
				}

				// if directory is created, and watching recursively, then register it and its
				// sub-directories
				if (kind == ENTRY_CREATE) {
					try {
						if (Files.isDirectory(child)) {
							walkAndRegisterDirectories(child);
						}
					} catch (IOException x) {
						// do something useful
					}
				}
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					break;
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LOGGER.trace("file watcher loop");
		}
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

	@Override
	public void run() {
		processEvents();

	}
}
