package de.cronn.diff.util;

public class DiffToHtmlParameters {
	public enum DiffType {
		FILES, DIRECTORIES;
	}

	private DiffType diffType;
	private String inputLeftPath;
	private String inputRightPath;
	private String outputPath;
	private String diffCommandLineAsString;
	private boolean ignoreUniqueFiles = false;
	private boolean ignoreWhiteSpaces = false;
	private boolean ignoreSpaceChange = false;
	private boolean detectTextFileEncoding = false;
	private int unifiedContext = 3;

	public DiffToHtmlParameters() {
	}

	public DiffToHtmlParameters(DiffToHtmlParameters other) {
		this.diffType = other.getDiffType();
		this.inputLeftPath = other.getInputLeftPath();
		this.inputRightPath = other.getInputRightPath();
		this.outputPath = other.getOutputPath();
		this.diffCommandLineAsString = other.getDiffCommandLineAsString();
		this.ignoreUniqueFiles = other.isIgnoreUniqueFiles();
		this.ignoreWhiteSpaces = other.isIgnoreWhiteSpaces();
		this.ignoreSpaceChange = other.isIgnoreSpaceChange();
		this.detectTextFileEncoding = other.isDetectTextFileEncoding();
		this.unifiedContext = other.unifiedContext;
	}

	public DiffToHtmlParameters withDiffType(DiffType diffType) {
		this.diffType = diffType;
		return this;
	}

	public DiffType getDiffType() {
		return diffType;
	}

	public DiffToHtmlParameters withInputLeftPath(String inputLeftPath) {
		this.inputLeftPath = inputLeftPath;
		return this;
	}

	public String getInputLeftPath() {
		return inputLeftPath;
	}

	public DiffToHtmlParameters withInputRightPath(String inputRightPath) {
		this.inputRightPath = inputRightPath;
		return this;
	}

	public String getInputRightPath() {
		return inputRightPath;
	}

	public DiffToHtmlParameters withOutputPath(String outputPath) {
		this.outputPath = outputPath;
		return this;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public String getDiffCommandLineAsString() {
		return diffCommandLineAsString;
	}


	public void setDiffCommandLineAsString(String diffCommandLineAsString) {
		this.diffCommandLineAsString = diffCommandLineAsString;
	}

	public DiffToHtmlParameters withIgnoreUniqueFiles(boolean ignoreUniqueFiles) {
		this.ignoreUniqueFiles = ignoreUniqueFiles;
		return this;
	}

	public boolean isIgnoreUniqueFiles() {
		return ignoreUniqueFiles;
	}

	public DiffToHtmlParameters withDetectTextFileEncoding(boolean detectTextFileEncoding) {
		this.detectTextFileEncoding = detectTextFileEncoding;
		return this;
	}

	public boolean isDetectTextFileEncoding() {
		return detectTextFileEncoding;
	}

	public DiffToHtmlParameters withIgnoreWhiteSpaces(boolean ignoreWhiteSpaces) {
		this.ignoreWhiteSpaces = ignoreWhiteSpaces;
		return this;
	}

	public boolean isIgnoreWhiteSpaces() {
		return ignoreWhiteSpaces;
	}

	public DiffToHtmlParameters withIgnoreSpaceChange(boolean ignoreSpaceChange) {
		this.ignoreSpaceChange = ignoreSpaceChange;
		return this;
	}

	public boolean isIgnoreSpaceChange() {
		return ignoreSpaceChange;
	}

	public DiffToHtmlParameters withUnifiedContext(int unifiedContext) {
		this.unifiedContext = unifiedContext;
		return this;
	}

	public int getUnifiedContext() {
		return unifiedContext;
	}
}
