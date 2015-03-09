package com.mygame;

import loon.stg.STGScreen;
import loon.stg.shot.MoonShot;

public class BossShot2 extends MoonShot{

	public BossShot2(STGScreen stg, int no, float x, float y, int tpno) {
		super(stg, no, x, y, tpno);
		setPlaneBitmap(0, 13);
		setPlaneBitmap(1, 14);
		setPlaneBitmap(2, 15);
		setPlaneBitmap(3, 16);
		setPlaneAnime(true);
	}


}
