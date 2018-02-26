package de.cronn.diff.impl;

import static de.cronn.diff.util.UnifiedDiffValues.BINARY_FILES_DIFFER_PREFIX;
import static de.cronn.diff.util.UnifiedDiffValues.BINARY_FILES_SUFFIX;
import static de.cronn.diff.util.UnifiedDiffValues.BINARY_LINE_SPLIT_STR;
import static de.cronn.diff.util.UnifiedDiffValues.IDENTICAL_FILES_PREFIX;
import static de.cronn.diff.util.UnifiedDiffValues.IDENTICAL_FILES_SUFFIX;
import static de.cronn.diff.util.UnifiedDiffValues.IDENTICAL_LINE_SPLIT_STR;
import static de.cronn.diff.util.UnifiedDiffValues.UNIQUE_FILE_PREFIX;
import static de.cronn.diff.util.UnifiedDiffValues.UNIQUE_LINE_SPLIT_STR;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import de.cronn.diff.Main;
import de.cronn.diff.html.DirectoryDiffHtmlBuilder;
import de.cronn.diff.html.FileDiffHtmlBuilder;
import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.DiffToHtmlParameters.DiffType;
import de.cronn.diff.util.FileHelper;
import de.cronn.diff.util.SimpleFileInfo;

public class JavaDiffToHtml {

	private DiffToHtmlParameters params = null;

	private int contextLinesAmount = 3;

	private int resultCode = Main.EXIT_CODE_ERROR;

	public String generateDiff2Html(DiffToHtmlParameters params) throws IOException {
		this.params = params;
		this.contextLinesAmount = params.getUnifiedContext();
		if (params.getDiffType() == DiffType.DIRECTORIES) {
			return generateDirectoryDiff2Html();
		} else {
			return generateFileDiff2Html();
		}
	}

	public int getResultCode() {
		return resultCode;
	}

	private String generateDirectoryDiff2Html() throws IOException {
		DirectoryDiffHtmlBuilder dirDiffHtmlBuilder = new DirectoryDiffHtmlBuilder(params);
		ArrayList<File> filesAndDirsLeft = getSortedFilesAndDirs(params.getInputLeftPath());
		ArrayList<File> filesAndDirsRight = getSortedFilesAndDirs(params.getInputRightPath());
		resultCode = Main.EXIT_CODE_OK;

		dirDiffHtmlBuilder = traverseDirectoryLeft(dirDiffHtmlBuilder, filesAndDirsLeft);
		dirDiffHtmlBuilder = traverseDirectoryRight(dirDiffHtmlBuilder, filesAndDirsRight);
		return dirDiffHtmlBuilder.toString();
	}

	private DirectoryDiffHtmlBuilder traverseDirectoryLeft(DirectoryDiffHtmlBuilder dirDiffHtmlBuilder, ArrayList<File> filesAndDirs) throws IOException {
		for (File fileLeft : filesAndDirs) {
			String fileLeftPath = fileLeft.getPath();
			String fileRightPath = fileLeftPath.replace(params.getInputLeftPath(), params.getInputRightPath());
			DiffToHtmlParameters fileDiffParams = new DiffToHtmlParameters(params)
					.withDiffType(DiffType.FILES)
					.withInputLeftPath(fileLeftPath)
					.withInputRightPath(fileRightPath);

			if (Files.exists(Paths.get(fileRightPath))) {
				if (Files.isRegularFile(Paths.get(fileLeftPath)) && !isInputFilesAreIdentical(fileDiffParams)) {
					makeDifferingFilesEntry(dirDiffHtmlBuilder, fileDiffParams);
				}
			} else {
				makeUniqueFileEntry(dirDiffHtmlBuilder, fileDiffParams, true);
			}
		}
		return dirDiffHtmlBuilder;
	}

	private DirectoryDiffHtmlBuilder traverseDirectoryRight(DirectoryDiffHtmlBuilder dirDiffHtmlBuilder, ArrayList<File> filesAndDirs) throws IOException {
		for (File fileRight : filesAndDirs) {
			String fileRightPath = fileRight.getPath();
			String fileLeftPath = fileRightPath.replace(params.getInputRightPath(), params.getInputLeftPath());
			DiffToHtmlParameters fileDiffParams = new DiffToHtmlParameters(params)
					.withDiffType(DiffType.FILES)
					.withInputLeftPath(fileLeftPath)
					.withInputRightPath(fileRightPath);
			if (!Files.exists(Paths.get(fileLeftPath))) {
				makeUniqueFileEntry(dirDiffHtmlBuilder, fileDiffParams, false);
			}
		}
		return dirDiffHtmlBuilder;
	}

	private void makeDifferingFilesEntry(DirectoryDiffHtmlBuilder dirDiffHtmlBuilder, DiffToHtmlParameters diffParams) throws IOException {
		FileDiffHtmlBuilder htmlTableBuilder = new FileDiffHtmlBuilder(diffParams);
		htmlTableBuilder = (FileDiffHtmlBuilder) appendFileDiff2Html(htmlTableBuilder, diffParams);

		String fileLeftPath = diffParams.getInputLeftPath();
		if (FileHelper.isFileBinary(fileLeftPath)) {
			dirDiffHtmlBuilder.appendChangedBinaryFile(fileLeftPath, htmlTableBuilder.createDiffTable());
		} else {
			dirDiffHtmlBuilder.appendChangedTextFile(fileLeftPath, htmlTableBuilder.createDiffTable());
		}
		resultCode = Main.EXIT_CODE_ERROR;
	}

	private void makeUniqueFileEntry(DirectoryDiffHtmlBuilder dirDiffHtmlBuilder, DiffToHtmlParameters diffParams, boolean isLeftSide)
			throws IOException {
		if(params.isIgnoreUniqueFiles()) {
			return;
		}
		FileDiffHtmlBuilder htmlTableBuilder = new FileDiffHtmlBuilder(diffParams);
		String filePath = isLeftSide ? diffParams.getInputLeftPath() : diffParams.getInputRightPath();
		setFileInfos(htmlTableBuilder, diffParams);

		if (Files.isDirectory(Paths.get(filePath)) || FileHelper.isFileBinary(filePath)) {
			htmlTableBuilder.appendInfoLine(createUniqueFileMessage(filePath));
		} else {
			htmlTableBuilder.appendTextFile(new String(Files.readAllBytes(Paths.get(filePath))));
		}
		if(isLeftSide) {
			dirDiffHtmlBuilder.appendUniqueFileLeft(filePath, htmlTableBuilder.createDiffTable());
		} else {
			dirDiffHtmlBuilder.appendUniqueFileRight(filePath, htmlTableBuilder.createDiffTable());
		}
		resultCode = Main.EXIT_CODE_ERROR;
	}

	private ArrayList<File> getSortedFilesAndDirs(String dirPath) {
		ArrayList<File> files = new ArrayList<>(FileUtils.listFilesAndDirs(new File(dirPath), TrueFileFilter.TRUE, TrueFileFilter.TRUE));
		Collections.sort(files);
		return files;
	}

	private String createUniqueFileMessage(String fileLeftPath) {
		return UNIQUE_FILE_PREFIX + FilenameUtils.getPathNoEndSeparator(fileLeftPath) + UNIQUE_LINE_SPLIT_STR
				+ FilenameUtils.getName(fileLeftPath);
	}

	private String generateFileDiff2Html() throws IOException {
		FileDiffHtmlBuilder htmlBuilder = appendFileDiff2Html(new FileDiffHtmlBuilder(params), params);
		return htmlBuilder.toString();
	}

	private FileDiffHtmlBuilder appendFileDiff2Html(FileDiffHtmlBuilder fileDiffHtmlBuilder, DiffToHtmlParameters params)
			throws IOException {
		if (FileHelper.isFileBinary(params.getInputLeftPath())) {
			return generateBinaryFilesDiff2Html(fileDiffHtmlBuilder, params);
		} else {
			return generateTextFilesDiff2Html(fileDiffHtmlBuilder, params);
		}
	}

	private FileDiffHtmlBuilder generateBinaryFilesDiff2Html(FileDiffHtmlBuilder htmlBuilder, DiffToHtmlParameters params)
			throws IOException {
		htmlBuilder = setFileInfos(htmlBuilder, params);
		if (isInputFilesAreIdentical(params, true)) {
			htmlBuilder.appendInfoLine(createFilesIdenticalMessage(params));
			resultCode = Main.EXIT_CODE_OK;
		} else {
			htmlBuilder.appendAttentionLine(createBinaryFilesDifferMessage(params));
			resultCode = Main.EXIT_CODE_ERROR;
		}
		return htmlBuilder;
	}

	private FileDiffHtmlBuilder generateTextFilesDiff2Html(FileDiffHtmlBuilder htmlBuilder, DiffToHtmlParameters params)
			throws IOException {
		htmlBuilder = setFileInfos(htmlBuilder, params);

		if (isInputFilesAreIdentical(params, false)) {
			htmlBuilder.appendInfoLine(createFilesIdenticalMessage(params));
			resultCode = Main.EXIT_CODE_OK;
		} else {
			JavaDiffUtils2HtmlWrapper diffUtilsWrapper = new JavaDiffUtils2HtmlWrapper();
			diffUtilsWrapper.setContextLinesAmount(contextLinesAmount);
			htmlBuilder = diffUtilsWrapper.makeJavaDiff2Html(htmlBuilder, params);
			resultCode = Main.EXIT_CODE_ERROR;
		}
		return htmlBuilder;
	}

	private FileDiffHtmlBuilder setFileInfos(FileDiffHtmlBuilder htmlBuilder, DiffToHtmlParameters params) throws IOException {
		String fileLeftPath = params.getInputLeftPath();
		String fileRightPath = params.getInputRightPath();

		if (Files.exists(Paths.get(fileLeftPath))) {
			String fileLeftLastModified = Files.getLastModifiedTime(Paths.get(fileLeftPath)).toString();
			htmlBuilder.setFileLeftInfo(new SimpleFileInfo(fileLeftPath, fileLeftLastModified));
		}
		if (Files.exists(Paths.get(fileRightPath))) {
			String fileRightLastModified = Files.getLastModifiedTime(Paths.get(fileRightPath)).toString();
			htmlBuilder.setFileRightInfo(new SimpleFileInfo(fileRightPath, fileRightLastModified));
		}
		return htmlBuilder;
	}

	private boolean isInputFilesAreIdentical(DiffToHtmlParameters params) throws IOException {
		return isInputFilesAreIdentical(params, FileHelper.isFileBinary(params.getInputLeftPath()));
	}

	private boolean isInputFilesAreIdentical(DiffToHtmlParameters params, boolean isFilesBinary) throws IOException {
		if(!isFilesBinary) {
			String text1 = new String(Files.readAllBytes(Paths.get(params.getInputLeftPath())));
			String text2 = new String(Files.readAllBytes(Paths.get(params.getInputRightPath())));
			if(params.isIgnoreWhiteSpaces() || params.isIgnoreSpaceChange()) {
				String replacement = params.isIgnoreWhiteSpaces() ? "" : " ";
				text1 = text1.replaceAll("\\s+", replacement);
				text2 = text2.replaceAll("\\s+", replacement);
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
