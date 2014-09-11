package com.example.blastergamegles;

import org.test.MainGame;

import loon.LGame;
import loon.core.graphics.opengl.LTexture;

public class MainActivity extends LGame {

	@Override
	public void onGamePaused() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGameResumed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMain() {
		//PS:该游戏原版地图渲染方式较为耗时(多纹理拼接)，在Android环境下默认未启动（需要背景的话加张单独纹理背景就好了……）
		LTexture.ALL_LINEAR = true;
		LSetting setting = new LSetting();
		setting.width = 480;
		setting.height = 800;
		setting.showFPS = true;
		setting.landscape = false;
		setting.fps = 30;
		setting.showFPS = false;
		register(setting, MainGame.class);
	}

}
