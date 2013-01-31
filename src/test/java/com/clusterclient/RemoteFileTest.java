package com.clusterclient;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.clusterclient.RemoteFile;

public class RemoteFileTest {

	@Test
	public void testDirectory() {
		RemoteFile file = new RemoteFile("/home", "test", true, 0L, 0);
		assertThat(file.getDirectory(), is("/home"));
		assertThat(file.getName(), is("test"));
		assertThat(file.getFullName(), is("/home/test"));
		assertThat(file.isDir(), is(true));
		assertThat(file.getSize(), is(0L));
	}
	
	@Test
	public void testFile() {
		RemoteFile file = new RemoteFile("/tmp", "aFile", false, 10L, 0);
		assertThat(file.getDirectory(), is("/tmp"));
		assertThat(file.getName(), is("aFile"));
		assertThat(file.getFullName(), is("/tmp/aFile"));
		assertThat(file.isDir(), is(false));
		assertThat(file.getSize(), is(10L));
	}

	@Test
	public void testFileWithSpaces() {
		RemoteFile file = new RemoteFile("/home", "with space", false, 10L, 0);
		assertThat(file.getDirectory(), is("/home"));
		assertThat(file.getName(), is("with space"));
		assertThat(file.getFullName(), is("/home/with space"));
	}
	
	@Test
	public void testBottomOfTheDirTree() {
		RemoteFile file = new RemoteFile("/", "afile", false, 10L, 0);
		assertThat(file.getDirectory(), is("/"));
		assertThat(file.getName(), is("afile"));
		assertThat(file.getFullName(), is("/afile"));
	}
}
