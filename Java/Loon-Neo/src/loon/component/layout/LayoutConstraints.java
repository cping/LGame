package loon.component.layout;

import loon.geom.SizeValue;

public class LayoutConstraints {

	private SizeValue x;

	private SizeValue y;

	private SizeValue width;

	private SizeValue height;

	private HorizontalAlign horizontalAlign;

	private VerticalAlign verticalAlign;

	private SizeValue paddingLeft;
	private SizeValue paddingRight;
	private SizeValue paddingTop;
	private SizeValue paddingBottom;

	public LayoutConstraints() {
		this.x = null;
		this.y = null;
		this.width = null;
		this.height = null;
		this.horizontalAlign = HorizontalAlign.left;
		this.verticalAlign = VerticalAlign.top;
		paddingLeft = new SizeValue("0px");
		paddingRight = new SizeValue("0px");
		paddingTop = new SizeValue("0px");
		paddingBottom = new SizeValue("0px");
	}

	public LayoutConstraints(final SizeValue newX, final SizeValue newY,
			final SizeValue newWidth, final SizeValue newHeight,
			final HorizontalAlign newHorizontalAlign,
			final VerticalAlign newVerticalAlign) {
		this.x = newX;
		this.y = newY;
		this.width = newWidth;
		this.height = newHeight;
		this.horizontalAlign = newHorizontalAlign;
		this.verticalAlign = newVerticalAlign;
	}

	public LayoutConstraints(final LayoutConstraints src) {
		this.x = src.x;
		this.y = src.y;
		this.width = src.width;
		this.height = src.height;
		this.horizontalAlign = src.horizontalAlign;
		this.verticalAlign = src.verticalAlign;
	}

	public final SizeValue getX() {
		return x;
	}

	public final LayoutConstraints setX(final SizeValue newX) {
		this.x = newX;
		return this;
	}

	public final SizeValue getY() {
		return y;
	}

	public final LayoutConstraints setY(final SizeValue newY) {
		this.y = newY;
		return this;
	}

	public final SizeValue getHeight() {
		return height;
	}

	public final LayoutConstraints setHeight(final SizeValue newHeight) {
		this.height = newHeight;
		return this;
	}

	public final SizeValue getWidth() {
		return width;
	}

	public final LayoutConstraints setWidth(final SizeValue newWidth) {
		this.width = newWidth;
		return this;
	}

	public final HorizontalAlign getHorizontalAlign() {
		return horizontalAlign;
	}

	public final LayoutConstraints setHorizontalAlign(
			final HorizontalAlign newHorizontalAlign) {
		this.horizontalAlign = newHorizontalAlign;
		return this;
	}

	public final VerticalAlign getVerticalAlign() {
		return verticalAlign;
	}

	public final LayoutConstraints setVerticalAlign(
			final VerticalAlign newVerticalAlign) {
		this.verticalAlign = newVerticalAlign;
		return this;
	}

	public SizeValue getPaddingLeft() {
		return paddingLeft;
	}

	public SizeValue getPaddingRight() {
		return paddingRight;
	}

	public SizeValue getPaddingTop() {
		return paddingTop;
	}

	public SizeValue getPaddingBottom() {
		return paddingBottom;
	}

	public LayoutConstraints setPaddingLeft(final int padding) {
		return setPaddingLeft(new SizeValue(padding));
	}

	public LayoutConstraints setPaddingLeft(final String padding) {
		return setPaddingLeft(new SizeValue(padding));
	}

	public LayoutConstraints setPaddingLeft(final SizeValue paddingLeftParam) {
		paddingLeft = paddingLeftParam;
		return this;
	}

	public LayoutConstraints setPaddingRight(final int padding) {
		return setPaddingRight(new SizeValue(padding));
	}

	public LayoutConstraints setPaddingRight(final String padding) {
		return setPaddingRight(new SizeValue(padding));
	}

	public LayoutConstraints setPaddingRight(final SizeValue paddingRightParam) {
		paddingRight = paddingRightParam;
		return this;
	}

	public LayoutConstraints setPaddingTop(final int padding) {
		return setPaddingTop(new SizeValue(padding));
	}

	public LayoutConstraints setPaddingTop(final String padding) {
		return setPaddingTop(new SizeValue(padding));
	}

	public LayoutConstraints setPaddingTop(final SizeValue paddingTopParam) {
		paddingTop = paddingTopParam;
		return this;
	}

	public LayoutConstraints setPaddingBottom(final int padding) {
		return setPaddingBottom(new SizeValue(padding));
	}

	public LayoutConstraints setPaddingBottom(final String padding) {
		return setPaddingBottom(new SizeValue(padding));
	}

	public LayoutConstraints setPaddingBottom(final SizeValue paddingBottomParam) {
		paddingBottom = paddingBottomParam;
		return this;
	}

	public LayoutConstraints setPadding(final int topBottomParam,
			final int leftRightParam) {
		return setPadding(new SizeValue(topBottomParam), new SizeValue(
				leftRightParam));
	}

	public LayoutConstraints setPadding(final String topBottomParam,
			final String leftRightParam) {
		return setPadding(new SizeValue(topBottomParam), new SizeValue(
				leftRightParam));
	}

	public LayoutConstraints setPadding(final SizeValue topBottomParam,
			final SizeValue leftRightParam) {
		paddingLeft = leftRightParam;
		paddingRight = leftRightParam;
		paddingTop = topBottomParam;
		paddingBottom = topBottomParam;
		return this;
	}

	public LayoutConstraints setPadding(final int topParam,
			final int leftRightParam, final int bottomParam) {
		return setPadding(new SizeValue(topParam),
				new SizeValue(leftRightParam), new SizeValue(bottomParam));
	}

	public LayoutConstraints setPadding(final String topParam,
			final String leftRightParam, final String bottomParam) {
		return setPadding(new SizeValue(topParam),
				new SizeValue(leftRightParam), new SizeValue(bottomParam));
	}

	public LayoutConstraints setPadding(final SizeValue topParam,
			final SizeValue leftRightParam, final SizeValue bottomParam) {
		paddingLeft = leftRightParam;
		paddingRight = leftRightParam;
		paddingTop = topParam;
		paddingBottom = bottomParam;
		return this;
	}

	public LayoutConstraints setPadding(final int topParam,
			final int rightParam, final int bottomParam, final int leftParam) {
		return setPadding(new SizeValue(topParam), new SizeValue(rightParam),
				new SizeValue(bottomParam), new SizeValue(leftParam));
	}

	public LayoutConstraints setPadding(final String topParam,
			final String rightParam, final String bottomParam,
			final String leftParam) {
		return setPadding(new SizeValue(topParam), new SizeValue(rightParam),
				new SizeValue(bottomParam), new SizeValue(leftParam));
	}

	public LayoutConstraints setPadding(final SizeValue topParam,
			final SizeValue rightParam, final SizeValue bottomParam,
			final SizeValue leftParam) {
		paddingLeft = leftParam;
		paddingRight = rightParam;
		paddingTop = topParam;
		paddingBottom = bottomParam;
		return this;
	}

	public LayoutConstraints setPadding(final int padding) {
		return setPadding(new SizeValue(padding));
	}

	public LayoutConstraints setPadding(final String padding) {
		return setPadding(new SizeValue(padding));
	}

	public LayoutConstraints setPadding(final SizeValue padding) {
		paddingLeft = padding;
		paddingRight = padding;
		paddingTop = padding;
		paddingBottom = padding;
		return this;
	}
}
