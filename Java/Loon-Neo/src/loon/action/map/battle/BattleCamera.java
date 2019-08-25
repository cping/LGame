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
package loon.action.map.battle;

import loon.LSystem;
import loon.action.ActionBind;
import loon.utils.MathUtils;

public class BattleCamera {

	private float curX = 0f;
	private float curY = 0f;

	private float scaleX = 1f;
	private float scaleY = 1f;

	private float width = -1f;
	private float height = -1f;

	private ActionBind anchorTarget;
	private float anchorX = 0f;
	private float anchorY = 0f;

	private float shakeTime = 0f;
	private float shakeMagnitude = 0f;
	private float shakeX = 0f;
	private float shakeY = 0f;

	public BattleCamera(float w, float h) {
		this.width = w;
		this.height = h;
	}

	public BattleCamera() {
		this(LSystem.viewSize.width, LSystem.viewSize.height);
	}

	public BattleCamera setTo(float x, float y, float px, float py) {
		this.curX = x - ((width / scaleX) * px);
		this.curY = y - ((height / scaleY) * py);
		return this;
	}

	public BattleCamera adjustToPlayer(int mapWidth, int mapHeight) {
		return adjustToPlayer(anchorTarget, mapWidth, mapHeight);
	}

	public BattleCamera adjustToPlayer(ActionBind player, int mapWidth, int mapHeight) {

		float newCameraX = (player.getX() + player.getWidth() / 2) - width / 2;
		float newCameraY = (player.getY() + player.getHeight() / 2) - height / 2;

		if (newCameraX < 0) {
			newCameraX = 0;
		} else if (newCameraX + width > mapWidth) {
			newCameraX = mapWidth - width;
		}

		if (newCameraY < 0) {
			newCameraY = 0;
		} else if (newCameraY + height > mapHeight) {
			newCameraY = mapHeight - height;
		}

		this.curX = newCameraX;
		this.curY = newCameraY;

		return this;
	}

	public BattleCamera followPlayer(int mapWidth, int mapHeight, float cameraOffset, float cameraSpeed) {
		return followPlayer(anchorTarget, mapWidth, mapHeight, cameraOffset, cameraSpeed);
	}

	public BattleCamera followPlayer(ActionBind player, int mapWidth, int mapHeight, float cameraOffset,
			float cameraSpeed) {
		
		if (player.getX() - curX < cameraOffset) {
			if (curX > 0) {
				curX -= cameraSpeed;
			}
		} else if ((curX + width) - (player.getX() + player.getWidth()) < cameraOffset) {
			if (curX + width < mapWidth) {
				curX += cameraSpeed;
			}
		}
		
		if (player.getY() - curY < cameraOffset) {
			if (curY > 0) {
				curY -= cameraSpeed;
			}
		} else if ((curY + height) - (player.getY() + player.getHeight()) < cameraOffset) {
			if (curY + height < mapHeight) {
				curY += cameraSpeed;
			}
		}
		
		return this;
		
	}

	public float getWidth() {
		return width / scaleX;
	}

	public float getHeight() {
		return height / scaleY;
	}

	public float getCurX() {
		return curX;
	}

	public BattleCamera setCurX(float curX) {
		this.curX = curX;
		return this;
	}

	public float getCurY() {
		return curY;
	}

	public BattleCamera setCurY(float curY) {
		this.curY = curY;
		return this;
	}

	public BattleCamera anchor(ActionBind target, float anchorX, float anchorY) {
		this.anchorTarget = target;
		this.anchorX = anchorX;
		this.anchorY = anchorY;
		return this;
	}

	public boolean onCamera(ActionBind target) {
		return target.inContains(curX, curY, width, height);
	}

	public ActionBind getAnchorTarget() {
		return anchorTarget;
	}

	public BattleCamera setAnchorTarget(ActionBind anchorTarget) {
		this.anchorTarget = anchorTarget;
		return this;
	}

	public float getAnchorX() {
		return anchorX;
	}

	public BattleCamera setAnchorX(float anchorX) {
		this.anchorX = anchorX;
		return this;
	}

	public float getAnchorY() {
		return anchorY;
	}

	public BattleCamera setAnchorY(float anchorY) {
		this.anchorY = anchorY;
		return this;
	}

	public BattleCamera shakeStop() {
		this.shakeTime = 0f;
		return this;
	}

	public BattleCamera shake() {
		return shake(0.5f, 4f);
	}

	public BattleCamera shake(float duration, float magnitude) {
		if (shakeTime < duration) {
			shakeTime = duration;
		}
		shakeMagnitude = magnitude;
		return this;
	}

	public void update(long elapsed) {

		if (anchorTarget != null) {

			final float tx = anchorTarget.getX() + anchorTarget.getWidth() / 2;
			final float ty = anchorTarget.getY() + anchorTarget.getHeight() / 2;

			curX = tx - (width / scaleX * anchorX);
			curY = ty - (height / scaleY * anchorY);
		}

		if (shakeTime > 0f) {

			final float sx = MathUtils.random(shakeMagnitude * 2f + 1f) - shakeMagnitude;
			final float sy = MathUtils.random(shakeMagnitude * 2f + 1f) - shakeMagnitude;

			curX += sx - shakeX;
			curY += sy - shakeY;

			shakeX = sx;
			shakeY = sy;

			shakeTime -= MathUtils.min(elapsed / 1000f, LSystem.MIN_SECONE_SPEED_FIXED);
			if (shakeTime < 0) {
				shakeTime = 0;
			}
		} else if (shakeX != 0 || shakeY != 0) {
			this.curX -= shakeX;
			this.curY -= shakeY;
			this.shakeX = shakeY = 0;
		}
	}

}
