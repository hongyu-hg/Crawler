package com.hg.crawler.xpath;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hg.crawler.PageFetcherAbstract;

public class ElementExtract extends TextProcessing {
	protected String regExp;
	protected String defaultValue = "";

	@Override
	public String parse(PageFetcherAbstract fetcher, String domToHtml) {
		if (domToHtml == null || domToHtml.length() == 0) {
			return "";
		}
		Pattern p = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(domToHtml);
		if (m.find() && m.groupCount() > 0) {
			return m.group(1);
		} else {
			return defaultValue;
		}
	}

	public String getRegExp() {
		return regExp;
	}

	public void setRegExp(String regExp) {
		this.regExp = regExp;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}
