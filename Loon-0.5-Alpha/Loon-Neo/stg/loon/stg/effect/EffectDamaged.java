package loon.stg.effect;

import loon.stg.STGObject;
import loon.stg.STGScreen;

public class EffectDamaged extends STGObject {

	public EffectDamaged(STGScreen stg, int no, float x, float y, int tpno) {
		super(stg, no, x, y, tpno);
		super.attribute = STGScreen.NO_HIT;
		super.countUpdate = 8;
	}

	@Override
	public void update() {
		if (this.count > countUpdate || getY() > getScreenHeight()) {
			delete();
		}
		++this.count;
	}

}
