package com.clusterclient;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RollingLogsApp {

	public static final int FILE_SIZE = 100000;

	public static void main(String[] args) {

		Logger logger = Logger.getLogger(RollingLogsApp.class.getName());
		try {
			//
			// Creating an instance of FileHandler with 5 logging files
			// sequences.
			//
			FileHandler handler = new FileHandler("C:/cygwin/home/HolleranJ/output/test.log", FILE_SIZE, 5,
					true);
			handler.setFormatter(new SimpleFormatter());
			logger.addHandler(handler);
			logger.setUseParentHandlers(false);
		} catch (IOException e) {
			logger.warning("Failed to initialize logger handler.");
		}

		int i = 0;
		for (;;) {
			logger.info("logging message " + i++);
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
