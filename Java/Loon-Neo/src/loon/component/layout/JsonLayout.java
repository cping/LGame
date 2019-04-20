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
package loon.component.layout;

import loon.BaseIO;
import loon.Json;
import loon.LSysException;
import loon.LSystem;
import loon.component.LButton;
import loon.component.LCheckBox;
import loon.component.LClickButton;
import loon.component.LComponent;
import loon.component.LLabel;
import loon.component.LLayer;
import loon.component.LMenu;
import loon.component.LMenuSelect;
import loon.component.LPaper;
import loon.component.LProgress;
import loon.component.LSelect;
import loon.utils.StringUtils;

public class JsonLayout {

	private String layoutType;

	private String path;

	public JsonLayout(String path) {
		if (StringUtils.isEmpty(path)) {
			throw new LSysException("Path is null");
		}
		this.path = path;
	}

	public void parse() {

		String text = BaseIO.loadText(path);

		if (StringUtils.isEmpty(text)) {
			throw new LSysException("File Context is null");
		}

		parseText(text);
	}

	public void parseText(String context) {

		Json.Object jsonObj = LSystem.base().json().parse(context);
		layoutType = jsonObj.getString(JsonTemplate.LAYOUY_TYPE);

		if (StringUtils.isEmpty(layoutType)) {
			layoutType = LSystem.UNKOWN;
		}

		Json.Array arrays = jsonObj.getArray(JsonTemplate.LAYOUY_CHILD);

		for (int i = 0; i < arrays.length(); i++) {
			Json.Object obj = arrays.getObject(i);
			if (obj != null) {
				parseJsonProps(obj);
			}
		}
	}

	protected void parseJsonProps(Json.Object o) {
		Json.Object props = o.getObject(JsonTemplate.LAYOUY_PROPS);
		String typeName = o.getString(JsonTemplate.LAYOUY_TYPE);

		if (JsonTemplate.COMP_BTN.equals(typeName)) {

			LClickButton comp = createComponent(0, props);

		} else if (JsonTemplate.COMP_BTN_IMG.equals(typeName)) {

			LButton comp = createComponent(1, props);

		} else if (JsonTemplate.COMP_LABEL.equals(typeName)) {

			LLabel comp = createComponent(2, props);

		} else if (JsonTemplate.COMP_LAYER.equals(typeName)) {

			LLayer comp = createComponent(3, props);

		} else if (JsonTemplate.COMP_MENU.equals(typeName)) {

			LMenu comp = createComponent(4, props);

		} else if (JsonTemplate.COMP_MENU_SELECT.equals(typeName)) {

			LMenuSelect comp = createComponent(5, props);

		} else if (JsonTemplate.COMP_PAPER.equals(typeName)) {

			LPaper comp = createComponent(6, props);

		} else if (JsonTemplate.COMP_SELECT.equals(typeName)) {

			LSelect comp = createComponent(7, props);

		} else if (JsonTemplate.COMP_PROGRESS.equals(typeName)) {

			LProgress comp = createComponent(8, props);

		} else if (JsonTemplate.COMP_CHECK.equals(typeName)) {

			LCheckBox comp = createComponent(9, props);

		}

	}

	protected <T extends LComponent> T createComponent(int code, Json.Object props) {
		switch (code) {
		case 0:

			break;

		default:
			break;
		}
		return null;
	}
}
