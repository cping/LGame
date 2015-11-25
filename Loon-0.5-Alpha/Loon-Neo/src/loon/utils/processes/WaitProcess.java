package loon.utils.processes;

import loon.geom.BooleanValue;
import loon.utils.LIterator;
import loon.utils.SortedList;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public class WaitProcess implements GameProcess {

	public interface WaitEvent {
		public void action(WaitProcess process);
	}

	protected boolean isDead;

	protected final String id;

	private LTimer timer;

	private RealtimeProcessHost processHost;

	private SortedList<GameProcess> processesToFireWhenFinished;

	private WaitEvent update;

	private BooleanValue value = new BooleanValue(false);

	public WaitProcess(long delay, WaitEvent update) {
		this("Process" + System.currentTimeMillis(), delay, update);
	}

	public WaitProcess(String id, long delay, WaitEvent update) {
		this.timer = new LTimer(delay);
		this.isDead = false;
		this.id = id;
		this.update = update;
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

	@Override
	public void tick(LTimerContext time) {
		if (timer.action(time)) {
			if (update != null) {
				update.action(this);
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
			for (LIterator<GameProcess> it = this.processesToFireWhenFinished
					.listIterator(); it.hasNext();) {
				RealtimeProcessManager.get().addProcess(it.next());
			}
		}
		if (this.processHost != null) {
			this.processHost.processFinished(this.id, this);
		}
		value.set(true);
	}
}