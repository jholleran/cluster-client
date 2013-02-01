package com.clusterclient;

/**
 * This interface is used to implement a progress monitor for the user when long
 * commands are being run.
 * 
 * @author HolleranJ
 * 
 */
public interface CommandProgressMonitor {

	/**
	 * Initials the progress monitor.
	 * 
	 * @param display
	 * @param max
	 */
	void init(String display, long max);

	/**
	 * This method can be used to track how much of the command has been
	 * completed.
	 * 
	 * @param count
	 * @return
	 */
	boolean count(long count);

	/**
	 * This method is called when the command being run has ended.
	 */
	void end();
}
