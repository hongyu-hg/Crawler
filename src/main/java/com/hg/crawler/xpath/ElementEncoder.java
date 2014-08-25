package com.hg.crawler.xpath;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.hg.crawler.PageFetcherAbstract;

public class ElementEncoder extends TextProcessing {
	protected String encode;

	@Override
	public String parse(PageFetcherAbstract fetcher, String element) throws UnsupportedEncodingException {
		String r;
		r = URLEncoder.encode(element, encode);
		return r;

	}

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}
}
