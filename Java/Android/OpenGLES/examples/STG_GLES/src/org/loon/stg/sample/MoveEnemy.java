package org.loon.stg.sample;

import loon.stg.STGScreen;
import loon.stg.enemy.EnemyOne;

public class MoveEnemy extends EnemyOne {

	public MoveEnemy(STGScreen stg, int no, float x, float y, int tpno) {
		super(stg, no, x, y, tpno);
		//使用图像索引5（对应图像的注入顺序）
		setPlaneBitmap(0, 5);
		//坐标位于脚本导入的坐标
		setLocation(x, y);
	}

	public void onExplosion() {
		
	}

}
