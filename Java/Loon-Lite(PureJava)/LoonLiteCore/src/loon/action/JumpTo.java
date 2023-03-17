/**
 * Copyright 2008 - 2011
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
package loon.action;

import loon.geom.RectBox;
import loon.utils.StringKeyValue;

public class JumpTo extends ActionEvent {

	private float moveX;
	private float moveY;
	private float gravity;

	private int moveJump;

	public JumpTo(int m, float g) {
		this(0, 0, m, g);
	}

	public JumpTo(int x, int y, int m, float g) {
		this.moveX = x;
		this.moveY = y;
		this.moveJump = m;
		this.gravity = g;
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public void onLoad() {
		this.moveY = moveJump;
	}

	public float getMoveX() {
		return moveX;
	}

	public void setMoveX(float moveX) {
		this.moveX = moveX;
	}

	public float getMoveY() {
		return moveY;
	}

	public void setMoveY(float moveY) {
		this.moveY = moveY;
	}

	@Override
	public void update(long elapsedTime) {
		if (moveJump < 0) {
			if (this.moveY > -(moveJump)) {
				this.moveY = -moveJump;
			}
		} else {
			if (this.moveY > (moveJump)) {
				this.moveY = moveJump;
			}
		}
		movePos(offsetX + (original.getX() + this.moveX), offsetY + (original.getY() + this.moveY));
		if (moveJump < 0) {
			this.moveY += gravity;
		} else {
			this.moveY -= gravity;
		}
		if (moveJump > 0) {
			if (original.getY() + original.getHeight() > original.getContainerHeight() + original.getHeight()) {
				_isCompleted = true;
			}
		} else if (original.getY() + original.getHeight() < 0) {
			_isCompleted = true;
		}
		boolean isLimit = original.isBounded();
		if (isLimit) {
			RectBox rect = original.getRectBox();
			int limitWidth = (int) (original.getContainerWidth() - rect.getWidth());
			int limitHeight = (int) rect.getHeight();
			if (original.getX() > limitWidth) {
				movePos(offsetX + limitWidth, offsetY + original.getY());
				_isCompleted = true;
			} else if (original.getX() < 0) {
				movePos(offsetX, offsetY + original.getY());
				_isCompleted = true;
			}
			if (original.getY() < 0) {
				movePos(offsetX + original.getX(), offsetY + limitHeight);
				_isCompleted = true;
			} else if (original.getY() > original.getHeight() - limitHeight) {
				movePos(offsetX + original.getX(), offsetY + (original.getContainerHeight() - limitHeight));
				_isCompleted = true;
			}
		}
	}

	public float getGravity() {
		return gravity;
	}

	public int getMoveJump() {
		return moveJump;
	}

	@Override
	public ActionEvent cpy() {
		JumpTo jump = new JumpTo(moveJump, gravity);
		jump.set(this);
		return jump;
	}

	@Override
	public ActionEvent reverse() {
		JumpTo jump = new JumpTo(-moveJump, gravity);
		jump.set(this);
		return jump;
	}

	@Override
	public String getName() {
		return "jump";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("moveX", moveX).comma().kv("moveY", moveY).comma().kv("moveJump", moveJump).comma().kv("gravity",
				gravity);
		return builder.toString();
	}
}
