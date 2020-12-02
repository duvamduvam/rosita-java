package fr.duvam.video;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandListener implements Runnable {

	private static Logger LOGGER = LoggerFactory.getLogger(CommandListener.class);

	private List<String> keyList;
	PlayerManager playerManager;
	MediaManager mediaManager;
	
	public void setPlayerManager(PlayerManager playerManager) {
		this.playerManager = playerManager;
	}

	public CommandListener(PlayerManager playerManager, MediaManager mediaManager) {
		keyList = new LinkedList<String>();
		this.playerManager = playerManager;
		this.mediaManager = mediaManager;
	}

	public void addKey(String key) {
		if (!keyList.contains(key)) {
			keyList.add(key);
		}
	}
	
	private void removeKey(String key) {
		keyList.remove(key);
	}
	
	private synchronized void checkEvent() {
		String toRemove = new String();
		synchronized (keyList) {
			try {
				for (String key : keyList) {
					playerManager.play(key);
					toRemove = key;
				}
				if (!toRemove.isEmpty()) {
					removeKey(toRemove);
				}
			
			} catch (ConcurrentModificationException e) {
				LOGGER.error("error when removing item");
			}
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LOGGER.error("erreur KeyListener", e);
			}
			checkEvent();
		}
	}
}
