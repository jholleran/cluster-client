package com.clusterclient;

public interface CommandServiceFactory {

	CommandService makeCommandService(String host, String mode);
}
