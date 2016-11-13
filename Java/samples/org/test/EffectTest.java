package org.test;

import loon.LTransition;
import loon.Screen;
import loon.action.sprite.ISprite;
import loon.action.sprite.effect.BaseEffect;
import loon.action.sprite.effect.FadeDotEffect;
import loon.action.sprite.effect.FadeEffect;
import loon.action.sprite.effect.FadeOvalEffect;
import loon.action.sprite.effect.FadeSpiralEffect;
import loon.action.sprite.effect.FadeTileEffect;
import loon.canvas.LColor;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.utils.Array;
import loon.utils.processes.RealtimeProcess;
import loon.utils.timer.LTimerContext;

public class EffectTest extends Screen {

	RealtimeProcess process;
	
	Array<BaseEffect> effects = new Array<BaseEffect>();

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		//设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		// 设置背景图片
		setBackground("back1.png");
		// 插入不同的精灵特效
		effects.add(new FadeSpiralEffect(ISprite.TYPE_FADE_IN, LColor.black));
		effects.add(new FadeDotEffect(ISprite.TYPE_FADE_IN, LColor.black));
		effects.add(new FadeOvalEffect(ISprite.TYPE_FADE_IN, LColor.black));
		effects.add(new FadeTileEffect(ISprite.TYPE_FADE_IN, LColor.black));
		effects.add(new FadeSpiralEffect(ISprite.TYPE_FADE_IN, LColor.black));
		process = new RealtimeProcess() {

			@Override
			public void run(LTimerContext time) {
				// 每次获得第一个集合对象
				BaseEffect effect = effects.first();
				// 不包含则添加
				if (!contains(effect)) {
					add(effect);
				}
				// 完成则删除
				if (effect.isCompleted()) {
					effects.remove(0);
					remove(effect);
				}
				// 如果全部特效都执行完毕，删除这个游戏进程本身
				if (effects.size() == 0) {
					kill();
				}
			}
		};
		// 加入一个单独的游戏进程
		addProcess(process);
		
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
		removeProcess(process);
		effects.clear();
	}
}
