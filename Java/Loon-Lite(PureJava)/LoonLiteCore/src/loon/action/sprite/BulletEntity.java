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

import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.action.ActionBind;
import loon.action.collision.CollisionFilter;
import loon.action.collision.CollisionManager;
import loon.action.collision.CollisionObject;
import loon.action.collision.CollisionResult;
import loon.action.collision.CollisionWorld;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.LTextureFree;
import loon.utils.Easing.EasingMode;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 子弹用Entity,用来显示以及管理子弹(复数)用类
 */
public class BulletEntity extends Entity {

	public static interface BulletListener {

		public void attached(Bullet bullet);

		public void detached(Bullet bullet);

		public void drawable(GLEx g, Bullet bullet);

		public void updateable(long elapsedTime, Bullet bullet);
	}

	public static float getBallisticRange(float speed, float gravity, float iheight) {
		float angle = 45f * MathUtils.DEG_TO_RAD;
		float cos = MathUtils.cos(angle);
		float sin = MathUtils.sin(angle);
		float range = (speed * cos / gravity)
				* (speed * sin + MathUtils.sqrt(speed * speed * sin * sin + 2f * gravity * iheight));
		return range;
	}

	private CollisionWorld collisionWorld;

	protected CollisionFilter worldCollisionFilter;

	private BulletListener listener;

	private boolean _allowAutoFixMoved;

	private boolean _running;

	private boolean _limitMoved;

	private boolean selfWorld;

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
			this.collisionWorld = world;
			this.selfWorld = false;
		} else {
			this.collisionWorld = new CollisionWorld();
			this.selfWorld = true;
		}
		this._running = true;
		this._limitMoved = true;
		this.setRepaint(true);
		this.setLocation(x, y);
		this.setSize(w, h);
	}

	public BulletEntity setListener(BulletListener l) {
		this.listener = l;
		return this;
	}

	public BulletListener getListener() {
		return this.listener;
	}

	public BulletEntity setCollisionWorld(CollisionWorld world) {
		if (world == null) {
			return this;
		}
		if (selfWorld && collisionWorld != null) {
			collisionWorld.close();
		}
		this.selfWorld = false;
		this.collisionWorld = world;
		return this;
	}

	public CollisionManager getCollisionManager() {
		if (collisionWorld == null) {
			collisionWorld = new CollisionWorld();
			selfWorld = true;
		}
		return collisionWorld.getCollisionManager();
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
			addWorld(bullet);
		}
		return new TArray<Bullet>(bullets);
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
		bullets.add(bullet);
		collisionWorld.add(bullet);
		collisionWorld.getCollisionManager().addObject(bullet);
		if (listener != null) {
			listener.attached(bullet);
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
		collisionWorld.remove(bullet);
		collisionWorld.getCollisionManager().removeObject(bullet);
		bullet.onDetached();
		if (listener != null) {
			listener.detached(bullet);
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
				movePos(bullet);
				bullet.draw(g, drawX(offsetX), drawY(offsetX));
				if (listener != null) {
					listener.drawable(g, bullet);
				}
			}
		}
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
					movePos(bullet);
					bullet.update(elapsedTime);
					if (listener != null) {
						listener.updateable(elapsedTime, bullet);
					}
					if (_limitMoved && !getCollisionBox().contains(bullet.getRectBox())) {
						removeWorld(bullet);
					}
				}
			}
		}
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
			if (listener != null) {
				listener.detached(bullet);
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
			if (listener != null) {
				listener.detached(bullet);
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
				if (listener != null) {
					listener.detached(bullet);
				}
			}
		}
		bullets.clear();
		return this;
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

	public boolean isLimitMoved() {
		return _limitMoved;
	}

	public BulletEntity setLimitMoved(boolean limitMoved) {
		this._limitMoved = limitMoved;
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

	private void movePos(ActionBind bind) {
		if (_destroyed) {
			return;
		}
		if (bind == null) {
			return;
		}
		if (collisionWorld != null && _allowAutoFixMoved) {
			if (worldCollisionFilter == null) {
				worldCollisionFilter = CollisionFilter.getDefault();
			}
			CollisionResult.Result result = collisionWorld.move(bind, bind.getX(), bind.getY(), worldCollisionFilter);
			bind.setLocation(result.goalX, result.goalY);
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
		return worldCollisionFilter;
	}

	public void setCollisionFilter(CollisionFilter filter) {
		this.worldCollisionFilter = filter;
	}

	public CollisionWorld getCollisionWorld() {
		return collisionWorld;
	}

	@Override
	protected void _onDestroy() {
		super._onDestroy();
		clearBullets();
		if (textureFree != null) {
			textureFree.close();
			textureFree = null;
		}
		if (selfWorld) {
			if (collisionWorld != null) {
				collisionWorld.close();
				collisionWorld = null;
			}
		}
		listener = null;
		_running = false;
		_allowAutoFixMoved = false;
	}

}
