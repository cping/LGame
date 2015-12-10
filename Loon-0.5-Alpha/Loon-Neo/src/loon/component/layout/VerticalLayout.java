package loon.component.layout;

import loon.geom.RectBox;
import loon.geom.SizeValue;
import loon.utils.TArray;

public class VerticalLayout implements LayoutManager {

	public void layoutElements(final LayoutPort root,
			final TArray<LayoutPort> children) {
		if (isInvalid(root, children)) {
			return;
		}

		int rootBoxX = getRootBoxX(root);
		int rootBoxY = getRootBoxY(root);
		int rootBoxWidth = getRootBoxWidth(root);
		int rootBoxHeight = getRootBoxHeight(root);

		int y = rootBoxY;
		for (int i = 0; i < children.size; i++) {
			RectBox currentBox = children.get(i).getBox();
			LayoutConstraints currentBoxConstraints = children.get(i)
					.getBoxConstraints();

			int elementHeight;

			if (hasHeightConstraint(currentBoxConstraints)
					&& currentBoxConstraints.getHeight().hasWidthSuffix()) {
				int elementWidth = processWidthConstraints(rootBoxWidth,
						currentBoxConstraints, 0);
				currentBox.setWidth(elementWidth);

				elementHeight = calcElementHeight(children, rootBoxHeight,
						currentBoxConstraints, elementWidth);
				currentBox.setHeight(elementHeight);
			} else if (hasWidthConstraint(currentBoxConstraints)
					&& currentBoxConstraints.getWidth().hasHeightSuffix()) {
				elementHeight = calcElementHeight(children, rootBoxHeight,
						currentBoxConstraints, 0);
				currentBox.setHeight(elementHeight);

				int elementWidth = processWidthConstraints(rootBoxWidth,
						currentBoxConstraints, elementHeight);
				currentBox.setWidth(elementWidth);
			} else {
				int elementWidth = processWidthConstraints(rootBoxWidth,
						currentBoxConstraints, 0);
				currentBox.setWidth(elementWidth);

				elementHeight = calcElementHeight(children, rootBoxHeight,
						currentBoxConstraints, 0);
				currentBox.setHeight(elementHeight);
			}

			currentBox.setX(processHorizontalAlignment(rootBoxX, rootBoxWidth,
					currentBox.width(), currentBoxConstraints));
			currentBox.setY(y);

			y += elementHeight;
		}
	}

	public SizeValue calculateConstraintWidth(final LayoutPort root,
			final TArray<LayoutPort> children) {
		if (children.size == 0) {
			return null;
		}
		int newWidth = 0;
		for (LayoutPort e : children) {
			newWidth += e.getBoxConstraints().getWidth().getValueAsInt(0);
		}
		newWidth += root.getBoxConstraints().getPaddingLeft()
				.getValueAsInt(root.getBox().getWidth());
		newWidth += root.getBoxConstraints().getPaddingRight()
				.getValueAsInt(root.getBox().getWidth());

		return new SizeValue(newWidth + "px");
	}

	public SizeValue calculateConstraintHeight(final LayoutPort root,
			final TArray<LayoutPort> children) {
		int newHeight = 0;
		for (LayoutPort e : children) {
			newHeight += e.getBoxConstraints().getHeight().getValueAsInt(0);
		}
		newHeight += root.getBoxConstraints().getPaddingTop()
				.getValueAsInt(root.getBox().getHeight());
		newHeight += root.getBoxConstraints().getPaddingBottom()
				.getValueAsInt(root.getBox().getHeight());

		return new SizeValue(newHeight + "px");
	}

	private int processWidthConstraints(final int rootBoxWidth,
			final LayoutConstraints constraints, final int elementHeight) {
		if (hasWidthConstraint(constraints)) {
			if (constraints.getWidth().hasHeightSuffix()) {
				return constraints.getWidth().getValueAsInt(elementHeight);
			}
			return constraints.getWidth().getValueAsInt(rootBoxWidth);
		} else {
			return rootBoxWidth;
		}
	}

	private int processHorizontalAlignment(final int rootBoxX,
			final int rootBoxWidth, final int currentBoxWidth,
			final LayoutConstraints constraints) {
		if (HorizontalAlign.center.equals(constraints.getHorizontalAlign())) {
			return rootBoxX + ((rootBoxWidth - currentBoxWidth) / 2);
		} else if (HorizontalAlign.right.equals(constraints
				.getHorizontalAlign())) {
			return rootBoxX + (rootBoxWidth - currentBoxWidth);
		} else if (HorizontalAlign.left
				.equals(constraints.getHorizontalAlign())) {
			return rootBoxX;
		} else {
			return rootBoxX;
		}
	}

	private int calcElementHeight(final TArray<LayoutPort> children,
			final int rootBoxHeight, final LayoutConstraints boxConstraints,
			final int boxWidth) {
		if (hasHeightConstraint(boxConstraints)) {
			int h;
			if (boxConstraints.getHeight().hasWidthSuffix()) {
				h = boxConstraints.getHeight().getValueAsInt(boxWidth);
			} else {
				h = boxConstraints.getHeight().getValueAsInt(rootBoxHeight);
			}
			if (h != -1) {
				return h;
			}
		}
		return getMaxNonFixedHeight(children, rootBoxHeight);
	}

	private int getMaxNonFixedHeight(final TArray<LayoutPort> elements,
			final int parentHeight) {
		int maxFixedHeight = 0;
		int fixedCount = 0;

		for (int i = 0; i < elements.size; i++) {
			LayoutPort p = elements.get(i);
			LayoutConstraints original = p.getBoxConstraints();
			if (hasHeightConstraint(original)) {
				if (original.getHeight().isPercentOrPixel()) {
					maxFixedHeight += original.getHeight().getValue(
							parentHeight);
					fixedCount++;
				}
			}
		}

		int notFixedCount = elements.size - fixedCount;
		if (notFixedCount > 0) {
			return (parentHeight - maxFixedHeight) / notFixedCount;
		} else {
			return (parentHeight - maxFixedHeight);
		}
	}

	private boolean hasWidthConstraint(final LayoutConstraints constraints) {
		return constraints != null && constraints.getWidth() != null
				&& !constraints.getWidth().hasWildcard();
	}

	private boolean hasHeightConstraint(final LayoutConstraints boxConstraints) {
		return boxConstraints != null && boxConstraints.getHeight() != null;
	}

	private boolean isInvalid(final LayoutPort root,
			final TArray<LayoutPort> children) {
		return root == null || children == null || children.size == 0;
	}

	private int getRootBoxX(final LayoutPort root) {
		return root.getBox().x()
				+ root.getBoxConstraints().getPaddingLeft()
						.getValueAsInt(root.getBox().getWidth());
	}

	private int getRootBoxY(final LayoutPort root) {
		return root.getBox().y()
				+ root.getBoxConstraints().getPaddingTop()
						.getValueAsInt(root.getBox().getHeight());
	}

	private int getRootBoxWidth(final LayoutPort root) {
		return root.getBox().width()
				- root.getBoxConstraints().getPaddingLeft()
						.getValueAsInt(root.getBox().getWidth())
				- root.getBoxConstraints().getPaddingRight()
						.getValueAsInt(root.getBox().getWidth());
	}

	private int getRootBoxHeight(final LayoutPort root) {
		return root.getBox().height()
				- root.getBoxConstraints().getPaddingTop()
						.getValueAsInt(root.getBox().getHeight())
				- root.getBoxConstraints().getPaddingBottom()
						.getValueAsInt(root.getBox().getHeight());
	}
}
