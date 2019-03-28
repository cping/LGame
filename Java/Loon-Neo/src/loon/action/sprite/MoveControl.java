/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.action.sprite;

import loon.LRelease;
import loon.action.ActionBind;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.action.map.TileMap;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.timer.LTimerContext;

/**
 * 一个四方向(八方向)运动的控制器,主要用来键盘或虚拟摇杆控制角色移动
 */
public class MoveControl implements LRelease {

	private int _speed = 8;

	private int _px = 0, _py = 0, _direction = -1, _lastDirection = -1;

	private int _moveX = 0, _moveY = 0, _movingLength = 0;

	private boolean _isMoving = false, _running = false, _freeDir = true, _closed = false;

	private ActionBind _bind;

	private Field2D _map;

	public MoveControl(ActionBind bind, TileMap map) {
		this(bind, map.getField());
	}

	public MoveControl(ActionBind bind, int[][] map) {
		this(bind, new Field2D(map));
	}

	public MoveControl(ActionBind bind, Field2D field2d) {
		this._bind = bind;
		this._map = field2d;
	}

	public void setDirection(int d) {
		this._direction = d;
	}

	public void resetDirection() {
		setDirection(-1);
	}

	public int getDirection() {
		return this._direction;
	}

	public final void call() {
		move(_bind, _map, _direction);
	}

	public MoveControl start() {
		if (!_running) {
			RealtimeProcess process = new RealtimeProcess() {

				@Override
				public void run(LTimerContext time) {
					if (_running) {
						call();
						if (_freeDir) {
							resetDirection();
						}
					} else {
						kill();
					}
				}
			};
			process.setDelay(30);
			_running = true;
			RealtimeProcessManager.get().addProcess(process);
		}
		return this;
	}

	public MoveControl stop() {
		this._running = false;
		return this;
	}

	public final boolean move(ActionBind bind, Field2D field2d, int direction) {
		boolean notMove = false;
		this._movingLength = 0;
		float posX = bind.getX();
		float posY = bind.getY();
		posX = posX / field2d.getTileWidth();
		posY = posY / field2d.getTileHeight();
		if (posX - (int) posX > 0.4) {
			posX = field2d.pixelsToTilesWidth(bind.getX()) + 1;
		} else {
			posX = field2d.pixelsToTilesWidth(bind.getX());
		}
		if (posY - (int) posY > 0.4) {
			posY = field2d.pixelsToTilesHeight(bind.getY()) + 1;
		} else {
			posY = field2d.pixelsToTilesHeight(bind.getY());
		}
		this._px = bind.x();
		this._py = bind.y();
		this._moveX = (int) posX;
		this._moveY = (int) posY;
		switch (direction) {
		case Config.TLEFT:
			if (moveTLeft(field2d)) {
				notMove = true;
			}
			break;
		case Config.LEFT:
			if (moveLeft(field2d)) {
				notMove = true;
			}
			break;
		case Config.TRIGHT:
			if (moveTRight(field2d)) {
				notMove = true;
			}
			break;
		case Config.RIGHT:
			if (moveRight(field2d)) {
				notMove = true;
			}
			break;
		case Config.TUP:
			if (moveTUp(field2d)) {
				notMove = true;
			}
			break;
		case Config.UP:
			if (moveUp(field2d)) {
				notMove = true;
			}
			break;
		case Config.TDOWN:
			if (moveTDown(field2d)) {
				notMove = true;
			}
			break;
		case Config.DOWN:
			if (moveDown(field2d)) {
				notMove = true;
			}
			break;
		}
		if (!notMove) {
			bind.setX(_px);
			bind.setY(_py);
			_lastDirection = _direction;
		}
		return notMove;
	}

	private boolean moveLeft(Field2D field2d) {
		int nextX = _moveX - 1;
		int nextY = _moveY - 1;
		if (nextX < 0) {
			nextX = 0;
		}
		if (nextY < 0) {
			nextY = 0;
		}
		if (field2d.isHit(nextX, nextY)) {
			_px -= _speed;
			if (_px < 0) {
				_px = 0;
			}
			_py -= _speed;
			if (_py < 0) {
				_py = 0;
			}
			_movingLength += _speed;
			if (_movingLength >= field2d.getTileWidth()) {
				_moveX--;
				_px = _moveX * field2d.getTileWidth();
				_isMoving = false;
				return true;
			}
			if (_movingLength >= field2d.getTileHeight()) {
				_moveY--;
				_py = _moveY * field2d.getTileHeight();
				_isMoving = false;
				return true;
			}
		} else {
			_isMoving = false;
			_px = _moveX * field2d.getTileWidth();
			_py = _moveY * field2d.getTileHeight();
		}
		return false;
	}

	private boolean moveTLeft(Field2D field2d) {
		int nextX = _moveX - 1;
		int nextY = _moveY;
		if (nextX < 0) {
			nextX = 0;
		}
		if (field2d.isHit(nextX, nextY)) {
			_px -= _speed;
			if (_px < 0) {
				_px = 0;
			}
			_movingLength += _speed;
			if (_movingLength >= field2d.getTileWidth()) {
				_moveX--;
				_px = _moveX * field2d.getTileWidth();
				_isMoving = false;
				return true;
			}
		} else {
			_isMoving = false;
			_px = _moveX * field2d.getTileWidth();
			_py = _moveY * field2d.getTileHeight();
		}
		return false;
	}

	private boolean moveRight(Field2D field2d) {
		int nextX = _moveX + 1;
		int nextY = _moveY + 1;
		if (nextX > field2d.getWidth() - 1) {
			nextX = field2d.getWidth() - 1;
		}
		if (nextY > field2d.getHeight() - 1) {
			nextY = field2d.getHeight() - 1;
		}
		if (field2d.isHit(nextX, nextY)) {
			_px += _speed;
			if (_px > field2d.getViewWidth() - field2d.getTileWidth()) {
				_px = field2d.getViewWidth() - field2d.getTileWidth();
			}
			_py += _speed;
			if (_py > field2d.getViewHeight() - field2d.getTileHeight()) {
				_py = field2d.getViewHeight() - field2d.getTileHeight();
			}
			_movingLength += _speed;
			if (_movingLength >= field2d.getTileWidth()) {
				_moveX++;
				_px = _moveX * field2d.getTileWidth();
				_isMoving = false;
				return true;
			}
			if (_movingLength >= field2d.getTileHeight()) {
				_moveY++;
				_py = _moveY * field2d.getTileHeight();
				_isMoving = false;
				return true;
			}
		} else {
			_isMoving = false;
			_px = _moveX * field2d.getTileWidth();
			_py = _moveY * field2d.getTileHeight();
		}

		return false;
	}

	private boolean moveTRight(Field2D field2d) {
		int nextX = _moveX + 1;
		int nextY = _moveY;
		if (nextX > field2d.getWidth() - 1) {
			nextX = field2d.getWidth() - 1;
		}
		if (field2d.isHit(nextX, nextY)) {
			_px += _speed;
			if (_px > field2d.getViewWidth() - field2d.getTileWidth()) {
				_px = field2d.getViewWidth() - field2d.getTileWidth();
			}
			_movingLength += _speed;
			if (_movingLength >= field2d.getTileWidth()) {
				_moveX++;
				_px = _moveX * field2d.getTileWidth();
				_isMoving = false;
				return true;
			}
		} else {
			_isMoving = false;
			_px = _moveX * field2d.getTileWidth();
			_py = _moveY * field2d.getTileHeight();
		}

		return false;
	}

	private boolean moveUp(Field2D field2d) {
		int nextX = _moveX + 1;
		int nextY = _moveY - 1;
		if (nextX > field2d.getWidth() - 1) {
			nextX = field2d.getWidth() - 1;
		}
		if (nextY < 0) {
			nextY = 0;
		}
		if (field2d.isHit(nextX, nextY)) {
			_px += _speed;
			if (_px > field2d.getViewWidth() - field2d.getTileWidth()) {
				_px = field2d.getViewWidth() - field2d.getTileWidth();
			}
			_movingLength += _speed;
			if (_movingLength >= field2d.getTileWidth()) {
				_moveX++;
				_px = _moveX * field2d.getTileWidth();
				_isMoving = false;
				return true;
			}
			_py -= _speed;
			if (_py < 0) {
				_py = 0;
			}
			if (_movingLength >= field2d.getTileHeight()) {
				_moveY--;
				_py = _moveY * field2d.getTileHeight();
				_isMoving = false;
				return true;
			}
		} else {
			_isMoving = false;
			_px = _moveX * field2d.getTileWidth();
			_py = _moveY * field2d.getTileHeight();
		}

		return false;
	}

	private boolean moveTUp(Field2D field2d) {
		int nextX = _moveX;
		int nextY = _moveY - 1;
		if (nextY < 0) {
			nextY = 0;
		}
		if (field2d.isHit(nextX, nextY)) {
			_py -= _speed;
			if (_py < 0) {
				_py = 0;
			}
			_movingLength += _speed;
			if (_movingLength >= field2d.getTileHeight()) {
				_moveY--;
				_py = _moveY * field2d.getTileHeight();
				_isMoving = false;
				return true;
			}
		} else {
			_isMoving = false;
			_px = _moveX * field2d.getTileWidth();
			_py = _moveY * field2d.getTileHeight();
		}

		return false;
	}

	private boolean moveDown(Field2D field2d) {
		int nextX = _moveX - 1;
		int nextY = _moveY + 1;
		if (nextX < 0) {
			nextX = 0;
		}
		if (nextY > field2d.getHeight() - 1) {
			nextY = field2d.getHeight() - 1;
		}
		if (field2d.isHit(nextX, nextY)) {
			_px -= _speed;
			if (_px < 0) {
				_px = 0;
			}
			_movingLength += _speed;
			if (_movingLength >= field2d.getTileWidth()) {
				_moveX--;
				_px = _moveX * field2d.getTileWidth();
				_isMoving = false;
				return true;
			}
			_py += _speed;
			if (_py > field2d.getViewHeight() - field2d.getTileHeight()) {
				_py = field2d.getViewHeight() - field2d.getTileHeight();
			}
			if (_movingLength >= field2d.getTileHeight()) {
				_moveY++;
				_py = _moveY * field2d.getTileHeight();
				_isMoving = false;
				return true;
			}
		} else {
			_isMoving = false;
			_px = _moveX * field2d.getTileWidth();
			_py = _moveY * field2d.getTileHeight();
		}
		return false;
	}

	private boolean moveTDown(Field2D field2d) {
		int nextX = _moveX;
		int nextY = _moveY + 1;
		if (nextY > field2d.getHeight() - 1) {
			nextY = field2d.getHeight() - 1;
		}
		if (field2d.isHit(nextX, nextY)) {
			_py += _speed;
			if (_py > field2d.getViewHeight() - field2d.getTileHeight()) {
				_py = field2d.getViewHeight() - field2d.getTileHeight();
			}
			_movingLength += _speed;
			if (_movingLength >= field2d.getTileHeight()) {
				_moveY++;
				_py = _moveY * field2d.getTileHeight();
				_isMoving = false;
				return true;
			}
		} else {
			_isMoving = false;
			_px = _moveX * field2d.getTileWidth();
			_py = _moveY * field2d.getTileHeight();
		}
		return false;
	}

	public boolean isMoving() {
		return _isMoving;
	}

	public int getSpeed() {
		return _speed;
	}

	public MoveControl setSpeed(int s) {
		this._speed = s;
		return this;
	}

	public boolean isRunning() {
		return _running;
	}

	public boolean isFreeDir() {
		return _freeDir;
	}

	public void setFreeDir(boolean d) {
		this._freeDir = d;
	}

	public boolean isLeft() {
		return _lastDirection == Config.LEFT;
	}

	public boolean isRight() {
		return _lastDirection == Config.RIGHT;
	}

	public boolean isDown() {
		return _lastDirection == Config.DOWN;
	}

	public boolean isUp() {
		return _lastDirection == Config.UP;
	}

	public boolean isTLeft() {
		return _lastDirection == Config.TLEFT;
	}

	public boolean isTRight() {
		return _lastDirection == Config.TRIGHT;
	}

	public boolean isTDown() {
		return _lastDirection == Config.TDOWN;
	}

	public boolean isTUp() {
		return _lastDirection == Config.TUP;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		_running = false;
		_closed = true;
	}

}
