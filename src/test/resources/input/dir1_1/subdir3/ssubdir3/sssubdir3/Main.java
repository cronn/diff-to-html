package de.cronn.diff;

import static de.cronn.diff.util.cli.CliParser.OPT_DETECT_ENCODING;
import static de.cronn.diff.util.cli.CliParser.OPT_IGNORE_SPACE_CHANGE;
import static de.cronn.diff.util.cli.CliParser.OPT_IGNORE_UNIQUE_FILES;
import static de.cronn.diff.util.cli.CliParser.OPT_IGNORE_WHITESPACES;
import static de.cronn.diff.util.cli.CliParser.OPT_OS_DIFF;
import static de.cronn.diff.util.cli.CliParser.OPT_UNIFIED_CONTEXT;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.cli.ParseException;

import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.DiffToHtmlParameters.DiffType;
import de.cronn.diff.util.cli.CliParser;
import de.cronn.diff.util.cli.DiffToHtmlCommandLine;

public class Main {

	public static final int EXIT_CODE_ERROR = 1;

	public static final int EXIT_CODE_OK = 0;

	public static final int UNIFIED_CONTEXT_LINES = 3;
	
	public static final String PROGRAM_NAME = "cronn-diff-to-html";

	public static boolean terminateVMOnExit = true;

	public static void main(String args[]) throws Exception {
			Main m = new Main();
			int status = m.processDiffToHtml(args);
			exitProgram(status);
	}

	private static void exitProgram(int status) throws Exception {
		if (terminateVMOnExit) {
			System.exit(status);
		}
	}


	private int processDiffToHtml(String[] args) throws IOException, ParseException {
		int status = EXIT_CODE_OK;

		CronnDiffToHtml cronnDiffer = new CronnDiffToHtml();
		DiffToHtmlCommandLine cli = new CliParser().parse(args);
		cronnDiffer.setUseOSDiffTool(cli.hasOption(OPT_OS_DIFF));
		boolean inputsAreFiles = inputsAreTypeFile(args);
		boolean inputsAreDirs = inputsAreTypeDir(args);

		if (inputsAreFiles) {
			status = cronnDiffer.generateDiffToHtmlReport(createParams(cli, DiffType.FILES));

		} else if (inputsAreDirs) {
			status = cronnDiffer.generateDiffToHtmlReport(createParams(cli, DiffType.DIRECTORIES));
		}

		return cli.hasOption(CliParser.OPT_ONLY_REPORTS) ? EXIT_CODE_OK : status;
	}

	private DiffToHtmlParameters createParams(DiffToHtmlCommandLine cli, DiffType diffType) {
		DiffToHtmlParameters params = new DiffToHtmlParameters()
				.withDiffType(diffType)
				.withInputLeftPath(cli.getInputLeft())
				.withInputRightPath(cli.getInputRight())
				.withOutputPath(cli.getOutput())
				.withIgnoreUniqueFiles(cli.hasOption(OPT_IGNORE_UNIQUE_FILES))
				.withIgnoreWhiteSpaces(cli.hasOption(OPT_IGNORE_WHITESPACES))
				.withIgnoreSpaceChange(cli.hasOption(OPT_IGNORE_SPACE_CHANGE))
				.withDetectTextFileEncoding(cli.hasOption(OPT_DETECT_ENCODING))
				.withUnifiedContext(Integer.parseInt(cli.getOptionValue(OPT_UNIFIED_CONTEXT, Integer.toString(UNIFIED_CONTEXT_LINES))));
		return params;
	}

	private boolean inputsAreTypeFile(String[] args) {
		return Files.isRegularFile(Paths.get(args[0])) && Files.isRegularFile(Paths.get(args[1]));
	}

	private boolean inputsAreTypeDir(String[] args) {
		return Files.isDirectory(Paths.get(args[0])) && Files.isDirectory(Paths.get(args[1]));
	}
}
