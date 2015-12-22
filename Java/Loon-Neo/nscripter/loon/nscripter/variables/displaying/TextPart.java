package loon.nscripter.variables.displaying;

import loon.canvas.LColor;
import loon.geom.Vector2f;

public class TextPart {

	public LColor TextColor = new LColor(LColor.white);
	public String Text = "";
	public boolean isEndingWithNewLine = false;
	public boolean isEndingWithWait = false;
	public boolean isColoredPart = false;

	public Vector2f Position;
}
