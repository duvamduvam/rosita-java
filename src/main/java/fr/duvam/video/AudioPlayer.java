package fr.duvam.video;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;
 
/**
 * This is an example program that demonstrates how to play back an audio file
 * using the Clip in Java Sound API.
 * @author www.codejava.net
 *
 */
public class AudioPlayer implements LineListener {
     
	private static final Logger LOGGER = Logger.getLogger(AudioPlayer.class);	
    /**
     * this flag indicates whether the playback completes or not.
     */
    private static boolean playCompleted;
	private static boolean isAudioTriggeredButNotStarted = false;
	private static boolean isAudioTriggered = false;
    /**
     * Play a given audio file.
     * @param audioFilePath Path of the audio file.
     */
    public void play(String audioFilePath) {
        File audioFile = new File(audioFilePath);
		isAudioTriggeredButNotStarted = true;
		isAudioTriggered = true;
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
 
            AudioFormat format = audioStream.getFormat();
 
            DataLine.Info info = new DataLine.Info(Clip.class, format);
 
            Clip audioClip = (Clip) AudioSystem.getLine(info);
 
            audioClip.addLineListener(this);
 
            audioClip.open(audioStream);
             
            audioClip.start();
             
            playCompleted = false;
            while (!playCompleted) {
                // wait for the playback completes
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
             
            audioClip.close();
             
        } catch (UnsupportedAudioFileException ex) {
        	LOGGER.info("The specified audio file is not supported.", ex);
        } catch (LineUnavailableException ex) {
        	LOGGER.info("Audio line for playing back is unavailable.", ex);
        } catch (IOException ex) {
        	LOGGER.info("Error playing the audio file.", ex);
        }
         
    }
 
	public boolean isAudioPlaying() {
		boolean isPlaying = isPlaying();
		if (isPlaying) {
			isAudioTriggeredButNotStarted = false;
		}
		return (isPlaying || isAudioTriggeredButNotStarted);
	}

	public boolean isAudioFinised() {
		boolean isPlaying = isAudioPlaying();
		boolean finished = isAudioTriggered && !isPlaying;
		if (finished && isAudioTriggered) {
			isAudioTriggered = false;
		}
		return finished;
	}

	public boolean isAudioTriggered() {
		return isAudioTriggered;
	}

    
    public boolean isPlaying() {
    	return !playCompleted;
    }
    
    /**
     * Listens to the START and STOP events of the audio line.
     */
    @Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();
         
        if (type == LineEvent.Type.START) {
            System.out.println("Playback started.");
             
        } else if (type == LineEvent.Type.STOP) {
            playCompleted = true;
            System.out.println("Playback completed.");
        }
 
    }
 
 
}