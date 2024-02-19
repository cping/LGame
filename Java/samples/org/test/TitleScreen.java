package org.test;

import loon.Stage;
import loon.component.LButton;
import loon.component.LComponent;
import loon.component.LPaper;
import loon.events.ActionUpdate;

public class TitleScreen extends Stage {

	public void create() {

		final LComponent start, end;

		final LPaper title;

		setBackground("assets/avg/back1.png");

		// 构建开始按钮,大小191x57拆分图片,默认禁止使用,坐标[2,5],按下并离开时触发事件
		add(start = new LButton("assets/avg/title_start.png", 191, 57).up((float x, float y) -> {
			// 随机使用一个Screen替换效果
			replaceScreen(new MyAVGScreen());
			// 使用固定Screen过渡效果
			// replaceScreen(new MyAVGScreen(), PageMethod.ZoomOut);
			// 使用单纯Screen移动效果
			// replaceScreen(new MyAVGScreen(),MoveMethod.FROM_LEFT);
		}).setEnabled(false).coord(2, 5));

		LComponent btn2 = new LButton("assets/avg/title_load.png", 160, 56).setEnabled(false).coord(2,
				start.getY() + start.getHeight() + 20);
		add(btn2);

		LComponent btn3 = new LButton("assets/avg/title_option.png", 215, 57).setEnabled(false).coord(2,
				btn2.getY() + btn2.getHeight() + 20);
		add(btn3);

		add(end = new LButton("assets/avg/title_end.png", 142, 57).setEnabled(false).coord(2,
				btn3.getY() + btn3.getHeight() + 20));

		add(title = new LPaper("assets/avg/title.png", -200, 0));

		addProcess(new ActionUpdate() {

			@Override
			public void action(Object a) {
				if (title.getScreenX() + title.getWidth() + 25 <= getWidth()) {
					//加入时间轴修的移动(比如60帧下为3,30帧时开启fps_time_fixed参数修正,这个数值会自动变成6,以此类推)
					title.move_right(toFixed(3));
				} else {
					// 允许使用开始与结束按钮
					start.setEnabled(true);
					end.setEnabled(true);
				}
			}

			@Override
			public boolean completed() {
				return start.isEnabled() && end.isEnabled();
			}
		});

	}
}
