package loon.test;

import loon.Screen;
import loon.action.sprite.AnimationData;
import loon.component.LButton;
import loon.component.LPaper;
import loon.events.ActionKey;
import loon.events.GameTouch;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class TitleScreen extends Screen {

	LButton start, end;

	LPaper title;

	public TitleScreen() {

	}

	public void onLoad() {
		new AnimationData(new long[] { 0, 0 });
		setBackground("assets/back1.png");

		start = new LButton("assets/title_start.png", 191, 57) {

			ActionKey action = new ActionKey(ActionKey.DETECT_INITIAL_PRESS_ONLY);

			public void doClick() {
				if (!action.isPressed()) {
					action.press();
					replaceScreen(new MyAVGScreen());
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

	public void touchDown(GameTouch e) {

	}

	public void touchMove(GameTouch e) {

	}

	public void touchUp(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

	}

	@Override
	public void resize(int width, int height) {

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
