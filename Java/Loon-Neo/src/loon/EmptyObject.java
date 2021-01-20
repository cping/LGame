/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon;

import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.geom.RectBox;

public class EmptyObject extends LObject<Object> implements ActionBind, LRelease {

	private boolean _visible;

	@Override
	public void update(long elapsedTime) {

	}

	@Override
	public float getWidth() {
		return 0;
	}

	@Override
	public float getHeight() {
		return 0;
	}

	@Override
	public Field2D getField2D() {
		return null;
	}

	@Override
	public void setVisible(boolean v) {
		this._visible = v;
	}

	@Override
	public boolean isVisible() {
		return _visible;
	}

	private float scaleX = 1f, scaleY = 1f;

	@Override
	public float getScaleX() {
		return scaleX;
	}

	@Override
	public float getScaleY() {
		return scaleY;
	}

	@Override
	public void setScale(float sx, float sy) {
		this.scaleX = sx;
		this.scaleY = sy;
	}

	@Override
	public boolean isBounded() {
		return false;
	}

	@Override
	public boolean isContainer() {
		return false;
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return false;
	}

	@Override
	public RectBox getRectBox() {
		return getCollisionArea();
	}

	@Override
	public void setColor(LColor color) {

	}

	@Override
	public LColor getColor() {
		return null;
	}

	@Override
	public ActionTween selfAction() {
		return PlayerUtils.set(this);
	}

	@Override
	public boolean isActionCompleted() {
		return PlayerUtils.isActionCompleted(this);
	}

	public boolean isClosed() {
		return isDisposed();
	}

	@Override
	public void close() {
		setState(State.DISPOSED);
	}

}
