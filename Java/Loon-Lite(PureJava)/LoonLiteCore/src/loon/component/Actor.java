/**
 * 
 * Copyright 2008 - 2010
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
 * @version 0.1
 */
package loon.component;

import loon.LObject;
import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.PlayerUtils;
import loon.Screen;
import loon.Visible;
import loon.action.ActionBind;
import loon.action.ActionControl;
import loon.action.ActionTween;
import loon.action.ArrowTo;
import loon.action.CircleTo;
import loon.action.ColorTo;
import loon.action.FadeTo;
import loon.action.FireTo;
import loon.action.JumpTo;
import loon.action.MoveTo;
import loon.action.RotateTo;
import loon.action.ScaleTo;
import loon.action.ShakeTo;
import loon.action.collision.CollisionObject;
import loon.action.map.Field2D;
import loon.action.sprite.Animation;
import loon.canvas.LColor;
import loon.geom.BoxSize;
import loon.geom.RectBox;
import loon.geom.Shape;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.utils.Flip;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

/**
 * 演员类,用于同Layer合作渲染游戏,这个类的可控性要比Sprite包下类及其子类强,<br>
 * 但是操作上设定的也更加死板,不适用于复杂游戏
 */
public class Actor extends LObject<Actor>
		implements CollisionObject, Flip<Actor>, Comparable<Actor>, ActionBind, Visible, XY, LRelease, BoxSize {

	private static int sequenceNumber = 0;

	private int noSequenceNumber;

	private int lastPaintSequenceNumber;

	public boolean isConsumerDrawing = true;

	boolean visible = true, draged = true, clicked = false;

	private ActorLayer gameLayer;

	private LTexture image;

	private RectBox _boundingRect;

	private float _actorWidth, _actorHeight;

	private float[] _currentPos = new float[2];

	private float[] _positionXs = new float[4];

	private float[] _positionYs = new float[4];

	private LTimer _curTimer = new LTimer(0);

	private Animation animation;

	protected boolean isAnimation;

	protected LColor filterColor;

	protected ActorListener actorListener;

	protected float scaleX = 1, scaleY = 1;

	protected boolean flipX = false, flipY = false;

	public Actor(Animation animation) {
		this(animation, 0f, 0f);
	}

	public Actor(Animation animation, float x, float y) {
		this(animation, 0f, 0f, 0f, 0f);
	}

	public Actor(Animation animation, float x, float y, float w, float h) {
		if (animation == null) {
			throw new LSysException("Animation is null !");
		}
		this.noSequenceNumber = sequenceNumber++;
		this.animation = animation;
		this.isAnimation = true;
		this._actorWidth = w;
		this._actorHeight = h;
		this._objectLocation.set(x, y);
		this.setImage(animation.getSpriteImage());
		this.setObjectFlag("Actor");
	}

	public Actor() {
		this((LTexture) null);
	}

	public Actor(LTexture image) {
		this(image, 0f, 0f, 0f, 0f);
	}

	public Actor(LTexture image, float x, float y) {
		this(image, x, y, 0f, 0f);
	}

	public Actor(String fileName, int x, int y) {
		this(LSystem.loadTexture(fileName), x, y);
	}

	public Actor(String fileName) {
		this(fileName, 0, 0);
	}

	public Actor(LTexture image, float x, float y, float w, float h) {
		this.noSequenceNumber = sequenceNumber++;
		this._objectLocation.set(x, y);
		this._actorWidth = w;
		this._actorHeight = h;
		this.setImage(image);
		this.setObjectFlag("Actor");
	}

	public Actor startAnimation() {
		this.isAnimation = true;
		return this;
	}

	public Actor stopAnimation() {
		this.isAnimation = false;
		return this;
	}

	public Actor setSize(float size) {
		return setSize(size, size);
	}

	@Override
	public Actor setSize(float w, float h) {
		if (w != _actorWidth || h != _actorHeight) {
			_actorWidth = w;
			_actorHeight = h;
			_boundingRect = getRectBox();
		}
		return this;
	}

	/**
	 * 移动当前角色到指定位置并返回MoveTo控制器(flag为true时八方向行走，否则为四方向)
	 * 
	 * @param x
	 * @param y
	 */
	public MoveTo moveTo(int x, int y) {
		failIfNotInLayer();
		return gameLayer.callMoveTo(this, x, y);
	}

	/**
	 * 移动当前角色到指定位置并返回MoveTo控制器(flag为true时八方向行走，否则为四方向)
	 * 
	 * @param x
	 * @param y
	 * @param flag
	 * @return
	 */
	public MoveTo moveTo(int x, int y, boolean flag) {
		failIfNotInLayer();
		return gameLayer.callMoveTo(this, x, y, flag);
	}

	/**
	 * 命令当前角色执行淡出操作
	 * 
	 * @return
	 */
	public FadeTo fadeOut() {
		failIfNotInLayer();
		return gameLayer.callFadeOutTo(this, 60);
	}

	/**
	 * 命令当前角色执行淡入操作
	 * 
	 * @return
	 */
	public FadeTo fadeIn() {
		failIfNotInLayer();
		return gameLayer.callFadeInTo(this, 60);
	}

	/**
	 * 命令当前角色变化为指定颜色
	 * 
	 * @param end
	 * @return
	 */
	public ColorTo colorTo(LColor end) {
		failIfNotInLayer();
		return gameLayer.callColorTo(this, getColor(), end);
	}

	/**
	 * 命令当前角色变化为指定颜色
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public ColorTo colorTo(LColor start, LColor end) {
		failIfNotInLayer();
		return gameLayer.callColorTo(this, start, end);
	}

	/**
	 * 命令当前角色变化为指定颜色
	 * 
	 * @param start
	 * @param end
	 * @param duration
	 * @param delay
	 * @return
	 */
	public ColorTo colorTo(LColor start, LColor end, float duration, float delay) {
		failIfNotInLayer();
		return gameLayer.callColorTo(this, start, end, duration, delay);
	}

	/**
	 * 以指定速度渐进式旋转当前角色到指定角度
	 * 
	 * @param rotate
	 * @param speed
	 * @return
	 */
	public RotateTo rotateTo(float rotate, float speed) {
		failIfNotInLayer();
		return gameLayer.callRotateTo(this, rotate, speed);
	}

	/**
	 * 渐进式旋转当前角色到指定角度
	 * 
	 * @param rotate
	 * @return
	 */
	public RotateTo rotateTo(float rotate) {
		return rotateTo(rotate, 2f);
	}

	/**
	 * 以指定加速度指定重力跳跃当前角色
	 * 
	 * @param jump
	 * @param g
	 * @return
	 */
	public JumpTo jumpTo(int jump, float g) {
		failIfNotInLayer();
		return gameLayer.callJumpTo(this, -jump, g);
	}

	/**
	 * 以指定加速度跳跃当前角色
	 * 
	 * @param jump
	 * @return
	 */
	public JumpTo jumpTo(int jump) {
		return jumpTo(jump, 0.3f);
	}

	/**
	 * 让指定角色根据指定半径以指定速度循环转动
	 * 
	 * @param radius
	 * @param velocity
	 * @return
	 */
	public CircleTo circleTo(int radius, int velocity) {
		failIfNotInLayer();
		return gameLayer.callCircleTo(this, radius, velocity);
	}

	/**
	 * 将当前角色作为子弹以指定速度向指定坐标发射
	 * 
	 * @param endX
	 * @param endY
	 * @param speed
	 * @return
	 */
	public FireTo fireTo(float endX, float endY, float speed) {
		failIfNotInLayer();
		return gameLayer.callFireTo(this, endX, endY, speed);
	}

	/**
	 * 将当前角色向指定坐标发射
	 * 
	 * @param endX
	 * @param endY
	 * @return
	 */
	public FireTo fireTo(float endX, float endY) {
		return fireTo(endX, endY, 10);
	}

	/**
	 * 让当前角色缩放指定大小
	 * 
	 * @param sx
	 * @param sy
	 * @return
	 */
	public ScaleTo scaleTo(float sx, float sy) {
		failIfNotInLayer();
		return gameLayer.callScaleTo(this, sx, sy);
	}

	/**
	 * 让当前角色缩放指定大小
	 * 
	 * @param sx
	 * @param sy
	 * @return
	 */
	public ScaleTo scaleTo(float s) {
		failIfNotInLayer();
		return gameLayer.callScaleTo(this, s, s);
	}

	/**
	 * 让指定角色做箭状发射(抛物线)
	 * 
	 * @param tx
	 * @param ty
	 * @return
	 */
	public ArrowTo arrowTo(float tx, float ty) {
		failIfNotInLayer();
		return gameLayer.callArrowTo(this, tx, ty);
	}

	/**
	 * 振动指定对象
	 * 
	 * @param shakeX
	 * @param shakeY
	 * @return
	 */
	public ShakeTo shakeTo(float shakeX, float shakeY) {
		failIfNotInLayer();
		return gameLayer.callShakeTo(this, shakeX, shakeY);
	}

	/**
	 * 振动指定对象
	 * 
	 * @param shakeX
	 * @param shakeY
	 * @param duration
	 * @param delay
	 * @return
	 */
	public ShakeTo shakeTo(float shakeX, float shakeY, float duration, float delay) {
		failIfNotInLayer();
		return gameLayer.callShakeTo(this, shakeX, shakeY, duration, delay);
	}

	/**
	 * 删除所有以当前Actor注册的动作事件
	 * 
	 */
	public Actor removeActionEvents() {
		removeActionEvents(this);
		return this;
	}

	/**
	 * 缩放当前角色
	 * 
	 * @param scale
	 */
	public void setScale(final float s) {
		this.setScale(s, s);
	}

	@Override
	public void setScale(final float sx, final float sy) {
		if (this.scaleX == sx && this.scaleY == sy) {
			return;
		}
		this.scaleX = sx;
		this.scaleY = sy;
	}

	@Override
	public float getScaleX() {
		return this.scaleX;
	}

	@Override
	public float getScaleY() {
		return this.scaleY;
	}

	/**
	 * 按下
	 * 
	 */
	public void downClick(int x, int y) {

	}

	/**
	 * 放开
	 * 
	 */
	public void upClick(int x, int y) {

	}

	/**
	 * 键盘按下
	 * 
	 */
	public void downKey() {

	}

	/**
	 * 键盘放开
	 * 
	 */
	public void upKey() {

	}

	/**
	 * 拖拽
	 * 
	 */
	public void drag(int x, int y) {

	}

	/**
	 * 动作处理(内部传参)
	 * 
	 */
	@Override
	public void update(long elapsedTime) {
		if (_curTimer.action(elapsedTime)) {
			if (isAnimation) {
				if (animation != null) {
					animation.update(elapsedTime);
					setImage(animation.getSpriteImage());
				}
			}
			synchronized (LLayer.class) {
				action(elapsedTime);
			}
		}
	}

	/**
	 * 设定动作触发延迟时间
	 * 
	 * @param delay
	 */
	public Actor setDelay(long delay) {
		_curTimer.setDelay(delay);
		return this;
	}

	/**
	 * 设定动作触发延迟时间(秒)
	 * 
	 * @param s
	 * @return
	 */
	public Actor setDelayS(float s) {
		_curTimer.setDelayS(s);
		return this;
	}

	/**
	 * 返回动作触发延迟时间
	 * 
	 * @return
	 */
	public long getDelay() {
		return _curTimer.getDelay();
	}

	/**
	 * 返回动作触发延迟时间(秒)
	 * 
	 * @return
	 */
	public float getDelayS() {
		return _curTimer.getDelayS();
	}

	/**
	 * 动作处理
	 * 
	 * @param elapsedTime
	 */
	public void action(long elapsedTime) {

	}

	/**
	 * 决定当前对象旋转方向
	 * 
	 * @param _objectRotation
	 */
	@Override
	public void setRotation(float r) {
		if (r >= 360) {
			if (r < 720) {
				r -= 360;
			} else {
				r %= 360;
			}
		} else if (r < 0) {
			if (r >= -360) {
				r += 360;
			} else {
				r = 360 + r % 360;
			}
		}
		if (!MathUtils.equal(this._objectRotation, r)) {
			this._objectRotation = r;
			this._boundingRect = null;
			this.sizeChanged();
		}
	}

	protected float pixelWidth() {
		float width = 0f;
		if (_actorWidth > 0) {
			width = _actorWidth;
		} else if (image != null) {
			width = (image.getWidth());
		}
		if (width == 0) {
			width = getBoundingRect().getWidth();
		}
		return width;
	}

	@Override
	public float getWidth() {
		return pixelWidth() * scaleX;
	}

	@Override
	public void setWidth(float w) {
		setSize(w, this._actorHeight);
	}

	protected float pixelHeight() {
		float height = 0f;
		if (_actorHeight > 0) {
			height = _actorHeight;
		} else if (image != null) {
			height = (image.getHeight());
		}
		if (height == 0) {
			height = getBoundingRect().getHeight();
		}
		return height;
	}

	@Override
	public float getHeight() {
		return pixelHeight() * scaleY;
	}

	@Override
	public void setHeight(float h) {
		setSize(this._actorWidth, h);
	}

	/**
	 * 根据旋转方向移动坐标
	 * 
	 * @param distance
	 */
	public void move(float distance) {
		float angle = MathUtils.toRadians(getRotation());
		int x = MathUtils.round(getX() + MathUtils.cos(angle) * distance);
		int y = MathUtils.round(getY() + MathUtils.sin(angle) * distance);
		setLocation(x, y);
	}

	@Override
	public void move(Vector2f v) {
		move(v.x, v.y);
	}

	public void move(int x, int y) {
		setLocationDrag(_objectLocation.getX() + x, _objectLocation.getY() + y);
	}

	@Override
	public void move(float x, float y) {
		setLocationDrag(_objectLocation.getX() + x, _objectLocation.getY() + y);
	}

	public void setX(int x) {
		this.setLocationDrag(x, y());
	}

	public void setY(int y) {
		this.setLocationDrag(x(), y);
	}

	@Override
	public void setX(float x) {
		this.setLocation(x, getY());
	}

	@Override
	public void setY(float y) {
		this.setLocation(getX(), y);
	}

	@Override
	public void setX(Integer x) {
		setX(x.intValue());
	}

	@Override
	public void setY(Integer y) {
		setY(y.intValue());
	}

	public void setLocation(int x, int y) {
		this.setLocationDrag(x, y);
	}

	@Override
	public void setLocation(float x, float y) {
		setLocationDrag(x, y);
	}

	private void setLocationDrag(float x, float y) {
		this.failIfNotInLayer();
		float oldX = _objectLocation.getX();
		float oldY = _objectLocation.getY();
		if (this.gameLayer.isBounded()) {
			_objectLocation.x = this.limitValue(x, this.gameLayer.getWidth() - getWidth());
			_objectLocation.y = this.limitValue(y, this.gameLayer.getHeight() - getHeight());
		} else {
			_objectLocation.x = x;
			_objectLocation.y = y;
		}
		if (_objectLocation.x != oldX || _objectLocation.y != oldY) {
			if (this._boundingRect != null) {
				float dx = (_objectLocation.getX() - oldX) * this.gameLayer.cellSize;
				float dy = (_objectLocation.getY() - oldY) * this.gameLayer.cellSize;
				this._boundingRect.setX(this._boundingRect.getX() + dx);
				this._boundingRect.setY(this._boundingRect.getY() + dy);
				for (int i = 0; i < 4; ++i) {
					this._positionXs[i] += dx;
					this._positionYs[i] += dy;
				}
			}
			this.locationChanged(oldX, oldY);
		}
	}

	private float limitValue(float v, float limit) {
		if (v < 0) {
			v = 0;
		}
		if (limit < v) {
			v = limit;
		}
		return v;
	}

	protected void addLayer(ActorLayer gameLayer) {
	}

	public LTexture getImage() {
		return this.image;
	}

	public Actor setImage(String filename) {
		return this.setImage(LSystem.loadTexture(filename));
	}

	public Actor setImage(LTexture img) {
		if (img != null || this.image != null) {
			boolean sizeChanged = true;
			if (img != null && this.image != null && img.getWidth() == this.image.getWidth()
					&& img.getHeight() == this.image.getHeight()) {
				sizeChanged = false;
			}
			if (image != null && image.getParent() == null && image.isChildAllClose()) {
				if (image != null) {
					image.close();
					image = null;
				}
			}
			this.image = img;
			if (sizeChanged) {
				this._boundingRect = null;
				this.sizeChanged();
			}
		}
		return this;
	}

	public Actor setLocationInPixels(float x, float y) {
		float xCell = this.gameLayer.toCellFloor(x);
		float yCell = this.gameLayer.toCellFloor(y);
		if (xCell != _objectLocation.x || yCell != _objectLocation.y) {
			this.setLocationDrag(xCell, yCell);
		}
		return this;
	}

	void setLayer(ActorLayer gameLayer) {
		this.gameLayer = gameLayer;
	}

	void addLayer(float x, float y, ActorLayer gameLayer) {
		if (gameLayer.isBounded()) {
			x = this.limitValue(x, gameLayer.getWidth() - getWidth());
			y = this.limitValue(y, gameLayer.getHeight() - getHeight());
		}
		this._boundingRect = null;
		this.setLayer(gameLayer);
		this.setLocation(x, y);
	}

	@Override
	public RectBox getCollisionArea() {
		return getRectBox();
	}

	/**
	 * 获得当前Actor碰撞盒
	 * 
	 * @return
	 */
	public RectBox getRectBox() {
		return setRect(MathUtils.getBounds(getScalePixelX(), getScalePixelY(), getWidth(), getHeight(), _objectRotation,
				_objectRect));
	}

	public float getScalePixelX() {
		return ((scaleX == 1f) ? getX() : getCenterX());
	}

	public float getScalePixelY() {
		return ((scaleY == 1f) ? getY() : getCenterY());
	}

	/**
	 * 获得当前Actor碰撞盒
	 * 
	 * @return
	 */
	@Override
	public RectBox getBoundingRect() {
		if (this._boundingRect == null) {
			this.calcBounds();
		}
		return this._boundingRect;
	}

	/**
	 * 绘图接口，用以绘制额外的图形到Actor
	 * 
	 * @param g
	 */
	public void draw(GLEx g) {

	}

	/**
	 * 矫正当前图像大小
	 * 
	 */
	private void calcBounds() {
		ActorLayer layer = this.getLLayer();
		if (layer != null) {
			int width;
			int height;
			int cellSize = layer.getCellSize();
			int minY = 0;
			if (this.image == null) {
				width = _objectLocation.x() * cellSize + cellSize;
				height = _objectLocation.y() * cellSize + cellSize;
				this._boundingRect = new RectBox(width, height, 0, 0);
				for (minY = 0; minY < 4; ++minY) {
					this._positionXs[minY] = width;
					this._positionYs[minY] = height;
				}
			} else {
				this._boundingRect = getRectBox();
			}
		}
	}

	public RectBox getRandLocation() {
		if (gameLayer != null) {
			return gameLayer.getRandomLayerLocation(this);
		}
		return null;
	}

	public Actor sendToFront() {
		if (gameLayer != null) {
			gameLayer.sendToFront(this);
		}
		return this;
	}

	public Actor sendToBack() {
		if (gameLayer != null) {
			gameLayer.sendToBack(this);
		}
		return this;
	}

	public float[] toPixels() {
		float size = gameLayer.cellSize / 2;
		_currentPos[0] = _objectLocation.x * gameLayer.cellSize + size;
		_currentPos[1] = _objectLocation.y * gameLayer.cellSize + size;
		return _currentPos;
	}

	private void sizeChanged() {
		if (this.gameLayer != null) {
			this.gameLayer.updateObjectSize(this);
		}
		this._boundingRect = getRectBox();
	}

	private void locationChanged(float oldX, float oldY) {
		if (this.gameLayer != null) {
			this.gameLayer.updateObjectLocation(this, oldX, oldY);
		}
	}

	private void failIfNotInLayer() {
		if (this.gameLayer == null) {
			throw new LSysException("The actor has not been inserted into a Layer so it has no _objectLocation yet !");
		}
	}

	private static boolean checkOutside(float[] myX, float[] myY, float[] otherX, float[] otherY) {
		for (int v = 0; v < 4; ++v) {
			int v1 = v + 1 & 3;
			float edgeX = myX[v] - myX[v1];
			float edgeY = myY[v] - myY[v1];
			float reX = -edgeY;
			float reY = edgeX;
			if (reX != 0 || edgeX != 0) {
				for (int e = 0; e < 4; ++e) {
					float scalar = reX * (otherX[e] - myX[v1]) + reY * (otherY[e] - myY[v1]);
					if (scalar < 0) {
						continue;
					}
				}
				return true;
			}
		}
		return false;
	}

	public boolean isThereparent() {
		return gameLayer != null;
	}

	@Override
	public boolean intersects(CollisionObject obj) {
		if (obj instanceof Actor) {
			return intersects((Actor) obj);
		} else {
			RectBox thisBounds = this.getBoundingRect();
			RectBox otherBounds = obj.getBoundingRect();
			return thisBounds.intersects(otherBounds);
		}
	}

	@Override
	public boolean contains(CollisionObject obj) {
		if (obj instanceof Actor) {
			return contains((Actor) obj);
		} else {
			RectBox thisBounds = this.getBoundingRect();
			RectBox otherBounds = obj.getBoundingRect();
			return thisBounds.contains(otherBounds);
		}
	}

	@Override
	public boolean intersects(Shape s) {
		RectBox thisBounds = this.getBoundingRect();
		return thisBounds.intersects(s);
	}

	@Override
	public boolean contains(Shape s) {
		RectBox thisBounds = this.getBoundingRect();
		return thisBounds.contains(s);
	}

	@Override
	public boolean collided(Shape s) {
		RectBox thisBounds = this.getBoundingRect();
		return thisBounds.collided(s);
	}

	protected boolean checkActorContains(Actor other, boolean inc) {
		int thisBounds1;
		if (this.image == null) {
			if (other.image != null) {
				thisBounds1 = this.gameLayer.getCellSize();
				return other.containsPoint(_objectLocation.x() * thisBounds1 + thisBounds1 / 2,
						_objectLocation.y() * thisBounds1 + thisBounds1 / 2);
			} else {
				return _objectLocation.x == other._objectLocation.x && _objectLocation.y == other._objectLocation.y;
			}
		} else if (other.image == null) {
			thisBounds1 = this.gameLayer.getCellSize();
			return this.containsPoint(other._objectLocation.x() * thisBounds1 + thisBounds1 / 2,
					other._objectLocation.y() * thisBounds1 + thisBounds1 / 2);
		} else {
			RectBox thisBounds = this.getBoundingRect();
			RectBox otherBounds = other.getBoundingRect();
			if (inc) {
				if (this._objectRotation == 0 && other._objectRotation == 0) {
					return thisBounds.contains(otherBounds);
				} else if (!thisBounds.contains(otherBounds)) {
					return false;
				}
			} else {
				if (this._objectRotation == 0 && other._objectRotation == 0) {
					return thisBounds.intersects(otherBounds);
				} else if (!thisBounds.intersects(otherBounds)) {
					return false;
				}
			}
			final float[] myX = this._positionXs;
			final float[] myY = this._positionYs;
			final float[] otherX = other._positionXs;
			final float[] otherY = other._positionYs;
			return checkOutside(myX, myY, otherX, otherY) ? false : !checkOutside(otherX, otherY, myX, myY);
		}
	}

	public boolean contains(Actor other) {
		return checkActorContains(other, true);
	}

	public boolean intersects(Actor other) {
		return checkActorContains(other, false);
	}

	public TArray<Actor> getNeighbours(float distance, boolean diagonal, String flag) {
		this.failIfNotInLayer();
		return this.getLLayer().getNeighbours(this, distance, diagonal, flag);
	}

	public TArray<Actor> getCollisionObjects(float dx, float dy, String flag) {
		this.failIfNotInLayer();
		return this.gameLayer.getCollisionObjectsAt(_objectLocation.x + dx, _objectLocation.y + dy, flag);
	}

	public Actor getOnlyCollisionObject(float dx, float dy, String flag) {
		this.failIfNotInLayer();
		return this.gameLayer.getOnlyObjectAt(this, _objectLocation.x + dx, _objectLocation.y + dy, flag);
	}

	public TArray<Actor> getCollisionObjects(float radius, String flag) {
		this.failIfNotInLayer();
		TArray<Actor> inRange = this.gameLayer.getObjectsInRange(_objectLocation.x, _objectLocation.y, radius, flag);
		inRange.remove(this);
		return inRange;
	}

	public TArray<Actor> getCollisionObjects() {
		return getCollisionObjects(getObjectFlag());
	}

	public TArray<Actor> getCollisionObjects(String flag) {
		this.failIfNotInLayer();
		TArray<Actor> list = this.gameLayer.getIntersectingObjects(this, flag);
		list.remove(this);
		return list;
	}

	public Actor getOnlyCollisionObject() {
		return getOnlyCollisionObject(getObjectFlag());
	}

	public Actor getOnlyCollisionObject(String flag) {
		this.failIfNotInLayer();
		return this.gameLayer.getOnlyIntersectingObject(this, flag);
	}

	public Actor getOnlyCollisionObjectAt(float x, float y) {
		this.failIfNotInLayer();
		return this.gameLayer.getOnlyCollisionObjectsAt(x, y);
	}

	public Actor getOnlyCollisionObjectAt(float x, float y, Object tag) {
		this.failIfNotInLayer();
		return this.gameLayer.getOnlyCollisionObjectsAt(x, y, tag);
	}

	@Override
	public boolean containsPoint(float px, float py) {
		this.failIfNotInLayer();
		if (this.image == null) {
			return false;
		} else {
			if (this._boundingRect == null) {
				this.calcBounds();
			}
			if (this._objectRotation != 0 && this._objectRotation != 90 && this._objectRotation != 270) {
				for (int v = 0; v < 4; ++v) {
					int v1 = v + 1 & 3;
					float edgeX = this._positionXs[v] - this._positionXs[v1];
					float edgeY = this._positionYs[v] - this._positionYs[v1];
					float reX = -edgeY;
					if (reX != 0 || edgeX != 0) {
						float scalar = reX * (px - this._positionXs[v1]) + edgeX * (py - this._positionYs[v1]);
						if (scalar >= 0) {
							return false;
						}
					}
				}

				return true;
			} else {
				return px >= this._boundingRect.getX() && px < this._boundingRect.getRight()
						&& py >= this._boundingRect.getY() && py < this._boundingRect.getBottom();
			}
		}
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean v) {
		this.visible = v;
	}

	public boolean isDrag() {
		return draged;
	}

	public Actor setDrag(boolean d) {
		this.draged = d;
		return this;
	}

	public boolean isClick() {
		return clicked;
	}

	final void setLastPaintSeqNum(int num) {
		this.lastPaintSequenceNumber = num;
	}

	final public int getSequenceNumber() {
		return this.noSequenceNumber;
	}

	final public int getLastPaintSeqNum() {
		return this.lastPaintSequenceNumber;
	}

	public Animation getAnimation() {
		return animation;
	}

	public Actor setAnimation(Animation animation) {
		if (animation == null) {
			throw new LSysException("Animation is null !");
		}
		this.animation = animation;
		this.isAnimation = true;
		this.setImage(animation.getSpriteImage());
		return this;
	}

	public boolean isAnimation() {
		return isAnimation;
	}

	public Actor setAnimation(boolean isAnimation) {
		this.isAnimation = isAnimation;
		return this;
	}

	public ActorLayer buildToLayer() {
		ActorLayer layer = getLLayer();
		if (layer != null) {
			layer.addObject(this);
		}
		return layer;
	}

	public ActorLayer removeFromLayer() {
		ActorLayer layer = getLLayer();
		if (layer != null) {
			layer.removeObject(this);
		}
		return layer;
	}

	public ActorLayer getLLayer() {
		if (this.gameLayer == null && LSystem.getProcess() != null) {
			Screen screen = LSystem.getProcess().getScreen();
			if (screen != null) {
				Desktop ui = screen.getDesktop();
				if (ui != null) {
					LLayer layer = (LLayer) ui.findComponentUI("Layer");
					if (layer != null) {
						return layer;
					}
				}
			}
		}
		return this.gameLayer;
	}

	@Override
	public Field2D getField2D() {
		return gameLayer.getField2D();
	}

	@Override
	public boolean isBounded() {
		return gameLayer.isBounded();
	}

	@Override
	public boolean isContainer() {
		return gameLayer.isContainer();
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return gameLayer.contains(x, y, w, h);
	}

	@Override
	public float getContainerWidth() {
		return gameLayer.getWidth();
	}

	@Override
	public float getContainerHeight() {
		return gameLayer.getHeight();
	}

	public boolean isMirror() {
		return isFlipX();
	}

	public Actor setMirror(boolean m) {
		this.setFlipX(m);
		return this;
	}

	@Override
	public int compareTo(Actor o) {
		return o.getLayer() - this.getLayer();
	}

	@Override
	public void setColor(LColor c) {
		filterColor = c;
	}

	@Override
	public LColor getColor() {
		return new LColor(filterColor);
	}

	@Override
	public Actor setFlipX(boolean x) {
		this.flipX = x;
		return this;
	}

	@Override
	public Actor setFlipY(boolean y) {
		this.flipY = y;
		return this;
	}

	@Override
	public Actor setFlipXY(boolean x, boolean y) {
		setFlipX(x);
		setFlipY(y);
		return this;
	}

	@Override
	public boolean isFlipX() {
		return flipX;
	}

	@Override
	public boolean isFlipY() {
		return flipY;
	}

	@Override
	public float getCenterX() {
		return getX() + getWidth() / 2f;
	}

	@Override
	public float getCenterY() {
		return getY() + getHeight() / 2f;
	}

	@Override
	public ActionTween selfAction() {
		return PlayerUtils.set(this);
	}

	public boolean hasActions() {
		return ActionControl.get().containsKey(this);
	}

	public Actor clearActions() {
		ActionControl.get().removeAllActions(this);
		return this;
	}

	@Override
	public boolean isActionCompleted() {
		return PlayerUtils.isActionCompleted(this);
	}

	@Override
	public void _onDestroy() {
		if (image != null) {
			image.close();
		}
		if (animation != null) {
			animation.close();
		}
		removeActionEvents(this);
	}

}
