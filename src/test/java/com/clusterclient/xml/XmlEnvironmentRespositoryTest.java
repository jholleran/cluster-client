package com.clusterclient.xml;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.clusterclient.Command;
import com.clusterclient.xml.XmlEnvironmentRepository;

public class XmlEnvironmentRespositoryTest {

	private final XmlEnvironmentRepository environmentRepository = new XmlEnvironmentRepository(
			"src/test/resources/test-envs.xml");

	@Before
	public void setUp() throws Exception {
		environmentRepository.parse();
	}

	@Test
	public void testReadCluster1() {

		List<String> hosts = environmentRepository.findHostsWithId("cluster-1");
		assertThat(hosts, hasItem("host.cl1.1.dnb.com"));
		assertThat(hosts, hasItem("host.cl1.2.dnb.com"));
		assertThat(hosts, hasItem("host.cl1.3.dnb.com"));
		assertThat("Should only contain 3 envs", hosts.size(), is(3));
	}

	@Test
	public void testReadCluster2() {

		List<String> hosts = environmentRepository.findHostsWithId("cluster-2");
		assertThat(hosts, hasItem("host.cl2.1.dnb.com"));
		assertThat(hosts, hasItem("host.cl2.2.dnb.com"));
		assertThat("Should only contain 2 envs", hosts.size(), is(2));
	}

	
	@Test(expected=Exception.class)
	public void testNoFileFound() throws Exception {
		new XmlEnvironmentRepository("nonExistingFile.xml").parse();
	}
	
	@Test
	public void testUserNameAndPassword() {

		String userName = environmentRepository.findUserName();
		String password = environmentRepository.findPassword();
		
		assertThat(userName, is("aUserName"));
		assertThat(password, is("aPassword"));
	}
	
	@Test
	public void testReadAllCommands() {

		List<Command> commands = environmentRepository.findAllCommand();
		
		assertThat(commands, hasItem(commandWith("tail -f /file-1.log")));
		assertThat(commands, hasItem(commandWith("grep 'xception' /file-2.log")));
		assertThat(commands, hasItem(commandWith("tail /file-3.log")));
		assertThat(commands, hasItem(commandWith("tail /file.log", "tail log file")));
	}
	
	private static Command commandWith(String cmd) {
		return new Command(cmd);
	}
	
	private static Command commandWith(String cmd, String alias) {
		return new Command(cmd, alias);
	}

	@Test
	public void testReadAllEnvironments() {

		List<String> environment = environmentRepository.findAllEnvironments();
		
		assertThat(environment, hasItem("cluster-1"));
		assertThat(environment, hasItem("cluster-2"));
		assertThat("Should only contain 2 environment", environment.size(), is(2));
	}
}
