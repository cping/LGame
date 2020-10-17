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

import loon.Stage;
import loon.action.map.HexagonMap;
import loon.component.LClickButton;
import loon.events.Touched;

public class HexagonMapTest extends Stage {

	@Override
	public void create() {
		// 构建地图
		final HexagonMap map = new HexagonMap();
		//创建一个瓦片为32,32,16的六边形(宽64,高80),以6x6(6行6列)为地图范围的地图
		map.createMap(32, 32, 16, 6, 6);
		// 填充所有瓦片为id:1
		map.fillTiles(1);
		// 单独设定瓦片坐标1,2位置为索引2的瓦片
		map.setTile(1, 2, 2);
		// 设定寻径限制,让索引2为不许移动的瓦片索引(也就是上面让索引2的瓦片不可移动(寻径)),不限制则可以移动
		map.setLimit(new int[] { 2 });
		/*六边形地图在这里设置,没有会显示六边形彩块
		// 设置切图方式
		TArray<LTexturePackClip> clips = new TArray<LTexturePackClip>(10);
		// 索引,名称,开始切图的x,y位置,以及切下来多少
		clips.add(new LTexturePackClip(0, "1", 0, 0, 32, 32));
		clips.add(new LTexturePackClip(1, "2", 32, 0, 32, 32));
		clips.add(new LTexturePackClip(2, "3", 64, 0, 32, 32));
		clips.add(new LTexturePackClip(3, "4", 96, 0, 32, 32));
		clips.add(new LTexturePackClip(4, "5", 128, 0, 32, 32));
		clips.add(new LTexturePackClip(5, "6", 160, 0, 32, 32));
		// 注入切图用地图，以及切图方式(也可以直接注入xml配置文件)
		map.setImagePack("assets/hex.png", clips);
		// 执行切图
		map.pack();*/
		//强行魔改一个非六边形图片为[符合初始设定六边形大小]的六边形,并设定为id:2
		map.putTileAutoHexagon(2, "assets/block.png");
		// 限制显示区域
		// map.setViewRect(getViewRect());
		
		// 添加索引位置标识(不填写开始路径时,寻径以此为基准)
		map.setPositionFlag(2, 4);
		// 允许在没有具体图片时,显示瓦片的占位(也就是用临时的瓦片图显示出瓦片)
		map.setAllowDisplayPosition(true);
		// 允许显示寻径结果(显示出不同颜色的瓦片)
		map.setAllowDisplayFindPath(true);
		// 显示特殊位置标识
		map.setAllowDisplayFlag(true);
		// 显示坐标文字(不用BMFont时是资源大户,轻易勿开)
		map.setAllowDisplayPosText(true);
		down(new Touched() {

			@Override
			public void on(float x, float y) {
				// 从flag点开始寻径点击坐标
				map.findPath(x, y);
			}
		});
		//居中地图
		centerOn(map);
		//注入地图到Screen
		add(map);
		LClickButton click = MultiScreenTest.getBackButton(this, 1);
		//禁止触屏点击到click位置，也就是防止点击back时自动寻径
		addTouchLimit(click);
		add(click);
	}

}
