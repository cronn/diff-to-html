package de.cronn.diff.impl.java;

import static de.cronn.diff.Main.EXIT_CODE_ERROR;
import static de.cronn.diff.util.DiffToHtmlParameters.DiffSide.LEFT;
import static de.cronn.diff.util.DiffToHtmlParameters.DiffSide.RIGHT;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import de.cronn.diff.html.DirectoryDiffHtmlBuilder;
import de.cronn.diff.html.FileDiffHtmlBuilder;
import de.cronn.diff.impl.DiffToHtmlResult;
import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.DiffToHtmlParameters.DiffType;
import de.cronn.diff.util.FileHelper;

public class JavaDirDiffToHtmlImpl extends JavaFileDiffToHtmlImpl {
	
	private static final String UNIQUE_FILE_PREFIX = "Only in ";

	private static final String UNIQUE_LINE_SPLIT_STR = ": ";

	public JavaDirDiffToHtmlImpl(DiffToHtmlParameters params) {
		super(params);
	}

	@Override
	public DiffToHtmlResult runDiffToHtml() throws IOException {
		ArrayList<File> leftSortedFilesAndDirs = getSortedFilesAndDirs(params.getInputLeftPath());
		ArrayList<File> rightSortedFilesAndDirs = getSortedFilesAndDirs(params.getInputRightPath());

		if (leftSortedFilesAndDirs.size() > 2 && rightSortedFilesAndDirs.size() > 2) {
			DirectoryDiffHtmlBuilder dirDiffHtmlBuilder = new DirectoryDiffHtmlBuilder(params);
			traverseLeftDirectory(dirDiffHtmlBuilder, leftSortedFilesAndDirs);
			traverseRightDirectory(dirDiffHtmlBuilder, rightSortedFilesAndDirs);
			return new DiffToHtmlResult(dirDiffHtmlBuilder.toString(), resultCode);
		} else {
			FileDiffHtmlBuilder fileDiffHtmlBuilder = new FileDiffHtmlBuilder(params);
			System.out.println("One of the directories seems to be empty. Abort!");
			fileDiffHtmlBuilder.appendAttentionLine("One of the directories seems to be empty. Abort!");
			return new DiffToHtmlResult(fileDiffHtmlBuilder.toString(), EXIT_CODE_ERROR);
		}
	}

	private ArrayList<File> getSortedFilesAndDirs(String dirPath) {
		ArrayList<File> files = new ArrayList<>(FileUtils.listFilesAndDirs(new File(dirPath), TrueFileFilter.TRUE, TrueFileFilter.TRUE));
		Collections.sort(files);
		return files;
	}

	private void traverseLeftDirectory(DirectoryDiffHtmlBuilder dirDiffHtmlBuilder, ArrayList<File> filesAndDirs) throws IOException {
		for (File fileLeft : filesAndDirs) {
			String fileLeftPath = FilenameUtils.separatorsToUnix(fileLeft.getPath());
			String fileRightPath = fileLeftPath.replace(params.getInputLeftPath(), params.getInputRightPath());
			DiffToHtmlParameters fileDiffParams = createFileDiffParams(fileLeftPath, fileRightPath);

			if (Files.exists(Paths.get(fileRightPath))) {
				if (Files.isRegularFile(Paths.get(fileLeftPath)) && !isInputFilesAreIdentical(fileDiffParams)) {
					makeDifferingFilesEntry(dirDiffHtmlBuilder, fileDiffParams);
				}
			} else {
				makeUniqueFileEntry(dirDiffHtmlBuilder, fileDiffParams, LEFT);
			}
		}
	}

	private void traverseRightDirectory(DirectoryDiffHtmlBuilder dirDiffHtmlBuilder, ArrayList<File> filesAndDirs) throws IOException {
		for (File fileRight : filesAndDirs) {
			String fileRightPath = FilenameUtils.separatorsToUnix(fileRight.getPath());
			String fileLeftPath = fileRightPath.replace(params.getInputRightPath(), params.getInputLeftPath());
			DiffToHtmlParameters fileDiffParams = createFileDiffParams(fileLeftPath, fileRightPath);
			if (!Files.exists(Paths.get(fileLeftPath))) {
				makeUniqueFileEntry(dirDiffHtmlBuilder, fileDiffParams, RIGHT);
			}
		}
	}

	private DiffToHtmlParameters createFileDiffParams(String fileLeftPath, String fileRightPath) {
		return DiffToHtmlParameters.builder(params)
				.withDiffType(DiffType.FILES)
				.withInputLeftPath(fileLeftPath)
				.withInputRightPath(fileRightPath)
				.build();
	}

	private void makeDifferingFilesEntry(DirectoryDiffHtmlBuilder dirDiffHtmlBuilder, DiffToHtmlParameters diffParams) throws IOException {
		FileDiffHtmlBuilder htmlTableBuilder = appendFileDiffToBuilder(new FileDiffHtmlBuilder(params), diffParams);
		String fileLeftPath = diffParams.getInputLeftPath();

		if (FileHelper.isFileBinary(fileLeftPath)) {
			dirDiffHtmlBuilder.appendChangedBinaryFile(fileLeftPath, htmlTableBuilder.createDiffTable());
		} else {
			dirDiffHtmlBuilder.appendChangedTextFile(fileLeftPath, htmlTableBuilder.createDiffTable());
		}
		resultCode = EXIT_CODE_ERROR;
	}

	private void makeUniqueFileEntry(DirectoryDiffHtmlBuilder htmlBuilder, DiffToHtmlParameters diffParams, DiffToHtmlParameters.DiffSide diffSide)
			throws IOException {
		if(params.isIgnoreUniqueFiles()) {
			return;
		}
		FileDiffHtmlBuilder htmlTableBuilder = new FileDiffHtmlBuilder(diffParams);
		String filePath;
		if(diffSide == LEFT) {
			filePath = diffParams.getInputLeftPath();
			setLeftFileInfo(htmlTableBuilder, diffParams);
		} else {
			filePath = diffParams.getInputRightPath();
			setRightFileInfo(htmlTableBuilder, diffParams);
		}


		if (Files.isDirectory(Paths.get(filePath)) || FileHelper.isFileBinary(filePath)) {
			htmlTableBuilder.appendInfoLine(createUniqueFileMessage(filePath));
		} else {
			htmlTableBuilder.appendTextFile(new String(Files.readAllBytes(Paths.get(filePath))));
		}

		if(diffSide == LEFT) {
			htmlBuilder.appendUniqueFileLeft(filePath, htmlTableBuilder.createDiffTable());
		} else {
			htmlBuilder.appendUniqueFileRight(filePath, htmlTableBuilder.createDiffTable());
		}
		resultCode = EXIT_CODE_ERROR;
	}

	private String createUniqueFileMessage(String fileLeftPath) {
		return UNIQUE_FILE_PREFIX + FilenameUtils.getPathNoEndSeparator(fileLeftPath) + UNIQUE_LINE_SPLIT_STR
				+ FilenameUtils.getName(fileLeftPath);
	}
}
