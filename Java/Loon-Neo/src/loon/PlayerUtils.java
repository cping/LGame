package loon;

import loon.action.ActionBind;
import loon.action.ActionCallback;
import loon.action.ActionControl;
import loon.action.ActionEvent;
import loon.action.ActionScript;
import loon.action.ActionTween;
import loon.action.sprite.CanvasPlayer;
import loon.canvas.Canvas;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.font.TextLayout;
import loon.geom.BooleanValue;
import loon.utils.MathUtils;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.processes.WaitProcess;

public class PlayerUtils extends Director {

	public final BooleanValue waitGame(WaitProcess.WaitEvent update) {
		WaitProcess wait = new WaitProcess(60, update);
		RealtimeProcessManager.get().addProcess(wait);
		return wait.get();
	}

	public final BooleanValue waitGame(long time, WaitProcess.WaitEvent update) {
		WaitProcess wait = new WaitProcess(time, update);
		RealtimeProcessManager.get().addProcess(wait);
		return wait.get();
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

	public final static ActionTween to(ActionBind target, int tweenType,
			float duration) {
		return to(target, tweenType, duration, true);
	}

	public final static ActionTween to(ActionBind target, int tweenType,
			float duration, boolean removeActions) {
		if (removeActions) {
			removeAllActions(target);
		}
		return ActionTween.to(target, tweenType, duration);
	}

	public final static ActionTween from(ActionBind target, int tweenType,
			float duration) {
		return from(target, tweenType, duration, true);
	}

	public final static ActionTween from(ActionBind target, int tweenType,
			float duration, boolean removeActions) {
		if (removeActions) {
			removeAllActions(target);
		}
		return ActionTween.from(target, tweenType, duration);
	}

	public final static ActionTween set(ActionBind target, int tweenType,
			boolean removeActions) {
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

	public final static ActionScript act(ActionBind target,
			String script) {
		ActionTween tween = set(target, true);
		return new ActionScript(tween, script);
	}

	public final static ActionScript act(ActionBind target,
			boolean removeActions, String script) {
		ActionTween tween = set(target, -1, removeActions);
		return new ActionScript(tween, script);
	}

	public final static ActionTween call(ActionCallback callback) {
		return ActionTween.call(callback);
	}

	public final static CanvasPlayer createTextPlayer(LFont font, String text) {
		TextLayout layout = font.getLayoutText(text);
		Canvas canvas = LSystem
				.base()
				.graphics()
				.createCanvas(MathUtils.ceil(layout.stringWidth(text)),
						MathUtils.ceil(layout.getHeight()));
		canvas.setColor(LColor.white);
		canvas.setFont(font);
		canvas.drawText(text, 0f, 0f);
		CanvasPlayer player = new CanvasPlayer(LSystem.base().graphics(),
				canvas);
		return player;
	}

}
