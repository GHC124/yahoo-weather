package weather.common.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
	private final String mName;
	private final AtomicInteger mCount = new AtomicInteger(1);

	public NamedThreadFactory(String name) {
		mName = name;
	}

	public Thread newThread(Runnable r) {
		return new Thread(r, mName + " thread # " + mCount.getAndIncrement());
	}
}
