package de.cronn.diff.impl.java;

import static org.apache.commons.lang3.StringUtils.CR;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import de.cronn.diff.Main;
import de.cronn.diff.html.FileDiffHtmlBuilder;
import de.cronn.diff.impl.DiffToHtmlResult;
import de.cronn.diff.impl.java.wrapper.JavaDiffUtils2HtmlWrapper;
import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.FileHelper;
import de.cronn.diff.util.SimpleFileInfo;

public class JavaFileDiffToHtmlImpl {
	
	public static final String BINARY_FILES_DIFFER_PREFIX = "Binary files ";
	
	public static final String BINARY_FILES_SUFFIX = " differ";

	public static final String BINARY_LINE_SPLIT_STR = " and ";

	public static final String IDENTICAL_FILES_PREFIX = "Files ";

	public static final String IDENTICAL_FILES_SUFFIX = " are identical";

	public static final String IDENTICAL_LINE_SPLIT_STR = " and ";

	protected DiffToHtmlParameters params;

	protected int resultCode = Main.EXIT_CODE_OK;

	public JavaFileDiffToHtmlImpl(DiffToHtmlParameters params) {
		this.params = params;
	}

	public DiffToHtmlResult runDiffToHtml() throws IOException {
		String html = appendFileDiffToBuilder(new FileDiffHtmlBuilder(params), params).toString();
		return new DiffToHtmlResult(html, resultCode);
	}

	protected FileDiffHtmlBuilder appendFileDiffToBuilder(FileDiffHtmlBuilder htmlBuilder, DiffToHtmlParameters params)
			throws IOException {
		setFileInfos(htmlBuilder, params);

		if (isInputFilesAreIdentical(params)) {
			return appendIdenticalFilesToBuilder(htmlBuilder, params);

		} else if (FileHelper.isFileBinary(params.getInputLeftPath())) {
			return appendBinaryFilesDiffToBuilder(htmlBuilder, params);

		} else if (FileHelper.isFileSizeDifferenceTooBig(params.getInputLeftPath(),params.getInputRightPath(),params.getMaxAllowedDifferenceInByte())) {
			return appendFileSizeTooBigToBuilder(htmlBuilder, params);
		}
		else {
			return appendTextFilesDiffToBuilder(htmlBuilder, params);
		}
	}

	private FileDiffHtmlBuilder appendFileSizeTooBigToBuilder(FileDiffHtmlBuilder htmlBuilder, DiffToHtmlParameters params) {
		htmlBuilder.appendAttentionLine("Files differ but filesize difference too big to parse.");
		resultCode = Main.EXIT_CODE_ERROR;
		return  htmlBuilder;
	}

	private FileDiffHtmlBuilder appendIdenticalFilesToBuilder(FileDiffHtmlBuilder htmlBuilder, DiffToHtmlParameters params) {
		htmlBuilder.appendInfoLine(createFilesIdenticalMessage(params));
		resultCode = Main.EXIT_CODE_OK;
		return htmlBuilder;
	}

	private FileDiffHtmlBuilder appendBinaryFilesDiffToBuilder(FileDiffHtmlBuilder htmlBuilder, DiffToHtmlParameters params) {
		htmlBuilder.appendAttentionLine(createBinaryFilesDifferMessage(params));
		resultCode = Main.EXIT_CODE_ERROR;
		return htmlBuilder;
	}

	private FileDiffHtmlBuilder appendTextFilesDiffToBuilder(FileDiffHtmlBuilder htmlBuilder, DiffToHtmlParameters params)
			throws IOException {
		htmlBuilder = new JavaDiffUtils2HtmlWrapper().appendDiffToBuilder(htmlBuilder, params);
		resultCode = Main.EXIT_CODE_ERROR;
		return htmlBuilder;
	}

	private void setFileInfos(FileDiffHtmlBuilder htmlBuilder, DiffToHtmlParameters params) throws IOException {
		setLeftFileInfo(htmlBuilder, params);
		setRightFileInfo(htmlBuilder, params);
	}

	protected void setLeftFileInfo(FileDiffHtmlBuilder htmlBuilder, DiffToHtmlParameters params) throws IOException {
		String fileLeftPath = params.getInputLeftPath();
		if (Files.exists(Paths.get(fileLeftPath))) {
			String fileLeftLastModified = Files.getLastModifiedTime(Paths.get(fileLeftPath)).toString();
			htmlBuilder.setFileLeftInfo(new SimpleFileInfo(fileLeftPath, fileLeftLastModified));
		}
	}

	protected void setRightFileInfo(FileDiffHtmlBuilder htmlBuilder, DiffToHtmlParameters params) throws IOException {
		String fileRightPath = params.getInputRightPath();
		if (Files.exists(Paths.get(fileRightPath))) {
			String fileRightLastModified = Files.getLastModifiedTime(Paths.get(fileRightPath)).toString();
			htmlBuilder.setFileRightInfo(new SimpleFileInfo(fileRightPath, fileRightLastModified));
		}
	}

	protected boolean isInputFilesAreIdentical(DiffToHtmlParameters params) throws IOException {
		if(!FileHelper.isFileBinary(params.getInputLeftPath())) {
			String text1 = new String(Files.readAllBytes(Paths.get(params.getInputLeftPath())));
			String text2 = new String(Files.readAllBytes(Paths.get(params.getInputRightPath())));
			if(params.isIgnoreWhiteSpaces() || params.isIgnoreSpaceChange()) {
				String replacement = params.isIgnoreWhiteSpaces() ? "" : " ";
				text1 = text1.replaceAll("\\s+", replacement);
				text2 = text2.replaceAll("\\s+", replacement);
			}
			if(params.isIgnoreLineEndings()) {
				text1 = text1.replace(CR, "");
				text2 = text2.replace(CR, "");
			}
			return text1.equals(text2);
		} else {
			return FileUtils.contentEquals(new File(params.getInputLeftPath()), new File(params.getInputRightPath()));
		}
	}

	private String createFilesIdenticalMessage(DiffToHtmlParameters params) {
		return IDENTICAL_FILES_PREFIX + params.getInputLeftPath() + IDENTICAL_LINE_SPLIT_STR + params.getInputRightPath() + IDENTICAL_FILES_SUFFIX;
	}

	private String createBinaryFilesDifferMessage(DiffToHtmlParameters params) {
		return BINARY_FILES_DIFFER_PREFIX + params.getInputLeftPath() + BINARY_LINE_SPLIT_STR + params.getInputRightPath() + BINARY_FILES_SUFFIX;
	}
}
