package de.cronn.diff.util;

public class SimpleFileInfo {
	private final String path;
	private final String lastModified;

	public SimpleFileInfo() {
		this("", "");
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
}
