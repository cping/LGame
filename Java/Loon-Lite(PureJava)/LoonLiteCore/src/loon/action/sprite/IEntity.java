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

import loon.LRelease;
import loon.LTexture;
import loon.action.Flip;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.StrBuilder;
import loon.utils.TArray;

public interface IEntity extends ISprite, Flip<IEntity> {

	TArray<IEntity> getChildren();

	TArray<IEntity> getAscendantChildren();

	TArray<IEntity> getDescendantChildren();

	boolean isAscendantOf(ISprite actor);

	boolean isDescendantOf(ISprite actor);

	@Override
	boolean isVisible();

	@Override
	void setVisible(final boolean v);

	boolean isIgnoreUpdate();

	void setIgnoreUpdate(boolean u);

	boolean isChildrenVisible();

	IEntity setChildrenVisible(final boolean v);

	boolean isChildrenIgnoreUpdate();

	IEntity setChildrenIgnoreUpdate(boolean u);

	int getIndexTag();

	IEntity setIndexTag(final int t);

	@Override
	int getLayer();

	@Override
	void setLayer(final int l);

	boolean hasParent();

	@Override
	IEntity getParent();

	void setParent(final IEntity e);

	@Override
	float getX();

	@Override
	float getY();

	@Override
	void setX(final float x);

	@Override
	void setY(final float y);

	float getScalePixelX();

	float getScalePixelY();

	boolean isRotated();

	@Override
	float getRotation();

	@Override
	void setRotation(final float pRotation);

	float getRotationCenterX();

	float getRotationCenterY();

	void setRotationCenterX(final float rx);

	void setRotationCenterY(final float ry);

	void setRotationCenter(final float rx, final float ry);

	float getPivotX();

	float getPivotY();

	void setPivotX(final float rx);

	void setPivotY(final float ry);

	void setPivot(final float rx, final float ry);

	boolean isScaled();

	@Override
	float getScaleX();

	@Override
	float getScaleY();

	void setScaleX(final float sx);

	void setScaleY(final float sy);

	void setScale(final float s);

	@Override
	void setScale(final float sx, final float sy);

	float getScaleCenterX();

	float getScaleCenterY();

	void setScaleCenterX(final float sx);

	void setScaleCenterY(final float sy);

	void setScaleCenter(final float sx, final float sy);

	boolean isSkewed();

	float getSkewX();

	float getSkewY();

	void setSkewX(final float sx);

	void setSkewY(final float sy);

	void setSkew(final float pSkew);

	void setSkew(final float sx, final float sy);

	float getSkewCenterX();

	float getSkewCenterY();

	void setSkewCenterX(final float sx);

	void setSkewCenterY(final float sy);

	void setSkewCenter(final float sx, final float sy);

	boolean isRotatedOrScaledOrSkewed();

	float getRed();

	float getGreen();

	float getBlue();

	@Override
	float getAlpha();

	@Override
	LColor getColor();

	@Override
	void setAlpha(final float a);

	@Override
	void setColor(final LColor c);

	void setColor(final int c);

	void setColor(final float r, final float g, final float b);

	void setColor(final float r, final float g, final float b, final float a);

	int getChildCount();

	void onAttached();

	void onDetached();

	IEntity with(final TComponent<IEntity> c);

	TComponent<IEntity> findComponent(String name);

	TComponent<IEntity> findComponent(Class<? extends TComponent<IEntity>> typeClazz);

	IEntity addComponent(final TComponent<IEntity> c);

	IEntity removeComponents();

	boolean removeComponent(final TComponent<IEntity> c);

	boolean removeComponentType(Class<? extends TComponent<IEntity>> typeClazz);

	boolean removeComponentName(String typeName);

	boolean hasComponent();

	boolean isComponentIgnoreUpdate();

	IEntity setComponentIgnoreUpdate(final boolean c);

	int getComponentCount();

	TArray<TComponent<IEntity>> getComponents();

	IEntity addChild(final IEntity e);

	IEntity addChildAt(final IEntity e, float x, float y);

	IEntity getChildByTag(final int t);

	IEntity getChildByIndex(final int idx);

	IEntity getFirstChild();

	IEntity getLastChild();

	IEntity sortChildren();

	IEntity sortChildren(final boolean i);

	IEntity sortChildren(final Comparator<IEntity> c);

	boolean removeSelf();

	boolean removeChild(final IEntity e);

	boolean removeChild(final int i);

	IEntity removeChildIndexTag(final int t);

	IEntity removeChildren();

	IEntity removeParent();

	IEntity setUserData(final Object u);

	Object getUserData();

	void toString(final StrBuilder s);

	@Override
	void update(long elapsedTime);

	IEntity reset();

	@Override
	void createUI(final GLEx gl);

	@Override
	void createUI(final GLEx gl, final float offsetX, final float offsetY);

	IEntity view(LTexture tex);

	IEntity view(String path);

	IEntity setFollowRotation(boolean r);

	IEntity setFollowScale(boolean s);

	IEntity setFollowColor(boolean c);

	boolean isFollowRotation();

	boolean isFollowScale();

	boolean isFollowColor();

	IEntity show();

	IEntity hide();

	@Override
	IEntity triggerCollision(SpriteCollisionListener sc);

	@Override
	IEntity buildToScreen();

	@Override
	IEntity removeFromScreen();

	IEntity dispose(LRelease r);
}
