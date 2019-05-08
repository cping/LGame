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
import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.PlayerUtils;
import loon.Screen;
import loon.action.ActionScript;
import loon.action.ActionTween;
import loon.action.sprite.Entity;
import loon.action.sprite.ISprite;
import loon.action.sprite.Sprite;
import loon.action.sprite.SpriteControls;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.component.LButton;
import loon.component.LCheckBox;
import loon.component.LClickButton;
import loon.component.LComponent;
import loon.component.LContainer;
import loon.component.LLabel;
import loon.component.LLayer;
import loon.component.LMenu;
import loon.component.LMenu.MenuItem;
import loon.component.LMenuSelect;
import loon.component.LMessage;
import loon.component.LMessageBox;
import loon.component.LPanel;
import loon.component.LPaper;
import loon.component.LProgress;
import loon.component.LTextArea;
import loon.component.LTextField;
import loon.component.LProgress.ProgressType;
import loon.component.LSelect;
import loon.component.skin.CheckBoxSkin;
import loon.component.skin.MenuSkin;
import loon.component.skin.SkinManager;
import loon.component.skin.TextBarSkin;
import loon.event.Touched;
import loon.font.BMFont;
import loon.font.IFont;
import loon.font.LFont;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * Json布局器,用于解析组件和精灵配置到窗口显示
 */
public class JsonLayout implements LRelease {

	private JsonLayoutListener jsonLayoutListener;

	public static class SpriteParameter {

		public float x = 0f;
		public float y = 0f;
		public int z = -1;
		public float alpha = 0f;
		public float rotation = 0f;
		public float scaleX = 1f;
		public float scaleY = 1f;
		public int width = 1;
		public int height = 1;

		public int code = 0;

		public String path = null;

		public boolean visible = false;

		public LColor color = null;

		public LayoutAlign layoutAlgin;

		public String alignString;

		public SpriteParameter(JsonLayout layout, Json.Object props) {

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
				x = props.getNumber("x", x);
			} else if (props.containsKey("left")) {
				x = props.getNumber("left", x);
			}

			if (props.containsKey("y")) {
				y = props.getNumber("y", y);
			} else if (props.containsKey("top")) {
				y = props.getNumber("top", y);
			}

			if (props.containsKey("z")) {
				z = props.getInt("z", z);
			} else if (props.containsKey("layer")) {
				z = props.getInt("layer", z);
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
			} else if (props.containsKey("img")) {
				path = props.getString("img");
			}

			this.alignString = props.getString("align", "").trim().toLowerCase();

			if ("right".equals(alignString)) {
				this.layoutAlgin = LayoutAlign.Right;
			} else if ("center".equals(alignString)) {
				this.layoutAlgin = LayoutAlign.Center;
			} else if ("left".equals(alignString)) {
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

			this.alpha = props.getNumber("alpha", 1f);

			this.rotation = props.getNumber("rotation", 0f);

			this.scaleX = props.getNumber("scaleX", 1f);
			this.scaleY = props.getNumber("scaleX", 1f);

			String colorString = props.getString("color");

			this.color = StringUtils.isEmpty(colorString) ? LColor.white.cpy() : LColor.valueOf(colorString);

			this.visible = props.getBoolean("visible", true);
		}

	}

	public static class BaseParameter {

		public int x = 0;

		public int y = 0;

		public int z = -1;

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

			if (props.containsKey("z")) {
				z = props.getInt("z", z);
			} else if (props.containsKey("layer")) {
				z = props.getInt("layer", z);
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
			} else if (props.containsKey("img")) {
				path = props.getString("img");
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

	private Screen currentScreen;

	private boolean closed;

	private boolean createGameWindowImage;

	public JsonLayout(String path) {
		if (StringUtils.isEmpty(path)) {
			throw new LSysException("Path is null");
		}
		this.path = path;
		this.container = new TArray<LContainer>();
		this.sprites = new ObjectMap<String, ISprite>();
		createGameWindowImage = false;
	}

	public void pack(Screen screen) {
		this.currentScreen = screen;
		if (sprites.size > 0) {
			for (ISprite s : sprites.values()) {
				screen.add(s);
			}
		}
		if (container.size > 0) {
			for (LContainer c : container) {
				screen.add(c);
			}
		}
	}

	public void parse() {
		try {
			String text = BaseIO.loadText(path);
			if (StringUtils.isEmpty(text)) {
				throw new LSysException("File Context is null");
			}

			parseText(text);
		} catch (Throwable cause) {
			LSystem.error("JsonLayout parse exception", cause);
		}
	}

	public void parseText(String context) {
		try {
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
		} catch (Throwable cause) {
			LSystem.error("JsonLayout parseText exception", cause);
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
		} else if (JsonTemplate.COMP_TEXT_FIELD.equals(typeName)) {
			createComponent(13, props, view);
		} else if (JsonTemplate.SPR_SPRITE.equals(typeName)) {
			createSprite(0, props);
		} else if (JsonTemplate.SPR_ENTITY.equals(typeName)) {
			createSprite(1, props);
		}
	}

	protected ISprite createSprite(int code, Json.Object props) {

		ISprite sprite = null;

		String varName = props.getString(JsonTemplate.LAYOUY_VAR, LSystem.UNKOWN);

		if (StringUtils.isEmpty(varName)) {
			varName = props.getString("name", LSystem.UNKOWN);
		}

		SpriteParameter par = new SpriteParameter(this, props);

		switch (code) {
		case 0:
		default:
			int maxFrame = -1;
			if (props.containsKey("maxFrame")) {
				maxFrame = props.getInt("maxFrame", -1);
			} else if (props.containsKey("frame")) {
				maxFrame = props.getInt("frame", -1);
			}

			int delay = 150;
			if (props.containsKey("delay")) {
				delay = props.getInt("delay", delay);
			} else if (props.containsKey("time")) {
				delay = props.getInt("time", delay);
			}

			int transform = props.getInt("transform", 0);

			Sprite spr = null;
			if (!StringUtils.isEmpty(par.path)) {
				if (par.width > 1 && par.height > 1) {
					spr = new Sprite(par.path, maxFrame, par.x, par.y, par.width, par.height, delay);
				} else {
					spr = new Sprite(par.path);
					spr.setLocation(par.x, par.y);
				}
			} else {
				spr = new Sprite(par.x, par.y);
			}

			spr.setTransform(transform);

			spr.setFlipX(props.getBoolean("flipx", false));
			spr.setFlipY(props.getBoolean("flipy", false));

			sprite = spr;

			break;
		case 1:

			Entity entity = null;

			LTexture image = null;

			if (!StringUtils.isEmpty(par.path)) {
				image = LSystem.loadTexture(par.path);
			}

			if (image != null && par.width > 1 && par.height > 1) {
				entity = new Entity(image, par.x, par.y, par.width, par.height);
			} else if (image != null) {
				entity = new Entity(image, par.x, par.y);
			}

			entity.setRotationCenterX(props.getNumber("pivotx", -1f));
			entity.setRotationCenterY(props.getNumber("pivoty", -1f));

			entity.setSkewCenterX(props.getNumber("skewx", -1f));
			entity.setSkewCenterY(props.getNumber("skewy", -1f));

			entity.setFlipX(props.getBoolean("flipx", false));
			entity.setFlipY(props.getBoolean("flipy", false));

			sprite = entity;

			break;
		}

		if (par.z != -1) {
			sprite.setLayer(par.z);
		}

		sprite.setColor(par.color);
		sprite.setAlpha(par.alpha);
		sprite.setRotation(par.rotation);
		sprite.setScale(par.scaleX, par.scaleY);
		sprite.setVisible(par.visible);

		if (jsonLayoutListener != null) {
			jsonLayoutListener.on(props, varName, sprite);
		}

		putSprites(varName, sprite);

		return sprite;
	}

	protected ObjectMap<String, ISprite> putSprites(String name, ISprite sprite) {
		sprites.put(name, sprite);
		return sprites;
	}

	protected ObjectMap<String, LComponent> putComponents(String name, LComponent comp) {
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

		if (par.z != -1) {
			label.setLayer(par.z);
		}
		label.setVisible(par.visible);

		move(par.layoutAlgin, view, label);

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

		if (par.z != -1) {
			paper.setLayer(par.z);
		}
		paper.setVisible(par.visible);

		move(par.layoutAlgin, view, paper);

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

		if (par.z != -1) {
			box.setLayer(par.z);
		}
		box.setVisible(par.visible);

		move(par.layoutAlgin, view, box);

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

		if (par.z != -1) {
			layer.setLayer(par.z);
		}
		layer.setVisible(par.visible);

		move(par.layoutAlgin, view, layer);

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

		if (par.z != -1) {
			clickButton.setLayer(par.z);
		}

		clickButton.setVisible(par.visible);

		move(par.layoutAlgin, view, clickButton);

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

		if (par.z != -1) {
			clickButton.setLayer(par.z);
		}
		clickButton.setVisible(par.visible);

		move(par.layoutAlgin, view, clickButton);

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

		if (par.z != -1) {
			menu.setLayer(par.z);
		}
		menu.setVisible(par.visible);

		move(par.layoutAlgin, view, menu);

		view.add(menu);

		putComponents(varName, menu);

		return menu;

	}

	protected LMenu createMenu(Json.Object props, String varName, LContainer view) {

		LMenu menu = null;

		BaseParameter par = new BaseParameter(this, props);

		LTexture tabTexture = null;

		LTexture mainTexture = null;

		int moveType = props.getInt("type", 0);

		int taby = props.getInt("taby", 0);

		if (taby == 0) {
			taby = props.getInt("tab", 0);
		}

		int mainsize = props.getInt("mainsize", 0);
		if (mainsize == 0) {
			mainsize = props.getInt("size", 80);
		}

		int cellWidth = props.getInt("cellWidth", -1);
		int cellHeight = props.getInt("cellHeight", -1);

		boolean defaultUI = true;

		if (!StringUtils.isEmpty(par.path)) {
			String[] files = splitData(par.path);
			if (files.length >= 2) {
				tabTexture = LSystem.loadTexture(files[0]);
				mainTexture = LSystem.loadTexture(files[1]);
				defaultUI = false;
			}
		} else {
			MenuSkin skin = MenuSkin.def();
			tabTexture = skin.getTabTexture();
			mainTexture = skin.getMainTexture();
		}

		menu = new LMenu(moveType, par.font, par.text, par.width, par.height, tabTexture, mainTexture, taby, mainsize,
				defaultUI, par.color);

		if (cellWidth != -1) {
			menu.setCellWidth(cellWidth);
		}
		if (cellHeight != -1) {
			menu.setCellHeight(cellHeight);
		}

		Json.Array items = props.getArray("items");
		if (items != null) {

			for (int i = 0; i < items.length(); i++) {
				Json.Object o = items.getObject(i);
				BaseParameter itemPar = new BaseParameter(this, o);

				MenuItem item = menu.add(itemPar.text);
				item.setFont(itemPar.font);

				item.offsetX = itemPar.x;
				item.offsetY = itemPar.y;

				item.labelOffsetX = o.getNumber("laboffx", 0);
				item.labelOffsetY = o.getNumber("laboffy", 0);

				if (!StringUtils.isEmpty(itemPar.path)) {
					item.setTexture(LSystem.loadTexture(itemPar.path));
				}
				String newVarName = o.getString("var", null);
				if (newVarName == null) {
					newVarName = o.getString("name", null);
				}
				if (newVarName != null) {
					item.setVarName(newVarName);
				}

				menu.add(item);

			}

		}

		if (par.z != -1) {
			menu.setLayer(par.z);
		}
		menu.setVisible(par.visible);

		move(par.layoutAlgin, view, menu);

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

		if (par.z != -1) {
			textarea.setLayer(par.z);
		}
		textarea.setVisible(par.visible);

		move(par.layoutAlgin, view, textarea);

		view.add(textarea);

		putComponents(varName, textarea);

		return textarea;
	}

	protected LMessage createMessage(Json.Object props, String varName, LContainer view) {

		LMessage message = null;

		BaseParameter par = new BaseParameter(this, props);

		LTexture background = null;

		if (!StringUtils.isEmpty(par.path)) {
			background = LSystem.loadTexture(par.path);
		}

		if (background == null && createGameWindowImage) {
			background = DefUI.getGameWinFrame(par.width, par.height);
		}

		message = new LMessage(par.font, background, par.x, par.y, par.width, par.height, par.color);

		if (!StringUtils.isEmpty(par.text)) {
			message.setMessage(par.text, false);
		}

		if (par.z != -1) {
			message.setLayer(par.z);
		}
		message.setVisible(par.visible);

		move(par.layoutAlgin, view, message);

		view.add(message);

		putComponents(varName, message);
		parseChild(props, message);

		return message;

	}

	protected LSelect createSelect(Json.Object props, String varName, LContainer view) {

		LSelect message = null;

		BaseParameter par = new BaseParameter(this, props);

		LTexture background = null;

		if (!StringUtils.isEmpty(par.path)) {
			background = LSystem.loadTexture(par.path);
		}

		if (background == null && createGameWindowImage) {
			background = DefUI.getGameWinFrame(par.width, par.height);
		}

		message = new LSelect(par.font, background, par.x, par.y, par.width, par.height, par.color);

		if (!StringUtils.isEmpty(par.text)) {
			String[] mes = splitData(par.text);
			message.setMessage(mes[0], CollectionUtils.copyOf(mes, 1, mes.length));
		}

		if (par.z != -1) {
			message.setLayer(par.z);
		}
		message.setVisible(par.visible);

		move(par.layoutAlgin, view, message);

		view.add(message);

		putComponents(varName, message);
		parseChild(props, message);

		return message;

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

		if (par.z != -1) {
			check.setLayer(par.z);
		}
		check.setVisible(par.visible);

		move(par.layoutAlgin, view, check);

		view.add(check);

		putComponents(varName, check);

		return check;
	}

	protected LProgress createProgress(Json.Object props, String varName, LContainer view) {

		LProgress progress = null;

		BaseParameter par = new BaseParameter(this, props);

		LTexture background = null;
		LTexture bgProgress = null;

		ProgressType pType = ProgressType.GAME;

		if (!StringUtils.isEmpty(par.path)) {
			String[] files = splitData(par.path);
			if (files.length == 2) {
				background = LSystem.loadTexture(files[0]);
				bgProgress = LSystem.loadTexture(files[1]);
				pType = ProgressType.UI;
			}
		}

		progress = new LProgress(pType, par.color, par.x, par.y, par.width, par.height, background, bgProgress);

		progress.setVertical(props.getBoolean("vertical", false));
		float value = props.getNumber("value", 0f);

		if (value > 1f) {
			value = value / 100f;
		}
		progress.setPercentage(value);

		if (par.z != -1) {
			progress.setLayer(par.z);
		}
		progress.setVisible(par.visible);

		move(par.layoutAlgin, view, progress);

		view.add(progress);

		putComponents(varName, progress);

		return progress;
	}

	protected LTextField createTextField(Json.Object props, String varName, LContainer view) {

		BaseParameter par = new BaseParameter(this, props);

		LTexture background = null;

		LTexture leftImg = null;

		LTexture rightImg = null;

		if (!StringUtils.isEmpty(par.path)) {
			String[] files = splitData(par.path);
			if (files.length == 1) {
				background = LSystem.loadTexture(files[0]);
			} else if (files.length == 2) {
				background = LSystem.loadTexture(files[0]);
				leftImg = LSystem.loadTexture(files[1]);
				rightImg = leftImg;
			} else if (files.length == 3) {
				background = LSystem.loadTexture(files[0]);
				leftImg = LSystem.loadTexture(files[1]);
				rightImg = LSystem.loadTexture(files[2]);
			}
		} else {
			TextBarSkin skin = SkinManager.get().getTextBarSkin();
			background = skin.getBodyTexture();
			leftImg = skin.getLeftTexture();
			rightImg = skin.getRightTexture();
		}

		boolean hideBackground = props.getBoolean("hideback", false);
		int inputType = props.getInt("type", 0);
		int limit = props.getInt("limit", 128);

		LTextField field = new LTextField(par.font, par.text, leftImg, rightImg, background, par.x, par.y, par.color,
				inputType, limit);
		field.setHideBackground(hideBackground);

		if (par.z != -1) {
			field.setLayer(par.z);
		}
		field.setVisible(par.visible);

		move(par.layoutAlgin, view, field);

		view.add(field);

		putComponents(varName, field);

		return null;

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

	protected void move(LayoutAlign align, LContainer view, LComponent comp) {
		view.moveOn(align, comp);
	}

	@SuppressWarnings("unchecked")
	protected <T extends LComponent> T createComponent(int code, Json.Object props, LContainer view) {

		LComponent comp = null;

		String varName = props.getString(JsonTemplate.LAYOUY_VAR, LSystem.UNKOWN);

		if (StringUtils.isEmpty(varName)) {
			varName = props.getString("name", LSystem.UNKOWN);
		}
		switch (code) {
		case 0:
			comp = createClickButton(props, varName, view);
			break;
		case 1:
			comp = createImageButton(props, varName, view);
			break;
		case 2:
		default:
			comp = createLabel(props, varName, view);
			break;
		case 3:
			comp = createLayer(props, varName, view);
			break;
		case 4:
			comp = createMenu(props, varName, view);
			break;
		case 5:
			comp = createMenuSelect(props, varName, view);
			break;
		case 6:
			comp = createPaper(props, varName, view);
			break;
		case 7:
			comp = createSelect(props, varName, view);
			break;
		case 8:
			comp = createProgress(props, varName, view);
			break;
		case 9:
			comp = createCheckBox(props, varName, view);
			break;
		case 10:
			comp = createTextarea(props, varName, view);
			break;
		case 11:
			comp = createMessage(props, varName, view);
			break;
		case 12:
			comp = createMessageBox(props, varName, view);
			break;
		case 13:
			comp = createTextField(props, varName, view);
			break;
		}

		if (jsonLayoutListener != null) {
			jsonLayoutListener.on(props, varName, comp);
		}

		return (T) comp;
	}

	public boolean isCreateGameWindowImage() {
		return createGameWindowImage;
	}

	public void setCreateGameWindowImage(boolean gameWindowImage) {
		this.createGameWindowImage = gameWindowImage;
	}

	@SuppressWarnings("unchecked")
	public <T extends LComponent> T getComponent(String name) {
		return (T) components.get(name);
	}

	@SuppressWarnings("unchecked")
	public <T extends LComponent> T removeComponent(String name) {
		return (T) components.remove(name);
	}

	public ActionScript actionScript(String varName, String script) {
		LComponent comp = components.get(varName);
		if (comp != null) {
			return PlayerUtils.act(comp, script);
		}
		return null;
	}

	public ActionTween selfAction(String varName) {
		LComponent comp = components.get(varName);
		if (comp != null) {
			return comp.selfAction();
		}
		return null;
	}

	public boolean isActionCompleted(String varName) {
		LComponent comp = components.get(varName);
		if (comp != null) {
			return comp.isActionCompleted();
		}
		return false;
	}

	public TArray<LContainer> getContainers() {
		return container;
	}

	public TArray<ISprite> getSprites() {
		TArray<ISprite> list = new TArray<ISprite>(sprites.size());
		for (ISprite s : sprites.values()) {
			list.add(s);
		}
		return list;
	}

	public SpriteControls getSpriteControls() {
		return new SpriteControls(getSprites());
	}

	public String getLayoutType() {
		return this.layoutType;
	}

	public String getPath() {
		return this.path;
	}

	public void setListener(JsonLayoutListener listener) {
		this.jsonLayoutListener = listener;
	}

	public JsonLayoutListener getJsonLayoutListener() {
		return this.jsonLayoutListener;
	}

	public void setVisible(boolean v) {
		if (container != null) {
			for (LComponent c : container) {
				if (c != null) {
					c.setVisible(v);
				}
			}
		}
		if (sprites != null) {
			for (ISprite s : sprites.values()) {
				if (s != null) {
					s.setVisible(v);
				}
			}
		}
	}

	public boolean isClosed() {
		return closed;
	}

	@Override
	public void close() {
		Screen screen = this.currentScreen;
		if (screen == null && LSystem.getProcess() != null) {
			screen = LSystem.getProcess().getScreen();
		}
		if (container != null) {
			for (LComponent c : container) {
				if (screen != null) {
					screen.remove(c);
				}
				if (c != null) {
					c.close();
				}

			}
			container.clear();
		}
		if (sprites != null) {
			for (ISprite s : sprites.values()) {
				if (screen != null) {
					screen.remove(s);
				}
				if (s != null) {
					s.close();
				}
			}
			sprites.clear();
		}
		closed = true;
	}

}
