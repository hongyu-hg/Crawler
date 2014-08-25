package com.hg.crawler;

import java.net.URLEncoder;

public class SubmitUrlForAmazonPrice extends PageFetcherAbstract {
	@Override
	public String request() throws Exception {
		String uploadUrl = "http://hg-c-01.appspot.com/gae_crawler?m=Amazon" + "&id=" + getID() + "&u=" + URLEncoder.encode(url, "UTF-8");
		String s = getHtmlString(uploadUrl).trim();
		return line + "\t" + s;
	}

	private static int currID = 0;

	private synchronized int getID() {
		return currID++;
	}

}
