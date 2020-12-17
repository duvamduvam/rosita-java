package fr.duvam.media;

import org.apache.log4j.Logger;

import fr.duvam.media.MediaLoading.Type;



public class MediaItem {
	
	private static final Logger LOGGER = Logger.getLogger(MediaItem.class);
	
	Type type;
	String key;
	String sound;
	String video;

	public MediaItem(String key, Type type, String video, String sound) {
		LOGGER.info("loading media : key = "+key+", type = "+type.toString()+", video = "+video+", sound = "+sound);
		this.key = key;
		this.type = type;
		this.video = video;
		this.sound = sound;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSound() {
		return sound;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}
	
}

