package com.mygame;

import loon.LSystem;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.action.FadeTo;
import loon.action.MoveTo;
import loon.action.map.Field2D;
import loon.action.sprite.StatusBar;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.component.Actor;
import loon.component.ActorLayer;
import loon.component.LLayer;
import loon.component.LPaper;
import loon.events.FrameLoopEvent;
import loon.events.GameTouch;
import loon.font.LFont;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.ArrayMap;
import loon.utils.ConfigReader;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimerContext;

public class TDMain extends Screen {

	private int selectTurret = -1;

	private Field2D field;

	private String[] turrets = new String[] { "assets/bulletTurret.png", "assets/bombTurret.png",
			"assets/poisonTurret.png", "assets/laserTurret.png", "assets/bullet.png" };

	/**
	 * 子弹用类
	 * 
	 */
	class Bullet extends Actor {

		/* 抛物线用加速度、重力 */
		private float vx, vy, gravity;

		/* 增加一个type向，用来区分子弹类型 */
		int type;

		private float speed;

		private float dir;

		private int damage;

		private float x, y;

		private boolean removeFlag;

		public Bullet(int type, String fileName, float dir, int damage) {
			this.setObjectFlag("Bullet");
			this.type = type;
			this.dir = dir;
			this.damage = damage;
			this.setImage(fileName);
			this.setDelay(50);
			/* 如果子弹类型为抛物线 */
			if (type == 1) {
				this.setDelay(0);
				this.speed = 200f;
				this.gravity = 200f;
				// 转化方向(360度方向)为弧度
				float angle = MathUtils.toRadians(this.dir);
				// 计算加速度
				this.vx = speed * MathUtils.cos(angle);
				this.vy = speed * MathUtils.sin(angle);
			}
		}

		protected void addLayer(ActorLayer layer) {
			this.x = this.getX();
			this.y = this.getY();
		}

		public void action(long t) {
			if (removeFlag) {
				return;
			}
			Object o = null;

			switch (type) {
			case 0:
				for (int i = 0; i < 6; i++) {
					// 矫正弹道位置
					float angle = MathUtils.toRadians(this.dir);
					this.x += MathUtils.cos(angle);
					this.y += MathUtils.sin(angle);
				}
				this.setLocation(this.x + (field.getTileWidth() - this.getWidth()) / 2,
						this.y + (field.getTileHeight() - this.getHeight()) / 2);
				break;
			case 1:
				/*
				 * 计算每次子弹位置(这个例子如果直接做抛物线，有一处不妥，即MapLayer的setDelay被设定成了500
				 * 也就是每隔500毫秒（半秒），才改变一次地图中物体状态，这样做出来的抛物线会不太自然。)
				 */
				x = getX();
				y = getY();
				float dt = MathUtils.max((t / 1000), 0.01f);
				vy += gravity * dt;
				x += vx * dt;
				y += vy * dt;
				this.setLocation(this.x, this.y);
				break;
			}

			o = this.getOnlyCollisionObject("Enemy");
			// 当与敌相撞时
			if (o != null) {
				Enemy e = (Enemy) o;
				// 减少敌方HP
				e.hp -= this.damage;
				e.hpBar.setUpdate(e.hp);
				removeFlag = true;
				// 从Layer中删除自身
				getLLayer().removeObject(this);

				return;
				// 超出游戏画面时删除自身
			} else if (this.getX() <= 12 || this.getX() >= this.getLLayer().getWidth() - 12 || this.getY() <= 12
					|| this.getY() >= this.getLLayer().getHeight() - 12) {
				removeFlag = true;
				this.getLLayer().removeObject(this);
			}
		}
	}

	/**
	 * 炮塔用类
	 * 
	 */
	class Turret extends Actor {

		private int range = 50;

		private int delay = 10;

		boolean selected;

		public Turret(String fileName) {
			this.setObjectFlag("Turret");
			setImage(fileName);
			setDelay(100);
			setAlpha(0);
		}

		public void addLayer(ActorLayer layer) {
			// 让角色渐进式出现
			FadeTo fade = fadeIn();
			// 监听渐进淡出事件
			fade.setActionListener(new ActionListener() {

				public void process(ActionBind o) {

				}

				public void start(ActionBind o) {

				}

				// 当渐进完毕时
				public void stop(ActionBind o) {
					// 旋转90度
					rotateTo(90);
					// 透明度还原
					setAlpha(1f);
				}

			});

		}

		public void draw(GLEx g) {
			if (selected) {
				int tint = g.color();
				g.setColor(255, 0, 0, 100);
				g.fillOval(-(range * 2 - field.getTileWidth()) / 2, -(range * 2 - field.getTileHeight()) / 2,
						this.range * 2 - 1, this.range * 2 - 1);
				g.setColor(LColor.red);
				g.drawOval(-(range * 2 - field.getTileWidth()) / 2, -(range * 2 - field.getTileHeight()) / 2,
						this.range * 2 - 1, this.range * 2 - 1);
				g.setTint(tint);
			}
		}

		public void action(long t) {
			// 遍历指定半径内所有Enemy类
			TArray<Actor> es = this.getCollisionObjects(this.range, "Enemy");
			// 当敌人存在
			if (!es.isEmpty()) {
				Enemy target = (Enemy) es.get(0);

				// 添加一个循环体,0.5秒循环一次
				addFrameLoop(0.5f, new FrameLoopEvent() {

					@Override
					public void invoke(long elapsedTime, Screen e) {
						// 当炮台完全显示出来后
						if ((getAlpha() == 1f)) {
							// 删除原有炮台事件
							removeActionEvents();
							// 删除这个循环体
							kill();
						}
					}

					@Override
					public void completed() {

					}
				});

				// 旋转炮台对准Enemy坐标
				setRotation(Field2D.rotation(Vector2f.at(this.getX(), this.getY()), Vector2f.at(target.getX(), target.getY())));

			}
			// 延迟炮击
			if (this.delay > 0) {
				--this.delay;
			} else if (!es.isEmpty()) {

				// *将子弹类型设定为1*/
				// 构造炮弹
				Bullet bullet = new Bullet(0, turrets[4], this.getRotation(), 2);

				// 计算炮击点
				int x = MathUtils
						.round(MathUtils.cos(MathUtils.toRadians(this.getRotation())) * (float) bullet.getWidth() * 2)
						+ this.x();

				int y = MathUtils
						.round(MathUtils.sin(MathUtils.toRadians(this.getRotation())) * (float) bullet.getHeight() * 2)
						+ this.y();

				// 注入炮弹到Layer
				this.getLLayer().addObject(bullet, x, y);
				this.delay = 10;

			}

		}
	}

	/**
	 * 敌兵用类
	 * 
	 */
	class Enemy extends Actor {

		private int startX, startY;

		private int endX, endY;

		private int speed, hp;

		private boolean removeFlag;

		// 使用精灵StatusBar充当血槽
		protected StatusBar hpBar;

		public Enemy(String fileName, int sx, int sy, int ex, int ey, int speed, int hp) {
			this.setObjectFlag("Enemy");
			this.setDelay(300);
			this.setImage(fileName);
			this.hpBar = new StatusBar(hp, hp, (this.width() - 25) / 2, this.height() + 5, 25, 5);
			this.startX = sx;
			this.startY = sy;
			this.endX = ex;
			this.endY = ey;
			this.speed = speed;
			this.hp = hp;
		}

		public void draw(GLEx g) {

			// 绘制精灵
			hpBar.createUI(g);

		}

		public void action(long t) {
			// 触发精灵事件
			hpBar.update(t);
			if (hp <= 0 && !removeFlag) {
				// 设定死亡时渐变
				FadeTo fade = fadeOut();
				// 渐变时间为30毫秒
				fade.setSpeed(30);
				// 监听渐变过程
				fade.setActionListener(new ActionListener() {

					public void process(ActionBind o) {

					}

					public void start(ActionBind o) {

					}

					public void stop(ActionBind o) {
						Enemy.this.removeActionEvents();
						Enemy.this.getLLayer().removeObject(Enemy.this);
					}

				});

				this.removeFlag = true;
			}
		}

		// 首次注入Layer时调用此函数
		public void addLayer(final ActorLayer layer) {

			// 坐标矫正，用以让角色居于瓦片中心
			final int offsetX = (getLLayer().getField2D().getTileWidth() - this.width()) / 2;
			final int offsetY = (getLLayer().getField2D().getTileWidth() - this.height()) / 2;
			// 初始化角色在Layer中坐标
			setLocation(startX + offsetX, startY + offsetY);
			// 命令角色向指定坐标自行移动(参数为false为四方向寻径，为true时八方向)，并返回移动控制器
			// PS:endX与endY非显示位置，所以不必矫正
			final MoveTo move = moveTo(endX, endY, false);

			// 矫正坐标，让角色居中
			move.setOffset(offsetX, offsetY);
			// 启动角色事件监听
			move.setActionListener(new ActionListener() {
				// 截取事件进行中数据
				public void process(ActionBind o) {
					// 获得角色移动方向
					switch (move.getDirection()) {
					case Field2D.TUP:
						// 根据当前移动方向，变更角色旋转方向（以下同）
						o.setRotation(270);
						break;
					case Field2D.TLEFT:
						o.setRotation(180);
						break;
					case Field2D.TRIGHT:
						o.setRotation(0);
						break;
					case Field2D.TDOWN:
						o.setRotation(90);
						break;
					default:
						break;
					}

				}

				public void start(ActionBind o) {

				}

				// 当角色移动完毕时
				public void stop(ActionBind o) {
					// 从Layer中删除此角色
					layer.removeObject((Actor) o);
				}

			});
			// 设定移动速度
			move.setSpeed(speed);
		}
	}

	// 起始点
	class Begin extends Actor {
		public Begin(String fileName) {
			setImage(fileName);
		}
	}

	// 结束点
	class End extends Actor {
		public End(String fileName) {
			setImage(fileName);
		}
	}

	/**
	 * 拖拽用菜单
	 * 
	 */
	class Menu extends LLayer {
		public Menu() {
			super(128, 240);

			// 设定menu层级高于MapLayer
			setLayer(101);
			// 不锁定menu移动
			setLocked(false);
			setLimitMove(false);
			// 锁定Actor拖拽
			setActorDrag(false);
			setDelay(500);
			// 设定Menu背景
			Image image = Image.createImage(this.width(), this.height());
			Canvas g = image.getCanvas();
			g.setColor(0, 0, 0, 125);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(LColor.white);
			g.setFont(LFont.getFont(15));
			g.drawText("我是可拖拽菜单", 12, 25);
			setBackground(image.texture());
			image.close();
			LPaper bulletTurret = new LPaper(turrets[0]) {

				// 当选中当前按钮时，为按钮绘制选中框(以下同)
				public void paint(GLEx g) {
					if (selectTurret == 0) {
						g.setColor(LColor.red);
						g.drawRect(2, 2, this.getWidth() - 4, this.getHeight() - 4);
						g.resetColor();
					}
				}

				public void downClick() {
					selectTurret = 0;
				}
			};
			bulletTurret.setLocation(18, 64);
			LPaper bombTurret = new LPaper(turrets[1]) {

				public void paint(GLEx g) {
					if (selectTurret == 1) {
						g.setColor(LColor.red);
						g.drawRect(2, 2, this.getWidth() - 4, this.getHeight() - 4);
						g.resetColor();
					}
				}

				public void downClick() {
					selectTurret = 1;
				}
			};
			bombTurret.setLocation(78, 64);
			LPaper poisonTurret = new LPaper(turrets[2]) {

				public void paint(GLEx g) {
					if (selectTurret == 2) {
						g.setColor(LColor.red);
						g.drawRect(2, 2, this.getWidth() - 4, this.getHeight() - 4);
						g.resetColor();
					}
				}

				public void downClick() {
					selectTurret = 2;
				}
			};
			poisonTurret.setLocation(18, 134);
			LPaper laserTurret = new LPaper(turrets[3]) {

				public void paint(GLEx g) {
					if (selectTurret == 3) {
						g.setColor(LColor.red);
						g.drawRect(2, 2, this.getWidth() - 4, this.getHeight() - 4);
						g.resetColor();
					}
				}

				public void downClick() {
					selectTurret = 3;
				}
			};
			laserTurret.setLocation(78, 134);
			// 用LPaper制作敌人增加按钮
			LPaper button = new LPaper("assets/button.png") {

				public void downClick() {
					// 获得MapLayer
					MapLayer layer = (MapLayer) getBottomLayer();
					// 开始游戏演算
					layer.doStart();
				}
			};
			button.setLocation(27, 196);

			// 复合LPaper到Layer
			add(bulletTurret);
			add(bombTurret);
			add(poisonTurret);
			add(laserTurret);
			add(button);
		}

		public void downClick(int x, int y) {
			selectTurret = -1;
		}

	}

	/**
	 * 大地图用Layer
	 */
	class MapLayer extends LLayer {

		private boolean start;

		private int startX, startY, endX, endY;

		private int index, count;
		// 设置MapLayer背景元素(键值需要与map.txt文件中标识相对应)
		private final ArrayMap pathMap = new ArrayMap();

		public MapLayer() {
			super(576, 480, true);
			// 不锁定MapLayer拖拽
			setLocked(false);
			// 锁定MapLayer中角色拖拽
			setActorDrag(false);

			pathMap.put(0, Image.createImage("assets/sand.png"));
			pathMap.put(1, Image.createImage("assets/sandTurn1.png"));
			pathMap.put(2, Image.createImage("assets/sandTurn2.png"));
			pathMap.put(3, Image.createImage("assets/sandTurn3.png"));
			pathMap.put(4, Image.createImage("assets/sandTurn4.png"));
			pathMap.put(5, new Begin("assets/base.png"));
			pathMap.put(6, new End("assets/castle.png"));

			ConfigReader config = ConfigReader.getInstance("assets/map.txt");

			// 为Layer加入简单的2D地图背景，瓦片大小32x32，以rock图片铺底
			setField2DBackground(config.getField2D("test", 32, 32), pathMap, "assets/rock.png");

			TDMain.this.field = getField2D();

			// 敌人出现坐标
			this.startX = 64;
			this.startY = 416;
			// 敌人消失坐标
			this.endX = 480;
			this.endY = 416;

			// 设定MapLayer每隔2秒执行一次内部Action
			setDelay(LSystem.SECOND * 2);
		}

		public void action(long t) {
			// 当启动标识为true时执行以下操作
			if (start) {
				if (index < 3) {
					Enemy enemy = null;
					// 根据点击next(增加敌人)的次数变换敌人样式
					switch (count) {
					case 0:
						enemy = new Enemy("assets/enemy.png", startX, startY, endX, endY, 2, 4);
						break;
					case 1:
						enemy = new Enemy("assets/fastEnemy.png", startX, startY, endX, endY, 4, 6);
						break;
					case 2:
						enemy = new Enemy("assets/smallEnemy.png", startX, startY, endX, endY, 3, 10);
						break;
					case 3:
						enemy = new Enemy("assets/bigEnemy.png", startX, startY, endX, endY, 1, 16);
						break;
					default:
						count = 0;
						enemy = new Enemy("assets/enemy.png", startX, startY, endX, endY, 2, 2);
						break;
					}
					addObject(enemy);
					index++;
					// 否则复位
				} else {
					start = false;
					index = 0;
					count++;
				}
			}
		}

		private Actor o = null;

		public void downClick(int x, int y) {
			// 转换鼠标点击区域为数组地图坐标
			int newX = x / field.getTileWidth();
			int newY = y / field.getTileHeight();
			// 当选中炮塔(参数不为-1)且数组地图参数为-1(不可通过)并且无其它角色在此时
			if ((o = getClickActor()) == null && selectTurret != -1 && field.getTileType(newX, newY) == -1) {
				// 添加炮塔
				addObject(new Turret(turrets[selectTurret]), newX * field.getTileWidth(), newY * field.getTileHeight());
			}
			if (o != null && o instanceof Turret) {
				((Turret) o).selected = true;
			}
		}

		public void upClick(int x, int y) {
			if (o != null && o instanceof Turret) {
				((Turret) o).selected = false;
			}
		}

		public void doStart() {
			this.start = true;
		}

	}

	public void onLoad() {
		// 构建地图用Layer
		MapLayer layer = new MapLayer();
		layer.setAutoDestroy(true);
		// 居中
		centerOn(layer);
		// 添加MapLayer到Screen
		add(layer);
		// 构建菜单用Layer
		Menu menu = new Menu();
		// 让menu居于屏幕右侧
		rightOn(menu);
		menu.setY(0);
		// 添加menu到Screen
		add(menu);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchDown(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchUp(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchMove(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchDrag(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(GLEx g) {
		// TODO Auto-generated method stub

	}

	@Override
	public void alter(LTimerContext timer) {
		// TODO Auto-generated method stub

	}
}
