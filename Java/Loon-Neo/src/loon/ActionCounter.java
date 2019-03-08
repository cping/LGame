package loon;

import loon.event.Updateable;

public class ActionCounter extends LimitedCounter {

	private Updateable actListener;

	public ActionCounter(int limit, Updateable actListener) {
		super(limit);
	}

	public ActionCounter(int limit) {
		super(limit);
	}

	public void setActionListener(Updateable u) {
		this.actListener = u;
	}

	@Override
	public int increment() {
		boolean isLimitReachedBefore = isLimitReached();
		int result = super.increment();
		if (actListener != null && isLimitReached() && !isLimitReachedBefore) {
			actListener.action(this);
		}
		return result;
	}
}
