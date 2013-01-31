package com.clusterclient.ssh.format;

/**
 * This interface is used to filter out characters from the given string.
 * 
 * @author HolleranJ
 * 
 */
public interface Formatter {

	/**
	 * Method to filter unwanted characters from the given string
	 * 
	 * @param input
	 *            The string that has been filtered
	 * @return Filter string
	 */
	public String filter(String input);

}