package fr.duvam.lights;

import java.util.Random;

public class Color {

	private static String prefix = "0x";
	public static String BLACK = prefix+"000000";
	public static String BLUE = prefix+"FF44DD";
	public static String BLUEMARINE = prefix+"254d6b";
	public static String BROWN = prefix+"68091b";
	public static String CYAN = prefix+"00FFFF";
	public static String GREEN = prefix+"6daf21";	
	public static String RED = prefix+"ff4500";
	public static String VIOLET = prefix+"9932cc";	
	
	
	
	
	public static String randomHexaColor() {
		Random random = new Random();
		// create a big random number - maximum is ffffff (hex) = 16777215 (dez)
		int nextInt = random.nextInt(0xffffff + 1);
		// format it as hexadecimal string (with hashtag and leading zeros)
		String colorCode = String.format(prefix+"%06x", nextInt);
		// print it
		return colorCode;
	}	
	
}
