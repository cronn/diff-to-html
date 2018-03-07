package de.cronn.diff.impl;

import java.io.IOException;

import org.apache.commons.lang3.NotImplementedException;

import de.cronn.diff.impl.os.UnixDiffToHtmlImpl;
import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.OS;

public class OSDiffToHtmlGenerator implements DiffToHtmlGenerator {

	@Override
	public DiffToHtmlResult generateHtml(DiffToHtmlParameters params) throws IOException {

		if (params.getOperatingSystem() == OS.UNIX) {
			return new UnixDiffToHtmlImpl(params).runDiffToHtml();

		} else {
			throw new NotImplementedException("No support for this operating system.");
		}
	}
}
