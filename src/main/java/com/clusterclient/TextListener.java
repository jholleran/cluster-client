package com.clusterclient;



/**
 * Implementations of this interface are used to listen to responses from a
 * {@link CommandService}.
 * 
 * @author HolleranJ
 * 
 */
public interface TextListener {

	/**
	 * Prints the output string.
	 * 
	 * @param output
	 *            Value received that will be printed
	 */
	void print(String output);

	/**
	 * This method is used to alert that an error has occurred.
	 * 
	 * @param message
	 *            Description of the error
	 * @param t
	 *            The exception that been thrown
	 */
	void error(String message, Throwable t);

	/**
	 * Called when the {@link CommandService} has finished running a command.
	 */
	void finished();
	
	/**
	 * Get a progress monitor
	 * @return
	 */
	CommandProgressMonitor getProgressMonitor();

}
