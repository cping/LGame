package org.test;

import loon.LSetting;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.action.map.tmx.TMXMap;
import loon.action.map.tmx.renderers.TMXMapRenderer;
import loon.event.GameTouch;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class TileMapTest extends Screen{
	
	public LTransition onTransition(){
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {

		
	}

	@Override
	public void onLoad() {
		TMXMap tmx = new TMXMap("isometric_grass_and_water.tmx", "");
		TMXMapRenderer sprite = tmx.getMapRenderer();
	    sprite.setLocation(-200, -150);
		add(sprite);
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

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "test";
		setting.emulateTouch = false;
		setting.width = 800;
		setting.height = 600;
		Loon.register(setting, new LazyLoading.Data() {
			
			@Override
			public Screen onScreen() {
				return new TileMapTest();
			}
		});
	}
}
