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
import loon.action.ActionCallback;
import loon.action.ActionControl;
import loon.action.ActionEvent;
import loon.action.ActionScript;
import loon.action.ActionTween;
import loon.action.map.PathMove;
import loon.action.sprite.CanvasPlayer;
import loon.canvas.Canvas;
import loon.canvas.LColor;
import loon.events.ActionUpdate;
import loon.events.Updateable;
import loon.font.LFont;
import loon.font.TextLayout;
import loon.geom.BooleanValue;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.processes.GameProcess;
import loon.utils.processes.GameProcessType;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.processes.TimeLineProcess;
import loon.utils.processes.WaitProcess;
import loon.utils.timer.LTimerContext;

/**
 * 组件动作用工具类,主要用来处理一些和ActionBind相关的事物
 *
 */
public class PlayerUtils extends Director {

	/**
	 * 添加一个ActionUpdate进程到游戏,当completed为true时销毁
	 * 
	 * @param update
	 */
	public final static GameProcess addProcess(final ActionUpdate update) {
		return addProcess(update, 0);
	}

	/**
	 * 添加一个ActionUpdate进程到游戏,以指定延迟时间刷新,当completed为true时销毁
	 * 
	 * @param update
	 * @param delay
	 */
	public final static GameProcess addProcess(final ActionUpdate update, final long delay) {
		if (update == null) {
			return null;
		}
		RealtimeProcess process = new RealtimeProcess(delay) {

			@Override
			public void run(LTimerContext time) {
				if (update.completed()) {
					kill();
				}
				update.action(time);
			}
		};
		process.setProcessType(GameProcessType.Progress);
		RealtimeProcessManager.get().addProcess(process);
		return process;
	}

	/**
	 * 添加一个GameProcess进程到游戏,不kill(或者通过RealtimeProcessManager.get()注销)则一直存在
	 * 
	 * @param process
	 */
	public final static GameProcess addProcess(GameProcess process) {
		if (process == null) {
			return process;
		}
		RealtimeProcessManager.get().addProcess(process);
		return process;
	}

	/**
	 * 创建一个TimeLine进程
	 * 
	 * @param loop
	 * @return
	 */
	public final static TimeLineProcess createTimeLineProcess(int loop) {
		TimeLineProcess process = new TimeLineProcess(loop);
		addProcess(process);
		return process;
	}

	/**
	 * 创建一个TimeLine进程
	 * 
	 * @return
	 */
	public final static TimeLineProcess createTimeLineProcess() {
		return createTimeLineProcess(-1);
	}

	/**
	 * 查看GameProcess是否存在
	 * 
	 * @param process
	 * @return
	 */
	public final static boolean containsProcess(GameProcess process) {
		if (process == null) {
			return false;
		}
		return RealtimeProcessManager.get().containsProcess(process);
	}

	/**
	 * 删除一个GameProcess
	 * 
	 * @param process
	 */
	public final static TArray<GameProcess> removeProcess(GameProcess process) {
		if (process == null) {
			return new TArray<GameProcess>();
		}
		return removeProcess(process.getId());
	}

	/**
	 * 删除一个指定id的GameProcess
	 * 
	 * @param id
	 */
	public final static TArray<GameProcess> removeProcess(String id) {
		return RealtimeProcessManager.get().delete(id);
	}

	/**
	 * 删除一个[包含]指定id(比如删1则100,1,11之类也会消失,有1就没)的GameProcess
	 * 
	 * @param id
	 */
	public final static TArray<GameProcess> deleteIndex(String id) {
		return RealtimeProcessManager.get().deleteIndex(id);
	}

	/**
	 * 获得指定id的GameProcess
	 * 
	 * @param id
	 * @return
	 */
	public final static TArray<GameProcess> find(String id) {
		return RealtimeProcessManager.get().find(id);
	}

	/**
	 * 获得指定type的GameProcess
	 * 
	 * @param pt
	 * @return
	 */
	public final static TArray<GameProcess> find(GameProcessType pt) {
		return RealtimeProcessManager.get().find(pt);
	}

	/**
	 * 间隔指定时间后，异步执行Updateable中内容(默认仅执行一次)
	 * 
	 * @param delay
	 * @param update
	 * @return
	 */
	public final static WaitProcess asynWait(final long delay, final Updateable update) {
		WaitProcess process = new WaitProcess(delay, update);
		RealtimeProcessManager.get().addProcess(process);
		return process;
	}

	/**
	 * 异步执行Updateable中内容(默认仅执行一次)
	 * 
	 * @param update
	 * @return
	 */
	public final static WaitProcess asynWait(final Updateable update) {
		return asynWait(60, update);
	}

	public final static BooleanValue waitGame(final Updateable update) {
		return asynWait(update).get();
	}

	public final static BooleanValue waitGame(long time, Updateable update) {
		return asynWait(time, update).get();
	}

	public final static void addAction(ActionEvent e, ActionBind act) {
		ActionControl.get().addAction(e, act);
	}

	public final static void removeAction(ActionEvent e) {
		ActionControl.get().removeAction(e);
	}

	public final static void removeAction(Object tag, ActionBind act) {
		ActionControl.get().removeAction(tag, act);
	}

	public final static void removeAllActions(ActionBind act) {
		ActionControl.get().removeAllActions(act);
	}

	public final static void stop(ActionBind act) {
		ActionControl.get().stop(act);
	}

	public final static void start(ActionBind act) {
		ActionControl.get().start(act);
	}

	public final static void paused(boolean pause, ActionBind act) {
		ActionControl.get().paused(pause, act);
	}

	public final static ActionTween to(ActionBind target, int tweenType, float duration) {
		return to(target, tweenType, duration, true);
	}

	public final static ActionTween to(ActionBind target, int tweenType, float duration, boolean removeActions) {
		if (removeActions) {
			removeAllActions(target);
		}
		return ActionTween.to(target, tweenType, duration);
	}

	public final static ActionTween from(ActionBind target, int tweenType, float duration) {
		return from(target, tweenType, duration, true);
	}

	public final static ActionTween from(ActionBind target, int tweenType, float duration, boolean removeActions) {
		if (removeActions) {
			removeAllActions(target);
		}
		return ActionTween.from(target, tweenType, duration);
	}

	public final static ActionTween get(ActionBind target) {
		return set(target, -1, false);
	}

	public final static ActionTween get(ActionBind target, int tweenType) {
		return set(target, tweenType, false);
	}

	public final static ActionTween set(ActionBind target, int tweenType, boolean removeActions) {
		if (removeActions) {
			removeAllActions(target);
		}
		return ActionTween.set(target, tweenType);
	}

	public final static ActionTween set(ActionBind target) {
		return set(target, true);
	}

	public final static ActionTween set(ActionBind target, boolean removeActions) {
		return set(target, -1, removeActions);
	}

	public final static ActionTween on(ActionBind target) {
		return set(target);
	}

	public final static ActionTween on(ActionBind target, boolean removeActions) {
		return set(target, -1, removeActions);
	}

	public final static void off(ActionBind act) {
		removeAllActions(act);
	}

	public final static ActionScript act(ActionBind target, String script) {
		ActionTween tween = set(target, true);
		return new ActionScript(tween, script);
	}

	public final static ActionScript act(ActionBind target, boolean removeActions, String script) {
		ActionTween tween = set(target, -1, removeActions);
		return new ActionScript(tween, script);
	}

	public final static ActionTween call(ActionCallback callback) {
		return ActionTween.call(callback);
	}

	public final static boolean hasActions(ActionBind bind) {
		return ActionControl.get().containsKey(bind);
	}

	public final static boolean isActionCompleted(ActionBind bind) {
		return ActionControl.get().isCompleted(bind);
	}

	public final static boolean stopActionNames(ActionBind k, String name) {
		return ActionControl.get().stopNames(k, name);
	}

	public final static boolean stopActionTags(ActionBind k, Object tag) {
		return ActionControl.get().stopTags(k, tag);
	}

	public final static CanvasPlayer createTextPlayer(LFont font, String text) {
		TextLayout layout = font.getLayoutText(text);
		Canvas canvas = LSystem.base().graphics().createCanvas(MathUtils.ceil(layout.stringWidth(text)),
				MathUtils.ceil(layout.getHeight()));
		canvas.setColor(LColor.white);
		canvas.setFont(font);
		canvas.drawText(text, 0f, 0f);
		CanvasPlayer player = new CanvasPlayer(LSystem.base().graphics(), canvas);
		return player;
	}

	public final static PathMove createPathMove(Vector2f origin, Vector2f target, float speed, boolean limit) {
		return new PathMove(origin, target, speed, limit);
	}

	public final static PathMove createPathMove(Vector2f origin, Vector2f target, float speed) {
		return new PathMove(origin, target, speed);
	}

	public final static PathMove createPathMove(float srcX, float srcY, float destX, float destY, float speed,
			boolean limit) {
		return new PathMove(srcX, srcY, destX, destY, speed, limit);
	}

	public final static PathMove createPathMove(float srcX, float srcY, float destX, float destY, float speed) {
		return new PathMove(srcX, srcY, destX, destY, speed);
	}

	/**
	 * 求两个动作对象在X轴两点间距离
	 * 
	 * @param src
	 * @param dst
	 * @return
	 */
	public final static float getDistanceXBetween(ActionBind src, ActionBind dst) {
		return MathUtils.abs((dst.getX() + dst.getWidth() / 2) - (src.getX() + src.getWidth() / 2));
	}

	/**
	 * 求指定动作对象与指定方形区域在X轴两点间距离
	 * 
	 * @param src
	 * @param dst
	 * @return
	 */
	public final static float getDistanceXBetween(ActionBind src, RectBox dst) {
		return MathUtils.abs((dst.getX() + dst.getWidth() / 2) - (src.getX() + src.getWidth() / 2));
	}

	/**
	 * 求两个动作对象在Y轴两点间距离
	 * 
	 * @param src
	 * @param dst
	 * @return
	 */
	public static float getDistanceYBetween(ActionBind src, ActionBind dst) {
		return MathUtils.abs((dst.getY() + dst.getHeight() / 2) - (src.getY() + src.getHeight() / 2));
	}

	/**
	 * 求指定动作对象与指定方形区域在Y轴两点间距离
	 * 
	 * @param src
	 * @param dst
	 * @return
	 */
	public final static float getDistanceYBetween(ActionBind src, RectBox dst) {
		return MathUtils.abs((dst.getY() + dst.getHeight() / 2) - (src.getY() + src.getHeight() / 2));
	}

	/**
	 * 求两个动作对象在指定范围内的X轴距离
	 * 
	 * @param src
	 * @param dst
	 * @param allowDistance
	 * @return
	 */
	public static float getAllowableXDistance(ActionBind src, ActionBind dst, float allowDistance) {
		return (src.getWidth() / 2) + (dst.getWidth() / 2 - allowDistance);
	}

	/**
	 * 求两个动作对象在指定范围内的Y轴距离
	 * 
	 * @param src
	 * @param dst
	 * @param allowDistance
	 * @return
	 */
	public static float getAllowableYDistance(ActionBind src, ActionBind dst, float allowDistance) {
		return (src.getHeight() / 2) + (dst.getHeight() / 2 - allowDistance);
	}
	
}
