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
import loon.core.geom.RectBox;

/**
 * 修改了动作控制方式，自0.3.3起，只要实现了Action接口的类都可以被ActionControl执行。
 */
public interface ActionBind {
	
	public Field2D getField2D();

	public int x();

	public int y();
	
	public float getX();

	public float getY();

	public float getScaleX();

	public float getScaleY();

	public void setScale(float sx, float sy);

	public float getRotation();

	public void setRotation(float r);

	public int getWidth();

	public int getHeight();

	public float getAlpha();

	public void setAlpha(float a);

	public void setLocation(float x, float y);

	public boolean isBounded();

	public boolean isContainer();

	public boolean inContains(int x, int y, int w, int h);
	
	public RectBox getRectBox();

	public int getContainerWidth();

	public int getContainerHeight();
}
