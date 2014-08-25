package com.hg.file;

import hg.tool.file.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.hg.crawler.tool.Util;

public class PostUrl {

	// String postUri = "http://hg-c-00.appspot.com/upload";
	String postUri = "http://hg-c-90.appspot.com/upload";
	// String postUri = "http://localhost:8888/upload";

	private String MerchantName = "51buy";
	String merchatFile = "XXXitem.51buy.com.txt";

	// private String MerchantName = "yihaodian";
	// String merchatFile = "yhd.dat";

	// String postUri = "http://localhost:8888/upload";
	private int id = 0;

	private void dealwithFile(File file) throws IOException {
		BufferedReader reader = FileUtil.getReader(file);
		int count = 0;
		StringBuffer postString = new StringBuffer();
		Map<String, Integer> urlColumne = Util.getFileTitle(file);
		Integer urlC = urlColumne.get("OfferLink".toLowerCase());
		String[] seg;
		for (String line; (line = reader.readLine()) != null;) {
			seg = line.split("\t");
			count++;
			postString.append(seg[urlC]).append("\n");
			if (count % 1000 == 0) {
				postString(postString);
			}
		}
		if (postString.length() != 0)
			postString(postString);
		reader.close();
	}

	private void postString(StringBuffer postString) throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(postUri);
		List<NameValuePair> params = new ArrayList<NameValuePair>(1);
		params.add(new BasicNameValuePair("id", String.valueOf(++id)));
		params.add(new BasicNameValuePair("m", MerchantName));
		params.add(new BasicNameValuePair("u", postString.toString()));
		post.setEntity(new UrlEncodedFormEntity(params));
		HttpResponse r = httpclient.execute(post);
		System.out.println(id + "/text size:" + postString.length() + " " + r.getStatusLine());
		if (r.getStatusLine().getStatusCode() != 200) {
			httpclient = new DefaultHttpClient();
			r = httpclient.execute(post);
			System.out.println(id + " try again/text size:" + postString.length() + " " + r.getStatusLine());
		}
		if (r.getStatusLine().getStatusCode() != 200) {
			httpclient = new DefaultHttpClient();
			r = httpclient.execute(post);
			System.out.println(id + " try again/text size:" + postString.length() + " " + r.getStatusLine());
		}
		if (r.getStatusLine().getStatusCode() != 200) {
			httpclient = new DefaultHttpClient();
			r = httpclient.execute(post);
			System.out.println(id + " try again/text size:" + postString.length() + " " + r.getStatusLine());
		}
		postString.delete(0, postString.length());
	}

	public static void main(String[] args) throws IOException {
		PostUrl postUrl = new PostUrl();
		// D:\home\warren\jayadata\huihui\splitByHost\item.vjia.com.txt
		postUrl.dealwithFile(new File("/home/warren/jayadata/huihui/splitByHost/" + postUrl.merchatFile));
	}
}
