package com.clusterclient.ssh;

import com.jcraft.jsch.UserInfo;

final class PasswordUserInfo implements UserInfo {
	
	private final String password;
	
	public PasswordUserInfo(String password) {
		this.password = password;
	}
	
	public void showMessage(String paramString) {
	}

	public boolean promptYesNo(String paramString) {
		return true;
	}

	public boolean promptPassword(String paramString) {
		return true;
	}

	public boolean promptPassphrase(String paramString) {
		return false;
	}

	public String getPassword() {
		return password;
	}

	public String getPassphrase() {
		return null;
	}
}