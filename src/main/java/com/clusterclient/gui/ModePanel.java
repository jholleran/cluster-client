package com.clusterclient.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;

import com.clusterclient.CommandService;

public class ModePanel extends JPanel {
	
	protected final CommandService service;
	
	public ModePanel(CommandService service) {
		this.service = service;
		setLayout(new GridLayout());
		setBackground(Color.WHITE);
	}

	public void connect() {
		service.connect();
	}

	public void disconnect() {
		service.disconnect();
		removeAll();
	}

	
	void clearAll() {

	}

	void save(String fileName) {

	}
	
	void enablePanels() {
		
	}

	public void addHostPanel(TextHostPanel hostPanel) {
		add(hostPanel);
	}
	
	public void addHostPanel(FileNavigationHostPanel hostPanel) {
		add(hostPanel);		
	}
	

}
