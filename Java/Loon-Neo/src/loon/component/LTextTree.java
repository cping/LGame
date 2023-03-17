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
import loon.events.SysTouch;
import loon.font.FontSet;
import loon.font.FontUtils;
import loon.font.IFont;
import loon.font.LFont;
import loon.geom.RectF;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * 这是一个简单的字符串显示用类,通过输出具有上下级关系的字符串来描述一组事物,比如说角色转职表什么的
 * 
 * LTextTree _tree = new LTextTree(30, 100, 400, 400);
 * _tree.addElement("数学").addSub("微积分","几何"); add(_tree);
 */
public class LTextTree extends LComponent implements FontSet<LTextTree> {

	private TArray<TreeElement> _elements = new TArray<TreeElement>();

	private TArray<String> _lines;

	private RectF[] _selectRects;

	private int _selected = -1;

	private IFont _font;

	private LColor _fontColor = LColor.white.cpy();

	private String _root_name;

	private boolean _dirty;

	private float _space = 0;

	private String _templateResult = null;

	private int _totalElementsCount;

	public float _offsetX = 0;

	public float _offsetY = 0;

	private String _subTreeFlag = "├── ";

	private String _subTreeNextFlag = "│   ";

	private String _subLastTreeFlag = "└── ";

	public static class TreeElement {

		protected TArray<TreeElement> _childs;

		private int _selectedInSub = 0;

		protected boolean _onNextSublevel = false;

		private TreeElement _parent;

		private String _message;

		private LTextTree _tree;

		public TreeElement(String text) {
			this(null, text);
		}

		public TreeElement(LTextTree t, String text) {
			this.setTextTree(t);
			this._childs = new TArray<TreeElement>();
			this._message = text;
		}

		protected TreeElement setTextTree(LTextTree t) {
			this._tree = t;
			return this;
		}

		public String getText() {
			return StringUtils.replace(_message, LSystem.LS, LSystem.EMPTY);
		}

		public int getLevel() {
			if (this.isRoot()) {
				return 0;
			} else {
				return _parent.getLevel() + 1;
			}
		}

		public TArray<TreeElement> setSublevel(TreeElement[] array) {
			if (array != null && array.length > 0) {
				for (int i = 0; i < array.length; i++) {
					TreeElement e = array[i];
					if (e != null) {
						e._parent = this;
						e.setTextTree(_tree);
					}
				}
			}
			_childs = new TArray<TreeElement>(array);
			if (_tree != null) {
				_tree._dirty = true;
			}
			return _childs;
		}

		public TArray<TreeElement> addSub(String... eleNames) {
			for (int i = 0; i < eleNames.length; i++) {
				TreeElement e = new TreeElement(eleNames[i]);
				addSub(e);
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
			return new TArray<LTextTree.TreeElement>(_childs);
		}

		public TreeElement addChild(TreeElement me) {
			return addSub(me);
		}

		public TreeElement addSub(TreeElement me) {
			if (me == null) {
				return this;
			}
			_childs.add(me);
			me.setTextTree(_tree);
			me._parent = this;
			if (_tree != null) {
				_tree._dirty = true;
			}
			return me;
		}

		public TArray<TreeElement> addSub(TreeElement[] array) {
			for (int i = 0; i < array.length; i++) {
				TreeElement e = array[i];
				if (e != null) {
					_childs.add(e);
					e.setTextTree(_tree);
					e._parent = this;
				}
			}
			if (_tree != null) {
				_tree._dirty = true;
			}
			return getChilds();
		}

		public TreeElement getParent() {
			return this._parent;
		}

		public int getSelected() {
			return _selectedInSub;
		}

		public void increaseSelected(int amt) {
			if (_onNextSublevel) {
				if (_childs.get(_selectedInSub)._onNextSublevel) {
					_childs.get(_selectedInSub).increaseSelected(amt);
				} else {
					_selectedInSub = MathUtils.clamp(_selectedInSub + amt, 0, _childs.size - 1);
				}
			}
		}

		public boolean isRoot() {
			return _parent == null;
		}

		public boolean isLeaf() {
			return _childs.size() == 0;
		}

		public boolean moveSublevel(boolean right) {
			boolean old = _onNextSublevel;
			if (_onNextSublevel) {
				if (!_childs.get(_selectedInSub).moveSublevel(right)) {
					_onNextSublevel = right;
				}
			} else {
				if (_childs.size > 0) {
					_onNextSublevel = right;
				}
			}
			if (_childs.size == 0) {
				return (_onNextSublevel = false);
			}
			return !(old == _onNextSublevel);
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
		this._root_name = name;
		this.setFont(font);
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		if (!_component_visible) {
			return;
		}

		IFont tmp = g.getFont();
		g.setFont(_font);
		renderSub(g, _offsetX, _offsetY, x, y);
		g.setFont(tmp);

	}

	private void renderSub(GLEx g, float offX, float offY, float x, float y) {
		if (_dirty || _lines == null) {
			pack();
			return;
		}
		for (int i = 0; i < _lines.size; i++) {
			String text = _lines.get(i);
			RectF rect = _selectRects[i];
			g.drawString(text, rect.x + x + offX, rect.y + y + offY, _fontColor);
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
				String mes = new StrBuilder(ch).substring(0, size - 1).toString();
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
			return StringUtils.replacesTrim(_lines.get(_selected), _subLastTreeFlag, _subTreeNextFlag, _subTreeFlag);
		}
		return null;
	}

	public String getResult() {
		if (_dirty || _templateResult == null) {
			TreeElement trees = createTree();
			_templateResult = renderTree(trees);
		}
		return _templateResult;
	}

	protected TreeElement createTree() {
		String rootName = StringUtils.isEmpty(_root_name) ? "Root" : _root_name;
		TreeElement treeRoot = new TreeElement(this, rootName);
		for (TreeElement e : _elements) {
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
				putTree(n, treeRoot._childs.last());
				_totalElementsCount++;
			}
		}
	}

	protected void putNode(TreeElement node, TreeElement filenode) {
		if (filenode != node) {
			filenode.addSub(node);
			_totalElementsCount++;
		}
	}

	protected String renderTree(TreeElement _tree) {
		TArray<StrBuilder> lines = renderDirectoryTreeLines(_tree);
		String newline = LSystem.LS;
		StrBuilder sb = new StrBuilder(lines.size() * 20);
		for (StrBuilder line : lines) {
			sb.append(line);
			sb.append(newline);
		}
		return sb.toString();
	}

	protected TArray<StrBuilder> renderDirectoryTreeLines(TreeElement _tree) {
		TArray<StrBuilder> result = new TArray<StrBuilder>();
		result.add(new StrBuilder().append(_tree.getText()));
		Iterator<TreeElement> iterator = _tree._childs.iterator();
		while (iterator.hasNext()) {
			TArray<StrBuilder> subtree = renderDirectoryTreeLines(iterator.next());
			if (iterator.hasNext()) {
				addSubtree(result, subtree);
			} else {
				addLastSubtree(result, subtree);
			}
		}
		return result;
	}

	protected void addSubtree(TArray<StrBuilder> result, TArray<StrBuilder> subtree) {
		Iterator<StrBuilder> iterator = subtree.iterator();
		StrBuilder sbr = iterator.next();
		result.add(sbr.insert(0, _subTreeFlag));
		while (iterator.hasNext()) {
			result.add(iterator.next().insert(0, _subTreeNextFlag));
		}
	}

	private void addLastSubtree(TArray<StrBuilder> result, TArray<StrBuilder> subtree) {
		Iterator<StrBuilder> iterator = subtree.iterator();
		StrBuilder sbr = iterator.next();
		result.add(sbr.insert(0, _subLastTreeFlag));
		while (iterator.hasNext()) {
			result.add(iterator.next().insert(0, "    "));
		}
	}

	public TreeElement getSubElement(int idx) {
		return _elements.get(idx);
	}

	public TreeElement newElement(final String elementName) {
		return new TreeElement(this, elementName);
	}

	public TreeElement addElement(final String elementName) {
		return addElement(new TreeElement(this, elementName));
	}

	public TreeElement addElement(TreeElement me) {
		if (me == null) {
			throw new LSysException("TreeElement cannot be null!");
		}
		me.setTextTree(this);
		_elements.add(me);
		updateElements();
		return me;
	}

	public LTextTree clearElement() {
		_elements.clear();
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
		return _totalElementsCount;
	}

	@Override
	public IFont getFont() {
		return _font;
	}

	@Override
	public LTextTree setFont(IFont fn) {
		if (fn == null) {
			return this;
		}
		this._font = fn;
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
		return _subTreeFlag;
	}

	public LTextTree setSubTreeFlag(String t) {
		this._subTreeFlag = t;
		this._dirty = true;
		return this;
	}

	public String getSubTreeNextFlag() {
		return _subTreeNextFlag;
	}

	public LTextTree setSubTreeNextFlag(String tn) {
		this._subTreeNextFlag = tn;
		this._dirty = true;
		return this;
	}

	public String getSubLastTreeFlag() {
		return _subLastTreeFlag;
	}

	public LTextTree setSubLastTreeFlag(String lt) {
		this._subLastTreeFlag = lt;
		this._dirty = true;
		return this;
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

	public LTextTree setSelected(int selected) {
		this._selected = selected;
		return this;
	}

	@Override
	public String getUIName() {
		return "TextTree";
	}

	@Override
	public void destory() {

	}

}
