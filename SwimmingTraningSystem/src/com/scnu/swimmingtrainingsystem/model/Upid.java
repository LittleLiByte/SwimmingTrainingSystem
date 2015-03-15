package com.scnu.swimmingtrainingsystem.model;

public class Upid {
	int uid;
	int pid;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	@Override
	public String toString() {
		return "Upid [uid=" + uid + ", pid=" + pid + "]";
	}

}
