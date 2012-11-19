package org.loon.framework.android.game.core.graphics.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.loon.framework.android.game.action.BallTo;
import org.loon.framework.android.game.action.CircleTo;
import org.loon.framework.android.game.action.FadeTo;
import org.loon.framework.android.game.action.FireTo;
import org.loon.framework.android.game.action.JumpTo;
import org.loon.framework.android.game.action.MoveTo;
import org.loon.framework.android.game.action.RotateTo;
import org.loon.framework.android.game.action.ScaleTo;
import org.loon.framework.android.game.action.map.Field2D;
import org.loon.framework.android.game.action.sprite.Animation;
import org.loon.framework.android.game.action.sprite.SpriteRotateSheet;
import org.loon.framework.android.game.core.LRelease;
import org.loon.framework.android.game.core.LSystem;
import org.loon.framework.android.game.core.geom.RectBox;
import org.loon.framework.android.game.core.geom.Vector2D;
import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.core.graphics.filter.ImageFilterType;
import org.loon.framework.android.game.core.timer.LTimer;
import org.loon.framework.android.game.utils.GraphicsUtils;
import org.loon.framework.android.game.utils.NumberUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class Actor implements ImageFilterType, LRelease {

	private static int sequenceNumber = 0;

	protected int x;

	protected int y;

	protected float alpha = 1.0F;

	private int noSequenceNumber;

	private int lastPaintSequenceNumber;

	private int rotation;

	private boolean visible = true, drag = true, click = true;

	private ActorLayer gameLayer;

	private LImage image;

	private Object data, tag;

	private RectBox boundingRect, rect;

	private int[] xs = new int[4];

	private int[] ys = new int[4];

	private LTimer timer = new LTimer(0);

	private Animation animation;

	private boolean isAnimation;

	int filterType = ImageFilterType.NoneFilter;

	boolean supportRotateSheet;

	SpriteRotateSheet sheet;

	ActorListener actorListener;

	private int maxRotateCache = 60;

	private static HashMap<Integer, SpriteRotateSheet> lazySheets = new HashMap<Integer, SpriteRotateSheet>();

	float scaleX = 1, scaleY = 1;

	public Actor(Animation animation) {
		this(animation, 0, 0);
	}

	public Actor(Animation animation, int x, int y) {
		if (animation == null) {
			throw new RuntimeException("Animation is null !");
		}
		this.noSequenceNumber = sequenceNumber++;
		this.animation = animation;
		this.isAnimation = true;
		this.x = x;
		this.y = y;
		this.setImage(animation.getSpriteImage().getImage());
	}

	public Actor() {
		this((LImage) null);
	}

	public Actor(LImage image, int x, int y) {
		this.noSequenceNumber = sequenceNumber++;
		this.x = x;
		this.y = y;
		this.setImage(image);
	}

	public Actor(LImage image) {
		this(image, 0, 0);
	}

	public Actor(String fileName, int x, int y) {
		this(GraphicsUtils.loadImage(fileName), x, y);
	}

	public Actor(String fileName) {
		this(fileName, 0, 0);
	}

	public void stopAnimation() {
		this.isAnimation = false;
	}

	public void startAnimation() {
		this.isAnimation = true;
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
		return rotateTo(rotate, 2.0F);
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
		return jumpTo(jump, 0.3F);
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
	public FireTo fireTo(int endX, int endY, double speed) {
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
	public FireTo fireTo(int endX, int endY) {
		return fireTo(endX, endY, 10);
	}

	/**
	 * 将当前角色向指定坐标发射
	 * 
	 * @param endX
	 * @param endY
	 * @return
	 */
	public BallTo ballTo(int r, int vx, int vy, double g) {
		failIfNotInLayer();
		return gameLayer.callBallTo(this, r, vx, vy, g);
	}

	/**
	 * 将当前角色向指定坐标发射
	 * 
	 * @param vx
	 * @param vy
	 * @param g
	 * @return
	 */
	public BallTo ballTo(int vx, int vy, double g) {
		return ballTo(0, vx, vy, g);
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
	 * 删除所有以当前Actor注册的动作事件
	 * 
	 */
	public void removeActionEvents() {
		failIfNotInLayer();
		gameLayer.removeActionEvents(this);
	}

	/**
	 * 缩放当前角色
	 * 
	 * @param scale
	 */
	public void setScale(final float s) {
		this.setScale(s, s);
	}

	public void setScale(final float sx, final float sy) {
		if (this.scaleX == sx && this.scaleY == sy) {
			return;
		}
		this.scaleX = sx;
		this.scaleY = sy;
	}

	public float getScaleX() {
		return this.scaleX;
	}

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
	void update(long elapsedTime) {
		if (timer.action(elapsedTime)) {
			if (isAnimation) {
				animation.update(elapsedTime);
				setImage(animation.getSpriteImage().getImage());
			}
			action(elapsedTime);
		}
	}

	/**
	 * 设定动作触发延迟时间
	 * 
	 * @param delay
	 */
	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	/**
	 * 返回动作触发延迟时间
	 * 
	 * @return
	 */
	public long getDelay() {
		return timer.getDelay();
	}

	/**
	 * 动作处理
	 * 
	 * @param elapsedTime
	 */
	public void action(long elapsedTime) {

	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getRotation() {
		return this.rotation;
	}

	/**
	 * 释放所有的Actor旋转缓存
	 */
	public static void resetRotateAllCache() {
		if (lazySheets != null) {
			synchronized (lazySheets) {
				Set<Integer> set = lazySheets.keySet();
				for (Iterator<Integer> it = set.iterator(); it.hasNext();) {
					Integer key = it.next();
					SpriteRotateSheet sheet = lazySheets.remove(key);
					if (sheet != null) {
						sheet.dispose();
						sheet = null;
					}
				}
			}
		}
	}

	/**
	 * 释放指定Actor中的旋转缓存
	 * 
	 * @param actor
	 */
	public static void resetRotateCache(Actor actor) {
		SpriteRotateSheet sheet = lazySheets.remove(actor);
		if (sheet != null) {
			synchronized (sheet) {
				actor.supportRotateSheet = false;
				sheet.dispose();
				sheet = null;
			}
		}
	}

	/**
	 * 缓存图像旋转结果
	 * 
	 * @param img
	 */
	private void setRotateSheet(final Bitmap img) {
		if (lazySheets.size() > LSystem.DEFAULT_MAX_CACHE_SIZE) {
			return;
		}
		if (!LSystem.DEFAULT_ROTATE_CACHE
				|| (img.getConfig() != Config.ARGB_8888)
				|| (img.getWidth() < 48 && img.getHeight() < 48)) {
			return;
		}
		synchronized (lazySheets) {
			if (SpriteRotateSheet.suited2(img.getWidth(), img.getHeight())) {
				final Integer index = GraphicsUtils.hashBitmap(img);
				sheet = lazySheets.get(index);
				if (sheet == null) {
					Thread thread = new Thread() {
						public void run() {
							sheet = new SpriteRotateSheet(img, maxRotateCache,
									true);
							lazySheets.put(index, sheet);
							supportRotateSheet = true;
						}
					};
					thread.setPriority(Thread.MIN_PRIORITY);
					thread.start();
				} else {
					supportRotateSheet = true;
				}
			} else {
				supportRotateSheet = false;
			}
		}
	}

	/**
	 * 决定当前对象旋转方向
	 * 
	 * @param rotation
	 */
	public void setRotation(int rotation) {
		if (rotation >= 360) {
			if (rotation < 720) {
				rotation -= 360;
			} else {
				rotation %= 360;
			}
		} else if (rotation < 0) {
			if (rotation >= -360) {
				rotation += 360;
			} else {
				rotation = 360 + rotation % 360;
			}
		}
		if (this.rotation != rotation) {
			this.rotation = rotation;
			this.boundingRect = null;
			this.sizeChanged();
		}
	}

	/**
	 * 根据旋转方向移动坐标
	 * 
	 * @param distance
	 */
	public void move(double distance) {
		double angle = Math.toRadians(getRotation());
		int x = (int) Math.round(getX() + Math.cos(angle) * distance);
		int y = (int) Math.round(getY() + Math.sin(angle) * distance);
		setLocation(x, y);
	}

	public void setLocation(int x, int y) {
		this.setLocationDrag(x, y);
	}

	public void setX(int x) {
		this.setLocationDrag(x, getY());
	}

	public void setY(int y) {
		this.setLocationDrag(getX(), y);
	}

	public int getWidth() {
		if (image != null) {
			return image.getWidth();
		}
		return getRectBox().getWidth();
	}

	public int getHeight() {
		if (image != null) {
			return image.getHeight();
		}
		return getRectBox().getHeight();
	}

	public void move_45D_up() {
		move_45D_up(1);
	}

	public void move_45D_up(int multiples) {
		move_multiples(Field2D.UP, multiples);
	}

	public void move_45D_left() {
		move_45D_left(1);
	}

	public void move_45D_left(int multiples) {
		move_multiples(Field2D.LEFT, multiples);
	}

	public void move_45D_right() {
		move_45D_right(1);
	}

	public void move_45D_right(int multiples) {
		move_multiples(Field2D.RIGHT, multiples);
	}

	public void move_45D_down() {
		move_45D_down(1);
	}

	public void move_45D_down(int multiples) {
		move_multiples(Field2D.DOWN, multiples);
	}

	public void move_up() {
		move_up(1);
	}

	public void move_up(int multiples) {
		move_multiples(Field2D.TUP, multiples);
	}

	public void move_left() {
		move_left(1);
	}

	public void move_left(int multiples) {
		move_multiples(Field2D.TLEFT, multiples);
	}

	public void move_right() {
		move_right(1);
	}

	public void move_right(int multiples) {
		move_multiples(Field2D.TRIGHT, multiples);
	}

	public void move_down() {
		move_down(1);
	}

	public void move_down(int multiples) {
		move_multiples(Field2D.TDOWN, multiples);
	}

	public void move_multiples(int direction, int multiples) {
		if (multiples <= 0) {
			multiples = 1;
		}
		Vector2D v = Field2D.getDirection(direction);
		move(v.x() * multiples, v.y() * multiples);
	}

	public void move(int x, int y) {
		setLocationDrag(this.x + x, this.y + y);
	}

	private void setLocationDrag(int x, int y) {
		this.failIfNotInLayer();
		int oldX = this.x;
		int oldY = this.y;
		if (this.gameLayer.isBounded()) {
			this.x = this.limitValue(x, this.gameLayer.getWidth() - getWidth());
			this.y = this.limitValue(y, this.gameLayer.getHeight()
					- getHeight());
		} else {
			this.x = x;
			this.y = y;
		}
		if (this.x != oldX || this.y != oldY) {
			if (this.boundingRect != null) {
				int dx = (this.x - oldX) * this.gameLayer.cellSize;
				int dy = (this.y - oldY) * this.gameLayer.cellSize;
				this.boundingRect.setX(this.boundingRect.getX() + dx);
				this.boundingRect.setY(this.boundingRect.getY() + dy);
				for (int i = 0; i < 4; ++i) {
					this.xs[i] += dx;
					this.ys[i] += dy;
				}
			}
			this.locationChanged(oldX, oldY);
		}
	}

	private int limitValue(int v, int limit) {
		if (v < 0) {
			v = 0;
		}
		if (limit < v) {
			v = limit;
		}
		return v;
	}

	public ActorLayer getLLayer() {
		return this.gameLayer;
	}

	protected void addLayer(ActorLayer gameLayer) {
	}

	public LImage getImage() {
		return this.image;
	}

	public void setImage(String filename) throws IllegalArgumentException {
		this.setImage(LImage.createImage(filename));
	}

	public void setImage(LImage image) {
		if (image != null || this.image != null) {
			boolean sizeChanged = true;
			if (image != null && this.image != null
					&& image.getWidth() == this.image.getWidth()
					&& image.getHeight() == this.image.getHeight()) {
				sizeChanged = false;
			}
			setRotateSheet(image.getBitmap());
			this.image = image;
			if (sizeChanged) {
				this.boundingRect = null;
				this.sizeChanged();
			}
		}
	}

	public void setLocationInPixels(int x, int y) {
		int xCell = this.gameLayer.toCellFloor(x);
		int yCell = this.gameLayer.toCellFloor(y);
		if (xCell != this.x || yCell != this.y) {
			this.setLocationDrag(xCell, yCell);
		}
	}

	void setLayer(ActorLayer gameLayer) {
		this.gameLayer = gameLayer;
	}

	void addLayer(int x, int y, ActorLayer gameLayer) {
		if (gameLayer.isBounded()) {
			x = this.limitValue(x, gameLayer.getWidth() - getWidth());
			y = this.limitValue(y, gameLayer.getHeight() - getHeight());
		}
		this.boundingRect = null;
		this.setLayer(gameLayer);
		this.setLocation(x, y);
	}

	/**
	 * 获得当前Actor碰撞盒
	 * 
	 * @return
	 */
	public RectBox getRectBox() {
		RectBox tmp = getBoundingRect();
		if (rect == null) {
			rect = new RectBox(x, y, (int) (tmp.width * scaleX),
					(int) (tmp.height * scaleY));
		} else {
			rect.setBounds(x, y, (int) (tmp.width * scaleX),
					(int) (tmp.height * scaleY));
		}
		return rect;
	}

	/**
	 * 获得当前Actor碰撞盒(内部使用)
	 * 
	 * @return
	 */
	RectBox getBoundingRect() {
		if (this.boundingRect == null) {
			this.calcBounds();
		}
		return this.boundingRect;
	}

	/**
	 * 绘图接口，用以绘制额外的图形到Actor
	 * 
	 * @param g
	 */
	public void draw(LGraphics g) {

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
				width = this.x * cellSize + cellSize;
				height = this.y * cellSize + cellSize;
				this.boundingRect = new RectBox(width, height, 0, 0);
				for (minY = 0; minY < 4; ++minY) {
					this.xs[minY] = width;
					this.ys[minY] = height;
				}
			} else {
				this.boundingRect = NumberUtils.getBounds(x, y, this.image.getWidth(), this.image
								.getHeight(), rotation, boundingRect);
			}
		}
	}

	public RectBox getRandLocation() {
		if (gameLayer != null) {
			return gameLayer.getRandomLayerLocation(this);
		}
		return null;
	}

	public void setTag(Object o) {
		this.tag = o;
	}

	public Object getTag() {
		return this.tag;
	}

	void setData(Object o) {
		this.data = o;
	}

	Object getData() {
		return this.data;
	}

	public void sendToFront() {
		if (gameLayer != null) {
			gameLayer.sendToFront(this);
		}
	}

	public void sendToBack() {
		if (gameLayer != null) {
			gameLayer.sendToBack(this);
		}
	}

	public int toPixel(int x) {
		ActorLayer aLayer = this.getLLayer();
		if (aLayer == null) {
			return 0;
		} else {
			return x * aLayer.getCellSize() + aLayer.getCellSize() / 2;
		}
	}

	private void sizeChanged() {
		if (this.gameLayer != null) {
			this.gameLayer.updateObjectSize(this);
		}
	}

	private void locationChanged(int oldX, int oldY) {
		if (this.gameLayer != null) {
			this.gameLayer.updateObjectLocation(this, oldX, oldY);
		}
	}

	private void failIfNotInLayer() {
		if (this.gameLayer == null) {
			throw new IllegalStateException(
					"The actor has not been inserted into a Layer so it has no location yet !");
		}
	}

	private static boolean checkOutside(int[] myX, int[] myY, int[] otherX,
			int[] otherY) {
		for (int v = 0; v < 4; ++v) {
			int v1 = v + 1 & 3;
			int edgeX = myX[v] - myX[v1];
			int edgeY = myY[v] - myY[v1];
			int reX = -edgeY;
			int reY = edgeX;
			if (reX != 0 || edgeX != 0) {
				for (int e = 0; e < 4; ++e) {
					int scalar = reX * (otherX[e] - myX[v1]) + reY
							* (otherY[e] - myY[v1]);
					if (scalar < 0) {
						continue;
					}
				}
				return true;
			}
		}
		return false;
	}

	public boolean intersects(Actor other) {
		int thisBounds1;
		if (this.image == null) {
			if (other.image != null) {
				thisBounds1 = this.gameLayer.getCellSize();
				return other.containsPoint(this.x * thisBounds1 + thisBounds1
						/ 2, this.y * thisBounds1 + thisBounds1 / 2);
			} else {
				return this.x == other.x && this.y == other.y;
			}
		} else if (other.image == null) {
			thisBounds1 = this.gameLayer.getCellSize();
			return this.containsPoint(other.x * thisBounds1 + thisBounds1 / 2,
					other.y * thisBounds1 + thisBounds1 / 2);
		} else {
			RectBox thisBounds = this.getBoundingRect();
			RectBox otherBounds = other.getBoundingRect();
			if (this.rotation == 0 && other.rotation == 0) {
				return thisBounds.intersects(otherBounds);
			} else if (!thisBounds.intersects(otherBounds)) {
				return false;
			} else {
				int[] myX = this.xs;
				int[] myY = this.ys;
				int[] otherX = other.xs;
				int[] otherY = other.ys;
				return checkOutside(myX, myY, otherX, otherY) ? false
						: !checkOutside(otherX, otherY, myX, myY);
			}
		}
	}

	public List<?> getNeighbours(int distance, boolean diagonal,
			Class<? extends Actor> cls) {
		this.failIfNotInLayer();
		return this.getLLayer().getNeighbours(this, distance, diagonal, cls);
	}

	public List<?> getCollisionObjects(int dx, int dy,
			Class<? extends Actor> cls) {
		this.failIfNotInLayer();
		return this.gameLayer.getCollisionObjectsAt(this.x + dx, this.y + dy,
				cls);
	}

	public Actor getOnlyCollisionObject(int dx, int dy,
			Class<? extends Actor> cls) {
		this.failIfNotInLayer();
		return this.gameLayer.getOnlyObjectAt(this, this.x + dx, this.y + dy,
				cls);
	}

	public List<?> getCollisionObjects(int radius, Class<? extends Actor> cls) {
		this.failIfNotInLayer();
		List<?> inRange = this.gameLayer.getObjectsInRange(this.x, this.y,
				radius, cls);
		inRange.remove(this);
		return inRange;
	}

	public List<?> getCollisionObjects() {
		return getCollisionObjects(this.getClass());
	}

	public List<?> getCollisionObjects(Class<? extends Actor> cls) {
		this.failIfNotInLayer();
		List<?> list = this.gameLayer.getIntersectingObjects(this, cls);
		list.remove(this);
		return list;
	}

	public Actor getOnlyCollisionObject() {
		return getOnlyCollisionObject(Actor.class);
	}

	public Actor getOnlyCollisionObject(Class<? extends Actor> cls) {
		this.failIfNotInLayer();
		return this.gameLayer.getOnlyIntersectingObject(this, cls);
	}

	public Actor getOnlyCollisionObjectAt(int x, int y) {
		this.failIfNotInLayer();
		return this.gameLayer.getOnlyCollisionObjectsAt(x, y);
	}

	public Actor getOnlyCollisionObjectAt(int x, int y, Object tag) {
		this.failIfNotInLayer();
		return this.gameLayer.getOnlyCollisionObjectsAt(x, y, tag);
	}

	public boolean containsPoint(int px, int py) {
		this.failIfNotInLayer();
		if (this.image == null) {
			return false;
		} else {
			if (this.boundingRect == null) {
				this.calcBounds();
			}
			if (this.rotation != 0 && this.rotation != 90
					&& this.rotation != 270) {
				for (int v = 0; v < 4; ++v) {
					int v1 = v + 1 & 3;
					int edgeX = this.xs[v] - this.xs[v1];
					int edgeY = this.ys[v] - this.ys[v1];
					int reX = -edgeY;
					if (reX != 0 || edgeX != 0) {
						int scalar = reX * (px - this.xs[v1]) + edgeX
								* (py - this.ys[v1]);
						if (scalar >= 0) {
							return false;
						}
					}
				}

				return true;
			} else {
				return px >= this.boundingRect.getX()
						&& px < this.boundingRect.getRight()
						&& py >= this.boundingRect.getY()
						&& py < this.boundingRect.getTop();
			}
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isDrag() {
		return drag;
	}

	public void setDrag(boolean drag) {
		this.drag = drag;
	}

	public boolean isClick() {
		return click;
	}

	public void setClick(boolean click) {
		this.click = click;
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

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		if (animation == null) {
			throw new RuntimeException("Animation is null !");
		}
		this.animation = animation;
		this.isAnimation = true;
		this.setImage(animation.getSpriteImage().getImage());
	}

	public boolean isAnimation() {
		return isAnimation;
	}

	public void setAnimation(boolean isAnimation) {
		this.isAnimation = isAnimation;
	}

	public int getFilterType() {
		return filterType;
	}

	public void setFilterType(int filterType) {
		this.filterType = filterType;
	}

	public boolean isSupportRotateSheet() {
		return supportRotateSheet;
	}

	public void setSupportRotateSheet(boolean supportRotateSheet) {
		this.supportRotateSheet = supportRotateSheet;
	}

	public int getMaxRotateCache() {
		return maxRotateCache;
	}

	public void setMaxRotateCache(int maxRotateCache) {
		this.maxRotateCache = maxRotateCache;
	}

	public SpriteRotateSheet getSheet() {
		return sheet;
	}

	public void setSheet(SpriteRotateSheet sheet) {
		this.sheet = sheet;
	}

	public void dispose() {
		if (lazySheets != null) {
			lazySheets.clear();
		}
		if (sheet != null) {
			sheet.dispose();
			sheet = null;
		}
		if (image != null) {
			image.dispose();
			image = null;
		}
		if (animation != null) {
			animation.dispose();
			animation = null;
		}
	}

}
