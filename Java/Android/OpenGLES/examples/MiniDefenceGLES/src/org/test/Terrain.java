package org.test;

import loon.utils.MathUtils;

public class Terrain {
	public float Height;

	public final float getHeight(float positionX, int level) {
		switch (level) {
		case 1:
			this.Height = this.getHeight_Lv1(positionX);
			break;

		case 2:
			this.Height = this.getHeight_Lv2(positionX);
			break;

		case 3:
			this.Height = this.getHeight_Lv3(positionX);
			break;

		case 4:
			this.Height = this.getHeight_Lv4(positionX);
			break;

		case 5:
			this.Height = this.getHeight_Lv5(positionX);
			break;
		}
		return this.Height;
	}

	public final float getHeight_Lv1(float positionX) {
		return 350f;
	}

	public final float getHeight_Lv2(float positionX) {
		float num = 350f;
		if (positionX > 488f) {
			num = 350f - (positionX / 10f);
			num = 350f - ((MathUtils.sqrt(1000000f - MathUtils.pow(
					(800f - positionX), 2f))) - 950f);
		}
		return num;
	}

	public final float getHeight_Lv3(float positionX) {
		float num = 350f;
		if ((positionX > 500f) && (positionX <= 600f)) {
			return (350f - ((positionX - 500f) / 2f));
		}
		if (positionX > 600f) {
			num = 300f;
		}
		return num;
	}

	public final float getHeight_Lv4(float positionX) {
		float num = 350f;
		if (positionX > 600f) {
			num = 350f - ((positionX - 600f) / 4f);
		}
		if ((positionX > 500f) && (positionX <= 550f)) {
			num = 350f - ((550f - positionX) / 5f);
		}
		if ((positionX > 400f) && (positionX <= 500f)) {
			num = 340f;
		}
		if ((positionX > 300f) && (positionX <= 400f)) {
			num = 350f - ((positionX - 300f) / 10f);
		}
		return num;
	}

	public final float getHeight_Lv5(float positionX) {
		return 350f;
	}
}