package com.hg.crawler.xpath;

import java.net.MalformedURLException;
import java.net.URL;

import com.hg.crawler.PageFetcherAbstract;

public class ElementLink extends TextProcessing {

	@Override
	public String parse(PageFetcherAbstract fetcher, String domToHtml) throws MalformedURLException {
		boolean fullPath = domToHtml.startsWith("http://");
		String urlString = fetcher.getUrl();
		URL url = null;
		url = new URL(urlString);
		if (!fullPath && domToHtml.startsWith("/")) {
			String prefix = "http://" + url.getHost();
			domToHtml = prefix + domToHtml;
		} else if (!fullPath && !domToHtml.startsWith("/")) {
			String prefix = urlString.substring(0, urlString.lastIndexOf("/") + 1);
			domToHtml = prefix + domToHtml;
		}
		domToHtml = domToHtml.replaceAll("&amp;", "&");
		domToHtml = domToHtml.replaceAll("&+", "&");
		domToHtml = domToHtml.replaceAll("#.*", "");
		if (domToHtml.endsWith("&")) {
			domToHtml = domToHtml.substring(0, domToHtml.length() - 1);
		}
		return domToHtml;
	}

}
