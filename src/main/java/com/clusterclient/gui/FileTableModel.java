package com.clusterclient.gui;

import com.clusterclient.RemoteFile;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class FileTableModel extends AbstractTableModel {

	private static String[] COLUMN_NAMES = new String[] { "Name", "Size",
			"Modified" };

	protected static Class[] COLUMN_CLASSES = new Class[] { RemoteFile.class,
			Long.class, Date.class };

	private final List<RemoteFile> files;

	public FileTableModel(List<RemoteFile> files) {
		this.files = files;
	}

	@Override
	public int getRowCount() {
		return files.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	public String getColumnName(int col) {
		return COLUMN_NAMES[col];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		RemoteFile remoteFile = files.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return remoteFile;
		case 1:
			return remoteFile.getSize();
		case 2:
			return remoteFile.lastModified();
		default:
			break;
		}
		return null;
	}

	public Class getColumnClass(int col) {
		return COLUMN_CLASSES[col];
	}
}
