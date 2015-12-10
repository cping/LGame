package loon.component.layout;

import loon.geom.SizeValue;
import loon.utils.TArray;

public class CenterLayout extends LayoutManager {

	public void layoutElements(final LayoutPort rootElement,
			final LayoutPort... elements) {

		if (rootElement == null || elements == null || elements.length == 0) {
			return;
		}

		BoxSize rootBox = rootElement.getBox();
		LayoutConstraints rootBoxConstraints = rootElement.getBoxConstraints();

		BoxSize box = elements[0].getBox();
		LayoutConstraints constraint = elements[0].getBoxConstraints();

		if (constraint.getWidth() != null
				&& constraint.getWidth().hasHeightSuffix()) {
			handleVerticalAlignment(rootBox, rootBoxConstraints, box,
					constraint);
			handleHorizontalAlignment(rootBox, rootBoxConstraints, box,
					constraint);
		} else if (constraint.getHeight() != null
				&& constraint.getHeight().hasWidthSuffix()) {
			handleHorizontalAlignment(rootBox, rootBoxConstraints, box,
					constraint);
			handleVerticalAlignment(rootBox, rootBoxConstraints, box,
					constraint);
		} else {
			handleVerticalAlignment(rootBox, rootBoxConstraints, box,
					constraint);
			handleHorizontalAlignment(rootBox, rootBoxConstraints, box,
					constraint);
		}
	}

	void handleHorizontalAlignment(final BoxSize rootBox,
			final LayoutConstraints rootBoxConstraints, final BoxSize box,
			final LayoutConstraints constraint) {
		if (constraint.getWidth() != null) {
			handleWidthConstraint(rootBox, rootBoxConstraints, box, constraint);
		} else {
			box.setX(rootBox.getX()
					+ rootBoxConstraints.getPaddingLeft().getValueAsInt(
							rootBox.getWidth()));
			box.setWidth(rootBox.getWidth()
					- rootBoxConstraints.getPaddingLeft().getValueAsInt(
							rootBox.getWidth())
					- rootBoxConstraints.getPaddingRight().getValueAsInt(
							rootBox.getWidth()));
		}
	}

	void handleVerticalAlignment(final BoxSize rootBox,
			final LayoutConstraints rootBoxConstraints, final BoxSize box,
			final LayoutConstraints constraint) {
		if (constraint.getHeight() != null) {
			handleHeightConstraint(rootBox, rootBoxConstraints, box, constraint);
		} else {
			box.setY(rootBox.getY()
					+ rootBoxConstraints.getPaddingTop().getValueAsInt(
							rootBox.getHeight()));
			box.setHeight(rootBox.getHeight()
					- rootBoxConstraints.getPaddingTop().getValueAsInt(
							rootBox.getHeight())
					- rootBoxConstraints.getPaddingBottom().getValueAsInt(
							rootBox.getHeight()));
		}
	}

	private void handleWidthConstraint(final BoxSize rootBox,
			final LayoutConstraints rootBoxConstraints, final BoxSize box,
			final LayoutConstraints constraint) {
		int rootBoxX = (int) (rootBox.getX()
				+ rootBoxConstraints.getPaddingLeft().getValueAsInt(
						rootBox.getWidth()));
		int rootBoxWidth = (int) (rootBox.getWidth()
				- rootBoxConstraints.getPaddingLeft().getValueAsInt(
						rootBox.getWidth())
				- rootBoxConstraints.getPaddingRight().getValueAsInt(
						rootBox.getWidth()));

		int boxWidth = (int) constraint.getWidth().getValue(rootBoxWidth);
		if (constraint.getWidth().hasHeightSuffix()) {
			boxWidth = (int) constraint.getWidth().getValue(box.getHeight());
		}
		box.setWidth(boxWidth);

		if (constraint.getHorizontalAlign() == HorizontalAlign.left) {
			box.setX(rootBoxX);
		} else if (constraint.getHorizontalAlign() == HorizontalAlign.right) {
			box.setX(rootBoxX
					+ rootBox.getWidth()
					- rootBoxConstraints.getPaddingRight().getValueAsInt(
							rootBox.getWidth()) - boxWidth);
		} else {
			box.setX(rootBoxX + (rootBoxWidth - boxWidth) / 2);
		}
	}

	private void handleHeightConstraint(final BoxSize rootBox,
			final LayoutConstraints rootBoxConstraints, final BoxSize box,
			final LayoutConstraints constraint) {
		int rootBoxY = (int) (rootBox.getY()
				+ rootBoxConstraints.getPaddingTop().getValueAsInt(
						rootBox.getHeight()));
		int rootBoxHeight = (int) (rootBox.getHeight()
				- rootBoxConstraints.getPaddingTop().getValueAsInt(
						rootBox.getHeight())
				- rootBoxConstraints.getPaddingBottom().getValueAsInt(
						rootBox.getHeight()));

		int boxHeight = (int) constraint.getHeight().getValue(rootBoxHeight);
		if (constraint.getHeight().hasWidthSuffix()) {
			boxHeight = (int) constraint.getHeight().getValue(box.getWidth());
		}
		box.setHeight(boxHeight);

		if (constraint.getVerticalAlign() == VerticalAlign.top) {
			box.setY(rootBoxY);
		} else if (constraint.getVerticalAlign() == VerticalAlign.bottom) {
			box.setY(rootBox.getHeight()
					- rootBoxConstraints.getPaddingTop().getValueAsInt(
							rootBox.getHeight()) - boxHeight);
		} else {
			box.setY(rootBoxY + (rootBoxHeight - boxHeight) / 2);
		}
	}

	public SizeValue calculateConstraintWidth(final LayoutPort root,
			final TArray<LayoutPort> children) {
		if (children.isEmpty()) {
			return null;
		}
		LayoutPort firstChild = children.get(0);
		if (firstChild == null) {
			return null;
		}
		LayoutConstraints constraint = firstChild.getBoxConstraints();
		if (constraint == null) {
			return null;
		}
		return new SizeValue(constraint.getWidth().getValueAsInt(0)
				+ root.getBoxConstraints().getPaddingLeft()
						.getValueAsInt(root.getBox().getWidth())
				+ root.getBoxConstraints().getPaddingRight()
						.getValueAsInt(root.getBox().getWidth()) + "px");
	}

	public SizeValue calculateConstraintHeight(final LayoutPort root,
			final TArray<LayoutPort> children) {
		if (children.isEmpty()) {
			return null;
		}
		LayoutPort firstChild = children.get(0);
		if (firstChild == null) {
			return null;
		}
		LayoutConstraints constraint = firstChild.getBoxConstraints();
		if (constraint == null) {
			return null;
		}
		return new SizeValue(constraint.getHeight().getValueAsInt(0)
				+ root.getBoxConstraints().getPaddingTop()
						.getValueAsInt(root.getBox().getHeight())
				+ root.getBoxConstraints().getPaddingBottom()
						.getValueAsInt(root.getBox().getHeight()) + "px");

	}
}
