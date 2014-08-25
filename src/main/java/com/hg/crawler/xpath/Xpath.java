package com.hg.crawler.xpath;

import java.util.ArrayList;
import java.util.List;

import com.hg.crawler.PageFetcherAbstract;
import com.hg.crawler.levels.ElementNotFoundException;

public class Xpath {
	private String xpathString;
	private List<TextProcessing> postProcessing = new ArrayList<TextProcessing>();
	private List<ElementExclude> filterList = new ArrayList<ElementExclude>();
	private String xpathtTitle;
	private boolean necessary = false;

	public Xpath() {
		// dose nothing.
	}

	public Xpath(String xpath) {
		xpathString = xpath;
	}

	public List<TextProcessing> getPostProcessing() {
		return postProcessing;
	}

	public void setPostProcessing(List<TextProcessing> postProcessing) {
		this.postProcessing = postProcessing;
	}

	public List<ElementExclude> getFilterList() {
		return filterList;
	}

	public void setFilterList(List<ElementExclude> filterList) {
		this.filterList = filterList;
	}

	public String getXpathString() {
		return xpathString;
	}

	public void setXpathString(String xpathString) {
		this.xpathString = xpathString;
	}

	public String getXpathtTitle() {
		return xpathtTitle;
	}

	public void setXpathtTitle(String xpathtTitle) {
		this.xpathtTitle = xpathtTitle;
	}

	public boolean isNecessary() {
		return necessary;
	}

	public void setNecessary(boolean necessary) {
		this.necessary = necessary;
	}

	public String postProcessElement(PageFetcherAbstract fetcher, String domToHtml) throws Exception {
		List<TextProcessing> pList = getPostProcessing();
		for (TextProcessing p : pList) {
			domToHtml = p.parse(fetcher, domToHtml);
		}
		if (isNecessary() && domToHtml.trim().length() == 0) {
			throw new ElementNotFoundException("----------------------the " + getXpathString()
					+ " not found!!, It's necessray---------------------");
		}
		return domToHtml;
	}
}
