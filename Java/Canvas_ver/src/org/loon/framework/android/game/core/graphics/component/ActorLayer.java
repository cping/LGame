package org.loon.framework.android.game.core.graphics.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.loon.framework.android.game.action.ActionControl;
import org.loon.framework.android.game.action.ActionEvent;
import org.loon.framework.android.game.action.BallTo;
import org.loon.framework.android.game.action.CircleTo;
import org.loon.framework.android.game.action.FadeTo;
import org.loon.framework.android.game.action.FireTo;
import org.loon.framework.android.game.action.JumpTo;
import org.loon.framework.android.game.action.MoveTo;
import org.loon.framework.android.game.action.RotateTo;
import org.loon.framework.android.game.action.ScaleTo;
import org.loon.framework.android.game.action.map.Field2D;
import org.loon.framework.android.game.action.sprite.ISprite;
import org.loon.framework.android.game.core.LInput;
import org.loon.framework.android.game.core.LSystem;
import org.loon.framework.android.game.core.geom.RectBox;
import org.loon.framework.android.game.core.graphics.LContainer;

/**
 * 
 * Copyright 2008 - 2011
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
 * @version 0.1.1
 */

public abstract class ActorLayer extends LContainer {

	private final static int min_size = 48;

	private Field2D tmpField;

	private CollisionChecker collisionChecker;

	private boolean isBounded;

	protected int cellSize;

	ActorTreeSet objects;

	long elapsedTime;

	public ActorLayer(int x, int y, int layerWidth, int layerHeight,
			int cellSize) {
		this(x, y, layerWidth, layerHeight, cellSize, true);
	}

	public ActorLayer(int x, int y, int layerWidth, int layerHeight,
			int cellSize, boolean bounded) {
		super(x, y, layerWidth, layerHeight);
		this.collisionChecker = new CollisionManager();
		this.objects = new ActorTreeSet();
		this.cellSize = cellSize;
		this.initialize(layerWidth, layerHeight, cellSize);
		this.isBounded = bounded;
	}

	private void initialize(int width, int height, int cellSize) {
		this.cellSize = cellSize;
		this.collisionChecker.initialize(cellSize);
	}

	public LInput screenInput() {
		return input;
	}

	public int getCellSize() {
		return this.cellSize;
	}

	/**
	 * 添加一个独立事件，并选择是否暂不启动
	 * 
	 * @param action
	 * @param obj
	 * @param paused
	 */
	public void addActionEvent(ActionEvent action, Actor obj, boolean paused) {
		ActionControl.getInstance().addAction(action, obj, paused);
	}

	/**
	 * 添加一个独立事件
	 * 
	 * @param action
	 * @param obj
	 */
	public void addActionEvent(ActionEvent action, Actor obj) {
		ActionControl.getInstance().addAction(action, obj);
	}

	/**
	 * 删除所有和指定对象有关的独立事件
	 * 
	 * @param actObject
	 */
	public void removeActionEvents(Actor actObject) {
		ActionControl.getInstance().removeAllActions(actObject);
	}

	/**
	 * 获得当前独立事件总数
	 * 
	 * @return
	 */
	public int getActionEventCount() {
		return ActionControl.getInstance().getCount();
	}

	/**
	 * 删除指定的独立事件
	 * 
	 * @param tag
	 * @param actObject
	 */
	public void removeActionEvent(Object tag, Actor actObject) {
		ActionControl.getInstance().removeAction(tag, actObject);
	}

	/**
	 * 删除指定的独立事件
	 * 
	 * @param action
	 */
	public void removeActionEvent(ActionEvent action) {
		ActionControl.getInstance().removeAction(action);
	}

	/**
	 * 获得制定的独立事件
	 * 
	 * @param tag
	 * @param actObject
	 * @return
	 */
	public ActionEvent getActionEvent(Object tag, Actor actObject) {
		return ActionControl.getInstance().getAction(tag, actObject);
	}

	/**
	 * 停止对象对应的自动事件
	 * 
	 * @param actObject
	 */
	public void stopActionEvent(Actor actObject) {
		ActionControl.getInstance().stop(actObject);
	}

	/**
	 * 设定指定角色暂停状态
	 * 
	 * @param pause
	 * @param actObject
	 */
	public void pauseActionEvent(boolean pause, Actor actObject) {
		ActionControl.getInstance().paused(pause, actObject);
	}

	/**
	 * 设置是否暂停自动事件运行
	 * 
	 * @param pause
	 */
	public void pauseActionEvent(boolean pause) {
		ActionControl.getInstance().setPause(pause);
	}

	/**
	 * 获得是否暂停了独立事件运行
	 * 
	 * @return
	 */
	public boolean isPauseActionEvent() {
		return ActionControl.getInstance().isPause();
	}

	/**
	 * 启动指定对象对应的对立事件
	 * 
	 * @param actObject
	 */
	public void startActionEvent(Actor actObject) {
		ActionControl.getInstance().start(actObject);
	}

	/**
	 * 设定独立事件运行时FPS
	 * 
	 * @param fps
	 */
	public void setActionEventFPS(int fps) {
		ActionControl.getInstance().setFPS(fps);
	}

	/**
	 * 获得独立事件运行时FPS
	 * 
	 * @return
	 */
	public int getActionEventFPS() {
		return ActionControl.getInstance().getFPS();
	}

	/**
	 * 停止独立事件运行用线程
	 * 
	 */
	public void stopActionEvent() {
		ActionControl.getInstance().stop();
	}

	/**
	 * 让指定对象执行MoveTo事件
	 * 
	 * @param field
	 * @param o
	 * @param flag
	 * @param x
	 * @param y
	 * @return
	 */
	public MoveTo callMoveTo(Field2D field, Actor o, boolean flag, int x, int y) {
		MoveTo move = new MoveTo(field, x, y, flag);
		addActionEvent(move, o);
		return move;
	}

	/**
	 * 让指定对象执行MoveTo事件
	 * 
	 * @param field
	 * @param o
	 * @param x
	 * @param y
	 * @return
	 */
	public MoveTo callMoveTo(Field2D field, Actor o, int x, int y) {
		return callMoveTo(field, o, true, x, y);
	}

	/**
	 * 让指定对象执行MoveTo事件
	 * 
	 * @param o
	 * @param flag
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public MoveTo callMoveTo(Actor o, boolean flag, int x, int y, int w, int h) {
		if (tmpField == null) {
			tmpField = createArrayMap(w, h);
		}
		MoveTo move = new MoveTo(tmpField, x, y, flag);
		addActionEvent(move, o);
		return move;
	}

	/**
	 * 让指定对象执行MoveTo事件
	 * 
	 * @param o
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public MoveTo callMoveTo(Actor o, int x, int y, int w, int h) {
		return callMoveTo(o, true, x, y, w, h);
	}

	/**
	 * 让指定对象执行MoveTo事件
	 * 
	 * @param o
	 * @param x
	 * @param y
	 * @return
	 */
	public MoveTo callMoveTo(Actor o, int x, int y) {
		return callMoveTo(o, x, y, 32, 32);
	}

	/**
	 * 让指定对象执行MoveTo事件
	 * 
	 * @param o
	 * @param x
	 * @param y
	 * @param flag
	 * @return
	 */
	public MoveTo callMoveTo(Actor o, int x, int y, boolean flag) {
		return callMoveTo(o, flag, x, y, 32, 32);
	}

	/**
	 * 让指定对象执行FadeTo事件
	 * 
	 * @param o
	 * @param type
	 * @param speed
	 * @return
	 */
	public FadeTo callFadeTo(Actor o, int type, int speed) {
		FadeTo fade = new FadeTo(type, speed);
		addActionEvent(fade, o);
		return fade;
	}

	/**
	 * 让指定对象执行FadeTo淡入事件
	 * 
	 * @param o
	 * @param speed
	 * @return
	 */
	public FadeTo callFadeInTo(Actor o, int speed) {
		return callFadeTo(o, ISprite.TYPE_FADE_IN, speed);
	}

	/**
	 * 让指定对象执行FadeTo淡出事件
	 * 
	 * @param o
	 * @param speed
	 * @return
	 */
	public FadeTo callFadeOutTo(Actor o, int speed) {
		return callFadeTo(o, ISprite.TYPE_FADE_OUT, speed);
	}

	/**
	 * 让指定对象执行RotateTo旋转事件
	 * 
	 * @param o
	 * @param angle
	 * @param speed
	 * @return
	 */
	public RotateTo callRotateTo(Actor o, float angle, float speed) {
		RotateTo rotate = new RotateTo(angle, speed);
		addActionEvent(rotate, o);
		return rotate;
	}

	/**
	 * 让指定对象执行JumpTo跳跃事件
	 * 
	 * @param o
	 * @param j
	 * @param g
	 * @return
	 */
	public JumpTo callJumpTo(Actor o, int j, float g) {
		JumpTo jump = new JumpTo(j, g);
		addActionEvent(jump, o);
		return jump;
	}

	/**
	 * 让指定角色根据指定半径以指定速度循环转动
	 * 
	 * @param o
	 * @param radius
	 * @param velocity
	 * @return
	 */
	public CircleTo callCircleTo(Actor o, int radius, int velocity) {
		CircleTo circle = new CircleTo(radius, velocity);
		addActionEvent(circle, o);
		return circle;
	}

	/**
	 * 向指定坐标以指定速度让指定角色做为子弹发射
	 * 
	 * @param o
	 * @param x
	 * @param y
	 * @param speed
	 * @return
	 */
	public FireTo callFireTo(Actor o, int x, int y, double speed) {
		FireTo fire = new FireTo(x, y, speed);
		addActionEvent(fire, o);
		return fire;
	}

	/**
	 * 向指定方向以指定重力条件发射球类物体（触墙自动反弹）
	 * 
	 * @param o
	 * @param vx
	 * @param vy
	 * @param g
	 * @return
	 */
	public BallTo callBallTo(Actor o, int r, int vx, int vy, double g) {
		BallTo ball = new BallTo(r, vx, vy, g);
		addActionEvent(ball, o);
		return ball;
	}

	/**
	 * 让角色缩放指定大小
	 * 
	 * @param o
	 * @param sx
	 * @param sy
	 * @return
	 */
	public ScaleTo callScaleTo(Actor o, float sx, float sy) {
		ScaleTo scale = new ScaleTo(sx, sy);
		addActionEvent(scale, o);
		return scale;
	}

	/**
	 * 让角色缩放指定大小
	 * 
	 * @param o
	 * @param s
	 * @return
	 */
	public ScaleTo callScaleTo(Actor o, float s) {
		return callScaleTo(o, s, s);
	}

	/**
	 * 以指定瓦片大小创建数组地图
	 * 
	 * @param tileWidth
	 * @param tileHeight
	 * @return
	 */
	public Field2D createArrayMap(int tileWidth, int tileHeight) {
		tmpField = new Field2D(new int[getHeight() / tileHeight][getWidth()
				/ tileWidth], tileWidth, tileHeight);
		return tmpField;
	}

	/**
	 * 设定Layer对应的二维数组地图
	 * 
	 * @param map
	 */
	public void setField2D(Field2D field) {
		if (field == null) {
			return;
		}
		if (tmpField != null) {
			if ((field.getMap().length == tmpField.getMap().length)
					&& (field.getTileWidth() == tmpField.getTileWidth())
					&& (field.getTileHeight() == tmpField.getTileHeight())) {
				tmpField.set(field.getMap(), field.getTileWidth(), field
						.getTileHeight());
			}
		} else {
			tmpField = field;
		}
	}

	/**
	 * 返回Layer对应的二维数据地图
	 * 
	 * @param map
	 */
	public Field2D getField2D() {
		return tmpField;
	}

	/**
	 * 添加角色到Layer(在Layer中添加的角色将自动赋予碰撞检查)
	 * 
	 * @param object
	 * @param x
	 * @param y
	 */
	public void addObject(Actor object, int x, int y) {
		synchronized (collisionChecker) {
			if (this.objects.add(object)) {
				object.addLayer(x, y, this);
				this.collisionChecker.addObject(object);
				object.addLayer(this);
			}
		}
	}

	/**
	 * 添加角色到Layer
	 * 
	 * @param object
	 */
	public void addObject(Actor object) {
		addObject(object, object.x, object.y);
	}

	/**
	 * 将指定角色于Layer前置
	 * 
	 * @param actor
	 */
	void sendToFront(Actor actor) {
		if (objects != null) {
			synchronized (objects) {
				if (objects != null) {
					objects.sendToFront(actor);
				}
			}
		}
	}

	/**
	 * 将指定角色于Layer后置
	 * 
	 * @param actor
	 */
	void sendToBack(Actor actor) {
		if (objects != null) {
			synchronized (objects) {
				if (objects != null) {
					objects.sendToBack(actor);
				}
			}
		}
	}

	/**
	 * 参考指定大小根据Layer范围生成一组不重复的随机坐标
	 * 
	 * @param act
	 * @param count
	 * @return
	 */
	public RectBox[] getRandomLayerLocation(int nx, int ny, int nw, int nh,
			int count) {
		if (count <= 0) {
			throw new RuntimeException("count <= 0 !");
		}
		int layerWidth = getWidth();
		int layerHeight = getHeight();
		int actorWidth = nw > min_size ? nw : min_size;
		int actorHeight = nh > min_size ? nh : min_size;
		int x = nx / actorWidth;
		int y = ny / actorHeight;
		int row = layerWidth / actorWidth;
		int col = layerHeight / actorHeight;
		RectBox[] randoms = new RectBox[count];
		int oldRx = 0, oldRy = 0;
		int index = 0;
		for (int i = 0; i < count * 100; i++) {
			if (index >= count) {
				return randoms;
			}
			int rx = LSystem.random.nextInt(row);
			int ry = LSystem.random.nextInt(col);
			if (oldRx != rx && oldRy != ry && rx != x && ry != y
					&& rx * actorWidth != nx && ry * actorHeight != ny) {
				boolean stop = false;
				for (int j = 0; j < index; j++) {
					if (randoms[j].x == rx && randoms[j].y == ry && oldRx != x
							&& oldRy != y && rx * actorWidth != nx
							&& ry * actorHeight != ny) {
						stop = true;
						break;
					}
				}
				if (stop) {
					continue;
				}
				randoms[index] = new RectBox(rx * actorWidth, ry * actorHeight,
						actorWidth, actorHeight);
				oldRx = rx;
				oldRy = ry;
				index++;
			}
		}
		return null;
	}

	/**
	 * 参考指定大小根据Layer范围生成一组不重复的随机坐标
	 * 
	 * @param actorWidth
	 * @param actorHeight
	 * @param count
	 * @return
	 */
	public RectBox[] getRandomLayerLocation(int actorWidth, int actorHeight,
			int count) {
		return getRandomLayerLocation(0, 0, actorWidth, actorHeight, count);
	}

	/**
	 * 参考指定角色根据Layer范围生成一组不重复的随机坐标
	 * 
	 * @param actor
	 * @param count
	 * @return
	 */
	public RectBox[] getRandomLayerLocation(Actor actor, int count) {
		RectBox rect = actor.getRectBox();
		return getRandomLayerLocation(rect.x, rect.y, rect.width, rect.height,
				count);
	}

	/**
	 * 参考指定Actor大小根据Layer生成一个不重复的随机坐标
	 * 
	 * @param actor
	 * @return
	 */
	public RectBox getRandomLayerLocation(Actor actor) {
		RectBox[] rects = getRandomLayerLocation(actor, 1);
		if (rects != null) {
			return rects[0];
		}
		return null;
	}

	/**
	 * 删除指定的角色
	 * 
	 * @param object
	 */
	public void removeObject(Actor object) {
		if (object == null) {
			return;
		}
		synchronized (collisionChecker) {
			if (this.objects.remove(object)) {
				this.collisionChecker.removeObject(object);
			}
			removeActionEvents(object);
			object.setLayer((ActorLayer) null);
		}
	}

	/**
	 * 删除所有指定的游戏类
	 * 
	 * @param clazz
	 */
	public void removeObject(Class<?> clazz) {
		synchronized (collisionChecker) {
			Iterator<?> it = objects.iterator();
			while (it.hasNext()) {
				Actor actor = (Actor) it.next();
				if (actor == null) {
					continue;
				}
				Class<?> cls = actor.getClass();
				if (clazz == null || clazz == cls || clazz.isInstance(actor)
						|| clazz.equals(cls)) {
					if (this.objects.remove(actor)) {
						this.collisionChecker.removeObject(actor);
					}
					removeActionEvents(actor);
					actor.setLayer((ActorLayer) null);
				}
			}
		}
	}

	/**
	 * 删除指定集合中的所有角色
	 * 
	 * @param objects
	 */
	public void removeObjects(Collection<?> objects) {
		Iterator<?> iter = objects.iterator();
		while (iter.hasNext()) {
			Actor actor = (Actor) iter.next();
			this.removeObject(actor);
		}
	}

	/**
	 * 获得含有指定角色碰撞的List集合
	 * 
	 * @param actor
	 * @return
	 */
	public List<?> getCollisionObjects(Actor actor) {
		return getCollisionObjects(actor.getClass());
	}

	/**
	 * 刷新缓存数据，重置世界
	 * 
	 */
	public void reset() {
		if (objects != null) {
			synchronized (objects) {
				if (objects != null) {
					objects.clear();
					objects = null;
				}
				if (collisionChecker != null) {
					collisionChecker.clear();
					collisionChecker = null;
				}
				this.collisionChecker = new CollisionManager();
				this.objects = new ActorTreeSet();
			}
		}
	}

	/**
	 * 获得指定类所产生角色碰撞的List集合
	 * 
	 * @param cls
	 * @return
	 */
	public List<?> getCollisionObjects(Class<?> cls) {
		ArrayList<Actor> result = new ArrayList<Actor>();
		Iterator<?> it = this.objects.iterator();
		while (it.hasNext()) {
			Actor actor = (Actor) it.next();
			if (cls == null || cls.isInstance(actor)) {
				result.add(actor);
			}
		}
		return result;
	}

	/**
	 * 返回指定角色类在指定位置的List集合
	 * 
	 * @param x
	 * @param y
	 * @param cls
	 * @return
	 */
	public List<?> getCollisionObjectsAt(int x, int y, Class<?> cls) {
		return this.collisionChecker.getObjectsAt(x, y, cls);
	}

	/**
	 * 返回指定区域对应的单一Actor
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Actor getOnlyCollisionObjectsAt(int x, int y) {
		for (Iterator<?> it = objects.iterator(); it.hasNext();) {
			Actor a = (Actor) it.next();
			if (a.getRectBox().contains(x, y)) {
				return a;
			}
		}
		return null;
	}

	/**
	 * 返回指定区域和标记对应的单一Actor
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Actor getOnlyCollisionObjectsAt(int x, int y, Object tag) {
		for (Iterator<?> it = objects.iterator(); it.hasNext();) {
			Actor a = (Actor) it.next();
			if (a.getRectBox().contains(x, y) && a.getTag() == tag) {
				return a;
			}
		}
		return null;
	}

	/**
	 * 角色对象总数
	 * 
	 * @return
	 */
	public int size() {
		return this.objects.size();
	}

	public abstract void action(long elapsedTime);

	public boolean isBounded() {
		return this.isBounded;
	}

	Actor getSynchronizedObject(int x, int y) {
		if (objects == null) {
			return null;
		}
		synchronized (objects) {
			Collection<?> collection = getCollisionObjects(x, y);
			if (collection == null) {
				return null;
			}
			if (collection.isEmpty()) {
				return null;
			}
			Iterator<?> iter = collection.iterator();
			Actor tmp = (Actor) iter.next();
			int seq = tmp.getLastPaintSeqNum();
			while (iter.hasNext()) {
				Actor actor = (Actor) iter.next();
				int actorSeq = actor.getLastPaintSeqNum();
				if (actorSeq > seq) {
					tmp = actor;
					seq = actorSeq;
				}
			}
			return tmp;
		}
	}

	List<?> getIntersectingObjects(Actor actor, Class<?> cls) {
		return this.collisionChecker.getIntersectingObjects(actor, cls);
	}

	Actor getOnlyIntersectingObject(Actor object, Class<?> cls) {
		return this.collisionChecker.getOnlyIntersectingObject(object, cls);
	}

	List<?> getObjectsInRange(int x, int y, int r, Class<?> cls) {
		return this.collisionChecker.getObjectsInRange(x, y, r, cls);
	}

	List<?> getNeighbours(Actor actor, int distance, boolean d, Class<?> cls) {
		if (distance < 0) {
			throw new RuntimeException("distance < 0");
		} else {
			return this.collisionChecker.getNeighbours(actor, distance, d, cls);
		}
	}

	int getHeightInPixels() {
		return this.getHeight() * this.cellSize;
	}

	int getWidthInPixels() {
		return this.getWidth() * this.cellSize;
	}

	int toCellCeil(int pixel) {
		return (int) Math.ceil((double) pixel / (double) this.cellSize);
	}

	int toCellFloor(int pixel) {
		return (int) Math.floor((double) pixel / (double) this.cellSize);
	}

	double getCellCenter(int c) {
		double cellCenter = (double) (c * this.cellSize)
				+ (double) this.cellSize / 2.0D;
		return cellCenter;
	}

	Collection<?> getCollisionObjects(int x, int y) {
		ArrayList<Actor> result = new ArrayList<Actor>(20);
		Iterator<?> it = objects.iterator();
		while (it.hasNext()) {
			Actor actor = (Actor) it.next();
			RectBox bounds = actor.getRectBox();
			if (bounds.contains(x, y)) {
				result.add(actor);
			}
		}
		return result;
	}

	void updateObjectLocation(Actor object, int oldX, int oldY) {
		this.collisionChecker.updateObjectLocation(object, oldX, oldY);
	}

	void updateObjectSize(Actor object) {
		this.collisionChecker.updateObjectSize(object);
	}

	Actor getOnlyObjectAt(Actor object, int dx, int dy, Class<?> cls) {
		return this.collisionChecker.getOnlyObjectAt(object, dx, dy, cls);
	}

	ActorTreeSet getObjectsListInPaintO() {
		return this.objects;
	}

}
