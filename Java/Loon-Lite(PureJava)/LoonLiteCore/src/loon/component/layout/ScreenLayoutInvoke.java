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
package loon.component.layout;

import loon.Screen;
import loon.geom.BoxSize;
import loon.geom.RectBox;

public class ScreenLayoutInvoke implements BoxSize {

	private final RectBox _screenSize;

	private final Screen _screen;

	private final LayoutPort _layoutPort;

	public ScreenLayoutInvoke(Screen s) {
		this._screen = s;
		this._screenSize = _screen.getRectBox().cpy();
		this._layoutPort = new LayoutPort(this, new LayoutConstraints());
	}

	public LayoutPort getLayoutPort() {
		return this._layoutPort;
	}

	public RectBox getView() {
		return _screenSize;
	}

	public Screen get() {
		return this._screen;
	}

	@Override
	public float getX() {
		return _screenSize.getX();
	}

	@Override
	public float getY() {
		return _screenSize.getY();
	}

	@Override
	public float getCenterX() {
		return _screenSize.getCenterX();
	}

	@Override
	public float getCenterY() {
		return _screenSize.getCenterY();
	}

	@Override
	public float getWidth() {
		return _screenSize.getWidth();
	}

	@Override
	public float getHeight() {
		return _screenSize.getHeight();
	}

	@Override
	public void setX(float x) {
		_screenSize.setX(x);
	}

	@Override
	public void setY(float y) {
		_screenSize.setY(y);
	}

	@Override
	public void setWidth(float w) {
		_screenSize.setWidth(w);
	}

	@Override
	public void setHeight(float h) {
		_screenSize.setHeight(h);
	}

}
