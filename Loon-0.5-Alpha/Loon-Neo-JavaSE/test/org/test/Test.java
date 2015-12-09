package org.test;

import loon.LSetting;
import loon.LazyLoading;
import loon.Screen;
import loon.action.sprite.ColorBackground;
import loon.action.sprite.ImageBackground;
import loon.canvas.LColor;
import loon.event.GameTouch;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class Test extends Screen{

	@Override
	public void draw(GLEx g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoad() {
	add(new ColorBackground(LColor.white, 66, 66, 99, 99));
	}

	@Override
	public void alter(LTimerContext timer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void touchDown(GameTouch e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void touchUp(GameTouch e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void touchMove(GameTouch e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void touchDrag(GameTouch e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "test";
		setting.emulateTouch = false;
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new Test();
			}
		});
	}
}
