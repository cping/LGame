package loon;

import loon.Log.Level;
import loon.action.ActionBind;
import loon.action.ActionCallback;
import loon.action.ActionControl;
import loon.action.ActionEvent;
import loon.action.ActionScript;
import loon.action.ActionTween;
import loon.action.sprite.CanvasPlayer;
import loon.canvas.Canvas;
import loon.canvas.LColor;
import loon.event.Updateable;
import loon.font.LFont;
import loon.font.TextLayout;
import loon.geom.BooleanValue;
import loon.utils.MathUtils;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.processes.WaitProcess;

public class PlayerUtils extends Director {

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

	public final static void debug(String msg) {
		if (LSystem._base != null) {
			LSystem._base.log().debug(msg);
		}
	}

	public final static void debug(String msg, Object... args) {
		if (LSystem._base != null) {
			LSystem._base.log().debug(msg, args);
		}
	}

	public final static void debug(String msg, Throwable throwable) {
		if (LSystem._base != null) {
			LSystem._base.log().debug(msg, throwable);
		}
	}

	public final static void info(String msg) {
		if (LSystem._base != null) {
			LSystem._base.log().info(msg);
		}
	}

	public final static void info(String msg, Object... args) {
		if (LSystem._base != null) {
			LSystem._base.log().info(msg, args);
		}
	}

	public final static void info(String msg, Throwable throwable) {
		if (LSystem._base != null) {
			LSystem._base.log().info(msg, throwable);
		}
	}

	public final static void error(String msg) {
		if (LSystem._base != null) {
			LSystem._base.log().error(msg);
		}
	}

	public final static void error(String msg, Object... args) {
		if (LSystem._base != null) {
			LSystem._base.log().error(msg, args);
		}
	}

	public final static void error(String msg, Throwable throwable) {
		if (LSystem._base != null) {
			LSystem._base.log().error(msg, throwable);
		}
	}

	public final static void setLogMinLevel(Level level) {
		if (LSystem._base != null) {
			LSystem._base.log().setMinLevel(level);
		}
	}
}
