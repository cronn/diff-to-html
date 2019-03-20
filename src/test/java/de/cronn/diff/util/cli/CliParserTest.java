package de.cronn.diff.util.cli;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.cli.AmbiguousOptionException;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.text.StrBuilder;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cronn.diff.TestBase;
import de.cronn.diff.util.FileHelper;

public class CliParserTest extends TestBase {
	private static final String WORKING_DIR = FileHelper.getWorkingDir();
	private static final String DIR2 = TEST_DATA_INPUT_DIR + "dir1_2";
	private static final String DIR1 = TEST_DATA_INPUT_DIR + "dir1_1";
	private static final String FILE2 = TEST_DATA_INPUT_DIR + "code1_2.java.example";
	private static final String FILE1 = TEST_DATA_INPUT_DIR + "code1_1.java.example";
	private static final String FILE_OUT = "fileOut";
	private final ByteArrayOutputStream sysOut = new ByteArrayOutputStream();
	private final ByteArrayOutputStream sysError = new ByteArrayOutputStream();

	@Before
	public void setUpStreams() {
		System.setOut(new PrintStream(sysOut));
		System.setErr(new PrintStream(sysError));
	}

	@After
	public void cleanUpStreams() {
		System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
	}

	@Test
	public void testParse_OK_inputsAreFiles() throws Exception {
		DiffToHtmlCommandLine cliArgs = new CliParser(WORKING_DIR).parse(new String[] { FILE1, FILE2, FILE_OUT });
		String cliArgsAsString = getCliArgsAsString(cliArgs);
		assertStringResultEqualToValidation(cliArgsAsString);
	}

	@Test
	public void testParse_OK_inputsAreDirs() throws Exception {
		DiffToHtmlCommandLine cliArgs = new CliParser(WORKING_DIR).parse(new String[] { DIR1, DIR2, FILE_OUT });
		String cliArgsAsString = getCliArgsAsString(cliArgs);
		assertStringResultEqualToValidation(cliArgsAsString);
	}

	@Test
	public void testParse_OK_inputsAreDirs_noOutputSupplied() throws Exception {
		DiffToHtmlCommandLine cliArgs = new CliParser(WORKING_DIR).parse(
				new String[] { DIR1, DIR2});
		String cliArgsAsString = getCliArgsAsString(cliArgs);
		assertStringResultEqualToValidation(cliArgsAsString);
	}
	
	@Test
	public void testParse_Exception_inputsMissing() throws Exception {
		assertExceptionThrownHelpPrinted(new String[] { FILE_OUT },
				MissingArgumentException.class);
	}
	
	@Test
	public void testParse_Exception_inputsDifferentType() throws Exception {
		assertExceptionThrownHelpPrinted(new String[] { DIR1, FILE2, FILE_OUT },
				AmbiguousOptionException.class);
	}

	@Test
	public void testParse_Exception_inputsTooMany() throws Exception {
		assertExceptionThrownHelpPrinted(new String[] { DIR1, FILE2, DIR1, FILE_OUT },
				UnrecognizedOptionException.class);
	}

	@Test
	public void testParse_OK_allCliArgs() throws Exception {
		DiffToHtmlCommandLine cliArgs = new CliParser(WORKING_DIR)
				.parse(new String[] { DIR1, DIR2, FILE_OUT, "-w", "-or", "-iu", "-de" });
		String cliArgsAsString = getCliArgsAsString(cliArgs);
		assertStringResultEqualToValidation(cliArgsAsString);
	}

	@Test
	public void testParse_Exception_unkownOption() throws Exception {
		String[] commandLineOptions = new String[] { FILE1, FILE2, FILE_OUT, "-w", "-or", "-unknowOption",
				"-nothingAtAll" };
		assertExceptionThrownHelpPrinted(commandLineOptions, UnrecognizedOptionException.class);
	}

	@Test
	public void testParse_Exception_help() throws Exception {
		assertExceptionThrownHelpPrinted(new String[] { "" }, MissingArgumentException.class);
	}

	private void assertExceptionThrownHelpPrinted(String[] commandLineOptions, Class<? extends Exception> clazz)
			throws IOException {
		Assertions.assertThatExceptionOfType(clazz)
				.isThrownBy(() -> new CliParser(WORKING_DIR).parse(commandLineOptions));
		assertStringResultEqualToValidation(sysError.toString() + System.lineSeparator() + sysOut.toString());
	}

	private String getCliArgsAsString(DiffToHtmlCommandLine cli) {
		StrBuilder sb = new StrBuilder();

		sb.appendln(cli.getInputLeft());
		sb.appendln(cli.getInputRight());
		sb.appendln(cli.getOutput());
		sb.appendNewLine();

		for (Option option : cli.getOptions()) {
			sb.appendln(option.getOpt());
			sb.appendln(option.getLongOpt());
			sb.appendln(((Class<?>) option.getType()).getSimpleName());

			String[] values = option.getValues();
			if (values != null) {
				for (String s : values) {
					sb.appendln(s);
				}
			}
			sb.appendln(option.getDescription());
			sb.appendNewLine();
		}

		return sb.toString();
	}
}
