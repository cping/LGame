package org.test;

import loon.LTransition;
import loon.Screen;
import loon.action.map.tmx.TMXMap;
import loon.action.map.tmx.renderers.TMXMapRenderer;
import loon.event.GameTouch;
import loon.font.LFont;
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

		//设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		TMXMap tmx = new TMXMap("isometric_grass_and_water.tmx", "");
		TMXMapRenderer sprite = tmx.getMapRenderer();
	    sprite.setLocation(-200, -150);
		add(sprite);
		add(MultiScreenTest.getBackButton(this,0));
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
