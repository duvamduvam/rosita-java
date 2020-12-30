package fr.duvam.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesUtil {

	private static String token = "|";
	// problem with properties loading
	// Logger LOGGER = Logger.getLogger(Properties.class);
	Properties prop = new Properties();

	public PropertiesUtil() {

		try {
			prop.load(new FileInputStream("conf/config.properties"));
		} catch (IOException e) {
			// LOGGER.error("can't load properties", e);
		}
	}

	public String getString(String key) {
		return prop.getProperty(key);
	}

	public List<String> getList(String key) {
		String p = prop.getProperty(key);

		if (p.contains(token)) {
			String[] split = p.split("\\|");
			return Arrays.asList(split);
		}

		List<String> list = new LinkedList<String>();
		list.add(p);
		return list;
	}

	public String getArduinoOutFile() {
		switch (OSValidator.getOS()) {
		case PI:
			return getString("pi.arduino.out");
		case DEBIAN:
			return getString("debian.arduino.out");
		}
		return "";
	}
	
	public String getLogPath() {
		switch (OSValidator.getOS()) {
		case WIN:
			return getString("logs.win");
		case PI:
			return getString("logs.pi");
		case DEBIAN:
			return getString("logs.linux");
		}
		return "";
	}

	public String getPullPath() {
		switch (OSValidator.getOS()) {
		case WIN:
			return getString("pull.win.dir");
		case PI:
			return getString("pull.pi.dir");
		case DEBIAN:
			return getString("pull.linux.dir");
		}
		return "";
	}

	
}
