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

	private static final String NEWLINE = System.lineSeparator();

	private static final String SYSOUT_MSG_DIRECTORIES_IDENTICAL = NEWLINE + "Directories are identical!";

	private static final String SYSOUT_MSG_FILES_IDENTICAL = NEWLINE + "Files are identical!";

	private static final String SYSOUT_MSG_DIRECTORIES_DIFFER = NEWLINE + "Directories differ!";

	private static final String SYSOUT_MSG_FILES_DIFFER = NEWLINE + "Files differ!";

	private static final String SYSOUT_MSG_OUTPUT_WRITTEN_TO = NEWLINE + "Output written to: ";

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
		String outputDirPath = FilenameUtils.getFullPath(params.getOutputPath());
		FileHelper.copyCssFileToDir(outputDirPath);

		if (isUseOSDiffTool) {
			return generateOSDiffToHtml(params);

		} else {
			return generateJavaDiffToHtml(params);
		}
	}

	private int generateOSDiffToHtml(DiffToHtmlParameters params) throws IOException {
		OSDiffToHtml osDiffToHtml = new OSDiffToHtml();
		String html = osDiffToHtml.generateHtml(params);
		writeToDisk(html);
		int resultCode = osDiffToHtml.getResultCode();
		printResultMessage(resultCode);
		return resultCode;
	}

	private int generateJavaDiffToHtml(DiffToHtmlParameters params) throws IOException {
		JavaDiffToHtml javaDiffToHtml = new JavaDiffToHtml();
		String html = javaDiffToHtml.generateDiff2Html(params);
		writeToDisk(html);
		int resultCode = javaDiffToHtml.getResultCode();
		printResultMessage(resultCode);
		return resultCode;
	}

	private void writeToDisk(String html) throws IOException {
		String path = params.getOutputPath();
		Files.write(Paths.get(path), html.getBytes());
		System.out.println(SYSOUT_MSG_OUTPUT_WRITTEN_TO + params.getOutputPath());
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