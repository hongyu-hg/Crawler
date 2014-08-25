package com.hg.crawler.test;

import hg.tool.file.FileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CategoryCount {
	public static void main(String[] args) throws IOException {
		BufferedReader reader = FileUtil.getReader("C:/Users/Lihongyu/Desktop/level_3_after_filter.txt");
		BufferedWriter writer = FileUtil.getWriter("C:/Users/Lihongyu/Desktop/level_3_after_filter_1.txt");
		String[] seg;
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		Integer dc = 0;
		for (String line; (line = reader.readLine()) != null;) {
			seg = line.split("\t");
			String category = seg[0];
			if (!countMap.containsKey(category)) {
				countMap.put(category, 0);
			}
			Integer currentCount = countMap.get(category);
			countMap.put(category, currentCount + 1);
			if (currentCount > 5000) {
				continue;
			} else {
				dc++;
				writer.write(line);
				writer.newLine();
			}
		}

		for (Entry e : countMap.entrySet()) {
			System.out.println(e.getKey() + "\t" + e.getValue());
		}
		System.out.println(countMap.size());
		System.out.println(dc);
		reader.close();
		writer.close();
	}
}
