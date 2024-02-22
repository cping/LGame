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
package loon.action.avg.drama;

import loon.geom.Vector2f;

/**
 * loon提供的脚本执行器接口,用于绑定特定对象到脚本中去,从而按设定执行指定游戏操作
 */
public interface Scriptable {

	Vector2f getLocation();

	float getSpeed();

	boolean isFinishedAction();

	boolean isLeft();

	boolean isRight();

	boolean isUp();

	boolean isDown();

	boolean isMoveLeft();

	boolean isMoveRight();

	boolean isMoveUp();

	boolean isMoveDown();

	void moveRight(float x, float y);

	void moveLeft(float x, float y);

	void moveLeft(float x, float y, boolean limit);

	void moveUp(float x, float y);

	void moveUp(float x, float y, boolean limit);

	void moveDown(float x, float y);

	void moveDown(float x, float y, boolean limit);

	void setFaceRight();

	void setFaceLeft();

	void setFaceUp();

	void setFaceDown();

	void moveToX(Scriptable s, float x, float y);

	void moveToY(Scriptable s, float x, float y);

	void moveToBackground();

	void moveToForeground();
	
	void zapTo(Scriptable s);

	void pauseFor(float s);

	void animationFrame(String aniName, int idx);

	void playMessage(String role, String message);

	void choices(String[] selectes);

	int getCurrentChoice();

	void setCurrentChoice(int idx);

	void isFinishedTalking();

	void hide();

	void show();

	void disableCollisions();

	void enableCollisions();

	void detachFrom(Scriptable s);

	void attachTo(Scriptable s);

	void follow(Scriptable s);

	void unfollow(Scriptable s);

	void reset();

	void stop();

	void rotation(float r);

	void setRotationSpeed(float s);

	void animation(String n);

	void setScale(float s);

	void setSpeed(float s);

	boolean faceRight();

	boolean faceLeft();

	boolean faceUp();

	boolean faceDown();

	boolean isMoving();

	void fly(boolean limit);

	boolean isRunning();
}
