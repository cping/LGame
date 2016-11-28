package loon.action;

import loon.event.Updateable;

public class UpdateTo extends ActionEvent {

	private Updateable updateable;

	public UpdateTo(Updateable u) {
		this.updateable = u;
	}

	@Override
	public void update(long elapsedTime) {
		if (updateable != null) {
			updateable.action(original);
		}
		this._isCompleted = true;
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
		UpdateTo update = new UpdateTo(updateable);
		update.set(this);
		return update;
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "update";
	}
}
