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
package com.mygame;

import loon.LSystem;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.action.RotateTo;
import loon.action.map.TileMap;
import loon.action.sprite.Animation;
import loon.action.sprite.JumpObject;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatchObject;
import loon.action.sprite.SpriteBatchScreen;
import loon.canvas.LColor;
import loon.component.LPad;
import loon.event.ActionKey;
import loon.event.GameKey;
import loon.event.GameTouch;
import loon.event.SysKey;
import loon.geom.Vector2f;

//PS:使用SpriteBatch时，请尽可能使用同一LTexture衍生出的图片，这样后台能有效的合并处理
public class GameMapTest extends SpriteBatchScreen {

	// 敌人用类
	class Enemy extends SpriteBatchObject {

		private static final float SPEED = 1;

		protected float vx;
		protected float vy;

		public Enemy(float x, float y, Animation animation, TileMap tiles) {
			super(x, y, 32, 32, animation, tiles);
			vx = -SPEED;
			vy = 0;
		}

		public void update(long e) {

			float x = getX();
			float y = getY();

			vy += 0.6f;

			float newX = x + vx;

			// 判断预期坐标是否与瓦片相撞(X坐标测试)
			Vector2f tile = tiles.getTileCollision(this, newX, y);

			if (tile == null) {
				x = newX;
			} else {
				if (vx > 0) {
					x = tiles.tilesToPixelsX(tile.x) - getWidth();
				} else if (vx < 0) {
					x = tiles.tilesToPixelsY(tile.x + 1);
				}
				vx = -vx;
			}

			float newY = y + vy;

			// 判断预期坐标是否与瓦片相撞(y坐标测试)
			tile = tiles.getTileCollision(this, x, newY);
			if (tile == null) {
				y = newY;
			} else {
				if (vy > 0) {
					y = tiles.tilesToPixelsY(tile.y) - getHeight();
					vy = 0;
				} else if (vy < 0) {
					y = tiles.tilesToPixelsY(tile.y + 1);
					vy = 0;
				}
			}

			animation.update(elapsedTime);
			// 注入新坐标
			setLocation(x, y);
		}

	}

	// 二次跳跃用类（物品）
	class JumperTwo extends SpriteBatchObject {

		public JumperTwo(float x, float y, Animation animation, TileMap tiles) {
			super(x, y, 32, 32, animation, tiles);
		}

		public void use(JumpObject hero) {
			hero.setJumperTwo(true);
		}

		public void update(long elapsedTime) {
			animation.update(elapsedTime);
		}
	}

	// 加速用类（物品）
	class Accelerator extends SpriteBatchObject {

		public Accelerator(float x, float y, Animation animation, TileMap tiles) {
			super(x, y, 32, 32, animation, tiles);
		}

		public void use(JumpObject hero) {
			hero.setSpeed(hero.getSpeed() * 2);
		}

		public void update(long elapsedTime) {
			animation.update(elapsedTime);
		}
	}

	// 金币用类（物品）
	class Coin extends SpriteBatchObject {

		public Coin(float x, float y, Animation animation, TileMap tiles) {
			super(x, y, 32, 32, animation, tiles);
		}

		public void update(long elapsedTime) {
			animation.update(elapsedTime);
		}

	}

	private JumpObject hero;

	// PS：如果具体游戏开发时用到多动画切换，则建议使用AnimationStorage这个Animation的子类
	// 金币用动画图
	private Animation coinAnimation;

	// 敌人用动画图(过滤掉黑色)
	private Animation enemyAnimation;

	// 加速道具动画图
	private Animation accelAnimation;

	// 二级跳动画图
	private Animation jumpertwoAnimation;

	public void create() {

		// 以指定图片创建动画
		this.coinAnimation = Animation.getDefaultAnimation("assets/coin.png",
				32, 32, 200);
		this.enemyAnimation = Animation.getDefaultAnimation("assets/enemy.gif",
				32, 32, 200, LColor.black);
		this.accelAnimation = Animation.getDefaultAnimation(
				"assets/accelerator.gif", 32, 32, 200);
		this.jumpertwoAnimation = Animation.getDefaultAnimation(
				"assets/jumper_two.gif", 32, 32, 200);

		// 注销Screen时释放下列资源
		putReleases(coinAnimation, enemyAnimation, accelAnimation,
				jumpertwoAnimation, hero);

		// 加载一张由字符串形成的地图（如果不使用此方式加载，则默认使用标准的数组地图）
		final TileMap indexMap = TileMap.loadCharsMap("assets/map.chr", 32, 32);
		// 如果有配置好的LTexturePack文件，可于此注入
		// indexMap.setImagePack(file);
		// 设定无法穿越的区域(如果不设置此项，所有索引不等于"-1"的区域都可以穿越)
		indexMap.setLimit(new int[] { 'B', 'C', 'i', 'c' });
		indexMap.putTile('B', "assets/block.png");
		int imgId = indexMap.putTile('C', "assets/coin_block.gif");
		// 因为两块瓦片对应同一地图字符，所以此处使用了已载入的图片索引
		indexMap.putTile('i', imgId);
		indexMap.putTile('c', "assets/coin_block2.gif");

		// 加载此地图到窗体中
		putTileMap(indexMap);

		// 获得地图对应的二维数组
		final int[][] maps = indexMap.getMap();

		int w = indexMap.getRow();
		int h = indexMap.getCol();

		// 遍历二维数组地图，并以此为基础添加角色到窗体之上
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				switch (maps[j][i]) {
				case 'o':
					Coin coin = new Coin(indexMap.tilesToPixelsX(i),
							indexMap.tilesToPixelsY(j), new Animation(
									coinAnimation), indexMap);
					addTileObject(coin);
					break;
				case 'k':
					Enemy enemy = new Enemy(indexMap.tilesToPixelsX(i),
							indexMap.tilesToPixelsY(j), new Animation(
									enemyAnimation), indexMap);
					addTileObject(enemy);
					break;
				case 'a':
					Accelerator accelerator = new Accelerator(
							indexMap.tilesToPixelsX(i),
							indexMap.tilesToPixelsY(j), new Animation(
									accelAnimation), indexMap);
					addTileObject(accelerator);
					break;
				case 'j':
					JumperTwo jump = new JumperTwo(indexMap.tilesToPixelsX(i),
							indexMap.tilesToPixelsY(j), new Animation(
									jumpertwoAnimation), indexMap);
					addTileObject(jump);
					break;
				}
			}
		}

		// 获得主角动作图
		Animation animation = Animation.getDefaultAnimation("assets/hero.png",
				20, 20, 150, LColor.black);

		// 在像素坐标位置(192,32)放置角色，大小为32x32，动画为针对hero.png的分解图
		hero = addJumpObject(192, 32, 32, 32, animation);

		// 让地图跟随指定对象产生移动（无论插入有多少张数组地图，此跟随默认对所有地图生效）
		// 另外请注意，此处能产生跟随的对像是任意LObject，并不局限于游戏角色。
		follow(hero);

		// 监听跳跃事件
		hero.listener = new JumpObject.JumpListener() {

			public void update(long elapsedTime) {

			}

			// 检查角色与地图中瓦片的碰撞
			public void check(int x, int y) {
				if (indexMap.getTileID(x, y) == 'C') {
					indexMap.setTileID(x, y, 'c');
					Enemy enemy = new Enemy(indexMap.tilesToPixelsX(x),
							indexMap.tilesToPixelsY(y - 1), new Animation(
									enemyAnimation), indexMap);
					add(enemy);
					// 标注地图已脏，强制缓存刷新
					indexMap.setDirty(true);
				} else if (indexMap.getTileID(x + 1, y) == 'C') {
					indexMap.setTileID(x + 1, y, 'c');
					indexMap.setDirty(true);
				}

			}
		};

		// 对应向左行走的键盘事件
		ActionKey goLeftKey = new ActionKey() {
			public void act(long e) {
				hero.setMirror(true);
				hero.accelerateLeft();
			}
		};

		addActionKey(SysKey.LEFT, goLeftKey);

		// 对应向右行走的键盘事件
		ActionKey goRightKey = new ActionKey() {
			public void act(long e) {
				hero.setMirror(false);
				hero.accelerateRight();
			}
		};

		addActionKey(SysKey.RIGHT, goRightKey);

		// 对应跳跃的键盘事件（DETECT_INITIAL_PRESS_ONLY表示在放开之前，此按键不会再次触发）
		ActionKey jumpKey = new ActionKey(ActionKey.DETECT_INITIAL_PRESS_ONLY) {
			public void act(long e) {
				hero.jump();
			}
		};

		addActionKey(SysKey.UP, jumpKey);

			LPad pad = new LPad(10, 180);
			LPad.ClickListener click = new LPad.ClickListener() {

				public void up() {
					pressActionKey(SysKey.UP);
				}

				public void right() {
					pressActionKey(SysKey.RIGHT);
				}

				public void left() {
					pressActionKey(SysKey.LEFT);
				}

				public void down() {
					pressActionKey(SysKey.DOWN);
				}

				public void other() {
					releaseActionKeys();
				}

			};
			pad.setListener(click);
			add(pad);
		

		// 地图中角色事件监听(每帧都会触发一次此监听)
		this.updateListener = new UpdateListener() {

			public void act(SpriteBatchObject sprite, long elapsedTime) {

				// 如果主角与地图上其它对象发生碰撞（以下分别验证）
				if (hero.isCollision(sprite)) {
					// 与敌人
					if (sprite instanceof Enemy) {
						Enemy e = (Enemy) sprite;
						if (hero.y() < e.y()) {
							hero.setForceJump(true);
							hero.jump();
							removeTileObject(e);
						} else {
							damage();
						}
						// 与金币
					} else if (sprite instanceof Coin) {
						Coin coin = (Coin) sprite;
						removeTileObject(coin);
						// 与加速道具
					} else if (sprite instanceof Accelerator) {
						removeTileObject(sprite);
						Accelerator accelerator = (Accelerator) sprite;
						accelerator.use(hero);
						// 与二次弹跳道具
					} else if (sprite instanceof JumperTwo) {
						removeTileObject(sprite);
						JumperTwo jumperTwo = (JumperTwo) sprite;
						jumperTwo.use(hero);
					}
				}
			}
		};

	}

	private RotateTo rotate;

	public void damage() {
		// 主角与敌人碰撞时(而非踩到了敌人)，触发一个旋转动作(其实效果可以做的更有趣一些，
		// 比如先反弹到某一方向(FireTo)，然后再弹回等等，此处仅仅举个例子)
		if (rotate == null) {
			// 旋转360度，每帧累加5度
			rotate = new RotateTo(360f, 5f);
			rotate.setActionListener(new ActionListener() {

				public void stop(ActionBind o) {
					hero.setFilterColor(LColor.white);
					hero.setRotation(0);
				}

				public void start(ActionBind o) {
					hero.setFilterColor(LColor.red);
					hero.jump();
				}

				public void process(ActionBind o) {

				}
			});
			addAction(rotate, hero);
		} else if (rotate.isComplete()) {
			hero.setFilterColor(LColor.red);
			// 直接重置rotate对象
			rotate.start(hero);
			// 重新插入(LGame的方针是Action事件触发且结束后，自动删除该事件，所以需要重新插入)
			addAction(rotate, hero);
		}
	}

	// 背景绘制
	public void after(SpriteBatch batch) {

	}

	// 前景绘制
	public void before(SpriteBatch batch) {

	}

	public void update(long elapsedTime) {
		if (hero != null) {
			hero.stop();
		}
	}

	// 释放资源
	public void close() {

	}

	@Override
	public void touchDrag(GameTouch e) {
		
	}
	
	public void touchDown(GameTouch e) {

	}

	public void touchUp(GameTouch e) {

	}

	public void touchMove(GameTouch e) {

	}

	public void press(GameKey e) {

	}

	public void release(GameKey e) {

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}


}
