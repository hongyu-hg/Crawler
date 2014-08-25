package com.hg.crawler.test;

import java.util.regex.Pattern;

public class T {
	public static void main(String[] args) {
		System.out.println("1245712".matches("^[0-9]{7}$"));
		System.out.println(Pattern.matches("1245712", "^[0-9]{7}$"));
	}
}
