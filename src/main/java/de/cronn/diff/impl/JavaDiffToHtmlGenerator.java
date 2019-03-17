package de.cronn.diff.impl;

import java.io.IOException;

import de.cronn.diff.impl.java.JavaDirDiffToHtmlImpl;
import de.cronn.diff.impl.java.JavaFileDiffToHtmlImpl;
import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.DiffToHtmlParameters.DiffType;

public class JavaDiffToHtmlGenerator {

	public DiffToHtmlResult generateHtml(DiffToHtmlParameters params) throws IOException {
		if (params.getDiffType() == DiffType.DIRECTORIES) {
			return new JavaDirDiffToHtmlImpl(params).runDiffToHtml();
		} else {
			return new JavaFileDiffToHtmlImpl(params).runDiffToHtml();
		}
	}
}
