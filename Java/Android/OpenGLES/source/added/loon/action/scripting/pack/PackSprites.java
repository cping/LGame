package loon.action.scripting.pack;

import java.util.ArrayList;
import java.util.Iterator;

import loon.action.scripting.pack.PackView.EmptyView;
import loon.core.graphics.opengl.LTexturePack;


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
public class PackSprites {

	public static interface PackSpriteListener {

		public void update(PackSprite spr);

	}

	private PackSpriteListener listener;

	private ArrayList<PackSprite> sprites;

	private PackView view;

	public PackSprites(PackView view) {
		setView(view);
	}

	public PackSprites() {
		setView(null);
	}

	public void setView(PackView v) {
		if (sprites == null) {
			sprites = new ArrayList<PackSprite>(10);
		}
		this.view = v;
		if (view == null) {
			view = EmptyView.getInstance();
		}
	}

	public PackView getView() {
		return view;
	}

	public int size() {
		return sprites.size();
	}

	public Iterator<PackSprite> elements() {
		return sprites.iterator();
	}

	public void add(PackSprite sprite, float x, float y) {
		sprite.setX(x);
		sprite.setY(y);
		add(sprite);
	}

	public void add(PackSprite sprite) {
		sprites.add(sprite);
	}

	public void remove(PackSprite sprite) {
		sprites.remove(sprite);
	}

	public void clear() {
		sprites.clear();
	}

	public PackSprite find(String name) {
		for (int i = 0; i < sprites.size(); i++) {
			PackSprite s = sprites.get(i);
			if (s == null) {
				return null;
			}
			if (name.equalsIgnoreCase(s.getName())) {
				return s;
			}
		}
		return null;
	}

	public void draw(LTexturePack pack, long elapsedTime) {
		for (int i = 0; i < sprites.size(); i++) {
			PackSprite s = sprites.get(i);
			if (s == null || !s.visible) {
				return;
			}
			if (listener != null) {
				listener.update(s);
			}
			s.update(elapsedTime);
			s.draw(pack, view);
		}
	}

	public PackSpriteListener getListener() {
		return listener;
	}

	public void setListener(PackSpriteListener listener) {
		this.listener = listener;
	}

}
