package com.hg.crawler;

import hg.tool.file.FileUtil;

import java.io.BufferedWriter;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

public class WarpedBufferWriter {
	private BufferedWriter writer;
	@Autowired
	private String fileTitle;
	private String outputFilePath;

	public BufferedWriter getWriter() {
		return writer;
	}

	public void setOutputFilePath(String outputFilePath) throws IOException {
		this.outputFilePath = outputFilePath;
		this.writer = FileUtil.getWriter(outputFilePath);
		writer.write(fileTitle.replaceAll("\\\\t", "\t") + "\n");
	}

	public String getFileTitle() {
		return fileTitle;
	}

	public void setFileTitle(String fileTitle) {
		this.fileTitle = fileTitle;
	}

	public String getOutputFilePath() {
		return outputFilePath;
	}

}
