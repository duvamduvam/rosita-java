package fr.duvam.listener;

import java.io.File;

import java.util.EventObject;

public class FileEvent extends EventObject {

	private static final long serialVersionUID = 7780580027516736483L;

	public FileEvent(File file) {
		super(file);

	}

	public File getFile() {
		return (File) getSource();

	}

}
