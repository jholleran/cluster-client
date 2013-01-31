package com.clusterclient;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RemoteFile implements Comparable<RemoteFile>{

	private final String name;
	private final boolean isDir;
	private final long size;
	private final long modified;
	private final String directory;
	
	public RemoteFile(String directory, String name, boolean isDir, long size, long modified) {
		this.name = name;
		this.isDir = isDir;
		this.size = size;
		this.modified = modified;
		this.directory = directory;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the isDir
	 */
	public boolean isDir() {
		return isDir;
	}
	
	public Long getSize() {
		return new Long(size);
	}

	public Date lastModified() {
		return new Date(modified);
	}
	

	public String getDirectory() {
		return directory;
	}
	
	public String getFullName() {
		if("/".equals(getDirectory())){
			return "/" + getName();
		}
		return getDirectory() + "/" + getName();
	}
	
	@Override
	public int compareTo(RemoteFile o) {
		if(isDir && !o.isDir){
			return -1;
		} else if(o.isDir() && !isDir()) {
			return 1;
		} else {			
			return this.name.compareTo(o.name);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	
}
