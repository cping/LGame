/**
 * Copyright 2008 - 2016 The Loon Game Engine Authors
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
package loon.utils;

public class TimeUtils {

	private static final long nanosPerMilli = 1000000L;

	public static enum Unit {
		NANOS, MICROS, MILLIS, SECONDS
	}

	private TimeUtils() {
	}

	public static float currentNanos() {
		return currentMicros() * 1000f;
	}

	public static float currentMicros() {
		return currentMillis() * 1000f;
	}

	public static float currentMillis() {
		return currentSeconds() * 1000f;
	}

	public static float currentSeconds() {
		long timeMillis = millis();
		return timeMillis / 1000f;
	}

	public static float currentTime(Unit unit) {
		switch (unit) {
		case NANOS:
			return currentNanos();
		case MICROS:
			return currentMicros();
		case MILLIS:
			return currentMillis();
		default:
			return currentSeconds();
		}
	}

	public static float currentTime() {
		return currentTime(getDefaultTimeUnit());
	}

	public static float convert(float time, Unit source, Unit target) {
		if (source == target)
			return time;

		float factor = 1;

		if (source == Unit.SECONDS) {
			if (target == Unit.MILLIS)
				factor = 1000f;
			else if (target == Unit.MICROS)
				factor = 1000000f;
			else
				factor = 1000000000f;
		} else if (source == Unit.MILLIS) {
			if (target == Unit.SECONDS)
				factor = 1f / 1000f;
			else if (target == Unit.MICROS)
				factor = 1000f;
			else
				factor = 1000000f;
		} else if (source == Unit.MICROS) {
			if (target == Unit.SECONDS)
				factor = 1f / 1000000f;
			else if (target == Unit.MILLIS)
				factor = 1f / 1000f;
			else
				factor = 1000f;
		} else {
			if (target == Unit.SECONDS)
				factor = 1f / 1000000000f;
			else if (target == Unit.MILLIS)
				factor = 1f / 1000000f;
			else if (target == Unit.MICROS)
				factor = 1f / 1000f;
		}

		return time * factor;
	}

	public static Unit getDefaultTimeUnit() {
		return Unit.SECONDS;
	}

	public final static long nanoTime() {
		return System.currentTimeMillis() * nanosPerMilli;
	}

	public final static long millis() {
		return System.currentTimeMillis();
	}

	public final static long nanosToMillis(long nanos) {
		return nanos / nanosPerMilli;
	}

	public final static long millisToNanos(long millis) {
		return millis * nanosPerMilli;
	}

	public final static long timeSinceNanos(long prevTime) {
		return nanoTime() - prevTime;
	}

	public final static long timeSinceMillis(long prevTime) {
		return millis() - prevTime;
	}

	public final static String formatTime(long time) {
		int steps = 0;
		while (time >= 1000) {
			time /= 1000;
			steps++;
		}
		return time + getTimeUnit(steps);
	}

	private static String getTimeUnit(int steps) {
		switch (steps) {
		case 0:
			return "ns";
		case 1:
			return "us";
		case 2:
			return "ms";
		case 3:
			return "s";
		case 4:
			return "m";
		case 5:
			return "h";
		case 6:
			return "days";
		case 7:
			return "months";
		case 8:
			return "years";
		default:
			return "d (WTF dude check you calculation!)";
		}
	}

}
