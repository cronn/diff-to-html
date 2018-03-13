package de.cronn.diff.util.cli;

import java.util.Comparator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.cronn.diff.Main;
import de.cronn.diff.util.OS;

public class CliParser {

	public static final String OPT_OUTPUT = "o";

	public static final String OPT_INPUT = "i";

	public static final String OPT_DETECT_ENCODING = "de";

	public static final String OPT_IGNORE_UNIQUE_FILES = "iu";

	public static final String OPT_OS_DIFF = "od";

	public static final String OPT_ONLY_REPORTS = "or";

	public static final String OPT_IGNORE_WHITESPACES = "w";

	public static final String OPT_IGNORE_SPACE_CHANGE = "b";

	public static final String OPT_UNIFIED_CONTEXT = "u";

	private Options options;

	private HelpFormatter helpFormatter;
	
	private OS os;
	
	private String workingDir;

	public CliParser(OS operatingSystem, String workingDir) {
		this.os = operatingSystem;
		this.workingDir = workingDir;
		options = createOptions();
		helpFormatter = createHelpFormatter();
	}

	private Options createOptions() {
		Options options = new Options();
		options.addOption(new Option(OPT_IGNORE_WHITESPACES, "ignorewhitespaces", false, "ignore all white spaces"));
		options.addOption(new Option(OPT_IGNORE_SPACE_CHANGE, "ignorespacechange", false, "ignore changes in the amount of white space"));
		options.addOption(new Option(OPT_ONLY_REPORTS, "onlyreports", false, "always exits with zero"));
		if(os != OS.WINDOWS) {
			options.addOption(new Option(OPT_OS_DIFF, "osdiff", false, "uses operating system's diff instead of Java implementation and parses the output. Might slightly improve performance, depending on your machine. Try for large diffs in time-critial situations. Windows not supported currently"));
		}
		options.addOption(new Option(OPT_IGNORE_UNIQUE_FILES, "ignoreunique", false, "ignore unique files"));
		options.addOption(new Option(OPT_DETECT_ENCODING, "detectencoding", false, "tries to determine encoding type"));
		options.addOption(Option.builder(OPT_UNIFIED_CONTEXT).longOpt("unified").hasArg()
				.desc("output <arg> (default 3) lines of unified context").build());
		return options;
	}

	private HelpFormatter createHelpFormatter() {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.setOptionComparator(new Comparator<Option>() {
			@Override
			public int compare(Option o1, Option o2) {
				return o1.isRequired() && o2.isRequired() ? 0 : o1.isRequired() ? -1 : 1;
			}
		});
		return helpFormatter;
	}

	public DiffToHtmlCommandLine parse(String[] args) throws ParseException {
		try {
			CommandLine cli = new DefaultParser().parse(options, args);
			return new DiffToHtmlCommandLine(cli, workingDir);
		} catch (ParseException e) {
			helpFormatter.printHelp(Main.PROGRAM_NAME + " <input_left> <input_right> [<output_html>] ", options, true);
			System.err.println("Parsing failed. Reason: " + e.getMessage());
			throw e;
		}
	}
}
