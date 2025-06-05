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
import loon.LSystem;
import loon.LTexture;
import loon.PlayerUtils;
import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.collision.CollisionObject;
import loon.action.map.Field2D;
import loon.action.sprite.BulletEntity.BulletListener;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Shape;
import loon.geom.Vector2f;
import loon.geom.XY;
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
public class Bullet extends LObject<BulletEntity> implements CollisionObject {

	public static enum WaveType {
		None, Cos, Sin, Cos_Rotate, Sin_Rotate
	}

	protected static String BUTTLE_DEFAULT_NAME = "Buttle";

	// 初始化的子弹移动速度设定
	protected static int INIT_MOVE_SPEED = 50;

	// 初始化的缓动动画持续时长设定
	protected static float INIT_DURATION = 2f;

	private WaveType _waveType;

	private int _bulletType;
	private int _direction;
	private int _initSpeed;

	private final Vector2f _speed = new Vector2f();
	private final Vector2f _waveOffset = new Vector2f();

	private final EaseTimer _easeTimer;

	private Field2D _arrayMap;

	private Animation _animation;

	private BulletListener _listener;

	private boolean _dirToAngle;
	private boolean _visible;
	private boolean _active;
	private boolean _autoRemoved;

	private float _width;
	private float _height;
	private float _scaleX;
	private float _scaleY;
	private float _scaleSpeed;
	private float _waveamplitude;
	private float _wavefrequency;
	private float _lifeTimer;
	private float _lifeCounter;

	private LColor _baseColor;

	public Bullet(EasingMode easingMode, LTexture tex, float x, float y) {
		this(easingMode, tex, x, y, 0, INIT_DURATION);
	}

	public Bullet(EasingMode easingMode, Animation ani, float x, float y) {
		this(easingMode, ani, x, y, 0);
	}

	public Bullet(EasingMode easingMode, float x, float y) {
		this(easingMode, x, y, 0);
	}

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
		this(easingMode, null, x, y, LSystem.LAYER_TILE_SIZE, LSystem.LAYER_TILE_SIZE, dir, INIT_DURATION);
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
		this._easeTimer = new EaseTimer(duration, easingMode);
		this._waveType = WaveType.None;
		this._baseColor = LColor.white.cpy();
		this._animation = ani;
		this._initSpeed = bulletInitSpeed;
		this._bulletType = bulletType;
		this._direction = -1;
		this._lifeTimer = this._lifeCounter = 0f;
		this._scaleX = this._scaleY = 1f;
		this._waveamplitude = this._wavefrequency = 1f;
		this._scaleSpeed = 1f;
		this._visible = true;
		this._dirToAngle = true;
		this._active = true;
		this._autoRemoved = true;
		this._width = w;
		this._height = h;
		this.setDirection(dir);
	}

	public WaveType getWaveType() {
		return this._waveType;
	}

	public Bullet setWaveType(WaveType w) {
		if (w == null) {
			this._waveType = WaveType.None;
			return this;
		}
		this._waveType = w;
		return this;
	}

	public Bullet setWaveAmplitude(float a) {
		this._waveamplitude = a;
		return this;
	}

	public float getWaveAmplitude() {
		return this._waveamplitude;
	}

	public Bullet setWaveFrequency(float f) {
		this._wavefrequency = f;
		return this;
	}

	public float getWaveFrequency() {
		return this._wavefrequency;
	}

	public EaseTimer getEaseTimer() {
		return this._easeTimer;
	}

	public Bullet setEaseTimerLoop(boolean l) {
		this._easeTimer.setLoop(l);
		return this;
	}

	public boolean isEaseTimerLoop() {
		return this._easeTimer.isLoop();
	}

	public boolean isEaseCompleted() {
		return this._easeTimer.isCompleted();
	}

	public boolean isAutoRemoved() {
		return this._autoRemoved;
	}

	public Bullet setAutoRemoved(boolean a) {
		this._autoRemoved = a;
		return this;
	}

	public Bullet setListener(BulletListener l) {
		this._listener = l;
		return this;
	}

	public BulletListener getListener() {
		return this._listener;
	}

	public Bullet setDuration(float d) {
		this._easeTimer.setDuration(d);
		return this;
	}

	public float getLifeTimer() {
		return this._lifeTimer;
	}

	public Bullet setLifeTimer(float f) {
		this._lifeTimer = f;
		return this;
	}

	public void draw(GLEx g) {
		draw(g, 0, 0);
	}

	public void draw(GLEx g, float offsetX, float offsetY) {
		if (!_visible || _destroyed) {
			return;
		}
		onDrawable(g, offsetX, offsetY);
		if (_animation != null) {
			LTexture texture = _animation.getSpriteImage();
			float tmp = _baseColor.a;
			if (texture != null) {
				g.draw(texture, getX() + offsetX, getY() + offsetY, getWidth(), getHeight(),
						_baseColor.setAlpha(_objectAlpha), _objectRotation);
			}
			_baseColor.a = tmp;
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (_destroyed) {
			return;
		}
		if ((_speed.getX() == 0) && (_speed.getY() == 0)) {
			this._active = false;
		}
		if (_active) {
			onUpdateable(elapsedTime);
			_animation.update(elapsedTime);
			_easeTimer.update(elapsedTime);
			if (!checkLifeOver(_listener)) {
				Vector2f speedOffset = getWaveSpeedOffset(_easeTimer);
				setLocation(getX() + speedOffset.x, getY() + speedOffset.y);
			}
			if (_listener != null && _easeTimer.isCompleted()) {
				_listener.easeover(this);
			}
		}
	}

	/**
	 * 如果子弹生命周期到时,触发监听
	 * 
	 * @param l
	 */
	protected boolean checkLifeOver(BulletListener l) {
		if (_lifeTimer > 0f) {
			if (_lifeCounter >= _lifeTimer) {
				if (l != null) {
					l.lifeover(this);
				}
				if (_autoRemoved) {
					BulletEntity bm = getSuper();
					if (bm != null) {
						bm.removeBullet(this);
					}
				}
				_lifeCounter = 0f;
				return true;
			}
			_lifeCounter += _easeTimer.getDelta();
		}
		return false;
	}

	/**
	 * 当设定waveType类型为sin或cos时,会根据waveamplitude以及wavefrequency参数调整子弹的波形轨迹
	 * 
	 * @param ease
	 * @return
	 */
	protected Vector2f getWaveSpeedOffset(EaseTimer ease) {
		float delta = _easeTimer.getProgress();
		float angle = 0f;
		switch (_waveType) {
		case Sin:
			angle = _waveamplitude * MathUtils.sin(delta * MathUtils.TWO_PI * _wavefrequency);
			_waveOffset.set(_speed.getX() * MathUtils.cos(angle), _speed.getY() * MathUtils.sin(angle));
			break;
		case Cos:
			angle = _waveamplitude * MathUtils.cos(delta * MathUtils.TWO_PI * _wavefrequency);
			_waveOffset.set(_speed.getX() * MathUtils.cos(angle), _speed.getY() * MathUtils.sin(angle));
			break;
		case Sin_Rotate:
			angle = MathUtils.waveSin(_wavefrequency, _waveamplitude, ease.getDelta()) * delta;
			_waveOffset.set(_speed.getX() * MathUtils.cos(angle), _speed.getY() * MathUtils.sin(angle));
			break;
		case Cos_Rotate:
			angle = MathUtils.waveCos(_wavefrequency, _waveamplitude, ease.getDelta()) * delta;
			_waveOffset.set(_speed.getX() * MathUtils.cos(angle), _speed.getY() * MathUtils.sin(angle));
			break;
		default:
			_waveOffset.set(_speed.getX() * delta, _speed.getY() * delta);
			break;
		}
		return _waveOffset;
	}

	public void update(LTimerContext time) {
		update(time.timeSinceLastUpdate);
	}

	public float getScaleSpeed() {
		return this._scaleSpeed;
	}

	public Bullet setScaleSpeed(float s) {
		if (s == this._scaleSpeed) {
			return this;
		}
		this._scaleSpeed = s;
		this._speed.mulSelf(s);
		return this;
	}

	protected void onAttached() {
		if (_listener != null) {
			_listener.attached(this);
		}
	}

	protected void onDetached() {
		if (_listener != null) {
			_listener.detached(this);
		}
	}

	protected void onDrawable(GLEx g, float offsetX, float offsetY) {
		if (_listener != null) {
			_listener.drawable(g, this);
		}
	}

	protected void onUpdateable(long elapsedTime) {
		if (_listener != null) {
			_listener.updateable(elapsedTime, this);
		}
	}

	public int getDirection() {
		return _direction;
	}

	public boolean isDirToAngle() {
		return _dirToAngle;
	}

	public Bullet setDirToAngle(boolean dta) {
		this._dirToAngle = dta;
		return this;
	}

	public Vector2f getSpeed() {
		return _speed.cpy();
	}

	public Bullet setSpeedX(int x) {
		_speed.setX(x);
		return this;
	}

	public Bullet setSpeedY(int y) {
		_speed.setX(y);
		return this;
	}

	public Bullet setInitSpeed(int s) {
		if (this._initSpeed != s) {
			this._initSpeed = s;
			Field2D.getDirectionToPoint(this._direction, this._initSpeed, this._speed);
		}
		return this;
	}

	public int getInitSpeed() {
		return this._initSpeed;
	}

	public Bullet fireTo(ISprite spr) {
		return fireTo(spr, 0f, 0f);
	}

	public Bullet fireTo(ISprite spr, float offsetX, float offsetY) {
		if (spr == null) {
			return this;
		}
		return setMoveTargetToRotation(
				Vector2f.at(spr.getX() + spr.getWidth() / 2f + offsetX, spr.getY() + spr.getHeight() / 2f + offsetY));
	}

	public Bullet fireTo(ActionBind act) {
		return fireTo(act, 0f, 0f);
	}

	public Bullet fireTo(ActionBind act, float offsetX, float offsetY) {
		if (act == null) {
			return this;
		}
		return setMoveTargetToRotation(
				Vector2f.at(act.getX() + act.getWidth() / 2f + offsetX, act.getY() + act.getHeight() / 2f + offsetY));
	}

	public Bullet fireTo(XY pos) {
		if (pos == null) {
			return this;
		}
		return setMoveTargetToRotation(Vector2f.at(pos));
	}

	public Bullet fireTo(float dstX, float dstY) {
		return setMoveTargetToRotation(dstX, dstY);
	}

	public Bullet setMoveTargetToRotation(float dstX, float dstY) {
		return setMoveTargetToRotation(Vector2f.at(dstX, dstY));
	}

	public Bullet setMoveTargetToRotation(Vector2f target) {
		if (target == null) {
			return this;
		}
		float rot = Field2D.rotation(getLocation(), target);
		if (_dirToAngle) {
			this.setRotation(rot);
		}
		this._direction = Field2D.getDirection(getLocation(), target);
		float dir = MathUtils.atan2(target.getY() - getY(), target.getX() - getX());
		float sx = (MathUtils.cos(dir) * this._initSpeed);
		float sy = (MathUtils.sin(dir) * this._initSpeed);
		this._speed.setLocation(sx, sy);
		return this;
	}

	public Bullet setDirection(int dir) {
		Field2D.getDirectionToPoint(dir, this._initSpeed, _speed);
		if (_dirToAngle) {
			this.setRotation(Field2D.getDirectionToAngle(dir));
		}
		this._direction = dir;
		return this;
	}

	public boolean isCollision(CollisionObject o) {
		if (o == null) {
			return false;
		}
		RectBox rect = getCollisionArea();
		return rect.intersects(o.getRectBox()) || rect.contains(o.getRectBox());
	}

	public boolean isCollision(Shape shape) {
		if (shape == null) {
			return false;
		}
		RectBox rect = getCollisionArea();
		return rect.intersects(shape) || rect.contains(shape);
	}

	public Animation getAnimation() {
		return _animation;
	}

	public LTexture getTexture() {
		return _animation == null ? null : _animation.getSpriteImage();
	}

	@Override
	public RectBox getCollisionArea() {
		return setRect(MathUtils.getBounds(getScalePixelX(), getScalePixelY(), getWidth(), getHeight(), _objectRotation,
				_objectRect));
	}

	public float getScalePixelX() {
		return ((_scaleX == 1f) ? getX() : (getX() + getWidth() / 2f));
	}

	public float getScalePixelY() {
		return ((_scaleY == 1f) ? getY() : (getY() + getHeight() / 2f));
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
	public boolean contains(CollisionObject o) {
		return getCollisionArea().contains(o.getRectBox());
	}

	@Override
	public boolean intersects(CollisionObject o) {
		return getCollisionArea().intersects(o.getRectBox());
	}

	@Override
	public boolean intersects(Shape shape) {
		return getCollisionArea().intersects(shape);
	}

	@Override
	public boolean contains(Shape shape) {
		return getCollisionArea().contains(shape);
	}

	@Override
	public boolean collided(Shape shape) {
		return getCollisionArea().collided(shape);
	}

	@Override
	public float getWidth() {
		return _width > 1 ? (_width * this._scaleX) : _animation == null ? 0 : (_animation.getWidth() * this._scaleX);
	}

	@Override
	public float getHeight() {
		return _height > 1 ? (_height * this._scaleY)
				: _animation == null ? 0 : (_animation.getHeight() * this._scaleY);
	}

	@Override
	public boolean isVisible() {
		return _visible;
	}

	@Override
	public void setVisible(boolean v) {
		this._visible = v;
	}

	@Override
	public LColor getColor() {
		return _baseColor == null ? null : _baseColor.cpy();
	}

	@Override
	public void setColor(LColor newColor) {
		this._baseColor = newColor;
	}

	public Bullet setField2D(Field2D arrayMap) {
		this._arrayMap = arrayMap;
		return this;
	}

	@Override
	public Field2D getField2D() {
		return _arrayMap;
	}

	@Override
	public float getScaleX() {
		return _scaleX;
	}

	@Override
	public float getScaleY() {
		return _scaleY;
	}

	public Bullet setScale(float scale) {
		setScale(scale, scale);
		return this;
	}

	@Override
	public void setScale(float sx, float sy) {
		this._scaleX = sx;
		this._scaleY = sy;
	}

	@Override
	public Bullet setSize(float w, float h) {
		this._width = w;
		this._height = h;
		return this;
	}

	public Bullet setWidth(float w) {
		this._width = w;
		return this;
	}

	public Bullet setHeight(float h) {
		this._height = h;
		return this;
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
		return _active;
	}

	public Bullet setActive(boolean active) {
		this._active = active;
		return this;
	}

	public int getBulletType() {
		return _bulletType;
	}

	public Bullet setBulletType(int bulletType) {
		this._bulletType = bulletType;
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
	protected void _onDestroy() {
		if (_animation != null) {
			_animation.close();
			_animation = null;
		}
		_listener = null;
		_visible = false;
	}

}
