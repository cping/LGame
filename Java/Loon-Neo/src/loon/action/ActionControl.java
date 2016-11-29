package loon.action;

/** 全局生效的动作控制类（在Loon中任何场景都适用），所有实现了ActionBind的类都可以被此类控制 **/
public class ActionControl {

	public static final ActionLinear LINEAR = new ActionLinear();
	public static final ActionSmooth SMOOTH = new ActionSmooth();

	private static ActionControl instanceAction;

	private Actions actions;

	private boolean pause;

	public static ActionControl get() {
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

	public boolean containsKey(ActionBind actObject) {
		return actions.containsKey(actObject);
	}

	public boolean isCompleted(ActionBind actObject) {
		return actions.isCompleted(actObject);
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

	public void stop() {
		clear();
		pause();
	}

	public void pause() {
		pause = true;
	}

}
