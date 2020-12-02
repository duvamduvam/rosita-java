package fr.duvam.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.duvam.lights.Lights;

public class MediaListener implements Runnable {

	AudioPlayer audioPlayer;
	PlayerManager playerManager;
	CommandListener listener;
	Lights lights;

	private static Logger LOGGER = LoggerFactory.getLogger(MediaListener.class);

	public MediaListener(PlayerManager playerManager, CommandListener listener, Lights lights) {
		super();
		this.audioPlayer = new AudioPlayer();
		this.playerManager = playerManager;
		this.lights = lights;
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
				LOGGER.error("erreur ArduinoListener", e);
			}
			if (audioPlayer.isAudioTriggered()) {
				checkRunning();
			}
			if (!playerManager.isPlaying() && !playerManager.isDefaultVideoPlaying()) {
				playerManager.playDefaultVideo();
			}
			//if (Lights.mod != lights.getCurrentMod()) {
			//	lights.applyMod(Lights.mod);
			//}
		}
	}

}
