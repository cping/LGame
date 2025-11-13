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
 *          此类用以用户名生成，由用户选择指定字符构成用户名。
 * 
 *          Examples1:
 * 
 *          TArray<String> list = new TArray<String>(); list.add("赵钱孙李周吴郑王");
 *          list.add("冯陈褚卫蒋沈韩杨"); list.add("朱秦尤许何吕施张"); list.add("孔曹严华金魏陶姜");
 *          list.add("<>"); LDecideName decideName = new LDecideName(list,0, 0);
 *          add(decideName);
 * 
 */
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.component.skin.MessageSkin;
import loon.component.skin.SkinManager;
import loon.font.FontSet;
import loon.font.IFont;
import loon.opengl.GLEx;
import loon.utils.TArray;
import loon.utils.MathUtils;

/**
 * 在一些不方便输入字符串的设备上,输入角色名称时可用此UI,字数不够上下分页,多来几个就成了……
 * 
 * Examples:
 * 
 * <pre>
 * TArray<String> list = new TArray<String>();
 * list.add("赵钱孙李周吴郑王");
 * list.add("冯陈褚卫蒋沈韩杨");
 * list.add("朱秦尤许何吕施张");
 * list.add("孔曹严华金魏陶姜");
 * list.add("<>");
 * LDecideName decideName = new LDecideName(list, 0, 0);
 * add(decideName);
 * </pre>
 */
public class LDecideName extends LComponent implements FontSet<LDecideName> {

	private LColor _fontColor = LColor.white;

	private LColor _selectedColor;

	private LColor _labelNameColor = LColor.orange;

	private IFont _font;

	private int _text_width_space = 5;

	private String _enterFlag;

	private String _name;

	private String _labelName;

	private int _cursorX = 0;

	private int _cursorY = 0;

	private TArray<String> _keyArrays;

	private boolean _showGrid = false;;

	private float _dx = 0.1f;

	private float _dy = 0.1f;

	private float _labelOffsetX, _labelOffsetY;

	private int _maxNameString = 5;

	private char enterFlagString = '>', clearFlagString = '<';

	public LDecideName(TArray<String> mes, int x, int y) {
		this(mes, x, y, 400, 250);
	}

	public LDecideName(String label, TArray<String> mes, int x, int y, int width, int height) {
		this(label, LSystem.EMPTY, mes, SkinManager.get().getMessageSkin().getFont(), x, y, width, height,
				SkinManager.get().getMessageSkin().getBackgroundTexture());
	}

	public LDecideName(String label, TArray<String> mes, int x, int y, int width, int height, LTexture bg) {
		this(label, LSystem.EMPTY, mes, SkinManager.get().getMessageSkin().getFont(), x, y, width, height, bg);
	}

	public LDecideName(TArray<String> mes, int x, int y, int width, int height) {
		this("Name:", LSystem.EMPTY, mes, SkinManager.get().getMessageSkin().getFont(), x, y, width, height,
				SkinManager.get().getMessageSkin().getBackgroundTexture());
	}

	public LDecideName(String label, String name, TArray<String> mes, IFont f, int x, int y, int width, int height,
			LTexture bg) {
		this(label, name, mes, f, x, y, width, height, bg, SkinManager.get().getMessageSkin().getFontColor());
	}

	public LDecideName(MessageSkin skin, String label, String name, TArray<String> mes, int x, int y, int width,
			int height) {
		this(label, name, mes, skin.getFont(), x, y, width, height, skin.getBackgroundTexture(), skin.getFontColor());
	}

	public LDecideName(String label, String name, TArray<String> mes, IFont f, int x, int y, int width, int height,
			LTexture bg, LColor color) {
		super(x, y, width, height - f.getHeight() - 20);
		this._fontColor = color;
		this._selectedColor = new LColor(0, 150, 0, 150);
		this.setFont(f);
		this.onlyBackground(bg);
		this._labelName = label;
		this._name = name;
		this._keyArrays = mes;
		this.leftOffset = _font.getHeight() + 15;
		this.topOffset = _font.getHeight() + 20;
	}

	public void draw(GLEx g, int x, int y) {
		final IFont oldFont = g.getFont();
		final int oldColor = g.color();
		if (_background != null) {
			g.draw(_background, x, y, getWidth(), getHeight());
		}
		g.setFont(_font);
		float posX = x + leftOffset;
		if (_labelName != null) {
			g.drawString(_labelName + this._name, posX + _labelOffsetX + _text_width_space,
					y + _labelOffsetY - _text_width_space / 2, _labelNameColor);
		}
		float posY = y + topOffset;
		for (int j = 0; j < this._keyArrays.size; j++) {
			for (int i = 0; i < this._keyArrays.get(j).length(); i++)
				if (this._keyArrays.get(j).charAt(i) != '　') {
					g.drawString(String.valueOf(this._keyArrays.get(j).charAt(i)),
							posX + MathUtils.round((i * _dx + 0.01f) * getWidth()) + _text_width_space,
							posY + MathUtils.round(((j + 1) * _dy - 0.01f) * getHeight()) - _font.getAscent()
									- _text_width_space / 2,
							_fontColor);
					if (_showGrid) {
						g.drawRect(posX + MathUtils.round((i * _dx) * getWidth()),
								posY + MathUtils.round((j * _dy) * getHeight()), MathUtils.round(_dx * getWidth()),
								MathUtils.round(_dy * getHeight()), _selectedColor);
					}
				}
		}
		g.fillRect(posX + MathUtils.round((this._cursorX * _dx) * getWidth()) - 1,
				posY + MathUtils.round((this._cursorY * _dy) * getHeight()) - 1, MathUtils.round(_dx * getWidth()) + 2,
				MathUtils.round(_dy * getHeight()) + 2, _selectedColor);
		g.setFont(oldFont);
		g.setColor(oldColor);
	}

	public LColor getLabelNameColor() {
		return _labelNameColor;
	}

	public LDecideName setLabelNameColor(LColor c) {
		_labelNameColor = c;
		return this;
	}

	@Override
	protected void processTouchReleased() {
		super.processTouchReleased();
		this.pushEnter();
	}

	private char getArrays(int x, int y) {
		if (_keyArrays.size <= x) {
			return LSystem.SPACE;
		}
		String result = this._keyArrays.get(x);
		if (result.length() <= y) {
			return LSystem.SPACE;
		}
		return result.charAt(y);
	}

	public int pushEnter() {
		if (getArrays(this._cursorY, this._cursorX) == enterFlagString) {
			if (this._name.equals(LSystem.EMPTY)) {
				return -2;
			}
			_enterFlag = "Enter";
			return -1;
		}
		if (getArrays(this._cursorY, this._cursorX) == clearFlagString) {
			if (!this._name.equals(LSystem.EMPTY)) {
				this._name = this._name.substring(0, this._name.length() - 1);
			}
			_enterFlag = "Clear";
		} else if (this._name.length() < _maxNameString) {
			this._name += getArrays(this._cursorY, this._cursorX);
			_enterFlag = "Add";
		}
		return -2;
	}

	public int pushEscape() {
		return -1;
	}

	/**
	 * 强制坐标向左移动
	 * 
	 * @return
	 */
	public int pushLeft() {
		moving(-1, 0);
		return -2;
	}

	/**
	 * 强制坐标向右移动
	 * 
	 * @return
	 */
	public int pushRight() {
		moving(1, 0);
		return -2;
	}

	/**
	 * 强制坐标向下移动
	 * 
	 * @return
	 */
	public int pushDown() {
		moving(0, 1);
		return -2;
	}

	/**
	 * 强制坐标向上移动
	 * 
	 * @return
	 */
	public int pushUp() {
		moving(0, -1);
		return -2;
	}

	private void moving(int x, int y) {
		this._cursorX += x;
		this._cursorY += y;
		if (this._cursorX >= this._keyArrays.get(0).length()) {
			this._cursorX = 0;
		}
		if (this._cursorX < 0) {
			this._cursorX = (this._keyArrays.get(0).length() - 1);
		}
		if (this._cursorY >= this._keyArrays.size) {
			this._cursorY = 0;
		}
		if (this._cursorY < 0) {
			this._cursorY = (this._keyArrays.size - 1);
		}
		if (getArrays(this._cursorY, this._cursorX) == LSystem.SPACE) {
			moving(x, y);
		}
	}

	private int leftOffset, topOffset;

	public void moveCursor(float x, float y) {
		final int indexX = MathUtils.ceil((x - leftOffset)) / MathUtils.round(_dx * getWidth());
		final int indexY = MathUtils.ceil((y - topOffset)) / MathUtils.round(_dy * getHeight());
		if ((indexX < 0) || (indexY < 0) || (indexY >= this._keyArrays.size)
				|| (indexX >= this._keyArrays.get(0).length()))
			return;
		if (getArrays(indexY, indexX) != '　') {
			this._cursorX = indexX;
			this._cursorY = indexY;
		}
	}

	public void setSelectColor(LColor selectColor) {
		this._selectedColor = selectColor;
	}

	@Override
	public LColor getFontColor() {
		return this._fontColor;
	}

	@Override
	public LDecideName setFontColor(LColor fontColor) {
		this._fontColor = fontColor;
		return this;
	}

	public String getLabelName() {
		return _labelName;
	}

	public void setLabelName(String labelName) {
		this._labelName = labelName;
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		moveCursor(getUITouchX(), getUITouchY());
		draw(g, x, y);
	}

	public int getCursorX() {
		return _cursorX;
	}

	public void setCursorX(int cursorX) {
		this._cursorX = cursorX;
	}

	public int getCursorY() {
		return _cursorY;
	}

	public void setCursorY(int cursorY) {
		this._cursorY = cursorY;
	}

	public float getDx() {
		return _dx;
	}

	public void setDx(float dx) {
		this._dx = dx;
	}

	public float getDy() {
		return _dy;
	}

	public void setDy(float dy) {
		this._dy = dy;
	}

	public String getEnterFlag() {
		return _enterFlag;
	}

	public String getDecideName() {
		return _name;
	}

	public void setEnterFlag(String enterFlag) {
		this._enterFlag = enterFlag;
	}

	public LTexture getBgTexture() {
		return _background;
	}

	public void setBgTexture(LTexture bgTexture) {
		this.setBackground(bgTexture);
	}

	public int getMaxNameString() {
		return _maxNameString;
	}

	public void setMaxNameString(int maxNameString) {
		this._maxNameString = maxNameString;
	}

	public char getEnterFlagString() {
		return enterFlagString;
	}

	public void setEnterFlagString(char enterFlagString) {
		this.enterFlagString = enterFlagString;
	}

	public char getClearFlagString() {
		return clearFlagString;
	}

	public void setClearFlagString(char clearFlagString) {
		this.clearFlagString = clearFlagString;
	}

	public int getLeftOffset() {
		return leftOffset;
	}

	public void setLeftOffset(int leftOffset) {
		this.leftOffset = leftOffset;
	}

	public int getTopOffset() {
		return topOffset;
	}

	public void setTopOffset(int topOffset) {
		this.topOffset = topOffset;
	}

	@Override
	public LDecideName setFont(IFont font) {
		if (font == null) {
			return this;
		}
		this._font = font;
		return this;
	}

	@Override
	public IFont getFont() {
		return _font;
	}

	public float getLabelOffsetX() {
		return _labelOffsetX;
	}

	public void setLabelOffsetX(float x) {
		this._labelOffsetX = x;
	}

	public float getLabelOffsetY() {
		return _labelOffsetY;
	}

	public void setLabelOffsetY(float y) {
		this._labelOffsetY = y;
	}

	public boolean isShowGrid() {
		return _showGrid;
	}

	public LDecideName setShowGrid(boolean showGrid) {
		this._showGrid = showGrid;
		return this;
	}

	public int getTextWidthSpace() {
		return _text_width_space;
	}

	public LDecideName setTextWidthSpace(int tws) {
		this._text_width_space = tws;
		return this;
	}

	@Override
	public String getUIName() {
		return "DecideName";
	}

	@Override
	public void destory() {

	}
}
