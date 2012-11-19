package org.test;

import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.utils.MathUtils;
//地图瓦片用类
public final class Tile {

	private static RectBox rect = new RectBox();

	// 默认的单独瓦片大小
	public static final int size = 30;

	public static RectBox getBounds(Vector2f position) {
		int num = MathUtils.floor(position.y / size);
		int num2 = MathUtils.floor(position.x / size);
		rect.setBounds(num2 * size, num * size, size, size);
		return rect;
	}

	public static RectBox getBounds(float x, float y) {
		int num = MathUtils.floor(y / size);
		int num2 = MathUtils.floor(x / size);
		rect.setBounds(num2 * size, num * size, size, size);
		return rect;
	}
}