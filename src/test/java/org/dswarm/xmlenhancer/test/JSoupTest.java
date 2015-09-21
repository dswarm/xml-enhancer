package org.dswarm.xmlenhancer.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import org.apache.commons.io.Charsets;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;
import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

/**
 * @author tgaengler
 */
public class JSoupTest {

	private static final String DUMMY_BASE_URI = "http://example.com";
	private static final String UTF_8 = "UTF-8";
	private static final String START_CDATA = "<![CDATA[";
	private static final String END_CDATA = "]]>";

	@Test
	public void jsoupTest() throws IOException {

		final String inputFileName = "nonEscapedAmp.xml";
		final String expectedOutputFileName = "escapedAmp.xml";

		jsoupTestInternal(inputFileName, expectedOutputFileName);
	}

	@Test
	public void jsoupTest2() throws IOException {

		final String inputFileName = "pnx_dmp.xml";
		final String expectedOutputFileName = "cdataedPNX_DMP.xml";

		jsoupTestInternal(inputFileName, expectedOutputFileName);
	}

	@Test
	public void jsoupTest3() throws IOException {

		final String inputFileName = "testset7.xml";
		final String expectedOutputFileName = "cdataedTestset7.xml";

		jsoupTestInternal(inputFileName, expectedOutputFileName);
	}

	private void jsoupTestInternal(final String inputFileName, final String expectedOutputFileName) throws IOException {

		final URL resource = Resources.getResource(inputFileName);
		final CharSource charSource = Resources.asCharSource(resource, Charsets.UTF_8);
		final String read = charSource.read();

		final List<Node> nodes = Parser.parseXmlFragment(read, DUMMY_BASE_URI);

		printTextNodes(nodes, 0);

		final Path resourcePath = Paths.get(resource.getFile());
		final Path resourcePathParent = resourcePath.getParent();
		final String outResource = resourcePathParent.toString() + File.pathSeparator + expectedOutputFileName;

		final PrintWriter out = new PrintWriter(outResource, UTF_8);

		nodes.forEach(node -> {

			node.ownerDocument().outputSettings().escapeMode(Entities.EscapeMode.xhtml).prettyPrint(false);

			if (node instanceof TextNode) {

				final TextNode textNode = (TextNode) node;

				final String wholeText = textNode.getWholeText();

				out.print(wholeText);

				return;
			}

			final String nodeString = node.toString();
			final String unescapedNodeString = Parser.unescapeEntities(nodeString, true);

			out.print(unescapedNodeString);
		});

		out.flush();
		out.close();

		final byte[] outBytes = Files.readAllBytes(Paths.get(outResource));

		final URL expectedOutputResourceURL = Resources.getResource(expectedOutputFileName);
		final ByteSource expectedOutputByteSource = Resources.asByteSource(expectedOutputResourceURL);
		final byte[] expectedOutputBytes = expectedOutputByteSource.read();

		Assert.assertNotNull(outBytes);
		Assert.assertNotNull(expectedOutputBytes);

		final String outString = new String(outBytes, Charsets.UTF_8);
		final String expectedOutputString = new String (expectedOutputBytes, Charsets.UTF_8);

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

	public void printTextNodes(final List<Node> nodes, final int level) {

		final int currentLevel = level;
		final int newLevel = level + 1;
		nodes.forEach(node -> {

			if (node instanceof Element) {

				Element element = (Element) node;

				final List<Node> childNodes = element.childNodes();

//				System.out.println("element = '" + element.nodeName() + "'");

				printTextNodes(childNodes, newLevel);

				return;
			}

			if (node instanceof TextNode) {

				TextNode textNode = (TextNode) node;
				final String textNodeName = textNode.nodeName();
				final String wholeText = textNode.getWholeText();
				final boolean blank = textNode.isBlank();

				final String text = node.toString();
				final String cleanText = checkAndCleanString(text);
				final String text1 = textNode.text();

				if (text.trim().isEmpty()) {

					final Node parent = node.parent();
					final String parentNodeName = parent.nodeName();

//					System.out.println(
//							"text is empty, y? :: text node name = '" + textNodeName + "' :: is blank = '" + blank + "' :: whole text = '" + wholeText
//									+ "' ::text = '" + text1 + "' :: clean text '" + cleanText + "' (length = '" + cleanText
//									.length() + "' :: original text length = '"
//									+ text.length() + "') :: parent node type = '"
//									+ parent.getClass().getSimpleName() + "' :: parent node name = '" + parentNodeName + "'");

					return;
				}

//				System.out.println(
//						currentLevel + " ::  text node name = '" + textNodeName + "' :: is blank = '" + blank + "' :: whole text = '" + wholeText
//								+ "' :: clean text '" + cleanText + "' :: original text '" + text
//								+ "'");

				final String unescapeEntities = Parser.unescapeEntities(String.format("%s%s%s", START_CDATA, wholeText, END_CDATA), true);

				textNode.text(unescapeEntities);
			}
		});
	}

	private String checkAndCleanString(final String input) {

		if (input == null) {

			return null;
		}

		if (input.isEmpty()) {

			return input;
		}

		final String firstCharacter = input.substring(0, 1);

		final boolean removeFirstCharacter;

		switch (firstCharacter) {

			case "\n":
			case "\r":
			case "\t":

				removeFirstCharacter = true;

				break;
			default:

				removeFirstCharacter = false;
		}

		if (!removeFirstCharacter) {

			return input;
		}

		if (input.length() <= 1) {

			return "";
		}

		final String newInput = input.substring(1, input.length());

		return checkAndCleanString(newInput);
	}
}
