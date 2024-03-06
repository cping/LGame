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
import loon.action.map.TileIsoRect;
import loon.action.map.TileIsoRectGrid;
import loon.canvas.LColor;

/**
 * 示例类,斜角网格绘制器的使用
 */
public class IsometricGridDrawTest extends Stage {

	@Override
	public void create() {

		background("back1.png");
		// 构筑斜角瓦片区域渲染器,4行,3列,坐标0,0,大小64x64,旋转幅度0.7f(附带一提,斜角地图瓦片大小一般是不等的,
		// 比如64x32或者32x16这样旋转才能看出效果,这个示例之所以相当是为了图片示例绑定方便,否则扭曲的图片不能直接使用……)
		TileIsoRectGrid grid = new TileIsoRectGrid(4, 3, 0, 0, 64, 64, 0.7f);
		// 填充网格内部色彩
		// grid.setFill(true);
		// 网格红色
		grid.setColor(LColor.red);
		// 隐藏坐标2,1的网格
		grid.getTile(2, 1).setVisible(false);
		// 加载图片到2,2(默认会把正方形图片旋转为斜角45度绘制到图中，也可以手动设置旋转角度与图片大小)
		grid.getTile(2, 2).setImage("block.png");
		// 图片不参与任何颜色变化
		// grid.getTile(2, 2).setDrawImageNotColored(true);
		// 加载图片并设置参数,偏移2,2,图片旋转45度
		// grid.getTile(2, 2).setImage("block.png", 2,2, 45);
		// 显示坐标
		grid.setShowCoordinate(true);
		// grid.setFontColor(LColor.black);

		// 渲染到屏幕
		drawable((g, x, y) -> {
			grid.draw(g);
		});

		up((x, y) -> {
			// 转化鼠标像素到瓦片
			TileIsoRect rect = grid.pixelToTile(x, y);
			// 存在瓦片的话
			if (rect != null) {
				if (!rect.isSelected()) {
					// 选中区域黄色
					rect.setColor(LColor.yellow);
					rect.setSelected(true);
				} else {
					// 反选变红
					rect.setColor(LColor.red);
					rect.setSelected(false);
				}
			}
		});

	}

}
