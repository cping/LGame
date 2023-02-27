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

import loon.LSysException;
import loon.LSystem;
import loon.action.ActionBind;
import loon.component.layout.LayoutManager;
import loon.component.layout.LayoutPort;
import loon.component.layout.Margin;
import loon.events.GameKey;
import loon.events.QueryEvent;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.CollectionUtils;
import loon.utils.IArray;
import loon.utils.LayerSorter;
import loon.utils.MathUtils;
import loon.utils.StrBuilder;
import loon.utils.TArray;
import loon.utils.reply.Callback;

/**
 * UI组件的上级类,作为容器可以把多个LComponent注入一个LContainer
 */
public abstract class LContainer extends LComponent implements IArray {

	protected LComponent[] _childs = new LComponent[0];

	private Margin _margin;

	private float _newLineHeight = -1f;
	// 滚动x轴
	protected float _component_scrollX;
	// 滚动y轴
	protected float _component_scrollY;

	private boolean _sortableChildren;

	private final static LayerSorter<LComponent> compSorter = new LayerSorter<LComponent>(false);

	private int childCount = 0;

	private LComponent latestInserted = null;

	public LContainer(int x, int y, int w, int h) {
		super(x, y, w, h);
		this.setFocusable(false);
		this.setSortableChildren(true);
	}

	@Override
	public boolean isContainer() {
		return true;
	}

	public boolean hasChilds() {
		return childCount > 0;
	}

	public LComponent addPadding(LComponent comp, float offX, float offY) {
		return addPadding(comp, offX, offY, 2);
	}

	public LComponent addCol(LComponent comp) {
		return addPadding(comp, 0, 0, 1);
	}

	public LComponent addCol(LComponent comp, float offY) {
		return addPadding(comp, 0, offY, 1);
	}

	public LComponent addRow(LComponent comp) {
		return addPadding(comp, 0, 0, 0);
	}

	public LComponent addRow(LComponent comp, float offX) {
		return addPadding(comp, offX, 0, 0);
	}

	public LComponent addPadding(LComponent comp, float offX, float offY, int code) {
		if (_component_isClose) {
			return comp;
		}
		if (comp == null) {
			return null;
		}
		if (this == comp) {
			return this;
		}

		final String otherName = "ToolTip";

		float maxX = 0;
		float maxY = 0;

		LComponent tag = null;

		if (childCount == 1) {
			LComponent cp = _childs[0];
			if (cp != null && cp.getY() >= _newLineHeight && !otherName.equals(cp.getUIName())) {
				maxX = cp.getX();
				maxY = cp.getY();
				tag = cp;
			}
		} else {
			for (int i = 0; i < childCount; i++) {
				LComponent c = _childs[i];
				if (c != null && c != comp && c.getY() >= _newLineHeight && !otherName.equals(c.getUIName())) {
					float oldMaxX = maxX;
					float oldMaxY = maxY;
					maxX = MathUtils.max(maxX, c.getX());
					maxY = MathUtils.max(maxY, c.getY());
					if (oldMaxX != maxX || oldMaxY != maxY) {
						tag = c;
					}
				}
			}
		}
		if (tag == null && childCount > 0) {
			tag = _childs[childCount - 1];
		}
		if (tag != null && tag != comp && !otherName.equals(tag.getUIName())) {
			switch (code) {
			case 0:
				comp.setLocation(maxX + tag.getWidth() + offX, maxY + offY);
				break;
			case 1:
				comp.setLocation(0 + offX, maxY + tag.getHeight() + offY);
				break;
			default:
				comp.setLocation(maxX + tag.getWidth() + offX, maxY + tag.getHeight() + offY);
				break;
			}
		} else {
			switch (code) {
			case 0:
				comp.setLocation(maxX + offX, maxY + offY);
				break;
			case 1:
				comp.setLocation(0 + offX, maxY + offY);
				break;
			default:
				comp.setLocation(maxX + offX, maxY + offY);
				break;
			}
		}

		add(comp);
		_newLineHeight = comp.getY();
		return comp;
	}

	public LComponent add(LComponent... comps) {
		if (_component_isClose) {
			return null;
		}
		for (int i = 0; i < comps.length; i++) {
			add(comps[i]);
		}
		return this;
	}

	public LComponent add(LComponent comp, Vector2f pos) {
		return add(comp, pos.x(), pos.y());
	}

	public LComponent add(LComponent comp, int x, int y) {
		if (_component_isClose) {
			return null;
		}
		add(comp);
		comp.setLocation(x, y);
		return this;
	}

	public LComponent add(LComponent comp) {
		if (_component_isClose) {
			return null;
		}
		if (this == comp) {
			return this;
		}
		if (comp == null) {
			return this;
		}
		if (this.contains(comp)) {
			return this;
		}
		if (comp.getContainer() != null) {
			comp.setContainer(null);
		}
		comp.setContainer(this);
		comp.setDesktop(this._desktop);
		comp.setState(State.ADDED);
		if (comp.isContainer() && (comp instanceof LScrollContainer)) {
			((LScrollContainer) comp).scrollContainerRealSizeChanged();
		}
		this._childs = CollectionUtils.expand(this._childs, 1, false);
		this._childs[0] = comp;
		this.childCount++;
		if (_desktop != null) {
			this._desktop.setDesktop(comp);
			if (this.input == null) {
				this.input = _desktop.input;
			}
			if (comp.input == null) {
				comp.input = _desktop.input;
			}
		}
		if (_sortableChildren) {
			this.sortComponents();
		}
		this.latestInserted = comp;
		this.setDesktops(this._desktop);
		return this;
	}

	public LComponent add(LComponent comp, int index) {
		if (_component_isClose) {
			return null;
		}
		if (comp.getContainer() != null) {
			throw new LSysException(comp + " already reside in another container!!!");
		}
		comp.setContainer(this);
		comp.setState(State.ADDED);
		if (comp.isContainer() && (comp instanceof LScrollContainer)) {
			((LScrollContainer) comp).scrollContainerRealSizeChanged();
		}
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
		this._desktop.setDesktop(comp);
		if (_sortableChildren) {
			this.sortComponents();
		}
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
		if (_component_isClose) {
			return null;
		}
		TArray<LComponent> list = new TArray<LComponent>();
		final int size = this.childCount;
		for (Object tag : tags) {
			for (int i = size - 1; i > -1; i--) {
				if (this._childs[i].Tag == tag || tag.equals(this._childs[i].Tag)) {
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
		if (_component_isClose) {
			return null;
		}
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
		if (_component_isClose) {
			return null;
		}
		TArray<LComponent> list = new TArray<LComponent>();
		final int size = this.childCount;
		for (String name : names) {
			for (int i = size - 1; i > -1; i--) {
				LComponent comp = this._childs[i];
				if (comp != null && name.equals(comp.getUIName())) {
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
		if (_component_isClose) {
			return null;
		}
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
		if (_component_isClose) {
			return null;
		}
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

	public TArray<LComponent> findNameContains(String... names) {
		if (_component_isClose) {
			return null;
		}
		TArray<LComponent> list = new TArray<LComponent>();
		final int size = this.childCount;
		for (String name : names) {
			for (int i = size - 1; i > -1; i--) {
				LComponent comp = this._childs[i];
				if (comp != null) {
					String childName = comp.getName();
					if (childName != null && childName.contains(name)) {
						list.add(comp);
					}
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
		if (_component_isClose) {
			return null;
		}
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
		if (_component_isClose) {
			return false;
		}
		if (comp == null) {
			return false;
		}
		if (_childs == null) {
			return false;
		}
		for (int i = 0; i < this.childCount; i++) {
			LComponent child = _childs[i];
			boolean exist = (child != null);
			if (exist && comp.equals(child)) {
				return true;
			}
			if (exist && child.isContainer() && (child instanceof LContainer)) {
				LContainer superComp = (LContainer) child;
				for (int j = 0; j < superComp._childs.length; j++) {
					boolean superExist = (superComp._childs[j] != null);
					if (superExist && comp.equals(superComp._childs[j])) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public int remove(LComponent comp) {
		if (_component_isClose) {
			return -1;
		}
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
		if (_component_isClose) {
			return false;
		}
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
		if (_component_isClose) {
			return false;
		}
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
		if (_component_isClose) {
			return false;
		}
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
		if (_component_isClose) {
			return false;
		}
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
		if (_component_isClose) {
			return false;
		}
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
		if (_component_isClose) {
			return false;
		}
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
		if (_component_isClose) {
			return this;
		}
		LComponent comp = this._childs[index];
		if (comp != null) {
			this._desktop.setComponentStat(comp, false);
			comp.setContainer(null);
			comp.setState(State.REMOVED);
			if (comp instanceof ActionBind) {
				removeActionEvents((ActionBind) comp);
			}
			// comp.close();
		}
		this._childs = CollectionUtils.cut(this._childs, index);
		this.childCount--;
		return comp;
	}

	public void clear() {
		if (_component_isClose) {
			return;
		}
		this._desktop.clearComponentsStat(this._childs);
		for (int i = 0; i < this.childCount; i++) {
			LComponent comp = this._childs[i];
			if (comp != null) {
				comp.setContainer(null);
				comp.setState(State.REMOVED);
				if (comp instanceof ActionBind) {
					removeActionEvents((ActionBind) comp);
				}
				// comp.close();
			}
		}
		this._childs = new LComponent[0];
		this.childCount = 0;
	}

	public void replace(LComponent oldComp, LComponent newComp) {
		if (_component_isClose) {
			return;
		}
		int index = this.remove(oldComp);
		this.add(newComp, index);
	}

	@Override
	public void update(long timer) {
		if (this._component_isClose) {
			return;
		}
		if (!this._component_visible) {
			return;
		}
		synchronized (_childs) {
			try {
				super.update(timer);
				LComponent component;
				for (int i = 0; i < this.childCount; i++) {
					component = _childs[i];
					if (component != null && component != this) {
						component.update(timer);
					}
				}
			} catch (Throwable cause) {
				LSystem.error("LContainer update() exception", cause);
			}
		}
	}

	@Override
	public void validatePosition() {
		if (_component_isClose) {
			return;
		}
		super.validatePosition();
		for (int i = 0; i < this.childCount; i++) {
			LComponent comp = this._childs[i];
			if (comp != null) {
				comp.validatePosition();
			}
		}
		if (!this._component_elastic) {
			for (int i = 0; i < this.childCount; i++) {
				LComponent comp = this._childs[i];
				if (comp != null) {
					if (comp.getX() > this.getWidth() || comp.getY() > this.getHeight()
							|| comp.getX() + comp.getWidth() < 0 || comp.getY() + comp.getHeight() < 0) {
						setElastic(true);
						break;
					}
				}
			}
		}
	}

	@Override
	public void createUI(GLEx g) {
		if (_component_isClose) {
			return;
		}
		if (!this.isVisible()) {
			return;
		}
		final float newScrollX = _component_scrollX;
		final float newScrollY = _component_scrollY;

		final float drawWidth = _width;
		final float drawHeight = _height;

		final float startX = MathUtils.scroll(newScrollX, drawWidth);
		final float startY = MathUtils.scroll(newScrollY, drawHeight);

		final boolean update = (startX != 0f || startY != 0f);

		if (update) {
			g.translate(startX, startY);
		}
		synchronized (_childs) {
			super.createUI(g);
			if (this._component_elastic) {
				g.setClip(this.getScreenX(), this.getScreenY(), this.getWidth(), this.getHeight());
			}
			this.renderComponents(g);
			if (this._component_elastic) {
				g.clearClip();
			}
		}

		if (update) {
			g.translate(-startX, -startY);
		}
	}

	protected void renderComponents(GLEx g) {
		if (_component_isClose) {
			return;
		}
		for (int i = this.childCount - 1; i >= 0; i--) {
			LComponent comp = this._childs[i];
			if (comp != null && comp != this) {
				comp.createUI(g);
			}
		}
	}

	public void sendToFront(LComponent comp) {
		if (_component_isClose) {
			return;
		}
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
				if (_sortableChildren) {
					this.sortComponents();
				}
				break;
			}
		}
	}

	public void sendToBack(LComponent comp) {
		if (_component_isClose) {
			return;
		}
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
				if (_sortableChildren) {
					this.sortComponents();
				}
				break;
			}
		}
	}

	public void sortComponents() {
		if (this._component_isClose) {
			return;
		}
		if (this.childCount <= 1) {
			return;
		}
		compSorter.sort(this._childs);
	}

	protected void transferFocus(LComponent component) {
		if (_component_isClose) {
			return;
		}
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
		if (_component_isClose) {
			return;
		}
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

	@Override
	public boolean isSelected() {
		if (_component_isClose) {
			return false;
		}
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
		return this._component_elastic;
	}

	public void setElastic(boolean b) {
		if (getWidth() > 32 || getHeight() > 32) {
			this._component_elastic = b;
		} else {
			this._component_elastic = false;
		}
	}

	public LComponent findComponent(int x1, int y1) {
		if (_component_isClose) {
			return null;
		}
		if (!this.intersects(x1, y1)) {
			return null;
		}
		for (int i = 0; i < this.childCount; i++) {
			LComponent child = this._childs[i];
			if (child != null && child.getSuper() != null && child.getSuper().isContainer()
					&& (child.getSuper() instanceof LScrollContainer)) {
				LScrollContainer scr = (LScrollContainer) child.getSuper();
				int nx = x1 + scr.getScrollX();
				int ny = y1 + scr.getScrollY();
				if (child.intersects(nx, ny)) {
					LComponent comp = (!child.isContainer()) ? child : ((LContainer) child).findComponent(nx, ny);
					LContainer container = comp.getContainer();
					if (container != null && container.isContainer() && (container instanceof LScrollContainer)) {
						if (container.contains(comp) && (comp.getWidth() >= container.getWidth()
								|| comp.getHeight() >= container.getHeight())) {
							return comp.getContainer();
						}
					}
					return comp;
				}
			}
			if (child != null && child.intersects(x1, y1)) {
				LComponent comp = (!child.isContainer()) ? child : ((LContainer) child).findComponent(x1, y1);
				LContainer container = comp.getContainer();
				if (container != null && container.isContainer() && (container instanceof LScrollContainer)) {
					if (container.contains(comp)
							&& (comp.getWidth() >= container.getWidth() || comp.getHeight() >= container.getHeight())) {
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
		if (_component_isClose) {
			return null;
		}
		return CollectionUtils.copyOf(this._childs, this.childCount);
	}

	public LComponent get() {
		return this.latestInserted;
	}

	private RectBox getBox() {
		return setRect(MathUtils.getBounds(0, 0, getWidth() * _scaleX, getHeight() * _scaleY, _objectRotation, _objectRect));
	}

	@Override
	public LayoutPort getLayoutPort() {
		if (_objectSuper == null) {
			return new LayoutPort(getBox(), getRootConstraints());
		} else {
			return new LayoutPort(this, getRootConstraints());
		}
	}

	public LContainer layoutElements(final LayoutManager manager, final LComponent... comps) {
		if (manager != null) {
			TArray<LComponent> list = new TArray<LComponent>();
			for (int i = 0; i < comps.length; i++) {
				LComponent c = comps[i];
				if (c != null && !(c instanceof LToolTip)) {
					list.add(c);
				}
			}
			LComponent[] tmp = new LComponent[list.size];
			for (int i = 0; i < list.size; i++) {
				tmp[i] = list.get(i);
			}
			manager.layoutElements(this, tmp);
		}
		return this;
	}

	public LContainer packLayout(final LayoutManager manager) {
		return packLayout(manager, 0, 0, 0, 0);
	}

	public LContainer packLayout(final LayoutManager manager, final float spacex, final float spacey,
			final float spaceWidth, final float spaceHeight) {
		LComponent[] comps = getComponents();
		CollectionUtils.reverse(comps);
		layoutElements(manager, comps);
		if (spacex != 0 || spacey != 0 || spaceWidth != 0 || spaceHeight != 0) {
			for (int i = 0; i < comps.length; i++) {
				LComponent comp = comps[i];
				if (comp != null && !(comp instanceof LToolTip)) {
					comp.setX(comp.getX() + spacex);
					comp.setY(comp.getY() + spacey);
					comp.setWidth(comp.getWidth() + spaceWidth);
					comp.setHeight(comp.getHeight() + spaceHeight);
				}
			}
		}
		return this;
	}

	void setDesktops(Desktop d) {
		if (_component_isClose) {
			return;
		}
		LComponent[] comps = this._childs;
		if (comps != null) {
			for (int i = 0; i > comps.length; i++) {
				if (comps[i] != null) {
					comps[i].setDesktop(d);
				}
			}
		}
	}

	/**
	 * 删除符合指定条件的组件并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public TArray<LComponent> remove(QueryEvent<LComponent> query) {

		TArray<LComponent> result = new TArray<LComponent>();

		for (int i = _childs.length - 1; i > -1; i--) {
			LComponent comp = _childs[i];
			if (comp != null) {
				if (query.hit(comp)) {
					result.add(comp);
					remove(i);
				}
			}
		}

		return result;
	}

	/**
	 * 查找符合指定条件的组件并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public TArray<LComponent> find(QueryEvent<LComponent> query) {

		TArray<LComponent> result = new TArray<LComponent>();

		for (int i = _childs.length - 1; i > -1; i--) {
			LComponent comp = _childs[i];
			if (comp != null) {
				if (query.hit(comp)) {
					result.add(comp);
				}
			}
		}

		return result;
	}

	/**
	 * 删除指定条件的组件并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public <T extends LComponent> TArray<T> delete(QueryEvent<T> query) {

		TArray<T> result = new TArray<T>();

		for (int i = _childs.length - 1; i > -1; i--) {
			LComponent comp = _childs[i];

			if (comp != null) {
				@SuppressWarnings("unchecked")
				T v = (T) comp;
				if (query.hit(v)) {
					result.add(v);
					remove(i);
				}
			}
		}
		return result;
	}

	/**
	 * 查找符合指定条件的组件并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public <T extends LComponent> TArray<T> select(QueryEvent<T> query) {

		TArray<T> result = new TArray<T>();

		for (int i = _childs.length - 1; i > -1; i--) {
			LComponent comp = _childs[i];

			if (comp != null) {
				@SuppressWarnings("unchecked")
				T v = (T) comp;
				if (query.hit(v)) {
					result.add(v);
				}
			}
		}
		return result;
	}

	@Override
	public LComponent in() {
		if (_component_isClose) {
			return this;
		}
		for (int i = 0; i < _childs.length; i++) {
			LComponent comp = (LComponent) _childs[i];
			if (comp != null) {
				comp.in();
			}
		}
		super.in();
		return this;
	}

	@Override
	public LComponent out() {
		if (_component_isClose) {
			return this;
		}
		for (int i = 0; i < _childs.length; i++) {
			LComponent comp = (LComponent) _childs[i];
			if (comp != null) {
				comp.out();
			}
		}
		super.out();
		return this;
	}

	public int getChildCount() {
		return size();
	}

	@Override
	public int size() {
		return childCount;
	}

	@Override
	public boolean isEmpty() {
		return childCount == 0 || _childs == null;
	}

	@Override
	public void keyPressed(GameKey key) {
		if (_component_isClose) {
			return;
		}
		super.keyPressed(key);
		LComponent[] childs = _childs;
		for (int i = 0; i < childs.length; i++) {
			LComponent c = childs[i];
			if (c != null) {
				c.keyPressed(key);
			}
		}
	}

	@Override
	public void keyReleased(GameKey key) {
		if (_component_isClose) {
			return;
		}
		super.keyReleased(key);
		LComponent[] childs = _childs;
		for (int i = 0; i < childs.length; i++) {
			LComponent c = childs[i];
			if (c != null) {
				c.keyReleased(key);
			}
		}
	}

	void toString(StrBuilder buffer, int indent) {
		buffer.append(super.toString());
		LComponent[] comps = _childs;
		int size = comps.length;
		if (size > 0) {
			buffer.append(LSystem.LS);
			for (int i = 0; i < size; i++) {
				for (int ii = 0; ii < indent; ii++) {
					buffer.append("|  ");
				}
				LComponent c = comps[i];
				if (c != null && c.isContainer() && (c instanceof LContainer)) {
					((LContainer) c).toString(buffer, indent + 1);
				} else {
					buffer.append(c);
					buffer.append(LSystem.LS);
				}
			}
		}
	}

	@Override
	protected void processResize() {
		if (_component_isClose) {
			return;
		}
		if (_childs != null && childCount > 0) {
			for (int i = this.childCount - 1; i >= 0; i--) {
				LComponent comp = _childs[i];
				if (comp != null && comp != this && comp.getParent() == this) {
					comp.processResize();
				}
			}
		}
	}

	@Override
	protected LContainer validateResize() {
		if (_component_isClose) {
			return this;
		}
		super.validateResize();
		if (_childs != null && childCount > 0) {
			for (int i = this.childCount - 1; i >= 0; i--) {
				LComponent comp = _childs[i];
				if (comp != null && comp != this && comp.getParent() == this) {
					comp.validateResize();
				}
			}
		}
		return this;
	}

	public LContainer scrollBy(float x, float y) {
		this._component_scrollX += x;
		this._component_scrollY += y;
		return this;
	}

	public LContainer scrollTo(float x, float y) {
		this._component_scrollX = x;
		this._component_scrollY = y;
		return this;
	}

	public float scrollX() {
		return this._component_scrollX;
	}

	public float scrollY() {
		return this._component_scrollY;
	}

	public LContainer scrollX(float x) {
		this._component_scrollX = x;
		return this;
	}

	public LContainer scrollY(float y) {
		this._component_scrollY = y;
		return this;
	}

	public UIControls createUIControls() {
		UIControls controls = null;
		if (_childs != null && childCount > 0) {
			controls = new UIControls(_childs);
		} else {
			controls = new UIControls();
		}
		return controls;
	}

	@Override
	public void setRotation(float rotate) {
		super.setRotation(rotate);
		if (_childs != null) {
			for (int i = _childs.length - 1; i > -1; i--) {
				LComponent comp = _childs[i];
				if (comp != null) {
					comp.setRotation(rotate);
				}
			}
		}
	}

	public UIControls controls() {
		return createUIControls();
	}

	public Margin margin(boolean vertical, float left, float top, float right, float bottom) {
		float size = vertical ? getHeight() : getWidth();
		if (_component_isClose) {
			return new Margin(size, vertical);
		}
		if (_margin == null) {
			_margin = new Margin(size, vertical);
		} else {
			_margin.setSize(size);
			_margin.setVertical(vertical);
		}
		_margin.setMargin(left, top, right, bottom);
		_margin.clear();
		for (int i = childCount - 1; i > -1; --i) {
			LComponent comp = _childs[i];
			if (comp != null) {
				_margin.addChild(comp);
			}
		}
		return _margin;
	}

	/**
	 * 遍历LContainer中所有组件对象并反馈给Callback
	 * 
	 * @param callback
	 */
	public LContainer forChildren(Callback<LComponent> callback) {
		if (callback == null) {
			return this;
		}
		for (LComponent child : this._childs) {
			if (child != null) {
				callback.onSuccess(child);
			}
		}
		return this;
	}

	public float getStageX() {
		return (getX() - getScreenX()) / getScaleX();
	}

	public float getStageY() {
		return (getX() - getScreenX()) / getScaleY();
	}

	public LContainer setSortableChildren(boolean v) {
		this._sortableChildren = v;
		return this;
	}

	public boolean isSortableChildren() {
		return this._sortableChildren;
	}

	@Override
	public String toString() {
		StrBuilder buffer = new StrBuilder(128);
		toString(buffer, 1);
		buffer.setLength(buffer.length() - 1);
		return buffer.toString();
	}

	@Override
	public void close() {
		super.close();
		if (_component_autoDestroy) {
			if (_childs != null) {
				for (LComponent c : _childs) {
					if (c != null && !c._component_isClose) {
						c.close();
						c = null;
					}
				}
			}
			_childs = null;
		}
		this._component_isClose = true;
		this._newLineHeight = 0;
	}

}
