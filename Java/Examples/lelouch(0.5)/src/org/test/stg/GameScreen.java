package org.test.stg;

import loon.LTexture;
import loon.LTextures;
import loon.Screen;
import loon.component.Actor;
import loon.component.ActorSpeed;
import loon.component.LButton;
import loon.component.LLayer;
import loon.component.LPad;
import loon.event.GameTouch;
import loon.event.SysInput;
import loon.event.SysKey;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.Speed;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public class GameScreen extends Screen {

	/**
	 * 杂兵用类
	 */
	class Enemy extends ActorSpeed {

		private boolean exploded;

		private int health;

		public Enemy(LTexture image) {
			super(new Speed(MathUtils.nextInt(360), 2f));
			setEnemy(image, image.getWidth());
			setFlag("Enemy");
			// Action动作延迟50毫秒执行
			setDelay(30);
		}

		@Override
		public void action(long t) {
			if (this.exploded) {
				return;
			}
			move();
		}

		public void setEnemy(LTexture image, int size) {
			this.health = size;
			this.setImage(image);
		}

		private void explode() {
			if (this.exploded) {
				return;
			}
			this.exploded = true;
			getLLayer().removeObject(this);
		}

		public void hit(int damage) {
			if (this.exploded) {
				return;
			}
			this.health -= damage;
			if (this.health <= 0) {
				explode();
			}
		}
	}

	/**
	 * 子弹用类
	 */
	class Fire extends ActorSpeed {

		private int damage = 100;

		private int life = 35;

		public Fire(Speed speed, LTexture image, float rotation) {
			super(speed);
			setImage(image);
			setRotation(rotation);
			setDelay(30);
			increaseSpeed(new Speed(rotation, 7f));
		}

		@Override
		public void action(long elapsedTime) {
			// 查询子弹生命（持久力）是否耗尽
			if (this.life <= 0) {
				getLLayer().removeObject(this);
				// 查询子弹是否超过边界
			} else if (!getLLayer().getCollisionBox().contains(getRectBox())) {
				getLLayer().removeObject(this);
			} else {
				// 移动子弹
				move();
				// 获得杂兵是否与子弹碰撞，返回唯一值（第一个查询到的）
				// PS:此仅为矩形碰撞，更精确可用SpriteImage取Polygon比对
				Enemy e = (Enemy) getOnlyCollisionObject("Enemy");
				if (e != null) {

					getLLayer().removeObject(this);
					e.hit(this.damage);
				}
				this.life -= 1;
			}
		}
	}

	/**
	 * 主角用类
	 */
	class Hero extends ActorSpeed {

		private int minGunFireDelay;

		private int gunFireDelay;

		private Speed acceleration = new Speed(0, 0);

		private LTimer timer = new LTimer(100);

		private LTexture fireImage;

		public Hero(LTexture image) {
			this.setImage(image);
		}

		public void setMinGunFireDelay(int delay) {
			this.minGunFireDelay = delay;
		}

		public void setAcceleration(int direction, float length) {
			acceleration.set(direction, length);
		}

		public void action(long elapsedTime) {
			SysInput input = getLLayer().screenInput();
			if (input.isKeyPressed(SysKey.SPACE)) {
				fire();
			}
			if (timer.action(elapsedTime)) {
				// 根据左右摇摆来旋转角色
				if (input.isKeyPressed(SysKey.LEFT)) {
					setRotation(getRotation() - 5);
				}
				if (input.isKeyPressed(SysKey.RIGHT)) {
					setRotation(getRotation() + 5);
				}
				move();
			}
			this.gunFireDelay += 1;
		}

		private void fire() {
			if (this.gunFireDelay >= this.minGunFireDelay) {
				Fire h = new Fire(getSpeed().copy(), fireImage, getRotation());
				getLLayer().addObject(h, getX(), getY());
				h.move();
				this.gunFireDelay = 0;
			}
		}

		public LTexture getFireImage() {
			return fireImage;
		}

		public void setFireImage(LTexture fireImage) {
			this.fireImage = fireImage;
		}
	}

	public void onLoad() {

		// 构建一个等于Screen大小的图层，不锁定角色移动范围(为true时将刚性制约角色坐标为Layer内,
		// 无论拖拽还是set坐标都无法超出)
		final LLayer layer = new LLayer(getWidth(), getHeight(), false);

		// 杂兵出现的边界位置
		final int enemyWidth = getWidth() - 50;
		final int enemyHeight = getHeight() - 50;

		// 杂兵1使用的图形大小
		final int e1size = 68;
		// 主角机图片
		final LTexture a1 = LTextures.loadTexture("a1.png");
		// 敌人图片
		final LTexture e1 = LTextures.loadTexture("e1.png").scale(e1size,
				e1size);
		// 子弹图片
		final LTexture f1 = LTextures.loadTexture("f1.png");

		// 此项参数决定了Layer拖拽是否受到限制；当Layer小于Screen大小时，Layer拖拽无法
		// 超过Screen的显示范围；当Layer大于Screen大小时，Screen仅允许拖拽Layer能够被
		// 显示出来的部分(比如设定一个800x480的Layer在480x320的Screen中，那么无论如何
		// 拖拽也仅可见800x480范围内的Layer画面，而不会出现黑边),默认此项开启。
		// PS:与标准LGame组件一样，Layer能被拖拽的大前提是setLocked项必须设为false
		// layer.setLimitMove(false);
		// layer.setLocked(false);

		// 此项参数决定了Layer中Actor角色是否能被直接拖拽，默认为true，即可以直接拖拽
		// layer.setActorDrag(false);

		// 此项参数决定了Layer中Actor角色是否响应鼠标点击，默认为true，即可以直接获得点击事件
		// layer.setMouseClick(false);

		// 为Layer加载背景
		layer.setBackground("back.png");

		// 构建按钮1，点击时触发【狼与车夫】机体的战斗事件
		LButton button1 = new LButton("b1.png") {
			public void downClick() {
				// 获得Button所在Layer
				LLayer superLayer = getTopLayer();

				// 清空Layer中所有LButton按钮
				superLayer.removeUIName("Button");

				// 构建【狼与车夫】机体
				Hero hero = new Hero(a1);
				hero.setFireImage(f1);
				hero.setAcceleration(0, 0.05f);
				hero.setMinGunFireDelay(70);

				// 添加主角机并居中
				superLayer.addObject(hero);
				superLayer.centerOn(hero);

				// 在Layer上设置杂兵
				superLayer.addObject(new Enemy(e1), enemyWidth, enemyHeight);
				superLayer.addObject(new Enemy(e1), enemyWidth, 50);
				superLayer.addObject(new Enemy(e1), 50, enemyHeight);
				superLayer.addObject(new Enemy(e1), 50, 50);
				superLayer.addObject(new Enemy(e1), 50, enemyHeight);
				superLayer.addObject(new Enemy(e1), 300, 50);
				superLayer.addObject(new Enemy(e1), 300, enemyHeight);
				superLayer.addObject(new Enemy(e1), enemyWidth, 300);

				// 制作一个固定位置的角色，充当Layer内按钮
				Actor click = new Actor("b2.png") {

					public void downClick(int x, int y) {
						// 当点击此角色时，伪造space事件给Layer（子弹发射）
						setKeyDown(SysKey.SPACE);
					}

					public void upClick(int x, int y) {
						// 释放space事件
						setKeyUp(SysKey.SPACE);
					}
				};
				// 此角色无视拖拽事件
				click.setDrag(false);
				superLayer.addObject(click, 10, 260);

				// 层级100（loon设定中，层级越高越靠前）
				LPad pad = new LPad(300, 100);
				pad.setListener(new LPad.ClickListener() {

					@Override
					public void up() {
						setKeyDown(SysKey.LEFT);
					}

					@Override
					public void down() {
						setKeyDown(SysKey.RIGHT);
					}

					@Override
					public void left() {
						setKeyDown(SysKey.LEFT);
					}

					@Override
					public void right() {
						setKeyDown(SysKey.RIGHT);
					}

					@Override
					public void other() {
						setKeyUp(SysKey.LEFT);
						setKeyUp(SysKey.RIGHT);
					}

				});
				pad.setLayer(0);
				add(pad);
				pad.setLayer(100);

			}
		};

		// 居中显示按钮1
		layer.centerOn(button1);

		// 在Layer中添加按钮1
		layer.add(button1);
		// 层级0
		layer.setLayer(0);
		// 将Layer添加到Screen当中
		add(layer);

	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void alter(LTimerContext timer) {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {

	}

	@Override
	public void touchUp(GameTouch e) {

	}

	@Override
	public void touchMove(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void close() {

	}

}
