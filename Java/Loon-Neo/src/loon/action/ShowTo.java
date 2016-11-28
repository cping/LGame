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
		ShowTo show = new ShowTo(visible);
		show.set(this);
		return show;
	}

	@Override
	public ActionEvent reverse() {
		ShowTo show = new ShowTo(!visible);
		show.set(this);
		return show;
	}

	@Override
	public String getName() {
		return "show";
	}
}
