package loon.component;

import java.util.ArrayList;
import java.util.List;

import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.LSTRFont;
import loon.utils.StringUtils;

public class Print implements LRelease {

	// they is other char flags
	private final static char[] _wrapchars = { '\u3002', '\u3001', '\uff0c', '\uff0e', '\u300d',
			'\uff3d', '\u3011', '\u300f', '\u30fc', '\uff5e', '\uff09',
			'\u3041', '\u3043', '\u3045', '\u3047', '\u3049', '\u30a1',
			'\u30a3', '\u30a5', '\u30a7', '\u30a9', '\u30c3', '\u30e3',
			'\u30e5', '\u30e7', '\u30ee', '\u308e', '\u3083', '\u3085',
			'\u3087', '\u3063', '\u2026', '\uff0d', '\uff01', '\uff1f' };

	private final static int _otherFlagsSize = _wrapchars.length;
			
	public static List<String> formatMessage(String text, LFont font, int width) {
		List<String> list = new ArrayList<String>();

		if (text == null) {
			return list;
		}

		char c1 = '〜';
		char c2 = 65374;
		String str = text.replace(c1, c2);
		String line = "";

		int i = 0;

		while (i <= str.length()) {
			if (i == str.length()) {
				list.add(line);
				break;
			}

			char c = str.charAt(i);

			if ((c == '\n') || (font.stringWidth(line + c) > width)) {
				line = str.substring(0, i);
				
				for (int j = 0; j < _otherFlagsSize; j++) {
					if (c == _wrapchars[j]) {
						int delta = font.stringWidth(line + c) - width;
						if (delta < 15) {
							line = str.substring(0, ++i);
							break;
						}
					}
				}
				i += (c == '\n' ? 1 : 0);
				list.add(line);
				line = "";
				str = str.substring(i);
				i = 0;
			} else {
				line = line + c;
				i++;
			}
		}
		return list;
	}

	private int index, offset, font, tmp_font;

	private int fontSizeDouble;

	private char text;

	private char[] showMessages;

	private int iconWidth;

	private LColor fontColor = LColor.white;

	private int interceptMaxString;

	private int interceptCount;

	private int messageLength = 10;

	private String messages;

	private boolean onComplete, newLine, visible;

	private StringBuffer messageBuffer = new StringBuffer(messageLength);

	private int width, height, leftOffset, topOffset, next, messageCount;

	private float alpha;

	private int size, wait, tmp_left, left, fontSize, fontHeight;

	private Vector2f vector;

	private LTexture creeseIcon;

	private LSTRFont strings;

	private boolean isEnglish, isLeft, isWait;

	private float iconX, iconY;

	private int lazyHashCade = 1;

	public Print(Vector2f vector, LFont font, int width, int height) {
		this("", font, vector, width, height);
	}

	public Print(String context, LFont font, Vector2f vector, int width,
			int height) {
		this.setMessage(context, font);
		this.vector = vector;
		this.width = width;
		this.height = height;
		this.wait = 0;
		this.isWait = false;
	}

	public void setMessage(String context, LFont font) {
		setMessage(context, font, false);
	}

	public void setMessage(String context, LFont font, boolean isComplete) {
		if (strings != null) {
			strings.close();
		}
		this.strings = new LSTRFont(font, context);
		this.lazyHashCade = 1;
		this.wait = 0;
		this.visible = false;
		this.showMessages = new char[] { '\0' };
		this.interceptMaxString = 0;
		this.next = 0;
		this.messageCount = 0;
		this.interceptCount = 0;
		this.size = 0;
		this.tmp_left = 0;
		this.left = 0;
		this.fontSize = 0;
		this.fontHeight = 0;
		this.messages = context;
		this.next = context.length();
		this.onComplete = false;
		this.newLine = false;
		this.messageCount = 0;
		this.messageBuffer.delete(0, messageBuffer.length());
		if (isComplete) {
			this.complete();
		}
		this.visible = true;
	}

	public String getMessage() {
		return messages;
	}

	private LColor getColor(char flagName) {
		if ('r' == flagName || 'R' == flagName) {
			return LColor.red;
		} else if ('b' == flagName || 'B' == flagName) {
			return LColor.black;
		} else if ('l' == flagName || 'L' == flagName) {
			return LColor.blue;
		} else if ('g' == flagName || 'G' == flagName) {
			return LColor.green;
		} else if ('o' == flagName || 'O' == flagName) {
			return LColor.orange;
		} else if ('y' == flagName || 'Y' == flagName) {
			return LColor.yellow;
		} else if ('m' == flagName || 'M' == flagName) {
			return LColor.magenta;
		} else if ('d' == flagName || 'D' == flagName) {
			return LColor.darkGray;
		} else if ('e' == flagName || 'E' == flagName) {
			return LColor.green;
		} else if ('p' == flagName || 'P' == flagName) {
			return LColor.pink;
		}
		return null;
	}

	public void draw(GLEx g) {
		draw(g, LColor.white);
	}

	private void drawMessage(GLEx gl, LColor old) {
		if (!visible) {
			return;
		}
		if (strings == null) {
			return;
		}
		synchronized (showMessages) {

			this.size = showMessages.length;
			this.fontSize = (int) (isEnglish ? strings.getSize() / 2 : gl
					.getFont().getSize());
			this.fontHeight = strings.getHeight();
			this.tmp_left = isLeft ? 0 : (width - (fontSize * messageLength))
					/ 2 - (int) (fontSize * 1.5);
			this.left = tmp_left;
			this.index = offset = font = tmp_font = 0;
			this.fontSizeDouble = fontSize * 2;

			int hashCode = 1;
			hashCode = LSystem.unite(hashCode, size);
			hashCode = LSystem.unite(hashCode, left);
			hashCode = LSystem.unite(hashCode, fontSize);
			hashCode = LSystem.unite(hashCode, fontHeight);

			if (strings == null) {
				return;
			}

			if (hashCode == lazyHashCade) {
				strings.postCharCache();
				if (iconX != 0 && iconY != 0) {
					gl.draw(creeseIcon, iconX, iconY);
				}
				return;
			}

			strings.startChar();
			fontColor = old;

			
			for (int i = 0; i < size; i++) {
				text = showMessages[i];
				if (text == '\0') {
					continue;
				}
                
				if (interceptCount < interceptMaxString) {
					interceptCount++;
					continue;
				} else {
					interceptMaxString = 0;
					interceptCount = 0;
				}
				if (showMessages[i] == 'n'
						&& showMessages[i > 0 ? i - 1 : 0] == '\\') {
					index = 0;
					left = tmp_left;
					offset++;
					continue;
				} else if (text == '\n') {
					index = 0;
					left = tmp_left;
					offset++;
					continue;
				} else if (text == '<') {
					LColor color = getColor(showMessages[i < size - 1 ? i + 1
							: i]);
					if (color != null) {
						interceptMaxString = 1;
						fontColor = color;
					}
					continue;
				} else if (showMessages[i > 0 ? i - 1 : i] == '<'
						&& getColor(text) != null) {
					continue;
				} else if (text == '/') {
					if (showMessages[i < size - 1 ? i + 1 : i] == '>') {
						interceptMaxString = 1;
						fontColor = old;
					}
					continue;
				} else if (index > messageLength) {
					index = 0;
					left = tmp_left;
					offset++;
					newLine = false;
				} else if (text == '\\') {
					continue;
				}
				tmp_font = strings.charWidth(text);
				if (Character.isLetter(text)) {
					if (tmp_font < fontSize) {
						font = fontSize;
					} else {
						font = tmp_font;
					}
				} else {
					font = fontSize;
				}
				left += font;
				if (font <= 10 && StringUtils.isSingle(text)) {
					left += 12;
				}
				if (i != size - 1) {
					strings.addChar(text, vector.x + left + leftOffset,
							(offset * fontHeight) + vector.y + fontSizeDouble
									+ topOffset, fontColor);
				} else if (!newLine && !onComplete) {
					iconX = vector.x + left + leftOffset + iconWidth;
					iconY = (offset * fontHeight) + vector.y + fontSize
							+ topOffset + strings.getAscent();
					if (iconX != 0 && iconY != 0) {
						gl.draw(creeseIcon, iconX, iconY);
					}
				}
				index++;
			}

			strings.stopChar();
			strings.saveCharCache();

			lazyHashCade = hashCode;

			if (messageCount == next) {
				onComplete = true;
			}
		}
	}

	public void draw(GLEx g, LColor old) {
		if (!visible) {
			return;
		}
		alpha = g.alpha();
		if (alpha != 1f) {
			g.setAlpha(1f);
		}
		drawMessage(g, old);
		if (alpha != 1f) {
			g.setAlpha(alpha);
		}
	}

	public void setX(int x) {
		vector.setX(x);
	}

	public void setY(int y) {
		vector.setY(y);
	}

	public int getX() {
		return vector.x();
	}

	public int getY() {
		return vector.y();
	}

	public void complete() {
		synchronized (showMessages) {
			this.onComplete = true;
			this.messageCount = messages.length();
			this.next = messageCount;
			this.showMessages = (messages + "_").toCharArray();
			this.size = showMessages.length;
		}
	}

	public boolean isComplete() {
		if (isWait) {
			if (onComplete) {
				wait++;
			}
			return onComplete && wait > 100;
		}
		return onComplete;
	}

	public boolean next() {
		synchronized (messageBuffer) {
			if (!onComplete) {
				if (messageCount == next) {
					onComplete = true;
					return false;
				}
				if (messageBuffer.length() > 0) {
					messageBuffer.delete(messageBuffer.length() - 1,
							messageBuffer.length());
				}
				this.messageBuffer.append(messages.charAt(messageCount));
				this.messageBuffer.append('_');
				this.showMessages = messageBuffer.toString().toCharArray();
				this.size = showMessages.length;
				this.messageCount++;
			} else {
				return false;
			}
			return true;
		}
	}

	public LTexture getCreeseIcon() {
		return creeseIcon;
	}

	public void setCreeseIcon(LTexture icon) {
		if (this.creeseIcon != null) {
			creeseIcon.close();
			creeseIcon = null;
		}
		this.creeseIcon = icon;
		if (icon == null) {
			return;
		}
		this.iconWidth = icon.getWidth();
	}

	public int getMessageLength() {
		return messageLength;
	}

	public void setMessageLength(int messageLength) {
		this.messageLength = messageLength;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
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

	public boolean isEnglish() {
		return isEnglish;
	}

	public void setEnglish(boolean isEnglish) {
		this.isEnglish = isEnglish;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isLeft() {
		return isLeft;
	}

	public void setLeft(boolean isLeft) {
		this.isLeft = isLeft;
	}

	public boolean isWait() {
		return isWait;
	}

	public void setWait(boolean isWait) {
		this.isWait = isWait;
	}

	@Override
	public void close() {
		if (strings != null) {
			strings.close();
			strings = null;
		}
	}

}
