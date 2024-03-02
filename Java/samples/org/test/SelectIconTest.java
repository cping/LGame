package org.test;

import loon.Stage;
import loon.action.sprite.Sprite;
import loon.canvas.LColor;
import loon.component.LClickButton;
import loon.component.LSelectorIcon;

/**
 * 选择区域设定示例
 */
public class SelectIconTest extends Stage {

	@Override
	public void create() {
		background("back1.png");
		final LClickButton back = MultiScreenTest.getBackButton(this, 0);
		// 构建选择区域标记用类,大小32x32,默认蓝色
		final LSelectorIcon selectIcon = new LSelectorIcon(0, 0, 32, LColor.blue);
		// 设定icon图片(仅无布局设置时生效)
		// selectIcon.setTexture("ccc.png");
		// 禁止标记闪烁
		// selectIcon.setFlashAlpha(false);
		// 绘制边界线
		// selectIcon.setDrawBorder(false);
		// 网格间边缘色
		selectIcon.setBorderColor(LColor.yellow);
		// 以字符串设定选择区域网格格式布局(不设定默认仅有一格)
		selectIcon.setGridLayout(
				  "  0  ", 
				  " 202 ", 
				  "01110", 
				  "01310",
		          "01110", 
		          " 202 ", 
		          "  0  ");		
		// 绑定字符和色彩关系
		selectIcon.bindColor(0, LColor.red);
		selectIcon.bindColor(1, LColor.green);
		selectIcon.bindColor(2, LColor.blue);
		selectIcon.bindColor(3, LColor.yellow);
		// 绑定字符和图片关系
		selectIcon.bindImage(3, "ccc.png");
		// 设置为图片的区域不参与颜色渲染
		// selectIcon.setDrawImageNotColored(true);
		add(selectIcon);
		final Sprite s = node("sprite", "ball.png", 45, 45);
		up((x, y) -> {
			selectIcon.setLocation(x, y);
			println("碰撞:" + selectIcon.intersects(s));
		});
		centerOn(selectIcon);
		add(back);

	}

}
