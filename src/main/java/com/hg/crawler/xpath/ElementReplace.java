package com.hg.crawler.xpath;

import com.hg.crawler.PageFetcherAbstract;

public class ElementReplace extends TextProcessing {
	protected String regExp;
	protected String replacement;

	@Override
	public String parse(PageFetcherAbstract fetcher, String element) {
		if (element == null) {
			return "";
		}
		return element.replaceAll(regExp, replacement).trim();
	}

	public String getRegExp() {
		return regExp;
	}

	public void setRegExp(String regExp) {
		this.regExp = regExp;
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}

}
