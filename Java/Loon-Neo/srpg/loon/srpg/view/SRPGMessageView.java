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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
package loon.srpg.view;

import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.Screen;
import loon.canvas.LColor;
import loon.canvas.LGradation;
import loon.font.IFont;
import loon.font.LFont;
import loon.opengl.GLEx;

public class SRPGMessageView extends SRPGView implements LRelease {

	private SRPGMessageListener listener;

	private int max, chara_x, chara_y;

	private SRPGMessage[] message;

	private int index;

	private long delay;

	private boolean isLock;

	private LTexture face, background, character;

	public SRPGMessageView(String[] mes, int languageIndex, LFont font, int x,
			int y, int w, int h, int offsetLeft, int offsetTop) {
		this.set(mes, languageIndex, font, x, y, w, h, offsetLeft, offsetTop,
				false);
	}

	public SRPGMessageView(String mes, int languageIndex, LFont font, int x,
			int y, int w, int h, int offsetLeft, int offsetTop) {
		this.set(mes, languageIndex, font, x, y, w, h, offsetLeft, offsetTop);
	}

	public SRPGMessageView(SRPGMessage[] mes) {
		this.message = mes;
		this.max = mes.length;
		this.reset();
	}

	public SRPGMessageView() {
		this.index = 0;
		this.max = 0;
		this.delay = 30;
		super.exist = false;
	}

	@Override
	public void close() {
		if (message != null) {
			for (int i = 0; i < message.length; i++) {
				message[i] = null;
			}
			message = null;
		}
	}

	public void set(String[] mes, int languageIndex, LFont font, int x, int y,
			int w, int h, int offsetLeft, int offsetTop, boolean isLeft) {
		this.max = mes.length;
		this.message = new SRPGMessage[max];

		boolean isEnglish = (languageIndex == 3);
	
		for (int i = 0; i < max; i++) {
			message[i] = new SRPGMessage(mes[i], font, x, y, w, h);
			message[i].setEnglish(isEnglish);
			message[i].setDelay(100);
			if (isEnglish) {
				message[i].setMessageLength(font.stringWidth(mes[i])
						/ mes[i].length() * 4);
			} else if (languageIndex == 0 || languageIndex == 1) {
				message[i].setMessageLength(font.stringWidth(mes[i])
						/ mes[i].length());
			} else {
				message[i].setMessageLength(font.stringWidth(mes[i])
						/ mes[i].length() * 2 - 2);
			}
			message[i].center();
			if (isLeft) {
				if (!isEnglish) {
					if (languageIndex == 0 || languageIndex == 1) {
						message[i].setMessageLength(message[i]
								.getMessageLength() - 1);
					} else {
						message[i].setMessageLength(font.stringWidth(mes[i])
								/ mes[i].length() - 1);
						message[i].setLeftOffset(10);
					}
				}
			}
			if (offsetLeft != 0) {
				message[i].setLeftOffset(offsetLeft);
			}
			if (offsetTop != 0) {
				message[i].setTopOffset(offsetTop);
			}
		}
		this.reset();
	}

	public void set(String mes, int languageIndex, LFont font, int x, int y,
			int w, int h, int offsetLeft, int offsetTop) {
		this.set(new String[] { mes }, languageIndex, font, x, y, w, h,
				offsetLeft, offsetTop, false);
	}

	public void reset() {
		this.delay = 30;
		this.index = 0;
		this.isLock = false;
		super.exist = true;
	}

	public boolean messageExist() {
		return max > index;
	}

	@Override
	public boolean isExist() {
		return super.exist;
	}

	public void setX(int x) {
		message().setX(x);
	}

	public void setY(int y) {
		message().setY(y);
	}

	public int getX() {
		return message().getX();
	}

	public int getY() {
		return message().getY();
	}

	public void setLock(boolean flag) {
		this.isLock = flag;
	}

	public boolean isLock() {
		if (isExist()) {
			return isLock;
		} else {
			return false;
		}
	}

	public SRPGMessage getMessage(int i) {
		if (max <= i) {
			i = max - 1;
		}
		if (message != null) {
			return message[i];
		} else {
			return null;
		}
	}

	public void setIndex(int i) {
		index = i;
	}

	public int getIndex() {
		int i = index;
		if (max <= i) {
			i = max - 1;
		}
		return i;
	}

	public LFont getFont() {
		return getMessage(getIndex()).getFont();
	}

	public void setMessageLength(int i) {
		getMessage(getIndex()).setMessageLength(i);
	}

	public void nextIndex() {
		this.index++;
	}

	public int getWidth() {
		return getMessage(getIndex()).getWidth();
	}

	public int getHeight() {
		return getMessage(getIndex()).getHeight();
	}

	public void setBackground(String fileName) {
		setBackground(LTexture.createTexture(fileName));
	}

	public void setBackground(LTexture img) {
		if (img == background) {
			return;
		}
		if (background != null) {
			background.close();
			background = null;
		}
		this.background = img;
	}

	public LTexture getBackground() {
		return background;
	}

	public void setCharacterImage(String fileName, int x, int y) {
		setCharacterImage(LTexture.createTexture(fileName), x, y);
	}

	public void setCharacterImage(LTexture img, int x, int y) {
		if (img == character) {
			return;
		}
		if (character != null) {
			character.close();
			character = null;
		}
		this.character = img;
		this.setCharacterLocation(x, y);
	}

	public void setCharacterImage(String fileName) {
		setCharacterImage(LTexture.createTexture(fileName));
	}

	public void setCharacterImage(LTexture img) {
		this.character = img;
	}

	public void setCharacterLocation(int x, int y) {
		this.chara_x = x;
		this.chara_y = y;
	}

	public void setFaceImage(String fileName) {
		setFaceImage( LTexture.createTexture(fileName));
	}

	public void setFaceImage(LTexture img) {
		if (img == face) {
			return;
		}
		if (face != null) {
			face.close();
			face = null;
		}
		this.face = img;
	}

	public synchronized void draw(GLEx g) {
		if (isExist()) {
			int i = index;
			if (max <= i) {
				i = max - 1;
			}
			SRPGMessage mes = getMessage(i);
			if (listener != null) {
				listener.drawBackground(i, g);
			}
			if (character != null) {
				g.draw(character, chara_x, chara_y);
			}
			IFont font = g.getFont();
			if (face != null) {
				int w = face.getWidth();
				int h = face.getHeight();
				int x = mes.getX();
				int y = mes.getY() - h;
				g.setColor(LColor.black);
				g.fillRect(x, y, w, h);
				g.draw(face, x, y);
			}
			g.setFont(mes.getFont());
			if (background == null) {
				LGradation.create(LColor.white, LColor.black,
						mes.getWidth(), mes.getHeight()).drawHeight(g,
						mes.getX(), mes.getY());
				g.setColor(LColor.black);
				g.drawRect(mes.getX(), mes.getY(), mes.getWidth(), mes
						.getHeight());
			} else {
				g.draw(background, mes.getX(), mes.getY());
			}
			mes.update(delay);
			g.setColor(LColor.white);
			mes.draw(g);
			if (listener != null) {
				listener.drawForeground(i, g);
			}
			g.setFont(font);
			g.resetColor();
		}
	}

	public boolean isRunning() {
		return isLock() && maxCheck();
	}

	public void next() {
		nextIndex();
		boolean exist = listener != null;
		if (exist) {
			listener.next(getMessage(getIndex()));
		}
		if (!messageExist()) {
			setExist(false);
			if (exist) {
				listener.close();
			}
		}
	}

	public boolean maxCheck() {
		SRPGMessage mes = getMessage(getIndex());
		if (mes == null) {
			return true;
		} else {
			return mes.isComplete();
		}
	}

	/**
	 * 代码有待替换
	 * 
	 * @deprecated 
	 * @param screen
	 */
	public void messageWait() {
		this.isLock = true;
		this.viewWait();
		this.setExist(false);
	}

	/**
	 * 代码有待替换
	 * 
	 * @deprecated 
	 * @param screen
	 */
	public void messageWait(Screen screen) {
		this.isLock = true;
		this.viewWait(screen);
		this.setExist(false);
	}

	/**
	 * 代码有待替换
	 * 
	 * @deprecated
	 * @param screen
	 */
	public void printWait(Screen screen) {
		this.isLock = true;
		for (int i = 0; i < max; i++) {
			for (; !maxCheck();) {
				setDelay(screen.elapsedTime);
				try {
					screen.wait(LSystem.SECOND);
				} catch (InterruptedException e) {
				}
			}
			next();
			setDelay(screen.elapsedTime);
			try {
				screen.wait(LSystem.SECOND);
			} catch (InterruptedException e) {
			}
		}
		setExist(false);
		if (exist) {
			listener.close();
		}
	}

	public SRPGMessageListener getListener() {
		return listener;
	}

	public void setListener(SRPGMessageListener l) {
		this.listener = l;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public SRPGMessage message() {
		return getMessage(getIndex());
	}

	public boolean getCacheExist() {
		return !messageExist();
	}
	

}
