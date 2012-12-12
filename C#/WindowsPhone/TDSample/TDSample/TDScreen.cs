using Loon.Core.Graphics;
using Loon.Action.Map;
using Loon.Core.Graphics.Component;
using Loon.Utils;
using Loon.Action;
using Loon.Core.Graphics.Opengl;
using System.Collections;
using Loon.Action.Sprite;
using Loon.Core.Graphics.Device;
using System;
using Loon.Core;
using Loon.Core.Resource;
using Loon.Core.Timer;
using Loon.Core.Input;
namespace TDSample
{
    public class TDScreen : Screen
    {

        private static int selectTurret = -1;

        private static Field2D Field;

        private static string[] turrets = new string[] { "assets/bulletTurret.png",
			"assets/bombTurret.png", "assets/poisonTurret.png",
			"assets/laserTurret.png", "assets/bullet.png" };

        /**
         * 子弹用类
         * 
         */
        class Bullet : Actor
        {

            /* 抛物线用加速度、重力 */
            private float vx, vy, gravity;

            /* 增加一个type向，用来区分子弹类型 */
            int type;

            private float speed;

            private float dir;

            private int damage;

            private float x, y;

            private bool removeFlag;

            public Bullet(int type, string fileName, float dir, int damage)
            {
                this.type = type;
                this.dir = dir;
                this.damage = damage;
                this.SetImage(fileName);
                this.SetDelay(50);
                /* 如果子弹类型为抛物线 */
                if (type == 1)
                {
                    this.SetDelay(0);
                    this.speed = 200f;
                    this.gravity = 200f;
                    // 转化方向(360度方向)为弧度
                    float angle = MathUtils.ToRadians(this.dir);
                    // 计算加速度
                    this.vx = speed * MathUtils.Cos(angle);
                    this.vy = speed * MathUtils.Sin(angle);
                }
            }

            protected override void AddLayer(ActorLayer layer)
            {
                this.x = this.GetX();
                this.y = this.GetY();
            }

            public override void Action(long t)
            {
                if (removeFlag)
                {
                    return;
                }
                object o = null;

                switch (type)
                {
                    case 0:
                        for (int i = 0; i < 6; i++)
                        {
                            // 矫正弹道位置
                            float angle = MathUtils.ToRadians(this.dir);
                            this.x += MathUtils.Cos(angle);
                            this.y += MathUtils.Sin(angle);
                        }
                        this.SetLocation(
                                this.x + (Field.GetTileWidth() - this.GetWidth()) / 2,
                                this.y + (Field.GetTileHeight() - this.GetHeight()) / 2);
                        break;
                    case 1:
                        /*
                         * 计算每次子弹位置(这个例子如果直接做抛物线，有一处不妥，即MapLayer的setDelay被设定成了500
                         * 也就是每隔500毫秒（半秒），才改变一次地图中物体状态，这样做出来的抛物线会不太自然。)
                         */
                        x = GetX();
                        y = GetY();
                        float dt = MathUtils.Max((t / 1000), 0.01f);
                        vy += gravity * dt;
                        x += vx * dt;
                        y += vy * dt;
                        this.SetLocation(this.x, this.y);
                        break;
                }

                o = this.GetOnlyCollisionObject(typeof(Enemy));
                // 当与敌相撞时
                if (o != null)
                {
                    Enemy e = (Enemy)o;
                    // 减少敌方HP
                    e.hp -= this.damage;
                    e.hpBar.SetUpdate(e.hp);
                    removeFlag = true;
                    // 从Layer中删除自身
                    GetLLayer().RemoveObject(this);

                    return;
                    // 超出游戏画面时删除自身
                }
                else if (this.GetX() <= 12
                      || this.GetX() >= this.GetLLayer().GetWidth() - 12
                      || this.GetY() <= 12
                      || this.GetY() >= this.GetLLayer().GetHeight() - 12)
                {
                    removeFlag = true;
                    this.GetLLayer().RemoveObject(this);
                }
            }
        }

        /**
         * 炮塔用类
         * 
         */
        class Turret : Actor
        {

            private int range = 50;

            private int delay = 10;

            internal bool selected;

            public Turret(string fileName)
            {
                SetImage(fileName);
                SetDelay(100);
                SetAlpha(0);
            }

            class RotationAction : ActionListener
            {
                public void Start(ActionBind o)
                {

                }

                public void Process(ActionBind o)
                {

                }

                public void Stop(ActionBind o)
                {
                    ((Actor)o).RotateTo(90);
                }
            }

            protected override void AddLayer(ActorLayer layer)
            {
                // 让角色渐进式出现
                FadeTo fade = FadeOut();
                fade.SetActionListener(new RotationAction());
            }

            public override void Draw(GLEx g)
            {
                if (selected)
                {
                    g.SetColor(255, 0, 0, 100);
                    g.FillOval(-(range * 2 - Field.GetTileWidth()) / 2,
                            -(range * 2 - Field.GetTileHeight()) / 2,
                            this.range * 2 - 1, this.range * 2 - 1);
                    g.SetColor(LColor.red);
                    g.DrawOval(-(range * 2 - Field.GetTileWidth()) / 2,
                            -(range * 2 - Field.GetTileHeight()) / 2,
                            this.range * 2 - 1, this.range * 2 - 1);
                    g.ResetColor();
                }
            }

            public override void Action(long t)
            {
                // 遍历指定半径内所有Enemy类
                IList es = this.GetCollisionObjects(this.range, typeof(Enemy));
                // 当敌人存在
                if (es.Count != 0)
                {
                    Enemy target = (Enemy)es[0];
                    // 旋转炮台对准Enemy坐标
                    SetRotation((int)MathUtils.ToDegrees(MathUtils.Atan2(
                            (target.GetY() - this.GetY()),
                            (target.GetX() - this.GetX()))));

                }
                // 延迟炮击
                if (this.delay > 0)
                {
                    --this.delay;
                }
                else if (es.Count != 0)
                {

                    // *将子弹类型设定为1*/
                    // 构造炮弹
                    Bullet bullet = new Bullet(0, turrets[4], this.GetRotation(), 2);

                    // 计算炮击点
                    int x = MathUtils.Round(MathUtils.Cos(MathUtils.ToRadians(this
                            .GetRotation())) * (float)bullet.GetWidth() * 2)
                            + this.X();

                    int y = MathUtils.Round(MathUtils.Sin(MathUtils.ToRadians(this
                            .GetRotation())) * (float)bullet.GetHeight() * 2)
                            + this.Y();

                    // 注入炮弹到Layer
                    this.GetLLayer().AddObject(bullet, x, y);
                    this.delay = 10;

                }

            }
        }

        /**
         * 敌兵用类
         * 
         */
        class Enemy : Actor
        {

            private int startX, startY;

            private int endX, endY;

            internal int speed, hp;

            private bool removeFlag;

            // 使用精灵StatusBar充当血槽
            internal StatusBar hpBar;

            public Enemy(string fileName, int sx, int sy, int ex, int ey,
                    int speed, int hp)
            {
                this.SetDelay(300);
                this.SetImage(fileName);
                this.hpBar = new StatusBar(hp, hp, (this.GetWidth() - 25) / 2,
                        this.GetHeight() + 5, 25, 5);
                this.startX = sx;
                this.startY = sy;
                this.endX = ex;
                this.endY = ey;
                this.speed = speed;
                this.hp = hp;
            }

            public override void Draw(GLEx g)
            {

                // 绘制精灵
                hpBar.CreateUI(g);

            }

            public class RemoveAction : ActionListener
            {

                private Enemy enemy;

                public RemoveAction(Enemy e)
                {
                    this.enemy = e;
                }

                public void Start(ActionBind o)
                {

                }

                public void Process(ActionBind o)
                {

                }

                public void Stop(ActionBind o)
                {
                    enemy.RemoveActionEvents();
                    enemy.GetLLayer().RemoveObject(enemy);
                }
            }


            public override void Action(long t)
            {
                // 触发精灵事件
                hpBar.Update(t);
                if (hp <= 0 && !removeFlag)
                {
                    // 设定死亡时渐变
                    FadeTo fade = FadeIn();
                    // 渐变时间为30毫秒
                    fade.SetSpeed(30);
                    // 监听渐变过程
                    fade.SetActionListener(new RemoveAction(this));

                    this.removeFlag = true;
                }
            }

            class MoveAction : ActionListener
            {

                ActorLayer layer;

                MoveTo move;

                public MoveAction(MoveTo move, ActorLayer layer)
                {
                    this.move = move;
                    this.layer = layer;
                }

                // 截取事件进行中数据
                public void Process(ActionBind o)
                {
                    // 获得角色移动方向
                    switch (move.GetDirection())
                    {
                        case Field2D.TUP:
                            // 根据当前移动方向，变更角色旋转方向（以下同）
                            o.SetRotation(270);
                            break;
                        case Field2D.TLEFT:
                            o.SetRotation(180);
                            break;
                        case Field2D.TRIGHT:
                            o.SetRotation(0);
                            break;
                        case Field2D.TDOWN:
                            o.SetRotation(90);
                            break;
                        default:
                            break;
                    }

                }

                public void Start(ActionBind o)
                {

                }

                // 当角色移动完毕时
                public void Stop(ActionBind o)
                {
                    // 从Layer中删除此角色
                    layer.RemoveObject((Actor)o);
                }

            }


            // 首次注入Layer时调用此函数
            protected override void AddLayer(ActorLayer layer)
            {

                // 坐标矫正，用以让角色居于瓦片中心
                int offsetX = (GetLLayer().GetField2D().GetTileWidth() - this
                       .GetWidth()) / 2;
                int offsetY = (GetLLayer().GetField2D().GetTileWidth() - this
                       .GetHeight()) / 2;
                // 初始化角色在Layer中坐标
                SetLocation(startX + offsetX, startY + offsetY);
                // 命令角色向指定坐标自行移动(参数为false为四方向寻径，为true时八方向)，并返回移动控制器
                // PS:endX与endY非显示位置，所以不必矫正
                MoveTo move = MoveTo(endX, endY, false);

                // 矫正坐标，让角色居中
                move.SetOffset(offsetX, offsetY);
                // 启动角色事件监听
                move.SetActionListener(new MoveAction(move, layer));
                // 设定移动速度
                move.SetSpeed(speed);
            }
        }

        // 起始点
        class Begin : Actor
        {
            public Begin(string fileName)
            {
                SetImage(fileName);
            }
        }

        // 结束点
        class End : Actor
        {
            public End(string fileName)
            {
                SetImage(fileName);
            }
        }

        /**
         * 拖拽用菜单
         * 
         */
        class Menu : LLayer
        {

            class BulletTurret : LPaper
            {

                public BulletTurret()
                    : base(turrets[0])
                {
                }

                // 当选中当前按钮时，为按钮绘制选中框(以下同)
                public override void Paint(GLEx g)
                {
                    if (selectTurret == 0)
                    {
                         g.SetColor(LColor.red);
                         g.DrawRect(2, 2, this.GetWidth() - 4,
                                 this.GetHeight() - 4);
                         g.ResetColor();
                    }
                }

                public override void DownClick()
                {
                    selectTurret = 0;
                }
            };

            class BombTurret : LPaper
            {

                public BombTurret()
                    : base((turrets[1]))
                {

                }

                public override void Paint(GLEx g)
                {
                    if (selectTurret == 1)
                    {
                        g.SetColor(LColor.red);
                        g.DrawRect(2, 2, this.GetWidth() - 4,
                                this.GetHeight() - 4);
                        g.ResetColor();
                    }
                }

                public override void DownClick()
                {
                    selectTurret = 1;
                }
            };

            class PoisonTurret : LPaper
            {

                public PoisonTurret()
                    : base(turrets[2])
                {
                }

                public override void Paint(GLEx g)
                {
                    if (selectTurret == 2)
                    {
                        g.SetColor(LColor.red);
                        g.DrawRect(2, 2, this.GetWidth() - 4,
                                this.GetHeight() - 4);
                        g.ResetColor();
                    }
                }

                public override void DownClick()
                {
                    selectTurret = 2;
                }
            };

            class LaserTurret : LPaper
            {

                public LaserTurret()
                    : base(turrets[3])
                {
                }

                public override void Paint(GLEx g)
                {
                    if (selectTurret == 3)
                    {
                        g.SetColor(LColor.red);
                        g.DrawRect(2, 2, this.GetWidth() - 4,
                                this.GetHeight() - 4);
                        g.ResetColor();
                    }
                }

                public override void DownClick()
                {
                    selectTurret = 3;
                }
            };

            // 用LPaper制作敌人增加按钮
            class Button : LPaper
            {


                public Button()
                    : base("assets/button.png")
                {

                }

                public override void DownClick()
                {
                    // 获得MapLayer
                    MapLayer layer = (MapLayer)Screen.StaticCurrentSceen.GetBottomLayer();
                    // 开始游戏演算
                    layer.DoStart();
                }
            };

            public Menu()
                : base(128, 240)
            {


                // 设定menu层级高于MapLayer
                SetLayer(101);
                // 不锁定menu移动
                SetLocked(false);
                SetLimitMove(false);
                // 锁定Actor拖拽
                SetActorDrag(false);
                SetDelay(500);
                // 设定Menu背景
                LImage image = LImage.CreateImage(this.GetWidth(),
                        this.GetHeight(), true);
                LGraphics g = image.GetLGraphics();
                g.SetColor(0, 0, 0, 125);
                g.FillRect(0, 0, GetWidth(), GetHeight());
                g.SetColor(LColor.white);
                g.SetFont(15);
                g.DrawString("我是可拖拽菜单", 12, 25);
                g.Dispose();
                SetBackground(image.GetTexture());

                BulletTurret bulletTurret = new BulletTurret();
                bulletTurret.SetLocation(18, 64);


                BombTurret bombTurret = new BombTurret();
                bombTurret.SetLocation(78, 64);


                PoisonTurret poisonTurret = new PoisonTurret();
                poisonTurret.SetLocation(18, 134);


                LaserTurret laserTurret = new LaserTurret();
                laserTurret.SetLocation(78, 134);

                Button button = new Button();
                button.SetLocation(27, 196);

                // 复合LPaper到Layer
                Add(bulletTurret);
                Add(bombTurret);
                Add(poisonTurret);
                Add(laserTurret);
                Add(button);
            }

            public override void DownClick(int x, int y)
            {
                selectTurret = -1;
            }

        }

        /**
         * 大地图用Layer
         */
        class MapLayer : LLayer
        {

            private bool start;

            private int startX, startY, endX, endY;

            private int index, count;
            // 设置MapLayer背景元素(键值需要与map.txt文件中标识相对应)
            private System.Collections.Generic.Dictionary<object, object> pathMap = new System.Collections.Generic.Dictionary<object, object>();

            public MapLayer()
                : base(576, 480, true)
            {

                // 不锁定MapLayer拖拽
                SetLocked(false);
                // 锁定MapLayer中角色拖拽
                SetActorDrag(false);

                pathMap.Add(0, new LImage("assets/sand.png"));
                pathMap.Add(1, new LImage("assets/sandTurn1.png"));
                pathMap.Add(2, new LImage("assets/sandTurn2.png"));
                pathMap.Add(3, new LImage("assets/sandTurn3.png"));
                pathMap.Add(4, new LImage("assets/sandTurn4.png"));
                pathMap.Add(5, new Begin("assets/base.png"));
                pathMap.Add(6, new End("assets/castle.png"));

                ConfigReader config = ConfigReader.GetInstance("assets/map.txt");

                // 为Layer加入简单的2D地图背景，瓦片大小32x32，以rock图片铺底
                SetField2DBackground(config.GetField2D("test", 32, 32), pathMap,
                        "assets/rock.png");


                Field = GetField2D();

                // 敌人出现坐标
                this.startX = 64;
                this.startY = 416;
                // 敌人消失坐标
                this.endX = 480;
                this.endY = 416;

                // 设定MapLayer每隔2秒执行一次内部Action
                SetDelay(LSystem.SECOND * 2);
            }

            public override void Action(long t)
            {
                // 当启动标识为true时执行以下操作
                if (start)
                {
                    if (index < 3)
                    {
                        Enemy enemy = null;
                        // 根据点击next(增加敌人)的次数变换敌人样式
                        switch (count)
                        {
                            case 0:
                                enemy = new Enemy("assets/enemy.png", startX, startY,
                                        endX, endY, 2, 4);
                                break;
                            case 1:
                                enemy = new Enemy("assets/fastEnemy.png", startX,
                                        startY, endX, endY, 4, 6);
                                break;
                            case 2:
                                enemy = new Enemy("assets/smallEnemy.png", startX,
                                        startY, endX, endY, 3, 10);
                                break;
                            case 3:
                                enemy = new Enemy("assets/bigEnemy.png", startX,
                                        startY, endX, endY, 1, 16);
                                break;
                            default:
                                count = 0;
                                enemy = new Enemy("assets/enemy.png", startX, startY,
                                        endX, endY, 2, 2);
                                break;
                        }
                        AddObject(enemy);
                        index++;
                        // 否则复位
                    }
                    else
                    {
                        start = false;
                        index = 0;
                        count++;
                    }
                }
            }

            private Actor o = null;

            public override void DownClick(int x, int y)
            {
                // 转换鼠标点击区域为数组地图坐标
                int newX = x / Field.GetTileWidth();
                int newY = y / Field.GetTileHeight();
                // 当选中炮塔(参数不为-1)且数组地图参数为-1(不可通过)并且无其它角色在此时
                if ((o = GetClickActor()) == null && selectTurret != -1
                        && Field.GetType(newY, newX) == -1)
                {
                    // 添加炮塔
                    AddObject(new Turret(turrets[selectTurret]),
                            newX * Field.GetTileWidth(),
                            newY * Field.GetTileHeight());
                }
                if (o != null && o is Turret)
                {
                    ((Turret)o).selected = true;
                }
            }

            public override void UpClick(int x, int y)
            {
                if (o != null && o is Turret)
                {
                    ((Turret)o).selected = false;
                }
            }

            public void DoStart()
            {
                this.start = true;
            }

        }

        public override void OnLoad()
        {

            // 构建地图用Layer
            MapLayer layer = new MapLayer();
            layer.SetAutoDestroy(true);
            // 居中
            CenterOn(layer);
            // 添加MapLayer到Screen
            Add(layer);
            // 构建菜单用Layer
            Menu menu = new Menu();
            // 让menu居于屏幕右侧
            RightOn(menu);
            menu.SetY(0);
            // 添加menu到Screen
            Add(menu);
        }

        public override void Alter(LTimerContext timer)
        {

        }

        public override void Draw(GLEx g)
        {

        }

        public override void TouchDown(LTouch touch)
        {

        }

        public override void TouchUp(LTouch touch)
        {

        }

        public override void TouchMove(LTouch e)
        {

        }

        public override void TouchDrag(LTouch arg0)
        {

        }
    }
}
