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

	private final RectBox rect;

	private final ActionBind actionBind;

	public ActionBindRect(ActionBind bind) {
		this(bind, bind.getX(), bind.getY());
	}

	public ActionBindRect(ActionBind bind, float x, float y) {
		this(bind, x, y, bind.getWidth(), bind.getHeight());
	}

	public ActionBindRect(ActionBind bind, float x, float y, float w, float h) {
		this.actionBind = bind;
		this.rect = new RectBox(x, y, w, h);
	}

	@Override
	public Field2D getField2D() {
		return actionBind.getField2D();
	}

	@Override
	public void setVisible(boolean v) {
		actionBind.setVisible(v);
	}

	@Override
	public boolean isVisible() {
		return actionBind.isVisible();
	}

	@Override
	public int x() {
		return rect.x();
	}

	@Override
	public int y() {
		return rect.y();
	}

	@Override
	public float getX() {
		return rect.getX();
	}

	@Override
	public float getY() {
		return rect.getY();
	}

	@Override
	public float getScaleX() {
		return actionBind.getScaleX();
	}

	@Override
	public float getScaleY() {
		return actionBind.getScaleY();
	}

	@Override
	public void setColor(LColor color) {
		actionBind.setColor(color);
	}

	@Override
	public LColor getColor() {
		return actionBind.getColor();
	}

	@Override
	public void setScale(float sx, float sy) {
		actionBind.setScale(sx, sy);
	}

	@Override
	public float getRotation() {
		return actionBind.getRotation();
	}

	@Override
	public void setRotation(float r) {
		actionBind.setRotation(r);
	}

	@Override
	public float getWidth() {
		return rect.getWidth();
	}

	@Override
	public float getHeight() {
		return rect.getHeight();
	}

	@Override
	public float getAlpha() {
		return actionBind.getAlpha();
	}

	@Override
	public void setAlpha(float alpha) {
		actionBind.setAlpha(alpha);
	}

	@Override
	public void setLocation(float x, float y) {
		rect.setLocation(x, y);
	}

	public void setRect(float x, float y, float w, float h) {
		rect.setBounds(x, y, w, h);
	}

	@Override
	public void setX(float x) {
		rect.setX(x);
	}

	@Override
	public void setY(float y) {
		rect.setY(y);
	}

	@Override
	public boolean isBounded() {
		return actionBind.isBounded();
	}

	@Override
	public boolean isContainer() {
		return actionBind.isContainer();
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return rect.intersects(x, y, w, h);
	}

	@Override
	public RectBox getRectBox() {
		return rect;
	}

	@Override
	public float getContainerWidth() {
		return actionBind.getContainerWidth();
	}

	@Override
	public float getContainerHeight() {
		return actionBind.getContainerHeight();
	}

	@Override
	public ActionTween selfAction() {
		return actionBind.selfAction();
	}

	@Override
	public boolean isActionCompleted() {
		return actionBind.isActionCompleted();
	}

	@Override
	public void setWidth(float w) {
		rect.setWidth(w);
	}

	@Override
	public void setHeight(float h) {
		rect.setHeight(h);
	}

}
