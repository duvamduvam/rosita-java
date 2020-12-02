package fr.duvam.lights;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import fr.duvam.arduino.ArduinoComm;

public class Lights implements Runnable {

	// #100~130:200~300|0xFF44DD
	private static final Logger LOGGER = Logger.getLogger(Lights.class);
	ArduinoComm arduino;

	public static Integer MAX = 268;

	static Integer LINE_FRONT_BOTTOM_LEFT_START = 0;
	static Integer LINE_FRONT_BOTTOM_LEFT_END = 11;
	static Integer LINE_FRONT_UP_LEFT_START = 12;
	static Integer LINE_FRONT_UP_LEFT_END = 36;
	static Integer LINE_FRONT_UP_RIGHT_START = 37;
	static Integer LINE_FRONT_UP_RIGHT_END = 61;
	static Integer LINE_FRONT_BOTTOM_RIGHT_START = 62;
	static Integer LINE_FRONT_BOTTOM_RIGHT_END = 73;
	static Integer LINE_FRONT_MIDDLE_DOWN_START = 74;
	static Integer LINE_FRONT_MIDDLE_DOWN_END = 78;
	static Integer LINE_FRONT_MIDDLE_UP_START = 79;
	static Integer LINE_FRONT_MIDDLE_UP_END = 88;
	static Integer LEFT_DOWN_START = 89;
	static Integer LEFT_DOWN_END = 115;
	static Integer BACK_START = 116;
	static Integer BACK_END = 145;
	static Integer LEFT_UP_START = 146;
	static Integer LEFT_UP_END = 175;
	static Integer UP_LEFT_START = 176;
	static Integer UP_LEFT_END = 192;
	static Integer UP_RIGHT_START = 193;
	static Integer UP_RIGHT_END = 213;
	static Integer RIGHT_UP_START = 214;
	static Integer RIGHT_UP_END = 239;
	static Integer RIGHT_DOWN_START = 240;
	static Integer RIGHT_DOWN_END = 268;

	static String BLACK = "0xFF44DD";

	List<LedBlock> currentBlockList = new LinkedList<LedBlock>();

	public static Integer mod = 0;
	Integer currentMod = 0;

	Integer nbBlocks;
	Integer currentBlockNb;
	Integer currentPos;
	Integer updateInterval;
	Integer lastUpdate;
	boolean loop;

	public Lights(ArduinoComm arduino) {
		this.arduino = arduino;
	}

	public Integer getCurrentMod() {
		return currentMod;
	}
	
	public void applyMod(Integer m) {
		currentMod = m;
		mod = m;

		switch (mod) {
		case 0:
			currentBlockList = allBlack();
			loop = false;
			break;
		case 1:
			currentBlockList = fullRandom();
			loop = true;
			break;
		case 2:
			currentBlockList = frontLines(Color.BLUE, 2500);
			loop = true;
			break;
		case 3:
			currentBlockList = frontLines(Color.GREEN, 2500);
			loop = true;
			break;
		case 4:
			currentBlockList = frontLines(Color.VIOLET, 2500);
			loop = true;
			break;
		case 5:
			currentBlockList = frontLines(Color.RED, 2500);
			loop = true;
			break;
		}
		sendLight();
	}

	void sendLight() {

		while (loop) {
			for (ListIterator<LedBlock> blocksIterator = currentBlockList.listIterator(); blocksIterator.hasNext();) {

				LedBlock block = blocksIterator.next();
				String ledsMsg = generateLedString(block.getLedPos(), block.color);
				LOGGER.info("mod " + currentMod + " send : " + ledsMsg);
				arduino.sendString(ledsMsg);
				try {
					Thread.sleep(block.getTime());
				} catch (InterruptedException e) {
					LOGGER.error(e);
				}
				LOGGER.info("check currentMod : " + currentMod + " mod : " + mod);
				if (mod != currentMod) {
					return;
				}
			}
		}

	}

	String generateLedString(String pos, String color) {
		return pos + "|" + color;
	}

	List<LedBlock> allBlack() {
		List<LedBlock> blockList = new LinkedList<LedBlock>();
		LedBlock block = new LedBlock(0, MAX, Color.BLACK, 1000);
		blockList.add(block);
		return blockList;
	}

	List<LedBlock> fullRandom() {
		List<LedBlock> blockList = new LinkedList<LedBlock>();
		for (int i = 0; i < MAX; i++) {
			//blockList.add(new LedBlock(i, i, Color.randomHexaColor(), 1000));
			blockList.add(new LedBlock(i, i, Color.randomHexaColor(), 2000));
		}

		return blockList;
	}

	List<LedBlock> frontLines(String color, int time) {
		List<LedBlock> blockList = new LinkedList<LedBlock>();
		List<Position> positions1 = new LinkedList<Position>();
		positions1.add(new Position(LINE_FRONT_BOTTOM_LEFT_START, LINE_FRONT_BOTTOM_LEFT_END));
		positions1.add(new Position(LINE_FRONT_UP_LEFT_START, LINE_FRONT_UP_LEFT_END));
		positions1.add(new Position(LINE_FRONT_BOTTOM_RIGHT_START, LINE_FRONT_BOTTOM_RIGHT_END));
		positions1.add(new Position(LINE_FRONT_UP_RIGHT_START, LINE_FRONT_UP_RIGHT_END));
		blockList.add(new LedBlock(positions1, color, time));

		List<Position> positions2 = new LinkedList<Position>();
		positions2.add(new Position(LINE_FRONT_MIDDLE_UP_START, LINE_FRONT_MIDDLE_UP_END));
		positions2.add(new Position(LINE_FRONT_MIDDLE_DOWN_START, LINE_FRONT_MIDDLE_DOWN_END));

		blockList.add(new LedBlock(positions2, color, time));

		return blockList;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOGGER.error("erreur light listener", e);
			}
			//LOGGER.info("check currentMod : " + currentMod + " mod : " + mod);
			if (mod != currentMod) {
				applyMod(mod);
			}
		}
	}

}
