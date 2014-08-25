package com.hg.crawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageFetcherForYihaodianList extends PageFetcherAbstract {

	Pattern titlePattern = Pattern.compile("title=\"([^\"]+)\"");
	private Pattern productURLPattern = Pattern.compile("(http://www.yihaodian.com/product/[^\"]+)");
	private Pattern productPrice = Pattern.compile("<strong id=[^>]+>¥([0-9\\.]+)");
	private Pattern smallImgPattern = Pattern.compile("<img original=\"([^\"]+)\"");

	@Override
	public String request() throws Exception {
		String s = super.getHtmlString(url);
		s = s.replaceAll("\\\\\"", "\"");
		s = s.replaceAll("\\\\r\\\\n", "");
		s = s.replaceAll("\\\\t", "");
		s = s.substring(s.indexOf("<div class=\"itemSearchResult"));
		String[] seg = s.split("<li class=\"producteg\"");
		Matcher m;
		StringBuffer buffer = new StringBuffer();
		for (String s2 : seg) {
			String title;
			String productURL;
			String price;
			String smallImgUrl;
			m = titlePattern.matcher(s2);
			if (m.find()) {
				title = m.group(1);
			} else
				continue;
			m = productURLPattern.matcher(s2);
			if (m.find()) {
				productURL = m.group(1);
			} else
				continue;
			m = productPrice.matcher(s2);
			if (m.find()) {
				price = m.group(1);
			} else
				continue;
			m = smallImgPattern.matcher(s2);
			if (m.find()) {
				smallImgUrl = m.group(1);
			} else
				continue;
			String bigImgUrl = smallImgUrl.replace("160x160", "380x380");
			String productDetailUrl = productURL.replaceAll("^.*/product/", "");
			productDetailUrl = productDetailUrl.replaceAll("[^0-9].*$", "");
			productDetailUrl = "http://www.yihaodian.com/product/ajaxProdDescTabView.do?productID=" + productDetailUrl;
			buffer.append(line).append(T).append(price).append(T).append(productURL).append(T).append(title).append(T).append(smallImgUrl)
					.append(T).append(bigImgUrl).append(T).append(productDetailUrl).append("\n");
		}
		if (buffer.length() > 1) {
			buffer.deleteCharAt(buffer.length() - 1);
		}
		return buffer.toString();
	}

}
