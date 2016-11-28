package loon.action;

import loon.LSystem;
import loon.Screen;
import loon.event.FrameLoopEvent;

/**
 * 执行一个FrameLoopEvent事件
 *
 */
public class EventTo extends ActionEvent {

	private final FrameLoopEvent _event;

	public EventTo(FrameLoopEvent e) {
		this._event = e;
	}

	@Override
	public void update(long elapsedTime) {
		Screen screen = null;
		if (LSystem.getProcess() != null
				&& LSystem.getProcess().getScreen() != null) {
			screen = LSystem.getProcess().getScreen();
		}
		_event.call(elapsedTime, screen);
		if (_event.isDead()) {
			_event.completed();
		}
		_isCompleted = _event.isDead();
	}

	@Override
	public void onLoad() {
		_event.setDelay(getDelay());
	}

	public void kill() {
		_event.kill();
	}

	public FrameLoopEvent getLoopEvent() {
		return _event;
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		EventTo event = new EventTo(_event);
		event.set(this);
		return event;
	}

	@Override
	public ActionEvent reverse() {
		_event.reset();
		return cpy();
	}

	@Override
	public String getName() {
		return "event";
	}

}
