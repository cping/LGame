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

import loon.LRelease;
import loon.utils.Array;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

/** 全局生效的动作控制类（在Loon中任何场景都适用），所有实现了ActionBind的类都可以被此类控制 **/
public class ActionControl implements LRelease {

	public static final ActionLinear LINEAR = new ActionLinear();

	public static final ActionSmooth SMOOTH = new ActionSmooth();

	private static ActionControl instanceAction;

	private final Array<ActionBindData> _currentBindDatas;

	private final Actions _currentActions;

	private final LTimer _delayTimer;

	private LRelease _dispose;

	private boolean _pause;

	public static void freeStatic() {
		instanceAction = null;
	}

	/**
	 * 构建一个单独的缓动动画控制器
	 * 
	 * @return
	 */
	public final static ActionControl make() {
		return new ActionControl();
	}

	/**
	 * 获得缓动动画控制器实例
	 */
	public static final ActionControl get() {
		if (instanceAction == null) {
			synchronized (ActionControl.class) {
				if (instanceAction == null) {
					instanceAction = new ActionControl();
				}
			}
		}
		return instanceAction;
	}

	/**
	 * 调用缓动动画事件循环
	 */
	public final void call(long elapsedTime) {
		if (_pause) {
			return;
		}
		final boolean isEmpty = (_currentActions.getCount() == 0);
		if (isEmpty) {
			if (_currentActions.isStarted()) {
				if (_dispose != null) {
					_dispose.close();
				}
				_currentActions.resetAction();
			}
			return;
		}
		if (_delayTimer.action(elapsedTime)) {
			_currentActions.update(elapsedTime);
		}
	}

	/**
	 * 保存指定的缓动动画对象基本动作
	 * 
	 * @param bind
	 * @return
	 */
	public ActionBindData saveActionData(ActionBind bind) {
		ActionBindData data = new ActionBindData(bind);
		data.save();
		_currentBindDatas.add(data);
		return data;
	}

	/**
	 * 获得指定的缓动动画对象已保存的历史动作
	 * 
	 * @param bind
	 * @return
	 */
	public TArray<ActionBindData> getActionData(ActionBind bind) {
		return loadActionData(bind, false);
	}

	/**
	 * 读取指定缓动动画对象已保存的历史动作
	 * 
	 * @param bind
	 * @return
	 */
	public TArray<ActionBindData> loadActionData(ActionBind bind) {
		return loadActionData(bind, true);
	}

	/**
	 * 读取指定缓动动画对象已保存的历史动作
	 * 
	 * @param bind
	 * @param resetData true时还原数据,false时仅读取出历史记录数据
	 * @return
	 */
	public TArray<ActionBindData> loadActionData(ActionBind bind, boolean resetData) {
		TArray<ActionBindData> list = new TArray<ActionBindData>();
		for (; _currentBindDatas.hashNext();) {
			ActionBindData data = _currentBindDatas.next();
			if (data != null && data.getObject().equals(bind)) {
				if (resetData) {
					data.resetInitData();
				}
				list.add(data);
			}
		}
		_currentBindDatas.stopNext();
		return list;
	}

	/**
	 * 保存当前所有加入缓动动画事件的动作对象基本动作状态
	 * 
	 * @return
	 */
	public ActionControl saveAllActionData() {
		TArray<ActionBind> list = _currentActions.keys();
		for (int i = 0; i < list.size; i++) {
			saveActionData(list.get(i));
		}
		return this;
	}

	/**
	 * 读取(还原)当前所有加入基本动作事件的缓动动作对象动作状态
	 * 
	 * @return
	 */
	public ActionControl loadAllActionData() {
		for (; _currentBindDatas.hashNext();) {
			ActionBindData data = _currentBindDatas.next();
			data.resetInitData();
		}
		_currentBindDatas.stopNext();
		return this;
	}

	public ActionControl clearActionData() {
		_currentBindDatas.clear();
		return this;
	}

	public ActionBindData popActionData() {
		return _currentBindDatas.pop();
	}

	public ActionBindData peekActionData() {
		return _currentBindDatas.peek();
	}

	public ActionBindData lastActionData() {
		return _currentBindDatas.last();
	}

	public ActionBindData firstActionData() {
		return _currentBindDatas.first();
	}

	public final ActionControl delay(long d) {
		_delayTimer.setDelay(d);
		return this;
	}

	public final LTimer getTimer() {
		return _delayTimer;
	}

	public static final void setDelay(long delay) {
		if (instanceAction != null) {
			instanceAction.delay(delay);
		}
	}

	public static final void update(long elapsedTime) {
		if (instanceAction != null) {
			instanceAction.call(elapsedTime);
		}
	}

	private ActionControl() {
		_currentActions = new Actions();
		_delayTimer = new LTimer(0);
		_currentBindDatas = new Array<ActionBindData>();
		_pause = false;
	}

	public ActionControl addAction(ActionEvent action, ActionBind obj, boolean paused) {
		_currentActions.addAction(action, obj, paused);
		return this;
	}

	public ActionControl addAction(ActionEvent action, ActionBind obj) {
		addAction(action, obj, false);
		return this;
	}

	public ActionControl removeAllActions(ActionBind actObject) {
		_currentActions.removeAllActions(actObject);
		return this;
	}

	public boolean containsKey(ActionBind actObject) {
		return _currentActions.containsKey(actObject);
	}

	public boolean isCompleted(ActionBind actObject) {
		if (actObject == null) {
			return true;
		}
		return _currentActions.isCompleted(actObject);
	}

	public int getCount() {
		return _currentActions.getCount();
	}

	public boolean stopNames(ActionBind k, String name) {
		return _currentActions.stopNames(k, name);
	}

	public boolean stopTags(ActionBind k, Object tag) {
		return _currentActions.stopTags(k, tag);
	}

	public ActionControl removeAction(Object tag, ActionBind actObject) {
		_currentActions.removeAction(tag, actObject);
		return this;
	}

	public ActionControl removeAction(ActionEvent action) {
		_currentActions.removeAction(action);
		return this;
	}

	public ActionEvent getAction(Object tag, ActionBind actObject) {
		return _currentActions.getAction(tag, actObject);
	}

	public ActionControl stop(ActionBind actObject) {
		_currentActions.stop(actObject);
		return this;
	}

	public ActionControl start(ActionBind actObject) {
		_currentActions.start(actObject);
		return this;
	}

	public ActionControl paused(boolean pause, ActionBind actObject) {
		_currentActions.paused(pause, actObject);
		return this;
	}

	public boolean isPause() {
		return _pause;
	}

	public ActionControl setPause(boolean pause) {
		this._pause = pause;
		return this;
	}

	public ActionControl clear() {
		_currentActions.clear();
		return this;
	}

	public ActionControl stop() {
		clear();
		pause();
		return this;
	}

	public ActionControl pause() {
		_pause = true;
		return this;
	}

	public ActionControl dispose(LRelease dispose) {
		this._dispose = dispose;
		return this;
	}

	@Override
	public void close() {
		_currentActions.clear();
		_currentBindDatas.clear();
	}

}
