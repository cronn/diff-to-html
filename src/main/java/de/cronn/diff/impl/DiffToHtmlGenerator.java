package de.cronn.diff.impl;

import java.io.IOException;

import de.cronn.diff.util.DiffToHtmlParameters;

public interface DiffToHtmlGenerator {
	DiffToHtmlResult generateHtml(DiffToHtmlParameters params) throws IOException;
}
