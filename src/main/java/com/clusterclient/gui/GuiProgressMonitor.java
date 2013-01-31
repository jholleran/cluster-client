package com.clusterclient.gui;

import java.awt.Component;

import javax.swing.ProgressMonitor;

import com.clusterclient.CommandProgressMonitor;

public class GuiProgressMonitor implements CommandProgressMonitor {
	
	private final Component parent;
	private ProgressMonitor monitor;
	private long count = 0;
	private long max = 0;
	private long percent = -1;

	public GuiProgressMonitor(Component parent) {
		this.parent = parent;
	}
	
	public void init(String displayStr, long max) {
		this.max = max;
		monitor = new ProgressMonitor(parent, displayStr,
				"", 0, (int) max);
		count = 0;
		percent = -1;
		monitor.setProgress((int) this.count);
		monitor.setMillisToDecideToPopup(100);
	}

	public boolean count(long count) {

		this.count += count;

		if (percent >= this.count * 100 / max) {
			return true;
		}
		percent = this.count * 100 / max;

		monitor.setNote("Completed " + this.count + "(" + percent
				+ "%) out of " + max + ".");
		monitor.setProgress((int) this.count);

		return !(monitor.isCanceled());
	}

	public void end() {
		monitor.close();
	}
}