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
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.reply.Callback;

/**
 * UI组件的上级类,作为容器可以把多个LComponent注入一个LContainer
 */
public abstract class LContainer extends LComponent implements IArray {

	private final static LayerSorter<LComponent> componentSorter = new LayerSorter<LComponent>(false);

	protected LComponent[] _childs = new LComponent[0];

	protected boolean _scrolling = false;

	private boolean _sortableChildren = false;
	// 滚动x轴
	protected float _component_scrollX;
	// 滚动y轴
	protected float _component_scrollY;

	private float _newLineHeight = -1f;

	private int _childCount = 0;

	private int _childExpandCount = 1;

	private Margin _margin = null;

	private LComponent latestInserted = null;

	public LContainer(int x, int y, int w, int h) {
		super(x, y, w, h);
		this.setFocusable(false);
		this.setSortableChildren(true);
		this._childCount = 0;
		this._childExpandCount = 1;
		this._newLineHeight = -1f;
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
		if (_destroyed) {
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
		final LComponent[] childs = this._childs;
		final int size = this._childCount;

		if (size == 1) {
			LComponent cp = childs[0];
			if (cp != null && cp.getY() >= _newLineHeight && !otherName.equals(cp.getUIName())) {
				maxX = cp.getX();
				maxY = cp.getY();
				tag = cp;
			}
		} else {
			for (int i = 0; i < size; i++) {
				LComponent c = childs[i];
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
		if (tag == null && size > 0) {
			tag = childs[size - 1];
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
		if (_destroyed) {
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
		if (_destroyed) {
			return null;
		}
		add(comp);
		comp.setLocation(x, y);
		return this;
	}

	public LComponent add(LComponent comp) {
		if (_destroyed) {
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
		comp.onAttached();
		if (comp.isContainer() && (comp instanceof LScrollContainer)) {
			((LScrollContainer) comp).scrollContainerRealSizeChanged();
		}
		this._childs = CollectionUtils.expand(this._childs, _childExpandCount, false);
		this._childs[0] = comp;
		this._childCount++;
		if (_desktop != null) {
			this._desktop.setDesktop(comp);
			if (this._input == null) {
				this._input = _desktop._sysInput;
			}
			if (comp._input == null) {
				comp._input = _desktop._sysInput;
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
		if (_destroyed) {
			return null;
		}
		if (comp.getContainer() != null) {
			throw new LSysException(comp + " already reside in another container!!!");
		}
		comp.setContainer(this);
		comp.setState(State.ADDED);
		comp.onAttached();
		if (comp.isContainer() && (comp instanceof LScrollContainer)) {
			((LScrollContainer) comp).scrollContainerRealSizeChanged();
		}
		final LComponent[] newChilds = new LComponent[this._childs.length + 1];
		final LComponent[] oldChilds = this._childs;
		this._childCount++;
		int ctr = 0;
		for (int i = 0; i < this._childCount; i++) {
			if (i != index) {
				newChilds[i] = oldChilds[ctr];
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
		if (_destroyed) {
			return null;
		}
		TArray<LComponent> list = new TArray<LComponent>();
		final int size = this._childCount;
		final int len = tags.length;
		final LComponent[] childs = this._childs;
		for (int j = 0; j < len; j++) {
			final Object tag = tags[j];
			if (tag != null) {
				for (int i = size - 1; i > -1; i--) {
					LComponent child = childs[i];
					if (child != null && (tag == child.Tag || tag.equals(child.Tag))) {
						list.add(child);
					}
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
		if (_destroyed) {
			return null;
		}
		final TArray<LComponent> list = new TArray<LComponent>();
		final int size = this._childCount;
		final int len = tags.length;
		final LComponent[] childs = this._childs;
		for (int j = 0; j < len; j++) {
			final Object tag = tags[j];
			if (tag != null) {
				for (int i = size - 1; i > -1; i--) {
					LComponent child = childs[i];
					if (child != null && !tag.equals(child.Tag)) {
						list.add(child);
					}
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
		if (_destroyed) {
			return null;
		}
		TArray<LComponent> list = new TArray<LComponent>();
		final int size = this._childCount;
		final int len = names.length;
		final LComponent[] childs = this._childs;
		for (int j = 0; j < len; j++) {
			final String name = names[j];
			for (int i = size - 1; i > -1; i--) {
				LComponent comp = childs[i];
				if (comp != null && name.equals(comp.getUIName())) {
					list.add(comp);
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
		if (_destroyed) {
			return null;
		}
		TArray<LComponent> list = new TArray<LComponent>();
		final int size = this._childCount;
		final int len = names.length;
		final LComponent[] childs = this._childs;
		for (int j = 0; j < len; j++) {
			final String name = names[j];
			if (name != null) {
				for (int i = size - 1; i > -1; i--) {
					final LComponent child = childs[i];
					if (child != null && !name.equals(child.getUIName())) {
						list.add(child);
					}
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
		if (_destroyed) {
			return null;
		}
		TArray<LComponent> list = new TArray<LComponent>();
		final int size = this._childCount;
		final int len = names.length;
		final LComponent[] childs = this._childs;
		for (int j = 0; j < len; j++) {
			final String name = names[j];
			if (name != null) {
				for (int i = size - 1; i > -1; i--) {
					LComponent child = childs[i];
					if (child != null && name.equals(child.getName())) {
						list.add(child);
					}
				}
			}
		}
		return list;
	}

	public TArray<LComponent> findNameContains(String... names) {
		if (_destroyed) {
			return null;
		}
		TArray<LComponent> list = new TArray<LComponent>();
		final int size = this._childCount;
		final int len = names.length;
		final LComponent[] childs = this._childs;
		for (int j = 0; j < len; j++) {
			final String name = names[j];
			if (name != null) {
				for (int i = size - 1; i > -1; i--) {
					LComponent comp = childs[i];
					if (comp != null && comp.getName() != null && comp.getName().contains(name)) {
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
		if (_destroyed) {
			return null;
		}
		TArray<LComponent> list = new TArray<LComponent>();
		final int size = this._childCount;
		final int len = names.length;
		final LComponent[] childs = this._childs;
		for (int j = 0; j < len; j++) {
			final String name = names[j];
			if (name != null) {
				for (int i = size - 1; i > -1; i--) {
					final LComponent child = childs[i];
					if (!name.equals(child.getName())) {
						list.add(child);
					}
				}
			}
		}
		return list;
	}

	/**
	 * 检查指定组件是否存在
	 * 
	 * @param comp
	 * @return
	 */
	public boolean contains(LComponent comp) {
		if (_destroyed) {
			return false;
		}
		if (comp == null) {
			return false;
		}
		if (_childs == null) {
			return false;
		}
		final int len = this._childCount;
		final LComponent[] childs = _childs;
		for (int i = 0; i < len; i++) {
			LComponent child = childs[i];
			boolean exist = (child != null);
			if (exist && comp.equals(child)) {
				return true;
			}
			if (exist && child.isContainer() && (child instanceof LContainer)) {
				final LContainer superComp = (LContainer) child;
				final LComponent[] superChilds = superComp._childs;
				for (int j = 0; j < superChilds.length; j++) {
					boolean superExist = (superChilds[j] != null);
					if (superExist && comp.equals(superChilds[j])) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 查找符合的Flag整型对象
	 * 
	 * @param flag
	 * @return
	 */
	public TArray<LComponent> findFlagTypes(int flag) {
		if (_destroyed) {
			return null;
		}
		final TArray<LComponent> list = new TArray<LComponent>();
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = size - 1; i > -1; i--) {
			LComponent child = childs[i];
			if (child != null && child.getFlagType() == flag) {
				list.add(child);
			}
		}
		return list;
	}

	/**
	 * 查找符合的Flag字符对象
	 * 
	 * @param flag
	 * @return
	 */
	public TArray<LComponent> findFlagObjects(String flag) {
		if (_destroyed) {
			return null;
		}
		final TArray<LComponent> list = new TArray<LComponent>();
		if (flag == null) {
			return list;
		}
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = size - 1; i > -1; i--) {
			LComponent child = childs[i];
			if (child != null && flag.equals(child.getObjectFlag())) {
				list.add(child);
			}
		}
		return list;
	}

	public LContainer enableds() {
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = size - 1; i > -1; i--) {
			LComponent child = childs[i];
			if (child != null) {
				child.setEnabled(true);
			}
		}
		this.setEnabled(true);
		return this;
	}

	public LContainer disables() {
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = size - 1; i > -1; i--) {
			LComponent child = childs[i];
			if (child != null) {
				child.setEnabled(false);
			}
		}
		this.setEnabled(false);
		return this;
	}

	public LComponent getRandomComponent() {
		return getRandomComponent(0, _childCount);
	}

	public LComponent getRandomComponent(int min, int max) {
		if (_destroyed) {
			return null;
		}
		min = MathUtils.max(0, min);
		max = MathUtils.min(max, _childCount);
		return _childs[MathUtils.nextInt(min, max)];
	}

	public int remove(LComponent comp) {
		if (_destroyed) {
			return -1;
		}
		final int size = this._childCount;
		for (int i = size - 1; i > -1; i--) {
			if (this._childs[i] == comp) {
				this.remove(i);
				return i;
			}
		}
		return -1;
	}

	/**
	 * 删除alpha大于或小于指定范围的组件
	 * 
	 * @param comp
	 * @param more
	 * @param limit
	 * @return
	 */
	public boolean removeWhenAlpha(LComponent comp, boolean more, float limit) {
		if (comp != null && (more ? comp.getAlpha() >= limit : comp.getAlpha() <= limit)) {
			return remove(comp) > 0;
		}
		return false;
	}

	public boolean removeWhen(QueryEvent<LComponent> query) {
		if (_destroyed) {
			return false;
		}
		if (query == null) {
			return false;
		}
		boolean flag = false;
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = size - 1; i > -1; i--) {
			LComponent child = childs[i];
			if (query.hit(child)) {
				this.remove(i);
				flag = true;
			}
		}
		return flag;
	}

	public boolean removeTag(Object tag) {
		if (_destroyed) {
			return false;
		}
		boolean flag = false;
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = size - 1; i > -1; i--) {
			LComponent child = childs[i];
			if (child != null && (child.Tag == tag || tag.equals(child.Tag))) {
				this.remove(i);
				flag = true;
			}
		}
		return flag;
	}

	public boolean removeNotTag(Object tag) {
		if (_destroyed) {
			return false;
		}
		if (tag == null) {
			return false;
		}
		boolean flag = false;
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = size - 1; i > -1; i--) {
			final LComponent child = childs[i];
			if (child != null && !tag.equals(child.Tag)) {
				this.remove(i);
				flag = true;
			}
		}
		return flag;
	}

	public boolean removeUIName(String name) {
		if (_destroyed) {
			return false;
		}
		if (name == null) {
			return false;
		}
		boolean flag = false;
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = size - 1; i > -1; i--) {
			final LComponent child = childs[i];
			if (child != null && name.equals(child.getUIName())) {
				this.remove(i);
				flag = true;
			}
		}
		return flag;
	}

	public boolean removeNotUIName(String name) {
		if (_destroyed) {
			return false;
		}
		if (name == null) {
			return false;
		}
		boolean flag = false;
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = size - 1; i > -1; i--) {
			final LComponent child = childs[i];
			if (child != null && !name.equals(child.getUIName())) {
				this.remove(i);
				flag = true;
			}
		}
		return flag;
	}

	public boolean removeName(String name) {
		if (_destroyed) {
			return false;
		}
		if (name == null) {
			return false;
		}
		boolean flag = false;
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = size - 1; i > -1; i--) {
			final LComponent child = childs[i];
			if (child != null && name.equals(child.getName())) {
				this.remove(i);
				flag = true;
			}
		}
		return flag;
	}

	public boolean removeNotName(String name) {
		if (_destroyed) {
			return false;
		}
		if (name == null) {
			return false;
		}
		boolean flag = false;
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = size - 1; i > -1; i--) {
			final LComponent child = childs[i];
			if (child != null && !name.equals(child.getName())) {
				this.remove(i);
				flag = true;
			}
		}
		return flag;
	}

	public LComponent remove(int index) {
		if (_destroyed) {
			return this;
		}
		LComponent comp = this._childs[index];
		if (comp != null) {
			this._desktop.setComponentStat(comp, false);
			comp.setContainer(null);
			comp.setState(State.REMOVED);
			comp.onDetached();
			if (comp instanceof ActionBind) {
				removeActionEvents((ActionBind) comp);
			}
			// comp.close();
		}
		this._childs = CollectionUtils.cut(this._childs, index);
		this._childCount--;
		return comp;
	}

	/**
	 * 交换两个组件位置
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public LContainer swap(int first, int second) {
		if (_destroyed) {
			return this;
		}
		if (first == second) {
			return this;
		}
		if (first >= _childCount) {
			throw new LSysException("first can't be >= size: " + first + " >= " + _childCount);
		}
		if (second >= _childCount) {
			throw new LSysException("second can't be >= size: " + second + " >= " + _childCount);
		}
		final LComponent[] cs = this._childs;
		final LComponent firstValue = cs[first];
		cs[first] = cs[second];
		cs[second] = firstValue;
		return this;
	}

	/**
	 * 交换两个组件位置
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public LContainer swap(LComponent first, LComponent second) {
		if (_destroyed) {
			return this;
		}
		if ((first == null && second == null) || (first == second)) {
			return this;
		}
		int fi = -1;
		int bi = -1;
		final int size = this._childCount;
		final LComponent[] cs = this._childs;
		for (int i = 0; i < size; i++) {
			final LComponent comp = cs[i];
			if (comp == first) {
				fi = i;
			}
			if (comp == second) {
				bi = i;
			}
			if (fi != -1 && bi != -1) {
				break;
			}
		}
		if (fi != -1 && bi != -1) {
			return swap(fi, bi);
		}
		return this;
	}

	/**
	 * 获得一个指定名称的UI组件
	 * 
	 * @param name
	 * @return
	 */
	public LComponent findComponentUI(String name) {
		final LComponent[] childs = this._childs;
		final int size = this._childCount;
		for (int i = size - 1; i > -1; i--) {
			LComponent child = childs[i];
			if (child != null && child.getUIName().equals(name)) {
				return child;
			}
		}
		return null;
	}

	public LComponent unparent() {
		if (_destroyed) {
			return this;
		}
		LContainer c = this.getSuper();
		if (c != null) {
			c.remove(this);
			this.setSuper(null);
		}
		return this;
	}

	@Override
	public void clear() {
		if (_destroyed) {
			return;
		}
		this._desktop.clearComponentsStat(this._childs);
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = 0; i < size; i++) {
			LComponent comp = childs[i];
			if (comp != null) {
				comp.setContainer(null);
				comp.setState(State.REMOVED);
				comp.onDetached();
				if (comp instanceof ActionBind) {
					removeActionEvents((ActionBind) comp);
				}
				// comp.close();
			}
		}
		this._childs = null;
		this._childs = new LComponent[0];
		this._childCount = 0;
	}

	public LComponent replace(LComponent oldComp, LComponent newComp) {
		if (_destroyed) {
			return this;
		}
		int index = this.remove(oldComp);
		this.add(newComp, index);
		return this;
	}

	@Override
	public void update(long elapsedTime) {
		if (this._destroyed) {
			return;
		}
		if (!this._component_visible) {
			return;
		}
		synchronized (_childs) {
			try {
				super.update(elapsedTime);
				LComponent component;
				final int size = this._childCount;
				final LComponent[] childs = this._childs;
				for (int i = 0; i < size; i++) {
					component = childs[i];
					if (component != null && component != this) {
						component.update(elapsedTime);
					}
				}
			} catch (Throwable cause) {
				LSystem.error("LContainer update() exception", cause);
			}
		}
	}

	@Override
	public void validatePosition() {
		if (_destroyed) {
			return;
		}
		super.validatePosition();
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = 0; i < size; i++) {
			LComponent comp = childs[i];
			if (comp != null) {
				comp.validatePosition();
			}
		}
		if (!this._component_elastic) {
			for (int i = 0; i < size; i++) {
				LComponent comp = childs[i];
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
		if (_destroyed) {
			return;
		}
		if (!this._component_visible) {
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
				g.setClip(this.getScreenComponentLeft(), this.getScreenComponentTop(), this.getWidth(),
						this.getHeight());
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
		if (_destroyed) {
			return;
		}
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = size - 1; i >= 0; i--) {
			LComponent comp = childs[i];
			if (comp != null && comp != this) {
				comp.createUI(g);
			}
		}
	}

	@Override
	protected void fireShowComponent() {
		if (_destroyed) {
			return;
		}
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = size - 1; i >= 0; i--) {
			LComponent comp = childs[i];
			if (comp != null && comp != this) {
				comp.fireShowComponent();
			}
		}
	}

	@Override
	protected void fireHideComponent() {
		if (_destroyed) {
			return;
		}
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = size - 1; i >= 0; i--) {
			LComponent comp = childs[i];
			if (comp != null && comp != this) {
				comp.fireHideComponent();
			}
		}
	}

	public LComponent sendToFront(LComponent comp) {
		if (_destroyed) {
			return this;
		}
		if (this._childCount <= 1 || this._childs[0] == comp) {
			return this;
		}
		if (_childs[0] == comp) {
			return this;
		}
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = 0; i < size; i++) {
			if (childs[i] == comp) {
				this._childs = CollectionUtils.cut(childs, i);
				this._childs = CollectionUtils.expand(childs, _childExpandCount, false);
				this._childs[0] = comp;
				if (_sortableChildren) {
					this.sortComponents();
				}
				break;
			}
		}
		return this;
	}

	public LComponent sendToBack(LComponent comp) {
		if (_destroyed) {
			return this;
		}
		if (this._childCount <= 1 || this._childs[this._childCount - 1] == comp) {
			return this;
		}
		if (_childs[this._childCount - 1] == comp) {
			return this;
		}
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = 0; i < size; i++) {
			if (childs[i] == comp) {
				this._childs = CollectionUtils.cut(childs, i);
				this._childs = CollectionUtils.expand(childs, _childExpandCount, true);
				this._childs[size - 1] = comp;
				if (_sortableChildren) {
					this.sortComponents();
				}
				break;
			}
		}
		return this;
	}

	public LComponent sortComponents() {
		if (this._destroyed) {
			return this;
		}
		if (this._childCount <= 1) {
			return this;
		}
		componentSorter.sort(this._childs);
		return this;
	}

	protected void transferFocus(LComponent component) {
		if (_destroyed) {
			return;
		}
		if (component == null) {
			return;
		}
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = 0; i < size; i++) {
			final LComponent child = childs[i];
			if (component == child) {
				int j = i;
				do {
					if (--i < 0) {
						i = size - 1;
					}
					if (i == j) {
						return;
					}
				} while (!child.requestFocus());

				break;
			}
		}
	}

	protected void transferFocusBackward(LComponent component) {
		if (_destroyed) {
			return;
		}
		if (component == null) {
			return;
		}
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = 0; i < size; i++) {
			final LComponent child = childs[i];
			if (component == child) {
				int j = i;
				do {
					if (++i >= size) {
						i = 0;
					}
					if (i == j) {
						return;
					}
				} while (!child.requestFocus());
				break;
			}
		}
	}

	@Override
	public boolean isSelected() {
		if (_destroyed) {
			return false;
		}
		if (!super.isSelected()) {
			final int size = this._childCount;
			final LComponent[] childs = this._childs;
			for (int i = 0; i < size; i++) {
				final LComponent child = childs[i];
				if (child != null && child.isSelected()) {
					return true;
				}
			}
			return false;

		} else {
			return true;
		}
	}

	protected LComponent findComponentChecked(LComponent comp) {
		return comp;
	}

	public LComponent findComponent(int x1, int y1) {
		if (_destroyed) {
			return null;
		}
		if (!this.intersects(x1, y1)) {
			return null;
		}
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = 0; i < size; i++) {
			LComponent child = childs[i];
			if (child != null && child.getSuper() != null && child.getSuper().isContainer()
					&& (child.getSuper() instanceof LScrollContainer)) {
				LScrollContainer scr = (LScrollContainer) child.getSuper();
				final int nx = MathUtils.floor(x1 + scr.getBoxScrollX());
				final int ny = MathUtils.floor(y1 + scr.getBoxScrollY());
				if (child.intersects(nx, ny)) {
					LComponent comp = (!child.isContainer()) ? child : ((LContainer) child).findComponent(nx, ny);
					if (comp != null) {
						return checkComponent(comp, nx, ny);
					}
				}
			}
			if (child != null && child.intersects(x1, y1)) {
				LComponent comp = (!child.isContainer()) ? child : ((LContainer) child).findComponent(x1, y1);
				if (comp != null) {
					return checkComponent(comp, x1, y1);
				}
			}
		}
		return findComponentChecked(this);
	}

	LComponent checkComponent(LComponent comp, float x, float y) {
		LContainer container = comp.getContainer();
		if (container != null && container.isContainer()) {
			if (container instanceof LScrollContainer) {
				LScrollContainer scroll = (LScrollContainer) container;
				if (scroll.isClickSlider(x, y)) {
					return findComponentChecked(scroll);
				}
			}
		}
		return findComponentChecked(comp);
	}

	public int getComponentCount() {
		return this._childCount;
	}

	public LComponent[] getComponents() {
		if (_destroyed) {
			return null;
		}
		return CollectionUtils.copyOf(this._childs, this._childCount);
	}

	public float getContextWidth() {
		float max = 0f;
		final int size = this._childCount;
		for (int i = size - 1; i > -1; i--) {
			LComponent comp = _childs[i];
			if (comp != null && comp._component_visible) {
				max = MathUtils.max(comp.getScalePixelX() + comp.getWidth(), max);
			}
		}
		return max;
	}

	public float getContextHeight() {
		float max = 0f;
		final int size = this._childCount;
		for (int i = size - 1; i > -1; i--) {
			LComponent comp = _childs[i];
			if (comp != null && comp._component_visible) {
				max = MathUtils.max(comp.getScalePixelY() + comp.getHeight(), max);
			}
		}
		return max;
	}

	public LComponent get() {
		return this.latestInserted;
	}

	private RectBox getBox() {
		return setRect(
				MathUtils.getBounds(0, 0, getWidth() * _scaleX, getHeight() * _scaleY, _objectRotation, _objectRect));
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
		if (_destroyed) {
			return this;
		}
		if (manager == null) {
			return this;
		}
		final TArray<LComponent> list = new TArray<LComponent>();
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
		return this;
	}

	public LContainer packLayout(final LayoutManager manager) {
		return packLayout(manager, getComponents(), 0, 0, 0, 0, true);
	}

	public LContainer packLayout(final LayoutManager manager, final LComponent[] comps) {
		return packLayout(manager, comps, 0, 0, 0, 0, false);
	}

	public LContainer packLayout(final LayoutManager manager, final float spacex, final float spacey,
			final float spaceWidth, final float spaceHeight) {
		return packLayout(manager, getComponents(), spacex, spacey, spaceWidth, spaceHeight, true);
	}

	public LContainer packLayout(final LayoutManager manager, final TArray<LComponent> comps, final float spacex,
			final float spacey, final float spaceWidth, final float spaceHeight) {
		if (comps == null) {
			return this;
		}
		final int len = comps.size;
		LComponent[] list = new LComponent[len];
		for (int i = 0; i < len; i++) {
			list[i] = comps.get(i);
		}
		return packLayout(manager, list, spacex, spacey, spaceWidth, spaceHeight, false);
	}

	public LContainer packLayout(final LayoutManager manager, final LComponent[] comps, final float spacex,
			final float spacey, final float spaceWidth, final float spaceHeight) {
		return packLayout(manager, comps, spacex, spacey, spaceWidth, spaceHeight, false);
	}

	public LContainer packLayout(final LayoutManager manager, final LComponent[] comps, final float spacex,
			final float spacey, final float spaceWidth, final float spaceHeight, final boolean reversed) {
		if (_destroyed) {
			return this;
		}
		if (manager == null) {
			return this;
		}
		if (comps == null) {
			return this;
		}
		final RectBox maxSize = new RectBox();
		final LComponent[] oldComps = getComponents();
		if (oldComps != null) {
			boolean eq = (comps.length == oldComps.length);
			int count = 0;
			for (int i = 0; i < oldComps.length; i++) {
				LComponent srcComp = oldComps[i];
				if (eq) {
					LComponent dstComp = comps[i];
					if ((srcComp == dstComp) || (srcComp.equals(dstComp))) {
						count++;
					}
				}
				if (srcComp != null && !(srcComp instanceof LToolTip)) {
					maxSize.union(srcComp.getRectBox());
				}
			}
			if (eq && count == comps.length) {
				maxSize.setEmpty();
			}
		}
		if (reversed) {
			CollectionUtils.reverse(comps);
		}
		layoutElements(manager, comps);
		for (int i = 0; i < comps.length; i++) {
			LComponent comp = comps[i];
			if (comp != null && !(comp instanceof LToolTip)) {
				comp.setX(comp.getX() + spacex);
				comp.setY(comp.getY() + spacey + maxSize.getMaxY());
				comp.setWidth(comp.getWidth() + spaceWidth);
				comp.setHeight(comp.getHeight() + spaceHeight);
			}
		}
		return this;
	}

	void setDesktops(Desktop d) {
		if (_destroyed) {
			return;
		}
		final LComponent[] comps = this._childs;
		if (comps != null) {
			final int size = this._childCount;
			for (int i = 0; i > size; i++) {
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
		if (_destroyed) {
			return null;
		}
		if (query == null) {
			return null;
		}
		final TArray<LComponent> result = new TArray<LComponent>();
		final LComponent[] childs = this._childs;
		final int size = this._childCount;
		for (int i = size - 1; i > -1; i--) {
			LComponent comp = childs[i];
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
		if (_destroyed) {
			return null;
		}
		if (query == null) {
			return null;
		}
		final TArray<LComponent> result = new TArray<LComponent>();
		final LComponent[] childs = this._childs;
		final int size = this._childCount;
		for (int i = size - 1; i > -1; i--) {
			LComponent comp = childs[i];
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
	public TArray<LComponent> delete(QueryEvent<LComponent> query) {
		if (_destroyed) {
			return null;
		}
		if (query == null) {
			return null;
		}
		final TArray<LComponent> result = new TArray<LComponent>();
		final LComponent[] childs = this._childs;
		final int size = this._childCount;
		for (int i = size - 1; i > -1; i--) {
			LComponent comp = childs[i];
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
	public TArray<LComponent> select(QueryEvent<LComponent> query) {
		if (_destroyed) {
			return null;
		}
		if (query == null) {
			return null;
		}
		final TArray<LComponent> result = new TArray<LComponent>();
		final LComponent[] childs = this._childs;
		final int size = this._childCount;
		for (int i = size - 1; i > -1; i--) {
			LComponent comp = childs[i];
			if (comp != null) {
				if (query.hit(comp)) {
					result.add(comp);
				}
			}
		}
		return result;
	}

	@Override
	public LComponent in() {
		if (_destroyed) {
			return this;
		}
		final LComponent[] comps = this._childs;
		final int size = this._childCount;
		for (int i = 0; i < size; i++) {
			LComponent comp = comps[i];
			if (comp != null) {
				comp.in();
			}
		}
		super.in();
		return this;
	}

	@Override
	public LComponent out() {
		if (_destroyed) {
			return this;
		}
		final LComponent[] comps = this._childs;
		final int size = this._childCount;
		for (int i = 0; i < size; i++) {
			LComponent comp = comps[i];
			if (comp != null) {
				comp.out();
			}
		}
		super.out();
		return this;
	}

	@Override
	public boolean isContainer() {
		return true;
	}

	public boolean hasChilds() {
		return _childCount > 0;
	}

	public boolean hasChild(LComponent comp) {
		final LComponent[] comps = this._childs;
		final int size = this._childCount;
		for (int i = size - 1; i > -1; i--) {
			LComponent o = comps[i];
			if (o == comp || (o != null && o.equals(comp))) {
				return true;
			}
		}
		return false;
	}

	public LContainer setChildIndex(int index, LComponent comp) {
		if (comp == null) {
			return this;
		}
		final LComponent[] comps = this._childs;
		final int size = this._childCount;
		if (comp.getSuper() == this || index < 0 || index >= size) {
			return this;
		}
		comps[index] = comp;
		return this;
	}

	public LComponent getChildByIndex(int idx) {
		if (this._childs == null || (idx < 0 || idx > this._childCount - 1)) {
			return null;
		}
		return this._childs[idx];
	}

	public int getChildIndex(LComponent comp) {
		if (comp == null) {
			return -1;
		}
		final LComponent[] comps = this._childs;
		final int size = this._childCount;
		for (int i = size - 1; i > -1; i--) {
			LComponent o = comps[i];
			if (o == comp || (o != null && o.equals(comp))) {
				return i;
			}
		}
		return -1;
	}

	public int getChildCount() {
		return size();
	}

	public LComponent get(int index) {
		if (index < 0 || index >= _childCount) {
			return null;
		}
		return this._childs[index];
	}

	public int get(LComponent comp) {
		if (_destroyed) {
			return -1;
		}
		if (comp == null) {
			return -1;
		}
		int idx = -1;
		if (_childs != null) {
			final int size = this._childCount;
			final LComponent[] childs = this._childs;
			for (int i = 0; i < size; i++) {
				LComponent child = childs[i];
				if (child == comp) {
					idx = i;
					return idx;
				}
			}
		}
		return idx;
	}

	public TArray<LComponent> get(String name) {
		if (_destroyed) {
			return null;
		}
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		final TArray<LComponent> list = new TArray<LComponent>();
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = size - 1; i > -1; i--) {
			LComponent comp = childs[i];
			if (comp != null && name.equals(comp.getName())) {
				list.add(comp);
			}
		}
		return list;
	}

	public int getMaxX() {
		int maxX = 0;
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = size - 1; i > -1; i--) {
			LComponent comp = childs[i];
			if (comp != null) {
				int curX = comp.x();
				if (curX > maxX) {
					maxX = curX;
				}
			}
		}
		return maxX;
	}

	public int getMaxY() {
		int maxY = 0;
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = size - 1; i > -1; i--) {
			LComponent comp = childs[i];
			if (comp != null) {
				int curY = comp.y();
				if (curY > maxY) {
					maxY = curY;
				}
			}
		}
		return maxY;
	}

	public int getMaxZ() {
		int maxZ = 0;
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = size - 1; i > -1; i--) {
			LComponent comp = childs[i];
			if (comp != null) {
				int curZ = comp.getZ();
				if (curZ > maxZ) {
					maxZ = curZ;
				}
			}
		}
		return maxZ;
	}

	@Override
	public int size() {
		return _childCount;
	}

	@Override
	public boolean isEmpty() {
		return _childCount == 0 || _childs == null;
	}

	@Override
	public boolean isNotEmpty() {
		return !isEmpty();
	}

	@Override
	public void keyPressed(GameKey key) {
		if (_destroyed) {
			return;
		}
		super.keyPressed(key);
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = 0; i < size; i++) {
			LComponent c = childs[i];
			if (c != null) {
				c.keyPressed(key);
			}
		}
	}

	@Override
	public void keyReleased(GameKey key) {
		if (_destroyed) {
			return;
		}
		super.keyReleased(key);
		final int size = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = 0; i < size; i++) {
			LComponent c = childs[i];
			if (c != null) {
				c.keyReleased(key);
			}
		}
	}

	void toString(StrBuilder buffer, int indent) {
		buffer.append(super.toString());
		final LComponent[] comps = _childs;
		final int size = this._childCount;
		if (size > 0) {
			buffer.append(LSystem.LS);
			for (int i = 0; i < size; i++) {
				for (int ii = 0; ii < indent; ii++) {
					buffer.append(LSystem.VERTICALLINE);
					buffer.append(LSystem.SPACE);
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
		if (_destroyed) {
			return;
		}
		if (_childs != null && _childCount > 0) {
			final int size = this._childCount;
			final LComponent[] childs = this._childs;
			for (int i = size - 1; i >= 0; i--) {
				LComponent comp = childs[i];
				if (comp != null && comp != this && comp.getParent() == this) {
					comp.processResize();
				}
			}
		}
	}

	@Override
	protected LContainer validateResize() {
		if (_destroyed) {
			return this;
		}
		super.validateResize();
		if (_childs != null && _childCount > 0) {
			final int size = this._childCount;
			final LComponent[] childs = this._childs;
			for (int i = size - 1; i >= 0; i--) {
				LComponent comp = childs[i];
				if (comp != null && comp != this && comp.getParent() == this) {
					comp.validateResize();
				}
			}
		}
		return this;
	}

	@Override
	public void setRotation(float rotate) {
		super.setRotation(rotate);
		if (_childs != null && _childCount > 0) {
			final int size = this._childCount;
			final LComponent[] childs = this._childs;
			for (int i = size - 1; i > -1; i--) {
				LComponent comp = childs[i];
				if (comp != null) {
					comp.setRotation(rotate);
				}
			}
		}
	}

	public LContainer scrollBy(float x, float y) {
		this._component_scrollX += x;
		this._component_scrollY += y;
		this._scrolling = x != 0f && y != 0f;
		return this;
	}

	public LContainer scrollTo(float x, float y) {
		scrollX(x);
		scrollY(y);
		return this;
	}

	public float scrollX() {
		return this._component_scrollX;
	}

	public float scrollY() {
		return this._component_scrollY;
	}

	public LContainer scrollX(float x) {
		this._scrolling = !MathUtils.equal(x, this._component_scrollX);
		this._component_scrollX = x;
		return this;
	}

	public LContainer scrollY(float y) {
		this._scrolling = !MathUtils.equal(y, this._component_scrollY);
		this._component_scrollY = y;
		return this;
	}

	public boolean isScrolling() {
		return _scrolling;
	}

	public UIControls createUIControls() {
		UIControls controls = null;
		if (_childs != null && _childCount > 0) {
			controls = new UIControls(_childs);
		} else {
			controls = new UIControls();
		}
		return controls;
	}

	public UIControls controls() {
		return createUIControls();
	}

	public int getChildExpandCount() {
		return _childExpandCount;
	}

	public void setChildExpandCount(int e) {
		this._childExpandCount = e;
	}

	public float measureWidth() {
		return getContextWidth();
	}

	public float measureHeight() {
		return getContextHeight();
	}

	public Margin margin(boolean vertical, float left, float top, float right, float bottom) {
		float size = vertical ? getHeight() : getWidth();
		if (_destroyed) {
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
		final int len = this._childCount;
		final LComponent[] childs = this._childs;
		for (int i = len - 1; i > -1; --i) {
			LComponent comp = childs[i];
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
		final int size = this._childCount;
		final LComponent[] comps = this._childs;
		for (int i = size - 1; i > -1; i--) {
			final LComponent child = comps[i];
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
	protected void _onDestroy() {
		super._onDestroy();
		this._newLineHeight = 0;
		this._destroyed = true;
		if (_component_autoDestroy) {
			if (_childs != null) {
				final int size = this._childCount;
				final LComponent[] comps = this._childs;
				for (int i = size - 1; i > -1; i--) {
					LComponent child = comps[i];
					if (child != null && !child.isDestroyed()) {
						child.close();
						child = null;
					}
				}
			}
			_childs = null;
		}
	}

}
