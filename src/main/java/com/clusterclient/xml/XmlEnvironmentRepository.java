package com.clusterclient.xml;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.clusterclient.Command;
import com.clusterclient.EnvironmentRepository;

public class XmlEnvironmentRepository implements EnvironmentRepository {

	private final static Logger LOGGER = Logger
			.getLogger(XmlEnvironmentRepository.class.getName());

	private final String fileName;
	private Document dom;

	public static EnvironmentRepository load(String fileName) throws Exception {
		LOGGER.info("Loading repository from " + fileName);
		XmlEnvironmentRepository enviromentRepository = new XmlEnvironmentRepository(
				fileName);
		enviromentRepository.parse();
		return enviromentRepository;
	}

	XmlEnvironmentRepository(String fileName) {
		this.fileName = fileName;
	}

	public void parse() throws Exception {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(new FileInputStream(fileName));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception parsing " + fileName, e);
			throw new Exception("Unable to parse " + fileName, e);
		}
	}

	public List<String> findHostsWithId(String id) {
		LOGGER.info("Find Hosts with id = " + id);
		NodeList nl = dom.getElementsByTagName("environment");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);
				String envId = el.getAttribute("id");
				if (id.equalsIgnoreCase(envId)) {
					return getHosts(el);
				}
			}
		}
		return Collections.emptyList();
	}

	private List<String> getHosts(Element element) {
		List<String> hosts = new ArrayList<String>();
		NodeList nl = element.getElementsByTagName("server");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {

				Element el = (Element) nl.item(i);
				String host = el.getAttribute("host");
				hosts.add(host);
			}
		}
		LOGGER.info("Found hosts " + hosts);
		return hosts;
	}

	public String findUserName() {
		LOGGER.info("Find username");
		return findFirst("login", "username");
	}

	public String findPassword() {
		LOGGER.info("Find password");
		return findFirst("login", "password");
	}

	private String findFirst(String tagname, String attribute) {
		NodeList nl = dom.getElementsByTagName(tagname);
		if (nl != null) {
			Element el = (Element) nl.item(0);
			return el.getAttribute(attribute);
		}
		return "";
	}

	public List<Command> findAllCommand() {
		LOGGER.info("Find all commands");
		List<Command> commands = new ArrayList<Command>();
		NodeList nl = dom.getElementsByTagName("command");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {

				Element el = (Element) nl.item(i);
				String cmd = el.getTextContent();
				String alias = el.getAttribute("alias");
				if (alias != null) {
					commands.add(new Command(cmd, alias));
				} else {
					commands.add(new Command(cmd));
				}
			}
		}
		LOGGER.info("Commands found " + commands);
		return commands;
	}

	public List<String> findAllEnvironments() {
		LOGGER.info("Find all environments");
		List<String> commands = new ArrayList<String>();
		NodeList nl = dom.getElementsByTagName("environment");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {

				Element el = (Element) nl.item(i);
				String environment = el.getAttribute("id");
				commands.add(environment);
			}
		}
		LOGGER.info("Commands found " + commands);
		return commands;
	}
}
