﻿using java.lang;
using loon.events;
using loon.utils;
using loon.utils.reply;
using Microsoft.Xna.Framework.Graphics;
using static loon.LTexture;

namespace loon
{
    public abstract class LGame : LRelease
    {
        public enum Type
        {
            JAVASE, MONO, ANDROID, IOS, WP, HTML5, UNITY, SWITCH, PS, XBOX, STUB
        }

        public enum Status
        {
            PAUSE, RESUME, EXIT
        }

        public class Error
        {

            public string message;
            public System.Exception cause;

            public Error(string message, System.Exception cause)
            {
                this.message = message;
                this.cause = cause;
            }
        }


        protected internal const string FONT_NAME = "Dialog";

        protected internal const string APP_NAME = "Loon";

        protected internal static LGame _base = null;

        protected internal static Platform _platform = null;

        // 错误接口
        public Act<Error> errors = Act<Error>.Create<Error>();

        // 状态接口
        public Act<Status> status = Act<Status>.Create<Status>();

        // 游戏窗体刷新接口
        public Act<object> frame = Act<object>.Create<object>();

        // 游戏基本设置
        public LSetting setting;

        protected internal LProcess processImpl;

        protected internal Display displayImpl;

        public LGame(LSetting config, Platform plat)
        {
            LGame._platform = plat;
            if (config == null)
            {
                config = new LSetting();
            }
            this.setting = config;
            string appName = config.appName;
            if (StringUtils.IsEmpty(appName))
            {
                setting.appName = APP_NAME;
            }
            string fontName = config.fontName;
            if (StringUtils.IsEmpty(fontName))
            {
                setting.fontName = FONT_NAME;
            }

        }

        protected void InitProcess(LGame game)
        {
            _base = game;
            if (_base == null && _platform != null)
            {
                _base = _platform.GetGame();
            }
            processImpl = new LProcess(game);
            Log().Debug("The Loon Game Engine is Begin");
        }

        public LGame InitProcess()
        {
            InitProcess(this);
           /* if (setting.defaultGameFont == null)
            {
                setting.defaultGameFont = LFont.getFont(setting.fontName, 20);
            }
            if (setting.defaultLogFont == null)
            {
                setting.defaultLogFont = LSTRFont.getFont(LSystem.isDesktop() ? 16 : 20);
            }
            if (jsonImpl == null)
            {
                jsonImpl = new JsonImpl();
            }*/
            return this;
        }

        public virtual LTexture CreateTexture(int width, int height, Format config)
        {
            return Graphics().CreateTexture(width, height, config);
        }

        public virtual LTexture NewTexture(string path)
        {
            return NewTexture(path, Format.LINEAR);
        }


        public virtual LTexture NewTexture(string path, Format config)
        {
            if (StringUtils.IsEmpty(path))
            {
                return null;
            }
            Log().Debug("Texture : New " + path + " Loaded");
            return this.Graphics().CreateTexture(path,config);
        }

        public virtual void SetPlatform(Platform plat)
        {
            if (plat != null)
            {
                LGame._platform = plat;
                LGame game = plat.GetGame();
                if (game != null)
                {
                    LGame._base = game;
                  //  LGame._base.resetShader();
                }
            }
        }
        protected internal virtual LGame CheckBaseGame(LGame game)
        {
            LGame oldGame = _base;
            if (game != oldGame && game != null)
            {
                oldGame = game;
            }
            else if (game == null)
            {
                if (oldGame != null && _platform != null && _platform.GetGame() != null)
                {
                    if (oldGame != _platform.GetGame())
                    {
                        oldGame = _platform.GetGame();
                    }
                }
            }
            if (_base != game || _base != oldGame)
            {
                _base = oldGame;
            }
            return LSystem.Base;
        }

        public virtual LProcess Process()
        {
            return this.processImpl;
        }

        public virtual LGame ReportError(string message, System.Exception cause)
        {
            return ReportError(message, cause, true);
        }


        public virtual LGame ReportError(string message, System.Exception cause, bool logError)
        {
            errors.Emit(new Error(message, cause));
            if (logError)
            {
                Log().Error(message, cause);
            }
            return this;
        }

        public virtual void DispatchEvent<E>(Act<E> signal, E e)
        {
            try
            {
                signal.Emit(e);
            }
            catch (System.Exception cause)
            {
                ReportError("Event dispatch failure", cause);
            }
        }

        protected internal virtual void EmitFrame()
        {
            try
            {
                frame.Emit(this);
            }
            catch (System.Exception cause)
            {
                Log().Error("Frame tick exception :", cause);
                LSystem.StopRepaint();
            }
        }

        public virtual Display Register(Screen screen)
        {
            this.displayImpl = new Display(this, setting.fps);
            this.displayImpl.SetScreen(screen);
            return displayImpl;
        }

        public static void FreeStatic()
        {
            LGame._platform = null;
            LGame._base = null;
        }

        public virtual bool IsMobile()
        {
            Type type = this.TYPE;
            return (type == LGame.Type.ANDROID || type == LGame.Type.IOS || type == LGame.Type.WP
                    || type == LGame.Type.SWITCH);
        }

        public virtual bool IsHTML5()
        {
            Type type = this.TYPE;
            return type == LGame.Type.HTML5;
        }

        public virtual bool IsDesktop()
        {
            return !IsMobile() && !IsHTML5();
        }

        public abstract LGame.Type TYPE { get; }
        public abstract double Time();
        public abstract int Tick();

        public virtual Display Display()
        {
            return displayImpl;
        }
        public abstract Log Log();
        public abstract Asyn Asyn();
        public abstract Assets Assets();
        public abstract Support Support();
        public abstract InputMake Input();

        public abstract Graphics Graphics();

        public virtual void Close()
        {
            if (!errors.IsClosed())
            {
                errors.ClearConnections();
            }
            if (!status.IsClosed())
            {
                status.ClearConnections();
            }
            if (!frame.IsClosed())
            {
                frame.ClearConnections();
            }
        }

    }
}
