package org.loon.stg.sample;

import loon.action.map.Config;
import loon.core.graphics.LColor;
import loon.stg.STGHero;
import loon.stg.STGScreen;

public class Hero1 extends STGHero {

	public Hero1(STGScreen stg, int no, float x, float y, int tpno) {
		super(stg, no, x, y, tpno);

		// 设定主角生命值(被击中60次后死亡)
		this.setHP(60);
		// 设定主角魔法值
		this.setMP(60);
		// 设定自身动画(第一项参数为动画顺序，第二项参数为对应的图像索引)
		this.setPlaneBitmap(0, 0);
		// 如果设定有多个setPlaneBitmap，可开启此函数，以完成动画播放
		// setPlaneAnime(true);
		// 设定动画延迟
		// setPlaneAnimeDelay(delay);
		// 设定自身位置
		this.setLocation(x, y);
		// 旋转图像为指定角度
		// setPlaneAngle(90);
		// 变更图像为指定色彩
		// setPlaneBitmapColor(LColor.red);
		// 变更图像大小
		// setPlaneSize(w, h);
		// 显示图像
		this.setPlaneView(true);
		// 设定子弹用类
		this.setHeroShot("Shot1");
		// 设定自身受伤用类
		// this.setDamagedEffect("D1");
		this.setHitW(32);
		this.setHitH(32);
	}

	public void onShot() {

	}

	public void onDamaged() {
		this.setPlaneBitmapColor(LColor.red);
	}

	public void onMove() {
		this.setPlaneBitmapColor(LColor.white);
		// stg对象即当前的当前STGScreen，所有子类都可以调取到这个对象。通过此对象为中介，
		// 我们获得STGScreen状态，也可以 获得多个子类间的相互合作与调配。
		// 根据角色所朝向的方向，变更角色图
		switch (stg.getHeroTouch().getDirection()) {
		case Config.LEFT:
		case Config.TLEFT:
			setPlaneBitmap(0, 1);
			break;
		case Config.RIGHT:
		case Config.TRIGHT:
			setPlaneBitmap(0, 2);
			break;
		default:
			setPlaneBitmap(0, 0);
			break;
		}
	}
}
