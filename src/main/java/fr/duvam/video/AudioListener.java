package fr.duvam.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AudioListener implements Runnable {

	private static Logger LOGGER = LoggerFactory.getLogger(AudioListener.class);

	private AudioPlayer audioPlayer;
	private PlayerManager playerManager;

	public AudioListener(PlayerManager playerPlayer) {
		super();
		this.audioPlayer = new AudioPlayer();
		this.playerManager = playerPlayer;
	}

	private void checkRunning() {
		if (audioPlayer.isAudioFinised()) {
			playerManager.playDefaultVideo();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LOGGER.error("erreur AudioListener", e);
			}
			if (audioPlayer.isAudioTriggered()) {
				checkRunning();
			}
		}
	}

}
