package loon.component.layout;

import loon.geom.RectBox;
import loon.geom.SizeValue;

public class LayoutPort {

	private BoxSize box;

	private LayoutConstraints boxConstraints;

	public LayoutPort() {
		this.box = new RectBox();
		this.boxConstraints = new LayoutConstraints();
	}

	public LayoutPort(final BoxSize newBox,
			final LayoutConstraints newBoxConstraints) {
		this.box = newBox;
		this.boxConstraints = newBoxConstraints;
	}

	public LayoutPort(final LayoutPort src) {
		this.box = src.getBox();
		this.boxConstraints = new LayoutConstraints(src.getBoxConstraints());
	}

	public static void updateLayoutPart(final LayoutPort layoutPart,
			final int width, final int height) {
		BoxSize box = layoutPart.getBox();
		box.setWidth(width);
		box.setHeight(height);
		LayoutConstraints boxConstraints = layoutPart.getBoxConstraints();
		boxConstraints.setWidth(new SizeValue(width + "px"));
		boxConstraints.setHeight(new SizeValue(height + "px"));
	}

	public final BoxSize getBox() {
		return box;
	}

	public final LayoutConstraints getBoxConstraints() {
		return boxConstraints;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("box [" + box.getX() + ", " + box.getY() + ", "
				+ box.getWidth() + ", " + box.getHeight()
				+ "] with constraints [" + boxConstraints.getX() + ", "
				+ boxConstraints.getY() + ", " + boxConstraints.getWidth()
				+ ", " + boxConstraints.getHeight() + "]");
		return result.toString();
	}
}
