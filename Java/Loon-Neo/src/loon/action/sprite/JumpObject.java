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
package loon.action.sprite;

import loon.LSystem;
import loon.action.map.TileMapCollision;
import loon.geom.Vector2f;

/**
 * 一个可以做出'跳跃'动作的ActionObject实现
 */
public class JumpObject extends ActionObject {

	public static interface JumpCheckListener {

		void check(int x, int y);

	}

	public static interface JumpListener extends JumpCheckListener {

		void update(long elapsedTime);

	}

	public JumpCheckListener listener;

	public float GRAVITY;

	private float speed;

	private float jumpSpeed;

	private boolean forceJump;

	private boolean jumperTwo;

	private boolean canJumperTwo;

	public JumpObject(float x, float y, String path) {
		this(x, y, 0, 0, Animation.getDefaultAnimation(path), null);
	}

	public JumpObject(float x, float y, Animation animation) {
		this(x, y, 0, 0, animation, null);
	}

	public JumpObject(float x, float y, Animation animation, TileMapCollision map) {
		super(x, y, 0, 0, animation, map);
		this.init();
	}

	public JumpObject(float x, float y, float dw, float dh, Animation animation, TileMapCollision map) {
		super(x, y, dw, dh, animation, map);
		this.init();
	}

	public JumpObject init() {
		this.velocityX = 0f;
		this.velocityY = 0f;
		this.GRAVITY = 0.6f;
		this.speed = 6f;
		this.jumpSpeed = 12f;
		this.forceJump = false;
		this.jumperTwo = false;
		this.canJumperTwo = true;
		return this;
	}

	@Override
	public JumpObject reset() {
		this.init();
		super.reset();
		return this;
	}

	public JumpObject stop() {
		velocityX = 0;
		return this;
	}

	public JumpObject accelerateLeft() {
		velocityX = -speed;
		return this;
	}

	public JumpObject accelerateRight() {
		velocityX = speed;
		return this;
	}

	public JumpObject accelerateUp() {
		velocityY = speed;
		return this;
	}

	public JumpObject accelerateDown() {
		velocityY = -speed;
		return this;
	}

	public JumpObject jump() {
		return jump(jumpSpeed);
	}

	public JumpObject jump(float force) {
		if (isGround() || forceJump) {
			velocityY = -force;
			freeGround();
			forceJump = false;
		} else if (jumperTwo && canJumperTwo) {
			velocityY = -force;
			canJumperTwo = false;
		}
		return this;
	}

	public JumpObject setForceJump(boolean forceJump) {
		this.forceJump = forceJump;
		return this;
	}

	public float getSpeed() {
		return speed;
	}

	public JumpObject setSpeed(float speed) {
		this.speed = speed;
		return this;
	}

	public float getJumpSpeed() {
		return this.jumpSpeed;
	}

	public JumpObject setJumpSpeed(float js) {
		this.jumpSpeed = js;
		return this;
	}

	public JumpObject setJumperTwo(boolean jumperTwo) {
		this.jumperTwo = jumperTwo;
		return this;
	}

	public JumpObject setG(float g) {
		this.GRAVITY = g;
		return this;
	}

	public float getG() {
		return this.GRAVITY;
	}

	@Override
	public Vector2f collisionTileMap() {
		return collisionTileMap(0f, GRAVITY);
	}

	@Override
	public Vector2f collisionTileMap(float speedX, float speedY) {
		float x = getX();
		float y = getY();
		velocityX += speedX;
		velocityY += speedY;
		float newX = x + velocityX;
		Vector2f tile = tiles.getTileCollision(this, newX, y);
		if (tile == null) {
			x = newX;
			_groundedLeftRight = false;
		} else {
			if (velocityX > 0) {
				x = tiles.tilesToPixelsX(tile.x) - getWidth();
			} else if (velocityX < 0) {
				x = tiles.tilesToPixelsY(tile.x + 1);
			}
			velocityX = 0;
			_groundedLeftRight = true;
		}
		float newY = y + velocityY;
		tile = tiles.getTileCollision(this, x, newY);
		if (tile == null) {
			y = newY;
			_groundedTopBottom = false;
		} else {
			if (velocityY > 0) {
				y = tiles.tilesToPixelsY(tile.y) - getHeight();
				velocityY = 0;
				canJumperTwo = true;
				_groundedTopBottom = true;
			} else if (velocityY < 0) {
				y = tiles.tilesToPixelsY(tile.y + 1);
				velocityY = 0;
				isCheck(tile.x(), tile.y());
			}
		}
		return tile != null ? tile.set(x, y) : Vector2f.at(x, y);
	}

	@Override
	public void onProcess(long elapsedTime) {
		super.onProcess(elapsedTime);
		if (!isStaticObject()) {
			final Vector2f pos = collisionTileMap();
			setLocation(pos.x, pos.y);
		}
		if (listener != null && listener instanceof JumpListener) {
			((JumpListener) listener).update(elapsedTime);
		}
	}

	public JumpObject isCheck(int x, int y) {
		if (listener != null) {
			listener.check(x, y);
		}
		return this;
	}

	public JumpCheckListener getJumpListener() {
		return listener;
	}

	public JumpObject setJumpListener(JumpCheckListener listener) {
		this.listener = listener;
		return this;
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, super.hashCode());
		hashCode = LSystem.unite(hashCode, speed);
		hashCode = LSystem.unite(hashCode, jumpSpeed);
		hashCode = LSystem.unite(hashCode, GRAVITY);
		return hashCode;
	}

}
