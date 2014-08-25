package com.hg.crawler.file;

import hg.tool.file.FileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hg.crawler.levels.TaskPool;
import com.hg.crawler.tool.Util;

public class FileProcessImplForHuihuiTemp extends TaskPool<Object> {

	@Override
	public void startRun() throws Exception {
		Map<String, Integer> titleMap = Util.getFileTitle(inputFilePath);
		BufferedReader reader = FileUtil.getReader(inputFilePath);
		BufferedWriter writer = FileUtil.getWriter(outputFilePath);
		String[] seg;
		String title = "MerchantLink";
		writer.write(title);
		writer.newLine();
		reader.readLine();
		Set<String> t = new HashSet<String>();
		for (String line; (line = reader.readLine()) != null;) {
			try {
				seg = line.split("\t");
				String s = seg[titleMap.get(columnTitle.toLowerCase())];
				String[] seg2 = s.split("bO_ob");
				for (String s2 : seg2) {
					String[] seg3 = s2.split("sO_os");
					String offerUrl = seg3[1];
					if (!t.contains(offerUrl)) {
						writer.write(offerUrl);
						writer.newLine();
						t.add(offerUrl);
					}
				}
			} catch (Exception e) {
				System.out.println(line + "\t" + e.getMessage());
			}
		}
		reader.close();
		writer.flush();
		writer.close();
	}
}
