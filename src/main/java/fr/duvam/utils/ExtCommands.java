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

public class ExtCommands {

	public static void main(String[] args) throws Exception {
		CommandLine cmdLine = new CommandLine("python3");
		cmdLine.addArgument("//home/david/Nextcloud/rosita/java/py/serialCom.py");
		// cmdLine.addArgument("/h");
		// cmdLine.addArgument("${file}");

		// HashMap map = new HashMap();
		// map.put("file", new File("invoice.pdf"));
		// cmdLine.setSubstitutionMap(map);

		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

		ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
		Executor executor = new DefaultExecutor();
		executor.setExitValue(1);
		executor.setWatchdog(watchdog);
		executor.setStreamHandler(new CollectingLogOutputStream());
		executor.execute(cmdLine, resultHandler);

		// some time later the result handler callback was invoked so we
		// can safely request the exit value
		resultHandler.waitFor();
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
