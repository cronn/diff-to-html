package de.cronn.diff.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import de.cronn.diff.TestBase;

public class FileHelperTest extends TestBase {

	@Test
	public void testIsFileBinary_isBinary() throws Exception {
		boolean isBinary = FileHelper.isFileBinary(TEST_DATA_INPUT_DIR + "binaryFile1_1");
		assertThat(isBinary).isTrue();
	}

	@Test
	public void testIsFileBinary_isNotBinary() throws Exception {
		boolean isBinary = FileHelper.isFileBinary(TEST_DATA_INPUT_DIR + "text1_1.example");
		assertThat(isBinary).isFalse();
	}

	@Test
	public void testIsFileBinary_emptyFileIsBinary() throws Exception {
		boolean isBinary = FileHelper.isFileBinary(TEST_DATA_INPUT_DIR + "emptyFile");
		assertThat(isBinary).isTrue();
	}

	@Test
	public void testReadAllLines_notReadableWithCharset_exception() throws Exception {
		assertThatExceptionOfType(UnsupportedOperationException.class)
				.isThrownBy(() -> FileHelper.readAllLines(	TEST_DATA_INPUT_DIR + "iso88591textfile",
															StandardCharsets.UTF_8))
				.withCauseExactlyInstanceOf(MalformedInputException.class);
	}

	@Test
	public void testReadLinesWithEncoding_illegalCharsetName_exception() throws Exception {
		assertThatExceptionOfType(UnsupportedOperationException.class)
				.isThrownBy(() -> FileHelper.readLinesWithEncoding("", "this is an illegal charset name"))
				.withCauseExactlyInstanceOf(IllegalCharsetNameException.class);
	}

    @Test
    public void testIsFileSizeDifferenceTooBig_true() {
		boolean isTooBig = FileHelper.isFileSizeDifferenceTooBig(TEST_DATA_INPUT_DIR + "smallfile", TEST_DATA_INPUT_DIR + "bigfile", 5l);
		assertThat(isTooBig).isTrue();
	}
	@Test
	public void testIsFileSizeDifferenceTooBig_false() {
		boolean isTooBig = FileHelper.isFileSizeDifferenceTooBig(TEST_DATA_INPUT_DIR + "smallfile", TEST_DATA_INPUT_DIR + "bigfile", 5l);
		assertThat(!isTooBig).isFalse();
	}
}
