using Loon;
using Loon.Utils.Debug;
using Loon.Core.Graphics;
using Microsoft.Xna.Framework;
using Microsoft.Advertising.Mobile.Xna;
using System.Diagnostics;
using System.Device.Location;
using System;

namespace LGameAd
{
    /// <summary>
    /// 构建XNA监听，用以展示广告
    /// </summary>
    public class ADListener : XNAListener
    {
        //Advertising测试用标记（微软硬性规定，只有传这个才能启动Advertising测试）
        private static readonly string ApplicationId = "test_client";

        //广告单元ID（测试时只支持4种显示模式，就是Image480_80、Image480_80、Image300_50、TextAd，正式ID后才能自定义。）
        private static readonly string AdUnitId = "Image480_80";

        private DrawableAd bannerAd;

        //广告驱动定位器(用来通过GPS/AGPS找到你手机的物理位置)
        private GeoCoordinateWatcher gcw = null;

        /// <summary>
        /// LGame监听接口，用来监听标准XNA中Game类的构建
        /// </summary>
        /// <param name="game"></param>
        public void Create(Game game)
        {

        }

        /// <summary>
        /// LGame监听接口，用来监听标准XNA中Initialize的启动
        /// </summary>
        public void Initialize(Game game)
        {
            //初始化AdGameComponent组件，并将其添加到游戏中
            AdGameComponent.Initialize(game, ApplicationId);
            game.Components.Add(AdGameComponent.Current);
            //创建一个新的广告
            CreateAd(game);
        }

        /// <summary>
        /// LGame监听接口，用来监听标准XNA中LoadContent的启动
        /// </summary>
        public void LoadContent(Game game)
        {

        }

        /// <summary>
        /// LGame监听接口，用来监听标准XNA中UnloadContent的启动
        /// </summary>
        public void UnloadContent(Game game)
        {

        }

        /// <summary>
        /// LGame监听接口，用来监听标准XNA中Updatet的调用（每帧循环时都会调用）
        /// </summary>
        public void Update(Game game, GameTime gameTime)
        {

        }

        /// <summary>
        /// LGame监听接口，用来监听标准XNA中Draw的调用（每帧循环时都会调用）
        /// </summary>
        public void Draw(Game game, GameTime gameTime)
        {

        }

        /// <summary>
        /// 创建广告
        /// </summary>
        private void CreateAd(Game game)
        {
            // 创建指定大小的广告组件
            int width = 480;
            int height = 80;
            // 定位到屏幕中央上方
            int x = (game.GraphicsDevice.Viewport.Bounds.Width - width) / 2;
            int y = 5;

            bannerAd = AdGameComponent.Current.CreateAd(AdUnitId, new Rectangle(x, y, width, height), true);

            // 添加广告事件监听
            bannerAd.ErrorOccurred += new EventHandler<Microsoft.Advertising.AdErrorEventArgs>(bannerAd_ErrorOccurred);
            bannerAd.AdRefreshed += new EventHandler(bannerAd_AdRefreshed);

            // 并不是立即激活广告(在GPS定位成功后才激活)
            AdGameComponent.Current.Enabled = false;

            // 构建定位器
            this.gcw = new GeoCoordinateWatcher();
            // 监听定位器活动
            this.gcw.PositionChanged += new EventHandler<GeoPositionChangedEventArgs<GeoCoordinate>>(gcw_PositionChanged);
            this.gcw.StatusChanged += new EventHandler<GeoPositionStatusChangedEventArgs>(gcw_StatusChanged);
            this.gcw.Start();
        }

        private void bannerAd_AdRefreshed(object sender, EventArgs e)
        {
            Log.DebugWrite("Ad received successfully");
        }

        private void bannerAd_ErrorOccurred(object sender, Microsoft.Advertising.AdErrorEventArgs e)
        {
            Log.DebugWrite("Ad error: " + e.Error.Message);
        }

        private void gcw_PositionChanged(object sender, GeoPositionChangedEventArgs<GeoCoordinate> e)
        {

            this.gcw.Stop();

            bannerAd.LocationLatitude = e.Position.Location.Latitude;
            bannerAd.LocationLongitude = e.Position.Location.Longitude;

            AdGameComponent.Current.Enabled = true;

            Log.DebugWrite("Device lat/long: " + e.Position.Location.Latitude + ", " + e.Position.Location.Longitude);
        }

        private void gcw_StatusChanged(object sender, GeoPositionStatusChangedEventArgs e)
        {
            if (e.Status == GeoPositionStatus.Disabled || e.Status == GeoPositionStatus.NoData)
            {
                AdGameComponent.Current.Enabled = true;
                Log.DebugWrite("GeoCoordinateWatcher Status :" + e.Status);
            }
        }

        /// <summary>
        /// LGame监听接口，用来监听标准XNA中Dispose的调用（游戏结束时才会调用到）
        /// </summary>
        public void Dispose(Game game, bool disposing)
        {
            if (disposing)
            {
                if (this.gcw != null)
                {
                    this.gcw.Dispose();
                    this.gcw = null;
                }
            }
        }
    }

    public class Game1 : LFXPlus
    {
        public override void OnMain()
        {

            //加载LGame默认资源(不进行此操作，LGame内置的模拟按钮之类功能无法使用)
            XNAConfig.Load("assets/loon.def");
            //加载字体文件（此处是预编译好的xnb文件，也可以加载Content下的）
            XNAFont = new LFont("assets", "black", 0, 20);

            //注册AD监听(标准XNA事件监听)
            SetXNAListener(new ADListener());

            //设定启动参数
            LSetting setting = new LSetting();
            setting.fps = 60;
            setting.width = 480;
            setting.height = 320;
            setting.showFPS = true;
            setting.landscape = true;
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
