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

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.XMLConstants;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.google.common.io.Resources;

/**
 * @author tgaengler
 */
public class XSLTTest {

	private static final String TRANSFORMER_FACTORY_CLASS = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";

	private static final TransformerFactory TRANSFORMER_FACTORY;

	static {

		System.setProperty("javax.xml.transform.TransformerFactory", TRANSFORMER_FACTORY_CLASS);
		TRANSFORMER_FACTORY = TransformerFactory.newInstance();
	}

	/**
	 * does not work, just for investigating and evaluation
	 *
	 * @throws URISyntaxException
	 * @throws TransformerException
	 */
	//@Test
	public void xsltTest() throws URISyntaxException, TransformerException {

		/*

		https://xerces.apache.org/xerces2-j/features.html
		https://xerces.apache.org/xerces2-j/properties.html

		 */

		final URL inputResourceURL = Resources.getResource("nonEscapedAmp.xml");
		final URL xslResourceURL = Resources.getResource("nonEscapedAmp.xsl");
		final Path inputResourcePath = Paths.get(inputResourceURL.toURI());
		final Path inputResourceParentFolder = inputResourcePath.getParent();
		final String outputResourceName = inputResourceParentFolder.toString() + "/nonEscapedAmpResult.xml";

		final Source xmlInput = new StreamSource(new File(inputResourceURL.getFile()));
		final Source xsl = new StreamSource(new File(xslResourceURL.getFile()));
		final Result xmlOutput = new StreamResult(new File(outputResourceName));

		final TransformerFactory transformerFactory = TRANSFORMER_FACTORY;
		transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
		//transformerFactory.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
		//transformerFactory.setFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", false);
		final Transformer transformer = transformerFactory.newTransformer(xsl);
		transformer.transform(xmlInput, xmlOutput);
	}
}
