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

	public Screen tmp;

	public ScreenAction(Screen screen) {
		set(screen);
	}

	public ScreenAction set(Screen screen) {
		this.tmp = screen;
		return this;
	}

	@Override
	public Field2D getField2D() {
		return LSystem.viewSize.newField2D();
	}

	@Override
	public void setVisible(boolean v) {
		if (tmp != null) {
			this.tmp.setVisible(v);
		}
	}

	@Override
	public boolean isVisible() {
		return this.tmp == null ? false : this.tmp.isVisible();
	}

	@Override
	public int x() {
		return this.tmp == null ? 0 : (int) this.tmp.getX();
	}

	@Override
	public int y() {
		return this.tmp == null ? 0 : (int) this.tmp.getY();
	}

	@Override
	public float getX() {
		return this.tmp == null ? 0 : this.tmp.getX();
	}

	@Override
	public float getY() {
		return this.tmp == null ? 0 : this.tmp.getY();
	}

	@Override
	public float getScaleX() {
		return this.tmp == null ? 1f : this.tmp.getScaleX();
	}

	@Override
	public float getScaleY() {
		return this.tmp == null ? 1f : this.tmp.getScaleY();
	}

	@Override
	public void setColor(LColor color) {
		if (this.tmp != null) {
			this.tmp.setColor(color);
		}
	}

	@Override
	public LColor getColor() {
		return new LColor(tmp == null ? LColor.white : tmp.getColor());
	}

	@Override
	public void setScale(float sx, float sy) {
		if (tmp != null) {
			this.tmp.setScale(sx, sy);
		}
	}

	@Override
	public float getRotation() {
		return this.tmp == null ? 0 : this.tmp.getRotation();
	}

	@Override
	public void setRotation(float r) {
		if (tmp != null) {
			this.tmp.setRotation(r);
		}
	}

	@Override
	public float getWidth() {
		return tmp == null ? getContainerWidth() : tmp.getScreenWidth();
	}

	@Override
	public float getHeight() {
		return tmp == null ? getContainerHeight() : tmp.getScreenHeight();
	}

	@Override
	public float getAlpha() {
		return tmp == null ? 1f : tmp.getAlpha();
	}

	@Override
	public void setAlpha(float alpha) {
		if (tmp != null) {
			this.tmp.setAlpha(alpha);
		}
	}

	@Override
	public void setLocation(float x, float y) {
		if (tmp != null) {
			tmp.setLocation(x, y);
		}
	}

	@Override
	public void setX(float x) {
		if (tmp != null) {
			tmp.setX(x);
		}
	}

	@Override
	public void setY(float y) {
		if (tmp != null) {
			tmp.setX(y);
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
		return this.tmp != null ? tmp.getRectBox() : LSystem.viewSize.getRect();
	}

	@Override
	public float getContainerWidth() {
		return tmp == null ? LSystem.getProcess().getWidth() : tmp
				.getScreenWidth();
	}

	@Override
	public float getContainerHeight() {
		return tmp == null ? LSystem.getProcess().getWidth() : tmp
				.getScreenHeight();
	}

	@Override
	public Screen setFlipX(boolean x) {
		if (tmp != null) {
			return tmp.setFlipX(x);
		}
		return null;
	}

	@Override
	public Screen setFlipY(boolean y) {
		if (tmp != null) {
			return tmp.setFlipY(y);
		}
		return null;
	}

	@Override
	public Screen setFlipXY(boolean x, boolean y) {
		if (tmp != null) {
			return tmp.setFlipXY(x, y);
		}
		return null;
	}

	@Override
	public boolean isFlipX() {
		return tmp == null ? false : tmp.isFlipX();
	}

	@Override
	public boolean isFlipY() {
		return tmp == null ? false : tmp.isFlipY();
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