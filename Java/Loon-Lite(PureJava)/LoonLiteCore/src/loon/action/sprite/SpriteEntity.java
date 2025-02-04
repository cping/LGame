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
package loon.action.sprite;

import java.util.Comparator;

import loon.LObject.State;
import loon.LRelease;
import loon.LTexture;
import loon.Screen;
import loon.action.ActionTween;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.StrBuilder;
import loon.utils.TArray;

public class SpriteEntity implements IEntity {

	private ISprite _sprite;

	private IEntity _parent;

	public SpriteEntity(ISprite s) {
		this._sprite = s;
	}

	public ISprite getSprite() {
		return _sprite;
	}

	@Override
	public float getWidth() {
		return _sprite.getWidth();
	}

	@Override
	public float getHeight() {
		return _sprite.getHeight();
	}

	@Override
	public int x() {
		return _sprite.x();
	}

	@Override
	public int y() {
		return _sprite.y();
	}

	@Override
	public int getZ() {
		return _sprite.getZ();
	}

	@Override
	public boolean showShadow() {
		return _sprite.showShadow();
	}

	@Override
	public boolean autoXYSort() {
		return _sprite.autoXYSort();
	}

	@Override
	public ISprite setSize(float w, float h) {
		return _sprite.setSize(w, h);
	}

	@Override
	public void onResize() {
		if (_sprite != null) {
			_sprite.onResize();
		}
	}

	@Override
	public void onCollision(ISprite coll, int dir) {
		_sprite.onCollision(coll, dir);
	}

	@Override
	public RectBox getCollisionBox() {
		return _sprite.getCollisionBox();
	}

	@Override
	public LTexture getBitmap() {
		return _sprite.getBitmap();
	}

	@Override
	public String getName() {
		return _sprite.getName();
	}

	@Override
	public Object getTag() {
		return _sprite.getTag();
	}

	@Override
	public void setParent(ISprite s) {
		_sprite.setParent(s);
	}

	@Override
	public void setName(String s) {
		_sprite.setName(s);
	}

	@Override
	public void setState(State state) {
		_sprite.setState(state);
	}

	@Override
	public State getState() {
		return _sprite.getState();
	}

	@Override
	public ISprite setSprites(Sprites ss) {
		return _sprite.setSprites(ss);
	}

	@Override
	public Sprites getSprites() {
		return _sprite.getSprites();
	}

	@Override
	public Screen getScreen() {
		return _sprite.getScreen();
	}

	@Override
	public ISprite setOffset(Vector2f v) {
		return _sprite.setOffset(v);
	}

	@Override
	public float getOffsetX() {
		return _sprite.getOffsetX();
	}

	@Override
	public float getOffsetY() {
		return _sprite.getOffsetY();
	}

	@Override
	public float getFixedWidthOffset() {
		return _sprite.getFixedWidthOffset();
	}

	@Override
	public ISprite setFixedWidthOffset(float widthOffset) {
		return _sprite.setFixedWidthOffset(widthOffset);
	}

	@Override
	public float getFixedHeightOffset() {
		return _sprite.getFixedHeightOffset();
	}

	@Override
	public ISprite setFixedHeightOffset(float heightOffset) {
		return _sprite.setFixedHeightOffset(heightOffset);
	}

	@Override
	public boolean collides(ISprite other) {
		return _sprite.collides(other);
	}

	@Override
	public boolean collidesX(ISprite other) {
		return _sprite.collidesX(other);
	}

	@Override
	public boolean collidesY(ISprite other) {
		return _sprite.collidesY(other);
	}

	@Override
	public boolean isDisposed() {
		return _sprite.isDisposed();
	}

	@Override
	public Field2D getField2D() {
		return _sprite.getField2D();
	}

	@Override
	public void setLocation(float x, float y) {
		_sprite.setLocation(x, y);
	}

	@Override
	public boolean isBounded() {
		return _sprite.isBounded();
	}

	@Override
	public boolean isContainer() {
		return _sprite.isContainer();
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return _sprite.inContains(x, y, w, h);
	}

	@Override
	public RectBox getRectBox() {
		return _sprite.getRectBox();
	}

	@Override
	public float getContainerWidth() {
		return _sprite.getContainerWidth();
	}

	@Override
	public float getContainerHeight() {
		return _sprite.getContainerHeight();
	}

	@Override
	public ActionTween selfAction() {
		return _sprite.selfAction();
	}

	@Override
	public boolean isActionCompleted() {
		return _sprite.isActionCompleted();
	}

	@Override
	public void close() {
		_sprite.close();
	}

	@Override
	public IEntity setFlipX(boolean x) {
		return this;
	}

	@Override
	public IEntity setFlipY(boolean y) {
		return this;
	}

	@Override
	public IEntity setFlipXY(boolean x, boolean y) {
		return this;
	}

	@Override
	public boolean isFlipX() {
		return false;
	}

	@Override
	public boolean isFlipY() {
		return false;
	}

	@Override
	public TArray<IEntity> getChildren() {
		return null;
	}

	@Override
	public TArray<IEntity> getAscendantChildren() {
		return null;
	}

	@Override
	public TArray<IEntity> getDescendantChildren() {
		return null;
	}

	@Override
	public boolean isAscendantOf(ISprite actor) {
		return false;
	}

	@Override
	public boolean isDescendantOf(ISprite actor) {
		return false;
	}

	@Override
	public boolean isVisible() {
		return _sprite.isVisible();
	}

	@Override
	public void setVisible(boolean v) {
		_sprite.setVisible(v);
	}

	@Override
	public boolean isIgnoreUpdate() {
		return false;
	}

	@Override
	public void setIgnoreUpdate(boolean u) {
	}

	@Override
	public boolean isChildrenVisible() {
		return false;
	}

	@Override
	public IEntity setChildrenVisible(boolean v) {
		return this;
	}

	@Override
	public boolean isChildrenIgnoreUpdate() {
		return false;
	}

	@Override
	public IEntity setChildrenIgnoreUpdate(boolean u) {
		return this;
	}

	@Override
	public int getIndexTag() {
		return 0;
	}

	@Override
	public IEntity setIndexTag(int t) {
		return this;
	}

	@Override
	public int getLayer() {
		return _sprite.getLayer();
	}

	@Override
	public void setLayer(int l) {
		_sprite.setLayer(l);
	}

	@Override
	public boolean hasParent() {
		return _parent != null;
	}

	@Override
	public IEntity getParent() {
		return _parent;
	}

	@Override
	public void setParent(IEntity e) {
		_parent = e;
		_sprite.setParent(e);
	}

	@Override
	public float getX() {
		return _sprite.getX();
	}

	@Override
	public float getY() {
		return _sprite.getY();
	}

	@Override
	public void setX(float x) {
		_sprite.setX(x);
	}

	@Override
	public void setY(float y) {
		_sprite.setY(y);
	}

	@Override
	public float getScalePixelX() {
		return 0f;
	}

	@Override
	public float getScalePixelY() {
		return 0f;
	}

	@Override
	public boolean isRotated() {
		return _sprite.getRotation() != 0f;
	}

	@Override
	public float getRotation() {
		return _sprite.getRotation();
	}

	@Override
	public void setRotation(float pRotation) {
		_sprite.setRotation(pRotation);
	}

	@Override
	public float getRotationCenterX() {
		return 0f;
	}

	@Override
	public float getRotationCenterY() {
		return 0f;
	}

	@Override
	public void setRotationCenterX(float rx) {

	}

	@Override
	public void setRotationCenterY(float ry) {

	}

	@Override
	public void setRotationCenter(float rx, float ry) {

	}

	@Override
	public float getPivotX() {
		return 0f;
	}

	@Override
	public float getPivotY() {
		return 0f;
	}

	@Override
	public void setPivotX(float rx) {
	}

	@Override
	public void setPivotY(float ry) {
	}

	@Override
	public void setPivot(float rx, float ry) {
	}

	@Override
	public boolean isScaled() {
		return _sprite.getScaleX() != 1f || _sprite.getScaleY() != 1f;
	}

	@Override
	public float getScaleX() {
		return _sprite.getScaleX();
	}

	@Override
	public float getScaleY() {
		return _sprite.getScaleY();
	}

	@Override
	public void setScaleX(float sx) {
		_sprite.setScale(sx, _sprite.getScaleY());
	}

	@Override
	public void setScaleY(float sy) {
		_sprite.setScale(_sprite.getScaleX(), sy);
	}

	@Override
	public void setScale(float s) {
		_sprite.setScale(s, s);
	}

	@Override
	public void setScale(float sx, float sy) {
		_sprite.setScale(sx, sy);
	}

	@Override
	public float getScaleCenterX() {
		return 0;
	}

	@Override
	public float getScaleCenterY() {
		return 0;
	}

	@Override
	public void setScaleCenterX(float sx) {

	}

	@Override
	public void setScaleCenterY(float sy) {

	}

	@Override
	public void setScaleCenter(float sx, float sy) {

	}

	@Override
	public boolean isSkewed() {
		return false;
	}

	@Override
	public float getSkewX() {
		return 0;
	}

	@Override
	public float getSkewY() {
		return 0;
	}

	@Override
	public void setSkewX(float sx) {

	}

	@Override
	public void setSkewY(float sy) {

	}

	@Override
	public void setSkew(float pSkew) {

	}

	@Override
	public void setSkew(float sx, float sy) {

	}

	@Override
	public float getSkewCenterX() {
		return 0;
	}

	@Override
	public float getSkewCenterY() {
		return 0;
	}

	@Override
	public void setSkewCenterX(float sx) {
	}

	@Override
	public void setSkewCenterY(float sy) {
	}

	@Override
	public void setSkewCenter(float sx, float sy) {
	}

	@Override
	public boolean isRotatedOrScaledOrSkewed() {
		return false;
	}

	@Override
	public float getRed() {
		return _sprite.getColor().r;
	}

	@Override
	public float getGreen() {
		return _sprite.getColor().g;
	}

	@Override
	public float getBlue() {
		return _sprite.getColor().b;
	}

	@Override
	public float getAlpha() {
		return _sprite.getAlpha();
	}

	@Override
	public LColor getColor() {
		return _sprite.getColor();
	}

	@Override
	public void setAlpha(float a) {
		_sprite.setAlpha(a);
	}

	@Override
	public void setColor(LColor c) {
		_sprite.setColor(c);
	}

	@Override
	public void setColor(int c) {
		_sprite.setColor(new LColor(c));
	}

	@Override
	public void setColor(float r, float g, float b) {
		_sprite.setColor(new LColor(r, g, b));
	}

	@Override
	public void setColor(float r, float g, float b, float a) {
		_sprite.setColor(new LColor(r, g, b, a));
	}

	@Override
	public int getChildCount() {
		return 0;
	}

	@Override
	public void onAttached() {

	}

	@Override
	public void onDetached() {

	}

	@Override
	public IEntity with(TComponent<IEntity> c) {
		return this;
	}

	@Override
	public TComponent<IEntity> findComponent(String name) {
		return null;
	}

	@Override
	public TComponent<IEntity> findComponent(Class<? extends TComponent<IEntity>> typeClazz) {
		return null;
	}

	@Override
	public IEntity addComponent(TComponent<IEntity> c) {
		return this;
	}

	@Override
	public IEntity removeComponents() {
		return this;
	}

	@Override
	public boolean removeComponent(TComponent<IEntity> c) {
		return false;
	}

	@Override
	public boolean removeComponentType(Class<? extends TComponent<IEntity>> typeClazz) {
		return false;
	}

	@Override
	public boolean removeComponentName(String typeName) {
		return false;
	}

	@Override
	public boolean hasComponent() {
		return false;
	}

	@Override
	public boolean isComponentIgnoreUpdate() {
		return false;
	}

	@Override
	public IEntity setComponentIgnoreUpdate(boolean c) {
		return this;
	}

	@Override
	public int getComponentCount() {
		return 0;
	}

	@Override
	public TArray<TComponent<IEntity>> getComponents() {
		return null;
	}

	@Override
	public IEntity addChild(IEntity e) {
		return this;
	}

	@Override
	public IEntity addChildAt(IEntity e, float x, float y) {
		return this;
	}

	@Override
	public IEntity getChildByTag(int t) {
		return this;
	}

	@Override
	public IEntity getChildByIndex(int idx) {
		return this;
	}

	@Override
	public IEntity getFirstChild() {
		return this;
	}

	@Override
	public IEntity getLastChild() {
		return this;
	}

	@Override
	public IEntity sortChildren() {
		return this;
	}

	@Override
	public IEntity sortChildren(boolean i) {
		return this;
	}

	@Override
	public IEntity sortChildren(Comparator<IEntity> c) {
		return this;
	}

	@Override
	public boolean removeSelf() {
		return false;
	}

	@Override
	public boolean removeChild(IEntity e) {
		return false;
	}

	@Override
	public boolean removeChild(int t) {
		return false;
	}

	@Override
	public IEntity removeChildIndexTag(int t) {
		return this;
	}

	@Override
	public IEntity removeChildren() {
		return this;
	}

	@Override
	public IEntity removeParent() {
		return this;
	}

	@Override
	public IEntity setUserData(Object u) {
		return this;
	}

	@Override
	public Object getUserData() {
		return this;
	}

	@Override
	public void toString(StrBuilder s) {
	}

	@Override
	public void update(long elapsedTime) {
		_sprite.update(elapsedTime);
	}

	@Override
	public IEntity reset() {
		return this;
	}

	@Override
	public void createUI(GLEx gl) {
		_sprite.createUI(gl);
	}

	@Override
	public void createUI(GLEx gl, float offsetX, float offsetY) {
		_sprite.createUI(gl, offsetX, offsetY);
	}

	@Override
	public IEntity view(LTexture tex) {
		return this;
	}

	@Override
	public IEntity view(String path) {
		return this;
	}

	@Override
	public IEntity setFollowRotation(boolean r) {
		return this;
	}

	@Override
	public IEntity setFollowScale(boolean s) {
		return this;
	}

	@Override
	public IEntity setFollowColor(boolean c) {
		return this;
	}

	@Override
	public boolean isFollowRotation() {
		return false;
	}

	@Override
	public boolean isFollowScale() {
		return false;
	}

	@Override
	public boolean isFollowColor() {
		return false;
	}

	@Override
	public IEntity triggerCollision(SpriteCollisionListener sc) {
		return this;
	}

	@Override
	public int getFlagType() {
		return _sprite.getFlagType();
	}

	@Override
	public int getStatus() {
		return _sprite.getStatus();
	}

	@Override
	public String getObjectFlag() {
		return _sprite.getObjectFlag();
	}

	@Override
	public boolean isStatus(int s) {
		return _sprite.isStatus(s);
	}

	@Override
	public boolean isTag(Object tag) {
		return _sprite.isTag(tag);
	}

	@Override
	public boolean isFlagType(int type) {
		return _sprite.isFlagType(type);
	}

	@Override
	public boolean isObjectFlag(String flag) {
		return _sprite.isObjectFlag(flag);
	}

	@Override
	public IEntity show() {
		_sprite.setVisible(true);
		return this;
	}

	@Override
	public IEntity hide() {
		_sprite.setVisible(false);
		return this;
	}

	@Override
	public IEntity buildToScreen() {
		return this;
	}

	@Override
	public IEntity removeFromScreen() {
		return this;
	}

	@Override
	public IEntity dispose(LRelease r) {
		return this;
	}

}
