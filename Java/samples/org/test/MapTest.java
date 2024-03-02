package org.test;

import loon.LSysException;
import loon.Stage;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.action.MoveTo;
import loon.action.map.Config;
import loon.action.map.TileMap;
import loon.action.sprite.AnimatedEntity;
import loon.action.sprite.Arrow;
import loon.action.sprite.MoveControl;
import loon.action.sprite.effect.RippleEffect;
import loon.action.sprite.effect.RippleEffect.Model;
import loon.canvas.LColor;
import loon.component.LClickButton;
import loon.component.LPad;
import loon.component.LSelectorIcon;
import loon.events.SysKey;
import loon.events.Touched;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class MapTest extends Stage {

	// 此示例演示了三种角色的地图移动方式，分别是触屏移动,键盘移动,以及虚拟按钮移动,并且有移动路径箭头显示精灵的使用
	@Override
	public void create() {
		try {
			// 点击处波纹效果
			RippleEffect ripple = RippleEffect.at(Model.OVAL);
			// 红色
			ripple.setColor(LColor.red);
			// 设置一个高的z值,避免被精灵遮挡
			ripple.setZ(100);
			add(ripple);
			// 构建数组地图精灵
			final TileMap map = new TileMap("assets/rpg/map.txt", 32, 32);
			// 设置切图方式
			/*
			 * TArray<LTexturePackClip> clips = new TArray<LTexturePackClip>(10); //
			 * 索引,名称,开始切图的x,y位置,以及切下来多少 clips.add(new LTexturePackClip(0, "1", 0, 0, 32,
			 * 32)); clips.add(new LTexturePackClip(1, "2", 32, 0, 32, 32)); clips.add(new
			 * LTexturePackClip(2, "3", 64, 0, 32, 32)); clips.add(new LTexturePackClip(3,
			 * "4", 96, 0, 32, 32)); clips.add(new LTexturePackClip(4, "5", 128, 0, 32,
			 * 32)); clips.add(new LTexturePackClip(5, "6", 160, 0, 32, 32)); //
			 * 注入切图用地图，以及切图方式(也可以直接注入xml配置文件) map.setImagePack("assets/rpg/map.png", clips);
			 */
			// 按照瓦片规格自动获取地图切片(切出来大小都是一样的,只对规则图片有效)
			map.setImagePackAuto("assets/rpg/map.png", 32, 32);
			// 执行切图
			map.pack();

			// 设置数组瓦片索引id和切图id的绑定关系(不设置时按照setImagePack中注入的切图id自动和地图id匹配)
			/*
			 * map.putTile(0, 0); map.putTile(1, 1); map.putTile(2, 2); map.putTile(3, 3);
			 * map.putTile(4, 4); map.putTile(5, 5);
			 */
			// 注入地图到窗体
			add(map);

			// 制作动画角色,切分大小32x32每帧,显示位置到坐标3,4(换算为数组地图位置),显示大小32x32
			final AnimatedEntity hero = new AnimatedEntity("assets/rpg/hero.gif", 32, 32, map.tilesToPixelsX(3),
					map.tilesToPixelsY(4), 32, 32);
			// 播放动画,速度每帧220
			final long[] frames = { 220, 220, 220 };
			// 左右下上四方向的帧播放顺序(也可以理解为具体播放的帧)
			final int[] leftIds = { 3, 4, 5 };
			final int[] rightIds = { 6, 7, 8 };
			final int[] downIds = { 0, 1, 2 };
			final int[] upIds = { 9, 10, 11 };
			// 也可以这样设置，播放时直接传入key的字符串数值，两种方式都能生效
			/*
			 * hero.setPlayIndex("left", PlayIndex.at(frames,leftIds));
			 * hero.setPlayIndex("right", PlayIndex.at(frames,rightIds));
			 * hero.setPlayIndex("down", PlayIndex.at(frames,downIds));
			 * hero.setPlayIndex("up", PlayIndex.at(frames,upIds));
			 */
			// 播放动画,速度每帧220,播放顺序为第0,1,2帧
			// hero.animate(new long[]{220, 220, 220 }, new int[]{0, 1, 2});
			hero.animate(frames, downIds);
			// 限制精灵到索引1,2,3,5位置的移动
			map.setLimit(new int[] { 1, 2, 3, 5 });
			// 设置一个高的z值,避免被精灵遮挡
			hero.setZ(100);
			// 让地图追随hero
			map.followAction(hero);
			// 矫正hero的显示位置
			hero.setOffset(map.getOffset());
			// 添加hero到地图上
			add(hero);

			// 构建标记绘制器
			final LSelectorIcon selector = new LSelectorIcon(0, 0, 32);
			// 标记布局如下
			selector.setGridLayout("  0  ", 
					               " 010 ", 
					               "  0  ");
			// 绑定索引与颜色
			selector.bindColor(0, LColor.red);
			selector.bindColor(1, LColor.yellow);
			// 不绘制边框
			selector.setDrawBorder(false);
			// 跟随hero偏移
			selector.setOffset(hero.getOffset());
			// 移动到hero位置
			selector.moveTo(hero);

			add(selector);

			// 角色追随和地图滚动只能开一个(否则地图移动视角会乱跳),默认如果followAction注入则scroll无效化
			/*
			 * drag(new Touched() {
			 *
			 * @Override public void on(float x, float y) { map.scroll(x, y); } });
			 */
			// 构建一个箭头精灵,使用图片arrow.png,箭头原图每格占16像素
			final Arrow arrow = new Arrow("arrow.png", 16);
			// 使用默认的模式1拆分箭头(如果使用其他图片,请自行设置14个移动元素对应的具体位图)
			arrow.getArrowSet().defaultSet1();
			// 坐标跟随地图偏移
			arrow.setOffset(map.getOffset());
			// 箭头变为红色
			arrow.setColor(LColor.red);
			// 注入箭头精灵
			add(arrow);

			/**
			 * final Arrow arrow = new Arrow(TextureUtils.filterColor("assets/icon.png", new
			 * LColor(255, 0, 255)), 32); // 使用默认的模式2拆分箭头(如果使用其他图片,请自行设置14个移动元素对应的具体位图)
			 * arrow.getArrowSet().defaultSet2(); // 坐标跟随地图偏移
			 * arrow.setOffset(map.getOffset()); // 注入箭头精灵 add(arrow);
			 */

			// ----触屏移动---
			// 监听窗体down事件
			up(new Touched() {

				@Override
				public void on(final float x, final float y) {

					// 角色缓动动画移动,以map中二维数组为基础,4方走法,每次移动增加8个像素(并且矫正地图的偏移位置,否则直接获得屏幕触点肯定错误),速度8(实际就是一次移动几个像素格)
					final MoveTo move = new MoveTo(map.getField2D(), map.offsetXPixel(hero.x()),
							map.offsetYPixel(hero.y()), map.offsetXPixel(x), map.offsetYPixel(y), false, 8);

					// 额外查询一次移动目标，用以显示移动箭头精灵
					TArray<Vector2f> path = move.findPathBegin(map.offsetXPixel(hero.getDrawX()),
							map.offsetYPixel(hero.getDrawY()), false);

					// 如果可以获得移动数据
					if (path.size > 0) {
						// 变更移动箭头数据
						arrow.updatePath(path);
						// 监听MoveTo
						move.setActionListener(new ActionListener() {

							@Override
							public void stop(ActionBind o) {
								// 移动箭头隐藏
								arrow.setVisible(false);
								// 1/5(20/100)遇敌率
								if (MathUtils.chanceRoll(20)) {
									// 随机使用一种Screen转场效果，进入战斗画面
									gotoScreenEffectExitRand(new RpgBattleTest());
								}

								//改变标记显示位置
								selector.moveTo(o);
							}

							@Override
							public void start(ActionBind o) {
								// 显示移动箭头
								arrow.setVisible(true);
							}

							@Override
							public void process(ActionBind o) {
								// 存储上一个移动方向，避免反复刷新动画事件
								if (move.isDirectionUpdate()) {
									switch (move.getDirection()) {
									case Config.TUP:
										hero.animate(frames, upIds);
										break;
									default:
									case Config.TDOWN:
										hero.animate(frames, downIds);
										break;
									case Config.TLEFT:
										hero.animate(frames, leftIds);
										break;
									case Config.TRIGHT:
										hero.animate(frames, rightIds);
										break;
									}
								}
							}
						});
						// 开始缓动动画
						hero.selfAction().event(move).start();
					}
				}
			});

			// 构架移动控制器,注入控制的角色和二维数组
			final MoveControl mc = new MoveControl(hero, map);
			mc.start();
			// 注销窗体时关闭移动控制器
			putRelease(mc);
			// ----按键移动---
			// 构建键盘监听

			keyPress(SysKey.LEFT, () -> {
				if (!mc.isTLeft()) {
					hero.animate(frames, leftIds);
				}
				mc.setDirection(Config.TLEFT);
			});

			keyPress(SysKey.RIGHT, () -> {
				if (!mc.isTRight()) {
					hero.animate(frames, rightIds);
				}
				mc.setDirection(Config.TRIGHT);
			});

			keyPress(SysKey.UP, () -> {
				if (!mc.isTUp()) {
					hero.animate(frames, upIds);
				}
				mc.setDirection(Config.TUP);
			});

			keyPress(SysKey.DOWN, () -> {
				if (!mc.isTDown()) {
					hero.animate(frames, downIds);
				}
				mc.setDirection(Config.TDOWN);
			});

			// ----虚拟按键移动---
			// 创建控制按钮
			final LPad pad = new LPad(25, 150);
			// 禁止窗体触屏区域包含pad
			// addTouchLimit(pad);
			// 监听事件
			pad.setListener(new LPad.ClickListener() {

				@Override
				public void up() {
					// 只有上一次操作不是同方向时,才执行动画切换,以下同(否则会重复刷新动画状态到初始,也就是老在每次设置的第一帧跑)
					if (!pad.isLastUp()) {
						hero.animate(frames, upIds);
					}
					mc.setDirection(Config.TUP);
				}

				@Override
				public void right() {
					if (!pad.isLastRight()) {
						hero.animate(frames, rightIds);
					}
					mc.setDirection(Config.TRIGHT);
				}

				@Override
				public void other() {
				}

				@Override
				public void left() {
					if (!pad.isLastLeft()) {
						hero.animate(frames, leftIds);
					}
					mc.setDirection(Config.TLEFT);
				}

				@Override
				public void down() {
					if (!pad.isLastDown()) {
						hero.animate(frames, downIds);
					}
					mc.setDirection(Config.TDOWN);
				}
			});
			add(pad);
		} catch (LSysException e) {
			error(e.getMessage());
		}

		LClickButton click = MultiScreenTest.getBackButton(this, 1);
		// 禁止触屏点击到click位置，也就是防止点击back时自动寻径
		// addTouchLimit(click);
		add(click);
		// 插入网格GridEntity
		// add(new GridEntity());
	}

}
