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

import loon.HorizontalAlign;
import loon.geom.SizeValue;
import loon.utils.TArray;

public class VerticalLayout extends LayoutManager {

	public final static VerticalLayout at(){
		return new VerticalLayout();
	}
	
	public void layoutElements(final LayoutPort root,
			final LayoutPort... children) {
		if (isInvalid(root, children)) {
			return;
		}
		int rootBoxX = getRootBoxX(root);
		int rootBoxY = getRootBoxY(root);
		int rootBoxWidth = getRootBoxWidth(root);
		int rootBoxHeight = getRootBoxHeight(root);

		int getY = rootBoxY;
		for (int i = 0; i < children.length; i++) {
			BoxSize currentBox = children[i].getBox();
			LayoutConstraints currentBoxConstraints = children[i]
					.getBoxConstraints();

			int elementHeight;

			if (hasHeightConstraint(currentBoxConstraints)
					&& currentBoxConstraints.getHeight().hasWidthSuffix()) {
				int elementWidth = processWidthConstraints(rootBoxWidth,
						currentBoxConstraints, 0);
				if (_allow) {
					currentBox.setWidth(elementWidth);
				}

				elementHeight = calcElementHeight(new TArray<LayoutPort>(
						children), rootBoxHeight, currentBoxConstraints,
						elementWidth);
				if (_allow) {
					currentBox.setHeight(elementHeight);
				}
			} else if (hasWidthConstraint(currentBoxConstraints)
					&& currentBoxConstraints.getWidth().hasHeightSuffix()) {
				elementHeight = calcElementHeight(new TArray<LayoutPort>(
						children), rootBoxHeight, currentBoxConstraints, 0);
				if (_allow) {
					currentBox.setHeight(elementHeight);
				}

				int elementWidth = processWidthConstraints(rootBoxWidth,
						currentBoxConstraints, elementHeight);
				if (_allow) {
					currentBox.setWidth(elementWidth);
				}
			} else {
				int elementWidth = processWidthConstraints(rootBoxWidth,
						currentBoxConstraints, 0);
				if (_allow) {
					currentBox.setWidth(elementWidth);
				}

				elementHeight = calcElementHeight(new TArray<LayoutPort>(
						children), rootBoxHeight, currentBoxConstraints, 0);
				if (_allow) {
					currentBox.setHeight(elementHeight);
				}
			}

			currentBox.setX(processHorizontalAlignment(rootBoxX, rootBoxWidth,
					(int) currentBox.getWidth(), currentBoxConstraints));
			currentBox.setY(getY);

			getY += elementHeight;
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
		if (HorizontalAlign.CENTER.equals(constraints.getHorizontalAlign())) {
			return rootBoxX + ((rootBoxWidth - currentBoxWidth) / 2);
		} else if (HorizontalAlign.RIGHT.equals(constraints
				.getHorizontalAlign())) {
			return rootBoxX + (rootBoxWidth - currentBoxWidth);
		} else if (HorizontalAlign.LEFT
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
