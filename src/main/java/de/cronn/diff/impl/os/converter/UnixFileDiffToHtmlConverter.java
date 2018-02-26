package de.cronn.diff.impl.os.converter;

import static de.cronn.diff.util.UnifiedDiffValues.BINARY_FILES_SUFFIX;
import static de.cronn.diff.util.UnifiedDiffValues.BINARY_LINE_SPLIT_STR;
import static de.cronn.diff.util.UnifiedDiffValues.DELETION_PREFIX;
import static de.cronn.diff.util.UnifiedDiffValues.DIFF_FILE_LEFT_PREFIX;
import static de.cronn.diff.util.UnifiedDiffValues.DIFF_FILE_RIGHT_PREFIX;
import static de.cronn.diff.util.UnifiedDiffValues.IDENTICAL_FILES_PREFIX;
import static de.cronn.diff.util.UnifiedDiffValues.IDENTICAL_FILES_SUFFIX;
import static de.cronn.diff.util.UnifiedDiffValues.IDENTICAL_LINE_SPLIT_STR;
import static de.cronn.diff.util.UnifiedDiffValues.INFO_PREFIX_SUFFIX;
import static de.cronn.diff.util.UnifiedDiffValues.INSERTION_PREFIX;
import static de.cronn.diff.util.UnifiedDiffValues.NO_NEWLINE_AT_END_OF_FILE_WARNING;
import static de.cronn.diff.util.UnifiedDiffValues.UNIQUE_FILE_PREFIX;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import de.cronn.diff.html.FileDiffHtmlBuilder;
import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.SimpleFileInfo;
import de.cronn.diff.util.UnifiedDiffValues;
import j2html.tags.Tag;

public class UnixFileDiffToHtmlConverter {


	private DiffLinesInfo currentDiffLinesInfo = new DiffLinesInfo();

	public String convertDiffToHtml(String diff, DiffToHtmlParameters params) throws IOException {
		FileDiffHtmlBuilder htmlBuilder = new FileDiffHtmlBuilder(params);
		parseAllDiffLines(diff, htmlBuilder);
		return htmlBuilder.toString();
	}

	public Tag convertDiffToHtmlTable(String diff, DiffToHtmlParameters params) throws IOException {
		FileDiffHtmlBuilder htmlBuilder = new FileDiffHtmlBuilder(params);
		parseAllDiffLines(diff, htmlBuilder);
		return htmlBuilder.createDiffTable();
	}

	private void parseAllDiffLines(String diff, FileDiffHtmlBuilder htmlBuilder) throws IOException {
		try (BufferedReader br = new BufferedReader(new StringReader(diff))) {
			String line;
			while ((line = br.readLine()) != null) {
				parseDiffLine(htmlBuilder, line);
			}
		}
	}

	protected void parseDiffLine(FileDiffHtmlBuilder htmlBuilder, String line) throws IOException {
		if (line.startsWith(DIFF_FILE_LEFT_PREFIX)) {
			htmlBuilder.setFileLeftInfo(parseFileInfo(line, DIFF_FILE_LEFT_PREFIX));

		} else if (line.startsWith(DIFF_FILE_RIGHT_PREFIX)) {
			htmlBuilder.setFileRightInfo(parseFileInfo(line, DIFF_FILE_RIGHT_PREFIX));

		} else if (line.startsWith(INFO_PREFIX_SUFFIX) && line.endsWith(INFO_PREFIX_SUFFIX)) {
			parseInfoLineToCurrentDiffLinesInfo(line);
			htmlBuilder.appendEmptyLine();
			htmlBuilder.appendInfoLine(line);

		} else if (line.startsWith(DELETION_PREFIX)) {
			htmlBuilder.appendDeletionLine(line, currentDiffLinesInfo.lineLeft++, currentDiffLinesInfo.lineRight);

		} else if (line.startsWith(INSERTION_PREFIX)) {
			htmlBuilder.appendInsertionLine(line, currentDiffLinesInfo.lineLeft, currentDiffLinesInfo.lineRight++);

		} else if (line.startsWith(UnifiedDiffValues.getBinaryFilesDifferPrefix(line))) {
			SimpleFileInfo[] binaryFileInfos = getBinaryFileInfos(line);
			htmlBuilder.setFileInfoPair(binaryFileInfos);
			htmlBuilder.appendAttentionLine(line);

		} else if (line.startsWith(IDENTICAL_FILES_PREFIX) && line.endsWith(IDENTICAL_FILES_SUFFIX)) {
			SimpleFileInfo[] identicalFileInfos = getIdenticalFileInfos(line);
			htmlBuilder.setFileInfoPair(identicalFileInfos);
			htmlBuilder.appendInfoLine(line);

		} else if (line.startsWith(UNIQUE_FILE_PREFIX)) {
			htmlBuilder.setFileLeftInfo(getUniqueFileInfo(line));
			htmlBuilder.appendAttentionLine(line);
		}
		else if (line.equals(NO_NEWLINE_AT_END_OF_FILE_WARNING)) {
			htmlBuilder.appendInfoLine(line);
			
		} else {
			htmlBuilder.appendUnchangedLine(line, currentDiffLinesInfo.lineLeft++, currentDiffLinesInfo.lineRight++);
		}
	}

	private SimpleFileInfo getUniqueFileInfo(String line) {
		String path = StringUtils.substringAfter(line, UNIQUE_FILE_PREFIX).replace(": ", File.separator).trim();
		return new SimpleFileInfo(path);
	}

	private SimpleFileInfo[] getBinaryFileInfos(String line) {
		String binaryFilesDifferPrefix = UnifiedDiffValues.getBinaryFilesDifferPrefix(line);
		String[] paths = StringUtils.substringBeforeLast(
				StringUtils.substringAfter(line, binaryFilesDifferPrefix),
				BINARY_FILES_SUFFIX).split(BINARY_LINE_SPLIT_STR);
		return createFileInfoPair(paths);
	}

	private SimpleFileInfo[] getIdenticalFileInfos(String line) {
		String[] paths = StringUtils.substringBeforeLast(
				StringUtils.substringAfter(line, IDENTICAL_FILES_PREFIX),
				IDENTICAL_FILES_SUFFIX).split(IDENTICAL_LINE_SPLIT_STR);
		return createFileInfoPair(paths);
	}

	private SimpleFileInfo[] createFileInfoPair(String[] paths) {
		if (paths.length == 2) {
			SimpleFileInfo fileInfoLeft = new SimpleFileInfo(paths[0].trim());
			SimpleFileInfo fileInfoRight = new SimpleFileInfo(paths[1].trim());
			return new SimpleFileInfo[]{fileInfoLeft, fileInfoRight};
		}
		return new SimpleFileInfo[]{new SimpleFileInfo(), new SimpleFileInfo()};
	}

	private SimpleFileInfo parseFileInfo(String line, String identifierPrefix) {
		String substringAfter = StringUtils.substringAfter(line, identifierPrefix);
		String[] fileInfos = substringAfter.split("\t");
		SimpleFileInfo fInfo = new SimpleFileInfo();
		fInfo.setPath(fileInfos.length > 0 ? fileInfos[0].trim() : "");
		fInfo.setLastModified(fileInfos.length > 1 ? fileInfos[1] : "");
		return fInfo;
	}

	private class DiffLinesInfo {
		private Integer lineLeft = -1;
		private Integer lineRight = -1;
	}

	private static final Pattern diffLineNrsPatternLeft = Pattern.compile("[-]([0-9]+),[0-9]+");
	private static final Pattern diffLineNrsPatternRight = Pattern.compile("[+]([0-9]+),[0-9]+");

	private void parseInfoLineToCurrentDiffLinesInfo(String infoLine) {
		Matcher matcherLeft = diffLineNrsPatternLeft.matcher(infoLine);
		if (matcherLeft.find()) {
			currentDiffLinesInfo.lineLeft = new Integer(matcherLeft.group(1));
		}
		Matcher matcherRight = diffLineNrsPatternRight.matcher(infoLine);
		if (matcherRight.find()) {
			currentDiffLinesInfo.lineRight = new Integer(matcherRight.group(1));
		}
	}

	String getUniqueFileNameFromCurrentDiffLine() {
		return null;
	}
}
