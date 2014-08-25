package com.hg.crawler.file;

import hg.tool.file.FileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.URLEncoder;
import java.util.Map;

import com.hg.crawler.levels.TaskPool;
import com.hg.crawler.tool.Util;

public class FileProcessImplForHuihui extends TaskPool<Object> {

	@Override
	public void startRun() throws Exception {
		Map<String, Integer> titleMap = Util.getFileTitle(inputFilePath);
		BufferedReader reader = FileUtil.getReader(inputFilePath);
		BufferedWriter writer = FileUtil.getWriter(outputFilePath);
		String[] seg;
		String title = "ProductUrl\tOfferLink\tOfferPriceHistoryLink";
		writer.write(title);
		writer.newLine();
		reader.readLine();
		for (String line; (line = reader.readLine()) != null;) {
			try {
				seg = line.split("\t");
				String s = seg[titleMap.get(columnTitle.toLowerCase())];
				String productUrl = seg[titleMap.get("ProductUrl".toLowerCase())];
				String[] seg2 = s.split("bO_ob");
				for (String s2 : seg2) {
					String[] seg3 = s2.split("sO_os");
					String offerUrl = seg3[3];
					writer.write(productUrl + "\t" + offerUrl + "\t"
							+ "http://www.huihui.cn/search/ajax?call=getPriceHistory&type=cluster&shopNum=1&url0="
							+ URLEncoder.encode(offerUrl, "UTF-8"));
					writer.newLine();
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
