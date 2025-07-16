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

import loon.LSysException;
import loon.LSystem;
import loon.events.EventActionT;
import loon.geom.NumberValue;
import loon.utils.ArrayMap;
import loon.utils.ArrayMap.Entry;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.processes.GameProcessType;
import loon.utils.processes.RealtimeProcess;

/**
 * 时间模拟用类,用来虚拟游戏中的年月日变化,此函数时间流逝只与初始日期设定以及nextTimePass函数调用次数有关
 */
public class SimulationTimer extends RealtimeProcess {

	public static enum MonthType {
		January, February, March, April, May, June, July, August, September, October, November, December
	}

	public final static boolean isLeapYear(int year) {
		return (year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0);
	}

	private StringKeyValue _kvBuilder;

	private ArrayMap _monthDic = new ArrayMap();

	private int _year, _day, _hour;

	private MonthType _month;

	private float _minuteSpeed;

	private float _minute;

	private NumberValue _bindYear;

	private NumberValue _bindMonth;

	private NumberValue _bindDay;

	private NumberValue _bindHour;

	private NumberValue _bindMinute;

	private boolean _dirty;

	private EventActionT<SimulationTimer> _timeEvent;

	public SimulationTimer(int year) {
		this(year, 1, 1);
	}

	public SimulationTimer(int year, int day, int hour) {
		this(year, MonthType.January, day, hour, 0f, 1f);
	}

	public SimulationTimer(int year, MonthType month) {
		this(year, month, 1);
	}

	public SimulationTimer(int year, MonthType month, int day) {
		this(year, month, day, 1);
	}

	public SimulationTimer(int year, MonthType month, int day, int hour) {
		this(year, month, day, hour, 0f, 1f);
	}

	public SimulationTimer(int year, MonthType month, int day, int hour, float minute, float speed) {
		if (year <= 0 || year >= 9999) {
			throw new LSysException("The year number is error , " + year + " is not a calculable year number");
		}
		if (month == null) {
			throw new LSysException("The month cannot be null !");
		}
		_monthDic.put(MonthType.January, 31);
		_monthDic.put(MonthType.February, isLeapYear(year) ? 29 : 28);
		_monthDic.put(MonthType.March, 31);
		_monthDic.put(MonthType.April, 30);
		_monthDic.put(MonthType.May, 31);
		_monthDic.put(MonthType.June, 30);
		_monthDic.put(MonthType.July, 31);
		_monthDic.put(MonthType.August, 31);
		_monthDic.put(MonthType.September, 30);
		_monthDic.put(MonthType.October, 31);
		_monthDic.put(MonthType.November, 30);
		_monthDic.put(MonthType.December, 31);
		this.setYear(year);
		this.setMonth(month);
		this.setDay(day);
		this.setHour(hour);
		this.setMinute(minute);
		this.setMinuteSpeed(speed);
		this.setProcessType(GameProcessType.SimulationTime);
	}

	public boolean isMidnight() {
		return _hour == 24 || (_hour >= 0 && _hour < 6);
	}

	public boolean isMorning() {
		return _hour >= 6 && _hour < 8;
	}

	public boolean isBeforeNoon() {
		return _hour >= 8 && _hour < 12;
	}

	public boolean isAM() {
		return isMidnight() || isMorning() || isBeforeNoon();
	}

	public boolean isNoon() {
		return _hour >= 12 && _hour < 14;
	}

	public boolean isAfterNoon() {
		return _hour >= 14 && _hour < 18;
	}

	public boolean isEvening() {
		return _hour >= 18 && _hour < 24;
	}

	public boolean isPM() {
		return isNoon() || isAfterNoon() || isEvening();
	}

	public MonthType getMonthDaysToNameType(int days) {
		for (int i = 0; i < _monthDic.size(); i++) {
			Entry it = _monthDic.getEntry(i);
			if (it != null && (int) it.getValue() == days) {
				return (MonthType) it.getKey();
			}
		}
		return null;
	}

	public int getMonthNameTypeToInt(MonthType t) {
		int count = 0;
		for (int i = 0; i < _monthDic.size(); i++) {
			Entry it = _monthDic.getEntry(i);
			if (it != null && ((MonthType) it.getKey()).equals(t)) {
				return count;
			}
			count++;
		}
		return -1;
	}

	public MonthType getMonthIntToNameType(int m) {
		final int month = m % 12;
		int count = 0;
		for (int i = 0; i < _monthDic.size(); i++) {
			Entry it = _monthDic.getEntry(i);
			if (it != null && count == month) {
				return (MonthType) it.getKey();
			}
			count++;
		}
		return null;
	}

	public int getMonthDays(MonthType m) {
		if (m == null) {
			return -1;
		}
		return (int) _monthDic.get(m);
	}

	protected void onMinute(float m) {
		_bindMinute.update(m);
	}

	protected void onHour(int h) {
		_bindHour.update(h);
	}

	protected void onDay(int d) {
		_bindDay.update(d);
	}

	protected void onMonth(int m) {
		_bindMonth.set(m);
	}

	protected void onYear(int y) {
		_bindYear.set(y);
	}

	public SimulationTimer setEventAction(EventActionT<SimulationTimer> e) {
		this._timeEvent = e;
		return this;
	}

	public EventActionT<SimulationTimer> getEventAction() {
		return this._timeEvent;
	}

	@Override
	public void run(LTimerContext time) {
		nextTimePass();
	}

	public void nextTimePass() {
		if (_timeEvent != null) {
			_timeEvent.update(this);
		}
		this._minute += _minuteSpeed;
		onMinute(_minute);
		if (_minute > 60) {
			final int timeLoopCount = (int) (_minute / 60);
			for (int i = 0; i < timeLoopCount; i++) {
				this._hour++;
				final int dayAdded = _hour / 24;
				this._hour %= 24;
				if (dayAdded > 0) {
					onDay(dayAdded);
				}
				final int monthDays = getMonthDays(_month) + 1;
				this._day += dayAdded;
				int monthAdded = _day / monthDays;
				this._day %= monthDays;
				if (this._day == 0) {
					this._day = 1;
				}
				if (monthAdded > 0) {
					onMonth(monthAdded);
				}
				final int monthNewDays = getMonthNameTypeToInt(_month) + monthAdded;
				final int yearAdded = monthNewDays / 12;
				MonthType newMonth = getMonthIntToNameType(monthNewDays);
				if (newMonth != null) {
					this._month = newMonth;
				}
				if (yearAdded > 0) {
					onYear(yearAdded);
					this._month = MonthType.January;
				}
				this._year += yearAdded;
			}
			this._minute = 0f;
		}
	}

	public NumberValue getYearBind() {
		return _bindYear;
	}

	public NumberValue getMonthBind() {
		return _bindMonth;
	}

	public NumberValue getDayBind() {
		return _bindDay;
	}

	public NumberValue getHourBind() {
		return _bindHour;
	}

	public NumberValue getMinuteBind() {
		return _bindMinute;
	}

	public int getYear() {
		return _year;
	}

	public int getMonth() {
		return getMonthNameTypeToInt(_month);
	}

	public int getDay() {
		return _day;
	}

	public int getHour() {
		return _hour;
	}

	public float getMinute() {
		return _minute;
	}

	public SimulationTimer setMinute(float m) {
		this._minute = MathUtils.clamp(m, 0, 60);
		if (this._bindMinute == null) {
			this._bindMinute = new NumberValue(_minute);
		} else {
			this._bindMinute.update(_minute);
		}
		this._dirty = true;
		return this;
	}

	public SimulationTimer setMinuteSpeed(float m) {
		this._minuteSpeed = MathUtils.clamp(m, LSystem.MIN_SECONE_SPEED_FIXED, 65535f);
		this._dirty = true;
		return this;
	}

	public SimulationTimer setHour(int h) {
		this._hour = MathUtils.clamp(h, 1, 24);
		if (this._bindHour == null) {
			this._bindHour = new NumberValue(_hour);
		} else {
			this._bindHour.update(_hour);
		}
		this._dirty = true;
		return this;
	}

	public SimulationTimer setDay(int d) {
		this._day = MathUtils.clamp(d, 1, 31);
		if (this._bindDay == null) {
			this._bindDay = new NumberValue(_day);
		} else {
			this._bindDay.update(_day);
		}
		this._dirty = true;
		return this;
	}

	public SimulationTimer setMonth(int m) {
		this._month = getMonthIntToNameType(m = MathUtils.clamp(m, 1, 12));
		if (this._bindMonth == null) {
			this._bindMonth = new NumberValue(m);
		} else {
			this._bindMonth.update(m);
		}
		this._dirty = true;
		return this;
	}

	public SimulationTimer setMonth(MonthType m) {
		this._month = m;
		if (this._bindMonth == null) {
			this._bindMonth = new NumberValue(getMonthNameTypeToInt(m));
		} else {
			this._bindMonth.update(getMonthNameTypeToInt(m));
		}
		this._dirty = true;
		return this;
	}

	public SimulationTimer setYear(int y) {
		this._year = MathUtils.clamp(y, 1, 9999);
		if (this._bindYear == null) {
			this._bindYear = new NumberValue(_year);
		} else {
			this._bindYear.update(_year);
		}
		this._dirty = true;
		return this;
	}

	public boolean isDirty() {
		return this._dirty;
	}

	public SimulationTimer setDirty(boolean d) {
		this._dirty = d;
		return this;
	}

	public SimulationTimer addYear(int y) {
		return setYear(this._year + y);
	}

	public SimulationTimer addMonth(int m) {
		return setMonth(getMonthNameTypeToInt(this._month) + m);
	}

	public SimulationTimer addDay(int d) {
		return setDay(this._day + d);
	}

	public SimulationTimer addHour(int h) {
		return setHour(this._hour + h);
	}

	public SimulationTimer addMinute(int m) {
		return setMinute(this._minute + m);
	}

	public String toData() {
		return toString(LSystem.EMPTY, true);
	}

	public String toString(String name, boolean newLine) {
		if (_kvBuilder == null) {
			_kvBuilder = new StringKeyValue(name);
		} else {
			_kvBuilder.clear();
		}
		if (newLine) {
			_kvBuilder.kv("year", _year).comma().newLine().kv("month", _month).comma().newLine().kv("day", _day).comma()
					.newLine().kv("hour", _hour).comma().newLine().kv("minute", _minute);
		} else {
			_kvBuilder.kv("year", _year).comma().kv("month", _month).comma().kv("day", _day).comma().kv("hour", _hour)
					.comma().kv("minute", _minute);
		}
		return _kvBuilder.toData();
	}

	@Override
	public String toString() {
		return toString("SimulationTimer", true);
	}

}
