package fr.duvam.utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import jodd.io.StreamGobbler;

public class ShellCommands {

	private static final Logger LOGGER = Logger.getLogger(ShellCommands.class);
	
	List<String> pullCommand = new LinkedList<String>();

	public ShellCommands(){
		pullCommand.add("git");
		pullCommand.add("pull");
	}
	
	public static void main(String[] args) {

		
		List<String> command= new LinkedList<String>();
		command.add("rm");
		command.add("LCK..ttyS0");
		String dir = "/var/lock";
		ShellCommands shellCommand = new ShellCommands();
		shellCommand.run(dir, command);

	}
	
	public void run(String dir, List<String> command) {

		ProcessBuilder builder = new ProcessBuilder();

		builder.command(command);

		builder.directory(new File(dir));
		Process process;
		try {
			process = builder.start();
			StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out);
			Executors.newSingleThreadExecutor().submit(streamGobbler);
			int exitCode;
			exitCode = process.waitFor();
			assert exitCode == 0;
		} catch (IOException | InterruptedException e) {
			LOGGER.error(e);
		}
	}
	
	public void pull() {
		PropertiesUtil props = new PropertiesUtil();
		String dir = props.getPullPath();
		run(dir, pullCommand);
	}

}
