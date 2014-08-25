package com.hg.crawler;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hg.crawler.tool.Util;
import com.hg.crawler.xpath.ElementSkipException;
import com.hg.crawler.xpath.Xpath;

public class PageFetcherCommon extends PageFetcherAbstract {
	private String urlColumn;
	private List<Xpath> singleXpathList;
	private Xpath parentXpath;
	private List<Xpath> childXpathList;
	private Xpath paginationXpath;

	public synchronized String request() throws Exception {
		try {
			return request(url);
		} catch (ConnectException e) {
			return request(url);
		}
	}

	public synchronized String request(String url) throws Exception {
		String r = "";
		this.url = url;
		// get html by http client and build the document by neko
		doc = getDoc(url, preProcessing);
		// handle pagination
		if (paginationXpath != null) {
			this.moreUrlNeedToCrawl = dealWithPagination(doc, paginationXpath);
		}
		StringBuffer buffer = new StringBuffer(line).append(T);
		if (logUrlTrace) {
			buffer.append(url).append(T);
		}
		// handle single element xpath.
		if (singleXpathList != null)
			for (Xpath xpath : singleXpathList) {
				String elementContent = getElementContent(doc, xpath);
				buffer.append(elementContent).append(T);
			}
		if (parentXpath == null) {
			if (buffer.length() > 0)
				buffer.deleteCharAt(buffer.length() - 1);
			r = buffer.toString();
		} else {
			// handle multiple element xpath.
			r = dealWithMultipleElement(doc, buffer.toString()).toString();
		}
		doc = null;
		return r;
	}

	private List<String> dealWithPagination(Document doc, Xpath paginationXpath2) throws Exception {
		List<String> list = new ArrayList<String>();
		NodeList nodeList = getNodeList(doc, paginationXpath2.getXpathString());
		if (nodeList == null || nodeList.getLength() == 0) {
			return null;
		}
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String u = paginationXpath2.postProcessElement(this, Util.domToHtml(node));
			list.add(u);
		}
		return list;
	}

	private StringBuffer dealWithMultipleElement(Document doc, String prefix) throws Exception {
		StringBuffer buffer = new StringBuffer();
		NodeList nodeList = getNodeList(doc, parentXpath.getXpathString());
		boolean theDocIsNormal = false;
		for (int i = 0; i < nodeList.getLength(); i++) {
			try {
				StringBuffer buffer2 = new StringBuffer(prefix);
				Node node = nodeList.item(i);
				if (childXpathList == null || childXpathList.size() == 0) {
					try {
						buffer2.append(parentXpath.postProcessElement(this, Util.domToHtml(node))).append("\n");
					} catch (Exception e) {
						buffer2.append("\n");
						logger.error(Util.getTrace(e));
					}
					buffer.append(buffer2.toString());
					continue;
				}
				boolean skip = false;
				for (Xpath xpath : childXpathList) {
					try {
						String content = getElementContent(node, xpath);
						buffer2.append(content).append(T);
					} catch (ElementSkipException e3) {
						skip = true;
						theDocIsNormal = true;
						break;
					} catch (Exception e) {
						throw e;
					}
				}
				if (skip == true) {
					continue;
				}
				buffer2.deleteCharAt(buffer2.length() - 1);
				buffer2.append("\n");
				buffer.append(buffer2);
			} catch (RuntimeException e) {
				logger.warn("skip this child." + Util.getTrace(e));
			}
		}
		if (theDocIsNormal && buffer.length() == 0) {
			throw new ElementSkipException();
		}
		if (buffer.length() > 0)
			buffer.deleteCharAt(buffer.length() - 1);
		return buffer;
	}

	public List<Xpath> getSingleXpathList() {
		return singleXpathList;
	}

	public void setSingleXpathList(List<Xpath> singleXpathList) {
		this.singleXpathList = singleXpathList;
	}

	public Xpath getParentXpath() {
		return parentXpath;
	}

	public void setParentXpath(Xpath parentXpath) {
		this.parentXpath = parentXpath;
	}

	public List<Xpath> getChildXpathList() {
		return childXpathList;
	}

	public void setChildXpathList(List<Xpath> childXpathList) {
		this.childXpathList = childXpathList;
	}

	public String getUrlColumn() {
		return urlColumn;
	}

	public void setUrlColumn(String urlColumn) {
		this.urlColumn = urlColumn;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public Xpath getPaginationXpath() {
		return paginationXpath;
	}

	public void setPaginationXpath(Xpath paginationXpath) {
		this.paginationXpath = paginationXpath;
	}

	public static void main(String[] args) throws Exception {
		PageFetcherCommon fetcher = new PageFetcherCommon();
		fetcher.setUrl("xxx");
		PageFetcherAbstract f2 = (PageFetcherAbstract) fetcher.clone();
		System.out.println(f2.getUrl());

	}
}
