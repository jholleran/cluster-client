package com.clusterclient;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.clusterclient.Command;

public class CommandTest {

	@Test
	public void testEquals() {
		assertThat(new Command("test cmd"), is(new Command("test cmd")));
		assertThat(new Command("test cmd"), is(not(new Command("different cmd"))));
	}

	@Test
	public void testToString() {
		assertThat(new Command("test cmd").toString(), is("test cmd"));
	}
	
	@Test
	public void testEqualsWithAlias() {
		assertThat(new Command("test cmd", "an alias"), is(new Command("test cmd", "an alias")));
		assertThat(new Command("test cmd"), is(not(new Command("different cmd"))));
	}
	
	@Test
	public void testToStringWithAlais() {
		assertThat(new Command("test cmd", "command name").toString(), is("command name"));
	}
	
}
