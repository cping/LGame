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
package loon.opengl;

import loon.BaseIO;
import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.LTextureBatch;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.geom.PointI;
import loon.geom.RectBox;
import loon.geom.RectI;
import loon.utils.ArrayMap;
import loon.utils.ArrayMap.Entry;
import loon.utils.parse.StrTokenizer;
import loon.utils.IntArray;
import loon.utils.PathUtils;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.TimeUtils;
import loon.utils.xml.XMLDocument;
import loon.utils.xml.XMLElement;
import loon.utils.xml.XMLParser;

/**
 * 这是一个纹理打包及管理用类,用以动态的将多个小图组合在一起并进行渲染及管理
 */
public final class LTexturePack implements LRelease {

	private GLEx _glex;

	private final PointI _blittedSize = new PointI();

	private final ArrayMap _packedMap = new ArrayMap();

	private LTexture _texture = null;

	private int _count;

	protected LColor _colorMask;

	protected boolean _useAlpha, _packed, _packing;

	private String _fileName, _name;

	public LTexture getTexture(String name) {
		return getTexture(name, 0f, 0f, 0f, 0f);
	}

	public LTexture getTexture(String name, float x, float y, float w, float h) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return null;
		}
		return _texture.copy(entry.bounds.left + x, entry.bounds.top + y, entry.bounds.right - entry.bounds.left + w,
				entry.bounds.bottom - entry.bounds.top + h);
	}

	public LTexture getTextureAll(String name) {
		return getTextureAll(name, 0f, 0f, 0f, 0f);
	}

	public LTexture getTextureAll(String name, float x, float y, float w, float h) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return null;
		}
		return _texture.copy(entry.bounds.left + x, entry.bounds.top + y, entry.bounds.right + w,
				entry.bounds.bottom + h);
	}

	public LTexture getTexture(int id) {
		return getTexture(id, 0f, 0f, 0f, 0f);
	}

	public LTexture getTexture(int id, float x, float y, float w, float h) {
		this.pack();
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return null;
		}
		return _texture.copy(entry.bounds.left + x, entry.bounds.top + y, entry.bounds.right - entry.bounds.left + w,
				entry.bounds.bottom - entry.bounds.top + h);
	}

	public LTexture getTextureAll(int id) {
		return getTextureAll(id, 0f, 0f, 0f, 0f);
	}

	public LTexture getTextureAll(int id, float x, float y, float w, float h) {
		this.pack();
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return null;
		}
		return _texture.copy(entry.bounds.left + x, entry.bounds.top + y, entry.bounds.right + w,
				entry.bounds.bottom + h);
	}

	public LTexturePack() {
		this(true);
	}

	public LTexturePack(boolean hasAlpha) {
		this._useAlpha = hasAlpha;
	}

	public LTexturePack(String path) {
		if (StringUtils.isEmpty(path)) {
			throw new LSysException(path + " not found !");
		}
		final String ext = PathUtils.getExtension(path).trim().toLowerCase();
		if ("atlas".equals(ext)) {
			setAtlas(path);
		} else if (ext.length() == 0 || "xml".equals(ext) || "pack".equals(ext) || "txt".equals(ext)) {
			setXML(path);
		} else {
			throw new LSysException("The path with the [" + ext + "] extension cannot be resolved.");
		}
	}

	public LTexturePack(String fileName, TArray<LTexturePackClip> clips) {
		setXML(fileName, clips);
	}

	private void setAtlas(String path) {
		if (path == null) {
			return;
		}
		final String context = BaseIO.loadText(path);
		final StrTokenizer reader = new StrTokenizer(context, LSystem.NL + LSystem.BRANCH);
		String result = null;
		int count = 0;
		int id = 0;
		String itemName = LSystem.EMPTY;
		String itemIndex = LSystem.EMPTY;
		String itemFile = LSystem.EMPTY;
		for (; reader.hasMoreTokens();) {
			result = reader.nextToken();
			if (!StringUtils.isEmpty(result)) {
				if (count == 0) {
					this._fileName = result;
				} else {
					final String contextValue = result.trim().toLowerCase();
					final String[] keyValue = StringUtils.split(contextValue, LSystem.COLON);
					final int size = keyValue.length;
					if (size > 1 && "bounds".equals(keyValue[0])) {
						final String[] list = StringUtils.split(keyValue[1], LSystem.COMMA);
						final int len = list.length;
						if (len > 3) {
							int x = Integer.parseInt(list[0].trim());
							int y = Integer.parseInt(list[1].trim());
							int w = Integer.parseInt(list[2].trim());
							int h = Integer.parseInt(list[3].trim());
							PackEntry entry = new PackEntry(null);
							entry.fileName = StringUtils.isEmpty(itemFile) ? _fileName : itemFile;
							entry.bounds.set(x, y, x + w, y + h);
							entry.id = id++;
							if (!StringUtils.isEmpty(itemName)) {
								_packedMap.put(itemName + itemIndex, entry);
							} else if (!StringUtils.isEmpty(itemIndex)) {
								_packedMap.put(_fileName + itemIndex + entry.id, entry);
							} else {
								_packedMap.put(_fileName + entry.id, entry);
							}
							itemName = itemIndex = itemFile = LSystem.EMPTY;
						}
					} else if (size > 1 && "colormask".equals(keyValue[0])) {
						_colorMask = new LColor(keyValue[1]);
					} else if (size > 1 && "index".equals(keyValue[0])) {
						itemIndex = keyValue[1];
					} else if (size > 1 && "file".equals(keyValue[0])) {
						itemFile = keyValue[1];
					} else if (size == 1) {
						itemName = keyValue[0];
					}
				}
				count++;
			}
		}
		this._count = _packedMap.size();
		this._packing = false;
		this._packed = true;
		this._useAlpha = true;
	}

	public void setXML(String fileName, TArray<LTexturePackClip> clips) {
		XMLElement doc = new XMLElement("pack");
		doc.addAttribute("file", fileName);
		for (int i = 0; i < clips.size; i++) {
			LTexturePackClip clip = clips.get(i);
			if (clip != null) {
				XMLElement block = new XMLElement("block");
				block.addAttribute("id", clip.id);
				block.addAttribute("name", clip.name);
				block.addAttribute("left", clip.rect.x());
				block.addAttribute("top", clip.rect.y());
				block.addAttribute("right", clip.rect.width());
				block.addAttribute("bottom", clip.rect.height());
				doc.addContents(block);
			}
		}
		setXML(doc);
	}

	public LTexturePack(XMLElement pack) {
		setXML(pack);
	}

	private void setXML(String path) {
		XMLDocument doc = XMLParser.parse(path);
		XMLElement pack = doc.getRoot();
		setXML(pack);
	}

	private void setXML(XMLElement pack) {
		this._fileName = pack.getAttribute("file", null);
		this._name = pack.getAttribute("name", _fileName);
		int r = pack.getIntAttribute("r", -1);
		int g = pack.getIntAttribute("g", -1);
		int b = pack.getIntAttribute("b", -1);
		int a = pack.getIntAttribute("a", -1);
		if (r != -1 && g != -1 && b != -1 && a != -1) {
			_colorMask = new LColor(r, g, b, a);
		}
		if (_fileName != null) {
			TArray<XMLElement> blocks = pack.list("block");
			for (XMLElement e : blocks) {
				PackEntry entry = new PackEntry(null);
				final int id = e.getIntAttribute("id", _count);
				entry.id = id;
				entry.fileName = e.getAttribute("name", null);
				entry.bounds.left = e.getIntAttribute("left", 0);
				entry.bounds.top = e.getIntAttribute("top", 0);
				entry.bounds.right = e.getIntAttribute("right", 0);
				entry.bounds.bottom = e.getIntAttribute("bottom", 0);
				if (entry.fileName != null) {
					_packedMap.put(entry.fileName, entry);
				} else {
					_packedMap.put(String.valueOf(id), entry);
				}
				_count++;
			}
			this._packing = false;
			this._packed = true;
		}
		this._useAlpha = true;
	}

	public synchronized Image getImage(int id) {
		PackEntry entry = getEntry(id);
		if (entry != null) {
			if (entry.image != null) {
				return entry.image;
			} else if (entry.fileName != null) {
				return BaseIO.loadImage(entry.fileName);
			}
		}
		return null;
	}

	public synchronized Image getImage(String name) {
		PackEntry entry = getEntry(name);
		if (entry != null) {
			if (entry.image != null) {
				return entry.image;
			} else if (entry.fileName != null) {
				return BaseIO.loadImage(entry.fileName);
			}
		}
		return null;
	}

	public synchronized int putImage(String res) {
		return putImage(res, BaseIO.loadImage(res));
	}

	public synchronized int putImage(Image image) {
		return putImage(TimeUtils.millis() + "|" + String.valueOf((_count + 1)), image);
	}

	public synchronized int putImage(String name, Image image) {
		checkPacked();
		if (image == null) {
			throw new LSysException("name :" + name + " the image is null");
		}
		if (image.width() <= 0 || image.height() <= 0) {
			throw new LSysException("width and height must be positive");
		}
		this._packedMap.put(name, new PackEntry(image));
		this._packing = true;
		this._count++;
		return _packedMap.size() - 1;
	}

	public synchronized int putImage(String name, LTexture tex2d) {
		if (tex2d != null) {
			return putImage(name, tex2d.getImage());
		}
		return _count;
	}

	public synchronized int putImage(LTexture tex2d) {
		if (tex2d != null) {
			return putImage(tex2d.getImage());
		}
		return _count;
	}

	public synchronized int removeImage(String name) {
		if (name != null) {
			PackEntry e = getEntry(name);
			if (e != null) {
				if (e.image != null) {
					e.image.close();
					e.image = null;
					this._count--;
					this._packing = true;
					return _packedMap.size() - 1;
				}
			}
		}
		return _count;
	}

	public synchronized int removeImage(int id) {
		if (id > -1) {
			PackEntry e = getEntry(id);
			if (e != null) {
				if (e.image != null) {
					e.image.close();
					e.image = null;
					this._count--;
					this._packing = true;
					return _packedMap.size() - 1;
				}
			}
		}
		return _count;
	}

	public int size() {
		return _count;
	}

	private synchronized Image packImage() {
		checkPacked();
		if (_packing) {
			if (_packedMap.isEmpty()) {
				throw new LSysException("Nothing to Pack !");
			}
			int maxWidth = 0;
			int maxHeight = 0;
			int totalArea = 0;

			for (int i = 0; i < _packedMap.size(); i++) {
				PackEntry entry = (PackEntry) _packedMap.get(i);
				int width = (int) entry.image.width();
				int height = (int) entry.image.height();
				if (width > maxWidth) {
					maxWidth = width;
				}
				if (height > maxHeight) {
					maxHeight = height;
				}
				totalArea += width * height;
			}

			PointI size = new PointI(closeTwoPower(maxWidth), closeTwoPower(maxHeight));
			boolean fitAll = false;
			loop: while (!fitAll) {
				int area = size.x * size.y;
				if (area < totalArea) {
					nextSize(size);
					continue;
				}
				Node root = new Node(size.x, size.y);
				for (int i = 0; i < _packedMap.size(); i++) {
					PackEntry entry = (PackEntry) _packedMap.get(i);
					Node inserted = root.insert(entry);
					if (inserted == null) {
						nextSize(size);
						continue loop;
					}
				}
				fitAll = true;
			}
			Canvas canvas = LSystem.base().graphics().createCanvas(size.x, size.y);
			for (int i = 0; i < _packedMap.size(); i++) {
				PackEntry entry = (PackEntry) _packedMap.get(i);
				canvas.draw(entry.image, entry.bounds.left, entry.bounds.top);
			}
			_packing = false;
			return canvas.getImage();
		}
		return null;
	}

	public synchronized LTexture pack() {
		if (_texture != null && !_packing && !_texture.isClosed()) {
			return _texture;
		}
		if (_glex == null) {
			_glex = LSystem.base().display().GL();
		}
		if (_fileName != null) {
			_texture = LSystem.loadTexture(_fileName);
		} else {
			Image image = packImage();
			if (image == null) {
				return null;
			}
			if (_texture != null) {
				_texture.close();
				_texture = null;
			}
			if (_colorMask != null) {
				int[] pixels = image.getPixels();
				final int size = pixels.length;
				final int color = _colorMask.getRGB();
				for (int i = 0; i < size; i++) {
					if (pixels[i] == color) {
						pixels[i] = 0xffffff;
					}
				}
				image.setPixels(pixels, (int) image.width(), (int) image.height());
			}
			_texture = image.texture();
			if (image != null) {
				image.close();
				image = null;
			}
		}
		return _texture;
	}

	public LTexture getTexture() {
		return _texture;
	}

	public PackEntry getEntry(int id) {
		return (PackEntry) _packedMap.get(id);
	}

	public PackEntry getEntry(String name) {
		return (PackEntry) _packedMap.get(name);
	}

	public int[] getIdList() {
		if (_packedMap == null || _packedMap.size() == 0) {
			return new int[] {};
		}
		IntArray ints = new IntArray();
		for (int i = 0; i < _packedMap.size(); i++) {
			Entry entry = _packedMap.getEntry(i);
			if (entry != null) {
				PackEntry e = (PackEntry) entry.getValue();
				ints.add(e.id);
			}
		}
		return ints.toArray();
	}

	public boolean saveCache() {
		if (isBatch()) {
			_texture.disposeLastCache();
			_texture.newBatchCache();
		}
		return false;
	}

	public boolean postCache() {
		if (isBatch()) {
			_texture.postLastBatchCache();
		}
		return false;
	}

	public boolean isBatch() {
		return _texture != null && _texture.isBatch();
	}

	public boolean existCache() {
		return _texture != null && _texture.existCache();
	}

	public LTexturePack glBegin() {
		if (_count > 0) {
			pack();
			_texture.glBegin();
		}
		return this;
	}

	public LTextureBatch getTextureBatch() {
		return _texture.getTextureBatch();
	}

	public LTexturePack glEnd() {
		if (_count > 0) {
			_texture.glEnd();
		}
		return this;
	}

	public boolean isBatchLocked() {
		return _texture != null && _texture.isBatchLocked();
	}

	public void initGL(GLEx g) {
		if (g == null) {
			_glex = LSystem.base().display().GL();
		} else {
			_glex = g;
		}
	}

	public PointI draw(int id, float x, float y) {
		return draw(id, x, y, 0, null);
	}

	public PointI draw(int id, float x, float y, LColor color) {
		return draw(id, x, y, 0, color);
	}

	public PointI draw(int id, float x, float y, float rotation, LColor color) {
		this.pack();
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return null;
		}
		if (_texture.isBatch()) {
			_texture.draw(x, y, entry.bounds.width(), entry.bounds.height(), entry.bounds.left, entry.bounds.top,
					entry.bounds.right, entry.bounds.bottom, rotation, color);
		} else {
			_glex.draw(_texture, x, y, entry.bounds.width(), entry.bounds.height(), entry.bounds.left, entry.bounds.top,
					entry.bounds.right - entry.bounds.left, entry.bounds.bottom - entry.bounds.top, color, rotation);
		}
		_blittedSize.set(entry.bounds.width(), entry.bounds.height());
		return _blittedSize;
	}

	public PointI draw(int id, float x, float y, float w, float h) {
		return draw(id, x, y, w, h, 0, null);
	}

	public PointI draw(int id, float x, float y, float w, float h, LColor color) {
		return draw(id, x, y, w, h, 0, color);
	}

	public PointI draw(int id, float x, float y, float w, float h, float rotation, LColor color) {
		this.pack();
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return null;
		}
		if (_texture.isBatch()) {
			_texture.draw(x, y, w, h, entry.bounds.left, entry.bounds.top, entry.bounds.right, entry.bounds.bottom,
					rotation, color);
		} else {
			_glex.draw(_texture, x, y, w, h, entry.bounds.left, entry.bounds.top,
					entry.bounds.right - entry.bounds.left, entry.bounds.bottom - entry.bounds.top, color, rotation);
		}
		_blittedSize.set(entry.bounds.width(), entry.bounds.height());
		return _blittedSize;
	}

	public PointI draw(int id, float dx1, float dy1, float dx2, float dy2, float sx1, float sy1, float sx2, float sy2) {
		return draw(id, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0);
	}

	public PointI draw(int id, float dx1, float dy1, float dx2, float dy2, float sx1, float sy1, float sx2, float sy2,
			float rotation) {
		return draw(id, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, rotation, null);
	}

	public PointI draw(int id, float dx1, float dy1, float dx2, float dy2, float sx1, float sy1, float sx2, float sy2,
			LColor color) {
		return draw(id, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0, color);
	}

	public PointI draw(int id, float dx1, float dy1, float dx2, float dy2, float sx1, float sy1, float sx2, float sy2,
			float rotation, LColor color) {
		this.pack();
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return null;
		}
		if (_texture.isBatch()) {
			_texture.draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1 + entry.bounds.top, sx2 + entry.bounds.left,
					sy2 + entry.bounds.top, rotation, color);
		} else {
			_glex.draw(_texture, dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1 + entry.bounds.top,
					sx2 + entry.bounds.right - entry.bounds.left, sy2 + entry.bounds.bottom - entry.bounds.top, color,
					rotation);
		}
		_blittedSize.set(entry.bounds.width(), entry.bounds.height());
		return _blittedSize;
	}

	public PointI draw(String name, float x, float y) {
		return draw(name, x, y, 0, null);
	}

	public PointI draw(String name, float x, float y, LColor color) {
		return draw(name, x, y, 0, color);
	}

	public void drawOnlyBatch(String name, float x, float y, LColor c) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (_texture.isBatch()) {
			_texture.draw(x, y, entry.bounds.width(), entry.bounds.height(), entry.bounds.left, entry.bounds.top,
					entry.bounds.right, entry.bounds.bottom, c);
		}
	}

	public void draw(PackEntry entry, GLEx gl, float x, float y, float rotation, LColor color) {
		this.pack();
		gl.draw(_texture, x, y, entry.bounds.width(), entry.bounds.height(), entry.bounds.left, entry.bounds.top,
				entry.bounds.right - entry.bounds.left, entry.bounds.bottom - entry.bounds.top, color, rotation);
	}

	public PointI draw(String name, float x, float y, float rotation, LColor color) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return null;
		}
		if (_texture.isBatch()) {
			_texture.draw(x, y, entry.bounds.width(), entry.bounds.height(), entry.bounds.left, entry.bounds.top,
					entry.bounds.right, entry.bounds.bottom, rotation, color);
		} else {
			_glex.draw(_texture, x, y, entry.bounds.width(), entry.bounds.height(), entry.bounds.left, entry.bounds.top,
					entry.bounds.right - entry.bounds.left, entry.bounds.bottom - entry.bounds.top, color, rotation);
		}
		_blittedSize.set(entry.bounds.width(), entry.bounds.height());
		return _blittedSize;
	}

	public PointI draw(String name, float x, float y, float w, float h) {
		return draw(name, x, y, w, h, 0, null);
	}

	public PointI draw(String name, float x, float y, float w, float h, LColor color) {
		return draw(name, x, y, w, h, 0, color);
	}

	public PointI draw(String name, float x, float y, float w, float h, float rotation, LColor color) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return null;
		}
		if (_texture.isBatch()) {
			_texture.draw(x, y, w, h, entry.bounds.left, entry.bounds.top, entry.bounds.right, entry.bounds.bottom,
					rotation, color);
		} else {
			_glex.draw(_texture, x, y, w, h, entry.bounds.left, entry.bounds.top,
					entry.bounds.right - entry.bounds.left, entry.bounds.bottom - entry.bounds.top, color, rotation);
		}
		_blittedSize.set(entry.bounds.width(), entry.bounds.height());
		return _blittedSize;
	}

	public PointI draw(String name, float dx1, float dy1, float dx2, float dy2, float sx1, float sy1, float sx2,
			float sy2) {
		return draw(name, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0);
	}

	public PointI draw(String name, float dx1, float dy1, float dx2, float dy2, float sx1, float sy1, float sx2,
			float sy2, float rotation) {
		return draw(name, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0, null);
	}

	public PointI draw(String name, float dx1, float dy1, float dx2, float dy2, float sx1, float sy1, float sx2,
			float sy2, LColor color) {
		return draw(name, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0, color);
	}

	public LTexturePack drawOnlyBatch(String name, float dx1, float dy1, float dx2, float dy2, float sx1, float sy1,
			float sx2, float sy2, LColor color) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return this;
		}
		if (_texture.isBatch()) {
			_texture.draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1 + entry.bounds.top,
					sx2 + entry.bounds.right - entry.bounds.left, sy2 + entry.bounds.bottom - entry.bounds.top, color);
		}
		_blittedSize.set(entry.bounds.width(), entry.bounds.height());
		return this;
	}

	public PointI draw(String name, float dx1, float dy1, float dx2, float dy2, float sx1, float sy1, float sx2,
			float sy2, float rotation, LColor color) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return null;
		}
		if (_texture.isBatch()) {
			_texture.draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1 + entry.bounds.top, sx2 + entry.bounds.right,
					sy2 + entry.bounds.bottom, rotation, color);
		} else {
			_glex.draw(_texture, dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1 + entry.bounds.top,
					sx2 + entry.bounds.right - entry.bounds.left, sy2 + entry.bounds.bottom - entry.bounds.top, color,
					rotation);
		}
		_blittedSize.set(entry.bounds.width(), entry.bounds.height());
		return _blittedSize;
	}

	public RectBox getImageRect(int id) {
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return new RectBox(0, 0, 0, 0);
		}
		return new RectBox(0, 0, entry.width, entry.height);
	}

	public int[] getImageRectArray(int id) {
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return new int[] { 0, 0 };
		}
		return new int[] { entry.width, entry.height };
	}

	public RectI.Range getImageSize(int id) {
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return null;
		}
		return entry.bounds;
	}

	public RectI.Range getImageSize(String name) {
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return null;
		}
		return entry.bounds;
	}

	private void nextSize(PointI size) {
		if (size.x > size.y) {
			size.y <<= 1;
		} else {
			size.x <<= 1;
		}
	}

	public String getFileName() {
		return _fileName;
	}

	private int closeTwoPower(int i) {
		int power = 1;
		while (power < i) {
			power <<= 1;
		}
		return power;
	}

	private void checkPacked() {
		if (_packed) {
			throw new LSysException("the packed !");
		}
	}

	public boolean isPacked() {
		return _packed;
	}

	public synchronized void packed() {
		this.pack();
		this._packed = true;
		this.free();
	}

	public class PackEntry {

		private final RectI.Range bounds = new RectI.Range();

		private Image image;

		private String fileName;

		private int id;

		private int width, height;

		private PackEntry(Image image) {
			this.image = image;
			if (image != null) {
				this.fileName = image.getSource();
				this.width = (int) image.width();
				this.height = (int) image.height();
			}
		}

		public int id() {
			return id;
		}

		public String name() {
			return fileName;
		}

		public RectI.Range getBounds() {
			return bounds;
		}

		public int width() {
			return bounds.width();
		}

		public int height() {
			return bounds.height();
		}
	}

	private static class Node {

		private final Node[] child = new Node[2];

		private final RectI.Range bounds = new RectI.Range();

		private PackEntry entry;

		private Node() {
		}

		private Node(int width, int height) {
			bounds.set(0, 0, width, height);
		}

		private boolean isLeaf() {
			return (child[0] == null) && (child[1] == null);
		}

		private Node insert(PackEntry entry) {
			if (isLeaf()) {
				if (this.entry != null) {
					return null;
				}
				int width = (int) entry.image.width();
				int height = (int) entry.image.height();

				if ((width > bounds.width()) || (height > bounds.height())) {
					return null;
				}

				if ((width == bounds.width()) && (height == bounds.height())) {
					this.entry = entry;
					this.entry.bounds.set(this.bounds);
					return this;
				}

				child[0] = new Node();
				child[1] = new Node();

				int dw = bounds.width() - width;
				int dh = bounds.height() - height;

				if (dw > dh) {
					child[0].bounds.set(bounds.left, bounds.top, bounds.left + width, bounds.bottom);
					child[1].bounds.set(bounds.left + width, bounds.top, bounds.right, bounds.bottom);
				} else {
					child[0].bounds.set(bounds.left, bounds.top, bounds.right, bounds.top + height);
					child[1].bounds.set(bounds.left, bounds.top + height, bounds.right, bounds.bottom);
				}
				return child[0].insert(entry);
			} else {
				Node newNode = child[0].insert(entry);
				if (newNode != null) {
					return newNode;
				}
				return child[1].insert(entry);
			}
		}

	}

	public String getName() {
		return _name;
	}

	public LColor getColorMask() {
		return _colorMask;
	}

	public LTexturePack setColorMask(LColor colorMask) {
		this._colorMask = colorMask;
		return this;
	}

	private LTexturePack free() {
		if (_packedMap != null) {
			for (int i = 0; i < _packedMap.size(); i++) {
				PackEntry e = (PackEntry) _packedMap.get(i);
				if (e != null) {
					if (e.image != null) {
						e.image.close();
						e.image = null;
					}
				}
			}
		}
		return this;
	}

	@Override
	public String toString() {
		StrBuilder sbr = new StrBuilder(1024);
		sbr.append("<?xml version=\"1.0\" standalone=\"yes\" ?>\n");
		if (_colorMask != null) {
			sbr.append("<pack file=\"" + _fileName + "\" mask=\"" + _colorMask.getRed() + "," + _colorMask.getGreen()
					+ "," + _colorMask.getBlue() + "\">\n");
		} else {
			sbr.append("<pack file=\"" + _fileName + "\">\n");
		}
		for (int i = 0; i < _packedMap.size(); i++) {
			PackEntry e = (PackEntry) _packedMap.get(i);
			if (e != null && e.bounds != null) {
				sbr.append("<block id=\"" + i + "\" name=\"" + e.fileName + "\" left=\"" + e.bounds.left + "\" top=\""
						+ e.bounds.top + "\" right=\"" + e.bounds.right + "\" bottom=\"" + e.bounds.bottom + "\"/>\n");
			}
		}
		sbr.append("</pack>");
		return sbr.toString();
	}

	public LTexturePack setDisabledTexture(boolean d) {
		if (this._texture != null) {
			this._texture.setDisabledTexture(d);
		}
		return this;
	}

	public boolean isDisabledTexture() {
		return this._texture == null ? false : this._texture.isDisabledTexture();
	}

	public boolean closed() {
		return _texture == null || _texture.isClosed();
	}

	@Override
	public synchronized void close() {
		free();
		if (_texture != null) {
			_texture.close();
			_texture = null;
		}
		_packedMap.clear();
	}
}
