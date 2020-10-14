/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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

import loon.LSystem;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;
import loon.utils.processes.GameProcessType;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;

/**
 * 倒计时处理器(比如勇者30之类需要倒数计算时间的游戏会用到)
 */
public class CountdownTimer extends RealtimeProcess {

	// 因为考虑web平台的关系，所以毫秒精确不到小数点后3位，只能2位……（gwt没有实现精确的nanoTime,获取3位最后一个数也会是0）
	private final static String DEF_FORMAT = "s{0}m";

	private final static String SEPARATOR_FORMAT = ":";

	private String _separator = SEPARATOR_FORMAT;

	private String _format = DEF_FORMAT;

	private String _result = DEF_FORMAT;

	private float _second = 0;

	private long _millisecond = 0;

	private int _digits = 2;

	private boolean _finished;

	private boolean _displayMillisecond;

	public CountdownTimer() {
		this(0f, true);
	}

	public CountdownTimer(float second) {
		this(second, true);
	}

	public CountdownTimer(float second, boolean displayMilliSecond) {
		super(0);
		this._digits = 2;
		this.set(second);
		this.setProcessType(GameProcessType.Time);
		this.setDisplayMilliSecond(displayMilliSecond);
	}

	public void add(float second) {
		this._millisecond += (second * LSystem.SECOND);
	}

	public void set(float second) {
		this._second = second;
		this._millisecond = (long) (second * LSystem.SECOND);
		this._finished = false;
	}

	@Override
	public void kill() {
		super.kill();
		this._finished = true;
	}

	public float startSecond() {
		return _second;
	}

	public String getTime() {
		return nowSecond();
	}

	public long getMillisecondLong() {
		return _millisecond;
	}

	public float getMillisecond() {
		return MathUtils.min((float) _millisecond / LSystem.SECOND, 0);
	}

	public long getSecond() {
		return MathUtils.max(_millisecond / LSystem.SECOND, 0);
	}

	protected String formatZeroTimeData() {
		return formatTimeData(null, null);
	}

	protected String formatTimeData(String m, String s) {
		String f = StringUtils.format(_format, _separator);
		if (!StringUtils.isEmpty(m)) {
			this._result = StringUtils.replaceIgnoreCase(f, "s", MathUtils.addZeros(m, _digits));
		} else {
			this._result = StringUtils.replaceIgnoreCase(f, "s", MathUtils.addZeros(0, _digits));
		}
		if (_displayMillisecond) {
			if (!StringUtils.isEmpty(s)) {
				this._result = StringUtils.replaceIgnoreCase(_result, "m", MathUtils.addZeros(s, _digits));
			} else {
				this._result = StringUtils.replaceIgnoreCase(_result, "m", MathUtils.addZeros(0, _digits));
			}
		} else {
			this._result = StringUtils.replaceIgnoreCase(_result, _separator, "");
			this._result = StringUtils.replaceIgnoreCase(_result, "m", "");
		}
		return this._result;
	}

	public String nowSecond() {
		if (StringUtils.isEmpty(_result)) {
			return StringUtils.format(_format, _separator);
		}
		if (_millisecond >= 0) {
			String text = null;
			if (_displayMillisecond) {
				text = String.valueOf(_millisecond);
				final int size = text.length();
				final int len = size - 3;
				if (size > 3) {
					String m = text.substring(0, len);
					String s = text.substring(len, size - 1);
					formatTimeData(m, s);
				} else {
					formatZeroTimeData();
				}
			} else {
				text = String.valueOf(MathUtils.max(0, getSecond()));
				if (text.length() > 0) {
					formatTimeData(text, null);
				} else {
					formatZeroTimeData();
				}
			}
		}
		return this._result;
	}

	public CountdownTimer play() {
		return play(this._second);
	}

	public CountdownTimer play(float second) {
		synchronized (this) {
			this.set(second);
			RealtimeProcessManager manager = RealtimeProcessManager.get();
			manager.delete(getId());
			super.isDead = false;
			manager.addProcess(this);
		}
		return this;
	}

	public boolean isCompleted() {
		return _finished;
	}

	@Override
	public void run(LTimerContext time) {
		_millisecond -= MathUtils.min(time.timeSinceLastUpdate, 60f);
		if (_millisecond <= 0) {
			kill();
		}
	}

	public String getSeparator() {
		return _separator;
	}

	public CountdownTimer setSeparator(String separator) {
		this._separator = separator;
		return this;
	}

	public boolean isDisplayMilliSecond() {
		return _displayMillisecond;
	}

	public CountdownTimer setDisplayMilliSecond(boolean d) {
		this._displayMillisecond = d;
		return this;
	}

	public CountdownTimer resetDefaultFormat() {
		return setFormat(DEF_FORMAT);
	}

	public String getFormat() {
		return _format;
	}

	public CountdownTimer setFormat(String f) {
		this._format = f;
		return this;
	}

	public int getDigits() {
		return _digits;
	}

	public CountdownTimer setDigits(int d) {
		this._digits = d;
		return this;
	}

	public String getResult() {
		return this._result;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("CountdownTimer");
		builder.kv("second", _second).comma().kv("millisecond", _millisecond).comma().kv("result", _result).comma()
				.kv("finished", _finished);
		return builder.toString();
	}

}
