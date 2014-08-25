package com.hg.crawler.levels;

import java.util.List;


public class TaskCenter {
	List<TaskPool<?>> taskList;
	protected Integer threadSize;
	protected Integer batchSize = 200;
	protected String imgFolder;
	protected Boolean testing = false;
	protected Integer testingSize = 3;
	protected Boolean useNewFile = false;

	public void doTheJobList() throws Exception {
		for (TaskPool<?> task : taskList) {
			if (threadSize != null && task.getThreadSize() == null) {
				task.setThreadSize(threadSize);
			}
			if (batchSize != null && task.getBatchSize() == null) {
				task.setBatchSize(batchSize);
			}
			if (imgFolder != null && task.getImgFolder() == null) {
				task.setImgFolder(imgFolder);
			}
			if (testing != null && task.getTesting() == null) {
				task.setTesting(testing);
			}
			if (testingSize != null && task.getTestingSize() == null) {
				task.setTestingSize(testingSize);
			}
			if (useNewFile != null && task.getUseNewFile() == null) {
				task.setUseNewFile(useNewFile);
			}
			task.startRun();
			System.out.println("finished the " + task.getInputFilePath() + " and the result file: " + task.getOutputFilePath());
		}
	}

	public List<TaskPool<?>> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<TaskPool<?>> taskList) {
		this.taskList = taskList;
	}

	public Integer getThreadSize() {
		return threadSize;
	}

	public void setThreadSize(Integer threadSize) {
		this.threadSize = threadSize;
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

}
