package com.clusterclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a composite of {@link CommandService}'s. It is also responsible for
 * executing the commands on a separate thread. It uses an {@link Executor}
 * execute the commands. Some of the methods on this class are blocking and
 * others are non blocking.
 * 
 * @author HolleranJ
 * 
 */
public class CommandServiceExecutor implements CommandService {

	private final static Logger LOGGER = Logger
			.getLogger(CommandServiceExecutor.class.getName());

	private ExecutorService executorService;
	private Executor executor;

	private final List<CommandService> commandServices = new ArrayList<CommandService>();

	public CommandServiceExecutor() {
		this.executorService = Executors.newCachedThreadPool();
		executor = executorService;
	}

	CommandServiceExecutor(Executor executor) {
		this.executor = executor;
	}

	public void add(CommandService commandService) {
		LOGGER.info("Adding command service to composite");
		commandServices.add(commandService);
	}

	/**
	 * Connects to each {@link CommandService} added on a separate thread.
	 * Blocks until all have completed.
	 */
	public void connect() {
		LOGGER.info("Connecting");
		final CountDownLatch latch = new CountDownLatch(commandServices.size());
		for (final CommandService commandService : commandServices) {
			Runnable runnable = new Runnable() {
				public void run() {
					commandService.connect();
					latch.countDown();
				}
			};
			executor.execute(runnable);
		}
		try {
			latch.await(); // wait for all threads to finish
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE,
					"Interrupted exception waiting for thread to finish", e);
		}
	}

	/**
	 * Executes the command on each of the {@link CommandService}'s on a
	 * separate thread. This is a non-blocking method.
	 */
	public void exec(final String... command) {
		LOGGER.info("Executing command " + Arrays.toString(command));
		for (final CommandService commandService : commandServices) {
			Runnable runnable = new Runnable() {
				public void run() {
					commandService.exec(command);
				}
			};
			executor.execute(runnable);
		}
	}

	/**
	 * Executes {@link CommandService#disconnect()} on all the
	 * {@link CommandService}'s on a separate thread. This is a non-blocking
	 * method.
	 */
	public void disconnect() {
		LOGGER.info("Shutting down command services");
		for (final CommandService commandService : commandServices) {
			Runnable runnable = new Runnable() {
				public void run() {
					commandService.disconnect();
				}
			};
			executor.execute(runnable);
		}

		shutdownExecutorService();
	}

	private void shutdownExecutorService() {
		LOGGER.info("Shutting down Executor Service");
		executorService.shutdown();
		try {
			executorService.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE,
					"Error occured waiting for executer to finish", e);
		}
	}

	/**
	 * Executes {@link CommandService#stop()} on all the {@link CommandService}
	 * 's on a separate thread. This is a non-blocking method.
	 */
	public void stop() {
		LOGGER.info("Killing the command services");
		for (final CommandService commandService : commandServices) {
			Runnable runnable = new Runnable() {
				public void run() {
					commandService.stop();
				}
			};
			executor.execute(runnable);
		}
	}

}
