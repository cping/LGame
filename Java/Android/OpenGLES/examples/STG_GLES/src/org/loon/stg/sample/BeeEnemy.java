package org.loon.stg.sample;

import loon.stg.STGObject;
import loon.stg.STGScreen;
import loon.stg.enemy.EnemyOne;
import loon.utils.MathUtils;

public class BeeEnemy extends EnemyOne {

	public BeeEnemy(STGScreen stg, int no, float x, float y, int tpno) {
		super(stg, no, x, y, tpno);
		this.setPlaneBitmap(0, 8);
		this.setPlaneBitmap(1, 9);
		this.setPlaneBitmap(2, 10);
		this.setPlaneBitmap(3, 11);
		this.setPlaneBitmap(4, 12);
		this.setPlaneAnime(true);
		this.setLocation(x, y);
		//死亡延迟时间为0，即命中足够次数后立刻消失
		this.setDieSleep(0);
		//移动速度3
		this.speed = 3;
		//命中三次后，敌人消失
		this.hitPoint = 3;
	}

	public float distance(float x1, float y1, float x2, float y2) {
		x1 -= x2;
		y1 -= y2;
		return MathUtils.sqrt(x1 * x1 + y1 * y1);
	}

	private int c;

	public void update() {
		super.update();
		if (getY() >= 50) {
			if (c == 0) {
				for (int i = 0; i < 360; i += 30) {
					float rad = 2 * MathUtils.PI * ((float) i / 360);
					STGObject bow = newPlane("BeeShot", getX() + 18,
							getY() + 32, targetPlnNo);
					bow.offsetX = MathUtils.cos(rad);
					bow.offsetY = MathUtils.sin(rad);
				}
			}
			++c;
			c %= 150;
		}
	}

	//如果敌人角色死后，将自动执行此函数
	public void onExplosion() {

	}

}
