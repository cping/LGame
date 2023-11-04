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
import loon.utils.TArray;

/**
 * 子弹用Entity,用来显示以及管理子弹(复数)用类
 */
public class BulletEntity extends Entity {

	private CollisionWorld collisionWorld;

	protected CollisionFilter worldCollisionFilter;

	private boolean closed;

	private boolean running;

	private boolean limitMoved;

	private boolean selfWorld;

	private TArray<Bullet> bullets;

	private LTextureFree textureFree;

	private EasingMode easingMode;

	public BulletEntity() {
		this(null);
	}

	public BulletEntity(int x, int y, int w, int h) {
		this(null, x, y, w, h);
	}

	public BulletEntity(CollisionWorld world) {
		this(world, 0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public BulletEntity(CollisionWorld world, int x, int y, int w, int h) {
		this.easingMode = EasingMode.Linear;
		this.bullets = new TArray<Bullet>(32);
		this.textureFree = new LTextureFree();
		if (world != null) {
			this.collisionWorld = world;
			this.selfWorld = false;
		} else {
			this.collisionWorld = new CollisionWorld();
			this.selfWorld = true;
		}
		this.closed = false;
		this.running = true;
		this.limitMoved = true;
		this.setRepaint(true);
		this.setLocation(x, y);
		this.setSize(w, h);
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

	public BulletEntity addBullet(LTexture texture, float x, float y, int dir) {
		return addBullet(easingMode, texture, x, y, dir);
	}

	public BulletEntity addBullet(Animation ani, float x, float y, int dir) {
		return addBullet(easingMode, ani, x, y, dir);
	}

	public BulletEntity addBullet(String path, float x, float y, int dir) {
		return addBullet(easingMode, LSystem.loadTexture(path), x, y, dir);
	}

	public BulletEntity addBullet(Animation ani, float x, float y, int dir, int initSpeed, float duration) {
		return addBullet(easingMode, ani, x, y, dir, initSpeed, duration);
	}

	public BulletEntity addBullet(String path, float x, float y, int dir, int initSpeed, float duration) {
		return addBullet(easingMode, path, x, y, dir, initSpeed, duration);
	}

	public BulletEntity addBullet(String path, float x, float y, int dir, int initSpeed) {
		return addBullet(easingMode, path, x, y, dir, initSpeed);
	}

	public BulletEntity addBullet(EasingMode easing, LTexture texture, float x, float y, int dir) {
		return addBullet(new Bullet(easing, texture, x, y, dir));
	}

	public BulletEntity addBullet(EasingMode easing, Animation ani, float x, float y, int dir) {
		return addBullet(new Bullet(easing, ani, x, y, dir));
	}

	public BulletEntity addBullet(EasingMode easing, String path, float x, float y, int dir) {
		return addBullet(new Bullet(easing, LSystem.loadTexture(path), x, y, dir));
	}

	public BulletEntity addBullet(EasingMode easing, Animation ani, float x, float y, int dir, int initSpeed,
			float duration) {
		return addBullet(new Bullet(easing, ani, x, y, dir, initSpeed, duration));
	}

	public BulletEntity addBullet(EasingMode easing, Animation ani, float x, float y, int dir, int initSpeed) {
		return addBullet(new Bullet(easing, ani, x, y, dir, initSpeed));
	}

	public BulletEntity addBullet(EasingMode easing, String path, float x, float y, int dir, int initSpeed) {
		return addBullet(new Bullet(easing, LSystem.loadTexture(path), x, y, dir, initSpeed));
	}

	public BulletEntity addBullet(EasingMode easing, String path, float x, float y, int dir, int initSpeed,
			float duration) {
		return addBullet(new Bullet(easing, LSystem.loadTexture(path), x, y, dir, initSpeed, duration));
	}

	public BulletEntity addBullet(Bullet bullet) {
		if (closed) {
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

	private void addWorld(Bullet bullet) {
		bullets.add(bullet);
		collisionWorld.add(bullet);
		collisionWorld.getCollisionManager().addObject(bullet);
	}

	private void removeWorld(Bullet bullet) {
		bullets.remove(bullet);
		collisionWorld.remove(bullet);
		collisionWorld.getCollisionManager().removeObject(bullet);
	}

	@Override
	protected void repaint(GLEx g, float offsetX, float offsetY) {
		if (closed) {
			return;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bu = bullets.get(i);
			if (bu != null) {
				movePos(bu);
				bu.draw(g, drawX(offsetX), drawY(offsetX));
			}
		}
	}

	@Override
	protected void onUpdate(final long elapsedTime) {
		if (closed) {
			return;
		}
		if (running) {
			for (int i = this.bullets.size - 1; i >= 0; i--) {
				Bullet bu = bullets.get(i);
				if (bu != null) {
					movePos(bu);
					bu.update(elapsedTime);
					if (limitMoved && !getCollisionBox().contains(bu.getRectBox())) {
						removeWorld(bu);
					}
				}
			}
		}
	}

	public TArray<Bullet> getBullets() {
		return bullets;
	}

	public Bullet getBullet(int idx) {
		return bullets.get(idx);
	}

	public BulletEntity removeBullet(Bullet bullet) {
		bullets.remove(bullet);
		return this;
	}

	public BulletEntity removeBulletIndex(int bulletIdx) {
		bullets.removeIndex(bulletIdx);
		return this;
	}

	public BulletEntity clearBullets() {
		bullets.clear();
		return this;
	}

	public boolean isRunning() {
		return running;
	}

	public BulletEntity setRunning(boolean r) {
		this.running = r;
		return this;
	}

	public BulletEntity start() {
		return setRunning(true);
	}

	public BulletEntity stop() {
		return setRunning(false);
	}

	public boolean isLimitMoved() {
		return limitMoved;
	}

	public BulletEntity setLimitMoved(boolean limitMoved) {
		this.limitMoved = limitMoved;
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
		if (closed) {
			return this;
		}
		getCollisionManager().setInTheLayer(itlayer);
		return this;
	}

	public boolean getCollisionInTheLayer() {
		if (closed) {
			return false;
		}
		return getCollisionManager().getInTheLayer();
	}

	public BulletEntity setCollisionOffsetPos(Vector2f offset) {
		if (closed) {
			return this;
		}
		getCollisionManager().setOffsetPos(offset);
		return this;
	}

	public BulletEntity setCollisionOffsetPos(float x, float y) {
		if (closed) {
			return this;
		}
		getCollisionManager().setOffsetPos(x, y);
		return this;
	}

	public BulletEntity setCollisionOffsetX(float x) {
		if (closed) {
			return this;
		}
		getCollisionManager().setOffsetX(x);
		return this;
	}

	public BulletEntity setCollisionOffsetY(float y) {
		if (closed) {
			return this;
		}
		getCollisionManager().setOffsetY(y);
		return this;
	}

	public Vector2f getCollisionOffsetPos() {
		if (closed) {
			return Vector2f.ZERO();
		}
		return getCollisionManager().getOffsetPos();
	}

	public BulletEntity putCollision(CollisionObject obj) {
		if (closed) {
			return this;
		}
		getCollisionManager().addObject(obj);
		return this;
	}

	public BulletEntity removeCollision(CollisionObject obj) {
		if (closed) {
			return this;
		}
		getCollisionManager().removeObject(obj);
		return this;
	}

	public BulletEntity removeCollision(String objFlag) {
		if (closed) {
			return this;
		}
		getCollisionManager().removeObject(objFlag);
		return this;
	}

	public int getCollisionSize() {
		if (closed) {
			return 0;
		}
		return getCollisionManager().numberActors();
	}

	public TArray<CollisionObject> getCollisionObjects() {
		if (closed) {
			return null;
		}
		return getCollisionManager().getActorsList();
	}

	public TArray<CollisionObject> getCollisionObjects(String objFlag) {
		if (closed) {
			return null;
		}
		return getCollisionManager().getObjects(objFlag);
	}

	public TArray<CollisionObject> getCollisionButtles() {
		return getCollisionObjects(Bullet.BUTTLE_DEFAULT_NAME);
	}

	public TArray<CollisionObject> getCollisionObjectsAt(float x, float y, String objFlag) {
		if (closed) {
			return null;
		}
		return getCollisionManager().getObjectsAt(x, y, objFlag);
	}

	public TArray<CollisionObject> getCollisionButtlesAt(float x, float y) {
		return getCollisionObjectsAt(x, y, Bullet.BUTTLE_DEFAULT_NAME);
	}

	public TArray<CollisionObject> getIntersectingObjects(CollisionObject obj, String objFlag) {
		if (closed) {
			return null;
		}
		return getCollisionManager().getIntersectingObjects(obj, objFlag);
	}

	public TArray<CollisionObject> getIntersectingButtles(CollisionObject obj) {
		return getIntersectingObjects(obj, Bullet.BUTTLE_DEFAULT_NAME);
	}

	public CollisionObject getOnlyIntersectingObject(CollisionObject obj, String objFlag) {
		if (closed) {
			return null;
		}
		return getCollisionManager().getOnlyIntersectingObject(obj, objFlag);
	}

	public CollisionObject getOnlyIntersectingButtle(CollisionObject obj) {
		return getOnlyIntersectingObject(obj, Bullet.BUTTLE_DEFAULT_NAME);
	}

	public TArray<CollisionObject> getObjectsInRange(float x, float y, float r, String objFlag) {
		if (closed) {
			return null;
		}
		return getCollisionManager().getObjectsInRange(x, y, r, objFlag);
	}

	public TArray<CollisionObject> getButtlesInRange(float x, float y, float r) {
		return getObjectsInRange(x, y, r, Bullet.BUTTLE_DEFAULT_NAME);
	}

	public TArray<CollisionObject> getNeighbours(CollisionObject obj, float distance, boolean d, String objFlag) {
		if (closed) {
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
		if (bind == null) {
			return;
		}
		if (collisionWorld != null) {
			if (worldCollisionFilter == null) {
				worldCollisionFilter = CollisionFilter.getDefault();
			}
			CollisionResult.Result result = collisionWorld.move(bind, bind.getX(), bind.getY(), worldCollisionFilter);
			bind.setLocation(result.goalX, result.goalY);
		}
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
	public boolean isClosed() {
		return closed || super.isClosed();
	}

	@Override
	public void close() {
		super.close();
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
		running = false;
		closed = true;
	}

}
