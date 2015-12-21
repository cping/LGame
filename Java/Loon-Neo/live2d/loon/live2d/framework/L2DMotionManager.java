package loon.live2d.framework;

import loon.live2d.ALive2DModel;
import loon.live2d.motion.AMotion;
import loon.live2d.motion.MotionQueueManager;

public class L2DMotionManager extends MotionQueueManager {

	private int currentPriority;
	private int reservePriority;

	public int getCurrentPriority() {
		return currentPriority;
	}

	public int getReservePriority() {
		return reservePriority;
	}

	public boolean reserveMotion(int priority) {
		if (reservePriority >= priority) {
			return false;
		}
		if (currentPriority >= priority) {
			return false;
		}
		reservePriority = priority;
		return true;
	}

	public void setReservePriority(int val) {
		reservePriority = val;
	}

	@Override
	public boolean updateParam(ALive2DModel model) {
		boolean updated = super.updateParam(model);
		if (isFinished()) {
			currentPriority = 0;
		}
		return updated;
	}

	public int startMotionPrio(AMotion motion, int priority) {
		if (priority == reservePriority) {
			reservePriority = 0;
		}
		currentPriority = priority;
		return super.startMotion(motion, false);
	}
}
