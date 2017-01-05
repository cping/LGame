package org.test;

import java.io.IOException;

import loon.Stage;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.action.MoveTo;
import loon.action.map.Field2D;
import loon.action.map.TileMap;
import loon.action.sprite.AnimatedEntity;
import loon.action.sprite.effect.RippleEffect;
import loon.canvas.LColor;
import loon.event.Touched;
import loon.font.LFont;
import loon.opengl.LTexturePackClip;
import loon.utils.TArray;

public class MapTest extends Stage {

	@Override
	public void create() {
		try {
			//点击处波纹效果
			RippleEffect ripple = new RippleEffect();
			//红色
			ripple.setColor(LColor.red);
			//设置一个高的z值,避免被精灵遮挡
			ripple.setZ(100);
			add(ripple);
			// 构建数组地图精灵
			final TileMap map = new TileMap("assets/rpg/map.txt", 32, 32);
			// 设置切图方式
			TArray<LTexturePackClip> clips = new TArray<LTexturePackClip>(10);
			//索引,名称,开始切图的x,y位置,以及切下来多少
			clips.add(new LTexturePackClip(0, "1", 0, 0, 32, 32));
			clips.add(new LTexturePackClip(1, "2", 32, 0, 32, 32));
			clips.add(new LTexturePackClip(2, "3", 64, 0, 32, 32));
			clips.add(new LTexturePackClip(3, "4", 96, 0, 32, 32));
			clips.add(new LTexturePackClip(4, "5", 128, 0, 32, 32));
			clips.add(new LTexturePackClip(5, "6", 160, 0, 32, 32));
			// 注入切图用地图，以及切图方式(也可以直接注入xml配置文件)
			map.setImagePack("assets/rpg/map.png", clips);
			// 执行切图
			map.pack();
			// 设置数组瓦片索引id和切图id的绑定关系
			map.putTile(0, 0);
			map.putTile(1, 1);
			map.putTile(2, 2);
			map.putTile(3, 3);
			map.putTile(4, 4);
			map.putTile(5, 5);
			// 注入地图到窗体
			add(map);
			// 制作动画角色,切分大小32x32每帧,显示位置到坐标3,4(换算为数组地图位置),显示大小32x32
			final AnimatedEntity hero = new AnimatedEntity("assets/rpg/hero.gif", 32, 32, map.tilesToPixelsX(3),
					map.tilesToPixelsY(4), 32, 32);
			// 播放动画,速度每帧220,播放顺序为第0,1,2帧
			hero.animate(new long[] { 220, 220, 220 }, new int[] { 0, 1, 2 });
			// 限制精灵到索引1,2,3,5位置的移动
			map.setLimit(new int[] { 1, 2, 3, 5 });
			//设置一个高的z值,避免被精灵遮挡
			hero.setZ(100);
			add(hero);
			// 监听窗体up事件
			up(new Touched() {

				@Override
				public void on(final float x, final float y) {
					// 角色缓动动画移动,以map中二维数组为基础,4方走法,每次移动增加8个像素
					final MoveTo move = new MoveTo(map.getField(), x, y, false, 8);
					// 监听MoveTo
					move.setActionListener(new ActionListener() {

						private int lastDirection = -1;

						@Override
						public void stop(ActionBind o) {

						}

						@Override
						public void start(ActionBind o) {

						}

						@Override
						public void process(ActionBind o) {
							// 存储上一个移动方向，避免反复刷新动画事件
							if (lastDirection != move.getDirection() && o.getX() != x && o.getY() != y) {
								switch (move.getDirection()) {
								case Field2D.TUP:
									hero.animate(new long[] { 220, 220, 220 }, new int[] { 9, 10, 11 });
									break;
								default:
								case Field2D.TDOWN:
									hero.animate(new long[] { 220, 220, 220 }, new int[] { 0, 1, 2 });
									break;
								case Field2D.TLEFT:
									hero.animate(new long[] { 220, 220, 220 }, new int[] { 3, 4, 5 });
									break;
								case Field2D.TRIGHT:
									hero.animate(new long[] { 220, 220, 220 }, new int[] { 6, 7, 8 });
									break;
								}
								lastDirection = move.getDirection();
							}
						}
					});
					// 开始缓动动画
					hero.selfAction().event(move).start();

				}
			});
		} catch (Exception e) {
			error(e.getMessage());
		}
		LFont.setDefaultFont(LFont.getFont(20));
		add(MultiScreenTest.getBackButton(this, 1));
	}

}
