package loon.action;

import loon.LSystem;
import loon.Screen;
import loon.event.FrameLoopEvent;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.timer.LTimerContext;

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
		_isCompleted = _event.isDead();
	}

	@Override
	public void onLoad() {
		_event.setDelay(getDelay());
		RealtimeProcess process = new RealtimeProcess() {

			@Override
			public void run(LTimerContext time) {
				Screen screen = null;
				if (LSystem.getProcess() != null
						&& LSystem.getProcess().getScreen() != null) {
					screen = LSystem.getProcess().getScreen();
				}
				_event.call(time.timeSinceLastUpdate, screen);
				if (_event.isDead()) {
					kill();
					_event.completed();
				}
			}
		};
		process.setDelay(0);
		RealtimeProcessManager.get().addProcess(process);
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
		return new EventTo(_event);
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
