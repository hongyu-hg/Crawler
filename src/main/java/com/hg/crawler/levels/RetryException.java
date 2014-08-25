package com.hg.crawler.levels;

public class RetryException extends Exception {
	private static final long serialVersionUID = 1L;

	public RetryException(String message) {
		super(message);
	}
}
