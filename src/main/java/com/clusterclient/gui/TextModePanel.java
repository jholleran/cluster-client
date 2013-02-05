package com.clusterclient.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.clusterclient.Command;
import com.clusterclient.CommandService;
import com.clusterclient.EnvironmentRepository;

public class TextModePanel extends ModePanel implements MouseListener, KeyListener {

	private final MainWindow parent;
	private final EnvironmentRepository repository;
	
	private final JButton runButton = new JButton("Run");
	private final JButton stopButton = new JButton("Stop");
	
	private final JPanel content = new JPanel(new GridLayout());
	
	private final List<TextHostPanel> textHostPanels = new ArrayList<TextHostPanel>();
	private LayoutManager layoutManager = new GridLayout();

	public TextModePanel(MainWindow parent, EnvironmentRepository repository,
			CommandService service) {
		super(service);
		this.parent = parent;
		this.repository = repository;
		setLayout(new BorderLayout());
		add(makeControls(), BorderLayout.NORTH);
		add(content, BorderLayout.CENTER);
	}

	private JPanel makeControls() {
		JPanel panel = new JPanel();
		final JComboBox comboBox = new JComboBox(repository.findAllCommand()
				.toArray());
		comboBox.setEditable(true);
		panel.add(comboBox);

		disableControls();

		runButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent paramActionEvent) {
				Object selectedItem = comboBox.getSelectedItem();
				if (selectedItem instanceof Command) {
					Command command = (Command) selectedItem;
					service.exec(command.getCmd());
				} else {
					String command = (String) selectedItem;
					handleCommand(command);
				}
			}

		});

		comboBox.getEditor().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent paramActionEvent) {
				String command = paramActionEvent.getActionCommand();
				handleCommand(command);
			}
		});

		stopButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent paramActionEvent) {
				service.stop();
			}
		});

		panel.add(runButton);
		panel.add(stopButton);
		return panel;
	}

	private void handleCommand(String command) {
		if (internalCommand(command)) {
			handleInternalCommand(command);
		} else {
			service.exec(command);
		}
	}

	private boolean internalCommand(String command) {
		if ("clear".equals(command) || "clr".equals(command)) {
			return true;
		}
		return false;
	}

	private void handleInternalCommand(String command) {
		if ("clear".equals(command) || "clr".equals(command)) {
			clearAll();
		}
	}

	public void enableControls() {
		runButton.setEnabled(true);
		stopButton.setEnabled(true);
	}

	private void disableControls() {
		runButton.setEnabled(false);
		stopButton.setEnabled(false);
	}
	
	public void addHostPanel(TextHostPanel panel) {		
		textHostPanels.add(panel);
		content.add(panel);
		panel.addMouseListener(this);
		panel.addKeyListener(this);
	}

	void clearAll() {
		for (TextHostPanel panel : textHostPanels) {
			panel.clear();
		}
	}

	private void redraw() {
		content.removeAll();
		for (TextHostPanel panel : textHostPanels) {
			content.add(panel);
		}
		parent.updateStatus("All views");
		parent.validate();
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (textHostPanels.size() > 1) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				content.removeAll();
				content.setLayout(new GridLayout());
				content.add(findHostPanel(e));
				parent.updateStatus("ESC to Return");
				parent.validate();
			}
		}
	}

	private Component findHostPanel(MouseEvent e) {
		for (TextHostPanel panel : textHostPanels) {
			if (panel.contains(e.getSource())) {
				return panel;
			}
		}
		throw new IllegalStateException("Should not get here");
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			content.setLayout(layoutManager);
			redraw();
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	void save(String fileName) {
		for (TextHostPanel panel : textHostPanels) {
			panel.save(fileName);
		}
	}
	
	void enablePanels() {
		enableControls();
	}
	
	public void changeLayout(LayoutManager manager) {
		this.layoutManager  = manager;
		content.setLayout(manager);
		redraw();
	}
}
