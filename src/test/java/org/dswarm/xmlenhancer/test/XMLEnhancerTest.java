package org.dswarm.xmlenhancer.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import org.apache.commons.io.Charsets;
import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

import org.dswarm.xmlenhancer.XMLEnhancer;

/**
 * @author tgaengler
 */
public class XMLEnhancerTest {

	@Test
	public void xmlEnhancerTest() throws IOException {

		final String inputFileName = "nonEscapedAmp.xml";
		final String expectedOutputFileName = "escapedAmp.xml";

		xmlEnhancerTestInternal(inputFileName, expectedOutputFileName);
	}

	@Test
	public void xmlEnhancerTest2() throws IOException {

		final String inputFileName = "pnx_dmp.xml";
		final String expectedOutputFileName = "cdataedPNX_DMP.xml";

		xmlEnhancerTestInternal(inputFileName, expectedOutputFileName);
	}

	@Test
	public void jsoupTest3() throws IOException {

		final String inputFileName = "testset7.xml";
		final String expectedOutputFileName = "cdataedTestset7.xml";

		xmlEnhancerTestInternal(inputFileName, expectedOutputFileName);
	}

	private void xmlEnhancerTestInternal(final String inputFileName, final String expectedOutputFileName) throws IOException {

		final URL inputResourceURL = Resources.getResource(inputFileName);

		final Path inputResourcePath = Paths.get(inputResourceURL.getFile());
		final Path inputResourcePathParent = inputResourcePath.getParent();
		final String outputFilePath = inputResourcePathParent.toString() + File.pathSeparator + expectedOutputFileName;
		final String inputFilePath = inputResourcePath.toString();

		XMLEnhancer.enhanceXML(inputFilePath, outputFilePath);

		final byte[] outBytes = Files.readAllBytes(Paths.get(outputFilePath));

		final URL expectedOutputResourceURL = Resources.getResource(expectedOutputFileName);
		final ByteSource expectedOutputByteSource = Resources.asByteSource(expectedOutputResourceURL);
		final byte[] expectedOutputBytes = expectedOutputByteSource.read();

		Assert.assertNotNull(outBytes);
		Assert.assertNotNull(expectedOutputBytes);

		final String outString = new String(outBytes, Charsets.UTF_8);
		final String expectedOutputString = new String(expectedOutputBytes, Charsets.UTF_8);

		final Diff xmlDiff = DiffBuilder
				.compare(Input.fromString(expectedOutputString).build())
				.withTest(Input.fromString(outString).build())
				.ignoreWhitespace()
				.checkForSimilar()
				.build();

		if (xmlDiff.hasDifferences()) {
			final StringBuilder sb = new StringBuilder("Oi chap, there seem to ba a mishap!");
			for (final Difference difference : xmlDiff.getDifferences()) {
				sb.append('\n').append(difference);
			}
			Assert.fail(sb.toString());
		}
	}
}