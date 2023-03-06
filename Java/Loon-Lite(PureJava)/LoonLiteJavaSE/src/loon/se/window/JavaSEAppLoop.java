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
package loon.se.window;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import loon.utils.MathUtils;
import loon.utils.TimeUtils;

public class JavaSEAppLoop extends Thread {

	private final Lock _lock = new ReentrantLock();

	private long _totalTicks;

	private int _tickRate;

	private float _deltaTime;
	private float _processTime;
	private float _delayException;

	private JavaSELoop _loop;

	public JavaSEAppLoop(final JavaSELoop loop, final int fps) {
		this._loop = loop;
		this._tickRate = fps;
	}

	@Override
	public void run() {
		for (; _loop.get();) {
			++this._totalTicks;
			Lock threadLock = this.getLock();
			threadLock.lock();
			try {
				_loop.process();
			} finally {
				threadLock.unlock();
			}
			float sleep;
			try {
				sleep = this.sleep();
			} catch (InterruptedException e) {
				break;
			}
			this._deltaTime = (long) (sleep + this._processTime);
		}
	}

	public long toTicks(final int milliseconds) {
		return this.toTicks(milliseconds, getTickRate());
	}

	public long toTicks(final int milliseconds, int updateRate) {
		return (long) (updateRate / 1000f * milliseconds);
	}

	protected long toDelayTime(long start) {
		return ((TimeUtils.nanoTime() - start) / 1000000L);
	}

	public JavaSEAppLoop terminate() {
		this.interrupt();
		try {
			this.join();
		} catch (InterruptedException ex) {
		}
		return this;
	}

	public void close() {
		this.terminate();
	}

	public long getTicks() {
		return this._totalTicks;
	}

	public int getTickRate() {
		return this._tickRate;
	}

	public float getDeltaTime() {
		return this._deltaTime;
	}

	public float getProcessTime() {
		return this._processTime;
	}

	public JavaSEAppLoop setTickRate(int tickRate) {
		this._tickRate = tickRate;
		return this;
	}

	protected long getExpectedDelta() {
		return (long) (1000f / this._tickRate);
	}

	protected float sleep() throws InterruptedException {
		float delay = MathUtils.max(0, this.getExpectedDelta() - this.getProcessTime());
		long sleepDelay = MathUtils.round(delay);
		this._delayException += delay - sleepDelay;
		if (MathUtils.abs(this._delayException) > 1) {
			long exceptionAdjustment = (long) this._delayException;
			sleepDelay += exceptionAdjustment;
			this._delayException -= exceptionAdjustment;
		}
		if (delay > 0) {
			sleep(sleepDelay);
		}
		return delay;
	}

	public Lock getLock() {
		return this._lock;
	}

}
