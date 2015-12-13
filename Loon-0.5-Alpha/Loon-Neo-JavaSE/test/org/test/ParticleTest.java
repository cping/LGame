package org.test;

import loon.LSetting;
import loon.LSystem;
import loon.LTextures;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.javase.Loon;
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
		particleSystem.setState(LSystem.MODE_ALPHA_ONE);
		particleSystem.addEmitter(new SimpleFireEmitter(300, 300, 25));
		particleSystem.addEmitter(new SimpleFireEmitter(100, 300, 30));
		particleSystem.addEmitter(new SimpleFireEmitter(200, 300, 20));
		add(particleSystem);
		
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
		setting.width = 480;
		setting.height = 320;

		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new ParticleTest();
			}
		});
	}
}
