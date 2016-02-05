package loon.test.stggame;

import loon.stg.STGObject;
import loon.stg.STGScreen;

public class BossShot1 extends STGObject {

	public BossShot1(STGScreen stg, int no, float x, float y, int tpno) {
		super(stg, no, x, y, tpno);
		super.attribute = STGScreen.ENEMY_SHOT;
		setPlaneBitmap(0, 7);
		setLocation(x, y);
        hitX = hitY = 1;
	}

	public void update() {
		move(0, 12);
		if (getY() > stg.getHeight()) {
			delete();
		}
	}

}
