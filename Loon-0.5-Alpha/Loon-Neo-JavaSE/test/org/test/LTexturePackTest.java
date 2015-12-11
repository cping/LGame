package org.test;

import loon.LSetting;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.canvas.LColor;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.geom.RectBox;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.opengl.LTexturePack;
import loon.utils.TArray;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public class LTexturePackTest extends Screen {

	TArray<Move> moveList;

	// 显示用精灵大小
	final int show_size = 64;

	// 实际精灵大小
	final int really_size = 24;

	LTexturePack imagePack;

	/**
	 * 建立一个移动对象，用以管理LTexturePack中图像的移动
	 */
	public class Move {

		RectBox rect = new RectBox(0, 0, show_size, show_size);

		LTimer timer = new LTimer(150);

		int id;

		float x, y;

		int action = 1;

		int type = -1;

		public Move(int id, int type, float x, float y) {
			this.id = id;
			this.type = type;
			this.x = x;
			this.y = y;
		}

		public void setX(float x) {
			this.x = x;
			rect.setX(x);
		}

		public void setY(float y) {
			this.y = y;
			rect.setY(y);
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

		public boolean intersects(Move m) {
			return rect.intersects(m.rect);
		}

		public void update() {
			if (timer.action(elapsedTime)) {
				action++;
				if (action > 4) {
					action = 1;
				}
			}
		}

	}

	@Override
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {
		if (isOnLoadComplete()) {
			synchronized (moveList) {
				// 当执行glBegin后，将在GLEx触发一个渲染批处理事件，仅在执行glEnd后提交
				// 渲染内容到窗体。如果不调用此函数，则LTexturePack依旧可以执行，但是效率
				// 可能会受到一定影响（每次渲染都单独提交）。
				imagePack.glBegin();
				for (Move o : moveList) {
					switch (o.type) {
					case 0:
						imagePack.draw(o.id, o.x, o.y, show_size, show_size,
								(o.action - 1) * really_size, 0, o.action
										* really_size, really_size, 0,
								LColor.yellow);
						break;
					case 1:
						imagePack.draw(o.id, o.x, o.y, show_size, show_size,
								o.action * really_size, 0, (o.action - 1)
										* really_size, really_size, 0);
						break;
					}
					o.update();
				}

				// 提交渲染结果到游戏画面
				imagePack.glEnd();
			}

		}
	}

	@Override
	public void onLoad() {

		//设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		// 最先绘制用户画面
		setFristOrder(DRAW_USER_PAINT());
		// 不绘制精灵
		setSecondOrder(null);
		// 最后绘制桌面
		setLastOrder(DRAW_DESKTOP_PAINT());
		
		imagePack = new LTexturePack();

		// 加载小图到LTexturePack
		int heroImgId = imagePack.putImage("assets/h_a.png");
		int enemyImgId = imagePack.putImage("assets/e_a.png");

		// 宣布所有图像加载完毕(如果调用此函数，则释放所有已加载的资源，仅保留一块主纹理;如果不调用此函数，
		// LTexturePack将允许动态增减图像，但是已加载的小图资源不会自动释放(可手动释放，或者dispose全部清空))
		imagePack.packed();

		// 构建一个Move集合，用以控制图像移动与显示
		this.moveList = new TArray<Move>(10);

		moveList.add(new Move(heroImgId, 0, 0, 32));
		moveList.add(new Move(heroImgId, 0, 0, 136));
		moveList.add(new Move(heroImgId, 0, 0, 220));

		moveList.add(new Move(enemyImgId, 1, getWidth() - show_size, 32));
		moveList.add(new Move(enemyImgId, 1, getWidth() - show_size, 136));
		moveList.add(new Move(enemyImgId, 1, getWidth() - show_size, 220));
		
		add(MultiScreenTest.getBackButton(this));

	}

	@Override
	public void alter(LTimerContext timer) {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {

	}

	@Override
	public void touchUp(GameTouch e) {

	}

	@Override
	public void touchMove(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void close() {

	}

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "test";
		setting.emulateTouch = false;
		setting.width = 640;
		setting.height = 480;

		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new LTexturePackTest();
			}
		});
	}

}
