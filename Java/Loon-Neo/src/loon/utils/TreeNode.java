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

public class TreeNode {
	
	private final String name;

	private TArray<TreeNode> children = new TArray<TreeNode>();

	public TreeNode() {
          this("unkown");
	}

	public TreeNode(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public int getDepth() {
		int result = 1;
		TArray<TreeNode> looper = children;
		while (looper.size > 0) {
			result = result + 1;
			TArray<TreeNode> next = new TArray<TreeNode>();
			for (TreeNode node : looper) {
				next.addAll(node.getChildren());
			}
			looper = next;
		}
		return result;
	}

	public int getWidth() {
		int result = 0;
		TArray<TreeNode> looper = children;
		while (looper.size > 0) {
			TArray<TreeNode> next = new TArray<TreeNode>();
			for (TreeNode node : looper) {
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
		TArray<TreeNode> looper = children;
		while (looper.size > 0) {
			result = result + looper.size;
			TArray<TreeNode> next = new TArray<TreeNode>();
			for (TreeNode node : looper) {
				next.addAll(node.getChildren());
			}
			looper = next;
		}
		return result;
	}

	public void addNode(TreeNode node) {
		children.add(node);
	}

	public TArray<TreeNode> getChildren() {
		return this.children;
	}

	public TreeNode getNode(String name) {
		TreeNode sbr = null;
		TArray<TreeNode> looper = children;
		while (looper.size > 0) {
			TArray<TreeNode> next = new TArray<TreeNode>();
			for (TreeNode node : looper) {
				if (node.getName().equals(name)) {
					sbr = node;
					break;
				}
				next.addAll(node.getChildren());
			}
			looper = next;
		}
		return sbr;
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
			TreeNode node = (TreeNode) o;
			if (this.name.equals(node.getName())) {
				result = true;
			}
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sbr = new StringBuilder();
		sbr.append("\n" + this.name);
		sbr.append("\n[");
		for (TreeNode node : children) {
			sbr.append(StringUtils.replace(node.toString(), "\n", "\n\t"));
		}
		sbr.append("\n]");
		return sbr.toString();
	}
}
