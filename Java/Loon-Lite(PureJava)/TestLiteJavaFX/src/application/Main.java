package application;

import loon.LTexture;
import loon.LTransition;
import loon.Screen;
import loon.canvas.LColor;
import loon.events.GameTouch;
import loon.fx.JavaFXSetting;
import loon.fx.Loon;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class Main extends Loon {

	public static class ScreenTest extends Screen {

		LTexture texture = loadTexture("player.png");

		public LTransition onTransition() {
			return LTransition.newArc(LColor.red);
		}
		
		@Override
		public void draw(GLEx g) {
		//	 g.fillRect(66, 66, 388, 388,LColor.red);
			g.draw(texture, 77, 77,128,128);
			g.drawString("数据测试avddf", 77, 77);
		}

		@Override
		public void onLoad() {
			texture = texture.cpy(0,32,32,32);
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

	}

	public static void main(String[] args) {

		JavaFXSetting setting = new JavaFXSetting();
		// 原始大小
		setting.width = 480;
		setting.height = 320;
		setting.isDebug = true;
		setting.isLogo = false;
		setting.isDisplayLog = false;

		// 要求显示的大小
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		setting.logoPath = "loon_logo.png";
		setting.isFPS = false;
		setting.isMemory = false;
		setting.iconPaths = new String[] { "l.png" };
		setting.fullscreen = false;
		// 默认字体
		setting.fontName = "黑体";
		//setting.fullscreen = true;

		register(Main.class, setting, () -> {
			return new ScreenTest();
		});
	}

}
