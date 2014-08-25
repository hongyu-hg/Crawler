package com.hg.crawler.levels;

import hg.tool.file.FileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.hg.bloom.Bloom;
import com.hg.crawler.PageFetcherAbstract;
import com.hg.crawler.PageFetcherForMutipleOutput;
import com.hg.crawler.tool.Util;
import com.hg.crawler.xpath.ElementSkipException;
import com.hg.crawler.xpath.TextProcessing;

public class TaskPoolImpl<E> extends TaskPool<PageFetcherAbstract> {
	protected BufferedWriter writer;
	protected BufferedWriter errorWriter;
	protected Bloom submittedURL = new Bloom(10000000);
	protected PageFetcherAbstract task;
	protected int retryLimit = 10;

	private Callable<PageFetcherAbstract> generateCallable(final PageFetcherAbstract fetcher) {
		if (fetcher == null) {
			return null;
		}
		if (submittedURL.contains(fetcher.getUrl().toLowerCase().trim())) {
			return null;
		} else {
			submittedURL.add(fetcher.getUrl().trim().toLowerCase());
		}
		Callable<PageFetcherAbstract> task = new Callable<PageFetcherAbstract>() {
			@Override
			public PageFetcherAbstract call() throws Exception {
				boolean writeErrorFile = false;
				String errorMessage = "";
				while (fetcher.getRetryCount() <= retryLimit) {
					try {
						long t = System.currentTimeMillis();
						fetcher.setHtmlSrc(null);
						String c = fetcher.request();
						if (c != null && c.trim().length() != 0) {
							writer.write(c + "\n");
							logger.info((System.currentTimeMillis() - t) + " " + fetcher.getUrl());
							writeErrorFile = false;
							break;
						} else if (fetcher.isDoNotRetry()) {
							// for really no date returned.
							writeErrorFile = true;
							errorMessage = "normal\tEmpty String\n";
							logger.error("Empty String " + fetcher.getUrl());
							System.out.println("Empty String " + fetcher.getUrl());
							break;
						}
						// No exception but content is null try it again
						logger.error("No exception but content is null, try it again -----------" + fetcher.getRetryCount()
								+ "-------------" + fetcher.getUrl());
					} catch (ElementSkipException e) {
						logger.info("========all Element had been skiped for this page============== " + fetcher.getUrl());
						writeErrorFile = true;
						errorMessage = "normal\tall Element had been skiped for this page\n";
						break;
					} catch (Exception e) {
						writeErrorFile = true;
						errorMessage = "Exception\t" + e.getMessage();
						if (fetcher.isDoNotRetry()) {
							logger.error(fetcher.getUrl() + Util.getTrace(e));
							errorMessage = "normal\t" + e.getMessage();
							break;
						} else {
							logger.error("try it again -----------" + fetcher.getRetryCount() + "------------- " + fetcher.getUrl()
									+ Util.getTrace(e));
							System.out.println("try it again -----------" + fetcher.getRetryCount() + "------------- " + fetcher.getUrl()
									+ e.getMessage());
						}
						continue;
					} finally {
						fetcher.countRetry();
					}
				}
				if (writeErrorFile) {
					if (fetcher.isTraceHtmlStringWhenError()) {
						errorWriter.write(fetcher.getLine() + "\t" + errorMessage + fetcher.getHtmlSrc().replaceAll("\\s+", " ") + "\n");
					} else {
						errorWriter.write(fetcher.getLine() + "\t" + errorMessage + "\n");
					}
				}
				return fetcher;
			}
		};
		return task;
	}

	public void dealWithFile(File input, File output, String columnTitle) throws IOException {
		BufferedReader reader = FileUtil.getReader(input);
		Map<String, Integer> titleMap = Util.getFileTitle(input);
		String inputTitle = reader.readLine();
		String title = inputTitle + "\t" + newClumnNames;
		Integer column = titleMap.get(columnTitle.toLowerCase());
		if (column == null) {
			reader.close();
			throw new RuntimeException("the title " + columnTitle + " not existed.");
		}
		boolean appendDateFile = ifOutputFileExistedThenSkipThoseData(output, false);
		this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output, appendDateFile), "UTF-8"));
		if (!appendDateFile)
			writer.write(title + "\n");
		File errorFile = new File(output.getAbsolutePath() + ".error");
		boolean appendErrorFile = ifOutputFileExistedThenSkipThoseData(errorFile, true);
		this.errorWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(errorFile, appendErrorFile), "UTF-8"));
		if (!appendErrorFile) {
			errorWriter.write(inputTitle + "\tstatus\tMessage\n");
		}
		String[] seg;
		int count = 0;
		for (String line; (line = reader.readLine()) != null;) {
			count++;
			if (testing && count > testingSize) {
				break;
			}
			seg = line.split("\t");
			try {
				String url = seg[column];
				new URL(url);
				PageFetcherAbstract fetcher = generateFetcher(url, line);
				Callable<PageFetcherAbstract> task = generateCallable(fetcher);
				if (task != null)
					submitTask(task);
				else {
					logger.debug("skip task " + url);
					System.out.println("skip task " + url);
				}
			} catch (Exception e) {
				logger.error(line + "\t" + Util.getTrace(e));
			}
			if (fList.size() % batchSize == 0) {
				waitingOngoingTaskLowThanALimit(threadSize);
			}
		}
		waitingOngoingTaskLowThanALimit(0);
		if (task instanceof PageFetcherForMutipleOutput) {
			((PageFetcherForMutipleOutput) task).closeWriter();
		}
		writer.close();
		errorWriter.close();
		reader.close();
		if (errorFile.exists()) {
			if (errorFile.length() == 0) {
				errorFile.delete();
			}
		}
	}

	private boolean urlSouldSkip(String url) {
		if (urlFilterList == null || urlFilterList.isEmpty()) {
			return false;
		}
		for (TextProcessing f : urlFilterList) {
			try {
				f.parse(null, url);
			} catch (Exception e) {
				return true;
			}
		}
		return false;
	}

	private PageFetcherAbstract generateFetcher(String url, String line) throws CloneNotSupportedException {
		if (submittedURL.contains(url.toLowerCase().trim())) {
			return null;
		} else if (urlSouldSkip(url)) {
			return null;
		}
		PageFetcherAbstract fetcher = (PageFetcherAbstract) task.clone();
		fetcher.setUrl(url);
		fetcher.setLine(line);
		fetcher.setImageFolder(imgFolder);
		fetcher.setPageEncoding(pageEncoding);
		return fetcher;
	}

	private boolean ifOutputFileExistedThenSkipThoseData(File output, boolean errorfile) throws IOException {
		if (useNewFile) {
			return false;
		}
		if (!output.exists()) {
			return false;
		}
		Map<String, Integer> titleMap = Util.getFileTitle(output);
		if (titleMap == null) {
			return false;
		}
		Integer urlColumn = titleMap.get(columnTitle.toLowerCase());
		Integer statusColumnForErrorFile = null;
		if (errorfile) {
			statusColumnForErrorFile = titleMap.get("status".toLowerCase());
			if (statusColumnForErrorFile == null) {
				return false;
			}
		}
		if (urlColumn == null) {
			return false;
		}
		logger.info("the output file " + output + " existed. load those normal url to memory.");
		BufferedReader reader = FileUtil.getReader(output);
		reader.readLine();
		String[] seg;
		for (String line; (line = reader.readLine()) != null;) {
			try {
				seg = line.split("\t");
				String url = seg[urlColumn];
				if (errorfile && statusColumnForErrorFile != null) {
					String status = seg[statusColumnForErrorFile];
					if (status.equalsIgnoreCase("Exception")) {
						continue;
					}
				}
				System.out.println("load " + url);
				submittedURL.add(url.toLowerCase().trim());
			} catch (Exception e) {
				logger.equals(output + e.getMessage());
				e.printStackTrace();
			}
		}
		reader.close();
		return true;
	}

	int prossedLineCount = 0;

	@Override
	protected List<PageFetcherAbstract> waitingOngoingTaskLowThanALimit(int limt) {
		if (fList.size() <= limt) {
			return null;
		}
		prossedLineCount = prossedLineCount + fList.size();
		while (fList.size() > limt) {
			try {
				List<PageFetcherAbstract> hasMoreUrlList = new ArrayList<PageFetcherAbstract>();
				for (Future<PageFetcherAbstract> f : fList) {
					PageFetcherAbstract fetcher = f.get();
					if (fetcher.getMoreUrlNeedToCrawl() != null && !fetcher.getMoreUrlNeedToCrawl().isEmpty()) {
						hasMoreUrlList.add(fetcher);
					}
				}
				fList.clear();
				System.out.println("----------a-------Task num----" + fList.size() + "--------------------------");
				System.out.println("----------a-------Task num----" + fList.size() + "--------------------------");
				System.out.println("----------a-------Task num----" + fList.size() + "--------------------------");
				System.out.println("----------a-------Task num----" + fList.size() + "--------------------------");
				System.out.println("----------a-------Task num----" + fList.size() + "--------------------------");
				Set<String> moreUrlSet = new HashSet<String>();
				for (PageFetcherAbstract pageFetcher : hasMoreUrlList) {
					List<String> moreUrl = pageFetcher.getMoreUrlNeedToCrawl();
					if (moreUrl != null && moreUrl.size() != 0)
						for (String url : moreUrl) {
							if (url == null || url.trim().length() == 0)
								continue;
							if (moreUrlSet.contains(url)) {
								continue;
							} else {
								moreUrlSet.add(url);
							}
							PageFetcherAbstract f2 = generateFetcher(url, pageFetcher.getLine());
							Callable<PageFetcherAbstract> callable = generateCallable(f2);
							if (callable != null) {
								submitTask(callable);
								System.out.println("generate child " + pageFetcher.getUrl() + " : " + url);
								logger.debug("generate child " + pageFetcher.getUrl() + " : " + url);
							} else {
								logger.debug("skip task " + pageFetcher.getUrl() + " : " + url);
								System.out.println("skip task " + pageFetcher.getUrl() + " : " + url);
							}
						}

				}
				hasMoreUrlList.clear();
				hasMoreUrlList = null;
				moreUrlSet.clear();
				moreUrlSet = null;
				System.out.println("----------b-------Task num----" + fList.size() + "--------------------------");
				System.out.println("----------b-------Task num----" + fList.size() + "--------------------------");
				System.out.println("----------b-------Task num----" + fList.size() + "--------------------------");
				System.out.println("----------b-------Task num----" + fList.size() + "--------------------------");
				System.out.println("----------b-------Task num----" + fList.size() + "--------------------------");
				System.out.println("----------b-------Task num----" + fList.size() + "--------------------------");
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(Util.getTrace(e));
			}
		}

		System.out.println("-----------------Processed Line num " + prossedLineCount + " to read more-------------------");
		System.out.println("-----------------Tasks num " + fList.size() + " to read more-------------------");
		return null;
	}

	public void setExecutorSize(int size) {
		executor = Executors.newFixedThreadPool(size);
	}

	@Override
	public void startRun() throws Exception {
		try {
			dealWithFile(new File(inputFilePath), new File(outputFilePath), columnTitle);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				writer.close();
			} catch (Exception e2) {
			}
			try {
				errorWriter.close();
			} catch (Exception e2) {
			}
		}
	}

	public PageFetcherAbstract getTask() {
		return task;
	}

	public void setTask(PageFetcherAbstract task) {
		this.task = task;
	}

	public int getRetryLimit() {
		return retryLimit;
	}

	public void setRetryLimit(int retryLimit) {
		this.retryLimit = retryLimit;
	}

}
