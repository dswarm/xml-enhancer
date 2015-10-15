/**
 * Copyright (C) 2013 – 2015 SLUB Dresden & Avantgarde Labs GmbH (<code@dswarm.org>)
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
package org.dswarm.xmlenhancer;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.Charsets;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;

/**
 * Tries to enhance (maybe) invalid XML as much as possible.<br/>
 * Currently, it tries to the following:<br/>
 * - add CDATA to all texts of XML Elements<br/>
 *
 * @author tgaengler
 */
public class XMLEnhancer {

	private static final String DUMMY_BASE_URI = "http://example.com";
	private static final String UTF_8          = "UTF-8";
	private static final String START_CDATA    = "<![CDATA[";
	private static final String END_CDATA      = "]]>";

	public static void enhanceXML(final String inputFileName, final String outputFileName) throws IOException {

		final Path inputFilePath = Paths.get(inputFileName);
		final byte[] inputBytes = Files.readAllBytes(inputFilePath);
		final String inputString = new String(inputBytes, Charsets.UTF_8);

		final List<Node> nodes = Parser.parseXmlFragment(inputString, DUMMY_BASE_URI);

		enhanceNodes(nodes);

		final Path outputFilePath = Paths.get(outputFileName);
		final String outResource = outputFilePath.toString();

		final PrintWriter out = new PrintWriter(outResource, UTF_8);

		nodes.forEach(node -> {

			node.ownerDocument().outputSettings().escapeMode(Entities.EscapeMode.xhtml).preserveCase(true).prettyPrint(false);

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
	}

	private static void enhanceNodes(final List<Node> nodes) {

		nodes.forEach(node -> {

			if (node instanceof Element) {

				Element element = (Element) node;

				final List<Node> childNodes = element.childNodes();

				enhanceNodes(childNodes);

				return;
			}

			if (node instanceof TextNode) {

				final TextNode textNode = (TextNode) node;
				final String wholeText = textNode.getWholeText();
				final String text = node.toString();

				if (text.trim().isEmpty()) {

					return;
				}

				if (wholeText.startsWith(START_CDATA)) {

					// do not add CDATA multiple times

					return;
				}

				final String alignedText = alignTextWithWholeText(wholeText, text);

				final String unescapeEntities = String.format("%s%s%s", START_CDATA, alignedText, END_CDATA);

				textNode.text(unescapeEntities);
			}
		});
	}

	private static String alignTextWithWholeText(final String wholeText, final String text) {

		// align start and ending between whole text and text
		String cuttedStartText = text;

		int j = 0;

		for(int i = 0; i < text.length(); i++) {

			final char currentTextCharacter = text.charAt(i);
			final char currentWholeTextCharacter = wholeText.charAt(j);

			if(currentTextCharacter == currentWholeTextCharacter) {

				if(currentTextCharacter != '\n' && currentTextCharacter != '\r' && currentTextCharacter != '\t') {

					// hit first real character, now move back to the point where both matched first

					cuttedStartText = text.substring(i - j, text.length());

					break;
				}

				j++;
			}
		}

		return cuttedStartText;
	}
}
