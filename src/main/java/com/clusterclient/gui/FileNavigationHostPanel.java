package com.clusterclient.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import com.clusterclient.CommandProgressMonitor;
import com.clusterclient.CommandService;
import com.clusterclient.FileStructureListener;
import com.clusterclient.RemoteFile;

public class FileNavigationHostPanel extends JPanel implements FileStructureListener {

	private final static Logger LOGGER = Logger.getLogger(FileNavigationHostPanel.class.getName());

	private final String hostName;	
	private CommandService service;
	
	private final JLabel label = new JLabel();

	private final JTextField path = new JTextField();
	private final JTable table = new JTable();
	
	private final JPopupMenu popupMenu = createPopup();
	

	public FileNavigationHostPanel(String hostname, CommandService service) {
		this.hostName = hostname;
		this.service = service;
		setLayout(new BorderLayout());
		add(infoPanel(), BorderLayout.NORTH);
		setUpTable();
		add(scrollBar(table), BorderLayout.CENTER);
	}

	private void setUpTable() {
		table.setModel(new FileTableModel(Collections.<RemoteFile> emptyList()));
		table.getColumnModel().getColumn(0)
				.setCellRenderer(new KeyIconCellRenderer());
		table.addMouseListener(new FileTableMouseListener());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setShowGrid(false);
		table.setAutoCreateRowSorter(true);
	}

	private JPanel infoPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(labelPanel(), BorderLayout.NORTH);
		panel.add(pathPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private Component pathPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		path.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				service.exec("cd", e.getActionCommand());
			}
		});
		
		GridBagConstraints iconConstraints = new GridBagConstraints();
		iconConstraints.anchor = GridBagConstraints.WEST;
		
		JButton button = new JButton(UIManager.getIcon("FileChooser.upFolderIcon"));
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				service.exec("cd", parentDirectory());
			}

			private String parentDirectory() {
				String file = path.getText();
				String parent = file.substring(file.indexOf("/"), file.lastIndexOf('/'));
				if("".equals(parent)) {
					return "/";
				}
				return parent;
			}
		});
		
		panel.add(button, iconConstraints);

		GridBagConstraints pathConstraints = new GridBagConstraints();
		pathConstraints.anchor = GridBagConstraints.WEST;
		pathConstraints.fill = GridBagConstraints.HORIZONTAL;
		pathConstraints.weightx = 1.0;
		
		panel.add(path, pathConstraints);
		return panel;
	}
	
	private JPanel labelPanel() {
		JPanel panel = new JPanel();
		label.setText(hostName);
		panel.add(label);
		return panel;
	}


	private JScrollPane scrollBar(final Component component) {
		return new JScrollPane(component);
	}
	
	public void addCommandService(CommandService service) {
		this.service = service;
	}
	
	@Override
	public CommandProgressMonitor getProgressMonitor() {
		return new GuiProgressMonitor(this);
	}

	private final class FileTableMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent evt) {
			if (SwingUtilities.isLeftMouseButton(evt)
					&& evt.getClickCount() == 2) {
				RemoteFile remoteFile = (RemoteFile) table.getValueAt(
						table.getSelectedRow(), 0);
				if (remoteFile.isDir()) {
					service.exec("cd", remoteFile.getFullName());
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent evt) {
			int idx = findRow(evt);
			selectRow(idx);
		}

		@Override
		public void mouseReleased(MouseEvent evt) {
			if (evt.isPopupTrigger() && SwingUtilities.isRightMouseButton(evt)) {
				int index = findRow(evt);
				selectRow(index);
				RemoteFile remoteFile = (RemoteFile) table.getValueAt(index, 0);
				if (!remoteFile.isDir()) {
					popupMenu.show(table, evt.getX(), evt.getY());
				}
			}
		}

		private int findRow(MouseEvent evt) {
			return table.rowAtPoint(evt.getPoint());
		}

		private void selectRow(int idx) {
			table.getSelectionModel().setSelectionInterval(idx, idx);
		}

		public void mouseEntered(MouseEvent e) {
			
		}
	}

	private JPopupMenu createPopup() {
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem downloadItem = new JMenuItem("Download");
		downloadItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RemoteFile value = (RemoteFile) table.getValueAt(
						table.getSelectedRow(), 0);

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showSaveDialog(FileNavigationHostPanel.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					String localDirectory = fileChooser.getSelectedFile()
							.getAbsolutePath();
					service.exec("get", value.getFullName(), localDirectory);
				}
			}
		});
		popupMenu.add(downloadItem);
		return popupMenu;
	}

	@Override
	public void fileList(final String directory, final List<RemoteFile> files) {
		LOGGER.info("File List in " + directory);
		LOGGER.info(files.toString());
		Collections.sort(files, new ByName());
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				path.setBackground(Color.WHITE);
				table.setBackground(Color.WHITE);
				path.setText(directory);
				table.setModel(new FileTableModel(files));
				table.getColumnModel().getColumn(0)
						.setCellRenderer(new KeyIconCellRenderer());
			}
		});
	}

	public class ByName implements Comparator<RemoteFile> {

		@Override
		public int compare(RemoteFile o1, RemoteFile o2) {
			if(o1.isDir() && !o2.isDir()){
				return -1;
			} else if(o2.isDir() && !o1.isDir()) {
				return 1;
			} else {			
				return o1.getName().compareTo(o2.getName());
			}
		}

	}
	
	private static class KeyIconCellRenderer extends DefaultTableCellRenderer {

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component c = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
			JLabel l = (JLabel) c;
			RemoteFile remoteFile = (RemoteFile) value;
			l.setText(remoteFile.getName());

			if (remoteFile.isDir()) {
				l.setIcon(UIManager.getIcon("FileView.directoryIcon"));
			} else {
				l.setIcon(UIManager.getIcon("FileView.fileIcon"));
			}

			return l;
		}

	}

	@Override
	public void error(String message, Throwable t) {
		path.setText(message);
		path.setBackground(Color.YELLOW);
		table.setModel(new FileTableModel(Collections.<RemoteFile>emptyList()));
		table.setBackground(Color.LIGHT_GRAY);
	}

}
