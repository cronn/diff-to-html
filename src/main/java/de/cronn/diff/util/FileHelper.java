package de.cronn.diff.util;

import static org.apache.commons.lang3.StringUtils.CR;
import static org.apache.commons.lang3.StringUtils.LF;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.mozilla.universalchardet.Constants;
import org.mozilla.universalchardet.UniversalDetector;

public class FileHelper {

	private static final int BYTE_BUFFER_SIZE_DETECT_BINARY = 16000;
	private static final int BYTE_BUFFER_SIZE_DETECT_ENCODING = 4096;
	public static final String CSS_FILE = "diffToHtml.css";

	private FileHelper( ) {}
	
	private static Map<String, Boolean> binaryFilesMap = new HashMap<>();
		
	// Imitates Unix diff's behavior in determining if a file is binary or text by checking the first few thousand bytes for zero values. See <a href="http://www.gnu.org/software/diffutils/manual/html_node/Binary.html">http://www.gnu.org/software/diffutils/manual/html_node/Binary.html</a>
	public static boolean isFileBinary(String filePath) throws IOException {
		if(binaryFilesMap.containsKey(filePath)) {
			return binaryFilesMap.get(filePath);
		} else {
			boolean isBinary = isFileBinaryCheck(filePath);
			binaryFilesMap.put(filePath, isBinary);
			return isBinary;
		}
	}
	
	private static boolean isFileBinaryCheck(String filePath) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(new File(filePath));
		byte[] buffer = new byte[BYTE_BUFFER_SIZE_DETECT_BINARY];
		int bytesRead = IOUtils.read(fileInputStream, buffer, 0, BYTE_BUFFER_SIZE_DETECT_BINARY);
		fileInputStream.close();

		if(bytesRead == 0) {
			return true;
		}

		for(int i = 0; i < bytesRead; i++) {
			if(buffer[i] == 0) {
				return true;
			}
		}
		return false;
	}


	public static List<String> readAllLinesWithDetectedEncoding(String filePath) throws IOException {
		String encoding = getCharsetEncoding(filePath);
		if (encoding == null) {
			return readAllLines(filePath, StandardCharsets.US_ASCII);

		} else if (encoding.equals(Constants.CHARSET_UTF_8)) {
			return readAllLines(filePath, StandardCharsets.UTF_8);

		} else if (encoding.equals(Constants.CHARSET_WINDOWS_1252)) {
			return readAllLines(filePath, StandardCharsets.ISO_8859_1);

		} else if (encoding.equals(Constants.CHARSET_UTF_16BE)) {
			return readAllLines(filePath, StandardCharsets.UTF_16BE);

		} else if (encoding.equals(Constants.CHARSET_UTF_16LE)) {
			return readAllLines(filePath, StandardCharsets.UTF_16LE);

		} else {
			return readLinesWithEncoding(filePath, encoding);
		}
	}

	static List<String> readLinesWithEncoding(String filePath, String encoding) throws IOException {
		try {
			return readAllLines(filePath, Charset.forName(encoding));
		} catch(IllegalCharsetNameException|UnsupportedCharsetException e) {
			throw new UnsupportedOperationException(
					"The charset encoding '" + encoding + "' of file " + filePath + " is not supported", e);
		}
	}

	static List<String> readAllLines(String filePath, Charset charset) throws IOException {
		try {
			return Files.readAllLines(Paths.get(filePath), charset);
		} catch (MalformedInputException e) {
			throw new UnsupportedOperationException(
					"File " + filePath + " could not be read with charset " + charset.toString(), e);
		}
	}

	static String getCharsetEncoding(String filePath) throws IOException {
		UniversalDetector detector = new UniversalDetector(null);
		FileInputStream fileInputStream = new FileInputStream(new File(filePath));
		byte[] buffer = new byte[BYTE_BUFFER_SIZE_DETECT_ENCODING];

		int bytesRead = IOUtils.read(fileInputStream, buffer, 0, BYTE_BUFFER_SIZE_DETECT_ENCODING);
		while(bytesRead > 0 && !detector.isDone()) {
			detector.handleData(buffer, 0, bytesRead);
			bytesRead = IOUtils.read(fileInputStream, buffer, 0, BYTE_BUFFER_SIZE_DETECT_ENCODING);
		}

		detector.dataEnd();
		return detector.getDetectedCharset();
	}
	
	public static String getWorkingDir() {
		return new File("").getAbsolutePath() + File.separator;
	}

	public static String normalizeLineSeparators(String s) {
		return s.replaceAll(CR + LF, LF);
	}

	public static boolean isFileSizeDifferenceTooBig(String inputLeftPath, String inputRightPath, long maxAllowedDifferenceInByte) {
        long fileSizeLeft = new File (inputLeftPath).length();
        long fileSizeRight = new File(inputRightPath).length();
		long difference = Math.abs(fileSizeLeft-fileSizeRight);

		return difference > maxAllowedDifferenceInByte;
	}
}
