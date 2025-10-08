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
import loon.action.collision.CollisionFilter;
import loon.action.collision.CollisionResult;
import loon.action.collision.CollisionWorld;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.action.map.HexagonMap;
import loon.action.map.TileCollisionListener;
import loon.action.map.TileMap;
import loon.component.LComponent;
import loon.events.EventActionN;
import loon.events.SysInput;
import loon.events.SysKey;
import loon.geom.ActionBindRect;
import loon.geom.Vector2f;
import loon.utils.processes.GameProcessType;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.timer.LTimerContext;

/**
 * 一个四方向(八方向)运动的控制器,主要用来键盘或虚拟摇杆控制角色移动
 */
public class MoveControl implements LRelease {

	public class MoveControlProcess extends RealtimeProcess {

		private final MoveControl _moveControl;

		public MoveControlProcess(MoveControl m) {
			this._moveControl = m;
		}

		@Override
		public void run(LTimerContext time) {
			if (_moveControl._running) {
				_moveControl.call();
				if (_moveControl._freeDir) {
					_moveControl.resetDirection();
				}
			} else {
				kill();
			}
		}

	}

	private TileCollisionListener _tileCollisionListener;

	private final Vector2f _tempPos = new Vector2f();

	private float _moveSpeed;

	private float _offsetX;

	private float _offsetY;

	private int _direction, _lastDirection;

	private float _lastX;

	private float _lastY;

	private float _vagueWidthScale, _vagueHeightScale;

	private boolean _isMoving, _running, _freeDir, _closed;

	private boolean _moveBlocked;

	private boolean _iso45DMovement;

	private CollisionWorld _collisionWorld;

	private CollisionFilter _worldCollisionFilter;

	private ActionBind _bindObject;

	private ActionBindRect _actionRect;

	private Field2D _currentArrayMap;

	private EventActionN _upEvent;

	private EventActionN _downEvent;

	private EventActionN _leftEvent;

	private EventActionN _rightEvent;

	private EventActionN _actionAnyEvent;

	private EventActionN _action1Event;

	private EventActionN _action2Event;

	private EventActionN _action3Event;

	private EventActionN _jumpEvent;

	private EventActionN _submitEvent;

	private EventActionN _cancelEvent;

	private EventActionN _backEvent;

	private long _delay;

	private boolean _up;

	private boolean _down;

	private boolean _right;

	private boolean _left;

	private boolean _justUp;

	private boolean _justDown;

	private boolean _justRight;

	private boolean _justLeft;

	private boolean _isPressedKeyA;

	private boolean _isReleasedKeyA;

	private boolean _isPressedKeyS;

	private boolean _isReleasedKeyS;

	private boolean _isPressedKeyD;

	private boolean _isReleasedKeyD;

	private boolean _isPressedKeyW;

	private boolean _isReleasedKeyW;

	private boolean _isLeftOrRight;

	private boolean _isDownOrUp;

	private boolean _isAction1, _isAction2, _isAction3;

	private boolean _isJump, _isSubmit, _isCancel, _isBack;

	private SysInput _currentInput;

	public MoveControl(ActionBind bind, TileMap map) {
		this(bind, map.getField2D());
	}

	public MoveControl(ActionBind bind, HexagonMap map) {
		this(bind, map.getField2D());
	}

	public MoveControl(ActionBind bind, int[][] map) {
		this(bind, new Field2D(map));
	}

	public MoveControl(ActionBind bind) {
		this(bind, (Field2D) null);
	}

	public MoveControl(ActionBind bind, Field2D field2d) {
		this(bind, field2d, 8, 30, 1f, 1f);
	}

	public MoveControl(ActionBind bind, Field2D field2d, int moveSpeed, int delay, float ws, float hs) {
		if (bind != null) {
			if (bind instanceof LComponent) {
				_currentInput = ((LComponent) bind).getScreen();
			} else if (bind instanceof ISprite) {
				_currentInput = ((ISprite) bind).getScreen();
			}
			this._bindObject = bind;
			this._actionRect = new ActionBindRect(bind);
		}
		this._currentArrayMap = field2d;
		this._delay = delay;
		this._moveSpeed = moveSpeed;
		this._vagueWidthScale = ws;
		this._vagueHeightScale = hs;
		this._direction = _lastDirection = -1;
		this._freeDir = true;
	}

	public void setCurrentInput(SysInput input) {
		this._currentInput = input;
	}

	public SysInput getCurrentInput() {
		return this._currentInput;
	}

	public MoveControl setActionAnyEvent(EventActionN e) {
		this._actionAnyEvent = e;
		return this;
	}

	public EventActionN getActionAnyEvent() {
		return this._actionAnyEvent;
	}

	public MoveControl setAction1Event(EventActionN e) {
		this._action1Event = e;
		return this;
	}

	public EventActionN getAction1Event() {
		return this._action1Event;
	}

	public MoveControl setAction2Event(EventActionN e) {
		this._action2Event = e;
		return this;
	}

	public EventActionN getAction2Event() {
		return this._action2Event;
	}

	public MoveControl setAction3Event(EventActionN e) {
		this._action3Event = e;
		return this;
	}

	public EventActionN getAction3Event() {
		return this._action3Event;
	}

	public MoveControl setJumpEvent(EventActionN e) {
		this._jumpEvent = e;
		return this;
	}

	public EventActionN getJumpEvent() {
		return this._jumpEvent;
	}

	public MoveControl setSubmitEvent(EventActionN e) {
		this._submitEvent = e;
		return this;
	}

	public EventActionN getSubmitEvent() {
		return this._submitEvent;
	}

	public MoveControl setCancelEvent(EventActionN e) {
		this._cancelEvent = e;
		return this;
	}

	public EventActionN getCancelEvent() {
		return this._cancelEvent;
	}

	public MoveControl setBackEvent(EventActionN e) {
		this._backEvent = e;
		return this;
	}

	public EventActionN getBackEvent() {
		return this._backEvent;
	}

	public void onInputCall() {
		if (_currentInput != null) {
			onInputCall(_currentInput);
		}
	}

	public void onInputCall(SysInput input) {
		if (!_running) {
			return;
		}
		if (input == null) {
			return;
		}
		_isPressedKeyD = input.isKeyPressed(SysKey.RIGHT) || input.isKeyPressed(SysKey.D);
		_isPressedKeyA = input.isKeyPressed(SysKey.LEFT) || input.isKeyPressed(SysKey.A);
		_isPressedKeyW = input.isKeyPressed(SysKey.UP) || input.isKeyPressed(SysKey.W);
		_isPressedKeyS = input.isKeyPressed(SysKey.DOWN) || input.isKeyPressed(SysKey.S);

		_isAction1 = input.isKeyPressed(SysKey.Z);
		_isAction2 = input.isKeyPressed(SysKey.X);
		_isAction3 = input.isKeyPressed(SysKey.C);

		final boolean newRight = _isPressedKeyD;
		boolean newLeft = _isPressedKeyA;
		boolean newUp = _isPressedKeyW;
		boolean newDown = _isPressedKeyS;

		_isReleasedKeyD = (input.isKeyReleased(SysKey.RIGHT) || input.isKeyReleased(SysKey.D));
		_isReleasedKeyA = (input.isKeyReleased(SysKey.LEFT) || input.isKeyReleased(SysKey.A));
		_isReleasedKeyW = (input.isKeyReleased(SysKey.UP) || input.isKeyReleased(SysKey.W));
		_isReleasedKeyS = (input.isKeyReleased(SysKey.DOWN) || input.isKeyReleased(SysKey.S));

		_isJump = input.isKeyReleased(SysKey.SPACE);
		_isSubmit = input.isKeyReleased(SysKey.ENTER) || input.isKeyReleased(SysKey.END);
		_isCancel = input.isKeyReleased(SysKey.ESCAPE);
		_isBack = input.isKeyReleased(SysKey.BACKSPACE) || input.isKeyReleased(SysKey.BACK);

		_justRight = !(_left && newLeft) && _isReleasedKeyD;
		_justLeft = !(_right && newRight) && _isReleasedKeyA;
		_justUp = !(_down && newDown) && _isReleasedKeyW;
		_justDown = !(_up && newUp) && _isReleasedKeyS;
		if ((_right && newRight) || (_left && newLeft)) {
			_isLeftOrRight = true;
			_isDownOrUp = false;
		} else if (newRight) {
			_right = true;
			_left = false;
		} else if (newLeft) {
			_right = false;
			_left = true;
		} else {
			_left = false;
			_right = _left;
		}
		if ((_up && newUp) || (_down && newDown)) {
			_isDownOrUp = true;
			_isLeftOrRight = false;
		} else if (newUp) {
			_up = true;
			_down = false;
		} else if (newDown) {
			_up = false;
			_down = true;
		} else {
			_down = false;
			_up = false;
		}
		if (_left) {
			if (_leftEvent != null) {
				_leftEvent.update();
			}
			setDirection(_iso45DMovement ? Config.LEFT : Config.TLEFT);
		}
		if (_right) {
			if (_rightEvent != null) {
				_rightEvent.update();
			}
			setDirection(_iso45DMovement ? Config.RIGHT : Config.TRIGHT);
		}
		if (_down) {
			if (_downEvent != null) {
				_downEvent.update();
			}
			setDirection(_iso45DMovement ? Config.DOWN : Config.TDOWN);
		}
		if (_up) {
			if (_upEvent != null) {
				_upEvent.update();
			}
			setDirection(_iso45DMovement ? Config.UP : Config.TUP);
		}
		if (_isAction1 && _action1Event != null) {
			_action1Event.update();
		}
		if (_isAction2 && _action2Event != null) {
			_action2Event.update();
		}
		if (_isAction3 && _action3Event != null) {
			_action3Event.update();
		}
		if (_isJump && _jumpEvent != null) {
			_jumpEvent.update();
		}
		if (_isCancel && _cancelEvent != null) {
			_cancelEvent.update();
		}
		if (_isSubmit && _submitEvent != null) {
			_submitEvent.update();
		}
		if (_isBack && _backEvent != null) {
			_backEvent.update();
		}
		if (_actionAnyEvent != null) {
			_actionAnyEvent.update();
		}
	}

	public MoveControl setUpAction(EventActionN e) {
		this._upEvent = e;
		return this;
	}

	public EventActionN getUpAction() {
		return this._upEvent;
	}

	public MoveControl setDownAction(EventActionN e) {
		this._downEvent = e;
		return this;
	}

	public EventActionN getDownAction() {
		return this._downEvent;
	}

	public MoveControl setLeftAction(EventActionN e) {
		this._leftEvent = e;
		return this;
	}

	public EventActionN getLeftAction() {
		return this._leftEvent;
	}

	public MoveControl setRightAction(EventActionN e) {
		this._rightEvent = e;
		return this;
	}

	public EventActionN getRightAction() {
		return this._rightEvent;
	}

	public MoveControl upIso() {
		return this.setDirection(Config.UP);
	}

	public MoveControl tup() {
		return this.setDirection(Config.TUP);
	}

	public MoveControl downIso() {
		return this.setDirection(Config.DOWN);
	}

	public MoveControl tdown() {
		return this.setDirection(Config.TDOWN);
	}

	public MoveControl leftIso() {
		return this.setDirection(Config.LEFT);
	}

	public MoveControl tleft() {
		return this.setDirection(Config.TLEFT);
	}

	public MoveControl rightIso() {
		return this.setDirection(Config.RIGHT);
	}

	public MoveControl tright() {
		return this.setDirection(Config.TRIGHT);
	}

	public MoveControl setDirection(int d) {
		this._direction = d;
		return this;
	}

	public MoveControl resetDirection() {
		return setDirection(-1);
	}

	public int getDirection() {
		return this._direction;
	}

	public boolean isIso45DMovement() {
		return _iso45DMovement;
	}

	public MoveControl setIso45DMovement(boolean d) {
		this._iso45DMovement = d;
		return this;
	}

	public final MoveControl call() {
		onInputCall();
		move(_bindObject, _currentArrayMap, _direction);
		return this;
	}

	public MoveControl start() {
		return submit();
	}

	public MoveControl submit() {
		if (!_running) {
			final MoveControlProcess process = new MoveControlProcess(this);
			process.setProcessType(GameProcessType.Progress);
			process.setDelay(_delay);
			_running = true;
			RealtimeProcessManager.get().addProcess(process);
		}
		return this;
	}

	public MoveControl stop() {
		this._running = false;
		return this;
	}

	public boolean isMoveBlock() {
		return _moveBlocked;
	}

	public MoveControl setMoveBlock(boolean b) {
		this._moveBlocked = b;
		return this;
	}

	protected final boolean checkTileCollision(Field2D field2d, ActionBind bind, float newX, float newY) {
		if (field2d == null) {
			if (!_moveBlocked && _tileCollisionListener != null) {
				return _tileCollisionListener.checkTileCollision(bind.getX() - _offsetX, bind.getY() - _offsetY,
						bind.getWidth() * _vagueWidthScale, bind.getHeight() * _vagueHeightScale, newX, newY);
			}
			return false;
		}
		return !_moveBlocked && field2d.checkTileCollision(bind.getX() - _offsetX, bind.getY() - _offsetY,
				bind.getWidth() * _vagueWidthScale, bind.getHeight() * _vagueHeightScale, newX, newY);
	}

	public final boolean move(ActionBind bind, Field2D field2d, int direction) {
		if (bind == null) {
			return false;
		}
		float startX = bind.getX() - _offsetX;
		float startY = bind.getY() - _offsetY;
		float newX = 0;
		float newY = 0;
		_isMoving = true;
		if (field2d != null) {
			switch (direction) {
			case Field2D.TUP:
				newY = startY - _moveSpeed;
				if (!checkTileCollision(field2d, bind, startX, newY)) {
					startY = newY;
				} else {
					_isMoving = false;
				}
				break;
			case Field2D.TDOWN:
				newY = startY + _moveSpeed;
				if (!checkTileCollision(field2d, bind, startX, newY)) {
					startY = newY;
				} else {
					_isMoving = false;
				}
				break;
			case Field2D.TLEFT:
				newX = startX - _moveSpeed;
				if (!checkTileCollision(field2d, bind, newX, startY)) {
					startX = newX;
				} else {
					_isMoving = false;
				}
				break;
			case Field2D.TRIGHT:
				newX = startX + _moveSpeed;
				if (!checkTileCollision(field2d, bind, newX, startY)) {
					startX = newX;
				} else {
					_isMoving = false;
				}
				break;
			case Field2D.UP:
				newX = startX + _moveSpeed;
				newY = startY - _moveSpeed;
				if (!checkTileCollision(field2d, bind, newX, newY)) {
					startX = newX;
					startY = newY;
				} else {
					_isMoving = false;
				}
				break;
			case Field2D.DOWN:
				newX = startX - _moveSpeed;
				newY = startY + _moveSpeed;
				if (!checkTileCollision(field2d, bind, newX, newY)) {
					startX = newX;
					startY = newY;
				} else {
					_isMoving = false;
				}
				break;
			case Field2D.LEFT:
				newX = startX - _moveSpeed;
				newY = startY - _moveSpeed;
				if (!checkTileCollision(field2d, bind, newX, newY)) {
					startX = newX;
					startY = newY;
				} else {
					_isMoving = false;
				}
				break;
			case Field2D.RIGHT:
				newX = startX + _moveSpeed;
				newY = startY + _moveSpeed;
				if (!checkTileCollision(field2d, bind, newX, newY)) {
					startX = newX;
					startY = newY;
				} else {
					_isMoving = false;
				}
				break;
			}
		} else if (!_moveBlocked) {
			final Vector2f moved = Field2D.getDirectionToPoint(direction, 1, _tempPos);
			newX = startX + (_moveSpeed * moved.x);
			newY = startY + (_moveSpeed * moved.y);
			if (!checkTileCollision(field2d, bind, newX, newY)) {
				startX = newX;
				startY = newY;
			} else {
				_isMoving = false;
			}
		}
		if (_isMoving) {
			movePos(bind, startX, startY, direction);
		}
		return true;
	}

	public MoveControl movePos(ActionBind bind, float x, float y, int dir) {
		return movePos(bind, x, y, -1f, -1f, dir);
	}

	public MoveControl movePos(ActionBind bind, float x, float y, float lastX, float lastY, int dir) {
		if (bind == null) {
			return this;
		}
		if (_collisionWorld != null) {
			if (_worldCollisionFilter == null) {
				_worldCollisionFilter = CollisionFilter.getDefault();
			}
			_actionRect.setRect(bind.getX() - _offsetX, bind.getY() - _offsetY, bind.getWidth() * _vagueWidthScale,
					bind.getHeight() * _vagueHeightScale);
			CollisionResult.Result result = _collisionWorld.move(_actionRect, x, y, _worldCollisionFilter);
			if (lastX != -1 && lastY != -1) {
				if (result.goalX != x || result.goalY != y) {
					bind.setLocation(lastX + _offsetX, lastY + _offsetY);
				} else {
					bind.setLocation(result.goalX + _offsetY, result.goalY + _offsetY);
				}
			} else {
				bind.setLocation(result.goalX + _offsetX, result.goalY + _offsetY);
			}
		} else {
			bind.setLocation(x + _offsetX, y + _offsetY);
		}
		_lastX = bind.getX() - _offsetX;
		_lastY = bind.getY() - _offsetY;
		_lastDirection = dir;
		return this;
	}

	public boolean isMoving() {
		return _isMoving;
	}

	public TileCollisionListener getTileCollisionListener() {
		return _currentArrayMap == null ? _tileCollisionListener : _currentArrayMap.getTileCollisionListener();
	}

	public MoveControl setTileCollisionListener(TileCollisionListener t) {
		if (_currentArrayMap != null) {
			_currentArrayMap.setTileCollisionListener(t);
		}
		_tileCollisionListener = t;
		return this;
	}

	public float getSpeed() {
		return _moveSpeed;
	}

	public MoveControl setSpeed(float s) {
		this._moveSpeed = s;
		return this;
	}

	public boolean isRunning() {
		return _running;
	}

	public boolean isFreeDir() {
		return _freeDir;
	}

	public MoveControl setFreeDir(boolean d) {
		this._freeDir = d;
		return this;
	}

	public boolean isWN() {
		return _lastDirection == Config.WN;
	}

	public boolean isW() {
		return _lastDirection == Config.W;
	}

	public boolean isSW() {
		return _lastDirection == Config.SW;
	}

	public boolean isS() {
		return _lastDirection == Config.S;
	}

	public boolean isNE() {
		return _lastDirection == Config.NE;
	}

	public boolean isN() {
		return _lastDirection == Config.N;
	}

	public boolean isES() {
		return _lastDirection == Config.ES;
	}

	public boolean isE() {
		return _lastDirection == Config.E;
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

	public long getDelay() {
		return _delay;
	}

	public MoveControl setDelay(long delay) {
		this._delay = delay;
		return this;
	}

	public float getLastX() {
		return _lastX;
	}

	public float getLastY() {
		return _lastY;
	}

	public float getOffsetX() {
		return _offsetX;
	}

	public MoveControl setOffsetX(float offsetX) {
		this._offsetX = offsetX;
		return this;
	}

	public float getOffsetY() {
		return _offsetY;
	}

	public MoveControl setOffsetY(float offsetY) {
		this._offsetY = offsetY;
		return this;
	}

	public CollisionFilter getCollisionFilter() {
		return _worldCollisionFilter;
	}

	public MoveControl setCollisionFilter(CollisionFilter filter) {
		this._worldCollisionFilter = filter;
		return this;
	}

	public CollisionWorld getCollisionWorld() {
		return _collisionWorld;
	}

	public MoveControl setCollisionWorld(CollisionWorld world) {
		this._collisionWorld = world;
		return this;
	}

	public MoveControl setVagueScale(float scale) {
		this.setVagueScale(scale, scale);
		return this;
	}

	public MoveControl setVagueScale(float ws, float hs) {
		this.setVagueWidthScale(ws);
		this.setVagueHeightScale(hs);
		return this;
	}

	public float getVagueWidthScale() {
		return _vagueWidthScale;
	}

	public MoveControl setVagueWidthScale(float ws) {
		this._vagueWidthScale = ws;
		return this;
	}

	public float getVagueHeightScale() {
		return _vagueHeightScale;
	}

	public MoveControl setVagueHeightScale(float hs) {
		this._vagueHeightScale = hs;
		return this;
	}

	public boolean isKeyUp() {
		return _up;
	}

	public boolean isKeyDown() {
		return _down;
	}

	public boolean isKeyRight() {
		return _right;
	}

	public boolean isKeyLeft() {
		return _left;
	}

	public boolean isDirChangeUpPressed() {
		return isKeyUp() && !isTUp();
	}

	public boolean isDirChangeDownPressed() {
		return isKeyDown() && !isTDown();
	}

	public boolean isDirChangeRightPressed() {
		return isKeyRight() && !isTRight();
	}

	public boolean isDirChangeLeftPressed() {
		return isKeyLeft() && !isTLeft();
	}

	public boolean isDirChangeIsoUpPressed() {
		return isKeyUp() && !isUp();
	}

	public boolean isDirChangeIsoDownPressed() {
		return isKeyDown() && !isDown();
	}

	public boolean isDirChangeIsoRightPressed() {
		return isKeyRight() && !isRight();
	}

	public boolean isDirChangeIsoLeftPressed() {
		return isKeyLeft() && !isLeft();
	}

	public boolean isAnyDirKeyPressed() {
		return _left || _right || _down || _up;
	}

	public boolean isAnyDirKeyReleased() {
		return _isReleasedKeyA || _isReleasedKeyD || _isReleasedKeyS || _isReleasedKeyW;
	}

	public boolean isAnyDirKeyJust() {
		return _justRight || _justLeft || _justUp || _justDown;
	}

	public boolean isJustUp() {
		return _justUp;
	}

	public boolean isJustDown() {
		return _justDown;
	}

	public boolean isJustRight() {
		return _justRight;
	}

	public boolean isJustLeft() {
		return _justLeft;
	}

	public boolean isPressedKeyA() {
		return _isPressedKeyA;
	}

	public boolean isReleasedKeyA() {
		return _isReleasedKeyA;
	}

	public boolean isPressedKeyS() {
		return _isPressedKeyS;
	}

	public boolean isReleasedKeyS() {
		return _isReleasedKeyS;
	}

	public boolean isPressedKeyD() {
		return _isPressedKeyD;
	}

	public boolean isReleasedKeyD() {
		return _isReleasedKeyD;
	}

	public boolean isPressedKeyW() {
		return _isPressedKeyW;
	}

	public boolean isReleasedKeyW() {
		return _isReleasedKeyW;
	}

	public boolean isLeftOrRight() {
		return _isLeftOrRight;
	}

	public boolean isDownOrUp() {
		return _isDownOrUp;
	}

	public boolean isAction1() {
		return _isAction1;
	}

	public boolean isAction2() {
		return _isAction2;
	}

	public boolean isAction3() {
		return _isAction3;
	}

	public boolean isJump() {
		return _isJump;
	}

	public boolean isSubmit() {
		return _isSubmit;
	}

	public boolean isCancel() {
		return _isCancel;
	}

	public boolean isBack() {
		return _isBack;
	}

	public boolean isClosed() {
		return this._closed;
	}

	@Override
	public void close() {
		_running = false;
		_closed = true;
	}

}
