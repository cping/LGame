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
package loon.action;

import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.geom.RectBox;

/**
 * Loon核心接口之一，实现此接口者，才可以通过action包统一操作(接口不要求全部实现 ，但只有实现了的部分，才能进行相应的动作)
 */
public interface ActionBind {

	public Field2D getField2D();

	public void setVisible(boolean v);

	public boolean isVisible();

	public int x();

	public int y();

	public float getX();

	public float getY();

	public float getScaleX();

	public float getScaleY();

	public void setColor(LColor color);

	public LColor getColor();

	public void setScale(float sx, float sy);

	public float getRotation();

	public void setRotation(float r);

	public float getWidth();

	public float getHeight();

	public ActionBind setSize(float w, float h);

	public float getAlpha();

	public void setAlpha(float alpha);

	public void setLocation(float x, float y);

	public void setX(float x);

	public void setY(float y);

	public boolean isBounded();

	public boolean isContainer();

	public boolean inContains(float x, float y, float w, float h);

	public RectBox getRectBox();

	public float getContainerWidth();

	public float getContainerHeight();

	public ActionTween selfAction();

	public boolean isActionCompleted();
	
	public int getLayer();
}
