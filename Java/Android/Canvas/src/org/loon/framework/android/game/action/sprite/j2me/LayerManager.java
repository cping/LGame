package org.loon.framework.android.game.action.sprite.j2me;

import java.util.Vector;

import org.loon.framework.android.game.core.graphics.device.LGraphics;


/**
 * Copyright 2008 - 2009
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
public class LayerManager {
	
	private Vector<Layer> layers;

	private int viewX, viewY, viewW, viewH;

	public LayerManager() {
		layers = new Vector<Layer>();
		viewX = viewY = 0;
		viewW = viewH = Integer.MAX_VALUE;
	}

	public void append(Layer layer) {
		synchronized (this) {
			if (layer == null)
				throw new NullPointerException();
			layers.add(layer);
		}
	}

	public Layer getLayerAt(int i) {
		return (Layer) layers.get(i);
	}

	public int getSize() {
		return layers.size();
	}

	public void insert(Layer layer, int i) {
		synchronized (this) {
			if (layer == null)
				throw new NullPointerException();
			layers.insertElementAt(layer, i);
		}
	}

	public void remove(Layer layer) {
		synchronized (this) {
			if (layer == null)
				throw new NullPointerException();
			layers.remove(layer);
		}
	}

	public void setViewWindow(int x, int y, int width, int height) {
		synchronized (this) {
			if (width < 0 || height < 0)
				throw new IllegalArgumentException();
			viewX = x;
			viewY = y;
			viewW = width;
			viewH = height;
		}
	}

	public void paint(LGraphics g, int x, int y) {
		synchronized (this) {
			if (g == null){
				throw new NullPointerException();
			}
			int clipX = g.getClipX();
			int clipY = g.getClipY();
			int clipW = g.getClipWidth();
			int clipH = g.getClipHeight();
			g.translate(x - viewX, y - viewY);
			g.clipRect(viewX, viewY, viewW, viewH);
			for (int i = getSize(); --i >= 0;) {
				Layer comp = getLayerAt(i);
				if (comp.isVisible()) {
					comp.paint(g);
				}
			}
			g.translate(-x + viewX, -y + viewY);
			g.setClip(clipX, clipY, clipW, clipH);
		}
	}

}
