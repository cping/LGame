package loon.action;

import loon.utils.Flip;

public class FlipXTo extends ActionEvent {

	private boolean flipX;

	public FlipXTo(boolean x) {
		this.flipX = x;
	}

	@Override
	public void update(long elapsedTime) {
		if (original != null && original instanceof Flip<?>) {
			Flip<?> flip = (Flip<?>) original;
			flip.setFlipX(flipX);
			this._isCompleted = true;
		}
	}

	@Override
	public void onLoad() {

	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		FlipXTo flip = new FlipXTo(flipX);
		flip.set(this);
		return flip;
	}

	@Override
	public ActionEvent reverse() {
		FlipXTo flip = new FlipXTo(!flipX);
		flip.set(this);
		return flip;
	}

	@Override
	public String getName() {
		return "flipx";
	}
}
