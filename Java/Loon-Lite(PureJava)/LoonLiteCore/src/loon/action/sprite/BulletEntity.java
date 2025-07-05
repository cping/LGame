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

import java.util.Iterator;

import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.action.collision.CollisionAction;
import loon.action.collision.CollisionFilter;
import loon.action.collision.CollisionManager;
import loon.action.collision.CollisionObject;
import loon.action.collision.CollisionResult;
import loon.action.collision.CollisionWorld;
import loon.action.map.Config;
import loon.action.map.Side;
import loon.action.sprite.Bullet.WaveType;
import loon.canvas.LColor;
import loon.geom.RangeF;
import loon.geom.RectBox;
import loon.geom.Shape;
import loon.geom.Vector2f;
import loon.geom.XYZW;
import loon.opengl.GLEx;
import loon.opengl.LTextureFree;
import loon.utils.Easing.EasingMode;
import loon.utils.IntArray;
import loon.utils.MathUtils;
import loon.utils.ObjectSet;
import loon.utils.TArray;

/**
 * 子弹用Entity,用来显示以及管理子弹(复数)用类
 */
public class BulletEntity extends Entity {

	/**
	 * 子弹状态监听用类
	 */
	public static interface BulletListener {

		public void attached(Bullet bullet);

		public void detached(Bullet bullet);

		public void drawable(GLEx g, Bullet bullet);

		public void updateable(long elapsedTime, Bullet bullet);

		public void lifeover(Bullet bullet);

		public void easeover(Bullet bullet);
	}

	public static float getBallisticRange(float speed, float gravity, float iheight) {
		float angle = 45f * MathUtils.DEG_TO_RAD;
		float cos = MathUtils.cos(angle);
		float sin = MathUtils.sin(angle);
		float range = (speed * cos / gravity)
				* (speed * sin + MathUtils.sqrt(speed * speed * sin * sin + 2f * gravity * iheight));
		return range;
	}

	private IntArray _collisionIgnoreTypes;

	private ObjectSet<String> _collisionIgnoreStrings;

	private CollisionWorld _collisionWorld;

	private CollisionFilter _worldCollisionFilter;

	private CollisionAction<CollisionObject> _collisionActionListener;

	private BulletListener _bulletListener;

	private boolean _checkCollision;

	private boolean _allowAutoFixMoved;

	private boolean _running;

	private boolean _autoRemoveOfBounds;

	private boolean _limitMovedOfBounds;

	private boolean _selfWorld;

	private RangeF _limitRangeX;

	private RangeF _limitRangeY;

	private TArray<Bullet> bullets;

	private LTextureFree textureFree;

	private EasingMode easingMode;

	public BulletEntity() {
		this(null);
	}

	public BulletEntity(int x, int y) {
		this(null, x, y, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public BulletEntity(int x, int y, int w, int h) {
		this(null, x, y, w, h);
	}

	public BulletEntity(CollisionWorld world) {
		this(world, 0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public BulletEntity(CollisionWorld world, int x, int y, int w, int h) {
		this.easingMode = EasingMode.Linear;
		this.bullets = new TArray<Bullet>(LSystem.DEFAULT_MAX_CACHE_SIZE);
		this.textureFree = new LTextureFree();
		if (world != null) {
			this._collisionWorld = world;
			this._selfWorld = false;
		} else {
			this._collisionWorld = new CollisionWorld();
			this._selfWorld = true;
		}
		this._running = true;
		this._autoRemoveOfBounds = true;
		this.setRepaint(true);
		this.setLocation(x, y);
		this.setSize(w, h);
	}

	public BulletEntity setListener(BulletListener l) {
		this._bulletListener = l;
		return this;
	}

	public BulletListener getListener() {
		return this._bulletListener;
	}

	public BulletEntity setLimitMoveRangeOfSelf() {
		return setLimitMoveRangeOfSelf(0f, 0f, 0f, 0f);
	}

	public BulletEntity setLimitMoveRangeOfSelf(float offsetX, float offsetY) {
		return setLimitMoveRangeOf(getX() + offsetX, getWidth() - offsetX, getY() + offsetY, getHeight() - offsetY);
	}

	public BulletEntity setLimitMoveRangeOfSelf(float offsetX, float offsetY, float offsetWidth, float offsetHeight) {
		return setLimitMoveRangeOf(getX() + offsetX, getWidth() + offsetWidth, getY() + offsetY,
				getHeight() + offsetHeight);
	}

	public BulletEntity setLimitMoveRangeOf(XYZW rect) {
		if (rect == null) {
			return this;
		}
		return setLimitMoveRangeOf(rect.getX(), rect.getZ(), rect.getY(), rect.getW());
	}

	public BulletEntity setLimitMoveRangeOf(float minX, float maxX, float minY, float maxY) {
		setLimitRangeX(minX, maxX);
		setLimitRangeY(minY, maxY);
		return this;
	}

	public BulletEntity setLimitRangeX(float min, float max) {
		if (_limitRangeX == null) {
			_limitRangeX = new RangeF(min, max);
		} else {
			_limitRangeX.set(min, max);
		}
		_limitMovedOfBounds = true;
		return this;
	}

	public BulletEntity setLimitRangeY(float min, float max) {
		if (_limitRangeY == null) {
			_limitRangeY = new RangeF(min, max);
		} else {
			_limitRangeX.set(min, max);
		}
		_limitMovedOfBounds = true;
		return this;
	}

	public RangeF getLimitRangeX() {
		return this._limitRangeX;
	}

	public RangeF getLimitRangeY() {
		return this._limitRangeY;
	}

	public BulletEntity clearLimitRangeX() {
		this._limitRangeX = null;
		if (this._limitRangeY == null) {
			this._limitMovedOfBounds = false;
		}
		return this;
	}

	public BulletEntity clearLimitRangeY() {
		this._limitRangeY = null;
		if (this._limitRangeX == null) {
			this._limitMovedOfBounds = false;
		}
		return this;
	}

	public BulletEntity clearLimitRange() {
		this._limitRangeX = null;
		this._limitRangeY = null;
		this._limitMovedOfBounds = false;
		return this;
	}

	public BulletEntity setCollisionWorld(CollisionWorld world) {
		if (world == null) {
			return this;
		}
		if (_selfWorld && _collisionWorld != null) {
			_collisionWorld.close();
		}
		this._selfWorld = false;
		this._collisionWorld = world;
		return this;
	}

	public CollisionManager getCollisionManager() {
		if (_collisionWorld == null) {
			_collisionWorld = new CollisionWorld();
			_selfWorld = true;
		}
		return _collisionWorld.getCollisionManager();
	}

	/**
	 * 在指定位置同时添加一组子弹,自动成圆形扩散发射(弹幕效果)
	 * 
	 * @param easing
	 * @param ani
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @param size
	 * @param space
	 * @return
	 */
	public TArray<Bullet> addCircleBullets(EasingMode easing, Animation ani, float startX, float startY, float w,
			float h, float size, float space) {
		TArray<Bullet> result = new TArray<Bullet>();
		float count = MathUtils.DEG_FULL / size;
		float dot = count / 10f;
		for (int i = 0; i < MathUtils.DEG_FULL; i += count) {
			float newX = startX + MathUtils.cos(MathUtils.toRadians(i + space)) * dot;
			float newY = startY + MathUtils.sin(MathUtils.toRadians(i + space)) * dot;
			Bullet bullet = new Bullet(easing, ani, startX, startY);
			if (w != -1f && h != -1f) {
				bullet.setSize(w, h);
			}
			bullet.fireTo(newX, newY);
			addBullet(bullet);
			result.add(bullet);
		}
		return result;
	}

	/**
	 * 在指定位置同时添加一组子弹,自动成圆形扩散发射(弹幕效果)
	 * 
	 * @param easing
	 * @param ani
	 * @param startX
	 * @param startY
	 * @return
	 */
	public TArray<Bullet> addCircleBullets(EasingMode easing, Animation ani, float startX, float startY) {
		return addCircleBullets(easing, ani, startX, startY, 6f);
	}

	/**
	 * 在指定位置同时添加一组子弹,自动成圆形扩散发射(弹幕效果)
	 * 
	 * @param easing
	 * @param ani
	 * @param startX
	 * @param startY
	 * @param size
	 * @return
	 */
	public TArray<Bullet> addCircleBullets(EasingMode easing, Animation ani, float startX, float startY, float size) {
		return addCircleBullets(easing, ani, startX, startY, -1f, -1f, size, 30f);
	}

	/**
	 * 在指定位置同时添加一组子弹,自动成圆形扩散发射(弹幕效果)
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @return
	 */
	public TArray<Bullet> addCircleBullets(String path, float startX, float startY) {
		return addCircleBullets(path, startX, startY, 6f);
	}

	/**
	 * 在指定位置同时添加一组子弹,自动成圆形扩散发射(弹幕效果)
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @param size
	 * @return
	 */
	public TArray<Bullet> addCircleBullets(String path, float startX, float startY, float size) {
		return addCircleBullets(easingMode, path, startX, startY, size);
	}

	/**
	 * 在指定位置同时添加一组子弹,自动成圆形扩散发射(弹幕效果)
	 * 
	 * @param easing
	 * @param path
	 * @param startX
	 * @param startY
	 * @param size
	 * @return
	 */
	public TArray<Bullet> addCircleBullets(EasingMode easing, String path, float startX, float startY, float size) {
		return addCircleBullets(easing, LTextures.loadTexture(path), startX, startY, size);
	}

	/**
	 * 在指定位置同时添加一组子弹,自动成圆形扩散发射(弹幕效果)
	 * 
	 * @param easing
	 * @param tex
	 * @param startX
	 * @param startY
	 * @param size
	 * @return
	 */
	public TArray<Bullet> addCircleBullets(EasingMode easing, LTexture tex, float startX, float startY, float size) {
		return addCircleBullets(easing, tex, startX, startY, -1f, -1f, size, 30f);
	}

	/**
	 * 在指定位置同时添加一组子弹,自动成圆形扩散发射(弹幕效果)
	 * 
	 * @param easing
	 * @param tex
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @param size
	 * @param space
	 * @return
	 */
	public TArray<Bullet> addCircleBullets(EasingMode easing, LTexture tex, float startX, float startY, float w,
			float h, float size, float space) {
		return addCircleBullets(easing, Animation.getDefaultAnimation(tex), startX, startY, w, h, size, space);
	}

	/**
	 * 同时向上下两方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @return
	 */
	public TArray<Bullet> addUpDownBullets(String path, float startX, float startY) {
		return addUpDownBullets(path, startX, startY, -1f, -1f);
	}

	/**
	 * 同时向上下两方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addUpDownBullets(String path, float startX, float startY, float w, float h) {
		return addUpDownBullets(easingMode, LTextures.loadTexture(path), startX, startY, w, h);
	}

	/**
	 * 同时向上下两方向发射子弹
	 * 
	 * @param easing
	 * @param tex
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addUpDownBullets(EasingMode easing, LTexture tex, float startX, float startY, float w,
			float h) {
		return addUpDownBullets(easing, Animation.getDefaultAnimation(tex), startX, startY, w, h);
	}

	/**
	 * 同时向上下两方向发射子弹
	 * 
	 * @param easing
	 * @param ani
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addUpDownBullets(EasingMode easing, Animation ani, float startX, float startY, float w,
			float h) {
		TArray<Bullet> result = new TArray<Bullet>();
		Bullet upBullet = new Bullet(easing, ani, startX, startY, Config.TUP);
		if (w != -1f && h != -1f) {
			upBullet.setSize(w, h);
		}
		addBullet(upBullet);
		result.add(upBullet);
		Bullet downBullet = new Bullet(easing, ani, startX, startY, Config.TDOWN);
		if (w != -1f && h != -1f) {
			downBullet.setSize(w, h);
		}
		addBullet(downBullet);
		result.add(downBullet);
		return result;
	}

	/**
	 * 子弹呈十字方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @return
	 */
	public TArray<Bullet> addCorssBullets(String path, float startX, float startY) {
		return addCorssBullets(path, startX, startY, -1f, -1f);
	}

	/**
	 * 子弹呈十字方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addCorssBullets(String path, float startX, float startY, float w, float h) {
		return addCorssBullets(easingMode, LTextures.loadTexture(path), startX, startY, w, h);
	}

	/**
	 * 子弹呈十字方向发射子弹
	 * 
	 * @param easing
	 * @param tex
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addCorssBullets(EasingMode easing, LTexture tex, float startX, float startY, float w,
			float h) {
		return addCorssBullets(easing, Animation.getDefaultAnimation(tex), startX, startY, w, h);
	}

	/**
	 * 子弹呈十字方向发射子弹
	 * 
	 * @param easing
	 * @param ani
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addCorssBullets(EasingMode easing, Animation ani, float startX, float startY, float w,
			float h) {
		TArray<Bullet> result = new TArray<Bullet>();
		Bullet upBullet = new Bullet(easing, ani, startX, startY, Config.TUP);
		if (w != -1f && h != -1f) {
			upBullet.setSize(w, h);
		}
		addBullet(upBullet);
		result.add(upBullet);
		Bullet downBullet = new Bullet(easing, ani, startX, startY, Config.TDOWN);
		if (w != -1f && h != -1f) {
			downBullet.setSize(w, h);
		}
		addBullet(downBullet);
		result.add(downBullet);
		Bullet leftBullet = new Bullet(easing, ani, startX, startY, Config.TLEFT);
		if (w != -1f && h != -1f) {
			leftBullet.setSize(w, h);
		}
		addBullet(leftBullet);
		result.add(leftBullet);
		Bullet rightBullet = new Bullet(easing, ani, startX, startY, Config.TRIGHT);
		if (w != -1f && h != -1f) {
			rightBullet.setSize(w, h);
		}
		addBullet(rightBullet);
		result.add(rightBullet);
		return result;
	}

	/**
	 * 子弹呈X字方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @return
	 */
	public TArray<Bullet> addXBullets(String path, float startX, float startY) {
		return addXBullets(path, startX, startY, -1f, -1f);
	}

	/**
	 * 子弹呈X字方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addXBullets(String path, float startX, float startY, float w, float h) {
		return addXBullets(easingMode, LTextures.loadTexture(path), startX, startY, w, h);
	}

	/**
	 * 子弹呈X字方向发射子弹
	 * 
	 * @param easing
	 * @param tex
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addXBullets(EasingMode easing, LTexture tex, float startX, float startY, float w, float h) {
		return addXBullets(easing, Animation.getDefaultAnimation(tex), startX, startY, w, h);
	}

	/**
	 * 子弹呈X字方向发射子弹
	 * 
	 * @param easing
	 * @param ani
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addXBullets(EasingMode easing, Animation ani, float startX, float startY, float w, float h) {
		TArray<Bullet> result = new TArray<Bullet>();
		Bullet upBullet = new Bullet(easing, ani, startX, startY, Config.UP);
		if (w != -1f && h != -1f) {
			upBullet.setSize(w, h);
		}
		addBullet(upBullet);
		result.add(upBullet);
		Bullet downBullet = new Bullet(easing, ani, startX, startY, Config.DOWN);
		if (w != -1f && h != -1f) {
			downBullet.setSize(w, h);
		}
		addBullet(downBullet);
		result.add(downBullet);
		Bullet leftBullet = new Bullet(easing, ani, startX, startY, Config.LEFT);
		if (w != -1f && h != -1f) {
			leftBullet.setSize(w, h);
		}
		addBullet(leftBullet);
		result.add(leftBullet);
		Bullet rightBullet = new Bullet(easing, ani, startX, startY, Config.RIGHT);
		if (w != -1f && h != -1f) {
			rightBullet.setSize(w, h);
		}
		addBullet(rightBullet);
		result.add(rightBullet);
		return result;
	}

	/**
	 * 子弹呈斜角上下方向发射
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @return
	 */
	public TArray<Bullet> addBevelUpDownBullets(String path, float startX, float startY) {
		return addBevelUpDownBullets(path, startX, startY, -1f, -1f);
	}

	/**
	 * 子弹呈斜角上下方向发射
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addBevelUpDownBullets(String path, float startX, float startY, float w, float h) {
		return addBevelUpDownBullets(easingMode, LTextures.loadTexture(path), startX, startY, w, h);
	}

	/**
	 * 子弹呈斜角上下方向发射
	 * 
	 * @param easing
	 * @param tex
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addBevelUpDownBullets(EasingMode easing, LTexture tex, float startX, float startY, float w,
			float h) {
		return addBevelUpDownBullets(easing, Animation.getDefaultAnimation(tex), startX, startY, w, h);
	}

	/**
	 * 子弹呈斜角上下方向发射
	 * 
	 * @param easing
	 * @param ani
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addBevelUpDownBullets(EasingMode easing, Animation ani, float startX, float startY, float w,
			float h) {
		TArray<Bullet> result = new TArray<Bullet>();
		Bullet upBullet = new Bullet(easing, ani, startX, startY, Config.UP);
		if (w != -1f && h != -1f) {
			upBullet.setSize(w, h);
		}
		addBullet(upBullet);
		result.add(upBullet);
		Bullet downBullet = new Bullet(easing, ani, startX, startY, Config.DOWN);
		if (w != -1f && h != -1f) {
			downBullet.setSize(w, h);
		}
		addBullet(downBullet);
		result.add(downBullet);
		return result;
	}

	/**
	 * 子弹斜角左右方向发射
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @return
	 */
	public TArray<Bullet> addBevelLeftRightBullets(String path, float startX, float startY) {
		return addBevelLeftRightBullets(path, startX, startY, -1f, -1f);
	}

	/**
	 * 子弹斜角左右方向发射
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addBevelLeftRightBullets(String path, float startX, float startY, float w, float h) {
		return addBevelLeftRightBullets(easingMode, LTextures.loadTexture(path), startX, startY, w, h);
	}

	/**
	 * 子弹斜角左右方向发射
	 * 
	 * @param easing
	 * @param tex
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addBevelLeftRightBullets(EasingMode easing, LTexture tex, float startX, float startY, float w,
			float h) {
		return addBevelLeftRightBullets(easing, Animation.getDefaultAnimation(tex), startX, startY, w, h);
	}

	/**
	 * 子弹斜角左右方向发射
	 * 
	 * @param easing
	 * @param ani
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addBevelLeftRightBullets(EasingMode easing, Animation ani, float startX, float startY,
			float w, float h) {
		TArray<Bullet> result = new TArray<Bullet>();
		Bullet leftBullet = new Bullet(easing, ani, startX, startY, Config.LEFT);
		if (w != -1f && h != -1f) {
			leftBullet.setSize(w, h);
		}
		addBullet(leftBullet);
		result.add(leftBullet);
		Bullet rightBullet = new Bullet(easing, ani, startX, startY, Config.RIGHT);
		if (w != -1f && h != -1f) {
			rightBullet.setSize(w, h);
		}
		addBullet(rightBullet);
		result.add(rightBullet);
		return result;
	}

	/**
	 * 同时向左右两方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @return
	 */
	public TArray<Bullet> addLeftRightBullets(String path, float startX, float startY) {
		return addLeftRightBullets(path, startX, startY, -1f, -1f);
	}

	/**
	 * 同时向左右两方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addLeftRightBullets(String path, float startX, float startY, float w, float h) {
		return addLeftRightBullets(easingMode, LTextures.loadTexture(path), startX, startY, w, h);
	}

	/**
	 * 同时向左右两方向发射子弹
	 * 
	 * @param easing
	 * @param tex
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addLeftRightBullets(EasingMode easing, LTexture tex, float startX, float startY, float w,
			float h) {
		return addLeftRightBullets(easing, Animation.getDefaultAnimation(tex), startX, startY, w, h);
	}

	/**
	 * 同时向左右两方向发射子弹
	 * 
	 * @param easing
	 * @param ani
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addLeftRightBullets(EasingMode easing, Animation ani, float startX, float startY, float w,
			float h) {
		TArray<Bullet> result = new TArray<Bullet>();
		Bullet leftBullet = new Bullet(easing, ani, startX, startY, Config.TLEFT);
		if (w != -1f && h != -1f) {
			leftBullet.setSize(w, h);
		}
		addBullet(leftBullet);
		result.add(leftBullet);
		Bullet rightBullet = new Bullet(easing, ani, startX, startY, Config.TRIGHT);
		if (w != -1f && h != -1f) {
			rightBullet.setSize(w, h);
		}
		addBullet(rightBullet);
		result.add(rightBullet);
		return result;
	}

	/**
	 * 同时向下方三个方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @return
	 */
	public TArray<Bullet> addThreeDownBullets(String path, float startX, float startY) {
		return addThreeDownBullets(path, startX, startY, -1f, -1f);
	}

	/**
	 * 同时向下方三个方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addThreeDownBullets(String path, float startX, float startY, float w, float h) {
		return addThreeDownBullets(easingMode, LTextures.loadTexture(path), startX, startY, w, h);
	}

	/**
	 * 同时向下方三个方向发射子弹
	 * 
	 * @param easing
	 * @param tex
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addThreeDownBullets(EasingMode easing, LTexture tex, float startX, float startY, float w,
			float h) {
		return addThreeDownBullets(easing, Animation.getDefaultAnimation(tex), startX, startY, w, h);
	}

	/**
	 * 同时向下方三个方向发射子弹
	 * 
	 * @param easing
	 * @param ani
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addThreeDownBullets(EasingMode easing, Animation ani, float startX, float startY, float w,
			float h) {
		TArray<Bullet> result = new TArray<Bullet>();
		Bullet downBullet = new Bullet(easing, ani, startX, startY, Config.TDOWN);
		if (w != -1f && h != -1f) {
			downBullet.setSize(w, h);
		}
		addBullet(downBullet);
		result.add(downBullet);
		Bullet dupBullet = new Bullet(easing, ani, startX, startY, Config.DOWN);
		if (w != -1f && h != -1f) {
			dupBullet.setSize(w, h);
		}
		addBullet(dupBullet);
		result.add(dupBullet);
		Bullet drightBullet = new Bullet(easing, ani, startX, startY, Config.RIGHT);
		if (w != -1f && h != -1f) {
			drightBullet.setSize(w, h);
		}
		addBullet(drightBullet);
		result.add(drightBullet);
		return result;
	}

	/**
	 * 同时向上方三个方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @return
	 */
	public TArray<Bullet> addThreeUpBullets(String path, float startX, float startY) {
		return addThreeUpBullets(path, startX, startY, -1f, -1f);
	}

	/**
	 * 同时向上方三个方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addThreeUpBullets(String path, float startX, float startY, float w, float h) {
		return addThreeUpBullets(easingMode, LTextures.loadTexture(path), startX, startY, w, h);
	}

	/**
	 * 同时向上方三个方向发射子弹
	 * 
	 * @param easing
	 * @param tex
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addThreeUpBullets(EasingMode easing, LTexture tex, float startX, float startY, float w,
			float h) {
		return addThreeUpBullets(easing, Animation.getDefaultAnimation(tex), startX, startY, w, h);
	}

	/**
	 * 同时向上方三个方向发射子弹
	 * 
	 * @param easing
	 * @param ani
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addThreeUpBullets(EasingMode easing, Animation ani, float startX, float startY, float w,
			float h) {
		TArray<Bullet> result = new TArray<Bullet>();
		Bullet upBullet = new Bullet(easing, ani, startX, startY, Config.TUP);
		if (w != -1f && h != -1f) {
			upBullet.setSize(w, h);
		}
		addBullet(upBullet);
		result.add(upBullet);
		Bullet dupBullet = new Bullet(easing, ani, startX, startY, Config.UP);
		if (w != -1f && h != -1f) {
			dupBullet.setSize(w, h);
		}
		addBullet(dupBullet);
		result.add(dupBullet);
		Bullet leftBullet = new Bullet(easing, ani, startX, startY, Config.LEFT);
		if (w != -1f && h != -1f) {
			leftBullet.setSize(w, h);
		}
		addBullet(leftBullet);
		result.add(leftBullet);
		return result;
	}

	/**
	 * 同时向左方三个方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @return
	 */
	public TArray<Bullet> addThreeLeftBullets(String path, float startX, float startY) {
		return addThreeLeftBullets(path, startX, startY, -1f, -1f);
	}

	/**
	 * 同时向左方三个方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addThreeLeftBullets(String path, float startX, float startY, float w, float h) {
		return addThreeLeftBullets(easingMode, LTextures.loadTexture(path), startX, startY, w, h);
	}

	/**
	 * 同时向左方三个方向发射子弹
	 * 
	 * @param easing
	 * @param tex
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addThreeLeftBullets(EasingMode easing, LTexture tex, float startX, float startY, float w,
			float h) {
		return addThreeLeftBullets(easing, Animation.getDefaultAnimation(tex), startX, startY, w, h);
	}

	/**
	 * 同时向左方三个方向发射子弹
	 * 
	 * @param easing
	 * @param ani
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addThreeLeftBullets(EasingMode easing, Animation ani, float startX, float startY, float w,
			float h) {
		TArray<Bullet> result = new TArray<Bullet>();
		Bullet leftBullet = new Bullet(easing, ani, startX, startY, Config.TLEFT);
		if (w != -1f && h != -1f) {
			leftBullet.setSize(w, h);
		}
		addBullet(leftBullet);
		result.add(leftBullet);
		Bullet dleftBullet = new Bullet(easing, ani, startX, startY, Config.LEFT);
		if (w != -1f && h != -1f) {
			dleftBullet.setSize(w, h);
		}
		addBullet(dleftBullet);
		result.add(dleftBullet);
		Bullet downBullet = new Bullet(easing, ani, startX, startY, Config.DOWN);
		if (w != -1f && h != -1f) {
			downBullet.setSize(w, h);
		}
		addBullet(downBullet);
		result.add(downBullet);
		return result;
	}

	/**
	 * 同时向右方三个方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @return
	 */
	public TArray<Bullet> addThreeRightBullets(String path, float startX, float startY) {
		return addThreeRightBullets(path, startX, startY, -1f, -1f);
	}

	/**
	 * 同时向右方三个方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addThreeRightBullets(String path, float startX, float startY, float w, float h) {
		return addThreeRightBullets(easingMode, LTextures.loadTexture(path), startX, startY, w, h);
	}

	/**
	 * 同时向右方三个方向发射子弹
	 * 
	 * @param easing
	 * @param tex
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addThreeRightBullets(EasingMode easing, LTexture tex, float startX, float startY, float w,
			float h) {
		return addThreeRightBullets(easing, Animation.getDefaultAnimation(tex), startX, startY, w, h);
	}

	/**
	 * 同时向右方三个方向发射子弹
	 * 
	 * @param easing
	 * @param ani
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addThreeRightBullets(EasingMode easing, Animation ani, float startX, float startY, float w,
			float h) {
		TArray<Bullet> result = new TArray<Bullet>();
		Bullet rightBullet = new Bullet(easing, ani, startX, startY, Config.TRIGHT);
		if (w != -1f && h != -1f) {
			rightBullet.setSize(w, h);
		}
		addBullet(rightBullet);
		result.add(rightBullet);
		Bullet drightBullet = new Bullet(easing, ani, startX, startY, Config.RIGHT);
		if (w != -1f && h != -1f) {
			drightBullet.setSize(w, h);
		}
		addBullet(drightBullet);
		result.add(drightBullet);
		Bullet upBullet = new Bullet(easing, ani, startX, startY, Config.UP);
		if (w != -1f && h != -1f) {
			upBullet.setSize(w, h);
		}
		addBullet(upBullet);
		result.add(upBullet);
		return result;
	}

	/**
	 * 同时向下方两个方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @return
	 */
	public TArray<Bullet> addTwoDownBullets(String path, float startX, float startY) {
		return addTwoDownBullets(path, startX, startY, -1f, -1f);
	}

	/**
	 * 同时向下方两个方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addTwoDownBullets(String path, float startX, float startY, float w, float h) {
		return addTwoDownBullets(easingMode, LTextures.loadTexture(path), startX, startY, w, h);
	}

	/**
	 * 同时向下方两个方向发射子弹
	 * 
	 * @param easing
	 * @param tex
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addTwoDownBullets(EasingMode easing, LTexture tex, float startX, float startY, float w,
			float h) {
		return addTwoDownBullets(easing, Animation.getDefaultAnimation(tex), startX, startY, w, h);
	}

	/**
	 * 同时向下方两个方向发射子弹
	 * 
	 * @param easing
	 * @param ani
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addTwoDownBullets(EasingMode easing, Animation ani, float startX, float startY, float w,
			float h) {
		TArray<Bullet> result = new TArray<Bullet>();
		Bullet dupBullet = new Bullet(easing, ani, startX, startY, Config.DOWN);
		if (w != -1f && h != -1f) {
			dupBullet.setSize(w, h);
		}
		addBullet(dupBullet);
		result.add(dupBullet);
		Bullet drightBullet = new Bullet(easing, ani, startX, startY, Config.RIGHT);
		if (w != -1f && h != -1f) {
			drightBullet.setSize(w, h);
		}
		addBullet(drightBullet);
		result.add(drightBullet);
		return result;
	}

	/**
	 * 同时向上方两个方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @return
	 */
	public TArray<Bullet> addTwoUpBullets(String path, float startX, float startY) {
		return addTwoUpBullets(path, startX, startY, -1f, -1f);
	}

	/**
	 * 同时向上方两个方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addTwoUpBullets(String path, float startX, float startY, float w, float h) {
		return addTwoUpBullets(easingMode, LTextures.loadTexture(path), startX, startY, w, h);
	}

	/**
	 * 同时向上方两个方向发射子弹
	 * 
	 * @param easing
	 * @param tex
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addTwoUpBullets(EasingMode easing, LTexture tex, float startX, float startY, float w,
			float h) {
		return addTwoUpBullets(easing, Animation.getDefaultAnimation(tex), startX, startY, w, h);
	}

	/**
	 * 同时向上方两个方向发射子弹
	 * 
	 * @param easing
	 * @param ani
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addTwoUpBullets(EasingMode easing, Animation ani, float startX, float startY, float w,
			float h) {
		TArray<Bullet> result = new TArray<Bullet>();
		Bullet dupBullet = new Bullet(easing, ani, startX, startY, Config.UP);
		if (w != -1f && h != -1f) {
			dupBullet.setSize(w, h);
		}
		addBullet(dupBullet);
		result.add(dupBullet);
		Bullet leftBullet = new Bullet(easing, ani, startX, startY, Config.LEFT);
		if (w != -1f && h != -1f) {
			leftBullet.setSize(w, h);
		}
		addBullet(leftBullet);
		result.add(leftBullet);
		return result;
	}

	/**
	 * 同时向左方两个方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @return
	 */
	public TArray<Bullet> addTwoLeftBullets(String path, float startX, float startY) {
		return addTwoLeftBullets(path, startX, startY, -1f, -1f);
	}

	/**
	 * 同时向左方两个方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addTwoLeftBullets(String path, float startX, float startY, float w, float h) {
		return addTwoLeftBullets(easingMode, LTextures.loadTexture(path), startX, startY, w, h);
	}

	/**
	 * 同时向左方两个方向发射子弹
	 * 
	 * @param easing
	 * @param tex
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addTwoLeftBullets(EasingMode easing, LTexture tex, float startX, float startY, float w,
			float h) {
		return addTwoLeftBullets(easing, Animation.getDefaultAnimation(tex), startX, startY, w, h);
	}

	/**
	 * 同时向左方两个方向发射子弹
	 * 
	 * @param easing
	 * @param ani
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addTwoLeftBullets(EasingMode easing, Animation ani, float startX, float startY, float w,
			float h) {
		TArray<Bullet> result = new TArray<Bullet>();
		Bullet dleftBullet = new Bullet(easing, ani, startX, startY, Config.LEFT);
		if (w != -1f && h != -1f) {
			dleftBullet.setSize(w, h);
		}
		addBullet(dleftBullet);
		result.add(dleftBullet);
		Bullet downBullet = new Bullet(easing, ani, startX, startY, Config.DOWN);
		if (w != -1f && h != -1f) {
			downBullet.setSize(w, h);
		}
		addBullet(downBullet);
		result.add(downBullet);
		return result;
	}

	/**
	 * 同时向右方两个方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @return
	 */
	public TArray<Bullet> addTwoRightBullets(String path, float startX, float startY) {
		return addTwoRightBullets(path, startX, startY, -1f, -1f);
	}

	/**
	 * 同时向右方两个方向发射子弹
	 * 
	 * @param path
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addTwoRightBullets(String path, float startX, float startY, float w, float h) {
		return addTwoRightBullets(easingMode, LTextures.loadTexture(path), startX, startY, w, h);
	}

	/**
	 * 同时向右方两个方向发射子弹
	 * 
	 * @param easing
	 * @param tex
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addTwoRightBullets(EasingMode easing, LTexture tex, float startX, float startY, float w,
			float h) {
		return addTwoRightBullets(easing, Animation.getDefaultAnimation(tex), startX, startY, w, h);
	}

	/**
	 * 同时向右方两个方向发射子弹
	 * 
	 * @param easing
	 * @param ani
	 * @param startX
	 * @param startY
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<Bullet> addTwoRightBullets(EasingMode easing, Animation ani, float startX, float startY, float w,
			float h) {
		TArray<Bullet> result = new TArray<Bullet>();
		Bullet drightBullet = new Bullet(easing, ani, startX, startY, Config.RIGHT);
		if (w != -1f && h != -1f) {
			drightBullet.setSize(w, h);
		}
		addBullet(drightBullet);
		result.add(drightBullet);
		Bullet upBullet = new Bullet(easing, ani, startX, startY, Config.UP);
		if (w != -1f && h != -1f) {
			upBullet.setSize(w, h);
		}
		addBullet(upBullet);
		result.add(upBullet);
		return result;
	}

	public Bullet addBullet(LTexture texture, float x, float y, int dir) {
		return addBullet(easingMode, texture, x, y, dir);
	}

	public Bullet addBullet(Animation ani, float x, float y, int dir) {
		return addBullet(easingMode, ani, x, y, dir);
	}

	public Bullet addBullet(String path, float x, float y, int dir) {
		return addBullet(easingMode, LSystem.loadTexture(path), x, y, dir);
	}

	public Bullet addBullet(Animation ani, float x, float y, int dir, int initSpeed, float duration) {
		return addBullet(easingMode, ani, x, y, dir, initSpeed, duration);
	}

	public Bullet addBullet(String path, float x, float y, int dir, int initSpeed, float duration) {
		return addBullet(easingMode, path, x, y, dir, initSpeed, duration);
	}

	public Bullet addBullet(String path, float x, float y, int dir, int initSpeed) {
		return addBullet(easingMode, path, x, y, dir, initSpeed);
	}

	public Bullet addBullet(EasingMode easing, LTexture texture, float x, float y, int dir) {
		Bullet bullet = new Bullet(easing, texture, x, y, dir);
		addBullet(bullet);
		return bullet;
	}

	public Bullet addBullet(EasingMode easing, Animation ani, float x, float y, int dir) {
		Bullet bullet = new Bullet(easing, ani, x, y, dir);
		addBullet(bullet);
		return bullet;
	}

	public Bullet addBullet(EasingMode easing, String path, float x, float y, int dir) {
		Bullet bullet = new Bullet(easing, LSystem.loadTexture(path), x, y, dir);
		addBullet(bullet);
		return bullet;
	}

	public Bullet addBullet(EasingMode easing, Animation ani, float x, float y, int dir, int initSpeed,
			float duration) {
		Bullet bullet = new Bullet(easing, ani, x, y, dir, initSpeed, duration);
		addBullet(bullet);
		return bullet;
	}

	public Bullet addBullet(EasingMode easing, Animation ani, float x, float y, int dir, int initSpeed) {
		Bullet bullet = new Bullet(easing, ani, x, y, dir, initSpeed);
		addBullet(bullet);
		return bullet;
	}

	public Bullet addBullet(EasingMode easing, String path, float x, float y, int dir, int initSpeed) {
		Bullet bullet = new Bullet(easing, LSystem.loadTexture(path), x, y, dir, initSpeed);
		addBullet(bullet);
		return bullet;
	}

	public Bullet addBullet(EasingMode easing, String path, float x, float y, int dir, int initSpeed, float duration) {
		Bullet bullet = new Bullet(easing, LSystem.loadTexture(path), x, y, dir, initSpeed, duration);
		addBullet(bullet);
		return bullet;
	}

	public BulletEntity addBullet(Bullet bullet) {
		if (_destroyed) {
			return this;
		}
		if (bullet == null) {
			return this;
		}
		if (bullet.getTexture() != null) {
			textureFree.add(bullet.getTexture());
		}
		addWorld(bullet);
		return this;
	}

	protected void addWorld(Bullet bullet) {
		if (_destroyed) {
			return;
		}
		if (bullet == null) {
			return;
		}
		if (!bullets.contains(bullet)) {
			bullets.add(bullet);
			_collisionWorld.add(bullet);
			_collisionWorld.getCollisionManager().addObject(bullet);
			bullet.setSuper(this);
			bullet.onAttached();
			if (_bulletListener != null) {
				_bulletListener.attached(bullet);
			}
		}
	}

	protected void removeWorld(Bullet bullet) {
		if (_destroyed) {
			return;
		}
		if (bullet == null) {
			return;
		}
		bullets.remove(bullet);
		_collisionWorld.remove(bullet);
		_collisionWorld.getCollisionManager().removeObject(bullet);
		bullet.setSuper(null);
		bullet.onDetached();
		if (_bulletListener != null) {
			_bulletListener.detached(bullet);
		}
	}

	public BulletEntity setSpeedScale(float s) {
		if (_destroyed) {
			return this;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				bullet.setScaleSpeed(s);
			}
		}
		return this;
	}

	public BulletEntity setSpeedX(int x) {
		if (_destroyed) {
			return this;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				bullet.setSpeedX(x);
			}
		}
		return this;
	}

	public BulletEntity setSpeedY(int y) {
		if (_destroyed) {
			return this;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				bullet.setSpeedY(y);
			}
		}
		return this;
	}

	public BulletEntity setInitSpeed(int s) {
		if (_destroyed) {
			return this;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				bullet.setInitSpeed(s);
			}
		}
		return this;
	}

	public BulletEntity setBulletColor(LColor c) {
		if (_destroyed) {
			return this;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				bullet.setColor(c);
			}
		}
		return this;
	}

	public BulletEntity clearSpeed() {
		if (_destroyed) {
			return this;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				bullet.clearSpeed();
			}
		}
		return this;
	}

	public BulletEntity setWaveType(WaveType w) {
		if (_destroyed) {
			return this;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				bullet.setWaveType(w);
			}
		}
		return this;
	}

	public BulletEntity setWaveAmplitude(float a) {
		if (_destroyed) {
			return this;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				bullet.setWaveAmplitude(a);
			}
		}
		return this;
	}

	public BulletEntity setWaveFrequency(float f) {
		if (_destroyed) {
			return this;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				bullet.setWaveFrequency(f);
			}
		}
		return this;
	}

	public BulletEntity setEaseTimerLoop(boolean l) {
		if (_destroyed) {
			return this;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				bullet.setEaseTimerLoop(l);
			}
		}
		return this;
	}

	public BulletEntity setDuration(float d) {
		if (_destroyed) {
			return this;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				bullet.setDuration(d);
			}
		}
		return this;
	}

	public BulletEntity setLifeTimer(float l) {
		if (_destroyed) {
			return this;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				bullet.setLifeTimer(l);
			}
		}
		return this;
	}

	public BulletEntity fireTo(ISprite spr) {
		if (_destroyed) {
			return this;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				bullet.fireTo(spr);
			}
		}
		return this;
	}

	@Override
	protected void onUpdate(final long elapsedTime) {
		if (_destroyed) {
			return;
		}
		if (_running) {
			for (int i = this.bullets.size - 1; i >= 0; i--) {
				Bullet bullet = bullets.get(i);
				if (bullet != null) {
					bullet.update(elapsedTime);
					if (_bulletListener != null) {
						_bulletListener.updateable(elapsedTime, bullet);
						bullet.checkLifeOver(_bulletListener);
						if (bullet.isEaseCompleted()) {
							_bulletListener.easeover(bullet);
						}
					}
					fixMovePosition(bullet);
					if (_autoRemoveOfBounds) {
						RectBox worldRect = getCollisionBox();
						RectBox bulletRect = bullet.getRectBox();
						if (!(worldRect.contains(bulletRect) || worldRect.intersects(bulletRect))) {
							removeWorld(bullet);
						}
					}
					if (_checkCollision) {
						final Sprites sprs = _sprites;
						if (sprs != null) {
							final ISprite[] list = sprs._sprites;
							if (list != null) {
								for (int j = list.length - 1; j >= 0; j--) {
									final ISprite spr = list[j];
									if (spr != null && spr != this && spr instanceof CollisionObject) {
										CollisionObject dstObject = (CollisionObject) spr;
										if (bullet.intersects(dstObject) || bullet.contains(dstObject)) {
											onTriggerCollision(bullet, dstObject, _collisionActionListener);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected void repaint(GLEx g, float offsetX, float offsetY) {
		if (_destroyed) {
			return;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				bullet.draw(g, drawX(offsetX), drawY(offsetX));
				if (_bulletListener != null) {
					_bulletListener.drawable(g, bullet);
				}
				fixMovePosition(bullet);
			}
		}
	}

	public BulletEntity collidable() {
		return setCollision(true);
	}

	public BulletEntity disableCollidable() {
		return setCollision(false);
	}

	public boolean isCollision() {
		return this._checkCollision;
	}

	public BulletEntity setCollision(boolean c) {
		this._checkCollision = c;
		return this;
	}

	public BulletEntity setCollisionAction(CollisionAction<CollisionObject> c) {
		this._collisionActionListener = c;
		return this;
	}

	public BulletEntity triggerCollision(CollisionAction<CollisionObject> c) {
		setCollision(c != null);
		setCollisionAction(c);
		return this;
	}

	public CollisionAction<CollisionObject> getCollisionAction() {
		return _collisionActionListener;
	}

	private void onTriggerCollision(final CollisionObject src, final CollisionObject dst,
			final CollisionAction<CollisionObject> collisionAction) {
		if (src == null || dst == null) {
			return;
		}
		if (src == this || dst == this) {
			return;
		}
		if (checkCollisionSkip(src, dst)) {
			return;
		}
		if (collisionAction != null) {
			collisionAction.onCollision(src, dst, Side.getCollisionSide(src.getRectBox(), dst.getRectBox()));
		}
	}

	private boolean checkCollisionSkip(final CollisionObject spr, final CollisionObject dst) {
		if (_collisionIgnoreTypes != null) {
			if (_collisionIgnoreTypes.contains(spr.getFlagType())
					|| _collisionIgnoreTypes.contains(dst.getFlagType())) {
				return true;
			}
		}
		if (_collisionIgnoreStrings != null) {
			if (_collisionIgnoreStrings.contains(spr.getObjectFlag())
					|| _collisionIgnoreStrings.contains(dst.getObjectFlag())) {
				return true;
			}
		}
		return false;
	}

	public IntArray getCollisionIgnoreTypes() {
		return _collisionIgnoreTypes.cpy();
	}

	public BulletEntity addCollisionIgnoreType(int t) {
		if (_collisionIgnoreTypes == null) {
			_collisionIgnoreTypes = new IntArray();
		}
		if (!_collisionIgnoreTypes.contains(t)) {
			_collisionIgnoreTypes.add(t);
		}
		return this;
	}

	public boolean removeCollisionIgnoreType(int t) {
		if (_collisionIgnoreTypes == null) {
			_collisionIgnoreTypes = new IntArray();
		}
		return _collisionIgnoreTypes.removeValue(t);
	}

	public TArray<String> getCollisionIgnoreStrings() {
		final TArray<String> result = new TArray<String>(_collisionIgnoreStrings.size());
		for (Iterator<String> it = _collisionIgnoreStrings.keys(); it.hasNext();) {
			final String key = it.next();
			if (key != null) {
				result.add(key);
			}
		}
		return result;
	}

	public BulletEntity addCollisionIgnoreString(String t) {
		if (_collisionIgnoreStrings == null) {
			_collisionIgnoreStrings = new ObjectSet<String>();
		}
		_collisionIgnoreStrings.add(t);
		return this;
	}

	public boolean removeCollisionIgnoreString(String t) {
		if (_collisionIgnoreStrings == null) {
			_collisionIgnoreStrings = new ObjectSet<String>();
		}
		return _collisionIgnoreStrings.remove(t);
	}

	public BulletEntity checkBulletCollision() {
		return checkBulletCollision(_collisionActionListener);
	}

	public BulletEntity checkBulletCollision(final CollisionAction<CollisionObject> collisionAction) {
		return checkBulletCollision(getSprites(), collisionAction);
	}

	public BulletEntity checkBulletCollision(final Sprites sprs,
			final CollisionAction<CollisionObject> collisionAction) {
		if (sprs == null) {
			return null;
		}
		return checkBulletCollision(sprs._sprites, collisionAction);
	}

	public BulletEntity checkBulletCollision(final TArray<ISprite> sprs,
			final CollisionAction<CollisionObject> collisionAction) {
		if (_destroyed) {
			return null;
		}
		if (collisionAction == null) {
			return null;
		}
		if (sprs == null) {
			return null;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			final Bullet bullet = bullets.get(i);
			if (bullet != null) {
				for (int j = sprs.size - 1; j >= 0; j--) {
					final ISprite spr = sprs.get(j);
					if (spr != null && spr != this && spr instanceof CollisionObject) {
						final CollisionObject dstObject = (CollisionObject) spr;
						if (bullet.intersects(dstObject) || bullet.contains(dstObject)) {
							onTriggerCollision(bullet, dstObject, collisionAction);
						}
					}
				}
			}
		}
		return this;
	}

	public BulletEntity checkBulletCollision(final ISprite[] sprs,
			final CollisionAction<CollisionObject> collisionAction) {
		if (_destroyed) {
			return null;
		}
		if (collisionAction == null) {
			return null;
		}
		if (sprs == null) {
			return null;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			final Bullet bullet = bullets.get(i);
			if (bullet != null) {
				for (int j = sprs.length - 1; j >= 0; j--) {
					final ISprite spr = sprs[j];
					if (spr != null && spr != this && spr instanceof CollisionObject) {
						final CollisionObject dstObject = (CollisionObject) spr;
						if (bullet.intersects(dstObject) || bullet.contains(dstObject)) {
							onTriggerCollision(bullet, dstObject, collisionAction);
						}
					}
				}
			}
		}
		return this;
	}

	public TArray<ISprite> findBulletCollision() {
		return findBulletCollision(getSprites());
	}

	public TArray<ISprite> findBulletCollision(final Sprites sprs) {
		if (sprs == null) {
			return null;
		}
		return findBulletCollision(sprs._sprites);
	}

	public TArray<ISprite> findBulletCollision(final TArray<ISprite> sprs) {
		if (_destroyed) {
			return null;
		}
		if (sprs == null) {
			return null;
		}
		final TArray<ISprite> result = new TArray<ISprite>();
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			final Bullet bullet = bullets.get(i);
			if (bullet != null) {
				for (int j = sprs.size - 1; j >= 0; j--) {
					final ISprite spr = sprs.get(j);
					if (spr != null && spr != this && spr instanceof CollisionObject) {
						final CollisionObject dstObject = (CollisionObject) spr;
						if (bullet.intersects(dstObject) || bullet.contains(dstObject)) {
							result.add(spr);
						}
					}
				}
			}
		}
		return result;
	}

	public TArray<ISprite> findBulletCollision(final ISprite... sprs) {
		if (_destroyed) {
			return null;
		}
		if (sprs == null) {
			return null;
		}
		final TArray<ISprite> result = new TArray<ISprite>();
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			final Bullet bullet = bullets.get(i);
			if (bullet != null) {
				for (int j = sprs.length - 1; j >= 0; j--) {
					final ISprite spr = sprs[j];
					if (spr != null && spr != this && spr instanceof CollisionObject) {
						final CollisionObject dstObject = (CollisionObject) spr;
						if (bullet.intersects(dstObject) || bullet.contains(dstObject)) {
							result.add(spr);
						}
					}
				}
			}
		}
		return result;
	}

	public TArray<Bullet> getBullets() {
		if (_destroyed) {
			return null;
		}
		return bullets;
	}

	public Bullet getBullet(int idx) {
		if (_destroyed) {
			return null;
		}
		return bullets.get(idx);
	}

	public BulletEntity removeBullet(Bullet bullet) {
		if (_destroyed) {
			return null;
		}
		bullets.remove(bullet);
		if (bullet != null) {
			bullet.onDetached();
			if (_bulletListener != null) {
				_bulletListener.detached(bullet);
			}
		}
		return this;
	}

	public BulletEntity removeBulletIndex(int bulletIdx) {
		if (_destroyed) {
			return null;
		}
		Bullet bullet = bullets.removeIndex(bulletIdx);
		if (bullet != null) {
			bullet.onDetached();
			if (_bulletListener != null) {
				_bulletListener.detached(bullet);
			}
		}
		return this;
	}

	public BulletEntity clearBullets() {
		if (_destroyed) {
			return null;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				bullet.onDetached();
				if (_bulletListener != null) {
					_bulletListener.detached(bullet);
				}
			}
		}
		bullets.clear();
		return this;
	}

	public boolean isBulletCollision(CollisionObject o) {
		if (_destroyed) {
			return false;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				return bullet.isCollision(o);
			}
		}
		return false;
	}

	public boolean isBulletIntersects(CollisionObject o) {
		if (_destroyed) {
			return false;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				return bullet.intersects(o);
			}
		}
		return false;
	}

	public boolean isBulletContains(CollisionObject o) {
		if (_destroyed) {
			return false;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				return bullet.contains(o);
			}
		}
		return false;
	}

	public boolean isBulletCollision(Shape o) {
		if (_destroyed) {
			return false;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				return bullet.isCollision(o);
			}
		}
		return false;
	}

	public boolean isBulletIntersects(Shape o) {
		if (_destroyed) {
			return false;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				return bullet.intersects(o);
			}
		}
		return false;
	}

	public boolean isBulletContains(Shape o) {
		if (_destroyed) {
			return false;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			if (bullet != null) {
				return bullet.contains(o);
			}
		}
		return false;
	}

	public boolean isRunning() {
		return _running;
	}

	public BulletEntity setRunning(boolean r) {
		this._running = r;
		return this;
	}

	public BulletEntity start() {
		return setRunning(true);
	}

	public BulletEntity stop() {
		return setRunning(false);
	}

	public boolean isAutoRemoveOfBounds() {
		return _autoRemoveOfBounds;
	}

	public BulletEntity setAutoRemoveOfBounds(boolean a) {
		this._autoRemoveOfBounds = a;
		return this;
	}

	public BulletEntity initializeCollision(int tileSize) {
		getCollisionManager().initialize(tileSize);
		return this;
	}

	public BulletEntity initializeCollision(int tileSizeX, int tileSizeY) {
		getCollisionManager().initialize(tileSizeX, tileSizeY);
		return this;
	}

	public BulletEntity setCollisionInTheLayer(boolean itlayer) {
		if (_destroyed) {
			return this;
		}
		getCollisionManager().setInTheLayer(itlayer);
		return this;
	}

	public boolean getCollisionInTheLayer() {
		if (_destroyed) {
			return false;
		}
		return getCollisionManager().getInTheLayer();
	}

	public BulletEntity setCollisionOffsetPos(Vector2f offset) {
		if (_destroyed) {
			return this;
		}
		getCollisionManager().setOffsetPos(offset);
		return this;
	}

	public BulletEntity setCollisionOffsetPos(float x, float y) {
		if (_destroyed) {
			return this;
		}
		getCollisionManager().setOffsetPos(x, y);
		return this;
	}

	public BulletEntity setCollisionOffsetX(float x) {
		if (_destroyed) {
			return this;
		}
		getCollisionManager().setOffsetX(x);
		return this;
	}

	public BulletEntity setCollisionOffsetY(float y) {
		if (_destroyed) {
			return this;
		}
		getCollisionManager().setOffsetY(y);
		return this;
	}

	public Vector2f getCollisionOffsetPos() {
		if (_destroyed) {
			return Vector2f.ZERO();
		}
		return getCollisionManager().getOffsetPos();
	}

	public BulletEntity putCollision(CollisionObject obj) {
		if (_destroyed) {
			return this;
		}
		getCollisionManager().addObject(obj);
		return this;
	}

	public BulletEntity removeCollision(CollisionObject obj) {
		if (_destroyed) {
			return this;
		}
		getCollisionManager().removeObject(obj);
		return this;
	}

	public BulletEntity removeCollision(String objFlag) {
		if (_destroyed) {
			return this;
		}
		getCollisionManager().removeObject(objFlag);
		return this;
	}

	public int getCollisionSize() {
		if (_destroyed) {
			return 0;
		}
		return getCollisionManager().numberActors();
	}

	public TArray<CollisionObject> getCollisionObjects() {
		if (_destroyed) {
			return null;
		}
		return getCollisionManager().getActorsList();
	}

	public TArray<CollisionObject> getCollisionObjects(String objFlag) {
		if (_destroyed) {
			return null;
		}
		return getCollisionManager().getObjects(objFlag);
	}

	public TArray<CollisionObject> getCollisionButtles() {
		return getCollisionObjects(Bullet.BUTTLE_DEFAULT_NAME);
	}

	public TArray<CollisionObject> getCollisionObjectsAt(float x, float y, String objFlag) {
		if (_destroyed) {
			return null;
		}
		return getCollisionManager().getObjectsAt(x, y, objFlag);
	}

	public TArray<CollisionObject> getCollisionButtlesAt(float x, float y) {
		return getCollisionObjectsAt(x, y, Bullet.BUTTLE_DEFAULT_NAME);
	}

	public TArray<CollisionObject> getIntersectingObjects(CollisionObject obj, String objFlag) {
		if (_destroyed) {
			return null;
		}
		return getCollisionManager().getIntersectingObjects(obj, objFlag);
	}

	public TArray<CollisionObject> getIntersectingButtles(CollisionObject obj) {
		return getIntersectingObjects(obj, Bullet.BUTTLE_DEFAULT_NAME);
	}

	public CollisionObject getOnlyIntersectingObject(CollisionObject obj, String objFlag) {
		if (_destroyed) {
			return null;
		}
		return getCollisionManager().getOnlyIntersectingObject(obj, objFlag);
	}

	public CollisionObject getOnlyIntersectingButtle(CollisionObject obj) {
		return getOnlyIntersectingObject(obj, Bullet.BUTTLE_DEFAULT_NAME);
	}

	public TArray<CollisionObject> getObjectsInRange(float x, float y, float r, String objFlag) {
		if (_destroyed) {
			return null;
		}
		return getCollisionManager().getObjectsInRange(x, y, r, objFlag);
	}

	public TArray<CollisionObject> getButtlesInRange(float x, float y, float r) {
		return getObjectsInRange(x, y, r, Bullet.BUTTLE_DEFAULT_NAME);
	}

	public TArray<CollisionObject> getNeighbours(CollisionObject obj, float distance, boolean d, String objFlag) {
		if (_destroyed) {
			return null;
		}
		if (distance < 0) {
			throw new LSysException("distance < 0");
		} else {
			return getCollisionManager().getNeighbours(obj, distance, d, objFlag);
		}
	}

	public TArray<CollisionObject> getNeighboursButtles(CollisionObject obj, float distance, boolean d) {
		return getNeighbours(obj, distance, d, Bullet.BUTTLE_DEFAULT_NAME);
	}

	public EasingMode getEasingMode() {
		return easingMode;
	}

	public void setEasingMode(EasingMode easingMode) {
		this.easingMode = easingMode;
	}

	protected void fixMovePosition(Bullet bind) {
		if (_destroyed) {
			return;
		}
		if (bind == null) {
			return;
		}
		if (_allowAutoFixMoved && _collisionWorld != null) {
			if (_worldCollisionFilter == null) {
				_worldCollisionFilter = CollisionFilter.getDefault();
			}
			CollisionResult.Result result = _collisionWorld.move(bind, bind.getX(), bind.getY(), _worldCollisionFilter);
			if (result != null) {
				bind.setLocation(result.goalX, result.goalY);
			}
		}
		if (_limitMovedOfBounds) {
			if (_limitRangeX != null) {
				bind.setX(MathUtils.clamp(bind.getX(), _limitRangeX.getMin() - 1f,
						_limitRangeX.getMax() - bind.getWidth() - 1f));
			}
			if (_limitRangeY != null) {
				bind.setY(MathUtils.clamp(bind.getY(), _limitRangeY.getMin() - 1f,
						_limitRangeY.getMax() - bind.getHeight() - 1f));
			}
		}
	}

	public boolean isAllowAutoFixMoved() {
		return this._allowAutoFixMoved;
	}

	public BulletEntity setAllowAutoFixMove(boolean a) {
		this._allowAutoFixMoved = a;
		return this;
	}

	public CollisionFilter getCollisionFilter() {
		return _worldCollisionFilter;
	}

	public void setCollisionFilter(CollisionFilter filter) {
		this._worldCollisionFilter = filter;
	}

	public CollisionWorld getCollisionWorld() {
		return _collisionWorld;
	}

	public BulletEntity clearListerner() {
		this._bulletListener = null;
		this._resizeListener = null;
		this._collisionActionListener = null;
		if (this._collisionIgnoreTypes != null) {
			this._collisionIgnoreTypes.clear();
			this._collisionIgnoreTypes = null;
		}
		if (this._collisionIgnoreStrings != null) {
			this._collisionIgnoreStrings.clear();
			this._collisionIgnoreStrings = null;
		}
		return this;
	}

	@Override
	protected void _onDestroy() {
		super._onDestroy();
		clearBullets();
		clearListerner();
		if (textureFree != null) {
			textureFree.close();
			textureFree = null;
		}
		if (_selfWorld) {
			if (_collisionWorld != null) {
				_collisionWorld.close();
				_collisionWorld = null;
			}
		}
		_running = false;
		_allowAutoFixMoved = false;
		_autoRemoveOfBounds = false;
		_limitMovedOfBounds = false;
		_checkCollision = false;
	}

}
