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

import loon.LSystem;
import loon.Save;
import loon.Session;
import loon.utils.TArray;

public class SDLKeyBind {

	private static final TArray<SDLKeyBind> KEY_LIST = new TArray<SDLKeyBind>();

	public static SDLKeyBind add(String name, SDLKeybindValue defaultValue, String category) {
		return new SDLKeyBind(name, defaultValue, category);
	}

	public static SDLKeyBind add(String name, SDLKeybindValue defaultValue) {
		return new SDLKeyBind(name, defaultValue, null);
	}

	public static void resetAll() {
		for (SDLKeyBind def : KEY_LIST) {
			def.resetToDefault();
		}
	}

	private final String _name;
	private final SDLKeybindValue _defaultValue;
	private final String _category;
	private SDLAxis _value;

	protected SDLKeyBind(String name, SDLKeybindValue defaultValue, String category) {
		this._name = name;
		this._defaultValue = defaultValue;
		this._category = category;
		this._value = defaultValue instanceof SDLAxis ? (SDLAxis) defaultValue : new SDLAxis((SDLKey) defaultValue);

		KEY_LIST.add(this);

		load();
	}

	String defaultKeyName() {
		return "sdl-default-keyboard-" + _name;
	}

	public void save() {
		final String name = defaultKeyName();
		final Session session = new Session(name);
		if (session.isPersisted()) {
			session.set(name + "-single", _value.key != null);
			if (_value.key != null) {
				session.set(name + "-key", _value.key._index);
			} else {
				session.set(name + "-min", _value.min._index);
				session.set(name + "-max", _value.max._index);
			}
			session.save();
		}
	}

	public void load() {
		final String name = defaultKeyName();
		final Session session = new Session(name);
		SDLAxis loaded;
		if (session.isPersisted()) {
			if (session.getBooleanV(name + "-single", true)) {
				SDLKey key = SDLKey.byOrdinal(session.getIntV(name + "-key", SDLKey.unset.getIndex()));
				loaded = key == SDLKey.unset ? null : new SDLAxis(key);
			} else {
				SDLKey min = SDLKey.byIndex(session.getIntV(name + "-min", SDLKey.unset.getIndex()));
				SDLKey max = SDLKey.byIndex(session.getIntV(name + "-max", SDLKey.unset.getIndex()));
				loaded = min == SDLKey.unset || max.equals(SDLKey.unset) ? null : new SDLAxis(min, max);
			}
			if (loaded != null) {
				_value = loaded;
			}
		}
	}

	public boolean isDefault() {
		if (_defaultValue instanceof SDLAxis) {
			if (((SDLAxis) _defaultValue).min == null) {
				return ((SDLAxis) _defaultValue).key.equals(_value.key);
			} else {
				return ((SDLAxis) _defaultValue).max.equals(_value.max)
						&& ((SDLAxis) _defaultValue).min.equals(_value.min);
			}
		} else {
			return _defaultValue.equals(_value.key);
		}
	}

	public void resetToDefault() {
		String name = defaultKeyName();
		final Session session = new Session(name);
		if (session.isPersisted()) {
			session.remove(name + "-single");
			session.remove(name + "-key");
			session.remove(name + "-min");
			session.remove(name + "-max");
			if (_defaultValue instanceof SDLAxis) {
				if (((SDLAxis) _defaultValue).min == null) {
					_value = new SDLAxis(((SDLAxis) _defaultValue).key);
				} else {
					_value = new SDLAxis(((SDLAxis) _defaultValue).min, ((SDLAxis) _defaultValue).max);
				}
			} else {
				_value = new SDLAxis((SDLKey) _defaultValue);
			}
		}
	}

}
