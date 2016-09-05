/**
 * Copyright (C) 2013 – 2016 SLUB Dresden & Avantgarde Labs GmbH (<code@dswarm.org>)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dswarm.xmlenhancer.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Ignore;
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

	public static final String TEMP_PREFIX = "temp_";

	/**
	 * we escape non-escaped entities atm, i.e., we do not keep content as it is
	 *
	 * @throws IOException
	 */
	@Ignore
	@Test
	public void xmlEnhancerTest() throws IOException {

		final String inputFileName = "nonEscapedAmp.xml";
		final String expectedOutputFileName = "escapedAmp.xml";

		xmlEnhancerTestInternal(inputFileName, expectedOutputFileName);
	}

	@Test
	public void xmlEnhancerTest3() throws IOException {

		final String inputFileName = "nonEscapedAmp.xml";
		final String expectedOutputFileName = "cdataed_escapedAmp.xml";

		xmlEnhancerTestInternal(inputFileName, expectedOutputFileName);
	}

	@Test
	public void xmlEnhancerTest4() throws IOException {

		final String inputFileName = "nonEscapedAmp_2.xml";
		final String expectedOutputFileName = "cdataed_nonEscapedAmp_2.xml";

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

	@Test
	public void jsoupTest4() throws IOException {

		final String inputFileName = "escaping.xml";
		final String expectedOutputFileName = "escaping_result.xml";

		xmlEnhancerTestInternal(inputFileName, expectedOutputFileName);
	}

	@Test
	public void jsoupTest5() throws IOException {

		final String inputFileName = "testxml_w_amp_in_attr.xml";
		final String expectedOutputFileName = "cdataed_testxml_w_amp_in_attr.xml";

		xmlEnhancerTestInternal(inputFileName, expectedOutputFileName);
	}

	private void xmlEnhancerTestInternal(final String inputFileName, final String expectedOutputFileName) throws IOException {

		final URL inputResourceURL = Resources.getResource(inputFileName);

		final Path inputResourcePath = Paths.get(inputResourceURL.getFile());
		final Path inputResourcePathParent = inputResourcePath.getParent();
		final String outputFilePath = inputResourcePathParent.toString() + File.separator + TEMP_PREFIX + expectedOutputFileName;
		final String inputFilePath = inputResourcePath.toString();

		XMLEnhancer.enhanceXML(inputFilePath, outputFilePath);

		final byte[] outBytes = Files.readAllBytes(Paths.get(outputFilePath));

		final URL expectedOutputResourceURL = Resources.getResource(expectedOutputFileName);
		final ByteSource expectedOutputByteSource = Resources.asByteSource(expectedOutputResourceURL);
		final byte[] expectedOutputBytes = expectedOutputByteSource.read();

		Assert.assertNotNull(outBytes);
		Assert.assertNotNull(expectedOutputBytes);

		final String outString = new String(outBytes, StandardCharsets.UTF_8);
		final String expectedOutputString = new String(expectedOutputBytes, StandardCharsets.UTF_8);

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
