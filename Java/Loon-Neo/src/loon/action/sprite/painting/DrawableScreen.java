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

import loon.LGame;
import loon.LSystem;
import loon.LTransition;
import loon.Screen;
import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;
import loon.event.GameKey;
import loon.event.GameTouch;
import loon.font.IFont;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.TArray;
import loon.utils.timer.GameTime;
import loon.utils.timer.LTimerContext;

public abstract class DrawableScreen extends Screen {

	@Override
	public LTransition onTransition(){
		return LTransition.newEmpty();
	}

	private TArray<Drawable> drawables;

	private TArray<Drawable> drawablesToUpdate;

	private TArray<Drawable> drawablesToDraw;

	private GameComponentCollection gameCollection;

	private boolean isInit;

	private SpriteBatch batch;

	private final GameTime gameTime = new GameTime();
	
	public DrawableScreen() {
		this.drawables = new TArray<Drawable>();
		this.drawablesToUpdate = new TArray<Drawable>();
		this.drawablesToDraw = new TArray<Drawable>();
		this.gameCollection = new GameComponentCollection();
	}

	public IFont getFont() {
		if (batch != null) {
			return batch.getFont();
		}
		return LSystem.getSystemGameFont();
	}

	public void addDrawable(Drawable drawable) {
		drawable.drawableScreen = this;
		drawable.loadContent();
		drawables.add(drawable);
	}

	public void addDrawable(Drawable drawable, int index) {
		drawable.drawableScreen = this;
		drawable.loadContent();
		drawables.add(drawable);
		for (int i = 0; i < drawables.size; i++) {
			if (i == index) {
				drawables.get(i)._enabled = true;
			} else {
				drawables.get(i)._enabled = false;
			}
		}
	}

	public void draw(GLEx g) {
		if (isOnLoadComplete()) {
			if (batch != null) {
				synchronized (batch) {
					try {
						batch.begin();
						gameCollection.draw(batch, gameTime);
						if (drawablesToDraw.size > 0) {
							drawablesToDraw.clear();
						}
						for (Drawable drawable : drawables) {
							drawablesToDraw.add(drawable);
						}
						for (Drawable drawable : drawablesToDraw) {
							if (drawable._enabled) {
								if (drawable.getDrawableState() == DrawableState.Hidden) {
									continue;
								}
								drawable.draw(batch, gameTime);
							}
						}
						draw(batch);
					} finally {
						batch.end();
					}
				}
			}
		}
	}

	public abstract void draw(SpriteBatch batch);

	public void fadeBackBufferToBlack(SpriteBatch bth) {
		drawRectangle(bth, LSystem.viewSize.getRect(), 0f, 0f, 0f, 1f);
	}
	
	public void fadeBackBufferToBlack(SpriteBatch bth, float a) {
		drawRectangle(bth, LSystem.viewSize.getRect(), 0f, 0f, 0f, a);
	}

	public void drawRectangle(SpriteBatch bth, RectBox rect, LColor c) {
		drawRectangle(bth, rect, c.r, c.g, c.b, c.a);
	}

	public void drawRectangle(SpriteBatch bth, RectBox rect, float r, float g,
			float b, float a) {
		if (bth != null) {
			float color = bth.color();
			bth.setColor(r, g, b, a);
			bth.fillRect(rect.x, rect.y, rect.width, rect.height);
			bth.setColor(color);
		}
	}

	public TArray<Drawable> getDrawables() {
		return new TArray<Drawable>(drawables);
	}

	@Override
	public final void onLoad() {
		LGame game = LSystem.base();
		if (game != null) {
			if (batch == null) {
				batch = new SpriteBatch(256);
			}
			for (Drawable drawable : drawables) {
				drawable.loadContent();
			}
			isInit = true;
			loadContent();
			gameCollection.load();
		}
	}

	public abstract void loadContent();

	public void removeDrawable(Drawable drawable) {
		// drawable.drawableScreen = null;
		drawable.unloadContent();
		drawables.remove(drawable);
		drawablesToUpdate.remove(drawable);
	}

	public abstract void unloadContent();

	public abstract void pressed(GameTouch e);

	public abstract void released(GameTouch e);

	public abstract void move(GameTouch e);

	public abstract void drag(GameTouch e);

	public abstract void pressed(GameKey e);

	public abstract void released(GameKey e);

	@Override
	public final void alter(LTimerContext timer) {
		if (!isOnLoadComplete()) {
			return;
		}

		gameTime.update(timer);

		if (!isInit) {
			loadContent();
		}

		gameCollection.update(gameTime);
		if (drawablesToUpdate.size > 0) {
			drawablesToUpdate.clear();
		}
		for (Drawable drawable : drawables) {
			drawablesToUpdate.add(drawable);
		}

		boolean otherScreenHasFocus = false;
		boolean coveredByOtherScreen = false;

		Drawable drawable;
		int screenIndex;
		for (; drawablesToUpdate.size > 0;) {

			screenIndex = drawablesToUpdate.size - 1;
			drawable = drawablesToUpdate.get(screenIndex);

			drawablesToUpdate.removeIndex(screenIndex);

			if (drawable._enabled) {
				drawable.update(gameTime, otherScreenHasFocus,
						coveredByOtherScreen);

				if (drawable.getDrawableState() == DrawableState.TransitionOn
						|| drawable.getDrawableState() == DrawableState.Active) {
					if (!otherScreenHasFocus) {
						drawable.handleInput(this);
						otherScreenHasFocus = true;
					}
					if (!drawable.IsPopup) {
						coveredByOtherScreen = true;
					}
				}
			}
		}

		update(gameTime);
	}

	public abstract void update(GameTime gameTime);

	@Override
	public final void onKeyDown(GameKey e) {
		super.onKeyDown(e);
		for (Drawable drawable : drawablesToDraw) {
			if (drawable._enabled) {
				if (drawable != null) {
					if (drawable.getDrawableState() == DrawableState.Hidden) {
						continue;
					}
					drawable.pressed(e);
				}
			}
		}
		pressed(e);
	}

	@Override
	public final void onKeyUp(GameKey e) {
		super.onKeyUp(e);
		for (Drawable drawable : drawablesToDraw) {
			if (drawable._enabled) {
				if (drawable != null) {
					if (drawable.getDrawableState() == DrawableState.Hidden) {
						continue;
					}
					drawable.released(e);
				}
			}
		}
		released(e);
	}

	@Override
	public final void touchDown(GameTouch e) {
		for (Drawable drawable : drawablesToDraw) {
			if (drawable._enabled) {
				if (drawable != null) {
					if (drawable.getDrawableState() == DrawableState.Hidden) {
						continue;
					}
					drawable.pressed(e);
				}
			}
		}
		pressed(e);
	}

	@Override
	public final void touchUp(GameTouch e) {
		for (Drawable drawable : drawablesToDraw) {
			if (drawable._enabled) {
				if (drawable != null) {
					if (drawable.getDrawableState() == DrawableState.Hidden) {
						continue;
					}
					drawable.released(e);
				}
			}
		}
		released(e);
	}

	@Override
	public final void touchMove(GameTouch e) {
		for (Drawable drawable : drawablesToDraw) {
			if (drawable._enabled) {
				if (drawable != null) {
					if (drawable.getDrawableState() == DrawableState.Hidden) {
						continue;
					}
					drawable.move(e);
				}
			}
		}
		move(e);
	}

	@Override
	public final void touchDrag(GameTouch e) {
		drag(e);
	}

	public SpriteBatch getSpriteBatch() {
		return batch;
	}

	public GameTime getGameTime() {
		return gameTime;
	}

	public GameComponentCollection Components() {
		return gameCollection;
	}

	@Override
	public void close() {
		for (Drawable drawable : drawables) {
			if (drawable != null) {
				drawable._enabled = false;
				drawable.unloadContent();
				drawable.dispose();
			}
		}
		drawables.clear();
		drawablesToUpdate.clear();
		drawablesToDraw.clear();
		gameCollection.clear();
		if (batch != null) {
			batch.close();
			batch = null;
		}
		unloadContent();
		isInit = false;
	}

}
