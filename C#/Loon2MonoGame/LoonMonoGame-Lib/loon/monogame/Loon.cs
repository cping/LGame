using java.lang;
using loon.geom;
using loon.utils;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.Graphics;
using MonoGame.Framework.Utilities;
using System;

namespace loon.monogame
{
    public abstract class Loon : Game, Platform
    {

        public interface XNAListener
        {
            void OnResume();

            void OnExit();

            void OnPause();

            void Create(Game game);

            void Initialize();

            void LoadContent();

            void UnloadContent();

            void Update(GameTime gameTime);

            void Draw(GameTime gameTime);

            void Dispose(bool disposing);
        }

        private XNAListener _xnalistener;

        private DisplayMode _displayMode;

        private GraphicsDeviceManager _graphics;

        private MonoGameSetting _setting;

        public delegate Screen ScreenDelegate();

        private ScreenDelegate _mainDelegateData;

        private Data _mainInterfaceData;

        private MonoGameGame _game;

        public Loon() : this("")
        {

        }

        public Loon(string dir)
        {
            LSystem.FreeStaticObject();
            CheckDisplayMode();
            _graphics = new GraphicsDeviceManager(this);
            Content.RootDirectory = dir;
        }


        protected internal virtual MonoGameGame InitializeGame()
        {
            this.CreateGame();
            if (_game != null && _mainDelegateData != null)
            {
                _game.Register(_mainDelegateData.Invoke());
            }
            else if (_game != null && _mainInterfaceData != null)
            {
                _game.Register(_mainInterfaceData.OnScreen());
            }
            if (_xnalistener != null)
            {
                _xnalistener.Create(this);
            }
            return _game;
        }

        protected internal virtual MonoGameGame CreateGame()
        {
            return this._game = new MonoGameGame(this._setting, this);
        }

        /// <summary>
        /// 以Delegate委托方式注入初始数据
        /// </summary>
        /// <param name="s"></param>
        /// <param name="data"></param>
        public virtual void Register(LSetting s, ScreenDelegate data)
        {
            if (s is MonoGameSetting mgs)
            {
                this._setting = mgs;
            }
            else
            {
                MonoGameSetting tmp = new MonoGameSetting();
                tmp.Copy(s);
                tmp.fullscreen = true;
                this._setting = tmp;
            }
            this._mainDelegateData = data;
        }
        protected internal void CheckDisplayMode()
        {
            if (_displayMode == null)
            {
                this._displayMode = GraphicsAdapter.DefaultAdapter.CurrentDisplayMode;
            }
        }

        /// <summary>
        /// 以Data接口方式注入初始数据
        /// </summary>
        /// <param name="s"></param>
        /// <param name="data"></param>
        public virtual void Register(LSetting s, Data data)
        {
            if (s is MonoGameSetting mgs)
            {
                this._setting = mgs;
            }
            else
            {
                MonoGameSetting tmp = new MonoGameSetting();
                tmp.Copy(s);
                tmp.fullscreen = true;
                this._setting = tmp;
            }
            this._mainInterfaceData = data;
        }

        private int maxWidth, maxHeight;

        private int zoomWidth, zoomHeight;

        public abstract void OnMain();

        protected override void Initialize()
        {
            LSystem.FreeStaticObject();

            if (_xnalistener != null)
            {
                _xnalistener.Initialize();
            }

            this.OnMain();

            if (_setting == null)
            {
                _setting = new MonoGameSetting();
            }
            LMode mode = _setting.showMode;
            if (mode == default)
            {
                mode = LMode.Fill;
            }

            float width = _setting.width;
            float height = _setting.height;

            // 是否按比例缩放屏幕
            if (_setting.useRatioScaleFactor)
            {
                float scale = ScaleFactor();
                width *= scale;
                height *= scale;
                _setting.width_zoom = (int)width;
                _setting.height_zoom = (int)height;
                _setting.UpdateScale();
                mode = LMode.MaxRatio;
            }
            else if (PlatformInfo.MonoGamePlatform != MonoGamePlatform.DesktopGL && (_setting.width_zoom <= 0 || _setting.height_zoom <= 0))
            {
                UpdateViewSize(_setting.Landscape(), _setting.width, _setting.height, mode);
                width = this.maxWidth;
                height = this.maxHeight;
                _setting.width_zoom = this.maxWidth;
                _setting.height_zoom = this.maxHeight;
                _setting.UpdateScale();
                mode = LMode.Fill;
            }
            else
            {
                this.maxWidth = _setting.width;
                this.maxHeight = _setting.height;
                this.zoomWidth = _setting.width_zoom;
                this.zoomHeight = _setting.height_zoom;
                UpdateViewSizeData(mode);
                _setting.UpdateScale();
            }

            Window.AllowUserResizing = _setting.allowUserResizing;
            Window.Title = _setting.appName;
#if WINDOWS || DEBUG
            IsMouseVisible = _setting.isMouseVisible;
#endif
            IsFixedTimeStep = _setting.isFixedTimeStep;

            _graphics.PreparingDeviceSettings += (object s, PreparingDeviceSettingsEventArgs args) =>
            {
                args.GraphicsDeviceInformation.PresentationParameters.RenderTargetUsage = RenderTargetUsage.PreserveContents;
            };
            _graphics.PreferredDepthStencilFormat = DepthFormat.Depth24Stencil8;
            _graphics.PreferredBackBufferFormat = _displayMode.Format;
            _graphics.PreferredBackBufferWidth = _setting.Width;
            _graphics.PreferredBackBufferHeight = _setting.Height;
            _graphics.IsFullScreen = _setting.fullscreen;
            _graphics.SynchronizeWithVerticalRetrace = _setting.synchronizeVerticalRetrace;
            _graphics.PreferMultiSampling = _setting.preferMultiSampling;

            if (_setting.Landscape())
            {
                _graphics.SupportedOrientations = DisplayOrientation.LandscapeLeft | DisplayOrientation.LandscapeRight;
            }
            else
            {
                _graphics.SupportedOrientations = DisplayOrientation.Portrait | DisplayOrientation.PortraitDown;
            }

            _graphics.ApplyChanges();

            base.Initialize();

            this.InitializeGame();

        }

        public virtual float ScaleFactor()
        {
            return 1f;
        }

        protected void UpdateViewSize(bool landscape, int width, int height, LMode mode)
        {

            RectBox d = GetScreenDimension();

            this.maxWidth = MathUtils.Max((int)d.GetWidth(), 1);
            this.maxHeight = MathUtils.Max((int)d.GetHeight(), 1);

            if (landscape && (d.GetWidth() > d.GetHeight()))
            {
                maxWidth = (int)d.GetWidth();
                maxHeight = (int)d.GetHeight();
            }
            else if (landscape && (d.GetWidth() < d.GetHeight()))
            {
                maxHeight = (int)d.GetWidth();
                maxWidth = (int)d.GetHeight();
            }
            else if (!landscape && (d.GetWidth() < d.GetHeight()))
            {
                maxWidth = (int)d.GetWidth();
                maxHeight = (int)d.GetHeight();
            }
            else if (!landscape && (d.GetWidth() > d.GetHeight()))
            {
                maxHeight = (int)d.GetWidth();
                maxWidth = (int)d.GetHeight();
            }

            if (mode != LMode.Max)
            {
                if (landscape)
                {
                    this.zoomWidth = width;
                    this.zoomHeight = height;
                }
                else
                {
                    this.zoomWidth = height;
                    this.zoomHeight = width;
                }
            }
            else
            {
                if (landscape)
                {
                    this.zoomWidth = maxWidth >= width ? width : maxWidth;
                    this.zoomHeight = maxHeight >= height ? height : maxHeight;
                }
                else
                {
                    this.zoomWidth = maxWidth >= height ? height : maxWidth;
                    this.zoomHeight = maxHeight >= width ? width : maxHeight;
                }
            }

            if (mode == LMode.Fill)
            {

                LSystem.SetScaleWidth(((float)maxWidth) / zoomWidth);
                LSystem.SetScaleHeight(((float)maxHeight) / zoomHeight);

            }
            else if (mode == LMode.FitFill)
            {

                RectBox res = FitLimitSize(zoomWidth, zoomHeight, maxWidth, maxHeight);
                maxWidth = res.width;
                maxHeight = res.height;
                LSystem.SetScaleWidth(((float)maxWidth) / zoomWidth);
                LSystem.SetScaleHeight(((float)maxHeight) / zoomHeight);

            }
            else if (mode == LMode.Ratio)
            {

                float userAspect = (float)zoomWidth / (float)zoomHeight;
                float realAspect = (float)maxWidth / (float)maxHeight;

                if (realAspect < userAspect)
                {
                    maxHeight = MathUtils.Round(maxWidth / userAspect);
                }
                else
                {
                    maxWidth = MathUtils.Round(maxHeight * userAspect);
                }

                LSystem.SetScaleWidth(((float)maxWidth) / zoomWidth);
                LSystem.SetScaleHeight(((float)maxHeight) / zoomHeight);

            }
            else if (mode == LMode.MaxRatio)
            {

                float userAspect = (float)zoomWidth / (float)zoomHeight;
                float realAspect = (float)maxWidth / (float)maxHeight;

                if ((realAspect < 1 && userAspect > 1) || (realAspect > 1 && userAspect < 1))
                {
                    userAspect = (float)zoomHeight / (float)zoomWidth;
                }

                if (realAspect < userAspect)
                {
                    maxHeight = MathUtils.Round(maxWidth / userAspect);
                }
                else
                {
                    maxWidth = MathUtils.Round(maxHeight * userAspect);
                }

                LSystem.SetScaleWidth(((float)maxWidth) / zoomWidth);
                LSystem.SetScaleHeight(((float)maxHeight) / zoomHeight);

            }
            else
            {

                LSystem.SetScaleWidth(1f);
                LSystem.SetScaleHeight(1f);

            }
            UpdateViewSizeData(mode);
        }

        public void UpdateViewSizeData(LMode mode)
        {
            if (zoomWidth <= 0)
            {
                zoomWidth = maxWidth;
            }
            if (zoomHeight <= 0)
            {
                zoomHeight = maxHeight;
            }
            LSystem.SetScaleWidth((float)maxWidth / zoomWidth);
            LSystem.SetScaleHeight((float)maxHeight / zoomHeight);
            LSystem.viewSize.SetSize(zoomWidth, zoomHeight);

            StringBuffer sbr = new StringBuffer();
            sbr.Append("Mode:").Append(mode);
            sbr.Append("\nWidth:").Append(zoomWidth).Append(",Height:" + zoomHeight);
            sbr.Append("\nMaxWidth:").Append(maxWidth).Append(",MaxHeight:" + maxHeight);
            JavaSystem.Out.Println(sbr.ToString());
        }

        private static RectBox FitLimitSize(int srcWidth, int srcHeight,
              int dstWidth, int dstHeight)
        {
            int dw = dstWidth;
            int dh = dstHeight;
            if (dw != 0 && dh != 0)
            {
                double waspect = (double)dw / srcWidth;
                double haspect = (double)dh / srcHeight;
                if (waspect > haspect)
                {
                    dw = (int)(srcWidth * haspect);
                }
                else
                {
                    dh = (int)(srcHeight * waspect);
                }
            }
            return new RectBox(0, 0, dw, dh);
        }

        protected override void OnActivated(object sender, EventArgs args)
        {
            if (_game != null)
            {
                _game.SetPlatform(this);
                _game.OnResume();
            }
            if (_xnalistener != null)
            {
                _xnalistener.OnResume();
            }
            base.OnActivated(sender, args);
        }
        protected override void OnDeactivated(object sender, EventArgs args)
        {
            if (_game != null)
            {
                _game.OnPause();
            }
            if (_xnalistener != null)
            {
                _xnalistener.OnPause();
            }
            base.OnDeactivated(sender, args);
        }

        protected override void OnExiting(object sender, EventArgs args)
        {
            LSystem.FreeStaticObject();
            if (_game != null)
            {
                _game.OnExit();
            }
            if (_xnalistener != null)
            {
                _xnalistener.OnExit();
            }
            base.OnExiting(sender, args);
        }

        protected override void Dispose(bool disposing)
        {
            if (this._xnalistener != null)
            {
                this._xnalistener.Dispose(disposing);
            }
            base.Dispose(disposing);
        }

        protected override void LoadContent()
        {
            if (_xnalistener != null)
            {
                _xnalistener.LoadContent();
            }
        }

        protected override void Update(GameTime gameTime)
        {
            if (_xnalistener != null)
            {
                _xnalistener.Update(gameTime);
            }
            base.Update(gameTime);
        }

        protected override void Draw(GameTime gameTime)
        {
            base.GraphicsDevice.Clear(Color.Black);
            if (_xnalistener != null)
            {
                _xnalistener.Draw(gameTime);
            }
            if (_game != null)
            {
                _game.ProcessFrame();
            }
            base.Draw(gameTime);
        }

        protected override void UnloadContent()
        {
            if (_xnalistener != null)
            {
                _xnalistener.UnloadContent();
            }
            if (Content != null)
            {
                Content.Unload();
            }
        }

        public void SetXNAListener(XNAListener l)
        {
            this._xnalistener = l;
        }

        public XNAListener GetXNAListener()
        {
            return this._xnalistener;
        }


        public GraphicsDeviceManager GetGraphicsDeviceManager()
        {
            return this._graphics;
        }

        public GraphicsDevice GetGraphicsDevice()
        {
            return base.GraphicsDevice;
        }


        public RectBox GetScreenDimension()
        {
            CheckDisplayMode();
            return new RectBox(0, 0, _displayMode.Width, _displayMode.Height);
        }

        public Loon SetSleepTime(int time)
        {
            if (time <= 0)
            {
                InactiveSleepTime = TimeSpan.Zero;
            }
            else
            {
                InactiveSleepTime = TimeSpan.FromSeconds(time);
            }
            return this;
        }

        public Loon SetFPS(int fps)
        {
            if (fps <= 0)
            {
                TargetElapsedTime = TimeSpan.Zero;
            }
            else
            {
                if (30 == fps)
                {
                    TargetElapsedTime = TimeSpan.FromTicks(333333);
                }
                else
                {
                    try
                    {
                        TargetElapsedTime = System.TimeSpan.FromTicks((System.TimeSpan.TicksPerSecond / fps) + 1);
                    }
                    catch
                    {
                        TargetElapsedTime = TimeSpan.FromTicks(333333);
                    }
                }
            }
            return this;
        }

        public virtual void SetFullScreen(bool fullScreen)
        {
            _graphics.IsFullScreen = fullScreen;
            _graphics.ApplyChanges();
        }

        public ContentManager GetContentManager()
        {
            return this.Content;
        }

        public void Close()
        {
            throw new NotImplementedException();
        }

        public int GetContainerWidth()
        {
            return GetScreenDimension().width;
        }

        public int GetContainerHeight()
        {
            return GetScreenDimension().height;
        }

        public Platform_Orientation GetOrientation()
        {

            if (GetContainerHeight() > GetContainerWidth())
            {
                return Platform_Orientation.Portrait;
            }
            else
            {
                return Platform_Orientation.Landscape;
            }

        }

        public LGame GetGame()
        {
            return this._game;
        }

    }
}
