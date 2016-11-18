package loon.action;

public class ShowTo extends ActionEvent {

	private boolean visible;

	public ShowTo(boolean v) {
		this.visible = v;
	}

	@Override
	public void update(long elapsedTime) {
		if (original.isVisible() != visible) {
			original.setVisible(visible);
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
		return new ShowTo(visible);
	}

	@Override
	public ActionEvent reverse() {
		return new ShowTo(!visible);
	}

	@Override
	public String getName() {
		return "show";
	}
}
