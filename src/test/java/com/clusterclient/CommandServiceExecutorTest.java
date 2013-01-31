package com.clusterclient;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.DeterministicExecutor;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.clusterclient.CommandService;
import com.clusterclient.CommandServiceExecutor;

@RunWith(JMock.class)
public class CommandServiceExecutorTest {

	private final Mockery context = new JUnit4Mockery();

	private final DeterministicExecutor executor = new DeterministicExecutor();

	private final CommandService commandService1 = context.mock(
			CommandService.class, "service 1");
	private final CommandService commandService2 = context.mock(
			CommandService.class, "service 2");

	private CommandServiceExecutor commandExecutor;

	@Before
	public void setUp() {
		commandExecutor = new CommandServiceExecutor(executor);
		commandExecutor.add(commandService1);
		commandExecutor.add(commandService2);
	}

	@Test
	public void testConnectingPerCommandService() throws Exception {
		/*
		 * Using normal Executor because connect is an blocking method.
		 */
		CommandServiceExecutor commandExecutor = new CommandServiceExecutor();

		commandExecutor.add(commandService1);
		commandExecutor.add(commandService2);

		context.checking(new Expectations() {
			{
				one(commandService1).connect();
				one(commandService2).connect();
			}
		});

		commandExecutor.connect();
	}

	@Test
	public void testExecutingCommandPerCommandService() {

		context.checking(new Expectations() {
			{
				one(commandService1).exec("ls -ltr");
				one(commandService2).exec("ls -ltr");
			}
		});

		commandExecutor.exec("ls -ltr");
		executor.runUntilIdle();
	}

	@Test
	public void testKillingCommandPerCommandService() {

		context.checking(new Expectations() {
			{
				one(commandService1).stop();
				one(commandService2).stop();
			}
		});

		commandExecutor.stop();
		executor.runUntilIdle();
	}

	@Test
	@Ignore
	public void testShutdownCommandPerCommandService() {

		context.checking(new Expectations() {
			{
				one(commandService1).disconnect();
				one(commandService2).disconnect();
			}
		});

		commandExecutor.disconnect();
		executor.runUntilIdle();
	}
}
