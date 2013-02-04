package com.clusterclient.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.clusterclient.CommandListenerFactory;
import com.clusterclient.CommandService;
import com.clusterclient.Configurations;
import com.clusterclient.ConnectListener;
import com.clusterclient.EnvironmentRepository;
import com.clusterclient.FileStructureListener;
import com.clusterclient.TextListener;

public class MainWindow extends JFrame implements CommandListenerFactory {


	private final Map<String, ModePanel> modePanels = new HashMap<String, ModePanel>();

	private final ConnectListener connectListener;
	
	private final JTabbedPane tabbedPane = new JTabbedPane();
	private final JLabel statusLabel = new JLabel("Connecting...");

	private final EnvironmentRepository repository;
	private final Configurations configurations;

	private String mode = "";

	public MainWindow(EnvironmentRepository repository,
			Configurations configurations, ConnectListener connectListener) {
		this.repository = repository;
		this.configurations = configurations;
		this.connectListener = connectListener;

		mode = configurations.getMode();

		setTitle("Cluster Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setJMenuBar(makeMenuBar());
		fillContentPane(mainContent(), makeStatusBar());

		addShutdownListener();
		
		setPreferredSize(new Dimension(1200, 600));
		pack();		
		setVisible(true);
		
	}

	private JMenuBar makeMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu());
		menuBar.add(viewMenu());
		return menuBar;
	}

	private JMenu fileMenu() {
		JMenu fileMenu = new JMenu("File");
		JMenuItem connectItem = new JMenuItem("Connect");
		connectItem.addActionListener(new ConnectActionListener());
		
		JMenuItem saveItem = new JMenuItem("Save As...");
		saveItem.addActionListener(new SaveAsActionListener());

		fileMenu.add(connectItem);
		fileMenu.add(saveItem);
		return fileMenu;
	}
	
	private JMenu viewMenu() {
		JMenu viewMenu = new JMenu("View");
		JMenuItem horizontal = new JMenuItem("Horizontal");
		horizontal.addActionListener(new HorizontalActionListener());
		
		JMenuItem verticalItem = new JMenuItem("Vertical");
		verticalItem.addActionListener(new VerticalActionListener());

		//JMenuItem gridItem = new JMenuItem("Grid");
		//gridItem.addActionListener(new GridActionListener());
		
		viewMenu.add(horizontal);
		viewMenu.add(verticalItem);
		//viewMenu.add(gridItem);
		return viewMenu;
	}

	private JPanel makeStatusBar() {
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(statusLabel);
		return panel;
	}

	private void fillContentPane(Component outputPanel, Component statusPanel) {
		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(outputPanel, BorderLayout.CENTER);
		contentPane.add(statusPanel, BorderLayout.SOUTH);
	}

	private Container mainContent() {
		tabbedPane.setSelectedIndex(tabbedPane.indexOfTab(mode));

		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int selectedIndex = tabbedPane.getSelectedIndex();
				//System.out.println(selectedIndex);
				if(selectedIndex != -1) {
					mode = tabbedPane.getTitleAt(selectedIndex);
					modePanels.get(mode).connect();					
				}
			}
		});
		return tabbedPane;
	}

	public TextListener makeTextListener(CommandService service, String host,
			String mode) {
		TextHostPanel hostPanel = new TextHostPanel(host,
				configurations.getTerminalBuffer());
		
		ModePanel modePanel = modePanels.get(mode);
		if (modePanel == null) {
			modePanel = new TextModePanel(this, repository,
					service);
			modePanel.addHostPanel(hostPanel);
			addModePanel(mode, modePanel);
		} else {
			modePanel.addHostPanel(hostPanel);
		}
		repaint();
		return hostPanel;
	}

	@Override
	public FileStructureListener makeFileNavigationListener(
			CommandService service, String host, String mode) {
		FileNavigationHostPanel hostPanel = new FileNavigationHostPanel(host,
				service);
		
		ModePanel modePanel = modePanels.get(mode);
		if (modePanel == null) {
			modePanel = new ModePanel(service);
			modePanel.addHostPanel(hostPanel);
			addModePanel(mode, modePanel);
		} else {
			modePanel.addHostPanel(hostPanel);
		}
		repaint();
		return hostPanel;
	}

	private void addModePanel(String mode, ModePanel modePanel) {
		modePanels.put(mode, modePanel);
		tabbedPane.addTab(mode, modePanel);
	}

	private void addShutdownListener() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				disconnectAll();
			}
		});
	}

	private final class SaveAsActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			int result = fileChooser.showSaveDialog(MainWindow.this);
			if (result == JFileChooser.APPROVE_OPTION) {
				String name = fileChooser.getSelectedFile().getAbsolutePath();
				modePanels.get(mode).save(name);
			}
		}
	}
	
	private final class ConnectActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			disconnectAll();
			modePanels.clear();
			tabbedPane.removeAll();
			connectListener.connect();
		}

	}

	public void updateStatus(String text) {
		statusLabel.setText(text);
	}

	public void start() {
		modePanels.get(mode).connect();
		modePanels.get(mode).enablePanels();
	}

	private void disconnectAll() {
		for(ModePanel modePanel : modePanels.values()) {
			modePanel.disconnect();
		}
	}

	public class GridActionListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			for(ModePanel modePanel : modePanels.values()) {
				modePanel.changeLayout(new GridLayout(0, 3));
			}
		}
		
	}
	
	public class VerticalActionListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			for(ModePanel modePanel : modePanels.values()) {
				modePanel.changeLayout(new GridLayout());
			}
		}
		
	}
	
	public class HorizontalActionListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			for(ModePanel modePanel : modePanels.values()) {
				modePanel.changeLayout(new GridLayout(0, 1));
			}
		}
		
	}
}
