/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.component.layout;

import loon.geom.BoxSize;
import loon.geom.SizeValue;
import loon.utils.TArray;

public class CenterLayout extends LayoutManager {

	public final static CenterLayout at() {
		return new CenterLayout();
	}

	@Override
	public LayoutManager layoutElements(final LayoutPort rootElement, final LayoutPort... elements) {

		if (rootElement == null || elements == null || elements.length == 0) {
			return this;
		}

		BoxSize rootBox = rootElement.getBox();
		LayoutConstraints rootBoxConstraints = rootElement.getBoxConstraints();

		BoxSize box = elements[0].getBox();
		LayoutConstraints constraint = elements[0].getBoxConstraints();

		if (constraint.getWidth() != null && constraint.getWidth().hasHeightSuffix()) {
			handleVerticalAlignment(rootBox, rootBoxConstraints, box, constraint);
			handleHorizontalAlignment(rootBox, rootBoxConstraints, box, constraint);
		} else if (constraint.getHeight() != null && constraint.getHeight().hasWidthSuffix()) {
			handleHorizontalAlignment(rootBox, rootBoxConstraints, box, constraint);
			handleVerticalAlignment(rootBox, rootBoxConstraints, box, constraint);
		} else {
			handleVerticalAlignment(rootBox, rootBoxConstraints, box, constraint);
			handleHorizontalAlignment(rootBox, rootBoxConstraints, box, constraint);
		}
		return this;
	}

	void handleHorizontalAlignment(final BoxSize rootBox, final LayoutConstraints rootBoxConstraints, final BoxSize box,
			final LayoutConstraints constraint) {
		if (constraint.getWidth() != null) {
			handleWidthConstraint(rootBox, rootBoxConstraints, box, constraint);
		} else {
			box.setX(rootBox.getX() + rootBoxConstraints.getPaddingLeft().getValueAsInt(rootBox.getWidth()));
			if (_allow) {
				box.setWidth(rootBox.getWidth() - rootBoxConstraints.getPaddingLeft().getValueAsInt(rootBox.getWidth())
						- rootBoxConstraints.getPaddingRight().getValueAsInt(rootBox.getWidth()));
			}
		}
	}

	void handleVerticalAlignment(final BoxSize rootBox, final LayoutConstraints rootBoxConstraints, final BoxSize box,
			final LayoutConstraints constraint) {
		if (constraint.getHeight() != null) {
			handleHeightConstraint(rootBox, rootBoxConstraints, box, constraint);
		} else {
			box.setY(rootBox.getY() + rootBoxConstraints.getPaddingTop().getValueAsInt(rootBox.getHeight()));
			if (_allow) {
				box.setHeight(
						rootBox.getHeight() - rootBoxConstraints.getPaddingTop().getValueAsInt(rootBox.getHeight())
								- rootBoxConstraints.getPaddingBottom().getValueAsInt(rootBox.getHeight()));
			}
		}
	}

	private void handleWidthConstraint(final BoxSize rootBox, final LayoutConstraints rootBoxConstraints,
			final BoxSize box, final LayoutConstraints constraint) {
		int rootBoxX = (int) (rootBox.getX() + rootBoxConstraints.getPaddingLeft().getValueAsInt(rootBox.getWidth()));
		int rootBoxWidth = (int) (rootBox.getWidth()
				- rootBoxConstraints.getPaddingLeft().getValueAsInt(rootBox.getWidth())
				- rootBoxConstraints.getPaddingRight().getValueAsInt(rootBox.getWidth()));

		int boxWidth = (int) constraint.getWidth().getValue(rootBoxWidth);
		if (constraint.getWidth().hasHeightSuffix()) {
			boxWidth = (int) constraint.getWidth().getValue(box.getHeight());
		}
		if (_allow) {
			box.setWidth(boxWidth);
		}

		if (constraint.getHorizontalAlign() == HorizontalAlign.LEFT) {
			box.setX(rootBoxX);
		} else if (constraint.getHorizontalAlign() == HorizontalAlign.RIGHT) {
			box.setX(rootBoxX + rootBox.getWidth()
					- rootBoxConstraints.getPaddingRight().getValueAsInt(rootBox.getWidth()) - boxWidth);
		} else {
			box.setX(rootBoxX + (rootBoxWidth - boxWidth) / 2);
		}
	}

	private void handleHeightConstraint(final BoxSize rootBox, final LayoutConstraints rootBoxConstraints,
			final BoxSize box, final LayoutConstraints constraint) {
		int rootBoxY = (int) (rootBox.getY() + rootBoxConstraints.getPaddingTop().getValueAsInt(rootBox.getHeight()));
		int rootBoxHeight = (int) (rootBox.getHeight()
				- rootBoxConstraints.getPaddingTop().getValueAsInt(rootBox.getHeight())
				- rootBoxConstraints.getPaddingBottom().getValueAsInt(rootBox.getHeight()));

		int boxHeight = (int) constraint.getHeight().getValue(rootBoxHeight);
		if (constraint.getHeight().hasWidthSuffix()) {
			boxHeight = (int) constraint.getHeight().getValue(box.getWidth());
		}
		if (_allow) {
			box.setHeight(boxHeight);
		}
		if (constraint.getVerticalAlign() == VerticalAlign.TOP) {
			box.setY(rootBoxY);
		} else if (constraint.getVerticalAlign() == VerticalAlign.BOTTOM) {
			box.setY(rootBox.getHeight() - rootBoxConstraints.getPaddingTop().getValueAsInt(rootBox.getHeight())
					- boxHeight);
		} else {
			box.setY(rootBoxY + (rootBoxHeight - boxHeight) / 2);
		}
	}

	@Override
	public SizeValue calculateConstraintWidth(final LayoutPort root, final TArray<LayoutPort> children) {
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
				+ root.getBoxConstraints().getPaddingLeft().getValueAsInt(root.getBox().getWidth())
				+ root.getBoxConstraints().getPaddingRight().getValueAsInt(root.getBox().getWidth()) + "px");
	}

	@Override
	public SizeValue calculateConstraintHeight(final LayoutPort root, final TArray<LayoutPort> children) {
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
				+ root.getBoxConstraints().getPaddingTop().getValueAsInt(root.getBox().getHeight())
				+ root.getBoxConstraints().getPaddingBottom().getValueAsInt(root.getBox().getHeight()) + "px");

	}
}
