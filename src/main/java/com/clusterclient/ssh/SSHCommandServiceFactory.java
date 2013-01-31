package com.clusterclient.ssh;

import com.clusterclient.CommandListenerFactory;
import com.clusterclient.CommandService;
import com.clusterclient.CommandServiceExecutor;
import com.clusterclient.CommandServiceFactory;
import com.clusterclient.Configurations;
import com.clusterclient.TextListener;

public class SSHCommandServiceFactory implements CommandServiceFactory {

	private final CommandListenerFactory listenerFactory;
	private final String username;
	private final String password;
	private final int port;
	private final Configurations configurations;

	private CommandServiceExecutor serviceExecutor = new CommandServiceExecutor();

	public SSHCommandServiceFactory(CommandListenerFactory listenerFactory,
			String username, String password, int port,
			Configurations configurations) {
		this.listenerFactory = listenerFactory;
		this.username = username;
		this.password = password;
		this.configurations = configurations;
		this.port = port;
	}

	public CommandService makeCommandService(String host, String mode) {
		CommandService service;
		if ("shell".equals(mode)) {
			service = makeShellCommandService(host, mode);
		} else if ("sftp".equals(mode)) {
			service = makeSftpCommandService(host, mode);
		} else {
			throw new IllegalStateException("Invalid mode selected [" + mode
					+ "]");
		}
		serviceExecutor.add(service);
		return service;
	}

	private CommandService makeShellCommandService(String host, String mode) {
		ShellCommandService service = new ShellCommandService(host, username,
				password, port, configurations);
		TextListener listener = listenerFactory.makeTextListener(
				serviceExecutor, host, mode);
		service.addListener(listener);
		return service;
	}

	private CommandService makeSftpCommandService(String host, String mode) {
		SftpCommandService service = new SftpCommandService(host, username,
				password, port, configurations);
		service.addFileNavigationListener(listenerFactory
				.makeFileNavigationListener(serviceExecutor, host, mode));
		return service;
	}

}
