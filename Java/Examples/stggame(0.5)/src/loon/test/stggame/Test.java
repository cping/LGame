package loon.test.stggame;

import loon.LTransition;
import loon.canvas.LColor;
import loon.component.LButton;
import loon.event.GameTouch;
import loon.event.SysKey;
import loon.opengl.GLEx;
import loon.stg.STGScreen;

public class Test extends STGScreen {

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	public Test(String path) {
		// 需要读取的脚本文件
		super(path);

	}

	public void loadDrawable(DrawableVisit bitmap) {

		// 注入图像到STGScreen(内部会形成单幅纹理，ID即插入顺序)
		bitmap.add("assets/hero0.png");
		bitmap.add("assets/hero1.png");
		bitmap.add("assets/hero2.png");
		bitmap.add("assets/shot.png");
		bitmap.add("assets/boom.png");
		bitmap.add("assets/ghost.png");
		bitmap.add("assets/boss.png");
		bitmap.add("assets/greenfire.png");
		bitmap.add("assets/bee.png", 48, 48);
		bitmap.add("assets/moon1.png");
		bitmap.add("assets/moon2.png");
		bitmap.add("assets/moon3.png");
		bitmap.add("assets/moon4.png");
		// 设定背景为星空图（绘制产生）
		setStarModeBackground(LColor.white);
		// 设定滚屏背景图片
		// setScrollModeBackground("assets/background.png");
		// 设定无背景(无设定时默认为此)
		//setNotBackground();
	}

	/**
	 * 游戏脚本监听（返回true时强制中断脚本，也可于此自定义游戏脚本）
	 */
	public boolean onCommandAction(String cmd) {
		return false;
	}

	/**
	 * 指定的图像ID监听（用于渲染指定ID对应的图像）
	 */
	public boolean onDrawPlane(GLEx g, int id) {
		return false;
	}

	/**
	 * 游戏主循环（位于循环线程中）
	 */
	public void onGameLoop() {

	}

	/**
	 * 当脚本读取完毕时，将触发此函数
	 */
	public void onCommandAchieve() {

	}

	/**
	 * 当主角死亡时
	 */
	public void onHeroDeath() {
		System.out.println("over");
	}

	/**
	 * 当敌兵被清空时
	 */
	public void onEnemyClear() {

	}

	public void onLoading() {
		LButton btn = new LButton("assets/button.png") {
			public void downClick() {

				setKeyDown(SysKey.ENTER);
			}

			public void upClick() {

				setKeyUp(SysKey.ENTER);

			}
		};
		bottomOn(btn);
		btn.setLocation(getWidth() - btn.getHeight() - 25, btn.getY() - 25);
		add(btn);
		// 禁止此按钮影响STG触屏事件
		addTouchLimit(btn);
	}


	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void update(long elapsedTime) {
		
		
	}

	@Override
	public void onDown(GameTouch e) {
		
		
	}

	@Override
	public void onMove(GameTouch e) {
		
		
	}

	@Override
	public void onUp(GameTouch e) {
		
		
	}

	@Override
	public void touchDrag(GameTouch e) {
		
		
	}

}
