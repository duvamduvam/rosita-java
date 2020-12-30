package fr.duvam.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.output.ByteArrayOutputStream;

public class ExtCommands {

	public static void main(String[] args) throws Exception {
		CommandLine cmdLine = new CommandLine("python3");
		cmdLine.addArgument("/home/david/Nextcloud/rosita/java/py/serialCom.py");

		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		//CommandLine cmdLine = new CommandLine(p4d);
		//cmdLine.addArgument("-V");
		//DefaultExecutor executor = new DefaultExecutor();
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

		
		ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
		Executor executor = new DefaultExecutor();
		executor.setExitValue(1);
		executor.setWatchdog(watchdog);
		executor.setStreamHandler(streamHandler);
		executor.execute(cmdLine, resultHandler);
		
		
		
		

		// some time later the result handler callback was invoked so we
		// can safely request the exit value
		
		
		
		//resultHandler.waitFor();
		
		//resultHandler.
		
		//resultHandler.
		
	}
}

class CollectingLogOutputStream implements ExecuteStreamHandler {
	private final List<String> lines = new LinkedList<String>();

	public void setProcessInputStream(OutputStream outputStream) throws IOException {

	}

	public void setProcessErrorStream(InputStream inputStream) throws IOException {
		// InputStream is = ...; // keyboard, file or Internet
		InputStreamReader isr = new InputStreamReader(inputStream);
		BufferedReader br = new BufferedReader(isr);
		String line = "";
		while ((line = br.readLine()) != null) {
			// use lines whereever you want - for now just print on console
			System.out.println("error:" + line);
		}
	}

	public void setProcessOutputStream(InputStream inputStream) throws IOException {
		InputStreamReader isr = new InputStreamReader(inputStream);
		BufferedReader br = new BufferedReader(isr);
		String line = "";
		while ((line = br.readLine()) != null) {
			// use lines whereever you want - for now just print on console
			System.out.println("output:" + line);
		}
	}

	public void start() throws IOException {

	}

	public void stop() throws IOException {

	}
}
