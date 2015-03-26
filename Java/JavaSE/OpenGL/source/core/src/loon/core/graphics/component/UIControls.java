/**
 * 
 * Copyright 2014
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
 * @version 0.4.1
 * 
 *          新增类，用以同时处理多个组件对象到同一状态
 */
package loon.core.graphics.component;

import loon.core.graphics.LComponent;
import loon.core.graphics.LContainer;
import loon.utils.collection.ArrayList;

public class UIControls {

	public static float getChildrenHeight(LContainer c) {
		float totalHeight = 0;
		LComponent[] list = c.getComponents();
		for (int i = 0; i < list.length; i++) {
			totalHeight += list[i].getHeight();
		}
		return totalHeight;
	}

	public static float getChildrenWidth(LContainer c) {
		float totalWidth = 0;
		LComponent[] list = c.getComponents();
		for (int i = 0; i < list.length; i++) {
			totalWidth += list[i].getWidth();
		}
		return totalWidth;
	}

	public static float getMaxChildHeight(LContainer c) {
		int maxHeight = 0;
		LComponent[] list = c.getComponents();
		for (int i = 0; i < list.length; i++) {
			maxHeight = Math.max(maxHeight, list[i].getHeight());
		}
		return maxHeight;
	}

	public static int getMaxChildWidth(LContainer c) {
		int maxWidth = 0;
		LComponent[] list = c.getComponents();
		for (int i = 0; i < list.length; i++) {
			maxWidth = Math.max(maxWidth, list[i].getWidth());
		}
		return maxWidth;
	}

	private final ArrayList _comps;

	public UIControls(LComponent... comps) {
		this();
		add(comps);
	}

	public UIControls() {
		this._comps = new ArrayList();
	}

	public void add(LComponent comp) {
		if (comp == null) {
			throw new IllegalArgumentException("LComponent cannot be null.");
		}
		_comps.add(comp);
	}

	public void add(LComponent... comps) {
		if (comps == null) {
			throw new IllegalArgumentException("LComponents cannot be null.");
		}
		for (int i = 0, n = comps.length; i < n; i++) {
			add(comps[i]);
		}
	}

	public void remove(LComponent... comps) {
		if (comps == null) {
			throw new IllegalArgumentException("LComponents cannot be null.");
		}
		for (int i = 0, n = comps.length; i < n; i++) {
			add(comps[i]);
		}
	}

	public void setFocusable(boolean focus) {
		for (int i = 0, n = _comps.size(); i < n; i++) {
			LComponent comp = (LComponent) _comps.get(i);
			if (comp != null) {
				comp.setFocusable(focus);
			}
		}
	}

	public void setEnabled(boolean e) {
		for (int i = 0, n = _comps.size(); i < n; i++) {
			LComponent comp = (LComponent) _comps.get(i);
			if (comp != null) {
				comp.setEnabled(e);
			}
		}
	}

	/**
	 * 附带一提，此处Set大写是为了显示作用比较特殊，以及建议使用一个ClickListener，监听多个组件，所以"S"
	 * 
	 * @param click
	 */
	public void SetClick(ClickListener click) {
		for (int i = 0, n = _comps.size(); i < n; i++) {
			LComponent comp = (LComponent) _comps.get(i);
			if (comp != null) {
				comp.SetClick(click);
			}
		}
	}

	public void setAlpha(float alpha) {
		for (int i = 0, n = _comps.size(); i < n; i++) {
			LComponent comp = (LComponent) _comps.get(i);
			if (comp != null) {
				comp.setAlpha(alpha);
			}
		}
	}

	public void setScale(float s) {
		for (int i = 0, n = _comps.size(); i < n; i++) {
			LComponent comp = (LComponent) _comps.get(i);
			if (comp != null) {
				comp.setScale(s);
			}
		}
	}

	public void setVisible(boolean v) {
		for (int i = 0, n = _comps.size(); i < n; i++) {
			LComponent comp = (LComponent) _comps.get(i);
			if (comp != null) {
				comp.setVisible(v);
			}
		}
	}

	public void setTicked(boolean c) {
		for (int i = 0, n = _comps.size(); i < n; i++) {
			Object o = _comps.get(i);
			if (o != null && o instanceof LCheckBox) {
				LCheckBox box = (LCheckBox) o;
				box.setTicked(c);
			}
		}
	}

	public void setPercentage(float p) {
		for (int i = 0, n = _comps.size(); i < n; i++) {
			Object o = _comps.get(i);
			if (o != null && o instanceof LProgress) {
				LProgress progress = (LProgress) o;
				progress.setPercentage(p);
			}
		}
	}

	public void remove(LComponent comp) {
		if (comp == null) {
			throw new IllegalArgumentException("LComponent cannot be null.");
		}
		_comps.remove(comp);
	}

}
