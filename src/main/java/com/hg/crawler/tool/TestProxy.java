package com.hg.crawler.tool;

import hg.tool.file.FileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class TestProxy {
	public static void main(String[] args) throws ClientProtocolException, IOException {
		TestProxy proxy = new TestProxy();
		proxy.dealWithFile(new File(args[0]));
	}

	public Long testProxy(String url, String httpProxy) throws IOException, ClientProtocolException {
		long t = System.currentTimeMillis();
		HttpParams httpParams = new BasicHttpParams();
		HttpClient client = new DefaultHttpClient(httpParams);
		if (httpProxy != null && httpProxy.trim().length() != 0) {
			HttpHost proxy = new HttpHost(httpProxy.split(":")[0], Integer.valueOf(httpProxy.split(":")[1]));
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		HttpGet get = new HttpGet(url);
		HttpResponse r = client.execute(get);
		String html;
		html = new String(EntityUtils.toByteArray(r.getEntity()), "UTF-8");
		html = html.replaceAll("</html>", "");
		html = html.replaceAll("</body>", "");
		html = html + "</body></html>";
		return System.currentTimeMillis() - t;
	}

	protected BufferedWriter writer;

	public void dealWithFile(File file) throws IOException {
		BufferedReader reader = FileUtil.getReader(file);
		writer = FileUtil.getWriter(file.getAbsolutePath() + ".R");
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		for (String line; (line = reader.readLine()) != null;) {
			final String lin = line;
			executorService.submit(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					try {
						System.out.println(lin);
						Long t = testProxy("http://www.baidu.com", lin);
						writer.write(lin + "\t" + t);
						writer.newLine();
						writer.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
			});
		}
		writer.flush();
	}
}
