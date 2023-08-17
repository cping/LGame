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
package loon.utils;

import java.util.Iterator;

import loon.LSysException;
import loon.LSystem;

public class TreeNode<T> implements Iterable<TreeNode<T>> {

	public static final class TreeNodeIter<T> implements Iterator<TreeNode<T>> {

		public enum TreeState {
			Parent, ChildCurNode, ChildSubNode
		}

		private TreeState _onNext;
		private TreeNode<T> _next;
		private Iterator<TreeNode<T>> _childrenCurNodeIter;
		private Iterator<TreeNode<T>> _childrenSubNodeIter;

		private TreeNode<T> _treeNode;

		public TreeNodeIter(TreeNode<T> treeNode) {
			if (treeNode == null) {
				throw new LSysException("the TreeNode cannot be null !");
			}
			this._treeNode = treeNode;
			this._onNext = TreeState.Parent;
			this._childrenCurNodeIter = _treeNode._children.newListIterator();
		}

		public TreeState getNextState() {
			return _onNext;
		}

		@Override
		public boolean hasNext() {

			if (this._onNext == TreeState.Parent) {
				this._next = this._treeNode;
				this._onNext = TreeState.ChildCurNode;
				return true;
			}

			if (this._onNext == TreeState.ChildCurNode) {
				if (_childrenCurNodeIter.hasNext()) {
					TreeNode<T> childDirect = _childrenCurNodeIter.next();
					_childrenSubNodeIter = childDirect.iterator();
					this._onNext = TreeState.ChildSubNode;
					return hasNext();
				} else {
					this._onNext = null;
					return false;
				}
			}

			if (this._onNext == TreeState.ChildSubNode) {

				if (_childrenSubNodeIter.hasNext()) {
					this._next = _childrenSubNodeIter.next();
					return true;
				} else {
					this._next = null;
					this._onNext = TreeState.ChildCurNode;
					return hasNext();
				}
			}

			return false;
		}

		@Override
		public TreeNode<T> next() {
			if (this._next != null && !this._next._visible) {
				return null;
			}
			return this._next;
		}

		@Override
		public void remove() {
		}

		@Override
		public String toString() {
			return _treeNode.toString();
		}

	}

	private int _idx = 0;

	private final String _name;

	private SortedList<TreeNode<T>> _elementsIndex;

	private SortedList<TreeNode<T>> _children;

	private TreeNode<T> _parent = null;

	private boolean _visible = false;

	private T _data = null;

	public TreeNode() {
		this(LSystem.UNKNOWN, null);
	}

	public TreeNode(T data) {
		this(LSystem.UNKNOWN, data);
	}

	public TreeNode(String name, T data) {
		if (name == null || LSystem.UNKNOWN.equals(name)) {
			if (data == null) {
				name = LSystem.UNKNOWN;
			} else {
				name = data.toString();
			}
		}
		this._name = name;
		this._data = data;
		this._children = new SortedList<TreeNode<T>>();
		this._elementsIndex = new SortedList<TreeNode<T>>();
		this._elementsIndex.add(this);
		this._visible = true;
		_idx++;
	}

	public int getID() {
		return this._idx;
	}

	public TreeNode<T> addChild(T child) {
		return addNode(new TreeNode<T>(child));
	}

	public TreeNode<T> addFirstChild(T child) {
		return addFirstNode(new TreeNode<T>(child));
	}

	public TreeNode<T> addLastChild(T child) {
		return addLastNode(new TreeNode<T>(child));
	}

	public TreeNode<T> addNode(TreeNode<T> childNode) {
		childNode._parent = this;
		_children.add(childNode);
		this.registerChildForSearch(childNode);
		return childNode;
	}

	public TreeNode<T> addFirstNode(TreeNode<T> childNode) {
		childNode._parent = this;
		_children.addFirst(childNode);
		this.registerChildForSearch(childNode);
		return childNode;
	}

	public TreeNode<T> addLastNode(TreeNode<T> childNode) {
		childNode._parent = this;
		_children.addLast(childNode);
		this.registerChildForSearch(childNode);
		return childNode;
	}

	public int getLevel() {
		if (this.isRoot()) {
			return 0;
		} else {
			return _parent.getLevel() + 1;
		}
	}

	public TreeNode<T> findTreeNode(Comparable<T> comp) {
		for (TreeNode<T> element : this._elementsIndex) {
			if (element != null && element._visible) {
				T elData = element._data;
				if (comp.compareTo(elData) == 0) {
					return element;
				}
			}
		}
		return null;
	}

	public int getDepth() {
		if (!_visible) {
			return 0;
		}
		int result = 1;
		SortedList<TreeNode<T>> looper = _children;
		while (looper.size > 0) {
			result = result + 1;
			SortedList<TreeNode<T>> _next = new SortedList<TreeNode<T>>();
			for (TreeNode<T> node : looper) {
				if (node != null && node._visible) {
					_next.addAll(node.getChildren());
				}
			}
			looper = _next;
		}
		return result;
	}

	public int getWidth() {
		if (!_visible) {
			return 0;
		}
		int result = 0;
		SortedList<TreeNode<T>> looper = _children;
		while (looper.size > 0) {
			SortedList<TreeNode<T>> _next = new SortedList<TreeNode<T>>();
			for (TreeNode<T> node : looper) {
				if (node != null && node._visible) {
					if (node._children.size == 0) {
						result = result + 1;
					}
					_next.addAll(node.getChildren());
				}
			}
			looper = _next;
		}
		return (result > 0) ? result : 1;
	}

	public int getAll() {
		if (!_visible) {
			return 0;
		}
		int result = 1;
		SortedList<TreeNode<T>> looper = _children;
		while (looper.size > 0) {
			result = result + looper.size;
			SortedList<TreeNode<T>> _next = new SortedList<TreeNode<T>>();
			for (TreeNode<T> node : looper) {
				if (node != null && node._visible) {
					_next.addAll(node.getChildren());
				}
			}
			looper = _next;
		}
		return result;
	}

	public TreeNode<T> getParent() {
		return this._parent;
	}

	public SortedList<TreeNode<T>> getChildren() {
		return this._children;
	}

	public boolean isRoot() {
		return _parent == null;
	}

	public boolean isLeaf() {
		return _children.size() == 0;
	}

	private void registerChildForSearch(TreeNode<T> node) {
		_elementsIndex.add(node);
		if (_parent != null) {
			_parent.registerChildForSearch(node);
		}
	}

	@SuppressWarnings("unchecked")
	public TreeNode<T> removeChild(T child) {
		if (child != null) {
			if (child instanceof String) {
				TreeNode<T> childNode = searchNode((String) child);
				if (childNode != null) {
					return removeNode(childNode);
				}
			} else if (child instanceof TreeNode) {
				return removeNode((TreeNode<T>) child);
			}
		}
		return this;
	}

	public TreeNode<T> removeNode(TreeNode<T> childNode) {
		if (childNode != null) {
			childNode._parent = null;
			_children.remove(childNode);
		}
		return this;
	}

	public TreeNode<T> removeNode() {
		TreeNode<T> node = _children.remove();
		if (node != null) {
			node._parent = null;
		}
		return this;
	}

	public TreeNode<T> removeNode(int idx) {
		TreeNode<T> node = _children.remove(idx);
		if (node != null) {
			node._parent = null;
		}
		return this;
	}

	public TreeNode<T> searchParentName(String name) {
		if (!_visible) {
			return null;
		}
		TreeNode<T> data = null;
		SortedList<TreeNode<T>> looper = _children;
		while (looper.size > 0) {
			SortedList<TreeNode<T>> _next = new SortedList<TreeNode<T>>();
			for (TreeNode<T> node : looper) {
				if (node.getParent() != null && node.getParent().getName().equals(name)) {
					data = node;
					break;
				}
				_next.addAll(node.getChildren());
			}
			looper = _next;
		}
		return data;
	}

	public TreeNode<T> searchNode(String name) {
		if (!_visible) {
			return null;
		}
		TreeNode<T> data = null;
		SortedList<TreeNode<T>> looper = _children;
		while (looper.size > 0) {
			SortedList<TreeNode<T>> _next = new SortedList<TreeNode<T>>();
			for (TreeNode<T> node : looper) {
				if (node.getName().equals(name)) {
					data = node;
					break;
				}
				_next.addAll(node.getChildren());
			}
			looper = _next;
		}
		return data;
	}

	public TreeNodeIter<T> treeIterator() {
		return new TreeNodeIter<T>(this);
	}

	@Override
	public Iterator<TreeNode<T>> iterator() {
		return treeIterator();
	}

	@Override
	public boolean equals(Object o) {
		boolean result = false;
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (o instanceof TreeNode) {
			@SuppressWarnings("unchecked")
			TreeNode<T> node = (TreeNode<T>) o;
			if (this._name.equals(node.getName())) {
				result = true;
			}
		}
		return result;
	}

	public TreeNode<T> current() {
		if (!_visible) {
			return null;
		}
		TreeNode<T> child = _children.element();
		if (child != null && child._visible) {
			return child;
		}
		return null;
	}

	public TreeNode<T> first() {
		if (!_visible) {
			return null;
		}
		TreeNode<T> child = _children.getFirst();
		if (child != null && child._visible) {
			return child;
		}
		return null;
	}

	public TreeNode<T> last() {
		if (!_visible) {
			return null;
		}
		TreeNode<T> child = _children.getLast();
		if (child != null && child._visible) {
			return child;
		}
		return null;
	}

	public TreeNode<T> next() {
		return _children.next();
	}

	public TreeNode<T> prev() {
		return _children.prev();
	}

	public String getName() {
		return this._name;
	}

	public TreeNode<T> show() {
		return setVisible(true);
	}

	public TreeNode<T> hide() {
		return setVisible(false);
	}

	public boolean isVisible() {
		return _visible;
	}

	public TreeNode<T> setVisible(boolean v) {
		this._visible = v;
		return this;
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = _children.size - 1; i > -1; i--) {
			TreeNode<T> node = _children.get(i);
			hashCode = 31 * hashCode + (node == null ? 0 : node.hashCode());
		}
		return hashCode;
	}

	@Override
	public String toString() {
		if (!_visible) {
			return LSystem.EMPTY;
		}
		StrBuilder sbr = new StrBuilder();
		sbr.append(LSystem.LF);
		sbr.append(this._name);
		sbr.append(LSystem.LF);
		sbr.append(LSystem.BRACKET_START);
		for (TreeNode<T> node : _children) {
			if (node != null && node._visible) {
				sbr.append(StringUtils.replace(node.toString(), "\n", "\n\t"));
			}
		}
		sbr.append(LSystem.LF);
		sbr.append(LSystem.BRACKET_END);
		return sbr.toString();
	}

}
