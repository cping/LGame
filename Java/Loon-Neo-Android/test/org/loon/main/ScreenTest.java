package org.loon.main;

import org.test.MultiScreenTest;

import loon.EmulatorListener;
import loon.LTexture;
import loon.Screen;
import loon.action.sprite.ScrollText;
import loon.action.sprite.ScrollText.Direction;
import loon.action.sprite.effect.LightningEffect;
import loon.action.sprite.effect.PShadowEffect;
import loon.android.Loon;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.component.LButton;
import loon.component.LCheckBox;
import loon.component.LClickButton;
import loon.component.LComponent;
import loon.component.LMessageBox;
import loon.component.LTextArea;
import loon.component.LTextField;
import loon.component.LToast;
import loon.component.LWindow;
import loon.component.LToast.Style;
import loon.events.CallFunction;
import loon.events.ClickListener;
import loon.events.GameTouch;
import loon.events.Touched;
import loon.font.IFont;
import loon.font.LFont;
import loon.font.TextOptions;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.LSTRFont;
import loon.opengl.LTexturePack;
import loon.utils.processes.GameProcess;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessHost;
import loon.utils.timer.LTimerContext;

public class ScreenTest extends Screen implements EmulatorListener {


	// LTexture texture = loadTexture("loon_wbar.png");

	

	@Override
	public void draw(GLEx g) {
		
	}

	@Override
	public void onLoad() {

		// 构建一个300x240的游戏窗体背景图,颜色黑蓝相间,横向渐变
		LTexture texture = getGameWinFrame(300, 240, LColor.black, LColor.blue, false);
		// 允许显示行数默认(若显示行数设置可以写成(10,66,36,300,240)这类),位置66,36,大小300x240
		final LTextArea area = new LTextArea(66, 36, 300, 240);
		// 替换默认文字颜色并禁止文字闪烁
		area.setDefaultColor(255, 255, 255, false);
		area.setBackground(texture);
		area.put("你惊扰了【撒旦】的安眠", LColor.red);
		area.put("2333333333333", LColor.yellow);
		area.put("6666666666");
		area.put("点击我增加数据");
		// 从下向上刷数据
		// area.setShowType(LTextArea.TYPE_UP);
		// 清空数据
		// area.clear();
		area.up(new Touched() {

			@Override
			public void on(float x, float y) {
				area.put("数据增加", LColor.red);

			}
		});
		// 偏移文字显示位置
		area.setLeftOffset(5);
		area.setTopOffset(5);
		// addString为在前一行追加数据
		area.addString("1", LColor.red);
		add(area);
		add(MultiScreenTest.getBackButton(this, 0));
	
	}

	// g.end();
	// g.restoreTx();
	/*
	 * (if(!flag){ flag=true; batch.begin(); batch.draw(66, 66); batch.draw(166,
	 * 166); batch.end(); }
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void alter(LTimerContext timer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchDown(GameTouch e) {
		
	}

	@Override
	public void touchUp(GameTouch e) {
		debug(e.getX()+","+e.getY());
	}

	@Override
	public void touchMove(GameTouch e) {

		System.out.println("move");

	}

	@Override
	public void touchDrag(GameTouch e) {

		System.out.println("drag");

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {

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
