package de.cronn.diff.util.cli;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.commons.cli.AmbiguousOptionException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.io.FilenameUtils;

import de.cronn.diff.util.FileHelper;

public class DiffToHtmlCommandLine extends CommandLine {

	private static final long serialVersionUID = 1L;

	private String inputLeft;

	private String inputRight;

	private String output;

	private boolean inputsAreFiles;

	public DiffToHtmlCommandLine(CommandLine cli) throws ParseException {
		for(Option opt : cli.getOptions()) {
			this.addOption(opt);
		}
		for(String arg : cli.getArgs()) {
			this.addArg(arg);
		}
		checkCliArguments();
		setupInputs();
		setupOutput();
	}

	private void checkCliArguments() throws ParseException {
		String[] noOptArgs = getArgs();
		if(noOptArgs.length < 2) {
			throw new MissingArgumentException("Input arguments are missing");
		} else if(noOptArgs.length > 3) {
			throw new UnrecognizedOptionException("Too many arguments: " + String.join(", ", noOptArgs));
		}
		if(!(inputsAreFiles = isBothInputsFiles(noOptArgs)) && !isBothInputsDirs(noOptArgs)) {
			throw new AmbiguousOptionException("Inputs must be either both regular files or both directories",
					Arrays.asList(noOptArgs[0], noOptArgs[1]));
		}
	}

	private boolean isBothInputsFiles(String[] noOptArgs) {
		return Files.isRegularFile(Paths.get(noOptArgs[0])) && Files.isRegularFile(Paths.get(noOptArgs[1]));
	}

	private boolean isBothInputsDirs(String[] noOptArgs) {
		return Files.isDirectory(Paths.get(noOptArgs[0])) && Files.isDirectory(Paths.get(noOptArgs[1]));
	}

	private void setupInputs() {
		String[] args = getArgs();
		if (args.length >= 2) {
			inputLeft = args[0];
			inputRight = args[1];
		}
	}

	private void setupOutput() {
		if(outputPathSupplied()) {
			output = new File(getArgs()[2]).getAbsolutePath();
		} else {
			output = createOutputHtmlPath();
		}
	}

	private boolean outputPathSupplied() {
		return getArgs().length >= 3;
	}
	
	private String createOutputHtmlPath() {
		return FileHelper.getWorkingDir() + "diff_" + getShortFileName(inputLeft) + "_" + getShortFileName(inputRight) + ".html";
	}

	private String getShortFileName(String f) {
		return FilenameUtils.getBaseName(FilenameUtils.normalizeNoEndSeparator(f));
	}

	public String getInputLeft() {
		return inputLeft;
	}

	public String getInputRight() {
		return inputRight;
	}

	public boolean isInputsFiles() {
		return inputsAreFiles;
	}

	public String getOutput() {
		return output;
	}
}
