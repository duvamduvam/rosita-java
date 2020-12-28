package fr.duvam;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesUtil {

	private static String token = "|";
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

	public String getString(String key) {
		return prop.getProperty(key);
	}

	public List<String> getList(String key) {
		String p =  prop.getProperty(key);

		if(p.contains(token)) {
			String[] split = p.split("\\|");
			return Arrays.asList(split);
		}
		
		List<String> list = new LinkedList<String>();
		list.add(p);
		return list;
	}

	
}
