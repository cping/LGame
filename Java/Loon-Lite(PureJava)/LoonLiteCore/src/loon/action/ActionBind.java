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

	Field2D getField2D();

	void setVisible(boolean v);

	boolean isVisible();

	int x();

	int y();

	float getX();

	float getY();

	float getScaleX();

	float getScaleY();

	void setColor(LColor color);

	LColor getColor();

	void setScale(float sx, float sy);

	float getRotation();

	void setRotation(float r);

	float getWidth();

	float getHeight();

	ActionBind setSize(float w, float h);

	float getAlpha();

	void setAlpha(float alpha);

	void setLocation(float x, float y);

	void setX(float x);

	void setY(float y);

	boolean isBounded();

	boolean isContainer();

	boolean inContains(float x, float y, float w, float h);

	RectBox getRectBox();

	float getContainerWidth();

	float getContainerHeight();

	ActionTween selfAction();

	boolean isActionCompleted();

	int getLayer();
}
