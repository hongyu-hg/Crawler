package com.hg.crawler.tool;

import hg.tool.file.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Util {
	static Pattern urlPattern = Pattern.compile("href=\"([^\"]+)\"");

	public static <T> List<T> toList(T[] ts) {
		List<T> r = new ArrayList<T>();
		for (T t : ts) {
			r.add(t);
		}
		return r;
	}

	public static int[] toIntArray(long[] ls) {
		int[] r = new int[ls.length];
		for (int i = 0; i < r.length; i++) {
			r[i] = ((Long) ls[i]).intValue();

		}
		return r;
	}

	public static String extractUrl(String html) throws TransformerException {
		if (html.startsWith("http"))
			return html;
		Matcher m;
		m = urlPattern.matcher(html);
		if (m.find()) {
			return m.group(1);
		} else {
			return null;
		}
	}

	public static String getTrace(Throwable t) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		StringBuffer buffer = stringWriter.getBuffer();
		return buffer.toString();
	}

	public static String domToHtml(Node o) throws TransformerException {
		if (o == null) {
			return "";
		}
		if (o instanceof Element || o instanceof Document) {
			StringWriter writer = new StringWriter();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(o), new StreamResult(writer));
			String xml = writer.toString();
			xml = nomalHtml(xml);
			return xml;
		} else {
			return o.getTextContent();
		}
	}

	public static String nomalHtml(String h) {
		h = h.replaceAll("(\t|\n|\f|\r)", "");
		h = h.replaceAll(" +", " ");
		h = h.replaceAll("> +<", "><");
		return h;
	}

	public static String onlyText(String h) {
		h = h.replaceAll("<[^<>]+>", "");
		return h.trim();
	}

	public static String getRealUrl(String currentUrl, String link) throws MalformedURLException {
		boolean fullPath = link.trim().startsWith("http://") || link.trim().startsWith("https://");
		if (!fullPath && link.startsWith("/")) {
			URL url = new URL(currentUrl);
			String prefix = "http://" + url.getHost();
			link = prefix + link;
		} else if (!fullPath && !link.startsWith("/")) {
			String prefix = currentUrl.substring(0, currentUrl.lastIndexOf("/") + 1);
			link = prefix + link;
		}
		return link;
	}

	static Pattern nP = Pattern.compile("[0-9]+");

	public static boolean hasNumber(String s) {
		Matcher m = nP.matcher(s);
		if (m.find()) {
			return true;
		} else {
			return false;
		}
	}

	public static int getColumnLength(File file) throws IOException {
		BufferedReader reader = FileUtil.getReader(file);
		String firstLine = reader.readLine();
		reader.close();
		int r = firstLine.split("\t").length;
		return r;
	}

	public static String nomalURL(String s) {
		s = s.replaceAll("#[A-Za-z0-9]+$", "");
		s = s.replaceAll("&amp;", "&");
		return s;
	}

	public static Map<String, Integer> getFileTitle(String path) throws IOException {
		return getFileTitle(new File(path));
	}

	public static Map<String, Integer> getFileTitle(File file) throws IOException {
		Map<String, Integer> r = new HashMap<String, Integer>();
		BufferedReader reader = FileUtil.getReader(file);
		String titleLine = reader.readLine();
		if (titleLine == null) {
			return null;
		}
		if (titleLine.startsWith("#Title:")) {
			titleLine = titleLine.substring(titleLine.length());
		}
		if (titleLine == null) {
			return r;
		}
		String[] seg = titleLine.split("\t");
		for (int i = 0; i < seg.length; i++) {
			r.put(seg[i].trim().toLowerCase(), i);
		}
		reader.close();
		return r;
	}

}
