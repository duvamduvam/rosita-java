package fr.duvam.lights;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class LedBlock {

	List<Position> positions = new LinkedList<Position>();
	Integer position;
	Integer brightness;
	String color;
	Integer time;

	public LedBlock(Integer led, String color, Integer time) {
		this(led, led, color, time, null);
	}

	public LedBlock(Integer start, Integer end, String color, Integer time) {
		this(start, end, color, time, null);
	}

	public LedBlock(Integer start, Integer end, String color, Integer time, Integer brigthness) {
		this.time = time;
		this.brightness = brigthness;
		this.color = color;
		Position position = new Position(start, end);
		positions.add(position);
	}

	public LedBlock(List<Position> positions, String color, Integer time) {
		this(positions, color, time, null);
	}

	public LedBlock(List<Position> positions, String color, Integer time, Integer brigthness) {
		this.time = time;
		this.brightness = brigthness;
		this.color = color;
		this.positions = positions;
	}

	public String getLedPos() {
		String pos = "";
		for (Iterator<Position> positionIterator = positions.listIterator(); positionIterator.hasNext();) {
			Position position = positionIterator.next();
			if (position.getStart()==position.getEnd()) {
				pos = pos+ position.getStart();
			} else {
				pos = pos+  position.getStart() + "~" + position.getEnd();
			}
			if (positionIterator.hasNext()) {
				pos = pos + ":";
			}
		}
		return pos;
	}	
	
	public List<Position> getPositions() {
		return positions;
	}

	public void setPositions(List<Position> positions) {
		this.positions = positions;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getBrightness() {
		return brightness;
	}

	public void setBrightness(Integer brightness) {
		this.brightness = brightness;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
