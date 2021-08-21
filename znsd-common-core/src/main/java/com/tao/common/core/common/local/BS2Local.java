package com.tao.common.core.common.local;

public class BS2Local {

	private String local;
	private String title;
	private int index;
	private boolean isDefault;

	public BS2Local(String local, String title, int index) {
		this(local, title, index, false);
	}

	public BS2Local(String local, String title, int index, boolean isDefault) {
		this.local = local;
		this.title = title;
		this.index = index;
		this.isDefault = isDefault;
	}

	public int getIndex() {
		return index;
	}

	public String getLocal() {
		return local;
	}

	public String getTitle() {
		return title;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	@Override
	public String toString() {
		return this.local;
	}
}
