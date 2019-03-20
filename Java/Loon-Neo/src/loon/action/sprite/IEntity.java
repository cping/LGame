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
package loon.action.sprite;

import java.util.Comparator;

import loon.ZIndex;
import loon.action.sprite.IEntity;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.Flip;

public interface IEntity extends ISprite, ZIndex, Flip<IEntity> {

	public static final int TAG_INVALID = Integer.MIN_VALUE;

	public boolean isVisible();

	public void setVisible(final boolean v);

	public boolean isIgnoreUpdate();

	public void setIgnoreUpdate(boolean u);

	public boolean isChildrenVisible();

	public void setChildrenVisible(final boolean v);

	public boolean isChildrenIgnoreUpdate();

	public void setChildrenIgnoreUpdate(boolean u);

	public int getIndexTag();

	public void setIndexTag(final int t);

	public int getLayer();

	public void setLayer(final int l);

	public boolean hasParent();

	public IEntity getParent();

	public void setParent(final IEntity e);

	public float getX();

	public float getY();

	public void setX(final float x);

	public void setY(final float y);

	public boolean isRotated();

	public float getRotation();

	public void setRotation(final float pRotation);

	public float getRotationCenterX();

	public float getRotationCenterY();

	public void setRotationCenterX(final float rx);

	public void setRotationCenterY(final float ry);

	public void setRotationCenter(final float rx, final float ry);

	public float getPivotX();

	public float getPivotY();

	public void setPivotX(final float rx);

	public void setPivotY(final float ry);

	public void setPivot(final float rx, final float ry);

	public boolean isScaled();

	public float getScaleX();

	public float getScaleY();

	public void setScaleX(final float sx);

	public void setScaleY(final float sy);

	public void setScale(final float s);

	public void setScale(final float sx, final float sy);

	public float getScaleCenterX();

	public float getScaleCenterY();

	public void setScaleCenterX(final float sx);

	public void setScaleCenterY(final float sy);

	public void setScaleCenter(final float sx, final float sy);

	public boolean isSkewed();

	public float getSkewX();

	public float getSkewY();

	public void setSkewX(final float sx);

	public void setSkewY(final float sy);

	public void setSkew(final float pSkew);

	public void setSkew(final float sx, final float sy);

	public float getSkewCenterX();

	public float getSkewCenterY();

	public void setSkewCenterX(final float sx);

	public void setSkewCenterY(final float sy);

	public void setSkewCenter(final float sx, final float sy);

	public boolean isRotatedOrScaledOrSkewed();

	public float getRed();

	public float getGreen();

	public float getBlue();

	public float getAlpha();

	public LColor getColor();

	public void setAlpha(final float a);

	public void setColor(final LColor c);

	public void setColor(final int c);

	public void setColor(final float r, final float g, final float b);

	public void setColor(final float r, final float g, final float b,
			final float a);

	public int getChildCount();

	public void onAttached();

	public void onDetached();

	public void addChild(final IEntity e);

	public void addChildAt(final IEntity e, float x, float y);

	public IEntity getChildByTag(final int t);

	public IEntity getChildByIndex(final int idx);

	public IEntity getFirstChild();

	public IEntity getLastChild();

	public void sortChildren();

	public void sortChildren(final boolean i);

	public void sortChildren(final Comparator<IEntity> c);

	public boolean removeSelf();

	public boolean removeChild(final IEntity e);

	public IEntity removeChild(final int t);

	public void removeChildren();

	public void setUserData(final Object u);

	public Object getUserData();

	public void toString(final StringBuilder s);

	public void update(long elapsedTime);

	public void reset();

	public void createUI(final GLEx gl);

	public void createUI(final GLEx gl, final float offsetX, final float offsetY);
}
