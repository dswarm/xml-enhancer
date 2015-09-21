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
import org.junit.Test;

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

	@Test
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
