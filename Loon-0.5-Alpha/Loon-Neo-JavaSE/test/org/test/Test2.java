package org.test;

import org.test.Test.ScreenTest;

import loon.LSetting;
import loon.LTexture;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatchScreen;
import loon.canvas.LColor;
import loon.event.GameKey;
import loon.event.GameTouch;
import loon.geom.Circle;
import loon.javase.Loon;
import loon.utils.Scale;

public class Test2 extends SpriteBatchScreen{
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void create() {
	//setBackground(LColor.red);
		
	}
	LTexture tex=LTexture.createTexture("back1.png");

	@Override
	public void after(SpriteBatch batch) {
	//	batch.setColor(LColor.blue);

		batch.draw(tex, 66, 66);
		batch.drawRect(55, 55, 100, 100);
	

		batch.drawRect(255, 155, 300, 300);
		
	}

	@Override
	public void before(SpriteBatch batch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void press(GameKey e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void release(GameKey e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(long elapsedTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
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

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		setting.fps = 60;
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		setting.fontName = "黑体";
		setting.appName = "test";
		setting.emulateTouch = false;
		Loon.register(setting, new LazyLoading.Data() {
			
			@Override
			public Screen onScreen() {
				return new Test2();
			}
		});
	}
}
