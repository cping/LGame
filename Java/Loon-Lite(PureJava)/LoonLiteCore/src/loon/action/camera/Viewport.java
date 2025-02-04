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
package loon.action.camera;

import loon.LRelease;
import loon.action.ActionBind;
import loon.action.sprite.ISprite;
import loon.action.sprite.Sprites;
import loon.component.Desktop;
import loon.component.LComponent;
import loon.geom.Affine2f;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimerContext;

public abstract class Viewport implements LRelease {

	private RectBox _bounds = new RectBox();

	private RectBox _viewWorld = new RectBox();

	private RectBox _limitRect = null;

	private boolean _dirty;

	private boolean _useBounds;

	private final Affine2f _view = new Affine2f();

	private float x, y, width, height;
	private float scaleX = 1f, invScaleX = 1f;
	private float scaleY = 1f, invScaleY = 1f;
	private float scrollX;
	private float scrollY;
	private float originX = 0.5f;
	private float originY = 0.5f;
	private float rotation;
	private float previousWindowWidth, previousWindowHeight;
	private float previousScaleX, previousScaleY, previousTranslateX, previousTranslateY;

	private ActionBind _follow;

	private final Vector2f _followOffset = new Vector2f();

	private final Vector2f _lerp = new Vector2f();

	private final Vector2f _centerPoint = new Vector2f();

	private ViewportEffect _effect;

	public abstract void onResize(float windowWidth, float windowHeight);

	public Viewport apply(GLEx g) {

		final float updateWidth = g.getWidth();
		final float updateHeight = g.getHeight();

		if (previousWindowWidth != updateWidth || previousWindowHeight != updateHeight) {

			onResize(updateWidth, updateHeight);
			previousWindowWidth = updateWidth;
			previousWindowHeight = updateHeight;

		}

		previousScaleX = g.getScaleX();
		previousScaleY = g.getScaleY();
		previousTranslateX = g.getTranslationX();
		previousTranslateY = g.getTranslationY();

		float width = this.width;
		float height = this.height;

		float halfWidth = width * 0.5f;
		float halfHeight = height * 0.5f;

		float zoomX = this.scaleX;
		float zoomY = this.scaleY;

		float curOriginX = width * this.originX;
		float curOriginY = height * this.originY;

		ActionBind follow = this._follow;

		float sx = this.scrollX;
		float sy = this.scrollY;

		if (follow != null) {
			Vector2f lerp = this._lerp;

			float fx = follow.getX() + this._followOffset.x;
			float fy = follow.getY() + this._followOffset.y;
			if (this._limitRect != null) {
				if (fx < _limitRect.x) {
					sx = getLinear(sx, sx - (_limitRect.x - fx), lerp.x);
				} else if (fx > _limitRect.getRight()) {
					sx = getLinear(sx, sx + (fx - _limitRect.getRight()), lerp.x);
				}
				if (fy < _limitRect.y) {
					sy = getLinear(sy, sy - (_limitRect.y - fy), lerp.y);
				} else if (fy > _limitRect.getBottom()) {
					sy = getLinear(sy, sy + (fy - _limitRect.getBottom()), lerp.y);
				}
			} else {
				sx = getLinear(sx, fx - curOriginX, lerp.x);
				sy = getLinear(sy, fy - curOriginY, lerp.y);
			}

		}

		if (this._useBounds) {
			sx = this.getClampX(sx);
			sy = this.getClampY(sy);
		}

		this.scrollX = sx;
		this.scrollY = sy;

		float midX = sx + halfWidth;
		float midY = sy + halfHeight;

		this._centerPoint.set(midX, midY);

		float displayWidth = MathUtils.ifloor((width / zoomX) + 0.5f);
		float displayHeight = MathUtils.ifloor((height / zoomY) + 0.5f);

		float newX = MathUtils.ifloor((midX - (displayWidth / 2)) + 0.5f);
		float newY = MathUtils.ifloor((midY - (displayHeight / 2)) + 0.5f);

		g.saveTx();
		if (_dirty) {
			_viewWorld.set(newX, newY, displayWidth, displayHeight);
			_view.applyITRS(MathUtils.ifloor(this.x + curOriginX + 0.5f), MathUtils.ifloor(this.y + curOriginY + 0.5f),
					this.rotation, zoomX, zoomY);
			if (scrollX != 0f || scrollY != 0f) {
				_view.translate(-(curOriginX + newX), -(curOriginY + newY));
			} else {
				_view.translate(-curOriginX, -curOriginY);
			}
			if (_effect != null) {
				_effect.draw(g, _view);
			}
			updateCustom(g, _view);
			_dirty = false;
		}
		g.mulAffine(_view);

		return this;
	}

	public void updateCustom(GLEx g, Affine2f view) {

	}

	public void update(final LTimerContext timer) {
		if (_effect != null) {
			_effect.update(timer);
		}
	}

	public Viewport unapply(GLEx g) {
		g.restoreTx();
		return this;
	}

	public RectBox getViewWorld() {
		return this._viewWorld;
	}

	public Affine2f getView() {
		return this._view;
	}

	public Viewport setDirty(boolean d) {
		this._dirty = d;
		return this;
	}

	public Viewport updateDirty() {
		this._dirty = !this._dirty;
		return this;
	}

	public Viewport follow(ActionBind target, float lerpX, float lerpY) {
		return follow(target, lerpX, lerpY, 0f, 0f);
	}

	public Viewport follow(ActionBind target, float lerpX, float lerpY, float offsetX, float offsetY) {
		this._follow = target;
		lerpX = MathUtils.clamp(lerpX, 0f, 1f);
		lerpY = MathUtils.clamp(lerpY, 0f, 1f);
		this._lerp.set(lerpX, lerpY);
		this._followOffset.set(offsetX, offsetY);
		float originX = this.width / 2;
		float originY = this.height / 2;
		float fx = target.getX() - offsetX;
		float fy = target.getY() - offsetY;
		this._centerPoint.set(fx, fy);
		this.scrollX = fx - originX;
		this.scrollY = fy - originY;
		if (this._useBounds) {
			this.scrollX = this.getClampX(this.scrollX);
			this.scrollY = this.getClampY(this.scrollY);
		}
		this._dirty = true;
		return this;
	}

	public Viewport setLmitRect(float width, float height) {
		if (width <= 0 || height <= 0) {
			this._limitRect = null;
		} else {
			if (this._limitRect != null) {
				this._limitRect.setSize(width, height);
			} else {
				this._limitRect = new RectBox(0, 0, width, height);
			}
			if (this._follow != null) {
				float originX = this.width / 2;
				float originY = this.height / 2;
				float fx = this._follow.getX() - this._followOffset.x;
				float fy = this._follow.getY() - this._followOffset.y;
				this._centerPoint.set(fx, fy);
				this.scrollX = fx - originX;
				this.scrollY = fy - originY;
			}
			centerOn(this._limitRect, this._follow, this._centerPoint.x, this._centerPoint.y);
		}
		return this;
	}

	public void centerOn(RectBox size, ActionBind follow, float x, float y) {

	}

	public void centerOn(float x, float y) {

	}

	public TArray<ActionBind> getCullObjects(Sprites sprites) {
		return getCullObjects(sprites, 1f, 1f, 0.5f, 0.5f);
	}

	public TArray<ActionBind> getCullObjects(Sprites sprites, float scrollFactorX, float scrollFactorY, float originX,
			float originY) {
		ISprite[] sprs = sprites.getSprites();
		TArray<ActionBind> list = new TArray<ActionBind>(sprs.length);
		for (int i = 0; i < list.size; i++) {
			list.add(sprs[i]);
		}
		return getCullObjects(list, scrollFactorX, scrollFactorY, originX, originY);
	}

	public TArray<ActionBind> getCullObjects(Desktop desktop) {
		return getCullObjects(desktop, 1f, 1f, 0.5f, 0.5f);
	}

	public TArray<ActionBind> getCullObjects(Desktop desktop, float scrollFactorX, float scrollFactorY, float originX,
			float originY) {
		LComponent[] comps = desktop.getComponents();
		TArray<ActionBind> list = new TArray<ActionBind>(comps.length);
		for (int i = 0; i < list.size; i++) {
			list.add(comps[i]);
		}
		return getCullObjects(list, scrollFactorX, scrollFactorY, originX, originY);
	}

	public TArray<ActionBind> getCullObjects(TArray<ActionBind> renderableObjects) {
		return getCullObjects(renderableObjects, 1f, 1f, 0.5f, 0.5f);
	}

	public TArray<ActionBind> getCullObjects(TArray<ActionBind> renderableObjects, float scrollFactorX,
			float scrollFactorY, float originX, float originY) {

		TArray<ActionBind> cullObjects = new TArray<ActionBind>();
		Affine2f cameraMatrix = this._view;

		float mva = cameraMatrix.m00;
		float mvb = cameraMatrix.m01;
		float mvc = cameraMatrix.m10;
		float mvd = cameraMatrix.m11;

		float determinant = (mva * mvd) - (mvb * mvc);

		if (determinant <= 0f) {
			return renderableObjects;
		}

		float mve = cameraMatrix.tx;
		float mvf = cameraMatrix.ty;

		float scrollX = this.scrollX;
		float scrollY = this.scrollY;
		float cameraW = this.width;
		float cameraH = this.height;
		float cullTop = this.y;
		float cullBottom = cullTop + cameraH;
		float cullLeft = this.x;
		float cullRight = cullLeft + cameraW;

		float length = renderableObjects.size;

		determinant = 1 / determinant;

		for (int index = 0; index < length; ++index) {
			ActionBind object = renderableObjects.get(index);

			float objectW = object.getWidth();
			float objectH = object.getHeight();
			float objectX = object.getX() - (scrollX * scrollFactorX) - (objectW * originX);
			float objectY = object.getY() - (scrollY * scrollFactorY) - (objectH * originY);
			float tx = (objectX * mva + objectY * mvc + mve);
			float ty = (objectX * mvb + objectY * mvd + mvf);
			float tw = ((objectX + objectW) * mva + (objectY + objectH) * mvc + mve);
			float th = ((objectX + objectW) * mvb + (objectY + objectH) * mvd + mvf);

			if ((tw > cullLeft && tx < cullRight) && (th > cullTop && ty < cullBottom)) {
				cullObjects.add(object);
			}
		}

		return cullObjects;
	}

	public Vector2f getWorldPoint(float x, float y) {
		return getWorldPoint(x, y, new Vector2f());
	}

	public Vector2f getWorldPoint(float x, float y, Vector2f o) {

		if (o == null) {
			o = new Vector2f();
		}

		Affine2f matrix = this._view;

		float mva = matrix.m00;
		float mvb = matrix.m01;
		float mvc = matrix.m10;
		float mvd = matrix.m11;
		float mve = matrix.tx;
		float mvf = matrix.ty;

		float determinant = (mva * mvd) - (mvb * mvc);

		if (determinant <= 0) {
			o.x = x;
			o.y = y;
			return o;
		}

		determinant = 1 / determinant;

		float ima = mvd * determinant;
		float imb = -mvb * determinant;
		float imc = -mvc * determinant;
		float imd = mva * determinant;
		float ime = (mvc * mvf - mvd * mve) * determinant;
		float imf = (mvb * mve - mva * mvf) * determinant;

		float c = MathUtils.cos(this.rotation);
		float s = MathUtils.sin(this.rotation);

		float zoomX = this.scaleX;
		float zoomY = this.scaleY;

		float scrollX = this.scrollX;
		float scrollY = this.scrollY;

		float sx = x + ((scrollX * c - scrollY * s) * zoomX);
		float sy = y + ((scrollX * s + scrollY * c) * zoomY);

		o.x = (sx * ima + sy * imc) + ime;
		o.y = (sx * imb + sy * imd) + imf;

		return o;
	}

	public Viewport setAngle(float r) {
		this.rotation = MathUtils.toRadians(r);
		this._dirty = true;
		return this;
	}

	public float getAngle() {
		return MathUtils.toDegrees(this.rotation);
	}

	public Viewport centerToSize() {
		this.scrollX = this.width * 0.5f;
		this.scrollY = this.height * 0.5f;
		this._dirty = true;
		return this;
	}

	public Viewport centerToBounds() {
		if (this._useBounds) {
			RectBox bounds = this._bounds;
			float originX = this.width * 0.5f;
			float originY = this.height * 0.5f;
			this._centerPoint.set(bounds.getCenterX(), bounds.getCenterY());
			this.scrollX = bounds.getCenterX() - originX;
			this.scrollY = bounds.getCenterY() - originY;
			this._dirty = true;
		}
		return this;
	}

	public Viewport centerOnX(float x) {
		float originX = this.width * 0.5f;
		this._centerPoint.x = x;
		this.scrollX = x - originX;
		if (this._useBounds) {
			this.scrollX = this.getClampX(this.scrollX);
		}
		return this;
	}

	public Viewport centerOnY(float y) {
		float originY = this.height * 0.5f;
		this._centerPoint.y = y;
		this.scrollY = y - originY;
		if (this._useBounds) {
			this.scrollY = this.getClampY(this.scrollY);
		}
		return this;
	}

	public float getLinear(float p0, float p1, float t) {
		return (p1 - p0) * t + p0;
	}

	public Viewport toScreenCoordinates(Vector2f result, float worldX, float worldY) {
		result.x = (worldX * scaleX) + x;
		result.y = (worldY * scaleY) + y;
		return this;
	}

	public Viewport toWorldCoordinates(Vector2f result, float screenX, float screenY) {
		result.x = (screenX - x) * invScaleX;
		result.y = (screenY - y) * invScaleY;
		return this;
	}

	public Viewport toScreenCoordinates(Vector2f worldCoordinates) {
		return toScreenCoordinates(worldCoordinates, worldCoordinates.x, worldCoordinates.y);
	}

	public Viewport toWorldCoordinates(Vector2f screenCoordinates) {
		return toWorldCoordinates(screenCoordinates, screenCoordinates.x, screenCoordinates.y);
	}

	protected Viewport setBounds(float width, float height) {
		return setBounds(width, height, this.scaleX, this.scaleY);
	}

	protected Viewport setBounds(float width, float height, float scaleX, float scaleY) {
		return setBounds(this.x, this.y, width, height, scaleX, scaleY);
	}

	protected Viewport setBounds(float x, float y, float width, float height, float scaleX, float scaleY) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.scaleX = scaleX;
		this.invScaleX = 1f / scaleX;
		this.scaleY = scaleY;
		this.invScaleY = 1f / scaleY;
		this._centerPoint.set(width / 2f, height / 2f);
		this._dirty = true;
		return this;
	}

	public Vector2f getScroll(float x, float y) {
		return getScroll(x, y, new Vector2f());
	}

	public Vector2f getScroll(float x, float y, Vector2f o) {
		if (o == null) {
			o = new Vector2f();
		}
		float originX = this.width * 0.5f;
		float originY = this.height * 0.5f;
		o.x = x - originX;
		o.y = y - originY;
		if (this._useBounds) {
			o.x = this.getClampX(o.x);
			o.y = this.getClampY(o.y);
		}
		return o;
	}

	public float getDisplayWidth() {
		return this.width * this.scaleX;
	}

	public float getDisplayHeight() {
		return this.height * this.scaleY;
	}

	public float getClampX(float x) {
		RectBox bounds = this._bounds;
		float dw = this.getDisplayWidth();
		float bx = bounds.x + ((dw - this.width) / 2);
		float bw = MathUtils.max(bx, bx + bounds.width - dw);
		if (x < bx) {
			x = bx;
		} else if (x > bw) {
			x = bw;
		}
		return x;
	}

	public float getClampY(float y) {
		RectBox bounds = this._bounds;
		float dh = this.getDisplayHeight();
		float by = bounds.y + ((dh - this.height) / 2);
		float bh = MathUtils.max(by, by + bounds.height - dh);
		if (y < by) {
			y = by;
		} else if (y > bh) {
			y = bh;
		}
		return y;
	}

	public Viewport removeBounds() {
		this._useBounds = false;
		this._dirty = true;
		this._bounds.setEmpty();
		return this;
	}

	public Viewport setLocation(float x, float y) {
		this.x = x;
		this.y = y;
		this._dirty = true;
		return this;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public Viewport setSize(float w, float h) {
		this.width = w;
		this.height = h;
		this._dirty = true;
		return this;
	}

	public Viewport setSize(float x, float y, float width, float height) {
		return setBounds(x, y, width, height, 1f, 1f);
	}

	public Viewport setSize(float x, float y, float width, float height, float sx, float sy) {
		return setBounds(x, y, width, height, sx, sy);
	}

	public boolean isScaled() {
		return scaleX != 1f && scaleY != 1f;
	}

	public Vector2f getScale() {
		return new Vector2f(scaleX, scaleY);
	}

	public float getScaleX() {
		return scaleX;
	}

	public float getInvScaleX() {
		return invScaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public float getInvScaleY() {
		return invScaleY;
	}

	public float getPreviousWindowWidth() {
		return previousWindowWidth;
	}

	public float getPreviousWindowHeight() {
		return previousWindowHeight;
	}

	public float getPreviousScaleX() {
		return previousScaleX;
	}

	public float getPreviousScaleY() {
		return previousScaleY;
	}

	public float getPreviousTranslateX() {
		return previousTranslateX;
	}

	public float getPreviousTranslateY() {
		return previousTranslateY;
	}

	public Viewport setZoom(float x, float y) {
		this.scaleX = x;
		this.scaleY = y;
		this._dirty = true;
		return this;
	}

	public Viewport setScroll(float x, float y) {
		this.scrollX = x;
		this.scrollY = y;
		this._dirty = true;
		return this;
	}

	public float getScrollX() {
		return scrollX;
	}

	public Viewport setScrollX(float x) {
		this.scrollX = x;
		this._dirty = true;
		return this;
	}

	public float getScrollY() {
		return scrollY;
	}

	public Viewport setScrollY(float y) {
		this.scrollY = y;
		this._dirty = true;
		return this;
	}

	public float getOriginX() {
		return originX;
	}

	public Viewport setOriginX(float x) {
		this.originX = x;
		this._dirty = true;
		return this;
	}

	public float getOriginY() {
		return originY;
	}

	public Viewport setOriginY(float y) {
		this.originY = y;
		this._dirty = true;
		return this;
	}

	public ActionBind getFollow() {
		return _follow;
	}

	public Viewport setFollow(ActionBind f) {
		this._follow = f;
		this._dirty = true;
		return this;
	}

	public Vector2f getFollowOffset() {
		return _followOffset;
	}

	public Viewport setFollowOffset(float x, float y) {
		this._followOffset.set(x, y);
		this._dirty = true;
		return this;
	}

	public Viewport setFollowOffset(Vector2f f) {
		return setFollowOffset(f.x, f.y);
	}

	public float getZoomWidth() {
		return this.scaleX * this.width;
	}

	public float getZoomHeight() {
		return this.scaleY * this.height;
	}

	public Viewport setX(float x) {
		this.x = x;
		this._dirty = true;
		return this;
	}

	public Viewport setY(float y) {
		this.y = y;
		this._dirty = true;
		return this;
	}

	public Viewport setWidth(float w) {
		this.width = w;
		this._dirty = true;
		return this;
	}

	public Viewport setHeight(float h) {
		this.height = h;
		this._dirty = true;
		return this;
	}

	public Viewport setScale(float s) {
		return setScale(s, s);
	}

	public Viewport setScale(Vector2f v) {
		return setScale(v.x, v.y);
	}

	public Viewport setScale(float x, float y) {
		this.setScaleX(x);
		this.setScaleY(y);
		return this;
	}

	public Viewport setScaleX(float x) {
		this.scaleX = x;
		this._dirty = true;
		return this;
	}

	public Viewport setScaleY(float y) {
		this.scaleY = y;
		this._dirty = true;
		return this;
	}

	public boolean isUseBounds() {
		return _useBounds;
	}

	public Viewport setUseBounds(boolean b) {
		this._useBounds = b;
		this._dirty = true;
		return this;
	}

	public Vector2f getLerp() {
		return this._lerp;
	}

	public Viewport setLerp(float x, float y) {
		this._lerp.set(x, y);
		this._dirty = true;
		return this;
	}

	public Viewport setEffect(ViewportEffect e) {
		this._effect = e;
		return this;
	}

	public ViewportEffect getEffect() {
		return this._effect;
	}

	@Override
	public void close() {
		if (_effect != null) {
			_effect.close();
		}
	}
}
