package com.hg.crawler.xpath;

import com.hg.crawler.PageFetcherAbstract;
import com.hg.crawler.tool.Util;

public class ElementText extends TextProcessing {

	@Override
	public String parse(PageFetcherAbstract fetcher, String element) {
		if (element == null) {
			return "";
		}
		return Util.onlyText(element).trim();
	}

}
