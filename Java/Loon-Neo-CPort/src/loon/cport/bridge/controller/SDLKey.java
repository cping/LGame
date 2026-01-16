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
package loon.cport.bridge.controller;

import loon.LSysException;
import loon.LSystem;
import loon.utils.TArray;

public class SDLKey implements SDLKeybindValue {

	public enum KeyType {
		key, mouse, controller, scroll
	}

	private final static TArray<SDLKey> _numbers = new TArray<SDLKey>();

	private final static TArray<SDLKey> _tempKeys = new TArray<SDLKey>();

	private static int _KEY_INDEX_COUNT = 0;

	// key
	public final static SDLKey controllerA = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller);
	public final static SDLKey controllerB = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller);
	public final static SDLKey controllerX = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller);
	public final static SDLKey controllerY = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller);
	public final static SDLKey controllerGuide = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller);
	public final static SDLKey controllerLBumper = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller);
	public final static SDLKey controllerBack = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller);
	public final static SDLKey controllerStart = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller);
	public final static SDLKey controllerLStick = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller);
	public final static SDLKey controllerRStick = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller);
	public final static SDLKey controllerdPadUp = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller);
	public final static SDLKey controllerdPadDown = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller);
	public final static SDLKey controllerdPadLeft = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller);
	public final static SDLKey controllerdPadRight = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller);
	public final static SDLKey controllerLTrigger = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller, true);
	public final static SDLKey controllerRTrigger = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller, true);
	public final static SDLKey controllerLStickYAxis = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller, true);
	public final static SDLKey controllerLStickXAxis = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller, true);
	public final static SDLKey controllerRStickYAxis = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller, true);
	public final static SDLKey controllerRStickXAxis = new SDLKey(_KEY_INDEX_COUNT++, KeyType.controller, true);
	// mouse
	public final static SDLKey mouseLeft = new SDLKey(_KEY_INDEX_COUNT++, KeyType.mouse);
	public final static SDLKey mouseRight = new SDLKey(_KEY_INDEX_COUNT++, KeyType.mouse);
	public final static SDLKey mouseMiddle = new SDLKey(_KEY_INDEX_COUNT++, KeyType.mouse);
	public final static SDLKey mouseBack = new SDLKey(_KEY_INDEX_COUNT++, KeyType.mouse);
	public final static SDLKey mouseForward = new SDLKey(_KEY_INDEX_COUNT++, KeyType.mouse);
	// scroll
	public final static SDLKey scroll = new SDLKey(_KEY_INDEX_COUNT++, KeyType.mouse, true);
	// keyboard
	public final static SDLKey anyKey = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey num0 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey num1 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey num2 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey num3 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey num4 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey num5 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey num6 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey num7 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey num8 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey num9 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey a = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey altLeft = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey altRight = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey apostrophe = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey at = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey b = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey back = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey backslash = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey c = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey call = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey camera = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey clear = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey comma = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey d = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey del = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey backspace = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey forwardDel = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey dpadCenter = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey dpadDown = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey dpadLeft = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey dpadRight = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey dpadUp = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey center = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey down = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey left = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey right = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey up = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey e = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey endcall = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey enter = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey envelope = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey equals = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey explorer = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey f = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey focus = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey g = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey backtick = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey h = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey headsetHook = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey home = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey i = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey j = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey k = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey l = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey leftBracket = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey m = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey mediaFastForward = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey mediaNext = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey mediaPlayPause = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey mediaPrevious = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey mediaRewind = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey mediaStop = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey menu = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey minus = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey mute = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey n = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey notification = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey num = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey o = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey p = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey period = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey plus = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey pound = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey power = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey q = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey r = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey rightBracket = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey s = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey search = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey semicolon = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey shiftLeft = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey shiftRight = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey slash = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey softLeft = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey softRight = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey space = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey star = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey sym = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey t = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey tab = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey u = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey unknown = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey v = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey volumeDown = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey volumeUp = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey w = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey x = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey y = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey z = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey metaAltLeftOn = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey metaAltOn = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey metaAltRightOn = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey metaShiftLeftOn = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey metaShiftOn = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey metaShiftRightOn = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey metaSymOn = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey controlLeft = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey controlRight = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey escape = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey end = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey insert = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey pageUp = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey pageDown = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey pictSymbols = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey switchCharset = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey buttonCircle = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey buttonA = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey buttonB = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey buttonC = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey buttonX = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey buttonY = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey buttonZ = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey buttonL1 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey buttonR1 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey buttonL2 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey buttonR2 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey buttonThumbL = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey buttonThumbR = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey buttonStart = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey buttonSelect = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey buttonMode = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey numpad0 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey numpad1 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey numpad2 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey numpad3 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey numpad4 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey numpad5 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey numpad6 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey numpad7 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey numpad8 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey numpad9 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey colon = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey f1 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey f2 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey f3 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey f4 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey f5 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey f6 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey f7 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey f8 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey f9 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey f10 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey f11 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey f12 = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey unset = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey application = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey asterisk = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey capsLock = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey pause = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey printScreen = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);
	public final static SDLKey scrollLock = new SDLKey(_KEY_INDEX_COUNT++, KeyType.key);

	public final static TArray<SDLKey> getKeys() {
		return _tempKeys;
	}

	public final static TArray<SDLKey> getNumbers() {
		if (_numbers.size == 0) {
			_numbers.add(num0);
			_numbers.add(num1);
			_numbers.add(num2);
			_numbers.add(num3);
			_numbers.add(num4);
			_numbers.add(num5);
			_numbers.add(num6);
			_numbers.add(num7);
			_numbers.add(num8);
			_numbers.add(num9);
		}
		return _numbers;
	}

	public static int getKeyCount() {
		return _KEY_INDEX_COUNT;
	}

	public static SDLKey getIndex(int id) {
		if (id < 0 || id >= _tempKeys.size) {
			throw new LSysException("Invalid key code: " + id);
		}
		return _tempKeys.get(id);
	}

	protected final int _index;
	protected final KeyType _type;
	protected final boolean _axis;

	SDLKey(int idx, KeyType type) {
		this(idx, type, false);
	}

	SDLKey(int idx, KeyType type, boolean axis) {
		this._index = idx;
		this._type = type;
		this._axis = axis;
		_tempKeys.add(this);
	}

	public int getIndex() {
		return _index;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = LSystem.unite(result, _index);
		result = LSystem.unite(result, _type);
		result = LSystem.unite(result, _axis);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SDLKey key = (SDLKey) o;
		return key._index == _index && key._type == _type && key._axis == _axis;
	}

	@Override
	public String toString() {
		return getKeyTypeName();
	}

	@Override
	public String getKeyTypeName() {
		return _index + ":" + _type;
	}

}
