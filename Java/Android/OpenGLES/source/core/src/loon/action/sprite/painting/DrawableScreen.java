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

import loon.action.sprite.SpriteBatch;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.Screen;
import loon.core.graphics.opengl.GLEx;
import loon.core.input.LKey;
import loon.core.input.LTouch;
import loon.core.timer.GameTime;
import loon.core.timer.LTimerContext;

public abstract class DrawableScreen extends Screen {

	private ArrayList<Drawable> drawables;

	private ArrayList<Drawable> drawablesToUpdate;

	private ArrayList<Drawable> drawablesToDraw;

	private GameComponentCollection gameCollection;
	
	private boolean isInit;

	private SpriteBatch batch;

	private final GameTime gameTime = new GameTime();
	
	public DrawableScreen() {
		this.drawables = new ArrayList<Drawable>();
		this.drawablesToUpdate = new ArrayList<Drawable>();
		this.drawablesToDraw = new ArrayList<Drawable>();
		this.gameCollection = new GameComponentCollection();
	}

	public LFont getFont() {
		if (batch != null) {
			return batch.getFont();
		}
		return LFont.getDefaultFont();
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
		for (int i = 0; i < drawables.size(); i++) {
			if (i == index) {
				drawables.get(i)._enabled = true;
			} else {
				drawables.get(i)._enabled = false;
			}
		}
	}

	@Override
	public void draw(GLEx g) {
		if (isOnLoadComplete()) {
			batch.begin();
			gameCollection.draw(batch, gameTime);
			if (drawablesToDraw.size() > 0) {
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
			batch.end();
		}
	}

	public abstract void draw(SpriteBatch batch);

	public void fadeBackBufferToBlack(float a) {
		drawRectangle(LSystem.screenRect, 0f, 0f, 0f, a);
	}

	public void drawRectangle(RectBox rect, LColor c) {
		drawRectangle(rect, c.r, c.g, c.b, c.a);
	}

	public void drawRectangle(RectBox rect, float r, float g, float b, float a) {
		GLEx gl = GLEx.self;
		if (gl != null) {
			gl.glTex2DDisable();
			gl.setColor(r, g, b, a);
			gl.fillRect(rect.x, rect.y, rect.width, rect.height);
			gl.resetColor();
			gl.glTex2DEnable();
		}
	}

	public ArrayList<Drawable> getDrawables() {
		return new ArrayList<Drawable>(drawables);
	}

	@Override
	public final void onLoad() {
		if (GLEx.self != null) {
			if (batch == null) {
				batch = new SpriteBatch();
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

	public abstract void pressed(LTouch e);

	public abstract void released(LTouch e);

	public abstract void move(LTouch e);

	public abstract void drag(LTouch e);

	public abstract void pressed(LKey e);

	public abstract void released(LKey e);

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
		if (drawablesToUpdate.size() > 0) {
			drawablesToUpdate.clear();
		}
		for (Drawable drawable : drawables) {
			drawablesToUpdate.add(drawable);
		}

		boolean otherScreenHasFocus = false;
		boolean coveredByOtherScreen = false;

		Drawable drawable;
		int screenIndex;
		for (; drawablesToUpdate.size() > 0;) {

			screenIndex = drawablesToUpdate.size() - 1;
			drawable = drawablesToUpdate.get(screenIndex);

			drawablesToUpdate.remove(screenIndex);

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
	public final void onKeyDown(LKey e) {
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
	public final void onKeyUp(LKey e) {
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
	public final void touchDown(LTouch e) {
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
	public final void touchUp(LTouch e) {
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
	public final void touchMove(LTouch e) {
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
	public final void touchDrag(LTouch e) {
		drag(e);
	}

	public SpriteBatch getSpriteBatch() {
		return batch;
	}

	public GameTime getGameTime() {
		return gameTime;
	}
	
	public GameComponentCollection Components(){
		return gameCollection;
	}

	@Override
	public final void dispose() {
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
			batch.dispose();
			batch = null;
		}
		unloadContent();
		isInit = false;
	}

}
