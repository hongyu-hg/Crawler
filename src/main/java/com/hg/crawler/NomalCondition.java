package com.hg.crawler;

import java.util.List;

import com.hg.crawler.levels.RetryException;
import com.hg.crawler.xpath.TextProcessing;

public class NomalCondition {
	List<TextProcessing> conditions;

	public boolean check(PageFetcherAbstract fetcher, String html) throws RetryException {
		for (TextProcessing e : conditions) {
			try {
				e.parse(fetcher, html);
			} catch (Exception e2) {
				throw new RetryException("Retried By Rule " + e);
			}
		}
		return true;
	}

	public List<TextProcessing> getConditions() {
		return conditions;
	}

	public void setConditions(List<TextProcessing> conditions) {
		this.conditions = conditions;
	}

}
