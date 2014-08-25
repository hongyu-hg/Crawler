package com.hg.crawler;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hg.crawler.xpath.ElementImg;

public class PageFetcherForImg extends PageFetcherAbstract {
	ElementImg img = new ElementImg();
	Pattern pattern = Pattern.compile("url=([^&]+)");

	@Override
	public String request() throws Exception {
		return dumpHuiuiMerchantImg();
	}

	@SuppressWarnings("unused")
	private String dumpHuiuiProductImg() throws UnsupportedEncodingException, MalformedURLException, IOException {
		Matcher m = pattern.matcher(url);
		m.find();
		String imgUrl = URLDecoder.decode(m.group(1), "UTF8");
		String localPath = getImageFolder() + "/" + new URL(imgUrl).getHost() + "/" + imgUrl.replaceAll("http://[^/]+/", "");
		localPath = localPath.replaceAll("\\?.*$", "");
		img.dumpImg(new URL(url), new File(localPath));
		return line + T + localPath;
	}

	Pattern p2 = Pattern.compile("([0-9]{15})");

	private String dumpHuiuiMerchantImg() throws UnsupportedEncodingException, MalformedURLException, IOException {
		Matcher m = p2.matcher(url);
		m.find();
		String localPath = getImageFolder() + "/merchant/" + m.group(1) + ".png";
		localPath = localPath.replaceAll("\\?.*$", "");
		img.dumpImg(new URL(url), new File(localPath));
		return line + T + localPath;
	}
}
