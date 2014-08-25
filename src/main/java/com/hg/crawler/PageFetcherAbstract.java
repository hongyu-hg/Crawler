package com.hg.crawler;

import hg.tool.file.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.apache.xerces.parsers.DOMParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hg.crawler.levels.RetryException;
import com.hg.crawler.tool.ProxyContainer;
import com.hg.crawler.tool.Util;
import com.hg.crawler.xpath.TextProcessing;
import com.hg.crawler.xpath.Xpath;

/**
 * @author wali
 * 
 */
public abstract class PageFetcherAbstract implements Cloneable {
	public Log logger = LogFactory.getLog(this.getClass());

	protected boolean useTaskWriter;
	protected WarpedBufferWriter warpedBufferWriter;
	@Autowired
	public static final String T = "\t";
	protected String url;
	protected Document doc;
	protected int connectTimeOut = 12000;
	protected int readTimeOut = 60000;
	protected String imageFolder;
	protected String pageEncoding;
	protected String line;
	protected boolean htmlPersistence = false;
	protected List<TextProcessing> preProcessing;
	protected List<String> moreUrlNeedToCrawl;
	protected String useragent = "Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)";
	// protected String useragent =
	// "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/16.0.2";
	// protected String useragent =
	// "Baidu spidering engine - used by diff. IPs";
	// protected String httpProxy = "221.7.11.11:80";
	protected ProxyContainer proxyContainer;
	protected String htmlSrc = "";
	protected boolean logUrlTrace = false;
	protected boolean traceHtmlStringWhenError = false;
	protected int retryCount = 1;
	protected NomalCondition normalCondition;
	protected boolean doNotRetry = false;

	public boolean isLogUrlTrace() {
		return logUrlTrace;
	}

	public void setLogUrlTrace(boolean logUrlTrace) {
		this.logUrlTrace = logUrlTrace;
	}

	public List<TextProcessing> getPreProcessing() {
		return preProcessing;
	}

	public void setPreProcessing(List<TextProcessing> preProcessing) {
		this.preProcessing = preProcessing;
	}

	abstract public String request() throws Exception;

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public Document createDom(InputSource is) throws SAXException, IOException {
		// settings on HTMLConfiguration
		org.apache.xerces.xni.parser.XMLParserConfiguration config = new org.cyberneko.html.HTMLConfiguration();
		config.setFeature("http://cyberneko.org/html/features/augmentations", true);
		config.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
		config.setProperty("http://cyberneko.org/html/properties/names/attrs", "lower");
		config.setProperty("http://cyberneko.org/html/properties/default-encoding", "UTF-8");
		config.setFeature("http://cyberneko.org/html/features/report-errors", false);
		config.setFeature("http://cyberneko.org/html/features/balance-tags/ignore-outside-content", true);
		config.setFeature("http://cyberneko.org/html/features/augmentations", true);
		config.setFeature("http://xml.org/sax/features/namespaces", false);
		// settings on DOMParser
		DOMParser parser = new DOMParser(config);
		parser.parse(is);
		return parser.getDocument();
	}

	public NodeList queryXPath(Node node, String xpathS) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		// XPath Query for showing all nodes value
		XPathExpression expr = xpath.compile(xpathS);
		Object result = expr.evaluate(node, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		return nodes;
	}

	public static void main(String[] args) throws Exception {
		PageFetcherCommon common = new PageFetcherCommon();
		common.setPageEncoding("UTF8");
		String url2 = "http://www.amazon.cn/gp/product/B008HXD86A/ref=s9_simh_gw_p147_d0_i2?pf_rd_m=A1AJ19PSB66TGU&pf_rd_s=center-2&pf_rd_r=1SPZ9S18B9WM1V2F5733&pf_rd_t=101&pf_rd_p=58223232&pf_rd_i=899254051";
		System.out.println(common.getHtmlString(url2));
	}

	public String getHtmlString(String url) throws Exception {
		String httpProxy = null;
		String html = "";
		try {
			long t = System.currentTimeMillis();
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, connectTimeOut);
			HttpConnectionParams.setSoTimeout(httpParams, readTimeOut);
			HttpClient client = new DefaultHttpClient(httpParams);
			if (proxyContainer != null) {
				httpProxy = proxyContainer.getAProxy();
				if (httpProxy != null && httpProxy.trim().length() != 0) {
					HttpHost proxy = new HttpHost(httpProxy.split(":")[0], Integer.valueOf(httpProxy.split(":")[1]));
					client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
				}
			}
			HttpProtocolParams.setUserAgent(client.getParams(), useragent);
			HttpGet get = new HttpGet(url);
			// get.setHeader("Accept",
			// "application/json, text/javascript, */*; q=0.01");
			// get.setHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");
			// // get.setHeader("Accept-Encoding", "gzip,deflate,sdch");
			// get.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			// get.setHeader("Connection", "keep-alive");
			// get.setHeader(
			// "Cookie",
			// "fvf_gouwu=http://www.huihui.cn/; fvt_gouwu=2012-12-11; ARMANI_USER_ID=1814793504@180.153.253.134; OUTFOX_SEARCH_USER_ID=1822422038@180.153.253.134; CNZZDATA3995408=cnzz_eid=40642154-1355364615-&ntime=1355810606&cnzz_a=6&retime=1355814395728&sin=none&ltime=1355814395728&rtime=1; P_INFO=hongyu.hg@gmail.com|1355918588|1|huihui|00&99|shh&1354881647&huihui#shh&null#10#0#0|&0; NTES_PASSPORT=t9CBYpnJEmK9tUlXHnBF1Zz.mPmxnd3xdH0PszfI60f8QQkrgfbc2SgE_7b2CZt87TrQfNAnptKSL_4AfypBnWyVXEcNWiOqH8Bc31h_9sjCb; gw_actid=1220296851; sf_gouwu=null; notice=%7B%22isLock%22%3Atrue%7D; ARMANIDCRC=1; ARMANIDCR_1=%7B%22d%22%3A%5B%7B%22c%22%3A%22%E5%A5%B3%E5%A3%AB%E7%BE%BD%E7%BB%92%E6%9C%8D%22%2C%22n%22%3A2%7D%2C%7B%22c%22%3A%22%E5%8A%A0%E6%B9%BF%E5%99%A8%22%2C%22n%22%3A1%7D%2C%7B%22c%22%3A%22%E8%BF%9E%E8%A1%A3%E8%A3%99%22%2C%22n%22%3A1%7D%2C%7B%22c%22%3A%22%E5%A5%B3%E5%A3%AB%E5%86%AC%E9%9D%B4%22%2C%22n%22%3A1%7D%2C%7B%22c%22%3A%22%E7%94%B7%E5%A3%AB%E7%BE%BD%E7%BB%92%E6%9C%8D%22%2C%22n%22%3A1%7D%2C%7B%22c%22%3A%22%E7%94%B7%E4%BF%9D%E6%9A%96%E5%86%85%E8%A1%A3%22%2C%22n%22%3A1%7D%2C%7B%22c%22%3A%22%E6%B4%97%E8%A1%A3%E6%9C%BA%22%2C%22n%22%3A1%7D%2C%7B%22c%22%3A%22%E6%8C%82%E7%83%AB%E6%9C%BA%22%2C%22n%22%3A4%7D%5D%7D; vts_gouwu=252; __utma=36751673.1059201487.1355214890.1356156751.1356157178.33; __utmb=36751673.15.9.1356160093095; __utmc=36751673; __utmz=36751673.1355214890.1.1.utmcsr=dog.youdao.com|utmccn=(referral)|utmcmd=referral|utmcct=/redirect; NTES_LOGINED=true");
			// get.setHeader("Host", "www.huihui.cn");
			// get.setHeader("Referer",
			// "http://www.huihui.cn/search?q=%E6%B5%B7%E4%BF%A1+XQB60-2132+%E6%B4%97%E8%A1%A3%E6%9C%BA&keyfrom=detail.top&ue=utf-8");
			// get.setHeader("User-Agent",
			// "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11");
			// get.setHeader("X-Requested-With", "XMLHttpRequest");
			HttpResponse r = client.execute(get);
			if (pageEncoding != null) {
				html = new String(EntityUtils.toByteArray(r.getEntity()), pageEncoding);
			} else {
				html = EntityUtils.toString(r.getEntity());
			}
			if (html != null && html.trim().length() != 0 && html.indexOf("</html>") != -1) {
				html = html.replaceAll("</html>", "");
				html = html.replaceAll("</body>", "");
				html = html + "</body></html>";
			}
			System.out.println((httpProxy != null ? (proxyContainer.getValiableProxySize() + "/" + proxyContainer.getProxySize() + "/"
					+ proxyContainer.getProxyPos() + " -- " + httpProxy + "  --->  ") : "")
					+ "get html spend: " + (System.currentTimeMillis() - t) + " --> " + url);
			if (html.indexOf("来自您ip的请求异常频繁") != -1 || html.indexOf("Please try again later") != -1) {
				System.out.println(httpProxy + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				if (proxyContainer != null) {
					proxyContainer.removeBlockProxy(httpProxy);
					System.out.println("huihui blocked the ip, remove the proxy" + httpProxy + " and change a new one.");
					html = getHtmlString(url);
				}
			}
			htmlSrc = html;
			if (proxyContainer != null && httpProxy != null && httpProxy.trim().length() != 0 && html.length() > 0) {
				proxyContainer.thisProxyIsValiable(httpProxy);
			}
			if (normalCondition != null) {
				if (normalCondition.check(this, html)) {
					this.doNotRetry = true;
				}
			}
		} catch (RetryException e) {
			if (proxyContainer != null && proxyContainer.getProxySize() != 0 && httpProxy != null) {
				proxyContainer.removeBadProxy(httpProxy);
				System.out.println("get Html Failed, remove the proxy" + httpProxy + " and change a new one." + e.getMessage() + " " + url);
			}
			throw e;
		} catch (Exception e) {
			if (proxyContainer != null && proxyContainer.getProxySize() != 0 && httpProxy != null) {
				proxyContainer.removeBadProxy(httpProxy);
				System.out.println("get Html Failed, remove the proxy" + httpProxy + " and change a new one." + e.getMessage() + " " + url);
				html = getHtmlString(url);
			} else if (e instanceof RetryException) {
				System.out.println("get Html Failed, try it again." + e.getMessage() + " " + url);
				html = getHtmlString(url);
			} else if (e instanceof ConnectException) {
				System.out.println("get Html Failed, try it again." + e.getMessage() + " " + url);
				html = getHtmlString(url);
			} else
				throw e;
		}
		return html;
	}

	public NodeList getNodeList(String xpath) throws Exception {
		if (doc == null)
			doc = getDoc(this.url);
		return getNodeList(doc, xpath);
	}

	public Document getDoc(String url) throws Exception {
		return getDoc(url, null);
	}

	public Document getDoc(String url, List<TextProcessing> preProcessing) throws Exception {
		this.url = url;
		String html = this.htmlSrc;
		if (html == null || html.trim().length() == 0) {
			html = getHtmlString(url);
		}
		if (preProcessing != null && preProcessing.size() != 0) {
			for (TextProcessing pp : preProcessing) {
				html = pp.parse(null, html);
			}
		}
		InputSource source = new InputSource(new StringReader(html));
		Document createDom = createDom(source);
		if (htmlPersistence) {
			File f = new File(URLEncoder.encode(url, "UTF-8"));
			logger.info("write html to: " + f.getAbsolutePath());
			BufferedWriter w = FileUtil.getWriter(f);
			w.write(Util.domToHtml(createDom));
			w.close();
		}
		return createDom;
	}

	public NodeList getNodeList(Node node, String xpath) throws XPathExpressionException, SAXException, IOException {
		NodeList nodeList = queryXPath(node, xpath);
		return nodeList;
	}

	public Node getNode(Node node, String xpath) throws XPathExpressionException, SAXException, IOException {
		NodeList nodeList = queryXPath(node, xpath);
		if (node == null || nodeList.getLength() == 0) {
			return null;
		} else
			return nodeList.item(0);
	}

	public String getElementContent(Node node, Xpath xpath) throws Exception {
		try {
			NodeList nodeList = getNodeList(node, xpath.getXpathString());
			String domToHtml = Util.domToHtml(nodeList.item(0));
			domToHtml = xpath.postProcessElement(this, domToHtml);
			return domToHtml;
		} catch (Exception e) {
			if (xpath.isNecessary()) {
				throw e;
			}
			logger.error(Util.getTrace(e));
			e.printStackTrace();
			return "";
		}
	}

	public ProxyContainer getProxyContainer() {
		return proxyContainer;
	}

	public void setProxyContainer(ProxyContainer proxyContainer) {
		this.proxyContainer = proxyContainer;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getConnectTimeOut() {
		return connectTimeOut;
	}

	public void setConnectTimeOut(int connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}

	public int getReadTimeOut() {
		return readTimeOut;
	}

	public void setReadTimeOut(int readTimeOut) {
		this.readTimeOut = readTimeOut;
	}

	public String getImageFolder() {
		return imageFolder;
	}

	public void setImageFolder(String imageFolder) {
		this.imageFolder = imageFolder;
	}

	public String getPageEncoding() {
		return pageEncoding;
	}

	public void setPageEncoding(String pageEncoding) {
		this.pageEncoding = pageEncoding;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public void setHtmlPersistence(boolean htmlPersistence) {
		this.htmlPersistence = htmlPersistence;
	}

	public List<String> getMoreUrlNeedToCrawl() {
		return moreUrlNeedToCrawl;
	}

	public void setMoreUrlNeedToCrawl(List<String> moreUrlNeedToCrawl) {
		this.moreUrlNeedToCrawl = moreUrlNeedToCrawl;
	}

	public String getUseragent() {
		return useragent;
	}

	public void setUseragent(String useragent) {
		this.useragent = useragent;
	}

	public String getHtmlSrc() {
		return htmlSrc;
	}

	public void setHtmlSrc(String htmlSrc) {
		this.htmlSrc = htmlSrc;
	}

	public boolean isTraceHtmlStringWhenError() {
		return traceHtmlStringWhenError;
	}

	public void setTraceHtmlStringWhenError(boolean traceHtmlStringWhenError) {
		this.traceHtmlStringWhenError = traceHtmlStringWhenError;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void countRetry() {
		this.retryCount = retryCount + 1;
	}

	public boolean isDoNotRetry() {
		return doNotRetry;
	}

	public void setDoNotRetry(boolean doNotRetry) {
		this.doNotRetry = doNotRetry;
	}

	public NomalCondition getNormalCondition() {
		return normalCondition;
	}

	public void setNormalCondition(NomalCondition normalCondition) {
		this.normalCondition = normalCondition;
	}

	public BufferedWriter getWriter() {
		return warpedBufferWriter.getWriter();
	}

	public boolean isUseTaskWriter() {
		return useTaskWriter;
	}

	public void setUseTaskWriter(boolean useTaskWriter) {
		this.useTaskWriter = useTaskWriter;
	}

	public void setWarpedBufferWriter(WarpedBufferWriter warpedBufferWriter) {
		this.warpedBufferWriter = warpedBufferWriter;
	}

	public WarpedBufferWriter getWarpedBufferWriter() {
		return warpedBufferWriter;
	}

}
