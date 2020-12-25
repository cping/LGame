using java.lang;
using loon.events;
using loon.jni;
using Microsoft.Xna.Framework.Graphics;
using MonoGame.Framework.Utilities;
using System;

namespace loon.monogame
{
    public class MonoGameGame : LGame
    {
        public enum State
        {
            RUNNING, PAUSED, EXITED
        }

        private State _state = State.RUNNING;

        private readonly long _start;

        private readonly static bool _isWindows;

        private readonly static bool _isLinux;

        private readonly static bool _isMac;


        static MonoGameGame()
        {
            _isWindows = Environment.OSVersion.Platform == PlatformID.Win32NT;
            _isLinux = Environment.OSVersion.Platform == PlatformID.Unix;
            _isMac = !_isWindows && !_isLinux;
        }

        private readonly Log _log;

        private readonly Assets _assets;

        private readonly Asyn _asyn;

        private readonly Support _support;

        private readonly MonoGameContentManager _contentManager;

        private readonly MonoGameInputMake _inputer;

        private readonly MonoGameGraphics _graphics;

        public MonoGameGame(LSetting config, Loon game) : base(config, game)
        {

            this._start = JavaSystem.NanoTime();
            this._contentManager = new MonoGameContentManager(game.GetContentManager().ServiceProvider, game.GetContentManager().RootDirectory);
            this._inputer = new MonoGameInputMake();
            this._log = new MonoGameLog();
            this._support = new NativeSupport();
            this._assets = new MonoGameAssets(this);
            this._asyn = new MonoGameAsyn<object>(_log,frame);
            this._graphics = new MonoGameGraphics(this, game.GetGraphicsDevice(), config.Width, config.Height);
            this.InitProcess();
        }

        public override int Tick()
        {
            return (int)((JavaSystem.NanoTime() - _start) / 1000000L);
        }

        public bool IsMac()
        {
            return IsDesktop() && _isMac;
        }

        public bool IsWindows()
        {
            return IsDesktop() && _isWindows;
        }

        public bool IsLinux()
        {
            return IsDesktop() && _isLinux;
        }

        public bool IsAndroid()
        {
            return PlatformInfo.MonoGamePlatform == MonoGamePlatform.Android;
        }

        public bool IsIos()
        {
            return PlatformInfo.MonoGamePlatform == MonoGamePlatform.iOS;
        }

        public bool IsSwitch()
        {
            return PlatformInfo.MonoGamePlatform == MonoGamePlatform.NintendoSwitch;
        }

        public override bool IsMobile()
        {
            return PlatformInfo.MonoGamePlatform == MonoGamePlatform.Android || PlatformInfo.MonoGamePlatform == MonoGamePlatform.iOS || PlatformInfo.MonoGamePlatform == MonoGamePlatform.tvOS;
        }

        public override bool IsHTML5()
        {
            return PlatformInfo.MonoGamePlatform == MonoGamePlatform.WebGL;
        }

        public override bool IsDesktop()
        {
            return PlatformInfo.MonoGamePlatform == MonoGamePlatform.DesktopGL;
        }

        public MonoGameContentManager GetMonoGameContentManager()
        {
            return _contentManager;
        }

        protected internal void OnPause()
        {
            _state = State.PAUSED;
            status.Emit(Status.PAUSE);
        }

        protected internal void OnResume()
        {
            _state = State.RUNNING;
            status.Emit(Status.RESUME);
        }

        protected internal void OnExit()
        {
            _state = State.EXITED;
            status.Emit(Status.EXIT);
        }

        protected internal void Update()
        {
            this._inputer.Update();
        }

        protected internal void ProcessFrame()
        {
            this.EmitFrame();
        }

        public State GetState()
        {
            return this._state;
        }

        public override Log Log()
        {
            return _log;
        }

        public override double Time()
        {
            return JavaSystem.CurrentTimeMillis();
        }

        public override Assets Assets()
        {
            return _assets;
        }

        public override Asyn Asyn()
        {
            return this._asyn;
        }

        public override Support Support()
        {
            return _support;
        }

        public override InputMake Input()
        {
            return this._inputer;
        }

        public override Graphics Graphics()
        {
            return this._graphics;
        }

        public override Type TYPE
        {
            get
            {
                return Type.MONO;
            }
        }
    }
}
