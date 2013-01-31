package com.clusterclient;

/**
 * 
 * @author HolleranJ
 * 
 */
public interface CommandService {

	/**
	 * Connects to any external services
	 */
	void connect();

	/**
	 * Executes a command on this service.
	 * 
	 * @param command
	 *            The command that will be run.
	 */
	void exec(String... command);

	/**
	 * Stops the current command from running. This will attempt stop the
	 * command if the {@link #exec(String)} method has not return yet. This will
	 * not disconnect and commands can be still run on the same service. Its up
	 * to the implementation of this method to stop this command. There is no
	 * guarantee that this method will stop the command, its a best effort.
	 */
	void stop();

	/**
	 * Disconnects from any external services
	 */
	void disconnect();

}
