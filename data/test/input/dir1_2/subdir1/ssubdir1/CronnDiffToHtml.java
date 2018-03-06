package de.cronn.diff;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;

import de.cronn.diff.impl.JavaDiffToHtml;
import de.cronn.diff.impl.OSDiffToHtml;
import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.DiffToHtmlParameters.DiffType;
import de.cronn.diff.util.FileHelper;

public class CronnDiffToHtml {

	private static final String SYSOUT_MSG_DIRECTORIES_IDENTICAL = "\nDirectories are identical!";

	private static final String SYSOUT_MSG_FILES_IDENTICAL = "\nFiles are identical!";

	private static final String SYSOUT_MSG_DIRECTORIES_DIFFER = "\nDirectories differ!";

	private static final String SYSOUT_MSG_FILES_DIFFER = "\nFiles differ!";

	private static final String SYSOUT_MSG_OUTPUT_WRITTEN_TO = "\nOutput written to: ";

	private static final int NUMBER_OF_DIFF_CONTEXT_LINES = 3;

	private boolean isUseOSDiffTool = true;

	private DiffToHtmlParameters params = null;

	public boolean isUseOSDiffTool() {
		return isUseOSDiffTool;
	}

	public void setUseOSDiffTool(boolean isUseOSDiffTool) {
		this.isUseOSDiffTool = isUseOSDiffTool;
	}

	public int generateDiffToHtmlReport(DiffToHtmlParameters params) throws IOException {
		this.params = params;
		String outputDirUri = FilenameUtils.getFullPath(params.getOutputUri());
		FileHelper.copyCssFileToDir(outputDirUri);

		if (isUseOSDiffTool) {
			return generateOSDiffToHtml(params);

		} else {
			return generateJavaDiffToHtml(params);
		}
	}

	private int generateOSDiffToHtml(DiffToHtmlParameters params) throws IOException {
		OSDiffToHtml osDiffToHtml = new OSDiffToHtml();
		String html = osDiffToHtml.generateHtml(params, NUMBER_OF_DIFF_CONTEXT_LINES);
		writeToDisk(html);
		int resultCode = osDiffToHtml.getResultCode();
		printResultMessage(resultCode);
		return resultCode;
	}

	private int generateJavaDiffToHtml(DiffToHtmlParameters params) throws IOException {
		JavaDiffToHtml javaDiffToHtml = new JavaDiffToHtml();
		String html = javaDiffToHtml.generateDiff2Html(params, NUMBER_OF_DIFF_CONTEXT_LINES);
		writeToDisk(html);
		int resultCode = javaDiffToHtml.getResultCode();
		printResultMessage(resultCode);
		return resultCode;
	}

	private void writeToDisk(String html) throws IOException {
		String uri = params.getOutputUri();
		Files.write(Paths.get(uri), html.getBytes());
		System.out.println(SYSOUT_MSG_OUTPUT_WRITTEN_TO + params.getOutputUri());
	}

	private void printResultMessage(int resultCode) {
		if (params.getDiffType() == DiffType.DIRECTORIES) {
			printDirDiffResultMessage(resultCode);
		} else {
			printFileDiffResultMessage(resultCode);
		}
	}

	private void printDirDiffResultMessage(int resultCode) {
		if (resultCode == Main.EXIT_CODE_OK) {
			System.out.println(SYSOUT_MSG_DIRECTORIES_IDENTICAL);
		} else {
			System.out.println(SYSOUT_MSG_DIRECTORIES_DIFFER);
		}
	}

	private void printFileDiffResultMessage(int resultCode) {
		if (resultCode == Main.EXIT_CODE_OK) {
			System.out.println(SYSOUT_MSG_FILES_IDENTICAL);
		} else {
			System.out.println(SYSOUT_MSG_FILES_DIFFER);
		}
	}
}