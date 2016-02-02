package org.doudizhu.test;

import loon.LSystem;
import loon.LTexture;
import loon.geom.RectF;
import loon.opengl.GLEx;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.timer.LTimerContext;

public class GameView {
	// 扑克台
	Desk desk;
	// 背景
	LTexture background;

	boolean threadFlag = true;
	public GameView() {
		desk = new Desk();
		background = Game.getImage("game_bg");
		RealtimeProcess process = new RealtimeProcess() {
			
			@Override
			public void run(LTimerContext time) {
				desk.gameLogic();
				if(!threadFlag){
					kill();
				}
				
			}
		};
		process.setDelay(LSystem.SECOND);
		RealtimeProcessManager.get().addProcess(process);
	}
	
	public boolean onTouch(int x,int y) {
		desk.onTuch(x, y);
		return true;
	}


	protected void onDraw(GLEx g) {
		RectF.Range src = new RectF.Range();
		RectF.Range des = new RectF.Range();
		src.set(0, 0, background.getWidth(), background.getHeight());
		des.set(0, 0, g.getWidth(), g.getHeight());
		g.drawBitmap(background, src, des, null);
		desk.controlPaint(g);
	}

}
