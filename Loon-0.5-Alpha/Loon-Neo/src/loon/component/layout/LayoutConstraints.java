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

	public final void setX(final SizeValue newX) {
		this.x = newX;
	}

	public final SizeValue getY() {
		return y;
	}

	public final void setY(final SizeValue newY) {
		this.y = newY;
	}

	public final SizeValue getHeight() {
		return height;
	}

	public final void setHeight(final SizeValue newHeight) {
		this.height = newHeight;
	}

	public final SizeValue getWidth() {
		return width;
	}

	public final void setWidth(final SizeValue newWidth) {
		this.width = newWidth;
	}

	public final HorizontalAlign getHorizontalAlign() {
		return horizontalAlign;
	}

	public final void setHorizontalAlign(
			final HorizontalAlign newHorizontalAlign) {
		this.horizontalAlign = newHorizontalAlign;
	}

	public final VerticalAlign getVerticalAlign() {
		return verticalAlign;
	}

	public final void setVerticalAlign(final VerticalAlign newVerticalAlign) {
		this.verticalAlign = newVerticalAlign;
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

	public void setPaddingLeft(final SizeValue paddingLeftParam) {
		paddingLeft = paddingLeftParam;
	}

	public void setPaddingRight(final SizeValue paddingRightParam) {
		paddingRight = paddingRightParam;
	}

	public void setPaddingTop(final SizeValue paddingTopParam) {
		paddingTop = paddingTopParam;
	}

	public void setPaddingBottom(final SizeValue paddingBottomParam) {
		paddingBottom = paddingBottomParam;
	}

	public void setPadding(final SizeValue topBottomParam,
			final SizeValue leftRightParam) {
		paddingLeft = leftRightParam;
		paddingRight = leftRightParam;
		paddingTop = topBottomParam;
		paddingBottom = topBottomParam;
	}

	public void setPadding(final SizeValue topParam,
			final SizeValue leftRightParam, final SizeValue bottomParam) {
		paddingLeft = leftRightParam;
		paddingRight = leftRightParam;
		paddingTop = topParam;
		paddingBottom = bottomParam;
	}

	public void setPadding(final SizeValue topParam,
			final SizeValue rightParam, final SizeValue bottomParam,
			final SizeValue leftParam) {
		paddingLeft = leftParam;
		paddingRight = rightParam;
		paddingTop = topParam;
		paddingBottom = bottomParam;
	}

	public void setPadding(final SizeValue padding) {
		paddingLeft = padding;
		paddingRight = padding;
		paddingTop = padding;
		paddingBottom = padding;
	}
}
