package loon.action;

public class ActionControl {

	private static ActionControl instanceAction;

	private Actions actions;

	private boolean pause;

	public static ActionControl getInstance() {
		if (instanceAction != null) {
			return instanceAction;
		}
		synchronized (ActionControl.class) {
			if (instanceAction == null) {
				instanceAction = new ActionControl();
			}
			return instanceAction;
		}
	}

	private final void call(long elapsedTime) {
		if (pause || actions.getCount() == 0) {
			return;
		}
		actions.update(elapsedTime);
	}

	public static final void update(long elapsedTime) {
		if (instanceAction != null) {
			instanceAction.call(elapsedTime);
		}
	}

	private ActionControl() {
		actions = new Actions();
	}

	public void addAction(ActionEvent action, ActionBind obj, boolean paused) {
		actions.addAction(action, obj, paused);
	}

	public void addAction(ActionEvent action, ActionBind obj) {
		addAction(action, obj, false);
	}

	public void removeAllActions(ActionBind actObject) {
		actions.removeAllActions(actObject);
	}

	public int getCount() {
		return actions.getCount();
	}

	public void removeAction(Object tag, ActionBind actObject) {
		actions.removeAction(tag, actObject);
	}

	public void removeAction(ActionEvent action) {
		actions.removeAction(action);
	}

	public ActionEvent getAction(Object tag, ActionBind actObject) {
		return actions.getAction(tag, actObject);
	}

	public void stop(ActionBind actObject) {
		actions.stop(actObject);
	}

	public void start(ActionBind actObject) {
		actions.start(actObject);
	}

	public void paused(boolean pause, ActionBind actObject) {
		actions.paused(pause, actObject);
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public void clear() {
		actions.clear();
	}

	public void stopAll() {
		clear();
		stop();
	}

	public void stop() {
		pause = true;
	}

}
