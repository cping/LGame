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
package loon.geom;

import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.map.Field2D;
import loon.canvas.LColor;

public class ActionBindRect implements ActionBind, BoxSize {

	private final RectBox _rect;

	private final ActionBind _actionBind;

	private Vector2f _position;

	public ActionBindRect(ActionBind bind) {
		this(bind, bind.getX(), bind.getY());
	}

	public ActionBindRect(ActionBind bind, float x, float y) {
		this(bind, x, y, bind.getWidth(), bind.getHeight());
	}

	public ActionBindRect(ActionBind bind, float x, float y, float w, float h) {
		this._actionBind = bind;
		this._position = new Vector2f();
		this._rect = new RectBox(x, y, w, h);
	}

	@Override
	public Field2D getField2D() {
		return _actionBind.getField2D();
	}

	@Override
	public void setVisible(boolean v) {
		_actionBind.setVisible(v);
	}

	@Override
	public boolean isVisible() {
		return _actionBind.isVisible();
	}

	@Override
	public int x() {
		return _rect.x() + _position.x();
	}

	@Override
	public int y() {
		return _rect.y() + _position.y();
	}

	@Override
	public float getX() {
		return _rect.getX() + _position.x;
	}

	@Override
	public float getY() {
		return _rect.getY() + _position.y;
	}

	@Override
	public float getScaleX() {
		return _actionBind.getScaleX();
	}

	@Override
	public float getScaleY() {
		return _actionBind.getScaleY();
	}

	@Override
	public void setColor(LColor color) {
		_actionBind.setColor(color);
	}

	@Override
	public LColor getColor() {
		return _actionBind.getColor();
	}

	@Override
	public void setScale(float sx, float sy) {
		_actionBind.setScale(sx, sy);
	}

	@Override
	public float getRotation() {
		return _actionBind.getRotation();
	}

	@Override
	public void setRotation(float r) {
		_actionBind.setRotation(r);
	}

	@Override
	public float getWidth() {
		return _rect.getWidth();
	}

	@Override
	public float getHeight() {
		return _rect.getHeight();
	}

	@Override
	public float getAlpha() {
		return _actionBind.getAlpha();
	}

	@Override
	public void setAlpha(float alpha) {
		_actionBind.setAlpha(alpha);
	}

	@Override
	public void setLocation(float x, float y) {
		_rect.setLocation(x, y);
	}

	public void setRect(float x, float y, float w, float h) {
		_rect.setBounds(x, y, w, h);
	}

	@Override
	public void setX(float x) {
		_rect.setX(x);
	}

	@Override
	public void setY(float y) {
		_rect.setY(y);
	}

	@Override
	public boolean isBounded() {
		return _actionBind.isBounded();
	}

	@Override
	public boolean isContainer() {
		return _actionBind.isContainer();
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return _rect.intersects(x, y, w, h);
	}

	@Override
	public RectBox getRectBox() {
		return _rect;
	}

	@Override
	public float getContainerWidth() {
		return _actionBind.getContainerWidth();
	}

	@Override
	public float getContainerHeight() {
		return _actionBind.getContainerHeight();
	}

	@Override
	public ActionTween selfAction() {
		return _actionBind.selfAction();
	}

	@Override
	public boolean isActionCompleted() {
		return _actionBind.isActionCompleted();
	}

	@Override
	public void setWidth(float w) {
		_rect.setWidth(w);
	}

	@Override
	public void setHeight(float h) {
		_rect.setHeight(h);
	}

	@Override
	public float getCenterX() {
		return _rect.getCenterX() + _position.x;
	}

	@Override
	public float getCenterY() {
		return _rect.getCenterY() + _position.y;
	}

	public ActionBind offset(Vector2f v) {
		if (v != null) {
			this._position = v;
		}
		return this;
	}

	public float getOffsetX() {
		return _position.x;
	}

	public float getOffsetY() {
		return _position.y;
	}

	public ActionBind setOffsetX(float x) {
		this._position.setX(x);
		return this;
	}

	public ActionBind getOffsetY(float y) {
		this._position.setY(y);
		return this;
	}

	@Override
	public ActionBind setSize(float w, float h) {
		return _actionBind.setSize(w, h);
	}

}
