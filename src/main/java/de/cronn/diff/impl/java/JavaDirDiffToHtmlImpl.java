package de.cronn.diff.impl.java;

import static de.cronn.diff.util.DiffToHtmlParameters.DiffSide.LEFT;
import static de.cronn.diff.util.DiffToHtmlParameters.DiffSide.RIGHT;
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
import de.cronn.diff.impl.DiffToHtmlResult;
import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.DiffToHtmlParameters.DiffType;
import de.cronn.diff.util.FileHelper;

public class JavaDirDiffToHtmlImpl extends JavaFileDiffToHtmlImpl {

	public JavaDirDiffToHtmlImpl(DiffToHtmlParameters params) {
		super(params);
	}

	@Override
	public DiffToHtmlResult runDiffToHtml() throws IOException {
		DirectoryDiffHtmlBuilder dirDiffHtmlBuilder = new DirectoryDiffHtmlBuilder(params);
		dirDiffHtmlBuilder = traverseLeftDirectory(dirDiffHtmlBuilder, getSortedFilesAndDirs(params.getInputLeftPath()));
		dirDiffHtmlBuilder = traverseRightDirectory(dirDiffHtmlBuilder, getSortedFilesAndDirs(params.getInputRightPath()));
		String html = dirDiffHtmlBuilder.toString();
		return new DiffToHtmlResult(html, resultCode);
	}

	private ArrayList<File> getSortedFilesAndDirs(String dirPath) {
		ArrayList<File> files = new ArrayList<>(FileUtils.listFilesAndDirs(new File(dirPath), TrueFileFilter.TRUE, TrueFileFilter.TRUE));
		Collections.sort(files);
		return files;
	}

	private DirectoryDiffHtmlBuilder traverseLeftDirectory(DirectoryDiffHtmlBuilder dirDiffHtmlBuilder, ArrayList<File> filesAndDirs) throws IOException {
		for (File fileLeft : filesAndDirs) {
			String fileLeftPath = fileLeft.getPath();
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
		return dirDiffHtmlBuilder;
	}

	private DirectoryDiffHtmlBuilder traverseRightDirectory(DirectoryDiffHtmlBuilder dirDiffHtmlBuilder, ArrayList<File> filesAndDirs) throws IOException {
		for (File fileRight : filesAndDirs) {
			String fileRightPath = fileRight.getPath();
			String fileLeftPath = fileRightPath.replace(params.getInputRightPath(), params.getInputLeftPath());
			DiffToHtmlParameters fileDiffParams = createFileDiffParams(fileLeftPath, fileRightPath);
			if (!Files.exists(Paths.get(fileLeftPath))) {
				makeUniqueFileEntry(dirDiffHtmlBuilder, fileDiffParams, RIGHT);
			}
		}
		return dirDiffHtmlBuilder;
	}

	private DiffToHtmlParameters createFileDiffParams(String fileLeftPath, String fileRightPath) {
		DiffToHtmlParameters fileDiffParams =  DiffToHtmlParameters.builder(params)
				.withDiffType(DiffType.FILES)
				.withInputLeftPath(fileLeftPath)
				.withInputRightPath(fileRightPath)
				.build();
		return fileDiffParams;
	}

	private void makeDifferingFilesEntry(DirectoryDiffHtmlBuilder dirDiffHtmlBuilder, DiffToHtmlParameters diffParams) throws IOException {
		FileDiffHtmlBuilder htmlTableBuilder = appendFileDiffToBuilder(new FileDiffHtmlBuilder(params), diffParams);
		String fileLeftPath = diffParams.getInputLeftPath();

		if (FileHelper.isFileBinary(fileLeftPath)) {
			dirDiffHtmlBuilder.appendChangedBinaryFile(fileLeftPath, htmlTableBuilder.createDiffTable());
		} else {
			dirDiffHtmlBuilder.appendChangedTextFile(fileLeftPath, htmlTableBuilder.createDiffTable());
		}
		resultCode = Main.EXIT_CODE_ERROR;
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
		resultCode = Main.EXIT_CODE_ERROR;
	}

	private String createUniqueFileMessage(String fileLeftPath) {
		return UNIQUE_FILE_PREFIX + FilenameUtils.getPathNoEndSeparator(fileLeftPath) + UNIQUE_LINE_SPLIT_STR
				+ FilenameUtils.getName(fileLeftPath);
	}
}
