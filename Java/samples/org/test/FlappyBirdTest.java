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

import loon.LTexture;
import loon.Stage;
import loon.State;
import loon.action.sprite.Animation;
import loon.events.SysTouch;
import loon.events.Touched;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class FlappyBirdTest extends Stage {

	// 游戏结束
	private boolean gameover;
	// 测试模式
	private boolean testing;

	public class PlayState extends State {

		private static final int GROUND_Y_OFFSET = -30;
		private static final int TUBE_SPACING = 115;
		private static final int TUBE_COUNT = 50;

		private Bird bird;
		private LTexture ground;
		private LTexture gameoverImg;

		private TArray<Tube> tubes;

		@Override
		public void close() {
			if (ground != null) {
				ground.close();
			}
			if (gameoverImg != null) {
				gameoverImg.close();
			}
		}

		@Override
		public void load() {

			// 开启测试默认(不会over……)
			// testing = true;

			bird = new Bird(40, 220);

			ground = loadTexture("ground.png");
			gameoverImg = loadTexture("gameover.png");

			tubes = new TArray<Tube>();
			for (int i = 1; i <= TUBE_COUNT; i++) {
				tubes.add(new Tube(i * (TUBE_SPACING + Tube.TUBE_WIDTH)));
			}
			gameover = false;

		}

		@Override
		public void update(float delta) {
			if (SysTouch.isUp()) {
				bird.jump();
			}
			bird.update(delta);
			camera(bird.getX() - 50, getHalfHeight());
			for (Tube tube : tubes) {
				if (getCameraX() - getWidth() > tube.getPosTopTube().x + tube.getTopTube().getWidth()) {
					tube.reposition(tube.getPosTopTube().x + ((Tube.TUBE_WIDTH + TUBE_SPACING) * TUBE_COUNT));
				}
				if (!testing && tube.collides(bird.getBounds())) {
					bird.colliding = true;
					gameover = true;
				}
			}
			if (!testing && (bird.getY() <= ground.getHeight() + GROUND_Y_OFFSET)) {
				gameover = true;
				bird.colliding = true;
			}

		}

		@Override
		public void paint(GLEx g) {
			for (Tube tube : tubes) {
				g.draw(tube.getBottomTube(), tube.getPosBottomTube().x, tube.getPosBottomTube().y);
				g.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);
			}
			g.draw(bird.birdAnimation.getSpriteImage(), bird.getX(), bird.getY());
			if (gameover) {
				g.draw(gameoverImg, bird.getX() - 50 + (getWidth() - gameoverImg.getWidth()) / 2,
						(getCameraY() + getHeight()) / 2);
			}
		}
	}

	public class MenuState extends State {

		LTexture playBtn;

		@Override
		public void load() {
			playBtn = loadTexture("playbtn.png");
		}

		@Override
		public void update(float dt) {

		}

		@Override
		public void paint(GLEx g) {
			g.draw(playBtn, (getWidth() - playBtn.getWidth()) / 2, getHeight() / 2);
		}

		@Override
		public void close() {
			if (playBtn != null) {
				playBtn.close();
			}
		}

	}

	static class Tube {
		public static final int TUBE_WIDTH = 52;
		private static final int TUBE_GAP = 140;
		private static final int LOWEST_OPENING = 120;
		private static final int FLUCTUATION = 130;

		private LTexture topTube, bottomTube;
		private Vector2f posTopTube, posBottomTube;
		private RectBox boundsTop, boundsBottom;

		public Tube(float x) {
			topTube = loadTexture("toptube.png");
			bottomTube = loadTexture("bottomtube.png");
			posTopTube = new Vector2f(x, MathUtils.nextInt(FLUCTUATION) + LOWEST_OPENING + TUBE_GAP);
			posBottomTube = new Vector2f(x, posTopTube.y - TUBE_GAP - bottomTube.getHeight());
			boundsTop = new RectBox(posTopTube.x, posTopTube.y, topTube.getWidth(), topTube.getHeight());
			boundsBottom = new RectBox(posBottomTube.x, posBottomTube.y, bottomTube.getWidth(), bottomTube.getHeight());
		}

		public LTexture getTopTube() {
			return topTube;
		}

		public void setTopTube(LTexture topTube) {
			this.topTube = topTube;
		}

		public LTexture getBottomTube() {
			return bottomTube;
		}

		public void setBottomTube(LTexture bottomTube) {
			this.bottomTube = bottomTube;
		}

		public Vector2f getPosTopTube() {
			return posTopTube;
		}

		public void setPosTopTube(Vector2f posTopTube) {
			this.posTopTube = posTopTube;
		}

		public Vector2f getPosBottomTube() {
			return posBottomTube;
		}

		public void setPosBottomTube(Vector2f posBottomTube) {
			this.posBottomTube = posBottomTube;
		}

		public void reposition(float x) {
			posTopTube.set(x, MathUtils.nextInt(FLUCTUATION) + LOWEST_OPENING + TUBE_GAP);
			posBottomTube.set(x, posTopTube.y - TUBE_GAP - bottomTube.getHeight());
			boundsTop.setLocation(x, posTopTube.y);
			boundsBottom.setLocation(x, posBottomTube.y);
		}

		public boolean collides(RectBox player) {
			return player.overlaps(boundsBottom) || player.overlaps(boundsTop);
		}

		public RectBox getBoundsBottom() {
			return boundsBottom;
		}

		public RectBox getBoundsTop() {
			return boundsTop;
		}
	}

	static class Bird {
		private static final int GRAVITY = -15;
		private static final int MOVEMENT = 100;

		private Vector2f position;
		private Vector2f velocity;
		private RectBox bounds;
		private Animation birdAnimation;

		public boolean colliding;

		public Bird(int x, int y) {
			// 设定一个飞鸟动画,大小34x24,播放速度120
			birdAnimation = Animation.getDefaultAnimation("birdanimation.png", 34, 24, 120);
			position = new Vector2f(x, y);
			velocity = new Vector2f();
			bounds = new RectBox(x, y, birdAnimation.getWidth(), birdAnimation.getHeight());
			colliding = false;
		}

		public void update(float dt) {
			birdAnimation.update(dt);
			velocity.addSelf(0, GRAVITY);
			velocity.scaleSelf(dt);
			if (!colliding) {
				position.addSelf(MOVEMENT * dt, velocity.y);
			}
			if (position.y < 120) {
				position.y = 120;
			}
			velocity.scaleSelf(1f / dt);
			updateBounds();
		}

		public void jump() {
			velocity.y = 200;
		}

		public void updateBounds() {
			bounds.setLocation(position.x, position.y);
		}

		public float getX() {
			return position.x;
		}

		public float getY() {
			return position.y;
		}

		public float getWidth() {
			return birdAnimation.getWidth();
		}

		public float getHeight() {
			return birdAnimation.getHeight();
		}

		public RectBox getBounds() {
			return bounds;
		}
	}

	@Override
	public void create() {
		// 最后渲染桌面组件
		lastDesktopDraw();
		// 游戏背景
		setBackground("bg.png");
		// 注入开始用State
		addState("start", new MenuState());
		// 注入游戏画面播放用State
		addState("play", new PlayState());
		// 监听触屏按下事件
		up(new Touched() {

			@Override
			public void on(float x, float y) {
				// 如果当前State是start
				if (peekStateEquals("start")) {
					// 改为播放play state
					playState("play");
				} else if (peekStateEquals("play") && gameover) {
					// 如果gamover,改为播放start state
					playState("start");
				}
			}
		});

		add(MultiScreenTest.getBackButton(this, 1));
	}

}
