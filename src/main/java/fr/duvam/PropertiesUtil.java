package fr.duvam;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesUtil {

	Logger LOGGER = Logger.getLogger(Properties.class);
	Properties prop = new Properties();

	public PropertiesUtil() {

		try {
			prop.load(new FileInputStream("conf/config.properties"));
			LOGGER.debug("properties loaded");
		} catch (IOException e) {
			LOGGER.error("can't load properties", e);
		}
	}

	public String get(String key) {
		return prop.getProperty(key);
	}

}
