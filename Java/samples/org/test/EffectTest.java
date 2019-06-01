package org.test;

import loon.Stage;
import loon.action.sprite.ISprite;
import loon.action.sprite.effect.BaseEffect;
import loon.action.sprite.effect.FadeBoardEffect;
import loon.action.sprite.effect.FadeDotEffect;
import loon.action.sprite.effect.FadeOvalEffect;
import loon.action.sprite.effect.FadeSpiralEffect;
import loon.action.sprite.effect.FadeTileEffect;
import loon.action.sprite.effect.PixelBubbleEffect;
import loon.action.sprite.effect.SwipeEffect;
import loon.canvas.LColor;
import loon.component.LLabel;
import loon.utils.Array;
import loon.utils.processes.RealtimeProcess;
import loon.utils.timer.LTimerContext;

public class EffectTest extends Stage {

	@Override
	public void create() {

		final Array<BaseEffect> effects = new Array<BaseEffect>();

		// 设置背景图片
		setBackground("back1.png");
		// 插入不同的精灵特效
		effects.add(new FadeSpiralEffect(ISprite.TYPE_FADE_IN, LColor.black));
		effects.add(new FadeBoardEffect(ISprite.TYPE_FADE_IN, LColor.black));
		effects.add(new FadeDotEffect(ISprite.TYPE_FADE_IN, LColor.black));
		effects.add(new FadeOvalEffect(ISprite.TYPE_FADE_IN, LColor.black));
		effects.add(new FadeTileEffect(ISprite.TYPE_FADE_IN, LColor.black));
		effects.add(new SwipeEffect(ISprite.TYPE_FADE_IN, LColor.black));
		effects.add(new FadeSpiralEffect(ISprite.TYPE_FADE_OUT, LColor.black));
		effects.add(new FadeBoardEffect(ISprite.TYPE_FADE_OUT, LColor.black));
		effects.add(new FadeDotEffect(ISprite.TYPE_FADE_OUT, LColor.black));
		effects.add(new FadeOvalEffect(ISprite.TYPE_FADE_OUT, LColor.black));
		effects.add(new FadeTileEffect(ISprite.TYPE_FADE_OUT, LColor.black));
		effects.add(new SwipeEffect(ISprite.TYPE_FADE_OUT, LColor.black));
		RealtimeProcess process = new RealtimeProcess() {

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
					LLabel label = addLabel("Effect Over");
					centerOn(label);
					// 显示一堆红色气泡出来
					add(new PixelBubbleEffect(LColor.red));

				}
			}
		};
		// 加入一个单独的游戏进程
		addProcess(process);

		// Screen关闭时注销进程,注销effct集合
		putReleases(process, effects);

		add(MultiScreenTest.getBackButton(this, 0));
	}
}
