package com.hg.crawler.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.cyberneko.html.parsers.DOMParser;
import org.lobobrowser.html.UserAgentContext;
import org.lobobrowser.html.parser.HtmlParser;
import org.lobobrowser.html.test.SimpleUserAgentContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class TestCobra {
	private static final String TEST_URI = "http://www.newegg.com.cn/SubCategory/970.htm";

	public static void main(String[] args) throws Exception {
		// Disable most Cobra logging.
		Logger.getLogger("org.lobobrowser").setLevel(Level.WARNING);
		UserAgentContext uacontext = new SimpleUserAgentContext();
		// In this case we will use a standard XML document
		// as opposed to Cobra's HTML DOM implementation.
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		URL url = new URL(TEST_URI);
		InputStream in = url.openConnection().getInputStream();
		try {
			Reader reader = new InputStreamReader(in, "GBK");
			Document document = builder.newDocument();
			// Here is where we use Cobra's HTML parser.
			HtmlParser parser = new HtmlParser(uacontext, document);
			parser.parse(reader);
			// Now we use XPath to locate "a" elements that are
			// descendents of any "html" element.
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList nodeList = (NodeList) xpath.evaluate("/html/body/div[7]/div/div/div/div/div[5]/div[1]/dl/dd/div/p/a", document,
					XPathConstants.NODESET);
			int length = nodeList.getLength();
			for (int i = 0; i < length; i++) {
				Element element = (Element) nodeList.item(i);
				StringWriter writer = new StringWriter();
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.transform(new DOMSource(element), new StreamResult(writer));
				String xml = writer.toString();
				System.out.println(xml);
			}
		} finally {
			in.close();
		}
	}

}
