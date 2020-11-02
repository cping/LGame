package org.loon.framework.javase.game.core.timer;

/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class NanoTimer extends SystemTimer {

	private static final int NUM_TIMERS = 8;

	private static final long ONE_SEC = 1000000000L;

	private static final long MAX_DIFF = ONE_SEC;

	private static final long NEVER_USED = -1;

	private static final long DEFAULT_FAIL_RESET_TIME = ONE_SEC;

	private long[] lastTimeStamps = new long[NUM_TIMERS];

	private long[] timeSinceLastUsed = new long[NUM_TIMERS];

	private long virtualNanoTime;

	private int timesInARowNewTimerChosen;

	private long lastDiff;

	private long failTime;

	private long failResetTime;

	public NanoTimer() {
		virtualNanoTime = 0;
		failResetTime = DEFAULT_FAIL_RESET_TIME;
		reset();
	}

	private void reset() {
		failTime = 0;
		lastDiff = 0;
		timesInARowNewTimerChosen = 0;
		for (int i = 0; i < NUM_TIMERS; i++) {
			timeSinceLastUsed[i] = NEVER_USED;
		}
	}

	private long nanoTime() {
		long diff;

		if (timesInARowNewTimerChosen >= NUM_TIMERS) {
			long nanoTime = System.currentTimeMillis() * 1000000;
			diff = nanoTime - lastTimeStamps[0];
			failTime += diff;
			if (failTime >= failResetTime) {
				reset();
				failResetTime *= 2;
			}
		} else {
			long nanoTime = System.nanoTime();
			int bestTimer = -1;
			long bestDiff = 0;
			for (int i = 0; i < NUM_TIMERS; i++) {
				if (timeSinceLastUsed[i] != NEVER_USED) {
					long t = lastTimeStamps[i] + timeSinceLastUsed[i];
					long timerDiff = nanoTime - t;
					if (timerDiff > 0 && timerDiff < MAX_DIFF) {
						if (bestTimer == -1 || timerDiff < bestDiff) {
							bestTimer = i;
							bestDiff = timerDiff;
						}
					}
				}
			}

			if (bestTimer == -1) {
				diff = lastDiff;
				bestTimer = 0;
				for (int i = 0; i < NUM_TIMERS; i++) {
					if (timeSinceLastUsed[i] == NEVER_USED) {
						bestTimer = i;
						break;
					} else if (timeSinceLastUsed[i] > timeSinceLastUsed[bestTimer]) {
						bestTimer = i;
					}
				}
				timesInARowNewTimerChosen++;
			} else {
				timesInARowNewTimerChosen = 0;
				failResetTime = DEFAULT_FAIL_RESET_TIME;
				diff = nanoTime - lastTimeStamps[bestTimer]
						- timeSinceLastUsed[bestTimer];

				if (timeSinceLastUsed[bestTimer] == 0) {
					lastDiff = diff;
				}
			}

			lastTimeStamps[bestTimer] = nanoTime;
			timeSinceLastUsed[bestTimer] = 0;

			for (int i = 0; i < NUM_TIMERS; i++) {
				if (i != bestTimer && timeSinceLastUsed[i] != NEVER_USED) {
					timeSinceLastUsed[i] += diff;
				}
			}

			if (timesInARowNewTimerChosen >= NUM_TIMERS) {
				lastTimeStamps[0] = System.currentTimeMillis() * 1000000;
			}
		}

		virtualNanoTime += diff;

		return virtualNanoTime;
	}

	public long getTimeMillis() {
		return nanoTime() / 1000000;
	}

	public long getTimeMicros() {
		return nanoTime() / 1000;
	}
}
