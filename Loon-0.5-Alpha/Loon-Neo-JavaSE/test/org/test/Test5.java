package org.test;

import org.test.Test.ScreenTest;

import loon.LSetting;
import loon.LSystem;
import loon.LTexture;
import loon.LTextureBatch;
import loon.LTransition;
import loon.LTextureBatch.Cache;
import loon.LazyLoading;
import loon.Screen;
import loon.action.ActionType;
import loon.action.ActionControl;
import loon.action.ActionTween;
import loon.action.FireTo;
import loon.action.MoveTo;
import loon.action.avg.AVGDialog;
import loon.action.map.tmx.TMXImageLayer;
import loon.action.map.tmx.TMXMap;
import loon.action.map.tmx.TMXTileLayer;
import loon.action.map.tmx.renderers.TMXMapRenderer;
import loon.action.sprite.Entity;
import loon.action.sprite.Sprite;
import loon.action.sprite.WaitSprite;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.component.LComponent;
import loon.component.LMessageBox;
import loon.event.ClickListener;
import loon.event.GameTouch;
import loon.event.Updateable;
import loon.font.LFont;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.opengl.GLEx.Direction;
import loon.opengl.LSTRFont;
import loon.stage.ImagePlayer;
import loon.stage.Player;
import loon.stage.Stage;
import loon.stage.StageSystem;
import loon.utils.CollectionUtils;
import loon.utils.Easing;
import loon.utils.MathUtils;
import loon.utils.processes.RealtimeProcess;
import loon.utils.timer.LTimerContext;

public class Test5 extends Screen {

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	public void onLoad() {
	
		TMXMap map=new TMXMap("testmap.tmx", "");
		add(map.getMapRenderer());

	}

	@Override
	public void alter(LTimerContext timer) {
	//	if(renderer!=null){
	//		renderer.update(timer.timeSinceLastUpdate);
	//	}
	}
	@Override
	public void draw(GLEx g) {
		
	}

	// g.end();
	// g.restoreTx();
	/*
	 * (if(!flag){ flag=true; batch.begin(); batch.draw(66, 66); batch.draw(166,
	 * 166); batch.end(); }
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub

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
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {

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
		setting.width = 640;
		setting.height = 480;
		
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new Test5();
			}
		});
	}

}
