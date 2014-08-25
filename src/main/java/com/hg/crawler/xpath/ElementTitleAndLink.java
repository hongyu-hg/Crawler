package com.hg.crawler.xpath;

import java.net.MalformedURLException;

import javax.xml.transform.TransformerException;

import com.hg.crawler.PageFetcherAbstract;
import com.hg.crawler.tool.Util;

public class ElementTitleAndLink extends TextProcessing {

	@Override
	public String parse(PageFetcherAbstract fetcher, String domToHtml) throws TransformerException, MalformedURLException {
		String text = Util.onlyText(domToHtml);
		String u;
		u = Util.extractUrl(domToHtml);
		if (u == null || text == null) {
			return "";
		}
		String link = Util.getRealUrl(fetcher.getUrl(), u);
		link = Util.nomalURL(link);
		String value = text + "\t" + link;
		return value;
	}

}
