package com.clusterclient.ssh;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clusterclient.CommandService;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public abstract class AbstractCommandService implements CommandService {
	final static Logger LOGGER = Logger.getLogger(AbstractCommandService.class
			.getName());

	protected final String host;
	protected final String user;
	protected final String password;
	protected final int port;
	protected Session session;
	protected Channel channel;
	
	private boolean connected = false;

	public AbstractCommandService(String host, String user, String password, int port) {
		this.host = host;
		this.user = user;
		this.password = password;
		this.port = port;
	}

	protected void openSession() throws JSchException {
		JSch jsch = new JSch();
//		jsch.setLogger(new com.jcraft.jsch.Logger() {
//            public boolean isEnabled(int i) {
//                return true;
//            }
//
//            public void log(int i, String s) {
//                System.out.println("Log(jsch," + i + "): " + s);
//            }
//        });
		
		session = jsch.getSession(user, host, port);
		session.setUserInfo(new PasswordUserInfo(password));
		session.connect();
	}

	@Override
	public void connect() {
		if(connected) {
			return;
		}
		LOGGER.info("Connecting to " + host);
		try {
			openSession();
			openChannel();
		} catch (Exception e) {
			String errorMsg = "Unable to connect to ssh server [user:" + user
					+ " host:" + host + "]";
			LOGGER.log(Level.SEVERE, errorMsg, e);
			e.printStackTrace();
			error(errorMsg, e);
		}
		connected = true;
	}

	@Override
	public void disconnect() {
		if(!connected) {
			return;
		}
		LOGGER.info("Shutdown command service to " + host);
		channel.disconnect();
		session.disconnect();
	}
	
	protected abstract void openChannel() throws JSchException, IOException;
	protected abstract void error(String msg, Throwable t);

	protected static String commandToString(String[] commandArgs) {
		StringBuilder builder = new StringBuilder();
		for(String args : commandArgs) {
			builder.append(args);
		}
		return builder.toString();
	}
}
