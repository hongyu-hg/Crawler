package com.hg.sitemap;

import hg.tool.file.FileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenerateSEOSiteMap {
	private File currentFile;
	private int currentFileSize;
	private BufferedWriter currentWriter;
	private int FILE_SIZE_LIMIT = 1000000000;
	private static String URL_PREFIX = "http://www.ushang.net/";
	private List<File> fileList = new ArrayList<File>();

	public void generate() throws IOException {
		// generate Category Url
		BufferedReader reader = FileUtil.getReader("/home/warren/jayadata/huihui/categoryid.txt");
		for (String line; (line = reader.readLine()) != null;) {
			String catID = line.trim();
			if (catID == null || catID.trim().length() == 0) {
				continue;
			}
			writeUrlToFile(URL_PREFIX + "search?cid=" + catID);
		}
		reader.close();

		// generate Brand Url
		reader = FileUtil.getReader("/home/warren/jayadata/huihui/brandid.txt");
		for (String line; (line = reader.readLine()) != null;) {
			String brandID = line.trim();
			if (brandID == null || brandID.trim().length() == 0) {
				continue;
			}
			writeUrlToFile(URL_PREFIX + "search?bid=" + brandID);
		}
		reader.close();
	}

	private BufferedWriter getOutStream() throws IOException {
		if (currentFile == null) {
			currentFile = new File("ushang_" + (fileList.size() + 1) + ".xml");
			currentWriter = FileUtil.getWriter(currentFile);
			writeFileStart();
		}
		if (currentFileSize > FILE_SIZE_LIMIT) {
			fileList.add(currentFile);
			writeFileEnd();
			currentWriter.flush();
			currentWriter.close();
			currentWriter = null;
			currentFile = new File("ushang_" + (fileList.size() + 1) + ".xml");
			currentWriter = FileUtil.getWriter(currentFile);
			writeFileStart();
		}
		return currentWriter;
	}

	private void writeFileEnd() throws IOException {
		currentWriter.write("</urlset>");
		currentWriter.flush();
	}

	private void writeFileStart() throws IOException {
		currentWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		currentWriter.newLine();
		currentWriter.write("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
		currentWriter.newLine();
	}

	private void writeUrlToFile(String url) throws IOException {
		BufferedWriter out = getOutStream();
		StringBuffer buffer = new StringBuffer();
		buffer.append("\t<url>\n");
		buffer.append("\t\t" + "<loc>" + url + "</loc>\n");
		buffer.append("\t</url>\n");
		currentFileSize = currentFileSize + buffer.length();
		out.write(buffer.toString());
	}

	public static void main(String[] args) throws IOException {
		GenerateSEOSiteMap seoSiteMap = new GenerateSEOSiteMap();
		seoSiteMap.currentFile = new File("sitemap.xml");
		seoSiteMap.currentWriter = FileUtil.getWriter(seoSiteMap.currentFile);
		seoSiteMap.writeFileStart();
		seoSiteMap.generate();
		seoSiteMap.writeFileEnd();
	}
}
