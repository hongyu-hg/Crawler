package com.hg.crawler;

import java.net.ConnectException;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hg.crawler.tool.Util;
import com.hg.crawler.xpath.TextProcessing;
import com.hg.crawler.xpath.Xpath;

public class PageFetcherForMutipleXpathMutipleLine extends PageFetcherAbstract {
	private List<Xpath> xpathList;
	private List<TextProcessing> preProcessing;

	public synchronized String request() throws Exception {
		try {
			return request(url);
		} catch (ConnectException e) {
			return request(url);
		}
	}

	public synchronized String request(String url) throws Exception {
		this.url = url;
		doc = getDoc(url, preProcessing);
		StringBuffer r = new StringBuffer();
		// get html by http client and build the document by neko
		for (Xpath xpath : xpathList) {
			NodeList nodeList = getNodeList(doc, xpath.getXpathString());
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				r.append(line).append(T);
				String e = xpath.postProcessElement(this, Util.domToHtml(node));
				r.append(e).append("\n");
			}
		}
		if (r.length() > 1) {
			r.deleteCharAt(r.length() - 1);
		}
		doc = null;
		return r.toString();
	}

	public List<Xpath> getXpathList() {
		return xpathList;
	}

	public void setXpathList(List<Xpath> xpathList) {
		this.xpathList = xpathList;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public List<TextProcessing> getPreProcessing() {
		return preProcessing;
	}

	public void setPreProcessing(List<TextProcessing> preProcessing) {
		this.preProcessing = preProcessing;
	}

}
