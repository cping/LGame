package org.test;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import loon.BaseIO;
import loon.HorizontalAlign;
import loon.LSystem;
import loon.LTexture;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.action.MoveTo;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.action.map.TileMap;
import loon.action.sprite.AnimatedEntity;
import loon.action.sprite.GifAnimation;
import loon.action.sprite.MoveControl;
import loon.action.sprite.ShapeEntity;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteSheet;
import loon.action.sprite.SpriteSheetFont;
import loon.action.sprite.effect.LightningEffect;
import loon.action.sprite.effect.LightningRandom;
import loon.action.sprite.effect.NaturalEffect;
import loon.action.sprite.effect.RippleEffect;
import loon.action.sprite.effect.StringEffect;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.Pixmap;
import loon.component.DefUI;
import loon.component.LLabel;
import loon.component.LPad;
import loon.component.LPaper;
import loon.component.LScrollBar;
import loon.component.LScrollContainer;
import loon.events.ActionKey;
import loon.events.FrameLoopEvent;
import loon.events.GameTouch;
import loon.events.SysKey;
import loon.events.Touched;
import loon.events.Updateable;
import loon.font.BMFont;
import loon.font.IFont;
import loon.font.LFont;
import loon.geom.Circle;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.javase.Loon;
import loon.javase.JavaSEGame.JavaSetting;
import loon.opengl.BlendState;
import loon.opengl.GLEx;
import loon.opengl.LSTRFont;
import loon.opengl.LTexturePackClip;
import loon.utils.ArrayByte;
import loon.utils.ArrayByteOutput;
import loon.utils.Calculator;
import loon.utils.GLUtils;
import loon.utils.GifEncoder;
import loon.utils.TArray;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.WaitProcess;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public class TestGif extends Screen {

	public static void main(String[] args) {
		JavaSetting setting = new JavaSetting();
		setting.isDebug = true;
		setting.isDisplayLog = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		// 原始大小
		setting.width = 480;
		setting.height = 320;
		// 缩放为
		setting.width_zoom = 1200;
		setting.height_zoom = 800;
		setting.iconPaths = new String[] { "l.png" };
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "test";
		setting.emulateTouch = false;
		setting.isDisplayLog = true;
		// setting.setSystemLogFont(BMFont.getDefaultFont());
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				// 此Screen位于sample文件夹下，引入资源即可加载
				return new TestGif();
			}
		});

	}


	SpriteSheetFont font1;
	SpriteSheetFont font2;
	SpriteSheetFont font3;
	SpriteSheetFont font4;
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {
		if(font1!=null){
			font1.drawString("abcfdfd{}xzxzc+-*/GOTOBREAK", 66, 266);
		}
		if(font2!=null){
			font2.drawString("abcfdfd{}xzxzc+-*/GOTOBREAK", 66, 100);
		}
		if(font3!=null){
			font3.drawString("abcfdfd{}xzxzc+-*/GOTOBREAK", 66, 166);
		}
		if(font4!=null){
			font4.drawString("abcfdfd{}xzxzc+-*/GOTOBREAK", 66, 200);
		}
	}

	@Override
	public void onLoad() {

		font1 = new SpriteSheetFont("font1.png", 6, 6);
		font1.setFontScale(1.5f);
		font2 = new SpriteSheetFont("font2.png", 6, 6);
		font2.setFontScale(1.5f);
		font3 = new SpriteSheetFont("font3.png", 8, 8);
		font3.setFontScale(1.5f);
		font4 = new SpriteSheetFont("font4.png", 3, 5);
		font4.setFontScale(1.5f);
		add(MultiScreenTest.getBackButton(this,0));
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
