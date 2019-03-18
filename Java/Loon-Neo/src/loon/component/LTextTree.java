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
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.font.FontSet;
import loon.font.IFont;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 这是一个简单的字符串显示用类,通过输出具有上下级关系的字符串来描述一组事物
 * 
 * LTextTree tree = new LTextTree(30, 100, 400, 400);
 * tree.addElement("数学").addSub("微积分").addSub("几何"); add(tree);
 */
public class LTextTree extends LComponent implements FontSet<LTextTree>{

	private TArray<TreeElement> list = new TArray<TreeElement>();
	private TArray<TreeElement> elements = new TArray<TreeElement>();
	private int totalElementsCount = 0;

	private IFont _font;

	private float _space = 8;

	private String flagStyle = " => ";

	public static abstract class TreeElement {

		public TArray<TreeElement> childs;

		private int selectedInSub = 0;
		public boolean onNextSublevel = false;

		public TreeElement() {
			this.childs = new TArray<TreeElement>();
		}

		public abstract String getText();

		public TreeElement setSublevel(TreeElement[] array) {
			childs = new TArray<TreeElement>(array);
			return this;
		}

		public TreeElement addSub(final String elementName) {
			return addSub(new TreeElement() {

				@Override
				public String getText() {
					return elementName;
				}
			});
		}

		public TreeElement addSub(TreeElement me) {
			childs.add(me);
			return this;
		}

		public TreeElement addSub(TreeElement[] array) {
			for (int i = 0; i < array.length; i++) {
				childs.add(array[i]);
			}
			return this;
		}

		public int getSelected() {
			return selectedInSub;
		}

		public void increaseSelected(int amt) {
			if (onNextSublevel) {
				if (childs.get(selectedInSub).onNextSublevel) {
					childs.get(selectedInSub).increaseSelected(amt);
				} else {
					selectedInSub = MathUtils.clamp(selectedInSub + amt, 0, childs.size - 1);
				}
			}
		}

		public boolean moveSublevel(boolean right) {
			boolean old = onNextSublevel;
			if (onNextSublevel) {
				if (!childs.get(selectedInSub).moveSublevel(right)) {
					onNextSublevel = right;
				}
			} else {
				if (childs.size > 0) {
					onNextSublevel = right;
				}
			}
			if (childs.size == 0) {
				return (onNextSublevel = false);
			}
			return !(old == onNextSublevel);
		}

		public boolean isEnabled() {
			return true;
		}

	}

	public LTextTree(int x, int y, int width, int height) {
		this(x, y, width, height, 8f);
	}

	public LTextTree(IFont font, int x, int y, int width, int height) {
		this(font, x, y, width, height, 8f);
	}

	public LTextTree(int x, int y, int width, int height, float space) {
		this(LFont.getDefaultFont(), x, y, width, height, space);
	}

	public LTextTree(IFont font, int x, int y, int width, int height, float space) {
		super(x, y, width, height);
		this._space = space;
		this._font = font;
	}

	public float offsetX = 0;

	public float offsetY = 0;

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		IFont tmp = g.getFont();
		g.setFont(_font);
		renderSub(g, offsetX, offsetY, x, y, elements, 0f, g.alpha());
		g.setFont(tmp);
	}

	private float renderSub(GLEx g, float offsetX, float offsetY, float x, float y, TArray<TreeElement> level,
			float moved, float alpha) {
		float offX = offsetX;
		float offY = offsetY;

		IFont font = g.getFont();

		float posHeight = 0;
		for (int i = 0; i < level.size; i++) {

			TreeElement me = level.get(i);

			float superPos = y + offY + font.stringHeight(me.getText());
			if (i != 0) {
				superPos = posHeight + font.stringHeight(me.getText()) + _space;
			}
			g.drawString(me.getText(), x + offX, superPos);

			TArray<TreeElement> childs = me.childs;

			if (childs.size > 0) {
				float offsetPosX = x + font.stringWidth(me.getText()) + _space;
				for (int j = 0; j < childs.size; j++) {
					TreeElement tme = childs.get(j);

					float offsetPosY = (j * (font.stringHeight(tme.getText()) + _space));
					posHeight = (superPos + offsetPosY);

					g.drawString(flagStyle, offsetPosX, posHeight);
					float posX = offsetPosX + font.stringWidth(flagStyle) + _space;
					float size = 0;

					posHeight += size;
					g.drawString(tme.getText(), posX, posHeight);

				}
			}

		}
		return offY;
	}

	public TreeElement getSubElement(int idx) {
		return elements.get(idx);
	}

	public TreeElement addElement(final String elementName) {
		return addElement(new TreeElement() {

			@Override
			public String getText() {
				return elementName;
			}
		});
	}

	public TreeElement addElement(TreeElement me) {
		if (me == null) {
			throw LSystem.runThrow("TreeElement cannot be null!");
		}

		elements.add(me);
		updateElements();
		return me;
	}

	public int updateElements() {
		totalElementsCount = 0;
		resetQueue();
		int index = 0;
		TreeElement element = null;
		while (list.size > 0 && index < list.size) {
			element = list.get(index);
			totalElementsCount++;
			index++;
			if (element.childs.size <= 0) {
				continue;
			}
			for (TreeElement m : element.childs) {
				list.add(m);
			}
		}
		return getAmountOfTotalElements();
	}

	private void resetQueue() {
		list.clear();
		for (TreeElement m : elements) {
			list.add(m);
		}
	}

	public int getAmountOfTotalElements() {
		return totalElementsCount;
	}

	public String getFlagStyle() {
		return flagStyle;
	}

	public void setFlagStyle(String flagStyle) {
		this.flagStyle = flagStyle;
	}

	@Override
	public IFont getFont() {
		return _font;
	}

	@Override
	public LTextTree setFont(IFont font) {
		this._font = font;
		return this;
	}

	@Override
	public String getUIName() {
		return "TextTree";
	}

}
