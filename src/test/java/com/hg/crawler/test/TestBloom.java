package com.hg.crawler.test;

import com.hg.bloom.Bloom;

public class TestBloom {
	public static void main(String[] args) {
		Bloom bloom = new Bloom(100000);
		bloom.add("xxx");
		System.out.println(bloom.contains("ttt"));
	}
}
