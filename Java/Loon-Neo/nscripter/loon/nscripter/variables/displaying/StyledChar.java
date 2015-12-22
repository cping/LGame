package loon.nscripter.variables.displaying;

import loon.canvas.LColor;

public class StyledChar {

	public String C = " ";

	public LColor charColor;

	public StyledChar(char p, LColor color) {
		this.C = String.valueOf(p);
		this.charColor = color;
	}

	public StyledChar() {
	}

	public StyledChar(String p, LColor color) {
		this.C = p;
		this.charColor = color;
	}

	void setColor(LColor color) {
		this.charColor = color;
	}
}
