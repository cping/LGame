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
import loon.action.map.Field2D;
import loon.canvas.Alpha;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.utils.MathUtils;

public abstract class LObject implements XY, ZIndex {

	// 无状态
	public static final int NOT = -1;
	// 真
	public static final int TRUE = 1;
	// 假
	public static final int FALSE = 2;

	/**
	 * 添加一个独立事件，并选择是否暂不启动
	 * 
	 * @param action
	 * @param obj
	 * @param paused
	 */
	public final static void addActionEvent(ActionEvent action, ActionBind obj,
			boolean paused) {
		ActionControl.getInstance().addAction(action, obj, paused);
	}

	/**
	 * 添加一个独立事件
	 * 
	 * @param action
	 * @param obj
	 */
	public final static void addActionEvent(ActionEvent action, ActionBind obj) {
		ActionControl.getInstance().addAction(action, obj);
	}

	/**
	 * 删除所有和指定对象有关的独立事件
	 * 
	 * @param actObject
	 */
	public final static void removeActionEvents(ActionBind actObject) {
		ActionControl.getInstance().removeAllActions(actObject);
	}

	/**
	 * 获得当前独立事件总数
	 * 
	 * @return
	 */
	public final static int getActionEventCount() {
		return ActionControl.getInstance().getCount();
	}

	/**
	 * 删除指定的独立事件
	 * 
	 * @param tag
	 * @param actObject
	 */
	public final static void removeActionEvent(Object tag, ActionBind actObject) {
		ActionControl.getInstance().removeAction(tag, actObject);
	}

	/**
	 * 删除指定的独立事件
	 * 
	 * @param action
	 */
	public final static void removeActionEvent(ActionEvent action) {
		ActionControl.getInstance().removeAction(action);
	}

	/**
	 * 获得制定的独立事件
	 * 
	 * @param tag
	 * @param actObject
	 * @return
	 */
	public final static ActionEvent getActionEvent(Object tag,
			ActionBind actObject) {
		return ActionControl.getInstance().getAction(tag, actObject);
	}

	/**
	 * 停止对象对应的自动事件
	 * 
	 * @param actObject
	 */
	public final static void stopActionEvent(ActionBind actObject) {
		ActionControl.getInstance().stop(actObject);
	}

	/**
	 * 设定指定角色暂停状态
	 * 
	 * @param pause
	 * @param actObject
	 */
	public final static void pauseActionEvent(boolean pause,
			ActionBind actObject) {
		ActionControl.getInstance().paused(pause, actObject);
	}

	/**
	 * 设置是否暂停自动事件运行
	 * 
	 * @param pause
	 */
	public final static void pauseActionEvent(boolean pause) {
		ActionControl.getInstance().setPause(pause);
	}

	/**
	 * 获得是否暂停了独立事件运行
	 * 
	 * @return
	 */
	public final static boolean isPauseActionEvent() {
		return ActionControl.getInstance().isPause();
	}

	/**
	 * 启动指定对象对应的对立事件
	 * 
	 * @param actObject
	 */
	public final static void startActionEvent(ActionBind actObject) {
		ActionControl.getInstance().start(actObject);
	}

	/**
	 * 停止独立事件运行用线程
	 * 
	 */
	public final static void stopActionEvent() {
		ActionControl.getInstance().stop();
	}

	public Object Tag;

	protected float _alpha = 1f;

	protected RectBox _rect;

	protected String _name;

	protected Vector2f _location = new Vector2f(0, 0);

	protected int _layer;

	protected float _rotation;

	private int _objStatus = NOT;

	private static int _sys_seqNo = 0;

	private int _seqNo = 0;

	public LObject() {
		_name = getClass().getSimpleName();
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

	public final int getStatus() {
		return this._objStatus;
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
		this._alpha = a;
	}

	public float getAlpha() {
		return this._alpha;
	}

	public void setRotation(float r) {
		this._rotation = r;
		if (_rect != null) {
			_rect.setBounds(MathUtils.getBounds(_location.x, _location.y,
					getWidth(), getHeight(), r, _rect));
		} else {
			_rect = MathUtils.getBounds(_location.x, _location.y, getWidth(),
					getHeight(), r, _rect);
		}
	}

	public float getRotation() {
		return _rotation;
	}

	public abstract void update(long elapsedTime);

	public void centerOnScreen() {
		LObject.centerOn(this, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public void bottomOnScreen() {
		LObject.bottomOn(this, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public void leftOnScreen() {
		LObject.leftOn(this, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public void rightOnScreen() {
		LObject.rightOn(this, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public void topOnScreen() {
		LObject.topOn(this, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public RectBox getCollisionArea() {
		return getRect(getX(), getY(), getWidth(), getHeight());
	}

	protected RectBox getRect(float x, float y, float w, float h) {
		if (_rect == null) {
			_rect = new RectBox(x, y, w, h);
		} else {
			_rect.setBounds(x, y, w, h);
		}
		return _rect;
	}

	public void setName(String name) {
		this._name = name;
	}

	public String getName() {
		return _name==null?getClass().getSimpleName():_name;
	}

	public int getLayer() {
		return _layer;
	}

	public void setLayer(int z) {
		this._layer = z;
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
		_location.move(v);
	}

	public void move(float x, float y) {
		_location.move(x, y);
	}

	public void setLocation(XY local) {
		_location.setLocation(local.getX(), local.getY());
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

	public float getX() {
		return _location.getX();
	}

	public float getY() {
		return _location.getY();
	}

	public void setX(Integer x) {
		_location.setX(x.intValue());
	}

	public void setX(float x) {
		_location.setX(x);
	}

	public void setY(Integer y) {
		_location.setY(y.intValue());
	}

	public void setY(float y) {
		_location.setY(y);
	}

	public Vector2f getLocation() {
		return _location;
	}

	public void setLocation(Vector2f _location) {
		this._location = _location;
	}

	public static void centerOn(final LObject object, float w, float h) {
		object.setLocation(w / 2 - object.getWidth() / 2,
				h / 2 - object.getHeight() / 2);
	}

	public static void topOn(final LObject object, float w, float h) {
		object.setLocation(w / 2 - h / 2, 0);
	}

	public static void leftOn(final LObject object, float w, float h) {
		object.setLocation(0, h / 2 - object.getHeight() / 2);
	}

	public static void rightOn(final LObject object, float w, float h) {
		object.setLocation(w - object.getWidth(), h / 2 - object.getHeight()
				/ 2);
	}

	public static void bottomOn(final LObject object, float w, float h) {
		object.setLocation(w / 2 - object.getWidth() / 2,
				h - object.getHeight());
	}

	public void centerOn(final LObject object) {
		centerOn(object, getWidth(), getHeight());
	}

	public void topOn(final LObject object) {
		topOn(object, getWidth(), getHeight());
	}

	public void leftOn(final LObject object) {
		leftOn(object, getWidth(), getHeight());
	}

	public void rightOn(final LObject object) {
		rightOn(object, getWidth(), getHeight());
	}

	public void bottomOn(final LObject object) {
		bottomOn(object, getWidth(), getHeight());
	}
	
	public abstract float getWidth();

	public abstract float getHeight();

	public float getContainerWidth() {
		return LSystem.viewSize.getWidth();
	}

	public float getContainerHeight() {
		return LSystem.viewSize.getHeight();
	}

	@Override
	public int hashCode() {
		return _seqNo;
	}

}
