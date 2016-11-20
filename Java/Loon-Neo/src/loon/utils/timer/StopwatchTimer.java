package loon.utils.timer;

import loon.event.Updateable;

public class StopwatchTimer {

	private long from;

	private long to;

	private long lastStop;

	public static StopwatchTimer begin() {
		StopwatchTimer sw = new StopwatchTimer();
		sw.start();
		return sw;
	}

	public static StopwatchTimer make() {
		return new StopwatchTimer();
	}

	public static StopwatchTimer run(Updateable u) {
		StopwatchTimer sw = begin();
		u.action(null);
		sw.stop();
		return sw;
	}

	public long start() {
		from = currentTime();
		to = from;
		lastStop = to;
		return from;
	}

	private long currentTime() {
		return System.currentTimeMillis();
	}

	public StopwatchTimer stop() {
		lastStop = to;
		to = currentTime();
		return this;
	}

	public long getDuration() {
		return to - from;
	}

	public long getLastDuration() {
		return to - lastStop;
	}

	public long getStartTime() {
		return from;
	}

	public long getEndTime() {
		return to;
	}
}
