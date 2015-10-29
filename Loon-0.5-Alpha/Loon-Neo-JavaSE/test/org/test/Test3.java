package org.test;

import java.sql.BatchUpdateException;

import org.test.Test.ScreenTest;

import loon.LSetting;
import loon.LazyLoading;
import loon.Screen;
import loon.action.sprite.SpriteBatch;
import loon.event.GameTouch;
import loon.geom.Circle;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.Scale;
import loon.utils.timer.LTimerContext;

public class Test3 extends Screen{

	@Override
	public void draw(GLEx g) {
		SpriteBatch batch=new SpriteBatch();
		batch.begin();
		batch.fill(new Circle(155, 55, 100));
		batch.end();
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void alter(LTimerContext timer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(Scale scale, int width, int height) {
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
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "test";
		setting.emulateTouch = false;
		Loon.register(setting, new LazyLoading.Data() {
			
			@Override
			public Screen onScreen() {
				return new Test3();
			}
		});
	}
}
