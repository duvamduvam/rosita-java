package fr.duvam.media.player;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import fr.duvam.media.MediaLoading;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;

public class VideoPlayer {

	private static final Logger LOGGER = Logger.getLogger(VideoPlayer.class);

	private EmbeddedMediaPlayerComponent videoPlayerComponent;
	private EmbeddedMediaPlayer videoPlayer;
	MediaLoading mediaManager;

	private boolean defaultPlaying = true;
	private boolean repeat = false;
	private boolean isPlaying = false;

	public VideoPlayer(MediaLoading mediaManager, JFrame frame) {
		////////// video

		this.mediaManager = mediaManager;
		videoPlayerComponent = new EmbeddedMediaPlayerComponent(null, null, new AdaptiveFullScreenStrategy(frame), null,
				null);

		videoPlayer = videoPlayerComponent.mediaPlayer();

		videoPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void finished(final MediaPlayer mediaPlayer) {
				if (!repeat) {
					isPlaying = false;
				}
			}
		});

		frame.setContentPane(videoPlayerComponent);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}

	public EmbeddedMediaPlayer getPlayer() {
		return videoPlayer;
	}

	public void stop() {
		videoPlayer.controls().stop();
	}

	public synchronized void play(String video, boolean r) {
		stop();

		repeat = r;
		videoPlayer.controls().setRepeat(repeat);
		LOGGER.info("play video : " + video);
		videoPlayer.media().play(video);

		isPlaying = true;
		videoPlayer.fullScreen().set(true);

	}

	public void playGIF(String gif) {
		stopVideo();
		try {
			JFrame frame2 = new JFrame();
			URL url = Paths.get(gif).toUri().toURL();

			ImageIcon imageIcon = new ImageIcon(url);
			JLabel label = new JLabel(imageIcon);
			// label.setBounds(668, 43, 46, 14); // You can use your own values
			frame2.getContentPane().add(label);
			frame2.pack();
			frame2.setLocationRelativeTo(null);
			frame2.setExtendedState(JFrame.MAXIMIZED_BOTH);
			// frame2.setUndecorated(true);
			frame2.setVisible(true);

		} catch (MalformedURLException e) {
			LOGGER.error(e);
		}

	}

	public final void playDefault() {
		final String defaultVideo = mediaManager.getVideo(MediaLoading.KEY_VIDEO_BASE);
		play(defaultVideo, true);
	}

	public boolean isDefaultPlaying() {
		return defaultPlaying;
	}

	public void setDefaultPlaying(boolean defaultPlaying) {
		this.defaultPlaying = defaultPlaying;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public void stopVideo() {
		videoPlayer.controls().stop();
		// videoPlayerComponent.release();
	}

}
