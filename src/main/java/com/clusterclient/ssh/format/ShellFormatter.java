package com.clusterclient.ssh.format;

public class ShellFormatter implements Formatter {

	/**
	 * ESC hex value
	 */
	private static final String ESC = "\u001B";

	/**
	 * This constant is a regex expression to match display attribute coming
	 * from the shh terminal. These attributes are in the following format:
	 * <ESC>[{attr1};...;{attrn}m They contain display information such as
	 * foreground and background colors.
	 */
	private static final String DISPLAY_ATTRIBUTES = ESC + "\\[(.*?)m";

	/**
	 * This constant is a regex expression to match additional information
	 * coming from the shh terminal. These attributes are in the following
	 * format: ESC]0;<string>^G. They contain display information such as
	 * hostname your logged into or your current directory.
	 */
	private static final String ADDITIONAL_INFO = ESC + "]0;(.*?)\u0007";
	
	
	/* (non-Javadoc)
	 * @see com.dnb.loggrabber.ssh.format.Formatter#filter(java.lang.String)
	 */
	@Override
	public String filter(String output) {
		return output.replaceAll(
				DISPLAY_ATTRIBUTES + "|" + ADDITIONAL_INFO, "");
	}
	
}
