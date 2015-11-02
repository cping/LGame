package org.test;

import loon.LSetting;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.action.sprite.effect.PixelChopEffect;
import loon.action.sprite.effect.PixelDarkOutEffect;
import loon.action.sprite.effect.PixelWindEffect;
import loon.action.sprite.effect.PixelDarkInEffect;
import loon.action.sprite.effect.PixelSnowEffect;
import loon.canvas.LColor;
import loon.event.GameTouch;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

import org.test.Test.ScreenTest;

public class Test4 extends Screen{
	
	
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
				return new Test4();
			}
		});
	}

	@Override
	public void draw(GLEx g) {
	
		
	}
	
	public LTransition onTransition(){
		return LTransition.newEmpty();
	}

	@Override
	public void onLoad() {
		add(new PixelChopEffect(LColor.red,166,66));
		setBackground(LColor.black);
		
	}

	@Override
	public void alter(LTimerContext timer) {
	
		
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
}
