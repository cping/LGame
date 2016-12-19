package loon.action;

import loon.utils.Flip;

public class FlipYTo extends ActionEvent {

	private boolean flipY;

	public FlipYTo(boolean x) {
		this.flipY = x;
	}

	@Override
	public void update(long elapsedTime) {
		if (original != null && original instanceof Flip<?>) {
			Flip<?> flip = (Flip<?>) original;
			flip.setFlipY(flipY);
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
		FlipYTo flip = new FlipYTo(flipY);
		flip.set(this);
		return flip;
	}

	@Override
	public ActionEvent reverse() {
		FlipYTo flip = new FlipYTo(!flipY);
		flip.set(this);
		return flip;
	}

	@Override
	public String getName() {
		return "flipy";
	}
}
