package com.clusterclient;

import org.junit.Test;

import com.clusterclient.Configurations;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionContaining.*;

public class ConfigurationsTest {

	
	@Test
	public void readsDefaultsIfFileDoesNotExist() {
		Configurations configurations = new Configurations("noneExisting.properties");
		assertThat(configurations.getEnvironmentsFile(), is("environments.xml"));
		assertThat(configurations.getTerminalBuffer(), is(1000));
		assertThat(configurations.getModes(), hasItem("shell"));
		assertThat(configurations.getModes(), hasItem("sftp"));
	}

	@Test
	public void readsValuesFromFile() {
		Configurations configurations = new Configurations("src/test/resources/test.properties");
		assertThat(configurations.getEnvironmentsFile(), is("test-envs.xml"));
		assertThat(configurations.getTerminalBuffer(), is(900));
		assertThat(configurations.getModes(), hasItem("shell"));
		assertThat(configurations.getModes(), hasItem("sftp"));
	}
}
