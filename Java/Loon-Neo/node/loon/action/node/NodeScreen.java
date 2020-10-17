package loon.action.node;

import loon.LSystem;
import loon.Screen;
import loon.action.sprite.SpriteBatch;
import loon.events.SysInput;
import loon.events.SysTouch;
import loon.events.UpdateListener;
import loon.opengl.GLEx;
import loon.physics.SpriteBatchScreen;
import loon.utils.timer.LTimerContext;

public abstract class NodeScreen extends SpriteBatchScreen {

	private LNNode content;

	private LNNode modal;

	private LNNode hoverNode;

	private LNNode selectedNode;

	private LNNode[] clickNode;

	private boolean isClicked;

	protected UpdateListener updateListener;

	public NodeScreen() {
		super();
	}

	@Override
	public void init() {
		super.init();
		this.clickNode = new LNNode[1];
		this.setNode(new LNNode(this, LSystem.viewSize.getRect()));
	}

	public void setNode(LNNode node) {
		if (content == node) {
			return;
		}
		this.content = node;
	}

	public LNNode node() {
		return content;
	}

	public int size() {
		return content == null ? 0 : content.getNodeCount();
	}

	public void runAction(LNAction action) {
		if (content != null) {
			content.runAction(action);
		}
	}

	public void addNode(LNNode node) {
		addNode(node, 0);
	}

	public void add(LNNode node) {
		addNode(node, 0);
	}

	public void addNode(LNNode node, int z) {
		if (node == null) {
			return;
		}
		this.content.addNode(node, z);
		this.processTouchMotionEvent();
	}

	public int removeNode(LNNode node) {
		int removed = this.removeNode(this.content, node);
		if (removed != -1) {
			this.processTouchMotionEvent();
		}
		return removed;
	}

	private int removeNode(LNNode container, LNNode node) {
		int removed = container.removeNode(node);
		LNNode[] nodes = container.childs;
		int i = 0;
		while (removed == -1 && i < nodes.length - 1) {
			if (nodes[i].isContainer()) {
				removed = this.removeNode(nodes[i], node);
			}
			i++;
		}

		return removed;
	}

	private void processEvents() {
		this.processTouchMotionEvent();
		if (this.hoverNode != null && this.hoverNode.isEnabled()) {
			this.processTouchEvent();
		}
		if (this.selectedNode != null && this.selectedNode.isEnabled()) {
			this.processKeyEvent();
		}
	}

	private void processTouchMotionEvent() {
		if (this.hoverNode != null && this.hoverNode.isEnabled() && SysTouch.isDrag()) {
			if (getTouchDY() != 0 || getTouchDY() != 0) {
				this.hoverNode.processTouchDragged();
			}
		} else {
			if (SysTouch.isDrag() || SysTouch.isMove() || SysTouch.isDown()) {
				LNNode node = this.findNode(getTouchX(), getTouchY());
				if (node != null) {
					this.hoverNode = node;
				}
			}
		}
	}

	private void processTouchEvent() {
		int pressed = getTouchPressed(), released = getTouchReleased();
		if (pressed > SysInput.NO_BUTTON) {
			if (!isClicked) {
				this.hoverNode.processTouchPressed();
			}
			this.clickNode[0] = this.hoverNode;
			if (this.hoverNode.isFocusable()) {
				if ((pressed == SysTouch.TOUCH_DOWN || pressed == SysTouch.TOUCH_UP)
						&& this.hoverNode != this.selectedNode) {
					this.selectNode(this.hoverNode);
				}
			}
		}
		if (released > SysInput.NO_BUTTON) {
			if (!isClicked) {
				this.hoverNode.processTouchReleased();
			}
		}
		this.isClicked = false;
	}

	private void processKeyEvent() {
		if (getKeyPressed() != SysInput.NO_KEY) {
			this.selectedNode.keyPressed();
		}
		if (getKeyReleased() != SysInput.NO_KEY && this.selectedNode != null) {
			this.selectedNode.processKeyReleased();
		}
	}

	public LNNode findNode(int x, int y) {
		if (content == null) {
			return null;
		}
		if (this.modal != null && !this.modal.isContainer()) {
			return content.findNode(x, y);
		}
		LNNode panel = (this.modal == null) ? this.content : (this.modal);
		LNNode node = panel.findNode(x, y);
		return node;
	}

	public void clearFocus() {
		this.deselectNode();
	}

	void deselectNode() {
		if (this.selectedNode == null) {
			return;
		}
		this.selectedNode.setSelected(false);
		this.selectedNode = null;
	}

	public boolean selectNode(LNNode node) {
		if (!node.isVisible() || !node.isEnabled() || !node.isFocusable()) {
			return false;
		}
		this.deselectNode();
		node.setSelected(true);
		this.selectedNode = node;
		return true;
	}

	public void setNodeStat(LNNode node, boolean active) {
		if (!active) {
			if (this.hoverNode == node) {
				this.processTouchMotionEvent();
			}
			if (this.selectedNode == node) {
				this.deselectNode();
			}
			this.clickNode[0] = null;
			if (this.modal == node) {
				this.modal = null;
			}
		} else {
			this.processTouchMotionEvent();
		}
		if (node == null) {
			return;
		}
		if (node.isContainer()) {
			LNNode[] nodes = (node).childs;
			int size = (node).getNodeCount();
			for (int i = 0; i < size; i++) {
				this.setNodeStat(nodes[i], active);
			}
		}
	}

	public void clearNodesStat(LNNode[] node) {
		boolean checkTouchMotion = false;
		for (int i = 0; i < node.length; i++) {
			if (this.hoverNode == node[i]) {
				checkTouchMotion = true;
			}

			if (this.selectedNode == node[i]) {
				this.deselectNode();
			}

			this.clickNode[0] = null;

		}

		if (checkTouchMotion) {
			this.processTouchMotionEvent();
		}
	}

	final void validateContainer(LNNode container) {
		if (content == null) {
			return;
		}
		LNNode[] nodes = container.childs;
		int size = container.getNodeCount();
		for (int i = 0; i < size; i++) {
			if (nodes[i].isContainer()) {
				this.validateContainer(nodes[i]);
			}
		}
	}

	public LNNode getTopNode() {
		if (content == null) {
			return null;
		}
		LNNode[] nodes = content.childs;
		int size = nodes.length;
		if (size > 1) {
			return nodes[1];
		}
		return null;
	}

	public LNNode getBottomNode() {
		if (content == null) {
			return null;
		}
		LNNode[] nodes = content.childs;
		int size = nodes.length;
		if (size > 0) {
			return nodes[size - 1];
		}
		return null;
	}

	@Override
	public void setSize(int w, int h) {
		if (content != null) {
			this.content.setSize(w, h);
		}
	}

	public LNNode getHoverNode() {
		return this.hoverNode;
	}

	public LNNode getSelectedNode() {
		return this.selectedNode;
	}

	public LNNode getModal() {
		return this.modal;
	}

	public void setModal(LNNode node) {
		if (node != null && !node.isVisible()) {
			throw new RuntimeException("Can't set invisible node as modal node!");
		}
		this.modal = node;
	}

	public LNNode get() {
		if (content != null) {
			return content.get();
		}
		return null;
	}

	@Override
	public void onLoading() {
		content.setScreen(this);
		for (LNNode node : content.childs) {
			if (node != null) {
				node.onSceneActive();
			}
		}
	}

	@Override
	public final void updating(LTimerContext timer) {
		if (content == null) {
			return;
		}
		if (content.isVisible()) {
			processEvents();
			content.update(timer.timeSinceLastUpdate);
		}
	}

	protected void drawing(GLEx g, SpriteBatch batch) {
		if (batch == null || isUseGLEx()) {
			if (content != null && content.isVisible()) {
				content.drawNode(g);
			}
		} else {
			if (content != null && content.isVisible()) {
				content.drawNode(batch);
			}
		}
	}

	@Override
	public Screen setAutoDestory(final boolean a) {
		super.setAutoDestory(a);
		if (content != null) {
			content.setAutoDestroy(a);
		}
		return this;
	}

	@Override
	public boolean isAutoDestory() {
		if (content != null) {
			return content.isAutoDestory();
		}
		return super.isAutoDestory();
	}

	static LNNode[] expand(LNNode[] objs, int i, boolean flag) {
		int size = objs.length;
		LNNode[] newArrays = new LNNode[size + i];
		System.arraycopy(objs, 0, newArrays, flag ? 0 : i, size);
		return newArrays;
	}

	static LNNode[] cut(LNNode[] objs, int size) {
		int j;
		if ((j = objs.length) == 1) {
			return new LNNode[0];
		}
		int k;
		if ((k = j - size - 1) > 0) {
			System.arraycopy(objs, size + 1, objs, size, k);
		}
		j--;
		LNNode[] newArrays = new LNNode[j];
		System.arraycopy(objs, 0, newArrays, 0, j);
		return newArrays;
	}

	@Override
	public void close() {
		super.close();
		if (content != null) {
			content.close();
			content = null;
		}
	}

}
