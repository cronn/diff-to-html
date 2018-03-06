package de.cronn.diff;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.text.StrBuilder;

import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.DiffToHtmlParameters.DiffType;

public class Main {

	public static final String FLAG_JAVA_DIFF = "--javadiff";

	public static final String FLAG_OS_DIFF = "--osdiff";

	public static final String FLAG_IGNORE_UNIQUE = "--ignoreunique";

	public static final String FLAG_ONLY_REPORTS = "--onlyreports";

	public static final String FLAG_IGNORE_WHITESPACES = "--ignorewhitespaces";

	public static final String FLAG_DETECT_ENCODING = "--detectencoding";

	public static final int EXIT_CODE_ERROR = 1;

	public static final int EXIT_CODE_OK = 0;

	public static final String PROGRAM_NAME = "cronn-diff-to-html";

	public static boolean terminateVMOnExit = true;

	private boolean useOSDiff = false;

	private boolean ignoreUniqueFiles = false;

	private boolean onlyReports = false;

	private boolean ignoreWhiteSpaces = false;

	private boolean detectTextFileEncoding = false;

	private static class DiffToHtmlIllegalArgException extends IllegalArgumentException {
		private static final long serialVersionUID = 4635432089135223356L;

		public DiffToHtmlIllegalArgException(String msg) {
			super(msg);
		}
	}

	public static void main(String args[]) throws Exception {
		try {
			Main m = new Main();
			int status = m.processDiffToHtml(args);
			exitProgram(status);
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
			exitProgram(EXIT_CODE_ERROR, e);
		}
	}

	private static void exitProgram(int status) throws Exception {
		exitProgram(status, null);
	}

	private static void exitProgram(int status, Exception e) throws Exception {
		if (terminateVMOnExit) {
			System.exit(status);
		} else if (e != null && e.getClass() != DiffToHtmlIllegalArgException.class) {
			throw e;
		}
	}

	private int processDiffToHtml(String[] args) throws IOException, IllegalArgumentException {
		int status = EXIT_CODE_OK;
		if (args.length < 3) {
			throw createIllegalArgException(getWrongNumberOfArgsMessage(args.length), getUsageInstructions());

		} else {
			CronnDiffToHtml cronnDiffer = new CronnDiffToHtml();
			parseOptionalArgs(args);
			cronnDiffer.setUseOSDiffTool(useOSDiff);
			boolean inputsAreFiles = inputsAreTypeFile(args);
			boolean inputsAreDirs = inputsAreTypeDir(args);

			if (!(inputsAreFiles || inputsAreDirs)) {
				throw createIllegalArgException(getInputTypeMessage(), getUsageInstructions());

			} else if (inputsAreFiles) {
				DiffToHtmlParameters params = new DiffToHtmlParameters()
						.withDiffType(DiffType.FILES)
						.withInputLeftPath(args[0])
						.withInputRightPath(args[1])
						.withOutputPath(args[2])
						.withIgnoreUniqueFiles(ignoreUniqueFiles)
						.withIgnoreWhiteSpaces(ignoreWhiteSpaces)
						.withDetectTextFileEncoding(detectTextFileEncoding);
				status = cronnDiffer.generateDiffToHtmlReport(params);

			} else if (inputsAreDirs) {
				DiffToHtmlParameters params = new DiffToHtmlParameters()
						.withDiffType(DiffType.DIRECTORIES)
						.withInputLeftPath(args[0])
						.withInputRightPath(args[1])
						.withOutputPath(args[2])
						.withIgnoreUniqueFiles(ignoreUniqueFiles)
						.withIgnoreWhiteSpaces(ignoreWhiteSpaces)
						.withDetectTextFileEncoding(detectTextFileEncoding);
				status = cronnDiffer.generateDiffToHtmlReport(params);
			}
		}

		return onlyReports ? EXIT_CODE_OK : status;
	}

	private void parseOptionalArgs(String[] args) {
		for (int i = 3; i < args.length; i++) {
			String arg = args[i];
			if (arg.equals(FLAG_JAVA_DIFF)) {
				useOSDiff = false;
			} else if (arg.equals(FLAG_OS_DIFF)) {
				useOSDiff = true;
			} else if (arg.equals(FLAG_IGNORE_UNIQUE)) {
				ignoreUniqueFiles = true;
			} else if (arg.equals(FLAG_ONLY_REPORTS)) {
				onlyReports = true;
			} else if (arg.equals(FLAG_IGNORE_WHITESPACES)) {
				ignoreWhiteSpaces = true;
			} else if (arg.equals(FLAG_DETECT_ENCODING)) {
				detectTextFileEncoding = true;
			} else {
				throw createIllegalArgException(getInvalidArgsMessage(arg), getUsageInstructions());
			}
		}
	}

	private boolean inputsAreTypeFile(String[] args) {
		return Files.isRegularFile(Paths.get(args[0])) && Files.isRegularFile(Paths.get(args[1]));
	}

	private boolean inputsAreTypeDir(String[] args) {
		return Files.isDirectory(Paths.get(args[0])) && Files.isDirectory(Paths.get(args[1]));
	}

	private IllegalArgumentException createIllegalArgException(String... messages) {
		StrBuilder sb = new StrBuilder();
		for (String msg : messages) {
			sb.appendln(msg);
		}
		return new DiffToHtmlIllegalArgException(sb.toString());
	}

	private String getWrongNumberOfArgsMessage(int num) {
		return PROGRAM_NAME + " has terminated with an error: Wrong number of arguments: " + num;
	}

	private String getInvalidArgsMessage(String arg) {
		return PROGRAM_NAME + " has terminated with an error: Invalid argument: [" + arg + "]";
	}

	private String getUsageInstructions() {
		return "Usage for comparing two files: " + PROGRAM_NAME + " <inputFileLeft> <inputFileRight> <outputFile> <optional args>"
				+ "\nUsage for comparing two folders: " + PROGRAM_NAME + " <inputDirLeft> <inputDirRight> <outputFile> <optional args>"
				+ "\n\nOptional one of:"
				+ "\nOptional argument for java-based diff algorithm:         " + FLAG_JAVA_DIFF + " (default)"
				+ "\nOptional argument for OS diff algorithm, e.g. unix diff: " + FLAG_OS_DIFF
				+ "\nOptional argument to ignore unique files:                " + FLAG_IGNORE_UNIQUE
				+ "\nOptional argument to ignore white spaces:                " + FLAG_IGNORE_WHITESPACES
				+ "\nOptional argument to generate only reports:              " + FLAG_ONLY_REPORTS
				+ "\nOptional argument to detect file encoding:               " + FLAG_DETECT_ENCODING;
	}

	private String getInputTypeMessage() {
		return PROGRAM_NAME + "has terminated with an error: Wrong type of input files"
				+ "\nInputs for " + PROGRAM_NAME + " must be a pair of either files or directories";
	}
}
