package loon.core.graphics.opengl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import loon.action.sprite.SpriteBatch;
import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.resource.Resources;
import loon.utils.CollectionUtils;
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
public class LTextureList implements LRelease {

	private final int width = LSystem.screenRect.width;

	private final int height = LSystem.screenRect.height;

	private class ImageData {

		public int index;

		public int x;

		public int y;

		public int w;

		public int h;

		public float scale;

		public int scaleType;

		public LColor mask;

		public String ref;
	}

	private HashMap<String, ImageData> imageList;

	public LTextureList(String res) {
		this(Resources.getResourceAsStream(res));
	}

	public LTextureList(InputStream in) {
		this.imageList = new HashMap<String, ImageData>(10);
		this.autoExpand = false;
		this.visible = true;

		int index = 0;

		final String x = "x", y = "y", w = "w", h = "h";
		final String scale = "scale", src = "src", maskName = "mask", empty = "empty";
		final String name = "name", filterName = "filter", n = "nearest", l = "linear";

		XMLDocument doc = XMLParser.parse(in);
		ArrayList<XMLElement> images = doc.getRoot().find("image");

		if (images.size() > 0) {
			Iterator<XMLElement> it = images.iterator();
			for (; it.hasNext();) {
				XMLElement ele = it.next();
				if (ele != null) {
					ImageData data = new ImageData();
					data.x = ele.getIntAttribute(x, 0);
					data.y = ele.getIntAttribute(y, 0);
					data.w = ele.getIntAttribute(w, 0);
					data.h = ele.getIntAttribute(h, 0);
					data.scale = ele.getFloatAttribute(scale, 0);
					data.ref = ele.getAttribute(src, empty);
					XMLElement mask = ele.getChildrenByName(maskName);
					if (mask != null) {
						int r = mask.getIntAttribute("r", 0);
						int g = mask.getIntAttribute("g", 0);
						int b = mask.getIntAttribute("b", 0);
						int a = mask.getIntAttribute("a", 0);
						data.mask = new LColor(r, g, b, a);
					} else {
						data.mask = null;
					}
					String filter = ele.getAttribute(filterName, n);
					if (filter.equals(n)) {
						data.scaleType = 0;
					}
					if (filter.equals(l)) {
						data.scaleType = 1;
					}
					data.index = index;
					XMLElement parent = ele.getParent();
					if (parent != null) {
						imageList.put(parent.getAttribute(name, empty), data);
						index++;
					}
				}
			}
		}
		this.count = imageList.size();
		this.values = new LTextureObject[count];
	}

	public LTexture loadTexture(String name) {
		if (imageList == null) {
			throw new RuntimeException("Xml data not loaded !");
		}

		ImageData data = imageList.get(name);
		if (data == null) {
			throw new RuntimeException("No such image reference: '" + name
					+ "'");
		}
		if (this.values[data.index] != null) {
			return this.values[data.index].texture;
		}
		LTexture img = null;
		if (data.mask != null) {
			img = TextureUtils.filterColor(data.ref, data.mask,
					data.scaleType == 0 ? Format.DEFAULT : Format.LINEAR);
		} else {
			img = LTextures.loadTexture(data.ref,
					data.scaleType == 0 ? Format.DEFAULT : Format.LINEAR);
		}
		if ((data.w != 0) && (data.h != 0)) {
			img = img.getSubTexture(data.x, data.y, data.w, data.h);
		}
		if (data.scale != 0) {
			img = img.scale(data.scale);
		}
		this.values[data.index] = new LTextureObject(img, 0, 0);
		return img;
	}

	public int getTextureX(String name) {
		return imageList.get(name).x;
	}

	public int getTextureY(String name) {
		return imageList.get(name).y;
	}

	public int getTextureWidth(String name) {
		return imageList.get(name).w;
	}

	public int getTextureHeight(String name) {
		return imageList.get(name).h;
	}

	public float getTextureScale(String name) {
		return imageList.get(name).scale;
	}

	public final static class LTextureObject {

		public LTexture texture;

		public float x, y;

		public int width, height;

		public boolean visible;

		public LTextureObject(LTexture texture, float x, float y) {
			this.texture = texture;
			this.x = x;
			this.y = y;
			this.width = texture.getWidth();
			this.height = texture.getHeight();
			this.visible = true;
		}
	}

	private float alpha = 1;

	private boolean visible;

	private int count = 0;

	private final boolean autoExpand;

	LTextureObject[] values;

	float nx, ny;

	LTextureList(LTextureList tiles) {
		this.autoExpand = tiles.autoExpand;
		this.values = (LTextureObject[]) CollectionUtils.copyOf(tiles.values);
		this.count = tiles.count;
		this.visible = tiles.visible;
	}

	public LTextureList() {
		this(CollectionUtils.INITIAL_CAPACITY);
	}

	public LTextureList(int size) {
		this(true, size);
	}

	public LTextureList(boolean expand, int size) {
		this.autoExpand = expand;
		this.values = new LTextureObject[size];
		this.count = size;
		this.visible = true;
	}

	public int add(LTexture tex2d, float x, float y) {
		return add(new LTextureObject(tex2d, x, y));
	}

	public int add(String res, float x, float y) {
		return add(res, Format.SPEED, x, y);
	}

	public int add(String res, Format format, float x, float y) {
		return add(new LTextureObject(LTextures.loadTexture(res, format), x, y));
	}

	public int add(LTextureObject tex2d) {
		if (this.count == this.values.length) {
			LTextureObject[] oldValue = this.values;
			if (this.autoExpand)
				this.values = new LTextureObject[(oldValue.length << 1) + 1];
			else {
				this.values = new LTextureObject[oldValue.length + 1];
			}
			System.arraycopy(oldValue, 0, this.values, 0, oldValue.length);
		}
		this.values[this.count] = tex2d;
		return this.count++;
	}

	public LTextureObject remove(int i) {
		if ((i >= this.count) || (i < 0)) {
			throw new IndexOutOfBoundsException("Referenced " + i + ", size = "
					+ this.count);
		}
		LTextureObject ret = this.values[i];
		if (i < this.count - 1) {
			System.arraycopy(this.values, i + 1, this.values, i, this.count - i
					- 1);
		}
		this.count -= 1;
		return ret;
	}

	public void addAll(LTextureObject[] t) {
		capacity(this.count + t.length);
		System.arraycopy(t, 0, this.values, this.count, t.length);
		this.count += t.length;
	}

	public void addAll(LTextureList t) {
		capacity(this.count + t.count);
		System.arraycopy(t.values, 0, this.values, this.count, t.count);
		this.count += t.count;
	}

	public void draw(GLEx g) {
		draw(g, 0, 0, width, height);
	}

	private SpriteBatch batch = new SpriteBatch(1000);

	public void draw(GLEx g, int minX, int minY, int maxX, int maxY) {
		if (!visible) {
			return;
		}
		synchronized (values) {
			batch.begin();
			if (alpha > 0 && alpha < 1) {
				batch.setAlpha(alpha);
			}
			for (int i = 0; i < count; i++) {
				LTextureObject tex2d = this.values[i];
				if (tex2d == null || !tex2d.visible) {
					continue;
				}
				nx = minX + tex2d.x;
				ny = minY + tex2d.y;
				if (nx + tex2d.width < minX || nx > maxX
						|| ny + tex2d.height < minY || ny > maxY) {
					continue;
				}
				LTexture texture = tex2d.texture;
				if (texture == null) {
					continue;
				}
				if (tex2d.width != 0 && tex2d.height != 0) {
					batch.draw(texture, tex2d.x, tex2d.y, tex2d.width,
							tex2d.height);
				} else {
					batch.draw(texture, tex2d.x, tex2d.y);
				}
			}
			if (alpha > 0 && alpha < 1) {
				batch.resetColor();
			}
			batch.end();
		}
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public float getAlpha() {
		return alpha;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public LTextureObject[] array() {
		return this.values;
	}

	public int capacity() {
		return this.values.length;
	}

	public void clear() {
		this.count = 0;
	}

	public void capacity(int size) {
		if (this.values.length >= size) {
			return;
		}
		LTextureObject[] oldValue = this.values;
		this.values = new LTextureObject[size];
		System.arraycopy(oldValue, 0, this.values, 0, oldValue.length);
	}

	public LTextureObject get(int index) {
		return this.values[index];
	}

	public boolean isEmpty() {
		return this.count == 0;
	}

	public int size() {
		return this.count;
	}

	@Override
	public LTextureList clone() {
		return new LTextureList(this);
	}

	public void update() {
		if (this.count == this.values.length) {
			return;
		}
		LTextureObject[] oldValue = this.values;
		this.values = new LTextureObject[this.count];
		System.arraycopy(oldValue, 0, this.values, 0, this.count);
	}

	public LTextureObject[] toArray(LTextureObject[] dest) {
		System.arraycopy(this.values, 0, dest, 0, this.count);
		return dest;
	}

	@Override
	public void dispose() {
		this.visible = false;
		for (LTextureObject tex2d : this.values) {
			if (tex2d != null) {
				if (tex2d.texture != null) {
					tex2d.texture.destroy();
					tex2d.texture = null;
				}
				tex2d = null;
			}
		}
		if (batch != null) {
			batch.dispose();
		}
	}

}
