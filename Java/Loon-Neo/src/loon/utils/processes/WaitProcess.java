package loon.utils.processes;

import loon.LRelease;
import loon.event.Updateable;
import loon.geom.BooleanValue;
import loon.utils.LIterator;
import loon.utils.SortedList;
import loon.utils.TimeUtils;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public class WaitProcess implements GameProcess, LRelease {

	protected boolean isDead = false, isAutoKill = true;

	protected final String id;

	private LTimer timer;

	private RealtimeProcessHost processHost;

	private SortedList<GameProcess> processesToFireWhenFinished;

	private Updateable update;

	private BooleanValue value = new BooleanValue(false);

	private RealtimeProcess _waitProcess;

	public WaitProcess(Updateable update) {
		this("Process" + TimeUtils.millis(), 60, update);
	}

	public WaitProcess(long delay, Updateable update) {
		this("Process" + TimeUtils.millis(), delay, update);
	}

	public WaitProcess(String id, long delay, Updateable update) {
		this.timer = new LTimer(delay);
		this.isDead = false;
		this.isAutoKill = true;
		this.id = id;
		this.update = update;
	}

	public boolean completed() {
		return value.result();
	}

	public BooleanValue get() {
		return value;
	}

	@Override
	public void setProcessHost(RealtimeProcessHost processHost) {
		this.processHost = processHost;
	}

	public void fireThisWhenFinished(GameProcess realtimeProcess) {
		if (this.processesToFireWhenFinished == null) {
			this.processesToFireWhenFinished = new SortedList<GameProcess>();
		}
		this.processesToFireWhenFinished.add(realtimeProcess);
	}

	public WaitProcess wait(RealtimeProcess process) {
		this._waitProcess = process;
		return this;
	}

	@Override
	public void tick(LTimerContext time) {
		if (timer.action(time)) {
			if (update != null) {
				if (!(_waitProcess != null && !_waitProcess.isDead)) {
					update.action(this);
					if (isAutoKill) {
						kill();
					}
				}
			}
		}
	}

	@Override
	public void kill() {
		this.isDead = true;
	}

	@Override
	public boolean isDead() {
		return this.isDead;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void finish() {
		if (!this.isDead) {
			kill();
		}
		if (this.processesToFireWhenFinished != null) {
			for (LIterator<GameProcess> it = this.processesToFireWhenFinished.listIterator(); it.hasNext();) {
				RealtimeProcessManager.get().addProcess(it.next());
			}
		}
		if (this.processHost != null) {
			this.processHost.processFinished(this.id, this);
		}
		value.set(true);
	}

	public boolean isAutoKill() {
		return isAutoKill;
	}

	public void setAutoKill(boolean isAutoKill) {
		this.isAutoKill = isAutoKill;
	}

	@Override
	public void close() {
		finish();
	}

}