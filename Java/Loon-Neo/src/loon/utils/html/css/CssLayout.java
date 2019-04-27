/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.utils.html.css;

import loon.LSystem;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class CssLayout {

	protected CssDimensions dimensions;

	protected CssLayoutType layoutType;

	protected TArray<CssLayout> children;

	protected int defaultHeight = 5;

	public CssLayout(CssLayoutType newType) {
		this.dimensions = new CssDimensions();
		this.children = new TArray<CssLayout>();
		layoutType = newType;
	}

	public CssLayoutType getLayoutType() {
		return layoutType;
	}

	public CssStyleNode getStyleNode() {

		return layoutType.accept(new CssLayoutTypeVisitor<CssStyleNode>() {

			@Override
			public CssStyleNode visit(CssBlockType box) {
				return box.styledNode;
			}

			@Override
			public CssStyleNode visit(CssInlineType box) {
				return box.styledNode;
			}

			@Override
			public CssStyleNode visit(CssAnonymousType box) {
				return null;
			}
		});

	}

	public CssLayout getInlineContainer() {
		if (layoutType instanceof CssAnonymousType || layoutType instanceof CssInlineType) {
			return this;
		} else if (layoutType instanceof CssBlockType) {
			children.add(new CssLayout(new CssAnonymousType()));
			return this;
		} else {
			return null;
		}
	}

	private void calculateBlockPosition(CssDimensions conBlock) {

		CssStyleNode style = getStyleNode();

		CssLength zeroLength = new CssLength(0.0f, CssUnit.PX());

		this.dimensions.margin.top = (style.find(zeroLength, "margin-top", "margin")).toPx();
		this.dimensions.margin.bottom = (style.find(zeroLength, "margin-bottom", "margin")).toPx();

		this.dimensions.border.top = (style.find(zeroLength, "border-top-width", "border-width")).toPx();
		this.dimensions.border.bottom = ((CssLength) style.find(zeroLength, "border-bottom-width", "border-width"))
				.toPx();

		this.dimensions.padding.top = (style.find(zeroLength, "padding-top", "padding")).toPx();
		this.dimensions.padding.bottom = (style.find(zeroLength, "padding-bottom", "padding")).toPx();

		this.dimensions.content.x = conBlock.content.x + this.dimensions.margin.left + this.dimensions.border.left
				+ this.dimensions.padding.left;

		this.dimensions.content.y = conBlock.content.y + conBlock.content.height + this.dimensions.margin.top
				+ this.dimensions.border.top + this.dimensions.padding.top;
	}

	private void calculateBlockWidth(CssDimensions conBlock) {

		CssStyleNode style = getStyleNode();

		CssValue initWidth = new CssKeyword("auto");
		CssValue width = style.find(initWidth, "width");
		CssLength zeroLength = new CssLength(0.0f, CssUnit.PX());

		CssValue leftMargin = style.find(zeroLength, "margin-left", "margin");
		CssValue rightMargin = style.find(zeroLength, "margin-right", "margin");

		CssValue leftBorder = style.find(zeroLength, "border-left-width", "border-width");
		CssValue rightBorder = style.find(zeroLength, "border-right-width", "border-width");

		CssValue leftPadding = style.find(zeroLength, "padding-left", "padding");
		CssValue rightPadding = style.find(zeroLength, "padding-right", "padding");

		float total = leftMargin.toPx() + rightMargin.toPx() + leftBorder.toPx() + rightBorder.toPx()
				+ leftPadding.toPx() + rightPadding.toPx() + width.toPx();

		if (!width.getValueString().equals("auto") && total > conBlock.content.width) {
			if (leftMargin.getValueString().equals("auto")) {
				leftMargin = new CssLength(0.0f, CssUnit.PX());
			}
			if (rightMargin.getValueString().equals("auto")) {
				rightMargin = new CssLength(0.0f, CssUnit.PX());
			}
		}

		float underflow = conBlock.content.width - total;

		boolean widthAuto, marginLeftAuto, marginRightAuto;

		widthAuto = width.getValueString().equals("auto");
		marginLeftAuto = leftMargin.getValueString().equals("auto");
		marginRightAuto = rightMargin.getValueString().equals("auto");

		if (!widthAuto & !marginLeftAuto & !marginRightAuto) {
			rightMargin = new CssLength(rightMargin.toPx() + underflow, CssUnit.PX());
		} else if ((!widthAuto & !marginLeftAuto & marginRightAuto)) {
			rightMargin = new CssLength(underflow, CssUnit.PX());
		} else if ((!widthAuto & marginLeftAuto & !marginRightAuto)) {
			leftMargin = new CssLength(underflow, CssUnit.PX());
		} else if ((!widthAuto & marginLeftAuto & marginRightAuto)) {
			leftMargin = new CssLength(underflow / 2, CssUnit.PX());
			rightMargin = new CssLength(underflow / 2, CssUnit.PX());
		} else if (widthAuto) {
			if (marginLeftAuto) {
				leftMargin = new CssLength(0.0f, CssUnit.PX());
			}
			if (marginRightAuto) {
				rightMargin = new CssLength(0.0f, CssUnit.PX());
			}
			if (underflow >= 0.0f) {
				width = new CssLength(underflow, CssUnit.PX());
			} else {
				width = new CssLength(0.0f, CssUnit.PX());
				rightMargin = new CssLength(rightMargin.toPx() + underflow, CssUnit.PX());
			}
		}

		this.dimensions.content.width = width.toPx();
		this.dimensions.content.height = defaultHeight;
		this.dimensions.padding.left = leftPadding.toPx();
		this.dimensions.padding.right = rightPadding.toPx();
		this.dimensions.margin.right = rightMargin.toPx();
		this.dimensions.margin.left = leftMargin.toPx();

	}

	private void layoutBlock(CssDimensions conBlock) {

		calculateBlockWidth(conBlock);

		calculateBlockPosition(conBlock);

		layoutBlockChildren();

		calculateBlockHeight();

	}

	public void layout(CssDimensions conBlock) {
		if (layoutType instanceof CssBlockType) {
			layoutBlock(conBlock);
		} else if (layoutType instanceof CssInlineType) {
			layoutBlock(conBlock);
		} else {
			// anonymous
		}
	}

	private void calculateBlockHeight() {
		CssStyleNode style = getStyleNode();
		CssValue height = style.values.get("height");
		if (height != null && height.toPx() != 0.0f) {
			this.dimensions.content.height = height.toPx();
		}
	}

	private void layoutBlockChildren() {
		int minHeight = MathUtils.max(defaultHeight, LSystem.getSystemGameFont().getHeight());
		if (children.size() > 0) {
			for (CssLayout child : children) {
				child.layout(dimensions);
				this.dimensions.content.height += child.dimensions.extraBox().height;
			}
		} else {
			this.dimensions.content.height = minHeight + this.dimensions.content.height
					+ this.dimensions.extraBox().height;
		}
	}
}
