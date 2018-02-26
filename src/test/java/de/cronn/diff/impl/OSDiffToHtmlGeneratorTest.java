package de.cronn.diff.impl;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cronn.diff.TestBase;
import de.cronn.diff.util.DiffToHtmlParameters;

public class OSDiffToHtmlGeneratorTest extends TestBase {

	@Before
	public void setUp() {
		backupOrigOS();
	}

	@After
	public void resetOs() throws Exception {
		resetOS();
	}

	@Test(expected=NullPointerException.class) //expected, because using empty parameters
	public void testGenerateHtml_OK_Unix() throws Exception {
		setOS(OS.UNIX);
		new OSDiffToHtmlGenerator().generateHtml(DiffToHtmlParameters.builder().build());
	}
	
	@Test(expected=NotImplementedException.class)
	public void testGenerateHtml_Exception_Windows() throws Exception {
		setOS(OS.WINDOWS);
		new OSDiffToHtmlGenerator().generateHtml(DiffToHtmlParameters.builder().build());
	}

	@Test(expected=NotImplementedException.class)
	public void testGenerateHtml_Exception_Other() throws Exception {
		setOS(OS.SUN);
		new OSDiffToHtmlGenerator().generateHtml(DiffToHtmlParameters.builder().build());
	}
}
