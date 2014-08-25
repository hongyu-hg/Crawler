package com.hg.crawler.test;

import hg.tool.file.FileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hg.crawler.tool.Util;

public class Level4Parser {
	static String boxTitle2BoxBody = "---";
	static String key2Value = "::";
	static String box2Box = "====";
	static String attribute2Attribute = "##";
	static Pattern titlePattern = Pattern.compile("<h1>(.*)</h1>");
	static Pattern attrPattern = Pattern.compile("<ul><li>([^<]+)</li><li>([^<]+)</li></ul>");

	public static void main(String[] args) throws IOException {
		String fileName = "C:/Users/Lihongyu/Desktop/level_4_after_filter_0.txt";
		Map<String, Integer> columns = Util.getFileTitle(fileName);
		Integer priceColumn = columns.get("merchantprice");
		Integer destColumn = columns.get("MerchantLink".toLowerCase());
		Integer descColumn = columns.get("ProductDetail".toLowerCase());
		BufferedReader reader = FileUtil.getReader(fileName);
		BufferedWriter writer = FileUtil.getWriter("C:/Users/Lihongyu/Desktop/level_4_finally_1.txt");
		writer.write(reader.readLine());
		writer.newLine();
		String[] seg;
		for (String line; (line = reader.readLine()) != null;) {
			seg = line.split("\t");
			String priceS = seg[priceColumn];
			priceS = priceS.replaceAll("¥", "");
			try {
				Float.parseFloat(priceS);
			} catch (Exception e) {
				continue;
			}
			String destUrl = seg[destColumn];
			String t = destUrl.replace("/redirect.jhtml?link=", "");
			t = URLDecoder.decode(t, "UTF-8");
			line = line.replace(destUrl, t);
			StringBuffer sb = new StringBuffer();
			String desc = seg[descColumn];
			desc = desc.replaceAll("(<[a-zA-Z0-9]+) [^>]+", "$1").toLowerCase();
			String[] boxSeg = desc.split("<h1");
			if (boxSeg.length == 1) {
				sb.append(Util.onlyText(desc));
			} else {
				boolean boxSecond = false;
				for (String box : boxSeg) {
					box = "<h1" + box;
					if (box.indexOf("</h1>") == -1) {
						continue;
					}
					if (boxSecond) {
						sb.append(box2Box);
					}
					Matcher m = titlePattern.matcher(box);
					m.find();
					String title = m.group(1).trim();
					sb.append(Util.onlyText(title).trim());
					sb.append(boxTitle2BoxBody);
					Matcher m2 = attrPattern.matcher(box);
					boolean second = false;
					while (m2.find()) {
						if (second) {
							sb.append(attribute2Attribute);
						}
						String key = m2.group(1);
						String value = m2.group(2);
						sb.append(Util.onlyText(key).trim()).append(key2Value).append(Util.onlyText(value).trim());
						second = true;
					}
					boxSecond = true;
				}
			}
			writer.write(line + "\t" + sb.toString());
			writer.newLine();
		}
		reader.close();
		writer.flush();
		writer.close();
	}
}
