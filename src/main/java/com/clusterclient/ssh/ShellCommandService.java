package com.clusterclient.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clusterclient.Configurations;
import com.clusterclient.TextListener;
import com.clusterclient.ssh.format.Formatter;
import com.clusterclient.ssh.format.LessFormatter;
import com.clusterclient.ssh.format.ShellFormatter;
import com.jcraft.jsch.JSchException;

public class ShellCommandService extends AbstractCommandService {

	final static Logger LOGGER = Logger.getLogger(ShellCommandService.class
			.getName());

	/**
	 * CTRL + C hex value
	 */
	private static final String CTRL_C = "\u0003";

	private final SSHInputStream inputStream = new SSHInputStream();

	private Formatter formatter = new ShellFormatter();

	private TextListener textListener;

	public ShellCommandService(String host, String user, String password,
			int port, Configurations configurations) {
		super(host, user, password, port);
	}

	public void addListener(TextListener output) {
		this.textListener = output;
	}
	
	protected void openChannel() throws JSchException, IOException {
		channel = session.openChannel("shell");
		channel.setInputStream(inputStream);
		channel.setOutputStream(new PrintStream(new SSHOutputStream(), true)); // print
																				// stream
																				// with
																				// auto
																				// flush
		channel.connect();
	}

	@Override
	public void exec(String... commandArgs) {
		String command = commandToString(commandArgs);
		LOGGER.info("Execute command: " + command + " on " + host);
		selectFormatter(command);
		inputStream.send(command + "\n");
	}

	private void selectFormatter(String command) {
		if (command.startsWith("less")) {
			LOGGER.info("Switching to Less Formatter");
			formatter = new LessFormatter();
		}
		if (command.equals("q") || command.equals("quit")) {
			LOGGER.info("Switching back to the Shell Formatter");
			formatter = new ShellFormatter();
		}
	}

	@Override
	public void stop() {
		LOGGER.info("Sending CRTL+C command to " + host);
		inputStream.send(CTRL_C);
	}

	private final class SSHOutputStream extends OutputStream {

		private static final int BUFFER_SIZE = 8192;
		private byte[] buffer = new byte[BUFFER_SIZE];
		private int pos = 0;

		@Override
		public void write(int b) throws IOException {
			buffer[pos++] = (byte) b;
			// Append to the listener when the buffer is full
			if (pos == BUFFER_SIZE) {
				flush();
			}
		}

		@Override
		public void flush() throws IOException {
			byte[] flush = null;
			if (pos != BUFFER_SIZE) {
				flush = new byte[pos];
				System.arraycopy(buffer, 0, flush, 0, pos);
			} else {
				flush = buffer;
			}

			textListener.print(formatter.filter(new String(flush)));
			pos = 0;
		}

	}

	private final class SSHInputStream extends InputStream {

		private static final int END_OF_STREAM = -1;
		private byte[] contents;
		private int pointer = 0;
		private CountDownLatch latch = new CountDownLatch(1);

		private void send(String str) {
			contents = str.getBytes();
			pointer = 0;
			latch.countDown();
		}

		@Override
		public int read() throws IOException {
			try {
				latch.await();
				return readNextByte();
			} catch (InterruptedException e) {
				LOGGER.log(Level.WARNING,
						"Interrupted exception occurred waiting for input", e);
				return END_OF_STREAM;
			}
		}

		private int readNextByte() {
			if (pointer >= contents.length) {
				latch = new CountDownLatch(1);
				return END_OF_STREAM;
			}
			return this.contents[pointer++];
		}

	}

	@Override
	protected void error(String msg, Throwable t) {
		textListener.error(msg, t);
	}

}
