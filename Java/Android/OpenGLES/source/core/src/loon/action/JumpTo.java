package loon.action;

import loon.core.geom.RectBox;

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
public class JumpTo extends ActionEvent {

	private float moveY;

	private float moveX;

	private int moveJump;

	private float g;

	public JumpTo(int m, float g) {
		this.moveJump = m;
		this.g = g;
	}

	@Override
	public boolean isComplete() {
		return isComplete;
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
		original.setLocation(offsetX + (original.getX() + this.moveX), offsetY
				+ (original.getY() + this.moveY));
		if (moveJump < 0) {
			this.moveY += g;
		} else {
			this.moveY -= g;
		}
	
		if (moveJump > 0) {
			if (original.getY() + original.getHeight() > original.getContainerHeight()
					+ original.getHeight()) {
				isComplete = true;
			}
		} else if (original.getY() + original.getHeight() < 0) {
			isComplete = true;
		}
		boolean isLimit = original.isBounded();
		if (isLimit) {
			RectBox rect = original.getRectBox();
			int limitWidth = (int) (original.getContainerWidth() - rect.getWidth());
			int limitHeight = (int) rect.getHeight();
			if (original.getX() > limitWidth) {
				original.setLocation(offsetX + limitWidth,
						offsetY + original.getY());
				isComplete = true;
			} else if (original.getX() < 0) {
				original.setLocation(offsetX, offsetY + original.getY());
				isComplete = true;
			}
			if (original.getY() < 0) {
				original.setLocation(offsetX + original.getX(), offsetY
						+ limitHeight);
				isComplete = true;
			} else if (original.getY() > original.getHeight() - limitHeight) {
				original.setLocation(offsetX + original.getX(), offsetY
						+ (original.getContainerHeight() - limitHeight));
				isComplete = true;
			}
		}
	}

}
