package com.example.ptest;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import loon.core.geom.RectBox;
import loon.core.graphics.LColor;
import loon.core.graphics.LComponent;
import loon.core.graphics.Screen;
import loon.core.graphics.component.ClickListener;
import loon.core.graphics.opengl.GLEx;
import loon.core.input.LTouch;
import loon.core.timer.LTimerContext;
import loon.physics.PhysicsLayer;
import loon.physics.PhysicsObject;

public class Test2 extends Screen {

	public Test2() {

	}

	public void onLoad() {

		// 创建一个物理游戏用层（与Screen不同，层是可自定义大小，可拖拽且能自由移动的）
		PhysicsLayer layer = new PhysicsLayer(new RectBox(0, 0, 300, 300), 0,
				0.2f, true);
		PhysicsObject o = layer.bindTo("assets/a4.png", BodyType.DynamicBody,
				50, 0);
		o.density.set(2.0f);
		o.friction.set(0.2f);
		o.make();

		// 不限制拖拽范围（为true时不能拖拽出Screen画面）
		layer.setLimitMove(false);

		ClickListener click = new ClickListener() {

			public void UpClick(LComponent comp, float x, float y) {

			}

			public void DragClick(LComponent comp, float x, float y) {

			}

			public void DownClick(LComponent comp, float x, float y) {
				PhysicsLayer layer = (PhysicsLayer) comp;
				PhysicsObject ball = layer.find((int) x, (int) y, "Ball");
				if (ball != null) {
					// 存在则删除
					layer.removeObject(ball);
				} else {
					// 添加一个圆形的物理精灵,到触摸屏点击区域，大小为64x64
					ball = layer.bindTo(PhysicsLayer.Circle,
							BodyType.DynamicBody, (int) x, (int) y, 64, 64);
					// 标记为Ball（便于查找）
					ball.setTag("Ball");
					ball.setDrawImage("assets/ball.png");
					// 设定图片(请注意，当为固定形状注入图片时，图片大小必须为设定大小，
					// 否则会发生图片位置与物理位置不匹配的情况)
					ball.setBitmapFilter(true);
					ball.density.set(2.0f);
					ball.friction.set(0.2f);
					ball.make();

				}

			}
		};
		layer.Click = click;
		// 不固定位置（允许拖拽）
		layer.setLocked(false);
		// 背景红色
		layer.setBackground(LColor.red);
		add(layer);
	}

	@Override
	public void draw(GLEx g) {
		// TODO Auto-generated method stub

	}

	@Override
	public void alter(LTimerContext timer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchDown(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchUp(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchMove(LTouch e) {
		// TODO Auto-generated method stub

	}

}
