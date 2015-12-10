package loon.geom;

public class SizeValue {

	private static final String PIXEL = "px";

	private static final String PERCENT = "%";

	private static final String WIDTH_SUFFIX = "w";

	private static final String HEIGHT_SUFFIX = "h";

	private static final String WILDCARD = "*";

	private static final float MAX_PERCENT = 100.0f;

	private String value;

	private float percentValue;

	private float pixelValue;

	private boolean hasWidthSuffix;

	private boolean hasHeightSuffix;

	public SizeValue(final int size) {
		this(size + "px");
	}

	public SizeValue(final String valueParam) {
		if (valueParam != null) {
			if (valueParam.endsWith(PERCENT + WIDTH_SUFFIX)) {
				hasWidthSuffix = true;
				this.value = valueParam.substring(0, valueParam.length() - 1);
			} else if (valueParam.endsWith(PERCENT + HEIGHT_SUFFIX)) {
				hasHeightSuffix = true;
				this.value = valueParam.substring(0, valueParam.length() - 1);
			} else {
				this.value = valueParam;
			}
		} else {
			this.value = valueParam;
		}
		this.percentValue = getPercentValue();
		this.pixelValue = getPixelValue();
	}

	public boolean isPercentOrPixel() {
		return isPercent() || isPixel();
	}

	public float getValue(final float range) {
		if (isPercent()) {
			return (range / MAX_PERCENT) * percentValue;
		} else if (isPixel()) {
			return pixelValue;
		} else {
			return -1;
		}
	}

	public int getValueAsInt(final float range) {
		return (int) getValue(range);
	}

	private float getPercentValue() {
		if (isPercent()) {
			String percent = value.substring(0,
					value.length() - PERCENT.length());
			return Float.parseFloat(percent);
		} else {
			return 0;
		}
	}

	private int getPixelValue() {
		if (isPixel()) {
			if (hasNoSuffix()) {
				return Integer.parseInt(value);
			}
			String pixel = value.substring(0, value.length() - PIXEL.length());
			return Integer.parseInt(pixel);
		} else {
			return 0;
		}
	}

	private boolean isPercent() {
		if (value == null) {
			return false;
		} else {
			return value.endsWith(PERCENT);
		}
	}

	public boolean isPixel() {
		if (value == null) {
			return false;
		} else {
			return !value.equals(WILDCARD)
					&& (value.endsWith(PIXEL) || hasNoSuffix());
		}
	}

	private boolean hasNoSuffix() {
		if (value == null) {
			return false;
		}

		if (value.endsWith(PIXEL) || value.endsWith(PERCENT)
				|| value.endsWith(WIDTH_SUFFIX)
				|| value.endsWith(HEIGHT_SUFFIX)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return value;
	}

	public boolean hasWidthSuffix() {
		return hasWidthSuffix;
	}

	public boolean hasHeightSuffix() {
		return hasHeightSuffix;
	}

	public boolean hasWildcard() {
		return "*".equals(value);
	}

}
