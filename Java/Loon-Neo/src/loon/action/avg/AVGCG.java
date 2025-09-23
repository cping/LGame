/**
 * Copyright 2008 - 2010
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
package loon.action.avg;

import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.Screen;
import loon.action.sprite.ISprite;
import loon.action.sprite.Sprites;
import loon.canvas.LColor;
import loon.events.Updateable;
import loon.geom.PointI;
import loon.opengl.GLEx;
import loon.utils.ArrayMap;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TimeUtils;
import loon.utils.timer.LTimerContext;

public final class AVGCG implements LRelease {

	protected Sprites actionRole;

	private float _roleDelay = 60;

	private LTexture _background;

	private ArrayMap _roles;

	private boolean _style, _loop, _closed;

	private int _speed;

	protected int sleep, sleepMax, shakeNumber;

	private LColor _tempColor = new LColor();

	private LColor _cgColor;

	public AVGCG(Screen screen) {
		this(screen, 60f);
	}

	public AVGCG(Screen screen, float delay) {
		this.actionRole = new Sprites(screen);
		this._roles = new ArrayMap();
		this._style = true;
		this._loop = true;
		this._roleDelay = delay;
		this.setSpeed(1);
	}

	public int getSpeed() {
		return _speed;
	}

	public AVGCG setSpeed(int s) {
		this._speed = LSystem.toIScaleFPS(s);
		return this;
	}

	public LTexture getBackgroundCG() {
		return _background;
	}

	public void noneBackgroundCG() {
		if (_background != null) {
			_background.close();
			_background = null;
		}
	}

	public void setBackgroundCG(LTexture backgroundCG) {
		if (backgroundCG == this._background) {
			return;
		}
		if (_background != null) {
			_background.close();
			_background = null;
		}
		this._background = backgroundCG;
	}

	private final static String update(final String n) {
		String name = n;
		if (StringUtils.startsWith(name, LSystem.DOUBLE_QUOTES)) {
			name = StringUtils.replace(name, "\"", LSystem.EMPTY);
		}
		return name;
	}

	public void setBackgroundCG(final String resName) {
		this.setBackgroundCG(LSystem.loadTexture(update(resName)));
	}

	public void add(final String resName, AVGChara chara) {
		if (chara == null) {
			return;
		}
		String path = update(resName);
		synchronized (_roles) {
			chara.setFlag(ISprite.TYPE_FADE_OUT, _roleDelay);
			this._roles.put(path.replaceAll(" ", LSystem.EMPTY).toLowerCase(), chara);
		}
	}

	public void add(String resName, float x, float y) {
		add(resName, x, y, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public void add(final String resName, float x, float y, float w, float h) {
		add(resName, x, y, w, h, true);
	}

	public void add(final String resName, float x, float y, float w, float h, boolean updatePos) {
		add(resName, x, y, MathUtils.ifloor(w), MathUtils.ifloor(h), updatePos);
	}

	public void add(final String resName, float x, float y, int sw, int sh, boolean updatePos) {
		add(resName, x, y, -1, -1, sw, sh, updatePos);
	}

	public void add(final String resName, float x, float y, int w, int h, int sw, int sh) {
		add(resName, x, y, w, h, sw, sh, true);
	}

	public void add(final String resName, float x, float y, int w, int h, int sw, int sh, boolean updatePos) {
		String path = update(resName);
		synchronized (_roles) {
			String keyName = path.replaceAll(" ", LSystem.EMPTY).toLowerCase();
			AVGChara chara = (AVGChara) _roles.get(keyName);
			if (chara == null) {
				final float newX = (x == -1) ? 0 : x;
				final float newY = (y == -1) ? 0 : y;
				if (w > 0 || h > 0) {
					chara = new AVGChara(path, newX, newY, w, h, sw, sh);
				} else {
					chara = new AVGChara(path, newX, newY, sw, sh);
				}
				chara.setFlag(ISprite.TYPE_FADE_OUT, _roleDelay);
				_roles.put(keyName, chara);
			} else {
				chara.setFlag(ISprite.TYPE_FADE_OUT, _roleDelay);
				if (w > 0) {
					chara.setWidth(w);
				}
				if (h > 0) {
					chara.setHeight(h);
				}
				if (updatePos) {
					chara.setX(x == -1 ? chara.getX() : x);
					chara.setY(y == -1 ? chara.getY() : y);
					if (chara.isMoved() && !chara.isXMoved()) {
						chara.clearMovePos();
					}
				}
			}
		}
	}

	public AVGChara remove(final String resName) {
		String path = update(resName);
		synchronized (_roles) {
			final String name = path.replaceAll(" ", LSystem.EMPTY).toLowerCase();
			AVGChara chara = null;
			if (_style) {
				chara = (AVGChara) _roles.get(name);
				if (chara != null) {
					chara.setFlag(ISprite.TYPE_FADE_IN, _roleDelay);
				}
			} else {
				chara = (AVGChara) _roles.remove(name);
				if (chara != null) {
					dispose(chara);
				}
			}
			return chara;
		}
	}

	public void replace(String res1, String res2) {
		String path1 = update(res1);
		String path2 = update(res2);
		synchronized (_roles) {
			final String name = path1.replaceAll(" ", LSystem.EMPTY).toLowerCase();
			AVGChara old = null;
			if (_style) {
				old = (AVGChara) _roles.get(name);
				if (old != null) {
					old.setFlag(ISprite.TYPE_FADE_IN, _roleDelay);
				}
			} else {
				old = (AVGChara) _roles.remove(name);
				if (old != null) {
					dispose(old);
				}
			}
			if (old != null) {
				final float x = old.getX();
				final float y = old.getY();
				AVGChara newObject = new AVGChara(path2, 0, 0, old.maxWidth, old.maxHeight);
				newObject.setFlip(old.flipX, old.flipY);
				newObject.setVisible(old.visible);
				newObject.setColor(old.getColor());
				newObject.setMove(false);
				newObject.setX(x);
				newObject.setY(y);
				add(path2, newObject);
			}
		}
	}

	private final static void dispose(final AVGChara c) {
		final Updateable remove = new Updateable() {
			@Override
			public void action(Object a) {
				c.close();
			}
		};
		LSystem.load(remove);
	}

	public void update(LTimerContext context) {
		actionRole.update(context.timeSinceLastUpdate);
	}

	public LColor getCGColor() {
		return _cgColor;
	}

	public AVGCG setCGColor(LColor c) {
		if (this._cgColor == null) {
			this._cgColor = new LColor(c);
		} else {
			this._cgColor.setColor(c);
		}
		return this;
	}

	public AVGCG setCGColor(String c) {
		if (this._cgColor == null) {
			this._cgColor = new LColor(c);
		} else {
			this._cgColor.setColor(c);
		}
		return this;
	}

	public AVGCG emptyCGColor() {
		return setCGColor(LColor.white);
	}

	public AVGChara getChara(String name) {
		return (AVGChara) _roles.get(name);
	}

	public AVGCG setCharaFlip(String name, boolean flipX, boolean flipY) {
		AVGChara chara = (AVGChara) _roles.get(name);
		if (chara != null) {
			chara.setFlip(flipX, flipY);
		}
		return this;
	}

	public AVGCG setCharaColor(String name, String color) {
		AVGChara chara = (AVGChara) _roles.get(name);
		if (chara != null) {
			chara.setColor(color);
		}
		return this;
	}

	public AVGCG setCharaColor(String name, LColor color) {
		AVGChara chara = (AVGChara) _roles.get(name);
		if (chara != null) {
			chara.setColor(color);
		}
		return this;
	}

	public void paint(GLEx g) {
		final float newX = actionRole.getX();
		final float newY = actionRole.getY();
		final float a = g.alpha();
		if (_background != null) {
			if (shakeNumber > 0) {
				g.draw(_background, newX + shakeNumber / 2 - MathUtils.random(shakeNumber),
						newY + shakeNumber / 2 - MathUtils.random(shakeNumber));
			} else {
				g.draw(_background, newX, newY);
			}
		}
		synchronized (_roles) {
			for (int i = 0; i < _roles.size(); i++) {
				AVGChara chara = (AVGChara) _roles.get(i);
				if (chara == null || !chara.visible) {
					continue;
				}
				if (_style) {
					if (chara.flag != -1) {
						if (chara.flag == ISprite.TYPE_FADE_IN) {
							chara.currentFrame -= _speed;
							if (chara.currentFrame == 0) {
								chara.opacity = 0;
								chara.flag = -1;
								chara.close();
								_roles.remove(chara);
							}
						} else {
							chara.currentFrame += _speed;
							if (MathUtils.equal(chara.currentFrame, chara.time)) {
								chara.opacity = 0;
								chara.flag = -1;
							}
						}
						chara.opacity = (chara.currentFrame / chara.time) * 255;
						if (chara.opacity > 0) {
							g.setAlpha(chara.opacity / 255);
						}
					}
				}
				if (chara.showAnimation) {
					AVGAnm animation = chara.anm;
					if (animation.loaded) {
						if (animation.loop && animation.startTime == -1) {
							animation.start(0, _loop);
						}
						PointI point = animation.getPos(TimeUtils.millis());
						if (animation.alpha != 1f) {
							g.setAlpha(animation.alpha);
						}
						g.draw(animation.texture, newX + chara.getX(), newY + chara.getY(), animation.width,
								animation.height, point.x, point.y, point.x + animation.imageWidth,
								point.y + animation.imageHeight,
								_tempColor.setColor(LColor.combine(animation.color, _cgColor)), animation.angle);
						if (animation.alpha != 1f) {
							g.setAlpha(1f);
						}
					}
				} else {
					chara.next();
					chara.draw(g, newX, newY, _cgColor);
				}
				if (_style) {
					if (chara.flag != -1 && chara.opacity > 0) {
						g.setAlpha(1f);
					}
				}
			}
		}
		actionRole.createUI(g);
		g.setAlpha(a);
	}

	public void clear() {
		synchronized (_roles) {
			_roles.clear();
			actionRole.clear();
		}
	}

	public ArrayMap getCharas() {
		return _roles;
	}

	public int count() {
		if (_roles != null) {
			return _roles.size();
		}
		return 0;
	}

	public float getRoleDelay() {
		return _roleDelay;
	}

	public void setRoleDelay(float roleDelay) {
		this._roleDelay = roleDelay;
	}

	public boolean isStyle() {
		return _style;
	}

	public void setStyle(boolean style) {
		this._style = style;
	}

	public boolean isLoop() {
		return _loop;
	}

	public void setLoop(boolean loop) {
		this._loop = loop;
	}

	public Sprites getSprites() {
		return actionRole;
	}

	public Sprites getActionRole() {
		return actionRole;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		synchronized (_roles) {
			if (_style) {
				for (int i = 0; i < _roles.size(); i++) {
					AVGChara ch = (AVGChara) _roles.get(i);
					if (ch != null) {
						ch.setFlag(ISprite.TYPE_FADE_IN, _roleDelay);
					}
				}
			} else {
				for (int i = 0; i < _roles.size(); i++) {
					AVGChara ch = (AVGChara) _roles.get(i);
					if (ch != null) {
						ch.close();
						ch = null;
					}
				}
				_roles.clear();
			}
			actionRole.clear();
		}
		_closed = true;
	}

}
