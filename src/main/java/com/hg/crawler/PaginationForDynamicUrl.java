package com.hg.crawler;

import com.hg.crawler.xpath.Xpath;

public class PaginationForDynamicUrl extends PageFetcherAbstract {
	protected Boolean haveMaxProductCount = false;
	protected Boolean haveMaxPageNumber = false;
	protected Xpath productSizeXpath;
	protected Integer productCountPerPage;
	protected String paginationParam;
	protected Integer stepSize = 1;
	protected Integer beginNum = 1;
	protected Xpath paginationXpath;

	@Override
	public Object clone() throws CloneNotSupportedException {
		PaginationForDynamicUrl t = (PaginationForDynamicUrl) super.clone();
		return t;
	}

	@Override
	public String request() throws Exception {
		if (doc == null)
			doc = getDoc(this.url);
		if (haveMaxProductCount) {
			return dealWithMaxProductCount();
		} else {
			return dealWithMaxPageNumber();
		}
	}

	private String dealWithMaxProductCount() throws Exception {
		int count = 0;
		try {
			count = Integer.parseInt(getElementContent(doc, productSizeXpath));
		} catch (Exception e) {
			count = 0;
		}
		StringBuffer r = new StringBuffer();
		int pageNumber = count / productCountPerPage + 1;
		if (pageNumber <= 1) {
			return r.append(line).append(T).append(url).append("\n").toString();
		}
		for (int i = beginNum; i <= pageNumber; i = i + stepSize) {
			String paginationUrl = url;
			paginationUrl = url.replaceAll("&(?i)" + paginationParam + "=" + "[0-9]+", "");
			paginationUrl = url + "&" + paginationParam + "=" + i;
			r.append(line).append(T).append(paginationUrl).append("\n");
		}
		if (r.length() > 0) {
			r.deleteCharAt(r.length() - 1);
		}
		return r.toString();
	}

	private String dealWithMaxPageNumber() throws Exception {
		return "";
	}

	public Boolean getHaveMaxProductCount() {
		return haveMaxProductCount;
	}

	public void setHaveMaxProductCount(Boolean haveMaxProductCount) {
		this.haveMaxProductCount = haveMaxProductCount;
	}

	public Boolean getHaveMaxPageNumber() {
		return haveMaxPageNumber;
	}

	public void setHaveMaxPageNumber(Boolean haveMaxPageNumber) {
		this.haveMaxPageNumber = haveMaxPageNumber;
	}

	public Xpath getProductSizeXpath() {
		return productSizeXpath;
	}

	public void setProductSizeXpath(Xpath productSizeXpath) {
		this.productSizeXpath = productSizeXpath;
		if (productSizeXpath != null) {
			this.haveMaxProductCount = true;
		}
	}

	public Integer getProductCountPerPage() {
		return productCountPerPage;
	}

	public void setProductCountPerPage(Integer productCountPerPage) {
		this.productCountPerPage = productCountPerPage;
	}

	public Xpath getPaginationXpath() {
		return paginationXpath;
	}

	public void setPaginationXpath(Xpath paginationXpath) {
		this.paginationXpath = paginationXpath;
	}

	public Integer getStepSize() {
		return stepSize;
	}

	public void setStepSize(Integer stepSize) {
		this.stepSize = stepSize;
	}

	public String getPaginationParam() {
		return paginationParam;
	}

	public void setPaginationParam(String paginationParam) {
		this.paginationParam = paginationParam;
	}

	public Integer getBeginNum() {
		return beginNum;
	}

	public void setBeginNum(Integer beginNum) {
		this.beginNum = beginNum;
	}

}
