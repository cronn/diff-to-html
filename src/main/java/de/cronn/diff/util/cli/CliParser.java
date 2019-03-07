package de.cronn.diff.util.cli;

import static java.lang.String.format;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.cronn.diff.Main;

public class CliParser {
	
	public static final String OPT_DETECT_ENCODING = "de";

	public static final String OPT_IGNORE_UNIQUE_FILES = "iu";

	public static final String OPT_ONLY_REPORTS = "or";

	public static final String OPT_IGNORE_WHITESPACES = "w";

	public static final String OPT_IGNORE_SPACE_CHANGE = "b";
	
	public static final String OPT_IGNORE_LINE_ENDINGS = "crlf";

	public static final String OPT_UNIFIED_CONTEXT = "u";

	public static final String OPT_MAX_ALLOWED_FILESIZE_DIFFERENCE = "fs";

	private Options options;

	private HelpFormatter helpFormatter;
	
	private String workingDir;

	public CliParser(String workingDir) {
		this.workingDir = workingDir;
		options = createOptions();
		helpFormatter = createHelpFormatter();
	}

	private Options createOptions() {
		Options options = new Options();
		options.addOption(new Option(OPT_IGNORE_WHITESPACES, "ignore-white-spaces", false, "ignore all white spaces"));
		options.addOption(new Option(OPT_IGNORE_SPACE_CHANGE, "ignore-space-change", false, "ignore changes in the amount of white space"));
		options.addOption(new Option(OPT_IGNORE_LINE_ENDINGS, "ignore-line-endings", false, "ignore line endings, i.e. normalize CRLF / LF while comparing files"));
		options.addOption(new Option(OPT_ONLY_REPORTS, "only-reports", false, "always exits with zero"));
		options.addOption(new Option(OPT_IGNORE_UNIQUE_FILES, "ignore-unique", false, "ignore unique files"));
		options.addOption(new Option(OPT_DETECT_ENCODING, "detect-encoding", false, "tries to determine encoding type"));
		options.addOption(Option.builder(OPT_UNIFIED_CONTEXT).longOpt("unified").hasArg()
				.desc("output <arg> (default 3) lines of unified context").build());
		options.addOption(new Option(OPT_MAX_ALLOWED_FILESIZE_DIFFERENCE, "max-size-diff", true, "no textual diff if file size differs too much"));
		return options;
	}

	private HelpFormatter createHelpFormatter() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setOptionComparator((Option o1, Option o2) -> {
			if (o1.isRequired() && o2.isRequired()) {
				return 0;
			}
			return o1.isRequired() ? -1 : 1;
		});
		return formatter;
	}

	public DiffToHtmlCommandLine parse(String[] args) throws ParseException {
		try {
			CommandLine cli = new DefaultParser().parse(options, args);
			return new DiffToHtmlCommandLine(cli, workingDir);
		} catch (ParseException e) {
			helpFormatter.printHelp(Main.PROGRAM_NAME + " <input_left> <input_right> [<output_html>] ", options, true);
			System.err.println(format("Parsing failed. Reason: %1$s", e.getMessage()));
			throw e;
		}
	}
}
