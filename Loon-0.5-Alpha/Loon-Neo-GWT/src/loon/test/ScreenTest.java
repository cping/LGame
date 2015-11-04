package loon.test;

import loon.LTexture;
import loon.LTransition;
import loon.Screen;
import loon.action.avg.AVGDialog;
import loon.action.sprite.effect.NaturalEffect;
import loon.event.GameTouch;
import loon.opengl.GLEx;
import loon.utils.Scale;
import loon.utils.timer.LTimerContext;

public class ScreenTest extends Screen{

	
	public LTransition onTransition(){
		return LTransition.newEmpty();
	}
	
	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {
		add(NaturalEffect.getSnowEffect());
		
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
