package loon.action.avg;

import loon.action.sprite.ISprite;
import loon.core.LSystem;
import loon.core.event.Updateable;
import loon.core.geom.Point.Point2i;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.graphics.opengl.LTexture.Format;
import loon.utils.StringUtils;
import loon.utils.collection.ArrayMap;

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
public class AVGCG {

	private long charaShowDelay = 60;

	private LTexture background;

	private ArrayMap charas;

	private boolean style, loop;

	int sleep, sleepMax, shakeNumber;

	public AVGCG() {
		this.charas = new ArrayMap(10);
		this.style = true;
		this.loop = true;
	}

	public LTexture getBackgroundCG() {
		return background;
	}

	public void noneBackgroundCG() {
		if (background != null) {
			background.destroy();
			background = null;
		}
	}

	public void setBackgroundCG(LTexture backgroundCG) {
		if (backgroundCG == this.background) {
			return;
		}
		if (background != null) {
			background.destroy();
			background = null;
		}
		this.background = backgroundCG;
	}

	private final static String _update(final String n) {
		String name = n;
		if (StringUtils.startsWith(name, '"')) {
			name = name.replaceAll("\"", "");
		}
		return name;
	}

	public void setBackgroundCG(final String resName) {
		this.setBackgroundCG(LTextures.loadTexture(_update(resName),
				Format.STATIC));
	}

	public void add(final String resName, AVGChara chara) {
		if (chara == null) {
			return;
		}
		String path = _update(resName);
		synchronized (charas) {
			chara.setFlag(ISprite.TYPE_FADE_OUT, charaShowDelay);
			this.charas.put(path.replaceAll(" ", "").toLowerCase(), chara);
		}
	}

	public void add(String resName, int x, int y) {
		add(resName, x, y, LSystem.screenRect.width, LSystem.screenRect.height);
	}

	public void add(final String resName, int x, int y, int w, int h) {
		String path = _update(resName);
		synchronized (charas) {
			String keyName = path.replaceAll(" ", "").toLowerCase();
			AVGChara chara = (AVGChara) charas.get(keyName);
			if (chara == null) {
				chara = new AVGChara(path, x, y, w, h);
				chara.setFlag(ISprite.TYPE_FADE_OUT, charaShowDelay);
				charas.put(keyName, chara);
			} else {
				chara.setFlag(ISprite.TYPE_FADE_OUT, charaShowDelay);
				chara.setX(x);
				chara.setY(y);
			}
		}
	}

	public AVGChara remove(final String resName) {
		String path = _update(resName);
		synchronized (charas) {
			final String name = path.replaceAll(" ", "").toLowerCase();
			AVGChara chara = null;
			if (style) {
				chara = (AVGChara) charas.get(name);
				if (chara != null) {
					chara.setFlag(ISprite.TYPE_FADE_IN, charaShowDelay);
				}
			} else {
				chara = (AVGChara) charas.remove(name);
				if (chara != null) {
					android_dispose(chara);
				}
			}
			return chara;
		}
	}

	public void replace(String res1, String res2) {
		String path1 = _update(res1);
		String path2 = _update(res2);
		synchronized (charas) {
			final String name = path1.replaceAll(" ", "").toLowerCase();
			AVGChara old = null;
			if (style) {
				old = (AVGChara) charas.get(name);
				if (old != null) {
					old.setFlag(ISprite.TYPE_FADE_IN, charaShowDelay);
				}
			} else {
				old = (AVGChara) charas.remove(name);
				if (old != null) {
					android_dispose(old);
				}
			}
			if (old != null) {
				final int x = old.getX();
				final int y = old.getY();
				AVGChara newObject = new AVGChara(path2, 0, 0, old.maxWidth,
						old.maxHeight);
				newObject.setMove(false);
				newObject.setX(x);
				newObject.setY(y);
				add(path2, newObject);
			}
		}
	}

	private final static void android_dispose(final AVGChara c) {
		Updateable remove = new Updateable() {
			@Override
			public void action() {
				c.dispose();
			}
		};
		LSystem.load(remove);
	}

	public void paint(GLEx g) {
		if (background != null) {
			if (shakeNumber > 0) {
				g.drawTexture(background,
						shakeNumber / 2 - LSystem.random.nextInt(shakeNumber),
						shakeNumber / 2 - LSystem.random.nextInt(shakeNumber));
			} else {
				g.drawTexture(background, 0, 0);
			}
		}
		synchronized (charas) {
			for (int i = 0; i < charas.size(); i++) {
				AVGChara chara = (AVGChara) charas.get(i);
				if (chara == null || !chara.isVisible) {
					continue;
				}
				if (style) {
					if (chara.flag != -1) {
						if (chara.flag == ISprite.TYPE_FADE_IN) {
							chara.currentFrame--;
							if (chara.currentFrame == 0) {
								chara.opacity = 0;
								chara.flag = -1;
								chara.dispose();
								charas.remove(chara);
							}
						} else {
							chara.currentFrame++;
							if (chara.currentFrame == chara.time) {
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
				if (chara.isAnimation) {
					AVGAnm animation = chara.anm;
					if (animation.load) {
						if (animation.loop && animation.startTime == -1) {
							animation.start(0, loop);
						}
						Point2i point = animation.getPos(System
								.currentTimeMillis());
						if (animation.alpha != 1f) {
							g.setAlpha(animation.alpha);
						}
						g.drawTexture(animation.texture, chara.x, chara.y,
								animation.width, animation.height, point.x,
								point.y, point.x + animation.imageWidth,
								point.y + animation.imageHeight,
								animation.angle, animation.color);
						if (animation.alpha != 1f) {
							g.setAlpha(1f);
						}
					}
				} else {
					chara.next();
					chara.draw(g);
				}
				if (style) {
					if (chara.flag != -1 && chara.opacity > 0) {
						g.setAlpha(1f);
					}
				}
			}
		}
	}

	public void clear() {
		synchronized (charas) {
			charas.clear();
		}
	}

	public ArrayMap getCharas() {
		return charas;
	}

	public int count() {
		if (charas != null) {
			return charas.size();
		}
		return 0;
	}

	public long getCharaShowDelay() {
		return charaShowDelay;
	}

	public void setCharaShowDelay(long charaShowDelay) {
		this.charaShowDelay = charaShowDelay;
	}

	public boolean isStyle() {
		return style;
	}

	public void setStyle(boolean style) {
		this.style = style;
	}

	public boolean isLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	public void dispose() {
		synchronized (charas) {
			if (style) {
				for (int i = 0; i < charas.size(); i++) {
					AVGChara ch = (AVGChara) charas.get(i);
					if (ch != null) {
						ch.setFlag(ISprite.TYPE_FADE_IN, charaShowDelay);
					}
				}
			} else {
				for (int i = 0; i < charas.size(); i++) {
					AVGChara ch = (AVGChara) charas.get(i);
					if (ch != null) {
						ch.dispose();
						ch = null;
					}
				}
				charas.clear();
				System.gc();
			}
		}
	}

}
