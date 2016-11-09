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

	public void setSecond(float s) {
		timer.setDelay((long) (LSystem.SECOND * s));
	}

	public float getSecond() {
		return timer.getDelay() / (float) LSystem.SECOND;
	}

	public LTimer getTimer() {
		return timer;
	}
	
	public void kill(){
		killSelf = true;
	}
	
	public boolean isDead(){
		return killSelf;
	}

}
