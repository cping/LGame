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
package loon.action;

import loon.LSysException;
import loon.LSystem;
import loon.action.map.CustomPath;
import loon.action.map.Field2D;
import loon.action.sprite.ISprite;
import loon.action.sprite.effect.BaseEffect;
import loon.canvas.LColor;
import loon.events.ActionUpdate;
import loon.events.EventAction;
import loon.events.FrameLoopEvent;
import loon.geom.Bezier;
import loon.geom.BooleanValue;
import loon.geom.Vector2f;
import loon.utils.Array;
import loon.utils.Easing;
import loon.utils.TArray;
import loon.utils.Easing.EasingMode;
import loon.utils.MathUtils;
import loon.utils.timer.Duration;
import loon.utils.StringKeyValue;

public class ActionTween extends ActionTweenBase<ActionTween> {

	private float _initMoveSpeed;

	private int _combinedAttrsLimit;

	private int _funPointsLimit;

	public void setCombinedAttributesLimit(int limit) {
		this._combinedAttrsLimit = limit;
	}

	public void setfunPointsLimit(int limit) {
		this._funPointsLimit = limit;
	}

	private static final ActionTweenPool.Callback<ActionTween> _POOL_CALLBACK = new ActionTweenPool.Callback<ActionTween>() {
		@Override
		public void onPool(ActionTween obj) {
			obj.reset();
		}

		@Override
		public void onUnPool(ActionTween obj) {
			try {
				obj.reset();
				onSuccess(obj);
			} catch (Throwable ex) {
				LSystem.error("Action Tween exception", ex);
				onFailure(ex);
			}
		}

		@Override
		public void onSuccess(ActionTween result) {

		}

		@Override
		public void onFailure(Throwable cause) {

		}
	};

	private static final ActionTweenPool<ActionTween> _POOLS = new ActionTweenPool<ActionTween>(20, _POOL_CALLBACK) {
		@Override
		protected ActionTween create() {
			return new ActionTween();
		}
	};

	/**
	 * 从当前ActionBind数值到指定目标(大多数时候，调用此状态已经足够)
	 * 
	 * @param target    具体的操作对象
	 * @param tweenType 需要转变的接口
	 * @param duration  持续时间
	 * @return
	 */
	public static ActionTween to(ActionBind target, int tweenType, float duration) {
		ActionTween tween = _POOLS.get();
		tween.setup(target, tweenType, duration);
		tween.ease(Easing.QUAD_INOUT);
		tween.path(ActionControl.SMOOTH);
		return tween;
	}

	/**
	 * 从注入的数值演变到当前值
	 * 
	 * @param target
	 * @param tweenType
	 * @param duration
	 * @return
	 */
	public static ActionTween from(ActionBind target, int tweenType, float duration) {
		ActionTween tween = _POOLS.get();
		tween.setup(target, tweenType, duration);
		tween.ease(Easing.QUAD_INOUT);
		tween.path(ActionControl.SMOOTH);
		tween._isFrom = true;
		return tween;
	}

	/**
	 * 直接注入当前对象为指定数值
	 * 
	 * @param target
	 * @param tweenType
	 * @return
	 */
	public static ActionTween set(ActionBind target, int tweenType) {
		ActionTween tween = _POOLS.get();
		tween.setup(target, tweenType, 0);
		tween.ease(Easing.QUAD_INOUT);
		return tween;
	}

	/**
	 * 直接调用一个ActionCallback方法
	 * 
	 * @param callback
	 * @return
	 */
	public static ActionTween call(ActionCallback callback) {
		ActionTween tween = _POOLS.get();
		tween.setup(null, -1, 0);
		tween.setCallback(callback);
		tween.setCallbackTriggers(ActionMode.START);
		return tween;
	}

	/**
	 * 制作一个无状态的空ActionTween对象
	 * 
	 * @return
	 */
	public static ActionTween mark() {
		ActionTween tween = _POOLS.get();
		tween.setup(null, -1, 0);
		return tween;
	}

	public static int getPoolSize() {
		return _POOLS.size();
	}

	public static void resize(int minCapacity) {
		_POOLS.resize(minCapacity);
	}

	private ActionEvent _currentActionEvent;

	private Array<ActionEvent> _repeatList;

	private int _type;
	private Easing _equation;
	private ActionPath _path;

	private boolean _isFrom;
	private boolean _isRelative;
	private boolean _isRepeat;

	private int _combinedAttrsSize;
	private int _funPointsSize;

	private final float[] _startValues;
	private final float[] _targetValues;
	private final float[] _funPoints;

	private float[] _accessorBuffer;
	private float[] _pathBuffer;

	private Array<ActionEvent> _actionEvents;

	private ActionTween() {
		this(3, 0);
	}

	private ActionTween(int limit, int funlimit) {
		this._initMoveSpeed = MoveTo._INIT_MOVE_SPEED;
		this._combinedAttrsLimit = limit;
		this._funPointsLimit = funlimit;
		this._startValues = new float[_combinedAttrsLimit];
		this._targetValues = new float[_combinedAttrsLimit];
		this._funPoints = new float[_funPointsLimit * _combinedAttrsLimit];
		this._accessorBuffer = new float[_combinedAttrsLimit];
		this._pathBuffer = new float[(2 + _funPointsLimit) * _combinedAttrsLimit];
		this.reset();
	}

	public ActionTween select(boolean selected, ActionEvent a, ActionEvent b) {
		return event(selected ? a : b);
	}

	public ActionTween flashTo() {
		return event(new FlashTo());
	}

	public ActionTween flashTo(float duration) {
		return event(new FlashTo(duration));
	}

	public ActionTween flashTo(float duration, EasingMode easing) {
		return event(new FlashTo(duration, easing));
	}

	public ActionTween flashTo(float duration, float delay) {
		return event(new FlashTo(duration, delay));
	}

	public ActionTween flashTo(float duration, float delay, EasingMode easing) {
		return event(new FlashTo(duration, delay, easing));
	}

	/**
	 * 按照指定路径移动
	 * 
	 * @param path
	 * @return
	 */
	public ActionTween defineMoveTo(CustomPath path) {
		return defineMoveTo(null, path, true, _initMoveSpeed);
	}

	/**
	 * 按照指定路径移动,并选择是八方向走法或四方向走法
	 * 
	 * @param path
	 * @param flag
	 * @return
	 */
	public ActionTween defineMoveTo(CustomPath path, boolean all) {
		return defineMoveTo(null, path, all, _initMoveSpeed);
	}

	/**
	 * 按照指定路径移动,并选择是八方向走法或四方向走法,以及移动速度
	 * 
	 * @param path
	 * @param all
	 * @param speed
	 * @return
	 */
	public ActionTween defineMoveTo(CustomPath path, boolean all, float speed) {
		return defineMoveTo(null, path, all, speed);
	}

	/**
	 * 在地址的二维数组地图上,按照指定路径移动,并选择是八方向走法或四方向走法
	 * 
	 * @param map
	 * @param path
	 * @param all
	 * @param speed
	 * @return
	 */
	public ActionTween defineMoveTo(Field2D map, CustomPath path, boolean all, float speed) {
		return defineMoveTo(map, path, all, speed, null);
	}

	/**
	 * 按照指定路径移动,并选择监听器
	 * 
	 * @param path
	 * @param l
	 * @return
	 */
	public ActionTween defineMoveTo(CustomPath path, ActionListener l) {
		return defineMoveTo(null, path, true, _initMoveSpeed, l);
	}

	/**
	 * 按照指定路径移动,并选择是八方向走法或四方向走法以及监听器
	 * 
	 * @param path
	 * @param all
	 * @param l
	 * @return
	 */
	public ActionTween defineMoveTo(CustomPath path, boolean all, ActionListener l) {
		return defineMoveTo(null, path, all, _initMoveSpeed, l);
	}

	/**
	 * 按照指定路径移动,并选择是八方向走法或四方向走法以及速度和监听器
	 * 
	 * @param path
	 * @param all
	 * @param speed
	 * @param l
	 * @return
	 */
	public ActionTween defineMoveTo(CustomPath path, boolean all, float speed, ActionListener l) {
		return defineMoveTo(null, path, all, speed, l);
	}

	/**
	 * 在指定的二维数组地图上,按照指定路径移动,并选择是八方向走法或四方向走法以及速度和监听器
	 * 
	 * @param map
	 * @param path
	 * @param all
	 * @param speed
	 * @param l
	 * @return
	 */
	public ActionTween defineMoveTo(Field2D map, CustomPath path, boolean all, float speed, ActionListener l) {
		return defineMoveTo(map, path, all, speed, 0f, 0f, l);
	}

	public ActionTween defineMoveTo(Field2D map, CustomPath path, boolean all, float speed, float offsetX,
			float offsetY, ActionListener l) {
		DefineMoveTo move = new DefineMoveTo(map, path, all, speed);
		move.setDelay(0);
		move.setOffset(offsetX, offsetY);
		return event(move, l);
	}

	/**
	 * 移动角色到指定目标
	 * 
	 * @param endX
	 * @param endY
	 * @return
	 */
	public ActionTween moveTo(float endX, float endY) {
		return moveTo(endX, endY, false, _initMoveSpeed);
	}

	/**
	 * 移动角色到指定目标并监听
	 * 
	 * @param endX
	 * @param endY
	 * @param l
	 * @return
	 */
	public ActionTween moveTo(float endX, float endY, ActionListener l) {
		return moveTo(endX, endY, false, _initMoveSpeed, l);
	}

	/**
	 * 移动角色到指定目标并指定速度
	 * 
	 * @param endX
	 * @param endY
	 * @param speed
	 * @return
	 */
	public ActionTween moveTo(float endX, float endY, float speed) {
		return moveTo(endX, endY, false, speed);
	}

	/**
	 * 移动角色到指定目标并指定速度以及监听
	 * 
	 * @param endX
	 * @param endY
	 * @param speed
	 * @param l
	 * @return
	 */
	public ActionTween moveTo(float endX, float endY, float speed, ActionListener l) {
		return moveTo(endX, endY, false, speed, l);
	}

	/**
	 * 移动角色到指定目标并设置8方向行走或4方向行走
	 * 
	 * @param endX
	 * @param endY
	 * @param flag
	 * @return
	 */
	public ActionTween moveTo(float endX, float endY, boolean flag) {
		return moveTo(null, endX, endY, flag, _initMoveSpeed, 0, 0, null);
	}

	/**
	 * 移动角色到指定目标并设置8方向行走或4方向行走以及监听
	 * 
	 * @param endX
	 * @param endY
	 * @param flag
	 * @param l
	 * @return
	 */
	public ActionTween moveTo(float endX, float endY, boolean flag, ActionListener l) {
		return moveTo(null, endX, endY, flag, _initMoveSpeed, 0, 0, l);
	}

	/**
	 * 移动角色到指定目标并设置8方向行走或4方向行走以及移动速度
	 * 
	 * @param endX
	 * @param endY
	 * @param flag
	 * @param speed
	 * @return
	 */
	public ActionTween moveTo(float endX, float endY, boolean flag, float speed) {
		return moveTo(null, endX, endY, flag, speed, 0, 0, null);
	}

	/**
	 * 移动角色到指定目标并设置8方向行走或4方向行走以及移动速度并且监听
	 * 
	 * @param endX
	 * @param endY
	 * @param flag
	 * @param speed
	 * @param l
	 * @return
	 */
	public ActionTween moveTo(float endX, float endY, boolean flag, float speed, ActionListener l) {
		return moveTo(null, endX, endY, flag, speed, 0, 0, l);
	}

	/**
	 * 移动角色到指定目标并设置8方向行走或4方向行走以及移动速度并进行位置偏移
	 * 
	 * @param endX
	 * @param endY
	 * @param flag
	 * @param offsetX
	 * @param offsetY
	 * @return
	 */
	public ActionTween moveTo(float endX, float endY, boolean flag, float offsetX, float offsetY) {
		return moveTo(null, endX, endY, flag, _initMoveSpeed, offsetX, offsetY, null);
	}

	/**
	 * 移动角色到指定目标并设置8方向行走或4方向行走并进行位置偏移并且监听
	 * 
	 * @param endX
	 * @param endY
	 * @param flag
	 * @param offsetX
	 * @param offsetY
	 * @param l
	 * @return
	 */
	public ActionTween moveTo(float endX, float endY, boolean flag, float offsetX, float offsetY, ActionListener l) {
		return moveTo(null, endX, endY, flag, _initMoveSpeed, offsetX, offsetY, l);
	}

	/**
	 * 移动角色到指定目标并设置8方向行走或4方向行走以及移动速度并进行位置偏移并且监听
	 * 
	 * @param endX
	 * @param endY
	 * @param flag
	 * @param speed
	 * @param offsetX
	 * @param offsetY
	 * @return
	 */
	public ActionTween moveTo(float endX, float endY, boolean flag, float speed, float offsetX, float offsetY) {
		return moveTo(null, endX, endY, flag, speed, offsetX, offsetY, null);
	}

	public ActionTween moveTo(float endX, float endY, boolean flag, float speed, float offsetX, float offsetY,
			ActionListener l) {
		return moveTo(null, endX, endY, flag, speed, offsetX, offsetY, l);
	}

	public ActionTween moveTo(Field2D map, float endX, float endY, boolean flag) {
		return moveTo(map, endX, endY, flag, _initMoveSpeed, 0, 0, null);
	}

	public ActionTween moveTo(Field2D map, float endX, float endY, int delayTime, boolean flag) {
		return moveTo(map, endX, endY, flag, _initMoveSpeed, 0, 0, delayTime, null);
	}

	public ActionTween moveTo(Field2D map, float endX, float endY, boolean flag, float speed) {
		return moveTo(map, endX, endY, flag, speed, 0, 0, null);
	}

	public ActionTween moveTo(Field2D map, float endX, float endY, boolean flag, float speed, ActionListener l) {
		return moveTo(map, endX, endY, flag, speed, 0, 0, l);
	}

	public ActionTween moveTo(Field2D map, float endX, float endY, boolean flag, float speed, float offsetX,
			float offsetY, ActionListener l) {
		return moveTo(map, endX, endY, flag, speed, offsetX, offsetY, 0, l);
	}

	public ActionTween moveTo(Field2D map, float endX, float endY, boolean flag, float speed, float offsetX,
			float offsetY, int delayTime, ActionListener l) {
		if (map != null && map.inside(endX, endY)) {
			MoveTo move = new MoveTo(map, endX, endY, flag, speed, delayTime);
			move.setDelay(0);
			move.setOffset(offsetX, offsetY);
			return event(move, l);
		} else {
			return moveBy(endX, endY, speed, EasingMode.Linear, offsetX, offsetY, l);
		}
	}

	public ActionTween moveTo(Field2D map, float startX, float startY, float endX, float endY, boolean flag) {
		return moveTo(map, startX, startY, endX, endY, 0, flag);
	}

	public ActionTween moveTo(Field2D map, float startX, float startY, float endX, float endY, int delayTime,
			boolean flag) {
		return moveTo(map, startX, startY, endX, endY, delayTime, flag, _initMoveSpeed);
	}

	public ActionTween moveTo(Field2D map, float startX, float startY, float endX, float endY, int delayTime,
			boolean flag, float speed) {
		return moveTo(map, startX, startY, endX, endY, delayTime, flag, speed, 0, 0);
	}

	public ActionTween moveTo(Field2D map, float startX, float startY, float endX, float endY, int delayTime,
			boolean flag, float speed, float offsetX, float offsetY) {
		return moveTo(map, startX, startY, endX, endY, delayTime, flag, speed, offsetX, offsetY, null);
	}

	public ActionTween moveTo(Field2D map, float startX, float startY, float endX, float endY, int delayTime,
			boolean flag, float speed, float offsetX, float offsetY, ActionListener l) {
		if (map != null && map.inside(endX, endY)) {
			MoveTo move = new MoveTo(map, startX, startY, endX, endY, flag, speed, true, false, delayTime);
			move.setDelay(0);
			move.setOffset(offsetX, offsetY);
			return event(move, l);
		} else {
			return moveBy(endX, endY, speed, EasingMode.Linear, offsetX, offsetY, l);
		}
	}

	public ActionTween sizeTo(float endX, float endY) {
		return sizeTo(endX, endY, EasingMode.Linear, null);
	}

	public ActionTween sizeTo(float endX, float endY, ActionListener l) {
		return sizeTo(endX, endY, EasingMode.Linear, l);
	}

	public ActionTween sizeTo(float endX, float endY, EasingMode ease) {
		return sizeTo(endX, endY, ease, null);
	}

	public ActionTween sizeTo(float endX, float endY, EasingMode ease, ActionListener l) {
		return sizeTo(endX, endY, 1f, ease, l);
	}

	public ActionTween sizeTo(float endX, float endY, float duration) {
		return sizeTo(endX, endY, duration, EasingMode.Linear, null);
	}

	public ActionTween sizeTo(float endX, float endY, float duration, EasingMode ease, ActionListener l) {
		SizeTo size = new SizeTo(endX, endY, duration, ease);
		size.setDelay(0);
		return event(size, l);
	}

	public ActionTween sizeBy(float amountWidth, float amountHeight) {
		return sizeBy(amountWidth, amountHeight, EasingMode.Linear, null);
	}

	public ActionTween sizeBy(float amountWidth, float amountHeight, ActionListener l) {
		return sizeBy(amountWidth, amountHeight, EasingMode.Linear, l);
	}

	public ActionTween sizeBy(float amountWidth, float amountHeight, EasingMode ease) {
		return sizeBy(amountWidth, amountHeight, ease, null);
	}

	public ActionTween sizeBy(float amountWidth, float amountHeight, EasingMode ease, ActionListener l) {
		return sizeBy(amountWidth, amountHeight, 1f, ease, l);
	}

	public ActionTween sizeBy(float amountWidth, float amountHeight, float duration) {
		return sizeBy(amountWidth, amountHeight, duration, EasingMode.Linear, null);
	}

	public ActionTween sizeBy(float amountWidth, float amountHeight, float duration, EasingMode ease,
			ActionListener l) {
		SizeBy size = new SizeBy(amountWidth, amountHeight, duration, ease);
		size.setDelay(0);
		return event(size, l);
	}

	public ActionTween doWhile(ActionCondition condition) {
		return event(new DoWhileTo(condition));
	}

	public ActionTween doWhen(ActionCondition... conditions) {
		return event(new DoWhenTo(conditions));
	}

	public ActionTween followTo(ActionBind actorToFollow) {
		return event(new FollowTo(actorToFollow));
	}

	public ActionTween followTo(Field2D field2d, ActionBind actorToFollow) {
		return event(new FollowTo(field2d, actorToFollow));
	}

	public ActionTween followTo(ActionBind actorToFollow, float speed) {
		return event(new FollowTo(actorToFollow, speed));
	}

	public ActionTween followTo(Field2D field2d, ActionBind actorToFollow, float speed) {
		return event(new FollowTo(field2d, actorToFollow, speed));
	}

	public ActionTween followTo(ActionBind actorToFollow, float follow, float speed) {
		return event(new FollowTo(actorToFollow, follow, speed));
	}

	public ActionTween followTo(Field2D field2d, ActionBind actorToFollow, float follow, float speed) {
		return event(new FollowTo(field2d, actorToFollow, follow, speed));
	}

	public ActionTween followTo(ActionBind actorToFollow, float vx, float vy, float follow, float speed) {
		return event(new FollowTo(actorToFollow, vx, vy, follow, speed));
	}

	public ActionTween followTo(Field2D field2d, ActionBind actorToFollow, float vx, float vy, float follow,
			float speed) {
		return event(new FollowTo(field2d, actorToFollow, vx, vy, follow, speed));
	}

	public ActionTween moveBy(float endX, float endY) {
		return moveBy(endX, endY, _initMoveSpeed);
	}

	public ActionTween moveBy(float endX, float endY, ActionListener l) {
		return moveBy(endX, endY, _initMoveSpeed, l);
	}

	public ActionTween moveBy(float endX, float endY, float speed) {
		return event(new MoveBy(endX, endY, speed), null);
	}

	public ActionTween moveBy(float endX, float endY, float speed, ActionListener l) {
		return event(new MoveBy(endX, endY, speed), l);
	}

	public ActionTween moveBy(float endX, float endY, float duration, float delay, EasingMode easing, float offsetX,
			float offsetY) {
		return event(new MoveBy(-1f, -1f, endX, endY, 0, duration, delay, easing, offsetX, offsetY));
	}

	public ActionTween moveBy(float endX, float endY, float duration, float delay, EasingMode easing, float offsetX,
			float offsetY, ActionListener l) {
		return event(new MoveBy(-1f, -1f, endX, endY, 0, duration, delay, easing, offsetX, offsetY), l);
	}

	public ActionTween moveBy(float endX, float endY, float speed, EasingMode easing, float offsetX, float offsetY) {
		return moveBy(endX, endY, speed, easing, offsetX, offsetY, null);
	}

	public ActionTween moveBy(float endX, float endY, float speed, EasingMode easing, float offsetX, float offsetY,
			ActionListener l) {
		return event(new MoveBy(endX, endY, speed, easing, offsetX, offsetY), l);
	}

	public ActionTween moveBy(float startX, float startY, float endX, float endY, float duration, float delay,
			EasingMode easing, float offsetX, float offsetY) {
		return event(new MoveBy(startX, startY, endX, endY, 0, duration, delay, easing, offsetX, offsetY), null);
	}

	public ActionTween moveBy(float startX, float startY, float endX, float endY, float duration, float delay,
			EasingMode easing, float offsetX, float offsetY, ActionListener l) {
		return event(new MoveBy(startX, startY, endX, endY, 0, duration, delay, easing, offsetX, offsetY), l);
	}

	public ActionTween moveBy(float endX, float endY, EasingMode easing) {
		return event(new MoveBy(endX, endY, easing));
	}

	public ActionTween moveBy(float endX, float endY, EasingMode easing, ActionListener l) {
		return event(new MoveBy(endX, endY, easing), l);
	}

	public ActionTween moveBy(float endX, float endY, float duration, float delay, EasingMode easing) {
		return event(new MoveBy(endX, endY, duration, delay, easing));
	}

	public ActionTween moveBy(float endX, float endY, float duration, float delay, EasingMode easing,
			ActionListener l) {
		return event(new MoveBy(endX, endY, duration, delay, easing), l);
	}

	public ActionTween moveBy(float endX, float endY, float duration, EasingMode easing) {
		return event(new MoveBy(endX, endY, duration, easing));
	}

	public ActionTween moveBy(float endX, float endY, float duration, EasingMode easing, ActionListener l) {
		return event(new MoveBy(endX, endY, duration, easing), l);
	}

	public ActionTween fadeIn(float speed) {
		return fadeTo(ISprite.TYPE_FADE_IN, speed);
	}

	public ActionTween fadeOut(float speed) {
		return fadeTo(ISprite.TYPE_FADE_OUT, speed);
	}

	public ActionTween fadeTo(int fadeMode, float speed) {
		return event(new FadeTo(fadeMode, speed));
	}

	public ActionTween alphaTo(float end) {
		return alphaTo(-1f, end);
	}

	public ActionTween alphaTo(float start, float end) {
		return alphaTo(start, end, EasingMode.Linear);
	}

	public ActionTween alphaTo(float start, float end, EasingMode mode) {
		return alphaTo(start, end, 1f, mode);
	}

	public ActionTween alphaTo(float start, float end, float duration, EasingMode mode) {
		AlphaTo alpha = new AlphaTo(start, end, duration, mode);
		alpha.setDelay(0);
		return event(alpha);
	}

	public ActionTween rotateTo(float angle) {
		return rotateTo(angle, 6f);
	}

	public ActionTween rotateTo(float angle, float speed) {
		return rotateTo(angle, 1f, speed);
	}

	public ActionTween rotateTo(float angle, float diff, float speed) {
		return event(new RotateTo(angle, diff, speed));
	}

	public ActionTween rotateTo(float angle, float diff, float speed, EasingMode easing) {
		return event(new RotateTo(angle, diff, speed, easing));
	}

	public ActionTween rotateTo(float startRotation, float dstAngle, float diffAngle, float duration, float delay,
			EasingMode easing) {
		return event(new RotateTo(startRotation, dstAngle, diffAngle, duration, delay, easing));
	}

	public ActionTween colorTo(LColor end) {
		return colorTo(null, end);
	}

	public ActionTween colorTo(LColor start, LColor end) {
		ColorTo color = new ColorTo(start, end, 1f);
		color.setDelay(0);
		return event(color);
	}

	public ActionTween colorTo(LColor start, LColor end, float delay) {
		return colorTo(start, end, delay, 1f);
	}

	public ActionTween colorTo(LColor start, LColor end, float duration, float delay) {
		ColorTo color = new ColorTo(start, end, duration, delay);
		color.setDelay(0);
		return event(color);
	}

	public ActionTween eventTo(FrameLoopEvent e) {
		EventTo event = new EventTo(e);
		event.setDelay(0);
		return event(event);
	}

	public ActionTween transferTo(float startPos, float endPos, EasingMode mode, boolean controlX, boolean controlY) {
		TransferTo transfer = new TransferTo(startPos, endPos, 1f, mode, controlX, controlY);
		transfer.setDelay(0);
		return event(transfer);
	}

	public ActionTween transferTo(float startPos, float endPos, float duration, EasingMode mode) {
		TransferTo transfer = new TransferTo(startPos, endPos, duration, mode);
		transfer.setDelay(0);
		return event(transfer);
	}

	public ActionTween transferTo(float startPos, float endPos, float duration, EasingMode mode, boolean controlX,
			boolean controlY) {
		TransferTo transfer = new TransferTo(startPos, endPos, duration, mode, controlX, controlY);
		transfer.setDelay(0);
		return event(transfer);
	}

	public ActionTween transferTo(float startPos, float endPos, float duration, float delay, EasingMode mode,
			boolean controlX, boolean controlY) {
		TransferTo transfer = new TransferTo(startPos, endPos, delay, duration, mode, controlX, controlY);
		transfer.setDelay(0);
		return event(transfer);
	}

	public ActionTween shakeTo(float shake) {
		return shakeTo(shake, shake);
	}

	public ActionTween shakeTo(float shakeX, float shakeY) {
		ShakeTo shake = new ShakeTo(shakeX, shakeY);
		shake.setDelay(0);
		return event(shake);
	}

	public ActionTween shakeTo(float shakeX, float shakeY, float duration) {
		ShakeTo shake = new ShakeTo(shakeX, shakeY, duration);
		shake.setDelay(0);
		return event(shake);
	}

	public ActionTween shakeTo(float shakeX, float shakeY, float duration, float delay) {
		ShakeTo shake = new ShakeTo(shakeX, shakeY, duration, delay);
		shake.setDelay(0);
		return event(shake);
	}

	public ActionTween shakeTo(float shakeX, float shakeY, float duration, float delay, EasingMode easing) {
		ShakeTo shake = new ShakeTo(shakeX, shakeY, duration, delay, easing);
		shake.setDelay(0);
		return event(shake);
	}

	public ActionTween scaleTo(float s) {
		return scaleTo(s, s, 0.1f, 1f, EasingMode.Linear);
	}

	public ActionTween scaleTo(float s, float duration, EasingMode mode) {
		return scaleTo(s, s, 0.1f, duration, mode);
	}

	public ActionTween scaleTo(float sx, float sy, float duration) {
		return scaleTo(sx, sy, 0.1f, duration, EasingMode.Linear);
	}

	public ActionTween scaleTo(float sx, float sy, float duration, EasingMode mode) {
		return scaleTo(sx, sy, 0.1f, duration, mode);
	}

	public ActionTween scaleTo(float sx, float sy) {
		return scaleTo(sx, sy, EasingMode.Linear);
	}

	public ActionTween scaleTo(float sx, float sy, float speed, float duration, EasingMode mode) {
		ScaleTo scale = new ScaleTo(sx, sy, duration, mode);
		scale.setDelay(0);
		scale.setSpeed(speed);
		return event(scale);
	}

	public ActionTween scaleBy(float sx, float sy) {
		return scaleBy(sx, sy, 1f);
	}

	public ActionTween scaleBy(float sx, float sy, float duration) {
		return scaleBy(sx, sy, duration, EasingMode.Linear);
	}

	public ActionTween scaleBy(float sx, float sy, float duration, EasingMode mode) {
		ScaleBy scale = new ScaleBy(sx, sy, duration, mode);
		scale.setDelay(0);
		return event(scale);
	}

	public ActionTween showTo(boolean v) {
		ShowTo show = new ShowTo(v);
		show.setDelay(0);
		return event(show);
	}

	public ActionTween arrowTo(float x, float y) {
		return event(new ArrowTo(x, y));
	}

	public ActionTween arrowTo(float x, float y, float g) {
		return event(new ArrowTo(x, y, g));
	}

	public ActionTween arrowTo(float x, float y, float speed, float g, EasingMode easing) {
		return event(new ArrowTo(x, y, speed, g, easing));
	}

	public ActionTween arrowTo(float tx, float ty, float speed, float g) {
		return event(new ArrowTo(tx, ty, speed, g));
	}

	public ActionTween arrowTo(float st, float sy, float tx, float ty, float speed, float g, float duration,
			float delay, EasingMode easing) {
		return event(new ArrowTo(st, sy, tx, ty, speed, g, duration, delay, easing));
	}

	public ActionTween circleTo(int radius, int velocity) {
		return event(new CircleTo(radius, velocity));
	}

	public ActionTween circleTo(float centerX, float cenertY, int radius, int velocity) {
		return circleTo(-1, -1, radius, velocity, 0.1f);
	}

	public ActionTween circleTo(float centerX, float cenertY, int radius, int velocity, float speed) {
		return event(new CircleTo(centerX, cenertY, radius, velocity, speed));
	}

	public ActionTween effectTo(BaseEffect eff) {
		return event(new EffectTo(eff));
	}

	public ActionTween fireTo(ActionBind act, float speed) {
		return event(new FireTo(act, speed));
	}

	public ActionTween fireTo(float endX, float endY, float speed) {
		return event(new FireTo(endX, endY, speed));
	}

	public ActionTween jumpTo(int moveJump, float gravity) {
		return event(new JumpTo(moveJump, gravity));
	}

	/**
	 * <p>
	 * 需要[并行]的缓动动画事件在此注入
	 * </p>
	 * 
	 * 如果要[并行](也就是旋转,变色什么的一起来)进行缓动动画,而非分别进行,请把要演示的ActionEvent注入此类,此类用于同时运行多个ActionEvent
	 * 
	 * @param eves
	 * @return
	 */
	public ActionTween parallelTo(ActionEvent... eves) {
		return event(new ParallelTo(eves));
	}

	/**
	 * <p>
	 * 需要[并行]的缓动动画事件在此注入
	 * </p>
	 * 
	 * 如果要[并行](也就是旋转,变色什么的一起来)进行缓动动画,而非分别进行,请把要演示的ActionEvent注入此类,此类用于同时运行多个ActionEvent
	 * 
	 * @param list
	 * @return
	 */
	public ActionTween parallelTo(TArray<ActionEvent> list) {
		return event(new ParallelTo(list));
	}

	/**
	 * 将此前设置的所有缓动事件转化为并行事件后返回
	 * 
	 * @return
	 */
	public ActionTween parallel() {
		final TArray<ActionEvent> events = getCurrentActionEvents(true);
		if (events != null) {
			return parallelTo(events);
		}
		return this;
	}

	public TArray<ActionEvent> getCurrentActionEvents() {
		return getCurrentActionEvents(false);
	}

	private TArray<ActionEvent> getCurrentActionEvents(boolean clear) {
		if (_actionEvents == null || _actionEvents.size() == 0) {
			return null;
		}
		final TArray<ActionEvent> events = new TArray<ActionEvent>(_actionEvents.size());
		for (; _actionEvents.hashNext();) {
			ActionEvent e = _actionEvents.next();
			if (e != null) {
				events.add(e);
			}
		}
		_actionEvents.stopNext();
		if (clear) {
			_actionEvents.clear();
		}
		return events;
	}

	public ActionTween updateTo(EventAction e) {
		return event(new UpdateTo(e));
	}

	public ActionTween waitTo(ActionUpdate au) {
		return event(new WaitTo(au));
	}

	public ActionTween transformPos(float x, float y) {
		return event(TransformTo.pos(x, y));
	}

	public ActionTween transformScale(float scaleX, float scaleY) {
		return event(TransformTo.scale(scaleX, scaleY));
	}

	public ActionTween transformAlpha(float newAlpha) {
		return event(TransformTo.alpha(newAlpha));
	}

	public ActionTween transformRotation(float newRotation) {
		return event(TransformTo.rotation(newRotation));
	}

	public ActionTween transformColor(LColor newColor) {
		return event(TransformTo.color(newColor));
	}

	public ActionTween waitTo(BooleanValue bv) {
		return event(new WaitTo(bv));
	}

	public ActionTween moveRoundTo(float angle, float radius, Vector2f centerPoint, EasingMode easing) {
		return event(new MoveRoundTo(angle, radius, centerPoint, easing));
	}

	public ActionTween moveRoundTo(float angle, float radius, Vector2f centerPoint, float duration, EasingMode easing) {
		return event(new MoveRoundTo(angle, radius, centerPoint, duration, easing));
	}

	public ActionTween moveRoundTo(float startAngle, float angle, float startRadius, float radius, Vector2f centerPoint,
			Vector2f startPoint, float duration, float delay, EasingMode easing) {
		return event(new MoveRoundTo(startAngle, angle, startRadius, radius, centerPoint, startPoint, duration, delay,
				easing));
	}

	public ActionTween moveOvalTo(float angle, float width, float height, Vector2f centerPoint, float duration,
			EasingMode easing) {
		return event(new MoveOvalTo(0, angle, width, height, centerPoint, duration, easing));
	}

	public ActionTween moveOvalTo(float startAngle, float angle, float width, float height, Vector2f centerPoint,
			float duration, EasingMode easing) {
		return event(new MoveOvalTo(startAngle, angle, width, height, centerPoint, duration, easing));
	}

	public ActionTween moveOvalTo(float startAngle, float angle, float width, float height, Vector2f centerPoint,
			Vector2f startPoint, float duration, float delay, EasingMode easing) {
		return event(
				new MoveOvalTo(startAngle, angle, width, height, centerPoint, startPoint, duration, delay, easing));
	}

	public ActionTween bezierBy(float duration, Bezier b) {
		return event(new BezierBy(duration, b));
	}

	public ActionTween bezierBy(float sx, float sy, float duration, Bezier b) {
		return event(new BezierBy(sx, sy, duration, b));
	}

	public ActionTween bezierBy(float sx, float sy, float duration, EasingMode mode, Bezier b) {
		return event(new BezierBy(sx, sy, duration, mode, b));
	}

	public ActionTween bezierTo(float duration, Bezier b) {
		return event(new BezierTo(duration, b));
	}

	public ActionTween bezierTo(float sx, float sy, float duration, Bezier b) {
		return event(new BezierTo(sx, sy, duration, b));
	}

	public ActionTween bezierTo(float sx, float sy, float duration, EasingMode mode, Bezier b) {
		return event(new BezierTo(sx, sy, duration, mode, b));
	}

	public ActionTween flipXNoneEffect(boolean x) {
		return event(new FlipXTo(EasingMode.Linear, 0f, x, false));
	}

	public ActionTween flipYNoneEffect(boolean x) {
		return event(new FlipYTo(EasingMode.Linear, 0f, x, false));
	}

	public ActionTween flipX(EasingMode e, float d, boolean x) {
		return event(new FlipXTo(e, d, x, true));
	}

	public ActionTween flipY(EasingMode e, float d, boolean y) {
		return event(new FlipYTo(e, d, y, true));
	}

	public ActionTween flipX(float d, boolean x) {
		return event(new FlipXTo(d, x));
	}

	public ActionTween flipY(float d, boolean y) {
		return event(new FlipYTo(d, y));
	}

	public ActionTween flipX(boolean x) {
		return event(new FlipXTo(x));
	}

	public ActionTween flipY(boolean y) {
		return event(new FlipYTo(y));
	}

	public ActionTween flipX() {
		if (_target != null && _target instanceof Flip<?>) {
			Flip<?> flip = (Flip<?>) _target;
			return event(new FlipXTo(!flip.isFlipX()));
		}
		return this;
	}

	public ActionTween flipY() {
		if (_target != null && _target instanceof Flip<?>) {
			Flip<?> flip = (Flip<?>) _target;
			return event(new FlipYTo(!flip.isFlipY()));
		}
		return this;
	}

	public ActionTween removeActionsTo(ActionBind bind) {
		return event(new RemoveActionsTo(bind));
	}

	public ActionTween removeActionsTo() {
		return removeActionsTo(null);
	}

	/**
	 * 监听所有指定名称的已注入事件
	 * 
	 * @param name
	 * @param listener
	 * @return
	 */
	public ActionTween listenTags(Object tag, ActionListener listener) {
		if (_actionEvents == null || tag == null) {
			return this;
		}
		for (; _actionEvents.hashNext();) {
			ActionEvent tmp = _actionEvents.next();
			if (tmp != null) {
				if (tag.equals(tmp.tag) || tmp.tag == tag) {
					tmp.setActionListener(listener);
				}
			}
		}
		_actionEvents.stopNext();
		return this;
	}

	/**
	 * 监听所有指定名称的已注入事件
	 * 
	 * @param name
	 * @param listener
	 * @return
	 */
	public ActionTween listenNames(String name, ActionListener listener) {
		if (_actionEvents == null || name == null) {
			return this;
		}
		String findName = name.trim().toLowerCase();
		for (; _actionEvents.hashNext();) {
			ActionEvent tmp = _actionEvents.next();
			if (tmp != null) {
				if (findName.equals(tmp.getName())) {
					tmp.setActionListener(listener);
				}
			}
		}
		_actionEvents.stopNext();
		return this;
	}

	/**
	 * 停止所有指定名的动画
	 * 
	 * @param name
	 * @return
	 */
	public ActionTween killNames(String name) {
		if (_actionEvents == null || name == null) {
			return this;
		}
		String findName = name.trim().toLowerCase();
		for (; _actionEvents.hashNext();) {
			ActionEvent tmp = _actionEvents.next();
			if (tmp != null) {
				if (findName.equals(tmp.getName())) {
					tmp.kill();
				}
			}
		}
		_actionEvents.stopNext();
		return this;
	}

	/**
	 * 停止所有包含指定标记的动画
	 * 
	 * @param tag
	 * @return
	 */
	public ActionTween killTags(Object tag) {
		if (_actionEvents == null || tag == null) {
			return this;
		}
		for (; _actionEvents.hashNext();) {
			ActionEvent tmp = _actionEvents.next();
			if (tmp != null) {
				if (tag.equals(tmp.tag) || tmp.tag == tag) {
					tmp.kill();
				}
			}
		}
		_actionEvents.stopNext();
		return this;
	}

	public ActionTween loop(int count) {
		return loop(count, false);
	}

	public ActionTween loop(int count, boolean reverse) {
		if (_actionEvents == null) {
			return this;
		}
		if (count < 1) {
			return this;
		}
		if (count == 1) {
			count++;
		}
		ActionEvent e = null;
		Array<ActionEvent> tmps = new Array<ActionEvent>();
		for (int i = 0; i < count - 1; i++) {
			for (; _actionEvents.hashNext();) {
				ActionEvent tmp = _actionEvents.next();
				if (tmp != null) {
					e = tmp;
				}
				tmps.add(reverse ? e.reverse() : e.cpy());
			}
			_actionEvents.stopNext();
		}
		_actionEvents.addAll(tmps);
		return this;
	}

	public ActionTween loopLast(int count) {
		return loopLast(count, false);
	}

	public ActionTween loopLast(int count, boolean reverse) {
		if (_actionEvents == null) {
			return this;
		}
		if (count < 1) {
			return this;
		}
		if (count == 1) {
			count++;
		}
		Array<ActionEvent> tmps = new Array<ActionEvent>();
		for (int i = 0; i < count - 1; i++) {
			tmps.add(reverse ? _actionEvents.last().reverse() : _actionEvents.last().cpy());
		}
		_actionEvents.addAll(tmps);
		return this;
	}

	public TArray<ActionEvent> getActionEvents() {
		if (_actionEvents == null) {
			return new TArray<ActionEvent>(0);
		}
		return new TArray<ActionEvent>(_actionEvents);
	}

	@Override
	public ActionTween delay(float d) {
		super.delay(delay);
		if (_actionEvents != null && d > 0) {
			DelayTo delay = new DelayTo(d);
			delay.setDelay(0);
			return event(delay);
		} else {
			return this;
		}
	}

	public ActionTween repeat(float time) {
		return repeat(1, time);
	}

	@Override
	public ActionTween repeat(int count, float time) {
		super.repeat(count, time);
		if (_actionEvents == null) {
			return this;
		}
		_isRepeat = true;
		boolean update = count > 1;
		ReplayTo replay = new ReplayTo(null, update);
		if (update) {
			replay.count = count;
		}
		event(replay);
		return delay(time);
	}

	@Override
	public ActionTween repeatBackward(int count, float time) {
		super.repeatBackward(count, time);
		if (_actionEvents == null) {
			return this;
		}
		_isRepeat = true;
		boolean update = count > 1;
		ReplayTo replay = new ReplayTo(null, update);
		if (update) {
			replay.count = count;
		}
		event(replay);
		return delay(time);
	}

	/**
	 * 自定义事件(连续动画)请在此处注入
	 * 
	 * @param event
	 * @return
	 */
	public ActionTween event(ActionEvent event) {
		return event(event, null);
	}

	/**
	 * 注入缓动动画(连续动画)事件(自定义事件也请在此处注入)
	 * 
	 * @param event
	 * @param listener
	 * @return
	 */
	public ActionTween event(ActionEvent event, ActionListener listener) {
		if (_actionEvents == null) {
			_actionEvents = new Array<ActionEvent>();
		}
		if (event != null) {
			_actionEvents.add(event);
			if (listener != null) {
				event.setActionListener(listener);
			}
		}
		return this;
	}

	public boolean notEvent() {
		return _actionEvents == null || _actionEvents.size() == 0;
	}

	public int countEvent() {
		return _actionEvents == null ? 0 : _actionEvents.size();
	}

	public boolean isRunning() {
		return (!notEvent() || (this._currentActionEvent != null && !this._currentActionEvent.isComplete()));
	}

	public ActionEvent getCurrentActionEvent() {
		return this._currentActionEvent;
	}

	@Override
	protected void reset() {
		super.reset();
		_target = null;
		_actionEvents = null;
		_currentActionEvent = null;
		_type = -1;
		_equation = null;
		_path = null;
		_isFrom = _isRelative = false;
		_combinedAttrsSize = _funPointsSize = 0;
		if (_accessorBuffer.length != _combinedAttrsLimit) {
			_accessorBuffer = new float[_combinedAttrsLimit];
		}
		if (_pathBuffer.length != (2 + _funPointsLimit) * _combinedAttrsLimit) {
			_pathBuffer = new float[(2 + _funPointsLimit) * _combinedAttrsLimit];
		}
	}

	private void setup(ActionBind target, int tweenType, float duration) {
		if (duration < 0) {
			throw new LSysException("Duration can't be negative .");
		}
		this._target = target;
		this._type = tweenType;
		this.duration = duration;
	}

	public ActionTween delayGlobal(float delay) {
		ActionControl.setDelay(MathUtils.max(Duration.ofS(delay), 8));
		return this;
	}

	public ActionTween ease(Easing ease) {
		this._equation = ease;
		return this;
	}

	public ActionTween target(float targetValue) {
		_targetValues[0] = targetValue;
		return this;
	}

	public ActionTween target(float targetValue1, float targetValue2) {
		_targetValues[0] = targetValue1;
		_targetValues[1] = targetValue2;
		return this;
	}

	public ActionTween target(float targetValue1, float targetValue2, float targetValue3) {
		_targetValues[0] = targetValue1;
		_targetValues[1] = targetValue2;
		_targetValues[2] = targetValue3;
		return this;
	}

	public ActionTween target(float... ts) {
		if (ts.length > _combinedAttrsLimit) {
			return this;
		}
		System.arraycopy(ts, 0, this._targetValues, 0, ts.length);
		return this;
	}

	public ActionTween targetRelative(float targetValue) {
		_isRelative = true;
		_targetValues[0] = isInitialized() ? targetValue + _startValues[0] : targetValue;
		return this;
	}

	public ActionTween targetRelative(float targetValue1, float targetValue2) {
		_isRelative = true;
		_targetValues[0] = isInitialized() ? targetValue1 + _startValues[0] : targetValue1;
		_targetValues[1] = isInitialized() ? targetValue2 + _startValues[1] : targetValue2;
		return this;
	}

	public ActionTween targetRelative(float targetValue1, float targetValue2, float targetValue3) {
		_isRelative = true;
		_targetValues[0] = isInitialized() ? targetValue1 + _startValues[0] : targetValue1;
		_targetValues[1] = isInitialized() ? targetValue2 + _startValues[1] : targetValue2;
		_targetValues[2] = isInitialized() ? targetValue3 + _startValues[2] : targetValue3;
		return this;
	}

	public ActionTween targetRelative(float... ts) {
		if (ts.length > _combinedAttrsLimit) {
			return this;
		}
		for (int i = 0; i < ts.length; i++) {
			this._targetValues[i] = isInitialized() ? ts[i] + _startValues[i] : ts[i];
		}

		_isRelative = true;
		return this;
	}

	public ActionTween funPoint(float targetValue) {
		if (_funPointsSize == _funPointsLimit) {
			return this;
		}
		_funPoints[_funPointsSize] = targetValue;
		_funPointsSize += 1;
		return this;
	}

	public ActionTween funPoint(float targetValue1, float targetValue2) {
		if (_funPointsSize == _funPointsLimit) {
			return this;
		}
		_funPoints[_funPointsSize * 2] = targetValue1;
		_funPoints[_funPointsSize * 2 + 1] = targetValue2;
		_funPointsSize += 1;
		return this;
	}

	public ActionTween funPoint(float targetValue1, float targetValue2, float targetValue3) {
		if (_funPointsSize == _funPointsLimit) {
			return this;
		}
		_funPoints[_funPointsSize * 3] = targetValue1;
		_funPoints[_funPointsSize * 3 + 1] = targetValue2;
		_funPoints[_funPointsSize * 3 + 2] = targetValue3;
		_funPointsSize += 1;
		return this;
	}

	public ActionTween funPoint(float... ts) {
		if (_funPointsSize == _funPointsLimit) {
			return this;
		}
		System.arraycopy(ts, 0, _funPoints, _funPointsSize * ts.length, ts.length);
		_funPointsSize += 1;
		return this;
	}

	public ActionTween path(ActionPath path) {
		this._path = path;
		return this;
	}

	public ActionBind getTarget() {
		return _target;
	}

	public int getTypeCode() {
		return _type;
	}

	public Easing getEasing() {
		return _equation;
	}

	public float[] getTargetValues() {
		return _targetValues;
	}

	public int getCombinedAttributesCount() {
		return _combinedAttrsSize;
	}

	@Override
	public void free() {
		super.free();
		_POOLS.free(this);
		ActionControl.get().removeAllActions(_target);
	}

	@Override
	protected void initializeOverride() {
		if (_target == null) {
			return;
		}

		ActionType.getValues(_target, _type, _startValues);

		for (int i = 0; i < _combinedAttrsSize; i++) {
			_targetValues[i] += _isRelative ? _startValues[i] : 0;

			for (int ii = 0; ii < _funPointsSize; ii++) {
				_funPoints[ii * _combinedAttrsSize + i] += _isRelative ? _startValues[i] : 0;
			}

			if (_isFrom) {
				float tmp = _startValues[i];
				_startValues[i] = _targetValues[i];
				_targetValues[i] = tmp;
			}
		}
	}

	@Override
	protected boolean actionEventOver() {
		if (_actionEvents == null) {
			return true;
		}
		if (_actionEvents != null) {
			if (_currentActionEvent != null && !_currentActionEvent.isComplete()) {
				return false;
			} else if (_currentActionEvent != null && _currentActionEvent.isComplete()) {
				if (_repeatList == null) {
					_repeatList = new Array<ActionEvent>();
				}
				if (!(_currentActionEvent instanceof ReplayTo)) {
					_repeatList.add(_currentActionEvent.reverse());
				}
			}
			ActionEvent event = _actionEvents.first();
			if (event != _currentActionEvent && event != null) {
				_actionEvents.remove(0);
				if (_isRepeat) {
					if (event instanceof ReplayTo && _repeatList != null && _repeatList.size() > 0) {
						ReplayTo replayTo = ((ReplayTo) event);
						int size = replayTo.count - 1;
						if (size > 0) {
							for (int i = 0; i < size; i++) {
								_repeatList.addFront(new ReplayTo(null));
								_repeatList.addFront(new DelayTo(0));
							}
						}
						replayTo.set(_repeatList);
						_repeatList.clear();
					}
				}
				ActionControl.get().addAction(event, _target);
				_currentActionEvent = event;
			}
		}
		if (_currentActionEvent != null && !_currentActionEvent.isComplete()) {
			return false;
		}
		return (_actionEvents == null || _actionEvents.size() == 0);
	}

	@Override
	protected void update(int step, int lastStep, boolean isIterationStep, float delta) {
		if (_target == null || _equation == null) {
			return;
		}
		final float epsilon = MathUtils.EPSILON;
		if (!isIterationStep && step > lastStep) {
			ActionType.setValues(_target, _type, isReverse(lastStep) ? _startValues : _targetValues);
			return;
		}

		if (!isIterationStep && step < lastStep) {
			ActionType.setValues(_target, _type, isReverse(lastStep) ? _targetValues : _startValues);
			return;
		}

		if (duration < epsilon && delta > -epsilon) {
			ActionType.setValues(_target, _type, isReverse(step) ? _targetValues : _startValues);
			return;
		}

		if (duration < epsilon && delta < epsilon) {
			ActionType.setValues(_target, _type, isReverse(step) ? _startValues : _targetValues);
			return;
		}

		float time = isReverse(step) ? duration - getCurrentTime() : getCurrentTime();

		float t = _equation.apply(time, duration, false);

		if (_funPointsSize == 0 || _path == null) {
			for (int i = 0; i < _combinedAttrsSize; i++) {
				_accessorBuffer[i] = _startValues[i] + t * (_targetValues[i] - _startValues[i]);
			}
		} else {
			for (int i = 0; i < _combinedAttrsSize; i++) {
				_pathBuffer[0] = _startValues[i];
				_pathBuffer[1 + _funPointsSize] = _targetValues[i];
				for (int ii = 0; ii < _funPointsSize; ii++) {
					_pathBuffer[ii + 1] = _funPoints[ii * _combinedAttrsSize + i];
				}

				_accessorBuffer[i] = _path.compute(t, _pathBuffer, _funPointsSize + 2);
			}
		}

		ActionType.setValues(_target, _type, _accessorBuffer);
	}

	@Override
	protected void forceStartValues() {
		if (_target == null) {
			return;
		}
		ActionType.setValues(_target, _type, _startValues);
	}

	@Override
	protected void forceEndValues() {
		if (_target == null) {
			return;
		}
		ActionType.setValues(_target, _type, _targetValues);
	}

	@Override
	public ActionTween build() {
		if (_target == null) {
			return this;
		}
		_combinedAttrsSize = ActionType.getValues(_target, _type, _accessorBuffer);
		return this;
	}

	@Override
	protected boolean containsTarget(ActionBind target) {
		return this._target == target;
	}

	@Override
	protected boolean containsTarget(ActionBind target, int tweenType) {
		return this._target == target && this._type == tweenType;
	}

	public float getInitMoveSpeed() {
		return _initMoveSpeed;
	}

	public ActionTween setInitMoveSpeed(float m) {
		this._initMoveSpeed = m;
		return this;
	}

	@Override
	public String toString() {
		if (_actionEvents == null) {
			return "ActionTween []";
		}
		StringKeyValue builder = new StringKeyValue("ActionTween");
		for (; _actionEvents.hashNext();) {
			ActionEvent eve = _actionEvents.next();
			if (eve != null) {
				builder.addValue(eve.toString());
				builder.newLine();
			}
		}
		_actionEvents.stopNext();
		return builder.toString();
	}

}
