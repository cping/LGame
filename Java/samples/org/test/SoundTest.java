package org.test;

import loon.Screen;
import loon.component.LClickButton;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class SoundTest extends Screen{

	@Override
	public void draw(GLEx g) {
	
	}

	@Override
	public void onLoad() {

		// 设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		
		LClickButton click1 = new LClickButton("Sound Ogg", 150, 100, 100, 25){
			
			public void downClick(){
				playSound("assets/shotgun.ogg");
			}
		};
		add(click1);
		

		LClickButton click2 = new LClickButton("Sound Wav", 150, 150, 100, 25){
			
			public void downClick(){
				playSound("assets/shotgun.wav");
			}
		};
		add(click2);
		
		add(MultiScreenTest.getBackButton(this));

		
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
