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
import loon.HorizontalAlign;
import loon.Json;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.Screen;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.component.LButton;
import loon.component.LCheckBox;
import loon.component.LClickButton;
import loon.component.LComponent;
import loon.component.LContainer;
import loon.component.LLabel;
import loon.component.LLayer;
import loon.component.LMenuSelect;
import loon.component.LMessageBox;
import loon.component.LPanel;
import loon.component.LPaper;
import loon.component.LTextArea;
import loon.component.LMessageBox.Message;
import loon.component.skin.CheckBoxSkin;
import loon.component.skin.SkinManager;
import loon.event.Touched;
import loon.font.BMFont;
import loon.font.IFont;
import loon.font.LFont;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.TArray;

@SuppressWarnings("unchecked")
public class JsonLayout {

	private static enum LayoutAlign {

		Left, Center, Right, Top, Bottom, TopLeft, TopRight, BottomLeft, BottomRight;

	}

	private static class BaseParameter {

		public int x = 0;

		public int y = 0;

		public int width = 1;

		public int height = 1;

		public int code = 0;

		public LColor color = null;

		public String text = null;

		public String path = null;

		public String alignString = null;

		public HorizontalAlign algin;

		public LayoutAlign layoutAlgin;

		public IFont font;

		public boolean visible;

		public BaseParameter(JsonLayout layout, Json.Object props) {

			if (props.containsKey("code")) {
				code = props.getInt("code", code);
			} else if (props.containsKey("stateNum")) {
				code = props.getInt("stateNum", code);
			} else if (props.containsKey("state")) {
				code = props.getInt("state", code);
			} else if (props.containsKey("number")) {
				code = props.getInt("number", code);
			}

			if (props.containsKey("x")) {
				x = props.getInt("x", x);
			} else if (props.containsKey("left")) {
				x = props.getInt("left", x);
			}

			if (props.containsKey("y")) {
				y = props.getInt("y", y);
			} else if (props.containsKey("top")) {
				y = props.getInt("top", y);
			}

			if (props.containsKey("width")) {
				width = props.getInt("width", width);
			} else if (props.containsKey("right")) {
				width = props.getInt("right", width);
			}

			if (props.containsKey("height")) {
				height = props.getInt("height", height);
			} else if (props.containsKey("bottom")) {
				height = props.getInt("bottom", height);
			}

			if (props.containsKey("path")) {
				path = props.getString("path");
			} else if (props.containsKey("skin")) {
				path = props.getString("skin");
			} else if (props.containsKey("texture")) {
				path = props.getString("texture");
			}

			if (props.containsKey("text")) {
				text = props.getString("text");
			} else if (props.containsKey("context")) {
				text = props.getString("context");
			} else if (props.containsKey("string")) {
				text = props.getString("string");
			}

			this.alignString = props.getString("align", "").trim().toLowerCase();

			if ("right".equals(alignString)) {
				this.algin = HorizontalAlign.RIGHT;
				this.layoutAlgin = LayoutAlign.Right;
			} else if ("center".equals(alignString)) {
				this.algin = HorizontalAlign.CENTER;
				this.layoutAlgin = LayoutAlign.Center;
			} else if ("left".equals(alignString)) {
				this.algin = HorizontalAlign.LEFT;
				this.layoutAlgin = LayoutAlign.Left;
			} else if ("bottom".equals(alignString)) {
				this.layoutAlgin = LayoutAlign.Bottom;
			} else if ("top".equals(alignString)) {
				this.layoutAlgin = LayoutAlign.Top;
			} else if ("topleft".equals(alignString)) {
				this.layoutAlgin = LayoutAlign.TopLeft;
			} else if ("topright".equals(alignString)) {
				this.layoutAlgin = LayoutAlign.TopRight;
			} else if ("bottomleft".equals(alignString)) {
				this.layoutAlgin = LayoutAlign.BottomLeft;
			} else if ("bottomright".equals(alignString)) {
				this.layoutAlgin = LayoutAlign.BottomRight;
			}

			int fontSize = 0;

			if (props.containsKey("fontSize")) {
				fontSize = props.getInt("fontSize", 20);
			} else if (props.containsKey("size")) {
				fontSize = props.getInt("size", 20);
			}

			String fontName = props.getString("fontName");

			if (fontName == null) {
				fontName = props.getString("name");
			}
			if (fontName == null) {
				fontName = props.getString("font");
			}

			String bmFont = props.getString("resource");
			if (bmFont == null) {
				bmFont = props.getString("fontAssets");
			}
			if (bmFont == null) {
				bmFont = props.getString("assets");
			}

			if (!StringUtils.isEmpty(bmFont)) {
				this.font = new BMFont(bmFont);
			} else {
				if (fontSize == 0) {
					this.font = LSystem.getSystemGameFont();
				} else {
					if (StringUtils.isEmpty(fontName) && fontSize == LSystem.getSystemGameFont().getSize()) {
						this.font = LSystem.getSystemGameFont();
					} else {
						this.font = layout.getFont(fontName, fontSize);
					}
				}
			}

			String colorString = props.getString("color");

			this.color = StringUtils.isEmpty(colorString) ? LColor.white.cpy() : LColor.valueOf(colorString);

			this.visible = props.getBoolean("visible", true);
		}

	}

	private final TArray<LContainer> container;

	private ObjectMap<String, ISprite> sprites;

	private ObjectMap<String, LComponent> components;

	private ObjectMap<String, IFont> fonts;

	private String layoutType;

	private String path;

	private boolean createGameWindowImage;

	public JsonLayout(String path) {
		if (StringUtils.isEmpty(path)) {
			throw new LSysException("Path is null");
		}
		this.path = path;
		this.container = new TArray<LContainer>();
		createGameWindowImage = false;
	}

	public void pack(Screen screen) {
		for (LContainer c : container) {
			screen.add(c);
		}
	}

	public void parse() {

		String text = BaseIO.loadText(path);

		if (StringUtils.isEmpty(text)) {
			throw new LSysException("File Context is null");
		}

		parseText(text);
	}

	public void parseText(String context) {

		if (StringUtils.isEmpty(context)) {
			throw new LSysException("Context is null");
		}

		Json.Object jsonObj = LSystem.base().json().parse(context.trim());
		layoutType = jsonObj.getString(JsonTemplate.LAYOUY_TYPE, LSystem.UNKOWN).trim().toLowerCase();

		if ("view".equals(layoutType) || "panel".equals(layoutType)) {

			Json.Object props = jsonObj.getObject(JsonTemplate.LAYOUY_PROPS);

			LPanel panel = new LPanel(props.getInt("x", 0), props.getInt("y", 0),
					props.getInt("width", LSystem.viewSize.getWidth()),
					props.getInt("height", LSystem.viewSize.getHeight()));

			container.add(panel);

			parseChild(jsonObj, panel);

		} else {
			LPanel panel = new LPanel(0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
			container.add(panel);

			parseJsonProps(jsonObj.getObject(JsonTemplate.LAYOUY_PROPS), panel);
		}

	}

	protected void parseJsonProps(Json.Object o, LContainer view) {

		Json.Object props = o.getObject(JsonTemplate.LAYOUY_PROPS);

		String typeName = o.getString(JsonTemplate.LAYOUY_TYPE).trim().toLowerCase();

		if (JsonTemplate.COMP_BTN.equals(typeName)) {
			createComponent(0, props, view);
		} else if (JsonTemplate.COMP_BTN_IMG.equals(typeName)) {
			createComponent(1, props, view);
		} else if (JsonTemplate.COMP_LABEL.equals(typeName)) {
			createComponent(2, props, view);
		} else if (JsonTemplate.COMP_LAYER.equals(typeName)) {

			createComponent(3, props, view);

		} else if (JsonTemplate.COMP_MENU.equals(typeName)) {

			createComponent(4, props, view);

		} else if (JsonTemplate.COMP_MENU_SELECT.equals(typeName)) {
			createComponent(5, props, view);
		} else if (JsonTemplate.COMP_PAPER.equals(typeName)) {
			createComponent(6, props, view);
		} else if (JsonTemplate.COMP_SELECT.equals(typeName)) {

			createComponent(7, props, view);

		} else if (JsonTemplate.COMP_PROGRESS.equals(typeName)) {

			createComponent(8, props, view);

		} else if (JsonTemplate.COMP_CHECK.equals(typeName)) {
			createComponent(9, props, view);
		} else if (JsonTemplate.COMP_TEXTAREA.equals(typeName)) {
			createComponent(10, props, view);
		} else if (JsonTemplate.COMP_MESSAGE.equals(typeName)) {
			createComponent(11, props, view);
		} else if (JsonTemplate.COMP_MESSAGEBOX.equals(typeName)) {
			createComponent(12, props, view);
		}

	}

	private ObjectMap<String, LComponent> putComponents(String name, LComponent comp) {
		if (components == null) {
			components = new ObjectMap<String, LComponent>();
		}
		components.put(name, comp);
		return components;
	}

	protected IFont getFont(String fontName, int fontSize) {
		if (fonts == null) {
			fonts = new ObjectMap<String, IFont>();
		}
		String key = (StringUtils.isEmpty(fontName) ? "def" : fontName) + "." + fontSize;
		IFont font = fonts.get(key);
		if (font == null) {
			if (StringUtils.isEmpty(fontName)) {
				font = LFont.getFont(fontSize);
			} else {
				font = LFont.getFont(fontName, fontSize);
			}
			fonts.put(key, font);
		}
		return font;
	}

	protected LLabel createLabel(Json.Object props, String varName, LContainer view) {

		BaseParameter par = new BaseParameter(this, props);

		LTexture background = null;

		if (!StringUtils.isEmpty(par.path)) {
			background = LSystem.loadTexture(par.path);
		}

		LLabel label = new LLabel(par.algin, par.font, par.color, background, par.text, par.x, par.y, par.width,
				par.height);

		label.setVisible(par.visible);

		onMove(par.layoutAlgin, view, label);

		view.add(label);

		putComponents(varName, label);

		return label;
	}

	protected LPaper createPaper(Json.Object props, String varName, LContainer view) {

		BaseParameter par = new BaseParameter(this, props);

		LTexture background = null;

		if (!StringUtils.isEmpty(par.path)) {
			background = LSystem.loadTexture(par.path);
		}

		LPaper paper = new LPaper(background, par.x, par.y, par.width, par.height);

		paper.setVisible(par.visible);

		onMove(par.layoutAlgin, view, paper);

		view.add(paper);

		putComponents(varName, paper);

		parseChild(props, paper);

		return paper;
	}

	protected LMessageBox createMessageBox(Json.Object props, String varName, LContainer view) {

		BaseParameter par = new BaseParameter(this, props);

		LTexture background = null;

		if (!StringUtils.isEmpty(par.path)) {
			background = LSystem.loadTexture(par.path);
		}

		String[] messages = null;

		if (!StringUtils.isEmpty(par.text)) {
			messages = splitData(par.text);
		}

		String flagStr = props.getString("flag", null);

		// actor face image
		String facePath = props.getString("face", null);

		final LMessageBox box = new LMessageBox(messages, flagStr, par.font, facePath, background, par.x, par.y,
				par.width, par.height);

		if (background == null && createGameWindowImage) {
			box.setBackground(DefUI.getGameWinFrame(box.width(), box.height()));
			box.setOffsetX(10);
			box.setOffsetY(10);
		}

		box.up(new Touched() {

			@Override
			public void on(float x, float y) {
				box.loop();
			}
		});

		box.setVisible(par.visible);

		onMove(par.layoutAlgin, view, box);

		view.add(box);

		putComponents(varName, box);

		return null;

	}

	protected LLayer createLayer(Json.Object props, String varName, LContainer view) {

		BaseParameter par = new BaseParameter(this, props);

		LTexture background = null;

		if (!StringUtils.isEmpty(par.path)) {
			background = LSystem.loadTexture(par.path);
		}

		boolean bounded = props.getBoolean("bound", true);

		LLayer layer = new LLayer(par.x, par.y, par.width, par.height, bounded);

		layer.setBackground(background);
		layer.setVisible(par.visible);

		onMove(par.layoutAlgin, view, layer);

		view.add(layer);

		putComponents(varName, layer);

		parseChild(props, layer);

		return layer;
	}

	protected String[] splitData(String message) {
		String[] result = null;
		if (message.indexOf('|') == -1) {
			result = StringUtils.split(message, ',');
		} else {
			result = StringUtils.split(message, '|');
		}
		return result;
	}

	protected LClickButton createClickButton(Json.Object props, String varName, LContainer view) {

		LClickButton clickButton = null;

		BaseParameter par = new BaseParameter(this, props);

		if (!StringUtils.isEmpty(par.path)) {

			String[] args = splitData(par.path);

			if (args.length == 1) {

				LTexture tex = LSystem.loadTexture(args[0]);
				clickButton = new LClickButton(par.text, par.color, par.x, par.y, par.width, par.height, tex, tex, tex);
				clickButton.setGrayButton(true);

			} else if (args.length == 2) {

				LTexture a = LSystem.loadTexture(args[0]);
				LTexture b = LSystem.loadTexture(args[1]);

				clickButton = new LClickButton(par.text, par.color, par.x, par.y, par.width, par.height, a, a, b);

			} else if (args.length >= 3) {

				LTexture a = LSystem.loadTexture(args[0]);
				LTexture b = LSystem.loadTexture(args[1]);
				LTexture c = LSystem.loadTexture(args[2]);

				clickButton = new LClickButton(par.text, par.font, par.color, par.x, par.y, par.width, par.height, a, b,
						c);
			}

		} else {

			clickButton = new LClickButton(par.text, par.font, par.color, par.x, par.y, par.width, par.height);

		}

		clickButton.setVisible(par.visible);

		onMove(par.layoutAlgin, view, clickButton);

		view.add(clickButton);

		putComponents(varName, clickButton);

		return clickButton;

	}

	protected LButton createImageButton(Json.Object props, String varName, LContainer view) {

		LButton clickButton = null;

		BaseParameter par = new BaseParameter(this, props);

		if (!StringUtils.isEmpty(par.path)) {
			clickButton = new LButton(par.path, par.text, par.color, par.width, par.height, par.x, par.y);
		} else {
			clickButton = new LButton(par.font, par.text, par.color, par.x, par.y, par.width, par.height);
		}

		clickButton.setVisible(par.visible);

		onMove(par.layoutAlgin, view, clickButton);

		view.add(clickButton);

		putComponents(varName, clickButton);

		return clickButton;

	}

	protected LMenuSelect createMenuSelect(Json.Object props, String varName, LContainer view) {

		LMenuSelect menu = null;

		BaseParameter par = new BaseParameter(this, props);

		LTexture background = null;
		if (!StringUtils.isEmpty(par.path)) {
			background = LSystem.loadTexture(par.path);
		}

		if (!StringUtils.isEmpty(par.text)) {
			String[] texts = splitData(par.text);
			menu = new LMenuSelect(par.font, texts, background, par.x, par.y);
		} else {
			menu = new LMenuSelect(par.font, new String[] { LSystem.UNKOWN }, background, par.x, par.y);
		}

		if (background == null && createGameWindowImage) {
			menu.setBackground(DefUI.getGameWinFrame(menu.width(), menu.height()));
		}

		menu.setVisible(par.visible);

		onMove(par.layoutAlgin, view, menu);

		view.add(menu);

		putComponents(varName, menu);

		return menu;

	}

	protected LTextArea createTextarea(Json.Object props, String varName, LContainer view) {

		LTextArea textarea = null;

		BaseParameter par = new BaseParameter(this, props);

		LTexture background = null;
		if (!StringUtils.isEmpty(par.path)) {
			background = LSystem.loadTexture(par.path);
		}

		int typeCode = props.getInt("type", 0);
		if (typeCode < 0) {
			typeCode = 0;
		} else if (typeCode > 0) {
			typeCode = 1;
		}
		int maxLine = props.getInt("max", -1);
		if (maxLine == -1) {
			maxLine = props.getInt("maxLine", -1);
		}

		int offsetX = 5;
		int offsetY = 5;

		textarea = new LTextArea(typeCode, maxLine, par.font, par.x, par.y, par.width + offsetX, par.height + offsetY,
				background);

		if (!StringUtils.isEmpty(par.text)) {
			String[] mes = splitData(par.text);
			for (int i = 0; i < mes.length; i++) {
				textarea.put(mes[i], par.color);
			}
		}

		if (background == null && createGameWindowImage) {
			textarea.setBackground(DefUI.getGameWinFrame(textarea.width(), textarea.height()));
			textarea.setLeftOffset(offsetX);
			textarea.setTopOffset(offsetY);
		}

		textarea.setVisible(par.visible);

		onMove(par.layoutAlgin, view, textarea);

		view.add(textarea);

		putComponents(varName, textarea);

		return textarea;
	}

	protected LCheckBox createCheckBox(Json.Object props, String varName, LContainer view) {

		LCheckBox check = null;

		BaseParameter par = new BaseParameter(this, props);

		LTexture checked = null;
		LTexture unchecked = null;

		if (!StringUtils.isEmpty(par.path)) {
			String[] files = splitData(par.path);
			if (files.length == 1) {
				unchecked = LSystem.loadTexture(files[0]);
				checked = unchecked;
			} else {
				unchecked = LSystem.loadTexture(files[0]);
				checked = LSystem.loadTexture(files[1]);
			}
		} else {
			CheckBoxSkin skin = SkinManager.get().getCheckBoxSkin();
			unchecked = skin.getUncheckedTexture();
			checked = skin.getCheckedTexture();
		}

		check = new LCheckBox(par.text, par.x, par.y, unchecked, checked, MathUtils.max(par.width, checked.getWidth()),
				true, par.color, par.font);

		check.setVisible(par.visible);

		onMove(par.layoutAlgin, view, check);

		view.add(check);

		putComponents(varName, check);

		return check;
	}

	protected void parseChild(Json.Object props, LContainer view) {

		if (props.containsKey(JsonTemplate.LAYOUY_CHILD)) {

			Json.Array arrays = props.getArray(JsonTemplate.LAYOUY_CHILD);

			for (int i = 0; i < arrays.length(); i++) {
				Json.Object obj = arrays.getObject(i);
				if (obj != null) {
					parseJsonProps(obj, view);
				}
			}
		}

	}

	protected void onMove(LayoutAlign align, LContainer view, LComponent comp) {
		if (align != null) {
			switch (align) {
			case Left:
				view.leftOn(comp);
				break;
			case Right:
				view.rightOn(comp);
				break;
			case Center:
				view.centerOn(comp);
				break;
			case Top:
				view.topOn(comp);
				break;
			case Bottom:
				view.bottomOn(comp);
				break;
			case TopLeft:
				view.topLeftOn(comp);
				break;
			case TopRight:
				view.topRightOn(comp);
				break;
			case BottomLeft:
				view.bottomLeftOn(comp);
				break;
			case BottomRight:
				view.bottomRightOn(comp);
				break;
			}
		}
	}

	protected <T extends LComponent> T createComponent(int code, Json.Object props, LContainer view) {

		LComponent comp = null;
		String varName = props.getString(JsonTemplate.LAYOUY_VAR, LSystem.UNKOWN);
		if (varName == null) {
			varName = props.getString("name", LSystem.UNKOWN);
		}
		switch (code) {
		case 0:
			comp = createClickButton(props, varName, view);
			break;
		case 1:
			comp = createImageButton(props, varName, view);
			break;
		case 3:
			comp = createLayer(props, varName, view);
			break;
		case 5:
			comp = createMenuSelect(props, varName, view);
			break;
		case 6:
			comp = createPaper(props, varName, view);
			break;
		case 9:
			comp = createCheckBox(props, varName, view);
			break;
		case 10:
			comp = createTextarea(props, varName, view);
			break;
		case 12:
			comp = createMessageBox(props, varName, view);
			break;
		case 2:
		default:
			comp = createLabel(props, varName, view);
			break;
		}
		return (T) comp;
	}

	public boolean isCreateGameWindowImage() {
		return createGameWindowImage;
	}

	public void setCreateGameWindowImage(boolean gameWindowImage) {
		this.createGameWindowImage = gameWindowImage;
	}

	public <T extends LComponent> T getComponent(String name) {
		return (T) components.get(name);
	}

	public <T extends LComponent> T removeComponent(String name) {
		return (T) components.remove(name);
	}

	public TArray<LContainer> getContainers() {
		return container;
	}

}
