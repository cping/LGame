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
package loon.stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import loon.geom.Affine2f;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

public class GroupPlayer extends ClippedPlayer implements Iterable<Player> {

	private final List<Player> children = new ArrayList<>();
	private final Affine2f paintTx = new Affine2f();
	private final boolean disableClip;

	public GroupPlayer() {
		super(0, 0);
		disableClip = true;
	}

	public GroupPlayer(float width, float height) {
		super(width, height);
		disableClip = false;
	}

	public boolean isEmpty() {
		return children.isEmpty();
	}

	public int children() {
		return children.size();
	}

	public Player childAt(int index) {
		return children.get(index);
	}

	public void add(Player child) {
		GroupPlayer parent = child.parent();
		if (parent == this) {
			return;
		}
		int count = children.size(), index;
		if (count == 0 || children.get(count - 1).depth() <= child.depth()) {
			index = count;
		} else {
			index = findInsertion(child.depth());
		}
		if (parent != null) {
			parent.remove(child);
		}
		children.add(index, child);
		child.setParent(this);
		if (state.get() == State.ADDED) {
			child.onAdd();
		}
		if (child.interactive()) {
			setInteractive(true);
		}
	}

	public void addAt(Player child, float tx, float ty) {
		add(child.setTranslation(tx, ty));
	}

	public void addCenterAt(Player child, float tx, float ty) {
		add(child.setTranslation(tx - child.width() / 2, ty - child.height()
				/ 2));
	}

	public void addFloorAt(Player child, float tx, float ty) {
		add(child.setTranslation(MathUtils.ifloor(tx), MathUtils.ifloor(ty)));
	}

	public void remove(Player child) {
		int index = findChild(child, child.depth());
		if (index < 0) {
			return;
		}
		remove(index);
	}

	public void removeAll() {
		while (!children.isEmpty()) {
			remove(children.size() - 1);
		}
	}

	public void disposeAll() {
		Player[] toDispose = children.toArray(new Player[children.size()]);
		removeAll();
		for (Player child : toDispose) {
			child.close();
		}
	}

	@Override
	public Iterator<Player> iterator() {
		return children.iterator();
	}

	@Override
	public void close() {
		super.close();
		disposeAll();
	}

	@Override
	public Player hitTestDefault(Vector2f point) {
		float x = point.x, y = point.y;
		boolean sawInteractiveChild = false;
		for (int ii = children.size() - 1; ii >= 0; ii--) {
			Player child = children.get(ii);
			if (!child.interactive()) {
				continue;
			}
			sawInteractiveChild = true;
			if (!child.visible()) {
				continue;
			}
			try {
				child.affine().inverseTransform(point.set(x, y), point);
				point.x += child.originX();
				point.y += child.originY();
				Player l = child.hitTest(point);
				if (l != null) {
					return l;
				}
			} catch (Exception nte) {
				continue;
			}
		}
		if (!sawInteractiveChild && !hasEventListeners()) {
			setInteractive(false);
		}
		return super.hitTestDefault(point);
	}

	@Override
	protected boolean disableClip() {
		return disableClip;
	}

	@Override
	protected void paintClipped(GLEx gl) {
		paintTx.set(gl.tx());
		List<Player> children = this.children;
		for (int ii = 0, ll = children.size(); ii < ll; ii++) {
			gl.tx().set(paintTx);
			children.get(ii).paint(gl);
		}
	}

	int depthChanged(Player child, float oldDepth) {
		int oldIndex = findChild(child, oldDepth);
		float newDepth = child.depth();
		boolean leftCorrect = (oldIndex == 0 || children.get(oldIndex - 1)
				.depth() <= newDepth);
		boolean rightCorrect = (oldIndex == children.size() - 1 || children
				.get(oldIndex + 1).depth() >= newDepth);
		if (leftCorrect && rightCorrect) {
			return oldIndex;
		}
		children.remove(oldIndex);
		int newIndex = findInsertion(newDepth);
		children.add(newIndex, child);
		return newIndex;
	}

	@Override
	void onAdd() {
		super.onAdd();
		for (int ii = 0, ll = children.size(); ii < ll; ii++) {
			children.get(ii).onAdd();
		}
	}

	@Override
	void onRemove() {
		super.onRemove();
		for (int ii = 0, ll = children.size(); ii < ll; ii++) {
			children.get(ii).onRemove();
		}
	}

	@Override
	protected boolean deactivateOnNoListeners() {
		return false;
	}

	private void remove(int index) {
		Player child = children.remove(index);
		child.onRemove();
		child.setParent(null);
	}

	private int findChild(Player child, float depth) {
		int startIdx = findInsertion(depth);
		for (int ii = startIdx - 1; ii >= 0; ii--) {
			Player c = children.get(ii);
			if (c == child) {
				return ii;
			}
			if (c.depth() != depth) {
				break;
			}
		}
		for (int ii = startIdx, ll = children.size(); ii < ll; ii++) {
			Player c = children.get(ii);
			if (c == child) {
				return ii;
			}
			if (c.depth() != depth) {
				break;
			}
		}
		return -1;
	}

	private int findInsertion(float depth) {
		int low = 0, high = children.size() - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			float midDepth = children.get(mid).depth();
			if (depth > midDepth) {
				low = mid + 1;
			} else if (depth < midDepth) {
				high = mid - 1;
			} else {
				return mid;
			}
		}
		return low;
	}

	private long _elapsed;

	@Override
	public void update(long elapsedTime) {
		this._elapsed = elapsedTime;
	}

	public long getElapsed() {
		return this._elapsed;
	}

}
