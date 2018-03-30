package com.legacy.server.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class RSCLThreadFactory implements ThreadFactory {

	private final String name;

	private final AtomicInteger threadCount = new AtomicInteger();

	public RSCLThreadFactory(String name) {
		this.name = name;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r, new StringBuilder(name).append("-")
				.append(threadCount.getAndIncrement()).toString());
		return thread;
	}
}
