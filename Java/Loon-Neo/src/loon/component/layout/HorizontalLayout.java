package loon.component.layout;

import loon.VerticalAlign;
import loon.geom.SizeValue;
import loon.utils.TArray;

public class HorizontalLayout extends LayoutManager {

	public final static HorizontalLayout at(){
		return new HorizontalLayout();
	}
	
	public final void layoutElements(final LayoutPort root,
			final LayoutPort... children) {
		if (isInvalid(root, children)) {
			return;
		}

		int rootBoxX = getRootBoxX(root);
		int rootBoxY = getRootBoxY(root);
		int rootBoxWidth = getRootBoxWidth(root);
		int rootBoxHeight = getRootBoxHeight(root);

		int getX = rootBoxX;
		for (int i = 0; i < children.length; i++) {
			LayoutPort current = children[i];
			BoxSize box = current.getBox();
			LayoutConstraints boxConstraints = current.getBoxConstraints();

			int elementWidth;
			if (boxConstraints.getWidth() != null
					&& boxConstraints.getWidth().hasHeightSuffix()) {
				int elementHeight = processHeightConstraint(rootBoxHeight, box,
						boxConstraints, 0);
				if (_allow) {
					box.setHeight(elementHeight);
				}

				elementWidth = calcElementWidth(
						new TArray<LayoutPort>(children), rootBoxWidth,
						boxConstraints, elementHeight);
				if (_allow) {
					box.setWidth(elementWidth);
				}
			} else if (hasHeightConstraint(boxConstraints)
					&& boxConstraints.getHeight().hasWidthSuffix()) {
				elementWidth = calcElementWidth(
						new TArray<LayoutPort>(children), rootBoxWidth,
						boxConstraints, 0);
				if (_allow) {
					box.setWidth(elementWidth);
				}

				int elementHeight = processHeightConstraint(rootBoxHeight, box,
						boxConstraints, elementWidth);
				if (_allow) {
					box.setHeight(elementHeight);
				}
			} else {
				elementWidth = calcElementWidth(
						new TArray<LayoutPort>(children), rootBoxWidth,
						boxConstraints, 0);
				if (_allow) {
					box.setWidth(elementWidth);
				}

				int elementHeight = processHeightConstraint(rootBoxHeight, box,
						boxConstraints, 0);
				if (_allow) {
					box.setHeight(elementHeight);
				}
			}

			box.setY(processVerticalAlignment(rootBoxY, rootBoxHeight, box,
					boxConstraints));
			box.setX(getX);

			getX += elementWidth;
		}
	}

	private int processHeightConstraint(final int rootBoxHeight,
			final BoxSize box, final LayoutConstraints constraint,
			final int elementWidth) {
		if (hasHeightConstraint(constraint)) {
			if (constraint.getHeight().hasWidthSuffix()) {
				return constraint.getHeight().getValueAsInt(elementWidth);
			}
			return constraint.getHeight().getValueAsInt(rootBoxHeight);
		} else {
			return rootBoxHeight;
		}
	}

	private boolean hasHeightConstraint(final LayoutConstraints constraint) {
		return constraint != null && constraint.getHeight() != null
				&& !constraint.getHeight().hasWildcard();
	}

	private int calcElementWidth(final TArray<LayoutPort> children,
			final int rootBoxWidth, final LayoutConstraints boxConstraints,
			final int elementHeight) {
		if (boxConstraints.getWidth() != null) {
			int h = (int) boxConstraints.getWidth().getValue(rootBoxWidth);
			if (boxConstraints.getWidth().hasHeightSuffix()) {
				h = (int) boxConstraints.getWidth().getValue(elementHeight);
			}
			if (h != -1) {
				return h;
			}
		}
		return getMaxNonFixedWidth(children, rootBoxWidth);
	}

	private int processVerticalAlignment(final int rootBoxY,
			final int rootBoxHeight, final BoxSize box,
			final LayoutConstraints boxConstraints) {
		if (VerticalAlign.CENTER.equals(boxConstraints.getVerticalAlign())) {
			return (int) (rootBoxY + ((rootBoxHeight - box.getHeight()) / 2));
		} else if (VerticalAlign.TOP.equals(boxConstraints.getVerticalAlign())) {
			return rootBoxY;
		} else if (VerticalAlign.BOTTOM.equals(boxConstraints
				.getVerticalAlign())) {
			return (int) (rootBoxY + (rootBoxHeight - box.getHeight()));
		} else {
			return rootBoxY;
		}
	}

	private int getMaxNonFixedWidth(final TArray<LayoutPort> elements,
			final int parentWidth) {
		int maxFixedWidth = 0;
		int fixedCount = 0;
		for (int i = 0; i < elements.size; i++) {
			LayoutPort p = elements.get(i);
			LayoutConstraints original = p.getBoxConstraints();

			if (original.getWidth() != null) {
				if (original.getWidth().isPercentOrPixel()) {
					maxFixedWidth += original.getWidth().getValue(parentWidth);
					fixedCount++;
				}
			}
		}

		int notFixedCount = elements.size - fixedCount;
		if (notFixedCount > 0) {
			return (parentWidth - maxFixedWidth) / notFixedCount;
		} else {
			return (parentWidth - maxFixedWidth);
		}
	}

	public final SizeValue calculateConstraintWidth(final LayoutPort root,
			final TArray<LayoutPort> children) {
		return null;
	}

	public final SizeValue calculateConstraintHeight(final LayoutPort root,
			final TArray<LayoutPort> children) {
		int newHeight = 0;
		for (LayoutPort e : children) {
			int partHeight = e.getBoxConstraints().getHeight().getValueAsInt(0)
					- e.getBoxConstraints().getPaddingTop()
							.getValueAsInt(root.getBox().getHeight())
					- e.getBoxConstraints().getPaddingBottom()
							.getValueAsInt(root.getBox().getHeight());
			if (partHeight > newHeight) {
				newHeight = partHeight;
			}
		}
		return new SizeValue(newHeight + "px");
	}

	private boolean isInvalid(final LayoutPort root,
			final LayoutPort... children) {
		return root == null || children == null || children.length == 0;
	}

	private int getRootBoxX(final LayoutPort root) {
		return (int) (root.getBox().getX() + root.getBoxConstraints()
				.getPaddingLeft().getValueAsInt(root.getBox().getWidth()));
	}

	private int getRootBoxY(final LayoutPort root) {
		return (int) (root.getBox().getY() + root.getBoxConstraints()
				.getPaddingTop().getValueAsInt(root.getBox().getHeight()));
	}

	private int getRootBoxWidth(final LayoutPort root) {
		return (int) (root.getBox().getWidth()
				- root.getBoxConstraints().getPaddingLeft()
						.getValueAsInt(root.getBox().getWidth()) - root
				.getBoxConstraints().getPaddingRight()
				.getValueAsInt(root.getBox().getWidth()));
	}

	private int getRootBoxHeight(final LayoutPort root) {
		return (int) (root.getBox().getHeight()
				- root.getBoxConstraints().getPaddingTop()
						.getValueAsInt(root.getBox().getHeight()) - root
				.getBoxConstraints().getPaddingBottom()
				.getValueAsInt(root.getBox().getHeight()));
	}
}
