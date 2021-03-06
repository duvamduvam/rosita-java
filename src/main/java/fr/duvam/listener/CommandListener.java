package fr.duvam.listener;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.duvam.media.MediaLoading;
import fr.duvam.media.PlayerManager;

public class CommandListener implements Runnable {

	private static Logger LOGGER = LoggerFactory.getLogger(CommandListener.class);

	private static List<String> keyList = new LinkedList<String>();
	PlayerManager playerManager;

	private boolean checkEvent = false;
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public void setPlayerManager(PlayerManager playerManager) {
		this.playerManager = playerManager;
	}

	public void addKey(String key) {
		while(checkEvent) {
			// avoid ConcurrentModificationException
		}
		if (!keyList.contains(key)) {
			keyList.add(key);
		}
	}

	private void removeKey(String key) {
		keyList.remove(key);
	}

	private synchronized void checkEvent() {
		String toRemove = new String();
		try {
			checkEvent = true;
			for (String key : keyList) {
				playerManager.play(key);
				toRemove = key;
			}
			checkEvent = false;
			if (!toRemove.isEmpty()) {
				removeKey(toRemove);
			}

		} catch (ConcurrentModificationException e) {
			LOGGER.error("error when removing item");
			//checkEvent();
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
