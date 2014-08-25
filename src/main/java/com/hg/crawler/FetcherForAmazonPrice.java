package com.hg.crawler;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FetcherForAmazonPrice extends PageFetcherAbstract {

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	Pattern p = Pattern.compile("class=\"priceLarge\">￥\\s+([0-9.,]+)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	@Override
	public String request() throws Exception {
		StringBuffer buffer = new StringBuffer(line).append(T);
		String s = super.getHtmlString(url);
		Matcher m = p.matcher(s);
		m.find();
		buffer.append(m.group(1).replaceAll(",", ""));
		htmlSrc = null;
		return buffer.toString();
	}

}
