package de.cronn.diff;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import de.cronn.diff.impl.DiffToHtmlGenerator;
import de.cronn.diff.impl.DiffToHtmlResult;
import de.cronn.diff.impl.JavaDiffToHtmlGenerator;
import de.cronn.diff.impl.OSDiffToHtmlGenerator;
import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.DiffToHtmlParameters.DiffType;

public class CronnDiffToHtml {

	private static final String NEWLINE = System.lineSeparator();

	private static final String SYSOUT_MSG_DIRECTORIES_IDENTICAL = NEWLINE + "Directories are identical!";

	private static final String SYSOUT_MSG_FILES_IDENTICAL = NEWLINE + "Files are identical!";

	private static final String SYSOUT_MSG_DIRECTORIES_DIFFER = NEWLINE + "Directories differ!";

	private static final String SYSOUT_MSG_FILES_DIFFER = NEWLINE + "Files differ!";

	private static final String SYSOUT_MSG_OUTPUT_WRITTEN_TO = NEWLINE + "Output written to: file://";

	private DiffToHtmlParameters params = null;

	public int generateDiffToHtmlReport(DiffToHtmlParameters params) throws IOException {
		this.params = params;
		int status = generateDiffToHtml();
		return params.isOnlyReports() ? Main.EXIT_CODE_OK : status;
	}

	private int generateDiffToHtml() throws IOException {
		DiffToHtmlGenerator diffToHtml = params.isUseOsDiffTool() ? new OSDiffToHtmlGenerator() : new JavaDiffToHtmlGenerator();
		DiffToHtmlResult res = diffToHtml.generateHtml(params);
		writeToDisk(res.getHtml());
		printResultMessage(res.getResultCode());
		return res.getResultCode();
	}

	private void writeToDisk(String html) throws IOException {
		String path = params.getOutputPath();
		Files.write(Paths.get(path), html.getBytes());
		System.out.println(SYSOUT_MSG_OUTPUT_WRITTEN_TO + path);
	}

	private void printResultMessage(int resultCode) {
		if (params.getDiffType() == DiffType.DIRECTORIES) {
			printDirDiffResultMessage(resultCode);
		} else {
			printFileDiffResultMessage(resultCode);
		}
	}

	private void printDirDiffResultMessage(int resultCode) {
		System.out.println(resultCode == Main.EXIT_CODE_OK ?
				SYSOUT_MSG_DIRECTORIES_IDENTICAL : SYSOUT_MSG_DIRECTORIES_DIFFER);
	}

	private void printFileDiffResultMessage(int resultCode) {
		System.out.println(resultCode == Main.EXIT_CODE_OK ?
				SYSOUT_MSG_FILES_IDENTICAL : SYSOUT_MSG_FILES_DIFFER);
	}
}