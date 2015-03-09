package com.mygame;

import loon.stg.STGScreen;
import loon.stg.shot.HeroShot;

public class Shot1 extends HeroShot {

	public Shot1(STGScreen stg, int no, float x, float y, int tpno) {
		super(stg, no, x, y, tpno);
		//下列两参数为命中点偏移
		hitX = hitY = 2;
		//设定角色图像索引
		setPlaneBitmap(0, 3);
		setLocation(x + 14, y);
		//设定角色大小（如不设定，直接视为图像大小）
		setHitW(15);
		setHitH(15);
	}

}
