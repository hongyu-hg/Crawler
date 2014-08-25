package com.hg.crawler.test;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hg.crawler.tool.Util;

public class XPathDemo {
	protected static int connectTimeOut = 120000;
	protected static int readTimeOut = 60000;
	protected static String pageEncoding;

	public static void main(String[] args) throws Exception {
		String url = "http://web3.sasa.com/SasaWeb/sch/product/searchProduct.jspa?brandId=6&type=1";
		String s = getHtmlString(url);
		InputSource is = new InputSource(new StringReader(s));
		Document doc = createDom(is);
		NodeList ns1 = queryXPath(
				doc,
				"/html/body/table/tbody/tr[6]/td[2]/table/tbody/tr/td[2]/table/tbody/tr/td[3]/table[5]/tbody/tr/td[2]/table/tbody/tr[2]/td[2]/table/tbody/tr[1]/td[2]");
		System.out.println(ns1.getLength());
		for (int i = 0; i < ns1.getLength(); i++) {
			System.out.println(Util.domToHtml(ns1.item(i)));
		}
	}

	public static NodeList getNodeList(Node node, String xpathS) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		// XPath Query for showing all nodes value
		XPathExpression expr = xpath.compile(xpathS);
		Object result = expr.evaluate(node, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		return nodes;
	}

	public static Document createDom(InputSource is) throws SAXException, IOException {
		// settings on HTMLConfiguration
		org.apache.xerces.xni.parser.XMLParserConfiguration config = new org.cyberneko.html.HTMLConfiguration();
		config.setFeature("http://cyberneko.org/html/features/augmentations", true);
		config.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
		config.setProperty("http://cyberneko.org/html/properties/names/attrs", "lower");
		config.setProperty("http://cyberneko.org/html/properties/default-encoding", "UTF-8");
		config.setFeature("http://cyberneko.org/html/features/report-errors", false);
		config.setFeature("http://cyberneko.org/html/features/balance-tags/ignore-outside-content", true);
		config.setFeature("http://cyberneko.org/html/features/augmentations", true);
		config.setFeature("http://xml.org/sax/features/namespaces", false);
		// settings on DOMParser
		DOMParser parser = new DOMParser(config);
		parser.parse(is);
		return parser.getDocument();
	}

	public static NodeList queryXPath(Node node, String xpathS) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		// XPath Query for showing all nodes value
		XPathExpression expr = xpath.compile(xpathS);
		Object result = expr.evaluate(node, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		return nodes;
	}

	public static String getHtmlString(String url) throws IOException, ClientProtocolException {
		long t = System.currentTimeMillis();
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, connectTimeOut);
		HttpConnectionParams.setSoTimeout(httpParams, readTimeOut);
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpGet get = new HttpGet(url);
		HttpResponse r = client.execute(get);
		String html;
		if (pageEncoding != null) {
			html = new String(EntityUtils.toByteArray(r.getEntity()), pageEncoding);
		} else {
			html = EntityUtils.toString(r.getEntity());
		}
		// html = html.replaceFirst("<html[^>]*>", "<html>");
		html = html.replaceAll("</html>", "");
		html = html.replaceAll("</body>", "");
		html = html + "</body></html>";
		System.out.println("get html spend: " + (System.currentTimeMillis() - t) + " --> " + url);
		return html;
	}
}