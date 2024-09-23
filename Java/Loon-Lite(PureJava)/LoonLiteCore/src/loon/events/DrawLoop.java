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
package loon.events;

import loon.LRelease;
import loon.geom.BooleanValue;
import loon.opengl.GLEx;

/**
 * 渲染循环器,可以以注入接口的方式改变Screen循环与渲染内容
 */
public class DrawLoop<T> implements DrawListener<T>, LRelease {

	public static interface Drawable {

		public void draw(GLEx g, float px, float py);

	}

	public final BooleanValue enabled = new BooleanValue(true);

	private Drawable _drawable;

	private EventActionTN<T, Long> _eventAction;

	private T _value;

	public DrawLoop(T v, Drawable d) {
		this._value = v;
		this._drawable = d;
	}

	@Override
	public T update(long elapsedTime) {
		if (enabled.get() && _eventAction != null) {
			_eventAction.update(_value, elapsedTime);
		}
		return _value;
	}

	@Override
	public T draw(GLEx g, float x, float y) {
		if (enabled.get() && _drawable != null) {
			_drawable.draw(g, x, y);
		}
		return _value;
	}

	public EventActionTN<T, Long> getEventAction() {
		return _eventAction;
	}

	public DrawLoop<T> setEventAction(EventActionTN<T, Long> e) {
		this._eventAction = e;
		return this;
	}

	public DrawLoop<T> onUpdate(EventActionTN<T, Long> e) {
		return setEventAction(e);
	}

	public DrawLoop<T> onDrawable(DrawLoop.Drawable draw) {
		this._drawable = draw;
		return this;
	}

	public Drawable drawable() {
		return _drawable;
	}

	public T get() {
		return _value;
	}

	@Override
	public void close() {
		enabled.set(false);
		_value = null;
	}

}
