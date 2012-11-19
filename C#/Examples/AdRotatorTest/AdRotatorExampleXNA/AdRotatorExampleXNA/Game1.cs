using System;
using System.Windows;
using AdRotatorXNA;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using Loon;
using Loon.Core.Graphics;

namespace AdRotatorExampleXNA
{
    /// <summary>
    /// 创建XNA监听器(稍微解释一下LGame中所谓的XNA监听器。本质上说，LGame-XNA版其实就是一个XNA的封装马甲。所以此监听器的实际作用，就是
    /// 在LGame处理完毕后，把XNA应有的操作权限在监听中显示出来罢了~)
    /// </summary>
    public class ADListener : XNAListener 
    {

        public void Create(Game gamne)
        {

        }

        public void Initialize(Game game)
        {
            // 初始化广告组件
            AdRotatorXNAComponent.Initialize(game);
            
            //硬编码的话就填下面这些

            //AdRotatorXNAComponent.Current.PubCenterAppId = "test_client";
            //AdRotatorXNAComponent.Current.PubCenterAdUnitId = "Image480_80";

            //AdRotatorXNAComponent.Current.AdDuplexAppId = "0";

            //AdRotatorXNAComponent.Current.InneractiveAppId = "DavideCleopadre_ClockAlarmNightLight_WP7";

            //AdRotatorXNAComponent.Current.MobFoxAppId = "474b65a3575dcbc261090efb2b996301";
            //AdRotatorXNAComponent.Current.MobFoxIsTest = true;

            //读取配置文件的话就填下面这些(本例为读取AdDuplex的测试广告，AdRotator也支持Admob广告)

            //定位广告位置
            AdRotatorXNAComponent.Current.AdPosition = new Vector2(0,720);

            //设定默认的广告图片
            AdRotatorXNAComponent.Current.DefaultHouseAdImage = game.Content.Load<Texture2D>(@"Content/AdRotatorDefaultAdImage");

            //当点击默认广告时，指向此操作。
            AdRotatorXNAComponent.Current.DefaultHouseAdClick += new AdRotatorXNAComponent.DefaultHouseAdClickEventHandler(Current_DefaultHouseAdClick);

            //用以选择广告幻灯效果的弹出方向
            AdRotatorXNAComponent.Current.SlidingAdDirection = SlideDirection.None;

            //选择本地的广告配置文件地址（针对不同的广告商，此处配置效果不同，以具体广告商提供的配置方式为准）
            AdRotatorXNAComponent.Current.DefaultSettingsFileUri = "defaultAdSettings.xml";

            //设定远程配置文件（可选项，非必填）
            AdRotatorXNAComponent.Current.SettingsUrl = "http://xna-uk.net/adrotator/XNAdefaultAdSettingsV2.xml";

            //添加广告组件到XNA画面当中
            game.Components.Add(AdRotatorXNAComponent.Current);

        }

        void Current_DefaultHouseAdClick()
        {
            try
            {
                MessageBox.Show("非常感谢您点了小弟的广告^_^");
            }
            catch { }
        }

        public void LoadContent(Game game)
        {

        }

        public void UnloadContent(Game game)
        {

        }

        public void Update(Game game,GameTime gameTime)
        {
 
        }

        public void Draw(Game game, GameTime gameTime)
        {

        }

        public void Dispose(Game game, bool close)
        {

        }

    }

    public class Game1 : LFXPlus
    {
        public override void OnMain()
        {

            //加载LGame默认资源(不进行此操作，LGame内置的模拟按钮之类功能无法使用)
            XNAConfig.Load("content/loon.def");
            //加载字体文件（此处是预编译好的xnb文件 PS：当自定义资源文件夹命名为Content时，
            //打包后会自动和标准的Content文件夹合并，这里做个演示。另，Windows系统不区分文件
            //名大小写）
            XNAFont = new LFont("content", "black", 0, 20);

            //注册AD监听(标准XNA事件监听)
            SetXNAListener(new ADListener());

            //设定启动参数
            LSetting setting = new LSetting();
            setting.fps = 60;
            setting.width = 480;
            setting.height = 320;
            setting.showFPS = true;
            setting.landscape = false;
            //注册初始Screen
            Register(setting, typeof(ScreenTest));

        }

        public override void OnGameResumed()
        {

        }

        public override void OnGamePaused()
        {

        }
    }
}
