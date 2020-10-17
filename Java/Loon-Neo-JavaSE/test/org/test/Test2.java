package org.test;

import java.awt.geom.GeneralPath;

import loon.LSetting;
import loon.LSystem;
import loon.LTexture;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.action.RotateTo;
import loon.action.sprite.Background;
import loon.action.sprite.ImageBackground;
import loon.action.sprite.StatusBar;
import loon.action.sprite.WaitSprite;
import loon.action.sprite.effect.ArcEffect;
import loon.action.sprite.effect.LightningEffect;
import loon.action.sprite.effect.PixelGossipEffect;
import loon.canvas.LColor;
import loon.canvas.Path2D;
import loon.canvas.Pixmap;
import loon.component.LDragging;
import loon.component.LRadar;
import loon.component.LToast;
import loon.component.LWindow;
import loon.events.GameTouch;
import loon.events.SelectAreaListener;
import loon.events.Touched;
import loon.font.LFont;
import loon.geom.Circle;
import loon.geom.Line;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.opengl.LSTRFont;
import loon.opengl.ShaderProgram;
import loon.opengl.ShaderSource;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimerContext;

public class Test2 extends Screen {
	public static void main(String[] args) {
		LSetting setting = new LSetting();
		setting.isDebug = true;
		setting.isDisplayLog = false;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		// 原始大小
		setting.width = 480;
		setting.height = 320;
		// 缩放为
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "test";
		setting.emulateTouch = true;
		// 设置全局字体为bmfont字体
		// setting.setSystemGameFont(BMFont.getDefaultFont());
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				// 此Screen位于sample文件夹下，引入资源即可加载
				return new PathMoveTest();
			}
		});
	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {
		setBackground("back1.png");

		Pixmap pix = new Pixmap(300, 300);
		Path2D path = new Path2D();
		path.drawCircle(25+30/2, 25+30/2, 30);
		pix.fill(path);

		add(pix.texture());

		// LSpider sp=new LSpider(125,125, 50, 50);
		// add(sp);
	}

	@Override
	public void alter(LTimerContext timer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchDown(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchUp(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchMove(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchDrag(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}
}
