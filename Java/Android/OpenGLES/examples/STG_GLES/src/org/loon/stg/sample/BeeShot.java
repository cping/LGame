package org.loon.stg.sample;

import loon.core.LSystem;
import loon.stg.STGObject;
import loon.stg.STGScreen;

//请注意，该类直接继承的STGObject
public class BeeShot extends STGObject {

	public BeeShot(STGScreen stg, int no, float x, float y, int tpno) {
		super(stg, no, x, y, tpno);
		//设定对象属性为敌方子弹
		this.attribute = STGScreen.ENEMY_SHOT;
		//图像索引为7
		setPlaneBitmap(0, 7);
		setLocation(x, y);
		hitX = hitY = 1;
	}

	public void update() {
		//每次移动时，按照偏移值的数值进行操作
		move(offsetX, offsetY);
		//如果角色被命中（就子弹来讲，也意味着命中目标），或者超出屏幕
		if (hitFlag || !LSystem.screenRect.contains(getX(), getY())) {
			//删除当前角色
			delete();
		}
	}

}
