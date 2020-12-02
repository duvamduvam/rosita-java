package fr.duvam.video;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import fr.duvam.KeyboardListener;
import fr.duvam.arduino.ArduinoComm;
import fr.duvam.lights.Lights;
import fr.duvam.midi.MidiHandler;
import fr.duvam.video.MediaManager.Type;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;

public class PlayerManager {

	private static final Logger LOGGER = Logger.getLogger(PlayerManager.class);

	private boolean defaultVideoPlaying = true;
	private boolean isPlaying = false;
	private boolean repeat = false;

	//ArduinoComm arduino;
	MidiHandler midi;

	private EmbeddedMediaPlayer videoPlayer;
	private EmbeddedMediaPlayerComponent videoPlayerComponent;
	private AudioPlayer audioPlayer;
	// private Clip audioPlayer;
	private JFrame frame;

	private final MediaManager mediaManager;

	public PlayerManager(MediaManager mediaManager, CommandListener commandListener, MidiHandler midi) {

		this.mediaManager = mediaManager;
		//this.arduino = arduino;
		this.midi = midi;

		frame = new JFrame();
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(100, 100);
		frame.setSize(1200, 800);

		frame.addKeyListener(new KeyboardListener(commandListener));

		initVideo();
		initAudio();
	}

	public boolean isDefaultVideoPlaying() {
		return defaultVideoPlaying;
	}

	public void setDefaultVideoPlaying(boolean defaultVideoPlaying) {
		this.defaultVideoPlaying = defaultVideoPlaying;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	private void initVideo() {
		////////// video

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
		frame.setVisible(true);
	}

	private void initAudio() {
		audioPlayer = new AudioPlayer();
	}

	public EmbeddedMediaPlayer getVideoPlayer() {
		return videoPlayer;
	}

	public void playDefaultVideo() {
		final String defaultVideo = mediaManager.getVideo(MediaManager.KEY_VIDEO_BASE);
		playVideo(defaultVideo, true);
	}

	public void playVideo(String video, boolean r) {
		// stopVideo();
		//initVideo();
		repeat = r;
		getVideoPlayer().controls().setRepeat(repeat);
		LOGGER.info("play video : " + video);
		videoPlayer.media().play(video);
		isPlaying = true;
		videoPlayer.fullScreen().set(true);
	}	
	
	public void stopVideo() {
		getVideoPlayer().controls().stop();
		//videoPlayerComponent.release();
	}

	public void play(String key) {

		LOGGER.info("fire : " + key);
		RositaMedia media = mediaManager.getMedia(key);

		if (media == null) {
			LOGGER.error("no media for key " + key);
			return;
		}

		Type type = media.getType();

		switch (type) {
		case GIF:
			// TODO make full screen integrated to jpane
			playGIF(media.getVideo());
			break;
		case VIDEO:
			String video = media.getVideo();
			playVideo(video, false);
			break;
		case VIDEOR:
			String videor = media.getVideo();
			playVideo(videor, true);
			break;
			
		//TODO use speak video with time frame ?
		case SPEAK:
			speak(media.getSound());
			break;
		case AUDIO_VIDEO:
			playAudioVideo(media.getSound(), media.getVideo());
			break;
		//case ARDUINO:
		//	arduino.sendString(media.getVideo());
		//	break;
		case LIGHTS:
			Lights.mod = Integer.parseInt(media.getVideo());
			break;
		case MIDI:
			midi.sendMsg();
			//Lights.mod = Integer.parseInt(media.getVideo());
			break;
		}
		defaultVideoPlaying = false;
	}

	public void speakAudio(String audio) {
		final String video = mediaManager.getVideo(MediaManager.KEY_VIDEO_SPEAK);
		playAudioVideo(audio, video);
	}

	public void playAudioVideo(String audio, String video) {
		playVideo(video, false);
		audioPlayer.play(audio);

	}

	public void speak(String audio) {
		speakAudio(audio);
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

	public AudioPlayer getAudioPlayer() {
		return audioPlayer;
	}

	public void setAudioPlayer(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;
	}

}