
using java.lang;
using loon.geom;
using loon.monogame;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using MonoGame.Framework.Utilities;
using System;
using System.Transactions;

namespace loon
{
    public abstract class Loon : Game, Platform
    {
        private DisplayMode _displayMode;

        private GraphicsDeviceManager _graphics;

        private MonoGameSetting _setting;

        public delegate Screen ScreenDelegate();

        private ScreenDelegate _mainDelegateData;

        private Data _mainInterfaceData;

        private MonoGameGame _game;

        public Loon() : this("assets")
        {

        }

        public Loon(string dir)
        {
            CheckDisplayMode();
            _graphics = new GraphicsDeviceManager(this);
            Content.RootDirectory = dir;
        }

        protected internal virtual MonoGameGame InitializeGame()
        {
            if (_game != null && _mainDelegateData != null)
            {
                _game.Register(_mainDelegateData.Invoke());
            }
            else if (_game != null && _mainInterfaceData != null)
            {
                _game.Register(_mainInterfaceData.OnScreen());
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
        private void CheckDisplayMode()
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

        public abstract void OnMain();

        protected override void Initialize()
        {
            this.OnMain();
            if (_setting == null)
            {
                _setting = new MonoGameSetting();
            }
     
            this.CreateGame();
            
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
            InitializeGame();

        }

        public GraphicsDeviceManager GetGraphicsDeviceManager()
        {
            return this._graphics;
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

        protected override void LoadContent()
        {
        }

        protected override void Update(GameTime gameTime)
        {
            base.Update(gameTime);
        }

        protected override void Draw(GameTime gameTime)
        {
            GraphicsDevice.Clear(Color.Black);
            base.Draw(gameTime);
            if (_game != null)
            {
                _game.ProcessFrame();
            }
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
