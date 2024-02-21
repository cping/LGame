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
package com.mygame;

import loon.BaseIO;
import loon.LSystem;
import loon.LTransition;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableScreen;
import loon.events.GameKey;
import loon.events.GameTouch;
import loon.events.SysTouch;
import loon.opengl.GLEx;
import loon.utils.ArrayByte;
import loon.utils.ArrayByteReader;
import loon.utils.reply.ObjRef;
import loon.utils.timer.GameTime;

public class GameMain extends DrawableScreen implements GameCore {

	public GameState activeState;

	private boolean buyDialogActive = false;
	private boolean changingState;
	private GameState[] gameStates = new GameState[10];
	private int gameTick;

	private int height;

	private boolean menuMusicQuieter;
	private float menuMusicVolume;
	private float menuMusicVolumeTarget;
	private boolean mouseAlreadyDown;
	private boolean mouseDown;
	private int mouseDownTick;
	private boolean mouseUp;
	private int mouseX;
	private int mouseY;

	private EStates nextState;

	private Settings settings;

	private int stateTick;

	private int[] values = new int[6];
	private int width;

	Painter painter;

	@Override
	public void loadContent() {
		painter = new Painter(getSpriteBatch());
		this.width = LSystem.viewSize.getWidth();
		this.height = LSystem.viewSize.getHeight();
		this.menuMusicQuieter = false;
		this.menuMusicVolumeTarget = 0f;
		this.menuMusicVolume = this.menuMusicVolumeTarget;
		for (int i = 0; i < 6; i++) {
			this.values[i] = -1;
		}
		this.mouseAlreadyDown = false;
		this.settings = new Settings();

		if (this.getSettings().m_sounds) {

		} else {

		}

		this.gameStates[0] = this.initState(EStates.EGameStateSplash);
		this.changeState(EStates.EGameStateSplash);

	}

	public final void changeState(EStates id) {
		this.stateTick = 0;
		this.mouseDownTick = -1;
		this.clearMouseStatus();
		if (this.activeState == null) {
			this.activeState = this.gameStates[(id.getValue())];
			this.activeState.activateState();
			this.changingState = false;
		} else {
			this.changingState = true;
			this.nextState = id;
		}
	}

	public final void clearMouseStatus() {
		this.mouseX = -1;
		this.mouseY = -1;
		this.mouseDown = false;
		this.mouseUp = false;
	}

	public final void doButtonPressSound() {

	}

	public void draw(GLEx glex) {
		if (!isOnLoadComplete()) {
			return;
		}
		if (this.buyDialogActive) {
			this.clearMouseStatus();
		}
		this.painter.begin();
		this.activeState.paint(this.painter);
		this.painter.end();
	}

	public final void exit() {
		this.stopMenuMusic(true);
	}

	public final GameState getGameState(EStates id) {
		return this.gameStates[id.getValue()];
	}

	public final int getH() {
		return this.height;
	}

	public final String getLevelDir(int index) {
		return "";
	}

	public final int getMouseDownTick() {
		return this.mouseDownTick;
	}

	public final int getMouseX() {
		return this.mouseX;
	}

	public final int getMouseY() {
		return this.mouseY;
	}

	public final Settings getSettings() {
		return this.settings;
	}

	public final int getStateTick() {
		return this.stateTick;
	}

	public final int getTick() {
		return this.gameTick;
	}

	public final int getValue(EValues valueId) {
		return this.values[valueId.getValue()];
	}

	public final int getW() {
		return this.width;
	}

	private GameState initState(EStates id) {
		switch (id) {
		case EGameStateSplash:
			return new StateSplash(this);

		case EGameStateMainMenu:
			return new StateMainMenu(this);

		case EGameStateGame:
			return new StateGame(this);

		case EGameStateLevelFailed:
			return new StateDummy(this);

		case EGameStateLevelSuccess:
			return new StateDummy(this);

		case EGameStateLoadGame:
			return new StateDummy(this);

		case EGameStateLevelSelect:
			return new StateLevelSelect(this);

		case EGameStateMainLevelSelect:
			return new StateMainLevelSelect(this);

		case EGameStateGameEnd:
			return new StateGameEnd(this);

		case EGameStateTrial:
			return new StateTrial(this);
		default:
			break;
		}
		return null;
	}

	public final boolean isMouseDown() {
		return this.mouseDown;
	}

	public final boolean isMouseUp() {
		return this.mouseUp;
	}

	public final boolean isTrial() {
		return false;
	}

	public final void loadAllStates() {
		for (int i = 1; i < 10; i++) {
			this.gameStates[i] = this.initState(EStates.forValue(i));
		}
	}

	public final boolean LoadLevel(int level, ObjRef<Integer> speed, ObjRef<java.util.ArrayList<Tile>> tiles,
			ObjRef<java.util.ArrayList<Tile>> caves, ObjRef<java.util.ArrayList<ScheduleItem>> schedule) {
		try {
			ArrayByte bytes = BaseIO.loadArrayByte("assets/levels/level_" + level + ".lev");
			ArrayByteReader reader = new ArrayByteReader(bytes);
			String record;
			for (; (record = reader.readLine()) != null;) {

				String tempVar = record.substring(0, 1);
				if (tempVar.equals("t")) {
					Tile tile = new Tile();
					tile.InitWithString(record.substring(2));
					tiles.get().add(tile);
				} else if (tempVar.equals("c")) {
					Tile tile2 = new Tile();
					tile2.InitWithString(record.substring(2));
					caves.get().add(tile2);
				}

				else if (tempVar.equals("s")) {
					speed.set(Integer.parseInt(record.substring(2)));

				}

				else if (tempVar.equals("x")) {
					String[] strArray = record.substring(2).split("[,]", -1);
					int aCaveId = Integer.parseInt(strArray[0]);
					int aTicks = Integer.parseInt(strArray[1]);
					ScheduleItem item = new ScheduleItem(aCaveId, aTicks);
					schedule.get().add(item);
				}
			}
			reader.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void onPause() {
		if (this.activeState != null) {
			this.activeState.gameHidden();
		}
	}

	public void onResume() {

	}

	public final void setMenuMusicQuieter(boolean quiet) {
		this.menuMusicQuieter = quiet;
		this.menuMusicVolumeTarget = this.menuMusicQuieter ? 0.6f : 1f;
	}

	public final void setValue(EValues valueId, int value) {
		this.values[valueId.getValue()] = value;
	}

	public final boolean shouldDoMusic() {
		return false;
	}

	public final void startMenuMusic(boolean instant) {
		if (this.shouldDoMusic() && this.getSettings().m_sounds) {
			this.menuMusicVolumeTarget = this.menuMusicQuieter ? 0.6f : 1f;

			if (instant) {
				this.menuMusicVolume = this.menuMusicVolumeTarget;

			}
		}
	}

	public final void stopMenuMusic(boolean instant) {
		if (this.shouldDoMusic()) {
			this.menuMusicVolumeTarget = 0f;
			if (instant) {
				this.menuMusicVolume = this.menuMusicVolumeTarget;

			}
		}
	}

	public final void testLoad() {
	}

	public final void tickMenuMusic() {
		this.shouldDoMusic();
		if ((!this.shouldDoMusic() && (this.menuMusicVolume != 0f))) {
			this.menuMusicVolumeTarget = 0f;
			this.menuMusicVolume = 0f;

		} else if (this.menuMusicVolume != this.menuMusicVolumeTarget) {
			float num = ((this.menuMusicQuieter && (this.menuMusicVolumeTarget == 0.6f))
					&& (this.menuMusicVolume < 0.6f)) ? 0.01f : 0.08f;
			if (this.menuMusicVolume < this.menuMusicVolumeTarget) {
				this.menuMusicVolume += num;
				if (this.menuMusicVolume > this.menuMusicVolumeTarget) {
					this.menuMusicVolume = this.menuMusicVolumeTarget;
				}
			} else if (this.menuMusicVolume > this.menuMusicVolumeTarget) {
				this.menuMusicVolume -= num;
				if (this.menuMusicVolume < this.menuMusicVolumeTarget) {
					this.menuMusicVolume = this.menuMusicVolumeTarget;
				}
			}

		}
	}

	@Override
	public void draw(SpriteBatch batch) {

	}

	@Override
	public void unloadContent() {
		this.painter.close();

	}

	@Override
	public void pressed(GameTouch e) {

	}

	@Override
	public void released(GameTouch e) {

	}

	@Override
	public void move(GameTouch e) {

	}

	@Override
	public void drag(GameTouch e) {

	}

	@Override
	public void pressed(GameKey e) {

	}

	@Override
	public void released(GameKey e) {

	}

	@Override
	public void update(GameTime gameTime) {
		if (!isOnLoadComplete()) {
			return;
		}
		if (this.changingState) {
			if (this.activeState != null) {
				this.activeState.deactivateState();
			}
			this.activeState = null;
			this.changeState(this.nextState);
		}
		if ((this.gameTick % 5) == 0) {
			this.tickMenuMusic();
		}

		this.gameTick++;

		if (SysTouch.isDown()) {
			if (!this.mouseAlreadyDown) {
				this.mouseDown = true;
				this.mouseUp = false;
				this.mouseDownTick = this.gameTick;
				this.mouseAlreadyDown = true;
			}
			this.mouseX = SysTouch.x();
			this.mouseY = SysTouch.y();
		} else {
			this.mouseAlreadyDown = false;
			this.mouseX = SysTouch.x();
			this.mouseY = SysTouch.y();
			if (this.mouseDown) {
				this.mouseUp = true;
				this.mouseDown = false;
			}
		}
		if (!this.buyDialogActive) {
			this.activeState.tick();
		}

	}

	@Override
	public void showPurchaseDialog() {

	}

	public LTransition onTransition() {
		return LTransition.newFadeIn();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

}
