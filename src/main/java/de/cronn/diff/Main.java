package de.cronn.diff;

import static de.cronn.diff.util.cli.CliParser.OPT_DETECT_ENCODING;
import static de.cronn.diff.util.cli.CliParser.OPT_IGNORE_LINE_ENDINGS;
import static de.cronn.diff.util.cli.CliParser.OPT_IGNORE_SPACE_CHANGE;
import static de.cronn.diff.util.cli.CliParser.OPT_IGNORE_UNIQUE_FILES;
import static de.cronn.diff.util.cli.CliParser.OPT_IGNORE_WHITESPACES;
import static de.cronn.diff.util.cli.CliParser.OPT_ONLY_REPORTS;
import static de.cronn.diff.util.cli.CliParser.OPT_UNIFIED_CONTEXT;

import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.DiffToHtmlParameters.DiffType;
import de.cronn.diff.util.FileHelper;
import de.cronn.diff.util.cli.CliParser;
import de.cronn.diff.util.cli.DiffToHtmlCommandLine;

public final class Main {
	
    public static final int EXIT_CODE_ERROR = 1;

    public static final int EXIT_CODE_OK = 0;

    public static final int UNIFIED_CONTEXT_LINES = 3;

    public static final String PROGRAM_NAME = "cronn-diff-to-html";

    private static String workingDir = FileHelper.getWorkingDir();

	private Main() {}

	public static void main(String[] args) throws Exception {
		DiffToHtmlCommandLine cli;
		cli = new CliParser(workingDir).parse(args);
		DiffToHtmlParameters parameters = DiffToHtmlParameters.builder()
				.withDiffType(cli.isInputsFiles() ? DiffType.FILES : DiffType.DIRECTORIES)
				.withInputLeftPath(cli.getInputLeft())
				.withInputRightPath(cli.getInputRight())
				.withOutputPath(cli.getOutput())
				.withIgnoreUniqueFiles(cli.hasOption(OPT_IGNORE_UNIQUE_FILES))
				.withIgnoreWhiteSpaces(cli.hasOption(OPT_IGNORE_WHITESPACES))
				.withIgnoreSpaceChange(cli.hasOption(OPT_IGNORE_SPACE_CHANGE))
				.withIgnoreLineEndings(cli.hasOption(OPT_IGNORE_LINE_ENDINGS))
				.withDetectTextFileEncoding(cli.hasOption(OPT_DETECT_ENCODING))
				.withOnlyReports(cli.hasOption(OPT_ONLY_REPORTS))
				.withUnifiedContext(Integer
						.parseInt(cli.getOptionValue(OPT_UNIFIED_CONTEXT, Integer.toString(UNIFIED_CONTEXT_LINES))))
				.build();
		int status = new CronnDiffToHtml().generateDiffToHtmlReport(parameters);
		System.exit(status);
	}
    
    public static String getWorkingDir() {
		return workingDir;
	}

	public static void setWorkingDir(String workingDir) {
		Main.workingDir = workingDir;
	}
}
