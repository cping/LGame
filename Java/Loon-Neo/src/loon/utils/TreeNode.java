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

import loon.LSystem;

public class TreeNode<T> implements Iterable<TreeNode<T>> {

	private final String name;

	private TArray<TreeNode<T>> elementsIndex;

	private TArray<TreeNode<T>> children;

	private TreeNode<T> parent = null;

	private T data = null;

	public TreeNode() {
		this(LSystem.UNKNOWN, null);
	}

	public TreeNode(T data) {
		this(LSystem.UNKNOWN, data);
	}

	public TreeNode(String name, T data) {
		this.name = name;
		this.data = data;
		this.children = new TArray<TreeNode<T>>();
		this.elementsIndex = new TArray<TreeNode<T>>();
		this.elementsIndex.add(this);
	}

	public TreeNode<T> addChild(T child) {
		return addNode(new TreeNode<T>(child));
	}

	public TreeNode<T> addNode(TreeNode<T> childNode) {
		childNode.parent = this;
		children.add(childNode);
		this.registerChildForSearch(childNode);
		return childNode;
	}

	public int getLevel() {
		if (this.isRoot()) {
			return 0;
		} else {
			return parent.getLevel() + 1;
		}
	}

	public TreeNode<T> findTreeNode(Comparable<T> comp) {
		for (TreeNode<T> element : this.elementsIndex) {
			T elData = element.data;
			if (comp.compareTo(elData) == 0) {
				return element;
			}
		}
		return null;
	}

	public int getDepth() {
		int result = 1;
		TArray<TreeNode<T>> looper = children;
		while (looper.size > 0) {
			result = result + 1;
			TArray<TreeNode<T>> next = new TArray<TreeNode<T>>();
			for (TreeNode<T> node : looper) {
				next.addAll(node.getChildren());
			}
			looper = next;
		}
		return result;
	}

	public int getWidth() {
		int result = 0;
		TArray<TreeNode<T>> looper = children;
		while (looper.size > 0) {
			TArray<TreeNode<T>> next = new TArray<TreeNode<T>>();
			for (TreeNode<T> node : looper) {
				if (node.children.size == 0) {
					result = result + 1;
				}
				next.addAll(node.getChildren());
			}
			looper = next;
		}
		return (result > 0) ? result : 1;
	}

	public int getAll() {
		int result = 1;
		TArray<TreeNode<T>> looper = children;
		while (looper.size > 0) {
			result = result + looper.size;
			TArray<TreeNode<T>> next = new TArray<TreeNode<T>>();
			for (TreeNode<T> node : looper) {
				next.addAll(node.getChildren());
			}
			looper = next;
		}
		return result;
	}

	public TreeNode<T> getParent() {
		return this.parent;
	}

	public TArray<TreeNode<T>> getChildren() {
		return this.children;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public boolean isLeaf() {
		return children.size() == 0;
	}

	private void registerChildForSearch(TreeNode<T> node) {
		elementsIndex.add(node);
		if (parent != null) {
			parent.registerChildForSearch(node);
		}
	}

	public TreeNode<T> searchParentName(String name) {
		TreeNode<T> data = null;
		TArray<TreeNode<T>> looper = children;
		while (looper.size > 0) {
			TArray<TreeNode<T>> next = new TArray<TreeNode<T>>();
			for (TreeNode<T> node : looper) {
				if (node.getParent() != null && node.getParent().getName().equals(name)) {
					data = node;
					break;
				}
				next.addAll(node.getChildren());
			}
			looper = next;
		}
		return data;
	}

	public TreeNode<T> searchNode(String name) {
		TreeNode<T> data = null;
		TArray<TreeNode<T>> looper = children;
		while (looper.size > 0) {
			TArray<TreeNode<T>> next = new TArray<TreeNode<T>>();
			for (TreeNode<T> node : looper) {
				if (node.getName().equals(name)) {
					data = node;
					break;
				}
				next.addAll(node.getChildren());
			}
			looper = next;
		}
		return data;
	}

	@Override
	public Iterator<TreeNode<T>> iterator() {
		return new TreeNodeIter<T>(this);
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
			if (this.name.equals(node.getName())) {
				result = true;
			}
		}
		return result;
	}

	public String getName() {
		return this.name;
	}

	static final class TreeNodeIter<T> implements Iterator<TreeNode<T>> {

		enum TreeState {
			Parent, ChildCurNode, ChildSubNode
		}

		private TreeState onNext;
		private TreeNode<T> next;
		private Iterator<TreeNode<T>> childrenCurNodeIter;
		private Iterator<TreeNode<T>> childrenSubNodeIter;

		private TreeNode<T> treeNode;

		public TreeNodeIter(TreeNode<T> treeNode) {
			this.treeNode = treeNode;
			this.onNext = TreeState.Parent;
			this.childrenCurNodeIter = treeNode.children.iterator();
		}

		@Override
		public boolean hasNext() {

			if (this.onNext == TreeState.Parent) {
				this.next = this.treeNode;
				this.onNext = TreeState.ChildCurNode;
				return true;
			}

			if (this.onNext == TreeState.ChildCurNode) {
				if (childrenCurNodeIter.hasNext()) {
					TreeNode<T> childDirect = childrenCurNodeIter.next();
					childrenSubNodeIter = childDirect.iterator();
					this.onNext = TreeState.ChildSubNode;
					return hasNext();
				} else {
					this.onNext = null;
					return false;
				}
			}

			if (this.onNext == TreeState.ChildSubNode) {
				if (childrenSubNodeIter.hasNext()) {
					this.next = childrenSubNodeIter.next();
					return true;
				} else {
					this.next = null;
					this.onNext = TreeState.ChildCurNode;
					return hasNext();
				}
			}

			return false;
		}

		@Override
		public TreeNode<T> next() {
			return this.next;
		}

		@Override
		public void remove() {
		}

	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = children.size - 1; i > -1; i--) {
			TreeNode<T> node = children.get(i);
			hashCode = 31 * hashCode + (node == null ? 0 : node.hashCode());
		}
		return hashCode;
	}

	@Override
	public String toString() {
		StrBuilder sbr = new StrBuilder();
		sbr.append("\n" + this.name);
		sbr.append("\n[");
		for (TreeNode<T> node : children) {
			sbr.append(StringUtils.replace(node.toString(), "\n", "\n\t"));
		}
		sbr.append("\n]");
		return sbr.toString();
	}
}
