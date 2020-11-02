package org.loon.framework.android.game.action.sprite.effect;

import org.loon.framework.android.game.core.LSystem;
import org.loon.framework.android.game.core.geom.Vector2D;
import org.loon.framework.android.game.core.graphics.LColor;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.utils.CollectionUtils;

/**
 * Copyright 2008 - 2011
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * @project loonframework
 * @author chenpeng  
 * @email：ceponline@yahoo.com.cn 
 * @version 0.1
 */
public class SmashEffect {

	private Explosion[] explosionsList;

	public static enum SmashType {
		red, yellow, orange, green, blue, white, star;
	}

	class Smash {

		private float velocity;

		private Vector2D direction;

		private int moveX;

		private int moveY;

		private SmashType type;

		public Smash() {
			this.direction = new Vector2D();
		}

		public Smash(int x, int y, float v, Vector2D d, SmashEffect.SmashType p) {
			this.moveX = x;
			this.moveY = y;
			this.velocity = v;
			this.direction = d;
			this.type = p;
		}
	}

	public SmashEffect() {
		this(40);
	}

	public SmashEffect(int s) {
		this(s, 0, 2, 2);
	}

	public SmashEffect(int s, int type, int w, int h) {
		this.explosionsList = new Explosion[s];
		for (int i = 0; i < this.explosionsList.length; i++) {
			this.explosionsList[i] = new Explosion(s, type, w, h);
		}
	}

	public synchronized void reset() {
		int size = explosionsList.length;
		for (int i = 0; i < size; i++) {
			explosionsList[i].expired = true;
			explosionsList[i].timer = 0;
		}
	}

	public synchronized void createExplosionEffect(int x, int y,
			SmashEffect.SmashType p) {
		int i = -1;
		for (int j = 0; j < this.explosionsList.length; j++) {
			if (this.explosionsList[j].expired != true) {
				continue;
			}
			i = j;
			break;
		}
		if (i > -1) {
			this.explosionsList[i].createExplosion(x, y, p, 40);
		}
	}

	public synchronized void createSmallExplosionEffect(int x, int y) {
		createSmallExplosionEffect(x, y, SmashType.white, 20);
	}

	public synchronized void createSmallExplosionEffect(int x, int y,
			SmashType p, int n) {
		int i = -1;
		for (int j = 0; j < this.explosionsList.length; j++) {
			if (this.explosionsList[j].expired != true) {
				continue;
			}
			i = j;
			break;
		}
		if (i > -1) {
			this.explosionsList[i].createExplosion(x, y, p, n);
		}
	}

	public synchronized void createPlayerExplosionEffect(int x, int y) {
		int i = -1;
		for (int j = 0; j < this.explosionsList.length; j++) {
			if (this.explosionsList[j].expired != true)
				continue;
			i = j;
			break;
		}
		if (i > -1) {
			this.explosionsList[i].createExplosion(x, y, SmashType.white, 200);
		}
	}

	public synchronized void update(long elapsedTime) {
		int i = 0;
		Explosion explosion;
		for (i = 0; i < this.explosionsList.length; i++) {
			explosion = this.explosionsList[i];
			if (explosion.expired) {
				continue;
			}
			explosion.update(elapsedTime);
		}
		for (i = 0; i < this.explosionsList.length; i++) {
			explosion = this.explosionsList[i];
			if (explosion.expired) {
				continue;
			}
			if (explosion.timer <= 1500L) {
				continue;
			}
			explosion.expired = true;
		}
	}

	public synchronized void draw(LGraphics g) {
		for (int i = 0; i < this.explosionsList.length; i++) {
			if (this.explosionsList[i].expired) {
				continue;
			}
			this.explosionsList[i].draw(g);
		}
	}

	private class Explosion {

		private int type;

		private Smash[] smashs;

		private int width, height;

		private long timer = 0L;

		private float alpha = 1.0F;

		public boolean expired = true;

		private int numSmash;


		public Explosion(int s, int type, int width, int height) {
			this.type = type;
			this.width = width;
			this.height = height;
			this.smashs = new Smash[s];
			this.expired = true;
			for (int i = 0; i < this.smashs.length && i < s; i++) {
				this.smashs[i] = new Smash();
			}
		}

		public synchronized void createExplosion(int x, int y, SmashType type,
				int n) {
			this.timer = 0L;
			this.expired = false;
			this.numSmash = n;
			if (smashs.length != n) {
				smashs = (Smash[]) CollectionUtils.expand(smashs, n);
				for (int i = 0; i < this.smashs.length; i++) {
					this.smashs[i] = new Smash();
				}
			}
			for (int i = 0; i < n; i++) {
				this.smashs[i].moveX = x;
				this.smashs[i].moveY = y;
				this.smashs[i].direction.x = (LSystem.getRandomBetWeen(-1000,
						1000) / 1000.0F);
				this.smashs[i].direction.y = (LSystem.getRandomBetWeen(-1000,
						1000) / 1000.0F);
				this.smashs[i].type = type;
				this.smashs[i].velocity = (LSystem.getRandomBetWeen(4, 8) / 10.0F);
			}
		}

		public synchronized void draw(LGraphics g) {
			this.alpha = ((float) (1500L - this.timer) / 1500.0F);
			if (this.alpha < 0.0F) {
				this.alpha = 0.0F;
			}
			if (this.alpha > 1.0F) {
				this.alpha = 1.0F;
			}
			if (alpha != 1) {
				g.setAlpha(alpha);
			}
			switch (type) {
			case 0:
				for (int i = 0; i < this.numSmash; i++) {
					Smash p = this.smashs[i];
					if (p.type == SmashType.red) {
						g.setColor(LColor.red);
						g.fillRect(p.moveX, p.moveY, width, height);
					} else if (p.type == SmashType.yellow) {
						g.setColor(LColor.yellow);
						g.fillRect(p.moveX, p.moveY, width, height);
					} else if (p.type == SmashType.orange) {
						g.setColor(LColor.orange);
						g.fillRect(p.moveX, p.moveY, width, height);
					} else if (p.type == SmashType.green) {
						g.setColor(LColor.green);
						g.fillRect(p.moveX, p.moveY, width, height);
					} else if (p.type == SmashType.blue) {
						g.setColor(LColor.blue);
						g.fillRect(p.moveX, p.moveY, width, height);
					} else if (p.type == SmashType.white) {
						g.setColor(LColor.white);
						g.fillRect(p.moveX, p.moveY, width, height);
					}
				}
				break;
			case 1:
				for (int i = 0; i < this.numSmash; i++) {
					Smash p = this.smashs[i];
					if (p.type == SmashType.red) {
						g.setColor(LColor.red);
						g.fillOval(p.moveX, p.moveY, width, height);
					} else if (p.type == SmashType.yellow) {
						g.setColor(LColor.yellow);
						g.fillOval(p.moveX, p.moveY, width, height);
					} else if (p.type == SmashType.orange) {
						g.setColor(LColor.orange);
						g.fillOval(p.moveX, p.moveY, width, height);
					} else if (p.type == SmashType.green) {
						g.setColor(LColor.green);
						g.fillOval(p.moveX, p.moveY, width, height);
					} else if (p.type == SmashType.blue) {
						g.setColor(LColor.blue);
						g.fillOval(p.moveX, p.moveY, width, height);
					} else if (p.type == SmashType.white) {
						g.setColor(LColor.white);
						g.fillOval(p.moveX, p.moveY, width, height);
					}
				}
				break;
			}
			g.resetColor();
			if (alpha != 1) {
				g.setAlpha(1.0f);
			}
		}

		public synchronized void update(long elapsedTime) {
			for (int i = 0; i < this.smashs.length; i++) {
				Smash p = this.smashs[i];
				p.moveX += (int) (p.velocity * p.direction.x * elapsedTime);
				p.moveY += (int) (p.velocity * p.direction.y * elapsedTime);
			}
			this.timer += elapsedTime;
		}

	}

}


