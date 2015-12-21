package loon.stage;

import loon.LProcess;
import loon.LRelease;
import loon.LSystem;
import loon.utils.reply.Act;
import loon.utils.reply.Closeable;
import loon.utils.timer.LTimerContext;

public abstract class Stage extends PlayerUtils implements LRelease {

	protected Closeable.Set _closeList = new Closeable.Set();

	public final GroupPlayer players = new GroupPlayer();

	public final Act<LTimerContext> update = Act.create();

	public final Act<LTimerContext> paint = Act.create();

	public float width() {
		return LSystem.viewSize.width;
	}

	public float height() {
		return LSystem.viewSize.height;
	}

	public void wasShown() {
		addClose(LSystem.base().display().update.connect(update.port()));
		addClose(LSystem.base().display().paint.connect(paint.port()));
	}

	public void wasHidden() {
		_closeList.close();
	}

	public Stage addPlayer(Player child) {
		players.add(child);
		return this;
	}

	public Stage remove(Player child) {
		players.remove(child);
		return this;
	}

	public Stage addStage(Stage stage) {
		LProcess process = LSystem.getProcess();
		if (process != null) {
			process.getStageSystem().push(stage);
		}
		return this;
	}

	public Stage addStage(Stage stage, StageTransition trans) {
		LProcess process = LSystem.getProcess();
		if (process != null) {
			process.getStageSystem().push(stage, trans);
		}
		return this;
	}

	public Stage remove(Stage stage) {
		LProcess process = LSystem.getProcess();
		if (process != null) {
			process.getStageSystem().remove(stage);
		}
		return this;
	}

	public StageSystem getStageSystem() {
		LProcess process = LSystem.getProcess();
		if (process != null) {
			return process.getStageSystem();
		}
		return null;
	}

	@Override
	public void close() {
		_closeList.close();
		update.clearConnections();
		paint.clearConnections();
		players.close();
	}

	public abstract void onAdded();

	public abstract void onRemoved();

	public abstract void onShowTransitionCompleted();

	public abstract void onHideTransitionStarted();

	public void addClose(AutoCloseable ac) {
		_closeList.add(ac);
	}
}