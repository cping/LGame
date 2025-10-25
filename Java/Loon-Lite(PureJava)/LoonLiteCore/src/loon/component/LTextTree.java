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

import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.canvas.LColor;
import loon.font.FontSet;
import loon.font.FontUtils;
import loon.font.IFont;
import loon.geom.RectF;
import loon.opengl.GLEx;
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

	public static interface TreeListener {

		public void onSelectClick(int idx);

		public void onDrawSelect(int idx, TreeElement ele, GLEx g, float x, float y);

	}

	private final static String SPACE = "   ";

	private TreeListener _treeListener;

	private TArray<TreeElement> _elements = new TArray<TreeElement>();

	private TArray<String> _lines;

	private TArray<TreeNode> _treeNodes;

	private RectF[] _selectRects;

	private int _selected = -1;

	private IFont _font;

	private LColor _treeColor;

	private LColor _fontColor;

	private String _root_name;

	private String _expandFlag = "(+)";

	private String _shrinkFlag = "(-)";

	private boolean _updateTree;

	private boolean _root_hide;

	private boolean _show_fold_flag;

	private boolean _dirty;

	private String _templateResult = null;

	private int _totalElementsCount;

	protected float _offsetX = 0;

	protected float _offsetY = 0;

	protected float _windowSpace = 0;

	protected float _fontSpace = 1;

	static class TreeNode implements LRelease {

		public String _treeFlag;

		public TreeElement _treeNode;

		public TreeNode(TreeElement tree) {
			this(tree.getText(), tree);
		}

		public TreeNode(String treeFlag, TreeElement tree) {
			this._treeFlag = treeFlag;
			this._treeNode = tree;
		}

		public TreeElement getElement() {
			return _treeNode;
		}

		public String getText() {
			return _treeNode.getText();
		}

		@Override
		public void close() {
			if (_treeNode != null) {
				_treeNode.close();
			}
		}

	}

	static class TreeNodeType {

		public static TreeNodeType createLineString() {
			return new TreeNodeType(0, "├── ", "│   ", "└── ");
		}

		public static TreeNodeType createArrowString() {
			return new TreeNodeType(1, "↓", SPACE, "→");
		}

		public static TreeNodeType createArrowSolidString() {
			return new TreeNodeType(2, LSystem.FLAG_TAG, SPACE, LSystem.FLAG_SELECT_TAG);
		}

		public static TreeNodeType createCricleString() {
			return new TreeNodeType(3, "●", SPACE, "○");
		}

		public static TreeNodeType createArrowLineString() {
			return new TreeNodeType(4, "﹀", SPACE, "〉");
		}

		public static TreeNodeType createArrowHeavyString() {
			return new TreeNodeType(5, "▽", SPACE, "▽");
		}

		String _subTreeBranchFlag;

		String _subTreeNextFlag;

		String _subTreeLastFlag;

		int _index;

		public TreeNodeType(int id, String branch, String next, String last) {
			this._index = id;
			this._subTreeBranchFlag = branch;
			this._subTreeNextFlag = next;
			this._subTreeLastFlag = last;
		}

		public int getId() {
			return this._index;
		}

	}

	public static enum TreeType {
		Line, Arrow, ArrowSolid, ArrowLine, ArrowHeavy, Cricle
	}

	private TreeType _type;

	private TreeNodeType _nodeType;

	public static class TreeElement implements LRelease {

		protected TArray<TreeElement> _childs;

		protected boolean _onNextSublevel = false;

		protected boolean _hideChild = false;

		protected LColor _fontColor = null;

		protected LTexture _icon;

		private int _selectedInSub = 0;

		private TreeElement _parent;

		private String _message;

		private LTextTree _tree;

		public TreeElement(LColor c, String text) {
			this(c, null, text);
		}

		public TreeElement(LColor c, LTextTree t, String text) {
			this.setTextTree(t);
			this._childs = new TArray<TreeElement>();
			this._message = text;
			this._fontColor = c;
		}

		protected TreeElement setTextTree(LTextTree t) {
			this._tree = t;
			return this;
		}

		public LTexture getIcon() {
			return this._icon;
		}

		public TreeElement setIcon(String path) {
			return setIcon(LTextures.loadTexture(path));
		}

		public TreeElement setIcon(LTexture tex) {
			this._icon = tex;
			return this;
		}

		public TreeElement setFontColor(LColor c) {
			this._fontColor = c;
			return this;
		}

		public LColor getFontColor() {
			return _fontColor.cpy();
		}

		public TreeElement reverseChild() {
			this._hideChild = !_hideChild;
			return this;
		}

		public TreeElement hideChild() {
			this._hideChild = true;
			return this;
		}

		public TreeElement showChild() {
			this._hideChild = false;
			return this;
		}

		public boolean isParentTreeHide() {
			return _parent != null && _parent._hideChild;
		}

		public boolean hasChild() {
			return _childs.size > 0;
		}

		public boolean isHideChild() {
			return _hideChild;
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

		public TArray<LTextTree.TreeElement> setSublevel(TreeElement[] array) {
			if (array == null) {
				return new TArray<LTextTree.TreeElement>();
			}
			if (array != null && array.length > 0) {
				for (int i = 0; i < array.length; i++) {
					TreeElement e = array[i];
					if (e != null) {
						e._parent = this;
						e.setFontColor(e._fontColor == null ? _fontColor : e._fontColor);
						e.setTextTree(_tree);
					}
				}
			}
			_childs = new TArray<LTextTree.TreeElement>(array);
			if (_tree != null) {
				_tree._dirty = true;
			}
			return _childs;
		}

		public TArray<TreeElement> addSub(final String... eleNames) {
			return addSub(_fontColor, eleNames);
		}

		public TArray<TreeElement> addSub(final LColor c, final String... eleNames) {
			for (int i = 0; i < eleNames.length; i++) {
				TreeElement e = new TreeElement(c, eleNames[i]);
				addSub(e);
			}
			return getChilds();
		}

		public TreeElement addSub(final String elementName) {
			return addSub(_fontColor, elementName);
		}

		public TreeElement addSub(final LColor c, final String elementName) {
			return addSub(new TreeElement(c, elementName));
		}

		public TreeElement addChild(final String elementName) {
			return addChild(_fontColor, elementName);
		}

		public TreeElement addChild(final LColor c, final String elementName) {
			return addChild(new TreeElement(c, elementName));
		}

		public TreeElement addChild(final TreeElement me) {
			return addSub(me);
		}

		public TreeElement addSub(final TreeElement me) {
			if (me == null) {
				return this;
			}
			_childs.add(me);
			me.setTextTree(_tree);
			me.setFontColor(me._fontColor == null ? _fontColor : me._fontColor);
			me._parent = this;
			if (_tree != null) {
				_tree._dirty = true;
			}
			return me;
		}

		public TArray<TreeElement> addSub(final TreeElement[] array) {
			for (int i = 0; i < array.length; i++) {
				TreeElement e = array[i];
				if (e != null) {
					_childs.add(e);
					e.setTextTree(_tree);
					e.setFontColor(e._fontColor == null ? _fontColor : e._fontColor);
					e._parent = this;
				}
			}
			if (_tree != null) {
				_tree._dirty = true;
			}
			return getChilds();
		}

		public TArray<TreeElement> getChilds() {
			return new TArray<LTextTree.TreeElement>(_childs);
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

		@Override
		public void close() {
			if (_icon != null) {
				_icon.close();
			}
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
		this(LSystem.getSystemGameFont(), name, TreeType.Line, x, y, width, height, space);
	}

	public LTextTree(IFont font, String name, TreeType treetype, int x, int y, int width, int height, float space) {
		super(x, y, width, height);
		this._treeColor = _fontColor = LColor.white;
		this._windowSpace = space;
		this._root_name = name;
		this.setFont(font);
		this.setBranchType(treetype);
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
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
		}
		for (int i = 0; i < _treeNodes.size && i < _selectRects.length; i++) {
			final TreeNode node = _treeNodes.get(i);
			if (node == null) {
				continue;
			}
			final TreeElement ele = node.getElement();
			if (ele != null) {
				final RectF rect = _selectRects[i];
				float newX = rect.x + x + offX;
				float newY = rect.y + y + offY;
				g.drawString(node._treeFlag, newX, newY, _treeColor);
				String text = _show_fold_flag
						? ele.isHideChild() ? ele.getText() + " " + _expandFlag : ele.getText() + " " + _shrinkFlag
						: ele.getText();
				final float width = MathUtils.max(node._treeFlag.length() * (_font.getSize() * 0.55f),
						_font.stringWidth(node._treeFlag)) + _fontSpace;
				final float offsetX = width + newX;
				if (ele._icon == null) {
					g.drawString(text, offsetX, newY, ele._fontColor == null ? _fontColor : ele._fontColor);
				} else {
					final float iconWidth = MathUtils.max(_font.getSize(),
							MathUtils.min(MathUtils.min(rect.width, rect.height), ele._icon.getWidth()) - 1);
					final float iconHeight = MathUtils.min(rect.height, ele._icon.getHeight()) - 1;
					g.draw(ele._icon, offsetX, newY + (rect.height - iconHeight) / 2, iconWidth, iconHeight);
					g.drawString(text, offsetX + iconWidth + 4, newY,
							ele._fontColor == null ? _fontColor : ele._fontColor);
				}
				if (_treeListener != null) {
					_treeListener.onDrawSelect(i, ele, g, offsetX, newY);
				}
			}
		}
	}

	private String getTreeText(String text) {
		if (StringUtils.isEmpty(text)) {
			return LSystem.EMPTY;
		}
		return StringUtils.replace(text, "&", LSystem.EMPTY);
	}

	private String getText(String text) {
		if (StringUtils.isEmpty(text)) {
			return LSystem.NULL;
		}
		text = StringUtils.replaces(text, LSystem.EMPTY, _nodeType._subTreeBranchFlag, _nodeType._subTreeLastFlag,
				_nodeType._subTreeNextFlag, "&");
		return text;
	}

	public LTextTree pack() {
		int count = getAmountOfTotalElements();
		if (this._lines == null) {
			this._lines = new TArray<String>(count);
		} else {
			this._lines.clear();
		}
		if (this._treeNodes == null) {
			this._treeNodes = new TArray<LTextTree.TreeNode>(count);
		} else {
			this._treeNodes.clear();
		}
		String result = getResult();
		TArray<CharSequence> treeList = new TArray<CharSequence>();
		FontUtils.splitLines(result, treeList);
		for (int i = 0; i < treeList.size; i++) {
			CharSequence ch = treeList.get(i);
			int size = ch.length();
			if (size > 1) {
				String mes = new StrBuilder(ch).substring(0, size).toString();
				_lines.add(mes);
				TreeNode node = _treeNodes.get(i);
				if (i > 0) {
					int idx = mes.lastIndexOf(LSystem.AMP);
					if (idx != -1) {
						node._treeFlag = getTreeText(mes.substring(0, idx));
					} else {
						node._treeFlag = LSystem.EMPTY;
					}
				} else {
					node._treeFlag = LSystem.EMPTY;
				}
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
			maxWidth = MathUtils.max(maxWidth, FontUtils.measureText(_font, text) + _font.getHeight() + _windowSpace)
					+ 4;
			int height = (int) (MathUtils.max(_font.stringHeight(text), _font.getHeight()) + _windowSpace);
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
		setSize(maxWidth + _windowSpace * 2 - _font.getSize(), maxHeight + _windowSpace * 2);
		if (!_updateTree) {
			_updateTree = false;
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
	}

	@Override
	public void processTouchPressed() {
		super.processTouchPressed();
		checkSelected(false);
	}

	@Override
	public void processTouchReleased() {
		super.processTouchReleased();
		checkSelected(true);
	}

	private void checkSelected(boolean clicked) {
		if (_selectRects != null) {
			for (int i = 0; i < _selectRects.length; i++) {
				RectF touched = _selectRects[i];
				if (touched != null && touched.inside(getUITouchX(), getUITouchY())) {
					_selected = i;
					if (_treeListener != null && clicked) {
						_treeListener.onSelectClick(i);
					}
				}
			}
		}
	}

	public String getSelectedResult() {
		if (_lines != null && _selected != -1 && CollectionUtils.safeRange(_lines.items, _selected)) {
			return getText(_lines.get(_selected));
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
		TreeElement treeRoot = new TreeElement(_fontColor, this, rootName);
		if (_root_hide) {
			treeRoot.hideChild();
		}
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

	protected String renderTree(TreeElement tree) {
		TArray<StrBuilder> lines = renderDirectoryTreeLines(tree);
		String newline = LSystem.LS;
		StrBuilder sb = new StrBuilder(lines.size() * 20);
		for (StrBuilder line : lines) {
			sb.append(line);
			sb.append(newline);
		}
		return sb.toString();
	}

	protected TArray<StrBuilder> renderDirectoryTreeLines(TreeElement tree) {
		if (_treeNodes != null) {
			_treeNodes.add(new TreeNode(tree));
		}
		TArray<StrBuilder> result = new TArray<StrBuilder>();
		result.add(new StrBuilder().append(tree.getText()));
		if (!tree.isHideChild()) {
			Iterator<TreeElement> iterator = tree._childs.iterator();
			while (iterator.hasNext()) {
				TreeElement e = iterator.next();
				TArray<StrBuilder> subtree = renderDirectoryTreeLines(e);
				if (iterator.hasNext()) {
					addSubtree(e, result, subtree);
				} else {
					addLastSubtree(e, result, subtree);
				}
			}
		}
		return result;
	}

	protected void addSubtree(TreeElement ele, TArray<StrBuilder> result, TArray<StrBuilder> subtree) {
		Iterator<StrBuilder> iterator = subtree.iterator();
		StrBuilder sbr = iterator.next();
		if (_nodeType._index == 0) {
			result.add(sbr.insert(0, _nodeType._subTreeBranchFlag + LSystem.AMP));
		} else {
			if (ele.hasChild()) {
				result.add(sbr.insert(0, _nodeType._subTreeBranchFlag + LSystem.AMP));
			} else {
				result.add(sbr.insert(0, _nodeType._subTreeLastFlag + LSystem.AMP));
			}
		}
		while (iterator.hasNext()) {
			result.add(iterator.next().insert(0, _nodeType._subTreeNextFlag + LSystem.AMP));
		}
	}

	private void addLastSubtree(TreeElement ele, TArray<StrBuilder> result, TArray<StrBuilder> subtree) {
		Iterator<StrBuilder> iterator = subtree.iterator();
		StrBuilder sbr = iterator.next();
		if (_nodeType._index == 0) {
			result.add(sbr.insert(0, _nodeType._subTreeLastFlag + LSystem.AMP));
		} else {
			if (ele.hasChild()) {
				result.add(sbr.insert(0, _nodeType._subTreeBranchFlag + LSystem.AMP));
			} else {
				result.add(sbr.insert(0, _nodeType._subTreeLastFlag + LSystem.AMP));
			}
		}
		while (iterator.hasNext()) {
			result.add(iterator.next().insert(0, LSystem.SPACE));
		}
	}

	public TreeElement getSubElement(int idx) {
		return _elements.get(idx);
	}

	public TreeElement newElement(final LColor c, final String elementName) {
		return new TreeElement(c, this, elementName);
	}

	public TreeElement addElement(final LColor c, final String elementName) {
		return addElement(newElement(c, elementName));
	}

	public TreeElement newElement(final String elementName) {
		return newElement(_fontColor, elementName);
	}

	public TreeElement addElement(final String elementName) {
		return addElement(_fontColor, elementName);
	}

	public TreeElement addElement(final TreeElement me) {
		if (me == null) {
			throw new LSysException("TreeElement cannot be null!");
		}
		me.setFontColor(me._fontColor == null ? _fontColor : me._fontColor);
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

	public LTextTree setTreeColor(LColor treeColor) {
		this._treeColor = treeColor;
		return this;
	}

	public LColor getTreeColor() {
		return _treeColor.cpy();
	}

	public String getSubTreeFlag() {
		return _nodeType._subTreeBranchFlag;
	}

	public LTextTree setSubTreeFlag(String t) {
		this._nodeType._subTreeBranchFlag = t;
		this._dirty = true;
		return this;
	}

	public String getSubTreeNextFlag() {
		return _nodeType._subTreeNextFlag;
	}

	public LTextTree setSubTreeNextFlag(String tn) {
		this._nodeType._subTreeNextFlag = tn;
		this._dirty = true;
		return this;
	}

	public String getSubLastTreeFlag() {
		return _nodeType._subTreeLastFlag;
	}

	public LTextTree setSubLastTreeFlag(String lt) {
		this._nodeType._subTreeLastFlag = lt;
		this._dirty = true;
		return this;
	}

	public String getRootName() {
		return _root_name;
	}

	public boolean isHideChild() {
		return _root_hide;
	}

	public LTextTree hideChild() {
		this._root_hide = true;
		return this;
	}

	public LTextTree showChild() {
		this._root_hide = false;
		return this;
	}

	public LTextTree reverseChild() {
		this._root_hide = !_root_hide;
		return this;
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
		if (selected < 0 || selected >= _lines.size) {
			return this;
		}
		this._selected = selected;
		return this;
	}

	public LTextTree updateTree() {
		if (_treeNodes != null && _selected != -1 && CollectionUtils.safeRange(_treeNodes.items, _selected)) {
			TreeNode node = _treeNodes.get(_selected);
			if (node != null) {
				TreeElement ele = node.getElement();
				if (ele != null) {
					if (_selected == 0) {
						_root_hide = !_root_hide;
					}
					ele.reverseChild();
					_dirty = true;
					_updateTree = true;
				}
			}
		}
		return this;
	}

	public boolean isShowFoldFlag() {
		return _show_fold_flag;
	}

	public LTextTree setShowFoldFlag(boolean f) {
		this._show_fold_flag = f;
		return this;
	}

	public LTextTree setHideFoldFlag(String e, String s) {
		setFoldExpandFlag(e);
		setFoldShrinkFlag(s);
		return this;
	}

	public String getFoldExpandFlag() {
		return _expandFlag;
	}

	public LTextTree setFoldExpandFlag(String e) {
		this._expandFlag = e;
		return this;
	}

	public String getFoldShrinkFlag() {
		return _shrinkFlag;
	}

	public LTextTree setFoldShrinkFlag(String s) {
		this._shrinkFlag = s;
		return this;
	}

	public TreeType getBranchType() {
		return _type;
	}

	public LTextTree setBranchType(TreeType t) {
		this._type = t;
		switch (_type) {
		case Line:
		default:
			_nodeType = TreeNodeType.createLineString();
			break;
		case Arrow:
			_nodeType = TreeNodeType.createArrowString();
			break;
		case ArrowSolid:
			_nodeType = TreeNodeType.createArrowSolidString();
			break;
		case ArrowLine:
			_nodeType = TreeNodeType.createArrowLineString();
			break;
		case ArrowHeavy:
			_nodeType = TreeNodeType.createArrowHeavyString();
			break;
		case Cricle:
			_nodeType = TreeNodeType.createCricleString();
			break;
		}
		return this;
	}

	public float getFontSpace() {
		return _fontSpace;
	}

	public LTextTree setFontSpace(float f) {
		this._fontSpace = f;
		this._dirty = true;
		return this;
	}

	public TreeListener getTreeListener() {
		return _treeListener;
	}

	public LTextTree setTreeListener(TreeListener t) {
		if (t == null) {
			return this;
		}
		this._treeListener = t;
		return this;
	}

	@Override
	public String getUIName() {
		return "TextTree";
	}

	@Override
	public void destory() {
		if (_treeNodes != null) {
			for (TreeNode node : _treeNodes) {
				if (node != null) {
					node.close();
				}
			}
		}
		_dirty = true;
		_updateTree = false;
	}

}
