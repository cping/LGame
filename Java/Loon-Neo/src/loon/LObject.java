/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon;

import loon.action.ActionBind;
import loon.action.ActionControl;
import loon.action.ActionEvent;
import loon.action.collision.CollisionHelper;
import loon.action.map.Field2D;
import loon.canvas.Alpha;
import loon.component.layout.LayoutAlign;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.BlendMode;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;
import loon.utils.reply.Var;
import loon.utils.reply.VarView;

public abstract class LObject<T> extends BlendMode implements XY, ZIndex {

	public static enum State {
		UNKOWN, REMOVED, ADDED, DISPOSED
	}

	protected final VarView<State> _state = Var.create(State.UNKOWN);

	public final void setState(State state) {
		((Var<State>) this._state).update(state);
	}

	public final State getState() {
		return this._state.get();
	}

	public final boolean isDisposed() {
		return _state.get() == State.DISPOSED;
	}

	public final boolean isRemoved() {
		return _state.get() == State.REMOVED;
	}

	public final boolean isAdded() {
		return _state.get() == State.ADDED;
	}

	public final boolean isUnkown() {
		return _state.get() == State.UNKOWN;
	}

	// 无状态
	public static final int NOT = -1;
	// 真
	public static final int TRUE = 1;
	// 假
	public static final int FALSE = 2;

	protected T _super = null;

	public void setSuper(T s) {
		this._super = s;
	}

	public T getSuper() {
		return this._super;
	}

	public boolean hasSuper() {
		return this._super != null;
	}

	public boolean hasParent() {
		return hasSuper();
	}

	public T getParent() {
		return getSuper();
	}

	public void setParent(final T e) {
		setSuper(e);
	}

	/**
	 * 添加一个独立事件，并选择是否暂不启动
	 * 
	 * @param action
	 * @param obj
	 * @param paused
	 */
	public final static void addActionEvent(ActionEvent action, ActionBind obj, boolean paused) {
		ActionControl.get().addAction(action, obj, paused);
	}

	/**
	 * 添加一个独立事件
	 * 
	 * @param action
	 * @param obj
	 */
	public final static void addActionEvent(ActionEvent action, ActionBind obj) {
		ActionControl.get().addAction(action, obj);
	}

	/**
	 * 删除所有和指定对象有关的独立事件
	 * 
	 * @param actObject
	 */
	public final static void removeActionEvents(ActionBind actObject) {
		ActionControl.get().removeAllActions(actObject);
	}

	/**
	 * 获得当前独立事件总数
	 * 
	 * @return
	 */
	public final static int getActionEventCount() {
		return ActionControl.get().getCount();
	}

	/**
	 * 删除指定的独立事件
	 * 
	 * @param tag
	 * @param actObject
	 */
	public final static void removeActionEvent(Object tag, ActionBind actObject) {
		ActionControl.get().removeAction(tag, actObject);
	}

	/**
	 * 删除指定的独立事件
	 * 
	 * @param action
	 */
	public final static void removeActionEvent(ActionEvent action) {
		ActionControl.get().removeAction(action);
	}

	/**
	 * 获得制定的独立事件
	 * 
	 * @param tag
	 * @param actObject
	 * @return
	 */
	public final static ActionEvent getActionEvent(Object tag, ActionBind actObject) {
		return ActionControl.get().getAction(tag, actObject);
	}

	/**
	 * 停止对象对应的自动事件
	 * 
	 * @param actObject
	 */
	public final static void stopActionEvent(ActionBind actObject) {
		ActionControl.get().stop(actObject);
	}

	/**
	 * 设定指定角色暂停状态
	 * 
	 * @param pause
	 * @param actObject
	 */
	public final static void pauseActionEvent(boolean pause, ActionBind actObject) {
		ActionControl.get().paused(pause, actObject);
	}

	/**
	 * 设置是否暂停自动事件运行
	 * 
	 * @param pause
	 */
	public final static void pauseActionEvent(boolean pause) {
		ActionControl.get().setPause(pause);
	}

	/**
	 * 获得是否暂停了独立事件运行
	 * 
	 * @return
	 */
	public final static boolean isPauseActionEvent() {
		return ActionControl.get().isPause();
	}

	/**
	 * 启动指定对象对应的对立事件
	 * 
	 * @param actObject
	 */
	public final static void startActionEvent(ActionBind actObject) {
		ActionControl.get().start(actObject);
	}

	/**
	 * 停止独立事件运行用线程
	 * 
	 */
	public final static void stopActionEvent() {
		ActionControl.get().stop();
	}

	// 附注用的Tag标记对象(加上什么都可以,传参也行,标记对象也行)
	public Object Tag;

	private Object _collisionData;

	protected float _alpha = 1f;

	protected RectBox _rect;

	protected String _name;

	protected String _object_flag;

	protected Vector2f _location = new Vector2f(0, 0);

	protected int _layer;

	protected float _rotation;

	private int _objStatus = NOT;

	private static int _sys_seqNo = 0;

	private int _seqNo = 0;

	public LObject() {
		_seqNo = _sys_seqNo;
		_sys_seqNo++;
	}

	public final static int allLObjects() {
		return _sys_seqNo;
	}

	public final int getSequenceNo() {
		return _seqNo;
	}

	public final void setStatus(int status) {
		this._objStatus = status;
	}

	public final void setLife(int status) {
		setStatus(status);
	}

	public final void addLife() {
		setStatus(_objStatus++);
	}

	public final int getStatus() {
		return this._objStatus;
	}

	public final void removeLife() {
		setStatus(_objStatus--);
	}

	public final int getLife() {
		return getStatus();
	}

	public final void setObjectFlag(String flag) {
		this._object_flag = flag;
	}

	public final String getObjectFlag() {
		return StringUtils.isEmpty(this._object_flag) ? getName() : this._object_flag;
	}

	public void setTransparency(int a) {
		setAlpha(a / 255f);
	}

	public int getTransparency() {
		return (int) (_alpha * 255);
	}

	public Alpha getAlphaObject() {
		return new Alpha(_alpha);
	}

	public void setAlpha(float a) {
		if (a < 0f) {
			a = 0f;
		}
		if (a > 1f) {
			a = 1f;
		}
		this._alpha = a;
	}

	public float getAlpha() {
		return this._alpha;
	}

	public void setRotation(float r) {
		this._rotation = r;
		if (_rotation > 360f) {
			_rotation = 0f;
		}
		if (_rect != null) {
			_rect.setBounds(MathUtils.getBounds(_location.x, _location.y, getWidth(), getHeight(), r, _rect));
		} else {
			_rect = MathUtils.getBounds(_location.x, _location.y, getWidth(), getHeight(), r, _rect);
		}
	}

	public float getRotation() {
		return _rotation;
	}

	public abstract void update(long elapsedTime);

	public void centerOnScreen() {
		LObject.centerOn(this, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public void bottomOnScreen() {
		LObject.bottomOn(this, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public void leftOnScreen() {
		LObject.leftOn(this, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public void rightOnScreen() {
		LObject.rightOn(this, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public void topOnScreen() {
		LObject.topOn(this, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public RectBox getCollisionArea() {
		return getRect(getX(), getY(), getWidth(), getHeight());
	}

	protected RectBox setRect(RectBox rect) {
		if (_rect == null) {
			_rect = rect;
		} else {
			_rect.setBounds(rect);
		}
		return this._rect;
	}

	protected RectBox getRect(float x, float y, float w, float h) {
		if (_rect == null) {
			_rect = new RectBox(x, y, w, h);
		} else {
			_rect.setBounds(x, y, w, h);
		}
		return _rect;
	}

	public boolean isContains(LObject<T> o) {
		return CollisionHelper.contains(this.getCollisionArea(), o.getCollisionArea());
	}

	public boolean isIntersects(LObject<T> o) {
		return CollisionHelper.isRectToRect(getCollisionArea(), o.getCollisionArea());
	}

	public boolean isIntersectsCircle(LObject<T> o) {
		return CollisionHelper.isRectToCirc(getCollisionArea(), o.getCollisionArea());
	}

	public boolean isCircleIntersectsCircle(LObject<T> o) {
		return CollisionHelper.isCircToCirc(getCollisionArea(), o.getCollisionArea());
	}

	public float getDistance(LObject<T> o) {
		return CollisionHelper.getDistance(getCollisionArea(), o.getCollisionArea());
	}

	public void setName(String name) {
		this._name = name;
	}

	public String getName() {
		if (_name == null) {
			_name = getClass().getName();
			int idx = _name.lastIndexOf('.');
			if (idx != -1 && idx > 0) {
				_name = _name.substring(idx + 1).intern();
			} else {
				_name = "LObject";
			}
		}
		return _name;
	}

	@Override
	public int getLayer() {
		return _layer;
	}

	public void setLayer(int z) {
		this._layer = z;
	}

	public void setZ(int z) {
		setLayer(-z);
	}

	public void setZOrder(int z) {
		setLayer(-z);
	}

	public int getZOrder() {
		return MathUtils.abs(getLayer());
	}

	public int getZ() {
		return MathUtils.abs(getLayer());
	}

	public void move_45D_up() {
		move_45D_up(1);
	}

	public void move_45D_up(int multiples) {
		_location.move_multiples(Field2D.UP, multiples);
	}

	public void move_45D_left() {
		move_45D_left(1);
	}

	public void move_45D_left(int multiples) {
		_location.move_multiples(Field2D.LEFT, multiples);
	}

	public void move_45D_right() {
		move_45D_right(1);
	}

	public void move_45D_right(int multiples) {
		_location.move_multiples(Field2D.RIGHT, multiples);
	}

	public void move_45D_down() {
		move_45D_down(1);
	}

	public void move_45D_down(int multiples) {
		_location.move_multiples(Field2D.DOWN, multiples);
	}

	public void move_up() {
		move_up(1);
	}

	public void move_up(int multiples) {
		_location.move_multiples(Field2D.TUP, multiples);
	}

	public void move_left() {
		move_left(1);
	}

	public void move_left(int multiples) {
		_location.move_multiples(Field2D.TLEFT, multiples);
	}

	public void move_right() {
		move_right(1);
	}

	public void move_right(int multiples) {
		_location.move_multiples(Field2D.TRIGHT, multiples);
	}

	public void move_down() {
		move_down(1);
	}

	public void move_down(int multiples) {
		_location.move_multiples(Field2D.TDOWN, multiples);
	}

	public void move(Vector2f v) {
		move(v.x, v.y);
	}

	public void move(float x, float y) {
		_location.move(x, y);
	}

	public void setLocation(XY local) {
		setLocation(local.getX(), local.getY());
	}

	public void setLocation(Vector2f v) {
		setLocation(v.x, v.y);
	}

	public void setPosition(float x, float y) {
		setLocation(x, y);
	}

	public void pos(float x, float y) {
		setLocation(x, y);
	}

	public void setLocation(float x, float y) {
		_location.setLocation(x, y);
	}

	public int x() {
		return (int) _location.getX();
	}

	public int y() {
		return (int) _location.getY();
	}

	@Override
	public float getX() {
		return _location.getX();
	}

	@Override
	public float getY() {
		return _location.getY();
	}

	public void setX(Integer x) {
		setX(x.intValue());
	}

	public void setX(float x) {
		_location.setX(x);
	}

	public void setY(Integer y) {
		setY(y.intValue());
	}

	public void setY(float y) {
		_location.setY(y);
	}

	public void nextXY(float nextValue) {
		setX(getX() + nextValue);
		setY(getY() + nextValue);
	}

	public Vector2f getRoundedLocation(float x, float y) {
		float nx = getX() + x;
		float ny = getY() + y;
		if ((nx + ny) % 2 == 1) {
			y++;
		}
		return Vector2f.at(nx, ny);
	}

	public Vector2f getLocation() {
		return _location;
	}

	public static void centerOn(final LObject<?> object, float w, float h) {
		object.setLocation(w / 2 - object.getWidth() / 2, h / 2 - object.getHeight() / 2);
	}

	public static void topOn(final LObject<?> object, float w, float h) {
		object.setLocation(w / 2 - object.getWidth() / 2, 0);
	}

	public static void topLeftOn(final LObject<?> object, float w, float h) {
		object.setLocation(0, 0);
	}

	public static void topRightOn(final LObject<?> object, float w, float h) {
		object.setLocation(w - object.getWidth(), 0);
	}

	public static void bottomLeftOn(final LObject<?> object, float w, float h) {
		object.setLocation(0, h - object.getHeight());
	}

	public static void bottomRightOn(final LObject<?> object, float w, float h) {
		object.setLocation(w - object.getWidth(), h - object.getHeight());
	}

	public static void leftOn(final LObject<?> object, float w, float h) {
		object.setLocation(0, h / 2 - object.getHeight() / 2);
	}

	public static void rightOn(final LObject<?> object, float w, float h) {
		object.setLocation(w - object.getWidth(), h / 2 - object.getHeight() / 2);
	}

	public static void bottomOn(final LObject<?> object, float w, float h) {
		object.setLocation(w / 2 - object.getWidth() / 2, h - object.getHeight());
	}

	public void moveOn(final LayoutAlign align, final LObject<?> obj) {
		if (align != null && obj != null) {
			switch (align) {
			case Left:
				this.leftOn(obj);
				break;
			case Right:
				this.rightOn(obj);
				break;
			case Center:
				this.centerOn(obj);
				break;
			case Top:
				this.topOn(obj);
				break;
			case Bottom:
				this.bottomOn(obj);
				break;
			case TopLeft:
				this.topLeftOn(obj);
				break;
			case TopRight:
				this.topRightOn(obj);
				break;
			case BottomLeft:
				this.bottomLeftOn(obj);
				break;
			case BottomRight:
				this.bottomRightOn(obj);
				break;
			}
		}
	}

	public void centerOn(final LObject<?> object) {
		centerOn(object, getWidth(), getHeight());
	}

	public void topOn(final LObject<?> object) {
		topOn(object, getWidth(), getHeight());
	}

	public void leftOn(final LObject<?> object) {
		leftOn(object, getWidth(), getHeight());
	}

	public void rightOn(final LObject<?> object) {
		rightOn(object, getWidth(), getHeight());
	}

	public void bottomOn(final LObject<?> object) {
		bottomOn(object, getWidth(), getHeight());
	}

	public void topLeftOn(final LObject<?> object) {
		topLeftOn(object, getWidth(), getHeight());
	}

	public void topRightOn(final LObject<?> object) {
		topRightOn(object, getWidth(), getHeight());
	}

	public void bottomLeftOn(final LObject<?> object) {
		bottomLeftOn(object, getWidth(), getHeight());
	}

	public void bottomRightOn(final LObject<?> object) {
		bottomRightOn(object, getWidth(), getHeight());
	}

	public final void setCollisionData(Object data) {
		this._collisionData = data;
	}

	public final Object getCollisionData() {
		return _collisionData;
	}

	public abstract float getWidth();

	public abstract float getHeight();

	public float getContainerX() {
		return LSystem.getProcess() == null ? 0 : LSystem.getProcess().getX();
	}

	public float getContainerY() {
		return LSystem.getProcess() == null ? 0 : LSystem.getProcess().getY();
	}

	public float getContainerWidth() {
		return LSystem.viewSize.width();
	}

	public float getContainerHeight() {
		return LSystem.viewSize.height();
	}

	public Object getTag() {
		return Tag;
	}

	public void setTag(Object t) {
		this.Tag = t;
	}

	@Override
	public int hashCode() {
		return _seqNo;
	}

	@Override
	public String toString() {
		return new StringKeyValue("LObject").kv("sequence", _seqNo).comma().kv("name", getName()).comma()
				.kv("state", _state.get()).comma().kv("super", _super == null ? "empty" : _super.getClass()).comma()
				.kv("pos", _location).comma().kv("size", _rect).comma().kv("alpha", _alpha).comma()
				.kv("rotation", _rotation).comma().kv("layer", _layer).comma().kv("tag", Tag).toString();
	}

}
