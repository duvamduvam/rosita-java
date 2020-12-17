package fr.duvam.listener;

import java.awt.event.KeyListener;

import org.apache.log4j.Logger;

public class KeyboardListener implements KeyListener {

	private static final Logger LOGGER = Logger.getLogger(KeyboardListener.class);

	CommandListener commandListener;

	public KeyboardListener(CommandListener commandListener) {
		super();
		this.commandListener = commandListener;
	}

	@Override
	public void keyTyped(java.awt.event.KeyEvent e) {
		//LOGGER.info(e);
	}

	@Override
	public void keyPressed(java.awt.event.KeyEvent e) {
		//LOGGER.info(e);
	}

	@Override
	public void keyReleased(java.awt.event.KeyEvent e) {
		LOGGER.info(e);
		String command = String.valueOf(e.getKeyChar());
		commandListener.addKey(command);
	}

}
