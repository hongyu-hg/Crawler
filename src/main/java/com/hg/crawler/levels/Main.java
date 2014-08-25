package com.hg.crawler.levels;

import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
	public static void main(String[] args) throws Exception {
		Date start = new Date();
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				args[0]);
		TaskCenter center = (TaskCenter) applicationContext
				.getBean("taskCenter");
		center.doTheJobList();
		System.out.println("start at " + start);
		System.out.println("end at " + new Date());
		System.exit(0);
	}
}
