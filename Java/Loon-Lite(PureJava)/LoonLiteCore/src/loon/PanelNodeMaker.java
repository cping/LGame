/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon;

import loon.action.ActionBind;
import loon.action.sprite.StatusBar;
import loon.action.sprite.effect.TextEffect;
import loon.component.LGesture;
import loon.component.LInventory;
import loon.component.LLayer;
import loon.component.LMenuSelect;
import loon.component.LProgress;
import loon.component.LRadar;
import loon.component.LScrollContainer;
import loon.component.LSlider;
import loon.component.LTabContainer;
import loon.component.LTextArea;
import loon.component.LTextBar;
import loon.component.LTextList;
import loon.component.LTextTree;
import loon.geom.IV;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

public final class PanelNodeMaker<T extends ActionBind> implements IV<T> {

	public final static boolean isType(String typeName) {
		if (StringUtils.isNullOrEmpty(typeName)) {
			return false;
		}
		return toType(typeName) != PanelNodeType.Unknown;
	}

	public final static PanelNodeType toType(String typeName) {
		if (StringUtils.isNullOrEmpty(typeName)) {
			return PanelNodeType.Unknown;
		}
		String tName = typeName.toLowerCase().trim();
		if (tName.equals("gesture") || tName.equals("ges") || tName.equals("g")) {
			return PanelNodeType.Gesture;
		} else if (tName.equals("layer") || tName.equals("lay") || tName.equals("l")) {
			return PanelNodeType.Layer;
		} else if (tName.equals("progress") || tName.equals("pro") || tName.equals("p")) {
			return PanelNodeType.Progress;
		} else if (tName.equals("scrollcontainer") || tName.equals("scroll") || tName.equals("s")) {
			return PanelNodeType.ScrollContainer;
		} else if (tName.equals("menuselect") || tName.equals("menu") || tName.equals("select")) {
			return PanelNodeType.MenuSelect;
		} else if (tName.equals("radar") || tName.equals("r")) {
			return PanelNodeType.Radar;
		} else if (tName.equals("slider") || tName.equals("sli")) {
			return PanelNodeType.Slider;
		} else if (tName.equals("tabContainer") || tName.equals("tab")) {
			return PanelNodeType.TabContainer;
		} else if (tName.equals("inventory") || tName.equals("inv")) {
			return PanelNodeType.Inventory;
		} else if (tName.equals("textarea") || tName.equals("area")) {
			return PanelNodeType.TextArea;
		} else if (tName.equals("textbar") || tName.equals("bar")) {
			return PanelNodeType.TextBar;
		} else if (tName.equals("textlist") || tName.equals("list")) {
			return PanelNodeType.TextList;
		} else if (tName.equals("texttree") || tName.equals("tree")) {
			return PanelNodeType.TextTree;
		} else if (tName.equals("statusbar") || tName.equals("status")) {
			return PanelNodeType.StatusBar;
		} else if (tName.equals("texteffect") || tName.equals("effect")) {
			return PanelNodeType.TextEffect;
		}
		return PanelNodeType.Unknown;
	}

	public final static <T extends ActionBind> T create(String typeName, float x, float y, float w, float h) {
		return new PanelNodeMaker<T>(toType(typeName), x, y, w, h).get();
	}

	public final static <T extends ActionBind> T create(PanelNodeType nodeType, float x, float y, float w, float h) {
		return new PanelNodeMaker<T>(nodeType, x, y, w, h).get();
	}

	private PanelNodeType _nodeType;

	private T _value;

	@SuppressWarnings("unchecked")
	private PanelNodeMaker(PanelNodeType nodeType, float x, float y, float w, float h) {
		if (nodeType == null) {
			return;
		}
		this._nodeType = nodeType;
		switch (_nodeType) {
		default:
		case Layer:
			_value = (T) new LLayer(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w), MathUtils.ifloor(h));
			break;
		case Gesture:
			_value = (T) new LGesture(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w),
					MathUtils.ifloor(h));
			break;
		case Progress:
			_value = (T) new LProgress(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w),
					MathUtils.ifloor(h));
			break;
		case ScrollContainer:
			_value = (T) new LScrollContainer(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w),
					MathUtils.ifloor(h));
			break;
		case MenuSelect:
			_value = (T) new LMenuSelect(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w),
					MathUtils.ifloor(h));
			break;
		case Radar:
			_value = (T) new LRadar(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w), MathUtils.ifloor(h));
			break;
		case Slider:
			_value = (T) new LSlider(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w),
					MathUtils.ifloor(h));
			break;
		case TabContainer:
			_value = (T) new LTabContainer(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w),
					MathUtils.ifloor(h));
			break;
		case Inventory:
			_value = (T) new LInventory(x, y, w, h);
			break;
		case TextArea:
			_value = (T) new LTextArea(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w),
					MathUtils.ifloor(h));
			break;
		case TextBar:
			_value = (T) new LTextBar(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w),
					MathUtils.ifloor(h));
			break;
		case TextList:
			_value = (T) new LTextList(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w),
					MathUtils.ifloor(h));
			break;
		case TextTree:
			_value = (T) new LTextTree(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w),
					MathUtils.ifloor(h));
			break;
		case StatusBar:
			_value = (T) new StatusBar(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w),
					MathUtils.ifloor(h));
			break;
		case TextEffect:
			_value = (T) new TextEffect(x, y, w, h);
			break;
		}
	}

	public PanelNodeType getNodeType() {
		return _nodeType;
	}

	@Override
	public T get() {
		return _value;
	}

}