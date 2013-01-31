package com.clusterclient;

import java.util.List;


/**
 * Implementations of this interface are used to listen to responses from a
 * {@link CommandService}.
 * 
 * @author HolleranJ
 * 
 */
public interface FileStructureListener {

	
	/**
	 * Get a progress monitor
	 * @return
	 */
	CommandProgressMonitor getProgressMonitor();

	/**
	 * Lists remote files to the user.
	 * 
	 * @param files Remote files found
	 */
	void fileList(String directory, List<RemoteFile> files);

	
	/**
	 * This method is used to alert that an error has occurred.
	 * 
	 * @param message
	 *            Description of the error
	 * @param t
	 *            The exception that been thrown
	 */
	void error(String message, Throwable t);

}
