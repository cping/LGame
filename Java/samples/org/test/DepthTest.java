package org.test;

import loon.Stage;
import loon.action.sprite.Entity;
import loon.canvas.Canvas;
import loon.canvas.Image;

public class DepthTest extends Stage {


	@Override
	public void create() {

		//设置layer层级
		int[] depths = { 0, -1, 1, 3, 2, -4, -3, 4, -2 };
		//设置填充色
		int[] fills = { 0xFF99CCFF, 0xFFFFFF33, 0xFF9933FF, 0xFF999999,
				0xFFFF0033, 0xFF00CC00, 0xFFFF9900, 0xFF0066FF, 0x0FFCC6666 };
		int size = depths.length;
		int width = 100, height = 60;
		for (int i = 0; i < size; i++) {
			int depth = depths[i];
			//创建图片
			Image img = Image.createImage(width, height);
			Canvas canvas = img.getCanvas();
			//设置颜色
			canvas.setFillColor(fills[i]).fillRect(0, 0, width, height);
			canvas.setFillColor(0xFF000000).drawText(depth + "/" + i, 5, 15);
			//添加图层
			Entity layer = new Entity(canvas.toTexture());
			//设置图层层级，并改变显示位置
			layer.setZOrder(depth);
			layer.setLocation(200 - 50 * depth,
					125 + 25 * depth);
			//添加layer到screen
			add(layer);
			//注册此资源，当screen注销时释放
			putRelease(layer);
		}
		

		add(MultiScreenTest.getBackButton(this,0));
	}

}
