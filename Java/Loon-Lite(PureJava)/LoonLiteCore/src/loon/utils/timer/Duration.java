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

import loon.LSystem;
import loon.utils.Calculator;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.TimeUtils;

/**
 * 一个时间管理用类,支持简单的时间换算,用于统一Loon的时间单位关系与数值,初始单位是ms,毫秒
 * 
 * <pre>
 * Duration.at(1000).toSeconds();
 * </pre>
 */
public class Duration implements Comparable<Duration> {

	protected static Duration _instance = null;

	public static void freeStatic() {
		_instance = null;
	}

	public static Duration get() {
		return shared();
	}

	public static Duration shared() {
		synchronized (Duration.class) {
			if (_instance == null) {
				_instance = new Duration();
			}
			return _instance;
		}
	}

	public static final Duration ZERO = new Duration(0f);

	public static final Duration HALF_ONE = new Duration(0.5f);

	public static final Duration ONE = new Duration(1f);

	public static final Duration ONE_SECOND = new Duration(LSystem.SECOND);

	public static final Duration ONE_MINUTE = new Duration(LSystem.MINUTE);

	public static final Duration ONE_HOUR = new Duration(LSystem.HOUR);

	public static final Duration ONE_DAY = new Duration(LSystem.DAY);

	public final static boolean isBetween(long time, long from, long end) {
		return from <= time && time <= end;
	}

	public final static long now() {
		return TimeUtils.millis();
	}

	public final static long ofS(float sec) {
		return MathUtils.floor(sec * LSystem.SECOND);
	}

	public final static long ofM(float min) {
		return MathUtils.floor(min * LSystem.MINUTE);
	}

	public final static long ofH(float hour) {
		return MathUtils.floor(hour * LSystem.HOUR);
	}

	public final static long ofD(float day) {
		return MathUtils.floor(day * LSystem.DAY);
	}

	public final static float toS(long millis) {
		return (float) millis / (float) LSystem.SECOND;
	}

	public final static float toM(long millis) {
		return (float) millis / (float) LSystem.MINUTE;
	}

	public final static float toH(long millis) {
		return (float) millis / (float) LSystem.HOUR;
	}

	public final static float toD(long millis) {
		return (float) millis / (float) LSystem.DAY;
	}

	public final static Duration at(float ms) {
		return new Duration(ms);
	}

	public final static Duration atSecond(float sec) {
		return new Duration(ofS(sec));
	}

	public final static Duration atMinute(float min) {
		return new Duration(ofM(min));
	}

	public final static Duration atHour(float hour) {
		return new Duration(ofH(hour));
	}

	public final static Duration atDay(float day) {
		return new Duration(ofD(day));
	}

	private float _millisTime;

	public Duration() {
		this(0f);
	}

	public Duration(float ms) {
		set(ms);
	}

	public Duration set(float ms) {
		long year = 100 * LSystem.YEAR;
		if (ms < -year) {
			this._millisTime = -year;
		} else if (Float.isNaN(ms)) {
			this._millisTime = 0;
		} else if (ms == NumberUtils.intBitsToFloat(0x7f800000)) {
			this._millisTime = 0;
		} else if (ms > year) {
			this._millisTime = year;
		} else {
			this._millisTime = ms;
		}
		return this;
	}

	public LTimer toTime() {
		return LTimer.at(this);
	}

	public Calculator calc() {
		return new Calculator(_millisTime);
	}

	public Duration add(float millis) {
		return set(_millisTime + millis);
	}

	public Duration add(Duration other) {
		if (other == null) {
			return this;
		}
		return set(_millisTime + other._millisTime);
	}

	public Duration sub(float millis) {
		return set(_millisTime - millis);
	}

	public Duration sub(Duration other) {
		if (other == null) {
			return this;
		}
		return set(_millisTime - other._millisTime);
	}

	public Duration mul(float millis) {
		return set(_millisTime * millis);
	}

	public Duration mul(Duration other) {
		if (other == null) {
			return this;
		}
		return set(_millisTime * other._millisTime);
	}

	public Duration div(float millis) {
		return set(_millisTime / millis);
	}

	public Duration div(Duration other) {
		if (other == null) {
			return this;
		}
		return set(_millisTime / other._millisTime);
	}

	public boolean lessThan(Duration other) {
		if (other == null) {
			return false;
		}
		return _millisTime < other._millisTime;
	}

	public boolean lessThanOrEquals(Duration other) {
		if (other == null) {
			return false;
		}
		return _millisTime <= other._millisTime;
	}

	public boolean lessEquals(Duration other) {
		if (other == null) {
			return false;
		}
		return _millisTime == other._millisTime;
	}

	public boolean greaterThan(Duration other) {
		if (other == null) {
			return true;
		}
		return _millisTime > other._millisTime;
	}

	public boolean greaterThanOrEquals(Duration other) {
		if (other == null) {
			return true;
		}
		return _millisTime >= other._millisTime;
	}

	public Duration negate() {
		return set(-_millisTime);
	}

	public Duration millis(float ms) {
		return set(ms);
	}

	public float toMillis() {
		return _millisTime;
	}

	public long getMillis() {
		return toMillisLong();
	}

	public long toMillisLong() {
		int bit = MathUtils.getFloatDotBackSize(_millisTime);
		long numBits = 0l;
		if (bit < 1) {
			numBits = 1l;
		} else if (bit < 2) {
			numBits = 10l;
		} else if (bit < 3) {
			numBits = 100l;
		} else if (bit < 4) {
			numBits = 1000l;
		} else if (bit < 5) {
			numBits = 10000l;
		} else if (bit < 6) {
			numBits = 100000l;
		} else if (bit < 7) {
			numBits = 1000000l;
		} else if (bit < 8) {
			numBits = 10000000l;
		} else if (bit < 9) {
			numBits = 100000000l;
		} else if (bit < 10) {
			numBits = 1000000000l;
		} else if (bit < 11) {
			numBits = 10000000000l;
		} else {
			// float显示不了那么多位,判断不判断都一样,游戏精度也没必要到double……
		}
		return (long) (numBits * _millisTime);
	}

	public Duration seconds(float s) {
		return set(s / (float) LSystem.SECOND);
	}

	public float toSeconds() {
		return _millisTime / (float) LSystem.SECOND;
	}

	public Duration minute(float m) {
		return set(m / (float) LSystem.MINUTE);
	}

	public float toMinute() {
		return _millisTime / (float) LSystem.MINUTE;
	}

	public Duration hours(float h) {
		return set(h / (float) LSystem.HOUR);
	}

	public float toHours() {
		return _millisTime / (float) LSystem.HOUR;
	}

	public Duration day(float d) {
		return set(d / (float) LSystem.DAY);
	}

	public float toDay() {
		return _millisTime / (float) LSystem.DAY;
	}

	public Duration week(float w) {
		return set(w / (float) LSystem.WEEK);
	}

	public float toWeek() {
		return _millisTime / (float) LSystem.WEEK;
	}

	public Duration year(float y) {
		return set(y / (float) LSystem.YEAR);
	}

	public float toYear() {
		return _millisTime / (float) LSystem.YEAR;
	}

	public String formatTime(String format) {
		return formatTime(this, format);
	}

	public String formatTime() {
		return formatTime(":");
	}

	protected static String formatTime(Duration timer, String format) {
		String str = LSystem.EMPTY;
		int minute = MathUtils.floor(timer.toMinute());
		if (minute < 10) {
			str = "0" + minute;
		} else {
			str = LSystem.EMPTY + minute;
		}
		str += format;
		int second = MathUtils.floor(timer.toSeconds() % 60f);
		if (second < 10) {
			str += "0" + second;
		} else {
			str += LSystem.EMPTY + second;
		}
		return str;
	}

	public boolean isLeast(final Duration other) {
		if (other == null) {
			return false;
		}
		return _millisTime >= other._millisTime;
	}

	public boolean isLessThan(final Duration other) {
		return !isLeast(other);
	}

	public boolean isNone() {
		return MathUtils.equal(0f, _millisTime);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Duration)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		return MathUtils.equal(((Duration) obj)._millisTime, this._millisTime);
	}

	@Override
	public int compareTo(Duration o) {
		if (o == null) {
			return 1;
		}
		return NumberUtils.compare(_millisTime, o._millisTime);
	}

	@Override
	public int hashCode() {
		int hashCode = 31;
		hashCode = LSystem.unite(hashCode, _millisTime);
		return hashCode;
	}

	@Override
	public String toString() {
		return MathUtils.toString(_millisTime, true) + " ms";
	}

}
