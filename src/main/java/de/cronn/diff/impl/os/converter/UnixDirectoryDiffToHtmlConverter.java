package de.cronn.diff.impl.os.converter;

import static de.cronn.diff.impl.os.converter.UnixDirectoryDiffToHtmlConverter.ParserState.AT_BINARY_DIFF_LINE;
import static de.cronn.diff.impl.os.converter.UnixDirectoryDiffToHtmlConverter.ParserState.AT_FILE_DIFF_START;
import static de.cronn.diff.impl.os.converter.UnixDirectoryDiffToHtmlConverter.ParserState.AT_UNIQUE_FILE_LEFT_DIFF_LINE;
import static de.cronn.diff.impl.os.converter.UnixDirectoryDiffToHtmlConverter.ParserState.AT_UNIQUE_FILE_RIGHT_DIFF_LINE;
import static de.cronn.diff.impl.os.converter.UnixDirectoryDiffToHtmlConverter.ParserState.IDLE;
import static de.cronn.diff.impl.os.converter.UnixDirectoryDiffToHtmlConverter.ParserState.READING_FILE_DIFF_LINE;
import static de.cronn.diff.util.UnifiedDiffValues.BLANK;
import static de.cronn.diff.util.UnifiedDiffValues.UNIQUE_FILE_PREFIX;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.lang3.StringUtils;

import de.cronn.diff.html.DirectoryDiffHtmlBuilder;
import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.DiffToHtmlParameters.DiffType;
import de.cronn.diff.util.UnifiedDiffValues;
import j2html.tags.Tag;

public class UnixDirectoryDiffToHtmlConverter {

	private String line = "";

	enum ParserState {
		AT_BINARY_DIFF_LINE,
		AT_UNIQUE_FILE_LEFT_DIFF_LINE,
		AT_UNIQUE_FILE_RIGHT_DIFF_LINE,
		AT_FILE_DIFF_START,
		READING_FILE_DIFF_LINE,
		IDLE
	}

	private ParserState parserState = IDLE;

	private ParserState lastParserState = IDLE;

	private String diff_unique_prefixLeft = "";

	private String diff_unique_prefixRight = "";

	private String diff_files_prefix = "";

	private StringBuilder currentFileDiffBuilder = new StringBuilder();

	private DiffToHtmlParameters params;

	private String currentDiffedFile;

	public String convertDiffToHtml(String dirDiff, DiffToHtmlParameters params) throws IOException {
		this.params = params;
		setupLineIdentifiers(params);
		DirectoryDiffHtmlBuilder mainHtmlBuilder = new DirectoryDiffHtmlBuilder(params);

		BufferedReader br = new BufferedReader(new StringReader(dirDiff));
		while ((line = br.readLine()) != null) {
			getParserState();
			if (finishedReadingFileDiff()) {
				mainHtmlBuilder.appendChangedTextFile(currentDiffedFile, createDiffTable());
			}

			if (parserState == AT_BINARY_DIFF_LINE) {
				resetToNextDiff();
				appendCurrentLine();
				mainHtmlBuilder.appendChangedBinaryFile(currentDiffedFile, createDiffTable());
			} else if (parserState == AT_UNIQUE_FILE_LEFT_DIFF_LINE) {
				resetToNextDiff();
				appendCurrentLine();
				if(!params.isIgnoreUniqueFiles()) {
					mainHtmlBuilder.appendUniqueFileLeft(currentDiffedFile, createDiffTable());
				}
			} else if (parserState == AT_UNIQUE_FILE_RIGHT_DIFF_LINE) {
				resetToNextDiff();
				appendCurrentLine();
				if(!params.isIgnoreUniqueFiles()) {
					mainHtmlBuilder.appendUniqueFileRight(currentDiffedFile, createDiffTable());
				}
			} else if (parserState == AT_FILE_DIFF_START) {
				resetToNextDiff();

			} else if (parserState == READING_FILE_DIFF_LINE) {
				appendCurrentLine();
			}
		}
		if (finishedReadingFileDiff()) {
			mainHtmlBuilder.appendChangedTextFile(currentDiffedFile, createDiffTable());
		}

		return mainHtmlBuilder.toString();
	}

	private void setupLineIdentifiers(DiffToHtmlParameters params) {
		diff_unique_prefixLeft = UNIQUE_FILE_PREFIX + params.getInputLeftPath();
		diff_unique_prefixRight = UNIQUE_FILE_PREFIX + params.getInputRightPath();
		diff_files_prefix = params.getDiffCommandLineAsString() + " " + params.getInputLeftPath();
	}

	private void getParserState() throws IOException {
		lastParserState = parserState;

		if (line.startsWith(diff_unique_prefixLeft)) {
			parserState = AT_UNIQUE_FILE_LEFT_DIFF_LINE;

		} else if (line.startsWith(diff_unique_prefixRight)) {
			parserState = AT_UNIQUE_FILE_RIGHT_DIFF_LINE;

		} else if (line.startsWith(UnifiedDiffValues.getBinaryFilesDifferPrefix(line))) {
			parserState = AT_BINARY_DIFF_LINE;

		} else if (line.startsWith(diff_files_prefix)) {
			parserState = AT_FILE_DIFF_START;

		} else if (lastParserState == AT_FILE_DIFF_START) {
			parserState = READING_FILE_DIFF_LINE;
		}
	}

	private void resetToNextDiff() {
		currentDiffedFile = getDiffedFileFromCurrentLine();
		currentFileDiffBuilder = new StringBuilder();
	}

	private Tag createDiffTable() throws IOException {
		UnixFileDiffToHtmlConverter converter = new UnixFileDiffToHtmlConverter();
		DiffToHtmlParameters fileParams = DiffToHtmlParameters.builder()
				.withDiffType(DiffType.FILES)
				.withInputLeftPath(params.getInputLeftPath())
				.withInputRightPath(params.getInputRightPath())
				.withOutputPath(params.getOutputPath())
				.build();

		Tag diffTable = converter.convertDiffToHtmlTable(currentFileDiffBuilder.toString(), fileParams);
		return diffTable;
	}

	private String getDiffedFileFromCurrentLine() {
		switch (parserState) {
		case AT_BINARY_DIFF_LINE:
			return getBinaryFileNameFromCurrentDiffLine();
		case AT_UNIQUE_FILE_LEFT_DIFF_LINE:
		case AT_UNIQUE_FILE_RIGHT_DIFF_LINE:
			return getUniqueFileNameFromCurrentDiffLine();
		default:
			return getTextFileNameFromCurrentDiffLine();
		}
	}

	protected String getUniqueFileNameFromCurrentDiffLine() {
		return StringUtils.substringAfter(line, UNIQUE_FILE_PREFIX).replace(": ", "/").trim();
	}

	private String getBinaryFileNameFromCurrentDiffLine() {
		return getFirstContinuousStringAfterSeparator(UnifiedDiffValues.getBinaryFilesDifferPrefix(line));
	}

	private String getTextFileNameFromCurrentDiffLine() {
		String separator = params.getDiffCommandLineAsString();
		return getFirstContinuousStringAfterSeparator(separator);
	}

	private String getFirstContinuousStringAfterSeparator(String separator) {
		String[] filePaths = StringUtils.substringAfter(line, separator).trim().split(BLANK);
		if (filePaths.length > 0) {
			return filePaths[0].trim();
		}
		return "";
	}

	private void appendCurrentLine() {
		currentFileDiffBuilder.append(line);
		currentFileDiffBuilder.append(System.lineSeparator());
	}

	private boolean finishedReadingFileDiff() {
		return (parserState != READING_FILE_DIFF_LINE || reachedEndOfFile())
				&& (lastParserState == READING_FILE_DIFF_LINE);
	}

	private boolean reachedEndOfFile() {
		return line == null;
	}
}
