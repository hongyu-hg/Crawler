package com.hg.crawler.file;

import hg.tool.file.FileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import com.hg.crawler.levels.TaskPool;
import com.hg.crawler.tool.Util;
import com.hg.crawler.xpath.TextProcessing;

public class FileProcessImpl extends TaskPool<Object> {
	List<TextProcessing> textProcessList;

	@Override
	public void startRun() throws Exception {
		Map<String, Integer> titleMap = Util.getFileTitle(inputFilePath);
		BufferedReader reader = FileUtil.getReader(inputFilePath);
		BufferedWriter writer = FileUtil.getWriter(outputFilePath);
		String[] seg;
		String title = reader.readLine() + "\t" + newClumnNames;
		writer.write(title);
		writer.newLine();
		for (String line; (line = reader.readLine()) != null;) {
			try {
				seg = line.split("\t");
				String s = seg[titleMap.get(columnTitle.toLowerCase())];
				for (TextProcessing tp : textProcessList) {
					s = tp.parse(null, s);
				}
				writer.write(line + "\t" + s);
				writer.newLine();
			} catch (Exception e) {
				System.out.println(line + "\t" + e.getMessage());
			}
		}
		reader.close();
		writer.flush();
	}

	public List<TextProcessing> getTextProcessList() {
		return textProcessList;
	}

	public void setTextProcessList(List<TextProcessing> textProcessList) {
		this.textProcessList = textProcessList;
	}

}
