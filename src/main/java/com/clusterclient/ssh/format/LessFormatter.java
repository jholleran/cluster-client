package com.clusterclient.ssh.format;

public class LessFormatter implements Formatter {

	private static final String ESC = "\u001B";

	@Override
	public String filter(String input) {
		return input.replaceAll(":" + ESC+"\\[K|"+ESC+"\\[K|"+ESC+"\\[7m(.*?)"+ESC+"\\[m", "");
	}

}
