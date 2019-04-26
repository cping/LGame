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

public class CssLayoutBuilder {

	public CssLayout layoutTree(CssStyleNode node, CssDimensions containingBlock) {

		containingBlock.content.height = 0.0f;

		CssLayout rootBox;
		try {
			rootBox = buildLayoutTree(node);
			rootBox.layout(containingBlock);
			return rootBox;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private CssLayout buildLayoutTree(CssStyleNode root) throws Exception {

		CssLayout rootBox = null;

		CssDisplay display = root.getValueOfDisplay();

		switch (display) {
		case Block:
			rootBox = new CssLayout(new CssBlockType(root));
			break;

		case Inline:
			rootBox = new CssLayout(new CssInlineType(root));
			break;

		default:
			throw new Exception("Root node has display: none.");
		}

		if (root.children != null && root.children.size() > 0) {

			for (CssStyleNode childNode : root.children) {

				switch (childNode.getValueOfDisplay()) {
				case Block:
					rootBox.children.add(buildLayoutTree(childNode));
					break;

				case Inline:
					rootBox.getInlineContainer().children.add(buildLayoutTree(childNode));
					break;

				default:
					break;
				}
			}

		}

		return rootBox;
	}
}
