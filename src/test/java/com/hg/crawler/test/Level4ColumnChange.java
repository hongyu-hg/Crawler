package com.hg.crawler.test;

import hg.tool.file.FileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

import com.hg.crawler.tool.Util;

public class Level4ColumnChange {
	public static void main(String[] args) throws IOException {
		BufferedReader reader = FileUtil.getReader("C:/Users/Lihongyu/Desktop/level_4_finally_1.txt");
		BufferedWriter writer = FileUtil.getWriter("C:/Users/Lihongyu/Desktop/level_4_finally_2.txt");
		String[] seg;
		// CategoryName Brand ImgPath ProductDetail MerchantName
		// MerchantPrductName MerchantPrice RealLink Title
		Map<String, Integer> columns = Util.getFileTitle("C:/Users/Lihongyu/Desktop/level_4_finally_1.txt");
		Integer c1 = columns.get("CategoryName".toLowerCase());
		Integer c2 = columns.get("Brand".toLowerCase());
		Integer c3 = columns.get("ImgPath".toLowerCase());
		Integer c4 = columns.get("RealDesc".toLowerCase());
		Integer c5 = columns.get("MerchantName".toLowerCase());
		Integer c6 = columns.get("MerchantTitle".toLowerCase());
		Integer c7 = columns.get("MerchantPrice".toLowerCase());
		Integer c8 = columns.get("MerchantLink".toLowerCase());
		Integer c9 = columns.get("Title".toLowerCase());
		reader.readLine();
		int count = 0;
		for (String line; (line = reader.readLine()) != null;) {
			seg = line.split("\t");
			String s1 = seg[c1];
			String s2 = seg[c2];
			String s3 = seg[c3];
			String s4;
			try {
				s4 = Util.onlyText(seg[c4]);
				// if (s4.trim().length() == 0) {
				// continue;
				// }
			} catch (Exception e) {
				count++;
				continue;
			}
			String s5 = seg[c5];
			String s6 = seg[c6];
			String s7 = seg[c7];
			String s8 = seg[c8];
			String s9 = seg[c9];
			writer.write(s1);
			writer.write("\t");
			writer.write(s2);
			writer.write("\t");
			writer.write(s3);
			writer.write("\t");
			writer.write(s4);
			writer.write("\t");
			writer.write(s5);
			writer.write("\t");
			writer.write(s6);
			writer.write("\t");
			writer.write(s7);
			writer.write("\t");
			writer.write(s8);
			writer.write("\t");
			writer.write(s9);
			writer.newLine();
		}
		reader.close();
		writer.flush();
		writer.close();
		System.out.println(count);
	}
}
