package com.hg.crawler.test;

import hg.tool.file.FileUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.hg.crawler.PageFetcherAbstract;
import com.hg.crawler.tool.Util;

public class XPathSample extends PageFetcherAbstract {
	public static void main(String[] args) throws Exception {
		XPathSample t = new XPathSample();
		t.request();
	}

	@Override
	public String request() throws Exception {
		String s = getHtmlString("http://www.amazon.cn/Elizabeth-Arden%E4%BC%8A%E4%B8%BD%E8%8E%8E%E7%99%BD%E9%9B%85%E9%A1%BF%E7%AC%AC%E4%BA%94%E5%A4%A7%E9%81%93%E5%96%B7%E5%BC%8F%E6%B7%A1%E9%A6%99%E6%B0%B430ml/dp/B0010MG6VE/ref=sr_1_1?s=beauty&ie=UTF8&qid=1315465416&sr=1-1");
		Document d = createDom(new InputSource(new StringReader(s)));
		NodeList l = queryXPath(d, "//h2[text()=\"查找其它相似商品\"]");
		System.out.println(Util.domToHtml(l.item(0)));
		return "";
	}

	public void writeString(String s) throws IOException {
		BufferedWriter writer = FileUtil.getWriter("t.html");
		writer.write(s);
		writer.close();
	}
}
