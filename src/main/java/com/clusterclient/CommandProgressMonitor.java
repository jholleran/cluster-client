package com.clusterclient;

public interface CommandProgressMonitor {

	  void init(String display, long max);

	  boolean count(long count);

	  void end();
}
