package de.cronn.diff.util;

public class SimpleFileInfo {
	private String path = "";
	private String lastModified = "";

	public SimpleFileInfo() {}

	public SimpleFileInfo(String path) {
		this(path, "");
	}

	public SimpleFileInfo(String path, String lastModified) {
		this.path = path;
		this.lastModified = lastModified;
	}

	public String getPath() {
		return path;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}
}