package loon;

public class LimitedCounter extends Counter {
	private int limit;

	public LimitedCounter(int limit) {
		this.limit = limit;
	}

	public int getLimit() {
		return limit;
	}

	@Override
	public int increment() {
		if (!isLimitReached()) {
			return super.increment();
		}
		return getValue();
	}

	public int valuesUntilLimitRemains() {
		return limit - getValue();
	}

	@Override
	public void clear() {
		super.clear();
	}

	public boolean isLimitReached() {
		return getValue() == limit;
	}

}
