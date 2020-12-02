package fr.duvam.midi;

import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import org.apache.log4j.Logger;

import fr.duvam.video.CommandListener;

public class MidiHandler {

	private static final Logger LOGGER = Logger.getLogger(MidiHandler.class);
	private String MIDI_LOOP = "ableton";
	CommandListener listener;
	MidiDevice device;
	Receiver receiver;

	public MidiHandler(CommandListener listener) {

		this.listener = listener;

		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++) {
			try {

				LOGGER.info(infos[i].getName());
				if (infos[i].getName().equals(MIDI_LOOP)) {

					device = MidiSystem.getMidiDevice(infos[i]);
					// get all transmitters
					List<Transmitter> transmitters = device.getTransmitters();
					// and for each transmitter
					LOGGER.info("number of transmitter :" + transmitters.size());
					for (int j = 0; j < transmitters.size(); j++) {
						// create a new receiver
						Receiver rcvr = new MidiInputReceiver(device.getDeviceInfo().toString());
						transmitters.get(j).setReceiver(rcvr);
						// test receiver
						sendMsg(rcvr);

					}

					Transmitter trans = device.getTransmitter();
					receiver = new MidiInputReceiver(device.getDeviceInfo().toString());
					trans.setReceiver(receiver);
					// test receiver
					// sendMsg(receiver);
					// open each device
					device.open();
					// if code gets this far without throwing an exception
					// print a success message
					LOGGER.info(device.getDeviceInfo() + " Was Opened");
					break;
				}
			} catch (MidiUnavailableException e) {
			}
		}

	}

	public void sendMsg(Receiver rcvr) {
		try {
			ShortMessage myMsg = new ShortMessage();
			myMsg.setMessage(ShortMessage.NOTE_ON, 1, 60, 93);
			long timeStamp = -1;
			rcvr.send(myMsg, timeStamp);
		} catch (Exception e) {
			LOGGER.error(e);
		}

	}

	public void sendMsg() {
		sendMsg(receiver);
	}

	/*public void send(MidiMessage msg) {
		
		MidiMessage myMsg = new MidiMessage();
		myMsg.setMessage(ShortMessage.NOTE_ON, 1, 60, 93);
		
		int size = msg.getLength();

		int bufSize = 200;

		if (bufSize == 0 || size <= bufSize) {
			receiver.send(msg, -1);
			// log("XMIT: ", msg);
		} else {
			// divide large System Exclusive Message into multiple
			// small messages.
			byte[] sysex = msg.getMessage();
			byte[] tmpArray = new byte[bufSize + 1];
			for (int i = 0; size > 0; i += bufSize, size -= bufSize) {
				try {
					int s = Math.min(size, bufSize);

					if (i == 0) {
						System.arraycopy(sysex, i, tmpArray, 0, s);
						((SysexMessage) msg).setMessage(tmpArray, s);
					} else {
						tmpArray[0] = (byte) SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE;
						System.arraycopy(sysex, i, tmpArray, 1, s);
						((SysexMessage) msg).setMessage(tmpArray, ++s);
					}
					receiver.send(msg, -1);
					// log("XMIT: ", msg);

					Thread.sleep(30);
				} catch (Exception e) {
					// do nothing
				}
			}
		}
	}*/

//tried to write my own class. I thought the send method handles an MidiEvents sent to it
	public class MidiInputReceiver implements Receiver {
		public String name;

		public MidiInputReceiver(String name) {
			this.name = name;
		}

		public void send(MidiMessage msg, long timeStamp) {
			ShortMessage sm = (ShortMessage)msg;

			int ch = ((ShortMessage) msg).getChannel();
			
			LOGGER.info("channel : " + ch + " | data1 : " + sm.getData1() + " | data2 : " + sm.getData2() );
			String midiMessage = Integer.toString(sm.getData1());
			listener.addKey(midiMessage);
		}

		public void close() {
		}
	}

	public MidiEvent makeEvent(int command, int channel, int note, int velocity, int tick) {

		MidiEvent event = null;

		try {

// ShortMessage stores a note as command type, channel, 
// instrument it has to be played on and its speed. 
			ShortMessage a = new ShortMessage();
			a.setMessage(command, channel, note, velocity);

// A midi event is comprised of a short message(representing 
// a note) and the tick at which that note has to be played 
			event = new MidiEvent(a, tick);
		} catch (Exception ex) {

			ex.printStackTrace();
		}
		return event;
	}

}