package com.hg.crawler.file;

import hg.tool.file.FileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import com.hg.crawler.levels.TaskPool;
import com.hg.crawler.tool.Util;
import com.hg.crawler.xpath.TextProcessing;

public class FileProcessImplForColumnSelector extends TaskPool<Object> {
	List<TextProcessing> textProcessList;
	List<String> columnList;

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
				StringBuffer newLine = new StringBuffer();
				for (String cName : columnList) {
					String s = seg[titleMap.get(cName)];
					newLine.append(s).append("\t");
				}
				writer.write(newLine.toString());
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
