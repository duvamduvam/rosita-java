package fr.duvam.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.duvam.lights.Lights;
import fr.duvam.media.PlayerManager;
import fr.duvam.media.player.AudioPlayer;

public class MediaListener implements Runnable {

	PlayerManager playerManager;
	CommandListener listener;
	Lights lights;

	private static Logger LOGGER = LoggerFactory.getLogger(MediaListener.class);

	public MediaListener(PlayerManager playerManager, CommandListener listener, Lights lights) {
		super();
		this.playerManager = playerManager;
		this.lights = lights;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LOGGER.error("error", e);
			}
			//if (playerManager.audioPlayer.isAudioTriggered()) {
			//	checkAudioRunning();
			//}
			if (!playerManager.videoPlayer.isPlaying() && !playerManager.videoPlayer.isDefaultPlaying()) {
				playerManager.videoPlayer.playDefault();
			}
			//if (Lights.mod != lights.getCurrentMod()) {
			//	lights.applyMod(Lights.mod);
			//}
		}
	}

}
