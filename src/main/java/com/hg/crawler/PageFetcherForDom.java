package com.hg.crawler;

import org.w3c.dom.NodeList;

import com.hg.crawler.tool.Util;
import com.hg.crawler.xpath.Xpath;

public class PageFetcherForDom extends PageFetcherAbstract {
	protected Xpath xpath;

	@Override
	public Object clone() throws CloneNotSupportedException {
		PageFetcherForDom t = (PageFetcherForDom) super.clone();
		return t;
	}

	public Xpath getXpath() {
		return xpath;
	}

	public void setXpath(Xpath xpath) {
		this.xpath = xpath;
	}

	@Override
	public String request() throws Exception {
		if (doc == null)
			doc = getDoc(url);
		NodeList ns = getNodeList(doc, xpath.getXpathString());
		// if no result, return empty list;
		if (ns == null || ns.getLength() == 0) {
			return "\t";
		}
		// deal with data
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < ns.getLength(); i++) {
			String domToHtml = Util.domToHtml(ns.item(i));
			domToHtml.replaceAll("\\s+", " ");
			String value = domToHtml;
			buffer.append(line).append(T).append(value).append("\n");
		}
		if (buffer.length() > 0) {
			buffer.deleteCharAt(buffer.length() - 1);
		}
		return buffer.toString();
	}
}
