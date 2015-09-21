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
package org.dswarm.xmlenhancer.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

/**
 * @author tgaengler
 */
public class AaltoXmlTest {

	/**
	 * does not work, just for investigation and evaluation
	 *
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	//@Test
	public void aaltoXmlTest() throws IOException, XMLStreamException {

		// http://fasterxml.github.io/stax2-api/javadoc/3.1.4/org/codehaus/stax2/XMLInputFactory2.html
		final AsyncXMLInputFactory inputF = new InputFactoryImpl();
		inputF.configureForRoundTripping();
		//inputF.setProperty(XMLInputFactory2.P_REPORT_ALL_TEXT_AS_CHARACTERS, "false");

		final URL resource = Resources.getResource("nonEscapedAmp.xml");
		final ByteSource byteSource = Resources.asByteSource(resource);
		final byte[] bytes = byteSource.read();

		final AsyncXMLStreamReader<AsyncByteArrayFeeder> parser = inputF.createAsyncFor(bytes);

		while (parser.hasNext()) {

			final int nextPartType = parser.next();

			final String partType;

			switch (nextPartType) {

				case XMLStreamConstants.ATTRIBUTE:

					partType = "attribute";

					break;
				case XMLStreamConstants.CDATA:

					partType = "cdata";

					break;
				case XMLStreamConstants.CHARACTERS:

					partType = "characters";

					break;
				case XMLStreamConstants.COMMENT:

					partType = "comment";

					break;
				case XMLStreamConstants.DTD:

					partType = "dtd";

					break;
				case XMLStreamConstants.END_DOCUMENT:

					partType = "end_document";

					break;
				case XMLStreamConstants.END_ELEMENT:

					partType = "end_element";

					break;
				case XMLStreamConstants.ENTITY_DECLARATION:

					partType = "entity_declaration";

					break;
				case XMLStreamConstants.ENTITY_REFERENCE:

					partType = "entity_reference";

					break;
				case XMLStreamConstants.NAMESPACE:

					partType = "namespace";

					break;
				case XMLStreamConstants.NOTATION_DECLARATION:

					partType = "notation_declaration";

					break;
				case XMLStreamConstants.PROCESSING_INSTRUCTION:

					partType = "processing_instruction";

					break;
				case XMLStreamConstants.SPACE:

					partType = "space";

					break;
				case XMLStreamConstants.START_DOCUMENT:

					partType = "start_document";

					break;
				case XMLStreamConstants.START_ELEMENT:

					partType = "start_element";

					break;
				default:

					partType = "UNKNOWN PART TYPE = '" + nextPartType + "'";
			}

			final StringBuilder sb = new StringBuilder();
			sb.append(partType).append(" :: ");

			if (parser.hasText()) {

				sb.append("'").append(parser.getText()).append("'");
			}

			System.out.println(sb.toString());
		}
	}

	/**
	 * does not work, just for investigation and evaluation
	 *
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	//@Test
	public void aaltoXmlTest2() throws IOException, XMLStreamException {

		final AsyncXMLInputFactory inputF = new InputFactoryImpl();
		inputF.configureForRoundTripping();
		//inputF.setProperty(XMLInputFactory2.P_REPORT_ALL_TEXT_AS_CHARACTERS, "false");

		final URL resource = Resources.getResource("nonEscapedAmp.xml");
		final ByteSource byteSource = Resources.asByteSource(resource);
		final InputStream inputStream = byteSource.openBufferedStream();

		final XMLEventReader reader = inputF.createXMLEventReader(inputStream);

		while (reader.hasNext()) {

			final Object next = reader.next();

			System.out.println("next object class = '" + next.getClass().getSimpleName() + "'");
		}
	}
}
