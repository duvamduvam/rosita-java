package fr.duvam.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import org.apache.log4j.Logger;

import fr.duvam.utils.PropertiesUtil;


//import com.sun.media.sound.MidiOutDevice;

public class MidiPlayer {

	private static final Logger LOGGER = Logger.getLogger(MidiPlayer.class);

	PropertiesUtil properties;
	Receiver receiver;

	public MidiPlayer(PropertiesUtil properties) {

		this.properties = properties;

		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		String midiOut = properties.getString("midi.out");
		for (int i = 0; i < infos.length; i++) {
			try {

				if (infos[i].getName().equals(midiOut)) {
					LOGGER.info("set up midi transmitter "+midiOut);

					MidiDevice device = MidiSystem.getMidiDevice(infos[i]);

					if (device.getReceiver() != null) {
						receiver = device.getReceiver();
						LOGGER.info("receiver from " + midiOut + " set");
					}

					device.open();
					LOGGER.info(device.getDeviceInfo() + " Was Opened");
				}
				
			} catch (MidiUnavailableException e) {
				LOGGER.error(e);
			}
		}

	}

	public void sendMsg(String note) {
		LOGGER.info(" send midi note " + note);
		
		ShortMessage myMsg = new ShortMessage();
		try {
			myMsg.setMessage(ShortMessage.NOTE_ON, 1, Integer.parseInt(note), 93);
		} catch (InvalidMidiDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// MidiMessage myMsg = new ShortMessage(ShortMessage.NOTE_ON, 90, 93);
		receiver.send(myMsg, -1);
		try {
			Thread.sleep(100); // wait time in milliseconds to control duration
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}

		try {
			myMsg.setMessage(ShortMessage.NOTE_OFF, 1, 60, 93);
		} catch (InvalidMidiDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		receiver.send(myMsg, -1);

		// sendMsg(receiver, Integer.parseInt(note));
	}
	

}