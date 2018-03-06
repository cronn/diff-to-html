package de.cronn.diff.util;

public final class UnifiedDiffValues {

	public static final String BLANK = " ";

	public static final String UNIQUE_FILE_PREFIX = "Only in ";

	public static final String UNIQUE_LINE_SPLIT_STR = ": ";

	public static final String BINARY_FILES_DIFFER_PREFIX = "Binary files ";

	public static final String BINARY_FILES_DIFFER_OLD_VERSION_PREFIX = "Files ";

	public static final String NO_NEWLINE_AT_END_OF_FILE_WARNING = "\\ No newline at end of file";

	public static final String DIFF_FILE_LEFT_PREFIX = "---";

	public static final String DIFF_FILE_RIGHT_PREFIX = "+++";

	public static final String INSERTION_PREFIX = "+";

	public static final String DELETION_PREFIX = "-";

	public static final String INFO_PREFIX_SUFFIX = "@@";

	public static final String BINARY_FILES_SUFFIX = " differ";

	public static final String BINARY_LINE_SPLIT_STR = " and ";

	public static final String IDENTICAL_FILES_PREFIX = "Files ";

	public static final String IDENTICAL_FILES_SUFFIX = " are identical";

	public static final String IDENTICAL_LINE_SPLIT_STR = " and ";

	/***
	 * Gets the correct prefix/identifier for a message about differing binary files.
	 * Older diff versions use the message 'Files ... differ' instead of 'Binary files ... differ'
	 */
	public static String getBinaryFilesDifferPrefix(String line) {
		if(isOlderVersionBinaryFilesDifferMessag(line)) {
			return BINARY_FILES_DIFFER_OLD_VERSION_PREFIX;
		}
		return BINARY_FILES_DIFFER_PREFIX;
	}

	private static boolean isOlderVersionBinaryFilesDifferMessag(String line) {
		return line.startsWith(BINARY_FILES_DIFFER_OLD_VERSION_PREFIX) && line.endsWith(BINARY_FILES_SUFFIX);
	}
}
