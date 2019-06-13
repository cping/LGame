/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.utils.timer;

import loon.LRelease;
import loon.event.Updateable;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.processes.GameProcessType;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;

/**
 * Loon的计时器类
 * 
 * <pre>
 *  //提交timer到游戏循环中
 *	LTimer time = new LTimer();
 *  //执行六次
 *  time.setRepeats(6);
 *  time.setUpdateable(...);
 *  time.submit();
 *  //关闭
 *  //time.close();
 * </pre>
 */
public class LTimer implements LRelease {

	private static class TimerProcess extends RealtimeProcess {

		private LTimer timer = null;

		public TimerProcess(LTimer t) {
			this.timer = t;
			this.setProcessType(GameProcessType.Time);
		}

		@Override
		public void run(LTimerContext time) {
			if (timer != null) {
				timer.action(time);
				if (timer.isClosed() || timer.isCompleted()) {
					kill();
				}
			}
		}

	}

	private static LTimer _instance = null;

	public static LTimer getInstance() {
		if (_instance == null) {
			synchronized (LTimer.class) {
				if (_instance == null) {
					_instance = new LTimer(0);
				}
			}
		}
		return _instance;
	}

	public static LTimer get() {
		return getInstance();
	}

	public static LTimer at() {
		return new LTimer();
	}

	public static LTimer at(long delay) {
		return new LTimer(delay);
	}

	public static LTimer at(Duration delay) {
		return new LTimer(delay);
	}

	private static int GLOBAL_ID = 0;

	private TimerProcess process = null;
	private int id = 0;
	private int maxNumberOfRepeats = -1;
	private int numberOfTicks = 0;

	private boolean repeats = true;
	private boolean completed = false;
	private boolean closed = false;

	private float speedFactor = 1f;

	private long delay = 0;
	private long currentTick = 0;
	private boolean active = true;

	private Updateable update;

	public LTimer() {
		this(450);
	}

	public LTimer(Duration d) {
		this(d == null ? 0 : d.toMillisLong());
	}

	public LTimer(long delay) {
		this(delay, 1f);
	}

	public LTimer(long delay, float factor) {
		this(delay, -1, factor, true);
	}

	public LTimer(long delay, int numberOfRepeats, float factor, boolean repeats) {
		this.id = GLOBAL_ID++;
		this.closed = false;
		this.reset(delay, numberOfRepeats, factor, repeats);
	}

	public boolean action(LTimerContext context) {
		return action(context.timeSinceLastUpdate);
	}
	
	public boolean action(float delta) {
		return action((long) (MathUtils.max(delta * 1000, 10)));
	}
	
	public boolean action(long elapsedTime) {
		if (this.closed) {
			return false;
		}
		if (this.active) {
			this.currentTick += (elapsedTime * speedFactor);
			if (this.maxNumberOfRepeats > -1 && this.numberOfTicks >= this.maxNumberOfRepeats) {
				this.completed = true;
			}
			if (!this.completed && this.currentTick >= this.delay) {
				if (this.update != null) {
					this.update.action(this);
				}
				this.numberOfTicks++;
				if (this.repeats) {
					this.currentTick = 0;
				} else {
					this.completed = true;
				}
				return true;
			}
		}
		return false;
	}

	public LTimer refresh() {
		return reset();
	}

	public LTimer reset() {
		return this.reset(this.delay, this.maxNumberOfRepeats, this.speedFactor, this.repeats);
	}

	public LTimer reset(long newDelay, int newNumberOfRepeats, float newFactor, boolean newRepeats) {
		this.delay = MathUtils.max(newDelay, 0);
		this.maxNumberOfRepeats = MathUtils.max(newNumberOfRepeats, -1);
		this.speedFactor = MathUtils.max(newFactor, 0.01f);
		this.repeats = newRepeats;
		this.active = true;
		this.completed = false;
		this.currentTick = 0;
		this.numberOfTicks = 0;
		this.speedFactor = 1f;
		return this;
	}

	public LTimer setEquals(LTimer other) {
		this.delay = MathUtils.max(other.delay, 0);
		this.maxNumberOfRepeats = MathUtils.max(other.maxNumberOfRepeats, -1);
		this.speedFactor = MathUtils.max(other.speedFactor, 0.01f);
		this.repeats = other.repeats;
		this.active = other.active;
		this.completed = other.completed;
		this.currentTick = other.currentTick;
		this.numberOfTicks = other.numberOfTicks;
		this.speedFactor = other.speedFactor;
		return this;
	}

	public LTimer addPercentage(long elapsedTime) {
		this.currentTick += elapsedTime;
		return this;
	}

	public LTimer addPercentage(LTimerContext context) {
		this.currentTick += context.timeSinceLastUpdate;
		return this;
	}

	public int getTimesRepeated() {
		return this.numberOfTicks;
	}

	public long getDelay() {
		return this.delay;
	}

	public LTimer setDelay(Duration d) {
		return setDelay(d == null ? 0 : d.toMillisLong());
	}

	public LTimer setDelay(long delay) {
		return reset(delay, this.maxNumberOfRepeats, this.speedFactor, this.repeats);
	}

	public LTimer setRepeats(int amount) {
		return setRepeats(amount, true);
	}

	public LTimer setRepeats(int amount, boolean newRepats) {
		return this.reset(this.delay, amount, this.speedFactor, newRepats);
	}

	public LTimer setActive(boolean bool) {
		this.reset();
		this.active = bool;
		return this;
	}

	public boolean isActive() {
		return this.active;
	}

	public LTimer start() {
		this.active = true;
		return this;
	}

	public LTimer stop() {
		this.active = false;
		return this;
	}

	public LTimer pause() {
		this.active = false;
		return this;
	}

	public LTimer unpause() {
		this.active = true;
		return this;
	}

	public int getId() {
		return id;
	}

	public long getCurrentTick() {
		return this.currentTick;
	}

	public LTimer setCurrentTick(long tick) {
		this.currentTick = tick;
		return this;
	}

	public float getPercentage() {
		return (float) this.currentTick / (float) this.delay;
	}

	public float getRemaining() {
		return (float) (this.delay - this.currentTick);
	}

	public boolean checkInterval(long interval) {
		return (currentTick / interval) > ((currentTick - delay) / interval);
	}

	public LTimer clamp() {
		if (this.currentTick > this.delay) {
			currentTick = delay;
		}
		return this;
	}

	public float getSpeedFactor() {
		return speedFactor;
	}

	public LTimer setSpeedFactor(float factor) {
		this.speedFactor = factor;
		return this;
	}

	public boolean isCompleted() {
		return completed;
	}

	public LTimer setCompleted(boolean completed) {
		this.completed = completed;
		return this;
	}

	public Updateable getUpdateable() {
		return update;
	}

	public LTimer setUpdateable(Updateable u) {
		this.update = u;
		return this;
	}

	public LTimer submit() {
		synchronized (RealtimeProcessManager.class) {
			RealtimeProcessManager.get().delete(process);
			if (process == null || process.isDead()) {
				process = new TimerProcess(this);
			}
			process.setDelay(0);
			RealtimeProcessManager.get().addProcess(process);
		}
		return this;
	}

	public boolean isClosed() {
		return this.closed;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("LTimer");
		builder.kv("currentTick", currentTick).comma().kv("delay", delay).comma().kv("factor", speedFactor).comma()
				.kv("active", active).comma().kv("repeats", repeats).comma()
				.kv("maxNumberOfRepeats", maxNumberOfRepeats).comma().kv("numberOfTicks", numberOfTicks).comma()
				.kv("completed", completed);
		return builder.toString();
	}

	@Override
	public void close() {
		stop();
		this.closed = true;
		if (process != null) {
			process.close();
			process = null;
		}
	}

}
