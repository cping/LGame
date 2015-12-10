package loon.canvas;

public class Alpha {
	
	public static final Alpha ZERO = new Alpha(0.0f);
	public static final Alpha FULL = new Alpha(1.0f);

	private static final int SCALE_SHORT_MODE = 0x11;
	private static final float MAX_INT_VALUE = 255.0f;
	private static final int HEX_BASE = 16;

	private float alpha = 0.0f;

	public Alpha(final String color) {
		this.alpha = getString(color);
	}

	public Alpha(final float a) {
		this.alpha = a;
	}

	public Alpha linear(final Alpha end, final float t) {
		return new Alpha(this.alpha + t * (end.alpha - this.alpha));
	}

	public final float getAlpha() {
		return alpha;
	}

	private float getString(final String color) {
		if (isShortMode(color)) {
			return (Integer.parseInt(color.substring(1, 2), HEX_BASE) * SCALE_SHORT_MODE)
					/ MAX_INT_VALUE;
		} else {
			return Integer.parseInt(color.substring(1, 3), HEX_BASE)
					/ MAX_INT_VALUE;
		}
	}

	private boolean isShortMode(final String color) {
		return color.length() == 2;
	}

	public Alpha mutiply(final float factor) {
		return new Alpha(alpha * factor);
	}

	public String toString() {
		return "(" + alpha + ")";
	}

	public void setAlpha(final float newColorAlpha) {
		alpha = newColorAlpha;
	}
}
