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
package loon.action.sprite;

import loon.action.map.Character;
import loon.action.map.Item;
import loon.opengl.GLEx;
import loon.utils.TArray;

/**
 * 由Entity产生的窗体Scene,方便递归调用
 */
public class Scene extends Entity {

	private long secondsElapsedTotal;

	protected Scene _parentScene;
	protected Scene _childScene;
	private boolean _childSceneModalDraw;
	private boolean _childSceneModalUpdate;

	private boolean mBackgroundEnabled = true;

	private TArray<Item> items = new TArray<Item>();

	private TArray<Character> characters = new TArray<Character>();

	public Scene() {
		this(0);
	}

	public Scene(final int count) {
		for (int i = 0; i < count; i++) {
			this.addChild(new Entity());
		}
	}

	public void addItem(Item item) {
		this.items.add(item);
	}

	public Item getItem(int index) {
		return this.items.get(index);
	}

	public Item getItem(String name) {
		int index = findItem(name);
		if (index == -1) {
			return null;
		}
		return getItem(index);
	}

	public int findItem(String name) {
		for (int i = 0; i < this.items.size; i++) {
			if (getItem(i).getName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}

	public Item removeItem(int index) {
		return this.items.removeIndex(index);
	}

	public int countItems() {
		return this.items.size;
	}

	public void addCharacter(Character character) {
		this.characters.add(character);
	}

	public Character getCharacter(int index) {
		return this.characters.get(index);
	}

	public Character getCharacter(String name) {
		int index = findCharacter(name);
		if (index == -1) {
			return null;
		}
		return getCharacter(index);
	}

	public int findCharacter(String name) {
		for (int i = 0; i < this.characters.size; i++) {
			if (getCharacter(i).getName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}

	public Character removeCharacter(int index) {
		return this.characters.removeIndex(index);
	}

	public int countCharacters() {
		return this.characters.size;
	}

	public float getSecondsElapsedTotal() {
		return this.secondsElapsedTotal;
	}

	private void setParentScene(final Scene pParentScene) {
		this._parentScene = pParentScene;
	}

	@Override
	public void reset() {
		super.reset();
		this.clearChildScene();
	}

	@Override
	protected void onManagedPaint(final GLEx gl, float offsetX, float offsetY) {
		final Scene childScene = this._childScene;
		if (childScene == null || !this._childSceneModalDraw) {
			if (this.mBackgroundEnabled) {
				super.onManagedPaint(gl, offsetX, offsetY);
			}
		}
		if (childScene != null) {
			childScene.createUI(gl, offsetX, offsetY);
		}
	}

	public void back() {
		this.clearChildScene();
		if (this._parentScene != null) {
			this._parentScene.clearChildScene();
			this._parentScene = null;
		}
	}

	public boolean hasChildScene() {
		return this._childScene != null;
	}

	public Scene getChildScene() {
		return this._childScene;
	}

	public void setChildSceneModal(final Scene child) {
		this.setChildScene(child, true, true);
	}

	public void setChildScene(final Scene child) {
		this.setChildScene(child, false, false);
	}

	public void setChildScene(final Scene child, final boolean modalDraw, final boolean modalUpdate) {
		child.setParentScene(this);
		this._childScene = child;
		this._childSceneModalDraw = modalDraw;
		this._childSceneModalUpdate = modalUpdate;
	}

	public void clearChildScene() {
		this._childScene = null;
	}

	@Override
	protected void onManagedUpdate(final long elapsedTime) {
		this.secondsElapsedTotal += elapsedTime;
		final Scene childScene = this._childScene;
		if (childScene == null || !this._childSceneModalUpdate) {
			super.onManagedUpdate(elapsedTime);
		}
		if (childScene != null) {
			childScene.update(elapsedTime);
		}
	}

}
