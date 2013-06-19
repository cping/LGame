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
package loon.action.sprite.painting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import loon.action.sprite.SpriteBatch;
import loon.core.timer.GameTime;

public final class GameComponentCollection {

	private ArrayList<IGameComponent> collections;

	private ArrayList<IGameComponent> collectionsToUpdate;

	private ArrayList<IGameComponent> collectionsToDraw;

	private Comparator<IGameComponent> igameDrawComparator = new Comparator<IGameComponent>() {
		@Override
		public int compare(IGameComponent one, IGameComponent two) {
			if (one instanceof DrawableGameComponent
					&& two instanceof DrawableGameComponent) {
				return ((DrawableGameComponent) one).getDrawOrder()
						- ((DrawableGameComponent) two).getDrawOrder();
			}
			return 0;
		}
	};

	private Comparator<IGameComponent> igameUpdateComparator = new Comparator<IGameComponent>() {
		@Override
		public int compare(IGameComponent one, IGameComponent two) {
			if (one instanceof GameComponent && two instanceof GameComponent) {
				return ((GameComponent) one).getUpdateOrder()
						- ((GameComponent) two).getUpdateOrder();
			}
			return 0;
		}
	};

	public GameComponentCollection() {
		this.collections = new ArrayList<IGameComponent>();
		this.collectionsToUpdate = new ArrayList<IGameComponent>();
		this.collectionsToDraw = new ArrayList<IGameComponent>();
	}

	public int size() {
		return collections.size();
	}

	public void sortDraw() {
		Collections.sort(collections, igameDrawComparator);
	}

	public void sortUpdate() {
		Collections.sort(collections, igameUpdateComparator);
	}

	public ArrayList<IGameComponent> list() {
		return new ArrayList<IGameComponent>(collections);
	}

	public IGameComponent get(int idx) {
		return collections.get(idx);
	}

	void draw(SpriteBatch batch, GameTime gameTime) {
		if (isClear) {
			return;
		}
		if (collectionsToDraw.size() > 0) {
			collectionsToDraw.clear();
		}
		for (IGameComponent drawable : collections) {
			collectionsToDraw.add(drawable);
		}
		for (IGameComponent drawable : collectionsToDraw) {
			if (drawable instanceof IDrawable) {
				IDrawable comp = (IDrawable) drawable;
				comp.draw(batch, gameTime);
			}
		}
	}

	void load() {
		isClear = false;
		for (IGameComponent comp : collections) {
			comp.initialize();
		}
	}

	void update(GameTime gameTime) {
		if (isClear) {
			return;
		}
		if (collectionsToUpdate.size() > 0) {
			collectionsToUpdate.clear();
		}
		for (IGameComponent drawable : collections) {
			collectionsToUpdate.add(drawable);
		}

		IGameComponent drawable;
		int screenIndex;
		for (; collectionsToUpdate.size() > 0;) {

			screenIndex = collectionsToUpdate.size() - 1;
			drawable = collectionsToUpdate.get(screenIndex);

			collectionsToUpdate.remove(screenIndex);

			if (drawable instanceof IUpdateable) {
				IUpdateable comp = (IUpdateable) drawable;
				comp.update(gameTime);
			}
		}

	}

	private boolean isClear;

	public void clear() {
		if (!isClear) {
			synchronized (GameComponentCollection.class) {
				collections.clear();
				collectionsToUpdate.clear();
				collectionsToDraw.clear();
				isClear = true;
			}
		}
	}

	public boolean add(IGameComponent gc) {
		if (isClear) {
			return false;
		}
		gc.initialize();
		boolean result = collections.add(gc);
		if (gc != null && Added != null) {
			Added.invoke(gc);
		}
		if (gc instanceof DrawableGameComponent) {
			if (((DrawableGameComponent) gc).getDrawOrder() != 0) {
				sortDraw();
			}
		}
		return result;
	}

	public boolean add(IGameComponent gc, int index) {
		if (isClear) {
			return false;
		}
		gc.initialize();
		boolean result = collections.add(gc);
		for (int i = 0; i < collections.size(); i++) {
			if (collections.get(i) instanceof DrawableGameComponent) {
				if (i == index) {
					((DrawableGameComponent) collections.get(i))
							.setEnabled(true);
				} else {
					((DrawableGameComponent) collections.get(i))
							.setEnabled(false);
				}
			}
		}
		if (gc != null && Added != null) {
			Added.invoke(gc);
		}
		if (gc instanceof DrawableGameComponent) {
			if (((DrawableGameComponent) gc).getDrawOrder() != 0) {
				sortDraw();
			}
		}
		return result;
	}

	public boolean remove(IGameComponent gc) {
		if (isClear) {
			return false;
		}
		boolean result = false;
		if (gc != null && gc instanceof DrawableGameComponent) {
			DrawableGameComponent comp = (DrawableGameComponent) gc;
			comp.unloadContent();
			result = collections.remove(gc);
			collectionsToUpdate.remove(gc);
			if (Removed != null) {
				Removed.invoke(gc);
			}
		}
		return result;
	}

	public boolean removeAt(int idx) {
		if (isClear) {
			return false;
		}
		IGameComponent comp = collections.remove(idx);
		boolean result = (comp != null);
		if (result) {
			if (comp instanceof DrawableGameComponent) {
				((DrawableGameComponent) comp).unloadContent();
			}
			collectionsToUpdate.remove(comp);
			if (Removed != null) {
				Removed.invoke(comp);
			}
		}
		return result;
	}

	private ComponentEvent Added;

	private ComponentEvent Removed;

	public ComponentEvent getAdded() {
		return Added;
	}

	public void setAdded(ComponentEvent added) {
		Added = added;
	}

	public ComponentEvent getRemoved() {
		return Removed;
	}

	public void setRemoved(ComponentEvent removed) {
		Removed = removed;
	}
}
