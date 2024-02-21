package com.mygame;

import loon.utils.reply.ObjRef;

public interface GameCore {
	void changeState(EStates id);

	void clearMouseStatus();

	void doButtonPressSound();

	void exit();

	GameState getGameState(EStates id);

	int getH();

	String getLevelDir(int index);

	int getMouseDownTick();

	int getMouseX();

	int getMouseY();

	Settings getSettings();

	int getStateTick();

	int getTick();

	int getValue(EValues valueId);

	int getW();

	boolean isMouseDown();

	boolean isMouseUp();

	boolean isTrial();

	void loadAllStates();

	boolean LoadLevel(int level, ObjRef<Integer> speed,
			ObjRef<java.util.ArrayList<Tile>> tiles,
			ObjRef<java.util.ArrayList<Tile>> caves,
			ObjRef<java.util.ArrayList<ScheduleItem>> schedule);

	void setMenuMusicQuieter(boolean quiet);

	void setValue(EValues valueId, int value);

	void showPurchaseDialog();

	void startMenuMusic(boolean instant);

	void stopMenuMusic(boolean instant);
}