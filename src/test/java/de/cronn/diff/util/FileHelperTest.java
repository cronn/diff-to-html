package de.cronn.diff.util;

import static org.assertj.core.api.Assertions.assertThat;

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
}
