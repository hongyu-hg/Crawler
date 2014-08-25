package com.hg.crawler.xpath;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hg.crawler.PageFetcherAbstract;
import com.hg.crawler.tool.Util;

public class ElementForHuihuiDetailPage extends TextProcessing {

	Pattern p = Pattern.compile("purl=([^&]+)");

	@Override
	public String parse(PageFetcherAbstract fetcher, String domToHtml) throws XPathExpressionException, SAXException, IOException,
			TransformerException {
		InputSource source = new InputSource(new StringReader(domToHtml));
		StringBuffer buffer = new StringBuffer();
		Document createDom = fetcher.createDom(source);
		NodeList nodeList = ((PageFetcherAbstract) fetcher).getNodeList(createDom, "//li[@class=\"tab-wrap clearfix\"]");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String siteName = Util.onlyText(Util.domToHtml(((PageFetcherAbstract) fetcher).getNode(node, "div/div[2]/a/img/@alt")));
			String siteImg = Util.onlyText(Util.domToHtml(((PageFetcherAbstract) fetcher).getNode(node, "div/div[2]/a/img/@src")));
			String siteAttribute = Util.domToHtml(((PageFetcherAbstract) fetcher).getNode(node, "div/div[3]"));
			siteAttribute = siteAttribute.replaceAll("<i>", "");
			siteAttribute = Util.onlyText(siteAttribute.replaceAll("</i>", "//"));
			if (siteAttribute.endsWith("//")) {
				siteAttribute = siteAttribute.substring(0, siteAttribute.length() - 2);
			}
			NodeList node2List = ((PageFetcherAbstract) fetcher).getNodeList(node, "div/div[@class=\"tab-bd-contract\"]/div");
			for (int j = 0; j < node2List.getLength(); j++) {
				Node node2 = node2List.item(j);
				String titleFromSite = Util.onlyText(Util.domToHtml(((PageFetcherAbstract) fetcher).getNode(node2, "div/div[1]/h3/a")));
				String offerLink = Util.onlyText(Util.domToHtml(((PageFetcherAbstract) fetcher).getNode(node2, "div/div/h3/a/@href")));
				if (offerLink.indexOf("http://www.huihui.cn/proxy?purl=") != -1) {
					Matcher m = p.matcher(offerLink);
					if (m.find()) {
						offerLink = URLDecoder.decode(m.group(1), "UTF-8");
					} else {
						throw new RuntimeException("can't find the offer link!!");
					}
				}
				String saleOut = Util.onlyText(Util.domToHtml(((PageFetcherAbstract) fetcher).getNode(node2,
						"div/div[3]/div/span[@class=\"color-gray\"]")));
				String offerPrice = Util.onlyText(Util.domToHtml(((PageFetcherAbstract) fetcher).getNode(node2, "div/div[3]/div/span/em")));
				buffer.append(siteName + "sO_os" + siteImg + "sO_os" + titleFromSite + "sO_os" + offerLink + "sO_os" + offerPrice + "sO_os"
						+ siteAttribute + "sO_os" + ((saleOut.trim().length() != 0) ? "y" : "n"));
				buffer.append("bO_ob");
			}

		}
		if (buffer.length() > 10) {
			buffer.delete(buffer.length() - 5, buffer.length());
		}
		return buffer.toString();
	}
}
