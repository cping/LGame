/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.font;

import java.util.Iterator;

import loon.BaseIO;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.LTextureBatch.Cache;
import loon.canvas.LColor;
import loon.geom.Affine2f;
import loon.geom.PointI;
import loon.opengl.GLEx;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;
import loon.utils.ObjectMap.Entries;
import loon.utils.ObjectMap.Entry;
import loon.utils.StrBuilder;
import loon.utils.TArray;
import loon.utils.parse.StrTokenizer;

// AngelCode图像字体专用类(因为仅处理限定范围内的字体，此类速度会比较早前版本中提供的文字渲染类更快，
// 但缺点在于，没有提供图像的文字不能被渲染).
public final class BMFont extends FontTrans implements IFont {

	public static BMFont create(String file, String imgFile) {
		try {
			return new BMFont(file, imgFile);
		} catch (Exception e) {
			return null;
		}
	}

	public static BMFont create(String file, int fontSize) {
		try {
			return new BMFont(file, fontSize);
		} catch (Exception e) {
			return null;
		}
	}

	public static BMFont create(String file) {
		try {
			return new BMFont(file);
		} catch (Exception e) {
			return null;
		}
	}

	private static final int DEFAULT_MAX_CHAR = 256;

	private final char newLineFlag = LSystem.LF;

	private final char newSpaceFlag = LSystem.SPACE;

	private final char newTabSpaceFlag = LSystem.TAB;

	private final char newRFlag = LSystem.CR;

	private int totalCharSet = DEFAULT_MAX_CHAR;

	private int _initDraw = -1;

	private int _drawLimit = 0;

	private int _dstFontSize = -1;

	private String _texPath = null;

	private String _imagePath = null;

	private boolean _initParse = false;

	private IntMap<CharDef> customChars = new IntMap<CharDef>();

	private PointI _offset = new PointI();

	private int advanceSpace = 8;

	private int _size = -1;

	private float _ascent = -1;

	private float fontScaleX = 1f, fontScaleY = 1f;

	private IntMap<Display> displays;

	private int lazyHashCode = 1;

	private LTexture displayList;

	private CharDef[] charArray;

	private int lineHeight, halfHeight;

	private boolean _isClose;

	private String info, common, page, face, charset;

	private static class Display {

		String text;

		Cache cache;

		int width;

		int height;
	}

	private static class CharDef {

		int id;

		short tx;

		short ty;

		short width;

		short height;

		short xoffset;

		short yoffset;

		short advance;

		short[] kerning;

		BMFont _bmFont;

		public CharDef(BMFont font) {
			this._bmFont = font;
		}

		public void draw(float x, float y, LColor c) {
			if (_bmFont._isClose) {
				return;
			}
			_bmFont.displayList.draw((x + xoffset) * _bmFont.fontScaleX, (y + yoffset) * _bmFont.fontScaleY,
					width * _bmFont.fontScaleX, height * _bmFont.fontScaleY, tx, ty, tx + width, ty + height, c);
		}

		public void draw(GLEx g, float sx, float sy, float x, float y, LColor c) {
			if (_bmFont._isClose) {
				return;
			}

			g.draw(_bmFont.displayList, sx + (x + xoffset) * _bmFont.fontScaleX,
					sy + (y + yoffset) * _bmFont.fontScaleX, width * _bmFont.fontScaleX, height * _bmFont.fontScaleY,
					tx, ty, width, height, c);
		}

		public int getKerning(int point) {
			if (kerning == null) {
				return 0;
			}
			int low = 0;
			int high = kerning.length - 1;
			while (low <= high) {
				int midIndex = (low + high) >>> 1;
				int value = kerning[midIndex];
				int foundCodePoint = value & 0xff;
				if (foundCodePoint < point) {
					low = midIndex + 1;
				} else if (foundCodePoint > point) {
					high = midIndex - 1;
				} else {
					return value >> 8;
				}
			}
			return 0;
		}
	}

	public BMFont(String file, LTexture image) throws LSysException {
		this(file, image, -1);
	}

	public BMFont(String file, LTexture image, int baseFontSize) throws LSysException {
		this._imagePath = image.getSource();
		this._texPath = file;
		this._dstFontSize = baseFontSize;
		this.displayList = image;
	}

	public BMFont(String file, String imgFile) throws LSysException {
		this(file, imgFile, -1);
	}

	public BMFont(String file, String imgFile, int baseFontSize) throws LSysException {
		this._texPath = file;
		this._imagePath = imgFile;
		this._dstFontSize = baseFontSize;
	}

	public BMFont(String file) throws LSysException {
		this(file, -1);
	}

	public BMFont(String file, int baseFontSize) throws LSysException {
		this(file, LSystem.getAllFileName(file) + ".png", baseFontSize);
	}

	private void parse(String text) throws LSysException {
		if (displays == null) {
			displays = new IntMap<Display>(DEFAULT_MAX_CHAR);
		} else {
			displays.clear();
		}
		if (StringUtils.isEmpty(text)) {
			throw new LSysException("BMFont resource is null !");
		}
		StrTokenizer br = new StrTokenizer(text, LSystem.NL);
		info = br.nextToken();
		common = br.nextToken();
		page = br.nextToken();

		if (info != null && !StringUtils.isEmpty(info)) {
			int size = info.length();
			StrBuilder sbr = new StrBuilder();
			for (int i = 0; i < size; i++) {
				char ch = info.charAt(i);
				if (ch == newSpaceFlag && sbr.length() > 0) {
					String result = sbr.toString().toLowerCase().trim();
					String[] list = StringUtils.split(result, LSystem.EQUAL);
					if (list.length == 2) {
						if (list[0].equals("size")) {
							_size = (int) Float.parseFloat(list[1]);
							continue;
						} else if (list[0].equals("face")) {
							face = list[1];
							continue;
						} else if (list[1].equals("charset")) {
							charset = list[1];
							continue;
						}
					}
					sbr.setLength(0);
				}
				sbr.append(ch);
			}
		}

		ObjectMap<Short, TArray<Short>> kerning = new ObjectMap<Short, TArray<Short>>(64);
		TArray<CharDef> charDefs = new TArray<CharDef>(DEFAULT_MAX_CHAR);

		int maxChar = 0;
		boolean done = false;
		for (; !done;) {
			String line = br.hasMoreTokens() ? br.nextToken() : null;
			if (line == null) {
				done = true;
			} else {
				if (line.startsWith("chars c")) {
				} else if (line.startsWith("char")) {
					CharDef def = parseChar(line);
					if (def != null) {
						maxChar = MathUtils.max(maxChar, def.id);
						charDefs.add(def);
					}
				}
				if (line.startsWith("kernings c")) {
				} else if (line.startsWith("kerning")) {
					StrTokenizer tokens = new StrTokenizer(line, " =");
					tokens.nextToken();
					tokens.nextToken();
					short first = Short.parseShort(tokens.nextToken());
					tokens.nextToken();
					int second = Integer.parseInt(tokens.nextToken());
					tokens.nextToken();
					int offset = Integer.parseInt(tokens.nextToken());
					TArray<Short> values = kerning.get(Short.valueOf(first));
					if (values == null) {
						values = new TArray<Short>();
						kerning.put(Short.valueOf(first), values);
					}
					values.add(Short.valueOf((short) ((offset << 8) | second)));
				}
			}
		}

		this.charArray = new CharDef[totalCharSet];

		for (Iterator<CharDef> iter = charDefs.iterator(); iter.hasNext();) {
			CharDef def = iter.next();
			if (def.id < totalCharSet) {
				charArray[def.id] = def;
			} else {
				customChars.put(def.id, def);
			}
		}

		for (Entries<Short, TArray<Short>> iter = kerning.entries(); iter.hasNext();) {
			Entry<Short, TArray<Short>> entry = iter.next();
			short first = entry.key;
			TArray<Short> valueList = entry.value;
			short[] valueArray = new short[valueList.size];
			int i = 0;
			for (Iterator<Short> valueIter = valueList.iterator(); valueIter.hasNext(); i++) {
				valueArray[i] = (valueIter.next()).shortValue();
			}
			if (first < totalCharSet) {
				charArray[first].kerning = valueArray;
			} else {
				customChars.get((int) first).kerning = valueArray;
			}
		}
		this.advanceSpace = MathUtils.max(1,
				(_size == -1 ? (int) (lineHeight * this.fontScaleY) - halfHeight / 4 : _size) / 2);
		if (_dstFontSize > 0 && (_size > 0 || lineHeight > 0)) {
			if (_size > 0) {
				setFontScale(((float) _dstFontSize / _size));
			} else {
				setFontScale(((float) _dstFontSize / lineHeight));
			}
		}
		LSystem.pushFontPool(this);
	}

	private CharDef parseChar(final String line) throws LSysException {
		CharDef def = new CharDef(this);
		StrTokenizer tokens = new StrTokenizer(line, " =");
		tokens.nextToken();
		tokens.nextToken();

		def.id = Integer.parseInt(tokens.nextToken());

		if (def.id < 0) {
			return null;
		}

		tokens.nextToken();
		def.tx = Short.parseShort(tokens.nextToken());
		tokens.nextToken();
		def.ty = Short.parseShort(tokens.nextToken());
		tokens.nextToken();
		def.width = Short.parseShort(tokens.nextToken());
		tokens.nextToken();
		def.height = Short.parseShort(tokens.nextToken());
		tokens.nextToken();
		def.xoffset = Short.parseShort(tokens.nextToken());
		tokens.nextToken();
		def.yoffset = Short.parseShort(tokens.nextToken());
		tokens.nextToken();
		def.advance = Short.parseShort(tokens.nextToken());

		if (def.id != (short) newSpaceFlag) {
			lineHeight = MathUtils.max(def.height + def.yoffset, lineHeight);
			halfHeight = lineHeight >> 1;
		}

		return def;
	}

	public void drawString(String text, float x, float y) {
		drawString(text, x, y, null);
	}

	public void drawString(String text, float x, float y, LColor col) {
		drawBatchString(text, x, y, col, 0, text.length());
	}

	private void drawBatchString(String msg, float tx, float ty, LColor c, int startIndex, int endIndex) {
		if (_isClose) {
			return;
		}
		if (StringUtils.isEmpty(msg)) {
			return;
		}
		String newMessage = toMessage(msg);
		if (checkEndIndexUpdate(endIndex, msg, newMessage)) {
			endIndex = newMessage.length();
		}
		make();
		if (displayList == null || displayList.isClosed()) {
			this.displayList = BaseIO.loadTexture(_imagePath);
			return;
		}
		if (_initDraw < _drawLimit) {
			_initDraw++;
			return;
		}
		if (displays.size > DEFAULT_MAX_CHAR) {
			displays.clear();
		}

		lazyHashCode = 1;

		if (c != null) {
			lazyHashCode = LSystem.unite(lazyHashCode, c.r);
			lazyHashCode = LSystem.unite(lazyHashCode, c.g);
			lazyHashCode = LSystem.unite(lazyHashCode, c.b);
			lazyHashCode = LSystem.unite(lazyHashCode, c.a);
		}

		int keyCode = 1;
		keyCode = LSystem.unite(keyCode, newMessage.hashCode());
		keyCode = LSystem.unite(keyCode, lazyHashCode);

		Display display = displays.get(keyCode);

		if (display == null) {

			int x = 0, y = 0;

			displayList.glBegin();
			displayList.setBatchPos(tx + _offset.x, ty + _offset.y);

			if (c != null) {
				displayList.setImageColor(c);
			}

			CharDef lastCharDef = null;
			for (int i = startIndex; i < endIndex; i++) {
				char id = newMessage.charAt(i);
				if (id == newRFlag) {
					continue;
				}
				if (id == newLineFlag) {
					x = 0;
					y += lineHeight;
					continue;
				}
				if (id == newSpaceFlag) {
					x += advanceSpace;
					continue;
				}
				if (id == newTabSpaceFlag) {
					x += (advanceSpace * 3);
					continue;
				}
				CharDef charDef = null;
				if (id < totalCharSet) {
					charDef = charArray[id];
				} else {
					charDef = customChars.get(id);
				}
				if (charDef == null) {
					continue;
				}
				if (lastCharDef != null) {
					x += lastCharDef.getKerning(id);
				}

				lastCharDef = charDef;
				charDef.draw(x, y, c);
				x += charDef.advance;
			}

			if (c != null) {
				displayList.setImageColor(LColor.white);
			}

			displayList.glEnd();

			display = new Display();

			display.cache = displayList.newBatchCache();
			display.text = newMessage;
			display.width = 0;
			display.height = 0;

			displays.put(keyCode, display);

		} else if (display.cache != null) {
			display.cache.x = tx + _offset.x;
			display.cache.y = ty + _offset.y;
			displayList.postCache(display.cache);
		}

	}

	@Override
	public void drawString(GLEx g, String text, float x, float y) {
		drawString(g, text, x, y, null);
	}

	@Override
	public void drawString(GLEx g, String text, float x, float y, LColor col) {
		drawString(g, text, x, y, col, 0, text.length());
	}

	private void make() {
		if (!_initParse) {
			try {
				this.parse(BaseIO.loadText(_texPath));
			} catch (Throwable e) {
				LSystem.error("BMFont error !", e);
			}
			_initParse = true;
		}
	}

	private void drawString(GLEx g, String msg, float tx, float ty, LColor c, int startIndex, int endIndex) {
		if (_isClose) {
			return;
		}
		if (StringUtils.isEmpty(msg)) {
			return;
		}
		String newMessage = toMessage(msg);
		if (checkEndIndexUpdate(endIndex, msg, newMessage)) {
			endIndex = newMessage.length();
		}
		make();
		if (displayList == null || displayList.isClosed()) {
			this.displayList = BaseIO.loadTexture(_imagePath);
			return;
		}
		if (_initDraw < _drawLimit) {
			_initDraw++;
			return;
		}
		int x = 0, y = 0;
		CharDef lastCharDef = null;
		for (int i = startIndex; i < endIndex; i++) {
			char id = newMessage.charAt(i);
			if (id == newRFlag) {
				continue;
			}
			if (id == newLineFlag) {
				x = 0;
				y += lineHeight;
				continue;
			}
			if (id == newSpaceFlag) {
				x += advanceSpace;
				continue;
			}
			if (id == newTabSpaceFlag) {
				x += (advanceSpace * 3);
				continue;
			}
			CharDef charDef = null;
			if (id < totalCharSet) {
				charDef = charArray[id];
			} else {
				charDef = customChars.get(id);
			}
			if (charDef == null) {
				continue;
			}
			if (lastCharDef != null) {
				x += lastCharDef.getKerning(id);
			}
			lastCharDef = charDef;
			charDef.draw(g, tx + _offset.x, ty + _offset.y, x, y, c);
			x += charDef.advance;
		}
	}

	@Override
	public void drawString(GLEx g, String text, float x, float y, float rotation, LColor c) {
		if (_isClose) {
			return;
		}
		if (StringUtils.isEmpty(text)) {
			return;
		}
		if (rotation == 0) {
			drawString(g, text, x, y, c);
			return;
		}
		try {
			g.saveTx();
			float centerX = x + stringWidth(text) / 2;
			float centerY = y + stringHeight(text) / 2;
			g.rotate(centerX, centerY, rotation);
			drawString(g, text, x, y, c);
		} finally {
			g.restoreTx();
		}
	}

	@Override
	public void drawString(GLEx gl, String msg, float x, float y, float sx, float sy, float ax, float ay,
			float rotation, LColor c) {
		if (_isClose) {
			return;
		}
		if (StringUtils.isEmpty(msg)) {
			return;
		}
		final String newMessage = toMessage(msg);
		boolean anchor = ax != 0 || ay != 0;
		boolean scale = sx != 1f || sy != 1f;
		boolean angle = rotation != 0;
		boolean update = scale || angle || anchor;
		try {
			if (update) {
				gl.saveTx();
				Affine2f xf = gl.tx();
				if (angle) {
					float centerX = x + this.stringWidth(newMessage) / 2;
					float centerY = y + this.stringHeight(newMessage) / 2;
					xf.translate(centerX, centerY);
					xf.preRotate(rotation);
					xf.translate(-centerX, -centerY);
				}
				if (scale) {
					float centerX = x + this.stringWidth(newMessage) / 2;
					float centerY = y + this.stringHeight(newMessage) / 2;
					xf.translate(centerX, centerY);
					xf.preScale(sx, sy);
					xf.translate(-centerX, -centerY);
				}
				if (anchor) {
					xf.translate(ax, ay);
				}
			}
			drawString(gl, newMessage, x, y, c);
		} finally {
			if (update) {
				gl.restoreTx();
			}
		}
	}

	@Override
	public int stringHeight(String msg) {
		if (StringUtils.isEmpty(msg)) {
			return 0;
		}
		final String newMessage = toMessage(msg);
		make();
		Display display = null;
		for (Display d : displays.values()) {
			if (d != null && newMessage.equals(d.text)) {
				display = d;
				break;
			}
		}
		if (display != null && display.height != 0) {
			return display.height;
		}
		if (display == null) {
			display = new Display();
		}
		int lines = 0;
		for (int i = 0; i < newMessage.length(); i++) {
			int id = newMessage.charAt(i);
			if (id == newLineFlag) {
				lines++;
				display.height = 0;
				continue;
			}
			if (id == newSpaceFlag) {
				continue;
			}
			CharDef charDef = null;
			if (id < totalCharSet) {
				charDef = charArray[id];
			} else {
				charDef = customChars.get(id);
			}
			if (charDef == null) {
				continue;
			}
			display.height = MathUtils.max(charDef.height + charDef.yoffset, display.height);
		}
		display.height += lines * lineHeight;
		return (int) (display.height * fontScaleY);
	}

	@Override
	public int charWidth(char c) {
		if (c == newLineFlag) {
			return 0;
		}
		make();
		CharDef charDef = null;
		if (c < totalCharSet) {
			charDef = charArray[(int) c];
		} else {
			charDef = customChars.get((int) c);
		}
		if (charDef == null) {
			return getSize();
		}
		return charDef.width;
	}

	@Override
	public int stringWidth(String msg) {
		if (StringUtils.isEmpty(msg)) {
			return 0;
		}
		make();
		final String newMessage = toMessage(msg);
		Display display = null;
		for (Display d : displays.values()) {
			if (d != null && newMessage.equals(d.text)) {
				display = d;
				break;
			}
		}
		if (display != null && display.width != 0) {
			return display.width;
		}
		if (display == null) {
			display = new Display();
		}
		int width = 0;
		CharDef lastCharDef = null;
		for (int i = 0, n = newMessage.length(); i < n; i++) {
			int id = newMessage.charAt(i);
			if (id == newLineFlag) {
				width = 0;
				continue;
			}
			CharDef charDef = null;
			if (id < totalCharSet) {
				charDef = charArray[id];
			} else {
				charDef = customChars.get(id);
			}
			if (charDef == null) {
				continue;
			}
			if (lastCharDef != null) {
				width += lastCharDef.getKerning(id);
			}
			lastCharDef = charDef;
			if (i < n - 1) {
				width += charDef.advance;
			} else {
				width += charDef.width;
			}
			display.width = MathUtils.max(display.width, width);
		}

		return (int) (display.width * fontScaleX);
	}

	public String getCommon() {
		return common;
	}

	public String getInfo() {
		return info;
	}

	public String getPage() {
		return page;
	}

	@Override
	public int getHeight() {
		make();
		return (int) (lineHeight * fontScaleY) - halfHeight;
	}

	public float getFontScaleX() {
		return this.fontScaleX;
	}

	public float getFontScaleY() {
		return this.fontScaleX;
	}

	public void setFontScale(float s) {
		this.setFontScale(s, s);
	}

	public void setFontScale(float sx, float sy) {
		this.fontScaleX = sx;
		this.fontScaleY = sy;
	}

	public void setFontScaleX(float x) {
		this.fontScaleX = x;
	}

	public void setFontScaleY(float y) {
		this.fontScaleY = y;
	}

	@Override
	public float getAscent() {
		make();
		return _ascent == -1 ? (int) (lineHeight * this.fontScaleY) - halfHeight / 3 : _ascent;
	}

	@Override
	public int getSize() {
		make();
		return _size == -1 ? (int) (lineHeight * this.fontScaleY) - halfHeight / 4 : _size;
	}

	@Override
	public String confineLength(String msg, int width) {
		String newMessage = toMessage(msg);
		int length = 0;
		for (int i = 0; i < newMessage.length(); i++) {
			length += stringWidth(String.valueOf(newMessage.charAt(i)));
			if (length >= width) {
				int pLength = stringWidth("...");
				while (length + pLength >= width && i >= 0) {
					length -= stringWidth(String.valueOf(newMessage.charAt(i)));
					i--;
				}
				newMessage = newMessage.substring(0, ++i) + "...";
				break;
			}
		}
		return newMessage;
	}

	@Override
	public PointI getOffset() {
		return _offset;
	}

	@Override
	public void setOffset(PointI val) {
		_offset.set(val);
	}

	@Override
	public void setOffsetX(int x) {
		_offset.x = x;
	}

	@Override
	public void setOffsetY(int y) {
		_offset.y = y;
	}

	@Override
	public void setAssent(float assent) {
		this._ascent = assent;
	}

	@Override
	public void setSize(int size) {
		this._size = size;
	}

	public String getFace() {
		return face;
	}

	public String getCharset() {
		return charset;
	}

	@Override
	public String getFontName() {
		return getFace();
	}

	@Override
	public ITranslator getTranslator() {
		return _translator;
	}

	@Override
	public IFont setTranslator(ITranslator translator) {
		this._translator = translator;
		return this;
	}

	public int getDrawLimit() {
		return _drawLimit;
	}

	public BMFont setDrawLimit(int d) {
		this._drawLimit = d;
		return this;
	}

	public boolean isClosed() {
		return _isClose;
	}

	@Override
	public void close() {
		if (_isClose) {
			return;
		}
		this._isClose = true;
		if (displayList != null) {
			displayList.close(true);
			displayList = null;
		}
		if (displays != null) {
			for (Display d : displays.values()) {
				if (d != null && d.cache != null) {
					d.cache.close();
				}
			}
			displays.clear();
		}
		_initDraw = -1;
		_initParse = false;
		LSystem.popFontPool(this);
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("BMFont");
		builder.kv("info", info).comma().kv("common", common).comma().kv("page", page);
		return builder.toString();
	}

}
