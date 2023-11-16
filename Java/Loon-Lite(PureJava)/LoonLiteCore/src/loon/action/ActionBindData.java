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
import loon.utils.ConfigReader;

/**
 * 工具类,用于缓存特定ActionBind的动作状态
 */
public class ActionBindData {

	public static final ActionBindData save(ActionBind act) {
		return new ActionBindData(act);
	}

	protected float oldX = 0f;
	protected float oldY = 0f;
	protected float oldAlpha = 0f;
	protected float oldRotation = 0f;
	protected float oldScaleX = 0f;
	protected float oldScaleY = 0f;
	protected float oldWidth = 0f;
	protected float oldHeight = 0f;

	protected boolean oldVisible = false;

	protected LColor oldColor = null;

	protected float newX = 0f;
	protected float newY = 0f;
	protected float newAlpha = 0f;
	protected float newRotation = 0f;
	protected float newScaleX = 0f;
	protected float newScaleY = 0f;
	protected float newWidth = 0f;
	protected float newHeight = 0f;

	protected float oldOffsetX;
	protected float oldOffsetY;
	protected float newOffsetX;
	protected float newOffsetY;

	protected boolean newVisible = false;

	protected LColor newColor = null;

	private final ActionBind bindObject;

	public ActionBindData(ActionBind bind, float offX, float offY) {

		this.oldX = bind.getX();
		this.oldY = bind.getY();
		this.oldOffsetX = offX;
		this.oldOffsetY = offY;
		this.oldAlpha = bind.getAlpha();
		this.oldRotation = bind.getRotation();
		this.oldScaleX = bind.getScaleX();
		this.oldScaleY = bind.getScaleY();
		this.oldWidth = bind.getWidth();
		this.oldHeight = bind.getHeight();
		this.oldColor = bind.getColor();
		this.oldVisible = bind.isVisible();
		this.bindObject = bind;

	}

	public ActionBindData(ActionBind bind) {
		this(bind, 0f, 0f);
	}

	public float getX() {
		return bindObject.getX() + oldOffsetX;
	}

	public float getY() {
		return bindObject.getY() + oldOffsetY;
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
		this.newWidth = bindObject.getWidth();
		this.newHeight = bindObject.getHeight();
		this.newColor = bindObject.getColor();
		this.newVisible = bindObject.isVisible();
		this.newOffsetX = oldOffsetX;
		this.newOffsetY = oldOffsetY;

		return this;
	}

	public ActionBindData resetInitData() {

		bindObject.setLocation(oldX, oldY);
		bindObject.setAlpha(oldAlpha);
		bindObject.setRotation(oldRotation);
		bindObject.setScale(oldScaleX, oldScaleY);
		bindObject.setSize(oldWidth, oldHeight);
		bindObject.setColor(oldColor);
		bindObject.setVisible(oldVisible);
		offset(oldOffsetX, oldOffsetY);

		return this;
	}

	public ActionBindData resetNewSaveData() {

		bindObject.setLocation(newX, newY);
		bindObject.setAlpha(newAlpha);
		bindObject.setRotation(newRotation);
		bindObject.setScale(newScaleX, newScaleY);
		bindObject.setSize(newWidth, newHeight);
		bindObject.setColor(newColor);
		bindObject.setVisible(newVisible);
		offset(newOffsetX, newOffsetY);

		return this;
	}

	public ActionBindData setPath(String path) {
		setConfig(ConfigReader.shared(path));
		return this;
	}

	public ActionBindData setContext(String context) {
		setConfig(ConfigReader.parse(context));
		return this;
	}

	protected void setConfig(ConfigReader config) {
		newAlpha = config.getFloatValue("alpha", oldAlpha);
		newRotation = config.getFloatValue("rotation", oldRotation);
		newVisible = config.getBoolValue("visible", oldVisible);
		final float[] pos = config.getFloatValues("location", new float[] { oldX, oldY });
		newX = pos[0];
		newY = pos[1];
		final float[] scale = config.getFloatValues("scale", new float[] { oldScaleX, oldScaleY });
		newScaleX = scale[0];
		newScaleY = scale[1];
		final float[] size = config.getFloatValues("size", new float[] { oldWidth, oldHeight });
		newWidth = size[0];
		newHeight = size[1];
		final float[] offset = config.getFloatValues("offset", new float[] { oldOffsetX, oldOffsetY });
		newOffsetX = offset[0];
		newOffsetY = offset[1];
		newColor = config.getColor("color", oldColor);
	}

	public ActionBindData sync() {
		return syncNew();
	}

	public ActionBindData syncNew() {
		return resetNewSaveData();
	}

	public ActionBindData syncOld() {
		return resetInitData();
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

	public float getOldWidth() {
		return oldWidth;
	}

	public float getOldHeight() {
		return oldHeight;
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

	public float getNewWidth() {
		return newWidth;
	}

	public float getNewHeight() {
		return newHeight;
	}

	public boolean isNewVisible() {
		return newVisible;
	}

	public LColor getNewColor() {
		return newColor.cpy();
	}

	public float getOffsetX() {
		return oldOffsetX;
	}

	public ActionBindData setOffsetX(float offsetX) {
		this.oldOffsetX = offsetX;
		return this;
	}

	public float getOffsetY() {
		return oldOffsetY;
	}

	public ActionBindData setOffsetY(float offsetY) {
		this.oldOffsetY = offsetY;
		return this;
	}

	public ActionBindData offset(float offX, float offY) {
		this.setOffsetX(offX);
		this.setOffsetY(offY);
		return this;
	}

}
