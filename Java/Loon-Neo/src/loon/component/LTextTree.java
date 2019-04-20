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

import java.util.Iterator;

import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.event.SysTouch;
import loon.font.FontSet;
import loon.font.FontUtils;
import loon.font.IFont;
import loon.font.LFont;
import loon.geom.RectF;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * 这是一个简单的字符串显示用类,通过输出具有上下级关系的字符串来描述一组事物,比如说角色转职表什么的
 * 
 * LTextTree tree = new LTextTree(30, 100, 400, 400);
 * tree.addElement("数学").addSub("微积分","几何"); add(tree);
 */
public class LTextTree extends LComponent implements FontSet<LTextTree> {

	private TArray<TreeElement> elements = new TArray<TreeElement>();

	private TArray<String> _lines;

	private RectF[] _selectRects;

	private int _selected = -1;

	private IFont _font;

	private LColor _fontColor = LColor.white.cpy();

	private String _root_name;

	private boolean _dirty;

	private float _space = 0;

	private String templateResult = null;

	private int totalElementsCount;

	public float offsetX = 0;

	public float offsetY = 0;

	private String subTreeFlag = "├── ";

	private String subTreeNextFlag = "│   ";

	private String subLastTreeFlag = "└── ";

	public class TreeElement {

		protected TArray<TreeElement> childs;

		private int selectedInSub = 0;

		protected boolean onNextSublevel = false;

		private TreeElement parent;

		private String message;

		public TreeElement(String text) {
			this.childs = new TArray<TreeElement>();
			this.message = text;
		}

		public String getText() {
			return StringUtils.replace(message, "\n", "");
		}

		public int getLevel() {
			if (this.isRoot()) {
				return 0;
			} else {
				return parent.getLevel() + 1;
			}
		}

		public TArray<TreeElement> setSublevel(TreeElement[] array) {
			if (array != null && array.length > 0) {
				for (int i = 0; i < array.length; i++) {
					array[i].parent = this;
				}
			}
			childs = new TArray<TreeElement>(array);
			_dirty = true;
			return childs;
		}

		public TArray<TreeElement> addSub(String... eleNames) {
			for (int i = 0; i < eleNames.length; i++) {
				addSub(new TreeElement(eleNames[i]));
			}
			return getChilds();
		}

		public TreeElement addSub(final String elementName) {
			return addSub(new TreeElement(elementName));
		}

		public TreeElement addChild(final String elementName) {
			return addChild(new TreeElement(elementName));
		}

		public TArray<TreeElement> getChilds() {
			return new TArray<LTextTree.TreeElement>(childs);
		}

		public TreeElement addChild(TreeElement me) {
			return addSub(me);
		}

		public TreeElement addSub(TreeElement me) {
			childs.add(me);
			me.parent = this;
			_dirty = true;
			return me;
		}

		public TArray<TreeElement> addSub(TreeElement[] array) {
			for (int i = 0; i < array.length; i++) {
				childs.add(array[i]);
				array[i].parent = this;
			}
			_dirty = true;
			return getChilds();
		}

		public TreeElement getParent() {
			return this.parent;
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

		public boolean isRoot() {
			return parent == null;
		}

		public boolean isLeaf() {
			return childs.size() == 0;
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
		this("Root", x, y, width, height);
	}

	public LTextTree(String name, int x, int y, int width, int height) {
		this(name, x, y, width, height, 0f);
	}

	public LTextTree(IFont font, String name, int x, int y, int width, int height) {
		this(font, name, x, y, width, height, 0f);
	}

	public LTextTree(String name, int x, int y, int width, int height, float space) {
		this(LSystem.getSystemGameFont(), name, x, y, width, height, space);
	}

	public LTextTree(IFont font, String name, int x, int y, int width, int height, float space) {
		super(x, y, width, height);
		this._space = space;
		this._font = font;
		this._root_name = name;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		if (!_component_visible) {
			return;
		}

		boolean useLFont = (_font instanceof LFont);
		boolean supportPack = false;

		if (useLFont) {
			LFont newFont = (LFont) _font;
			supportPack = newFont.isSupportCacheFontPack();
			newFont.setSupportCacheFontPack(false);
		}

		IFont tmp = g.getFont();
		g.setFont(_font);
		renderSub(g, offsetX, offsetY, x, y);
		g.setFont(tmp);

		if (useLFont && supportPack) {
			LFont newFont = (LFont) _font;
			newFont.setSupportCacheFontPack(supportPack);
		}
	}

	private void renderSub(GLEx g, float offsetX, float offsetY, float x, float y) {
		if (_dirty || _lines == null) {
			pack();
			return;
		}
		for (int i = 0; i < _lines.size; i++) {
			String text = _lines.get(i);
			RectF rect = _selectRects[i];
			g.drawString(text, rect.x + x + offsetX, rect.y + y + offsetY, _fontColor);
		}
	}

	public LTextTree pack() {
		String result = getResult();
		TArray<CharSequence> treeList = new TArray<CharSequence>();
		FontUtils.splitLines(result, treeList);
		this._lines = new TArray<String>(getAmountOfTotalElements());
		for (CharSequence ch : treeList) {
			int size = ch.length();
			if (size > 1) {
				String mes = new StringBuffer(ch).substring(0, size - 1).toString();
				_lines.add(mes);
			}
		}
		float maxWidth = 0;
		float maxHeight = 0;
		float lastWidth = 0;
		float lastHeight = 0;
		this._selectRects = new RectF[_lines.size];
		for (int i = 0; i < _lines.size; i++) {
			String text = _lines.get(i);
			lastWidth = maxWidth;
			lastHeight = maxHeight;
			maxWidth = MathUtils.max(maxWidth, FontUtils.measureText(_font, text) + _font.getHeight() + _space);
			int height = (int) (MathUtils.max(_font.stringHeight(text), _font.getHeight()) + _space);
			if (maxWidth > lastWidth) {
				for (int j = 0; j < _selectRects.length; j++) {
					if (_selectRects[j] != null) {
						_selectRects[j].width = maxWidth;
					}
				}
			}
			if (maxHeight > lastHeight) {
				for (int j = 0; j < _selectRects.length; j++) {
					if (_selectRects[j] != null) {
						_selectRects[j].height = maxHeight;
					}
				}
			}
			_selectRects[i] = new RectF(0, maxHeight, maxWidth, height);
			maxHeight += height;
		}
		setSize(maxWidth + _space * 2 - _font.getSize(), maxHeight + _space * 2);
		if (_font instanceof LFont) {
			LSTRDictionary.get().bind((LFont) _font, StringUtils.getListToStrings(_lines));
		}
		_dirty = false;
		return this;
	}

	@Override
	public void update(long elapsedTime) {
		if (!isVisible()) {
			return;
		}
		super.update(elapsedTime);
		if (SysTouch.isDown() || SysTouch.isDrag() || SysTouch.isMove()) {
			if (_selectRects != null) {
				for (int i = 0; i < _selectRects.length; i++) {
					RectF touched = _selectRects[i];
					if (touched != null && touched.inside(getUITouchX(), getUITouchY())) {
						_selected = i;
					}
				}
			}
		}
	}

	public String getSelectedResult() {
		if (_lines != null && _selected != -1 && CollectionUtils.safeRange(_lines.items, _selected)) {
			return StringUtils.replaceTrim(_lines.get(_selected), subLastTreeFlag, subTreeNextFlag, subTreeFlag);
		}
		return null;
	}

	public String getResult() {
		if (_dirty || templateResult == null) {
			TreeElement trees = createTree();
			templateResult = renderTree(trees);
		}
		return templateResult;
	}

	protected TreeElement createTree() {
		String rootName = StringUtils.isEmpty(_root_name) ? "Root" : _root_name;
		TreeElement treeRoot = new TreeElement(rootName);
		for (TreeElement e : elements) {
			if (e.isRoot()) {
				putTree(e, treeRoot);
			} else {
				putNode(e, treeRoot);
			}
		}
		return treeRoot;
	}

	protected void putTree(TreeElement node, TreeElement treeRoot) {
		treeRoot.addSub(node);
		TArray<TreeElement> root = node.getChilds();
		for (TreeElement n : root) {
			if (n.isRoot()) {
				putTree(n, treeRoot.childs.last());
				totalElementsCount++;
			}
		}
	}

	protected void putNode(TreeElement node, TreeElement filenode) {
		if (filenode != node) {
			filenode.addSub(node);
			totalElementsCount++;
		}
	}

	protected String renderTree(TreeElement tree) {
		TArray<StringBuilder> lines = renderDirectoryTreeLines(tree);
		String newline = LSystem.LS;
		StringBuilder sb = new StringBuilder(lines.size() * 20);
		for (StringBuilder line : lines) {
			sb.append(line);
			sb.append(newline);
		}
		return sb.toString();
	}

	protected TArray<StringBuilder> renderDirectoryTreeLines(TreeElement tree) {
		TArray<StringBuilder> result = new TArray<StringBuilder>();
		result.add(new StringBuilder().append(tree.getText()));
		Iterator<TreeElement> iterator = tree.childs.iterator();
		while (iterator.hasNext()) {
			TArray<StringBuilder> subtree = renderDirectoryTreeLines(iterator.next());
			if (iterator.hasNext()) {
				addSubtree(result, subtree);
			} else {
				addLastSubtree(result, subtree);
			}
		}
		return result;
	}

	protected void addSubtree(TArray<StringBuilder> result, TArray<StringBuilder> subtree) {
		Iterator<StringBuilder> iterator = subtree.iterator();
		StringBuilder sbr = iterator.next();
		result.add(sbr.insert(0, subTreeFlag));
		while (iterator.hasNext()) {
			result.add(iterator.next().insert(0, subTreeNextFlag));
		}
	}

	private void addLastSubtree(TArray<StringBuilder> result, TArray<StringBuilder> subtree) {
		Iterator<StringBuilder> iterator = subtree.iterator();
		StringBuilder sbr = iterator.next();
		result.add(sbr.insert(0, subLastTreeFlag));
		while (iterator.hasNext()) {
			result.add(iterator.next().insert(0, "    "));
		}
	}

	public TreeElement getSubElement(int idx) {
		return elements.get(idx);
	}

	public TreeElement newElement(final String elementName) {
		return new TreeElement(elementName);
	}

	public TreeElement addElement(final String elementName) {
		return addElement(new TreeElement(elementName));
	}

	public TreeElement addElement(TreeElement me) {
		if (me == null) {
			throw new LSysException("TreeElement cannot be null!");
		}
		elements.add(me);
		updateElements();
		return me;
	}

	public LTextTree clearElement() {
		elements.clear();
		_root_name = null;
		updateElements();
		return this;
	}

	public boolean isDirty() {
		return _dirty;
	}

	public int updateElements() {
		_dirty = true;
		return getAmountOfTotalElements();
	}

	public int getAmountOfTotalElements() {
		return totalElementsCount;
	}

	@Override
	public IFont getFont() {
		return _font;
	}

	@Override
	public LTextTree setFont(IFont font) {
		if (_font == null) {
			return this;
		}
		this._font = font;
		this._dirty = true;
		return this;
	}

	@Override
	public LTextTree setFontColor(LColor color) {
		this._fontColor = color;
		return this;
	}

	@Override
	public LColor getFontColor() {
		return _fontColor.cpy();
	}

	public String getSubTreeFlag() {
		return subTreeFlag;
	}

	public void setSubTreeFlag(String subTreeFlag) {
		this.subTreeFlag = subTreeFlag;
		this._dirty = true;
	}

	public String getSubTreeNextFlag() {
		return subTreeNextFlag;
	}

	public void setSubTreeNextFlag(String subTreeNextFlag) {
		this.subTreeNextFlag = subTreeNextFlag;
		this._dirty = true;
	}

	public String getSubLastTreeFlag() {
		return subLastTreeFlag;
	}

	public void setSubLastTreeFlag(String subLastTreeFlag) {
		this.subLastTreeFlag = subLastTreeFlag;
		this._dirty = true;
	}

	public String getRootName() {
		return _root_name;
	}

	public LTextTree setRootName(String name) {
		this._root_name = name;
		this._dirty = true;
		return this;
	}

	public int getSelected() {
		return _selected;
	}

	public void setSelected(int selected) {
		this._selected = selected;
	}

	@Override
	public String getUIName() {
		return "TextTree";
	}

}
