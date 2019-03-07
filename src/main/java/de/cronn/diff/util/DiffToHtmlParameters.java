package de.cronn.diff.util;

import org.apache.commons.io.FilenameUtils;

public final class DiffToHtmlParameters {
	public enum DiffType {
		FILES, DIRECTORIES
	}

	public enum DiffSide {
		LEFT, RIGHT;
	}

	private final DiffType diffType;
	private final String inputLeftPath;
	private final String inputRightPath;
	private final String outputPath;
	private final String diffCommandLineAsString;
	private final boolean ignoreUniqueFiles;
	private final boolean ignoreWhiteSpaces;
	private final boolean ignoreSpaceChange;
	private final boolean ignoreLineEndings;
	private final boolean detectTextFileEncoding;
	private final boolean onlyReports;
	private final int unifiedContext;
	private  final long maxAllowedDifferenceInByte;


	private DiffToHtmlParameters(DiffType diffType, String inputLeftPath, String inputRightPath, String outputPath,
			String diffCommandLineAsString, boolean ignoreUniqueFiles, boolean ignoreWhiteSpaces,
			boolean ignoreSpaceChange, boolean ignoreLineEndings, boolean detectTextFileEncoding, boolean onlyReports, int unifiedContext,
			long maxAllowedDifferenceInByte) {
		this.diffType = diffType;
		this.inputLeftPath = inputLeftPath;
		this.inputRightPath = inputRightPath;
		this.outputPath = outputPath;
		this.diffCommandLineAsString = diffCommandLineAsString;
		this.ignoreUniqueFiles = ignoreUniqueFiles;
		this.ignoreWhiteSpaces = ignoreWhiteSpaces;
		this.ignoreSpaceChange = ignoreSpaceChange;
		this.ignoreLineEndings = ignoreLineEndings;
		this.detectTextFileEncoding = detectTextFileEncoding;
		this.onlyReports = onlyReports;
		this.unifiedContext = unifiedContext;
		this.maxAllowedDifferenceInByte = maxAllowedDifferenceInByte;
	}

	public static class Builder {
		private DiffType diffType;
		private String inputLeftPath;
		private String inputRightPath;
		private String outputPath;
		private String diffCommandLineAsString;
		private boolean ignoreUniqueFiles = false;
		private boolean ignoreWhiteSpaces = false;
		private boolean ignoreSpaceChange = false;
		private boolean ignoreLineEndings = false;
		private boolean detectTextFileEncoding = false;
		private boolean onlyReports = false;
		private int unifiedContext = 3;
		private long maxAllowedDifferenceInByte = 5000000;

		private Builder() {
		}

		private Builder (DiffToHtmlParameters other) {
			this.diffType = other.getDiffType();
			this.inputLeftPath = other.getInputLeftPath();
			this.inputRightPath = other.getInputRightPath();
			this.outputPath = other.getOutputPath();
			this.diffCommandLineAsString = other.getDiffCommandLineAsString();
			this.ignoreUniqueFiles = other.isIgnoreUniqueFiles();
			this.ignoreWhiteSpaces = other.isIgnoreWhiteSpaces();
			this.ignoreSpaceChange = other.isIgnoreSpaceChange();
			this.ignoreLineEndings = other.isIgnoreLineEndings();
			this.detectTextFileEncoding = other.isDetectTextFileEncoding();
			this.onlyReports = other.isOnlyReports();
			this.unifiedContext = other.unifiedContext;
			this.maxAllowedDifferenceInByte = maxAllowedDifferenceInByte;
		}

		public Builder withDiffType(DiffType diffType) {
			this.diffType = diffType;
			return this;
		}

		public Builder withInputLeftPath(String inputLeftPath) {
			this.inputLeftPath = FilenameUtils.normalize(inputLeftPath);
			return this;
		}

		public Builder withInputRightPath(String inputRightPath) {
			this.inputRightPath = FilenameUtils.normalize(inputRightPath);
			return this;
		}

		public Builder withOutputPath(String outputPath) {
			this.outputPath = outputPath;
			return this;
		}

		public Builder withIgnoreUniqueFiles(boolean ignoreUniqueFiles) {
			this.ignoreUniqueFiles = ignoreUniqueFiles;
			return this;
		}

		public Builder withDetectTextFileEncoding(boolean detectTextFileEncoding) {
			this.detectTextFileEncoding = detectTextFileEncoding;
			return this;
		}

		public Builder withIgnoreWhiteSpaces(boolean ignoreWhiteSpaces) {
			this.ignoreWhiteSpaces = ignoreWhiteSpaces;
			return this;
		}

		public Builder withIgnoreLineEndings(boolean ignoreLineEndings) {
			this.ignoreLineEndings = ignoreLineEndings;
			return this;
		}

		public Builder withDiffCommandLineAsString(String diffCommandLineAsString) {
			this.diffCommandLineAsString = diffCommandLineAsString;
			return this;
		}

		public Builder withIgnoreSpaceChange(boolean ignoreSpaceChange) {
			this.ignoreSpaceChange = ignoreSpaceChange;
			return this;
		}

		public Builder withOnlyReports(boolean onlyReports) {
			this.onlyReports = onlyReports;
			return this;
		}

		public Builder withUnifiedContext(int unifiedContext) {
			this.unifiedContext = unifiedContext;
			return this;
		}

		public Builder withMaxAllowedDifferenceInByte(long maxAllowedDifferenceInByte) {
			this.maxAllowedDifferenceInByte = maxAllowedDifferenceInByte;
			return this;
		}

		public DiffToHtmlParameters build() {
			return new DiffToHtmlParameters(diffType, inputLeftPath, inputRightPath, outputPath, diffCommandLineAsString,
					ignoreUniqueFiles, ignoreWhiteSpaces, ignoreSpaceChange, ignoreLineEndings, detectTextFileEncoding, onlyReports, unifiedContext, maxAllowedDifferenceInByte);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	public static Builder builder(DiffToHtmlParameters other) {
		return new Builder(other);
	}

	public DiffType getDiffType() {
		return diffType;
	}

	public String getInputLeftPath() {
		return inputLeftPath;
	}

	public String getInputRightPath() {
		return inputRightPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public String getDiffCommandLineAsString() {
		return diffCommandLineAsString;
	}

	public boolean isIgnoreUniqueFiles() {
		return ignoreUniqueFiles;
	}

	public boolean isDetectTextFileEncoding() {
		return detectTextFileEncoding;
	}

	public boolean isIgnoreWhiteSpaces() {
		return ignoreWhiteSpaces;
	}

	public boolean isIgnoreSpaceChange() {
		return ignoreSpaceChange;
	}

	public boolean isIgnoreLineEndings() {
		return ignoreLineEndings;
	}

	public boolean isOnlyReports() {
		return onlyReports;
	}

	public int getUnifiedContext() {
		return unifiedContext;
	}

	public long getMaxAllowedDifferenceInByte() { return maxAllowedDifferenceInByte; }
}
