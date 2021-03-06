package com.hg.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.NodeList;

import com.hg.crawler.tool.Util;
import com.hg.crawler.xpath.Xpath;

public class PageFetcherForTitleAndUrl extends PageFetcherAbstract {
	protected Xpath xpath = new Xpath("//a");
	protected String regExp = ".*";
	protected Pattern filter;

	@Override
	public Object clone() throws CloneNotSupportedException {
		PageFetcherForTitleAndUrl t = (PageFetcherForTitleAndUrl) super.clone();
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
		List<String> urlList = new ArrayList<String>();
		if (filter == null)
			filter = Pattern.compile(regExp);
		if (doc == null)
			doc = getDoc(url);
		NodeList ns = getNodeList(doc, xpath.getXpathString());
		// if no result, return empty list;
		if (ns == null || ns.getLength() == 0) {
			return "\t";
		}
		// deal with data
		for (int i = 0; i < ns.getLength(); i++) {
			String domToHtml = Util.domToHtml(ns.item(i));
			String text = Util.onlyText(domToHtml);
			String u = Util.extractUrl(domToHtml);
			if (u == null || text == null) {
				continue;
			}
			String link = Util.getRealUrl(url, u);
			link = Util.nomalURL(link);
			String value = text + T + link;
			value = xpath.postProcessElement(this, value);
			if (filter != null) {
				Matcher m = filter.matcher(link);
				if (m.find() && !urlList.contains(value)) {
					urlList.add(value);
				}
			} else {
				if (!urlList.contains(value))
					urlList.add(value);
			}
		}
		StringBuffer buffer = new StringBuffer();
		for (String s : urlList) {
			buffer.append(line).append(T).append(s).append("\n");
		}
		if (buffer.length() > 0) {
			buffer.deleteCharAt(buffer.length() - 1);
		}
		return buffer.toString();
	}

	public String getRegExp() {
		return regExp;
	}

	public void setRegExp(String regExp) {
		this.regExp = regExp;
	}

	public Pattern getFilter() {
		return filter;
	}

	public void setFilter(Pattern filter) {
		this.filter = filter;
	}

}
