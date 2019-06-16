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
package org.test;

import loon.Counter;
import loon.Screen;
import loon.action.map.Field2D;
import loon.action.map.Side;
import loon.action.sprite.GridEntity;
import loon.canvas.LColor;
import loon.component.LPad;
import loon.event.ActionKey;
import loon.event.GameTouch;
import loon.event.SysKey;
import loon.event.SysTouch;
import loon.event.Updateable;
import loon.geom.BooleanValue;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public class SnakeTest extends Screen {

	private Vector2f fruit = new Vector2f();

	private int snakeLength;

	private int snakeHead;

	private Counter score;

	private Side direction = new Side();

	private Field2D map;

	private TArray<Vector2f> snake;

	private BooleanValue gameOver;

	private LColor color = new LColor();

	@Override
	public void draw(GLEx g) {
		if (!isOnLoadComplete()) {
			return;
		}
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				int type = map.getTileType(x, y);
				if (type != -1) {
					if (type == 1) {
						color.setColor(LColor.yellow);
					} else if (type == 2) {
						color.setColor(LColor.red);
					}
					//绘制'蛇'和果实(如果有图的话可以用图片替换这部分)
					g.fillOval(map.tilesToWidthPixels(x), map.tilesToWidthPixels(y), map.getTileWidth(),
							map.getTileHeight(), color);
				}
			}
		}
		g.drawString("Score:" + score.getValue(), 220, 40, LColor.green);
		if (gameOver.result()) {
			String text = "Game Over";
			int w = g.getFont().stringWidth(text);
			g.drawString(text, (getWidth() - w) / 2, getHalfHeight());
		}
	}

	@Override
	public void onLoad() {
		// 最后绘制桌面组件
		lastDesktopDraw();

		// 背景蓝色
		setBackground(LColor.blue);

		// 构建一个分数计数器
		score = newCounter();
		// 记录游戏是否失败
		gameOver = new BooleanValue(false);

		// 设定瓦片大小20x20
		int tileWidth = 20;
		int tileHeight = 20;

		// 构建一个等于屏幕大小的二维数组地图对象
		map = new Field2D(getWidth() / tileWidth, getHeight() / tileHeight, tileWidth, tileHeight);

		// 向上事件
		final ActionKey gameUp = new ActionKey();
		gameUp.setFunction(new Updateable() {

			@Override
			public void action(Object a) {
				if (direction.getDirection() != Side.TDOWN) {
					direction.setDirection(Side.TUP);
				}
			}
		});
		addActionKey(SysKey.UP, gameUp);

		// 向右
		final ActionKey gameRight = new ActionKey();
		gameRight.setFunction(new Updateable() {

			@Override
			public void action(Object a) {
				if (direction.getDirection() != Side.TLEFT) {
					direction.setDirection(Side.TRIGHT);
				}
			}
		});
		addActionKey(SysKey.RIGHT, gameRight);

		// 向左
		final ActionKey gameLeft = new ActionKey();
		gameLeft.setFunction(new Updateable() {

			@Override
			public void action(Object a) {
				if (direction.getDirection() != Side.TRIGHT) {
					direction.setDirection(Side.TLEFT);
				}
			}
		});
		addActionKey(SysKey.LEFT, gameLeft);

		// 向下
		final ActionKey gameDown = new ActionKey();
		gameDown.setFunction(new Updateable() {

			@Override
			public void action(Object a) {
				if (direction.getDirection() != Side.TUP) {
					direction.setDirection(Side.TDOWN);
				}
			}
		});
		addActionKey(SysKey.DOWN, gameDown);

		// 构建一个4方向虚拟手柄
		LPad pad = new LPad(25, 155);
		pad.setListener(new LPad.ClickListener() {

			@Override
			public void up() {
				gameUp.act();
			}

			@Override
			public void right() {
				gameRight.act();
			}

			@Override
			public void left() {
				gameLeft.act();
			}

			@Override
			public void down() {
				gameDown.act();
			}

			@Override
			public void other() {

			}
		});
		add(pad);

		// 构建一个计时器,管理'蛇'移动
		LTimer moveTimer = new LTimer(200);

		// 注入事件
		moveTimer.setUpdateable(new Updateable() {

			@Override
			public void action(Object a) {
				if (gameOver.result()) {
					if (SysTouch.isDown() || SysKey.isDown()) {
						newGame();
					}
					return;
				}
				Vector2f oldHead = (Vector2f) snake.get(snakeHead);
				Vector2f newHead = new Vector2f(oldHead.x + direction.dx(), oldHead.y + direction.dy());

				boolean selfCollision = false;
				for (Vector2f snakePart : snake) {
					if ((newHead.x == snakePart.x) && (newHead.y == snakePart.y) && (newHead != snake.get(0))) {
						selfCollision = true;
					}
				}

				if (!map.contains(newHead.x(), newHead.y())) {
					gameOver.set(true);
				} else if (selfCollision) {
					gameOver.set(true);
				} else {
					boolean eatFruit = (newHead.x == fruit.x) && (newHead.y == fruit.y);

					snake.add(newHead);
					snakeHead = (snake.size() - 1);

					if (!eatFruit) {
						snake.set(snakeHead - snakeLength, null);
					} else {
						snakeLength += 1;
						score.increment(1);
						newFruit();
					}

					TArray<Vector2f> newSnake = new TArray<Vector2f>();
					for (Vector2f snakePart : snake) {
						if (snakePart != null) {
							newSnake.add(snakePart);
						}
					}
					snake = newSnake;
					snakeHead = (snake.size() - 1);
				}

				map.fill(-1);
				for (Vector2f snakePart : snake) {
					if (snakePart != null) {
						map.setTileType(snakePart.x(), snakePart.y(), 1);
					}
				}
				map.setTileType(fruit.x(), fruit.y(), 2);

			}
		});
		// 提交计时器
		moveTimer.submit();
		// 注销Screen时销毁moveTimer
		putRelease(moveTimer);
		
		//注入网格,总大小为Screen大小,单独网格大小为二维地图瓦片大小
		add(new GridEntity(0, 0, getWidth(), getHeight(), map.getTileWidth(), map.getTileHeight(), LColor.gray));

		// 初始化游戏设定
		newGame();

		add(MultiScreenTest.getBackButton(this, 2));
	}

	/**
	 * 产生一个果实
	 */
	private void newFruit() {
		int w = map.getWidth() - 2;
		int h = map.getHeight() - 2;
		int x = MathUtils.random(1, w);
		int y = MathUtils.random(1, h);
		if(map.getTileType(x, y) != -1){
			newFruit();
			return;
		}
		this.fruit = new Vector2f(x, y);
	}

	/**
	 * 开始游戏(初始化参数)
	 */
	private void newGame() {
		newFruit();

		int startX = 5;
		int startY = 5;

		this.snake = new TArray<Vector2f>();
		this.snake.add(new Vector2f(startX - 3, startY));
		this.snake.add(new Vector2f(startX - 2, startY));
		this.snake.add(new Vector2f(startX - 1, startY));
		this.snake.add(new Vector2f(startX, startY));
		this.snakeLength = 4;
		this.snakeHead = 3;
		this.direction.setDirection(Side.TRIGHT);
		this.gameOver.set(false);
	}

	@Override
	public void alter(LTimerContext context) {
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {

	}

	@Override
	public void touchUp(GameTouch e) {

	}

	@Override
	public void touchMove(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void close() {

	}

}
