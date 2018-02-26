package de.cronn.diff.impl;

public final class DiffToHtmlResult {

	private final String html;

	private final int resultCode;

	public DiffToHtmlResult(String html, int resultCode) {
		this.html = html;
		this.resultCode = resultCode;
	}

	public String getHtml() {
		return html;
	}

	public int getResultCode() {
		return resultCode;
	}

}
