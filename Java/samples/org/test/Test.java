package org.test;

import loon.Stage;
import loon.component.LClickButton;
import loon.event.Touched;

public class Test extends Stage {

	@Override
	public void create() {

		add(MultiScreenTest.getBackButton(this, 1));
		setBackground("back1.png");
		add(new LClickButton("Scale", 66, 66, 120, 50).up(new Touched() {
			
			@Override
			public void on(float x, float y) {
				if (isActionCompleted()) {
					//如果Screen动画执行完毕则执行(改变Screen会影响全局，所以最好检查下是否有动画在播放，以免某些动画中途停止，导致Screen混乱
					//影响整个布局)
					selfAction().scaleTo(0.6f).start(); //缩放为60%
				}
			
			}
		}));
		add(new LClickButton("Shake", 256, 66, 120, 50).up(new Touched() {
			
			@Override
			public void on(float x, float y) {
				if (isActionCompleted()) {
					selfAction().shakeTo(2f).start();
				}
			}
		}));
		add(new LClickButton("Rotate", 66, 166, 120, 50).up(new Touched() {
			
			@Override
			public void on(float x, float y) {
				if (isActionCompleted()) {
					selfAction().rotateTo(-180).scaleTo(0.6f).start();
				}
			}
		}));
		add(new LClickButton("Reset", 256, 166, 120, 50).up(new Touched() {
			
			@Override
			public void on(float x, float y) {
				if (isActionCompleted()) {
					selfAction().rotateTo(0).scaleTo(1f).start();
				}
			}
		}));
	}


}
