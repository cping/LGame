/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0f (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0f
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
 * @version 0f.5
 */
package loon.action;

import loon.canvas.LColor;

/**
 * 工具类,用于缓存特定ActionBind的动作状态
 */
public class ActionBindData {

	public static final ActionBindData save(ActionBind act){
		return new ActionBindData(act);
	}
	
	protected float oldX = 0f;
	protected float oldY = 0f;
	protected float oldAlpha = 0f;
	protected float oldRotation = 0f;
	protected float oldScaleX = 0f;
	protected float oldScaleY = 0f;

	protected boolean oldVisible = false;

	protected LColor oldColor = null;

	protected float newX = 0f;
	protected float newY = 0f;
	protected float newAlpha = 0f;
	protected float newRotation = 0f;
	protected float newScaleX = 0f;
	protected float newScaleY = 0f;

	protected boolean newVisible = false;

	protected LColor newColor = null;

	private final ActionBind bindObject;

	public ActionBindData(ActionBind bind) {

		this.oldX = bind.getX();
		this.oldY = bind.getY();
		this.oldAlpha = bind.getAlpha();
		this.oldRotation = bind.getRotation();
		this.oldScaleX = bind.getScaleX();
		this.oldScaleY = bind.getScaleY();
		this.oldColor = bind.getColor();
		this.oldVisible = bind.isVisible();
		this.bindObject = bind;

	}

	public ActionBind getObject() {
		return this.bindObject;
	}

	public ActionBindData save() {

		this.newX = bindObject.getX();
		this.newY = bindObject.getY();
		this.newAlpha = bindObject.getAlpha();
		this.newRotation = bindObject.getRotation();
		this.newScaleX = bindObject.getScaleX();
		this.newScaleY = bindObject.getScaleY();
		this.newColor = bindObject.getColor();
		this.newVisible = bindObject.isVisible();

		return this;
	}

	public ActionBindData resetInitData() {

		bindObject.setLocation(oldX, oldY);
		bindObject.setAlpha(oldAlpha);
		bindObject.setRotation(oldRotation);
		bindObject.setScale(oldScaleX, oldScaleY);
		bindObject.setColor(oldColor);
		bindObject.setVisible(oldVisible);

		return this;
	}

	public ActionBindData resetNewSaveData() {

		bindObject.setLocation(newX, newY);
		bindObject.setAlpha(newAlpha);
		bindObject.setRotation(newRotation);
		bindObject.setScale(newScaleX, newScaleY);
		bindObject.setColor(newColor);
		bindObject.setVisible(newVisible);

		return this;
	}

	public boolean isActionCompleted() {
		return ActionControl.get().isCompleted(bindObject);
	}

	public float getOldX() {
		return oldX;
	}

	public float getOldY() {
		return oldY;
	}

	public float getOldAlpha() {
		return oldAlpha;
	}

	public float getOldRotation() {
		return oldRotation;
	}

	public float getOldScaleX() {
		return oldScaleX;
	}

	public float getOldScaleY() {
		return oldScaleY;
	}

	public boolean isOldVisible() {
		return oldVisible;
	}

	public LColor getOldColor() {
		return oldColor.cpy();
	}

	public float getNewX() {
		return newX;
	}

	public float getNewY() {
		return newY;
	}

	public float getNewAlpha() {
		return newAlpha;
	}

	public float getNewRotation() {
		return newRotation;
	}

	public float getNewScaleX() {
		return newScaleX;
	}

	public float getNewScaleY() {
		return newScaleY;
	}

	public boolean isNewVisible() {
		return newVisible;
	}

	public LColor getNewColor() {
		return newColor.cpy();
	}

}
