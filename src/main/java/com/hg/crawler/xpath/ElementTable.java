package com.hg.crawler.xpath;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hg.crawler.PageFetcherAbstract;
import com.hg.crawler.tool.Util;

public class ElementTable extends TextProcessing {
	static String boxTitle2BoxBody = "---";
	static String key2Value = "::";
	static String box2Box = "====";
	static String attribute2Attribute = "##";
	private String attrPattern;
	private Pattern pattern;

	public String normalTable(String domToHtml) {
		if (domToHtml.indexOf("<table") != -1) {
			domToHtml = domToHtml.replaceAll("</th>", "</td>");
			domToHtml = tableFormatForHtmlTable(domToHtml, "</tr>", "</td>");
		} else if (attrPattern != null) {
			if (pattern == null && attrPattern != null) {
				pattern = Pattern.compile(attrPattern);
			}
			domToHtml = tableFormatForPattern(domToHtml, pattern);
		}
		return domToHtml;
	}

	private String tableFormatForHtmlTable(String domToHtml, String lineSeparator, String kvSeparator) {
		domToHtml = domToHtml.replaceAll("(<[a-zA-Z]+) [^>]+", "$1").toLowerCase();
		String[] seg = domToHtml.split(lineSeparator);
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < seg.length; i++) {
			String boxTitle = null;
			String key = null;
			String value = null;
			String s = seg[i];
			if (s.indexOf(kvSeparator) == -1) {
				continue;
			}
			if (s.indexOf(kvSeparator) != s.lastIndexOf(kvSeparator)) {
				String[] s2 = s.split(kvSeparator);
				key = s2[0];
				value = s2[1];
			} else {
				boxTitle = s;
			}
			if (boxTitle != null && buffer.length() == 0) {
				buffer.append(boxTitle).append(boxTitle2BoxBody);
			} else if (boxTitle != null) {
				buffer.append(box2Box).append(boxTitle).append(boxTitle2BoxBody);
			} else if (key != null && value != null) {
				buffer.append(key).append(key2Value).append(value).append(attribute2Attribute);
			}
		}
		domToHtml = Util.onlyText(buffer.toString());
		domToHtml = domToHtml.replaceAll("###", "##");
		domToHtml = domToHtml.replaceAll(attribute2Attribute + box2Box, box2Box);
		if (domToHtml.endsWith("##"))
			domToHtml = domToHtml.substring(0, domToHtml.length() - 2);
		return domToHtml;
	}

	private String tableFormatForPattern(String domToHtml, Pattern p) {
		domToHtml = domToHtml.replaceAll("(<[a-zA-Z]+) [^>]+", "$1").toLowerCase();
		Matcher m = p.matcher(domToHtml);
		StringBuffer buffer = new StringBuffer();
		while (m.find()) {
			String boxTitle = null;
			String key = null;
			String value = null;
			key = Util.onlyText(m.group(1)).trim();
			value = Util.onlyText(m.group(2)).trim();
			if (value.trim().length() == 0) {
				boxTitle = key;
			}
			if (boxTitle != null && buffer.length() == 0) {
				buffer.append(boxTitle).append(boxTitle2BoxBody);
			} else if (boxTitle != null) {
				buffer.append(box2Box).append(boxTitle).append(boxTitle2BoxBody);
			} else if (key != null && value != null) {
				buffer.append(key).append(key2Value).append(value).append(attribute2Attribute);
			}
		}
		domToHtml = Util.onlyText(buffer.toString());
		domToHtml = domToHtml.replaceAll("###", "##");
		domToHtml = domToHtml.replaceAll(attribute2Attribute + box2Box, box2Box);
		if (domToHtml.endsWith("##"))
			domToHtml = domToHtml.substring(0, domToHtml.length() - 2);
		return domToHtml;
	}

	public String removeAttributes(String html) {
		html = html.replaceAll("<([a-zA-Z]+) [^>]+", "");
		return html;
	}

	@Override
	public String parse(PageFetcherAbstract fetcher, String element) {
		if (element == null) {
			return "";
		}
		return normalTable(element);
	}

	public String getAttrPattern() {
		return attrPattern;
	}

	public void setAttrPattern(String attrPattern) {
		this.attrPattern = attrPattern;
	}

}
