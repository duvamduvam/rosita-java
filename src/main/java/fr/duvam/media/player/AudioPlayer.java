package fr.duvam.media.player;

import org.apache.log4j.Logger;

import fr.duvam.media.MediaLoading;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent;

public class AudioPlayer {

	private static final Logger LOGGER = Logger.getLogger(AudioPlayer.class);


	private final VideoPlayer videoPlayer;
	private MediaPlayer mediaPlayer;
	AudioPlayerComponent mediaPlayerComponent;

	public AudioPlayer(VideoPlayer videoPlayer, MediaLoading mediaManager) {
		mediaPlayerComponent = new AudioPlayerComponent();
		mediaPlayer = mediaPlayerComponent.mediaPlayer();
		this.videoPlayer = videoPlayer;
		mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void finished(final MediaPlayer mediaPlayer) {
				videoPlayer.playDefault();
			}
			@Override
			public void stopped(final MediaPlayer mediaPlayer) {
				videoPlayer.playDefault();
			}
		});
	}

	public synchronized void play(String mrl) {
		mediaPlayer.controls().stop();
		mediaPlayer = mediaPlayerComponent.mediaPlayer();
		mediaPlayer.media().play(mrl);
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			LOGGER.error("error", e);
		}
	}



}