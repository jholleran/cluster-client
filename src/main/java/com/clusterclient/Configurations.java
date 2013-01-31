package com.clusterclient;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class Configurations {

	private static final String THREAD_TIMEOUT = "threadTimeout";
	private static final String THREADS_NUM = "threadsNum";
	private static final String ENV_REPOSITORY = "env-repository";
	private static final String TERMINAL_BUFFER = "terminal-buffer";
	private static final String MODES = "modes";
	private static final String MODE = "mode";
	private static final String SSH_PORT = "ssh-port";

	private final static Logger LOGGER = Logger.getLogger(Configurations.class
			.getName());

	private final Properties properties = new Properties();

	public Configurations(String fileName) {
		loadDefaults();
		LOGGER.info("Loading property file " + fileName);
		try {
			properties.load(new FileInputStream(fileName));
		} catch (Exception e) {
			LOGGER.warning("Unable to load properties " + fileName
					+ "\nUsing default values");
		}
	}

	private void loadDefaults() {
		properties.setProperty(THREAD_TIMEOUT, String.valueOf(500));
		properties.setProperty(THREADS_NUM, String.valueOf(10));
		properties.setProperty(ENV_REPOSITORY, "environments.xml");
		properties.setProperty(TERMINAL_BUFFER, String.valueOf(1000));
		properties.setProperty(MODES, "shell;sftp");
		properties.setProperty(MODE, "shell");
		properties.setProperty(SSH_PORT, "22");
	}

	public int getThreadTimeout() {
		return Integer.parseInt(properties.getProperty(THREAD_TIMEOUT));
	}

	public int getThreadsNum() {
		return Integer.parseInt(properties.getProperty(THREADS_NUM));
	}

	public String getEnvironmentsFile() {
		return properties.getProperty(ENV_REPOSITORY);
	}

	public int getTerminalBuffer() {
		return Integer.parseInt(properties.getProperty(TERMINAL_BUFFER));
	}

	public String getMode() {
		return properties.getProperty(MODE);
	}

	public List<String> getModes() {
		String modes = properties.getProperty("modes");
		return Arrays.asList(modes.split(";"));
	}

	public int getSshPort() {
		return Integer.parseInt(properties.getProperty(SSH_PORT));
	}

}
