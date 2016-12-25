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
package loon.component;

import loon.action.ActionBind;
import loon.component.layout.LayoutConstraints;
import loon.component.layout.LayoutManager;
import loon.component.layout.LayoutPort;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.CollectionUtils;
import loon.utils.LayerSorter;
import loon.utils.TArray;

public abstract class LContainer extends LComponent {

	protected LComponent[] _childs = new LComponent[0];

	private LayoutConstraints _rootConstraints;

	private final static LayerSorter<LComponent> compSorter = new LayerSorter<LComponent>(
			false);

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

	public LComponent add(LComponent... comps) {
		for (int i = 0; i < comps.length; i++) {
			add(comps[i]);
		}
		return this;
	}

	public LComponent add(LComponent comp) {
		if (this == comp) {
			return this;
		}
		if (this.contains(comp)) {
			return this;
		}
		if (comp.getContainer() != null) {
			comp.setContainer(null);
		}
		comp.setContainer(this);
		comp.setState(State.ADDED);
		this._childs = CollectionUtils.expand(this._childs, 1, false);
		this._childs[0] = comp;
		this.childCount++;
		this.desktop.setDesktop(comp);
		if (desktop != null) {
			if (this.input == null) {
				this.input = desktop.input;
			}
			if (comp.input == null) {
				comp.input = desktop.input;
			}
		}
		this.sortComponents();
		this.latestInserted = comp;
		return this;
	}

	public LComponent add(LComponent comp, int index) {
		if (comp.getContainer() != null) {
			throw new IllegalStateException(comp
					+ " already reside in another container!!!");
		}
		comp.setContainer(this);
		comp.setState(State.ADDED);
		LComponent[] newChilds = new LComponent[this._childs.length + 1];
		this.childCount++;
		int ctr = 0;
		for (int i = 0; i < this.childCount; i++) {
			if (i != index) {
				newChilds[i] = this._childs[ctr];
				ctr++;
			}
		}
		this._childs = newChilds;
		this._childs[index] = comp;
		this.desktop.setDesktop(comp);
		this.sortComponents();
		this.latestInserted = comp;
		return this;
	}

	/**
	 * 返回一组拥有指定标签的组件
	 * 
	 * @param tags
	 * @return
	 */
	public TArray<LComponent> findTags(Object... tags) {
		TArray<LComponent> list = new TArray<LComponent>();
		final int size = this.childCount;
		for (Object tag : tags) {
			for (int i = size - 1; i > -1; i--) {
				if (this._childs[i].Tag == tag
						|| tag.equals(this._childs[i].Tag)) {
					list.add(this._childs[i]);
				}
			}
		}
		return list;
	}

	/**
	 * 返回一组没有指定标签的组件
	 * 
	 * @param tags
	 * @return
	 */
	public TArray<LComponent> findNotTags(Object... tags) {
		TArray<LComponent> list = new TArray<LComponent>();
		final int size = this.childCount;
		for (Object tag : tags) {
			for (int i = size - 1; i > -1; i--) {
				if (!tag.equals(this._childs[i].Tag)) {
					list.add(this._childs[i]);
				}
			}
		}
		return list;
	}

	/**
	 * 返回一组拥有指定组件名称的组件
	 * 
	 * @param names
	 * @return
	 */
	public TArray<LComponent> findUINames(String... names) {
		TArray<LComponent> list = new TArray<LComponent>();
		final int size = this.childCount;
		for (String name : names) {
			for (int i = size - 1; i > -1; i--) {
				if (name.equals(this._childs[i].getUIName())) {
					list.add(this._childs[i]);
				}
			}
		}
		return list;
	}

	/**
	 * 返回一组没有指定名称的组件
	 * 
	 * @param names
	 * @return
	 */
	public TArray<LComponent> findNotUINames(String... names) {
		TArray<LComponent> list = new TArray<LComponent>();
		final int size = this.childCount;
		for (String name : names) {
			for (int i = size - 1; i > -1; i--) {
				if (!name.equals(this._childs[i].getUIName())) {
					list.add(this._childs[i]);
				}
			}
		}
		return list;
	}

	/**
	 * 返回一组指定名的组件
	 * 
	 * @param names
	 * @return
	 */
	public TArray<LComponent> findNames(String... names) {
		TArray<LComponent> list = new TArray<LComponent>();
		final int size = this.childCount;
		for (String name : names) {
			for (int i = size - 1; i > -1; i--) {
				if (name.equals(this._childs[i].getName())) {
					list.add(this._childs[i]);
				}
			}
		}
		return list;
	}

	/**
	 * 返回一组没有指定名的组件
	 * 
	 * @param names
	 * @return
	 */
	public TArray<LComponent> findNotNames(String... names) {
		TArray<LComponent> list = new TArray<LComponent>();
		final int size = this.childCount;
		for (String name : names) {
			for (int i = size - 1; i > -1; i--) {
				if (!name.equals(this._childs[i].getName())) {
					list.add(this._childs[i]);
				}
			}
		}
		return list;
	}

	public boolean contains(LComponent comp) {
		if (comp == null) {
			return false;
		}
		if (_childs == null) {
			return false;
		}
		for (int i = 0; i < this.childCount; i++) {
			if (_childs[i] != null && comp.equals(_childs[i])) {
				return true;
			}
		}
		return false;
	}

	public int remove(LComponent comp) {
		final int size = this.childCount;
		for (int i = size - 1; i > -1; i--) {
			if (this._childs[i] == comp) {
				this.remove(i);
				return i;
			}
		}
		return -1;
	}

	public boolean removeTag(Object tag) {
		boolean flag = false;
		final int size = this.childCount;
		for (int i = size - 1; i > -1; i--) {
			if (this._childs[i].Tag == tag || tag.equals(this._childs[i].Tag)) {
				this.remove(i);
				flag = true;
			}
		}
		return flag;
	}

	public boolean removeNotTag(Object tag) {
		boolean flag = false;
		final int size = this.childCount;
		for (int i = size - 1; i > -1; i--) {
			if (!tag.equals(this._childs[i].Tag)) {
				this.remove(i);
				flag = true;
			}
		}
		return flag;
	}

	public boolean removeUIName(String name) {
		boolean flag = false;
		final int size = this.childCount;
		for (int i = size - 1; i > -1; i--) {
			if (name.equals(this._childs[i].getUIName())) {
				this.remove(i);
				flag = true;
			}
		}
		return flag;
	}

	public boolean removeNotUIName(String name) {
		boolean flag = false;
		final int size = this.childCount;
		for (int i = size - 1; i > -1; i--) {
			if (!name.equals(this._childs[i].getUIName())) {
				this.remove(i);
				flag = true;
			}
		}
		return flag;
	}

	public boolean removeName(String name) {
		boolean flag = false;
		final int size = this.childCount;
		for (int i = size - 1; i > -1; i--) {
			if (name.equals(this._childs[i].getName())) {
				this.remove(i);
				flag = true;
			}
		}
		return flag;
	}

	public boolean removeNotName(String name) {
		boolean flag = false;
		final int size = this.childCount;
		for (int i = size - 1; i > -1; i--) {
			if (!name.equals(this._childs[i].getName())) {
				this.remove(i);
				flag = true;
			}
		}
		return flag;
	}

	public LComponent remove(int index) {
		LComponent comp = this._childs[index];
		this.desktop.setComponentStat(comp, false);
		comp.setContainer(null);
		comp.setState(State.REMOVED);
		if (comp != null && comp instanceof ActionBind) {
			removeActionEvents((ActionBind) comp);
		}
		// comp.dispose();
		this._childs = CollectionUtils.cut(this._childs, index);
		this.childCount--;
		return comp;
	}

	public void clear() {
		this.desktop.clearComponentsStat(this._childs);
		for (int i = 0; i < this.childCount; i++) {
			this._childs[i].setContainer(null);
			this._childs[i].setState(State.REMOVED);
			// this._childs[i].dispose();
		}
		this._childs = new LComponent[0];
		this.childCount = 0;
	}

	public void replace(LComponent oldComp, LComponent newComp) {
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
		synchronized (_childs) {
			super.update(timer);
			LComponent component;
			for (int i = 0; i < this.childCount; i++) {
				component = _childs[i];
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
			this._childs[i].validatePosition();
		}
		if (!this.elastic) {
			for (int i = 0; i < this.childCount; i++) {
				if (this._childs[i].getX() > this.getWidth()
						|| this._childs[i].getY() > this.getHeight()
						|| this._childs[i].getX() + this._childs[i].getWidth() < 0
						|| this._childs[i].getY() + this._childs[i].getHeight() < 0) {
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
			this._childs[i].validateSize();
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
		synchronized (_childs) {
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
			this._childs[i].createUI(g);
		}
	}

	public void sendToFront(LComponent comp) {
		if (this.childCount <= 1 || this._childs[0] == comp) {
			return;
		}
		if (_childs[0] == comp) {
			return;
		}
		for (int i = 0; i < this.childCount; i++) {
			if (this._childs[i] == comp) {
				this._childs = CollectionUtils.cut(this._childs, i);
				this._childs = CollectionUtils.expand(this._childs, 1, false);
				this._childs[0] = comp;
				this.sortComponents();
				break;
			}
		}
	}

	public void sendToBack(LComponent comp) {
		if (this.childCount <= 1 || this._childs[this.childCount - 1] == comp) {
			return;
		}
		if (_childs[this.childCount - 1] == comp) {
			return;
		}
		for (int i = 0; i < this.childCount; i++) {
			if (this._childs[i] == comp) {
				this._childs = CollectionUtils.cut(this._childs, i);
				this._childs = CollectionUtils.expand(this._childs, 1, true);
				this._childs[this.childCount - 1] = comp;
				this.sortComponents();
				break;
			}
		}
	}

	public void sortComponents() {
		compSorter.sort(this._childs);
	}

	protected void transferFocus(LComponent component) {
		for (int i = 0; i < this.childCount; i++) {
			if (component == this._childs[i]) {
				int j = i;
				do {
					if (--i < 0) {
						i = this.childCount - 1;
					}
					if (i == j) {
						return;
					}
				} while (!this._childs[i].requestFocus());

				break;
			}
		}
	}

	protected void transferFocusBackward(LComponent component) {
		for (int i = 0; i < this.childCount; i++) {
			if (component == this._childs[i]) {
				int j = i;
				do {
					if (++i >= this.childCount) {
						i = 0;
					}
					if (i == j) {
						return;
					}
				} while (!this._childs[i].requestFocus());

				break;
			}
		}
	}

	public boolean isSelected() {
		if (!super.isSelected()) {
			for (int i = 0; i < this.childCount; i++) {
				if (this._childs[i].isSelected()) {
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
		if (getWidth() > 32 || getHeight() > 32) {
			this.elastic = b;
		} else {
			this.elastic = false;
		}
	}

	public LComponent findComponent(int x1, int y1) {
		if (!this.intersects(x1, y1)) {
			return null;
		}
		for (int i = 0; i < this.childCount; i++) {
			if (this._childs[i].intersects(x1, y1)) {
				LComponent comp = (!this._childs[i].isContainer()) ? this._childs[i]
						: ((LContainer) this._childs[i]).findComponent(x1, y1);
				LContainer container = comp.getContainer();
				if (container != null && container instanceof LScrollContainer) {
					if (container.contains(comp)
							&& (comp.getWidth() >= container.getWidth() || comp
									.getHeight() >= container.getHeight())) {
						return comp.getContainer();
					}
				}
				return comp;
			}
		}
		return this;
	}

	public int getComponentCount() {
		return this.childCount;
	}

	public LComponent[] getComponents() {
		return CollectionUtils.copyOf(this._childs);
	}

	public LComponent get() {
		return this.latestInserted;
	}

	public LayoutConstraints getRootConstraints() {
		if (_rootConstraints == null) {
			_rootConstraints = new LayoutConstraints();
		}
		return _rootConstraints;
	}

	public LayoutPort getLayoutPort() {
		return new LayoutPort(getRectBox(), getRootConstraints());
	}

	public LayoutPort getLayoutPort(final RectBox newBox,
			final LayoutConstraints newBoxConstraints) {
		return new LayoutPort(newBox, newBoxConstraints);
	}

	public LayoutPort getLayoutPort(final LayoutPort src) {
		return new LayoutPort(src);
	}

	public void layoutElements(final LayoutManager manager,
			final LComponent... comps) {
		if (manager != null) {
			manager.layoutElements(this, comps);
		}
	}

	public void layoutElements(final LayoutManager manager,
			final LayoutPort... ports) {
		if (manager != null) {
			manager.layoutElements(getLayoutPort(), ports);
		}
	}

	public void packLayout(final LayoutManager manager) {
		layoutElements(manager, _childs);
	}

	@Override
	public void close() {
		super.close();
		if (autoDestroy) {
			if (_childs != null) {
				for (LComponent c : _childs) {
					if (c != null && !c.isClose) {
						c.close();
						c = null;
					}
				}
			}
		}
	}

}
