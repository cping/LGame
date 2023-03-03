package application;

import loon.EmulatorListener;
import loon.LSystem;
import loon.LTexture;
import loon.LTextureBatch;
import loon.LTransition;
import loon.Screen;
import loon.action.sprite.Bullet;
import loon.canvas.LColor;
import loon.component.LToast;
import loon.component.LToast.Style;
import loon.events.GameTouch;
import loon.font.BDFont;
import loon.font.BMFont;
import loon.fx.JavaFXSetting;
import loon.fx.Loon;
import loon.opengl.GLEx;
import loon.opengl.LTexturePack;
import loon.utils.timer.LTimerContext;

public class Main extends Loon {

	public static class ScreenTest extends Screen implements EmulatorListener {

		LTexture texture = loadTexture("player.png");

		BDFont bdFont;
		BMFont font;

		public LTransition onTransition() {
			return LTransition.newEmpty();
		}

		//纹理批处理
		LTextureBatch batch = null;

		@Override
		public void draw(GLEx g) {
			g.fillRect(66, 66, 388, 388, LColor.red);

			if (batch != null) {
				if (batch.existCache()) {
					batch.postCache(29, 29);
				} else {
					batch.begin();
					batch.draw(0, 0);
					batch.draw(199, 199);
					batch.end();
					batch.newCache();
				}
			}

			g.drawString("数据测试avddf", 77, 77);
			if (bdFont != null) {
				bdFont.drawString(g, "AfdBC", 55, 55);
			}
		}

		@Override
		public void onLoad() {
			texture = texture.cpy(0, 32, 32, 32);
			batch = texture.getTextureBatch();

			bdFont = new BDFont("pixfont.bdf", "MNBVCXZLKJHGFDSAPOIUYTREWQqwertyuiopasdfghjklzxcvbnm");
			bdFont.setFontSize(20);
			font = new BMFont("test.fnt");

		}

		@Override
		public void alter(LTimerContext timer) {

		}

		@Override
		public void resize(int width, int height) {

		}

		@Override
		public void touchDown(GameTouch e) {
			add(LToast.makeText("不在攻击范围中", Style.ERROR));
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

		@Override
		public void onUpClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLeftClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRightClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDownClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTriangleClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSquareClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCircleClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancelClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void unUpClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void unLeftClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void unRightClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void unDownClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void unTriangleClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void unSquareClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void unCircleClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void unCancelClick() {
			// TODO Auto-generated method stub

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
		setting.width_zoom = 800;
		setting.height_zoom = 600;
		setting.logoPath = "loon_logo.png";
		setting.isFPS = false;
		setting.isMemory = false;
		setting.iconPaths = new String[] { "l.png" };
		setting.fullscreen = false;
		// 默认字体
		setting.fontName = "黑体";
		// setting.fullscreen = true;

		register(Main.class, setting, () -> {
			return new TitleScreen();
		});
	}

}
