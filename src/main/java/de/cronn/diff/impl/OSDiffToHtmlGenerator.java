package de.cronn.diff.impl;

import java.io.IOException;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.SystemUtils;

import de.cronn.diff.impl.os.UnixDiffToHtmlImpl;
import de.cronn.diff.util.DiffToHtmlParameters;

public class OSDiffToHtmlGenerator implements DiffToHtmlGenerator {

	@Override
	public DiffToHtmlResult generateHtml(DiffToHtmlParameters params) throws IOException {

		if (SystemUtils.IS_OS_UNIX) {
			return new UnixDiffToHtmlImpl(params).runDiffToHtml();

		} else {
			throw new NotImplementedException("No support for this operating system.");
		}
	}
}
