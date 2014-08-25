package com.hg.crawler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hg.crawler.xpath.ElementSkipException;

public class PageFetcherForMutipleOutput extends PageFetcherAbstract {
	List<PageFetcherAbstract> taskList;

	@Override
	public String request() throws Exception {
		String html = null;
		// those task for same page.
		boolean allElementSkiped = true;
		for (PageFetcherAbstract task : taskList) {
			PageFetcherAbstract fetcher = (PageFetcherAbstract) task.clone();
			try {
				fetcher.setUrl(url);
				fetcher.setLine(line);
				fetcher.setImageFolder(imageFolder);
				fetcher.setPageEncoding(pageEncoding);
				fetcher.setHtmlSrc(html);
				String c = fetcher.request();
				if (fetcher.moreUrlNeedToCrawl != null && !fetcher.moreUrlNeedToCrawl.isEmpty()) {
					if (this.moreUrlNeedToCrawl == null) {
						this.moreUrlNeedToCrawl = new ArrayList<String>();
					}
					this.moreUrlNeedToCrawl.addAll(fetcher.moreUrlNeedToCrawl);
				}
				if (c != null && c.trim().length() != 0) {
					BufferedWriter w = fetcher.getWriter();
					w.write(c + "\n");
					allElementSkiped = false;
				}
			} catch (ElementSkipException e) {
				logger.debug("All element skipped for url " + url + " for writter " + task.getWarpedBufferWriter().getOutputFilePath());
			} finally {
				html = fetcher.getHtmlSrc();
			}
		}
		if (allElementSkiped)
			throw new ElementSkipException();
		return line + T + "success";
	}

	public List<PageFetcherAbstract> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<PageFetcherAbstract> taskList) {
		this.taskList = taskList;
	}

	public void closeWriter() throws IOException {
		for (PageFetcherAbstract task : taskList) {
			task.getWriter().flush();
			task.getWriter().close();
		}
	}

}
