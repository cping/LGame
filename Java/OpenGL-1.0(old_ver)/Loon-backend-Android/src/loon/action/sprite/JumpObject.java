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

import loon.action.map.TileMap;
import loon.core.geom.Vector2f;


public class JumpObject extends SpriteBatchObject {

	public static interface JumpListener {

		public void update(long elapsedTime);

		public void check(int x,int y);

	}

	public JumpListener listener;

	public float GRAVITY = 0.6f;

	protected float vx;

	protected float vy;

	private float speed;

	private float jumpSpeed;

	private boolean onGround;

	private boolean forceJump;

	private boolean jumperTwo;

	private boolean canJumperTwo;

	public JumpObject(float x, float y, Animation animation, TileMap map) {
		super(x, y, 0, 0, animation, map);
		vx = 0;
		vy = 0;
		speed = 6f;
		jumpSpeed = 12f;
		onGround = false;
		forceJump = false;
		jumperTwo = false;
		canJumperTwo = true;
	}

	public JumpObject(float x, float y, float dw, float dh,
			Animation animation, TileMap map) {
		super(x, y, dw, dh, animation, map);
		vx = 0;
		vy = 0;
		speed = 6f;
		jumpSpeed = 12f;
		onGround = false;
		forceJump = false;
		jumperTwo = false;
		canJumperTwo = true;
	}

	public void stop() {
		vx = 0;
	}

	public void accelerateLeft() {
		vx = -speed;
	}

	public void accelerateRight() {
		vx = speed;
	}

	public void accelerateUp() {
		vy = speed;
	}

	public void accelerateDown() {
		vy = -speed;
	}

	public void jump() {
		if (onGround || forceJump) {
			vy = -jumpSpeed;
			onGround = false;
			forceJump = false;
		} else if (jumperTwo && canJumperTwo) {
			vy = -jumpSpeed;
			canJumperTwo = false;
		}
	}

	public void setForceJump(boolean forceJump) {
		this.forceJump = forceJump;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void setJumperTwo(boolean jumperTwo) {
		this.jumperTwo = jumperTwo;
	}

	@Override
	public void update(long elapsedTime) {

		if (animation != null) {
			animation.update(elapsedTime);
		}
		final TileMap map = tiles;
		float x = getX();
		float y = getY();

		vy += GRAVITY;

		float newX = x + vx;

		Vector2f tile = map.getTileCollision(this, newX, y);
		if (tile == null) {
			x = newX;
		} else {
			if (vx > 0) {
				x = map.tilesToPixelsX(tile.x) - getWidth();
			} else if (vx < 0) {
				x = map.tilesToPixelsY(tile.x + 1);
			}
			vx = 0;
		}

		float newY = y + vy;
		tile = map.getTileCollision(this, x, newY);
		if (tile == null) {
			y = newY;
			onGround = false;
		} else {
			if (vy > 0) {
				y = map.tilesToPixelsY(tile.y) - getHeight();
				vy = 0;
				onGround = true;
				canJumperTwo = true;
			} else if (vy < 0) {
				y = map.tilesToPixelsY(tile.y + 1);
				vy = 0;
				isCheck(tile.x(), tile.y());
			}
		}

		setLocation(x, y);
		if (listener != null) {
			listener.update(elapsedTime);
		}
	}

	public void isCheck(int x, int y) {
		if (listener != null) {
			listener.check(x, y);
		}
	}

	public JumpListener getJumpListener() {
		return listener;
	}

	public void setJumpListener(JumpListener listener) {
		this.listener = listener;
	}


}
