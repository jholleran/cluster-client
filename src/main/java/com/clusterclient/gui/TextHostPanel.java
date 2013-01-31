package com.clusterclient.gui;

import java.awt.BorderLayout;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import com.clusterclient.CommandProgressMonitor;
import com.clusterclient.TextListener;

public class TextHostPanel extends JPanel implements TextListener {

	private final String hostName;
	private final int terminalBuffer;

	private final JLabel label = new JLabel();
	private final JTextArea textArea = new JTextArea();

	public TextHostPanel(String hostname, int terminalBuffer) {
		this.hostName = hostname;
		this.terminalBuffer = terminalBuffer;
		setLayout(new BorderLayout());
		add(makeLabelPanel(), BorderLayout.NORTH);
		makeTextArea();
		add(scrollBar(textArea), BorderLayout.CENTER);
	}



	private JPanel makeLabelPanel() {
		JPanel panel = new JPanel();
		label.setText(hostName);
		panel.add(label);
		return panel;
	}

	private void makeTextArea() {
		textArea.setEditable(false);
		textArea.getDocument().addDocumentListener(
				new LimitLinesDocumentListener(terminalBuffer));
	}

	void clear() {
		textArea.setText("");
	}

	private JScrollPane scrollBar(final JTextArea textArea) {
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		final JScrollPane scrollPane = new JScrollPane(textArea);
		return scrollPane;
	}

	public void addMouseListener(MouseListener listener) {
		super.addMouseListener(listener);
		textArea.addMouseListener(listener);
		label.addMouseListener(listener);
	}

	public void addKeyListener(KeyListener listener) {
		super.addKeyListener(listener);
		textArea.addKeyListener(listener);
		label.addKeyListener(listener);
	}

	public boolean contains(Object source) {
		return source == textArea || source == label || source == this;
	}

	public void print(String output) {
		textArea.append(output);
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}

	@Override
	public void error(String message, Throwable t) {
		textArea.append("\n********* [Log Grabber Tool] ***********\n");
		textArea.append(message + "\n");
		textArea.append(t.getMessage() + "\n");
		textArea.append("Please check logs for more information\n");
		textArea.append("****************************************\n");
	}

	public void finished() {

	}

	void save(String fileName) {
		try {
			PrintWriter printWriter = new PrintWriter(fileName + "-" + hostName
					+ ".log");
			printWriter.write(textArea.getText());
			printWriter.flush();
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public CommandProgressMonitor getProgressMonitor() {
		return new GuiProgressMonitor(this);
	}

}
