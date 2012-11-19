package org.test;

import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;

public final class ScreenData {
	public static RectBox fullscreen = new RectBox(0, 0, 800, 480);
	public static RectBox playground;
	public static float scale = 1f;
	public static Vector2f screenCenter = new Vector2f(400f, 240f);
	public static int screenHeight = 480;
	public static int screenWidth = 800;

	public static void SetScreenResolution(int width, int height) {
		screenWidth = width;
		screenHeight = height;
		fullscreen.width = width;
		fullscreen.height = height;
		screenCenter.x = width / 2;
		screenCenter.y = height / 2;
		playground = new RectBox(0, 0, width, height);
		scale = screenHeight / 800f;
	}
}