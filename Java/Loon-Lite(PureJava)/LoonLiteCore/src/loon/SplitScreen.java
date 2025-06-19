/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon;

import java.util.Iterator;

import loon.component.layout.AbsoluteLayout;
import loon.component.layout.HorizontalLayout;
import loon.component.layout.LayoutConstraints;
import loon.component.layout.LayoutManager;
import loon.component.layout.LayoutPort;
import loon.component.layout.ScreenLayoutInvoke;
import loon.component.layout.SplitLayout;
import loon.component.layout.VerticalLayout;
import loon.events.GameKey;
import loon.events.GameTouch;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimerContext;

public class SplitScreen extends Screen {

	public static SplitScreen create(SplitLayout sl, Screen... screens) {
		return new SplitScreen(sl, screens);
	}

	private GameTouch _tempTocuh = new GameTouch(0, 0, 0, 0);

	private LayoutConstraints _screenLayoutConstraints;

	private LayoutPort _screenLayoutPort;

	private SplitLayout _layout;

	private LayoutManager _manager;

	private final TArray<ScreenLayoutInvoke> _screenPool;

	private boolean _dirty, _poolCreated, _poolLoaded;

	public SplitScreen(SplitLayout sl, Screen... screens) {
		super(LSystem.UNKNOWN, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
		this._screenPool = new TArray<ScreenLayoutInvoke>();
		for (int i = 0; i < screens.length; i++) {
			Screen s = screens[i];
			if (s != null) {
				_screenPool.add(new ScreenLayoutInvoke(s));
			}
		}
		this.setLayout(sl);
	}

	@Override
	public void draw(GLEx g) {
		if (_screenPool != null && _screenPool.size > 0) {
			synchronized (this._screenPool) {
				for (int i = 0; i < _screenPool.size; i++) {
					ScreenLayoutInvoke layout = _screenPool.get(i);
					if (layout != null) {
						RectBox view = layout.getView();
						Screen s = layout.get();
						g.saveTx();
						float scaleX = view.getWidth() / s.getWidth();
						float scaleY = view.getHeight() / s.getHeight();
						final float tx = view.getX();
						final float ty = view.getY();
						if (tx != 0f || ty != 0f) {
							g.translate(tx, ty);
						}
						if ((scaleX != 1f) || (scaleY != 1f)) {
							final float scaleCenterX = tx + (view.getWidth() / 2f);
							final float scaleCenterY = ty + (view.getHeight() / 2f);
							g.scale(scaleX, scaleY, scaleCenterX, scaleCenterY);
							switch (this._layout) {
							case Vertical:
								g.translate(0, -scaleCenterY);
								break;
							case Horizontal:
								g.translate(-scaleCenterX, 0);
								break;
							default:
								g.translate(-scaleCenterX, -scaleCenterY);
								break;
							}
						}
						g.setClip(tx, ty, view.getWidth(), view.getHeight());
						s.createUI(g);
						g.resetClip();
						g.restoreTx();
					}
				}
			}
		}
	}

	@Override
	public void onLoad() {
		if (!_poolLoaded) {
			if (_screenPool != null && _screenPool.size > 0) {
				synchronized (this._screenPool) {
					for (Iterator<ScreenLayoutInvoke> it = _screenPool.iterator(); it.hasNext();) {
						ScreenLayoutInvoke layout = it.next();
						if (layout != null) {
							Screen screen = layout.get();
							if (!screen.isOnLoadComplete()) {
								screen.onLoad();
							}
							screen.setLock(false);
							screen.setClose(false);
							screen.setOnLoadState(true);
							if (screen.getBackground() != null) {
								screen.setRepaintMode(Screen.SCREEN_TEXTURE_REPAINT);
							}
						}
					}
				}
				updateLayout();
			}
			_poolLoaded = true;
		}
	}

	protected void dirty() {
		this._dirty = true;
	}

	protected boolean isDirty() {
		return this._dirty;
	}

	private void updateLayout() {
		if (_dirty) {
			layoutUpdate();
			_dirty = false;
		}
	}

	private void create(int w, int h) {
		if (!_poolCreated) {
			if (_screenPool != null && _screenPool.size > 0) {
				synchronized (this._screenPool) {
					for (Iterator<ScreenLayoutInvoke> it = _screenPool.iterator(); it.hasNext();) {
						ScreenLayoutInvoke layout = it.next();
						if (layout != null) {
							Screen screen = layout.get();
							screen.setLock(true);
							screen.setRepaintMode(SCREEN_NOT_REPAINT);

							screen.setOnLoadState(false);
							screen.onCreate(w, h);
							screen.setClose(false);
							screen.resetSize(w, h);
							if (!screen.isOnLoadComplete()) {
								screen.onLoad();
							}
							screen.onLoaded();
							screen.setOnLoadState(true);
							screen.resume();
							screen.setLock(false);
						}
					}
				}
			}
			_poolCreated = true;
		}
	}

	@Override
	public void onCreate(int w, int h) {
		create(w, h);
		super.onCreate(w, h);
	}

	private void poolCreate() {
		create(getWidth(), getHeight());
		onLoad();
		updateLayout();
	}

	@Override
	public void alter(LTimerContext context) {
		poolCreate();
		if (_screenPool != null && _screenPool.size > 0) {
			synchronized (this._screenPool) {
				for (Iterator<ScreenLayoutInvoke> it = _screenPool.iterator(); it.hasNext();) {
					ScreenLayoutInvoke screen = it.next();
					if (screen != null) {
						screen.get().runTimer(context);
					}
				}
			}
		}
	}

	@Override
	public void resize(int w, int h) {
		this._dirty = true;
		if (_screenPool != null && _screenPool.size > 0) {
			synchronized (this._screenPool) {
				for (Iterator<ScreenLayoutInvoke> it = _screenPool.iterator(); it.hasNext();) {
					ScreenLayoutInvoke screen = it.next();
					if (screen != null) {
						screen.get().resize(w, h);
					}
				}
			}
		}
	}

	protected GameTouch convertTouch(ScreenLayoutInvoke layout, GameTouch e) {
		return convertTouch(layout, e, true);
	}

	protected GameTouch convertTouch(ScreenLayoutInvoke layout, GameTouch e, boolean update) {
		final RectBox view = layout.getView();
		final Screen screen = layout.get();
		final float scaleX = view.getWidth() / screen.getWidth();
		final float scaleY = view.getHeight() / screen.getHeight();
		final float newX = MathUtils.clamp(e.getX() - view.getX(), 0f, view.getWidth());
		final float newY = MathUtils.clamp(e.getY() - view.getY(), 0f, view.getHeight());
		return _tempTocuh.set(newX / scaleX, newY / scaleY, e.getPointer(), e.getID());
	}

	@Override
	public void touchDown(GameTouch e) {
		if (_screenPool != null && _screenPool.size > 0) {
			synchronized (this._screenPool) {
				for (Iterator<ScreenLayoutInvoke> it = _screenPool.iterator(); it.hasNext();) {
					ScreenLayoutInvoke layout = it.next();
					if (layout != null && layout.isAcceptEventCall()) {
						layout.get().mousePressed(convertTouch(layout, e));
					}
				}
			}
		}
	}

	@Override
	public void touchUp(GameTouch e) {
		if (_screenPool != null && _screenPool.size > 0) {
			synchronized (this._screenPool) {
				for (Iterator<ScreenLayoutInvoke> it = _screenPool.iterator(); it.hasNext();) {
					ScreenLayoutInvoke layout = it.next();
					if (layout != null && layout.isAcceptEventCall()) {
						layout.get().mouseReleased(convertTouch(layout, e));
					}
				}
			}
		}
	}

	@Override
	public void touchMove(GameTouch e) {
		if (_screenPool != null && _screenPool.size > 0) {
			synchronized (this._screenPool) {
				for (Iterator<ScreenLayoutInvoke> it = _screenPool.iterator(); it.hasNext();) {
					ScreenLayoutInvoke layout = it.next();
					if (layout != null && layout.isAcceptEventCall()) {
						layout.get().mouseMoved(convertTouch(layout, e));
					}
				}
			}
		}
	}

	@Override
	public void touchDrag(GameTouch e) {
		if (_screenPool != null && _screenPool.size > 0) {
			synchronized (this._screenPool) {
				for (Iterator<ScreenLayoutInvoke> it = _screenPool.iterator(); it.hasNext();) {
					ScreenLayoutInvoke layout = it.next();
					if (layout != null && layout.isAcceptEventCall()) {
						layout.get().mouseDragged(convertTouch(layout, e));
					}
				}
			}
		}
	}

	@Override
	public void onKeyDown(GameKey e) {
		if (_screenPool != null && _screenPool.size > 0) {
			synchronized (this._screenPool) {
				for (Iterator<ScreenLayoutInvoke> it = _screenPool.iterator(); it.hasNext();) {
					ScreenLayoutInvoke layout = it.next();
					if (layout != null && layout.isAcceptEventCall()) {
						layout.get().onKeyDown(e);
					}
				}
			}
		}
	}

	@Override
	public void onKeyUp(GameKey e) {
		if (_screenPool != null && _screenPool.size > 0) {
			synchronized (this._screenPool) {
				for (Iterator<ScreenLayoutInvoke> it = _screenPool.iterator(); it.hasNext();) {
					ScreenLayoutInvoke layout = it.next();
					if (layout != null && layout.isAcceptEventCall()) {
						layout.get().onKeyUp(e);
					}
				}
			}
		}
	}

	@Override
	public void onKeyTyped(GameKey e) {
		if (_screenPool != null && _screenPool.size > 0) {
			synchronized (this._screenPool) {
				for (Iterator<ScreenLayoutInvoke> it = _screenPool.iterator(); it.hasNext();) {
					ScreenLayoutInvoke layout = it.next();
					if (layout != null && layout.isAcceptEventCall()) {
						layout.get().onKeyTyped(e);
					}
				}
			}
		}
	}

	@Override
	public void resume() {
		if (_screenPool != null && _screenPool.size > 0) {
			synchronized (this._screenPool) {
				for (Iterator<ScreenLayoutInvoke> it = _screenPool.iterator(); it.hasNext();) {
					ScreenLayoutInvoke layout = it.next();
					if (layout != null) {
						layout.get().resume();
					}
				}
			}
		}
	}

	@Override
	public void pause() {
		if (_screenPool != null && _screenPool.size > 0) {
			synchronized (this._screenPool) {
				for (Iterator<ScreenLayoutInvoke> it = _screenPool.iterator(); it.hasNext();) {
					ScreenLayoutInvoke screen = it.next();
					if (screen != null) {
						screen.get().pause();
					}
				}
			}
		}
	}

	@Override
	public Screen restart() {
		return restart(this, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), true);
	}

	@Override
	public Screen restart(int w, int h) {
		if (_screenPool != null && _screenPool.size > 0) {
			synchronized (this._screenPool) {
				for (Iterator<ScreenLayoutInvoke> it = _screenPool.iterator(); it.hasNext();) {
					ScreenLayoutInvoke screen = it.next();
					if (screen != null) {
						screen.get().restart(w, h);
					}
				}
			}
			updateLayout();
		}
		return restart(this, w, h, true);
	}

	public SplitScreen setLayout(SplitLayout sl) {
		this._layout = sl;
		switch (this._layout) {
		case Vertical:
			this._manager = VerticalLayout.at();
			break;
		case Horizontal:
			this._manager = HorizontalLayout.at();
			break;
		case Absolute:
			this._manager = AbsoluteLayout.at();
			break;
		}
		this._dirty = true;
		return this;
	}

	public SplitLayout getSplitLayout() {
		return this._layout;
	}

	public LayoutManager getLayoutManager() {
		return this._manager;
	}

	public SplitScreen layoutUpdate() {
		return layoutElements(_manager);
	}

	public SplitScreen layoutElements(final LayoutManager manager) {
		if (manager == null) {
			return this;
		}
		final TArray<LayoutPort> list = new TArray<LayoutPort>();
		for (Iterator<ScreenLayoutInvoke> it = _screenPool.iterator(); it.hasNext();) {
			ScreenLayoutInvoke c = it.next();
			if (c != null) {
				list.add(c.getLayoutPort());
			}
		}
		LayoutPort[] tmp = new LayoutPort[list.size];
		for (int i = 0; i < list.size; i++) {
			tmp[i] = list.get(i);
		}
		manager.layoutElements(this.getScreenLayoutPort(), tmp);
		return this;
	}

	public LayoutPort getScreenLayoutPort() {
		if (_screenLayoutPort == null) {
			_screenLayoutPort = new LayoutPort(new ScreenLayoutInvoke(this), getScreenRootConstraints());
		}
		return _screenLayoutPort;
	}

	public LayoutConstraints getScreenRootConstraints() {
		if (_screenLayoutConstraints == null) {
			_screenLayoutConstraints = new LayoutConstraints();
		}
		return _screenLayoutConstraints;
	}

	@Override
	public void close() {
		if (_screenPool != null && _screenPool.size > 0) {
			synchronized (this._screenPool) {
				for (Iterator<ScreenLayoutInvoke> it = _screenPool.iterator(); it.hasNext();) {
					ScreenLayoutInvoke screen = it.next();
					if (screen != null) {
						screen.get().destroy();
					}
				}
			}
		}
		_dirty = _poolCreated = _poolLoaded = false;
	}

}
