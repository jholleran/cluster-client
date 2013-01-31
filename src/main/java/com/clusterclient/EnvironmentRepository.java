package com.clusterclient;

import java.util.List;

public interface EnvironmentRepository {

	List<String> findHostsWithId(String id);

	String findPassword();

	String findUserName();

	List<Command> findAllCommand();

	List<String> findAllEnvironments();

}
