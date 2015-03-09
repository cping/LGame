package com.mygame;

import loon.LSystem;
import loon.LTouch;
import loon.LTransition;
import loon.core.event.ActionKey;
import loon.core.graphics.Screen;
import loon.core.graphics.component.LButton;
import loon.core.graphics.component.LPaper;
import loon.core.graphics.opengl.GLEx;
import loon.core.timer.LTimerContext;

public class TitleScreen extends Screen {

	LButton start, end;

	LPaper title;

	public TitleScreen() {

	}

	public LTransition onTransition() {
		return LTransition.newCrossRandom();
	}

	public void onLoad() {

		setBackground("assets/back1.png");

		start = new LButton("assets/title_start.png", 191, 57) {

			ActionKey action = new ActionKey(
					ActionKey.DETECT_INITIAL_PRESS_ONLY);

			public void doClick() {
				if (!action.isPressed()) {
					action.press();
					replaceScreen(new MyAVGScreen(), MoveMethod.FROM_LEFT);
				}
			}
		};
		start.setLocation(2, 5);
		start.setEnabled(false);
		add(start);

		LButton btn2 = new LButton("assets/title_load.png", 160, 56);
		btn2.setLocation(2, start.getY() + start.getHeight() + 20);
		btn2.setEnabled(false);
		add(btn2);

		LButton btn3 = new LButton("assets/title_option.png", 215, 57);
		btn3.setLocation(2, btn2.getY() + btn2.getHeight() + 20);
		btn3.setEnabled(false);
		add(btn3);

		end = new LButton("assets/title_end.png", 142, 57) {
			public void doClick() {
				LSystem.exit();
			}
		};
		end.setLocation(2, btn3.getY() + btn3.getHeight() + 20);
		end.setEnabled(false);
		add(end);

		title = new LPaper("assets/title.png", -200, 0);
		add(title);
	}

	public void alter(LTimerContext c) {
		if (isOnLoadComplete()) {
			if (title.getScreenX() + title.getWidth() + 25 <= getWidth()) {
				title.move_right(3);
			} else {
				start.setEnabled(true);
				end.setEnabled(true);
			}
		}
	}

	public void draw(GLEx g) {

	}

	public void touchDown(LTouch e) {

	}

	public void touchMove(LTouch e) {

	}

	public void touchUp(LTouch e) {

	}

	@Override
	public void touchDrag(LTouch e) {
		// TODO Auto-generated method stub
		
	}

}
