package de.cronn.diff.impl;

import static org.junit.Assume.assumeTrue;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import de.cronn.diff.TestBase;
import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.OS;

public class OSDiffToHtmlGeneratorTest extends TestBase {

	@Test(expected=NullPointerException.class) //expected, because using empty parameters
	public void testGenerateHtml_OK_Unix() throws Exception {
		assumeTrue(SystemUtils.IS_OS_UNIX);
		new OSDiffToHtmlGenerator().generateHtml(DiffToHtmlParameters.builder().withOperatingSystem(OS.UNIX).build());
	}
	
	@Test(expected=NotImplementedException.class)
	public void testGenerateHtml_Exception_Windows() throws Exception {
		new OSDiffToHtmlGenerator()
				.generateHtml(DiffToHtmlParameters.builder().withOperatingSystem(OS.WINDOWS).build());
	}

	@Test(expected=NotImplementedException.class)
	public void testGenerateHtml_Exception_Other() throws Exception {
		new OSDiffToHtmlGenerator().generateHtml(DiffToHtmlParameters.builder().withOperatingSystem(OS.SUN).build());
	}
}
