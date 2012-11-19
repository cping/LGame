package org.loon.framework.javase.game.action;

import org.loon.framework.javase.game.core.geom.RectBox;
import org.loon.framework.javase.game.core.graphics.component.ActorLayer;

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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
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

	public boolean isComplete() {
		return isComplete;
	}

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
		original.setLocation(original.getX() + (int) this.moveX, original
				.getY()
				+ (int) this.moveY);
		if (moveJump < 0) {
			this.moveY += g;
		} else {
			this.moveY -= g;
		}
		ActorLayer layer = original.getLLayer();
		if (moveJump > 0) {
			if (original.getY() + original.getHeight() > layer.getHeight()
					+ original.getHeight()) {
				isComplete = true;
			}
		} else if (original.getY() + original.getHeight() < 0) {
			isComplete = true;
		}
		boolean isLimit = layer.isBounded();
		if (isLimit) {
			RectBox rect = original.getRectBox();
			int limitWidth = layer.getWidth() - rect.getWidth();
			int limitHeight = rect.getHeight();
			if (original.getX() > limitWidth) {
				original.setLocation(limitWidth, original.getY());
				isComplete = true;
			} else if (original.getX() < 0) {
				original.setLocation(0, original.getY());
				isComplete = true;
			}
			if (original.getY() < 0) {
				original.setLocation(original.getX(), limitHeight);
				isComplete = true;
			} else if (original.getY() > layer.getHeight() - limitHeight) {
				original.setLocation(original.getX(), layer.getHeight()
						- limitHeight);
				isComplete = true;
			}
		}
	}

}
