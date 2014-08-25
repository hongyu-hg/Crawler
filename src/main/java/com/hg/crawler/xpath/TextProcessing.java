package com.hg.crawler.xpath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hg.crawler.PageFetcherAbstract;

public abstract class TextProcessing {
	Log logger = LogFactory.getLog(this.getClass());

	public abstract String parse(PageFetcherAbstract fetcher, String element) throws Exception;
}
