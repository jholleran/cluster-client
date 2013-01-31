package com.clusterclient.ssh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clusterclient.CommandProgressMonitor;
import com.clusterclient.Configurations;
import com.clusterclient.FileStructureListener;
import com.clusterclient.RemoteFile;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

public class SftpCommandService extends AbstractCommandService {

	private final static Logger LOGGER = Logger
			.getLogger(SftpCommandService.class.getName());

	private ChannelSftp channelFtp = null;

	private FileStructureListener fileStructureListener;

	private final Lock lock = new ReentrantLock();

	public SftpCommandService(String host, String user, String password,
			int port, Configurations configurations) {
		super(host, user, password, port);
	}

	public void addFileNavigationListener(FileStructureListener listener) {
		this.fileStructureListener = listener;
	}

	protected void openChannel() throws JSchException, IOException {
		LOGGER.info("Opening channel in sftp mode.");
		channel = session.openChannel("sftp");
		channelFtp = (ChannelSftp) channel;
		channelFtp.connect();
		exec("ls");
	}

	@Override
	public void exec(String... fullCommand) {
		LOGGER.info("Execute command: " + Arrays.toString(fullCommand) + " on "
				+ host);

		String cmd = fullCommand[0];

		lock.lock();
		try {
			if ("ls".equals(cmd)) {
				listDirectory();
			} else if ("cd".equals(cmd)) {
				String dir = fullCommand[1];
				changeDirectory(dir);
				listDirectory();
			} else if ("get".equals(cmd)) {
				getFile(fullCommand);
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,
					"Error running command: " + Arrays.toString(fullCommand), e);
			// e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	private void getFile(String... command) throws SftpException {
		String file = "";
		String toDir = ".";
		if (command.length > 1) {
			file = command[1];
		} else {
			LOGGER.warning("No file to download supplied");
			return;
		}

		if (command.length > 2) {
			toDir = command[2] + "/" + host + "-" + getFileName(file);
		} else {
			toDir = host + "-" + getFileName(file);
		}
		channelFtp.get(file, toDir, new ProgressMonitorAdapter());
	}

	private String getFileName(String file) {
		if (file.contains("/")) {
			return file.substring(file.lastIndexOf("/") + 1, file.length());
		}
		return file;
	}

	private void listDirectory() throws SftpException {
		String directory = channelFtp.pwd();
		try {
			Vector<LsEntry> lsEntries = channelFtp.ls(".");
			fileStructureListener.fileList(directory,
					files(directory, lsEntries));
		} catch (SftpException e) {
			fileStructureListener.error(e.getMessage() + " " + directory, e);
			throw e;
		}
	}

	private List<RemoteFile> files(String directory, Vector<LsEntry> lsEntries) {
		List<RemoteFile> files = new ArrayList<RemoteFile>();
		for (LsEntry entry : lsEntries) {
			String filename = entry.getFilename();
			if (!filename.equals(".") && !filename.equals("..")) {
				files.add(makeRemoteFile(directory, entry));
			}
		}
		return files;
	}

	private RemoteFile makeRemoteFile(String directory, LsEntry entry) {
		SftpATTRS attrs = entry.getAttrs();
		RemoteFile remoteFile = new RemoteFile(directory, entry.getFilename(),
				attrs.isDir(), attrs.getSize(), attrs.getMTime() * 1000L);
		return remoteFile;
	}

	private void changeDirectory(String directory) throws SftpException {
		// System.out.println(dir);
		try {
			channelFtp.cd(directory);
		} catch (SftpException e) {
			fileStructureListener.error(e.getMessage() + " " + directory, e);
			throw e;
		}
	}

	@Override
	public void stop() {
		LOGGER.info("Stop not implemented " + host);
	}

	public class ProgressMonitorAdapter implements SftpProgressMonitor {

		private CommandProgressMonitor progressMonitor = fileStructureListener
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
		t.printStackTrace();
		// TODO send to UI
	}
}
