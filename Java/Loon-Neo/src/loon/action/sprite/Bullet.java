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

import loon.LObject;
import loon.LRelease;
import loon.LTexture;
import loon.action.collision.CollisionObject;
import loon.action.map.Field2D;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.timer.LTimerContext;

public class Bullet extends LObject<Bullet> implements CollisionObject, LRelease {

	private int direction;
	private int initSpeed;

	private Vector2f speed;
	private Animation animation;

	private boolean closed;
	private boolean visible;

	private float width;
	private float height;

	public Bullet(LTexture tex, float x, float y, int dir) {
		this(Animation.getDefaultAnimation(tex), x, y, dir);
	}

	public Bullet(LTexture tex, float x, float y, int dir, int speed) {
		this(Animation.getDefaultAnimation(tex), x, y, dir, speed);
	}

	public Bullet(Animation ani, float x, float y, int dir) {
		this(ani, x, y, ani.getSpriteImage().getWidth(), ani.getSpriteImage().getHeight(), dir, 300);
	}

	public Bullet(Animation ani, float x, float y, int dir, int speed) {
		this(ani, x, y, ani.getSpriteImage().getWidth(), ani.getSpriteImage().getHeight(), dir, speed);
	}

	public Bullet(float x, float y, int dir) {
		this(null, x, y, 32, 32, dir, 300);
	}

	public Bullet(Animation ani, float x, float y, float w, float h, int dir, int bulletSpeed) {
		this.setLocation(x, y);
		this.setObjectFlag(BulletEntity.buttleDefaultName);
		this.animation = ani;
		this.direction = dir;
		this.initSpeed = bulletSpeed;
		this.speed = Field2D.getDirectionToPoint(this.direction, this.initSpeed);
		this.visible = true;
		this.closed = false;
		this.width = w;
		this.height = h;
		this._rect = new RectBox(x, y, w, h);
	}

	public void draw(GLEx g) {
		draw(g, 0, 0);
	}

	public void draw(GLEx g, float offsetX, float offsetY) {
		if (!visible || closed) {
			return;
		}
		if (animation != null) {
			LTexture texture = animation.getSpriteImage();
			if (texture != null) {
				g.draw(texture, getX() + offsetX, getY() + offsetY, getWidth(), getHeight());
				width = MathUtils.max(width, texture.width());
				height = MathUtils.max(height, texture.height());
			}
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (closed) {
			return;
		}
		animation.update(elapsedTime);
		float delta = elapsedTime / 1000f;
		float x = getX() + speed.getX() * delta;
		float y = getY() + speed.getY() * delta;
		setLocation(x, y);
	}

	public void update(LTimerContext time) {
		update(time.getTimeSinceLastUpdate());
	}

	public int getDirection() {
		return direction;
	}

	public Vector2f getSpeed() {
		return speed;
	}

	public Bullet setSpeedX(int x) {
		speed.setX(x);
		return this;
	}

	public Bullet setSpeedY(int y) {
		speed.setX(y);
		return this;
	}

	public void setDirection(int dir) {
		if (this.direction != dir) {
			this.speed = Field2D.getDirectionToPoint(this.direction, this.initSpeed);
		}
		this.direction = dir;
	}

	public Animation getAnimation() {
		return animation;
	}

	public LTexture getTexture() {
		return animation.getSpriteImage();
	}

	@Override
	public RectBox getBoundingRect() {
		return getCollisionArea();
	}

	@Override
	public RectBox getRectBox() {
		return getCollisionArea();
	}

	@Override
	public boolean containsPoint(float x, float y) {
		return getCollisionArea().contains(x, y);
	}

	@Override
	public boolean intersects(CollisionObject object) {
		return getCollisionArea().intersects(object.getRectBox());
	}

	@Override
	public boolean intersects(RectBox rect) {
		return getCollisionArea().intersects(rect);
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public float getHeight() {
		return height;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public void close() {
		if (animation != null) {
			animation.close();
			animation = null;
		}
		closed = true;
		visible = false;
	}

}
