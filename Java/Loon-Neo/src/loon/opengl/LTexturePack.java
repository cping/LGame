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
import loon.LSystem;
import loon.LTexture;
import loon.LTextureBatch;
import loon.LTextures;
import loon.LTexture.Format;
import loon.action.sprite.SpriteRegion;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.geom.PointI;
import loon.geom.RectBox;
import loon.geom.RectI;
import loon.utils.ArrayMap;
import loon.utils.IntArray;
import loon.utils.ArrayMap.Entry;
import loon.utils.TArray;
import loon.utils.xml.XMLDocument;
import loon.utils.xml.XMLElement;
import loon.utils.xml.XMLParser;

public class LTexturePack implements LRelease {

	private GLEx _glex;

	private final PointI blittedSize = new PointI();

	private final ArrayMap temps = new ArrayMap();

	private LTexture texture = null;

	private int count;

	LColor colorMask;

	boolean useAlpha, packed, packing;

	private String fileName, name;

	private Format format = Format.DEFAULT;

	public LTexture getTexture(String name) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return null;
		}
		return texture.copy(entry.bounds.left, entry.bounds.top, entry.bounds.right - entry.bounds.left,
				entry.bounds.bottom - entry.bounds.top);
	}

	public LTexture getTextureAll(String name) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return null;
		}
		return texture.copy(entry.bounds.left, entry.bounds.top, entry.bounds.right, entry.bounds.bottom);
	}

	public LTexture getTexture(int id) {
		this.pack();
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return null;
		}
		return texture.copy(entry.bounds.left, entry.bounds.top, entry.bounds.right - entry.bounds.left,
				entry.bounds.bottom - entry.bounds.top);
	}

	public LTexture getTextureAll(int id) {
		this.pack();
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return null;
		}
		return texture.copy(entry.bounds.left, entry.bounds.top, entry.bounds.right, entry.bounds.bottom);
	}

	public SpriteRegion createSpriteRegion(int id) {
		this.pack();
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return null;
		}
		SpriteRegion region = new SpriteRegion(texture, entry.bounds.left, entry.bounds.top,
				entry.bounds.right - entry.bounds.left, entry.bounds.bottom - entry.bounds.top);
		return region;
	}

	public SpriteRegion createSpriteRegionAll(int id) {
		this.pack();
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return null;
		}
		SpriteRegion region = new SpriteRegion(texture, entry.bounds.left, entry.bounds.top, entry.bounds.right,
				entry.bounds.bottom);
		return region;
	}

	public SpriteRegion createSpriteRegion(String name) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return null;
		}
		SpriteRegion region = new SpriteRegion(texture, entry.bounds.left, entry.bounds.top,
				entry.bounds.right - entry.bounds.left, entry.bounds.bottom - entry.bounds.top);
		return region;
	}

	public SpriteRegion createSpriteRegionAll(String name) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return null;
		}
		SpriteRegion region = new SpriteRegion(texture, entry.bounds.left, entry.bounds.top, entry.bounds.right,
				entry.bounds.bottom);
		return region;
	}

	public LTextureRegion createTextureRegion(int id) {
		this.pack();
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return null;
		}
		LTextureRegion region = new LTextureRegion(texture, entry.bounds.left, entry.bounds.top,
				entry.bounds.right - entry.bounds.left, entry.bounds.bottom - entry.bounds.top);
		return region;
	}

	public LTextureRegion createTextureRegionAll(int id) {
		this.pack();
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return null;
		}
		LTextureRegion region = new LTextureRegion(texture, entry.bounds.left, entry.bounds.top, entry.bounds.right,
				entry.bounds.bottom);
		return region;
	}

	public LTextureRegion createTextureRegion(String name) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return null;
		}
		LTextureRegion region = new LTextureRegion(texture, entry.bounds.left, entry.bounds.top,
				entry.bounds.right - entry.bounds.left, entry.bounds.bottom - entry.bounds.top);
		return region;
	}

	public LTextureRegion createTextureRegionAll(String name) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return null;
		}
		LTextureRegion region = new LTextureRegion(texture, entry.bounds.left, entry.bounds.top, entry.bounds.right,
				entry.bounds.bottom);
		return region;
	}

	public LTexturePack() {
		this(true);
	}

	public LTexturePack(boolean hasAlpha) {
		this.useAlpha = hasAlpha;
	}

	public LTexturePack(String path) {
		if (path == null || "".equals(path)) {
			throw new RuntimeException(path + " not found !");
		}
		set(path);
	}

	public LTexturePack(String fileName, TArray<LTexturePackClip> clips) {
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
		set(doc);
	}

	public LTexturePack(XMLElement pack) {
		set(pack);
	}

	private void set(String path) {
		XMLDocument doc = XMLParser.parse(path);
		XMLElement pack = doc.getRoot();
		set(pack);
	}

	private void set(XMLElement pack) {
		this.fileName = pack.getAttribute("file", null);
		this.name = pack.getAttribute("name", fileName);
		int r = pack.getIntAttribute("r", -1);
		int g = pack.getIntAttribute("g", -1);
		int b = pack.getIntAttribute("b", -1);
		int a = pack.getIntAttribute("a", -1);
		if (r != -1 && g != -1 && b != -1 && a != -1) {
			colorMask = new LColor(r, g, b, a);
		}
		if (fileName != null) {
			TArray<XMLElement> blocks = pack.list("block");
			for (XMLElement e : blocks) {
				PackEntry entry = new PackEntry(null);
				final int id = e.getIntAttribute("id", count);
				entry.id = id;
				entry.fileName = e.getAttribute("name", null);
				entry.bounds.left = e.getIntAttribute("left", 0);
				entry.bounds.top = e.getIntAttribute("top", 0);
				entry.bounds.right = e.getIntAttribute("right", 0);
				entry.bounds.bottom = e.getIntAttribute("bottom", 0);
				if (entry.fileName != null) {
					temps.put(entry.fileName, entry);
				} else {
					temps.put(String.valueOf(id), entry);
				}
				count++;
			}
			this.packing = false;
			this.packed = true;
		}
		this.useAlpha = true;
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
		return putImage(System.currentTimeMillis() + "|" + String.valueOf((count + 1)), image);
	}

	public synchronized int putImage(String name, Image image) {
		checkPacked();
		if (image == null) {
			throw new NullPointerException();
		}
		if (image.width() <= 0 || image.height() <= 0) {
			throw new IllegalArgumentException("width and height must be positive");
		}
		this.temps.put(name, new PackEntry(image));
		this.packing = true;
		this.count++;
		return temps.size() - 1;
	}

	public synchronized int putImage(String name, LTexture tex2d) {
		if (tex2d != null) {
			return putImage(name, tex2d.getImage());
		}
		return count;
	}

	public synchronized int putImage(LTexture tex2d) {
		if (tex2d != null) {
			return putImage(tex2d.getImage());
		}
		return count;
	}

	public synchronized int removeImage(String name) {
		if (name != null) {
			PackEntry e = getEntry(name);
			if (e != null) {
				if (e.image != null) {
					e.image.close();
					e.image = null;
					this.count--;
					this.packing = true;
					return temps.size() - 1;
				}
			}
		}
		return count;
	}

	public synchronized int removeImage(int id) {
		if (id > -1) {
			PackEntry e = getEntry(id);
			if (e != null) {
				if (e.image != null) {
					e.image.close();
					e.image = null;
					this.count--;
					this.packing = true;
					return temps.size() - 1;
				}
			}
		}
		return count;
	}

	public int size() {
		return count;
	}

	private synchronized Image packImage() {
		checkPacked();
		if (packing) {
			if (temps.isEmpty()) {
				throw new IllegalStateException("Nothing to Pack !");
			}
			int maxWidth = 0;
			int maxHeight = 0;
			int totalArea = 0;

			for (int i = 0; i < temps.size(); i++) {
				PackEntry entry = (PackEntry) temps.get(i);
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
				for (int i = 0; i < temps.size(); i++) {
					PackEntry entry = (PackEntry) temps.get(i);
					Node inserted = root.insert(entry);
					if (inserted == null) {
						nextSize(size);
						continue loop;
					}
				}
				fitAll = true;
			}
			Canvas canvas = LSystem.base().graphics().createCanvas(size.x, size.y);
			for (int i = 0; i < temps.size(); i++) {
				PackEntry entry = (PackEntry) temps.get(i);
				canvas.draw(entry.image, entry.bounds.left, entry.bounds.top);
			}
			packing = false;
			return canvas.image;
		}
		return null;
	}

	public synchronized LTexture pack() {
		return pack(format);
	}

	public synchronized LTexture pack(Format format) {
		if (texture != null && !packing) {
			return texture;
		}
		if (_glex == null) {
			_glex = LSystem.base().display().GL();
		}
		if (fileName != null) {
			texture = LTextures.loadTexture(fileName, format);
		} else {

			Image image = packImage();
			if (image == null) {
				return null;
			}
			if (texture != null) {
				texture.close();
				texture = null;
			}

			if (colorMask != null) {
				int[] pixels = image.getPixels();
				int size = pixels.length;
				int color = colorMask.getRGB();
				for (int i = 0; i < size; i++) {
					if (pixels[i] == color) {
						pixels[i] = 0xffffff;
					}
				}
				image.setPixels(pixels, (int) image.width(), (int) image.height());
			}
			texture = image.texture();
			if (image != null) {
				image.close();
				image = null;
			}
		}
		return texture;
	}

	public LTexture getTexture() {
		return texture;
	}

	public PackEntry getEntry(int id) {
		return (PackEntry) temps.get(id);
	}

	public PackEntry getEntry(String name) {
		return (PackEntry) temps.get(name);
	}

	public int[] getIdList() {
		if (temps == null || temps.size() == 0) {
			return new int[] {};
		}
		IntArray ints = new IntArray();
		for (int i = 0; i < temps.size(); i++) {
			Entry entry = (Entry) temps.getEntry(i);
			if (entry != null) {
				PackEntry e = (PackEntry) entry.getValue();
				ints.add(e.id);
			}
		}
		return ints.toArray();
	}

	public void saveCache() {
		if (texture != null) {
			texture.disposeLastCache();
			texture.newBatchCache();
		}
	}

	public void postCache() {
		if (texture != null) {
			texture.postLastBatchCache();
		}
	}

	public boolean isBatch() {
		return texture != null && texture.isBatch();
	}

	public void glBegin() {
		if (count > 0) {
			pack();
			texture.glBegin();
		}
	}

	public LTextureBatch getTextureBatch() {
		return texture.getTextureBatch();
	}

	public void glEnd() {
		if (count > 0) {
			texture.glEnd();
		}
	}

	public boolean isBatchLocked() {
		return texture != null && texture.isBatchLocked();
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
		if (texture.isBatch()) {
			texture.draw(x, y, entry.bounds.width(), entry.bounds.height(), entry.bounds.left, entry.bounds.top,
					entry.bounds.right, entry.bounds.bottom, rotation, color);
		} else {
			_glex.draw(texture, x, y, entry.bounds.width(), entry.bounds.height(), entry.bounds.left, entry.bounds.top,
					entry.bounds.right - entry.bounds.left, entry.bounds.bottom - entry.bounds.top, color, rotation);
		}
		blittedSize.set(entry.bounds.width(), entry.bounds.height());
		return blittedSize;
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
		if (texture.isBatch()) {
			texture.draw(x, y, w, h, entry.bounds.left, entry.bounds.top, entry.bounds.right, entry.bounds.bottom,
					rotation, color);
		} else {
			_glex.draw(texture, x, y, w, h, entry.bounds.left, entry.bounds.top, entry.bounds.right - entry.bounds.left,
					entry.bounds.bottom - entry.bounds.top, color, rotation);
		}
		blittedSize.set(entry.bounds.width(), entry.bounds.height());

		return blittedSize;
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
		if (texture.isBatch()) {
			texture.draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1 + entry.bounds.top, sx2 + entry.bounds.left,
					sy2 + entry.bounds.top, rotation, color);
		} else {
			_glex.draw(texture, dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1 + entry.bounds.top,
					sx2 + entry.bounds.right - entry.bounds.left, sy2 + entry.bounds.bottom - entry.bounds.top, color,
					rotation);
		}
		blittedSize.set(entry.bounds.width(), entry.bounds.height());
		return blittedSize;
	}

	public PointI draw(String name, float x, float y) {
		return draw(name, x, y, 0, null);
	}

	public PointI draw(String name, float x, float y, LColor color) {
		return draw(name, x, y, 0, color);
	}

	public void drawOnlyBatch(String name, float x, float y, LColor[] c) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (texture.isBatch()) {
			texture.draw(x, y, entry.bounds.width(), entry.bounds.height(), entry.bounds.left, entry.bounds.top,
					entry.bounds.right, entry.bounds.bottom, c);
		}
	}

	public PointI draw(String name, float x, float y, float rotation, LColor color) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return null;
		}
		if (texture.isBatch()) {
			texture.draw(x, y, entry.bounds.width(), entry.bounds.height(), entry.bounds.left, entry.bounds.top,
					entry.bounds.right, entry.bounds.bottom, rotation, color);
		} else {
			_glex.draw(texture, x, y, entry.bounds.width(), entry.bounds.height(), entry.bounds.left, entry.bounds.top,
					entry.bounds.right - entry.bounds.left, entry.bounds.bottom - entry.bounds.top, color, rotation);
		}
		blittedSize.set(entry.bounds.width(), entry.bounds.height());
		return blittedSize;
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
		if (texture.isBatch()) {
			texture.draw(x, y, w, h, entry.bounds.left, entry.bounds.top, entry.bounds.right, entry.bounds.bottom,
					rotation, color);
		} else {
			_glex.draw(texture, x, y, w, h, entry.bounds.left, entry.bounds.top, entry.bounds.right - entry.bounds.left,
					entry.bounds.bottom - entry.bounds.top, color, rotation);
		}
		blittedSize.set(entry.bounds.width(), entry.bounds.height());
		return blittedSize;
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

	public void drawOnlyBatch(String name, float dx1, float dy1, float dx2, float dy2, float sx1, float sy1, float sx2,
			float sy2, LColor[] color) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return;
		}
		if (texture.isBatch()) {
			texture.draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1 + entry.bounds.top,
					sx2 + entry.bounds.right - entry.bounds.left, sy2 + entry.bounds.bottom - entry.bounds.top, color);
		}
		blittedSize.set(entry.bounds.width(), entry.bounds.height());
	}

	public PointI draw(String name, float dx1, float dy1, float dx2, float dy2, float sx1, float sy1, float sx2,
			float sy2, float rotation, LColor color) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return null;
		}
		if (texture.isBatch()) {
			texture.draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1 + entry.bounds.top, sx2 + entry.bounds.right,
					sy2 + entry.bounds.bottom, rotation, color);
		} else {
			_glex.draw(texture, dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1 + entry.bounds.top,
					sx2 + entry.bounds.right - entry.bounds.left, sy2 + entry.bounds.bottom - entry.bounds.top, color,
					rotation);
		}
		blittedSize.set(entry.bounds.width(), entry.bounds.height());
		return blittedSize;
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
		return fileName;
	}

	private int closeTwoPower(int i) {
		int power = 1;
		while (power < i) {
			power <<= 1;
		}
		return power;
	}

	private void checkPacked() {
		if (packed) {
			throw new IllegalStateException("the packed !");
		}
	}

	public void packed() {
		this.packed(Format.DEFAULT);
	}

	public synchronized void packed(Format format) {
		this.pack(format);
		this.packed = true;
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

	private class Node {

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
		return name;
	}

	public LColor getColorMask() {
		return colorMask;
	}

	public void setColorMask(LColor colorMask) {
		this.colorMask = colorMask;
	}

	private void free() {
		if (temps != null) {
			for (int i = 0; i < temps.size(); i++) {
				PackEntry e = (PackEntry) temps.get(i);
				if (e != null) {
					if (e.image != null) {
						e.image.close();
						e.image = null;
					}
				}
			}
		}
	}

	public String toString() {
		StringBuffer sbr = new StringBuffer(1000);
		sbr.append("<?xml version=\"1.0\" standalone=\"yes\" ?>\n");
		if (colorMask != null) {
			sbr.append("<pack file=\"" + fileName + "\" mask=\"" + colorMask.getRed() + "," + colorMask.getGreen() + ","
					+ colorMask.getBlue() + "\">\n");
		} else {
			sbr.append("<pack file=\"" + fileName + "\">\n");
		}
		for (int i = 0; i < temps.size(); i++) {
			PackEntry e = (PackEntry) temps.get(i);
			if (e != null && e.bounds != null) {
				sbr.append("<block id=\"" + i + "\" name=\"" + e.fileName + "\" left=\"" + e.bounds.left + "\" top=\""
						+ e.bounds.top + "\" right=\"" + e.bounds.right + "\" bottom=\"" + e.bounds.bottom + "\"/>\n");
			}
		}
		sbr.append("</pack>");
		return sbr.toString();
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public synchronized void close() {
		free();
		if (texture != null) {
			texture.close();
			texture = null;
		}
	}
}
