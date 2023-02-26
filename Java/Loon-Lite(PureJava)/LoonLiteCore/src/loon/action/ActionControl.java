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

	private final Array<ActionBindData> bindDatas;

	private final Actions actions;

	private final LTimer delayTimer;

	private boolean pause;

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
		if (pause || actions.getCount() == 0) {
			return;
		}
		if (delayTimer.action(elapsedTime)) {
			actions.update(elapsedTime);
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
		bindDatas.add(data);
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
	 * @param resetData
	 *            true时还原数据,false时仅读取出历史记录数据
	 * @return
	 */
	public TArray<ActionBindData> loadActionData(ActionBind bind, boolean resetData) {
		TArray<ActionBindData> list = new TArray<ActionBindData>();
		for (; bindDatas.hashNext();) {
			ActionBindData data = bindDatas.next();
			if (data != null && data.getObject().equals(bind)) {
				if (resetData) {
					data.resetInitData();
				}
				list.add(data);
			}
		}
		bindDatas.stopNext();
		return list;
	}

	/**
	 * 保存当前所有加入缓动动画事件的动作对象基本动作状态
	 * 
	 * @return
	 */
	public ActionControl saveAllActionData() {
		TArray<ActionBind> list = actions.keys();
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
		for (; bindDatas.hashNext();) {
			ActionBindData data = bindDatas.next();
			data.resetInitData();
		}
		bindDatas.stopNext();
		return this;
	}

	public ActionControl clearActionData() {
		bindDatas.clear();
		return this;
	}

	public ActionBindData popActionData() {
		return bindDatas.pop();
	}

	public ActionBindData peekActionData() {
		return bindDatas.peek();
	}

	public ActionBindData lastActionData() {
		return bindDatas.last();
	}

	public ActionBindData firstActionData() {
		return bindDatas.first();
	}

	public final void delay(long d) {
		delayTimer.setDelay(d);
	}

	public final LTimer getTimer() {
		return delayTimer;
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
		actions = new Actions();
		delayTimer = new LTimer(0);
		bindDatas = new Array<ActionBindData>();
		pause = false;
	}

	public void addAction(ActionEvent action, ActionBind obj, boolean paused) {
		actions.addAction(action, obj, paused);
	}

	public void addAction(ActionEvent action, ActionBind obj) {
		addAction(action, obj, false);
	}

	public void removeAllActions(ActionBind actObject) {
		actions.removeAllActions(actObject);
	}

	public boolean containsKey(ActionBind actObject) {
		return actions.containsKey(actObject);
	}

	public boolean isCompleted(ActionBind actObject) {
		if (actObject == null) {
			return true;
		}
		return actions.isCompleted(actObject);
	}

	public int getCount() {
		return actions.getCount();
	}

	public boolean stopNames(ActionBind k, String name) {
		return actions.stopNames(k, name);
	}

	public boolean stopTags(ActionBind k, Object tag) {
		return actions.stopTags(k, tag);
	}

	public void removeAction(Object tag, ActionBind actObject) {
		actions.removeAction(tag, actObject);
	}

	public void removeAction(ActionEvent action) {
		actions.removeAction(action);
	}

	public ActionEvent getAction(Object tag, ActionBind actObject) {
		return actions.getAction(tag, actObject);
	}

	public void stop(ActionBind actObject) {
		actions.stop(actObject);
	}

	public void start(ActionBind actObject) {
		actions.start(actObject);
	}

	public void paused(boolean pause, ActionBind actObject) {
		actions.paused(pause, actObject);
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public void clear() {
		actions.clear();
	}

	public void stop() {
		clear();
		pause();
	}

	public void pause() {
		pause = true;
	}

	@Override
	public void close() {
		actions.clear();
		bindDatas.clear();
	}

}
