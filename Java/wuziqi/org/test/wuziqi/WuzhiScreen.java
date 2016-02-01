package org.test.wuziqi;

import loon.LTransition;
import loon.Screen;
import loon.canvas.LColor;
import loon.component.LCheckBox;
import loon.component.LCheckGroup;
import loon.component.LClickButton;
import loon.component.LComponent;
import loon.event.ClickListener;
import loon.event.GameTouch;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class WuzhiScreen extends Screen {

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	class MyClick implements ClickListener {

		@Override
		public void DoClick(LComponent comp) {

		}

		@Override
		public void DownClick(LComponent comp, float x, float y) {}

		@Override
		public void UpClick(LComponent comp, float x, float y) {

			Object source = comp.Tag;
			if ("start".equals(source)) {
				int mode = -1, intel = GobangPanel.EVAL, level = 3, node = 3;
				if (manualBtn.isSelected()) {
					mode = GobangPanel.MANUAL;
				} else if (halfAutoBtn.isSelected()) {
					mode = GobangPanel.HALF;
				} else if (autoBtn.isSelected()) {
					mode = GobangPanel.AUTO;
				}
				panel.startGame(mode, intel, level, node);
			} else if ("troggle".equals(source)) {
				panel.troggleOrder();
			} else if ("undo".equals(source)) {
				panel.undo();
			}
		
		}

		@Override
		public void DragClick(LComponent comp, float x, float y) {

		}

	}

	GobangPanel panel;
	LCheckBox manualBtn;
	LCheckBox halfAutoBtn;
	LCheckBox autoBtn;
	LCheckBox orderBtn;

	@Override
	public void onLoad() {

		panel = new GobangPanel();
		add(panel);

		manualBtn = new LCheckBox("双人", getWidth() - 140, 5);
		halfAutoBtn = new LCheckBox("人机", getWidth() - 70, 5);
		halfAutoBtn.setTicked(true);
		autoBtn = new LCheckBox("自动", getWidth() - 140, 45);
		orderBtn = new LCheckBox("落子顺序", getWidth() - 140, 85);
		orderBtn.Tag = "troggle";
		
		MyClick click = new MyClick();

		LCheckGroup group = new LCheckGroup();
		group.add(manualBtn);
		group.add(halfAutoBtn);
		group.add(autoBtn);
		group.setColor(LColor.red);
		add(group);

		LClickButton startBtn = LClickButton.make("新游戏", getWidth() - 140,
				getHeight() - 150, 130, 50);
		startBtn.Tag = "start";
		LClickButton undoBtn = LClickButton.make("悔棋", getWidth() - 140,
				getHeight() - 90, 130, 50);
		undoBtn.Tag = "undo";

		orderBtn.SetClick(click);
		startBtn.SetClick(click);
		undoBtn.SetClick(click);

		add(orderBtn);
		add(startBtn);
		add(undoBtn);

		panel.startGame(GobangPanel.HALF, GobangPanel.EVAL, 3, 3);
	}

	@Override
	public void alter(LTimerContext timer) {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {

	}

	@Override
	public void touchUp(GameTouch e) {

	}

	@Override
	public void touchMove(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void close() {

	}

}
