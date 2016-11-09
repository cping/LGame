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
package loon.utils;

public final class TimeUtils {

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
		long timeMillis = System.currentTimeMillis();
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

}
