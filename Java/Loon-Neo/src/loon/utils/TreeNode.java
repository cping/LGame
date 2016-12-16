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
