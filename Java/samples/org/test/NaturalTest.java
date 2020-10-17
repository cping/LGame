package org.test;

import loon.Stage;
import loon.action.sprite.effect.LightningEffect;
import loon.action.sprite.effect.NaturalEffect;
import loon.canvas.LColor;
import loon.events.Touched;
import loon.geom.Vector2f;

public class NaturalTest extends Stage {

	@Override
	public void create() {
		//设置背景
		setBackground("assets/back1.png")
		// 添加自然效果樱花
		.add(NaturalEffect.getPetalEffect())
		// 雪
		.add(NaturalEffect.getSnowEffect())
		// 雨
		.add(NaturalEffect.getRainEffect())
		// 雷(全屏随机)
		.add(NaturalEffect.getThunderEffect())
		// 在屏幕中心添加一个label
		.centerOn(addLabel("天打雷劈屠真龙", 0, 0))
		// 监听屏幕
		.down(new Touched() {

			@Override
			public void on(float x, float y) {
				// 从触屏位置,向屏幕中心,发射一道弧形闪电,完成后自动从精灵集合删除
				add(LightningEffect.addBolt(Vector2f.at(x, y), Vector2f.at(getHalfWidth(), getHalfHeight()),
						LColor.white).setAutoRemoved(true));
			}
		})
		.add(MultiScreenTest.getBackButton(this, 1));
	}

}
