package com.hg.crawler;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hg.crawler.tool.Util;
import com.hg.crawler.xpath.Xpath;

public class PaginationForStaticUrl extends PageFetcherAbstract {
	protected Xpath xpath;
	Pattern np = Pattern.compile("^([0-9]+).*$");
	String paginationPattern;
	String prefix = null;
	String suffix = null;
	int maxPagination = 0;

	@Override
	public Object clone() throws CloneNotSupportedException {
		PaginationForStaticUrl t = (PaginationForStaticUrl) super.clone();
		t.maxPagination = 0;
		t.prefix = null;
		t.suffix = null;
		t.paginationPattern = null;
		return t;
	}

	@Override
	public String request() throws Exception {
		List<String> links = getPaginationLinksHTML();
		List<String> r = generatePaginationUrl(links, this.url);
		StringBuffer buffer = new StringBuffer();
		for (String s : r) {
			s = xpath.postProcessElement(this, s);
			buffer.append(line).append("\t").append(s).append("\n");
		}
		if (buffer.length() > 0) {
			buffer.deleteCharAt(buffer.length() - 1);
		}
		return buffer.toString();
	}

	private List<String> getPaginationLinksHTML() throws Exception {
		if (doc == null)
			doc = getDoc(url);
		NodeList ns = getNodeList(doc, xpath.getXpathString());
		List<String> pageLinks = new ArrayList<String>();
		for (int i = 0; i < ns.getLength(); i++) {
			Node item = ns.item(i);
			try {
				String domToHtml = Util.domToHtml(item);
				pageLinks.add(domToHtml);
			} catch (Exception e) {
			}
		}
		return pageLinks;
	}

	public boolean hasPagination(List<String> pageNationLinksHTML) {
		if (pageNationLinksHTML != null && pageNationLinksHTML.size() > 0) {
			return true;
		} else
			return false;
	}

	public List<String> generatePaginationUrl(List<String> domList, String url) throws TransformerException, MalformedURLException {
		List<Link> list = new ArrayList<Link>();
		for (String s : domList) {
			String link = Util.extractUrl(s);
			if (link.toLowerCase().trim().startsWith("javascript")) {
				continue;
			}
			link = link.replaceAll("#[a-zA-Z0-9]*$", "");
			link = Util.getRealUrl(url, link);
			String text = Util.onlyText(s);
			Link l = new Link();
			l.link = link;
			l.text = text;
			list.add(l);
		}
		List<String> r = generate(list, url);
		// for only has one or two page.
		if (r == null || r.size() == 0) {
			r = new ArrayList<String>();
			r.add(url);
			for (Link s : list) {
				if (!r.contains(s))
					r.add(s.link);
				break;
			}
		}
		return r;
	}

	private List<String> generate(List<Link> list, String url) {
		try {
			Link link1 = null;
			Link link2 = null;
			for (Link link : list) {
				link2 = findLikelyUrl(link, list);
				if (link2 != null) {
					link1 = link;
					break;
				}
			}
			if (link1 == null || link2 == null) {
				return null;
			}
			Pattern p = getPattern(link1, link2);
			getMaxPageNumber(list, p);
			List<String> r = generateUrl();
			return r;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<String> generateUrl() {
		List<String> r = new ArrayList<String>();
		for (int i = 1; i <= maxPagination; i++) {
			r.add(prefix + i + suffix);
		}
		return r;
	}

	/**
	 * @param pageHtmlLinks
	 *            Map<PaginationLink, PaginationText>
	 * @return
	 */
	private Link findLikelyUrl(Link cLink, List<Link> list) {
		for (Link link : list) {
			if (cLink.link.trim().equalsIgnoreCase(link.link))
				continue;
			try {
				int n1 = Integer.parseInt(findDifferent(cLink.link, link.link));
				int n2 = Integer.parseInt(findDifferent(link.link, cLink.link));
				if (Math.abs(n1 - n2) < 3) {
					return link;
				}
				if (n1 % Integer.parseInt(cLink.text) == 0 && n2 % Integer.parseInt(link.text) == 0)
					return link;
			} catch (Exception e) {
			}
		}
		return null;
	}

	private Pattern getPattern(Link l1, Link l2) {
		String prefix = null;
		String suffix = null;
		int pos = StringUtils.indexOfDifference(l1.link, l2.link);
		prefix = l1.link.substring(0, pos);
		suffix = l1.link.substring(pos);
		Matcher m = np.matcher(suffix);
		if (m.find()) {
			suffix = suffix.substring(m.group(1).length());
			this.prefix = prefix;
			this.suffix = suffix;
			return Pattern.compile("^" + prefix.replace("?", "\\?") + "([0-9]+)" + suffix.replace("?", "\\?") + "$");
		}
		return null;
	}

	private int getMaxPageNumber(List<Link> links, Pattern p) {
		int max = 0;
		for (Link link : links) {
			Matcher m = p.matcher(link.link);
			if (m.find()) {
				int n = Integer.parseInt(m.group(1));
				if (n > max) {
					max = n;
				}
			}
		}
		this.maxPagination = max;
		return max;
	}

	private String findDifferent(String s1, String s2) {
		int pos1 = StringUtils.indexOfDifference(s1, s2);
		int pos2 = pos1;
		for (int i = pos1; i <= s1.length(); i++) {
			pos2 = i;
			if (s2.endsWith(s1.substring(i))) {
				break;
			}
		}
		return s1.substring(pos1, pos2);
	}

	public Xpath getXpath() {
		return xpath;
	}

	public void setXpath(Xpath xpath) {
		this.xpath = xpath;
	}

	private class Link {
		String link;
		String text;

		@Override
		public String toString() {
			return text + "\t" + link;
		}
	}

	public static void main(String[] args) throws CloneNotSupportedException {
		PaginationForStaticUrl url = new PaginationForStaticUrl();
		url.maxPagination = 190;
		PaginationForStaticUrl url2 = (PaginationForStaticUrl) url.clone();
		url2.maxPagination = 200;
		System.out.println(url.maxPagination);

	}
}
