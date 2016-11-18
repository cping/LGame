package loon.utils.timer;

public abstract class FloatTimerEvent {

	private float delay;
	private boolean repeat;
	private float acc;
	private boolean done;
	private boolean stopped;

	public FloatTimerEvent(float delay) {
		this(delay, false);
	}

	public FloatTimerEvent(float delay, boolean repeat) {
		this.delay = delay;
		this.repeat = repeat;
		this.acc = 0.0F;
	}

	public void update(long elapsedTime) {
		this.update(elapsedTime / 1000f);
	}

	public void update(float delta) {
		if ((!this.done) && (!this.stopped)) {
			this.acc += delta;

			if (this.acc >= this.delay) {
				this.acc -= this.delay;

				if (this.repeat)
					reset();
				else {
					this.done = true;
				}

				execute();
			}
		}
	}

	public void reset() {
		this.stopped = false;
		this.done = false;
		this.acc = 0.0F;
	}

	public boolean isCompleted() {
		return this.done;
	}

	public boolean isRunning() {
		return (!this.done) && (this.acc < this.delay) && (!this.stopped);
	}

	public void stop() {
		this.stopped = true;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public abstract void execute();

	public float getPercentageRemaining() {
		if (this.done)
			return 100.0F;
		if (this.stopped) {
			return 0.0F;
		}
		return 1f - (this.delay - this.acc) / this.delay;
	}

	public float getDelay() {
		return this.delay;
	}
}
