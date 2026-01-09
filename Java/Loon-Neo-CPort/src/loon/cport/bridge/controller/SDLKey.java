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
import loon.utils.TArray;

public class SDLKey implements SDLKeybindValue {

	public enum KeyType {
		key, mouse, controller, scroll
	}

	private static final TArray<SDLKey> _numbers = new TArray<SDLKey>();

	private final static TArray<SDLKey> _tempKeys = new TArray<SDLKey>();

	// key
	public final static SDLKey controllerA = new SDLKey(0, KeyType.controller, "A");
	public final static SDLKey controllerB = new SDLKey(1, KeyType.controller, "B");
	public final static SDLKey controllerX = new SDLKey(2, KeyType.controller, "X");
	public final static SDLKey controllerY = new SDLKey(3, KeyType.controller, "Y");
	public final static SDLKey controllerGuide = new SDLKey(4, KeyType.controller, "Guide");
	public final static SDLKey controllerLBumper = new SDLKey(5, KeyType.controller, "L Bumper");
	public final static SDLKey controllerBack = new SDLKey(6, KeyType.controller, "Back");
	public final static SDLKey controllerStart = new SDLKey(7, KeyType.controller, "Start");
	public final static SDLKey controllerLStick = new SDLKey(8, KeyType.controller, "L Stick");
	public final static SDLKey controllerRStick = new SDLKey(9, KeyType.controller, "R Stick");
	public final static SDLKey controllerdPadUp = new SDLKey(10, KeyType.controller, "D-Pad Up");
	public final static SDLKey controllerdPadDown = new SDLKey(11, KeyType.controller, "D-Pad Down");
	public final static SDLKey controllerdPadLeft = new SDLKey(12, KeyType.controller, "D-Pad Left");
	public final static SDLKey controllerdPadRight = new SDLKey(13, KeyType.controller, "D-Pad Right");
	public final static SDLKey controllerLTrigger = new SDLKey(14, KeyType.controller, "L Trigger", true);
	public final static SDLKey controllerRTrigger = new SDLKey(15, KeyType.controller, "R Trigger", true);
	public final static SDLKey controllerLStickYAxis = new SDLKey(16, KeyType.controller, "L Stick Y Axis", true);
	public final static SDLKey controllerLStickXAxis = new SDLKey(17, KeyType.controller, "L Stick X Axis", true);
	public final static SDLKey controllerRStickYAxis = new SDLKey(18, KeyType.controller, "R Stick Y Axis", true);
	public final static SDLKey controllerRStickXAxis = new SDLKey(19, KeyType.controller, "R Stick X Axis", true);
	// mouse
	public final static SDLKey mouseLeft = new SDLKey(20, KeyType.mouse, "Mouse Left");
	public final static SDLKey mouseRight = new SDLKey(21, KeyType.mouse, "Mouse Right");
	public final static SDLKey mouseMiddle = new SDLKey(22, KeyType.mouse, "Mouse Middle");
	public final static SDLKey mouseBack = new SDLKey(23, KeyType.mouse, "Mouse Back");
	public final static SDLKey mouseForward = new SDLKey(24, KeyType.mouse, "Mouse Forward");
	// scroll
	public final static SDLKey scroll = new SDLKey(25, KeyType.mouse, "Scrollwheel", true);
	// keyboard
	public final static SDLKey anyKey = new SDLKey(26, KeyType.key, "Any Key");
	public final static SDLKey num0 = new SDLKey(27, KeyType.key, "0");
	public final static SDLKey num1 = new SDLKey(28, KeyType.key, "1");
	public final static SDLKey num2 = new SDLKey(29, KeyType.key, "2");
	public final static SDLKey num3 = new SDLKey(30, KeyType.key, "3");
	public final static SDLKey num4 = new SDLKey(31, KeyType.key, "4");
	public final static SDLKey num5 = new SDLKey(32, KeyType.key, "5");
	public final static SDLKey num6 = new SDLKey(33, KeyType.key, "6");
	public final static SDLKey num7 = new SDLKey(34, KeyType.key, "7");
	public final static SDLKey num8 = new SDLKey(35, KeyType.key, "8");
	public final static SDLKey num9 = new SDLKey(36, KeyType.key, "9");
	public final static SDLKey a = new SDLKey(37, KeyType.key, "A");
	public final static SDLKey altLeft = new SDLKey(38, KeyType.key, "L-Alt");
	public final static SDLKey altRight = new SDLKey(39, KeyType.key, "R-Alt");

	public final static TArray<SDLKey> getKeys() {
		if (_tempKeys.size == 0) {

		}
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

	public static SDLKey getIndex(int id) {
		if (id < 0 || id >= _tempKeys.size) {
			throw new LSysException("Invalid key code: " + id);
		}
		return _tempKeys.get(id);
	}

	protected final int _index;
	protected final KeyType _type;
	protected final String _value;
	protected final boolean _axis;

	SDLKey(int idx, KeyType type, String value) {
		this(idx, type, value, false);
	}

	SDLKey(int idx, KeyType type, String value, boolean axis) {
		this._index = idx;
		this._type = type;
		this._value = value;
		this._axis = axis;
	}

	public int getIndex() {
		return _index;
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
		return key._index == _index && key._type == _type;
	}

	@Override
	public String toString() {
		return _value;
	}

}
