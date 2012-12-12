
using Loon.Action.Sprite;
using Loon.Action.Map;
using Loon.Core.Geom;
using Loon.Core.Graphics;
using Loon.Core.Event;
using Loon.Core.Input;
using Loon.Core.Graphics.Component;
using Loon.Core;
using Loon.Action;
namespace ACTSample
{
    public class GameMapTest : SpriteBatchScreen
    {
        // 敌人用类
        class Enemy : SpriteBatchObject
        {

            private float SPEED = 1;

            protected float vx;
            protected float vy;

            public Enemy(float x, float y, Animation animation, TileMap tiles)
                : base(x, y, 32, 32, animation, tiles)
            {
                vx = -SPEED;
                vy = 0;
            }

            public override void Update(long elapsedTime)
            {

                float x = GetX();
                float y = GetY();

                vy += 0.6f;

                float newX = x + vx;

                // 判断预期坐标是否与瓦片相撞(X坐标测试)
                Vector2f tile = tiles.GetTileCollision(this, newX, y);

                if (tile == null)
                {
                    x = newX;
                }
                else
                {
                    if (vx > 0)
                    {
                        x = tiles.TilesToPixelsX(tile.x) - GetWidth();
                    }
                    else if (vx < 0)
                    {
                        x = tiles.TilesToPixelsY(tile.x + 1);
                    }
                    vx = -vx;
                }

                float newY = y + vy;

                // 判断预期坐标是否与瓦片相撞(y坐标测试)
                tile = tiles.GetTileCollision(this, x, newY);
                if (tile == null)
                {
                    y = newY;
                }
                else
                {
                    if (vy > 0)
                    {
                        y = tiles.TilesToPixelsY(tile.y) - GetHeight();
                        vy = 0;
                    }
                    else if (vy < 0)
                    {
                        y = tiles.TilesToPixelsY(tile.y + 1);
                        vy = 0;
                    }
                }

                animation.Update(elapsedTime);
                // 注入新坐标
                SetLocation(x, y);
            }

        }

        // 二次跳跃用类（物品）
        class JumperTwo : SpriteBatchObject
        {

            public JumperTwo(float x, float y, Animation animation, TileMap tiles)
                : base(x, y, 32, 32, animation, tiles)
            {

            }

            public void Use(JumpObject hero)
            {
                hero.SetJumperTwo(true);
            }

            public override void Update(long elapsedTime)
            {
                animation.Update(elapsedTime);
            }
        }


        // 加速用类（物品）
        class Accelerator : SpriteBatchObject
        {

            public Accelerator(float x, float y, Animation animation, TileMap tiles)
                : base(x, y, 32, 32, animation, tiles)
            {

            }

            public void Use(JumpObject hero)
            {
                hero.SetSpeed(hero.GetSpeed() * 2);
            }

            public override void Update(long elapsedTime)
            {
                animation.Update(elapsedTime);
            }
        }

        // 金币用类（物品）
        class Coin : SpriteBatchObject
        {

            public Coin(float x, float y, Animation animation, TileMap tiles)
                : base(x, y, 32, 32, animation, tiles)
            {

            }

            public override void Update(long elapsedTime)
            {
                animation.Update(elapsedTime);
            }

        }

        private JumpObject hero;

        // PS：如果具体游戏开发时用到多动画切换，则建议使用AnimationStorage这个Animation的子类
        // 金币用动画图
        private Animation coinAnimation;

        // 敌人用动画图(过滤掉黑色)
        private Animation enemyAnimation;

        // 加速道具动画图
        private Animation accelAnimation;

        // 二级跳动画图
        private Animation jumpertwoAnimation;

        public override void Create()
        {

            // 以指定图片创建动画
            this.coinAnimation = Animation.GetDefaultAnimation("assets/coin.png",
                    32, 32, 200);
            this.enemyAnimation = Animation.GetDefaultAnimation("assets/enemy.gif",
                    32, 32, 200, LColor.black);
            this.accelAnimation = Animation.GetDefaultAnimation(
                    "assets/accelerator.gif", 32, 32, 200);
            this.jumpertwoAnimation = Animation.GetDefaultAnimation(
                    "assets/jumper_two.gif", 32, 32, 200);

            // 注销Screen时释放下列资源
            PutReleases(coinAnimation, enemyAnimation, accelAnimation,
                    jumpertwoAnimation, hero);

            // 加载一张由字符串形成的地图（如果不使用此方式加载，则默认使用标准的数组地图）
            TileMap indexMap = TileMap.LoadCharsMap("assets/map.chr", 32, 32);
            // 如果有配置好的LTexturePack文件，可于此注入
            // indexMap.setImagePack(file);
            // 设定无法穿越的区域(如果不设置此项，所有索引不等于"-1"的区域都可以穿越)
            indexMap.SetLimit(new int[] { 'B', 'C', 'i', 'c' });
            indexMap.PutTile('B', "assets/block.png");
            int imgId = indexMap.PutTile('C', "assets/coin_block.gif");
            // 因为两块瓦片对应同一地图字符，所以此处使用了已载入的图片索引
            indexMap.PutTile('i', imgId);
            indexMap.PutTile('c', "assets/coin_block2.gif");

            // 加载此地图到窗体中
            putTileMap(indexMap);

            // 获得地图对应的二维数组
            int[][] maps = indexMap.GetMap();

            int w = indexMap.GetRow();
            int h = indexMap.GetCol();

            // 遍历二维数组地图，并以此为基础添加角色到窗体之上
            for (int i = 0; i < w; i++)
            {
                for (int j = 0; j < h; j++)
                {
                    switch (maps[j][i])
                    {
                        case 'o':
                            Coin coin = new Coin(indexMap.TilesToPixelsX(i),
                                    indexMap.TilesToPixelsY(j), new Animation(
                                            coinAnimation), indexMap);
                            AddTileObject(coin);
                            break;
                        case 'k':
                            Enemy enemy = new Enemy(indexMap.TilesToPixelsX(i),
                                    indexMap.TilesToPixelsY(j), new Animation(
                                            enemyAnimation), indexMap);
                            AddTileObject(enemy);
                            break;
                        case 'a':
                            Accelerator accelerator = new Accelerator(
                                    indexMap.TilesToPixelsX(i),
                                    indexMap.TilesToPixelsY(j), new Animation(
                                            accelAnimation), indexMap);
                            AddTileObject(accelerator);
                            break;
                        case 'j':
                            JumperTwo jump = new JumperTwo(indexMap.TilesToPixelsX(i),
                                    indexMap.TilesToPixelsY(j), new Animation(
                                            jumpertwoAnimation), indexMap);
                            AddTileObject(jump);
                            break;
                    }
                }
            }

            // 获得主角动作图
            Animation animation = Animation.GetDefaultAnimation("assets/hero.png",
                    20, 20, 150, LColor.black);

            // 在像素坐标位置(192,32)放置角色，大小为32x32，动画为针对hero.png的分解图
            hero = AddJumpObject(192, 32, 32, 32, animation);

            // 让地图跟随指定对象产生移动（无论插入有多少张数组地图，此跟随默认对所有地图生效）
            // 另外请注意，此处能产生跟随的对像是任意LObject，并不局限于游戏角色。
            Follow(hero);

            // 监听跳跃事件
            hero.listener = new JumpI(indexMap, enemyAnimation);
            AddActionKey(Key.LEFT, new GoLeftKey());
            AddActionKey(Key.RIGHT, new GoRightKey());
            AddActionKey(Key.UP, new GoJumpKey());
            if (LSystem.type != LSystem.ApplicationType.JavaSE)
            {

                LPad pad = new LPad(10, 180);
                pad.SetListener(new PadClick(this));
                Add(pad);
            }
            this.updateListener =new GameUpdateListener(this);
        }

        class GameUpdateListener : UpdateListener
        {

            private GameMapTest game;

            public GameUpdateListener(GameMapTest test)
            {
                this.game = test;
            }

            public void Act(SpriteBatchObject sprite, long elapsedTime)
            {

                // 如果主角与地图上其它对象发生碰撞（以下分别验证）
                if (game.hero.IsCollision(sprite))
                {
                    // 与敌人
                    if (sprite is Enemy)
                    {
                        Enemy e = (Enemy)sprite;
                        if (game.hero.Y() < e.Y())
                        {
                            game.hero.SetForceJump(true);
                            game.hero.Jump();
                            game.RemoveTileObject(e);
                        }
                        else
                        {
                            game.Damage();
                        }
                        // 与金币
                    }
                    else if (sprite is Coin)
                    {
                        Coin coin = (Coin)sprite;
                        game.RemoveTileObject(coin);
                        // 与加速道具
                    }
                    else if (sprite is Accelerator)
                    {
                        game.RemoveTileObject(sprite);
                        Accelerator accelerator = (Accelerator)sprite;
                        accelerator.Use(game.hero);
                        // 与二次弹跳道具
                    }
                    else if (sprite is JumperTwo)
                    {
                        game.RemoveTileObject(sprite);
                        JumperTwo jumperTwo = (JumperTwo)sprite;
                        jumperTwo.Use(game.hero);
                    }
                }
            }
        }


        class PadClick : LPad.ClickListener
        {

            private GameMapTest game;

            public PadClick(GameMapTest test)
            {
                this.game = test;

            }

            public void Up()
            {
                game.PressActionKey(Key.UP);
            }

            public void Right()
            {
                game.PressActionKey(Key.RIGHT);
            }

            public void Left()
            {
                game.PressActionKey(Key.LEFT);
            }

            public void Down()
            {
                game.PressActionKey(Key.DOWN);
            }

            public void Other()
            {
                game.ReleaseActionKeys();
            }

        }


        // 对应向左行走的键盘事件
        class GoLeftKey : ActionKey
        {
            public override void Act(long e)
            {
                GameMapTest game = (GameMapTest)StaticCurrentSceen;
                game.hero.SetMirror(true);
                game.hero.AccelerateLeft();
            }
        };

        // 对应向右行走的键盘事件
        class GoRightKey : ActionKey
        {
            public override void Act(long e)
            {
                GameMapTest game = (GameMapTest)StaticCurrentSceen;
                game.hero.SetMirror(false);
                game.hero.AccelerateRight();
            }
        };

        // 对应向右行走的键盘事件
        class GoJumpKey : ActionKey
        {
            public override void Act(long e)
            {
                GameMapTest game = (GameMapTest)StaticCurrentSceen;
                game.hero.Jump();
            }
        };

        class JumpI : JumpObject.JumpListener
        {

            TileMap indexMap;

            Animation enemyAnimation;

            public JumpI(TileMap indexMap, Animation enemyAnimation)
            {
                this.indexMap = indexMap;
                this.enemyAnimation = enemyAnimation;
            }

            public void Update(long elapsedTime)
            {

            }

            public void Check(int x, int y)
            {
                GameMapTest game = (GameMapTest)StaticCurrentSceen;
                if (indexMap.GetTileID(x, y) == 'C')
                {
                    indexMap.SetTileID(x, y, 'c');
                    Enemy enemy = new Enemy(indexMap.TilesToPixelsX(x),
                            indexMap.TilesToPixelsY(y - 1), new Animation(
                                    enemyAnimation), indexMap);
                    game.Add(enemy);
                    // 标注地图已脏，强制缓存刷新
                    indexMap.SetDirty(true);
                }
                else if (indexMap.GetTileID(x + 1, y) == 'C')
                {
                    indexMap.SetTileID(x + 1, y, 'c');
                    indexMap.SetDirty(true);
                }

            }
        }
        
	private RotateTo rotate;

    public void Damage()
    {
        // 主角与敌人碰撞时(而非踩到了敌人)，触发一个旋转动作(其实效果可以做的更有趣一些，
        // 比如先反弹到某一方向(FireTo)，然后再弹回等等，此处仅仅举个例子)
        if (rotate == null)
        {
            // 旋转360度，每帧累加5度
            rotate = new RotateTo(360f, 5f);
            rotate.SetActionListener(new RotateActionListener(this));
            AddAction(rotate, hero);
        }
        else if (rotate.IsComplete())
        {
            hero.SetFilterColor(LColor.red);
            // 直接重置rotate对象
            rotate.Start(hero);
            // 重新插入(LGame的方针是Action事件触发且结束后，自动删除该事件，所以需要重新插入)
            AddAction(rotate, hero);
        }
    }

        class RotateActionListener : ActionListener {

                private GameMapTest game;

                public RotateActionListener(GameMapTest test){
                      this.game = test;
                }

				public void Stop(ActionBind o) {
					game.hero.SetFilterColor(LColor.white);
					game.hero.SetRotation(0);
				}

				public void Start(ActionBind o) {
					game.hero.SetFilterColor(LColor.red);
					game.hero.Jump();
				}

				public void Process(ActionBind o) {

				}
			}

        public override void After(SpriteBatch batch)
        {

        }

        public override void Before(SpriteBatch batch)
        {

        }

        public override void Press(Loon.Core.Input.LKey e)
        {

        }

        public override void Release(Loon.Core.Input.LKey e)
        {

        }

        public override void Update(long elapsedTime)
        {
            if (hero != null)
            {
                hero.Stop();
            }
        }

        public override void Close()
        {

        }

        public override void TouchDown(Loon.Core.Input.LTouch e)
        {

        }

        public override void TouchUp(Loon.Core.Input.LTouch e)
        {

        }

        public override void TouchMove(Loon.Core.Input.LTouch e)
        {

        }

        public override void TouchDrag(Loon.Core.Input.LTouch e)
        {

        }
    }
}
