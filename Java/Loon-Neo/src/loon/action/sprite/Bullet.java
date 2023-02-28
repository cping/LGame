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
import loon.PlayerUtils;
import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.collision.CollisionObject;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.Easing.EasingMode;
import loon.utils.MathUtils;
import loon.utils.timer.EaseTimer;
import loon.utils.timer.LTimerContext;

/**
 * 子弹渲染用类,支持动画播放,
 * 角色角度和方向的自动转换，但本身不是精灵,不能直接add到Screen,由精灵类BulletEntity管理和渲染到游戏中去<br>
 * 一个游戏中，可以存在多个甚至海量的Bullet, 如果子弹过多时,可以使用CacheManager管理子弹的生命周期.
 * 
 */
public class Bullet extends LObject<Bullet> implements CollisionObject, ActionBind, LRelease {

	protected static String BUTTLE_DEFAULT_NAME = "Buttle";

	protected static int INIT_MOVE_SPEED = 100;

	protected static float INIT_DURATION = 1f;

	private int bulletType;
	private int direction;
	private int initSpeed;

	private Vector2f speed;
	private Animation animation;

	private boolean dirToAngle;
	private boolean closed;
	private boolean visible;
	private boolean active;

	private float width;
	private float height;
	private float scaleX;
	private float scaleY;

	private LColor baseColor;

	private EaseTimer easeTimer;

	public Bullet(EasingMode easingMode, LTexture tex, float x, float y, int dir) {
		this(easingMode, Animation.getDefaultAnimation(tex), x, y, dir);
	}

	public Bullet(EasingMode easingMode, LTexture tex, float x, float y, int dir, float duration) {
		this(easingMode, Animation.getDefaultAnimation(tex), x, y, dir, INIT_MOVE_SPEED, duration);
	}

	public Bullet(EasingMode easingMode, Animation ani, float x, float y, int dir) {
		this(easingMode, ani, x, y, ani.getSpriteImage().getWidth(), ani.getSpriteImage().getHeight(), dir,
				INIT_DURATION);
	}

	public Bullet(EasingMode easingMode, LTexture texture, float x, float y, int dir, int initSpeed) {
		this(easingMode, Animation.getDefaultAnimation(texture), x, y, dir, initSpeed);
	}

	public Bullet(EasingMode easingMode, Animation ani, float x, float y, int dir, int initSpeed) {
		this(easingMode, ani, x, y, ani.getSpriteImage().getWidth(), ani.getSpriteImage().getHeight(), dir, initSpeed,
				INIT_DURATION);
	}

	public Bullet(EasingMode easingMode, float x, float y, int dir) {
		this(easingMode, null, x, y, 32, 32, dir, INIT_DURATION);
	}

	public Bullet(EasingMode easingMode, LTexture texture, float x, float y, int dir, int initSpeed, float duration) {
		this(easingMode, Animation.getDefaultAnimation(texture), x, y, texture.getWidth(), texture.getHeight(), dir,
				initSpeed, duration);
	}

	public Bullet(EasingMode easingMode, Animation ani, float x, float y, int dir, int initSpeed, float duration) {
		this(easingMode, ani, x, y, ani.getSpriteImage().getWidth(), ani.getSpriteImage().getHeight(), dir, initSpeed,
				duration);
	}

	public Bullet(EasingMode easingMode, Animation ani, float x, float y, float w, float h, int dir, float duration) {
		this(easingMode, ani, x, y, w, h, dir, INIT_MOVE_SPEED, duration);
	}

	public Bullet(EasingMode easingMode, Animation ani, float x, float y, float w, float h, int dir,
			int bulletInitSpeed, float duration) {
		this(0, easingMode, ani, x, y, w, h, dir, bulletInitSpeed, duration);
	}

	public Bullet(int bulletType, EasingMode easingMode, Animation ani, float x, float y, float w, float h, int dir,
			int bulletInitSpeed, float duration) {
		this.setLocation(x, y);
		this.setObjectFlag(BUTTLE_DEFAULT_NAME);
		this.easeTimer = new EaseTimer(duration, easingMode);
		this.baseColor = LColor.white.cpy();
		this.animation = ani;
		this.direction = dir;
		this.initSpeed = bulletInitSpeed;
		this.bulletType = bulletType;
		this.scaleX = this.scaleY = 1f;
		this.visible = true;
		this.dirToAngle = true;
		this.active = true;
		this.closed = false;
		this.width = w;
		this.height = h;
		this.setDirection(this.direction);
		this.getRect(x, y, w, h);
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
			float tmp = baseColor.a;
			if (texture != null) {
				g.draw(texture, getX() + offsetX, getY() + offsetY, getWidth(), getHeight(),
						baseColor.setAlpha(_objectAlpha), _objectRotation);
				width = MathUtils.max(width, texture.width());
				height = MathUtils.max(height, texture.height());
			}
			baseColor.a = tmp;
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (closed) {
			return;
		}
		if ((speed.getX() == 0) && (speed.getY() == 0)) {
			this.active = false;
		}
		if (active) {
			animation.update(elapsedTime);
			easeTimer.update(elapsedTime);
			float delta = easeTimer.getProgress();
			float x = getX() + speed.getX() * delta;
			float y = getY() + speed.getY() * delta;
			setLocation(x, y);
		}
	}

	public void update(LTimerContext time) {
		update(time.timeSinceLastUpdate);
	}

	public int getDirection() {
		return direction;
	}

	public boolean isDirToAngle() {
		return dirToAngle;
	}

	public void setDirToAngle(boolean dta) {
		this.dirToAngle = dta;
	}

	public Vector2f getInitSpeed() {
		return speed;
	}

	public Bullet setInitSpeedX(int x) {
		speed.setX(x);
		return this;
	}

	public Bullet setInitSpeedY(int y) {
		speed.setX(y);
		return this;
	}

	public Bullet setMoveTargetToRotation(float dstX, float dstY) {
		return setMoveTargetToRotation(Vector2f.at(dstX, dstY));
	}

	public Bullet setMoveTargetToRotation(Vector2f target) {
		if (target == null) {
			return this;
		}
		float rot = Field2D.rotation(getLocation(), target);
		this.setRotation(rot);
		this.direction = Field2D.getDirection(getLocation(), target);
		return this;
	}

	public Bullet setDirection(int dir) {
		if (this.direction != dir || this.speed == null) {
			this.speed = Field2D.getDirectionToPoint(this.direction, this.initSpeed);
			if (dirToAngle) {
				this.setRotation(Field2D.getDirectionToAngle(dir));
			}
		}
		this.direction = dir;
		return this;
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
	public boolean intersects(CollisionObject o) {
		return getCollisionArea().intersects(o.getRectBox());
	}

	@Override
	public boolean intersects(RectBox rect) {
		return getCollisionArea().intersects(rect);
	}

	@Override
	public float getWidth() {
		return width * scaleX;
	}

	@Override
	public float getHeight() {
		return height * scaleY;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean v) {
		this.visible = v;
	}

	@Override
	public LColor getColor() {
		return baseColor.cpy();
	}

	@Override
	public void setColor(LColor newColor) {
		this.baseColor = newColor;
	}

	@Override
	public Field2D getField2D() {
		return null;
	}

	@Override
	public float getScaleX() {
		return scaleX;
	}

	@Override
	public float getScaleY() {
		return scaleY;
	}

	public Bullet setScale(float scale) {
		setScale(scale, scale);
		return this;
	}

	@Override
	public void setScale(float sx, float sy) {
		this.scaleX = sx;
		this.scaleY = sy;
	}

	@Override
	public boolean isBounded() {
		return false;
	}

	@Override
	public boolean isContainer() {
		return false;
	}

	public boolean isActive() {
		return active;
	}

	public Bullet setActive(boolean active) {
		this.active = active;
		return this;
	}

	public int getBulletType() {
		return bulletType;
	}

	public Bullet setBulletType(int bulletType) {
		this.bulletType = bulletType;
		return this;
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return getRectBox().contains(x, y, w, h);
	}

	@Override
	public ActionTween selfAction() {
		return PlayerUtils.set(this);
	}

	@Override
	public boolean isActionCompleted() {
		return PlayerUtils.isActionCompleted(this);
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
