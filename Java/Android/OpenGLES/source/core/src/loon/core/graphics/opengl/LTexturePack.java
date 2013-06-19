package loon.core.graphics.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import loon.action.sprite.SpriteRegion;
import loon.core.LRelease;
import loon.core.geom.RectBox;
import loon.core.geom.Point.Point2i;
import loon.core.geom.RectBox.Rect2i;
import loon.core.graphics.LColor;
import loon.core.graphics.LImage;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.resource.Resources;
import loon.utils.collection.ArrayMap;
import loon.utils.xml.XMLDocument;
import loon.utils.xml.XMLElement;
import loon.utils.xml.XMLParser;

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
public class LTexturePack implements LRelease {

	private final Point2i blittedSize = new Point2i();

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
		return texture.getSubTexture(entry.bounds.left, entry.bounds.top,
				entry.bounds.right, entry.bounds.bottom);
	}

	public LTexture getTexture(int id) {
		this.pack();
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return null;
		}
		return texture.getSubTexture(entry.bounds.left, entry.bounds.top,
				entry.bounds.right, entry.bounds.bottom);
	}
	
	public SpriteRegion createSpriteRegion(int id) {
		this.pack();
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return null;
		}
		SpriteRegion region = new SpriteRegion(texture, entry.bounds.left,
				entry.bounds.top, entry.bounds.right, entry.bounds.bottom);
		return region;
	}

	public SpriteRegion createSpriteRegion(String name) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return null;
		}
		SpriteRegion region = new SpriteRegion(texture, entry.bounds.left,
				entry.bounds.top, entry.bounds.right, entry.bounds.bottom);
		return region;
	}

	public LTextureRegion createTextureRegion(int id) {
		this.pack();
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return null;
		}
		LTextureRegion region = new LTextureRegion(texture, entry.bounds.left,
				entry.bounds.top, entry.bounds.right, entry.bounds.bottom);
		return region;
	}

	public LTextureRegion createTextureRegion(String name) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return null;
		}
		LTextureRegion region = new LTextureRegion(texture, entry.bounds.left,
				entry.bounds.top, entry.bounds.right, entry.bounds.bottom);
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
		try {
			set(Resources.openResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public LTexturePack(InputStream in) {
		set(in);
	}

	public LTexturePack(XMLElement pack) {
		set(pack);
	}

	private void set(InputStream in) {
		XMLDocument doc = XMLParser.parse(in);
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
			ArrayList<XMLElement> blocks = pack.list("block");
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

	public synchronized LImage getImage(int id) {
		PackEntry entry = getEntry(id);
		if (entry != null) {
			if (entry.image != null && !entry.image.isClose()) {
				return entry.image;
			} else if (entry.fileName != null) {
				return LImage.createImage(entry.fileName);
			}
		}
		return null;
	}

	public synchronized LImage getImage(String name) {
		PackEntry entry = getEntry(name);
		if (entry != null) {
			if (entry.image != null && !entry.image.isClose()) {
				return entry.image;
			} else if (entry.fileName != null) {
				return LImage.createImage(entry.fileName);
			}
		}
		return null;
	}

	public synchronized int putImage(String res) {
		return putImage(res, LImage.createImage(res));
	}

	public synchronized int putImage(LImage image) {
		return putImage(
				System.currentTimeMillis() + "|" + String.valueOf((count + 1)),
				image);
	}

	public synchronized int putImage(String name, LImage image) {
		checkPacked();
		if (image == null) {
			throw new NullPointerException();
		}
		if (image.getWidth() <= 0 || image.getHeight() <= 0) {
			throw new IllegalArgumentException(
					"width and height must be positive");
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
					e.image.dispose();
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
					e.image.dispose();
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

	private synchronized LImage packImage() {
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
				int width = entry.image.getWidth();
				int height = entry.image.getHeight();
				if (width > maxWidth) {
					maxWidth = width;
				}
				if (height > maxHeight) {
					maxHeight = height;
				}
				totalArea += width * height;
			}
			Point2i size = new Point2i(closeTwoPower(maxWidth),
					closeTwoPower(maxHeight));
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
			LImage image = new LImage(size.x, size.y, useAlpha);
			LGraphics g = image.getLGraphics();
			for (int i = 0; i < temps.size(); i++) {
				PackEntry entry = (PackEntry) temps.get(i);
				g.drawImage(entry.image, entry.bounds.left, entry.bounds.top);
			}
			g.dispose();
			g = null;
			packing = false;
			return image;
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
		if (fileName != null) {
			texture = new LTexture(GLLoader.getTextureData(fileName), format);
		} else {
			LImage image = packImage();
			if (image == null) {
				return null;
			}
			if (texture != null) {
				texture.destroy();
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
				image.setPixels(pixels, image.getWidth(), image.getHeight());
			}
			texture = new LTexture(GLLoader.getTextureData(image), format);
			if (image != null) {
				image.dispose();
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

	public void glCache() {
		if (texture != null) {
			texture.glLock();
			texture.glCacheCommit();
			texture.glUnLock();
		}
	}

	public boolean isBatch() {
		return texture != null && texture.isBatch();
	}

	public void glBegin(int mode) {
		if (count > 0) {
			pack();
			texture.glBegin(mode);
		}
	}

	public void glBegin() {
		if (count > 0) {
			pack();
			texture.glBegin();
		}
	}

	public void glEnd() {
		if (count > 0) {
			texture.glEnd();
		}
	}

	public boolean isBatchLocked() {
		return texture != null && texture.isBatchLocked();
	}

	public Point2i draw(int id, float x, float y) {
		return draw(id, x, y, 0, null);
	}

	public Point2i draw(int id, float x, float y, LColor color) {
		return draw(id, x, y, 0, color);
	}

	public Point2i draw(int id, float x, float y, float rotation, LColor color) {
		this.pack();
		if (GLEx.self != null) {
			PackEntry entry = getEntry(id);
			if (entry == null) {
				return null;
			}
			if (texture.isBatch()) {
				texture.draw(x, y, entry.bounds.width(), entry.bounds.height(),
						entry.bounds.left, entry.bounds.top,
						entry.bounds.right, entry.bounds.bottom, rotation,
						color);
			} else {
				GLEx.self.drawTexture(texture, x, y, entry.bounds.width(),
						entry.bounds.height(), entry.bounds.left,
						entry.bounds.top, entry.bounds.right,
						entry.bounds.bottom, rotation, color);
			}
			blittedSize.set(entry.bounds.width(), entry.bounds.height());
		}
		return blittedSize;
	}

	public void drawOnlyBatch(int id, float x, float y, LColor[] c) {
		this.pack();
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return;
		}
		if (texture.isBatch()) {
			texture.draw(x, y, entry.bounds.width(), entry.bounds.height(),
					entry.bounds.left, entry.bounds.top, entry.bounds.right,
					entry.bounds.bottom, c);
		}
	}

	public Point2i draw(int id, float x, float y, float w, float h) {
		return draw(id, x, y, w, h, 0, null);
	}

	public Point2i draw(int id, float x, float y, float w, float h, LColor color) {
		return draw(id, x, y, w, h, 0, color);
	}

	public Point2i draw(int id, float x, float y, float w, float h,
			float rotation, LColor color) {
		this.pack();
		if (GLEx.self != null) {
			PackEntry entry = getEntry(id);
			if (entry == null) {
				return null;
			}
			if (texture.isBatch()) {
				texture.draw(x, y, w, h, entry.bounds.left, entry.bounds.top,
						entry.bounds.right, entry.bounds.bottom, rotation,
						color);
			} else {
				GLEx.self.drawTexture(texture, x, y, w, h, entry.bounds.left,
						entry.bounds.top, entry.bounds.right,
						entry.bounds.bottom, rotation, color);
			}
			blittedSize.set(entry.bounds.width(), entry.bounds.height());
		}
		return blittedSize;
	}

	public Point2i draw(int id, float dx1, float dy1, float dx2, float dy2,
			float sx1, float sy1, float sx2, float sy2) {
		return draw(id, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0);
	}

	public Point2i draw(int id, float dx1, float dy1, float dx2, float dy2,
			float sx1, float sy1, float sx2, float sy2, float rotation) {
		return draw(id, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, rotation, null);
	}

	public Point2i draw(int id, float dx1, float dy1, float dx2, float dy2,
			float sx1, float sy1, float sx2, float sy2, LColor color) {
		return draw(id, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0, color);
	}

	public void drawOnlyBatch(int id, float dx1, float dy1, float dx2,
			float dy2, float sx1, float sy1, float sx2, float sy2,
			LColor[] color) {
		this.pack();
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return;
		}
		if (texture.isBatch()) {
			texture.draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1
					+ entry.bounds.top, sx2 + entry.bounds.left, sy2
					+ entry.bounds.top, color);
		}
		blittedSize.set(entry.bounds.width(), entry.bounds.height());
	}

	public Point2i draw(int id, float dx1, float dy1, float dx2, float dy2,
			float sx1, float sy1, float sx2, float sy2, float rotation,
			LColor color) {
		this.pack();
		if (GLEx.self != null) {
			PackEntry entry = getEntry(id);
			if (entry == null) {
				return null;
			}
			if (texture.isBatch()) {
				texture.draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1
						+ entry.bounds.top, sx2 + entry.bounds.left, sy2
						+ entry.bounds.top, rotation, color);
			} else {
				GLEx.self.drawTexture(texture, dx1, dy1, dx2, dy2, sx1
						+ entry.bounds.left, sy1 + entry.bounds.top, sx2
						+ entry.bounds.left, sy2 + entry.bounds.top, rotation,
						color);
			}
			blittedSize.set(entry.bounds.width(), entry.bounds.height());
		}
		return blittedSize;
	}

	public Point2i draw(String name, float x, float y) {
		return draw(name, x, y, 0, null);
	}

	public Point2i draw(String name, float x, float y, LColor color) {
		return draw(name, x, y, 0, color);
	}

	public void drawOnlyBatch(String name, float x, float y, LColor[] c) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (texture.isBatch()) {
			texture.draw(x, y, entry.bounds.width(), entry.bounds.height(),
					entry.bounds.left, entry.bounds.top, entry.bounds.right,
					entry.bounds.bottom, c);
		}
	}

	public Point2i draw(String name, float x, float y, float rotation,
			LColor color) {
		this.pack();
		if (GLEx.self != null) {
			PackEntry entry = getEntry(name);
			if (entry == null) {
				return null;
			}
			if (texture.isBatch()) {
				texture.draw(x, y, entry.bounds.width(), entry.bounds.height(),
						entry.bounds.left, entry.bounds.top,
						entry.bounds.right, entry.bounds.bottom, rotation,
						color);
			} else {
				GLEx.self.drawTexture(texture, x, y, entry.bounds.width(),
						entry.bounds.height(), entry.bounds.left,
						entry.bounds.top, entry.bounds.right,
						entry.bounds.bottom, rotation, color);
			}
			blittedSize.set(entry.bounds.width(), entry.bounds.height());
		}
		return blittedSize;
	}

	public Point2i draw(String name, float x, float y, float w, float h) {
		return draw(name, x, y, w, h, 0, null);
	}

	public Point2i draw(String name, float x, float y, float w, float h,
			LColor color) {
		return draw(name, x, y, w, h, 0, color);
	}

	public Point2i draw(String name, float x, float y, float w, float h,
			float rotation, LColor color) {
		this.pack();
		if (GLEx.self != null) {
			PackEntry entry = getEntry(name);
			if (entry == null) {
				return null;
			}
			if (texture.isBatch()) {
				texture.draw(x, y, w, h, entry.bounds.left, entry.bounds.top,
						entry.bounds.right, entry.bounds.bottom, rotation,
						color);
			} else {
				GLEx.self.drawTexture(texture, x, y, w, h, entry.bounds.left,
						entry.bounds.top, entry.bounds.right,
						entry.bounds.bottom, rotation, color);
			}
			blittedSize.set(entry.bounds.width(), entry.bounds.height());
		}
		return blittedSize;
	}

	public Point2i draw(String name, float dx1, float dy1, float dx2,
			float dy2, float sx1, float sy1, float sx2, float sy2) {
		return draw(name, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0);
	}

	public Point2i draw(String name, float dx1, float dy1, float dx2,
			float dy2, float sx1, float sy1, float sx2, float sy2,
			float rotation) {
		return draw(name, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0, null);
	}

	public Point2i draw(String name, float dx1, float dy1, float dx2,
			float dy2, float sx1, float sy1, float sx2, float sy2, LColor color) {
		return draw(name, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0, color);
	}

	public void drawOnlyBatch(String name, float dx1, float dy1, float dx2,
			float dy2, float sx1, float sy1, float sx2, float sy2,
			LColor[] color) {
		this.pack();
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return;
		}
		if (texture.isBatch()) {
			texture.draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1
					+ entry.bounds.top, sx2 + entry.bounds.left, sy2
					+ entry.bounds.top, color);
		}
		blittedSize.set(entry.bounds.width(), entry.bounds.height());
	}

	public Point2i draw(String name, float dx1, float dy1, float dx2,
			float dy2, float sx1, float sy1, float sx2, float sy2,
			float rotation, LColor color) {
		this.pack();
		if (GLEx.self != null) {
			PackEntry entry = getEntry(name);
			if (entry == null) {
				return null;
			}
			if (texture.isBatch()) {
				texture.draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1
						+ entry.bounds.top, sx2 + entry.bounds.left, sy2
						+ entry.bounds.top, rotation, color);
			} else {
				GLEx.self.drawTexture(texture, dx1, dy1, dx2, dy2, sx1
						+ entry.bounds.left, sy1 + entry.bounds.top, sx2
						+ entry.bounds.left, sy2 + entry.bounds.top, rotation,
						color);
			}
			blittedSize.set(entry.bounds.width(), entry.bounds.height());
		}
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

	public Rect2i getImageSize(int id) {
		PackEntry entry = getEntry(id);
		if (entry == null) {
			return null;
		}
		return entry.bounds;
	}

	public Rect2i getImageSize(String name) {
		PackEntry entry = getEntry(name);
		if (entry == null) {
			return null;
		}
		return entry.bounds;
	}

	private void nextSize(Point2i size) {
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

		private final Rect2i bounds = new Rect2i();

		private LImage image;

		private String fileName;

		private int id;

		private int width, height;

		private PackEntry(LImage image) {
			this.image = image;
			if (image != null) {
				this.fileName = image.getPath();
				this.width = image.getWidth();
				this.height = image.getHeight();
			}
		}

		public int id() {
			return id;
		}

		public String name() {
			return fileName;
		}

		public Rect2i getBounds() {
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

		private final Rect2i bounds = new Rect2i();

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
				int width = entry.image.getWidth();
				int height = entry.image.getHeight();

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
					child[0].bounds.set(bounds.left, bounds.top, bounds.left
							+ width, bounds.bottom);
					child[1].bounds.set(bounds.left + width, bounds.top,
							bounds.right, bounds.bottom);
				} else {
					child[0].bounds.set(bounds.left, bounds.top, bounds.right,
							bounds.top + height);
					child[1].bounds.set(bounds.left, bounds.top + height,
							bounds.right, bounds.bottom);
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
						e.image.dispose();
						e.image = null;
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer sbr = new StringBuffer(1000);
		sbr.append("<?xml version=\"1.0\" standalone=\"yes\" ?>\n");
		if (colorMask != null) {
			sbr.append("<pack file=\"" + fileName + "\" mask=\""
					+ colorMask.getRed() + "," + colorMask.getGreen() + ","
					+ colorMask.getBlue() + "\">\n");
		} else {
			sbr.append("<pack file=\"" + fileName + "\">\n");
		}
		for (int i = 0; i < temps.size(); i++) {
			PackEntry e = (PackEntry) temps.get(i);
			if (e != null && e.bounds != null) {
				sbr.append("<block id=\"" + i + "\" name=\"" + e.fileName
						+ "\" left=\"" + e.bounds.left + "\" top=\""
						+ e.bounds.top + "\" right=\"" + e.bounds.right
						+ "\" bottom=\"" + e.bounds.bottom + "\"/>\n");
			}
		}
		sbr.append("</pack>");
		return sbr.toString();
	}

	@Override
	public synchronized void dispose() {
		free();
		if (texture != null) {
			texture.destroy();
			texture = null;
		}
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

}
