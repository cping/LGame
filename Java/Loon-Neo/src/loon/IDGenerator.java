package loon;

public class IDGenerator {

	private static IDGenerator instance;

	public final static IDGenerator make() {
		return new IDGenerator();
	}

	public final static IDGenerator get() {
		if (instance != null) {
			return instance;
		}
		synchronized (IDGenerator.class) {
			if (instance == null) {
				instance = make();
			}
			return instance;
		}
	}
	
	private final Counter _counter = new Counter();

	private IDGenerator() {
	}

	public final int generate() {
		return _counter.increment();
	}

	public final int getID() {
		return _counter.getValue();
	}

	public final void clear() {
		_counter.clear();
	}
}
