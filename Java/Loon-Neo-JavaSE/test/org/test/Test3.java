package org.test;

import java.nio.ShortBuffer;

import loon.BaseIO;
import loon.EmptyObject;
import loon.HorizontalAlign;
import loon.Json;
import loon.LSetting;
import loon.LSystem;
import loon.LTexture;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.action.avg.AVGDialog;
import loon.action.collision.CollisionFilter;
import loon.action.collision.CollisionObject;
import loon.action.collision.CollisionResult;
import loon.action.collision.CollisionWorld;
import loon.action.collision.Collisions;
import loon.action.map.Config;
import loon.action.map.Hexagon;
import loon.action.map.HexagonMap;
import loon.action.map.TileVisit;
import loon.action.map.colider.TileImpl;
import loon.action.sprite.Animation;
import loon.action.sprite.Bullet;
import loon.action.sprite.BulletEntity;
import loon.action.sprite.Entity;
import loon.action.sprite.GridEntity;
import loon.action.sprite.ISprite;
import loon.action.sprite.MoveObject;
import loon.action.sprite.Picture;
import loon.action.sprite.Sprite;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.WaitSprite;
import loon.action.sprite.effect.FadeBoardEffect;
import loon.action.sprite.effect.FadeDotEffect;
import loon.action.sprite.effect.FadeEffect;
import loon.action.sprite.effect.FadeOvalEffect;
import loon.action.sprite.effect.FadeSpiralEffect;
import loon.action.sprite.effect.FadeTileEffect;
import loon.action.sprite.effect.PixelBubbleEffect;
import loon.action.sprite.effect.PixelChopEffect;
import loon.action.sprite.effect.PixelDarkOutEffect;
import loon.action.sprite.effect.PixelFireEffect;
import loon.action.sprite.effect.StringEffect;
import loon.action.sprite.effect.SwipeEffect;
import loon.action.sprite.effect.TextEffect;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.Pixmap;
import loon.component.LTextTree;
import loon.component.LToast;
import loon.component.LToast.Style;
import loon.component.UIControls;
import loon.component.layout.JsonLayout;
import loon.component.layout.LayoutManager;
import loon.component.layout.Margin;
import loon.events.ActionUpdate;
import loon.events.CacheListener;
import loon.events.DrawListener;
import loon.events.GameTouch;
import loon.events.LTouchLocationState;
import loon.events.SysTouch;
import loon.events.Touched;
import loon.events.Updateable;
import loon.component.DefUI;
import loon.component.LButton;
import loon.component.LCheckBox;
import loon.component.LClickButton;
import loon.component.LHtmlView;
import loon.component.LLabel;
import loon.component.LMenuSelect;
import loon.component.LMessage;
import loon.component.LTextField;
import loon.font.LFont;
import loon.geom.Affine2f;
import loon.geom.Circle;
import loon.geom.Line;
import loon.geom.Matrix3;
import loon.geom.Matrix4;
import loon.geom.Path;
import loon.geom.RectBox;
import loon.geom.Triangle2f;
import loon.geom.Vector2f;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.opengl.LSTRFont;
import loon.opengl.LTexturePack;
import loon.opengl.LTexturePackClip;
import loon.opengl.d3d.shaders.ShaderOutput;
import loon.utils.ArrayMap;
import loon.utils.Easing.EasingMode;
import loon.utils.HtmlCmd;
import loon.utils.qrcode.QRCode;
import loon.utils.qrcode.QRErrorLevel;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.cache.ActionBindCache;
import loon.utils.cache.CacheObjectManager;
import loon.utils.cache.CacheObjectPool;
import loon.utils.cache.ListenerCache;
import loon.utils.html.HtmlDisplay;
import loon.utils.html.HtmlElement;
import loon.utils.html.HtmlParser;
import loon.utils.html.css.CssDimensions;
import loon.utils.html.css.CssParser;
import loon.utils.html.css.CssStyleBuilder;
import loon.utils.html.css.CssStyleNode;
import loon.utils.html.css.CssStyleSheet;
import loon.utils.timer.Duration;
import loon.utils.timer.Interval;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;
import loon.utils.timer.Scheduler;

public class Test3 extends Screen {

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	String css1 = " * { display: block; padding: 12px;}" + ".a { background: #ff0000; }" + ".b { background: #ffa500; }"
			+ ".c { background: #ffff00; }" + ".d { background: #008; }" + ".e { background: #0000ff; }"
			+ ".f { background: #4b0082; }" + ".g { background: #800080; }";

	LTexture texttt;

	@Override
	public void onLoad() {

		// 构建一个延迟事务管理器(remove项为true时,删除已经完成的延迟事务)
		final Scheduler s = new Scheduler(true);
		// 添加事务1,间隔0,执行3次
		s.add(new Interval(0, 3) {

			@Override
			public void loop() {
				i("a");
			}
		});
		// 添加事务2
		s.add(new Interval() {

			@Override
			public void loop() {
				i("b");
				// 停止此事务(强制完成)
				stop();

			}
		});
		// 添加事务3
		s.add(new Interval() {

			@Override
			public void loop() {
				i("c");
				// 停止此事务(强制完成)
				stop();
			}
		});
		// 延迟1秒
		s.setDelay(LSystem.SECOND);
		s.start();
		// 竖行(垂直)排列模式,越界自动换行(如果需要改变对象大小以适应区域大小,应使用LayoutManager处理,Margin只负责改变动作对象位置)

		// setBackground(LColor.blue);
		// addSprite(DefUI.getGameWinButton(55, 55, LColor.gray),66,66);
		/*
		 * LTexturePack pack= new LTexturePack();
		 * 
		 * pack.putImage("gushimoshi.png"); pack.putImage("quitgame.png");
		 * pack.putImage("tryagain.png");
		 * 
		 * LTexture tex1= loadTexture("gushimoshi.png"); LTexture tex2=
		 * loadTexture("quitgame.png"); LTexture tex3=
		 * loadTexture("tryagain.png");
		 * 
		 * LButton button = new LButton(66, 66);
		 * 
		 * button.setDefAndPress(tex1, tex2); add(button);
		 * getDesktop().scrollTo(155, 55);
		 */

		// 默认，划过，按下,禁用
		/*
		 * String html = "<div class='a'>Div A" + "  <div class='b'> Div B" +
		 * "<div class='c'> Div C" + "<div class='d'> Div D" +
		 * "<div class='e'> Div E" + "<div class='f'> Div F" +
		 * "<div class='g'> Div G </div>" + "</div>" + "</div>" + "</div>" +
		 * "</div>" + "</div>" +
		 * "<div class='d'>testttttttttttttttttestttttttttttttttttestttttttttttttttttestttttttttttttttt</div></div>"
		 * ;
		 * 
		 * String css = " * { display: block; padding: 12px;}" +
		 * ".a { background: #ff0000; }" + ".b { background: #ffa500; }" +
		 * ".c { background: #ffff00; }" + ".d { background: #008000; }" +
		 * ".e { background: #0000ff; }" + ".f { background: #4b0082; }" +
		 * ".g { background: #800080; }"; CssStyleSheet style =
		 * CssParser.loadText(css);
		 * 
		 * CssDimensions block = CssDimensions.createDimension(480, 320);
		 * 
		 * CssLayoutBuilder lbuilder = new CssLayoutBuilder(); HtmlElement ele =
		 * HtmlParser.loadText(html); CssStyleBuilder builder = new
		 * CssStyleBuilder(); CssStyleNode stycc = builder.build(ele, style);
		 * CssLayout box = lbuilder.layoutTree(stycc, block);
		 * 
		 * builder2.build(box); setColor(LColor.white);
		 */
		/*
		 * LTextField field=new LTextField(66, 66);
		 * field.setHideBackground(true); add(field);
		 * 
		 * 
		 * LTextField field2=new LTextField(166, 166);
		 * field2.setHideBackground(true); add(field2);
		 */
	}

	@Override
	public void draw(GLEx g) {

		g.draw(texttt, 122, 122);
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
		// 原始大小
		setting.width = 480;
		setting.height = 320;
		setting.isDebug = true;
		setting.isLogo = false;
		setting.isDisplayLog = false;
		setting.logoPath = "loon_logo.png";

		// setting.emulateTouch = true;
		// 要求显示的大小
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		setting.logoPath = "loon_logo.png";
		setting.isFPS = false;
		setting.isMemory = false;
		// 默认字体
		setting.fontName = "黑体";
		// setting.emulateTouch = true;
		// setting.emulateTouch = true;

		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {

				return new MapTest();
				// EmulatorTest
			}
		});
	}
}
