package com.hg.crawler.xpath;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hg.crawler.PageFetcherAbstract;

public class ElementFloat extends TextProcessing {
	protected Pattern regExp = Pattern.compile("([0-9\\.]+)");
	protected Float rate;
	protected DecimalFormat format = new DecimalFormat("##.00");

	@Override
	public String parse(PageFetcherAbstract fetcher, String element) {
		if (element == null) {
			return "";
		}
		Matcher m = regExp.matcher(element);
		if (!m.find()) {
			return "0";
		}
		Float value = Float.parseFloat(m.group(1));
		double r = value * rate;
		return format.format(r);
	}

	public Pattern getRegExp() {
		return regExp;
	}

	public void setRegExp(Pattern regExp) {
		this.regExp = regExp;
	}

	public Float getRate() {
		return rate;
	}

	public void setRate(Float rate) {
		this.rate = rate;
	}

	public DecimalFormat getFormat() {
		return format;
	}

	public void setFormat(DecimalFormat format) {
		this.format = format;
	}

}
