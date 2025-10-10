/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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

import loon.BaseIO;
import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.LTextureBatch.Cache;
import loon.canvas.Canvas;
import loon.canvas.LColor;
import loon.canvas.Pixmap;
import loon.events.Updateable;
import loon.geom.Affine2f;
import loon.geom.PointF;
import loon.geom.PointI;
import loon.geom.RectF;
import loon.geom.RectI;
import loon.opengl.GLEx;
import loon.opengl.LSTRFont;
import loon.utils.CharArray;
import loon.utils.CharIterator;
import loon.utils.CharUtils;
import loon.utils.IntMap;
import loon.utils.LIterator;
import loon.utils.MathUtils;
import loon.utils.OrderedSet;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.parse.StrTokenizer;

/**
 * Adobe的DBF格式字体文件支持(主要是给C#版monogame环境用的,本地字库默认没法调用,除非调用本地api,问题是不想自己写环境适配才用的monogame……)
 */
public final class BDFont extends FontTrans implements IFont, LRelease {

	public static BDFont create(String path) {
		try {
			return new BDFont(path);
		} catch (Exception e) {
			return null;
		}
	}

	public static BDFont create(String file, int size) {
		try {
			return new BDFont(file, size);
		} catch (Exception e) {
			return null;
		}
	}

	public static BDFont create(String file, int size, String message) {
		try {
			return new BDFont(file, size, message);
		} catch (Exception e) {
			return null;
		}
	}

	public final static class BDFGlyph implements LRelease {

		IntMap<Pixmap> pixmaps;
		protected byte[][] glyph;
		protected int x, y;
		protected int advance;
		protected int encoding;
		protected RectI bbx = new RectI();

		public BDFGlyph(IntMap<Pixmap> pixs) {
			this.pixmaps = pixs;
			this.glyph = new byte[0][0];
			x = 0;
			y = 0;
			advance = 0;
		}

		public BDFGlyph(final byte[][] glyph, IntMap<Pixmap> pixs) {
			this.pixmaps = pixs;
			this.glyph = glyph;
			x = 0;
			y = glyph.length;
			advance = (glyph.length < 1) ? 0 : (glyph[0].length);
		}

		public BDFGlyph(final byte[][] glyph, IntMap<Pixmap> pixs, int offset, int width, int ascent) {
			this.pixmaps = pixs;
			this.glyph = glyph;
			x = offset;
			y = ascent;
			advance = width;
		}

		public byte[][] getGlyph() {
			return glyph;
		}

		public BDFGlyph setGlyph(final byte[][] glyph) {
			this.glyph = glyph;
			return this;
		}

		public BDFGlyph setBBX(int x, int y, int w, int h) {
			bbx.set(x, y, w, h);
			return this;
		}

		public RectI getBBX() {
			return bbx;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public BDFGlyph set(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public int getGlyphWidth() {
			return (glyph.length < 1) ? 0 : (glyph[0].length);
		}

		public int getGlyphHeight() {
			return glyph.length;
		}

		public int getGlyphOffset() {
			return x;
		}

		public int getGlyphAscent() {
			return y;
		}

		public int getGlyphDescent() {
			return glyph.length - y;
		}

		public int getCharacterWidth() {
			return MathUtils.max(bbx.x, advance);
		}

		public int getCharacterHeight() {
			return MathUtils.max(bbx.y, advance);
		}

		public BDFGlyph setCharacterWidth(int v) {
			advance = v;
			return this;
		}

		public BDFGlyph setCharacterWidth(float v) {
			advance = MathUtils.ceil(v);
			return this;
		}

		public float paint(Canvas g, float x, float y, float scale) {
			return paint(g, x, y, scale, LColor.DEF_COLOR);
		}

		public float paint(Canvas g, float x, float y, float scale, int color) {
			final int w = ((glyph.length < 1) ? 0 : (glyph[0].length));
			final int h = glyph.length;
			final int key = w * h;
			if (key <= 0) {
				return 0f;
			}
			Pixmap pixmap = pixmaps.get(key);
			if (pixmap == null || (pixmap.getWidth() != w || pixmap.getHeight() != h)) {
				if (pixmap != null) {
					pixmap.close();
					pixmap = null;
				}
				pixmap = new Pixmap(w, h);
			} else {
				pixmap.clear();
			}
			for (int j = 0; j < h; j++) {
				for (int i = 0; i < w; i++) {
					pixmap.set(i, j, LColor.combine(glyph[j][i], color));
				}
			}
			final int dx = MathUtils.ifloor(x + this.x * scale);
			final int dy = MathUtils.ifloor(y - this.y * scale - 1);
			final int dw = MathUtils.ifloor(w * scale);
			final int dh = MathUtils.ifloor(h * scale);
			g.draw(pixmap.getImage(), dx, dy, dx + dw, dy + dh, 0, 0, w, h);
			pixmaps.put(key, pixmap);
			return advance * scale;
		}

		public byte getPixel(int x, int y) {
			final byte[][] data = getGlyph();
			int ix = x - getX();
			int iy = y + getY();
			if (iy >= 0 && iy < data.length) {
				if (ix >= 0 && ix < data[iy].length) {
					return data[iy][ix];
				}
			}
			return 0;
		}

		public void contract() {
			final byte[][] data = getGlyph();
			if (data.length == 0) {
				set(0, 0);
				return;
			}
			int gx = getX();
			int gy = getY();
			int gw = data[0].length;
			int gh = data.length;
			int cx1 = gx;
			int cy1 = -gy;
			int cx2 = gx + gw;
			int cy2 = -gy + gh;
			while (cy2 > cy1 && rowEmpty(data, cy2 + gy - 1))
				cy2--;
			while (cy1 < cy2 && rowEmpty(data, cy1 + gy))
				cy1++;
			while (cx2 > cx1 && colEmpty(data, cx2 - gx - 1))
				cx2--;
			while (cx1 < cx2 && colEmpty(data, cx1 - gx))
				cx1++;
			if (cx2 == cx1 || cy2 == cy1) {
				set(0, 0);
				setGlyph(new byte[0][0]);
				return;
			}
			if (cx1 != gx || cy1 != -gy || cx2 != gx + gw || cy2 != -gy + gh) {
				int cw = cx2 - cx1;
				int ch = cy2 - cy1;
				byte[][] newData = new byte[ch][cw];
				for (int dy = 0, sy = cy1 + gy; dy < ch; dy++, sy++) {
					for (int dx = 0, sx = cx1 - gx; dx < cw; dx++, sx++) {
						newData[dy][dx] = data[sy][sx];
					}
				}
				set(cx1, -cy1);
				setGlyph(newData);
			}
		}

		private static boolean rowEmpty(byte[][] a, int row) {
			for (byte b : a[row]) {
				if (b != 0) {
					return false;
				}
			}
			return true;
		}

		private static boolean colEmpty(byte[][] a, int col) {
			for (byte[] b : a) {
				if (b[col] != 0) {
					return false;
				}
			}
			return true;
		}

		@Override
		public void close() {
			glyph = null;
		}
	}

	public final class CharRect extends RectF {

		public Character name;

		public LColor color;

	}

	private final static class IntObject {

		public int width;

		public int height;

		public int storedX;

		public int storedY;

	}

	private void putChildChars(Character ch, float x, float y, float w, float h, LColor c) {
		if (_childChars == null) {
			_childChars = new TArray<CharRect>();
		}
		CharRect obj = new CharRect();
		obj.name = ch;
		obj.x = x;
		obj.y = y;
		obj.width = w;
		obj.height = h;
		obj.color = c;
		_childChars.add(obj);
	}

	private final static class UpdateFont implements Updateable {

		private final IntMap<Canvas> _fontCanvasList = new IntMap<Canvas>();

		private BDFont strfont;

		public UpdateFont(BDFont strf) {
			this.strfont = strf;
		}

		public final void clearFontCanvasLazy() {
			if (_fontCanvasList.size == 0) {
				return;
			}
			for (Canvas canvas : _fontCanvasList.values()) {
				if (canvas != null) {
					if (canvas.getImage() != null) {
						canvas.getImage().close();
					}
					canvas.close();
					canvas = null;
				}
			}
			_fontCanvasList.clear();
		}

		public final Canvas createFontCanvas(float w, float h) {
			final int cacheSize = _fontCanvasList.size;
			if (cacheSize > LSystem.DEFAULT_MAX_CACHE_SIZE) {
				clearFontCanvasLazy();
			}
			int keyFlag = 1;
			keyFlag = LSystem.unite(keyFlag, w);
			keyFlag = LSystem.unite(keyFlag, h);
			Canvas canvas = _fontCanvasList.get(keyFlag);
			if (canvas == null || canvas.getImage() == null || canvas.getImage().isClosed()) {
				canvas = LSystem.base().graphics().createCanvas(w, h);
				_fontCanvasList.put(keyFlag, canvas);
			}
			return canvas;
		}

		@Override
		public void action(Object a) {
			if (strfont._isClose) {
				return;
			}
			strfont.loadFont();
			strfont.expandTexture();

			if (strfont.textureWidth > strfont._maxTextureWidth || strfont.textureHeight > strfont._maxTextureHeight) {
				strfont._outBounds = true;
			}
			Canvas canvas = createFontCanvas(strfont.textureWidth, strfont.textureHeight);
			canvas.setFillColor(strfont._pixelColor);
			canvas.clear();

			int rowHeight = 0;
			int positionX = 0;
			int positionY = 0;
			final int customCharsLength = (strfont.additionalChars != null) ? strfont.additionalChars.length : 0;
			StrBuilder sbr = new StrBuilder(customCharsLength);

			final OrderedSet<Character> outchached = new OrderedSet<Character>();
			for (int i = 0, size = customCharsLength; i < size; i++) {

				boolean outchar = false;

				char ch = strfont.additionalChars[i];

				if (StringUtils.isWhitespace(ch)) {
					continue;
				}

				final BDFGlyph g = strfont.getCharacter(ch);
				if (g == null) {
					continue;
				}
				int charwidth = MathUtils.abs(g.getCharacterWidth() - g.bbx.width);

				int offset = 0;
				if (CharUtils.isCJK(ch) || StringUtils.isAlphaOrDigit(ch)) {
					charwidth = MathUtils.iceil(charwidth) + (offset = 1);
				} else if (CharUtils.isFullChar(ch) || CharUtils.isHalfChar(ch)) {
					charwidth = MathUtils.iceil(charwidth) + (offset = (charwidth + g.bbx.x + g.bbx.width));
				} else {
					charwidth = MathUtils.iceil(charwidth) + (offset = 2);
				}

				int charheight = strfont.getHeight();

				if (charheight <= 0) {
					charheight = MathUtils.iceil(strfont.getPixelFontSize());
				}

				final IntObject newIntObject = new IntObject();

				newIntObject.width = charwidth;
				newIntObject.height = charheight;

				if (positionY <= strfont.textureHeight - newIntObject.height
						&& positionX <= strfont.textureWidth - newIntObject.width) {
					strfont.drawAlphabet(canvas, ch, positionX, positionY);
				} else {
					outchached.add(ch);
					strfont._outBounds = true;
					outchar = true;
				}
				if (positionX + newIntObject.width >= strfont.textureWidth) {
					positionX = 0;
					positionY += rowHeight;
					rowHeight = 0;
				}

				newIntObject.storedX = positionX;
				newIntObject.storedY = positionY;

				if (newIntObject.height < strfont._fontHeight) {
					newIntObject.height = (int) strfont._fontHeight;
				}
				if (newIntObject.height - 1 <= strfont.getPixelFontSize()) {
					newIntObject.height += 1;
				}
				if (newIntObject.height > rowHeight) {
					rowHeight = newIntObject.height + 1;
				}

				positionX += newIntObject.width + offset;

				strfont.customChars.put(ch, newIntObject);

				if (!outchar) {
					strfont._chars.add(ch);
				}
			}

			if (sbr.length() > 0) {
				if (positionY <= strfont.textureHeight - strfont.getPixelFontSize()) {
					strfont.draw(canvas, sbr.toString(), 0, positionY);
				} else {
					for (int i = 0; i < sbr.length(); i++) {
						outchached.add(sbr.charAt(i));
					}
					strfont._outBounds = true;
				}
				sbr = null;

			}

			strfont.displayList = canvas.toTexture();
			// 若字符串超过当前纹理大小,则创建新纹理保存
			if (strfont._outBounds) {
				StrBuilder temp = new StrBuilder(outchached.size());
				for (LIterator<Character> it = outchached.iterator(); it.hasNext();) {
					temp.append(it.next());
				}
				strfont._childFont = new BDFont(strfont._path, strfont._fontIndex, strfont._pixelSize,
						temp.toString().toCharArray(), strfont.isasyn, strfont.textureWidth, strfont.textureHeight,
						strfont._maxTextureWidth, strfont._maxTextureHeight);
				strfont._childFont.cpy(strfont);
			}
			if (positionX > strfont.textureWidth || positionY > strfont.textureHeight) {
				strfont._outBounds = true;
			}
			strfont._initChars = true;
			strfont.isDrawing = false;
		}

	}

	protected static final int NAME_COPYRIGHT = 0;
	protected static final int NAME_FAMILY = 1;
	protected static final int NAME_STYLE = 2;
	protected static final int NAME_UNIQUE_ID = 3;
	protected static final int NAME_FAMILY_AND_STYLE = 4;
	protected static final int NAME_VERSION = 5;
	protected static final int NAME_POSTSCRIPT = 6;
	protected static final int NAME_TRADEMARK = 7;
	protected static final int NAME_MANUFACTURER = 8;
	protected static final int NAME_DESIGNER = 9;
	protected static final int NAME_DESCRIPTION = 10;
	protected static final int NAME_VENDOR_URL = 11;
	protected static final int NAME_DESIGNER_URL = 12;
	protected static final int NAME_LICENSE_DESCRIPTION = 13;
	protected static final int NAME_LICENSE_URL = 14;
	protected static final int NAME_WINDOWS_FAMILY = 16;
	protected static final int NAME_WINDOWS_STYLE = 17;
	protected static final int NAME_MACOS_FAMILY_AND_STYLE = 18;
	protected static final int NAME_SAMPLE_TEXT = 19;
	protected static final int NAME_POSTSCRIPT_CID = 20;
	protected static final int NAME_WWS_FAMILY = 21;
	protected static final int NAME_WWS_STYLE = 22;

	private final IntMap<Pixmap> bdPixmapList = new IntMap<Pixmap>();

	private Updateable _submitUpdate;

	private final static int defaultPixelMinFontSize = 12;

	private PointF _rectPoint = new PointF();

	private PointI _offset;

	private IntMap<String> _names = new IntMap<String>();

	private IntMap<BDFGlyph> _characters = new IntMap<BDFGlyph>();

	private String _fontVersionName;

	private String _encoding;

	private float _pixelSize, _pixelFontSize;

	private float _ascent, _descent;

	private float _typoascent, _typodescent;

	private float _xheight, _fontHeight, _linegap;

	private int _pixelColor = LColor.DEF_COLOR;

	private String _path;

	private boolean _isLoading, _isLoaded;

	private int _fontIndex = 0;

	private final char newLineFlag = LSystem.LF;

	private final char newSpaceFlag = LSystem.SPACE;

	private final char newTabSpaceFlag = LSystem.TAB;

	private final char newRFlag = LSystem.CR;

	private final CharArray _chars;

	private final int _maxTextureWidth;

	private final int _maxTextureHeight;

	private boolean _isClose = false;

	private boolean _outBounds = false;

	private boolean _displayLazy = false;

	private BDFont _childFont = null;

	private int _initDraw = -1;

	private int _drawLimit = 0;

	private int textureWidth = 512;

	private int textureHeight = 512;

	private LTexture displayList;

	private boolean isDrawing, isasyn;

	private float _fontScale = 1f, _scalePixelFont = 1f;

	private final IntMap<Cache> displays;

	private int totalCharSet = 256;

	private IntMap<IntObject> customChars = new IntMap<IntObject>();

	private String text;

	private IntObject intObject;

	private int charCurrent;

	private int totalWidth = 0, totalHeight = 0;

	private boolean _initChars = false;

	private char[] additionalChars = null;

	private TArray<CharRect> _childChars;

	public BDFont(String path) {
		this(path, defaultPixelMinFontSize);
	}

	public BDFont(String path, float fontSize) {
		this(path, 0, fontSize);
	}

	public BDFont(String path, int idx, float fontSize) {
		this(path, idx, fontSize, (char[]) null);
	}

	public BDFont(String path, String message) {
		this(path, 0, defaultPixelMinFontSize, message);
	}

	public BDFont(String path, float fontSize, String message) {
		this(path, 0, fontSize, message);
	}

	public BDFont(String path, int idx, float fontSize, String message) {
		this(path, idx, fontSize, message == null ? null : message.toCharArray(), true, 512, 512, 1024, 1024);
	}

	public BDFont(String path, int idx, float fontSize, char[] charMessage) {
		this(path, idx, fontSize, charMessage, true, 512, 512, 1024, 1024);
	}

	public BDFont(String path, int idx, float fontSize, char[] charMessage, boolean asyn, int tw, int th, int maxWidth,
			int maxHeight) {
		final CharSequence chs = (charMessage != null ? StringUtils.unificationChars(charMessage)
				: LSTRFont.getBaseCharsPool());
		this.set(0f, 0f, 0f, 0f, 0f, 0f, 1f);
		this._path = path;
		this._fontIndex = idx;
		this._isLoading = _isLoaded = false;
		this._pixelSize = this._fontSize = fontSize;
		this._displayLazy = true;
		this._maxTextureWidth = maxWidth;
		this._maxTextureHeight = maxHeight;
		this.textureWidth = tw;
		this.textureHeight = th;
		this.totalCharSet = getMaxTextCount();
		this.displays = new IntMap<Cache>(totalCharSet);
		this.isasyn = asyn;
		if (chs != null && chs.length() > 0) {
			this._chars = new CharArray(chs.length());
			this.text = StringUtils.getString(chs);
			this.expandTexture();
		} else {
			this._chars = new CharArray();
		}
		if (StringUtils.isEmpty(text)) {
			_isClose = true;
		}
		this._drawLimit = 0;
	}

	public boolean containsTexture(String mes) {
		if (StringUtils.isEmpty(text)) {
			return false;
		}
		if (StringUtils.isEmpty(mes)) {
			return true;
		}
		final String find = StringUtils.unificationStrings(mes);
		for (int i = 0; i < find.length(); i++) {
			char ch = find.charAt(i);
			if (!StringUtils.isWhitespace(ch) && text.indexOf(ch) == -1) {
				boolean child = false;
				if (_childFont != null) {
					child = _childFont.containsTexture(mes);
				}
				return child;
			}
		}
		return true;
	}

	public boolean containsTexture(char ch) {
		if (StringUtils.isEmpty(text)) {
			return false;
		}
		if (StringUtils.isWhitespace(ch)) {
			return true;
		}
		boolean child = false;
		if (_childFont != null) {
			child = _childFont.containsTexture(ch);
		}
		return child || text.indexOf(ch) != -1;
	}

	public BDFont updateTexture(String message) {
		return updateTexture(message, this.isasyn);
	}

	public BDFont updateTexture(String message, boolean asyn) {
		return updateTexture(message != null ? message.toCharArray() : null, asyn);
	}

	public BDFont updateTexture(char[] charMessage) {
		return updateTexture(charMessage, this.isasyn);
	}

	public BDFont updateTexture(char[] charMessage, boolean asyn) {
		if (_isClose) {
			return this;
		}
		cancelSubmit();
		this._chars.clear();
		for (Cache c : displays.values()) {
			if (c != null) {
				c.close();
			}
		}
		displays.clear();
		if (checkOutBounds()) {
			_childFont.close();
			_childFont = null;
		}
		if (displayList != null) {
			displayList.close(true);
			displayList = null;
		}
		if (customChars != null) {
			customChars.clear();
		}
		if (_childChars != null) {
			_childChars.clear();
		}
		final CharSequence chs = StringUtils.unificationChars(charMessage);
		this._initChars = _outBounds = isDrawing = false;
		this._initDraw = -1;
		this.isasyn = asyn;
		if (chs != null && chs.length() > 0) {
			this.text = StringUtils.getString(chs);
			this.expandTexture();
		}
		if (StringUtils.isEmpty(text)) {
			_isClose = true;
		}
		this._drawLimit = 0;
		return this;
	}

	public void cpy(BDFont font) {
		this._names = font._names;
		this._characters = font._characters;
		this._fontVersionName = font._fontVersionName;
		this._encoding = font._encoding;
		this._pixelSize = font._pixelSize;
		this._pixelFontSize = font._pixelFontSize;
		this._ascent = font._ascent;
		this._descent = font._descent;
		this._typoascent = font._typoascent;
		this._typodescent = font._typodescent;
		this._xheight = font._xheight;
		this._fontHeight = font._fontHeight;
		this._linegap = font._linegap;
		this._scalePixelFont = font._scalePixelFont;
		this._fontScale = font._fontScale;
		this._path = font._path;
		this._isLoading = font._isLoading;
		this._isLoaded = font._isLoaded;
	}

	private void make() {
		make(isasyn);
	}

	private synchronized void make(boolean asyn) {
		if (_isClose) {
			return;
		}
		if (_initChars) {
			return;
		}
		if (isDrawing) {
			return;
		}
		cancelSubmit();
		isDrawing = true;
		_submitUpdate = new UpdateFont(this);
		if (asyn) {
			LSystem.unload(_submitUpdate);
		} else {
			_submitUpdate.action(null);
		}
	}

	public boolean isSubmitting() {
		return _submitUpdate == null ? false : LSystem.containsUnLoad(_submitUpdate);
	}

	public BDFont cancelSubmit() {
		if (_submitUpdate != null) {
			LSystem.removeUnLoad(_submitUpdate);
		}
		return this;
	}

	public boolean loadFont() {
		if (!_isLoading) {
			_isLoading = true;
			if (!_isLoaded && !StringUtils.isEmpty(_path)) {
				loadFont(_path, _fontIndex);
				_isLoaded = true;
			}
		}
		return _isLoaded && _isLoading;
	}

	public boolean isLoaded() {
		return this._isLoaded;
	}

	public BDFont reset() {
		if (_isClose) {
			return this;
		}
		this.updateTexture(this.text);
		if (_names != null) {
			_names.clear();
		}
		if (_characters != null) {
			_characters.clear();
		}
		this._initDraw = 0;
		this._initChars = false;
		this.isDrawing = false;
		this._isLoaded = false;
		this._isLoading = false;
		return this;
	}

	protected void loadFont(String path, int idx) {
		final StrTokenizer tokenizer = BaseIO.loadStrTokenizer(path, "\t\n\r\f");
		int count = 0;
		while (tokenizer.hasMoreTokens()) {
			String[] kv = nextSplitChars(tokenizer);
			if (kv != null && kv.length > 0) {
				if (kv[0].equals("STARTFONT") && idx == count) {
					readFont(tokenizer, this);
					count++;
				}
			}
		}
		if (this._fontSize > 0) {
			setSize(MathUtils.iceil(this._fontSize));
		}
	}

	public BDFont set(float ascent, float descent, float typoascent, float typodescent, float xheight, float linegap,
			float scale) {
		if (_offset == null) {
			_offset = new PointI();
		}
		this._ascent = ascent;
		this._descent = descent;
		this._typoascent = typoascent;
		this._typodescent = typodescent;
		this._xheight = xheight;
		this._linegap = linegap;
		this._scalePixelFont = scale;
		return this;
	}

	private static String[] nextSplitChars(StrTokenizer tokenizer) {
		String result = tokenizer.nextToken().trim();
		return StringUtils.split(result, " ");
	}

	private void readChar(StrTokenizer tokenizer, BDFont bm) {
		final BDFGlyph g = new BDFGlyph(bdPixmapList);
		int encoding = -1;
		while (tokenizer.hasMoreTokens()) {
			final String[] kv = nextSplitChars(tokenizer);
			if (kv[0].equals("BITMAP")) {
				if (readBitmap(tokenizer, g)) {
					break;
				}
			} else if (kv[0].equals("ENDCHAR")) {
				break;
			} else if (kv.length < 2) {
				continue;
			} else if (kv[0].equals("ENCODING")) {
				encoding = Integer.parseInt(StringUtils.dequote(kv[1]));
				if (bm._encoding == "x") {
					encoding += 0xF000;
				}
				g.encoding = encoding;
			} else if (kv[0].equals("DWIDTH")) {
				try {
					g.setCharacterWidth(Integer.parseInt(StringUtils.dequote(kv[1])));
				} catch (LSysException ex) {
				}
			} else if (kv[0].equals("BBX")) {
				try {
					final int w = (kv.length > 1) ? Integer.parseInt(StringUtils.dequote(kv[1])) : 0;
					final int h = (kv.length > 2) ? Integer.parseInt(StringUtils.dequote(kv[2])) : 0;
					final int o = (kv.length > 3) ? Integer.parseInt(StringUtils.dequote(kv[3])) : 0;
					final int d = (kv.length > 4) ? Integer.parseInt(StringUtils.dequote(kv[4])) : 0;
					g.setBBX(w, h, o, d);
					g.setGlyph(new byte[h][w]);
					g.set(o, h + d);
				} catch (LSysException ex) {
				}
			}
		}
		if (encoding >= 0) {
			bm.putCharacter(encoding, g);
		}
	}

	private static boolean readBitmap(StrTokenizer tokenizer, BDFGlyph g) {
		final byte[][] glyph = g.getGlyph();
		int row = 0;
		while (tokenizer.hasMoreTokens() && row < glyph.length) {
			final String[] kv = nextSplitChars(tokenizer);
			if (kv[0].equals("ENDCHAR")) {
				return true;
			} else {
				unpack(kv[0], glyph[row++]);
			}
		}
		return false;
	}

	private BDFont readFont(StrTokenizer tokenizer, BDFont bm) {
		while (tokenizer.hasMoreTokens()) {
			final String[] kv = nextSplitChars(tokenizer);
			if (kv[0].equals("STARTCHAR")) {
				readChar(tokenizer, bm);
			} else if (kv[0].equals("ENDFONT")) {
				break;
			} else if (kv.length < 2) {
				continue;
			}
			if (kv[0].equals("FAMILY_NAME")) {
				bm.setName(NAME_FAMILY, StringUtils.dequote(kv[1]));
			} else if (kv[0].equals("WEIGHT_NAME")) {
				bm.setName(NAME_STYLE, StringUtils.dequote(kv[1]));
			} else if (kv[0].equals("FONT_VERSION")) {
				bm.setName(NAME_VERSION, StringUtils.dequote(kv[1]));
			} else if (kv[0].equals("COPYRIGHT")) {
				bm.setName(NAME_COPYRIGHT, StringUtils.dequote(kv[1]));
			} else if (kv[0].equals("FOUNDRY")) {
				bm.setName(NAME_MANUFACTURER, StringUtils.dequote(kv[1]));
			} else if (kv[0].equals("FONT")) {
				bm._fontVersionName = StringUtils.dequote(kv[1]);
			} else if (kv[0].equals("SIZE") || kv[0].equals("PIXEL_SIZE")) {
				try {
					bm._pixelSize = Integer.parseInt(StringUtils.dequote(kv[1]));
				} catch (Exception ex) {
				}
			} else if (kv[0].equals("FONT_ASCENT")) {
				try {
					int i = Integer.parseInt(StringUtils.dequote(kv[1]));
					bm.setLineAscent(i);
					bm.setAscent(i);
				} catch (Exception ex) {
				}
			} else if (kv[0].equals("FONT_DESCENT")) {
				try {
					int i = Integer.parseInt(StringUtils.dequote(kv[1]));
					bm.setLineDescent(i);
					bm.setDescent(i);
				} catch (Exception ex) {
				}
			} else if (kv[0].equals("X_HEIGHT")) {
				try {
					int i = Integer.parseInt(StringUtils.dequote(kv[1]));
					bm.setXHeight(i);
				} catch (Exception ex) {
				}
			} else if (kv[0].equals("CHARSET_REGISTRY")) {
				bm._encoding = StringUtils.dequote(kv[1]);
			}
		}
		return bm;
	}

	private static void unpack(String h, byte[] b) {
		int i = 0;
		final CharIterator ci = new CharIterator(h);
		for (char ch = ci.first(); ch != CharIterator.DONE; ch = ci.next()) {
			int v;
			if (ch >= '0' && ch <= '9') {
				v = (ch - '0');
			} else if (ch >= 'A' && ch <= 'F') {
				v = (ch - 'A' + 10);
			} else if (ch >= 'a' && ch <= 'f') {
				v = (ch - 'a' + 10);
			} else {
				continue;
			}
			if (i < b.length) {
				b[i++] = (byte) (((v & 0x08) == 0) ? 0 : -1);
			}
			if (i < b.length) {
				b[i++] = (byte) (((v & 0x04) == 0) ? 0 : -1);
			}
			if (i < b.length) {
				b[i++] = (byte) (((v & 0x02) == 0) ? 0 : -1);
			}
			if (i < b.length) {
				b[i++] = (byte) (((v & 0x01) == 0) ? 0 : -1);
			}
		}
	}

	public int[] getCharacters() {
		return _characters.keys();
	}

	public boolean containsBDFontChar(int c) {
		return _characters.containsKey(c);
	}

	public int getTextureWidth() {
		return this.textureWidth;
	}

	public int getTextureHeight() {
		return this.textureHeight;
	}

	public LTexture getTexture() {
		return displayList;
	}

	public boolean isEmpty() {
		return _characters.isEmpty();
	}

	public boolean containsCharacter(int ch) {
		return _characters.containsKey(ch);
	}

	public int charsCount() {
		return _characters.size;
	}

	public BDFGlyph getCharacter(int ch) {
		return _characters.get(ch);
	}

	public BDFGlyph putCharacter(int ch, BDFGlyph fc) {
		_characters.put(ch, fc);
		return fc;
	}

	public BDFGlyph removeCharacter(int ch) {
		return _characters.remove(ch);
	}

	public int[] codePoints() {
		return _characters.keys();
	}

	public float getScale() {
		return _scalePixelFont;
	}

	public BDFont setScale(float scale) {
		this.loadFont();
		this._scalePixelFont = scale;
		return this;
	}

	public BDFont setPixelFontSize(float size) {
		if (size <= 0) {
			return this;
		}
		this.loadFont();
		this._pixelFontSize = size;
		this._scalePixelFont = (_pixelFontSize / this._pixelSize);
		return this;
	}

	public float getPixelFontSize() {
		this.loadFont();
		return _pixelFontSize <= 0 ? this._pixelSize : _pixelFontSize;
	}

	@Override
	public float getAscent() {
		this.loadFont();
		if (_ascent == 0) {
			return getSize();
		}
		return _ascent * _scalePixelFont;
	}

	public float getDescent() {
		this.loadFont();
		return _descent * _scalePixelFont;
	}

	public float getLineAscent() {
		this.loadFont();
		return _typoascent * _scalePixelFont;
	}

	public float getLineDescent() {
		this.loadFont();
		return _typodescent * _scalePixelFont;
	}

	public float getXHeight() {
		this.loadFont();
		return _xheight * _scalePixelFont;
	}

	public float getLineGap() {
		this.loadFont();
		return _linegap * _scalePixelFont;
	}

	public BDFont setAscent(float v) {
		_ascent = MathUtils.ceil(v);
		return this;
	}

	public BDFont setDescent(float v) {
		_descent = MathUtils.ceil(v);
		return this;
	}

	public BDFont setLineAscent(float v) {
		_typoascent = MathUtils.ceil(v);
		return this;
	}

	public BDFont setLineDescent(float v) {
		_typodescent = MathUtils.ceil(v);
		return this;
	}

	public BDFont setXHeight(float v) {
		_xheight = MathUtils.ceil(v);
		return this;
	}

	public BDFont setLineGap(float v) {
		_linegap = MathUtils.ceil(v);
		return this;
	}

	public BDFont setXHeight() {
		if (_characters.containsKey((int) 'x')) {
			BDFGlyph g = _characters.get((int) 'x');
			_xheight = g.getGlyphAscent();
		}
		return this;
	}

	private void expandTexture() {
		this.additionalChars = text == null ? null : text.toCharArray();
		totalCharSet = getMaxTextCount();
		if (additionalChars != null && additionalChars.length > totalCharSet) {
			textureWidth = MathUtils.min(textureWidth * 2, this._maxTextureWidth);
			textureHeight = MathUtils.min(textureHeight * 2, this._maxTextureHeight);
		}
	}

	public PointF draw(Canvas g, String s, PointF b) {
		return draw(g, s, b.x, b.y, Integer.MAX_VALUE, _typoascent + _typodescent + _linegap);
	}

	public PointF draw(Canvas g, String s, PointF b, float w) {
		return draw(g, s, b.x, b.y, w, _typoascent + _typodescent + _linegap);
	}

	public PointF draw(Canvas g, String s, PointF b, float w, float h) {
		return draw(g, s, b.x, b.y, w, h);
	}

	public PointF draw(Canvas g, String s, float bx, float by) {
		return draw(g, s, bx, by, Integer.MAX_VALUE, _typoascent + _typodescent + _linegap);
	}

	public PointF draw(Canvas g, String s, float bx, float by, float w) {
		return draw(g, s, bx, by, w, _typoascent + _typodescent + _linegap);
	}

	public PointF draw(Canvas g, String s, float bx, float by, float w, float h) {
		if (!loadFont()) {
			return null;
		}
		if (_characters.size == 0) {
			return null;
		}
		float cx = bx, cy = by;
		int i = 0;
		while (i < s.length()) {
			int ch = s.charAt(i);
			if (ch < 0x10000) {
				i++;
			} else {
				i += 2;
			}
			switch (ch) {
			case LSystem.LF:
			case LSystem.CR:
				cx = bx;
				cy += ((h + 1) * _scalePixelFont);
				break;
			default:
				if (_characters.containsKey(ch)) {
					final BDFGlyph bm = _characters.get(ch);
					if (cx - bx + bm.getCharacterWidth() >= w) {
						cx = bx;
						cy += h;
					}
					final float pos = offsetPos((char) ch, bm);
					float hl = (bm.getCharacterWidth() * _scalePixelFont);
					if (CharUtils.isHalfChar(ch) || CharUtils.isAlphaOrDigit(ch)) {
						hl *= 2;
					}
					cx += bm.paint(g, cx + pos, cy + hl, _scalePixelFont, _pixelColor);
				} else if (_characters.containsKey(-1)) {
					final BDFGlyph bm = _characters.get(-1);
					if (cx - bx + bm.getCharacterWidth() >= w) {
						cx = bx;
						cy += h;
					}
					final float pos = offsetPos((char) ch, bm);
					float hl = (bm.getCharacterWidth() * _scalePixelFont);
					if (CharUtils.isHalfChar(ch) || StringUtils.isAlphaOrDigit(ch)) {
						hl *= 2;
					}
					cx += bm.paint(g, cx + pos, cy + hl, _scalePixelFont, _pixelColor);
				}
				break;
			}
		}
		return _rectPoint.set(cx, cy);
	}

	public PointF drawAlphabet(Canvas g, char ch, PointF b) {
		return drawAlphabet(g, ch, b.x, b.y, Integer.MAX_VALUE, _typoascent + _typodescent + _linegap);
	}

	public PointF drawAlphabet(Canvas g, char ch, PointF b, float w) {
		return drawAlphabet(g, ch, b.x, b.y, w, _typoascent + _typodescent + _linegap);
	}

	public PointF drawAlphabet(Canvas g, char ch, PointF b, float w, float h) {
		return drawAlphabet(g, ch, b.x, b.y, w, h);
	}

	public PointF drawAlphabet(Canvas g, char ch, float bx, float by) {
		return drawAlphabet(g, ch, bx, by, Integer.MAX_VALUE, _typoascent + _typodescent + _linegap);
	}

	public PointF drawAlphabet(Canvas g, char ch, float bx, float by, float w) {
		return drawAlphabet(g, ch, bx, by, w, _typoascent + _typodescent + _linegap);
	}

	public PointF drawAlphabet(Canvas g, char ch, float bx, float by, float w, float h) {
		if (!loadFont()) {
			return null;
		}
		if (_characters.size == 0) {
			return null;
		}
		float cx = bx, cy = by;
		final BDFGlyph bm = _characters.get(ch);
		if (bm != null) {
			if (cx - bx + bm.getCharacterWidth() >= w) {
				cx = bx;
				cy += h;
			}
			float pos = offsetPos((char) ch, bm);
			float hl = (bm.getCharacterWidth() * _scalePixelFont);
			if (CharUtils.isHalfChar(ch) || CharUtils.isAlphaOrDigit(ch)) {
				hl *= 2;
			}

			if (CharUtils.isFullChar(ch)) {
				final int wbbx = bm.bbx.x + bm.bbx.width;
				cx += bm.paint(g, cx + pos - MathUtils.abs(wbbx) - bm.getCharacterWidth() / 2 - 1, cy + hl,
						_scalePixelFont, _pixelColor);
			} else {
				cx += bm.paint(g, cx + pos, cy + hl, _scalePixelFont, _pixelColor);
			}
		}
		return new PointF(cx, cy);
	}

	private final static float offsetPos(char ch, BDFGlyph bm) {
		if (CharUtils.isFullChar(ch)) {
			return bm.getCharacterWidth() * 1.4f;
		}
		return CharUtils.isFullChar(ch) ? bm.getCharacterWidth() / 2f : 0;
	}

	public BDFont contractGlyphs() {
		for (BDFGlyph glyph : _characters.values()) {
			glyph.contract();
		}
		return this;
	}

	public boolean containsName(int nametype) {
		return _names.containsKey(nametype);
	}

	public String getName(int nametype) {
		return _names.get(nametype);
	}

	public void setName(int nametype, String name) {
		_names.put(nametype, name);
	}

	public void removeName(int nametype) {
		_names.remove(nametype);
	}

	public int[] nameTypes() {
		final int[] nt = _names.keys();
		final int[] nt2 = new int[nt.length];
		for (int i = 0; i < nt.length; i++) {
			nt2[i] = nt[i];
		}
		return nt2;
	}

	public boolean isBoldStyle() {
		if (_names.containsKey(NAME_STYLE)) {
			String s = _names.get(NAME_STYLE).toUpperCase();
			return s.contains("BOLD") || s.contains("BLACK") || s.contains("HEAVY");
		} else {
			return false;
		}
	}

	public boolean isItalicStyle() {
		if (_names.containsKey(NAME_STYLE)) {
			String s = _names.get(NAME_STYLE).toUpperCase();
			return s.contains("ITALIC") || s.contains("OBLIQUE") || s.contains("SLANT");
		} else {
			return false;
		}
	}

	public boolean isUnderlineStyle() {
		if (_names.containsKey(NAME_STYLE)) {
			String s = _names.get(NAME_STYLE).toUpperCase();
			return s.contains("UNDERLINE") || s.contains("UNDERSCORE");
		} else {
			return false;
		}
	}

	public boolean isOutlineStyle() {
		if (_names.containsKey(NAME_STYLE)) {
			String s = _names.get(NAME_STYLE).toUpperCase();
			return s.contains("OUTLINE");
		} else {
			return false;
		}
	}

	public boolean isShadowStyle() {
		if (_names.containsKey(NAME_STYLE)) {
			String s = _names.get(NAME_STYLE).toUpperCase();
			return s.contains("SHADOW");
		} else {
			return false;
		}
	}

	public boolean isCondensedStyle() {
		if (_names.containsKey(NAME_STYLE)) {
			String s = _names.get(NAME_STYLE).toUpperCase();
			return s.contains("CONDENSE") || s.contains("NARROW");
		} else {
			return false;
		}
	}

	public boolean isExtendedStyle() {
		if (_names.containsKey(NAME_STYLE)) {
			String s = _names.get(NAME_STYLE).toUpperCase();
			return s.contains("EXTEND") || s.contains("EXPAND") || s.contains("WIDE");
		} else {
			return false;
		}
	}

	public boolean isNegativeStyle() {
		if (_names.containsKey(NAME_STYLE)) {
			String s = _names.get(NAME_STYLE).toUpperCase();
			return s.contains("NEGATIVE") || s.contains("REVERSE") || s.contains("INVERSE") || s.contains("INVERT");
		} else {
			return false;
		}
	}

	public boolean isStrikeoutStyle() {
		if (_names.containsKey(NAME_STYLE)) {
			String s = _names.get(NAME_STYLE).toUpperCase();
			return s.contains("STRIKEOUT") || s.contains("STRIKETHR");
		} else {
			return false;
		}
	}

	public boolean isRegularStyle() {
		if (_names.containsKey(NAME_STYLE)) {
			String s = _names.get(NAME_STYLE).trim();
			return s.equalsIgnoreCase("") || s.equalsIgnoreCase("PLAIN") || s.equalsIgnoreCase("REGULAR")
					|| s.equalsIgnoreCase("NORMAL") || s.equalsIgnoreCase("MEDIUM");
		} else {
			return false;
		}
	}

	public boolean isObliqueStyle() {
		if (_names.containsKey(NAME_STYLE)) {
			String s = _names.get(NAME_STYLE).toUpperCase();
			return s.contains("OBLIQUE") || s.contains("SLANT");
		} else {
			return false;
		}
	}

	public int getMacStyle() {
		int s = 0;
		if (isBoldStyle())
			s |= 0x01;
		if (isItalicStyle())
			s |= 0x02;
		if (isUnderlineStyle())
			s |= 0x04;
		if (isOutlineStyle())
			s |= 0x08;
		if (isShadowStyle())
			s |= 0x10;
		if (isCondensedStyle())
			s |= 0x20;
		if (isExtendedStyle())
			s |= 0x40;
		return s;
	}

	public int getFsSelection() {
		int s = 0;
		if (isItalicStyle())
			s |= 0x0001;
		if (isUnderlineStyle())
			s |= 0x0002;
		if (isNegativeStyle())
			s |= 0x0004;
		if (isOutlineStyle())
			s |= 0x0008;
		if (isStrikeoutStyle())
			s |= 0x0010;
		if (isBoldStyle())
			s |= 0x0020;
		if (isRegularStyle())
			s |= 0x0040;
		if (isObliqueStyle())
			s |= 0x0200;
		return s;
	}

	private final boolean checkRunning(String chars) {

		if (_isClose) {
			return false;
		}

		if (StringUtils.isEmpty(chars)) {
			return false;
		}
		if (!_isLoaded) {
			return loadFont();
		}

		make();
		if (processing()) {
			return false;
		}
		if (_displayLazy) {
			if (_initDraw < _drawLimit) {
				_initDraw++;
				return false;
			}
		}
		if (displayList.isClosed()) {
			return false;
		}
		return true;
	}

	@Override
	public void drawString(GLEx gl, String chars, float x, float y) {
		drawString(gl, x, y, 1f, 1f, 0, chars, LColor.white);
	}

	@Override
	public void drawString(GLEx gl, String chars, float x, float y, LColor color) {
		drawString(gl, x, y, 1f, 1f, 0, chars, color);
	}

	@Override
	public void drawString(GLEx gl, String chars, float x, float y, float rotation, LColor color) {
		drawString(gl, x, y, 1f, 1f, rotation, chars, color);
	}

	public void drawString(GLEx gl, String chars, float x, float y, float rotation) {
		drawString(gl, x, y, 1f, 1f, rotation, chars, LColor.white);
	}

	public void drawString(GLEx gl, String chars, float x, float y, float sx, float sy, float rotation, LColor c) {
		drawString(gl, x, y, sx, sy, rotation, chars, c);
	}

	public void drawString(GLEx gl, float x, float y, float sx, float sy, float rotation, String chars, LColor c) {
		drawString(gl, x, y, sx, sy, 0, 0, rotation, chars, c, 0, chars.length());
	}

	public void drawString(GLEx gl, float x, float y, float sx, float sy, float ax, float ay, float rotation,
			String chars, LColor c) {
		drawString(gl, x, y, sx, sy, ax, ay, rotation, chars, c, 0, chars.length());
	}

	@Override
	public void drawString(GLEx g, String chars, float x, float y, float sx, float sy, float ax, float ay,
			float rotation, LColor c) {
		drawString(g, x, y, sx, sy, ax, ay, rotation, chars, c, 0, chars.length());
	}

	private void drawString(GLEx gl, float mx, float my, float sx, float sy, float ax, float ay, float rotation,
			String msg, LColor c, int startIndex, int endIndex) {
		if (StringUtils.isEmpty(msg)) {
			return;
		}
		String newMessage = toMessage(msg);
		if (checkEndIndexUpdate(endIndex, msg, newMessage)) {
			endIndex = newMessage.length();
		}
		if (!checkRunning(newMessage)) {
			return;
		}
		final float nsx = sx * _fontScale;
		final float nsy = sy * _fontScale;
		final float x = mx + _offset.x;
		final float y = my + _offset.y;
		this.intObject = null;
		this.charCurrent = 0;
		this.totalWidth = 0;
		this.totalHeight = 0;
		int old = gl.color();
		boolean childDraw = false;
		final boolean anchor = ax != 0 || ay != 0;
		final boolean angle = rotation != 0;
		final boolean update = angle || anchor;
		try {
			gl.setTint(c);
			if (update) {
				gl.saveTx();
				Affine2f xf = gl.tx();
				if (angle) {
					float centerX = x + this.getWidth(newMessage) / 2;
					float centerY = y + this.getHeight(newMessage) / 2;
					xf.translate(centerX, centerY);
					xf.preRotate(rotation);
					xf.translate(-centerX, -centerY);
				}
				if (anchor) {
					xf.translate(ax, ay);
				}
			}
			for (int i = startIndex; i < endIndex; i++) {
				char ch = newMessage.charAt(i);
				charCurrent = ch;

				intObject = customChars.get(charCurrent);

				if (charCurrent == newRFlag) {
					continue;
				}
				if (charCurrent == newLineFlag) {
					totalHeight += getPixelFontSize();
					totalWidth = 0;
					continue;
				}
				if (charCurrent == newSpaceFlag) {
					totalWidth += _ascent;
					continue;
				}
				if (charCurrent == newTabSpaceFlag) {
					totalWidth += (_ascent * 3);
					continue;
				}
				if (intObject != null) {
					if (!checkOutBounds() || containsChar(ch)) {
						gl.draw(displayList, x + (totalWidth * nsx), y + (totalHeight * nsy), intObject.width * nsx,
								intObject.height * nsy, intObject.storedX, intObject.storedY, intObject.width,
								intObject.height, c);
					} else if (checkOutBounds()) {
						putChildChars(ch, x + (totalWidth * nsx), y + (totalHeight * nsy), intObject.width * nsx,
								intObject.height * sy, null);
						childDraw = true;
					}
					totalWidth += intObject.width;
				}
			}
		} finally {
			gl.setTint(old);
			if (update) {
				gl.restoreTx();
			}
		}
		if (childDraw && _childChars != null) {
			_childFont._drawChildString(_childChars, gl, mx, my, sx, sy, ax, ay, rotation, newMessage, c, startIndex,
					endIndex);
			_childChars.clear();
		}
	}

	private void _drawChildString(TArray<CharRect> child, GLEx gl, float mx, float my, float sx, float sy, float ax,
			float ay, float rotation, String msg, LColor c, int startIndex, int endIndex) {
		if (!checkRunning(msg)) {
			return;
		}
		final float x = mx + _offset.x;
		final float y = my + _offset.y;
		this.intObject = null;
		this.charCurrent = 0;
		this.totalWidth = 0;
		this.totalHeight = 0;
		int old = gl.color();
		boolean childDraw = false;
		final boolean anchor = ax != 0 || ay != 0;
		final boolean angle = rotation != 0;
		final boolean update = angle || anchor;
		try {
			gl.setTint(c);
			if (update) {
				gl.saveTx();
				Affine2f xf = gl.tx();
				if (angle) {
					float centerX = x + this.getWidth(msg) / 2;
					float centerY = y + this.getHeight(msg) / 2;
					xf.translate(centerX, centerY);
					xf.preRotate(rotation);
					xf.translate(-centerX, -centerY);
				}
				if (anchor) {
					xf.translate(ax, ay);
				}
			}
			for (int i = 0; i < child.size; i++) {
				CharRect rect = child.get(i);
				if (rect != null) {
					char ch = rect.name;
					intObject = customChars.get(ch);
					if (intObject != null && containsChar(ch)) {
						gl.draw(displayList, rect.x, rect.y, rect.width, rect.height, intObject.storedX,
								intObject.storedY, intObject.width, intObject.height, c);
					} else if (checkOutBounds()) {
						putChildChars(ch, rect.x, rect.y, rect.width, rect.height, null);
						childDraw = true;
					}
				}
			}
		} finally {
			gl.setTint(old);
			if (update) {
				gl.restoreTx();
			}
		}
		if (childDraw && _childChars != null) {
			_childFont._drawChildString(_childChars, gl, mx, my, sx, sy, ax, ay, rotation, msg, c, startIndex,
					endIndex);
			_childChars.clear();
		}
	}

	public int getMaxTextCount() {
		float size = MathUtils.max(defaultPixelMinFontSize, _pixelSize) + 1;
		return MathUtils.max(0, (int) ((textureWidth / size) * (textureHeight / size)));
	}

	public int getTextCount() {
		return _chars != null ? _chars.size() : 0;
	}

	public String getChars() {
		return _chars.getString();
	}

	private boolean checkOutBounds() {
		return _outBounds && _childFont != null;
	}

	private boolean processing() {
		return isDrawing;
	}

	public boolean containsChar(char c) {
		return _chars.contains(c);
	}

	public boolean containsChars(String msg) {
		if (StringUtils.isEmpty(msg)) {
			return false;
		}
		String newMessage = toMessage(msg);
		int count = 0;
		int len = newMessage.length();
		for (int i = 0; i < len; i++) {
			if (_chars.contains(newMessage.charAt(i))) {
				count++;
			}
		}
		return count == len;
	}

	public int getPixelColor() {
		return this._pixelColor;
	}

	public void setPixelColor(int pixel) {
		this._pixelColor = pixel;
	}

	public void setPixelColor(LColor color) {
		this._pixelColor = (color == null ? LColor.DEF_COLOR : color.getARGB());
	}

	public int charHeight(char c) {
		loadFont();
		BDFGlyph g = _characters.get(c);
		if (g != null) {
			return MathUtils.iceil(g.bbx.y != 0 ? g.bbx.y * _scalePixelFont
					: (g.advance != 0 ? g.advance * _scalePixelFont : getPixelFontSize()));
		}
		return 0;
	}

	@Override
	public int charWidth(char c) {
		loadFont();
		BDFGlyph g = _characters.get(c);
		if (g != null) {
			return MathUtils.iceil(g.bbx.x != 0 ? (g.bbx.x + g.bbx.width) * _scalePixelFont
					: (g.advance != 0 ? g.advance * _scalePixelFont : getPixelFontSize()));
		}
		return 0;
	}

	public int getLineWidth(String msg) {
		if (StringUtils.isNullOrEmpty(msg)) {
			return 0;
		}
		final String newMessage = toMessage(msg);
		loadFont();
		int count = 0;
		for (int i = 0, size = newMessage.length(); i < size; i++) {
			final char ch = newMessage.charAt(i);
			count += charWidth(ch);
		}
		return count;
	}

	@Override
	public int stringWidth(String msg) {
		if (StringUtils.isNullOrEmpty(msg)) {
			return 0;
		}
		final String newMessage = toMessage(msg);
		loadFont();
		if (newMessage.indexOf(LSystem.LF) == -1) {
			return getLineWidth(newMessage);
		} else {
			final StrBuilder sbr = new StrBuilder();
			int width = 0;
			for (int i = 0, size = newMessage.length(); i < size; i++) {
				char ch = newMessage.charAt(i);
				if (ch == LSystem.LF) {
					width = MathUtils.max(getLineWidth(sbr.toString()), width);
					sbr.setLength(0);
				} else {
					sbr.append(ch);
				}
			}
			return width;
		}
	}

	@Override
	public int stringHeight(String msg) {
		if (StringUtils.isNullOrEmpty(msg)) {
			return 0;
		}
		final String newMessage = toMessage(msg);
		loadFont();
		if (newMessage.indexOf(LSystem.LF) == -1) {
			return getHeight();
		} else {
			final String[] list = StringUtils.split(newMessage, LSystem.LF);
			return list.length * getHeight();
		}
	}

	@Override
	public int getHeight() {
		return MathUtils.iceil(MathUtils.max(_fontHeight, _fontSize, getPixelFontSize()) * _scalePixelFont);
	}

	@Override
	public void setAssent(float assent) {
		this.setAscent(assent);
	}

	@Override
	public String getFontName() {
		return _names.get(NAME_FAMILY);
	}

	public void setFontSize(float size) {
		setSize(MathUtils.iceil(size));
	}

	private float _fontSize;

	@Override
	public void setSize(int size) {
		if (size <= 0) {
			return;
		}
		this._fontSize = size;
		this._fontScale = _fontSize / getPixelFontSize();
	}

	public int getFontSize() {
		return getSize();
	}

	@Override
	public int getSize() {
		return MathUtils.iceil(this._fontSize == 0 ? getPixelFontSize() : _fontSize);
	}

	@Override
	public PointI getOffset() {
		return _offset;
	}

	@Override
	public void setOffset(PointI v) {
		_offset.set(v);
	}

	@Override
	public void setOffsetX(int x) {
		_offset.x = x;
	}

	@Override
	public void setOffsetY(int y) {
		this._offset.y = y;
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

	public int getWidth(String msg) {
		if (_isClose) {
			return 0;
		}
		final String newMessage = toMessage(msg);
		make();
		if (processing()) {
			return stringWidth(newMessage);
		}
		if (displayList.isClosed()) {
			return 0;
		}
		int totalWidth = 0;
		IntObject intObject = null;
		int currentChar = 0;
		int maxWidth = 0;
		for (int i = 0; i < newMessage.length(); i++) {
			currentChar = newMessage.charAt(i);
			intObject = customChars.get(currentChar);
			if (intObject != null) {
				if (currentChar == newLineFlag) {
					maxWidth = MathUtils.max(maxWidth, totalWidth);
					totalWidth = 0;
				}
				totalWidth += intObject.width;
			}
		}
		return MathUtils.max(maxWidth, totalWidth);
	}

	public int getHeight(String msg) {
		if (_isClose) {
			return 0;
		}
		String newMessage = toMessage(msg);
		make();
		if (processing()) {
			return stringHeight(newMessage);
		}
		if (displayList.isClosed()) {
			return 0;
		}
		int currentChar = 0;
		int lines = 0;
		int height = 0;
		int maxHeight = 0;
		for (int i = 0; i < newMessage.length(); i++) {
			currentChar = newMessage.charAt(i);
			intObject = customChars.get(currentChar);
			if (intObject != null) {
				maxHeight = MathUtils.max(maxHeight, intObject.height);
				height = maxHeight;
			}
			if (currentChar == newLineFlag) {
				lines++;
				height = 0;
			}
		}
		return MathUtils.iceil(lines * getLineAscent() + height);
	}

	@Override
	public ITranslator getTranslator() {
		return this._translator;
	}

	@Override
	public IFont setTranslator(ITranslator translator) {
		this._translator = translator;
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
		cancelSubmit();
		for (Cache c : displays.values()) {
			if (c != null) {
				c.close();
			}
		}
		for (Pixmap pix : bdPixmapList) {
			if (pix != null) {
				pix.close();
				pix = null;
			}
		}
		bdPixmapList.clear();
		displays.clear();
		if (displayList != null) {
			displayList.close(true);
			displayList = null;
		}
		if (customChars != null) {
			customChars.clear();
			customChars = null;
		}
		isDrawing = false;
		_displayLazy = false;
		_initChars = false;
		_initDraw = -1;
		_isClose = true;
		if (checkOutBounds()) {
			_childFont.close();
			_childFont = null;
		}
	}

}
