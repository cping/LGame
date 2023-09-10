package loon.nscripter.variables.sprites;

import loon.LTexture;
import loon.geom.RectBox;
import loon.utils.TArray;

public class Sprite {

	public boolean isHidden;

	public boolean isDelayFull;

	public boolean isAnimated;

	public int type;

	public TArray<LTexture> texturesList = new TArray<LTexture>();

	public int cellNumber;

	public TArray<Integer> delayList = new TArray<Integer>();

	public int loopType;

	public RectBox Rectangle = new RectBox();

	public char transparencyType;
}
