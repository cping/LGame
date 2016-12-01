package loon.utils.timer;

import loon.LSystem;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;

/**
 * 倒计时处理器(比如勇者30之类需要倒数计算时间的游戏会用到)
 */
public class CountdownTimer extends RealtimeProcess {

	// 因为考虑web平台的关系，所以毫秒精确不到小数点后3位，只能2位……（gwt没有实现精确的nanoTime,获取3位最后一个数也会是0）
	private final static String DEF_FORMAT = "00{0}00";

	private final static String SEPARATOR_FORMAT = ":";

	private String _separator = SEPARATOR_FORMAT;

	private int _second = 0;

	private long _millisecond = 0;

	private String _result = DEF_FORMAT;

	private boolean _finished;

	public CountdownTimer() {
		this(0);
	}

	public CountdownTimer(int second) {
		super(0);
		this.set(second);
	}

	public void add(int second) {
		this._millisecond += second * LSystem.SECOND;
	}

	public void set(int second) {
		this._second = second;
		this._millisecond = second * LSystem.SECOND;
		this._finished = false;
	}

	@Override
	public void kill() {
		super.kill();
		this._finished = true;
	}

	public int startSecond() {
		return _second;
	}

	public String getTime() {
		return nowSecond();
	}

	public long getMillisecondLong() {
		return _millisecond;
	}

	public float getMillisecond() {
		return MathUtils.min((float) _millisecond / 1000f, 0);
	}

	public String nowSecond() {
		if (_result == null) {
			return StringUtils.format(DEF_FORMAT, _separator);
		}
		String text = String.valueOf(_millisecond);
		if (text.length() > 4) {
			return text.substring(0, 2) + _separator + text.substring(2, 4);
		} else {
			return StringUtils.format(DEF_FORMAT, _separator);
		}
	}

	public void play() {
		play(this._second);
	}

	public void play(int second) {
		synchronized (this) {
			this.set(second);
			RealtimeProcessManager manager = RealtimeProcessManager.get();
			manager.delete(getId());
			super.isDead = false;
			manager.addProcess(this);
		}
	}

	public boolean isCompleted() {
		return _finished;
	}

	@Override
	public void run(LTimerContext time) {
		_millisecond -= MathUtils.min(time.getTimeSinceLastUpdate(), 60f);
		if (_millisecond <= 0) {
			kill();
		}
	}
}
