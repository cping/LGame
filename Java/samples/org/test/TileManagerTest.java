/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package org.test;

import loon.LTexture;
import loon.Screen;
import loon.Stage;
import loon.action.map.AStarFindHeuristic;
import loon.action.map.AStarFinder;
import loon.action.map.colider.TileImpl;
import loon.action.map.colider.TileManager;
import loon.action.map.colider.TileManager.TileDrawListener;
import loon.action.sprite.AnimatedEntity;
import loon.event.DrawListener;
import loon.event.Touched;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.TArray;

public class TileManagerTest extends Stage {

	@Override
	public void create() {
		final AnimatedEntity hero = new AnimatedEntity("assets/rpg/hero.gif", 32, 32,50,
				50, 32, 32);
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
		// 设置一个高的z值,避免被精灵遮挡
		hero.setZ(100);
		// 添加hero到地图上
		add(hero);
		
		final TileManager manager = new TileManager(AStarFinder.ASTAR_MANHATTAN,getWidth(),getHeight());
		
		manager.put(new TileImpl(0, 80,100));
		manager.put(new TileImpl(0, 280,100));
		
		final LTexture tex = loadTexture("assets/ccc.png");
		
		up(new Touched() {
			
			@Override
			public void on(float x, float y) {
				TArray<TileImpl> path = manager.findMovePath(hero.x(), hero.y(), (int)x, (int)y, true);
				System.out.println(path);
			}
		});
		
		manager.setTileListener(new TileDrawListener<TileImpl>() {
			
			@Override
			public void update(long elapsedTime, TileImpl tile) {
			}
			
			@Override
			public void draw(GLEx g, TileImpl tile, float x, float y) {
				if(tile!=null){
					g.draw(tex, tile.getX(), tile.getY());
				}
			
	
			}
		});
		
		setDrawListener(new DrawListener<Screen>() {

			@Override
			public Screen update(long elapsedTime) {
				manager.updateTiles(elapsedTime);
				return null;
			}

			@Override
			public Screen draw(GLEx g, float x, float y) {
				manager.drawTiles(g, x, y);
				return null;
			}
		});
	}

}
