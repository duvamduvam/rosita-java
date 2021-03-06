package fr.duvam.media;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import fr.duvam.arduino.ArduinoComm;
import fr.duvam.lights.Lights;
import fr.duvam.listener.CommandListener;
import fr.duvam.listener.KeyboardListener;
import fr.duvam.media.player.AudioPlayer;
import fr.duvam.media.player.VideoPlayer;
import fr.duvam.utils.ShellCommands;

public class PlayerManager {

	private static final Logger LOGGER = Logger.getLogger(PlayerManager.class);

	public AudioPlayer audioPlayer;
	public VideoPlayer videoPlayer;

	private JFrame frame;

	private final MediaLoading mediaLoading;

	public PlayerManager(CommandListener commandListener) {

		this.mediaLoading = new MediaLoading();

		frame = new JFrame();
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(100, 100);
		frame.setSize(1200, 800);

		frame.addKeyListener(new KeyboardListener(commandListener));

		videoPlayer = new VideoPlayer(mediaLoading, frame);
		audioPlayer = new AudioPlayer(videoPlayer, mediaLoading);

	}

	public void play(String key) {

		try {

			MediaItem media = mediaLoading.getMedia(key);

			if (media == null) {
				LOGGER.info("no media for key " + key);
				return;
			}

			LOGGER.info(media.getType().toString() + " key : " + key + " video : " + media.getVideo() + " sound "
					+ media.getSound());
			switch (media.getType()) {
			case GIF:
				// TODO make full screen integrated to jpane
				videoPlayer.playGIF(media.getVideo());
				break;
			case VIDEO:
				String video = media.getVideo();
				videoPlayer.play(video, false);
				break;
			case VIDEOR:
				String videor = media.getVideo();
				videoPlayer.play(videor, true);
				break;

			// TODO use speak video with time frame ?
			// case SPEAK:
			// audioPlayer.speak(media.getSound());
			// break;
			case AUDIO_VIDEO:
				playAudioVideo(media.getSound(), media.getVideo());
				break;
			case ARDUINO:
				// arduino.sendString(media.getVideo());
				break;
			case LIGHTS:
				Lights.mod = Integer.parseInt(media.getVideo());
				break;
			// case MIDI:
			// midi.sendMsg(media.getVideo());
			// Lights.mod = Integer.parseInt(media.getVideo());
			// break;
			case EMOTION:
				break;
			case SPEAK:
				break;
			case RELOAD:
				ShellCommands shell = new ShellCommands();
				shell.pull();
				mediaLoading.loadMediaFile();
				break;
			}
			videoPlayer.setDefaultPlaying(false);
		} catch (NullPointerException e) {
			LOGGER.error(e);
		}
	}

	public void playAudioVideo(String audio, String video) {
		videoPlayer.play(video, true);
		audioPlayer.play(audio);
	}

}