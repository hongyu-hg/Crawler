package com.hg.crawler.test;

import hg.tool.file.FileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class BrandFilter {
	public static void main(String[] args) throws IOException {
		String[] badBrand = new String[] { "NULL", "手机通讯", "数码产品", "电脑网络", "美容化妆", "母婴用品", "家居日用", "服饰箱包", "珠宝首饰", "礼品饰品", "运动户外", "汽车用品",
				"食品餐饮", "办公用品", "鲜花园艺", "建材装潢", "旅行票务", "团购网站", "图书音像", "虚拟产品", "宠物专区", "淘宝精品" };
		BufferedReader reader = FileUtil.getReader("C:/Users/Lihongyu/Desktop/level_4.txt");
		BufferedWriter writer = FileUtil.getWriter("C:/Users/Lihongyu/Desktop/level_4_after_filter_0.txt");
		String[] seg;
		for (String line; (line = reader.readLine()) != null;) {
			seg = line.split("\t");
			String brand = seg[4];
			boolean skip = false;
			for (int i = 0; i < badBrand.length; i++) {
				if (brand.equalsIgnoreCase(badBrand[i])) {
					skip = true;
				}
			}
			if (skip) {
				continue;
			}
			writer.write(line);
			writer.newLine();
		}
		reader.close();
		writer.flush();
		writer.close();
	}
}
