package application;

import java.nio.IntBuffer;

import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import loon.EmulatorListener;
import loon.LSystem;
import loon.LTexture;
import loon.LTextureBatch;
import loon.LTransition;
import loon.Screen;
import loon.action.sprite.Bullet;
import loon.canvas.LColor;
import loon.canvas.Pixmap;
import loon.component.LColorPicker;
import loon.component.LToast;
import loon.component.LToast.Style;
import loon.component.table.LTable;
import loon.events.ActionKey;
import loon.events.GameKey;
import loon.events.GameTouch;
import loon.events.SysKey;
import loon.events.SysTouch;
import loon.events.Updateable;
import loon.font.BDFont;
import loon.font.BMFont;
import loon.fx.JavaFXImage;
import loon.fx.JavaFXSetting;
import loon.fx.Loon;
import loon.geom.DirtyRectList;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.opengl.LTexturePack;
import loon.utils.timer.LTimerContext;

public class Main extends Loon {

	public static class ScreenTest extends Screen {

		DirtyRectList list = new DirtyRectList(true);
		
		LTexture texture;

		public LTransition onTransition() {
			return LTransition.newEmpty();
		}

		@Override
		public void draw(GLEx g) {
			for (RectBox rect : list.initList()) {
				if (rect != null) {
					g.drawRect(rect.x, rect.y, rect.width, rect.height, LColor.white);
				}
			}
			for (RectBox rect : list.list()) {
				if (rect != null) {
					g.drawRect(rect.x + 1, rect.y + 1, rect.width + 1, rect.height + 1, LColor.red);
				}
			}
			

		}


		@Override
		public void onLoad() {

			list.add(45, 45, 40, 60);
			list.add(145, 45, 190, 60);
			list.add(45, 45, 95, 65);
			list.add(195, 245, 95, 65);
			list.add(35, 35, 95, 65);

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
		// setting.iconPaths = new String[] { "l.png" };
		setting.fullscreen = false;
		// 默认字体
		setting.fontName = "黑体";
		setting.allScreenRefresh = true;
		// setting.isCloseOnAppExit = true;
		// setting.fullscreen = true;

		register(Main.class, setting, () -> new TableTest());
	}

}
