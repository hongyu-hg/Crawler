package com.hg.crawler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageFetcherFor360Price extends PageFetcherAbstract {

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	@Override
	public String request() throws Exception {
		StringBuffer buffer = new StringBuffer(line).append(T);
		String s = super.getHtmlString(url);
		Pattern p = Pattern.compile("\"amount\":([0-9\\.]+)");
		Matcher m = p.matcher(s);
		if (m.find()) {
			s = m.group(1) + T + dateFormat.format(new Date());
		} else {
			s = "-1" + T + "yes";
		}
		buffer.append(s);
		return buffer.toString();
	}

}
