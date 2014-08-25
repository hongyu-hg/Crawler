package com.hg.crawler.levels;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hg.crawler.tool.Util;
import com.hg.crawler.xpath.TextProcessing;

public abstract class TaskPool<E> {
	protected Log logger = LogFactory.getLog(this.getClass());
	protected String inputFilePath;
	protected String outputFilePath;
	protected String columnTitle;
	protected ExecutorService executor = Executors.newFixedThreadPool(10);
	protected List<Future<E>> fList = new ArrayList<Future<E>>();
	protected int fCount = 0;
	protected Integer batchSize;
	protected Integer threadSize;
	protected String imgFolder;
	protected Boolean testing;
	protected Integer testingSize;
	protected String newClumnNames;
	protected String pageEncoding;
	protected Boolean useNewFile;
	protected List<TextProcessing> urlFilterList;

	public abstract void startRun() throws Exception;

	protected void submitTask(Callable<E> callable) {
		Future<E> f = executor.submit(callable);
		fList.add(f);
	}

	protected List<E> waitingOngoingTaskLowThanALimit(int batchSize) {
		if (fList.size() < batchSize) {
			return new ArrayList<E>();
		}
		System.out.println("-----------------Waiting This Batch Finish-------------------");
		List<E> list = new ArrayList<E>();
		for (Future<E> f : fList) {
			try {
				list.add(f.get());
			} catch (Exception e) {
				logger.error(Util.getTrace(e));
				e.printStackTrace();
			}
		}
		System.out.println("-----------------one batch finished, " + batchSize + "-------------------");
		fList.clear();
		return list;
	}

	public Integer getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	public String getImgFolder() {
		return imgFolder;
	}

	public void setImgFolder(String imgFolder) {
		this.imgFolder = imgFolder;
	}

	public Boolean getTesting() {
		return testing;
	}

	public void setTesting(Boolean testing) {
		this.testing = testing;
	}

	public String getNewClumnNames() {
		return newClumnNames;
	}

	public void setNewClumnNames(String newClumnNames) {
		this.newClumnNames = newClumnNames.replaceAll("\\\\t", "\t");
	}

	public Integer getThreadSize() {
		return threadSize;
	}

	public void setThreadSize(Integer threadSize) {
		this.threadSize = threadSize;
		executor = Executors.newFixedThreadPool(threadSize);
	}

	public String getPageEncoding() {
		return pageEncoding;
	}

	public void setPageEncoding(String pageEncoding) {
		this.pageEncoding = pageEncoding;
	}

	public String getInputFilePath() {
		return inputFilePath;
	}

	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}

	public String getOutputFilePath() {
		return outputFilePath;
	}

	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	public String getColumnTitle() {
		return columnTitle;
	}

	public void setColumnTitle(String columnTitle) {
		this.columnTitle = columnTitle;
	}

	public Integer getTestingSize() {
		return testingSize;
	}

	public void setTestingSize(Integer testingSize) {
		this.testingSize = testingSize;
	}

	public Boolean getUseNewFile() {
		return useNewFile;
	}

	public void setUseNewFile(Boolean useNewFile) {
		this.useNewFile = useNewFile;
	}

	public List<TextProcessing> getUrlFilterList() {
		return urlFilterList;
	}

	public void setUrlFilterList(List<TextProcessing> urlFilterList) {
		this.urlFilterList = urlFilterList;
	}

}
