package org.doudizhu.test;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.Paint;
import loon.geom.RectF;
import loon.opengl.GLEx;

public class MenuView {

	boolean threadFlag = true;
	LTexture background;
	private int x = 270;
	private int y = 50;
	private LTexture[] menuItems;
	public MenuView() {
		init();
	}

	private void init() {
		menuItems = new LTexture[5];
		background = Game.getImage("menu_bg");
		menuItems[0] = Game.getImage("menu1");
		menuItems[1] = Game.getImage("menu2");
		menuItems[2] = Game.getImage("menu3");
		menuItems[3] = Game.getImage("menu4");
		menuItems[4] = Game.getImage("menu5");
	}

	protected void onDraw(GLEx canvas) {

		RectF.Range src = new RectF.Range ();
		RectF.Range  des = new RectF.Range ();
		src.set(0, 0, background.getWidth(), background.getHeight());

		des.set(0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
		Paint paint = new Paint();
		canvas.drawBitmap(background, src, des, paint);
		for (int i = 0; i < menuItems.length; i++) {
			canvas.drawBitmap(menuItems[i], (int) (x * Game.SCALE_HORIAONTAL),
					(int) ((y + i * 43) * Game.SCALE_VERTICAL), paint);
		}
	}
	
	public void onTouch(int ex,int ey) {
		int selectIndex = -1;
		for (int i = 0; i < menuItems.length; i++) {
			if (CardsManager.inRect(ex, ey, (int) (x * Game.SCALE_HORIAONTAL),
					(int) ((y + i * 43) * Game.SCALE_VERTICAL),
					(int) (125 * Game.SCALE_HORIAONTAL),
					(int) (33 * Game.SCALE_VERTICAL))) {
				selectIndex = i;
				break;
			}
		}
		switch (selectIndex) {
			case 0 :
				Game.sendEmptyMessage(Game.GAME);
	
				break;
			case 1 :
				break;
			case 2 :
				break;
			case 3 :
				break;
			case 4 :
				Game.sendEmptyMessage(Game.EXIT);
				break;
		}
	}
}
