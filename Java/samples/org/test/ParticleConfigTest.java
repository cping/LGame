package org.test;

import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.LTransition;
import loon.Screen;
import loon.events.GameTouch;
import loon.opengl.GLEx;
import loon.particle.SimpleConfigurableEmitter;
import loon.particle.SimpleParticleConfig;
import loon.particle.SimpleParticleSystem;
import loon.utils.timer.LTimerContext;

public class ParticleConfigTest extends Screen {

	private LTexture image;

	private SimpleParticleSystem trail;

	private SimpleParticleSystem fire;

	private float rx = 100;

	private float ry = 200;

	@Override
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {
		if (isOnLoadComplete()) {
			((SimpleConfigurableEmitter) trail.getEmitter(0)).setPosition(
					rx + 14, ry + 35);
			trail.setPosition(rx + 14, ry + 35);
			trail.render(g);
			image.draw(rx-10, ry - 40);
			fire.setPosition(rx + 14, ry + 35);
			fire.render(g);
		}
	}

	@Override
	public void onLoad() {
		try {
			fire = SimpleParticleConfig.loadConfiguredSystem("system.xml");
			fire.setBlendingState(LSystem.MODE_ALPHA_ONE);
			trail = SimpleParticleConfig.loadConfiguredSystem("smoketrail.xml");
			trail.setBlendingState(LSystem.MODE_ALPHA_ONE);
			image = LTextures.loadTexture("rocket.png");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		add(MultiScreenTest.getBackButton(this,0));
	}

	@Override
	public void alter(LTimerContext timer) {
		if (isOnLoadComplete()) {
			long delta = timer.timeSinceLastUpdate;
			fire.update(delta);
			trail.update(delta);
		}

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {
		if (isOnLoadComplete()) {
			fire.reset();
			trail.reset();
			rx = e.x();
			ry = e.y();
		}
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
