package fr.duvam.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.script.ScriptContext;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;

import jodd.io.StreamGobbler;

public class ShellCommands {

	// private static final Logger LOGGER = Logger.getLogger(ShellCommands.class);

	List<String> pullCommand = new LinkedList<String>();

	public ShellCommands() {

		pullCommand.add("git");
		pullCommand.add("pull");
	}

	public static void main(String[] args) {

		CommandLine cmdLine = new CommandLine("python3");
		cmdLine.addArgument("/home/david/Nextcloud/rosita/java/py/serialCom.py");
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

		ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
		Executor executor = new DefaultExecutor();
		executor.setExitValue(1);
		executor.setWatchdog(watchdog);
		try {
			executor.execute(cmdLine, resultHandler);
			resultHandler.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// some time later the result handler callback was invoked so we
		// can safely request the exit value


		/*
		 * try { Process p =
		 * Runtime.getRuntime().exec("/home/david/Nextcloud/rosita/java/py/serialCom.py"
		 * ); } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		/*
		 * try { StringWriter writer = new StringWriter(); // ouput will be stored here
		 * 
		 * ScriptEngineManager manager = new ScriptEngineManager(); ScriptContext
		 * context = new SimpleScriptContext();
		 * 
		 * context.setWriter(writer); // configures output redirection
		 * "/usr/bin/python3");
		 * 
		 * engine.eval(new
		 * FileReader("/home/david/Nextcloud/rosita/java/py/serialCom.py"), context);
		 * System.out.println(writer.toString()); } catch (FileNotFoundException |
		 * ScriptException e) { // TODO Auto-generated catch block e.printStackTrace();
		 * }
		 */

		/*
		 * String dir = "/home/david/Nextcloud/rosita/java/py";
		 * 
		 * List<String> command= new LinkedList<String>();
		 * command.add("./serialCom.py"); ShellCommands shellCommand = new
		 * ShellCommands(); shellCommand.run(dir, command);
		 */

		/*
		 * List<String> command= new LinkedList<String>(); command.add("ls"); String dir
		 * = "/var/lock"; ShellCommands shellCommand = new ShellCommands();
		 * shellCommand.run(dir, command);
		 */

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
			// LOGGER.error(e);
		}
	}

	public void pull() {
		PropertiesUtil props = new PropertiesUtil();
		String dir = props.getPullPath();
		run(dir, pullCommand);
	}

}
