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

import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.action.collision.CollisionManager;
import loon.action.collision.CollisionObject;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.LTextureFree;
import loon.utils.TArray;

/**
 * 子弹用Entity,用来显示以及管理子弹(复数)用类
 */
public class BulletEntity extends Entity {

	protected final static String buttleDefaultName = "Buttle";

	private boolean closed;

	private boolean running;

	private boolean limitMoved;

	private TArray<Bullet> bullets;

	private LTextureFree textureFree;

	private CollisionManager collisionManager;

	public BulletEntity() {
		this(0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public BulletEntity(int x, int y, int w, int h) {
		this.bullets = new TArray<Bullet>(32);
		this.textureFree = new LTextureFree();
		this.collisionManager = getCollisionManager();
		this.closed = false;
		this.running = true;
		this.limitMoved = true;
		this.setRepaint(true);
		this.setLocation(x, y);
		this.setSize(w, h);
	}

	public CollisionManager getCollisionManager() {
		if (collisionManager == null) {
			collisionManager = new CollisionManager();
		}
		return collisionManager;
	}

	public BulletEntity addBullet(LTexture texture, float x, float y, int dir) {
		return addBullet(new Bullet(texture, x, y, dir));
	}

	public BulletEntity addBullet(Animation ani, float x, float y, int dir) {
		return addBullet(new Bullet(ani, x, y, dir));
	}
	
	public BulletEntity addBullet(String path, float x, float y, int dir) {
		return addBullet(new Bullet(LTextures.loadTexture(path), x, y, dir));
	}

	public BulletEntity addBullet(Animation ani, float x, float y, int dir, int speed) {
		return addBullet(new Bullet(ani, x, y, dir, speed));
	}
	
	public BulletEntity addBullet(String path, float x, float y, int dir, int speed) {
		return addBullet(new Bullet(LTextures.loadTexture(path), x, y, dir, speed));
	}

	public BulletEntity addBullet(Bullet bullet) {
		if (closed) {
			return this;
		}
		if (bullet == null) {
			return this;
		}
		if (bullet.getTexture() != null) {
			textureFree.add(bullet.getTexture(), false);
		}
		bullets.add(bullet);
		collisionManager.addObject(bullet);
		return this;
	}

	@Override
	protected void repaint(GLEx g, float offsetX, float offsetY) {
		if (closed) {
			return;
		}
		for (int i = this.bullets.size - 1; i >= 0; i--) {
			Bullet bu = bullets.get(i);
			if (bu != null) {
				bu.draw(g, offsetX + getX(), offsetY + getY());
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
					bu.update(elapsedTime);
					if (limitMoved && !getRectBox().intersects(bu.getRectBox())) {
						bullets.remove(bu);
						collisionManager.removeObject(bu);
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

	public void setLimitMoved(boolean limitMoved) {
		this.limitMoved = limitMoved;
	}

	public void initializeCollision(int tileSize) {
		getCollisionManager().initialize(tileSize);
	}

	public void initializeCollision(int tileSizeX, int tileSizeY) {
		getCollisionManager().initialize(tileSizeX, tileSizeY);
	}

	public void setCollisionInTheLayer(boolean itlayer) {
		if (closed) {
			return;
		}
		collisionManager.setInTheLayer(itlayer);
	}

	public boolean getCollisionInTheLayer() {
		if (closed) {
			return false;
		}
		return collisionManager.getInTheLayer();
	}

	public void setCollisionOffsetPos(float x, float y) {
		if (closed) {
			return;
		}
		collisionManager.setOffsetPos(x, y);
	}

	public void setCollisionOffsetX(float x) {
		if (closed) {
			return;
		}
		collisionManager.setOffsetX(x);
	}

	public void setCollisionOffsetY(float y) {
		if (closed) {
			return;
		}
		collisionManager.setOffsetY(y);
	}

	public Vector2f getCollisionOffsetPos() {
		if (closed) {
			return Vector2f.ZERO();
		}
		return collisionManager.getOffsetPos();
	}

	public void putCollision(CollisionObject obj) {
		if (closed) {
			return;
		}
		collisionManager.addObject(obj);
	}

	public void removeCollision(CollisionObject obj) {
		if (closed) {
			return;
		}
		collisionManager.removeObject(obj);
	}

	public void removeCollision(String objFlag) {
		if (closed) {
			return;
		}
		collisionManager.removeObject(objFlag);
	}

	public int getCollisionSize() {
		if (closed) {
			return 0;
		}
		return collisionManager.numberActors();
	}

	public TArray<CollisionObject> getCollisionObjects() {
		if (closed) {
			return null;
		}
		return collisionManager.getActorsList();
	}

	public TArray<CollisionObject> getCollisionObjects(String objFlag) {
		if (closed) {
			return null;
		}
		return collisionManager.getObjects(objFlag);
	}

	public TArray<CollisionObject> getCollisionButtles() {
		return getCollisionObjects(buttleDefaultName);
	}

	public TArray<CollisionObject> getCollisionObjectsAt(float x, float y, String objFlag) {
		if (closed) {
			return null;
		}
		return collisionManager.getObjectsAt(x, y, objFlag);
	}

	public TArray<CollisionObject> getCollisionButtlesAt(float x, float y) {
		return getCollisionObjectsAt(x, y, buttleDefaultName);
	}

	public TArray<CollisionObject> getIntersectingObjects(CollisionObject obj, String objFlag) {
		if (closed) {
			return null;
		}
		return collisionManager.getIntersectingObjects(obj, objFlag);
	}

	public TArray<CollisionObject> getIntersectingButtles(CollisionObject obj) {
		return getIntersectingObjects(obj, buttleDefaultName);
	}

	public CollisionObject getOnlyIntersectingObject(CollisionObject obj, String objFlag) {
		if (closed) {
			return null;
		}
		return collisionManager.getOnlyIntersectingObject(obj, objFlag);
	}

	public CollisionObject getOnlyIntersectingButtle(CollisionObject obj) {
		return getOnlyIntersectingObject(obj, buttleDefaultName);
	}

	public TArray<CollisionObject> getObjectsInRange(float x, float y, float r, String objFlag) {
		if (closed) {
			return null;
		}
		return collisionManager.getObjectsInRange(x, y, r, objFlag);
	}

	public TArray<CollisionObject> getButtlesInRange(float x, float y, float r) {
		return getObjectsInRange(x, y, r, buttleDefaultName);
	}

	public TArray<CollisionObject> getNeighbours(CollisionObject obj, float distance, boolean d, String objFlag) {
		if (closed) {
			return null;
		}
		if (distance < 0) {
			throw LSystem.runThrow("distance < 0");
		} else {
			return collisionManager.getNeighbours(obj, distance, d, objFlag);
		}
	}

	public TArray<CollisionObject> getNeighboursButtles(CollisionObject obj, float distance, boolean d) {
		return getNeighbours(obj, distance, d, buttleDefaultName);
	}

	@Override
	public void close() {
		super.close();
		clearBullets();
		if (textureFree != null) {
			textureFree.close();
			textureFree = null;
		}
		if (collisionManager != null) {
			collisionManager.dispose();
			collisionManager = null;
		}
		running = false;
		closed = true;
	}

}
