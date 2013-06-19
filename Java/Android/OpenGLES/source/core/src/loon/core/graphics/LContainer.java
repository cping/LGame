package loon.core.graphics;

import java.util.Arrays;
import java.util.Comparator;

import loon.core.graphics.opengl.GLEx;
import loon.utils.CollectionUtils;

/**
 * 
 * Copyright 2008 - 2009
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
 * @version 0.1
 */

public abstract class LContainer extends LComponent {

	private static final Comparator<LComponent> DEFAULT_COMPARATOR = new Comparator<LComponent>() {
		@Override
		public int compare(LComponent o1, LComponent o2) {
			return o2.getLayer() - o1.getLayer();
		}
	};

	protected boolean locked;

	private Comparator<LComponent> comparator = LContainer.DEFAULT_COMPARATOR;

	private LComponent[] childs = new LComponent[0];

	private int childCount = 0;

	private LComponent latestInserted = null;

	public LContainer(int x, int y, int w, int h) {
		super(x, y, w, h);
		this.setFocusable(false);
	}

	@Override
	public boolean isContainer() {
		return true;
	}

	public synchronized void add(LComponent comp) {
		if (this.contains(comp)) {
			return;
		}
		if (comp.getContainer() != null) {
			comp.setContainer(null);
		}
		comp.setContainer(this);
		this.childs = (LComponent[]) CollectionUtils.expand(this.childs, 1,
				false);
		this.childs[0] = comp;
		this.childCount++;
		this.desktop.setDesktop(comp);
		this.sortComponents();
		this.latestInserted = comp;
	}

	public synchronized void add(LComponent comp, int index) {
		if (comp.getContainer() != null) {
			throw new IllegalStateException(comp
					+ " already reside in another container!!!");
		}
		comp.setContainer(this);
		LComponent[] newChilds = new LComponent[this.childs.length + 1];
		this.childCount++;
		int ctr = 0;
		for (int i = 0; i < this.childCount; i++) {
			if (i != index) {
				newChilds[i] = this.childs[ctr];
				ctr++;
			}
		}
		this.childs = newChilds;
		this.childs[index] = comp;
		this.desktop.setDesktop(comp);
		this.sortComponents();
		this.latestInserted = comp;
	}

	public synchronized boolean contains(LComponent comp) {
		if (comp == null) {
			return false;
		}
		if (childs == null) {
			return false;
		}
		for (int i = 0; i < this.childCount; i++) {
			if (childs[i] != null && comp.equals(childs[i])) {
				return true;
			}
		}
		return false;
	}

	public synchronized int remove(LComponent comp) {
		for (int i = 0; i < this.childCount; i++) {
			if (this.childs[i] == comp) {
				this.remove(i);
				return i;
			}
		}
		return -1;
	}

	public synchronized int remove(Class<? extends LComponent> clazz) {
		if (clazz == null) {
			return -1;
		}
		int count = 0;
		for (int i = childCount; i > 0; i--) {
			int index = i - 1;
			LComponent comp = this.childs[index];
			Class<? extends LComponent> cls = comp.getClass();
			if (clazz == null || clazz == cls || clazz.isInstance(comp)
					|| clazz.equals(cls)) {
				this.remove(index);
				count++;
			}
		}
		return count;
	}

	public synchronized LComponent remove(int index) {
		LComponent comp = this.childs[index];

		this.desktop.setComponentStat(comp, false);
		comp.setContainer(null);
		// comp.dispose();
		this.childs = (LComponent[]) CollectionUtils.cut(this.childs, index);
		this.childCount--;

		return comp;
	}

	public void clear() {
		this.desktop.clearComponentsStat(this.childs);
		for (int i = 0; i < this.childCount; i++) {
			this.childs[i].setContainer(null);
			// this.childs[i].dispose();
		}
		this.childs = new LComponent[0];
		this.childCount = 0;
	}

	public synchronized void replace(LComponent oldComp, LComponent newComp) {
		int index = this.remove(oldComp);
		this.add(newComp, index);
	}

	@Override
	public void update(long timer) {
		if (isClose) {
			return;
		}
		if (!this.isVisible()) {
			return;
		}
		synchronized (childs) {
			super.update(timer);
			LComponent component;
			for (int i = 0; i < this.childCount; i++) {
				component = childs[i];
				if (component != null) {
					component.update(timer);
				}
			}
		}
	}

	@Override
	public void validatePosition() {
		if (isClose) {
			return;
		}
		super.validatePosition();

		for (int i = 0; i < this.childCount; i++) {
			this.childs[i].validatePosition();
		}

		if (!this.elastic) {
			for (int i = 0; i < this.childCount; i++) {
				if (this.childs[i].getX() > this.getWidth()
						|| this.childs[i].getY() > this.getHeight()
						|| this.childs[i].getX() + this.childs[i].getWidth() < 0
						|| this.childs[i].getY() + this.childs[i].getHeight() < 0) {
					setElastic(true);
					break;
				}
			}
		}
	}

	@Override
	protected void validateSize() {
		super.validateSize();

		for (int i = 0; i < this.childCount; i++) {
			this.childs[i].validateSize();
		}
	}

	@Override
	public void createUI(GLEx g) {
		if (isClose) {
			return;
		}
		if (!this.isVisible()) {
			return;
		}
		synchronized (childs) {
			super.createUI(g);
			if (this.elastic) {
				g.setClip(this.getScreenX(), this.getScreenY(),
						this.getWidth(), this.getHeight());
			}
			this.renderComponents(g);
			if (this.elastic) {
				g.clearClip();
			}
		}
	}

	protected void renderComponents(GLEx g) {
		for (int i = this.childCount - 1; i >= 0; i--) {
			this.childs[i].createUI(g);
		}
	}

	public void sendToFront(LComponent comp) {

		if (this.childCount <= 1 || this.childs[0] == comp) {
			return;
		}
		if (childs[0] == comp) {
			return;
		}
		for (int i = 0; i < this.childCount; i++) {
			if (this.childs[i] == comp) {
				this.childs = (LComponent[]) CollectionUtils
						.cut(this.childs, i);
				this.childs = (LComponent[]) CollectionUtils.expand(
						this.childs, 1, false);
				this.childs[0] = comp;
				this.sortComponents();
				break;
			}
		}
	}

	public void sendToBack(LComponent comp) {
		if (this.childCount <= 1 || this.childs[this.childCount - 1] == comp) {
			return;
		}
		if (childs[this.childCount - 1] == comp) {
			return;
		}
		for (int i = 0; i < this.childCount; i++) {
			if (this.childs[i] == comp) {
				this.childs = (LComponent[]) CollectionUtils
						.cut(this.childs, i);
				this.childs = (LComponent[]) CollectionUtils.expand(
						this.childs, 1, true);
				this.childs[this.childCount - 1] = comp;
				this.sortComponents();
				break;
			}
		}
	}

	public void sortComponents() {
		Arrays.sort(this.childs, this.comparator);
	}

	protected void transferFocus(LComponent component) {
		for (int i = 0; i < this.childCount; i++) {
			if (component == this.childs[i]) {
				int j = i;
				do {
					if (--i < 0) {
						i = this.childCount - 1;
					}
					if (i == j) {
						return;
					}
				} while (!this.childs[i].requestFocus());

				break;
			}
		}
	}

	protected void transferFocusBackward(LComponent component) {
		for (int i = 0; i < this.childCount; i++) {
			if (component == this.childs[i]) {
				int j = i;
				do {
					if (++i >= this.childCount) {
						i = 0;
					}
					if (i == j) {
						return;
					}
				} while (!this.childs[i].requestFocus());

				break;
			}
		}
	}

	@Override
	public boolean isSelected() {
		if (!super.isSelected()) {
			for (int i = 0; i < this.childCount; i++) {
				if (this.childs[i].isSelected()) {
					return true;
				}
			}
			return false;

		} else {
			return true;
		}
	}

	public boolean isElastic() {
		return this.elastic;
	}

	public void setElastic(boolean b) {
		if (getWidth() > 128 || getHeight() > 128) {
			this.elastic = b;
		} else {
			this.elastic = false;
		}
	}

	public Comparator<LComponent> getComparator() {
		return this.comparator;
	}

	public void setComparator(Comparator<LComponent> c) {
		if (c == null) {
			throw new NullPointerException("Comparator can not null !");
		}

		this.comparator = c;
		this.sortComponents();
	}

	public LComponent findComponent(int x1, int y1) {
		if (!this.intersects(x1, y1)) {
			return null;
		}
		for (int i = 0; i < this.childCount; i++) {
			if (this.childs[i].intersects(x1, y1)) {
				LComponent comp = (!this.childs[i].isContainer()) ? this.childs[i]
						: ((LContainer) this.childs[i]).findComponent(x1, y1);
				return comp;
			}
		}
		return this;
	}

	public int getComponentCount() {
		return this.childCount;
	}

	public LComponent[] getComponents() {
		return this.childs;
	}

	public LComponent get() {
		return this.latestInserted;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (autoDestroy) {
			if (childs != null) {
				for (LComponent c : childs) {
					if (c != null) {
						c.dispose();
						c = null;
					}
				}
			}
		}
	}

}
