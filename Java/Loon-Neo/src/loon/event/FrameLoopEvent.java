package loon.event;

import loon.LSystem;
import loon.Screen;
import loon.utils.timer.LTimer;

public abstract class FrameLoopEvent {

	private boolean killSelf = false;

	private LTimer timer = new LTimer(0);

	public abstract void invoke(long elapsedTime, Screen e);

	public abstract void completed();

	public final void call(long elapsedTime, Screen e) {
		if (timer.action(elapsedTime)) {
			invoke(elapsedTime, e);
		}
	}

	public FrameLoopEvent reset() {
		this.killSelf = false;
		this.timer.refresh();
		return this;
	}

	public FrameLoopEvent setDelay(long d) {
		timer.setDelay(d);
		return this;
	}

	public FrameLoopEvent setSecond(float s) {
		timer.setDelay((long) (LSystem.SECOND * s));
		return this;
	}

	public float getSecond() {
		return (float) timer.getDelay() / (float) LSystem.SECOND;
	}

	public LTimer getTimer() {
		return timer;
	}

	public FrameLoopEvent kill() {
		killSelf = true;
		return this;
	}

	public boolean isDead() {
		return killSelf;
	}

}
