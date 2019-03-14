package loon.action;

import loon.LSystem;
import loon.utils.MathUtils;
import loon.utils.TArray;

public final class TimeLine extends ActionTweenBase<TimeLine> {

	private enum Modes {
		SEQUENCE, PARALLEL
	}

	private static final ActionTweenPool.Callback<TimeLine> poolCallback = new ActionTweenPool.Callback<TimeLine>() {
		@Override
		public void onPool(TimeLine obj) {
			obj.reset();
		}

		@Override
		public void onUnPool(TimeLine obj) {
			try {
				obj.reset();
				onSuccess(obj);
			} catch (Exception ex) {
				onFailure(ex);
			}
		}

		@Override
		public void onSuccess(TimeLine result) {

		}

		@Override
		public void onFailure(Throwable cause) {
			// TODO Auto-generated method stub

		}
	};

	static final ActionTweenPool<TimeLine> pool = new ActionTweenPool<TimeLine>(10, poolCallback) {
		@Override
		protected TimeLine create() {
			return new TimeLine();
		}
	};

	public static int getPoolSize() {
		return pool.size();
	}

	public static void resize(int minCapacity) {
		pool.resize(minCapacity);
	}

	public static TimeLine createSequence() {
		TimeLine tl = pool.get();
		tl.setup(Modes.SEQUENCE);
		return tl;
	}

	public static TimeLine createParallel() {
		TimeLine tl = pool.get();
		tl.setup(Modes.PARALLEL);
		return tl;
	}

	private final TArray<ActionTweenBase<?>> children = new TArray<ActionTweenBase<?>>(10);
	private TimeLine current;
	private TimeLine parent;
	private Modes mode;

	private TimeLine() {
		reset();
	}

	@Override
	protected void reset() {
		super.reset();
		children.clear();
		current = parent = null;
	}

	private void setup(Modes mode) {
		this.mode = mode;
		this.current = this;
	}

	public TimeLine push(ActionTween tween) {
		current.children.add(tween);
		return this;
	}

	public TimeLine push(TimeLine timeline) {
		if (timeline.current != timeline) {
			throw LSystem.runThrow("You forgot to call a few 'end()' statements in your pushed timeline");
		}
		timeline.parent = current;
		current.children.add(timeline);
		return this;
	}

	public TimeLine pushPause(float time) {
		current.children.add(ActionTween.mark().delay(time));
		return this;
	}

	public TimeLine beginSequence() {
		TimeLine tl = pool.get();
		tl.parent = current;
		tl.mode = Modes.SEQUENCE;
		current.children.add(tl);
		current = tl;
		return this;
	}

	public TimeLine beginParallel() {
		TimeLine tl = pool.get();
		tl.parent = current;
		tl.mode = Modes.PARALLEL;
		current.children.add(tl);
		current = tl;
		return this;
	}

	public TimeLine end() {
		if (current == this) {
			throw LSystem.runThrow("Nothing to end...");
		}
		current = current.parent;
		return this;
	}

	@Override
	public TimeLine delay(float d) {
		super.delay(delay);
		for (int i = 0, n = children.size; i < n; i++) {
			ActionTweenBase<?> obj = children.get(i);
			obj.delay(d);
		}
		return this;
	}

	public TimeLine repeat(float time) {
		return repeat(1, time);
	}

	@Override
	public TimeLine repeat(int count, float time) {
		super.repeat(count, time);
		for (int i = 0, n = children.size; i < n; i++) {
			ActionTweenBase<?> obj = children.get(i);
			obj.repeat(count, time);
		}
		return this;
	}

	@Override
	public TimeLine repeatBackward(int count, float time) {
		super.repeatBackward(count, time);
		for (int i = 0, n = children.size; i < n; i++) {
			ActionTweenBase<?> obj = children.get(i);
			obj.repeatBackward(count, time);
		}
		return this;
	}

	public TArray<ActionTweenBase<?>> getChildren() {
		return current.children;
	}

	@Override
	public TimeLine build() {

		duration = 0;

		for (int i = 0; i < children.size; i++) {
			ActionTweenBase<?> obj = children.get(i);

			if (obj.getRepeatCount() < 0) {
				throw LSystem.runThrow("You can't push an object with infinite repetitions in a timeline");
			}
			obj.build();

			switch (mode) {
			case SEQUENCE:
				float tDelay = duration;
				duration += obj.getFullDuration();
				obj.delay += tDelay;
				break;

			case PARALLEL:
				duration = MathUtils.max(duration, obj.getFullDuration());
				break;
			}
		}

		return this;
	}

	@Override
	public TweenTo<TimeLine> start() {
		for (int i = 0; i < children.size; i++) {
			ActionTweenBase<?> obj = children.get(i);
			obj.start();
		}
		return super.start();
	}

	@Override
	public void free() {
		for (int i = children.size - 1; i >= 0; i--) {
			ActionTweenBase<?> obj = children.removeIndex(i);
			obj.free();
		}
		pool.free(this);
		ActionControl.get().removeAllActions(_target);
	}

	@Override
	protected boolean actionEventOver() {
		for (int i = 0, n = children.size; i < n; i++) {
			ActionTweenBase<?> obj = children.get(i);
			if (!obj.actionEventOver()) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void update(int step, int lastStep, boolean isIterationStep, float delta) {

		if (!isIterationStep && step > lastStep) {
			float dt = isReverse(lastStep) ? -delta - 1 : delta + 1;
			for (int i = 0, n = children.size; i < n; i++) {
				children.get(i).update(dt);
			}
			return;
		}

		if (!isIterationStep && step < lastStep) {
			float dt = isReverse(lastStep) ? -delta - 1 : delta + 1;
			for (int i = children.size - 1; i >= 0; i--) {
				children.get(i).update(dt);
			}
			return;
		}

		if (step > lastStep) {
			if (isReverse(step)) {
				forceEndValues();
				for (int i = 0, n = children.size; i < n; i++) {
					children.get(i).update(delta);
				}
			} else {
				forceStartValues();
				for (int i = 0, n = children.size; i < n; i++) {
					children.get(i).update(delta);
				}
			}

		} else if (step < lastStep) {
			if (isReverse(step)) {
				forceStartValues();
				for (int i = children.size - 1; i >= 0; i--) {
					children.get(i).update(delta);
				}
			} else {
				forceEndValues();
				for (int i = children.size - 1; i >= 0; i--) {
					children.get(i).update(delta);
				}
			}

		} else {
			float dt = isReverse(step) ? -delta : delta;
			if (delta >= 0) {
				for (int i = 0, n = children.size; i < n; i++) {
					children.get(i).update(dt);
				}
			} else {
				for (int i = children.size - 1; i >= 0; i--) {
					children.get(i).update(dt);
				}
			}
		}
	}

	@Override
	protected void forceStartValues() {
		for (int i = children.size - 1; i >= 0; i--) {
			ActionTweenBase<?> obj = children.get(i);
			obj.forceToStart();
		}
	}

	@Override
	protected void forceEndValues() {
		for (int i = 0, n = children.size; i < n; i++) {
			ActionTweenBase<?> obj = children.get(i);
			obj.forceToEnd(duration);
		}
	}

	@Override
	protected boolean containsTarget(ActionBind target) {
		for (int i = 0, n = children.size; i < n; i++) {
			ActionTweenBase<?> obj = children.get(i);
			if (obj.containsTarget(target)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean containsTarget(ActionBind target, int tweenType) {
		for (int i = 0, n = children.size; i < n; i++) {
			ActionTweenBase<?> obj = children.get(i);
			if (obj.containsTarget(target, tweenType)) {
				return true;
			}
		}
		return false;
	}
}
