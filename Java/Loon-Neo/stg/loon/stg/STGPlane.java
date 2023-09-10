package loon.stg;

import loon.canvas.LColor;
import loon.font.LFont;
import loon.geom.RectBox;
import loon.stg.effect.Picture;
import loon.utils.ObjectMap;
import loon.utils.timer.LTimer;

public class STGPlane {

	boolean view = false;

	boolean animation = false;

	public float posX = 0;

	public float posY = 0;

	public float rotation = 0;

	public LColor drawColor = new LColor(LColor.white);

	int animeNo;

	int[] animeList;

	int planeMode = 0;

	float scaleX = 1;

	float scaleY = 1;

	RectBox rect;

	ObjectMap<Integer, Integer> images = new ObjectMap<Integer, Integer>();

	LTimer delay = new LTimer(100);

	String str;

	LFont font;

	LColor color;

	Picture draw;

}
