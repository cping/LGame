/**
 * Copyright 2008 - 2023 The Loon Game Engine Authors
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
package loon.utils.processes;

import loon.utils.MathUtils;
import loon.utils.reply.ObjRef;
import loon.utils.timer.Duration;

public class WaitCoroutine {

	private enum WaitType {
		Frames, Time,
	}

	public static WaitCoroutine empty() {
		return seconds(0f);
	}

	public static WaitCoroutine frames(int frames) {
		return new WaitCoroutine(frames, WaitType.Frames);
	}

	public static WaitCoroutine seconds(float seconds) {
		return new WaitCoroutine(seconds, WaitType.Time);
	}

	public static WaitCoroutine frames(int frames, Object t) {
		return new WaitCoroutine(frames, WaitType.Frames, ObjRef.of(t));
	}

	public static WaitCoroutine frames(int frames, ObjRef<?> t) {
		return new WaitCoroutine(frames, WaitType.Frames, t);
	}

	public static WaitCoroutine seconds(float seconds, Object t) {
		return new WaitCoroutine(seconds, WaitType.Time, ObjRef.of(t));
	}

	public static WaitCoroutine seconds(float seconds, ObjRef<?> t) {
		return new WaitCoroutine(seconds, WaitType.Time, t);
	}

	private final WaitType _type;

	private float _internalValue;

	private float _currentValue;

	private ObjRef<?> _tag;

	private WaitCoroutine(float v, WaitType type) {
		this(v, type, null);
	}

	private WaitCoroutine(float v, WaitType type, ObjRef<?> t) {
		switch (type) {
		case Frames:
			this._internalValue = v;
			break;
		case Time:
			this._internalValue = Duration.ofS(v);
			break;
		}

		this._currentValue = _internalValue;
		this._type = type;
		this._tag = t;
	}

	public ObjRef<?> getRef() {
		return _tag;
	}

	protected void update(long millis) {
		switch (this._type) {
		case Frames:
			this._currentValue -= 1f;
			break;
		case Time:
			this._currentValue -= MathUtils.max(16, millis);
			break;
		}
	}

	public WaitCoroutine reset() {
		this._currentValue = this._internalValue;
		this._tag = null;
		return this;
	}

	public boolean isCompleted() {
		return this._currentValue <= 0f;
	}
}
