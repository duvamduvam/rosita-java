package fr.duvam.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import org.apache.log4j.Logger;

import fr.duvam.PropertiesUtil;
import fr.duvam.listener.CommandListener;


//import com.sun.media.sound.MidiOutDevice;

public class MidiListener {

	private static final Logger LOGGER = Logger.getLogger(MidiListener.class);

	PropertiesUtil properties;
	CommandListener commands;


	public MidiListener(CommandListener commands, PropertiesUtil properties) {

		this.commands = commands;
		this.properties = properties;

		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

		String midiIn = properties.getString("midi.in");
		for (int i = 0; i < infos.length; i++) {
			try {

				if (infos[i].getName().equals(midiIn)) {
					
					MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
					LOGGER.info("set up midi listener "+midiIn);
					Transmitter trans = device.getTransmitter();
					Receiver receiver = new MidiInputReceiver(device.getDeviceInfo().toString());					
					trans.setReceiver(receiver);
					device.open();
					
					LOGGER.info(device.getDeviceInfo() + " Was Opened");
					break;
				}
			} catch (MidiUnavailableException e) {
				LOGGER.error(e);
			}
		}

	}

//tried to write my own class. I thought the send method handles an MidiEvents sent to it
	public class MidiInputReceiver implements Receiver {
		public String name;

		public MidiInputReceiver(String name) {
			this.name = name;
		}

		public void send(MidiMessage msg, long timeStamp) {
			ShortMessage sm = (ShortMessage) msg;
			int ch = ((ShortMessage) msg).getChannel();
			LOGGER.info(name+" | channel : " + ch + " | data1 : " + sm.getData1() + " | data2 : " + sm.getData2());
			commands.addKey(sm.getData1()+"");
		}
		
		public void close() {
		}
	}



}