package com.example.ptest;

import loon.action.sprite.SpriteBatch;
import loon.core.LSystem;
import loon.core.input.LTouch;
import loon.core.timer.LTimerContext;
import loon.physics.PhysicsObject;
import loon.physics.PhysicsScreen;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Test1 extends PhysicsScreen {

	public Test1() {
		// x轴重力为0,y轴重力为0.2,重力自然衰弱（不循环）
		super(LSystem.screenRect, 0, 0.2f, true);
	}

	public void onLoad() {
		PhysicsObject o = bindTo("assets/a4.png", BodyType.DynamicBody, 50, 0);
		o.density.set(2.0f);
		o.friction.set(0.2f);
		o.make();
	}

	public void paint(SpriteBatch g) {

	}

	public void update(LTimerContext t) {

	}

	public void onDown(LTouch e) {
		// 获得指定位置,且标记为"Ball"的物理精灵
		PhysicsObject ball = find(getTouchX(), getTouchY(), "Ball");
		if (ball != null) {
			// 存在则删除
			removeObject(ball);
		} else {
			// 添加一个圆形的物理精灵,到触摸屏点击区域，大小为64x64
			ball = bindTo(Circle, BodyType.DynamicBody, getTouchX(),
					getTouchY(), 64, 64);
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

	public void onMove(LTouch e) {

	}

	public void onUp(LTouch e) {

	}

	public void onDrag(LTouch e) {

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

}
