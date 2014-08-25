package com.hg.crawler.test;

import hg.tool.file.FileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URLDecoder;

public class GetMerchant {
	public static void main(String[] args) throws IOException {
		BufferedReader reader = FileUtil.getReader("C:/Users/Lihongyu/Desktop/level_2.txt");
		BufferedWriter writer = FileUtil.getWriter("C:/Users/Lihongyu/Desktop/level_2_with_url.txt");
		String[] seg;
		writer.write(reader.readLine());
		writer.newLine();
		for (String line; (line = reader.readLine()) != null;) {
			seg = line.split("\t");
			String url1;
			try {
				url1 = URLDecoder.decode(seg[7].replace("/redirect.html?link=", ""), "UTF-8");
			} catch (Exception e) {
				System.out.println(line);
				continue;
			}
			String url2;
			try {
				url2 = seg[8];
			} catch (Exception e) {
				url2 = "";
			}
			String url;
			if (!url1.equals("")) {
				url = url1;
			} else {
				url = url2;
			}
			writer.write(line + "\t" + url);
			writer.newLine();
		}
		reader.close();
		writer.flush();
		writer.close();
	}
}
