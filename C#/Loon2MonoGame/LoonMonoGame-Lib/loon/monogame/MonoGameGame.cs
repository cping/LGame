using java.lang;
using MonoGame.Framework.Utilities;
using System;

namespace loon.monogame
{
    public class MonoGameGame : LGame
    {
        private readonly long _start;

        private readonly static bool _isWindows;

        private readonly static bool _isLinux;

        private readonly static bool _isMac;

        static MonoGameGame(){
            _isWindows = Environment.OSVersion.Platform == PlatformID.Win32NT;
            _isLinux = Environment.OSVersion.Platform == PlatformID.Unix;
            _isMac = !_isWindows && !_isLinux;
        }

        private Log _log;

        public MonoGameGame(LSetting config, Loon game) : base(config, game)
        {

            this._start = JavaSystem.NanoTime();
            this._log = new MonoGameLog();
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

        public override bool IsDesktop()
        {
            return PlatformInfo.MonoGamePlatform == MonoGamePlatform.DesktopGL;
        }

        public override Log Log()
        {
            return _log;
        }

        public override double Time()
        {
            throw new NotImplementedException();
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
