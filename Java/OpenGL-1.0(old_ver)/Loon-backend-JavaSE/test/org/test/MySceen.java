package org.test;

import java.io.IOException;
import java.util.ArrayList;

import loon.LGame;
import loon.JavaSEInputFactory;
import loon.LSetting;
import loon.LSystem;
import loon.LTouch;
import loon.LTransition;
import loon.Touch;
import loon.action.ActionBind;
import loon.action.ActionControl;
import loon.action.ActionListener;
import loon.action.FireTo;
import loon.action.MoveTo;
import loon.action.RotateTo;
import loon.action.ScaleTo;
import loon.action.avg.AVGDialog;
import loon.action.map.Field2D;
import loon.action.map.TileMap;
import loon.action.map.tmx.TMXLayer;
import loon.action.map.tmx.TMXTile;
import loon.action.map.tmx.TMXTiledMap;
import loon.action.sprite.Sprite;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteSheet;
import loon.core.Alphabet;
import loon.core.Assets;
import loon.core.geom.Path;
import loon.core.graphics.LComponent;
import loon.core.graphics.LScrollBar;
import loon.core.graphics.LScrollContainer;
import loon.core.graphics.Screen;
import loon.core.graphics.component.ClickListener;
import loon.core.graphics.component.DefUI;
import loon.core.graphics.component.LButton;
import loon.core.graphics.component.LCheckBox;
import loon.core.graphics.component.LClickButton;
import loon.core.graphics.component.LDecideName;
import loon.core.graphics.component.LLabel;
import loon.core.graphics.component.LLabel.LabelAlignment;
import loon.core.graphics.component.LMenu.MenuItem;
import loon.core.graphics.component.Actor;
import loon.core.graphics.component.LInfo;
import loon.core.graphics.component.LLabels;
import loon.core.graphics.component.LLayer;
import loon.core.graphics.component.LMap2D;
import loon.core.graphics.component.LMessage;
import loon.core.graphics.component.LPad;
import loon.core.graphics.component.LPanel;
import loon.core.graphics.component.LPaper;
import loon.core.graphics.component.LProgress;
import loon.core.graphics.component.LMenu;
import loon.core.graphics.component.LSelectorIcon;
import loon.core.graphics.component.LTextArea;
import loon.core.graphics.component.LProgress.ProgressType;
import loon.core.graphics.component.LTextBar;
import loon.core.graphics.component.LTextField;
import loon.core.graphics.component.LTextList;
import loon.core.graphics.component.LToast;
import loon.core.graphics.component.LToast.Style;
import loon.core.graphics.component.LWindow;
import loon.core.graphics.component.table.ITableModel;
import loon.core.graphics.component.table.LTable;
import loon.core.graphics.component.table.ListItem;
import loon.core.graphics.component.table.SimpleTableModel;
import loon.core.graphics.component.table.TableLayout;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LFont;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.device.LImage;
import loon.core.graphics.device.LShadow;
import loon.core.graphics.opengl.GL;
import loon.core.graphics.opengl.GL10;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.GLUtils;
import loon.core.graphics.opengl.LSTRFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.graphics.opengl.LTextures;
import loon.core.graphics.opengl.TextureUtils;
import loon.core.processes.RealtimeProcess;
import loon.core.processes.RealtimeProcessManager;
import loon.core.timer.LTimer;
import loon.core.timer.LTimerContext;
import loon.media.Sound;
import loon.utils.collection.Array;

public class MySceen extends Screen {

	public void onLoad() {

		// 这个null代表不要纹理显示
		/*LTextArea area = new LTextArea(66, 66, 300, 50, (LTexture) null);
		area.put("您获得了1个成就", LColor.red);
		area.put("GGGGGGGGGG");
		// addString时，将追加数据并动态显示
		area.addString("12345", LColor.red);
		area.setWaitFlag(false);
		area.setTopOffset(5);
		area.setLeftOffset(5);
		add(area);*/
	}

	
	LTexture texture=new LTexture("assets/back1.png");
	LTexture text2=null;
	@Override
	public void draw(GLEx g) {
		if(text2==null){
			text2=AVGDialog.getRMXPDialog("assets/w6.png", 400, 400);
		}
		g.drawTexture(text2, 0,0);
	//	g.drawTexture(texture, 0, 0,loon.core.graphics.opengl.GLEx.Direction.TRANS_MF);
	}

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		setting.showFPS = true;
		setting.showLogo = false;
		setting.resizable = false;
		setting.width = 480;
		setting.height = 320;
		LGame.register(setting, MySceen.class);
	}

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void alter(LTimerContext timer) {

	}

	@Override
	public void touchDown(LTouch e) {

		//playSound("assets/music/pussy.wav");
	}

	@Override
	public void touchUp(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchMove(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchDrag(LTouch e) {
		// TODO Auto-generated method stub

	}

}
