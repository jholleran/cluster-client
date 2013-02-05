package com.clusterclient.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;

import org.jdesktop.swingx.JXEditorPane;

import com.clusterclient.CommandProgressMonitor;
import com.clusterclient.TextListener;

public class TextHostPanel extends JPanel implements TextListener {

	private final String hostName;
	private final int terminalBuffer;

	private final JLabel label = new JLabel();
	private final JXEditorPane editor = new JXEditorPane();
	private final JPopupMenu popupMenu = createPopup();

	public TextHostPanel(String hostname, int terminalBuffer) {
		this.hostName = hostname;
		this.terminalBuffer = terminalBuffer;
		setLayout(new BorderLayout());
		add(makeLabelPanel(), BorderLayout.NORTH);
		makeTextArea();
		add(scrollBar(editor), BorderLayout.CENTER);
	}

	private JPanel makeLabelPanel() {
		JPanel panel = new JPanel();
		label.setText(hostName);
		panel.add(label);
		return panel;
	}

	private void makeTextArea() {
		editor.setEditable(false);
		editor.getDocument().addDocumentListener(
				new LimitLinesDocumentListener(terminalBuffer));
		editor.addMouseListener(new PopupMouseListener());

		DefaultCaret caret = (DefaultCaret) editor.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}

	void clear() {
		editor.setText("");
	}

	private JScrollPane scrollBar(final Component component) {
		return new JScrollPane(component);
	}

	public void addMouseListener(MouseListener listener) {
		super.addMouseListener(listener);
		editor.addMouseListener(listener);
		label.addMouseListener(listener);
	}

	public void addKeyListener(KeyListener listener) {
		super.addKeyListener(listener);
		editor.addKeyListener(listener);
		label.addKeyListener(listener);
	}

	public boolean contains(Object source) {
		return source == editor || source == label || source == this;
	}

	public void print(String output) {
		Document doc = editor.getDocument();
        if (doc != null) {
            try {
                doc.insertString(doc.getLength(), output, null);
            } catch (BadLocationException e) {
            }
        }
		editor.setCaretPosition(editor.getDocument().getLength());
	}

	@Override
	public void error(String message, Throwable t) {
		StringBuilder builder = new StringBuilder();
		builder.append("\n********* [Log Grabber Tool] ***********\n");
		builder.append(message + "\n");
		builder.append(t.getMessage() + "\n");
		builder.append("Please check logs for more information\n");
		builder.append("****************************************\n");
		print(builder.toString());
	}

	public void finished() {

	}

	void save(String fileName) {
		try {
			PrintWriter printWriter = new PrintWriter(fileName + "-" + hostName
					+ ".log");
			printWriter.write(editor.getText());
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

	private JPopupMenu createPopup() {
		JPopupMenu popupMenu = new JPopupMenu();
		Action copyAction = editor.getActionMap().get(DefaultEditorKit.copyAction);
		JMenuItem copyMenuItem = new JMenuItem(copyAction);
		copyMenuItem.setText("Copy");
		popupMenu.add(copyMenuItem);
		return popupMenu;
	}
	
	private final class PopupMouseListener extends MouseAdapter {

		@Override
		public void mouseReleased(MouseEvent evt) {
			if (evt.isPopupTrigger() && SwingUtilities.isRightMouseButton(evt)) {
				popupMenu.show(editor, evt.getX(), evt.getY());
			}
		}
	}
}
