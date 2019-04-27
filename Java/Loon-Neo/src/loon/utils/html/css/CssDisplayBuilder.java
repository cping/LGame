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
import loon.LTexture;
import loon.opengl.GLEx;
import loon.utils.TArray;
import loon.utils.html.css.CssDimensions.Rect;

public class CssDisplayBuilder {

	TArray<CssCmd> list = null;

	LTexture texture;

	float screenWidth;

	float screenHeight;

	public TArray<CssCmd> build(CssLayout rootBox) {
		texture = LSystem.base().graphics().finalColorTex();
		list = new TArray<CssCmd>();
		renderLayoutBox(rootBox);
		return list;
	}

	private void renderLayoutBox(CssLayout rootBox) {

		screenWidth = rootBox.dimensions.content.width;
		screenHeight = rootBox.dimensions.content.height;

		renderBackground(rootBox);
		list.addAll(renderBorders(rootBox));

		if (rootBox.getStyleNode() != null) {
			renderText(rootBox);

		}

		for (CssLayout child : rootBox.children) {
			renderLayoutBox(child);
		}
	}

	private void renderText(CssLayout rootBox) {

		CssTextCmd command = new CssTextCmd(screenWidth,screenHeight);
		command.rect = rootBox.dimensions.borderBox();
		command.text = rootBox.getStyleNode().node.getData();

		list.add(command);
	}

	private TArray<CssCmd> renderBorders(CssLayout rootBox) {

		TArray<CssCmd> list = new TArray<CssCmd>();

		CssColor borderColor = (CssColor) getColor(rootBox, "border-color");

		CssDimensions dims = rootBox.dimensions;

		Rect borderBox = rootBox.dimensions.borderBox();

		CssColorCmd leftBorderCommand = new CssColorCmd(screenWidth,screenHeight);
		leftBorderCommand.color = borderColor;

		Rect leftBorderRect = new Rect();
		leftBorderRect.x = borderBox.x;
		leftBorderRect.y = borderBox.y;
		leftBorderRect.width = dims.border.left;
		leftBorderRect.height = borderBox.height;

		leftBorderCommand.rect = rootBox.dimensions.borderBox();
		list.add(leftBorderCommand);

		CssColorCmd rightBorderCommand = new CssColorCmd(screenWidth,screenHeight);
		rightBorderCommand.color = borderColor;

		Rect rightBorderRect = new Rect();
		rightBorderRect.x = borderBox.x + borderBox.width - dims.border.right;
		rightBorderRect.y = borderBox.y;
		rightBorderRect.width = dims.border.right;
		rightBorderRect.height = borderBox.height;

		rightBorderCommand.rect = rootBox.dimensions.borderBox();
		list.add(rightBorderCommand);

		CssColorCmd topBorderCommand = new CssColorCmd(screenWidth,screenHeight);
		topBorderCommand.color = borderColor;

		Rect topBorderRect = new Rect();
		topBorderRect.x = borderBox.x;
		topBorderRect.y = borderBox.y;
		topBorderRect.width = borderBox.width;
		topBorderRect.height = dims.border.top;

		topBorderCommand.rect = rootBox.dimensions.borderBox();
		list.add(topBorderCommand);

		CssColorCmd bottomBorderCommand = new CssColorCmd(screenWidth,screenHeight);
		bottomBorderCommand.color = borderColor;

		Rect bottomBorderRect = new Rect();
		bottomBorderRect.x = borderBox.x;
		bottomBorderRect.y = borderBox.y + borderBox.height - dims.border.bottom;
		bottomBorderRect.width = borderBox.width;
		bottomBorderRect.height = dims.border.bottom;

		bottomBorderCommand.rect = rootBox.dimensions.borderBox();
		list.add(bottomBorderCommand);

		return list;
	}

	private void renderBackground(CssLayout rootBox) {

		CssColor backgroundColor = (CssColor) getColor(rootBox, "background");

		CssColorCmd command = new CssColorCmd(screenWidth,screenHeight);
		command.color = backgroundColor;
		command.rect = rootBox.dimensions.borderBox();

		list.add(command);
	}

	private CssValue getColor(CssLayout box, String name) {

		CssColor defaultColor = new CssColor(0, 0, 0, 0);

		if (box.layoutType instanceof CssBlockType || box.layoutType instanceof CssInlineType) {
			return box.getStyleNode().find(defaultColor, name);
		}
		return null;
	}

	public void paint(GLEx g, float x, float y) {
		if (list == null) {
			return;
		}
		for (CssCmd cmd : list) {
			cmd.paint(g, x, y);
		}
	}

}
