/**
 * Copyright 2008 - 2023 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain m00 copy of
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

import loon.utils.MathUtils;

public class Transform implements XYChange<Vector2f> {

	protected final Affine2f _worldTransform;

	protected final Affine2f _localTransform;

	public final ObservableXY<Vector2f> position;

	public final ObservableXY<Vector2f> scale;

	public final ObservableXY<Vector2f> pivot;

	public final ObservableXY<Vector2f> skew;

	protected int _parentId;

	protected int _worldId;

	protected float _rotation;

	protected float _cx;

	protected float _sx;

	protected float _cy;

	protected float _sy;

	protected int _localId;

	protected int _currentLocalId;

	public Transform() {
		this(Vector2f.ZERO(), Vector2f.ONE(), Vector2f.ZERO(), Vector2f.ZERO());
	}

	public Transform(Vector2f pos, Vector2f scale, Vector2f pivot, Vector2f skew) {
		this._worldTransform = new Affine2f();
		this._localTransform = new Affine2f();
		this.position = new ObservableXY<Vector2f>(this, pos, pos);
		this.scale = new ObservableXY<Vector2f>(this, scale, scale);
		this.pivot = new ObservableXY<Vector2f>(this, pivot, pivot);
		this.skew = new ObservableXY<Vector2f>(this, skew, skew);
		this._rotation = 0f;
		this._cx = 1f;
		this._cy = 0f;
		this._sx = 0f;
		this._sy = 1f;
		this._localId = 0;
		this._currentLocalId = 0;
		this._worldId = 0;
		this._parentId = 0;
	}

	protected void onUpdate() {
		this._localId++;
	}

	protected Transform onUpdateSkew() {
		this._cx = MathUtils.cos(this._rotation + this.skew.getY());
		this._sx = MathUtils.sin(this._rotation + this.skew.getY());
		this._cy = -MathUtils.sin(this._rotation - this.skew.getX());
		this._sy = MathUtils.cos(this._rotation - this.skew.getX());
		this.onUpdate();
		return this;
	}

	public float getRotation() {
		return this._rotation;
	}

	public Transform setRotation(float r) {
		if (this._rotation != r) {
			this._rotation = r;
			this.onUpdateSkew();
		}
		return this;
	}

	public Transform setAffine(Affine2f tx) {
		if (tx != null) {
			tx.decompose(this);
			this.onUpdate();
		}
		return this;
	}

	@Override
	public void onUpdate(Vector2f obj) {
		if (obj == null || obj != skew.getObj()) {
			this.onUpdate();
		} else {
			this.onUpdateSkew();
		}
	}

	public Transform updateLocalTransform() {
		final Affine2f lt = this._localTransform;
		if (this._localId != this._currentLocalId) {
			lt.m00 = this._cx * this.scale.getX();
			lt.m01 = this._sx * this.scale.getX();
			lt.m10 = this._cy * this.scale.getY();
			lt.m11 = this._sy * this.scale.getY();
			lt.tx = this.position.getX() - ((this.pivot.getX() * lt.m00) + (this.pivot.getY() * lt.m10));
			lt.ty = this.position.getY() - ((this.pivot.getX() * lt.m01) + (this.pivot.getY() * lt.m11));
			this._currentLocalId = this._localId;
			this._parentId = -1;
		}
		return this;
	}

	public Transform updateTransform(Transform p) {
		final Affine2f lt = this._localTransform;

		if (this._localId != this._currentLocalId) {
			lt.m00 = this._cx * this.scale.getX();
			lt.m01 = this._sx * this.scale.getX();
			lt.m10 = this._cy * this.scale.getY();
			lt.m11 = this._sy * this.scale.getY();

			lt.tx = this.position.getX() - ((this.pivot.getX() * lt.m00) + (this.pivot.getY() * lt.m10));
			lt.ty = this.position.getY() - ((this.pivot.getX() * lt.m01) + (this.pivot.getY() * lt.m11));
			this._currentLocalId = this._localId;

			this._parentId = -1;
		}

		if (this._parentId != p._worldId) {

			final Affine2f pt = p._worldTransform;
			final Affine2f wt = this._worldTransform;

			wt.m00 = (lt.m00 * pt.m00) + (lt.m01 * pt.m10);
			wt.m01 = (lt.m00 * pt.m01) + (lt.m01 * pt.m11);
			wt.m10 = (lt.m10 * pt.m00) + (lt.m11 * pt.m10);
			wt.m11 = (lt.m10 * pt.m01) + (lt.m11 * pt.m11);
			wt.tx = (lt.tx * pt.m00) + (lt.ty * pt.m10) + pt.tx;
			wt.ty = (lt.tx * pt.m01) + (lt.ty * pt.m11) + pt.ty;

			this._parentId = p._worldId;

			this._worldId++;
		}
		return this;
	}

	public Affine2f getWorldTransform() {
		return _worldTransform;
	}

	public Affine2f getLocalTransform() {
		return _localTransform;
	}
	
}
