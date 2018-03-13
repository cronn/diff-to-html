package de.cronn.diff.impl.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import de.cronn.diff.Main;
import de.cronn.diff.impl.DiffToHtmlResult;
import de.cronn.diff.impl.os.converter.UnixDirectoryDiffToHtmlConverter;
import de.cronn.diff.impl.os.converter.UnixFileDiffToHtmlConverter;
import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.DiffToHtmlParameters.DiffType;

public class UnixDiffToHtmlImpl {

	private static final String UNIX_DIFF_ARG_RECURSIVE = "-r";

	private static final String UNIX_DIFF_ARG_SHOW_IDENTICAL = "-s";

	private static final String UNIX_DIFF_ARG_UNIFIED_CONTEXT = "-U";

	private static final String UNIX_DIFF_ARG_IGNORE_WHITESPACE = "-w";

	private static final String UNIX_DIFF_ARG_IGNORE_SPACE_CHANGE = "-b";

	private static final String UNIX_DIFF_COMMAND = "diff";

	private static final String WHITESPACE = " ";

	private DiffToHtmlParameters params = null;

	private int resultCode = Main.EXIT_CODE_ERROR;

	public UnixDiffToHtmlImpl(DiffToHtmlParameters params) {
		this.params = params;
	}

	public DiffToHtmlResult runDiffToHtml() throws IOException {
		if (isDiffAvailableInUnix()) {
			return generateUnixDiffToHtmlReport();
		} else {
			throw new RuntimeException("UnixDiff not installed.");
		}
	}

	private boolean isDiffAvailableInUnix() throws IOException {
		String output = getRuntimeCommandOutputAsString(new String[] { "/bin/bash", "-c", "which diff" });
		return output != null;
	}

	private String getRuntimeCommandOutputAsString(String[] commands) throws IOException {
		Locale.setDefault(Locale.ROOT);
		Process process = Runtime.getRuntime().exec(commands);
		String lines;
		try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			lines = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
		}
		return lines;
	}

	private DiffToHtmlResult generateUnixDiffToHtmlReport() throws IOException {
		String diff = generateUnixDiff();
		String html = convertUnixDiffToHtml(diff);
		resultCode = getResultCodeFromUnixDiff(diff);
		return new DiffToHtmlResult(html, resultCode);
	}

	private String generateUnixDiff() throws IOException {
		List<String> arguments = createCommandLineArguments();
		addDiffCommandLineToParameters(arguments);
		return getRuntimeCommandOutputAsString(createRuntimeCommand(arguments));
	}

	private List<String> createCommandLineArguments() {
		List<String> argumentList = new ArrayList<>();
		argumentList.add(UNIX_DIFF_ARG_UNIFIED_CONTEXT);
		argumentList.add(Integer.toString(params.getUnifiedContext()));
		argumentList.add(params.getDiffType() == DiffType.FILES ? UNIX_DIFF_ARG_SHOW_IDENTICAL : UNIX_DIFF_ARG_RECURSIVE);
		if(params.isIgnoreWhiteSpaces()) {
			argumentList.add(UNIX_DIFF_ARG_IGNORE_WHITESPACE);
		}
		if(params.isIgnoreSpaceChange()) {
			argumentList.add(UNIX_DIFF_ARG_IGNORE_SPACE_CHANGE);
		}
		return argumentList;
	}

	private void addDiffCommandLineToParameters(List<String> argumentList) {
		StringBuilder sb = new StringBuilder();
		sb.append(UNIX_DIFF_COMMAND);
		for(String command : argumentList) {
			sb.append(WHITESPACE + command);
		}
		params = DiffToHtmlParameters.builder(params).withDiffCommandLineAsString(sb.toString()).build();
	}

	private String[] createRuntimeCommand(List<String> arguments) {
		List<String> command = new ArrayList<>();
		command.add(UNIX_DIFF_COMMAND);
		command.addAll(arguments);
		command.add(params.getInputLeftPath());
		command.add(params.getInputRightPath());
		return command.toArray(new String[0]);
	}

	private int getResultCodeFromUnixDiff(String diff) {
		if (params.getDiffType() == DiffType.DIRECTORIES) {
			return checkUnixDirDiffResult(diff);
		} else {
			return checkUnixFileDiffResult(diff);
		}
	}

	private int checkUnixDirDiffResult(String diff) {
		if(StringUtils.isBlank(diff) ) {
			return Main.EXIT_CODE_OK;
		} else {
			return Main.EXIT_CODE_ERROR;
		}
	}

	private int checkUnixFileDiffResult(String diff) {
		if (diff.matches("Files " + params.getInputLeftPath() + " and " + params.getInputRightPath() + " are identical")) {
			return Main.EXIT_CODE_OK;
		} else {
			return Main.EXIT_CODE_ERROR;
		}
	}

	private String convertUnixDiffToHtml(String diff) throws IOException {
		if (params.getDiffType() == DiffType.FILES) {
			return new UnixFileDiffToHtmlConverter().convertDiffToHtml(diff, params);
		} else  {
			return new UnixDirectoryDiffToHtmlConverter().convertDiffToHtml(diff, params);
		}
	}
}
