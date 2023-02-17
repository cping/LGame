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
package loon;

import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.utils.Flip;

public class ScreenAction implements Flip<Screen>, ActionBind {

	public Screen _tempScreen;

	public ScreenAction(Screen screen) {
		set(screen);
	}

	public ScreenAction set(Screen screen) {
		this._tempScreen = screen;
		return this;
	}

	@Override
	public Field2D getField2D() {
		return LSystem.viewSize.newField2D();
	}

	@Override
	public void setVisible(boolean v) {
		if (_tempScreen != null) {
			this._tempScreen.setVisible(v);
		}
	}

	@Override
	public boolean isVisible() {
		return this._tempScreen == null ? false : this._tempScreen.isVisible();
	}

	@Override
	public int x() {
		return this._tempScreen == null ? 0 : (int) this._tempScreen.getX();
	}

	@Override
	public int y() {
		return this._tempScreen == null ? 0 : (int) this._tempScreen.getY();
	}

	@Override
	public float getX() {
		return this._tempScreen == null ? 0 : this._tempScreen.getX();
	}

	@Override
	public float getY() {
		return this._tempScreen == null ? 0 : this._tempScreen.getY();
	}

	@Override
	public float getScaleX() {
		return this._tempScreen == null ? 1f : this._tempScreen.getScaleX();
	}

	@Override
	public float getScaleY() {
		return this._tempScreen == null ? 1f : this._tempScreen.getScaleY();
	}

	@Override
	public void setColor(LColor color) {
		if (this._tempScreen != null) {
			this._tempScreen.setColor(color);
		}
	}

	@Override
	public LColor getColor() {
		return new LColor(_tempScreen == null ? LColor.white : _tempScreen.getColor());
	}

	public ScreenAction setScale(float scale) {
		setScale(scale,scale);
		return this;
	}
	
	@Override
	public void setScale(float sx, float sy) {
		if (_tempScreen != null) {
			this._tempScreen.setScale(sx, sy);
		}
	}

	@Override
	public float getRotation() {
		return this._tempScreen == null ? 0 : this._tempScreen.getRotation();
	}

	@Override
	public void setRotation(float r) {
		if (_tempScreen != null) {
			this._tempScreen.setRotation(r);
		}
	}

	@Override
	public float getWidth() {
		return _tempScreen == null ? getContainerWidth() : _tempScreen.getScreenWidth();
	}

	@Override
	public float getHeight() {
		return _tempScreen == null ? getContainerHeight() : _tempScreen.getScreenHeight();
	}

	@Override
	public float getAlpha() {
		return _tempScreen == null ? 1f : _tempScreen.getAlpha();
	}

	@Override
	public void setAlpha(float alpha) {
		if (_tempScreen != null) {
			this._tempScreen.setAlpha(alpha);
		}
	}

	@Override
	public void setLocation(float x, float y) {
		if (_tempScreen != null) {
			_tempScreen.setLocation(x, y);
		}
	}

	@Override
	public void setX(float x) {
		if (_tempScreen != null) {
			_tempScreen.setX(x);
		}
	}

	@Override
	public void setY(float y) {
		if (_tempScreen != null) {
			_tempScreen.setX(y);
		}
	}

	@Override
	public boolean isBounded() {
		return false;
	}

	@Override
	public boolean isContainer() {
		return true;
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return getRectBox().contains(x, y, w, h);
	}

	@Override
	public RectBox getRectBox() {
		return this._tempScreen != null ? _tempScreen.getRectBox() : LSystem.viewSize.getRect();
	}

	@Override
	public float getContainerWidth() {
		return _tempScreen == null ? LSystem.getProcess().getWidth() : _tempScreen
				.getScreenWidth();
	}

	@Override
	public float getContainerHeight() {
		return _tempScreen == null ? LSystem.getProcess().getWidth() : _tempScreen
				.getScreenHeight();
	}

	@Override
	public Screen setFlipX(boolean x) {
		if (_tempScreen != null) {
			return _tempScreen.setFlipX(x);
		}
		return null;
	}

	@Override
	public Screen setFlipY(boolean y) {
		if (_tempScreen != null) {
			return _tempScreen.setFlipY(y);
		}
		return null;
	}

	@Override
	public Screen setFlipXY(boolean x, boolean y) {
		if (_tempScreen != null) {
			return _tempScreen.setFlipXY(x, y);
		}
		return null;
	}

	@Override
	public boolean isFlipX() {
		return _tempScreen == null ? false : _tempScreen.isFlipX();
	}

	@Override
	public boolean isFlipY() {
		return _tempScreen == null ? false : _tempScreen.isFlipY();
	}

	@Override
	public ActionTween selfAction() {
		return PlayerUtils.set(this);
	}

	@Override
	public boolean isActionCompleted() {
		return PlayerUtils.isActionCompleted(this);
	}

}