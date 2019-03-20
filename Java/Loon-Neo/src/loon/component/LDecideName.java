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

import loon.LTexture;
import loon.canvas.LColor;
import loon.component.skin.MessageSkin;
import loon.component.skin.SkinManager;
import loon.font.FontSet;
import loon.font.IFont;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.TArray;
import loon.utils.MathUtils;

/**
 * 在一些不方便输入字符串的设备上,输入角色名称时可用此UI,字数不够上下分页,多来几个就成了……
 * 
 * Examples:
 * 
 *      TArray<String> list = new TArray<String>();
 *      list.add("赵钱孙李周吴郑王");
 *      list.add("冯陈褚卫蒋沈韩杨"); 
 *      list.add("朱秦尤许何吕施张"); 
 *      list.add("孔曹严华金魏陶姜");
 *      list.add("<>"); 
 *      LDecideName decideName = new LDecideName(list,0, 0);
 *      add(decideName);
 */
public class LDecideName extends LComponent implements FontSet<LDecideName> {

	private LColor fontColor = LColor.white;

	private IFont _font;
	private String enterFlag;

	private String name;
	private String labelName;
	private int cursorX = 0;
	private int cursorY = 0;
	private TArray<String> keyArrays;
	private LTexture bgTexture;

	private boolean bind = false, showGrid = false;;

	private float dx = 0.1f;
	private float dy = 0.1f;

	private float labelOffsetX, labelOffsetY;

	private int maxNameString = 5;

	private int initDraw = -1;

	private char enterFlagString = '>', clearFlagString = '<';

	public LDecideName(TArray<String> mes, int x, int y) {
		this(mes, x, y, 400, 250);
	}

	public LDecideName(String label, TArray<String> mes, int x, int y,
			int width, int height) {
		this(label, "", mes, SkinManager.get().getMessageSkin().getFont(), x,
				y, width, height, SkinManager.get().getMessageSkin()
						.getBackgroundTexture());
	}

	public LDecideName(String label, TArray<String> mes, int x, int y,
			int width, int height, LTexture bg) {
		this(label, "", mes, SkinManager.get().getMessageSkin().getFont(), x,
				y, width, height, bg);
	}

	public LDecideName(TArray<String> mes, int x, int y, int width, int height) {
		this("Name:", "", mes, SkinManager.get().getMessageSkin().getFont(), x,
				y, width, height, SkinManager.get().getMessageSkin()
						.getBackgroundTexture());
	}

	public LDecideName(String label, String name, TArray<String> mes, IFont f,
			int x, int y, int width, int height, LTexture bg) {
		this(label, name, mes, f, x, y, width, height, bg, SkinManager.get()
				.getMessageSkin().getFontColor());
	}

	public LDecideName(MessageSkin skin, String label, String name,
			TArray<String> mes, int x, int y, int width, int height) {
		this(label, name, mes, skin.getFont(), x, y, width, height, skin
				.getBackgroundTexture(), skin.getFontColor());
	}

	public LDecideName(String label, String name, TArray<String> mes, IFont f,
			int x, int y, int width, int height, LTexture bg, LColor color) {
		super(x, y, width, height - f.getHeight() - 20);
		this._font = f;
		this.fontColor = color;
		this.baseColor = new LColor(0, 150, 0, 150);
		this.labelName = label;
		this.name = name;
		this.keyArrays = mes;
		this.bgTexture = bg;
		this.leftOffset = _font.getHeight() + 15;
		this.topOffset = _font.getHeight() + 20;
	}

	private void bindString() {
		if (!bind) {
			if (_font instanceof LFont) {
				StringBuffer sbr = new StringBuffer();
				for (int i = 0; i < keyArrays.size; i++) {
					sbr.append(keyArrays.get(i));
				}
				LSTRDictionary.get().bind((LFont) _font, sbr.toString());
			}
			bind = true;
		}
	}

	public void draw(GLEx g, int x, int y) {
		bindString();
		if (initDraw < 1) {
			initDraw++;
			return;
		}
		IFont oldFont = g.getFont();
		int oldColor = g.color();
		if (bgTexture != null) {
			g.draw(bgTexture, x, y, getWidth(), getHeight());
		}
		float posX = x + leftOffset;
		if (labelName != null) {
			g.drawString(labelName + this.name, posX + labelOffsetX, y
					+ labelOffsetY, LColor.orange);
		}
		float posY = y + topOffset;
		for (int j = 0; j < this.keyArrays.size; j++) {
			for (int i = 0; i < this.keyArrays.get(j).length(); i++)
				if (this.keyArrays.get(j).charAt(i) != '　') {
					g.drawString(
							String.valueOf(this.keyArrays.get(j).charAt(i)),
							posX
									+ MathUtils.round((i * dx + 0.01f)
											* getWidth()),
							posY
									+ MathUtils.round(((j + 1) * dy - 0.01f)
											* getHeight()) - _font.getAscent(),
							fontColor);
					if (showGrid) {
						g.drawRect(
								posX + MathUtils.round((i * dx) * getWidth()),
								posY + MathUtils.round((j * dy) * getHeight()),
								MathUtils.round(dx * getWidth()),
								MathUtils.round(dy * getHeight()));
					}
				}
		}
		g.setColor(baseColor);
		g.fillRect(posX + MathUtils.round((this.cursorX * dx) * getWidth()),
				posY + MathUtils.round((this.cursorY * dy) * getHeight()),
				MathUtils.round(dx * getWidth()),
				MathUtils.round(dy * getHeight()));
		g.setFont(oldFont);
		g.setColor(oldColor);
	}

	protected void processTouchReleased() {
		super.processKeyReleased();
		this.pushEnter();
	}

	private char getArrays(int x, int y) {
		if (keyArrays.size <= x) {
			return ' ';
		}
		String result = this.keyArrays.get(x);
		if (result.length() <= y) {
			return ' ';
		}
		return result.charAt(y);
	}

	public int pushEnter() {
		if (getArrays(this.cursorY, this.cursorX) == enterFlagString) {
			if (this.name.equals("")) {
				return -2;
			}
			enterFlag = "Enter";
			return -1;
		}
		if (getArrays(this.cursorY, this.cursorX) == clearFlagString) {
			if (!this.name.equals("")) {
				this.name = this.name.substring(0, this.name.length() - 1);
			}
			enterFlag = "Clear";
		} else if (this.name.length() < maxNameString) {
			this.name += getArrays(this.cursorY, this.cursorX);
			enterFlag = "Add";
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
		this.cursorX += x;
		this.cursorY += y;
		if (this.cursorX >= this.keyArrays.get(0).length()) {
			this.cursorX = 0;
		}
		if (this.cursorX < 0) {
			this.cursorX = (this.keyArrays.get(0).length() - 1);
		}
		if (this.cursorY >= this.keyArrays.size) {
			this.cursorY = 0;
		}
		if (this.cursorY < 0) {
			this.cursorY = (this.keyArrays.size - 1);
		}
		if (getArrays(this.cursorY, this.cursorX) == '　') {
			moving(x, y);
		}
	}

	private int leftOffset, topOffset;

	public void moveCursor(float x, float y) {
		int indexX = (int) ((x - (this.getX() + leftOffset)) / MathUtils
				.round(dx * getWidth()));
		int indexY = (int) ((y - (this.getY() + topOffset)) / MathUtils
				.round(dy * getHeight()));
		if ((indexX < 0) || (indexY < 0) || (indexY >= this.keyArrays.size)
				|| (indexX >= this.keyArrays.get(0).length()))
			return;
		if (getArrays(indexY, indexX) != '　') {
			this.cursorX = indexX;
			this.cursorY = indexY;
		}
	}

	public void setSelectColor(LColor selectColor) {
		this.baseColor = selectColor;
	}

	public void setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		moveCursor(this.input.getTouchX(), this.input.getTouchY());
		draw(g, x, y);
	}

	public int getCursorX() {
		return cursorX;
	}

	public void setCursorX(int cursorX) {
		this.cursorX = cursorX;
	}

	public int getCursorY() {
		return cursorY;
	}

	public void setCursorY(int cursorY) {
		this.cursorY = cursorY;
	}

	public float getDx() {
		return dx;
	}

	public void setDx(float dx) {
		this.dx = dx;
	}

	public float getDy() {
		return dy;
	}

	public void setDy(float dy) {
		this.dy = dy;
	}

	public String getEnterFlag() {
		return enterFlag;
	}

	public String getDecideName() {
		return name;
	}

	public void setEnterFlag(String enterFlag) {
		this.enterFlag = enterFlag;
	}

	public LTexture getBgTexture() {
		return bgTexture;
	}

	public void setBgTexture(LTexture bgTexture) {
		this.bgTexture = bgTexture;
	}

	public int getMaxNameString() {
		return maxNameString;
	}

	public void setMaxNameString(int maxNameString) {
		this.maxNameString = maxNameString;
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
		this._font = font;
		return this;
	}
	
	@Override
	public IFont getFont() {
		return _font;
	}

	public float getLabelOffsetX() {
		return labelOffsetX;
	}

	public void setLabelOffsetX(float x) {
		this.labelOffsetX = x;
	}

	public float getLabelOffsetY() {
		return labelOffsetY;
	}

	public void setLabelOffsetY(float y) {
		this.labelOffsetY = y;
	}

	@Override
	public String getUIName() {
		return "DecideName";
	}

	public boolean isShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}

	@Override
	public void close() {
		super.close();
		if (bgTexture != null) {
			bgTexture.close();
		}
	}

}
