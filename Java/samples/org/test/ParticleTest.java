package org.test;

import loon.LSystem;
import loon.LTextures;
import loon.LTransition;
import loon.Screen;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.particle.SimpleFireEmitter;
import loon.particle.SimpleParticleSystem;
import loon.utils.timer.LTimerContext;

public class ParticleTest extends Screen {

	@Override
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		// 设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));

		SimpleParticleSystem particleSystem = new SimpleParticleSystem(
				LTextures.loadTexture("particle.tga"));
		particleSystem.setBlendingState(LSystem.MODE_ALPHA_ONE);
		particleSystem.addEmitter(new SimpleFireEmitter(300, 300, 25));
		particleSystem.addEmitter(new SimpleFireEmitter(100, 300, 30));
		particleSystem.addEmitter(new SimpleFireEmitter(200, 300, 20));
		add(particleSystem);
		
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
