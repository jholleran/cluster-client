package com.clusterclient.ssh;

import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clusterclient.CommandService;
import com.clusterclient.Configurations;
import com.clusterclient.TextListener;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SSHCommandService implements CommandService {

	private static final int SSH_PORT = 22;

	private final static Logger LOGGER = Logger
			.getLogger(SSHCommandService.class.getName());

	private final String host;
	private final String user;
	private final String password;
	private final Configurations configurations;
	private Session session;
	private boolean run = true;
	private TextListener commandListener;

	public SSHCommandService(String host, String user, String password,
			Configurations configurations) {
		this.host = host;
		this.user = user;
		this.password = password;
		this.configurations = configurations;
	}

	public void addListener(TextListener output) {
		this.commandListener = output;
	}

	public void connect() {
		LOGGER.info("Connecting to " + host);
		try {
			JSch jsch = new JSch();

			session = jsch.getSession(user, host, SSH_PORT);

			UserInfo ui = new UserInfo() {

				public void showMessage(String paramString) {
				}

				public boolean promptYesNo(String paramString) {
					return true;
				}

				public boolean promptPassword(String paramString) {
					return true;
				}

				public boolean promptPassphrase(String paramString) {
					return false;
				}

				public String getPassword() {
					return password;
				}

				public String getPassphrase() {
					return null;
				}
			};

			session.setUserInfo(ui);

			session.connect();
		} catch (Exception e) {
			String errorMsg = "Unable to connect to ssh server [user:" + user
					+ " host:" + host + "]";
			LOGGER.log(Level.SEVERE, errorMsg, e);
			commandListener.error(errorMsg, e);
		}
	}

	public void exec(String... command) {
		LOGGER.info("Execute command: " + Arrays.toString(command));
		run = true;
		Channel channel = null;
		InputStream in = null;
		try {
			channel = session.openChannel("exec");

			((ChannelExec) channel).setCommand(commandToString(command));

			channel.setInputStream(null);

			// ((ChannelExec) channel).setErrStream(System.err);

			in = channel.getInputStream();

			channel.connect();

		} catch (Exception e) {
			e.printStackTrace();
			String msg = "Unable to connect to a ssh channel";
			LOGGER.log(Level.SEVERE, msg, e);
			commandListener.error(msg, e);
			return;
		}

		try {
			byte[] tmp = new byte[1024];

			while (run) {
				while (in.available() > 0 && run) {
					int i = in.read(tmp, 0, 1024);

					if (i < 0)
						break;

					commandListener.print(new String(tmp, 0, i));
				}

				if (channel.isClosed()) {
					break;
				}

				sleep();
			}
			commandListener.print("---END---\n");

			channel.disconnect();
			LOGGER.info("Finished executing command on host: " + host
					+ " exit status: " + channel.getExitStatus());
			commandListener.finished();
		}

		catch (Exception e) {
			e.printStackTrace();
			String msg = "Unable to read from ssh channel on host:" + host;
			LOGGER.log(Level.SEVERE, msg, e);
			commandListener.error(msg, e);
		}

	}
	
	private static String commandToString(String[] commandArgs) {
		StringBuilder builder = new StringBuilder();
		for(String args : commandArgs) {
			builder.append(args);
		}
		return builder.toString();
	}

	private void sleep() {
		try {
			Thread.sleep(configurations.getThreadTimeout());
		} catch (InterruptedException ee) {
			ee.printStackTrace();
			LOGGER.log(Level.WARNING, "Sleep interrupted exception", ee);
		}
	}

	public void disconnect() {
		LOGGER.info("Shutdown command service");
		run = false;
		session.disconnect();
	}

	public void stop() {
		LOGGER.info("Kill command");
		run = false;
	}

}
