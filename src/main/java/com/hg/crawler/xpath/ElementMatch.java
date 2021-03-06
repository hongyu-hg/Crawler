package com.hg.crawler.xpath;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hg.crawler.PageFetcherAbstract;

public class ElementMatch extends TextProcessing {
	protected String regExp;

	@Override
	public String parse(PageFetcherAbstract fetcher, String element) throws ElementSkipException {
		Pattern p = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE & Pattern.DOTALL);
		Matcher m = p.matcher(element);
		if (m.find()) {
			return "yes";
		} else {
			return "no";
		}
	}

	public String getRegExp() {
		return regExp;
	}

	public void setRegExp(String regExp) {
		this.regExp = regExp;
	}

}
