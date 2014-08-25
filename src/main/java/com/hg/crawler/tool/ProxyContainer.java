package com.hg.crawler.tool;

import hg.tool.file.FileUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

public class ProxyContainer {
	private List<String> proxyList = new ArrayList<String>();
	private Set<String> valiableProxyList = new HashSet<String>();
	private int dealLine = 200;
	private int pos = 0;
	@Autowired
	String filePath;

	public void loadProxyFromDisk() throws IOException {
		BufferedReader reader = FileUtil.getReader(filePath);
		for (String line; (line = reader.readLine()) != null;) {
			proxyList.add(line.split("\t")[0]);
		}
		reader.close();
	}

	public void thisProxyIsValiable(String proxy) {
		synchronized (this) {
			if (!valiableProxyList.contains(proxy)) {
				valiableProxyList.add(proxy);
			}
		}
	}

	public String getAProxy() {
		synchronized (this) {
			if (pos >= proxyList.size()) {
				pos = 0;
			}
			String pro = proxyList.get(pos);
			pos++;
			return pro;
		}
	}

	public void removeBadProxy(String proxy) {
		synchronized (this) {
			proxyList.remove(proxy);
			if (proxyList.size() < dealLine) {
				Set<String> t = valiableProxyList;
				for (String s : t) {
					if (!proxyList.contains(s))
						proxyList.add(s);
				}
			}
		}
	}

	public void removeBlockProxy(String proxy) {
		synchronized (this) {
			proxyList.remove(proxy);
			valiableProxyList.remove(proxy);
			if (proxyList.size() < dealLine) {
				Set<String> t = valiableProxyList;
				for (String s : t) {
					if (!proxyList.contains(s))
						proxyList.add(s);
				}
			}
		}
	}

	public int getProxyPos() {
		return pos;
	}

	public int getProxySize() {
		return proxyList.size();
	}

	public int getValiableProxySize() {
		return valiableProxyList.size();
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getDealLine() {
		return dealLine;
	}

	public void setDealLine(int dealLine) {
		this.dealLine = dealLine;
	}

}
