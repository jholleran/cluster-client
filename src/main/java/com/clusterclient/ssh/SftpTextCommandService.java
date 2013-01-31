package com.clusterclient.ssh;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clusterclient.CommandProgressMonitor;
import com.clusterclient.Configurations;
import com.clusterclient.TextListener;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

public class SftpTextCommandService extends AbstractCommandService {

	private final static Logger LOGGER = Logger
			.getLogger(SftpTextCommandService.class.getName());

	private ChannelSftp channelFtp = null;

	private TextListener textListener;
	
	public SftpTextCommandService(String host, String user, String password,
			int port, Configurations configurations) {
		super(host, user, password, port);
	}

	public void addListener(TextListener output) {
		this.textListener = output;
	}
	
	protected void openChannel() throws JSchException, IOException {
		channel = session.openChannel("sftp");
		channelFtp = (ChannelSftp) channel;
		channelFtp.connect();
		prompt();
	}

	@Override
	public void exec(String... fullCommand) {
		LOGGER.info("Execute command: " + Arrays.toString(fullCommand) + " on " + host);

		textListener.print(commandToString(fullCommand) + "\n");
		List<String> command = Arrays.asList(fullCommand);
		String first = command.get(0);
		try {
			if ("pwd".equals(first)) {
				pwd();
			} else if ("ls".equals(first)) {
				listDirectory(command);
			} else if ("cd".equals(first)) {
				changeDirectory(command);
			} else if ("get".equals(first)) {
				getFile(command);
			}

		} catch (SftpException e) {
			LOGGER.log(Level.SEVERE, "Error running command: " + Arrays.toString(fullCommand), e);
			e.printStackTrace();
		}
	}

	private void getFile(List<String> command) throws SftpException {
		String file = "";
		String toDir = ".";
		if (command.size() > 1) {
			file = command.get(1);
		} else {
			return;
		}

		if (command.size() > 2) {
			toDir = command.get(2) + "/" + getFileName(file) + "." + host;
		} else {
			toDir = getFileName(file) + "." + host;
		}
		channelFtp.get(file, toDir, new ProgressMonitorAdapter());
		textListener.print("Downloaded " + file + " to " + toDir + "\n");
		prompt();
	}

	private String getFileName(String file) {
		if (file.contains("/")) {
			return file.substring(file.lastIndexOf("/") + 1, file.length());
		}
		return file;
	}

	private void pwd() throws SftpException {
		textListener.print(channelFtp.pwd() + "\n");
		prompt();
	}

	private void prompt() {
		textListener.print("sftp>");
	}

	private void listDirectory(List<String> sections) throws SftpException {
		String dir = ".";
		if (sections.size() > 1) {
			dir = sections.get(1);
		}
		Vector<LsEntry> lsEntries = channelFtp.ls(dir);

		for (LsEntry entry : lsEntries) {
			textListener.print(entry.getFilename() + "\n");
		}

		prompt();
	}

	private void changeDirectory(List<String> sections) throws SftpException {
		String dir = ".";
		if (sections.size() > 1) {
			dir = sections.get(1);
		}
		channelFtp.cd(dir);
		prompt();
	}

	@Override
	public void stop() {
		LOGGER.info("Stop not implemented " + host);
	}

	public class ProgressMonitorAdapter implements SftpProgressMonitor {

		private CommandProgressMonitor progressMonitor = textListener
				.getProgressMonitor();

		public void init(int op, String src, String dest, long max) {
			String displayStr = ((op == SftpProgressMonitor.PUT) ? "put"
					: "get") + ": " + src;
			progressMonitor.init(displayStr, max);
		}

		public boolean count(long count) {
			return progressMonitor.count(count);
		}

		public void end() {
			progressMonitor.end();
		}
	}

	@Override
	protected void error(String msg, Throwable t) {
		// TODO Auto-generated method stub
		
	}
}
