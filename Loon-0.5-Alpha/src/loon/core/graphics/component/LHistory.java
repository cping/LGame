
/**
 * Copyright 2008 - 2011
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
package loon.core.graphics.component;

import loon.LSystem;
import loon.action.sprite.SpriteButton;
import loon.action.sprite.SpriteButton.ButtonFunc;
import loon.core.event.Updateable;
import loon.core.graphics.LComponent;
import loon.core.graphics.device.LFont;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.graphics.opengl.LTextures;

public class LHistory extends LComponent {

	public boolean enabled = true;

	public boolean output = true;

	private String[] message = null;

	private int point = 0;

	public int messageLineMax = 255;

	private int lineStringNum = 0;

	private int linesNum = 0;

	public float fontSize = 20f;

	public int linespacing = 25;

	private int charCount = 0;

	boolean downFlag = false;

	int touchX = 0;

	int touchY = 0;

	public SpriteButton closeButton = null;

	public SpriteButton nextPageButton = null;

	public SpriteButton prevPageButton = null;

	public int left, top;

	private LFont font;

	public LHistory(int x, int y, int width, int height) {
		this(x, y, width, height, null);
	}

	public LHistory(int x, int y, int width, int height, Updateable update) {
		this(x, y, width, height, LFont.getDefaultFont(), update);
	}

	public LHistory(int x, int y, int width, int height, LFont font,
			Updateable update) {
		this(x, y, width, height, 32, font, update);
	}

	public LHistory(int x, int y, int width, int height, int size, LFont font,
			final Updateable update) {
		super(x, y, width, height);
		this.font = font;
		this.fontSize = font.getSize();
		this.linespacing = (int) (fontSize + 5);
		closeButton = new SpriteButton(
				LTextures.loadTexture(LSystem.FRAMEWORK_IMG_NAME
						+ "history_close.png", Format.LINEAR), 1, size, size,
				null, null, null, null, false, null,
				new SpriteButton.ButtonFunc() {

					@Override
					public void func(ButtonFunc b) {
						if (update != null) {
							update.action(b);
						}
					}
				}, 2);
		closeButton.setLocation(height - size, y);
		prevPageButton = new SpriteButton(
				LTextures.loadTexture(LSystem.FRAMEWORK_IMG_NAME
						+ "history_prev.png", Format.LINEAR), 1, size, size,
				null, null, null, null, false, null,
				new SpriteButton.ButtonFunc() {

					@Override
					public void func(ButtonFunc b) {
						prevPage();
					}
				}, 2);
		prevPageButton.setLocation(height - size, y + size );
		nextPageButton = new SpriteButton(
				LTextures.loadTexture(LSystem.FRAMEWORK_IMG_NAME
						+ "history_next.png", Format.LINEAR), 1, size, size,
				null, null, null, null, false, null,
				new SpriteButton.ButtonFunc() {

					@Override
					public void func(ButtonFunc b) {
						nextPage();
					}
				}, 2);
		nextPageButton.setLocation(height - size, y + size* 2);


		closeButton.visible = true;
		nextPageButton.visible = true;
		prevPageButton.visible = true;

		message = new String[messageLineMax];
		for (int i = 0; i < message.length; i++) {
			message[i] = null;
		}

		lineStringNum = (int) ((float) (width-size) / (float) fontSize)-1;
		linesNum = (int) ((float) (height-size) / (float) linespacing)-2;

	}

	public void nextPage() {
		for (int i = 1; i < linesNum; i++) {
			nextLine();
		}
	}

	public void prevPage() {
		for (int i = 1; i < linesNum; i++) {
			prevLine();
		}
	}

	public void nextLine() {
		point--;
		if (point < 0) {
			point = 0;
		}
	}

	public void prevLine() {
		point++;
		if (message[point] == null) {
			point--;
		}
	}

	public void onDown(int x, int y) {
		downFlag = true;
		touchX = x;
		touchY = y;

		closeButton.onDown(x, y);
		nextPageButton.onDown(x, y);
		prevPageButton.onDown(x, y);
	}

	public void onMove(int x, int y) {
		if (downFlag) {
			int moveY = Math.abs(y - touchY);
			if (moveY > 50) {
				if (y < touchY){
					nextLine();
				}
				else{
					prevLine();
				}
				touchX = x;
				touchY = y;
			}
		}

		closeButton.onMove(x, y);
		nextPageButton.onMove(x, y);
		prevPageButton.onMove(x, y);
	}

	public void onUp(int x, int y) {
		downFlag = false;

		if (closeButton.onUp(x, y)) {
			return;
		}
		if (nextPageButton.onUp(x, y)) {
			return;
		}
		if (prevPageButton.onUp(x, y)) {
			return;
		}
	}

	public void addMessage(String message) {
		if (!output) {
			return;
		}
		if (message == null) {
			return;
		}
		int len = message.length();
		if (len == 0) {
			return;
		}
		if (this.message[0] == null) {
			this.message[0] = "";
		}
		char c;
		boolean flag = true;
		for (int i = 0; i < len; i++) {
			c = message.charAt(i);
			charCount++;
			if (charCount >= lineStringNum || c == '\n') {
				cr();
			}
			if (c != '\n') {
				this.message[0] += c;
			}
		}
		for (; flag;) {
			flag = false;
		}
	}

	private void cr() {
		for (int i = message.length - 1; i > 0; i--) {
			message[i] = message[i - 1];
		}
		message[0] = "";
		charCount = 0;
	}

	public void clear() {
		for (int i = 0; i < message.length; i++) {
			message[i] = null;
		}
		charCount = 0;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		if (isVisible()) {
			int max = point + linesNum;
			LFont old = g.getFont();
			g.setFont(font);
			int nx=x-font.getSize();
			int ny=y+font.getHeight()+(int)(fontSize*2);
			for (int i = point; i < max; i++) {
				if (message[i] != null) {
					g.drawString(message[i], left + nx, top + ny);
				}
				ny -= linespacing;
			}
			g.setFont(old);
			closeButton.createUI(g);
			nextPageButton.createUI(g);
			prevPageButton.createUI(g);
		}
	}

	protected void processTouchDragged() {
		super.processTouchDragged();
		onMove(input.getTouchX(),input.getTouchY());
	}

	protected void processTouchPressed() {
		super.processTouchPressed();
		onDown(input.getTouchX(),input.getTouchY());
	}

	protected void processTouchReleased() {
		super.processTouchReleased();
		onUp(input.getTouchX(),input.getTouchY());
	}

	@Override
	public String getUIName() {
		return "History";
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

}
