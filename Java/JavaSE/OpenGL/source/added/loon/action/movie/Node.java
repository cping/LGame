/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.action.movie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import loon.action.Event;
import loon.action.map.Field2D;
import loon.action.sprite.ISprite;
import loon.core.LObject;
import loon.core.geom.RectBox;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;


/**
 * 0.3.3新增类，用以实现多节点的叠加效果
 */
public class Node extends LObject implements ISprite, Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3211228930455651878L;

	private Comparator<Node> zIndexComparator = new Comparator<Node>() {
		public int compare(Node one, Node two) {
			return one.zIndex - two.zIndex;
		}
	};

	protected int width, height;

	protected LTexture texture;

	private LColor color = new LColor(LColor.white);

	protected ArrayList<Node> children = new ArrayList<Node>();

	private ArrayList<Node> requireRemove = new ArrayList<Node>();

	private int zIndex = 0;

	protected Node parent = null;

	private boolean visible = true;

	public Node(final String fileName) {
		this(LTextures.loadTexture(fileName), 0, 0);
	}

	public Node(final String fileName, final float x, final float y) {
		this(LTextures.loadTexture(fileName), x, y);
	}

	public Node(final LTexture tex2d, final float x, final float y) {
		this.texture = tex2d;
		this.setLocation(x, y);
		this.width = tex2d.getWidth();
		this.height = tex2d.getHeight();
	}

	public Node(final String fileName, final float x, final float y,
			final float w, final float h) {
		this(LTextures.loadTexture(fileName), x, y, w, h);
	}

	public Node(final LTexture tex2d, final float x, final float y,
			final float w, final float h) {
		this.texture = tex2d;
		this.setLocation(x, y);
		this.width = (int) w;
		this.height = (int) h;
	}

	public Node getParent() {
		return parent;
	}

	public void setSize(final float w, final float h) {
		this.width = (int) w;
		this.height = (int) h;
	}

	public void update(long elapsedTime) {
		final int remove = requireRemove.size();
		if (remove > 0) {
			for (int index = 0; index < remove; index++) {
				final Node removedChild = requireRemove.get(index);
				children.remove(removedChild);
			}
			requireRemove.clear();
		}
		Collections.sort(children, zIndexComparator);
		final int size = children.size();
		for (int index = 0; index < size; index++) {
			final Node child = children.get(index);
			child.update(elapsedTime);
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setColor(LColor c) {
		color.setColor(c);
	}

	public LColor getColor() {
		return color;
	}

	public float getAlpha() {
		return color.a;
	}

	public void setAlpha(float a) {
		color.a = a;
	}

	public void setVisible(boolean v) {
		this.visible = v;
	}

	public boolean isVisible() {
		return visible;
	}

	public void createUI(GLEx g) {
		if (!visible) {
			return;
		}
		if (texture != null) {
			g.drawTexture(texture, location.x, location.y, width * scaleX,
					height * scaleY, rotation, color);
		}
		paint(g, location.x, location.y);
		final int size = children.size();
		if (size == 0) {
			return;
		}
		for (int index = 0; index < size; index++) {
			final Node child = children.get(index);
			if (child.visible) {
				final float pX = location.x + child.location.x;
				final float pY = location.y + child.location.y;
				g.drawTexture(child.texture, pX, pY, child.width * scaleX,
						child.height * scaleY, child.rotation, child.color);
				paint(g, pX, pY);
			}
		}
	}

	public void paint(GLEx g, float x, float y) {

	}

	public void addChild(final Node child, final int zIndex) {
		child.zIndex = zIndex;
		child.parent = this;
		int index = Collections.binarySearch(children, child, zIndexComparator);
		if (index >= 0) {
			final int size = children.size();
			Node prevChild;
			do {
				prevChild = children.get(index);
				index++;
			} while (index < size
					&& children.get(index).zIndex == prevChild.zIndex);
		} else {
			index = -(index + 1);
		}
		children.add(index, child);
	}

	public void addChild(final Node pChild) {
		addChild(pChild, pChild.zIndex);
	}

	public void reorderZIndex(final Node pChild, final int pZIndex) {
		children.remove(pChild);
		addChild(pChild, pZIndex);
	}

	public void removeChild(final Node pChild) {
		pChild.parent = null;
		requireRemove.add(pChild);
	}

	public List<Node> getChildren() {
		return this.children;
	}

	public int getZIndex() {
		return this.zIndex;
	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), width * scaleX, height * scaleY);
	}

	public LTexture getBitmap() {
		return texture;
	}

	public void dispose() {
		this.visible = false;
		if (texture != null) {
			texture.destroy();
			texture = null;
		}
	}

	public Field2D getField2D() {
		return null;
	}

	public boolean isBounded() {
		return false;
	}

	public boolean isContainer() {
		return false;
	}

	public boolean inContains(int x, int y, int w, int h) {
		return false;
	}

	public RectBox getRectBox() {
		return getCollisionBox();
	}

	public int getContainerWidth() {
		return 0;
	}

	public int getContainerHeight() {
		return 0;
	}

	float scaleX = 1, scaleY = 1;

	public void setScale(final float s) {
		this.setScale(s, s);
	}

	public void setScale(final float sx, final float sy) {
		if (this.scaleX == sx && this.scaleY == sy) {
			return;
		}
		this.scaleX = sx;
		this.scaleY = sy;
	}

	public float getScaleX() {
		return this.scaleX;
	}

	public float getScaleY() {
		return this.scaleY;
	}
}
