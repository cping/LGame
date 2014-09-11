package org.loon.stg.sample;

import loon.stg.STGScreen;
import loon.stg.enemy.EnemyMidle;

public class Boss1 extends EnemyMidle {

	public Boss1(STGScreen stg, int no, float x, float y, int tpno) {
		super(stg, no, x, y, tpno);
		setPlaneBitmap(0, 6);
		setLocation((getScreenWidth() - getWidth()) / 2, y);
		setView(true);
		setHitPoint(60);
	}

	public void onExplosion() {

	}

	public void onEffectOne() {

	}

	int count;

	public void onEffectTwo() {
		count++;
		if (count % 5 == 0) {
			addClass("BossShot1", getX() + 32, getY() + 90, super.targetPlnNo);
		}
		if (count % 6 == 0) {
			addClass("BossShot1", getX() + 45, getY() + 90, super.targetPlnNo);
		}
		if (count % 10 == 0) {
			addClass("BossShot2", getX() + 32, getY() + 90, super.targetPlnNo);
		}
		if (count > 20){
			count = 0;
		}
	}

}
