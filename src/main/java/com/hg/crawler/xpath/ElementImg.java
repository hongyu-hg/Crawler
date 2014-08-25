package com.hg.crawler.xpath;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.hg.crawler.PageFetcherAbstract;
import com.hg.crawler.tool.Util;

public class ElementImg extends TextProcessing {
	private Integer imgNameColumn;

	@Override
	public String parse(PageFetcherAbstract fetcher, String element) {
		String T = "\t";
		if (element == null || element.trim().length() == 0) {
			return T;
		}
		String imgLink = element;
		String filePath = null;
		try {
			imgLink = Util.getRealUrl(fetcher.getUrl(), imgLink);
			filePath = downloadImg(fetcher, imgLink);
		} catch (Exception e) {
			try {
				filePath = downloadImg(fetcher, imgLink);
			} catch (Exception e2) {
				logger.error(imgLink + Util.getTrace(e));
				e.printStackTrace();
			}
		}
		if (filePath != null && filePath.trim().length() != 0) {
			return imgLink + T + filePath;
		} else if (imgLink != null) {
			return imgLink + T;
		} else {
			return T;
		}
	}

	private String downloadImg(PageFetcherAbstract fetcher, String imgLink) throws IOException, MalformedURLException {
		String localPath = fetcher.getImageFolder() + "/" + imgLink.replaceAll("http://[^/]+/", "");
		try {
			if (imgNameColumn != null)
				localPath = fetcher.getImageFolder() + "/" + fetcher.getLine().split("\t")[imgNameColumn] + ".jpg";
		} catch (Exception e) {
			logger.error(imgLink + ": " + Util.getTrace(e));
		}
		localPath = localPath.replaceAll("\\?.*$", "");
		dumpImg(new URL(imgLink), new File(localPath));
		return localPath;
	}

	public boolean dumpImg(URL imgUrl, File file) throws IOException {
		file.getParentFile().mkdirs();
		if (file.exists()) {
			logger.debug(imgUrl + "\t" + file + " exsitd, skip download it!!!!");
			return false;
		}
		URLConnection connection = imgUrl.openConnection();
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(60000);
		InputStream is = connection.getInputStream();
		OutputStream os = null;
		os = new FileOutputStream(file);
		int bytesRead = 0;
		byte[] buffer = new byte[8192];
		while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
			os.write(buffer, 0, bytesRead);
		}
		is.close();
		os.close();
		return true;
	}

	public int getImgNameColumn() {
		return imgNameColumn;
	}

	public void setImgNameColumn(int imgNameColumn) {
		this.imgNameColumn = imgNameColumn;
	}

	public static void main(String[] args) throws MalformedURLException, IOException {
		ElementImg e = new ElementImg();
		e.dumpImg(new URL("http://ui.51bi.com/opt/siteimg/store/www.qianzhihe.com.cn.jpg"), new File("d:/t.jpg"));
	}
}
