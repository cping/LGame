package org.test;

import loon.LSystem;
import loon.LTextures;
import loon.LTransition;
import loon.Screen;
import loon.events.GameTouch;
import loon.opengl.BlendMethod;
import loon.opengl.GLEx;
import loon.particle.ParticleFireEmitter;
import loon.particle.ParticleSystem;
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

		ParticleSystem particleSystem = new ParticleSystem(
				LTextures.loadTexture("particle.tga"));
		particleSystem.setBlendingState(BlendMethod.MODE_ALPHA_ONE);
		particleSystem.addEmitter(new ParticleFireEmitter(300, 300, 25));
		particleSystem.addEmitter(new ParticleFireEmitter(100, 300, 30));
		particleSystem.addEmitter(new ParticleFireEmitter(200, 300, 20));
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
